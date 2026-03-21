<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Thêm tác giả" />
    <c:set var="activeTab" value="authors" />
    <%@ include file="../layout/_admin_header.jsp" %>

    <div class="container">
        <div class="card">
            <h2>Thêm tác giả</h2>

            <c:if test="${not empty error}">
                <div class="error"><c:out value="${error}" /></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error"><c:out value="${param.error}" /></div>
            </c:if>

            <form method="POST" action="${pageContext.request.contextPath}/admin/authors?action=create">
                <div class="field">
                    <label for="authorName">Tên tác giả</label>
                    <input id="authorName" type="text" name="authorName" value="${authorName}" required>
                </div>

                <div class="actions">
                    <button class="btn btn-primary" type="submit">Lưu</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/authors?action=list">Hủy</a>
                </div>
            </form>
        </div>
    </div>
<%@ include file="../layout/_admin_footer.jsp" %>
