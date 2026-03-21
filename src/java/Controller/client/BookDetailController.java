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
import ViewModel.BookFieldSupport;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@WebServlet(name = "BookDetailController", urlPatterns = {"/home/book"})
public class BookDetailController extends HttpServlet {

    private static final int RELATED_BOOK_LIMIT = 4;
    private static final String NO_AUTHOR_LABEL = "Kh\u00f4ng c\u00f3 th\u00f4ng tin t\u00e1c gi\u1ea3";
    private static final String STATUS_AVAILABLE = "C\u00f2n s\u00e1ch";
    private static final String STATUS_LOW = "S\u1eafp h\u1ebft";
    private static final String STATUS_OUT = "H\u1ebft s\u00e1ch";

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
            BookFieldSupport fieldSupport = daoBook.getFieldSupport();
            List<BookFile> bookFiles = daoBookFile.getActiveByBookId(bookId);
            Map<Integer, String> bookFileSizeLabels = new HashMap<>();
            for (BookFile bookFile : bookFiles) {
                bookFileSizeLabels.put(bookFile.getBookFileID(), formatFileSize(bookFile.getFileSize()));
            }

            String authorsText = (authors == null || authors.isEmpty())
                    ? NO_AUTHOR_LABEL
                    : String.join(", ", authors);
            List<Book> relatedBooks = resolveRelatedBooks(book);

            request.setAttribute("book", book);
            request.setAttribute("category", category);
            request.setAttribute("publisher", publisher);
            request.setAttribute("authorsText", authorsText);
            request.setAttribute("bookFieldSupport", fieldSupport);
            request.setAttribute("availabilityStatusKey", resolveAvailabilityStatusKey(book));
            request.setAttribute("availabilityStatusLabel", resolveAvailabilityStatusLabel(book));
            request.setAttribute("availabilityStatusNote", resolveAvailabilityStatusNote(book));
            request.setAttribute("bookDescriptionText", resolveBookDescription(book, category, publisher, authorsText));
            request.setAttribute("bookHasManualDescription", !isBlank(book.getDescription()));
            request.setAttribute("bookLocationText", resolveBookLocation(book));
            request.setAttribute("bookLocationProvided", !isBlank(book.getShelfLocation()));
            request.setAttribute("relatedBooks", relatedBooks);
            request.setAttribute("relatedBookCount", relatedBooks.size());
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

    private List<Book> resolveRelatedBooks(Book currentBook) throws SQLException {
        List<Book> candidates = daoBook.getAll();
        List<Book> relatedBooks = new ArrayList<>();
        for (Book candidate : candidates) {
            if (candidate.getBookID() == currentBook.getBookID()) {
                continue;
            }
            if (candidate.getCategoryID() == currentBook.getCategoryID()
                    || candidate.getPublisherID() == currentBook.getPublisherID()) {
                relatedBooks.add(candidate);
            }
        }

        relatedBooks.sort(Comparator
                .comparingInt((Book candidate) -> relationScore(currentBook, candidate))
                .thenComparingInt(this::availabilityRank)
                .thenComparing(Book::getBookName, String.CASE_INSENSITIVE_ORDER));

        if (relatedBooks.size() <= RELATED_BOOK_LIMIT) {
            return relatedBooks;
        }
        return new ArrayList<>(relatedBooks.subList(0, RELATED_BOOK_LIMIT));
    }

    private int relationScore(Book currentBook, Book candidate) {
        boolean sameCategory = candidate.getCategoryID() == currentBook.getCategoryID();
        boolean samePublisher = candidate.getPublisherID() == currentBook.getPublisherID();
        if (sameCategory && samePublisher) {
            return 0;
        }
        if (sameCategory) {
            return 1;
        }
        return 2;
    }

    private int availabilityRank(Book book) {
        if (book.getAvailable() <= 0) {
            return 2;
        }
        return isLowStock(book) ? 1 : 0;
    }

    private String resolveAvailabilityStatusKey(Book book) {
        if (book.getAvailable() <= 0) {
            return "out";
        }
        return isLowStock(book) ? "low" : "ok";
    }

    private String resolveAvailabilityStatusLabel(Book book) {
        if (book.getAvailable() <= 0) {
            return STATUS_OUT;
        }
        return isLowStock(book) ? STATUS_LOW : STATUS_AVAILABLE;
    }

    private String resolveAvailabilityStatusNote(Book book) {
        if (book.getAvailable() <= 0) {
            return "Hi\u1ec7n kh\u00f4ng c\u00f2n b\u1ea3n s\u1eb5n s\u00e0ng \u0111\u1ec3 m\u01b0\u1ee3n. "
                    + "B\u1ea1n c\u00f3 th\u1ec3 theo d\u00f5i file s\u1ed1 ho\u1eb7c quay l\u1ea1i sau.";
        }
        if (isLowStock(book)) {
            return "Kho ch\u1ec9 c\u00f2n \u00edt b\u1ea3n s\u1eb5n s\u00e0ng. "
                    + "N\u00ean m\u01b0\u1ee3n s\u1edbm ho\u1eb7c th\u00eam v\u00e0o danh s\u00e1ch c\u1ea7n mua.";
        }
        return "S\u00e1ch \u0111ang c\u00f3 s\u1eb5n \u0111\u1ec3 m\u01b0\u1ee3n tr\u1ef1c ti\u1ebfp t\u1eeb m\u00e0n h\u00ecnh n\u00e0y.";
    }

    private String resolveBookDescription(Book book, Category category, Publisher publisher, String authorsText) {
        if (!isBlank(book.getDescription())) {
            return book.getDescription().trim();
        }

        List<String> fragments = new ArrayList<>();
        fragments.add("\u0110\u1ea7u s\u00e1ch n\u00e0y hi\u1ec7n thu\u1ed9c kho th\u01b0 vi\u1ec7n sinh vi\u00ean.");
        if (!isBlank(authorsText) && !NO_AUTHOR_LABEL.equals(authorsText)) {
            fragments.add("T\u00e1c gi\u1ea3: " + authorsText + ".");
        }
        if (category != null && !isBlank(category.getCategoryName())) {
            fragments.add("Th\u1ec3 lo\u1ea1i: " + category.getCategoryName() + ".");
        }
        if (publisher != null && !isBlank(publisher.getPublisherName())) {
            fragments.add("Nh\u00e0 xu\u1ea5t b\u1ea3n: " + publisher.getPublisherName() + ".");
        }
        fragments.add("H\u1ec7 th\u1ed1ng hi\u1ec7n c\u00f3 " + book.getQuantity() + " cu\u1ed1n, c\u00f2n "
                + book.getAvailable() + " cu\u1ed1n s\u1eb5n s\u00e0ng.");
        return String.join(" ", fragments);
    }

    private String resolveBookLocation(Book book) {
        if (!isBlank(book.getShelfLocation())) {
            return book.getShelfLocation().trim();
        }
        return "Th\u01b0 vi\u1ec7n ch\u01b0a c\u1eadp nh\u1eadt v\u1ecb tr\u00ed k\u1ec7 cho \u0111\u1ea7u s\u00e1ch n\u00e0y.";
    }

    private boolean isLowStock(Book book) {
        if (book.getAvailable() <= 0) {
            return false;
        }
        int threshold = Math.max(1, Math.min(3, (int) Math.ceil(book.getQuantity() * 0.2)));
        return book.getAvailable() <= threshold;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
