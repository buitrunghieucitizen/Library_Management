<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:url var="homeUrl" value="/home" />
<c:url var="logoutUrl" value="/logout" />

<nav class="top-nav">
    <a href="${homeUrl}" class="brand">
        <span class="brand-mark">LM</span>
        Cong thu vien
    </a>

    <div class="nav-right">
        <c:if test="${not empty sessionScope.staff}">
            <div class="user-chip">
                <span class="user-avatar">
                    <c:choose>
                        <c:when test="${not empty sessionScope.staff.staffName}">${fn:toUpperCase(fn:substring(sessionScope.staff.staffName, 0, 1))}</c:when>
                        <c:otherwise>U</c:otherwise>
                    </c:choose>
                </span>
                <span class="user-name">${sessionScope.staff.staffName}</span>
            </div>
            <a href="${logoutUrl}" class="nav-button">Dang xuat</a>
        </c:if>
    </div>
</nav>
