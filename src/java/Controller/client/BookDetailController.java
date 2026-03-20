package Controller.client;

import Entities.Book;
import Entities.BookFile;
import Entities.Category;
import Entities.Publisher;
import Model.DAOAuthor;
import Model.DAOBook;
import Model.DAOBookFile;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@WebServlet(name = "BookDetailController", urlPatterns = {"/home/book"})
public class BookDetailController extends HttpServlet {

    private final DAOBook daoBook = new DAOBook();
    private final DAOCategory daoCategory = new DAOCategory();
    private final DAOPublisher daoPublisher = new DAOPublisher();
    private final DAOAuthor daoAuthor = new DAOAuthor();
    private final DAOBookFile daoBookFile = new DAOBookFile();

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
            List<BookFile> bookFiles = daoBookFile.getActiveByBookId(bookId);
            Map<Integer, String> bookFileSizeLabels = new HashMap<>();
            for (BookFile bookFile : bookFiles) {
                bookFileSizeLabels.put(bookFile.getBookFileID(), formatFileSize(bookFile.getFileSize()));
            }
            String authorsText = (authors == null || authors.isEmpty())
                    ? "Khong co thong tin tac gia"
                    : String.join(", ", authors);

            request.setAttribute("book", book);
            request.setAttribute("category", category);
            request.setAttribute("publisher", publisher);
            request.setAttribute("authorsText", authorsText);
            request.setAttribute("bookFiles", bookFiles);
            request.setAttribute("bookFileCount", bookFiles == null ? 0 : bookFiles.size());
            request.setAttribute("bookFileSizeLabels", bookFileSizeLabels);
            request.getRequestDispatcher("/WEB-INF/views/client/book/detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String formatFileSize(long fileSize) {
        if (fileSize <= 0) {
            return "";
        }

        String[] units = {"B", "KB", "MB", "GB"};
        double value = fileSize;
        int unitIndex = 0;
        while (value >= 1024 && unitIndex < units.length - 1) {
            value /= 1024;
            unitIndex++;
        }

        String pattern = value >= 10 || unitIndex == 0 ? "%.0f %s" : "%.1f %s";
        return String.format(Locale.US, pattern, value, units[unitIndex]);
    }
}
