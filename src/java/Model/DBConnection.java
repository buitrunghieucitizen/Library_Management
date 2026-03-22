package Model;

import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class DBConnection {

    private static final String HOST = "localhost";
    private static final int PORT = 1433;
    private static final String DB_NAME = "LibraryManager_V2";
    private static final String USER = "sa";
    private static final String PASS = "123456789";

    private static final DataSource dataSource;

    static {
        SQLServerConnectionPoolDataSource ds = new SQLServerConnectionPoolDataSource();
        ds.setServerName(HOST);
        ds.setPortNumber(PORT);
        ds.setDatabaseName(DB_NAME);
        ds.setUser(USER);
        ds.setPassword(PASS);
        ds.setEncrypt(false);
        ds.setTrustServerCertificate(true);
        dataSource = ds;
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.err.println("ERROR: Cannot connect to database!");
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Connection con = getConnection();
        if (con != null) {
            try {
                System.out.println("=== KẾT NỐI THÀNH CÔNG! ===");
                System.out.println("Database: " + con.getCatalog());
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("=== KẾT NỐI THẤT BẠI! ===");
        }
    }
}
