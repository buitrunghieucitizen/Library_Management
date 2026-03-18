<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Thêm nhà xuất bản" />
    <c:set var="activeTab" value="publishers" />
    <%@ include file="../admin/layout/_admin_header.jsp" %>


    <div class="container"><div class="card">
        <h2>Thêm nhà xuất bản</h2>
        <form method="POST" action="${pageContext.request.contextPath}/admin/publishers?action=create">
            <label>Tên nhà xuất bản</label><input type="text" name="publisherName" required>
            <button class="btn btn-primary" type="submit">Lưu</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/publishers">Hủy</a>
        </form>
    </div></div>
<%@ include file="../admin/layout/_admin_footer.jsp" %>






