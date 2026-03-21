<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Sửa tác giả" />
    <c:set var="activeTab" value="authors" />
    <%@ include file="../layout/_admin_header.jsp" %>

    <div class="container">
        <div class="card">
            <h2>Sửa tác giả</h2>

            <c:if test="${not empty error}">
                <div class="error"><c:out value="${error}" /></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error"><c:out value="${param.error}" /></div>
            </c:if>

            <form method="POST" action="${pageContext.request.contextPath}/admin/authors?action=edit">
                <input type="hidden" name="authorID" value="${author.authorID}">

                <div class="field">
                    <label for="authorName">Tên tác giả</label>
                    <input id="authorName" type="text" name="authorName" value="${author.authorName}" required>
                </div>

                <div class="actions">
                    <button class="btn btn-primary" type="submit">Cập nhật</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/authors?action=list">Hủy</a>
                </div>
            </form>
        </div>
    </div>
<%@ include file="../layout/_admin_footer.jsp" %>
