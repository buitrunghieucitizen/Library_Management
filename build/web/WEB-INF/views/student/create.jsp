<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Thêm sinh viên" />
    <c:set var="activeTab" value="students" />
    <%@ include file="../admin/layout/_admin_header.jsp" %>

    <div class="container">
        <div class="student-form-card">
            <h2><span class="form-icon">➕</span> Thêm sinh viên mới</h2>

            <c:if test="${not empty param.error or not empty error}">
                <div class="error"><c:out value="${not empty param.error ? param.error : error}" /></div>
            </c:if>

            <form method="POST" action="${pageContext.request.contextPath}/admin/students?action=create">
                <div class="student-form-grid">
                    <div class="student-form-group">
                        <label>Tên sinh viên</label>
                        <input type="text" name="studentName" placeholder="Nhập họ và tên sinh viên" required>
                    </div>
                    <div class="student-form-group">
                        <label>Thư điện tử</label>
                        <input type="email" name="email" placeholder="example@email.com">
                    </div>
                    <div class="student-form-group">
                        <label>Số điện thoại</label>
                        <input type="text" name="phone" placeholder="0xx xxxx xxx">
                    </div>
                </div>
                <div class="student-form-actions">
                    <button class="btn btn-primary" type="submit">💾 Lưu sinh viên</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/students">✕ Hủy</a>
                </div>
            </form>
        </div>
    </div>
<%@ include file="../admin/layout/_admin_footer.jsp" %>
