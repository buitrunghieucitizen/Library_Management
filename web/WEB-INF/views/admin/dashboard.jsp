<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="activeTab" value="dashboard" />
<c:set var="pageSubtitle" value="${dashboardLabel}" />
<c:set var="enableCharts" value="true" />
<%@ include file="layout/_admin_header.jsp" %>
                <c:if test="${not empty dashboardLoadError}">
                    <div class="error dashboard-error"><c:out value="${dashboardLoadError}" /></div>
                </c:if>

                <section class="hero-grid">
                    <article class="hero-card hero-card-main">
                        <span class="hero-kicker">Điểm điều phối trung tâm</span>
                        <h2><c:out value="${dashboardTitle}" /></h2>
                        <p><c:out value="${dashboardIntro}" /></p>

                        <div class="hero-stat-strip">
                            <div class="hero-stat">
                                <span>Hôm nay</span>
                                <strong><c:out value="${todayLabel}" /></strong>
                            </div>
                            <div class="hero-stat">
                                <span>Vai trò</span>
                                <strong><c:out value="${dashboardRoleLabel}" /></strong>
                            </div>
                            <div class="hero-stat">
                                <span>Phân hệ quản lý</span>
                                <strong><c:out value="${managedAreas}" /></strong>
                            </div>
                        </div>

                        <div class="hero-cta-row">
                            <a class="hero-cta primary" href="${ctx}/admin/orders?action=list">Mở hàng đợi đơn</a>
                            <a class="hero-cta secondary" href="${ctx}/admin/books?action=list">Vào kho sách</a>
                        </div>
                    </article>

                    <article class="hero-card hero-card-side">
                        <div class="hero-side-head">
                            <span class="hero-kicker hero-kicker-soft">Người vận hành</span>
                            <h3><c:out value="${operatorName}" /></h3>
                            <p>Giao diện được dựng theo style `inapp`, nhưng toàn bộ KPI, cảnh báo và danh sách vẫn lấy trực tiếp từ dữ liệu của LibraryManager.</p>
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

                        <div class="hero-summary-list">
                            <div class="hero-summary-row">
                                <span>Phiếu quá hạn</span>
                                <strong><c:out value="${empty overdueBorrows ? 0 : overdueBorrows}" /></strong>
                            </div>
                            <div class="hero-summary-row">
                                <span>Đơn chờ xử lý</span>
                                <strong><c:out value="${empty pendingOrders ? 0 : pendingOrders}" /></strong>
                            </div>
                            <div class="hero-summary-row">
                                <span>Sách sắp thiếu</span>
                                <strong><c:out value="${empty lowStockCount ? 0 : lowStockCount}" /></strong>
                            </div>
                        </div>
                    </article>
                </section>

                <section class="metric-grid">
                    <article class="metric-card tone-blue">
                        <span class="metric-label">Đầu sách</span>
                        <strong class="metric-value"><c:out value="${empty totalBooks ? 0 : totalBooks}" /></strong>
                        <p><c:out value="${empty totalCopies ? 0 : totalCopies}" /> bản sao trong kho</p>
                    </article>

                    <article class="metric-card tone-green">
                        <span class="metric-label">Bản sao sẵn sàng</span>
                        <strong class="metric-value"><c:out value="${empty availableCopies ? 0 : availableCopies}" /></strong>
                        <p><c:out value="${empty borrowedCopies ? 0 : borrowedCopies}" /> đang mượn hoặc giữ chỗ</p>
                    </article>

                    <article class="metric-card tone-orange">
                        <span class="metric-label">Lượt mượn đang mở</span>
                        <strong class="metric-value"><c:out value="${empty activeBorrows ? 0 : activeBorrows}" /></strong>
                        <p><c:out value="${empty overdueBorrows ? 0 : overdueBorrows}" /> phiếu quá hạn</p>
                    </article>

                    <article class="metric-card tone-red">
                        <span class="metric-label">Đơn cần xử lý</span>
                        <strong class="metric-value"><c:out value="${empty pendingOrders ? 0 : pendingOrders}" /></strong>
                        <p><c:out value="${empty readyOrders ? 0 : readyOrders}" /> đơn đã duyệt hoặc sẵn sàng</p>
                    </article>

                    <article class="metric-card tone-slate">
                        <span class="metric-label">Sinh viên</span>
                        <strong class="metric-value"><c:out value="${empty totalStudents ? 0 : totalStudents}" /></strong>
                        <p>Người dùng đang được phục vụ</p>
                    </article>

                    <c:choose>
                        <c:when test="${isAdminDashboard}">
                            <article class="metric-card tone-violet">
                                <span class="metric-label">Nhân viên</span>
                                <strong class="metric-value"><c:out value="${empty totalStaff ? 0 : totalStaff}" /></strong>
                                <p>
                                    <c:choose>
                                        <c:when test="${not empty studentsPerStaffLabel}">
                                            ${studentsPerStaffLabel} sinh viên / nhân viên
                                        </c:when>
                                        <c:otherwise>Chưa có tỷ lệ phù hợp</c:otherwise>
                                    </c:choose>
                                </p>
                            </article>
                        </c:when>
                        <c:otherwise>
                            <article class="metric-card tone-violet">
                                <span class="metric-label">Sách cần bổ sung</span>
                                <strong class="metric-value"><c:out value="${empty lowStockCount ? 0 : lowStockCount}" /></strong>
                                <p><c:out value="${empty outOfStockCount ? 0 : outOfStockCount}" /> đầu sách đã hết sẵn sàng</p>
                            </article>
                        </c:otherwise>
                    </c:choose>

                    <article class="metric-card tone-amber">
                        <span class="metric-label">Đơn hoàn tất</span>
                        <strong class="metric-value"><c:out value="${empty completedOrders ? 0 : completedOrders}" /></strong>
                        <p><c:out value="${empty cancelledOrders ? 0 : cancelledOrders}" /> đơn bị hủy hoặc từ chối</p>
                    </article>
                </section>

                <section class="dashboard-chart-row">
                    <section class="dashboard-card dashboard-card-chart">
                        <div class="dashboard-card-head dashboard-card-head-spread">
                            <div>
                                <span class="card-kicker">Thống kê Giao dịch</span>
                                <h3>Mượn và Mua</h3>
                            </div>
                            <select class="chart-filter" id="borrowBuyChartFilter" aria-label="Phạm vi thời gian biểu đồ">
                                <option value="year" selected>Năm nay</option>
                                <option value="month">Tháng này</option>
                                <option value="week">Tuần này</option>
                            </select>
                        </div>
                        <div class="chart-panel-body">
                            <div id="borrowBuyChart"></div>
                            <script id="borrowBuyChartData" type="application/json"><c:out value="${borrowBuyChartJson}" escapeXml="false" /></script>
                        </div>
                    </section>

                    <section class="dashboard-card dashboard-card-chart">
                        <div class="dashboard-card-head dashboard-card-head-spread">
                            <div>
                                <span class="card-kicker">Doanh Thu</span>
                                <h3>Biến động doanh thu</h3>
                            </div>
                            <select class="chart-filter" id="revenueChartFilter" aria-label="Phạm vi thời gian biểu đồ doanh thu">
                                <option value="year" selected>Năm nay</option>
                                <option value="month">Tháng này</option>
                                <option value="week">Tuần này</option>
                            </select>
                        </div>
                        <div class="revenue-summary-strip">
                            <article class="revenue-summary-card">
                                <span>Tổng thu</span>
                                <strong id="revenueSummaryTotal">0 ₫</strong>
                                <p>Tiền đã ghi nhận theo phạm vi đang chọn</p>
                            </article>
                            <article class="revenue-summary-card">
                                <span>Từ mua sách</span>
                                <strong id="revenueSummaryOrder">0 ₫</strong>
                                <p>Đơn mua đã hoàn tất hoặc đã giao</p>
                            </article>
                            <article class="revenue-summary-card">
                                <span>Từ phí/phạt mượn</span>
                                <strong id="revenueSummaryBorrow">0 ₫</strong>
                                <p>Khoản thu phát sinh từ luồng mượn trả</p>
                            </article>
                            <article class="revenue-summary-card">
                                <span>Đơn / phiếu đã thu</span>
                                <strong id="revenueSummaryCollected">0 đơn / 0 phí</strong>
                                <p>Số bản ghi góp vào tổng doanh thu</p>
                            </article>
                        </div>
                        <div class="chart-panel-body">
                            <div id="revenueChart"></div>
                            <script id="revenueChartData" type="application/json"><c:out value="${revenueChartJson}" escapeXml="false" /></script>
                        </div>
                    </section>
                </section>

                <section class="dashboard-grid">
                    <div class="dashboard-column">
                        <%-- placeholder to keep column structure, first real card below --%>

                        <section class="dashboard-card">
                            <div class="dashboard-card-head">
                                <div>
                                    <span class="card-kicker">Báo cáo vận hành</span>
                                    <h3>Operational pulse</h3>
                                </div>
                                <a class="card-link" href="${ctx}/admin/borrows?action=list">Xem mượn trả</a>
                            </div>

                            <div class="signal-grid">
                                <article class="signal-card">
                                    <div class="signal-head">
                                        <span>Độ sẵn sàng kho sách</span>
                                        <strong><c:out value="${empty stockCoverageLabel ? '0%' : stockCoverageLabel}" /></strong>
                                    </div>
                                    <div class="signal-bar">
                                        <span style="width: ${empty stockCoverageValue ? 0 : stockCoverageValue}%;"></span>
                                    </div>
                                    <p>Tỷ lệ bản sao có thể phục vụ ngay trên tổng tồn kho hiện tại.</p>
                                </article>

                                <article class="signal-card">
                                    <div class="signal-head">
                                        <span>Sức khỏe phiếu mượn</span>
                                        <strong><c:out value="${empty borrowHealthLabel ? '0%' : borrowHealthLabel}" /></strong>
                                    </div>
                                    <div class="signal-bar tone-warning">
                                        <span style="width: ${empty borrowHealthValue ? 0 : borrowHealthValue}%;"></span>
                                    </div>
                                    <p>Phần trăm phiếu mượn đang mở nhưng chưa rơi vào quá hạn.</p>
                                </article>

                                <article class="signal-card">
                                    <div class="signal-head">
                                        <span>Tỷ lệ hoàn tất đơn</span>
                                        <strong><c:out value="${empty orderFulfillmentLabel ? '0%' : orderFulfillmentLabel}" /></strong>
                                    </div>
                                    <div class="signal-bar tone-success">
                                        <span style="width: ${empty orderFulfillmentValue ? 0 : orderFulfillmentValue}%;"></span>
                                    </div>
                                    <p>Đo lường số đơn đã giao thành công trên tổng đơn đã ghi nhận.</p>
                                </article>
                            </div>

                            <div class="notice-grid">
                                <c:choose>
                                    <c:when test="${empty overdueBorrows or overdueBorrows == 0}">
                                        <article class="notice-card is-good">
                                            <strong>Không có phiếu mượn quá hạn nổi bật</strong>
                                            <span>Hàng đợi mượn trả đang giữ trạng thái ổn định.</span>
                                        </article>
                                    </c:when>
                                    <c:otherwise>
                                        <article class="notice-card is-danger">
                                            <strong><c:out value="${overdueBorrows}" /> phiếu quá hạn cần theo dõi</strong>
                                            <span>Ưu tiên liên hệ sinh viên hoặc xử lý trả sách trước.</span>
                                        </article>
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <c:when test="${empty lowStockCount or lowStockCount == 0}">
                                        <article class="notice-card is-neutral">
                                            <strong>Tồn kho đang cân bằng</strong>
                                            <span>Chưa có đầu sách nào chạm ngưỡng cảnh báo thấp.</span>
                                        </article>
                                    </c:when>
                                    <c:otherwise>
                                        <article class="notice-card is-warning">
                                            <strong><c:out value="${lowStockCount}" /> đầu sách sắp cần bổ sung</strong>
                                            <span>Tập trung vào nhóm sách có số bản sao sẵn sàng thấp nhất.</span>
                                        </article>
                                    </c:otherwise>
                                </c:choose>

                                <article class="notice-card is-neutral">
                                    <strong><c:out value="${empty pendingOrders ? 0 : pendingOrders}" /> đơn đang chờ xử lý</strong>
                                    <span>Chuyển nhanh sang phân hệ đơn hàng để duyệt, giao hoặc hủy.</span>
                                </article>
                            </div>
                        </section>

                        <section class="dashboard-card">
                            <div class="dashboard-card-head">
                                <div>
                                    <span class="card-kicker">Hoạt động mượn gần đây</span>
                                    <h3>Phiếu mượn gần đây</h3>
                                </div>
                                <a class="card-link" href="${ctx}/admin/borrows?action=list">Mở danh sách đầy đủ</a>
                            </div>

                            <div class="activity-list">
                                <c:forEach var="row" items="${recentBorrowRows}">
                                    <article class="activity-card">
                                        <div class="activity-main">
                                            <div class="activity-headline">
                                                <span class="activity-id">Borrow #${row.borrowID}</span>
                                                <strong><c:out value="${row.studentName}" /></strong>
                                            </div>
                                            <p><c:out value="${empty row.items ? 'Không có chi tiết sách.' : row.items}" /></p>
                                        </div>

                                        <div class="activity-side">
                                            <span class="activity-meta">${row.borrowDate} -> ${row.dueDate}</span>
                                            <c:choose>
                                                <c:when test="${row.status eq 'Borrowing'}">
                                                    <span class="activity-status is-info">${row.status}</span>
                                                </c:when>
                                                <c:when test="${row.status eq 'Overdue'}">
                                                    <span class="activity-status is-danger">${row.status}</span>
                                                </c:when>
                                                <c:when test="${row.status eq 'Returned'}">
                                                    <span class="activity-status is-success">${row.status}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="activity-status is-neutral">${row.status}</span>
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

                    <div class="dashboard-column">
                        <section class="dashboard-card">
                            <div class="dashboard-card-head">
                                <div>
                                    <span class="card-kicker">Tình hình đơn hàng</span>
                                    <h3>Queue snapshot</h3>
                                </div>
                                <a class="card-link" href="${ctx}/admin/orders?action=list">Mở hàng đợi đơn</a>
                            </div>

                            <div class="queue-grid">
                                <article class="queue-card">
                                    <span>Đang chờ</span>
                                    <strong><c:out value="${empty pendingOrders ? 0 : pendingOrders}" /></strong>
                                </article>
                                <article class="queue-card">
                                    <span>Sẵn sàng</span>
                                    <strong><c:out value="${empty readyOrders ? 0 : readyOrders}" /></strong>
                                </article>
                                <article class="queue-card">
                                    <span>Hoàn tất</span>
                                    <strong><c:out value="${empty completedOrders ? 0 : completedOrders}" /></strong>
                                </article>
                                <article class="queue-card">
                                    <span>Đã hủy</span>
                                    <strong><c:out value="${empty cancelledOrders ? 0 : cancelledOrders}" /></strong>
                                </article>
                            </div>
                        </section>

                        <section class="dashboard-card">
                            <div class="dashboard-card-head">
                                <div>
                                    <span class="card-kicker">Trọng tâm tồn kho</span>
                                    <h3>Cảnh báo tồn kho</h3>
                                </div>
                                <a class="card-link" href="${ctx}/admin/books?action=list">Mở kho sách</a>
                            </div>

                            <div class="stock-list">
                                <c:forEach var="book" items="${lowStockBooks}">
                                    <article class="stock-item">
                                        <span class="stock-mark">BK</span>
                                        <div class="stock-copy">
                                            <strong><c:out value="${book.bookName}" /></strong>
                                            <p>Còn ${book.available}/${book.quantity} bản sao sẵn sàng</p>
                                        </div>

                                        <c:choose>
                                            <c:when test="${book.available == 0}">
                                                <span class="stock-pill danger">Hết sẵn sàng</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="stock-pill warning">Cần bổ sung</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </article>
                                </c:forEach>

                                <c:if test="${empty lowStockBooks}">
                                    <div class="empty-box">Tất cả đầu sách đang ở trên mức cảnh báo.</div>
                                </c:if>
                            </div>
                        </section>

                        <section class="dashboard-card">
                            <div class="dashboard-card-head">
                                <div>
                                    <span class="card-kicker">Đơn hàng gần đây</span>
                                    <h3>Recent orders</h3>
                                </div>
                                <a class="card-link" href="${ctx}/admin/orders?action=list">Mở danh sách đơn</a>
                            </div>

                            <div class="activity-list">
                                <c:forEach var="row" items="${recentOrderRows}">
                                    <article class="activity-card">
                                        <div class="activity-main">
                                            <div class="activity-headline">
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
                                                    <span class="activity-status is-warning">${row.status}</span>
                                                </c:when>
                                                <c:when test="${row.status eq 'Sẵn sàng'}">
                                                    <span class="activity-status is-info">${row.status}</span>
                                                </c:when>
                                                <c:when test="${row.status eq 'Approved' or row.status eq 'Đã giao'}">
                                                    <span class="activity-status is-success">${row.status}</span>
                                                </c:when>
                                                <c:when test="${row.status eq 'Rejected' or row.status eq 'Đã hủy'}">
                                                    <span class="activity-status is-danger">${row.status}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="activity-status is-neutral">${row.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </article>
                                </c:forEach>

                                <c:if test="${empty recentOrderRows}">
                                    <div class="empty-box">Chưa có đơn hàng nào để hiển thị.</div>
                                </c:if>
                            </div>
                        </section>

                        <section class="dashboard-card">
                            <div class="dashboard-card-head">
                                <div>
                                    <span class="card-kicker">Lối tắt</span>
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
                                    <span>Tra cứu và quản lý người dùng thư viện.</span>
                                </a>
                                <a class="shortcut-card" href="${ctx}/admin/bookfiles?action=list">
                                    <strong>Tệp sách</strong>
                                    <span>Quản lý tài nguyên số đính kèm theo đầu sách.</span>
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
                    </div>
                </section>
<script>
(function () {
    function safeJson(id) {
        try {
            var el = document.getElementById(id);
            return el ? JSON.parse(el.textContent.trim() || "{}") : {};
        } catch (e) {
            return {};
        }
    }

    var revenueRawData = safeJson("revenueChartData");

    if (!revenueRawData || (!revenueRawData.ranges && !revenueRawData.year)) {
        revenueRawData = {
            defaultRange: "year",
            ranges: {
                year: {
                    categories: ["Th.1", "Th.2", "Th.3", "Th.4", "Th.5", "Th.6", "Th.7", "Th.8", "Th.9", "Th.10", "Th.11", "Th.12"],
                    totalData: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                    orderData: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                    borrowData: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                    orderCountData: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                    borrowCountData: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                    summary: { totalRevenue: 0, orderRevenue: 0, borrowRevenue: 0, collectedOrders: 0, paidFines: 0 }
                },
                month: {
                    categories: ["1", "2", "3", "4", "5", "6", "7"],
                    totalData: [0, 0, 0, 0, 0, 0, 0],
                    orderData: [0, 0, 0, 0, 0, 0, 0],
                    borrowData: [0, 0, 0, 0, 0, 0, 0],
                    orderCountData: [0, 0, 0, 0, 0, 0, 0],
                    borrowCountData: [0, 0, 0, 0, 0, 0, 0],
                    summary: { totalRevenue: 0, orderRevenue: 0, borrowRevenue: 0, collectedOrders: 0, paidFines: 0 }
                },
                week: {
                    categories: ["T2", "T3", "T4", "T5", "T6", "T7", "CN"],
                    totalData: [0, 0, 0, 0, 0, 0, 0],
                    orderData: [0, 0, 0, 0, 0, 0, 0],
                    borrowData: [0, 0, 0, 0, 0, 0, 0],
                    orderCountData: [0, 0, 0, 0, 0, 0, 0],
                    borrowCountData: [0, 0, 0, 0, 0, 0, 0],
                    summary: { totalRevenue: 0, orderRevenue: 0, borrowRevenue: 0, collectedOrders: 0, paidFines: 0 }
                }
            }
        };
    }

    function formatCompactVnd(val) {
        var numeric = Number(val) || 0;
        var absolute = Math.abs(numeric);

        if (absolute >= 1000000000) {
            return (numeric / 1000000000).toFixed(1).replace(/\.0$/, "") + "B";
        }
        if (absolute >= 1000000) {
            return (numeric / 1000000).toFixed(1).replace(/\.0$/, "") + "M";
        }
        if (absolute >= 1000) {
            return (numeric / 1000).toFixed(0) + "K";
        }
        return numeric.toLocaleString("vi-VN");
    }

    function formatVnd(val) {
        return (Number(val) || 0).toLocaleString("vi-VN") + " ₫";
    }

    function formatCount(val) {
        return (Number(val) || 0).toLocaleString("vi-VN");
    }

    function setText(id, value) {
        var el = document.getElementById(id);
        if (el) {
            el.textContent = value;
        }
    }

    function normalizeRange(rangeKey) {
        var range = revenueRawData.ranges ? revenueRawData.ranges[rangeKey] : revenueRawData[rangeKey];
        if (!range) {
            return null;
        }

        if (range.series) {
            return range;
        }

        return {
            categories: range.categories || [],
            series: [
                { name: "Tổng doanh thu", data: range.totalData || [] },
                { name: "Mua sách", data: range.orderData || [] },
                { name: "Phí/phạt mượn", data: range.borrowData || [] }
            ],
            orderCountData: range.orderCountData || [],
            borrowCountData: range.borrowCountData || [],
            summary: range.summary || {}
        };
    }

    function updateSummary(range) {
        var summary = range && range.summary ? range.summary : {};
        setText("revenueSummaryTotal", formatVnd(summary.totalRevenue));
        setText("revenueSummaryOrder", formatVnd(summary.orderRevenue));
        setText("revenueSummaryBorrow", formatVnd(summary.borrowRevenue));
        setText(
            "revenueSummaryCollected",
            formatCount(summary.collectedOrders) + " đơn / " + formatCount(summary.paidFines) + " phí"
        );
    }

    function buildTooltip(range, pointIndex) {
        var orderCount = range.orderCountData && range.orderCountData[pointIndex] ? range.orderCountData[pointIndex] : 0;
        var borrowCount = range.borrowCountData && range.borrowCountData[pointIndex] ? range.borrowCountData[pointIndex] : 0;
        var category = range.categories && range.categories[pointIndex] ? range.categories[pointIndex] : "";
        var meta = formatCount(orderCount) + " đơn đã thu";

        if (borrowCount > 0) {
            meta += " • " + formatCount(borrowCount) + " phí/phạt đã thu";
        }

        return [
            '<div class="revenue-tooltip">',
            '<div class="revenue-tooltip-title">' + category + '</div>',
            '<div class="revenue-tooltip-row"><span>Tổng thu</span><strong>' + formatVnd(range.series[0].data[pointIndex]) + '</strong></div>',
            '<div class="revenue-tooltip-row"><span>Mua sách</span><strong>' + formatVnd(range.series[1].data[pointIndex]) + '</strong></div>',
            '<div class="revenue-tooltip-row"><span>Phí/phạt mượn</span><strong>' + formatVnd(range.series[2].data[pointIndex]) + '</strong></div>',
            '<div class="revenue-tooltip-meta">' + meta + '</div>',
            '</div>'
        ].join('');
    }

    function buildRevenueChart(rangeKey) {
        var range = normalizeRange(rangeKey) || normalizeRange("year");
        if (!range) {
            return;
        }

        if (window._revenueChartInst) {
            window._revenueChartInst.destroy();
        }

        updateSummary(range);

        var opts = {
            chart: {
                type: "line",
                height: 350,
                toolbar: { show: false },
                zoom: { enabled: false },
                fontFamily: "inherit"
            },
            series: range.series,
            xaxis: {
                categories: range.categories,
                labels: {
                    style: { fontSize: "11px" },
                    rotate: range.categories.length > 12 ? -45 : 0,
                    hideOverlappingLabels: true,
                    trim: false
                },
                axisBorder: { show: false },
                axisTicks: { show: false }
            },
            yaxis: {
                labels: {
                    formatter: formatCompactVnd,
                    style: { fontSize: "11px" }
                },
                title: {
                    text: "Doanh thu (VNĐ)",
                    style: { fontWeight: 500 }
                }
            },
            stroke: { curve: "smooth", width: [3, 3, 2] },
            markers: { size: [4, 4, 3], hover: { size: 6 } },
            colors: ["#0f62fe", "#16a34a", "#f59e0b"],
            legend: {
                position: "top",
                horizontalAlign: "right",
                fontSize: "12px",
                markers: { width: 10, height: 10, radius: 3 }
            },
            tooltip: {
                shared: true,
                intersect: false,
                custom: function (ctx) {
                    return buildTooltip(range, ctx.dataPointIndex);
                }
            },
            grid: { borderColor: "#e2e8f0", strokeDashArray: 4, xaxis: { lines: { show: false } } },
            dataLabels: { enabled: false }
        };

        window._revenueChartInst = new ApexCharts(document.getElementById("revenueChart"), opts);
        window._revenueChartInst.render();
    }

    document.addEventListener("DOMContentLoaded", function () {
        if (typeof ApexCharts === "undefined" || !document.getElementById("revenueChart")) {
            return;
        }

        var defaultRange = revenueRawData.defaultRange || "year";
        buildRevenueChart(defaultRange);

        var filter = document.getElementById("revenueChartFilter");
        if (filter) {
            filter.value = defaultRange;
            filter.addEventListener("change", function () {
                buildRevenueChart(this.value);
            });
        }
    });
})();
</script>
<%@ include file="layout/_admin_footer.jsp" %>
