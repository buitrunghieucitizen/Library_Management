<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Sửa sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <c:set var="activeTab" value="books" />
    <%@ include file="../admin/_header.jsp" %>


    <div class="container">
        <div class="card">
            <h2>Sửa sách</h2>
            <form method="POST" action="${pageContext.request.contextPath}/admin/books?action=edit">
                <input type="hidden" name="bookID" value="${book.bookID}">

                <label>Tên sách</label>
                <input type="text" name="bookName" value="${book.bookName}" required>

                <label>Số lượng</label>
                <input type="number" name="quantity" min="0" value="${book.quantity}" required>

                <label>Còn lại</label>
                <input type="number" name="available" min="0" value="${book.available}" required>

                <label>Mã thể loại</label>
                <input type="number" name="categoryID" min="1" value="${book.categoryID}" required>

                <label>Mã nhà xuất bản</label>
                <input type="number" name="publisherID" min="1" value="${book.publisherID}" required>

                <h3>Giá hiện hành</h3>

                <label>Giá</label>
                <input type="number" name="priceAmount" min="0" step="0.01" value="${empty currentPrice ? 0 : currentPrice.amount}" required>

                <label>Tiền tệ</label>
                <input type="text" name="priceCurrency" value="${empty currentPrice ? 'VND' : currentPrice.currency}" required>

                <label>Ghi chú giá</label>
                <input type="text" name="priceNote" value="${empty currentPrice ? '' : currentPrice.note}">
                <p class="meta">
                    <c:choose>
                        <c:when test="${not empty currentPrice}">
                            Giá hiện hành bắt đầu từ: ${currentPrice.startDate}
                        </c:when>
                        <c:otherwise>
                            Sách này chưa có giá, cập nhật lần này sẽ tạo giá mới.
                        </c:otherwise>
                    </c:choose>
                </p>

                <button class="btn btn-primary" type="submit">Cập nhật</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/books">Hủy</a>
            </form>
        </div>
    </div>
</body>
</html>


