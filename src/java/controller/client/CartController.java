package controller.client;

import DAL.DAOBook;
import DAL.DAOBorrow;
import entities.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@WebServlet(urlPatterns = {"/cart", "/cart/checkout"})
public class CartController extends HttpServlet {

    private final DAOBook   daoBook   = new DAOBook();
    private final DAOBorrow daoBorrow = new DAOBorrow();

    // ── Helpers ──────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> getBorrowCart(HttpSession s) {
        Map<Integer, Integer> c = (Map<Integer, Integer>) s.getAttribute("borrowCart");
        if (c == null) { c = new LinkedHashMap<>(); s.setAttribute("borrowCart", c); }
        return c;
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> getBuyCart(HttpSession s) {
        Map<Integer, Integer> c = (Map<Integer, Integer>) s.getAttribute("buyCart");
        if (c == null) { c = new LinkedHashMap<>(); s.setAttribute("buyCart", c); }
        return c;
    }

    private void syncCartBadge(HttpSession s,
                               Map<Integer,Integer> borrowCart,
                               Map<Integer,Integer> buyCart) {
        Map<Integer,Integer> all = new LinkedHashMap<>(borrowCart);
        all.putAll(buyCart);
        s.setAttribute("cart", all);
    }

    private void setError(HttpSession s, String msg) {
        s.setAttribute("cartError", msg);
    }

    // ── POST /cart ────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String servletPath = request.getServletPath();
        HttpSession session = request.getSession();

        if ("/cart/checkout".equals(servletPath)) {
            handleCheckout(request, response, session);
            return;
        }

        String action   = request.getParameter("action");
        String type     = request.getParameter("type");      // "borrow" | "buy"
        String redirect = request.getParameter("redirect");
        if (redirect == null || redirect.isEmpty()) redirect = "/home";

