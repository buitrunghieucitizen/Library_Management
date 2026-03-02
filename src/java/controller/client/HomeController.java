package controller.client;

import DAL.*;
import entities.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "Home", urlPatterns = {"/home"})
public class HomeController extends HttpServlet {

    private final DAOBook daoBook = new DAOBook();
    private final DAOCategory daoCate = new DAOCategory();
    private final DAOPublisher daoPub = new DAOPublisher();
    private final DAOBorrow daoBorrow = new DAOBorrow();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Lấy params filter
        String search = request.getParameter("search");
        String letter = request.getParameter("letter");
        String catParam = request.getParameter("categoryId");
        String pubParam = request.getParameter("publisherId");
        String author = request.getParameter("author");

        Integer categoryId = (catParam != null && !catParam.isEmpty()) ? Integer.parseInt(catParam) : null;
        Integer publisherId = (pubParam != null && !pubParam.isEmpty()) ? Integer.parseInt(pubParam) : null;

        try {
            // Books đã filter
            List<Book> books = daoBook.getFiltered(search, letter, categoryId, publisherId, author);

            // Dropdown data
            List<Category> categories = daoCate.getAll();
            List<Publisher> publishers = daoPub.getAll();

            // Holds của student hiện tại
            HttpSession session = request.getSession(false);
            Staff staff = (Staff) session.getAttribute("staff");
            // Lấy borrow đang Borrowing hoặc Overdue của student này
            List<Borrow> holds = daoBorrow.getActiveByStaffId(staff.getStaffID());

            request.setAttribute("books", books);
            request.setAttribute("categories", categories);
            request.setAttribute("publishers", publishers);
            request.setAttribute("holds", holds);

            // Giữ lại filter params để hiển thị trên form
            request.setAttribute("search", search != null ? search : "");
            request.setAttribute("letter", letter != null ? letter : "ALL");
            request.setAttribute("categoryId", catParam != null ? catParam : "");
            request.setAttribute("publisherId", pubParam != null ? pubParam : "");
            request.setAttribute("author", author != null ? author : "");

            // Thêm vào HomeController, sau phần holds, trước forward:
            @SuppressWarnings("unchecked")
            java.util.Map<Integer, Integer> borrowCart =
                    (java.util.Map<Integer, Integer>) session.getAttribute("borrowCart");
            @SuppressWarnings("unchecked")
            java.util.Map<Integer, Integer> buyCart =
                    (java.util.Map<Integer, Integer>) session.getAttribute("buyCart");

            List<Book> borrowBooks = new ArrayList<>();
            List<Book> buyBooks = new ArrayList<>();
            if (borrowCart != null)
                for (int id : borrowCart.keySet()) {
                    Book b = daoBook.getById(id);
                    if (b != null) borrowBooks.add(b);
                }
            if (buyCart != null)
                for (int id : buyCart.keySet()) {
                    Book b = daoBook.getById(id);
                    if (b != null) buyBooks.add(b);
                }

            request.setAttribute("borrowBooks", borrowBooks);
            request.setAttribute("buyBooks", buyBooks);
            request.setAttribute("borrowCount", borrowCart != null ? borrowCart.size() : 0);
            request.setAttribute("buyCount", buyCart != null ? buyCart.size() : 0);

            request.getRequestDispatcher("/WEB-INF/views/client/home/index.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}