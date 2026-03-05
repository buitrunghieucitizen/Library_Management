package Model;

import Entities.BookPrice;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class DAOBookPrice {

    public List<BookPrice> getByBookId(int bookId) throws SQLException {
        String sql = "SELECT BookID, PriceID, StartDate, EndDate FROM BookPrice WHERE BookID = ?";
        List<BookPrice> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BookPrice(rs.getInt("BookID"), rs.getInt("PriceID"),
                            rs.getString("StartDate"), rs.getString("EndDate")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    public List<BookPrice> getAll() throws SQLException {
        String sql = "SELECT BookID, PriceID, StartDate, EndDate FROM BookPrice";
        List<BookPrice> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new BookPrice(rs.getInt("BookID"), rs.getInt("PriceID"),
                        rs.getString("StartDate"), rs.getString("EndDate")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public int insert(BookPrice bp) throws SQLException {
        String sql = "INSERT INTO BookPrice(BookID, PriceID, StartDate, EndDate) VALUES(?,?,?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bp.getBookID());
            ps.setInt(2, bp.getPriceID());
            ps.setString(3, bp.getStartDate());
            ps.setString(4, bp.getEndDate());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int update(BookPrice bp) throws SQLException {
        String sql = "UPDATE BookPrice SET EndDate=? WHERE BookID=? AND PriceID=? AND StartDate=?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bp.getEndDate());
            ps.setInt(2, bp.getBookID());
            ps.setInt(3, bp.getPriceID());
            ps.setString(4, bp.getStartDate());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int bookId, int priceId, String startDate) throws SQLException {
        String sql = "DELETE FROM BookPrice WHERE BookID = ? AND PriceID = ? AND StartDate = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, priceId);
            ps.setString(3, startDate);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public CurrentPriceInfo getCurrentPriceInfo(int bookId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try {
            return getCurrentPriceInfo(con, bookId);
        } finally {
            con.close();
        }
    }

    public CurrentPriceInfo getCurrentPriceInfo(Connection con, int bookId) throws SQLException {
        String sql = "SELECT TOP 1 bp.PriceID, bp.StartDate, p.Amount, p.Currency, p.Note "
                + "FROM BookPrice bp "
                + "JOIN Price p ON p.PriceID = bp.PriceID "
                + "WHERE bp.BookID = ? AND bp.StartDate <= CAST(GETDATE() AS DATE) "
                + "AND (bp.EndDate IS NULL OR bp.EndDate >= CAST(GETDATE() AS DATE)) "
                + "ORDER BY bp.StartDate DESC, bp.PriceID DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CurrentPriceInfo(
                            rs.getInt("PriceID"),
                            rs.getDate("StartDate").toLocalDate(),
                            rs.getDouble("Amount"),
                            rs.getString("Currency"),
                            rs.getString("Note"));
                }
            }
        }
        return null;
    }

    public double getCurrentSellingPrice(Connection con, int bookId) throws SQLException {
        CurrentPriceInfo info = getCurrentPriceInfo(con, bookId);
        return info == null ? 0 : info.getAmount();
    }

    public int insertCurrent(Connection con, int bookId, int priceId, LocalDate startDate) throws SQLException {
        String sql = "INSERT INTO BookPrice(BookID, PriceID, StartDate, EndDate) VALUES(?,?,?,NULL)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, priceId);
            ps.setDate(3, Date.valueOf(startDate));
            return ps.executeUpdate();
        }
    }

    public int closeCurrent(Connection con, int bookId, int priceId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "UPDATE BookPrice SET EndDate = ? WHERE BookID = ? AND PriceID = ? AND StartDate = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(endDate));
            ps.setInt(2, bookId);
            ps.setInt(3, priceId);
            ps.setDate(4, Date.valueOf(startDate));
            return ps.executeUpdate();
        }
    }

    public List<BookPriceRow> getBookPriceRows() throws SQLException {
        String sql = "SELECT b.BookID, b.BookName, b.Available, p.Amount, p.Currency, p.Note "
                + "FROM Book b "
                + "LEFT JOIN ("
                + "    SELECT bp.BookID, bp.PriceID, bp.StartDate, "
                + "           ROW_NUMBER() OVER (PARTITION BY bp.BookID ORDER BY bp.StartDate DESC, bp.PriceID DESC) AS rn "
                + "    FROM BookPrice bp "
                + "    WHERE bp.StartDate <= CAST(GETDATE() AS DATE) "
                + "      AND (bp.EndDate IS NULL OR bp.EndDate >= CAST(GETDATE() AS DATE))"
                + ") currentPrice ON currentPrice.BookID = b.BookID AND currentPrice.rn = 1 "
                + "LEFT JOIN Price p ON p.PriceID = currentPrice.PriceID "
                + "ORDER BY b.BookID DESC";

        List<BookPriceRow> rows = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new BookPriceRow(
                        rs.getInt("BookID"),
                        rs.getString("BookName"),
                        rs.getInt("Available"),
                        rs.getDouble("Amount"),
                        rs.getString("Currency"),
                        rs.getString("Note")));
            }
        } finally {
            con.close();
        }

        return rows;
    }

    public static class CurrentPriceInfo {
        private final int priceID;
        private final LocalDate startDate;
        private final double amount;
        private final String currency;
        private final String note;

        public CurrentPriceInfo(int priceID, LocalDate startDate, double amount, String currency, String note) {
            this.priceID = priceID;
            this.startDate = startDate;
            this.amount = amount;
            this.currency = currency;
            this.note = note;
        }

        public int getPriceID() {
            return priceID;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public double getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }

        public String getNote() {
            return note;
        }
    }

    public static class BookPriceRow {
        private final int bookID;
        private final String bookName;
        private final int available;
        private final double amount;
        private final String currency;
        private final String note;

        public BookPriceRow(int bookID, String bookName, int available, double amount, String currency, String note) {
            this.bookID = bookID;
            this.bookName = bookName;
            this.available = available;
            this.amount = amount;
            this.currency = currency;
            this.note = note;
        }

        public int getBookID() {
            return bookID;
        }

        public String getBookName() {
            return bookName;
        }

        public int getAvailable() {
            return available;
        }

        public double getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }

        public String getNote() {
            return note;
        }
    }
}
