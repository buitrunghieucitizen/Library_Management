<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="currentUri" value="${pageContext.request.requestURI}" />
<c:url var="homeUrl" value="/home" />
<c:url var="booksUrl" value="/books" />
<c:url var="profileUrl" value="/profile" />
<c:url var="borrowsUrl" value="/borrows">
    <c:param name="action" value="list" />
</c:url>
<c:url var="dashboardUrl" value="/index.jsp" />
<c:url var="logoutUrl" value="/logout" />
<c:set var="viewerName" value="${empty requestScope.studentDisplayName ? (empty sessionScope.staff.staffName ? 'Sinh viên thư viện' : sessionScope.staff.staffName) : requestScope.studentDisplayName}" />

<aside class="sidebar-left student-sidebar" id="studentSidebar">
    <div class="student-sidebar-head">
        <span class="section-title">Điều hướng</span>
    </div>

    <div class="nav-item">
        <a href="${homeUrl}" class="${fn:contains(currentUri, '/home') ? 'active' : ''}">
            <span class="nav-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
            </span>
            <span>Trang sinh viên</span>
        </a>
    </div>

    <div class="nav-item">
        <a href="${booksUrl}" class="${fn:contains(currentUri, '/books') ? 'active' : ''}">
            <span class="nav-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
            </span>
            <span>Danh mục sách</span>
        </a>
    </div>

    <div class="nav-item">
        <a href="${borrowsUrl}" class="${fn:contains(currentUri, '/borrows') ? 'active' : ''}">
            <span class="nav-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 12H5"/><path d="m12 5 7 7-7 7"/><path d="m5 5-2 2 2 2"/></svg>
            </span>
            <span>Trung tâm mượn trả</span>
        </a>
    </div>

    <div class="nav-item">
        <a href="${pageContext.request.contextPath}/buy" class="${fn:contains(currentUri, '/buy') ? 'active' : ''}">
            <span class="nav-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
            </span>
            <span>Mua sách</span>
        </a>
    </div>

    <div class="nav-item">
        <a href="${profileUrl}" class="${fn:contains(currentUri, '/profile') ? 'active' : ''}">
            <span class="nav-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </span>
            <span>Hồ sơ sinh viên</span>
        </a>
    </div>

    <div class="divider"></div>
    <div class="section-title">Tài khoản</div>

    <div class="nav-item">
        <a href="${dashboardUrl}" class="${fn:contains(currentUri, '/index.jsp') ? 'active' : ''}">
            <span class="nav-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
            </span>
            <span>Bảng điều khiển</span>
        </a>
    </div>

    <div class="nav-item">
        <a href="${logoutUrl}" class="nav-item-logout">
            <span class="nav-icon nav-icon-danger">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
            </span>
            <span>Đăng xuất</span>
        </a>
    </div>

    <div class="student-sidebar-card">
        <span class="student-sidebar-kicker">Tài khoản hiện tại</span>
        <strong><c:out value="${viewerName}" /></strong>
        <span>
            <c:choose>
                <c:when test="${not empty currentStudent}">
                    Mã sinh viên #${currentStudent.studentID}
                </c:when>
                <c:otherwise>
                    Đăng nhập với vai trò sinh viên
                </c:otherwise>
            </c:choose>
        </span>
    </div>
</aside>
