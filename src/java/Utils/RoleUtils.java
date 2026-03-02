package Utils;

import Entities.Staff;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public final class RoleUtils {

    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_STAFF = 2;
    public static final int ROLE_STAFF_ALT = 4;
    public static final int ROLE_STUDENT = 8;
    public static final int ROLE_STUDENT_ALT = 9;

    private RoleUtils() {
    }

    public static Staff getLoggedStaff(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object rawStaff = session.getAttribute("staff");
        if (rawStaff instanceof Staff) {
            return (Staff) rawStaff;
        }
        return null;
    }

    public static boolean hasRole(HttpServletRequest request, int roleId) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        Object rawRoles = session.getAttribute("roles");
        if (!(rawRoles instanceof List<?>)) {
            return false;
        }

        for (Object rawRole : (List<?>) rawRoles) {
            if (rawRole instanceof Number && ((Number) rawRole).intValue() == roleId) {
                return true;
            }
            if (rawRole instanceof String) {
                try {
                    if (Integer.parseInt((String) rawRole) == roleId) {
                        return true;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return false;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        return hasRole(request, ROLE_ADMIN);
    }

    public static boolean isStaff(HttpServletRequest request) {
        return hasRole(request, ROLE_STAFF) || hasRole(request, ROLE_STAFF_ALT);
    }

    public static boolean isStudent(HttpServletRequest request) {
        return hasRole(request, ROLE_STUDENT) || hasRole(request, ROLE_STUDENT_ALT);
    }

    public static boolean isStudentOnly(HttpServletRequest request) {
        return isStudent(request) && !isAdmin(request) && !isStaff(request);
    }
}
