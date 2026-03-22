<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Quản lý nhân viên" />
<c:set var="pageSubtitle" value="Điều phối tài khoản và quyền truy cập" />
    <c:set var="activeTab" value="staffs" />
    <%@ include file="../layout/_admin_header.jsp" %>

    <div class="container staff-page-shell">
        <c:if test="${not empty param.msg}">
            <div class="msg"><c:out value="${param.msg}" /></div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="error"><c:out value="${param.error}" /></div>
        </c:if>

        <section class="hero-grid staff-overview-grid">
            <article class="hero-card hero-card-main">
                <span class="hero-kicker">Điều phối nhân sự</span>
                <h2>Quản lý nhân sự thư viện</h2>
                <p>Tập trung quản lý tài khoản nội bộ, phân quyền truy cập và trạng thái liên hệ ngay trên một màn hình điều phối thống nhất.</p>

                <div class="hero-stat-strip">
                    <div class="hero-stat">
                        <span>Đang hiển thị</span>
                        <strong>${totalItems}/${allStaffCount}</strong>
                    </div>
                    <div class="hero-stat">
                        <span>Quyền đã gán</span>
                        <strong>${roleAssignmentCount}</strong>
                    </div>
                    <div class="hero-stat">
                        <span>Đa vai trò</span>
                        <strong>${multiRoleCount}</strong>
                    </div>
                </div>

                <div class="hero-cta-row">
                    <a class="hero-cta primary" href="${ctx}/admin/staffs?action=create">+ Thêm nhân viên</a>
                    <c:choose>
                        <c:when test="${searchActive}">
                            <a class="hero-cta secondary" href="${ctx}/admin/staffs?action=list">Xóa bộ lọc</a>
                        </c:when>
                        <c:otherwise>
                            <a class="hero-cta secondary" href="${ctx}/admin/dashboard">Về dashboard</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </article>

            <article class="hero-card hero-card-side">
                <div class="hero-side-head">
                    <span class="hero-kicker hero-kicker-soft">Tình trạng hiện tại</span>
                    <c:choose>
                        <c:when test="${searchActive}">
                            <h3>Kết quả cho "<c:out value="${searchKeyword}" />"</h3>
                        </c:when>
                        <c:otherwise>
                            <h3>Toàn bộ tài khoản vận hành</h3>
                        </c:otherwise>
                    </c:choose>
                    <p>Ưu tiên bổ sung email cho tài khoản nội bộ và rà soát các tài khoản đang được gán nhiều quyền truy cập.</p>
                </div>

                <div class="hero-summary-list">
                    <div class="hero-summary-row">
                        <span>Quản trị viên</span>
                        <strong>${adminCount}</strong>
                    </div>
                    <div class="hero-summary-row">
                        <span>Vận hành thư viện</span>
                        <strong>${operationsCount}</strong>
                    </div>
                    <div class="hero-summary-row">
                        <span>Liên kết sinh viên</span>
                        <strong>${studentLinkedCount}</strong>
                    </div>
                    <div class="hero-summary-row">
                        <span>Thiếu email</span>
                        <strong>${missingEmailCount}</strong>
                    </div>
                </div>
            </article>
        </section>

        <section class="metric-grid">
            <article class="metric-card tone-blue">
                <span class="metric-label">Tài khoản hiển thị</span>
                <strong class="metric-value">${totalItems}</strong>
                <p><c:choose>
                        <c:when test="${searchActive}">Khớp với bộ lọc hiện tại</c:when>
                        <c:otherwise>Tổng số tài khoản trong hệ thống</c:otherwise>
                    </c:choose></p>
            </article>

            <article class="metric-card tone-green">
                <span class="metric-label">Đã có email</span>
                <strong class="metric-value">${emailCount}</strong>
                <p>Sẵn sàng nhận thông báo và khôi phục mật khẩu</p>
            </article>

            <article class="metric-card tone-orange">
                <span class="metric-label">Thiếu email</span>
                <strong class="metric-value">${missingEmailCount}</strong>
                <p>Cần cập nhật để tránh đứt kênh liên hệ</p>
            </article>

            <article class="metric-card tone-violet">
                <span class="metric-label">Đa vai trò</span>
                <strong class="metric-value">${multiRoleCount}</strong>
                <p>Tài khoản đang mang từ 2 quyền truy cập trở lên</p>
            </article>
        </section>

        <section class="dashboard-grid staff-dashboard-grid">
            <div class="dashboard-column">
                <section class="dashboard-card">
                    <div class="dashboard-card-head">
                        <div>
                            <span class="card-kicker">Danh sách nhân sự</span>
                            <h3>Tra cứu và cập nhật tài khoản</h3>
                        </div>
                        <span class="text-subtle">
                            <c:choose>
                                <c:when test="${searchActive}">Hiển thị ${totalItems} / ${allStaffCount} tài khoản</c:when>
                                <c:otherwise>Tổng bản ghi: ${allStaffCount}</c:otherwise>
                            </c:choose>
                        </span>
                    </div>

                    <form method="get" action="${ctx}/admin/staffs" class="staff-search-form">
                        <input type="hidden" name="action" value="list">
                        <input type="text" name="search" value="${searchKeyword}" placeholder="Tìm theo mã, tên, username, email hoặc vai trò...">
                        <button class="btn-apply" type="submit">Tìm kiếm</button>
                        <c:if test="${searchActive}">
                            <a class="btn btn-secondary" href="${ctx}/admin/staffs?action=list">Đặt lại</a>
                        </c:if>
                    </form>

                    <div class="staff-table-wrap">
                        <div class="table-scroll">
                            <table class="staff-table">
                                <thead>
                                    <tr>
                                        <th>STT</th>
                                        <th>Tài khoản</th>
                                        <th>Liên hệ</th>
                                        <th>Vai trò</th>
                                        <th>Trạng thái</th>
                                        <th>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="row" items="${staffRows}" varStatus="loop">
                                        <tr>
                                            <td>${pageStartIndex + loop.index + 1}</td>
                                            <td>
                                                <div class="staff-name-cell">
                                                    <span class="staff-avatar-badge"><c:out value="${row.displayInitial}" /></span>
                                                    <div class="staff-name-copy">
                                                        <strong><c:out value="${row.displayName}" /></strong>
                                                        <span>@<c:out value="${row.usernameDisplay}" /></span>
                                                        <small>ID #${row.staff.staffID}</small>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="staff-contact-cell">
                                                    <c:choose>
                                                        <c:when test="${row.missingEmail}">
                                                            <span class="staff-contact-empty">Chưa cập nhật email</span>
                                                            <span class="text-subtle">Nên bổ sung để hỗ trợ khôi phục tài khoản</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a class="staff-email-link" href="mailto:${row.emailDisplay}">
                                                                <c:out value="${row.emailDisplay}" />
                                                            </a>
                                                            <span class="text-subtle">Kênh nhận thông báo và đặt lại mật khẩu</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="staff-role-list">
                                                    <c:forEach var="roleLabel" items="${row.roleLabels}">
                                                        <span class="staff-role-pill"><c:out value="${roleLabel}" /></span>
                                                    </c:forEach>
                                                    <c:if test="${empty row.roleLabels}">
                                                        <span class="staff-role-pill">Chưa gán vai trò</span>
                                                    </c:if>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="staff-status-list">
                                                    <c:choose>
                                                        <c:when test="${row.missingEmail}">
                                                            <span class="staff-status-pill is-warning">Thiếu email</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="staff-status-pill is-success">Đã có email</span>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <c:choose>
                                                        <c:when test="${row.multiRole}">
                                                            <span class="staff-status-pill is-info">${row.roleCount} vai trò</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="staff-status-pill is-neutral">${row.roleCount} vai trò</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                            <td class="actions">
                                                <a class="btn btn-warning" href="${ctx}/admin/staffs?action=edit&id=${row.staff.staffID}">✏️ Sửa</a>
                                                <a class="btn btn-danger" href="${ctx}/admin/staffs?action=delete&id=${row.staff.staffID}" onclick="return confirm('Xóa tài khoản này?')">🗑️ Xóa</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty staffRows}">
                                        <tr>
                                            <td colspan="6">
                                                <div class="staff-empty-state">
                                                    <div class="staff-empty-icon">👥</div>
                                                    <p>Không tìm thấy tài khoản nào phù hợp.</p>
                                                    <span>Thử thay đổi từ khóa hoặc xóa bộ lọc để xem toàn bộ danh sách.</span>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <c:if test="${totalPages > 1}">
                        <div class="pagination">
                            <c:if test="${currentPage > 1}">
                                <c:url var="prevUrl" value="/admin/staffs">
                                    <c:param name="action" value="list" />
                                    <c:param name="page" value="${currentPage - 1}" />
                                    <c:if test="${searchActive}">
                                        <c:param name="search" value="${searchKeyword}" />
                                    </c:if>
                                </c:url>
                                <a class="page-link" href="${prevUrl}">Trang trước</a>
                            </c:if>
                            <c:forEach begin="1" end="${totalPages}" var="p">
                                <c:url var="pageUrl" value="/admin/staffs">
                                    <c:param name="action" value="list" />
                                    <c:param name="page" value="${p}" />
                                    <c:if test="${searchActive}">
                                        <c:param name="search" value="${searchKeyword}" />
                                    </c:if>
                                </c:url>
                                <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                            </c:forEach>
                            <c:if test="${currentPage < totalPages}">
                                <c:url var="nextUrl" value="/admin/staffs">
                                    <c:param name="action" value="list" />
                                    <c:param name="page" value="${currentPage + 1}" />
                                    <c:if test="${searchActive}">
                                        <c:param name="search" value="${searchKeyword}" />
                                    </c:if>
                                </c:url>
                                <a class="page-link" href="${nextUrl}">Trang sau</a>
                            </c:if>
                        </div>
                    </c:if>
                </section>
            </div>

            <div class="dashboard-column">
                <section class="dashboard-card">
                    <div class="dashboard-card-head">
                        <div>
                            <span class="card-kicker">Chất lượng dữ liệu</span>
                            <h3>Điểm cần lưu ý</h3>
                        </div>
                    </div>

                    <div class="notice-grid">
                        <c:choose>
                            <c:when test="${missingEmailCount == 0}">
                                <article class="notice-card is-good">
                                    <strong>Tất cả tài khoản đã có email liên hệ</strong>
                                    <span>Nhóm vận hành hiện đã sẵn sàng cho thông báo và khôi phục mật khẩu.</span>
                                </article>
                            </c:when>
                            <c:otherwise>
                                <article class="notice-card is-warning">
                                    <strong>${missingEmailCount} tài khoản còn thiếu email</strong>
                                    <span>Nên bổ sung để giảm rủi ro mất khả năng đăng nhập hoặc hỗ trợ chậm.</span>
                                </article>
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                            <c:when test="${multiRoleCount == 0}">
                                <article class="notice-card is-neutral">
                                    <strong>Không có tài khoản đa vai trò nổi bật</strong>
                                    <span>Phân quyền đang giữ trạng thái gọn và tương đối dễ kiểm soát.</span>
                                </article>
                            </c:when>
                            <c:otherwise>
                                <article class="notice-card is-neutral">
                                    <strong>${multiRoleCount} tài khoản đang mang nhiều vai trò</strong>
                                    <span>Kiểm tra lại để tránh chồng chéo quyền không cần thiết.</span>
                                </article>
                            </c:otherwise>
                        </c:choose>

                        <article class="notice-card is-neutral">
                            <strong>${adminCount} tài khoản có quyền quản trị</strong>
                            <span>Theo dõi nhóm này thường xuyên vì ảnh hưởng trực tiếp tới quyền truy cập hệ thống.</span>
                        </article>
                    </div>
                </section>

                <section class="dashboard-card">
                    <div class="dashboard-card-head">
                        <div>
                            <span class="card-kicker">Liên kết nhanh</span>
                            <h3>Đi tới phân hệ khác</h3>
                        </div>
                    </div>

                    <div class="shortcut-grid">
                        <a class="shortcut-card" href="${ctx}/admin/dashboard">
                            <strong>Dashboard</strong>
                            <span>Xem tổng quan KPI và trạng thái vận hành thư viện.</span>
                        </a>
                        <a class="shortcut-card" href="${ctx}/admin/orders?action=list">
                            <strong>Đơn hàng</strong>
                            <span>Xử lý các đơn đang chờ duyệt hoặc chờ giao.</span>
                        </a>
                        <a class="shortcut-card" href="${ctx}/admin/students?action=list">
                            <strong>Sinh viên</strong>
                            <span>Đối chiếu tài khoản staff với nhóm người dùng thư viện.</span>
                        </a>
                        <a class="shortcut-card" href="${ctx}/admin/staffs?action=create">
                            <strong>Thêm nhân viên</strong>
                            <span>Tạo mới tài khoản và gán quyền truy cập ngay lập tức.</span>
                        </a>
                    </div>
                </section>
            </div>
        </section>
    </div>
<%@ include file="../layout/_admin_footer.jsp" %>
