package Controller;

import Entities.Role;
import Entities.Staff;
import Entities.StaffRole;
import Model.DAORole;
import Model.DAOStaff;
import Model.DAOStaffRole;
import Utils.PaginationUtils;
import Utils.PasswordResetUtils;
import Utils.RoleUtils;
import ViewModel.PageSlice;
import ViewModel.StaffListRow;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@WebServlet(name = "StaffController", urlPatterns = {"/admin/staffs"})
public class StaffController extends HttpServlet {

    private static final String STAFFS_PATH = "/admin/staffs";
    private static final int PAGE_SIZE = 10;
    private static final int ROLE_ASSISTANT = 3;
    private static final int ROLE_SALES = 7;

    private final DAOStaff daoStaff = new DAOStaff();
    private final DAOStaffRole daoStaffRole = new DAOStaffRole();
    private final DAORole daoRole = new DAORole();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!RoleUtils.isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
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
                    req.getRequestDispatcher("/WEB-INF/views/admin/staff/create.jsp").forward(req, resp);
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
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
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

            resp.sendRedirect(req.getContextPath() + STAFFS_PATH + "?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int page = PaginationUtils.parsePage(req.getParameter("page"), 1);
        String searchKeyword = trim(req.getParameter("search"));
        List<Staff> staffs = daoStaff.getAll();
        List<Role> roles = daoRole.getAll();
        Map<Integer, String> roleNamesById = new HashMap<>();
        for (Role role : roles) {
            roleNamesById.put(role.getRoleID(), role.getRoleName());
        }

        List<StaffListRow> rows = new ArrayList<>();

        for (Staff staff : staffs) {
            List<StaffRole> staffRoles = daoStaffRole.getByStaffId(staff.getStaffID());
            rows.add(buildRow(staff, staffRoles, roleNamesById));
        }

        List<StaffListRow> filteredRows = filterRows(rows, searchKeyword);
        PageSlice<StaffListRow> pageSlice = PaginationUtils.paginate(filteredRows, page, PAGE_SIZE);
        req.setAttribute("staffRows", pageSlice.getItems());
        req.setAttribute("currentPage", pageSlice.getPage());
        req.setAttribute("totalPages", pageSlice.getTotalPages());
        req.setAttribute("totalItems", pageSlice.getTotalItems());
        req.setAttribute("allStaffCount", rows.size());
        req.setAttribute("searchKeyword", searchKeyword);
        req.setAttribute("searchActive", !searchKeyword.isEmpty());
        req.setAttribute("pageStartIndex", (pageSlice.getPage() - 1) * PAGE_SIZE);
        req.setAttribute("emailCount", countRowsWithEmail(filteredRows));
        req.setAttribute("missingEmailCount", countRowsMissingEmail(filteredRows));
        req.setAttribute("adminCount", countRowsWithAnyRole(filteredRows, RoleUtils.ROLE_ADMIN));
        req.setAttribute("operationsCount",
                countRowsWithAnyRole(filteredRows, RoleUtils.ROLE_STAFF, ROLE_ASSISTANT, ROLE_SALES,
                        RoleUtils.ROLE_STAFF_ALT));
        req.setAttribute("studentLinkedCount",
                countRowsWithAnyRole(filteredRows, RoleUtils.ROLE_STUDENT, RoleUtils.ROLE_STUDENT_ALT));
        req.setAttribute("multiRoleCount", countMultiRoleRows(filteredRows));
        req.setAttribute("roleAssignmentCount", countRoleAssignments(filteredRows));
        req.getRequestDispatcher("/WEB-INF/views/admin/staff/list.jsp").forward(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int staffId = Integer.parseInt(req.getParameter("id"));
        Staff staff = daoStaff.getById(staffId);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + STAFFS_PATH + "?action=list&error=Khong tim thay tai khoan");
            return;
        }

