package Controller;

import Entities.Role;
import Entities.Staff;
import Entities.StaffRole;
import Model.DAORole;
import Model.DAOStaff;
import Model.DAOStaffRole;
import Utils.RoleUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet(name = "StaffController", urlPatterns = {"/staffs"})
public class StaffController extends HttpServlet {

    private final DAOStaff daoStaff = new DAOStaff();
    private final DAOStaffRole daoStaffRole = new DAOStaffRole();
    private final DAORole daoRole = new DAORole();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!RoleUtils.isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Access Denied");
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
                    req.getRequestDispatcher("/WEB-INF/views/staff/create.jsp").forward(req, resp);
                    break;
                case "edit":
                    showEdit(req, resp);
                    break;
                case "delete":
                    deleteStaff(req, resp);
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
        if (!RoleUtils.isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Access Denied");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "create";
        }

        try {
            if ("edit".equals(action)) {
                updateStaff(req, resp);
                return;
            }

            if ("create".equals(action)) {
                createStaff(req, resp);
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/staffs?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        List<Staff> staffs = daoStaff.getAll();
        List<Role> roles = daoRole.getAll();
        List<StaffListRow> rows = new ArrayList<>();

        for (Staff staff : staffs) {
            List<StaffRole> staffRoles = daoStaffRole.getByStaffId(staff.getStaffID());
            rows.add(new StaffListRow(staff, joinRoleNames(staffRoles, roles)));
        }

        req.setAttribute("staffRows", rows);
        req.getRequestDispatcher("/WEB-INF/views/staff/list.jsp").forward(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int staffId = Integer.parseInt(req.getParameter("id"));
        Staff staff = daoStaff.getById(staffId);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/staffs?action=list&error=Khong tim thay tai khoan");
            return;
        }

        prepareForm(req, staff);
        req.getRequestDispatcher("/WEB-INF/views/staff/edit.jsp").forward(req, resp);
    }

    private void createStaff(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        Staff staff = readStaff(req, false);
        int[] roleIds = parseRoleIds(req);
        if (roleIds.length == 0) {
            forwardFormError(req, resp, null, "/WEB-INF/views/staff/create.jsp", "Phai chon it nhat 1 role.");
            return;
        }

        daoStaff.insert(staff);
        syncRoles(staff.getStaffID(), roleIds);
        resp.sendRedirect(req.getContextPath() + "/staffs?action=list&msg=Tao%20tai%20khoan%20thanh%20cong");
    }

    private void updateStaff(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        Staff staff = readStaff(req, true);
        int[] roleIds = parseRoleIds(req);
        if (roleIds.length == 0) {
            forwardFormError(req, resp, staff, "/WEB-INF/views/staff/edit.jsp", "Phai chon it nhat 1 role.");
            return;
        }

        daoStaff.update(staff);
        syncRoles(staff.getStaffID(), roleIds);
        resp.sendRedirect(req.getContextPath() + "/staffs?action=list&msg=Cap%20nhat%20tai%20khoan%20thanh%20cong");
    }

    private void deleteStaff(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int staffId = Integer.parseInt(req.getParameter("id"));
        for (StaffRole staffRole : daoStaffRole.getByStaffId(staffId)) {
            daoStaffRole.delete(staffRole.getStaffID(), staffRole.getRoleID());
        }
        daoStaff.delete(staffId);
        resp.sendRedirect(req.getContextPath() + "/staffs?action=list&msg=Xoa%20tai%20khoan%20thanh%20cong");
    }

    private Staff readStaff(HttpServletRequest req, boolean hasId) {
        Staff staff = new Staff(
                req.getParameter("staffName"),
                req.getParameter("username"),
                req.getParameter("password"));
        if (hasId) {
            staff.setStaffID(Integer.parseInt(req.getParameter("staffID")));
        }
        return staff;
    }

    private int[] parseRoleIds(HttpServletRequest req) {
        String[] values = req.getParameterValues("roleIDs");
        if (values == null || values.length == 0) {
            return new int[0];
        }

        int[] roleIds = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            roleIds[i] = Integer.parseInt(values[i]);
        }
        return roleIds;
    }

    private void prepareForm(HttpServletRequest req, Staff staff) throws SQLException {
        List<Role> roles = daoRole.getAll();
        Set<Integer> selectedRoleIds = new HashSet<>();
        Map<Integer, Boolean> selectedRoleFlags = new HashMap<>();
        if (staff != null) {
            for (StaffRole staffRole : daoStaffRole.getByStaffId(staff.getStaffID())) {
                selectedRoleIds.add(staffRole.getRoleID());
                selectedRoleFlags.put(staffRole.getRoleID(), Boolean.TRUE);
            }
        }

        req.setAttribute("staff", staff);
        req.setAttribute("roles", roles);
        req.setAttribute("selectedRoleIds", selectedRoleIds);
        req.setAttribute("selectedRoleFlags", selectedRoleFlags);
    }

    private void forwardFormError(HttpServletRequest req, HttpServletResponse resp, Staff staff, String view, String error)
            throws SQLException, ServletException, IOException {
        req.setAttribute("error", error);
        if (staff == null) {
            staff = readStaff(req, false);
        }
        prepareForm(req, staff);
        req.getRequestDispatcher(view).forward(req, resp);
    }

    private void syncRoles(int staffId, int[] roleIds) throws SQLException {
        List<StaffRole> currentRoles = daoStaffRole.getByStaffId(staffId);
        Set<Integer> targetRoleIds = new HashSet<>();
        for (int roleId : roleIds) {
            targetRoleIds.add(roleId);
        }

        for (StaffRole currentRole : currentRoles) {
            if (!targetRoleIds.contains(currentRole.getRoleID())) {
                daoStaffRole.delete(staffId, currentRole.getRoleID());
            }
        }

        Set<Integer> existingRoleIds = new HashSet<>();
        for (StaffRole currentRole : currentRoles) {
            existingRoleIds.add(currentRole.getRoleID());
        }

        for (int roleId : roleIds) {
            if (!existingRoleIds.contains(roleId)) {
                daoStaffRole.insert(new StaffRole(staffId, roleId));
            }
        }
    }

    private String joinRoleNames(List<StaffRole> staffRoles, List<Role> roles) {
        List<String> names = new ArrayList<>();
        for (StaffRole staffRole : staffRoles) {
            for (Role role : roles) {
                if (role.getRoleID() == staffRole.getRoleID()) {
                    names.add(role.getRoleName());
                    break;
                }
            }
        }
        if (names.isEmpty()) {
            return "No role";
        }
        return String.join(", ", names);
    }

    public static class StaffListRow {
        private final Staff staff;
        private final String roleNames;

        public StaffListRow(Staff staff, String roleNames) {
            this.staff = staff;
            this.roleNames = roleNames;
        }

        public Staff getStaff() {
            return staff;
        }

        public String getRoleNames() {
            return roleNames;
        }
    }
}
