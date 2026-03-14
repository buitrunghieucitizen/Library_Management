<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Them sach moi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="books" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <div class="card">
            <h2>Them sach moi</h2>

            <c:if test="${not empty error}">
                <div class="error"><c:out value="${error}" /></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error"><c:out value="${param.error}" /></div>
            </c:if>

            <form method="POST" action="${pageContext.request.contextPath}/admin/books?action=create">
                <div class="field">
                    <label for="bookName">Ten sach</label>
                    <input id="bookName" type="text" name="bookName" required>
                </div>

                <div class="field">
                    <label for="quantity">So luong</label>
                    <input id="quantity" type="number" name="quantity" min="0" value="1" required>
                </div>

                <div class="field">
                    <label for="available">Con lai</label>
                    <input id="available" type="number" name="available" min="0" value="1" required>
                </div>

                <div class="field">
                    <label for="categoryID">Ma the loai</label>
                    <input id="categoryID" type="number" name="categoryID" min="1" required>
                </div>

                <div class="field">
                    <label for="publisherID">Ma nha xuat ban</label>
                    <input id="publisherID" type="number" name="publisherID" min="1" required>
                </div>

                <h3 class="h3">Gia sach ban dau</h3>

                <div class="field">
                    <label for="priceAmount">Gia</label>
                    <input id="priceAmount" type="number" name="priceAmount" min="0" step="0.01" value="0" required>
                </div>

                <div class="field">
                    <label for="priceCurrency">Tien te</label>
                    <input id="priceCurrency" type="text" name="priceCurrency" value="VND" required>
                </div>

                <div class="field">
                    <label for="priceNote">Ghi chu gia</label>
                    <input id="priceNote" type="text" name="priceNote" placeholder="Vi du: Gia bia">
                    <p class="note">Khi tao sach, he thong se tao luon gia hien hanh cho sach nay.</p>
                </div>

                <div class="actions">
                    <button class="btn btn-primary" type="submit">Luu</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/books?action=list">Huy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
