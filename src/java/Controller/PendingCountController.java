package Controller;

import Model.DAOBorrow;
import Utils.RoleUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Simple JSON API endpoint for AJAX polling. Returns {"pendingCount": N} for
 * admin notification badge.
 */
@WebServlet(name = "PendingCountAPI", urlPatterns = {"/api/pending-count"})
public class PendingCountController extends HttpServlet {

    private final DAOBorrow daoBorrow = new DAOBorrow();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Only admin/staff can access
        if (!RoleUtils.isAdmin(req) && !RoleUtils.isStaff(req)) {
            resp.setStatus(403);
            resp.getWriter().write("{\"error\":\"forbidden\"}");
            return;
        }

        try {
            int count = daoBorrow.countPending();
            resp.getWriter().write("{\"pendingCount\":" + count + "}");
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"db\"}");
        }
    }
}
