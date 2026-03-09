<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quan ly tep sach</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="bookfiles" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <div class="panel">
            <h2>Quan ly tep sach</h2>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/bookfiles?action=create">Them tep sach</a>

            <c:if test="${not empty param.msg}">
                <div class="msg mb-3">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error mb-3">${param.error}</div>
            </c:if>
            <div class="note">Tong ban ghi: ${totalItems}</div>

            <table>
                <thead>
                    <tr>
                        <th>Ma</th>
                        <th>Sach</th>
                        <th>Nhan vien</th>
                        <th>Tep</th>
                        <th>Lien ket</th>
                        <th>Loai</th>
                        <th>Kich thuoc</th>
                        <th>Ngay tai</th>
                        <th>Kich hoat</th>
                        <th>Hanh dong</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${bookFiles}">
                        <tr>
                            <td>${row.bookFileID}</td>
                            <td>${row.bookName}</td>
                            <td>${row.staffName}</td>
                            <td>${row.fileName}</td>
                            <td><a href="${row.fileUrl}" target="_blank">${row.fileUrl}</a></td>
                            <td>${row.fileType}</td>
                            <td>${row.fileSize}</td>
                            <td>${row.uploadAt}</td>
                            <td>${row.active ? 'Co' : 'Khong'}</td>
                            <td>
                                <div class="actions">
                                    <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/bookfiles?action=edit&id=${row.bookFileID}">Sua</a>
                                    <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/bookfiles?action=delete&id=${row.bookFileID}" onclick="return confirm('Xoa tep sach nay?')">Xoa</a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty bookFiles}">
                        <tr>
                            <td colspan="10" class="empty-row">Chua co tep sach nao.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <c:url var="prevUrl" value="/admin/bookfiles">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${currentPage - 1}"/>
                        </c:url>
                        <a class="page-link" href="${pageContext.request.contextPath}${prevUrl}">Trang truoc</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:url var="pageUrl" value="/admin/bookfiles">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${p}"/>
                        </c:url>
                        <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageContext.request.contextPath}${pageUrl}">${p}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <c:url var="nextUrl" value="/admin/bookfiles">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${currentPage + 1}"/>
                        </c:url>
                        <a class="page-link" href="${pageContext.request.contextPath}${nextUrl}">Trang sau</a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>
</body>
</html>
