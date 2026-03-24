package Controller.client;

import Entities.BookFile;
import Model.DAOBookFile;
import Utils.RoleUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Locale;

@WebServlet(name = "StudentBookFileController", urlPatterns = {"/home/book/file"})
public class StudentBookFileController extends HttpServlet {

    private final DAOBookFile daoBookFile = new DAOBookFile();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!RoleUtils.isStudentOnly(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String idRaw = request.getParameter("id");
        if (idRaw == null || idRaw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int bookFileId = Integer.parseInt(idRaw);
            BookFile bookFile = daoBookFile.getActiveById(bookFileId);
            if (bookFile == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String targetUrl = resolveTargetUrl(bookFile.getFileUrl(), request);
            if (targetUrl == null) {
                redirectBackToBook(request, response, bookFile.getBookID(), "error",
                        "Liên kết tệp sách không hợp lệ.");
                return;
            }

            response.sendRedirect(targetUrl);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void redirectBackToBook(HttpServletRequest request, HttpServletResponse response,
            int bookId, String key, String value) throws IOException {
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/home/book?id=" + bookId + "&" + key + "=" + encoded + "#book-files");
    }

    private String resolveTargetUrl(String rawFileUrl, HttpServletRequest request) {
        if (rawFileUrl == null) {
            return null;
        }

        String fileUrl = rawFileUrl.trim().replace('\\', '/');
        if (fileUrl.isEmpty()) {
            return null;
        }

        String normalized = fileUrl.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("javascript:") || normalized.startsWith("data:") || normalized.startsWith("vbscript:")) {
            return null;
        }
        if (normalized.startsWith("//")) {
            return null;
        }

        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            return fileUrl;
        }
        if (normalized.matches("^[a-z][a-z0-9+.-]*:.*")) {
            return null;
        }

        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && fileUrl.startsWith(contextPath + "/")) {
            return fileUrl;
        }

        if (fileUrl.startsWith("/")) {
            return contextPath + fileUrl;
        }

        String relativePath = fileUrl.startsWith("./") ? fileUrl.substring(2) : fileUrl;
        return contextPath + "/" + relativePath;
    }
}