        prepareForm(req, staff);
        req.getRequestDispatcher("/WEB-INF/views/admin/staff/edit.jsp").forward(req, resp);
    }

    private void createStaff(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        Staff staff = readStaff(req, false);
        if (!validateStaff(req, resp, staff, "/WEB-INF/views/admin/staff/create.jsp")) {
            return;
        }

        int[] roleIds = parseRoleIds(req);
        if (roleIds.length == 0) {
            forwardFormError(req, resp, null, "/WEB-INF/views/admin/staff/create.jsp", "Phai chon it nhat 1 role.");
            return;
        }

        daoStaff.insert(staff);
        syncRoles(staff.getStaffID(), roleIds);
        resp.sendRedirect(req.getContextPath() + STAFFS_PATH + "?action=list&msg=Tao%20tai%20khoan%20thanh%20cong");
    }

    private void updateStaff(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        Staff staff = readStaff(req, true);
        if (!validateStaff(req, resp, staff, "/WEB-INF/views/admin/staff/edit.jsp")) {
            return;
        }

        int[] roleIds = parseRoleIds(req);
        if (roleIds.length == 0) {
            forwardFormError(req, resp, staff, "/WEB-INF/views/admin/staff/edit.jsp", "Phai chon it nhat 1 role.");
            return;
        }

        daoStaff.update(staff);
        syncRoles(staff.getStaffID(), roleIds);
        resp.sendRedirect(req.getContextPath() + STAFFS_PATH + "?action=list&msg=Cap%20nhat%20tai%20khoan%20thanh%20cong");
    }

    private void deleteStaff(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int staffId = Integer.parseInt(req.getParameter("id"));
        for (StaffRole staffRole : daoStaffRole.getByStaffId(staffId)) {
            daoStaffRole.delete(staffRole.getStaffID(), staffRole.getRoleID());
        }
        daoStaff.delete(staffId);
        resp.sendRedirect(req.getContextPath() + STAFFS_PATH + "?action=list&msg=Xoa%20tai%20khoan%20thanh%20cong");
    }

    private Staff readStaff(HttpServletRequest req, boolean hasId) {
        Staff staff = new Staff(
                req.getParameter("staffName"),
                req.getParameter("username"),
                req.getParameter("email"),
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

        String[] requestedRoleIds = req.getParameterValues("roleIDs");
        if (requestedRoleIds != null && requestedRoleIds.length > 0) {
            for (String rawRoleId : requestedRoleIds) {
                try {
                    int roleId = Integer.parseInt(rawRoleId);
                    selectedRoleIds.add(roleId);
                    selectedRoleFlags.put(roleId, Boolean.TRUE);
                } catch (NumberFormatException ignored) {
                }
            }
        } else if (staff != null) {
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

    private StaffListRow buildRow(Staff staff, List<StaffRole> staffRoles, Map<Integer, String> roleNamesById) {
        List<Integer> roleIds = new ArrayList<>();
        for (StaffRole staffRole : staffRoles) {
            roleIds.add(staffRole.getRoleID());
        }

        roleIds.sort(Comparator.comparingInt((Integer roleId) -> rolePriority(roleId)).thenComparingInt(Integer::intValue));

        List<String> roleLabels = new ArrayList<>();
        for (Integer roleId : roleIds) {
            String roleName = roleNamesById.get(roleId);
            roleLabels.add(roleName == null || roleName.isBlank() ? "Role #" + roleId : roleName);
        }

        return new StaffListRow(staff, roleIds, roleLabels);
    }

    private List<StaffListRow> filterRows(List<StaffListRow> rows, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return rows;
        }

        String normalizedKeyword = normalizeSearchValue(keyword);
        List<StaffListRow> filteredRows = new ArrayList<>();
        for (StaffListRow row : rows) {
            if (matchesSearch(row, normalizedKeyword)) {
                filteredRows.add(row);
            }
        }
        return filteredRows;
    }

    private boolean matchesSearch(StaffListRow row, String normalizedKeyword) {
        if (normalizedKeyword == null || normalizedKeyword.isEmpty()) {
            return true;
        }

        Staff staff = row.getStaff();
        return containsNormalized(String.valueOf(staff.getStaffID()), normalizedKeyword)
                || containsNormalized(staff.getStaffName(), normalizedKeyword)
                || containsNormalized(staff.getUsername(), normalizedKeyword)
                || containsNormalized(staff.getEmail(), normalizedKeyword)
                || containsNormalized(row.getRoleNames(), normalizedKeyword);
    }

    private boolean containsNormalized(String candidate, String normalizedKeyword) {
        return normalizeSearchValue(candidate).contains(normalizedKeyword);
    }

    private String normalizeSearchValue(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private int countRowsWithEmail(List<StaffListRow> rows) {
        int count = 0;
        for (StaffListRow row : rows) {
            if (row.hasEmail()) {
                count++;
            }
        }
        return count;
    }

    private int countRowsMissingEmail(List<StaffListRow> rows) {
        int count = 0;
        for (StaffListRow row : rows) {
            if (row.isMissingEmail()) {
                count++;
            }
        }
        return count;
    }

    private int countRowsWithAnyRole(List<StaffListRow> rows, int... roleIds) {
        int count = 0;
        for (StaffListRow row : rows) {
            if (row.hasAnyRole(roleIds)) {
                count++;
            }
        }
        return count;
    }

    private int countMultiRoleRows(List<StaffListRow> rows) {
        int count = 0;
        for (StaffListRow row : rows) {
            if (row.isMultiRole()) {
                count++;
            }
        }
        return count;
    }

    private int countRoleAssignments(List<StaffListRow> rows) {
        int total = 0;
        for (StaffListRow row : rows) {
            total += row.getRoleCount();
        }
        return total;
    }

    private int rolePriority(int roleId) {
        return switch (roleId) {
            case RoleUtils.ROLE_ADMIN -> 0;
            case RoleUtils.ROLE_STAFF -> 1;
            case ROLE_ASSISTANT -> 2;
            case ROLE_SALES -> 3;
            case RoleUtils.ROLE_STAFF_ALT -> 4;
            case RoleUtils.ROLE_STUDENT -> 5;
            case RoleUtils.ROLE_STUDENT_ALT -> 6;
            default -> 100 + roleId;
        };
    }

    private boolean validateStaff(HttpServletRequest req, HttpServletResponse resp, Staff staff, String view)
            throws SQLException, ServletException, IOException {
        String staffName = trim(staff.getStaffName());
        String username = trim(staff.getUsername());
        String email = trim(staff.getEmail());
        String password = staff.getPassword();

        if (staffName.isEmpty() || username.isEmpty() || email.isEmpty() || password == null || password.isBlank()) {
            forwardFormError(req, resp, staff, view, "Vui long nhap day du thong tin.");
            return false;
        }

        if (!PasswordResetUtils.isEmail(email)) {
            forwardFormError(req, resp, staff, view, "Email khong hop le.");
            return false;
        }

        Staff sameUsername = daoStaff.getByUsername(username);
        if (sameUsername != null && sameUsername.getStaffID() != staff.getStaffID()) {
            forwardFormError(req, resp, staff, view, "Ten dang nhap da ton tai.");
            return false;
        }

        Staff sameEmail = daoStaff.getByEmail(email);
        if (sameEmail != null && sameEmail.getStaffID() != staff.getStaffID()) {
            forwardFormError(req, resp, staff, view, "Email da duoc su dung.");
            return false;
        }

        Staff legacyEmailOwner = daoStaff.getByUsername(email);
        if (legacyEmailOwner != null && legacyEmailOwner.getStaffID() != staff.getStaffID()) {
            forwardFormError(req, resp, staff, view, "Email da duoc su dung.");
            return false;
        }

        return true;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}

