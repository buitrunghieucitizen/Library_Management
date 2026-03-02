package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=LibraryManager_V2;"
            + "encrypt=false;"
            + "trustServerCertificate=true";

    private static final String USER = "sa";
    private static final String PASS = "123456789";

    public static Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: SQL Server JDBC Driver not found!");
            System.out.println("Please add sqljdbc42.jar to Libraries.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("ERROR: Cannot connect to database!");
            System.out.println("URL: " + URL);
            e.printStackTrace();
        }
        return null;
    }

    // =============================
    // TEST CONNECTION
    // =============================
    public static void main(String[] args) {
        Connection con = getConnection();
        if (con != null) {
            try {
                System.out.println("=== KET NOI THANH CONG! ===");
                System.out.println("Database: " + con.getCatalog());
                System.out.println("AutoCommit: " + con.getAutoCommit());
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("=== KET NOI THAT BAI! ===");
        }
    }
}