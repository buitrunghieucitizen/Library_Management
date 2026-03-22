<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Sửa nhân viên" />
<c:set var="pageSubtitle" value="Cập nhật hồ sơ và quyền truy cập" />
    <c:set var="activeTab" value="staffs" />
    <%@ include file="../layout/_admin_header.jsp" %>

    <div class="container">
        <div class="student-form-card staff-form-card">
            <h2><span class="form-icon">✏️</span> Cập nhật tài khoản nhân viên</h2>

            <div class="student-info-badge">
                <span class="badge-icon">ID</span>
                Mã tài khoản: ${staff.staffID}
            </div>

            <p class="staff-form-intro">Chỉnh sửa hồ sơ đăng nhập, email liên hệ và tổ hợp vai trò cho tài khoản này.</p>

            <c:if test="${not empty error}">
                <div class="error"><c:out value="${error}" /></div>
            </c:if>

            <form method="post" action="${ctx}/admin/staffs?action=edit">
                <input type="hidden" name="staffID" value="${staff.staffID}">

                <div class="student-form-grid staff-form-grid">
                    <div class="student-form-group">
                        <label for="staffName">Họ và tên</label>
                        <input id="staffName" type="text" name="staffName" value="${staff.staffName}" placeholder="Nhập họ và tên nhân sự" autocomplete="name" required>
                    </div>

                    <div class="student-form-group">
                        <label for="username">Tên đăng nhập</label>
                        <input id="username" type="text" name="username" value="${staff.username}" placeholder="Ví dụ: librarian_01" autocomplete="username" required>
                    </div>

                    <div class="student-form-group">
                        <label for="email">Email liên hệ</label>
                        <input id="email" type="email" name="email" value="${staff.email}" placeholder="name@library.edu.vn" autocomplete="email" inputmode="email" spellcheck="false" required>
                    </div>

                    <div class="student-form-group">
                        <label for="password">Mật khẩu</label>
                        <input id="password" type="password" name="password" value="${staff.password}" placeholder="Cập nhật mật khẩu nếu cần" autocomplete="current-password" required>
                    </div>
                </div>

                <div class="staff-form-note">
                    Kiểm tra kỹ các tài khoản có nhiều quyền truy cập trước khi lưu để tránh chồng chéo quyền.
                </div>

                <div class="staff-role-panel">
                    <div class="staff-role-panel-head">
                        <div>
                            <span class="card-kicker">Phân quyền</span>
                            <h3>Điều chỉnh vai trò truy cập</h3>
                        </div>
                        <span class="staff-role-panel-hint">Có thể chọn nhiều vai trò</span>
                    </div>

                    <div class="staff-role-grid">
                        <c:forEach var="role" items="${roles}">
                            <label class="staff-role-option ${selectedRoleFlags[role.roleID] ? 'is-selected' : ''}">
                                <input type="checkbox" name="roleIDs" value="${role.roleID}" ${selectedRoleFlags[role.roleID] ? 'checked' : ''}>
                                <div class="staff-role-option-copy">
                                    <strong><c:out value="${role.roleName}" /></strong>
                                    <span>Role ID: ${role.roleID}</span>
                                </div>
                            </label>
                        </c:forEach>
                    </div>
                </div>

                <div class="student-form-actions">
                    <button class="btn btn-primary" type="submit">💾 Cập nhật tài khoản</button>
                    <a class="btn btn-secondary" href="${ctx}/admin/staffs?action=list">✕ Hủy</a>
                </div>
            </form>
        </div>
    </div>
<%@ include file="../layout/_admin_footer.jsp" %>