        int bookId;
        try {
            bookId = Integer.parseInt(request.getParameter("bookId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        Map<Integer, Integer> borrowCart = getBorrowCart(session);
        Map<Integer, Integer> buyCart    = getBuyCart(session);
        Staff staff = (Staff) session.getAttribute("staff");

        if ("add".equals(action)) {
            if ("buy".equals(type)) {
                // Mua: không giới hạn số lượng
                buyCart.put(bookId, 1);

            } else {
                // ── BORROW RULES ──

                // Rule 1: Tối đa 3 quyển trong giỏ mượn
                if (borrowCart.size() >= 3) {
                    setError(session, "Chỉ được mượn tối đa 3 quyển sách mỗi lần!");
                    response.sendRedirect(request.getContextPath() + redirect);
                    return;
                }

                // Rule 2: Không thêm sách đã có trong giỏ
                if (borrowCart.containsKey(bookId)) {
                    setError(session, "Sách này đã có trong giỏ mượn!");
                    response.sendRedirect(request.getContextPath() + redirect);
                    return;
                }

                try {
                    // Rule 3: Đang có sách mượn chưa trả (Borrowing/Overdue)
                    if (daoBorrow.hasActiveBorrow(staff.getStaffID())) {
                        setError(session, "Bạn đang có sách mượn chưa trả. Vui lòng trả sách trước khi mượn thêm!");
                        response.sendRedirect(request.getContextPath() + redirect);
                        return;
                    }

                    // Rule 4: Đang có request mượn pending chờ duyệt
                    if (daoBorrow.hasPendingBorrow(staff.getStaffID())) {
                        setError(session, "Bạn đang có yêu cầu mượn sách chờ xử lý. Vui lòng đợi xác nhận!");
                        response.sendRedirect(request.getContextPath() + redirect);
                        return;
                    }

                    // Rule 5: Check sách còn available không
                    Book book = daoBook.getById(bookId);
                    if (book == null || book.getAvailable() <= 0) {
                        setError(session, "Sách này hiện không còn để mượn!");
                        response.sendRedirect(request.getContextPath() + redirect);
                        return;
                    }

                } catch (SQLException e) {
                    setError(session, "Lỗi hệ thống khi kiểm tra: " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + redirect);
                    return;
                }

                borrowCart.put(bookId, 1);
            }

        } else if ("remove".equals(action)) {
            if ("buy".equals(type)) {
                buyCart.remove(bookId);
            } else {
                borrowCart.remove(bookId);
            }
        }

        syncCartBadge(session, borrowCart, buyCart);
        response.sendRedirect(request.getContextPath() + redirect);
    }

    // ── POST /cart/checkout ───────────────────────────────
    private void handleCheckout(HttpServletRequest request,
                                HttpServletResponse response,
                                HttpSession session)
            throws ServletException, IOException {

        String type = request.getParameter("type"); // "borrow" | "buy"
        String redirect = "/home";
        Staff staff = (Staff) session.getAttribute("staff");

        Map<Integer, Integer> borrowCart = getBorrowCart(session);
        Map<Integer, Integer> buyCart    = getBuyCart(session);

        if ("borrow".equals(type)) {
            if (borrowCart.isEmpty()) {
                response.sendRedirect(request.getContextPath() + redirect);
                return;
            }

            try {
                // Re-check tất cả rules trước khi tạo borrow

                // Rule: Đang mượn chưa trả
                if (daoBorrow.hasActiveBorrow(staff.getStaffID())) {
                    setError(session, "Bạn đang có sách mượn chưa trả!");
                    response.sendRedirect(request.getContextPath() + redirect);
                    return;
                }

                // Rule: Đang pending
                if (daoBorrow.hasPendingBorrow(staff.getStaffID())) {
                    setError(session, "Bạn đang có yêu cầu mượn chờ xử lý!");
                    response.sendRedirect(request.getContextPath() + redirect);
                    return;
                }

                // Rule: Tối đa 3 quyển
                if (borrowCart.size() > 3) {
                    setError(session, "Chỉ được mượn tối đa 3 quyển!");
                    response.sendRedirect(request.getContextPath() + redirect);
                    return;
                }

                // Re-check từng sách còn available không
                for (int bookId : borrowCart.keySet()) {
                    Book b = daoBook.getById(bookId);
                    if (b == null || b.getAvailable() <= 0) {
                        setError(session, "Sách \"" + (b != null ? b.getBookName() : "#" + bookId) + "\" vừa hết — vui lòng xóa khỏi giỏ!");
                        response.sendRedirect(request.getContextPath() + redirect);
                        return;
                    }
                }

                // Tạo Borrow + BorrowItems trong transaction
                String today   = LocalDate.now().toString();

                Borrow borrow = new Borrow(
                        staff.getStaffID(),  // StudentID (dùng chung Staff table)
                        1,                   // StaffID xử lý — dùng admin ID mặc định hoặc lấy từ config
                        today,
                        null,
                        "Pending"            // Chờ thư viện duyệt
                );

                List<BorrowItem> items = new ArrayList<>();
                for (int bookId : borrowCart.keySet()) {
                    items.add(new BorrowItem(0, bookId, 1));
                }

                daoBorrow.createBorrow(borrow, items);

                // Clear borrowCart sau khi checkout thành công
                session.removeAttribute("borrowCart");
                syncCartBadge(session, new LinkedHashMap<>(), buyCart);

                session.setAttribute("checkoutSuccess",
                        "Yêu cầu mượn " + items.size());
                response.sendRedirect(request.getContextPath() + "/home");

            } catch (SQLException e) {
                setError(session, "Lỗi hệ thống: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + redirect);
            }

        } else if ("buy".equals(type)) {
            if (buyCart.isEmpty()) {
                response.sendRedirect(request.getContextPath() + redirect);
                return;
            }
            // TODO: xử lý logic mua — tạo Order
            // Hiện tại placeholder
            session.setAttribute("checkoutSuccess", "Đặt mua thành công " + buyCart.size() + " sách!");
            session.removeAttribute("buyCart");
            syncCartBadge(session, borrowCart, new LinkedHashMap<>());
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}