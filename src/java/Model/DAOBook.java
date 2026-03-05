package Model;

import Entities.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAOBook {

    public List<Book> getAll() throws SQLException {
        String sql = "SELECT BookID, BookName, Quantity, Available, CategoryID, PublisherID FROM Book ORDER BY BookID DESC";
        List<Book> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Book b = new Book(
                        rs.getInt("BookID"),
                        rs.getString("BookName"),
                        rs.getInt("Quantity"),
                        rs.getInt("Available"),
                        rs.getInt("CategoryID"),
                        rs.getInt("PublisherID"));
                list.add(b);
            }
        } finally {
            con.close();
        }
        return list;
    }

    public Book getById(int id) throws SQLException {
        String sql = "SELECT BookID, BookName, Quantity, Available, CategoryID, PublisherID FROM Book WHERE BookID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                            rs.getInt("BookID"),
                            rs.getString("BookName"),
                            rs.getInt("Quantity"),
                            rs.getInt("Available"),
                            rs.getInt("CategoryID"),
                            rs.getInt("PublisherID"));
                }
            }
        } finally {
            con.close();
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
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT b.BookID, b.BookName, b.Quantity, b.Available, b.CategoryID, b.PublisherID "
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
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Book(
                            rs.getInt("BookID"),
                            rs.getString("BookName"),
                            rs.getInt("Quantity"),
                            rs.getInt("Available"),
                            rs.getInt("CategoryID"),
                            rs.getInt("PublisherID")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    public int insert(Connection con, Book b) throws SQLException {
        String sql = "INSERT INTO Book(BookName, Quantity, Available, CategoryID, PublisherID) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getBookName());
            ps.setInt(2, b.getQuantity());
            ps.setInt(3, b.getAvailable());
            ps.setInt(4, b.getCategoryID());
            ps.setInt(5, b.getPublisherID());

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
        String sql = "UPDATE Book SET BookName=?, Quantity=?, Available=?, CategoryID=?, PublisherID=? WHERE BookID=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getBookName());
            ps.setInt(2, b.getQuantity());
            ps.setInt(3, b.getAvailable());
            ps.setInt(4, b.getCategoryID());
            ps.setInt(5, b.getPublisherID());
            ps.setInt(6, b.getBookID());
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
}
