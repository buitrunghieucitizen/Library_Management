<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Thêm sách mới" />
<c:set var="activeTab" value="books" />
<%@ include file="../layout/_admin_header.jsp" %>

<div class="container">
    <div class="card">
        <h2>Thêm sách mới</h2>

        <c:if test="${not empty error}">
            <div class="error"><c:out value="${error}" /></div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="error"><c:out value="${param.error}" /></div>
        </c:if>

        <form method="POST" action="${pageContext.request.contextPath}/admin/books?action=create">
            <div class="field">
                <label for="bookName">Tên sách</label>
                <input id="bookName" type="text" name="bookName" required>
            </div>

            <div class="field">
                <label for="quantity">Số lượng</label>
                <input id="quantity" type="number" name="quantity" min="0" value="1" required>
            </div>

            <div class="field">
                <label for="available">Còn lại</label>
                <input id="available" type="number" name="available" min="0" value="1" required>
            </div>

            <div class="field">
                <label for="categoryID">Mã thể loại</label>
                <input id="categoryID" type="number" name="categoryID" min="1" required>
            </div>

            <div class="field">
                <label for="publisherID">Mã nhà xuất bản</label>
                <input id="publisherID" type="number" name="publisherID" min="1" required>
            </div>

            <div class="field">
                <label for="imageUrl">Link ảnh bìa</label>
                <input id="imageUrl" type="text" name="imageUrl" placeholder="https://... hoặc /assets/...">
                <p class="note">Để trống nếu muốn hệ thống tự tìm ảnh bìa từ OpenLibrary.</p>
            </div>

            <div class="field">
                <label for="shelfLocation">Vị trí kệ</label>
                <input id="shelfLocation" type="text" name="shelfLocation" placeholder="Ví dụ: Kệ A2 - Tầng 3">
            </div>

            <div class="field">
                <label for="description">Mô tả sách</label>
                <textarea id="description" name="description" rows="4" placeholder="Tóm tắt nội dung hoặc ghi chú cho sinh viên"></textarea>
            </div>

            <h3 class="h3">Giá sách ban đầu</h3>

            <div class="field">
                <label for="priceAmount">Giá</label>
                <input id="priceAmount" type="number" name="priceAmount" min="0" step="0.01" value="0" required>
            </div>

            <div class="field">
                <label for="priceCurrency">Tiền tệ</label>
                <input id="priceCurrency" type="text" name="priceCurrency" value="VND" required>
            </div>

            <div class="field">
                <label for="priceNote">Ghi chú giá</label>
                <input id="priceNote" type="text" name="priceNote" placeholder="Ví dụ: Giá bìa">
                <p class="note">Khi tạo sách, hệ thống sẽ tạo luôn giá hiện hành cho sách này.</p>
            </div>

            <div class="actions">
                <button class="btn btn-primary" type="submit">Lưu</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/books?action=list">Hủy</a>
            </div>
        </form>
    </div>
</div>
<%@ include file="../layout/_admin_footer.jsp" %>
