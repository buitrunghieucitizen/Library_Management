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
import java.util.*;

@WebServlet(name = "BuyController", urlPatterns = {"/buy"})
public class BuyController extends HttpServlet {

    private final DAOBook daoBook = new DAOBook();
    private final DAOBookPrice daoBookPrice = new DAOBookPrice();
    private final DAOOrders daoOrders = new DAOOrders();
    private final DAOOrderDetail daoOrderDetail = new DAOOrderDetail();
    private final DAOStudent daoStudent = new DAOStudent();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        try {
            Integer studentId = resolveStudentIdForStaff(staff);
            req.setAttribute("studentId", studentId != null ? studentId : "Không xác định");
            req.setAttribute("studentName", staff.getStaffName());

            // 1. Load Sách và Giá
            req.setAttribute("bookPrices", daoBookPrice.getBookPriceRows());

            // 2. Load lịch sử Order của Student
            if (studentId != null) {
                req.setAttribute("orderHistory", daoOrders.getOrderRowsByStudent(studentId));
            }

            // 3. Load Danh sách chờ từ Session
            HttpSession session = req.getSession();
            Map<Integer, WaitlistItem> waitlist = (Map<Integer, WaitlistItem>) session.getAttribute("waitlist");
            if (waitlist == null) {
                waitlist = new HashMap<>();
            }
            req.setAttribute("waitlistItems", waitlist.values()); // Gửi sang JSP với tên waitlistItems

            req.getRequestDispatcher("/WEB-INF/views/buy/student_buy.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        HttpSession session = req.getSession();

        // Đổi tên Session Attribute cho đúng ý nghĩa
        Map<Integer, WaitlistItem> waitlist = (Map<Integer, WaitlistItem>) session.getAttribute("waitlist");
        if (waitlist == null) {
            waitlist = new HashMap<>();
        }

        try {
            if ("addToWaitlist".equals(action)) {
                int bookId = Integer.parseInt(req.getParameter("bookID"));
                String bookName = req.getParameter("bookName");
                double price = Double.parseDouble(req.getParameter("price"));
                int quantity = Integer.parseInt(req.getParameter("quantity"));

                if (waitlist.containsKey(bookId)) {
                    WaitlistItem item = waitlist.get(bookId);
                    item.setQuantity(item.getQuantity() + quantity);
                } else {
                    waitlist.put(bookId, new WaitlistItem(bookId, bookName, quantity, price));
                }
                session.setAttribute("waitlist", waitlist);
                resp.sendRedirect(req.getContextPath() + "/buy?msg=" + java.net.URLEncoder.encode("Đã thêm sách vào danh sách chờ", "UTF-8"));
                return; // THÊM RETURN ĐỂ CHẶN LỖI 2 LẦN

            } else if ("removeFromWaitlist".equals(action)) {
                int bookId = Integer.parseInt(req.getParameter("bookID"));
                waitlist.remove(bookId);
                session.setAttribute("waitlist", waitlist);
                resp.sendRedirect(req.getContextPath() + "/buy?msg=" + java.net.URLEncoder.encode("Đã xóa sách khỏi danh sách chờ", "UTF-8"));
                return; // THÊM RETURN ĐỂ CHẶN LỖI 2 LẦN

            } else if ("checkout".equals(action)) {
                // 1. Lấy mảng các bookID mà sinh viên đã tích chọn
                String[] selectedBooks = req.getParameterValues("selectedBooks");

                if (selectedBooks == null || selectedBooks.length == 0) {
                    resp.sendRedirect(req.getContextPath() + "/buy?error=" + java.net.URLEncoder.encode("Vui lòng tích chọn ít nhất 1 sách để đặt", "UTF-8"));
                    return; // Đã có sẵn return, rất chuẩn
                }

                Staff staff = RoleUtils.getLoggedStaff(req);
                Integer studentId = resolveStudentIdForStaff(staff);

                // KIỂM TRA BẢO MẬT: Tránh lỗi NullPointerException khi studentId bị rỗng
                if (studentId == null) {
                    resp.sendRedirect(req.getContextPath() + "/buy?error=" + java.net.URLEncoder.encode("Lỗi: Không xác định được tài khoản sinh viên hợp lệ.", "UTF-8"));
                    return;
                }

                // 2. Lọc ra những cuốn sách được chọn từ danh sách chờ
                Map<Integer, WaitlistItem> itemsToOrder = new HashMap<>();
                for (String idStr : selectedBooks) {
                    int bId = Integer.parseInt(idStr);
                    if (waitlist.containsKey(bId)) {
                        itemsToOrder.put(bId, waitlist.get(bId));
                    }
                }

                // 3. Tiến hành đặt hàng với những sách đã chọn
                processCheckout(itemsToOrder, studentId, staff.getStaffID());

                // 4. Chỉ xóa những sách đã đặt thành công khỏi danh sách chờ (giữ lại sách chưa đặt)
                for (Integer bId : itemsToOrder.keySet()) {
                    waitlist.remove(bId);
                }
                session.setAttribute("waitlist", waitlist);

                resp.sendRedirect(req.getContextPath() + "/buy?msg=" + java.net.URLEncoder.encode("Đặt sách thành công. Vui lòng theo dõi mã đơn.", "UTF-8"));
                return; // THÊM RETURN ĐỂ CHẶN LỖI 2 LẦN
            } else {
                // Hành động không nhận diện được
                resp.sendRedirect(req.getContextPath() + "/buy?error=" + java.net.URLEncoder.encode("Lỗi: Hành động " + action + " không hợp lệ.", "UTF-8"));
                return; // THÊM RETURN ĐỂ CHẶN LỖI 2 LẦN
            }

        } catch (Exception e) {
            // Check nếu server CHƯA gửi redirect nào thì mới được phép gửi
            if (!resp.isCommitted()) {
                resp.sendRedirect(req.getContextPath() + "/buy?error=" + java.net.URLEncoder.encode("Lỗi xử lý: " + e.getMessage(), "UTF-8"));
            } else {
                // Nếu đã gửi redirect rồi mà vẫn dính lỗi thì in ra Console để debug
                e.printStackTrace();
            }
        }
    }

    private void processCheckout(Map<Integer, WaitlistItem> itemsToOrder, int studentId, int staffId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            double totalAmount = 0;
            boolean isAllAvailable = true;

            // Đổi cart.values() thành itemsToOrder.values()
            for (WaitlistItem item : itemsToOrder.values()) {
                totalAmount += item.getTotalPrice();
                int available = daoBook.getAvailable(con, item.getBookId());
                if (available < item.getQuantity()) {
                    isAllAvailable = false;
                }
            }

            // Theo logic: Nếu đủ kho -> Sẵn sàng, Nếu thiếu sách -> Hàng chờ
            String status = isAllAvailable ? "Sẵn sàng" : "Hàng chờ";

            // Tạo Orders
            int orderId = daoOrders.insertOrderCustomStatus(con, studentId, staffId, totalAmount, status);

            // Đổi cart.values() thành itemsToOrder.values()
            for (WaitlistItem item : itemsToOrder.values()) {
                daoOrderDetail.insert(con, new OrderDetail(orderId, item.getBookId(), item.getQuantity(), item.getUnitPrice()));
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

    // Copy hàm này từ BorrowController sang để đồng bộ cơ chế ánh xạ Sinh viên
    private Integer resolveStudentIdForStaff(Staff staff) throws SQLException {
        if (staff == null) {
            return null;
        }
        Integer candidateFromUsername = extractTrailingNumber(staff.getUsername());
        if (candidateFromUsername != null && daoStudent.getById(candidateFromUsername) != null) {
            return candidateFromUsername;
        }
        int sameId = staff.getStaffID();
        if (sameId > 0 && daoStudent.getById(sameId) != null) {
            return sameId;
        }
        return null;
    }

    private Integer extractTrailingNumber(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        int index = value.length() - 1;
        while (index >= 0 && Character.isDigit(value.charAt(index))) {
            index--;
        }
        if (index == value.length() - 1) {
            return null;
        }
        try {
            return Integer.parseInt(value.substring(index + 1));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
