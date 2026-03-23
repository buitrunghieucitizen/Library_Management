package Controller;

import Entities.Orders;
import Entities.Staff;
import Model.DAOBook;
import Model.DAOOrderDetail;
import Model.DAOOrders;
import Model.DBConnection;
import Utils.RoleUtils;
import ViewModel.OrderItemRow;
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

@WebServlet(name = "OrderStaffController", urlPatterns = {"/admin/orders"})
public class OrderStaffController extends HttpServlet {

    private final DAOOrders daoOrders = new DAOOrders();
    private final DAOOrderDetail daoOrderDetail = new DAOOrderDetail();
    private final DAOBook daoBook = new DAOBook();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!RoleUtils.isAdmin(req) && !RoleUtils.isStaff(req)) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String searchId = req.getParameter("orderID");
        try {
            if (searchId != null && !searchId.trim().isEmpty()) {
                int id = Integer.parseInt(searchId.trim());
                Orders order = daoOrders.getById(id);
                if (order != null) {
                    req.setAttribute("searchResult", order);
                    try (Connection con = DBConnection.getConnection()) {
                        List<OrderItemRow> items = daoOrderDetail.getOrderItemsWithBookName(con, id);
                        req.setAttribute("orderItems", items);
                    }
                } else {
                    req.setAttribute("error", "Khong tim thay don hang voi ma #" + id);
                }
            }

            req.setAttribute("allOrders", daoOrders.getOrderRows());
            req.setAttribute("isAdmin", RoleUtils.isAdmin(req));
            req.getRequestDispatcher("/WEB-INF/views/orders/list.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Loi: " + e.getMessage());
            req.setAttribute("isAdmin", RoleUtils.isAdmin(req));
            req.getRequestDispatcher("/WEB-INF/views/orders/list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null || (!RoleUtils.isAdmin(req) && !RoleUtils.isStaff(req))) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(req.getParameter("orderID"));
        } catch (NumberFormatException e) {
            redirectWithMessage(req, resp, "error", "OrderID khong hop le");
            return;
        }

        String action = req.getParameter("action");
        try {
            if ("complete".equals(action)) {
                processOrderComplete(orderId, staff.getStaffID());
                redirectWithMessage(req, resp, "msg", "Da hoan thanh don hang #" + orderId);
            } else if ("cancel".equals(action)) {
                cancelOrder(orderId, staff.getStaffID());
                redirectWithMessage(req, resp, "msg", "Da huy don hang #" + orderId);
            } else if ("approve".equals(action)) {
                approveOrder(orderId, staff.getStaffID());
                redirectWithMessage(req, resp, "msg", "Da duyet don hang #" + orderId);
            } else if ("reject".equals(action)) {
                rejectOrder(orderId, staff.getStaffID());
                redirectWithMessage(req, resp, "msg", "Da tu choi don hang #" + orderId);
            } else {
                redirectWithMessage(req, resp, "error", "Hanh dong khong hop le");
            }
        } catch (Exception e) {
            if (!resp.isCommitted()) {
                redirectWithMessage(req, resp, "error", "Loi xu ly don #" + orderId + ": " + e.getMessage());
            }
        }
    }

    private void processOrderComplete(int orderId, int staffId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Loi ket noi CSDL!");
        }

        try {
            con.setAutoCommit(false);

            String currentStatus = daoOrders.getStatusForUpdate(con, orderId);
            if (currentStatus == null || currentStatus.equals("Đã giao") || currentStatus.equals("Đã hủy")) {
                throw new SQLException("Don hang nay da duoc xu ly tu truoc!");
            }

            List<OrderItemRow> items = daoOrderDetail.getOrderItemsWithBookName(con, orderId);
            for (OrderItemRow item : items) {
                int affected = daoBook.decreaseAvailable(con, item.getBookID(), item.getQuantity());
                if (affected == 0) {
                    throw new SQLException("Sach '" + item.getBookName() + "' khong du so luong trong kho de giao!");
                }
            }

            daoOrders.updateStatus(con, orderId, "Đã giao", staffId);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private void cancelOrder(int orderId, int staffId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Loi ket noi CSDL!");
        }

        try {
            con.setAutoCommit(false);
            daoOrders.updateStatus(con, orderId, "Đã hủy", staffId);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private void approveOrder(int orderId, int staffId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Loi ket noi CSDL!");
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
                    throw new SQLException("Khong du ton kho de duyet don cho sach '" + item.getBookName() + "'");
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
            throw new SQLException("Loi ket noi CSDL!");
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

    private void redirectWithMessage(HttpServletRequest req, HttpServletResponse resp, String key, String message) throws IOException {
        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        resp.sendRedirect(req.getContextPath() + "/admin/orders?" + key + "=" + encoded);
    }
}
