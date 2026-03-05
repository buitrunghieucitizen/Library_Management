package Controller;

import Entities.Book;
import Entities.BookFile;
import Entities.Staff;
import Model.DAOBook;
import Model.DAOBookFile;
import Model.DBConnection;
import Utils.RoleUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "BookFileController", urlPatterns = {"/admin/bookfiles"})
public class BookFileController extends HttpServlet {

    private static final String BOOKFILES_PATH = "/admin/bookfiles";

    private final DAOBookFile daoBookFile = new DAOBookFile();
    private final DAOBook daoBook = new DAOBook();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!canManageBookFiles(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "create":
                    prepareForm(req, null);
                    req.getRequestDispatcher("/WEB-INF/views/bookfile/create.jsp").forward(req, resp);
                    break;
                case "edit":
                    showEdit(req, resp);
                    break;
                case "delete":
                    deleteBookFile(req, resp);
                    break;
                case "list":
                default:
                    showList(req, resp);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!canManageBookFiles(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "create";
        }

        try {
            if ("edit".equals(action)) {
                updateBookFile(req, resp);
                return;
            }
            if ("create".equals(action)) {
                createBookFile(req, resp);
                return;
            }
            resp.sendRedirect(req.getContextPath() + BOOKFILES_PATH);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private boolean canManageBookFiles(HttpServletRequest req) {
        return RoleUtils.isAdmin(req) || RoleUtils.isStaff(req);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        req.setAttribute("bookFiles", fetchBookFileRows());
        req.setAttribute("isAdmin", RoleUtils.isAdmin(req));
        req.getRequestDispatcher("/WEB-INF/views/bookfile/list.jsp").forward(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int bookFileId = Integer.parseInt(req.getParameter("id"));
        BookFile bookFile = daoBookFile.getById(bookFileId);
        if (bookFile == null) {
            resp.sendRedirect(req.getContextPath() + BOOKFILES_PATH + "?error=Khong%20tim%20thay%20bookfile");
            return;
        }

        prepareForm(req, bookFile);
        req.getRequestDispatcher("/WEB-INF/views/bookfile/edit.jsp").forward(req, resp);
    }

    private void createBookFile(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        BookFile bookFile = readBookFile(req, false);
       daoBookFile.insert(bookFile);
        resp.sendRedirect(req.getContextPath() + BOOKFILES_PATH + "?msg=Them%20bookfile%20thanh%20cong");
    }

    private void updateBookFile(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        BookFile bookFile = readBookFile(req, true);
        daoBookFile.update(bookFile);
        resp.sendRedirect(req.getContextPath() + BOOKFILES_PATH + "?msg=Cap%20nhat%20bookfile%20thanh%20cong");
    }

    private void deleteBookFile(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int bookFileId = Integer.parseInt(req.getParameter("id"));
        daoBookFile.delete(bookFileId);
        resp.sendRedirect(req.getContextPath() + BOOKFILES_PATH + "?msg=Xoa%20bookfile%20thanh%20cong");
    }

    private BookFile readBookFile(HttpServletRequest req, boolean hasId) {
        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            throw new IllegalStateException("Staff session is missing.");
        }
        long fileSize = 0;
        String rawFileSize = req.getParameter("fileSize");
        if (rawFileSize != null && !rawFileSize.trim().isEmpty()) {
            fileSize = Long.parseLong(rawFileSize);
        }

        BookFile bookFile = new BookFile(
                Integer.parseInt(req.getParameter("bookID")),
                staff.getStaffID(),
                req.getParameter("fileName"),
                req.getParameter("fileUrl"),
                req.getParameter("fileType"),
                fileSize);
        if (hasId) {
            bookFile.setBookFileID(Integer.parseInt(req.getParameter("bookFileID")));
            bookFile.setIsActive(req.getParameter("isActive") != null);
        }
        return bookFile;
    }

    private void prepareForm(HttpServletRequest req, BookFile bookFile) throws SQLException {
        List<Book> books = daoBook.getAll();
        req.setAttribute("books", books);
        req.setAttribute("bookFile", bookFile);
    }

    private List<BookFileRow> fetchBookFileRows() throws SQLException {
        String sql = "SELECT bf.BookFileID, bf.FileName, bf.FileUrl, bf.FileType, bf.FileSize, "
                + "CONVERT(varchar(19), bf.UploadAt, 120) AS UploadAt, bf.IsActive, "
                + "b.BookName, s.StaffName "
                + "FROM BookFile bf "
                + "JOIN Book b ON b.BookID = bf.BookID "
                + "JOIN Staff s ON s.StaffID = bf.StaffID "
                + "ORDER BY bf.BookFileID DESC";

        List<BookFileRow> rows = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new BookFileRow(
                        rs.getInt("BookFileID"),
                        rs.getString("BookName"),
                        rs.getString("StaffName"),
                        rs.getString("FileName"),
                        rs.getString("FileUrl"),
                        rs.getString("FileType"),
                        rs.getLong("FileSize"),
                        rs.getString("UploadAt"),
                        rs.getBoolean("IsActive")));
            }
        } finally {
            con.close();
        }

        return rows;
    }

    public static class BookFileRow {
        private final int bookFileID;
        private final String bookName;
        private final String staffName;
        private final String fileName;
        private final String fileUrl;
        private final String fileType;
        private final long fileSize;
        private final String uploadAt;
        private final boolean active;

        public BookFileRow(int bookFileID, String bookName, String staffName, String fileName, String fileUrl,
                String fileType, long fileSize, String uploadAt, boolean active) {
            this.bookFileID = bookFileID;
            this.bookName = bookName;
            this.staffName = staffName;
            this.fileName = fileName;
            this.fileUrl = fileUrl;
            this.fileType = fileType;
            this.fileSize = fileSize;
            this.uploadAt = uploadAt;
            this.active = active;
        }

        public int getBookFileID() {
            return bookFileID;
        }

        public String getBookName() {
            return bookName;
        }

        public String getStaffName() {
            return staffName;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public String getFileType() {
            return fileType;
        }

        public long getFileSize() {
            return fileSize;
        }

        public String getUploadAt() {
            return uploadAt;
        }

        public boolean isActive() {
            return active;
        }
    }
}

