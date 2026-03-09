package Controller;

import Entities.Staff;
import Model.DAOBook;
import Model.DAOOrderDetail;
import Model.DAOOrders;
import Model.DBConnection;
import Utils.RoleUtils;
import ViewModel.OrderItemRow;
import ViewModel.OrderRow;
import ViewModel.PageSlice;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "OrdersController", urlPatterns = {"/admin/orders"})
public class OrdersController extends HttpServlet {

    private static final String ORDERS_PATH = "/admin/orders";
    private static final int PAGE_SIZE = 10;

    private final DAOOrders daoOrders = new DAOOrders();
    private final DAOOrderDetail daoOrderDetail = new DAOOrderDetail();
    private final DAOBook daoBook = new DAOBook();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!canManageOrders(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        String search = trim(req.getParameter("search"));
        String status = trim(req.getParameter("status"));
        int requestedPage = parsePositiveInt(req.getParameter("page"), 1);

        try {
            List<OrderRow> filteredOrders = daoOrders.getOrderRows(null, search, status);
            PageSlice<OrderRow> pageSlice = paginate(filteredOrders, requestedPage, PAGE_SIZE);

            req.setAttribute("orders", pageSlice.getItems());
            req.setAttribute("search", search);
            req.setAttribute("status", status.isEmpty() ? "ALL" : status);
            req.setAttribute("currentPage", pageSlice.getPage());
            req.setAttribute("totalPages", pageSlice.getTotalPages());
            req.setAttribute("totalItems", pageSlice.getTotalItems());
            req.setAttribute("isAdmin", RoleUtils.isAdmin(req));
            req.getRequestDispatcher("/WEB-INF/views/orders/list.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!canManageOrders(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        String action = req.getParameter("action");
        if (!"approve".equals(action) && !"reject".equals(action)) {
            resp.sendRedirect(buildListRedirect(req, null, null));
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(req.getParameter("orderID"));
        } catch (NumberFormatException e) {
            redirectWithMessage(req, resp, "error", "OrderID khong hop le.");
            return;
        }

        try {
            if ("approve".equals(action)) {
                approveOrder(orderId, staff.getStaffID());
                redirectWithMessage(req, resp, "msg", "Da duyet don hang.");
            } else {
                rejectOrder(orderId, staff.getStaffID());
                redirectWithMessage(req, resp, "msg", "Da tu choi don hang.");
            }
        } catch (SQLException e) {
            redirectWithMessage(req, resp, "error", e.getMessage());
        }
    }

    private boolean canManageOrders(HttpServletRequest req) {
        return RoleUtils.isAdmin(req) || RoleUtils.isStaff(req);
    }

    private void approveOrder(int orderId, int staffId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            String status = daoOrders.getStatusForUpdate(con, orderId);
            if (status == null) {
                throw new SQLException("Khong tim thay don hang.");
            }
            if (!"Pending".equalsIgnoreCase(status)) {
                throw new SQLException("Chi co the duyet don Pending.");
            }

            List<OrderItemRow> items = daoOrderDetail.getOrderItemsWithBookName(con, orderId);
            if (items.isEmpty()) {
                throw new SQLException("Don hang khong co chi tiet sach.");
            }

            for (OrderItemRow item : items) {
                int affected = daoBook.decreaseStockAndAvailable(con, item.getBookID(), item.getQuantity());
                if (affected == 0) {
                    throw new SQLException("Khong du ton kho de duyet don cho sach id=" + item.getBookID());
                }
            }

            if (daoOrders.updateStatus(con, orderId, "Approved", staffId) == 0) {
                throw new SQLException("Cap nhat trang thai don hang that bai.");
            }
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private void rejectOrder(int orderId, int staffId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            String status = daoOrders.getStatusForUpdate(con, orderId);
            if (status == null) {
                throw new SQLException("Khong tim thay don hang.");
            }
            if (!"Pending".equalsIgnoreCase(status)) {
                throw new SQLException("Chi co the tu choi don Pending.");
            }

            if (daoOrders.updateStatus(con, orderId, "Rejected", staffId) == 0) {
                throw new SQLException("Cap nhat trang thai don hang that bai.");
            }
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private void redirectWithMessage(HttpServletRequest req, HttpServletResponse resp, String key, String value) throws IOException {
        resp.sendRedirect(buildListRedirect(req, key, value));
    }

    private String buildListRedirect(HttpServletRequest req, String key, String value) {
        StringBuilder url = new StringBuilder(req.getContextPath()).append(ORDERS_PATH).append("?action=list");
        appendQueryParam(url, "search", trim(req.getParameter("search")));
        appendQueryParam(url, "status", trim(req.getParameter("status")));
        appendQueryParam(url, "page", trim(req.getParameter("page")));
        if (key != null && value != null && !value.trim().isEmpty()) {
            appendQueryParam(url, key, value);
        }
        return url.toString();
    }

    private void appendQueryParam(StringBuilder url, String key, String value) {
        if (value == null || value.trim().isEmpty()) {
            return;
        }
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
        url.append("&").append(key).append("=").append(encoded);
    }

    private int parsePositiveInt(String raw, int defaultValue) {
        if (raw == null || raw.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            return value > 0 ? value : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private <T> PageSlice<T> paginate(List<T> source, int requestedPage, int pageSize) {
        int safePageSize = Math.max(1, pageSize);
        int totalItems = source == null ? 0 : source.size();
        int totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) safePageSize));
        int page = Math.max(1, Math.min(requestedPage, totalPages));
        int fromIndex = (page - 1) * safePageSize;
        int toIndex = Math.min(fromIndex + safePageSize, totalItems);
        List<T> items = totalItems == 0 ? List.of() : source.subList(fromIndex, toIndex);
        return new PageSlice<>(items, page, totalPages, totalItems);
    }
}
