<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>${dashboardTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-dashboard.css">
</head>
<body>
    <c:set var="activeTab" value="dashboard" />
    <c:set var="ctx" value="${pageContext.request.contextPath}" />
    <%@ include file="_header.jsp" %>

    <div class="container dashboard-page">
        <c:if test="${not empty dashboardLoadError}">
            <div class="error"><c:out value="${dashboardLoadError}" /></div>
        </c:if>

        <section class="dashboard-hero">
            <div class="dashboard-hero-main">
                <span class="dashboard-eyebrow"><c:out value="${dashboardLabel}" /></span>
                <h2 class="dashboard-title"><c:out value="${dashboardTitle}" /></h2>
                <p class="dashboard-intro"><c:out value="${dashboardIntro}" /></p>

                <div class="dashboard-hero-strip">
                    <div class="hero-chip">
                        <span>Hôm nay</span>
                        <strong><c:out value="${todayLabel}" /></strong>
                    </div>
                    <div class="hero-chip">
                        <span>Vai trò</span>
                        <strong><c:out value="${dashboardRoleLabel}" /></strong>
                    </div>
                    <div class="hero-chip">
                        <span>Phân hệ quản lý</span>
                        <strong><c:out value="${managedAreas}" /></strong>
                    </div>
                </div>
            </div>

            <div class="dashboard-hero-side">
                <div class="dashboard-hero-side-head">
                    <span class="dashboard-eyebrow dashboard-eyebrow-dark">Người vận hành</span>
                    <h3>
                        <c:choose>
                            <c:when test="${not empty sessionScope.staff.staffName}">
                                <c:out value="${sessionScope.staff.staffName}" />
                            </c:when>
                            <c:otherwise>Đội ngũ thư viện</c:otherwise>
                        </c:choose>
                    </h3>
                    <p>Bảng điều khiển này gồm KPI chính, cảnh báo vận hành và các lối tắt để đi vào từng phân hệ nhanh hơn.</p>
                </div>

                <div class="hero-side-grid">
                    <div class="hero-mini-card">
                        <span>Cần xử lý ngay</span>
                        <strong><c:out value="${empty priorityCount ? 0 : priorityCount}" /></strong>
                    </div>
                    <div class="hero-mini-card">
                        <span>Giao dịch đã ghi nhận</span>
                        <strong><c:out value="${empty totalTransactions ? 0 : totalTransactions}" /></strong>
                    </div>
                </div>

                <a class="hero-action-link" href="${ctx}/admin/orders?action=list">Mở hàng đợi đơn hàng</a>
            </div>
        </section>

        <section class="dashboard-stats">
            <article class="dashboard-stat-card tone-blue">
                <span class="stat-label">Đầu sách</span>
                <strong class="stat-value"><c:out value="${empty totalBooks ? 0 : totalBooks}" /></strong>
                <span class="stat-note"><c:out value="${empty totalCopies ? 0 : totalCopies}" /> bản sao trong kho</span>
            </article>

            <article class="dashboard-stat-card tone-green">
                <span class="stat-label">Bản sao sẵn sàng</span>
                <strong class="stat-value"><c:out value="${empty availableCopies ? 0 : availableCopies}" /></strong>
                <span class="stat-note"><c:out value="${empty borrowedCopies ? 0 : borrowedCopies}" /> đang được mượn hoặc giữ chỗ</span>
            </article>

            <article class="dashboard-stat-card tone-orange">
                <span class="stat-label">Lượt mượn đang mở</span>
                <strong class="stat-value"><c:out value="${empty activeBorrows ? 0 : activeBorrows}" /></strong>
                <span class="stat-note"><c:out value="${empty overdueBorrows ? 0 : overdueBorrows}" /> phiếu quá hạn</span>
            </article>

            <article class="dashboard-stat-card tone-red">
                <span class="stat-label">Đơn cần xử lý</span>
                <strong class="stat-value"><c:out value="${empty pendingOrders ? 0 : pendingOrders}" /></strong>
                <span class="stat-note"><c:out value="${empty readyOrders ? 0 : readyOrders}" /> đơn đã sẵn sàng / đã duyệt</span>
            </article>

            <article class="dashboard-stat-card tone-slate">
                <span class="stat-label">Sinh viên</span>
                <strong class="stat-value"><c:out value="${empty totalStudents ? 0 : totalStudents}" /></strong>
                <span class="stat-note">Người dùng đang được phục vụ</span>
            </article>

            <c:choose>
                <c:when test="${isAdminDashboard}">
                    <article class="dashboard-stat-card tone-violet">
                        <span class="stat-label">Nhân viên</span>
                        <strong class="stat-value"><c:out value="${empty totalStaff ? 0 : totalStaff}" /></strong>
                        <span class="stat-note">
                            <c:choose>
                                <c:when test="${not empty studentsPerStaffLabel}">
                                    ${studentsPerStaffLabel} sinh viên / nhân viên
                                </c:when>
                                <c:otherwise>Chưa có tỷ lệ phù hợp</c:otherwise>
                            </c:choose>
                        </span>
                    </article>
                </c:when>
                <c:otherwise>
                    <article class="dashboard-stat-card tone-violet">
                        <span class="stat-label">Sách cần bổ sung</span>
                        <strong class="stat-value"><c:out value="${empty lowStockCount ? 0 : lowStockCount}" /></strong>
                        <span class="stat-note"><c:out value="${empty outOfStockCount ? 0 : outOfStockCount}" /> đầu sách đã hết sẵn sàng</span>
                    </article>
                </c:otherwise>
            </c:choose>

            <article class="dashboard-stat-card tone-amber">
                <span class="stat-label">Đơn hoàn tất</span>
                <strong class="stat-value"><c:out value="${empty completedOrders ? 0 : completedOrders}" /></strong>
                <span class="stat-note"><c:out value="${empty cancelledOrders ? 0 : cancelledOrders}" /> đơn bị hủy / từ chối</span>
            </article>
        </section>

        <div class="dashboard-layout">
            <div class="dashboard-stack">
                <section class="dashboard-panel">
                    <div class="panel-head">
                        <div>
                            <span class="panel-kicker">Nhịp độ hệ thống</span>
                            <h3>Tổng quan vận hành</h3>
                        </div>
                        <a class="panel-link" href="${ctx}/admin/borrows?action=list">Xem mượn trả</a>
                    </div>

                    <div class="meter-grid">
                        <div class="meter-card">
                            <div class="meter-meta">
                                <span>Độ sẵn sàng kho sách</span>
                                <strong><c:out value="${empty stockCoverageLabel ? '0%' : stockCoverageLabel}" /></strong>
                            </div>
                            <div class="meter-bar">
                                <span style="width: ${empty stockCoverageValue ? 0 : stockCoverageValue}%;"></span>
                            </div>
                            <p>Đo lường tỷ lệ bản sao sẵn sàng trên tổng tồn kho hiện tại.</p>
                        </div>

                        <div class="meter-card">
                            <div class="meter-meta">
                                <span>Sức khỏe phiếu mượn</span>
                                <strong><c:out value="${empty borrowHealthLabel ? '0%' : borrowHealthLabel}" /></strong>
                            </div>
                            <div class="meter-bar tone-warning">
                                <span style="width: ${empty borrowHealthValue ? 0 : borrowHealthValue}%;"></span>
                            </div>
                            <p>Phần trăm phiếu mượn đang mở nhưng chưa quá hạn.</p>
                        </div>

                        <div class="meter-card">
                            <div class="meter-meta">
                                <span>Tỷ lệ hoàn tất đơn</span>
                                <strong><c:out value="${empty orderFulfillmentLabel ? '0%' : orderFulfillmentLabel}" /></strong>
                            </div>
                            <div class="meter-bar tone-success">
                                <span style="width: ${empty orderFulfillmentValue ? 0 : orderFulfillmentValue}%;"></span>
                            </div>
                            <p>Đo lường đơn đã giao thành công trên tổng số đơn đã ghi nhận.</p>
                        </div>
                    </div>

                    <div class="alert-list">
                        <c:choose>
                            <c:when test="${empty overdueBorrows or overdueBorrows == 0}">
                                <article class="alert-card alert-good">
                                    <strong>Không có phiếu mượn quá hạn nổi bật</strong>
                                    <span>Hàng đợi mượn trả đang ở trạng thái ổn định.</span>
                                </article>
                            </c:when>
                            <c:otherwise>
                                <article class="alert-card alert-danger">
                                    <strong><c:out value="${overdueBorrows}" /> phiếu quá hạn cần theo dõi</strong>
                                    <span>Ưu tiên liên hệ sinh viên hoặc xử lý trả sách trước.</span>
                                </article>
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                            <c:when test="${empty lowStockCount or lowStockCount == 0}">
                                <article class="alert-card alert-neutral">
                                    <strong>Tồn kho đang cân bằng</strong>
                                    <span>Chưa có đầu sách nào chạm ngưỡng cảnh báo.</span>
                                </article>
                            </c:when>
                            <c:otherwise>
                                <article class="alert-card alert-warning">
                                    <strong><c:out value="${lowStockCount}" /> đầu sách sắp cần bổ sung</strong>
                                    <span>Tập trung vào các sách có số bản sao sẵn sàng thấp.</span>
                                </article>
                            </c:otherwise>
                        </c:choose>

                        <article class="alert-card alert-neutral">
                            <strong><c:out value="${empty pendingOrders ? 0 : pendingOrders}" /> đơn đang chờ xử lý</strong>
                            <span>Chuyển nhanh sang hàng đợi đơn hàng để duyệt, giao hoặc hủy.</span>
                        </article>
                    </div>
                </section>

                <section class="dashboard-panel">
                    <div class="panel-head">
                        <div>
                            <span class="panel-kicker">Thao tác nhanh</span>
                            <h3>Đi vào từng phân hệ</h3>
                        </div>
                    </div>

                    <div class="shortcut-grid">
                        <a class="shortcut-card" href="${ctx}/admin/books?action=list">
                            <strong>Danh mục sách</strong>
                            <span>Xem, thêm và cập nhật thông tin đầu sách.</span>
                        </a>
                        <a class="shortcut-card" href="${ctx}/admin/borrows?action=list">
                            <strong>Mượn trả</strong>
                            <span>Theo dõi giao dịch mượn, trả và quá hạn.</span>
                        </a>
                        <a class="shortcut-card" href="${ctx}/admin/orders?action=list">
                            <strong>Đơn hàng</strong>
                            <span>Duyệt đơn, giao sách và quản lý hàng đợi.</span>
                        </a>
                        <a class="shortcut-card" href="${ctx}/admin/students?action=list">
                            <strong>Sinh viên</strong>
                            <span>Tra cứu danh sách người dùng sử dụng thư viện.</span>
                        </a>
                        <a class="shortcut-card" href="${ctx}/admin/bookfiles?action=list">
                            <strong>Tệp sách</strong>
                            <span>Quản lý tệp đính kèm và tài nguyên liên quan.</span>
                        </a>

                        <c:if test="${isAdminDashboard}">
                            <a class="shortcut-card" href="${ctx}/admin/authors?action=list">
                                <strong>Tác giả</strong>
                                <span>Cập nhật dữ liệu tác giả và thông tin liên kết.</span>
                            </a>
                            <a class="shortcut-card" href="${ctx}/admin/categories?action=list">
                                <strong>Thể loại</strong>
                                <span>Tổ chức danh mục sách theo nhóm nội dung.</span>
                            </a>
                            <a class="shortcut-card" href="${ctx}/admin/publishers?action=list">
                                <strong>Nhà xuất bản</strong>
                                <span>Quản lý siêu dữ liệu xuất bản và đối tác.</span>
                            </a>
                            <a class="shortcut-card" href="${ctx}/admin/staffs?action=list">
                                <strong>Nhân viên</strong>
                                <span>Điều phối tài khoản, quyền và vận hành.</span>
                            </a>
                        </c:if>
                    </div>
                </section>

                <section class="dashboard-panel">
                    <div class="panel-head">
                        <div>
                            <span class="panel-kicker">Hoạt động mượn gần đây</span>
                            <h3>Phiếu mượn gần đây</h3>
                        </div>
                        <a class="panel-link" href="${ctx}/admin/borrows?action=list">Mở danh sách đầy đủ</a>
                    </div>

                    <div class="activity-list">
                        <c:forEach var="row" items="${recentBorrowRows}">
                            <article class="activity-item">
                                <div class="activity-main">
                                    <div class="activity-topline">
                                        <span class="activity-id">Borrow #${row.borrowID}</span>
                                        <strong><c:out value="${row.studentName}" /></strong>
                                    </div>
                                    <p><c:out value="${empty row.items ? 'Không có chi tiết sách.' : row.items}" /></p>
                                </div>

                                <div class="activity-side">
                                    <span class="activity-meta">${row.borrowDate} -> ${row.dueDate}</span>
                                    <c:choose>
                                        <c:when test="${row.status eq 'Borrowing'}">
                                            <span class="status-borrowing">${row.status}</span>
                                        </c:when>
                                        <c:when test="${row.status eq 'Overdue'}">
                                            <span class="status-overdue">${row.status}</span>
                                        </c:when>
                                        <c:when test="${row.status eq 'Returned'}">
                                            <span class="status-returned">${row.status}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-pill ok">${row.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </article>
                        </c:forEach>

                        <c:if test="${empty recentBorrowRows}">
                            <div class="empty-box">Chưa có phiếu mượn nào để hiển thị.</div>
                        </c:if>
                    </div>
                </section>
            </div>

            <div class="dashboard-stack">
                <section class="dashboard-panel">
                    <div class="panel-head">
                        <div>
                            <span class="panel-kicker">Trọng tâm tồn kho</span>
                            <h3>Cảnh báo tồn kho</h3>
                        </div>
                        <a class="panel-link" href="${ctx}/admin/books?action=list">Mở kho sách</a>
                    </div>

                    <div class="inventory-list">
                        <c:forEach var="book" items="${lowStockBooks}">
                            <article class="inventory-item">
                                <div>
                                    <strong><c:out value="${book.bookName}" /></strong>
                                    <p>Con ${book.available}/${book.quantity} bản sao sẵn sàng</p>
                                </div>

                                <c:choose>
                                    <c:when test="${book.available == 0}">
                                        <span class="inventory-pill danger">Hết sẵn sàng</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="inventory-pill warning">Cần bổ sung</span>
                                    </c:otherwise>
                                </c:choose>
                            </article>
                        </c:forEach>

                        <c:if test="${empty lowStockBooks}">
                            <div class="empty-box">Tất cả đầu sách đang ở trên mức cảnh báo.</div>
                        </c:if>
                    </div>
                </section>

                <section class="dashboard-panel">
                    <div class="panel-head">
                        <div>
                            <span class="panel-kicker">Hàng đợi đơn hàng gần đây</span>
                            <h3>Đơn hàng gần đây</h3>
                        </div>
                        <a class="panel-link" href="${ctx}/admin/orders?action=list">Mở hàng đợi đơn</a>
                    </div>

                    <div class="activity-list">
                        <c:forEach var="row" items="${recentOrderRows}">
                            <article class="activity-item compact">
                                <div class="activity-main">
                                    <div class="activity-topline">
                                        <span class="activity-id">Order #${row.orderID}</span>
                                        <strong><c:out value="${row.studentName}" /></strong>
                                    </div>
                                    <p><c:out value="${empty row.items ? 'Không có chi tiết sách.' : row.items}" /></p>
                                </div>

                                <div class="activity-side">
                                    <span class="activity-meta">${row.orderDate}</span>
                                    <span class="activity-amount">${row.totalAmount} VND</span>
                                    <c:choose>
                                        <c:when test="${row.status eq 'Pending' or row.status eq 'Hàng chờ'}">
                                            <span class="status status-waiting">${row.status}</span>
                                        </c:when>
                                        <c:when test="${row.status eq 'Sẵn sàng'}">
                                            <span class="status status-ready">${row.status}</span>
                                        </c:when>
                                        <c:when test="${row.status eq 'Approved' or row.status eq 'Đã giao'}">
                                            <span class="status status-delivered">${row.status}</span>
                                        </c:when>
                                        <c:when test="${row.status eq 'Rejected' or row.status eq 'Đã hủy'}">
                                            <span class="status status-canceled">${row.status}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-pill ok">${row.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </article>
                        </c:forEach>

                        <c:if test="${empty recentOrderRows}">
                            <div class="empty-box">Chua co don hang nao de hien thi.</div>
                        </c:if>
                    </div>
                </section>
            </div>
        </div>
    </div>
</body>
</html>
