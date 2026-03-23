<%@ page pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:url var="footerHomeUrl" value="/home" />
<c:url var="footerProfileUrl" value="/profile" />
<c:url var="footerBorrowUrl" value="/borrows">
    <c:param name="action" value="list" />
</c:url>
<footer class="main-footer student-footer">
    <div class="student-footer-shell">
        <div class="student-footer-brand">
            <span class="student-footer-mark">LM</span>
            <div class="student-footer-copy">
                <strong>Library Manager</strong>
                <span>Không gian tra cứu, mượn trả và theo dõi tài nguyên học tập dành cho sinh viên.</span>
            </div>
        </div>

        <div class="student-footer-links">
            <a href="${footerHomeUrl}">Trang sinh viên</a>
            <a href="${footerProfileUrl}">Hồ sơ</a>
            <a href="${footerBorrowUrl}">Mượn trả</a>
        </div>
    </div>
</footer>
<script src="${pageContext.request.contextPath}/assets/js/student-shared.js"></script>
