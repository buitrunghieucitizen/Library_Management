package Utils;

import Model.DBConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebListener
public class AppStartupListener implements ServletContextListener {

    private static final String BORROW_STATUS_CONSTRAINT_NAME = "CK_Borrow_Status";
    private static final String BORROW_STATUS_CONSTRAINT_SQL
            = "ALTER TABLE [dbo].[Borrow] WITH CHECK ADD CONSTRAINT [CK_Borrow_Status] "
            + "CHECK (([Status]='Rejected' OR [Status]='Returned' OR [Status]='Overdue' "
            + "OR [Status]='Borrowing' OR [Status]='Pending' OR [Status]='ReturnRequested'))";
    private static final int MAX_BOOK_COVER_BACKFILL = 25;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String realPath = sce.getServletContext().getRealPath("/");
        EnvLoader.setWebappPath(realPath);
        EnvLoader.load();
        backfillMissingBookImageUrls();
        repairLegacyBorrowStatusConstraint();
        System.out.println("[App] .env loaded. Google configured: " + GoogleOAuthConfig.isConfigured()
                + ", Email configured: " + EmailConfig.isConfigured());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    private void repairLegacyBorrowStatusConstraint() {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            System.err.println("[App] Skip Borrow.Status constraint repair: no database connection.");
            return;
        }

        try {
            if (!borrowStatusColumnExists(con)) {
                return;
            }

            con.setAutoCommit(false);

            ConstraintState state = inspectBorrowStatusConstraints(con);
            boolean changed = false;

            for (String constraintName : state.constraintsToDrop) {
                try (Statement st = con.createStatement()) {
                    st.executeUpdate("ALTER TABLE [dbo].[Borrow] DROP CONSTRAINT [" + escapeSqlIdentifier(constraintName) + "]");
                }
                changed = true;
                System.out.println("[App] Dropped legacy Borrow.Status constraint: " + constraintName);
            }

            if (!state.hasExpectedConstraint) {
                try (Statement st = con.createStatement()) {
                    st.executeUpdate(BORROW_STATUS_CONSTRAINT_SQL);
                    st.executeUpdate("ALTER TABLE [dbo].[Borrow] CHECK CONSTRAINT [" + BORROW_STATUS_CONSTRAINT_NAME + "]");
                }
                changed = true;
                System.out.println("[App] Ensured Borrow.Status accepts pending and return-request states.");
            }

            con.commit();
            if (!changed) {
                System.out.println("[App] Borrow.Status constraint already up to date.");
            }
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("[App] Failed to rollback Borrow.Status constraint repair.");
                rollbackEx.printStackTrace();
            }
            System.err.println("[App] Failed to repair Borrow.Status constraint.");
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    private void backfillMissingBookImageUrls() {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            System.err.println("[App] Skip OpenLibrary cover sync: no database connection.");
            return;
        }

