package Model;

import Entities.BookFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAOBookFile {

    public List<BookFile> getAll() throws SQLException {
        String sql = "SELECT BookFileID, BookID, StaffID, FileName, FileUrl, FileType, FileSize, UploadAt, IsActive FROM BookFile ORDER BY BookFileID DESC";
        List<BookFile> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new BookFile(rs.getInt("BookFileID"), rs.getInt("BookID"), rs.getInt("StaffID"),
                        rs.getString("FileName"), rs.getString("FileUrl"), rs.getString("FileType"),
                        rs.getLong("FileSize"), rs.getString("UploadAt"), rs.getBoolean("IsActive")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public List<BookFile> getByBookId(int bookId) throws SQLException {
        String sql = "SELECT BookFileID, BookID, StaffID, FileName, FileUrl, FileType, FileSize, UploadAt, IsActive FROM BookFile WHERE BookID = ?";
        List<BookFile> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BookFile(rs.getInt("BookFileID"), rs.getInt("BookID"), rs.getInt("StaffID"),
                            rs.getString("FileName"), rs.getString("FileUrl"), rs.getString("FileType"),
                            rs.getLong("FileSize"), rs.getString("UploadAt"), rs.getBoolean("IsActive")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    public BookFile getById(int id) throws SQLException {
        String sql = "SELECT BookFileID, BookID, StaffID, FileName, FileUrl, FileType, FileSize, UploadAt, IsActive FROM BookFile WHERE BookFileID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BookFile(rs.getInt("BookFileID"), rs.getInt("BookID"), rs.getInt("StaffID"),
                            rs.getString("FileName"), rs.getString("FileUrl"), rs.getString("FileType"),
                            rs.getLong("FileSize"), rs.getString("UploadAt"), rs.getBoolean("IsActive"));
                }
            }
        } finally {
            con.close();
        }
        return null;
    }

    public int insert(BookFile bf) throws SQLException {
        String sql = "INSERT INTO BookFile(BookID, StaffID, FileName, FileUrl, FileType, FileSize) VALUES(?,?,?,?,?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bf.getBookID());
            ps.setInt(2, bf.getStaffID());
            ps.setString(3, bf.getFileName());
            ps.setString(4, bf.getFileUrl());
            ps.setString(5, bf.getFileType());
            ps.setLong(6, bf.getFileSize());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        bf.setBookFileID(keys.getInt(1));
                    }
                }
            }
            return affected;
        } finally {
            con.close();
        }
    }

    public int update(BookFile bf) throws SQLException {
        String sql = "UPDATE BookFile SET BookID=?, StaffID=?, FileName=?, FileUrl=?, FileType=?, FileSize=?, IsActive=? WHERE BookFileID=?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bf.getBookID());
            ps.setInt(2, bf.getStaffID());
            ps.setString(3, bf.getFileName());
            ps.setString(4, bf.getFileUrl());
            ps.setString(5, bf.getFileType());
            ps.setLong(6, bf.getFileSize());
            ps.setBoolean(7, bf.getIsActive());
            ps.setInt(8, bf.getBookFileID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM BookFile WHERE BookFileID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public List<BookFileRow> getBookFileRows() throws SQLException {
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
