package controller.client;

import DAL.*;
import entities.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet(urlPatterns = {"/home/book"})
public class BookDetailController extends HttpServlet {

    private final DAOBook daoBook = new DAOBook();
    private final DAOCategory daoCate = new DAOCategory();
    private final DAOPublisher daoPub = new DAOPublisher();
    private final DAOAuthor daoAuthor = new DAOAuthor();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        try {
            int bookId = Integer.parseInt(idParam);
            Book book = daoBook.getById(bookId);
            if (book == null) {
                response.sendError(404, "Không tìm thấy sách");
                return;
            }

            Category category = daoCate.getById(book.getCategoryID());
            Publisher publisher = daoPub.getById(book.getPublisherID());
            List<String> authors = daoAuthor.getNamesByBookId(bookId);

            // cartBooks để hiển thị trong sidebar
            @SuppressWarnings("unchecked")
            Map<Integer, Integer> borrowCart = (Map<Integer, Integer>) request.getSession().getAttribute("borrowCart");
            @SuppressWarnings("unchecked")
            Map<Integer, Integer> buyCart = (Map<Integer, Integer>) request.getSession().getAttribute("buyCart");

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
            request.setAttribute("book", book);
            request.setAttribute("category", category);
            request.setAttribute("publisher", publisher);
            request.setAttribute("authors", authors);

            request.getRequestDispatcher("/WEB-INF/views/client/book/detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
