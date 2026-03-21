<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tạo phiếu mượn sách — Admin</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    </head>
    <body class="bg-body-tertiary">
        <c:set var="activeTab" value="borrows" />
        <%@ include file="../admin/_header.jsp" %>

        <div class="container py-4" style="max-width:680px;">
            <a href="${pageContext.request.contextPath}/admin/borrows?action=list"
               class="text-decoration-none fw-semibold mb-3 d-inline-block" style="font-size:14px;">&larr; Quay lại danh sách</a>

            <div class="bg-white border rounded-4 p-4">
                <h2 class="h5 fw-bold mb-3">Tạo phiếu mượn sách</h2>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger py-2" style="font-size:14px;">${error}</div>
                </c:if>

                <form method="POST" action="${pageContext.request.contextPath}/admin/borrows">
                    <input type="hidden" name="action" value="create">

                    <div class="mb-3">
                        <label class="form-label fw-semibold" style="font-size:14px;">Sinh viên</label>
                        <select class="form-select" name="studentID" required>
                            <option value="">— Chọn sinh viên —</option>
                            <c:forEach var="s" items="${students}">
                                <option value="${s.studentID}" ${selectedStudentId == s.studentID ? 'selected' : ''}>
                                    ${s.studentID} — ${s.studentName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold" style="font-size:14px;">Sách</label>
                        <select class="form-select" name="bookID" required>
                            <option value="">— Chọn sách —</option>
                            <c:forEach var="b" items="${books}">
                                <option value="${b.bookID}" ${selectedBookId == b.bookID ? 'selected' : ''}>
                                    ${b.bookID} — ${b.bookName} (còn ${b.available})
                                </option>
                            </c:forEach>
                        </select>
                        <div class="form-text">Chỉ hiển thị sách còn trong kho (có sẵn &gt; 0).</div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold" style="font-size:14px;">Số lượng mượn</label>
                            <input type="number" class="form-control" name="quantity" min="1"
                                   value="${empty quantity ? 1 : quantity}" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold" style="font-size:14px;">Hạn trả</label>
                            <input type="date" class="form-control" name="dueDate" value="${dueDate}" required>
                        </div>
                    </div>

                    <div class="d-flex gap-2">
                        <button class="btn btn-primary" type="submit">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="me-1" style="vertical-align:-2px;">
                            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                            </svg>
                            Tạo phiếu
                        </button>
                        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/borrows?action=list">Hủy</a>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
