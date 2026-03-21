<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý thư viện</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    </head>
    <body class="bg-body-tertiary">

        <%-- Role detection --%>
        <c:set var="isAdmin" value="false" />
        <c:set var="isStaff" value="false" />
        <c:set var="isStudent" value="false" />
        <c:if test="${not empty sessionScope.roles}">
            <c:forEach var="roleId" items="${sessionScope.roles}">
                <c:if test="${roleId == 1}"><c:set var="isAdmin" value="true" /></c:if>
                <c:if test="${roleId == 2 || roleId == 4}"><c:set var="isStaff" value="true" /></c:if>
                <c:if test="${roleId == 8 || roleId == 9}"><c:set var="isStudent" value="true" /></c:if>
            </c:forEach>
        </c:if>

        <%-- Navbar --%>
        <nav class="navbar navbar-expand-lg navbar-dark sticky-top" style="background:linear-gradient(135deg,#1a2744 0%,#2a5298 100%);">
            <div class="container-fluid px-3">
                <a class="navbar-brand d-flex align-items-center gap-2" href="${pageContext.request.contextPath}/index.jsp">
                    <span class="d-flex align-items-center justify-content-center rounded-2 fw-bold"
                          style="width:32px;height:32px;background:rgba(255,255,255,.15);font-size:13px;color:#fff;">LM</span>
                    <span class="fw-semibold" style="font-size:15px;">Quản lý thư viện</span>
                </a>

                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#dashNav">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="dashNav">
                    <ul class="navbar-nav me-auto gap-1">
                        <c:choose>
                            <c:when test="${isAdmin}">
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/books">Sách</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/students">Sinh viên</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/bookfiles">Tệp sách</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/authors">Tác giả</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/categories">Thể loại</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/publishers">NXB</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/staffs?action=list">Nhân viên</a></li>
                                </c:when>
                                <c:when test="${isStaff}">
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/bookfiles">Tệp sách</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/books">Sách</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/students">Sinh viên</a></li>
                                </c:when>
                                <c:when test="${isStudent}">
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/home">Cổng sinh viên</a></li>
                                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/borrows?action=list">Mượn sách</a></li>
                                </c:when>
                            </c:choose>
                    </ul>

                    <div class="d-flex align-items-center gap-2">
                        <c:if test="${not empty sessionScope.staff}">
                            <div class="d-flex align-items-center gap-2">
                                <div class="d-flex align-items-center justify-content-center rounded-circle text-white fw-semibold"
                                     style="width:30px;height:30px;background:rgba(255,255,255,.2);font-size:12px;">
                                    ${fn:toUpperCase(fn:substring(sessionScope.staff.staffName, 0, 1))}
                                </div>
                                <span class="text-white-50 d-none d-lg-inline" style="font-size:13px;">
                                    ${sessionScope.staff.staffName}
                                    <span class="badge text-bg-light ms-1" style="font-size:10px;">
                                        ${isAdmin ? 'Quản trị' : (isStaff ? 'Nhân viên' : (isStudent ? 'Sinh viên' : '?'))}
                                    </span>
                                </span>
                            </div>
                            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-light" style="font-size:12px;">Đăng xuất</a>
                        </c:if>
                    </div>
                </div>
            </div>
        </nav>

        <%-- Main content --%>
        <div class="container py-4" style="max-width:1200px;">

            <%-- Hero card --%>
            <div class="rounded-4 text-white p-4 mb-4" style="background:linear-gradient(135deg,#1a2744 0%,#2a5298 100%);">
                <c:choose>
                    <c:when test="${isAdmin}">
                        <h1 class="h4 fw-bold mb-1">Xin chào, ${sessionScope.staff.staffName}</h1>
                        <p class="mb-0 opacity-75" style="font-size:14px;">Quản trị viên có thể quản lý toàn bộ hệ thống: sách, sinh viên, mượn trả, đơn hàng, tệp sách và tài khoản nhân viên.</p>
                    </c:when>
                    <c:when test="${isStaff}">
                        <h1 class="h4 fw-bold mb-1">Xin chào, ${sessionScope.staff.staffName}</h1>
                        <p class="mb-0 opacity-75" style="font-size:14px;">Nhân viên có thể quản lý mượn trả, duyệt đơn hàng, quản lý tệp sách và xem thông tin sách, sinh viên.</p>
                    </c:when>
                    <c:when test="${isStudent}">
                        <h1 class="h4 fw-bold mb-1">Xin chào, ${sessionScope.staff.staffName}</h1>
                        <p class="mb-0 opacity-75" style="font-size:14px;">Sinh viên có thể vào cổng sinh viên để tìm sách, mượn sách, hoặc đặt mua sách.</p>
                    </c:when>
                    <c:otherwise>
                        <h1 class="h4 fw-bold mb-1">Tài khoản chưa được gán quyền</h1>
                        <p class="mb-0 opacity-75" style="font-size:14px;">Vui lòng gán vai trò trong bảng StaffRole rồi đăng nhập lại.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <%-- Grid items --%>
            <div class="row g-3">
                <c:choose>
                    <%-- === ADMIN === --%>
                    <c:when test="${isAdmin}">
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/books" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#1a2744,#2a5298);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Sách</h3>
                                    <small class="opacity-75">Quản lý danh sách sách</small>
                                </div>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/students" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#0f766e,#14b8a6);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Sinh viên</h3>
                                    <small class="opacity-75">Quản lý sinh viên</small>
                                </div>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/borrows?action=list" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#9333ea,#c084fc);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Mượn trả</h3>
                                    <small class="opacity-75">Duyệt phiếu mượn & trả sách</small>
                                </div>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/orders" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#dc2626,#f87171);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Đơn hàng</h3>
                                    <small class="opacity-75">Quản lý đơn hàng sách</small>
                                </div>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/bookfiles" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#d97706,#fbbf24);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Tệp sách</h3>
                                    <small class="opacity-75">Tệp đính kèm sách</small>
                                </div>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/authors" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#059669,#34d399);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Tác giả</h3>
                                    <small class="opacity-75">Quản lý tác giả</small>
                                </div>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/categories" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#4f46e5,#818cf8);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Thể loại</h3>
                                    <small class="opacity-75">Quản lý thể loại</small>
                                </div>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/publishers" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#be185d,#f472b6);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Nhà xuất bản</h3>
                                    <small class="opacity-75">Quản lý NXB</small>
                                </div>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/staffs?action=list" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#475569,#94a3b8);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Nhân viên</h3>
                                    <small class="opacity-75">Tài khoản và vai trò</small>
                                </div>
                            </a>
                        </div>
                    </c:when>

                    <%-- === STAFF === --%>
                    <c:when test="${isStaff}">
                        <div class="col-6 col-md-4">
                            <a href="${pageContext.request.contextPath}/admin/borrows?action=list" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#9333ea,#c084fc);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Mượn trả</h3>
                                    <small class="opacity-75">Xác nhận mượn trả sách</small>
                                </div>
                            </a>
                        </div>
                        <div class="col-6 col-md-4">
                            <a href="${pageContext.request.contextPath}/admin/orders" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#dc2626,#f87171);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Đơn hàng</h3>
                                    <small class="opacity-75">Duyệt và từ chối đơn mua</small>
                                </div>
                            </a>
                        </div>
                        <div class="col-6 col-md-4">
                            <a href="${pageContext.request.contextPath}/admin/bookfiles" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#d97706,#fbbf24);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Tệp sách</h3>
                                    <small class="opacity-75">Quản lý tệp đính kèm</small>
                                </div>
                            </a>
                        </div>
                        <div class="col-6 col-md-4">
                            <a href="${pageContext.request.contextPath}/admin/books" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#1a2744,#2a5298);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Sách</h3>
                                    <small class="opacity-75">Xem danh sách sách</small>
                                </div>
                            </a>
                        </div>
                        <div class="col-6 col-md-4">
                            <a href="${pageContext.request.contextPath}/admin/students" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#0f766e,#14b8a6);transition:transform .15s;">
                                <div class="card-body text-white p-3">
                                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
                                    <h3 class="h6 fw-bold mb-1">Sinh viên</h3>
                                    <small class="opacity-75">Xem danh sách sinh viên</small>
                                </div>
                            </a>
                        </div>
                    </c:when>

                    <%-- === STUDENT === --%>
                    <c:when test="${isStudent}">
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/home" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#1a2744,#2a5298);transition:transform .15s;">
                                <div class="card-body text-white p-4">
                                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                                    <h3 class="h5 fw-bold mb-1">Cổng sinh viên</h3>
                                    <p class="mb-0 opacity-75" style="font-size:14px;">Tìm sách, mượn sách, đặt giữ chỗ</p>
                                </div>
                            </a>
                        </div>
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/borrows?action=list" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#9333ea,#c084fc);transition:transform .15s;">
                                <div class="card-body text-white p-4">
                                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
                                    <h3 class="h5 fw-bold mb-1">Mượn sách</h3>
                                    <p class="mb-0 opacity-75" style="font-size:14px;">Giao dịch mượn và trả sách</p>
                                </div>
                            </a>
                        </div>
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/borrows?action=list" class="card h-100 border-0 text-decoration-none" style="border-radius:12px;background:linear-gradient(135deg,#dc2626,#f87171);transition:transform .15s;">
                                <div class="card-body text-white p-4">
                                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="mb-2 opacity-75"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
                                    <h3 class="h5 fw-bold mb-1">Mua sách</h3>
                                    <p class="mb-0 opacity-75" style="font-size:14px;">Đặt mua sách và theo dõi đơn hàng</p>
                                </div>
                            </a>
                        </div>
                    </c:when>
                </c:choose>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <style>
            .card:hover {
                transform: translateY(-4px);
                box-shadow: 0 8px 24px rgba(0,0,0,.12);
            }
        </style>
    </body>
</html>
