<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Quản lý mượn trả" />
    <c:set var="activeTab" value="borrows" />
    <%@ include file="../admin/layout/_admin_header.jsp" %>

        <div class="container-fluid px-3 px-md-4 py-4" style="max-width:1400px;">

            <%-- Flash messages --%>
            <c:if test="${not empty param.msg}">
                <div class="alert alert-success alert-dismissible fade show py-2" role="alert" style="font-size:14px;">
                    ${param.msg}<button type="button" class="btn-close btn-sm" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger alert-dismissible fade show py-2" role="alert" style="font-size:14px;">
                    ${param.error}<button type="button" class="btn-close btn-sm" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <%-- Page header --%>
            <div class="d-flex align-items-center justify-content-between mb-3">
                <div>
                    <h1 class="h4 fw-bold mb-0">Quản lý mượn trả sách</h1>
                    <small class="text-muted">${totalItems} bản ghi tổng cộng</small>
                </div>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/borrows?action=create">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="me-1" style="vertical-align:-2px;">
                    <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Tạo phiếu mượn
                </a>
            </div>

            <%-- Tabs: Pending | Tất cả | Giữ chỗ --%>
            <ul class="nav nav-tabs mb-3" role="tablist">
                <li class="nav-item">
                    <button class="nav-link active position-relative" data-bs-toggle="tab" data-bs-target="#tab-pending" type="button">
                        Chờ duyệt
                        <c:if test="${pendingCount gt 0}">
                            <span class="badge text-bg-danger ms-1" style="font-size:10px;">${pendingCount}</span>
                        </c:if>
                    </button>
                </li>
                <li class="nav-item">
                    <button class="nav-link" data-bs-toggle="tab" data-bs-target="#tab-all" type="button">
                        Tất cả phiếu mượn
                    </button>
                </li>
                <li class="nav-item">
                    <button class="nav-link" data-bs-toggle="tab" data-bs-target="#tab-holds" type="button">
                        Giữ chỗ
                        <c:if test="${not empty activeHolds}">
                            <span class="badge text-bg-warning ms-1" style="font-size:10px;">${fn:length(activeHolds)}</span>
                        </c:if>
                    </button>
                </li>
            </ul>

            <div class="tab-content">

                <%-- ==================== TAB 1: PENDING ==================== --%>
                <div class="tab-pane fade show active" id="tab-pending">
                    <c:choose>
                        <c:when test="${empty pendingBorrows}">
                            <div class="bg-white border rounded-3 text-center text-muted py-5">
                                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" class="mb-2 text-muted opacity-50">
                                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>
                                </svg>
                                <div>Không có yêu cầu mượn nào đang chờ duyệt.</div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="row g-3">
                                <c:forEach var="pb" items="${pendingBorrows}">
                                    <div class="col-md-6 col-xl-4">
                                        <div class="card border h-100" style="border-radius:12px;">
                                            <div class="card-body p-3">
                                                <div class="d-flex align-items-start justify-content-between mb-2">
                                                    <div>
                                                        <span class="badge text-bg-warning" style="font-size:11px;">Chờ duyệt</span>
                                                        <span class="text-muted ms-1" style="font-size:12px;">#${pb.borrowID}</span>
                                                    </div>
                                                    <small class="text-muted">${pb.borrowDate}</small>
                                                </div>

                                                <div class="mb-2">
                                                    <div class="fw-semibold" style="font-size:14px;">${pb.studentName}</div>
                                                    <div class="text-muted" style="font-size:12px;">Nhân viên: ${pb.staffName}</div>
                                                </div>

                                                <div class="bg-body-tertiary rounded-2 p-2 mb-2" style="font-size:12px;">
                                                    <div class="text-muted mb-1">Sách mượn:</div>
                                                    <div class="fw-medium">${pb.items}</div>
                                                </div>

                                                <div class="d-flex align-items-center gap-1 mb-2" style="font-size:12px;">
                                                    <span class="text-muted">Hạn trả:</span>
                                                    <span class="fw-semibold">${pb.dueDate}</span>
                                                </div>

                                                <%-- Action buttons --%>
                                                <div class="d-flex gap-2">
                                                    <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" class="m-0 flex-fill"
                                                          onsubmit="return confirm('Duyệt phiếu mượn #${pb.borrowID}? Sách sẽ được trừ khỏi kho.');">
                                                        <input type="hidden" name="action" value="approve">
                                                        <input type="hidden" name="borrowID" value="${pb.borrowID}">
                                                        <button class="btn btn-success btn-sm w-100 d-flex align-items-center justify-content-center gap-1" type="submit">
                                                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                                                            Duyệt
                                                        </button>
                                                    </form>
                                                    <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" class="m-0 flex-fill"
                                                          onsubmit="return confirm('Từ chối phiếu mượn #${pb.borrowID}?');">
                                                        <input type="hidden" name="action" value="reject">
                                                        <input type="hidden" name="borrowID" value="${pb.borrowID}">
                                                        <button class="btn btn-outline-danger btn-sm w-100 d-flex align-items-center justify-content-center gap-1" type="submit">
                                                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                                                            Từ chối
                                                        </button>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <%-- ==================== TAB 2: ALL BORROWS ==================== --%>
                <div class="tab-pane fade" id="tab-all">
                    <div class="bg-white border rounded-3 overflow-hidden">
                        <div class="table-responsive">
                            <table class="table table-hover mb-0" style="font-size:13px;">
                                <thead>
                                    <tr class="table-dark">
                                        <th style="width:60px;">Mã</th>
                                        <th>Sinh viên</th>
                                        <th>Nhân viên</th>
                                        <th>Ngày mượn</th>
                                        <th>Hạn trả</th>
                                        <th>Ngày trả</th>
                                        <th>Trạng thái</th>
                                        <th>Sách</th>
                                        <th style="width:140px;">Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="b" items="${borrows}">
                                        <tr>
                                            <td class="fw-semibold">${b.borrowID}</td>
                                            <td>${b.studentName}</td>
                                            <td class="text-muted">${b.staffName}</td>
                                            <td>${b.borrowDate}</td>
                                            <td>${b.dueDate}</td>
                                            <td><c:out value="${b.returnDate}" default="—"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${b.status eq 'Pending'}">
                                                        <span class="badge text-bg-warning">Chờ duyệt</span>
                                                    </c:when>
                                                    <c:when test="${b.status eq 'Borrowing'}">
                                                        <span class="badge text-bg-info">Đang mượn</span>
                                                    </c:when>
                                                    <c:when test="${b.status eq 'Returned'}">
                                                        <span class="badge text-bg-success">Đã trả</span>
                                                    </c:when>
                                                    <c:when test="${b.status eq 'Overdue'}">
                                                        <span class="badge text-bg-danger">Quá hạn</span>
                                                    </c:when>
                                                    <c:when test="${b.status eq 'Rejected'}">
                                                        <span class="badge text-bg-secondary">Đã từ chối</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge text-bg-secondary">${b.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <span class="text-truncate d-inline-block" style="max-width:200px;" title="${b.items}">
                                                    ${b.items}
                                                </span>
                                            </td>
                                            <td>
                                                <div class="d-flex gap-1 flex-wrap">
                                                    <%-- Pending: approve/reject --%>
                                                    <c:if test="${b.status eq 'Pending'}">
                                                        <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" class="m-0">
                                                            <input type="hidden" name="action" value="approve">
                                                            <input type="hidden" name="borrowID" value="${b.borrowID}">
                                                            <button class="btn btn-success btn-sm py-0 px-2" type="submit" style="font-size:12px;"
                                                                    onclick="return confirm('Duyệt phiếu #${b.borrowID}?');">Duyệt</button>
                                                        </form>
                                                        <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" class="m-0">
                                                            <input type="hidden" name="action" value="reject">
                                                            <input type="hidden" name="borrowID" value="${b.borrowID}">
                                                            <button class="btn btn-outline-danger btn-sm py-0 px-2" type="submit" style="font-size:12px;"
                                                                    onclick="return confirm('Từ chối phiếu #${b.borrowID}?');">Từ chối</button>
                                                        </form>
                                                    </c:if>

                                                    <%-- Borrowing/Overdue: return --%>
                                                    <c:if test="${b.status eq 'Borrowing' || b.status eq 'Overdue'}">
                                                        <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" class="m-0">
                                                            <input type="hidden" name="action" value="return">
                                                            <input type="hidden" name="borrowID" value="${b.borrowID}">
                                                            <button class="btn btn-primary btn-sm py-0 px-2" type="submit" style="font-size:12px;"
                                                                    onclick="return confirm('Xác nhận trả sách phiếu #${b.borrowID}?');">Xác nhận trả</button>
                                                        </form>
                                                    </c:if>

                                                    <%-- Returned/Rejected: no action --%>
                                                    <c:if test="${b.status eq 'Returned' || b.status eq 'Rejected'}">
                                                        <span class="text-muted" style="font-size:12px;">—</span>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty borrows}">
                                        <tr><td colspan="9" class="text-center text-muted py-4">Chưa có phiếu mượn nào.</td></tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <%-- Pagination --%>
                    <c:if test="${totalPages gt 1}">
                        <nav class="mt-3">
                            <ul class="pagination pagination-sm justify-content-center">
                                <c:if test="${currentPage gt 1}">
                                    <c:url var="prevUrl" value="/admin/borrows"><c:param name="action" value="list"/><c:param name="page" value="${currentPage - 1}"/></c:url>
                                    <li class="page-item"><a class="page-link" href="${prevUrl}">&laquo;</a></li>
                                    </c:if>
                                    <c:forEach begin="1" end="${totalPages}" var="p">
                                        <c:url var="pageUrl" value="/admin/borrows"><c:param name="action" value="list"/><c:param name="page" value="${p}"/></c:url>
                                    <li class="page-item ${p eq currentPage ? 'active' : ''}"><a class="page-link" href="${pageUrl}">${p}</a></li>
                                    </c:forEach>
                                    <c:if test="${currentPage lt totalPages}">
                                        <c:url var="nextUrl" value="/admin/borrows"><c:param name="action" value="list"/><c:param name="page" value="${currentPage + 1}"/></c:url>
                                    <li class="page-item"><a class="page-link" href="${nextUrl}">&raquo;</a></li>
                                    </c:if>
                            </ul>
                        </nav>
                    </c:if>
                </div>

                <%-- ==================== TAB 3: HOLDS ==================== --%>
                <div class="tab-pane fade" id="tab-holds">
                    <c:choose>
                        <c:when test="${empty activeHolds}">
                            <div class="bg-white border rounded-3 text-center text-muted py-5">
                                Không có yêu cầu giữ chỗ nào đang hoạt động.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="bg-white border rounded-3 overflow-hidden">
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0" style="font-size:13px;">
                                        <thead>
                                            <tr class="table-dark">
                                                <th style="width:60px;">Mã</th>
                                                <th>Sinh viên</th>
                                                <th>Email</th>
                                                <th>Sách</th>
                                                <th>Ngày đặt</th>
                                                <th>Trạng thái</th>
                                                <th>Thông báo lúc</th>
                                                <th>Hết hạn</th>
                                                <th>Sách có sẵn</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="h" items="${activeHolds}">
                                                <tr>
                                                    <td class="fw-semibold">${h.holdID}</td>
                                                    <td>${h.studentName}</td>
                                                    <td class="text-muted">${h.studentEmail}</td>
                                                    <td>
                                                        <span class="text-truncate d-inline-block" style="max-width:180px;" title="${h.bookName}">
                                                            ${h.bookName}
                                                        </span>
                                                    </td>
                                                    <td>${h.holdDate}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${h.status eq 'Waiting'}">
                                                                <span class="badge text-bg-warning">Đang chờ</span>
                                                            </c:when>
                                                            <c:when test="${h.status eq 'Notified'}">
                                                                <span class="badge text-bg-success">Đã thông báo</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge text-bg-secondary">${h.status}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td><c:out value="${h.notifiedDate}" default="—"/></td>
                                                    <td><c:out value="${h.expireDate}" default="—"/></td>
                                                    <td>
                                                        <span class="badge ${h.bookAvailable gt 0 ? 'text-bg-success' : 'text-bg-danger'}">
                                                            ${h.bookAvailable}
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div><%-- end tab-content --%>
        </div>
    </div>
<%@ include file="../admin/layout/_admin_footer.jsp" %>

