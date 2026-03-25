package Controller;

import Entities.*;
import Model.*;
import Utils.RoleUtils;
import Utils.StudentContextUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@WebServlet(name = "BuyController", urlPatterns = { "/buy" })
public class BuyController extends HttpServlet {

    private final DAOBook daoBook = new DAOBook();
    private final DAOBookPrice daoBookPrice = new DAOBookPrice();
    private final DAOOrders daoOrders = new DAOOrders();
    private final DAOOrderDetail daoOrderDetail = new DAOOrderDetail();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        try {
            Student currentStudent = StudentContextUtils.resolveCurrentStudent(req);
            Integer studentId = currentStudent == null
                    ? StudentContextUtils.resolveStudentId(staff)
                    : currentStudent.getStudentID();
            String studentName = StudentContextUtils.buildDisplayName(staff, currentStudent);
            req.setAttribute("studentId", studentId != null ? studentId : "Không xác định");
            req.setAttribute("studentName", studentName);

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

            req.getRequestDispatcher("/WEB-INF/views/client/buy/student_buy.jsp").forward(req, resp);
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
                resp.sendRedirect(req.getContextPath() + "/buy?msg="
                        + java.net.URLEncoder.encode("Đã thêm sách vào danh sách chờ", "UTF-8"));
                return; // THÊM RETURN ĐỂ CHẶN LỖI 2 LẦN

            } else if ("removeFromWaitlist".equals(action)) {
                int bookId = Integer.parseInt(req.getParameter("bookID"));
                waitlist.remove(bookId);
                session.setAttribute("waitlist", waitlist);
                resp.sendRedirect(req.getContextPath() + "/buy?msg="
                        + java.net.URLEncoder.encode("Đã xóa sách khỏi danh sách chờ", "UTF-8"));
                return; // THÊM RETURN ĐỂ CHẶN LỖI 2 LẦN

            } else if ("updateWaitlistQty".equals(action)) {
                int bookId = Integer.parseInt(req.getParameter("bookID"));
                int quantity = Integer.parseInt(req.getParameter("quantity"));

                if (quantity < 1) {
                    respondWithBuyMessage(req, resp, false, "S\u1ed1 l\u01b0\u1ee3ng ph\u1ea3i l\u1edbn h\u01a1n 0.");
                    return;
                }

                WaitlistItem item = waitlist.get(bookId);
                if (item == null) {
                    respondWithBuyMessage(req, resp, false, "S\u00e1ch kh\u00f4ng c\u00f3 trong danh s\u00e1ch ch\u1edd.");
                    return;
                }

                item.setQuantity(quantity);
                session.setAttribute("waitlist", waitlist);
                respondWithBuyMessage(req, resp, true, "\u0110\u00e3 c\u1eadp nh\u1eadt s\u1ed1 l\u01b0\u1ee3ng.");
                return;

            } else if ("checkout".equals(action)) {
                // 1. Lấy mảng các bookID mà sinh viên đã tích chọn
                String[] selectedBooks = req.getParameterValues("selectedBooks");

                if (selectedBooks == null || selectedBooks.length == 0) {
                    resp.sendRedirect(req.getContextPath() + "/buy?error="
                            + java.net.URLEncoder.encode("Vui lòng tích chọn ít nhất 1 sách để đặt", "UTF-8"));
                    return; // Đã có sẵn return, rất chuẩn
                }

                Staff staff = RoleUtils.getLoggedStaff(req);
                Integer studentId = StudentContextUtils.resolveStudentId(staff);

                // KIỂM TRA BẢO MẬT: Tránh lỗi NullPointerException khi studentId bị rỗng
                if (studentId == null) {
                    resp.sendRedirect(req.getContextPath() + "/buy?error=" + java.net.URLEncoder
                            .encode("Lỗi: Không xác định được tài khoản sinh viên hợp lệ.", "UTF-8"));
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

                // 4. Chỉ xóa những sách đã đặt thành công khỏi danh sách chờ (giữ lại sách chưa
                // đặt)
                for (Integer bId : itemsToOrder.keySet()) {
                    waitlist.remove(bId);
                }
                session.setAttribute("waitlist", waitlist);

                resp.sendRedirect(req.getContextPath() + "/buy?msg="
                        + java.net.URLEncoder.encode("Đặt sách thành công. Vui lòng theo dõi mã đơn.", "UTF-8"));
                return; // THÊM RETURN ĐỂ CHẶN LỖI 2 LẦN
            } else {
                // Hành động không nhận diện được
                resp.sendRedirect(req.getContextPath() + "/buy?error="
                        + java.net.URLEncoder.encode("Lỗi: Hành động " + action + " không hợp lệ.", "UTF-8"));
                return; // THÊM RETURN ĐỂ CHẶN LỖI 2 LẦN
            }

        } catch (Exception e) {
            if (isAjaxRequest(req) && !resp.isCommitted()) {
                writeText(resp, HttpServletResponse.SC_BAD_REQUEST, "L\u1ed7i x\u1eed l\u00fd: " + e.getMessage());
                return;
            }
            // Check nếu server CHƯA gửi redirect nào thì mới được phép gửi
            if (!resp.isCommitted()) {
                resp.sendRedirect(req.getContextPath() + "/buy?error="
                        + java.net.URLEncoder.encode("Lỗi xử lý: " + e.getMessage(), "UTF-8"));
            } else {
                // Nếu đã gửi redirect rồi mà vẫn dính lỗi thì in ra Console để debug
                e.printStackTrace();
            }
        }
    }

    private void processCheckout(Map<Integer, WaitlistItem> itemsToOrder, int studentId, int staffId)
            throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Không thể kết nối đến cơ sở dữ liệu.");
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
                daoOrderDetail.insert(con,
                        new OrderDetail(orderId, item.getBookId(), item.getQuantity(), item.getUnitPrice()));
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

    private void respondWithBuyMessage(HttpServletRequest req, HttpServletResponse resp, boolean success, String message)
            throws IOException {
        if (isAjaxRequest(req)) {
            writeText(resp, success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST, message);
            return;
        }

        String key = success ? "msg" : "error";
        resp.sendRedirect(req.getContextPath() + "/buy?" + key + "="
                + java.net.URLEncoder.encode(message, "UTF-8"));
    }

    private boolean isAjaxRequest(HttpServletRequest req) {
        return "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"));
    }

    private void writeText(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().write(message == null ? "" : message);
    }

    // Copy hàm này từ BorrowController sang để đồng bộ cơ chế ánh xạ Sinh viên
}
