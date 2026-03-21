package Model;

import Entities.Book;
import ViewModel.BookFieldSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAOBook {

    public List<Book> getAll() throws SQLException {
        List<Book> list = new ArrayList<>();
        try (Connection con = openConnection()) {
            String sql = buildBookSelect(detectFieldSupport(con)) + " ORDER BY BookID DESC";
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapBook(rs));
                }
            }
        }
        return list;
    }

    public Book getById(int id) throws SQLException {
        try (Connection con = openConnection()) {
            String sql = buildBookSelect(detectFieldSupport(con)) + " WHERE BookID = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapBook(rs);
                    }
                }
            }
        }
        return null;
    }

    public int insert(Book b) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try {
            return insert(con, b);
        } finally {
            con.close();
        }
    }

    public int update(Book b) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try {
            return update(con, b);
        } finally {
            con.close();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM Book WHERE BookID = ?";
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

    public List<Book> getFiltered(String search, String letter, Integer categoryId,
            Integer publisherId, String authorName) throws SQLException {
        Connection con = openConnection();
        try {
            BookFieldSupport support = detectFieldSupport(con);
            StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT b.BookID, b.BookName, b.Quantity, b.Available, b.CategoryID, b.PublisherID "
                + (support.isDescriptionSupported()
                        ? ", b.Description"
                        : ", CAST(NULL AS NVARCHAR(1000)) AS Description ")
                + (support.isShelfLocationSupported()
                        ? ", b.ShelfLocation"
                        : ", CAST(NULL AS NVARCHAR(100)) AS ShelfLocation ")
                + (support.isImageUrlSupported()
                        ? ", b.ImageUrl "
                        : ", CAST(NULL AS NVARCHAR(500)) AS ImageUrl ")
                + "FROM Book b "
                + "LEFT JOIN BookAuthor ba ON b.BookID = ba.BookID "
                + "LEFT JOIN Author a ON ba.AuthorID = a.AuthorID "
                + "WHERE 1=1 ");
            List<Object> params = new ArrayList<>();

            if (search != null && !search.trim().isEmpty()) {
                sql.append("AND b.BookName LIKE ? ");
                params.add("%" + search.trim() + "%");
            }
            if (letter != null && !letter.trim().isEmpty() && !"ALL".equalsIgnoreCase(letter)) {
                sql.append("AND b.BookName LIKE ? ");
                params.add(letter.trim() + "%");
            }
            if (categoryId != null) {
                sql.append("AND b.CategoryID = ? ");
                params.add(categoryId);
            }
            if (publisherId != null) {
                sql.append("AND b.PublisherID = ? ");
                params.add(publisherId);
            }
            if (authorName != null && !authorName.trim().isEmpty()) {
                sql.append("AND a.AuthorName LIKE ? ");
                params.add("%" + authorName.trim() + "%");
            }

            sql.append("ORDER BY b.BookName ASC");

            List<Book> list = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(mapBook(rs));
                    }
                }
            }
            return list;
        } finally {
            con.close();
        }
    }

    public int insert(Connection con, Book b) throws SQLException {
        BookFieldSupport support = detectFieldSupport(con);
        StringBuilder sql = new StringBuilder(
                "INSERT INTO Book(BookName, Quantity, Available, CategoryID, PublisherID");
        List<Object> params = new ArrayList<>();
        params.add(b.getBookName());
        params.add(b.getQuantity());
        params.add(b.getAvailable());
        params.add(b.getCategoryID());
        params.add(b.getPublisherID());

        if (support.isDescriptionSupported()) {
            sql.append(", Description");
            params.add(emptyToNull(b.getDescription()));
        }
        if (support.isShelfLocationSupported()) {
            sql.append(", ShelfLocation");
            params.add(emptyToNull(b.getShelfLocation()));
        }
        if (support.isImageUrlSupported()) {
            sql.append(", ImageUrl");
            params.add(emptyToNull(b.getImageUrl()));
        }
        sql.append(") VALUES (?");
        for (int i = 1; i < params.size(); i++) {
            sql.append(",?");
        }
        sql.append(")");

        try (PreparedStatement ps = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        b.setBookID(keys.getInt(1));
                    }
                }
            }
            return affected;
        }
    }

    public int update(Connection con, Book b) throws SQLException {
        BookFieldSupport support = detectFieldSupport(con);
        StringBuilder sql = new StringBuilder(
                "UPDATE Book SET BookName=?, Quantity=?, Available=?, CategoryID=?, PublisherID=?");
        List<Object> params = new ArrayList<>();
        params.add(b.getBookName());
        params.add(b.getQuantity());
        params.add(b.getAvailable());
        params.add(b.getCategoryID());
        params.add(b.getPublisherID());

        if (support.isDescriptionSupported()) {
            sql.append(", Description=?");
            params.add(emptyToNull(b.getDescription()));
        }
        if (support.isShelfLocationSupported()) {
            sql.append(", ShelfLocation=?");
            params.add(emptyToNull(b.getShelfLocation()));
        }
        if (support.isImageUrlSupported()) {
            sql.append(", ImageUrl=?");
            params.add(emptyToNull(b.getImageUrl()));
        }

        sql.append(" WHERE BookID=?");
        params.add(b.getBookID());

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            return ps.executeUpdate();
        }
    }

    public int getAvailable(Connection con, int bookId) throws SQLException {
        String sql = "SELECT Available FROM Book WHERE BookID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Available");
                }
            }
        }
        throw new SQLException("Khong tim thay sach id=" + bookId);
    }

    public int decreaseAvailable(Connection con, int bookId, int quantity) throws SQLException {
        String sql = "UPDATE Book SET Available = Available - ? WHERE BookID = ? AND Available >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, bookId);
            ps.setInt(3, quantity);
            return ps.executeUpdate();
        }
    }

    public int increaseAvailable(Connection con, int bookId, int quantity) throws SQLException {
        String sql = "UPDATE Book SET Available = Available + ? WHERE BookID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, bookId);
            return ps.executeUpdate();
        }
    }

    public int decreaseStockAndAvailable(Connection con, int bookId, int quantity) throws SQLException {
        String sql = "UPDATE Book "
                + "SET Quantity = Quantity - ?, Available = Available - ? "
                + "WHERE BookID = ? AND Quantity >= ? AND Available >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, quantity);
            ps.setInt(3, bookId);
            ps.setInt(4, quantity);
            ps.setInt(5, quantity);
            return ps.executeUpdate();
        }
    }

    public BookFieldSupport getFieldSupport() throws SQLException {
        try (Connection con = openConnection()) {
            return detectFieldSupport(con);
        }
    }

    private Connection openConnection() throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        return con;
    }

    private BookFieldSupport detectFieldSupport(Connection con) throws SQLException {
        return new BookFieldSupport(
                hasColumn(con, "Description"),
                hasColumn(con, "ShelfLocation"),
                hasColumn(con, "ImageUrl"));
    }

    private boolean hasColumn(Connection con, String columnName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME = 'Book' AND COLUMN_NAME = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private String buildBookSelect(BookFieldSupport support) {
        return "SELECT BookID, BookName, Quantity, Available, CategoryID, PublisherID"
                + (support.isDescriptionSupported()
                        ? ", Description"
                        : ", CAST(NULL AS NVARCHAR(1000)) AS Description")
                + (support.isShelfLocationSupported()
                        ? ", ShelfLocation"
                        : ", CAST(NULL AS NVARCHAR(100)) AS ShelfLocation")
                + (support.isImageUrlSupported()
                        ? ", ImageUrl"
                        : ", CAST(NULL AS NVARCHAR(500)) AS ImageUrl")
                + " FROM Book";
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("BookID"),
                rs.getString("BookName"),
                rs.getInt("Quantity"),
                rs.getInt("Available"),
                rs.getInt("CategoryID"),
                rs.getInt("PublisherID"),
                rs.getString("Description"),
                rs.getString("ShelfLocation"),
                rs.getString("ImageUrl"));
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
