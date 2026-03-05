<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Entities.Book"%>
<%@page import="Entities.Category"%>
<%@page import="Entities.Publisher"%>
<%
    Book book = (Book) request.getAttribute("book");
    Category category = (Category) request.getAttribute("category");
    Publisher publisher = (Publisher) request.getAttribute("publisher");
    List<String> authors = (List<String>) request.getAttribute("authors");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= book.getBookName() %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <%@ include file="../_header.jsp" %>

    <div class="layout">
        <%@ include file="../_sidebar.jsp" %>

        <main class="content">
            <a class="back-link" href="<%=request.getContextPath()%>/home">Quay về trang sinh viên</a>

            <section class="detail-card">
                <div class="book-cover">
                    <% if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) { %>
                        <img src="<%= book.getImageUrl() %>" alt="<%= book.getBookName() %>">
                    <% } else { %>
                        <span><%= book.getBookName().substring(0, 1).toUpperCase() %></span>
                    <% } %>
                </div>

                <div>
                    <h1 class="detail-title"><%= book.getBookName() %></h1>
                    <p class="detail-author">
                        <%= (authors != null && !authors.isEmpty()) ? String.join(", ", authors) : "Không có thông tin tác giả" %>
                    </p>

                    <div class="meta-grid">
                        <div class="meta-card">
                            <span>Thể loại</span>
                            <strong><%= category != null ? category.getCategoryName() : "-" %></strong>
                        </div>
                        <div class="meta-card">
                            <span>Nhà xuất bản</span>
                            <strong><%= publisher != null ? publisher.getPublisherName() : "-" %></strong>
                        </div>
                        <div class="meta-card">
                            <span>Số lượng</span>
                            <strong><%= book.getQuantity() %> cuốn</strong>
                        </div>
                        <div class="meta-card">
                            <span>Có sẵn</span>
                            <strong><%= book.getAvailable() %> cuốn</strong>
                        </div>
                    </div>

                    <span class="status-pill <%= book.getAvailable() > 0 ? "ok" : "out" %>">
                        <%= book.getAvailable() > 0 ? "Có thể mượn" : "Tạm hết sách" %>
                    </span>

                    <div class="action-row">
                        <% if (book.getAvailable() > 0) { %>
                            <form method="post" action="<%=request.getContextPath()%>/borrows">
                                <input type="hidden" name="action" value="borrow">
                                <input type="hidden" name="bookID" value="<%= book.getBookID() %>">
                                <button class="btn-primary" type="submit">Mượn sách</button>
                            </form>
                        <% } %>
                        <form method="post" action="<%=request.getContextPath()%>/borrows">
                            <input type="hidden" name="action" value="buy">
                            <input type="hidden" name="bookID" value="<%= book.getBookID() %>">
                            <button class="btn-secondary" type="submit">Đặt mua</button>
                        </form>
                        <a class="btn-ghost" href="<%=request.getContextPath()%>/borrows?action=list">Mở trung tâm mượn trả</a>
                    </div>

                    <div class="detail-note">
                        Giao diện chi tiết sách đã được tích hợp từ nhánh Huynh và điều chỉnh theo luồng hiện tại trên nhánh chính. Thao tác mượn vẫn đi qua controller hiện có để đảm bảo tương thích với cơ sở dữ liệu và màn hình duyệt của quản trị.
                    </div>
                </div>
            </section>
        </main>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>


