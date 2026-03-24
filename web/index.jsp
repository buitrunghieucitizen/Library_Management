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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/portal-home.css">
    </head>
    <body class="portal-home-body">

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

        <c:set var="displayName" value="Thư viện" />
        <c:set var="displayInitial" value="LM" />
        <c:if test="${not empty sessionScope.staff.staffName}">
            <c:set var="displayName" value="${sessionScope.staff.staffName}" />
            <c:set var="displayInitial" value="${fn:toUpperCase(fn:substring(sessionScope.staff.staffName, 0, 1))}" />
        </c:if>
        <c:set var="roleLabel" value="${isAdmin ? 'Quản trị' : (isStaff ? 'Nhân viên' : (isStudent ? 'Sinh viên' : 'Khách'))}" />

        <nav class="navbar navbar-expand-lg navbar-dark sticky-top portal-home-nav">
            <div class="container-fluid px-3">
                <a class="navbar-brand d-flex align-items-center gap-2" href="${pageContext.request.contextPath}/index.jsp">
                    <span class="portal-brand-mark">LM</span>
                    <span class="portal-brand-title">Quản lý thư viện</span>
                </a>

                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#dashNav" aria-controls="dashNav" aria-expanded="false" aria-label="Mở điều hướng">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="dashNav">
                    <ul class="navbar-nav me-auto gap-1">
                        <c:choose>
                            <c:when test="${isAdmin}">
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/books">Sách</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/students">Sinh viên</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/bookfiles">Tệp sách</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/authors">Tác giả</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/categories">Thể loại</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/publishers">NXB</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/staffs?action=list">Nhân viên</a></li>
                            </c:when>
                            <c:when test="${isStaff}">
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/bookfiles">Tệp sách</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/books">Sách</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/admin/students">Sinh viên</a></li>
                            </c:when>
                            <c:when test="${isStudent}">
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/home">Cổng sinh viên</a></li>
                                <li class="nav-item"><a class="nav-link portal-nav-link" href="${pageContext.request.contextPath}/borrows?action=list">Mượn sách</a></li>
                            </c:when>
                        </c:choose>
                    </ul>

                    <div class="d-flex align-items-center gap-2">
                        <c:if test="${not empty sessionScope.staff}">
                            <div class="portal-user-chip">
                                <span class="portal-user-avatar">${displayInitial}</span>
                                <div class="portal-user-meta">
                                    <strong>${displayName}</strong>
                                    <span class="portal-role-badge">${roleLabel}</span>
                                </div>
                            </div>
                            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm portal-logout-btn">Đăng xuất</a>
                        </c:if>
                    </div>
                </div>
            </div>
        </nav>

        <div class="container portal-shell py-4">
            <section class="portal-hero mb-4">
                <div class="portal-hero-copy">
                    <span class="portal-hero-kicker">Library Manager</span>
                    <c:choose>
                        <c:when test="${isAdmin}">
                            <h1 class="h3 fw-bold">Xin chào, ${displayName}</h1>
                            <p>Quản trị viên có thể điều phối toàn bộ hệ thống từ kho sách, sinh viên, mượn trả, đơn hàng cho tới tệp sách và tài khoản nhân viên trong cùng một không gian làm việc.</p>
                        </c:when>
                        <c:when test="${isStaff}">
                            <h1 class="h3 fw-bold">Xin chào, ${displayName}</h1>
                            <p>Nhân viên có thể xử lý luồng vận hành hằng ngày, bao gồm phiếu mượn trả, đơn mua sách, tệp sách và tra cứu dữ liệu sách, sinh viên thật nhanh từ trang điều hướng này.</p>
                        </c:when>
                        <c:when test="${isStudent}">
                            <h1 class="h3 fw-bold">Xin chào, ${displayName}</h1>
                            <p>Sinh viên có thể truy cập cổng cá nhân để tìm sách, đặt mượn, theo dõi giao dịch và quản lý nhu cầu học tập của mình thuận tiện hơn.</p>
                        </c:when>
                        <c:otherwise>
                            <h1 class="h3 fw-bold">Tài khoản chưa được gán quyền</h1>
                            <p>Vui lòng cấu hình vai trò trong bảng StaffRole rồi đăng nhập lại để hệ thống hiển thị đúng khu vực làm việc phù hợp với tài khoản này.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>

            <div class="row g-3">
                <c:choose>
                    <c:when test="${isAdmin}">
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/books" class="portal-link-card portal-link-card--books">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
                                <h3 class="h6 fw-bold">Sách</h3>
                                <span class="portal-card-copy">Quản lý danh sách sách và tồn kho.</span>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/students" class="portal-link-card portal-link-card--students">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                                <h3 class="h6 fw-bold">Sinh viên</h3>
                                <span class="portal-card-copy">Quản lý hồ sơ và tra cứu thông tin người dùng.</span>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/borrows?action=list" class="portal-link-card portal-link-card--borrows">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
                                <h3 class="h6 fw-bold">Mượn trả</h3>
                                <span class="portal-card-copy">Duyệt phiếu mượn, xử lý trả và theo dõi giữ chỗ.</span>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/orders" class="portal-link-card portal-link-card--orders">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
                                <h3 class="h6 fw-bold">Đơn hàng</h3>
                                <span class="portal-card-copy">Quản lý đơn mua sách và trạng thái xử lý.</span>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/bookfiles" class="portal-link-card portal-link-card--files">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                                <h3 class="h6 fw-bold">Tệp sách</h3>
                                <span class="portal-card-copy">Quản lý tệp đính kèm và tài nguyên số.</span>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/authors" class="portal-link-card portal-link-card--authors">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                                <h3 class="h6 fw-bold">Tác giả</h3>
                                <span class="portal-card-copy">Quản lý hồ sơ tác giả và liên kết sách.</span>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/categories" class="portal-link-card portal-link-card--categories">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
                                <h3 class="h6 fw-bold">Thể loại</h3>
                                <span class="portal-card-copy">Chuẩn hóa danh mục và nhóm nội dung sách.</span>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/publishers" class="portal-link-card portal-link-card--publishers">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>
                                <h3 class="h6 fw-bold">Nhà xuất bản</h3>
                                <span class="portal-card-copy">Quản lý đối tác xuất bản và nguồn sách.</span>
                            </a>
                        </div>
                        <div class="col-6 col-md-4 col-lg-3">
                            <a href="${pageContext.request.contextPath}/admin/staffs?action=list" class="portal-link-card portal-link-card--staffs">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                                <h3 class="h6 fw-bold">Nhân viên</h3>
                                <span class="portal-card-copy">Tài khoản, vai trò và điều phối đội ngũ.</span>
                            </a>
                        </div>
                    </c:when>

                    <c:when test="${isStaff}">
                        <div class="col-6 col-md-4">
                            <a href="${pageContext.request.contextPath}/admin/borrows?action=list" class="portal-link-card portal-link-card--borrows">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
                                <h3 class="h6 fw-bold">Mượn trả</h3>
                                <span class="portal-card-copy">Xử lý mượn, trả và yêu cầu giữ chỗ.</span>
                            </a>
                        </div>
                        <div class="col-6 col-md-4">
                            <a href="${pageContext.request.contextPath}/admin/orders" class="portal-link-card portal-link-card--orders">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
                                <h3 class="h6 fw-bold">Đơn hàng</h3>
                                <span class="portal-card-copy">Duyệt và theo dõi đơn mua sách.</span>
                            </a>
                        </div>
                        <div class="col-6 col-md-4">
                            <a href="${pageContext.request.contextPath}/admin/bookfiles" class="portal-link-card portal-link-card--files">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                                <h3 class="h6 fw-bold">Tệp sách</h3>
                                <span class="portal-card-copy">Quản lý tài nguyên số đi kèm sách.</span>
                            </a>
                        </div>
                        <div class="col-6 col-md-4">
                            <a href="${pageContext.request.contextPath}/admin/books" class="portal-link-card portal-link-card--books">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
                                <h3 class="h6 fw-bold">Sách</h3>
                                <span class="portal-card-copy">Tra cứu kho sách và thông tin ấn phẩm.</span>
                            </a>
                        </div>
                        <div class="col-6 col-md-4">
                            <a href="${pageContext.request.contextPath}/admin/students" class="portal-link-card portal-link-card--students">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
                                <h3 class="h6 fw-bold">Sinh viên</h3>
                                <span class="portal-card-copy">Xem nhanh hồ sơ và thông tin liên hệ.</span>
                            </a>
                        </div>
                    </c:when>

                    <c:when test="${isStudent}">
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/home" class="portal-link-card portal-link-card--portal">
                                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                                <h3 class="h5 fw-bold">Cổng sinh viên</h3>
                                <span class="portal-card-copy">Tìm sách, theo dõi tài khoản và tra cứu tài nguyên.</span>
                            </a>
                        </div>
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/borrows?action=list" class="portal-link-card portal-link-card--borrows">
                                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
                                <h3 class="h5 fw-bold">Mượn sách</h3>
                                <span class="portal-card-copy">Tạo yêu cầu mượn và theo dõi lịch sử giao dịch.</span>
                            </a>
                        </div>
                        <div class="col-md-4">
                            <a href="${pageContext.request.contextPath}/borrows?action=list" class="portal-link-card portal-link-card--shop">
                                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/></svg>
                                <h3 class="h5 fw-bold">Mua sách</h3>
                                <span class="portal-card-copy">Đặt mua sách và theo dõi trạng thái xử lý đơn.</span>
                            </a>
                        </div>
                    </c:when>
                </c:choose>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
