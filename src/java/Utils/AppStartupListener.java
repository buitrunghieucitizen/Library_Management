package Utils;

import Model.DBConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
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
            + "OR [Status]='Borrowing' OR [Status]='Pending'))";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String realPath = sce.getServletContext().getRealPath("/");
        EnvLoader.setWebappPath(realPath);
        EnvLoader.load();
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
                System.out.println("[App] Ensured Borrow.Status accepts Pending/Rejected states.");
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

    private boolean borrowStatusColumnExists(Connection con) throws SQLException {
        String sql = "SELECT 1 "
                + "FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME = 'Borrow' AND COLUMN_NAME = 'Status'";
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next();
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
                && normalizedDefinition.contains("'RETURNED'");
    }

    private String escapeSqlIdentifier(String identifier) {
        return identifier == null ? "" : identifier.replace("]", "]]");
    }

    private static final class ConstraintState {

        private boolean hasExpectedConstraint;
        private final List<String> constraintsToDrop = new ArrayList<>();
    }
}
