<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Thêm nhân viên" />
    <c:set var="activeTab" value="staffs" />
    <%@ include file="../layout/_admin_header.jsp" %>

    <div class="container">
        <div class="card">
            <h2>Thêm nhân viên</h2>

            <c:if test="${not empty error}">
                <div class="error"><c:out value="${error}" /></div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/admin/staffs?action=create">
                <div class="field">
                    <label for="staffName">Tên nhân viên</label>
                    <input id="staffName" type="text" name="staffName" value="${staff.staffName}" required>
                </div>
                <div class="field">
                    <label for="username">Tên đăng nhập</label>
                    <input id="username" type="text" name="username" value="${staff.username}" required>
                </div>
                <div class="field">
                    <label for="email">Email</label>
                    <input id="email" type="email" name="email" value="${staff.email}" autocomplete="email" inputmode="email" spellcheck="false" required>
                </div>
                <div class="field">
                    <label for="password">Mật khẩu</label>
                    <input id="password" type="password" name="password" value="${staff.password}" autocomplete="new-password" required>
                </div>
                <div class="field">
                    <label>Vai trò</label>
                    <div class="roles">
                        <c:forEach var="role" items="${roles}">
                            <label class="role-item">
                                <input type="checkbox" name="roleIDs" value="${role.roleID}">
                                <span>${role.roleName}</span>
                            </label>
                        </c:forEach>
                    </div>
                </div>
                <div class="actions">
                    <button class="btn btn-primary" type="submit">Lưu</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/staffs?action=list">Hủy</a>
                </div>
            </form>
        </div>
    </div>
<%@ include file="../layout/_admin_footer.jsp" %>
