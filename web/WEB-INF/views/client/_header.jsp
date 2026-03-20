<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:url var="homeUrl" value="/home" />
<c:url var="profileUrl" value="/profile" />
<c:url var="logoutUrl" value="/logout" />
<c:set var="viewerName" value="${empty requestScope.studentDisplayName ? (empty sessionScope.staff.staffName ? 'Sinh viên thư viện' : sessionScope.staff.staffName) : requestScope.studentDisplayName}" />
<c:set var="viewerInitial" value="${empty requestScope.studentDisplayInitial ? (empty viewerName ? 'S' : fn:toUpperCase(fn:substring(viewerName, 0, 1))) : requestScope.studentDisplayInitial}" />

<nav class="top-nav student-top-nav">
    <div class="top-nav-shell">
        <div class="top-nav-left">
            <button class="student-nav-toggle" type="button" id="studentNavToggle" aria-label="Mở menu sinh viên">
                <span></span>
                <span></span>
                <span></span>
            </button>

            <a href="${homeUrl}" class="brand">
                <span class="brand-mark">LM</span>
                <span class="brand-copy">
                    <span class="brand-overline">Student Portal</span>
                    <strong>Cổng thư viện</strong>
                </span>
            </a>
        </div>

        <div class="nav-right">
            <c:if test="${not empty sessionScope.staff}">
                <a href="${profileUrl}" class="user-chip user-chip-link">
                    <span class="user-avatar"><c:out value="${viewerInitial}" /></span>
                    <span class="user-name"><c:out value="${viewerName}" /></span>
                </a>
                <a href="${logoutUrl}" class="nav-button">Đăng xuất</a>
            </c:if>
        </div>
    </div>
</nav>
<div class="student-nav-overlay" id="studentNavOverlay"></div>
