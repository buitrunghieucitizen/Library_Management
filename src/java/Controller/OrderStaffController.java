package Controller;

import Entities.*;
import Model.*;
import Utils.RoleUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
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
        // Kiểm tra quyền (chỉ Admin và Staff được vào)
        if (!RoleUtils.isAdmin(req) && !RoleUtils.isStaff(req)) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String searchId = req.getParameter("orderID");
        try {
            // Nếu có tìm kiếm theo mã đơn
            if (searchId != null && !searchId.trim().isEmpty()) {
                int id = Integer.parseInt(searchId.trim());
                Orders order = daoOrders.getById(id);
                if (order != null) {
                    req.setAttribute("searchResult", order);
                    // Lấy chi tiết các sách trong đơn
                    Connection con = DBConnection.getConnection();
                    List<DAOOrderDetail.OrderItemRow> items = daoOrderDetail.getOrderItemsWithBookName(con, id);
                    con.close();
                    req.setAttribute("orderItems", items);
                } else {
                    req.setAttribute("error", "Không tìm thấy đơn hàng với mã #" + id);
                }
            }
            
            // Luôn hiển thị danh sách tất cả các đơn
            req.setAttribute("allOrders", daoOrders.getOrderRows());
            
            // ĐÃ SỬA: Trỏ về đúng file list.jsp trong thư mục orders của bạn
            req.getRequestDispatcher("/WEB-INF/views/orders/list.jsp").forward(req, resp);
            
        } catch (Exception e) {
            req.setAttribute("error", "Lỗi: " + e.getMessage());
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

        String action = req.getParameter("action");
        int orderId = Integer.parseInt(req.getParameter("orderID"));

        try {
            if ("complete".equals(action)) {
                processOrderComplete(orderId, staff.getStaffID());
                resp.sendRedirect(req.getContextPath() + "/admin/orders?msg=" + java.net.URLEncoder.encode("Đã hoàn thành đơn hàng #" + orderId + " và trừ kho thành công!", "UTF-8"));
                return;
            } else if ("cancel".equals(action)) {
                cancelOrder(orderId, staff.getStaffID());
                resp.sendRedirect(req.getContextPath() + "/admin/orders?msg=" + java.net.URLEncoder.encode("Đã hủy đơn hàng #" + orderId, "UTF-8"));
                return;
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/orders?error=" + java.net.URLEncoder.encode("Hành động không hợp lệ", "UTF-8"));
                return;
            }
        } catch (Exception e) {
            if (!resp.isCommitted()) {
                resp.sendRedirect(req.getContextPath() + "/admin/orders?error=" + java.net.URLEncoder.encode("Lỗi xử lý đơn #" + orderId + ": " + e.getMessage(), "UTF-8"));
            }
        }
    }

    private void processOrderComplete(int orderId, int staffId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) throw new SQLException("Lỗi kết nối CSDL!");

        try {
            con.setAutoCommit(false);

            String currentStatus = daoOrders.getStatusForUpdate(con, orderId);
            if (currentStatus == null || currentStatus.equals("Đã giao") || currentStatus.equals("Đã hủy")) {
                throw new SQLException("Đơn hàng này đã được xử lý từ trước!");
            }

            List<DAOOrderDetail.OrderItemRow> items = daoOrderDetail.getOrderItemsWithBookName(con, orderId);
            
            for (DAOOrderDetail.OrderItemRow item : items) {
                int affected = daoBook.decreaseAvailable(con, item.getBookID(), item.getQuantity());
                if (affected == 0) {
                    throw new SQLException("Sách '" + item.getBookName() + "' không đủ số lượng trong kho để giao!");
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
}