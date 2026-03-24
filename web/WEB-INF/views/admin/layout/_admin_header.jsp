<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:out value="${empty pageTitle ? (empty dashboardTitle ? 'Quản trị hệ thống' : dashboardTitle) : pageTitle}" />
    </title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-dashboard.css">
    <c:if test="${not empty pageStylesheet}">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/${pageStylesheet}">
    </c:if>
</head>
<body class="dashboard-body">
    <c:set var="ctx" value="${pageContext.request.contextPath}" />
    <c:set var="operatorName" value="Đội ngũ thư viện" />
    <c:if test="${not empty sessionScope.staff.staffName}">
        <c:set var="operatorName" value="${sessionScope.staff.staffName}" />
    </c:if>
    <c:set var="isAdminNav" value="false" />
    <c:if test="${not empty sessionScope.roles}">
        <c:forEach var="roleId" items="${sessionScope.roles}">
            <c:if test="${roleId == 1}">
                <c:set var="isAdminNav" value="true" />
            </c:if>
        </c:forEach>
    </c:if>
    <c:set var="operatorBadge" value="${isAdminNav ? 'AD' : 'ST'}" />

    <div class="dashboard-shell" id="dashboardShell">
        <div class="dashboard-overlay" id="dashboardOverlay"></div>

        <aside class="dashboard-sidebar" id="dashboardSidebar">
            <div class="sidebar-brand">
                <span class="brand-badge">LM</span>
                <div class="brand-copy">
                    <span class="brand-label">Library Manager</span>
                    <strong>Control Center</strong>
                </div>
            </div>

            <div class="sidebar-scroll">
                <nav class="sidebar-nav">
                    <div class="sidebar-group">
                        <span class="sidebar-group-label">Tổng quan</span>
                        <a class="sidebar-link ${activeTab eq 'dashboard' ? 'active' : ''}" href="${ctx}/admin/dashboard" title="Dashboard">
                            <span class="sidebar-link-mark">DB</span>
                            <span>Dashboard</span>
                        </a>
                    </div>

                    <div class="sidebar-group">
                        <span class="sidebar-group-label">Vận hành</span>
                        <a class="sidebar-link ${activeTab eq 'books' ? 'active' : ''}" href="${ctx}/admin/books?action=list" title="Kho sách">
                            <span class="sidebar-link-mark">BK</span>
                            <span>Kho sách</span>
                        </a>
                        <a class="sidebar-link ${activeTab eq 'borrows' ? 'active' : ''}" href="${ctx}/admin/borrows?action=list" title="Mượn trả">
                            <span class="sidebar-link-mark">BR</span>
                            <span>Mượn trả</span>
                        </a>
                        <a class="sidebar-link ${activeTab eq 'orders' ? 'active' : ''}" href="${ctx}/admin/orders?action=list" title="Đơn hàng">
                            <span class="sidebar-link-mark">OR</span>
                            <span>Đơn hàng</span>
                        </a>
                        <a class="sidebar-link ${activeTab eq 'students' ? 'active' : ''}" href="${ctx}/admin/students?action=list" title="Sinh viên">
                            <span class="sidebar-link-mark">ST</span>
                            <span>Sinh viên</span>
                        </a>
                        <a class="sidebar-link ${activeTab eq 'bookfiles' ? 'active' : ''}" href="${ctx}/admin/bookfiles?action=list" title="Tệp sách">
                            <span class="sidebar-link-mark">FL</span>
                            <span>Tệp sách</span>
                        </a>
                    </div>

                    <c:if test="${isAdminNav}">
                        <div class="sidebar-group">
                            <span class="sidebar-group-label">Quản trị</span>
                            <a class="sidebar-link ${activeTab eq 'authors' ? 'active' : ''}" href="${ctx}/admin/authors?action=list" title="Tác giả">
                                <span class="sidebar-link-mark">AU</span>
                                <span>Tác giả</span>
                            </a>
                            <a class="sidebar-link ${activeTab eq 'categories' ? 'active' : ''}" href="${ctx}/admin/categories?action=list" title="Thể loại">
                                <span class="sidebar-link-mark">CT</span>
                                <span>Thể loại</span>
                            </a>
                            <a class="sidebar-link ${activeTab eq 'publishers' ? 'active' : ''}" href="${ctx}/admin/publishers?action=list" title="Nhà xuất bản">
                                <span class="sidebar-link-mark">PB</span>
                                <span>Nhà xuất bản</span>
                            </a>
                            <a class="sidebar-link ${activeTab eq 'staffs' ? 'active' : ''}" href="${ctx}/admin/staffs?action=list" title="Nhân viên">
                                <span class="sidebar-link-mark">SF</span>
                                <span>Nhân viên</span>
                            </a>
                        </div>
                    </c:if>
                </nav>
            </div>

            <div class="sidebar-footer">
                <div class="sidebar-user-chip">
                    <span class="sidebar-user-avatar"><c:out value="${operatorBadge}" /></span>
                    <div class="sidebar-user-copy">
                        <strong><c:out value="${operatorName}" /></strong>
                        <span><c:out value="${empty dashboardRoleLabel ? (isAdminNav ? 'Administrator' : 'Staff') : dashboardRoleLabel}" /></span>
                    </div>
                </div>
                <a class="sidebar-logout" href="${ctx}/logout" title="Đăng xuất">
                    <span class="sidebar-logout-icon">LG</span>
                    <span class="sidebar-logout-text">Đăng xuất</span>
                </a>
            </div>
        </aside>

        <div class="dashboard-main">
            <header class="dashboard-topbar">
                <div class="topbar-intro">
                    <button class="sidebar-toggle" type="button" id="sidebarToggle" aria-label="Mở menu điều hướng">
                        <span></span>
                        <span></span>
                        <span></span>
                    </button>
                    <div class="topbar-copy">
                        <span class="topbar-kicker"><c:out value="${empty pageSubtitle ? (empty dashboardLabel ? 'Control Panel' : dashboardLabel) : pageSubtitle}" /></span>
                        <h1><c:out value="${empty pageTitle ? (empty dashboardTitle ? 'Quản trị hệ thống' : dashboardTitle) : pageTitle}" /></h1>
                    </div>
                </div>

                <div class="topbar-actions">
                    <a class="topbar-button secondary" href="${ctx}/admin/books?action=list">Kho sách</a>
                    <a class="topbar-button secondary" href="${ctx}/admin/borrows?action=list">Mượn trả</a>
                    <a class="topbar-button secondary" href="${ctx}/chatbot">Trợ lý AI</a>
                    <a class="topbar-button primary" href="${ctx}/admin/orders?action=list">Đơn cần xử lý</a>
                    <div class="topbar-user">
                        <span class="topbar-user-avatar"><c:out value="${operatorBadge}" /></span>
                        <div>
                            <strong><c:out value="${operatorName}" /></strong>
                            <span>Hôm nay</span>
                        </div>
                    </div>
                </div>
            </header>

            <main class="dashboard-content">
