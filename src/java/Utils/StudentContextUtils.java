package Utils;

import Entities.Staff;
import Entities.Student;
import Model.DAOStudent;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.SQLException;

public final class StudentContextUtils {

    public static final String CURRENT_STUDENT_ATTR = "currentStudent";
    public static final String STUDENT_DISPLAY_NAME_ATTR = "studentDisplayName";
    public static final String STUDENT_DISPLAY_INITIAL_ATTR = "studentDisplayInitial";

    private StudentContextUtils() {
    }

    public static void attachCurrentStudent(HttpServletRequest request) throws SQLException {
        Staff staff = RoleUtils.getLoggedStaff(request);
        Student student = resolveCurrentStudent(staff);
        request.setAttribute(CURRENT_STUDENT_ATTR, student);
        request.setAttribute(STUDENT_DISPLAY_NAME_ATTR, buildDisplayName(staff, student));
        request.setAttribute(STUDENT_DISPLAY_INITIAL_ATTR, buildDisplayInitial(staff, student));
    }

    public static Student getAttachedStudent(HttpServletRequest request) {
        Object rawStudent = request.getAttribute(CURRENT_STUDENT_ATTR);
        if (rawStudent instanceof Student) {
            return (Student) rawStudent;
        }
        return null;
    }

    public static Student resolveCurrentStudent(HttpServletRequest request) throws SQLException {
        Student attachedStudent = getAttachedStudent(request);
        if (attachedStudent != null) {
            return attachedStudent;
        }
        return resolveCurrentStudent(RoleUtils.getLoggedStaff(request));
    }

    public static Student resolveCurrentStudent(Staff staff) throws SQLException {
        Integer studentId = resolveStudentId(staff);
        if (studentId == null) {
            return null;
        }
        return new DAOStudent().getById(studentId);
    }

    public static Integer resolveStudentId(Staff staff) throws SQLException {
        return resolveStudentId(staff, new DAOStudent());
    }

    public static Integer resolveStudentId(Staff staff, DAOStudent daoStudent) throws SQLException {
        if (staff == null || daoStudent == null) {
            return null;
        }

        Student mirroredStudent = daoStudent.ensureMirrorFromStaff(staff);
        if (mirroredStudent != null) {
            return mirroredStudent.getStudentID();
        }

        Integer candidateFromUsername = extractTrailingNumber(staff.getUsername());
        if (candidateFromUsername != null && daoStudent.getById(candidateFromUsername) != null) {
            return candidateFromUsername;
        }

        if (!trim(staff.getEmail()).isEmpty()) {
            Student byEmail = daoStudent.getByEmail(staff.getEmail());
            if (byEmail != null) {
                return byEmail.getStudentID();
            }
        }

        return null;
    }

    public static String buildDisplayName(Staff staff, Student student) {
        String studentName = trim(student == null ? null : student.getStudentName());
        if (!studentName.isEmpty()) {
            return studentName;
        }

        String staffName = trim(staff == null ? null : staff.getStaffName());
        if (!staffName.isEmpty()) {
            return staffName;
        }

        return "Sinh vien thu vien";
    }

    public static String buildDisplayInitial(Staff staff, Student student) {
        String displayName = buildDisplayName(staff, student);
        if (displayName.isEmpty()) {
            return "S";
        }
        return String.valueOf(Character.toUpperCase(displayName.charAt(0)));
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private static Integer extractTrailingNumber(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        int index = value.length() - 1;
        while (index >= 0 && Character.isDigit(value.charAt(index))) {
            index--;
        }
        if (index == value.length() - 1) {
            return null;
        }

        try {
            return Integer.parseInt(value.substring(index + 1));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
