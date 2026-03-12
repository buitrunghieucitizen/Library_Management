package Controller;

import Entities.Staff;
import Model.DAOBook;
import Model.DAOOrderDetail;
import Model.DAOOrders;
import Model.DBConnection;
import Utils.RoleUtils;
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

@WebServlet(name = "OrdersController", urlPatterns = {"/admin/orders_old"})
public class OrdersController extends HttpServlet {

    private static final String ORDERS_PATH = "/admin/orders";

    private final DAOOrders daoOrders = new DAOOrders();
    private final DAOOrderDetail daoOrderDetail = new DAOOrderDetail();
    private final DAOBook daoBook = new DAOBook();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!canManageOrders(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        try {
            req.setAttribute("orders", daoOrders.getOrderRows());
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
            resp.sendRedirect(req.getContextPath() + ORDERS_PATH);
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

            List<DAOOrderDetail.OrderItemRow> items = daoOrderDetail.getOrderItemsWithBookName(con, orderId);
            if (items.isEmpty()) {
                throw new SQLException("Don hang khong co chi tiet sach.");
            }

            for (DAOOrderDetail.OrderItemRow item : items) {
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
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
        resp.sendRedirect(req.getContextPath() + ORDERS_PATH + "?" + key + "=" + encoded);
    }
}
