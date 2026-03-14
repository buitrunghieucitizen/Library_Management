<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Sua sach</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="books" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <div class="card">
            <h2>Sua sach</h2>

            <c:if test="${not empty error}">
                <div class="error"><c:out value="${error}" /></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error"><c:out value="${param.error}" /></div>
            </c:if>

            <form method="POST" action="${pageContext.request.contextPath}/admin/books?action=edit">
                <input type="hidden" name="bookID" value="${book.bookID}">

                <div class="field">
                    <label for="bookName">Ten sach</label>
                    <input id="bookName" type="text" name="bookName" value="${book.bookName}" required>
                </div>

                <div class="field">
                    <label for="quantity">So luong</label>
                    <input id="quantity" type="number" name="quantity" min="0" value="${book.quantity}" required>
                </div>

                <div class="field">
                    <label for="available">Con lai</label>
                    <input id="available" type="number" name="available" min="0" value="${book.available}" required>
                </div>

                <div class="field">
                    <label for="categoryID">Ma the loai</label>
                    <input id="categoryID" type="number" name="categoryID" min="1" value="${book.categoryID}" required>
                </div>

                <div class="field">
                    <label for="publisherID">Ma nha xuat ban</label>
                    <input id="publisherID" type="number" name="publisherID" min="1" value="${book.publisherID}" required>
                </div>

                <h3 class="h3">Gia hien hanh</h3>

                <div class="field">
                    <label for="priceAmount">Gia</label>
                    <input id="priceAmount" type="number" name="priceAmount" min="0" step="0.01" value="${empty currentPrice ? 0 : currentPrice.amount}" required>
                </div>

                <div class="field">
                    <label for="priceCurrency">Tien te</label>
                    <input id="priceCurrency" type="text" name="priceCurrency" value="${empty currentPrice ? 'VND' : currentPrice.currency}" required>
                </div>

                <div class="field">
                    <label for="priceNote">Ghi chu gia</label>
                    <input id="priceNote" type="text" name="priceNote" value="${empty currentPrice ? '' : currentPrice.note}">
                    <p class="note">
                        <c:choose>
                            <c:when test="${not empty currentPrice}">
                                Gia hien hanh bat dau tu: ${currentPrice.startDate}
                            </c:when>
                            <c:otherwise>
                                Sach nay chua co gia, cap nhat lan nay se tao gia moi.
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>

                <div class="actions">
                    <button class="btn btn-primary" type="submit">Cap nhat</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/books?action=list">Huy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