        try {
            if (!bookImageUrlColumnExists(con)) {
                return;
            }

            List<BookCoverSeed> books = loadBooksMissingImageUrls(con);
            if (books.isEmpty()) {
                return;
            }

            int updatedCount = 0;
            for (BookCoverSeed book : books) {
                try {
                    String imageUrl = OpenLibraryCoverService.findCoverUrl(book.bookName, book.primaryAuthor);
                    if (isBlank(imageUrl)) {
                        continue;
                    }
                    updatedCount += updateBookImageUrlIfBlank(con, book.bookId, imageUrl);
                } catch (IOException e) {
                    System.err.println("[App] OpenLibrary lookup failed for \"" + book.bookName + "\": " + e.getMessage());
                }
            }

            if (updatedCount > 0) {
                System.out.println("[App] Backfilled " + updatedCount + " missing book cover URLs from OpenLibrary.");
            }
        } catch (SQLException e) {
            System.err.println("[App] Failed to backfill missing book cover URLs.");
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    private boolean borrowStatusColumnExists(Connection con) throws SQLException {
        String sql = "SELECT 1 "
                + "FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME = 'Borrow' AND COLUMN_NAME = 'Status'";
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }

    private boolean bookImageUrlColumnExists(Connection con) throws SQLException {
        String sql = "SELECT 1 "
                + "FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME = 'Book' AND COLUMN_NAME = 'ImageUrl'";
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }

    private List<BookCoverSeed> loadBooksMissingImageUrls(Connection con) throws SQLException {
        String sql = "SELECT TOP " + MAX_BOOK_COVER_BACKFILL + " "
                + "b.BookID, b.BookName, "
                + "(SELECT TOP 1 a.AuthorName "
                + " FROM [dbo].[BookAuthor] ba "
                + " JOIN [dbo].[Author] a ON a.AuthorID = ba.AuthorID "
                + " WHERE ba.BookID = b.BookID "
                + " ORDER BY a.AuthorName) AS PrimaryAuthor "
                + "FROM [dbo].[Book] b "
                + "WHERE b.ImageUrl IS NULL OR LTRIM(RTRIM(b.ImageUrl)) = '' "
                + "ORDER BY b.BookID";

        List<BookCoverSeed> books = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.add(new BookCoverSeed(
                        rs.getInt("BookID"),
                        rs.getString("BookName"),
                        rs.getString("PrimaryAuthor")));
            }
        }
        return books;
    }

    private int updateBookImageUrlIfBlank(Connection con, int bookId, String imageUrl) throws SQLException {
        String sql = "UPDATE [dbo].[Book] "
                + "SET ImageUrl = ? "
                + "WHERE BookID = ? AND (ImageUrl IS NULL OR LTRIM(RTRIM(ImageUrl)) = '')";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, imageUrl);
            ps.setInt(2, bookId);
            return ps.executeUpdate();
        }
    }

    private ConstraintState inspectBorrowStatusConstraints(Connection con) throws SQLException {
        String sql = "SELECT cc.name, cc.definition "
                + "FROM sys.check_constraints cc "
                + "JOIN sys.columns c ON c.object_id = cc.parent_object_id "
                + "AND c.column_id = cc.parent_column_id "
                + "JOIN sys.tables t ON t.object_id = cc.parent_object_id "
                + "JOIN sys.schemas s ON s.schema_id = t.schema_id "
                + "WHERE s.name = 'dbo' AND t.name = 'Borrow' AND c.name = 'Status'";

        ConstraintState state = new ConstraintState();
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                String definition = rs.getString("definition");
                if (isExpectedBorrowStatusConstraint(name, definition)) {
                    state.hasExpectedConstraint = true;
                } else {
                    state.constraintsToDrop.add(name);
                }
            }
        }
        return state;
    }

    private boolean isExpectedBorrowStatusConstraint(String name, String definition) {
        if (name == null || definition == null || !BORROW_STATUS_CONSTRAINT_NAME.equalsIgnoreCase(name)) {
            return false;
        }
        String normalizedDefinition = definition.toUpperCase();
        return normalizedDefinition.contains("'PENDING'")
                && normalizedDefinition.contains("'REJECTED'")
                && normalizedDefinition.contains("'BORROWING'")
                && normalizedDefinition.contains("'OVERDUE'")
                && normalizedDefinition.contains("'RETURNED'")
                && normalizedDefinition.contains("'RETURNREQUESTED'");
    }

    private String escapeSqlIdentifier(String identifier) {
        return identifier == null ? "" : identifier.replace("]", "]]");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static final class ConstraintState {

        private boolean hasExpectedConstraint;
        private final List<String> constraintsToDrop = new ArrayList<>();
    }

    private static final class BookCoverSeed {

        private final int bookId;
        private final String bookName;
        private final String primaryAuthor;

        private BookCoverSeed(int bookId, String bookName, String primaryAuthor) {
            this.bookId = bookId;
            this.bookName = bookName;
            this.primaryAuthor = primaryAuthor;
        }
    }
}
