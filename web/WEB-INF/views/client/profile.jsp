<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ sinh viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
</head>
<body class="student-body">
    <%@ include file="_header.jsp" %>

    <c:url var="homeUrl" value="/home" />
    <c:url var="profileUrl" value="/profile" />
    <c:url var="borrowCenterUrl" value="/borrows">
        <c:param name="action" value="list" />
    </c:url>

    <c:set var="profileNameValue" value="${not empty param.studentName ? param.studentName : (empty profileStudent ? '' : profileStudent.studentName)}" />
    <c:set var="profileEmailValue" value="${not empty param.email ? param.email : (empty profileStudent ? '' : profileStudent.email)}" />
    <c:set var="profilePhoneValue" value="${not empty param.phone ? param.phone : (empty profileStudent ? '' : profileStudent.phone)}" />

    <div class="layout student-layout layout-two-column">
        <%@ include file="_sidebar.jsp" %>

        <main class="content student-content content-wide">
            <section class="page-hero">
                <div>
                    <span class="page-hero-kicker">Student Profile</span>
                    <div class="profile-avatar-wrap">
                        <div class="profile-avatar-circle">
                            <c:choose>
                                <c:when test="${not empty studentDisplayName}">${fn:toUpperCase(fn:substring(studentDisplayName, 0, 1))}</c:when>
                                <c:when test="${not empty profileStudent.studentName}">${fn:toUpperCase(fn:substring(profileStudent.studentName, 0, 1))}</c:when>
                                <c:otherwise>S</c:otherwise>
                            </c:choose>
                        </div>
                        <h1>Hồ sơ sinh viên</h1>
                    </div>
                    <p>Cập nhật thông tin liên hệ, theo dõi mã sinh viên đang liên kết và kiểm tra nhanh tài khoản sử dụng trên cổng thư viện.</p>
                </div>
                <div class="page-hero-actions">
                    <a href="${borrowCenterUrl}" class="hero-action primary">Mở trung tâm mượn trả</a>
                    <a href="${homeUrl}" class="hero-action secondary">Về trang sinh viên</a>
                </div>
            </section>

            <section class="student-kpi-grid">
                <article class="student-kpi-card">
                    <span>Tên hiển thị</span>
                    <strong><c:out value="${studentDisplayName}" default="Sinh viên thư viện" /></strong>
                    <p>Tên này sẽ xuất hiện trên header của khu vực sinh viên.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Mã sinh viên</span>
                    <strong><c:out value="${profileStudent.studentID}" default="-" /></strong>
                    <p>Mã sinh viên đang được liên kết với tài khoản đăng nhập hiện tại.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Email</span>
                    <strong><c:out value="${empty profileStudent.email ? 'Chưa có' : profileStudent.email}" /></strong>
                    <p>Thông tin email để thư viện đối chiếu khi cần liên hệ.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Số điện thoại</span>
                    <strong><c:out value="${empty profileStudent.phone ? 'Chưa có' : profileStudent.phone}" /></strong>
                    <p>Bạn có thể cập nhật số điện thoại ngay trong biểu mẫu bên dưới.</p>
                </article>
            </section>

            <c:if test="${not empty param.msg}">
                <div class="msg">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error">${param.error}</div>
            </c:if>
            <c:if test="${not empty profileError}">
                <div class="error">${profileError}</div>
            </c:if>

            <c:choose>
                <c:when test="${empty profileStudent}">
                    <section class="card table-card">
                        <div class="section-header-inline">
                            <div>
                                <h2>Không có hồ sơ sinh viên</h2>
                                <div class="note">Tài khoản hiện tại chưa liên kết được tới bản ghi sinh viên trong hệ thống.</div>
                            </div>
                        </div>

                        <div class="summary-list">
                            <div class="summary-row">
                                <span>Tài khoản đăng nhập</span>
                                <strong><c:out value="${sessionScope.staff.username}" default="-" /></strong>
                            </div>
                            <div class="summary-row">
                                <span>Tên tài khoản</span>
                                <strong><c:out value="${sessionScope.staff.staffName}" default="-" /></strong>
                            </div>
                        </div>
                    </section>
                </c:when>
                <c:otherwise>
                    <section class="student-section-grid">
                        <section class="card table-card">
                            <div class="section-header-inline">
                                <div>
                                    <h2>Cập nhật thông tin cá nhân</h2>
                                    <div class="note">Chỉnh sửa tên, email và số điện thoại để đồng bộ trên phần đầu trang và các màn hình sinh viên.</div>
                                </div>
                            </div>

                            <form method="post" action="${profileUrl}">
                                <label for="studentName">Họ và tên</label>
                                <input id="studentName" type="text" name="studentName" value="${profileNameValue}" placeholder="Nhập họ và tên sinh viên" required>

                                <label for="email">Email</label>
                                <input id="email" type="email" name="email" value="${profileEmailValue}" placeholder="example@email.com">

                                <label for="phone">Số điện thoại</label>
                                <input id="phone" type="text" name="phone" value="${profilePhoneValue}" placeholder="0xx xxxx xxx">

                                <div class="actions">
                                    <button class="btn btn-primary" type="submit">Lưu cập nhật</button>
                                    <a class="btn btn-secondary" href="${profileUrl}">Khôi phục dữ liệu</a>
                                </div>
                            </form>
                        </section>

                        <div class="panel-stack">
                            <section class="card table-card">
                                <div class="section-header-inline">
                                    <div>
                                        <h3>Thông tin tài khoản</h3>
                                        <div class="note">Các dữ liệu chỉ đọc để bạn kiểm tra nhanh liên kết tài khoản.</div>
                                    </div>
                                </div>

                                <div class="summary-list">
                                    <div class="summary-row">
                                        <span>Mã sinh viên</span>
                                        <strong>#${profileStudent.studentID}</strong>
                                    </div>
                                    <div class="summary-row">
                                        <span>Tên sinh viên</span>
                                        <strong><c:out value="${profileStudent.studentName}" /></strong>
                                    </div>
                                    <div class="summary-row">
                                        <span>Tên đăng nhập</span>
                                        <strong><c:out value="${sessionScope.staff.username}" default="-" /></strong>
                                    </div>
                                    <div class="summary-row">
                                        <span>Tên tài khoản gốc</span>
                                        <strong><c:out value="${sessionScope.staff.staffName}" default="-" /></strong>
                                    </div>
                                </div>
                            </section>

                            <section class="card table-card">
                                <div class="section-header-inline">
                                    <div>
                                        <h3>Điều hướng nhanh</h3>
                                        <div class="note">Mở nhanh các khu vực mà sinh viên thường dùng sau khi cập nhật hồ sơ.</div>
                                    </div>
                                </div>

                                <div class="actions">
                                    <a class="btn btn-approve" href="${borrowCenterUrl}">Trung tâm mượn trả</a>
                                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/buy">Mua sách</a>
                                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/books">Danh mục sách</a>
                                </div>
                            </section>
                        </div>
                    </section>
                </c:otherwise>
            </c:choose>
        </main>
    </div>

    <%@ include file="_footer.jsp" %>
</body>
</html>
