<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ sinh viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css?v=20260323-student-refresh">
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
    <c:set var="profileAvatarValue" value="${not empty param.avatarUrl ? param.avatarUrl : (empty profileStudent ? '' : profileStudent.avatarUrl)}" />
    <c:set var="profileClassValue" value="${not empty param.className ? param.className : (empty profileStudent ? '' : profileStudent.className)}" />
    <c:set var="profileFacultyValue" value="${not empty param.facultyName ? param.facultyName : (empty profileStudent ? '' : profileStudent.facultyName)}" />
    <c:set var="profileInitial" value="${empty requestScope.studentDisplayInitial ? 'S' : requestScope.studentDisplayInitial}" />

    <div class="layout student-layout layout-two-column">
        <%@ include file="_sidebar.jsp" %>

        <main class="content student-content content-wide">
            <section class="page-hero">
                <div>
                    <span class="page-hero-kicker">Student Profile</span>
                    <div class="profile-avatar-wrap profile-hero-wrap">
                        <div class="profile-avatar-circle ${not empty profileAvatarValue ? 'has-image' : ''}">
                            <c:choose>
                                <c:when test="${not empty profileAvatarValue}">
                                    <img src="${profileAvatarValue}" alt="${profileNameValue}" class="profile-avatar-image">
                                </c:when>
                                <c:otherwise>
                                    <c:out value="${profileInitial}" />
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="profile-hero-copy">
                            <h1>Hồ sơ sinh viên</h1>
                            <p>Cập nhật thông tin cá nhân, đổi mật khẩu và kiểm tra nhanh trạng thái tài khoản đang dùng trong cổng thư viện.</p>
                            <div class="student-hero-badges">
                                <span class="student-chip">${profileAccountStatusLabel}</span>
                                <c:if test="${not empty profileStudent}">
                                    <span class="student-chip">Mã #${profileStudent.studentID}</span>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="page-hero-actions">
                    <a href="${borrowCenterUrl}" class="hero-action primary">Mở trung tâm mượn trả</a>
                    <a href="${homeUrl}" class="hero-action secondary">Về trang sinh viên</a>
                </div>
            </section>

            <section class="student-kpi-grid">
                <article class="student-kpi-card">
                    <span>Mã sinh viên</span>
                    <strong><c:out value="${empty profileStudent ? '-' : profileStudent.studentID}" /></strong>
                    <p>Bản ghi sinh viên đang liên kết với tài khoản hiện tại.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Trạng thái tài khoản</span>
                    <strong><c:out value="${profileAccountStatusLabel}" /></strong>
                    <p>Cho biết tài khoản student hiện đang ở trạng thái nào.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Ngày tạo</span>
                    <strong><c:out value="${profileCreatedAtLabel}" /></strong>
                    <p>Mốc thời gian được hệ thống ghi nhận cho hồ sơ hiện tại.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Tên đăng nhập</span>
                    <strong><c:out value="${sessionScope.staff.username}" default="-" /></strong>
                    <p>Tài khoản dùng để đăng nhập vào cổng thư viện sinh viên.</p>
                </article>
            </section>

            <c:if test="${not empty param.msg}">
                <div class="msg"><c:out value="${param.msg}" /></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error"><c:out value="${param.error}" /></div>
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
                    <section class="student-section-grid profile-shell-grid">
                        <section class="card table-card">
                            <div class="section-header-inline">
                                <div>
                                    <h2>Thông tin cá nhân</h2>
                                    <div class="note">Bạn có thể cập nhật tên, liên hệ, ảnh đại diện và thông tin học tập trực tiếp tại đây.</div>
                                </div>
                            </div>

                            <c:if test="${not empty profileError}">
                                <div class="error profile-local-error"><c:out value="${profileError}" /></div>
                            </c:if>

                            <c:if test="${not profileSupport.avatarUrlSupported or not profileSupport.classNameSupported or not profileSupport.facultyNameSupported}">
                                <div class="student-inline-alert">
                                    Một số trường mở rộng chưa được bật trong cơ sở dữ liệu hiện tại. Hệ thống vẫn lưu được các trường đang hỗ trợ.
                                </div>
                            </c:if>

                            <form method="post" action="${profileUrl}" class="profile-form-grid">
                                <input type="hidden" name="formAction" value="updateProfile">

                                <label for="studentName">Họ và tên</label>
                                <input id="studentName" type="text" name="studentName" value="${profileNameValue}" placeholder="Nhập họ và tên sinh viên" required>

                                <label for="email">Email</label>
                                <input id="email" type="email" name="email" value="${profileEmailValue}" placeholder="example@email.com">

                                <label for="phone">Số điện thoại</label>
                                <input id="phone" type="text" name="phone" value="${profilePhoneValue}" placeholder="0xx xxxx xxx">

                                <c:if test="${profileSupport.avatarUrlSupported}">
                                    <label for="avatarUrl">Ảnh đại diện</label>
                                    <input id="avatarUrl" type="text" name="avatarUrl" value="${profileAvatarValue}" placeholder="https://... hoặc /assets/...">
                                </c:if>

                                <c:if test="${profileSupport.classNameSupported}">
                                    <label for="className">Lớp</label>
                                    <input id="className" type="text" name="className" value="${profileClassValue}" placeholder="Ví dụ: CNTT 12A">
                                </c:if>

                                <c:if test="${profileSupport.facultyNameSupported}">
                                    <label for="facultyName">Khoa</label>
                                    <input id="facultyName" type="text" name="facultyName" value="${profileFacultyValue}" placeholder="Ví dụ: Công nghệ thông tin">
                                </c:if>

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
                                        <h3>Tóm tắt tài khoản</h3>
                                        <div class="note">Các dữ liệu chỉ đọc giúp bạn kiểm tra nhanh liên kết tài khoản và hồ sơ sinh viên.</div>
                                    </div>
                                </div>

                                <div class="summary-list">
                                    <div class="summary-row">
                                        <span>Mã sinh viên</span>
                                        <strong>#${profileStudent.studentID}</strong>
                                    </div>
                                    <div class="summary-row">
                                        <span>Họ và tên</span>
                                        <strong><c:out value="${profileStudent.studentName}" /></strong>
                                    </div>
                                    <div class="summary-row">
                                        <span>Lớp</span>
                                        <strong><c:out value="${empty profileStudent.className ? 'Chưa cập nhật' : profileStudent.className}" /></strong>
                                    </div>
                                    <div class="summary-row">
                                        <span>Khoa</span>
                                        <strong><c:out value="${empty profileStudent.facultyName ? 'Chưa cập nhật' : profileStudent.facultyName}" /></strong>
                                    </div>
                                    <div class="summary-row">
                                        <span>Trạng thái</span>
                                        <strong><c:out value="${profileAccountStatusLabel}" /></strong>
                                    </div>
                                    <div class="summary-row">
                                        <span>Ngày tạo</span>
                                        <strong><c:out value="${profileCreatedAtLabel}" /></strong>
                                    </div>
                                    <div class="summary-row">
                                        <span>Tên đăng nhập</span>
                                        <strong><c:out value="${sessionScope.staff.username}" default="-" /></strong>
                                    </div>
                                    <div class="summary-row">
                                        <span>Email tài khoản</span>
                                        <strong><c:out value="${empty sessionScope.staff.email ? 'Chưa liên kết' : sessionScope.staff.email}" /></strong>
                                    </div>
                                </div>
                            </section>

                            <section class="card table-card">
                                <div class="section-header-inline">
                                    <div>
                                        <h3>Bảo mật tài khoản</h3>
                                        <div class="note">Đổi mật khẩu ngay trong trang hồ sơ để giảm rủi ro dùng lại mật khẩu cũ.</div>
                                    </div>
                                </div>

                                <c:if test="${not empty passwordError}">
                                    <div class="error profile-local-error"><c:out value="${passwordError}" /></div>
                                </c:if>

                                <form method="post" action="${profileUrl}" class="profile-form-grid">
                                    <input type="hidden" name="formAction" value="changePassword">

                                    <label for="currentPassword">Mật khẩu hiện tại</label>
                                    <input id="currentPassword" type="password" name="currentPassword" placeholder="Nhập mật khẩu hiện tại" autocomplete="current-password">

                                    <label for="newPassword">Mật khẩu mới</label>
                                    <input id="newPassword" type="password" name="newPassword" placeholder="Tối thiểu 6 ký tự, có chữ hoa, chữ thường và số" autocomplete="new-password">

                                    <label for="confirmPassword">Xác nhận mật khẩu mới</label>
                                    <input id="confirmPassword" type="password" name="confirmPassword" placeholder="Nhập lại mật khẩu mới" autocomplete="new-password">

                                    <div class="actions">
                                        <button class="btn btn-primary" type="submit">Đổi mật khẩu</button>
                                    </div>
                                </form>
                            </section>

                            <section class="card table-card">
                                <div class="section-header-inline">
                                    <div>
                                        <h3>Điều hướng nhanh</h3>
                                        <div class="note">Mở nhanh các khu vực thường dùng sau khi cập nhật hồ sơ.</div>
                                    </div>
                                </div>

                                <div class="actions">
                                    <a class="btn btn-approve" href="${borrowCenterUrl}">Trung tâm mượn trả</a>
                                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/buy">Mua sách</a>
                                    <a class="btn btn-secondary" href="${homeUrl}">Dashboard cá nhân</a>
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
