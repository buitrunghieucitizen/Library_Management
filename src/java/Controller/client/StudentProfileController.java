package Controller.client;

import Controller.borrow.BorrowHelper;
import Controller.borrow.BorrowValidator;
import Entities.Book;
import Entities.Staff;
import Entities.Student;
import Model.DAOBook;
import Model.DAOBorrow;
import Model.DAOStaff;
import Model.DAOStudent;
import Model.DAOFine;
import Utils.PasswordResetUtils;
import Utils.RoleUtils;
import Utils.StudentContextUtils;
import ViewModel.StudentProfileSupport;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@WebServlet(name = "StudentProfileController", urlPatterns = {"/profile"})
public class StudentProfileController extends HttpServlet {

    private static final String ACTIVE_LABEL = "Ho\u1ea1t \u0111\u1ed9ng";
    private static final String NOT_UPDATED_LABEL = "Ch\u01b0a c\u1eadp nh\u1eadt";

    private final DAOStudent daoStudent = new DAOStudent();
    private final DAOStaff daoStaff = new DAOStaff();
    private final DAOBook daoBook = new DAOBook();
    private final BorrowHelper borrowHelper = new BorrowHelper(daoStudent);
    private final BorrowValidator borrowValidator = new BorrowValidator(new DAOBorrow(), new DAOFine());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!RoleUtils.isStudentOnly(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            Student profileStudent = resolveProfileStudent(request);
            prepareProfileView(request, profileStudent);
            if (profileStudent == null) {
                request.setAttribute("profileError",
                        "Kh\u00f4ng t\u00ecm th\u1ea5y h\u1ed3 s\u01a1 sinh vi\u00ean t\u01b0\u01a1ng \u1ee9ng v\u1edbi t\u00e0i kho\u1ea3n hi\u1ec7n t\u1ea1i.");
            }
            request.getRequestDispatcher("/WEB-INF/views/client/profile.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!RoleUtils.isStudentOnly(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        try {
            Student currentStudent = resolveProfileStudent(request);
            if (currentStudent == null) {
                redirectWithMessage(request, response, "error",
                        "Kh\u00f4ng t\u00ecm th\u1ea5y h\u1ed3 s\u01a1 sinh vi\u00ean \u0111\u1ec3 c\u1eadp nh\u1eadt.");
                return;
            }

            String formAction = trim(request.getParameter("formAction"));
            if ("changePassword".equalsIgnoreCase(formAction)) {
                handlePasswordChange(request, response, currentStudent);
                return;
            }

            handleProfileUpdate(request, response, currentStudent);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void handleProfileUpdate(HttpServletRequest request, HttpServletResponse response,
            Student currentStudent) throws SQLException, ServletException, IOException {
        StudentProfileSupport support = daoStudent.getProfileSupport();
        String studentName = trim(request.getParameter("studentName"));
        String email = trim(request.getParameter("email"));
        String phone = trim(request.getParameter("phone"));
        String avatarUrl = support.isAvatarUrlSupported() ? trim(request.getParameter("avatarUrl")) : "";
        String className = support.isClassNameSupported() ? trim(request.getParameter("className")) : "";
        String facultyName = support.isFacultyNameSupported() ? trim(request.getParameter("facultyName")) : "";

        Student draftStudent = rebuildStudent(currentStudent, studentName, email, phone,
                avatarUrl, className, facultyName);

        if (studentName.isEmpty()) {
            forwardWithErrors(request, response, draftStudent,
                    "H\u1ecd v\u00e0 t\u00ean kh\u00f4ng \u0111\u01b0\u1ee3c \u0111\u1ec3 tr\u1ed1ng.", null);
            return;
        }

        if (!email.isEmpty() && !isValidEmail(email)) {
            forwardWithErrors(request, response, draftStudent,
                    "Email kh\u00f4ng h\u1ee3p l\u1ec7.", null);
            return;
        }

        if (!avatarUrl.isEmpty() && !isSafeAvatarUrl(avatarUrl)) {
            forwardWithErrors(request, response, draftStudent,
                    "\u1ea2nh \u0111\u1ea1i di\u1ec7n ph\u1ea3i l\u00e0 link h\u1ee3p l\u1ec7 v\u00e0 kh\u00f4ng d\u00f9ng giao th\u1ee9c kh\u00f4ng an to\u00e0n.",
                    null);
            return;
        }

        daoStudent.updateProfileDetails(draftStudent);
        redirectWithMessage(request, response, "msg",
                "C\u1eadp nh\u1eadt h\u1ed3 s\u01a1 sinh vi\u00ean th\u00e0nh c\u00f4ng.");
    }

    private void handlePasswordChange(HttpServletRequest request, HttpServletResponse response,
            Student currentStudent) throws SQLException, ServletException, IOException {
        Staff loggedStaff = RoleUtils.getLoggedStaff(request);
        if (loggedStaff == null) {
            redirectWithMessage(request, response, "error",
                    "Phi\u00ean \u0111\u0103ng nh\u1eadp \u0111\u00e3 h\u1ebft h\u1ea1n. Vui l\u00f2ng \u0111\u0103ng nh\u1eadp l\u1ea1i.");
            return;
        }

        Staff freshStaff = daoStaff.getById(loggedStaff.getStaffID());
        if (freshStaff == null) {
            forwardWithErrors(request, response, currentStudent, null,
                    "Kh\u00f4ng t\u00ecm th\u1ea5y t\u00e0i kho\u1ea3n \u0111\u1ec3 \u0111\u1ed5i m\u1eadt kh\u1ea9u.");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (isBlank(currentPassword) || isBlank(newPassword) || isBlank(confirmPassword)) {
            forwardWithErrors(request, response, currentStudent, null,
                    "Vui l\u00f2ng nh\u1eadp \u0111\u1ea7y \u0111\u1ee7 m\u1eadt kh\u1ea9u hi\u1ec7n t\u1ea1i, m\u1eadt kh\u1ea9u m\u1edbi v\u00e0 x\u00e1c nh\u1eadn.");
            return;
        }

        if (!safeEquals(freshStaff.getPassword(), currentPassword)) {
            forwardWithErrors(request, response, currentStudent, null,
                    "M\u1eadt kh\u1ea9u hi\u1ec7n t\u1ea1i kh\u00f4ng ch\u00ednh x\u00e1c.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            forwardWithErrors(request, response, currentStudent, null,
                    "X\u00e1c nh\u1eadn m\u1eadt kh\u1ea9u m\u1edbi kh\u00f4ng kh\u1edbp.");
            return;
        }

        if (!PasswordResetUtils.isValidPassword(newPassword)) {
            forwardWithErrors(request, response, currentStudent, null,
                    "M\u1eadt kh\u1ea9u m\u1edbi ph\u1ea3i c\u00f3 \u00edt nh\u1ea5t 6 k\u00fd t\u1ef1, g\u1ed3m ch\u1eef hoa, ch\u1eef th\u01b0\u1eddng v\u00e0 s\u1ed1.");
            return;
        }

        if (safeEquals(currentPassword, newPassword)) {
            forwardWithErrors(request, response, currentStudent, null,
                    "M\u1eadt kh\u1ea9u m\u1edbi ph\u1ea3i kh\u00e1c m\u1eadt kh\u1ea9u hi\u1ec7n t\u1ea1i.");
            return;
        }

        if (daoStaff.updatePasswordByUsername(freshStaff.getUsername(), newPassword) <= 0) {
            forwardWithErrors(request, response, currentStudent, null,
                    "Kh\u00f4ng th\u1ec3 \u0111\u1ed5i m\u1eadt kh\u1ea9u. Vui l\u00f2ng th\u1eed l\u1ea1i.");
            return;
        }

        freshStaff.setPassword(newPassword);
        request.getSession().setAttribute("staff", freshStaff);
        redirectWithMessage(request, response, "msg",
                "\u0110\u1ed5i m\u1eadt kh\u1ea9u th\u00e0nh c\u00f4ng.");
    }

    private Student resolveProfileStudent(HttpServletRequest request) throws SQLException {
        Student currentStudent = StudentContextUtils.resolveCurrentStudent(request);
        if (currentStudent == null) {
            return null;
        }

        Student freshStudent = daoStudent.getById(currentStudent.getStudentID());
        return freshStudent == null ? currentStudent : freshStudent;
    }

    private Student rebuildStudent(Student currentStudent, String studentName, String email, String phone,
            String avatarUrl, String className, String facultyName) {
        return new Student(
                currentStudent.getStudentID(),
                studentName,
                email.isEmpty() ? null : email,
                phone.isEmpty() ? null : phone,
                emptyToNull(avatarUrl),
                emptyToNull(className),
                emptyToNull(facultyName),
                currentStudent.getAccountStatus(),
                currentStudent.getCreatedAt());
    }

    private void prepareProfileView(HttpServletRequest request, Student profileStudent) throws SQLException {
        StudentProfileSupport profileSupport = daoStudent.getProfileSupport();
        request.setAttribute("profileStudent", profileStudent);
        request.setAttribute("profileSupport", profileSupport);
        request.setAttribute("profileAccountStatusLabel", resolveAccountStatus(profileStudent));
        request.setAttribute("profileCreatedAtLabel", resolveCreatedAt(profileStudent));
        attachStudentHeaderContext(request, profileStudent);
    }

    private void attachStudentHeaderContext(HttpServletRequest request, Student profileStudent) throws SQLException {
        List<Integer> borrowCartIds = borrowHelper.getOrCreateBorrowCart(request);
        Map<Integer, Book> borrowCartBookMap = new HashMap<>();
        for (Integer cartBookId : borrowCartIds) {
            if (cartBookId == null) {
                continue;
            }
            Book cartBook = daoBook.getById(cartBookId);
            if (cartBook != null) {
                borrowCartBookMap.put(cartBookId, cartBook);
            }
        }

        request.setAttribute("borrowCart", borrowHelper.getBorrowCartBooks(request, borrowCartBookMap));
        request.setAttribute("borrowCartSize", borrowHelper.getBorrowCartSize(request));
        request.setAttribute("borrowCartIds", borrowCartIds);
        request.setAttribute("maxCartSize", BorrowValidator.MAX_CART_SIZE);

        if (profileStudent != null) {
            request.setAttribute("studentId", profileStudent.getStudentID());
            request.setAttribute("eligibility", borrowValidator.getEligibility(profileStudent.getStudentID()));
        }
    }

    private void forwardWithErrors(HttpServletRequest request, HttpServletResponse response,
            Student currentStudent, String profileMessage, String passwordMessage)
            throws ServletException, IOException, SQLException {
        prepareProfileView(request, currentStudent);
        if (!isBlank(profileMessage)) {
            request.setAttribute("profileError", profileMessage);
        }
        if (!isBlank(passwordMessage)) {
            request.setAttribute("passwordError", passwordMessage);
        }
        request.getRequestDispatcher("/WEB-INF/views/client/profile.jsp").forward(request, response);
    }

    private void redirectWithMessage(HttpServletRequest request, HttpServletResponse response,
            String key, String message) throws IOException {
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/profile?" + key + "=" + encodedMessage);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private boolean isSafeAvatarUrl(String value) {
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return !(normalized.startsWith("javascript:")
                || normalized.startsWith("vbscript:")
                || normalized.startsWith("data:text/html"));
    }

    private String resolveAccountStatus(Student student) {
        String accountStatus = trim(student == null ? null : student.getAccountStatus());
        String normalized = accountStatus.toLowerCase(Locale.ROOT);
        if (accountStatus.isEmpty() || "active".equals(normalized)) {
            return ACTIVE_LABEL;
        }
        if ("inactive".equals(normalized)) {
            return "T\u1ea1m kh\u00f3a";
        }
        return accountStatus;
    }

    private String resolveCreatedAt(Student student) {
        String createdAt = trim(student == null ? null : student.getCreatedAt());
        return createdAt.isEmpty() ? NOT_UPDATED_LABEL : createdAt;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String emptyToNull(String value) {
        String trimmed = trim(value);
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean safeEquals(String left, String right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }
}
