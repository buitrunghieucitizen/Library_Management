package Controller.client;

import Entities.Book;
import Entities.Category;
import Entities.Publisher;
import Model.DAOAuthor;
import Model.DAOBook;
import Model.DAOCategory;
import Model.DAOPublisher;
import Utils.RoleUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "BookDetailController", urlPatterns = {"/home/book"})
public class BookDetailController extends HttpServlet {

    private final DAOBook daoBook = new DAOBook();
    private final DAOCategory daoCategory = new DAOCategory();
    private final DAOPublisher daoPublisher = new DAOPublisher();
    private final DAOAuthor daoAuthor = new DAOAuthor();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!RoleUtils.isStudentOnly(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String idRaw = request.getParameter("id");
        if (idRaw == null || idRaw.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int bookId = Integer.parseInt(idRaw);
            Book book = daoBook.getById(bookId);
            if (book == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Category category = daoCategory.getById(book.getCategoryID());
            Publisher publisher = daoPublisher.getById(book.getPublisherID());
            List<String> authors = daoAuthor.getNamesByBookId(bookId);
            String authorsText = (authors == null || authors.isEmpty())
                    ? "Khong co thong tin tac gia"
                    : String.join(", ", authors);

            request.setAttribute("book", book);
            request.setAttribute("category", category);
            request.setAttribute("publisher", publisher);
            request.setAttribute("authorsText", authorsText);
            request.getRequestDispatcher("/WEB-INF/views/client/book/detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
