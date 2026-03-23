<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="currentUri" value="${pageContext.request.requestURI}" />
<c:url var="homeUrl" value="/home" />
<c:url var="booksUrl" value="/books" />
<c:url var="borrowsUrl" value="/borrows">
    <c:param name="action" value="list" />
</c:url>
<c:url var="dashboardUrl" value="/index.jsp" />
<c:url var="logoutUrl" value="/logout" />

<aside class="sidebar-left">
    <div class="section-title">Cong</div>

    <div class="nav-item">
        <a href="${homeUrl}" class="${fn:contains(currentUri, '/home') ? 'active' : ''}">
            Trang sinh vien
        </a>
    </div>

    <div class="nav-item">
        <a href="${booksUrl}" class="${fn:contains(currentUri, '/books') ? 'active' : ''}">
            Danh muc sach
        </a>
    </div>

    <div class="nav-item">
        <a href="${borrowsUrl}" class="${fn:contains(currentUri, '/borrows') ? 'active' : ''}">
            Trung tam muon tra
        </a>
    </div>

    <div class="divider"></div>
    <div class="section-title">Tai khoan</div>

    <div class="nav-item">
        <a href="${dashboardUrl}" class="${fn:contains(currentUri, '/index.jsp') ? 'active' : ''}">
            Bang dieu khien
        </a>
    </div>

    <div class="nav-item">
        <a href="${logoutUrl}">
            Dang xuat
        </a>
    </div>
</aside>
