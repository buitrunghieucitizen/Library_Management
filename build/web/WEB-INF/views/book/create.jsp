<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm sách mới</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <c:set var="activeTab" value="books" />
    <%@ include file="../admin/_header.jsp" %>


    <div class="container">
        <div class="card">
            <h2>Thêm sách mới</h2>
            <form method="POST" action="${pageContext.request.contextPath}/admin/books?action=create">
                <label>Tên sách</label>
                <input type="text" name="bookName" required>

                <label>Số lượng</label>
                <input type="number" name="quantity" min="0" value="1" required>

                <label>Còn lại</label>
                <input type="number" name="available" min="0" value="1" required>

                <label>Mã thể loại</label>
                <input type="number" name="categoryID" min="1" required>

                <label>Mã nhà xuất bản</label>
                <input type="number" name="publisherID" min="1" required>

                <h3>Giá sách ban đầu</h3>

                <label>Giá</label>
                <input type="number" name="priceAmount" min="0" step="0.01" value="0" required>

                <label>Tiền tệ</label>
                <input type="text" name="priceCurrency" value="VND" required>

                <label>Ghi chú giá</label>
                <input type="text" name="priceNote" placeholder="Ví dụ: Giá bìa">
                <p class="note">Khi tạo sách, hệ thống sẽ tạo luôn giá hiện hành cho sách này.</p>

                <button class="btn btn-primary" type="submit">Lưu</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/books">Hủy</a>
            </form>
        </div>
    </div>
</body>
</html>


