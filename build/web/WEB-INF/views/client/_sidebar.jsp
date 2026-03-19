<%@ page pageEncoding="UTF-8" %>
<c:set var="currentUri" value="${pageContext.request.requestURI}" />
<c:url var="homeUrl" value="/home" />
<c:url var="booksUrl" value="/books" />
<c:url var="borrowsUrl" value="/borrows">
    <c:param name="action" value="list" />
</c:url>
<c:url var="dashboardUrl" value="/index.jsp" />
<c:url var="logoutUrl" value="/logout" />
<c:set var="viewerName" value="${empty sessionScope.staff.staffName ? 'Sinh viên thư viện' : sessionScope.staff.staffName}" />

<aside class="sidebar-left student-sidebar" id="studentSidebar">
    <div class="student-sidebar-head">
        <span class="section-title">Điều hướng</span>
        <p class="student-sidebar-copy">Truy cập nhanh các phân hệ chính của sinh viên trong một layout thống nhất.</p>
    </div>

    <div class="nav-item">
        <a href="${homeUrl}" class="${fn:contains(currentUri, '/home') ? 'active' : ''}">
            <span class="nav-icon">HM</span>
            <span>Trang sinh viên</span>
        </a>
    </div>

    <div class="nav-item">
        <a href="${booksUrl}" class="${fn:contains(currentUri, '/books') ? 'active' : ''}">
            <span class="nav-icon">BK</span>
            <span>Danh mục sách</span>
        </a>
    </div>

    <div class="nav-item">
        <a href="${borrowsUrl}" class="${fn:contains(currentUri, '/borrows') ? 'active' : ''}">
            <span class="nav-icon">BR</span>
            <span>Trung tâm mượn trả</span>
        </a>
    </div>

    <div class="nav-item">
        <a href="${pageContext.request.contextPath}/buy" class="${fn:contains(currentUri, '/buy') ? 'active' : ''}">
            <span class="nav-icon">BY</span>
            <span>Mua sách</span>
        </a>
    </div>

    <div class="divider"></div>
    <div class="section-title">Tài khoản</div>

    <div class="nav-item">
        <a href="${dashboardUrl}" class="${fn:contains(currentUri, '/index.jsp') ? 'active' : ''}">
            <span class="nav-icon">DB</span>
            <span>Bảng điều khiển</span>
        </a>
    </div>

    <div class="nav-item">
        <a href="${logoutUrl}">
            <span class="nav-icon">LG</span>
            <span>Đăng xuất</span>
        </a>
    </div>

    <div class="student-sidebar-card">
        <span class="student-sidebar-kicker">Tài khoản hiện tại</span>
        <strong><c:out value="${viewerName}" /></strong>
        <span>Đăng nhập với vai trò sinh viên</span>
    </div>
</aside>
