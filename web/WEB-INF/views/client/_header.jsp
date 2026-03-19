<c:url var="homeUrl" value="/home" />
<c:url var="logoutUrl" value="/logout" />
<c:set var="viewerName" value="${empty sessionScope.staff.staffName ? 'Sinh viên thư viện' : sessionScope.staff.staffName}" />

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
                <div class="user-chip">
                    <span class="user-avatar">
                        <c:choose>
                            <c:when test="${not empty sessionScope.staff.staffName}">${fn:toUpperCase(fn:substring(sessionScope.staff.staffName, 0, 1))}</c:when>
                            <c:otherwise>U</c:otherwise>
                        </c:choose>
                    </span>
                    <span class="user-name"><c:out value="${viewerName}" /></span>
                </div>
                <a href="${logoutUrl}" class="nav-button">Đăng xuất</a>
            </c:if>
        </div>
    </div>
</nav>
<div class="student-nav-overlay" id="studentNavOverlay"></div>
