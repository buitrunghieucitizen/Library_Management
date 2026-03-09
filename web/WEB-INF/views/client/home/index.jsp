<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="Entities.Book"%>
<%@page import="Entities.Borrow"%>
<%@page import="Entities.Category"%>
<%@page import="Entities.Publisher"%>
<%
    List<Book> books = (List<Book>) request.getAttribute("books");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    List<Publisher> publishers = (List<Publisher>) request.getAttribute("publishers");
    List<Borrow> holds = (List<Borrow>) request.getAttribute("holds");

    String search = request.getAttribute("search") != null ? (String) request.getAttribute("search") : "";
    String letter = request.getAttribute("letter") != null ? (String) request.getAttribute("letter") : "ALL";
    String categoryId = request.getAttribute("categoryId") != null ? (String) request.getAttribute("categoryId") : "";
    String publisherId = request.getAttribute("publisherId") != null ? (String) request.getAttribute("publisherId") : "";
    String author = request.getAttribute("author") != null ? (String) request.getAttribute("author") : "";

    Integer currentPageObj = (Integer) request.getAttribute("currentPage");
    Integer totalPagesObj = (Integer) request.getAttribute("totalPages");
    Integer totalBooksObj = (Integer) request.getAttribute("totalBooks");
    int currentPage = currentPageObj == null ? 1 : currentPageObj;
    int totalPages = totalPagesObj == null ? 1 : totalPagesObj;
    int totalBooks = totalBooksObj == null ? 0 : totalBooksObj;

    String encodedSearch = URLEncoder.encode(search, StandardCharsets.UTF_8);
    String encodedAuthor = URLEncoder.encode(author, StandardCharsets.UTF_8);
    String encodedCategoryId = URLEncoder.encode(categoryId, StandardCharsets.UTF_8);
    String encodedPublisherId = URLEncoder.encode(publisherId, StandardCharsets.UTF_8);
    String baseQuery = "search=" + encodedSearch
            + "&author=" + encodedAuthor
            + "&categoryId=" + encodedCategoryId
            + "&publisherId=" + encodedPublisherId;
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng sinh viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <%@ include file="../_header.jsp" %>

    <div class="layout">
        <%@ include file="../_sidebar.jsp" %>

        <main class="content">
            <section class="hero">
                <h1>Cổng thư viện sinh viên</h1>
                <p>Tìm sách, lọc theo tác giả/thể loại/nhà xuất bản, và mở nhanh trung tâm mượn trả.</p>
            </section>

            <form class="search-form" method="get" action="<%=request.getContextPath()%>/home">
                <div class="search-row">
                    <input type="text" name="search" placeholder="Tìm theo tên sách" value="<%= search %>">
                    <input type="text" name="author" placeholder="Tác giả" value="<%= author %>">
                    <select name="categoryId">
                        <option value="">Tất cả thể loại</option>
                        <% for (Category category : categories) { %>
                            <option value="<%= category.getCategoryID() %>" <%= categoryId.equals(String.valueOf(category.getCategoryID())) ? "selected" : "" %>>
                                <%= category.getCategoryName() %>
                            </option>
                        <% } %>
                    </select>
                    <select name="publisherId">
                        <option value="">Tất cả nhà xuất bản</option>
                        <% for (Publisher publisher : publishers) { %>
                            <option value="<%= publisher.getPublisherID() %>" <%= publisherId.equals(String.valueOf(publisher.getPublisherID())) ? "selected" : "" %>>
                                <%= publisher.getPublisherName() %>
                            </option>
                        <% } %>
                    </select>
                    <div class="search-actions">
                        <button class="btn-apply" type="submit">Lọc</button>
                        <a class="btn-reset" href="<%=request.getContextPath()%>/home">Đặt lại</a>
                    </div>
                </div>

                <div class="letter-strip">
                    <a href="<%=request.getContextPath()%>/home?letter=ALL&<%= baseQuery %>&page=1"
                       class="<%= "ALL".equalsIgnoreCase(letter) ? "active" : "" %>">Tất cả</a>
                    <% for (char c = 'A'; c <= 'Z'; c++) { %>
                        <a href="<%=request.getContextPath()%>/home?letter=<%= c %>&<%= baseQuery %>&page=1"
                           class="<%= letter.equalsIgnoreCase(String.valueOf(c)) ? "active" : "" %>"><%= c %></a>
                    <% } %>
                </div>
            </form>

            <div class="section-header">
                <h2><%= "ALL".equalsIgnoreCase(letter) ? "Bộ sưu tập sách" : "Sách bắt đầu bằng " + letter %></h2>
                <span class="note">Tổng kết quả: <%= totalBooks %></span>
                <a href="<%=request.getContextPath()%>/borrows?action=list" class="section-link">Mở trung tâm mượn trả</a>
            </div>

            <% if (books == null || books.isEmpty()) { %>
                <div class="empty-box">Không tìm thấy sách phù hợp với bộ lọc hiện tại.</div>
            <% } else { %>
                <div class="book-grid">
                    <% for (Book book : books) { %>
                        <a class="book-card" href="<%=request.getContextPath()%>/home/book?id=<%= book.getBookID() %>">
                            <div class="book-visual">
                                <% if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) { %>
                                    <img src="<%= book.getImageUrl() %>" alt="<%= book.getBookName() %>" class="book-image" loading="lazy" decoding="async">
                                <% } else { %>
                                    <span><%= book.getBookName().substring(0, 1).toUpperCase() %></span>
                                <% } %>
                            </div>
                            <div class="book-meta">
                                <h3 class="book-title"><%= book.getBookName() %></h3>
                                <span class="pill <%= book.getAvailable() > 0 ? "ok" : "out" %>">
                                    <%= book.getAvailable() > 0 ? (book.getAvailable() + " có sẵn") : "Hết sách" %>
                                </span>
                            </div>
                        </a>
                    <% } %>
                </div>
            <% } %>

            <% if (totalPages > 1) { %>
                <div class="pagination">
                    <% if (currentPage > 1) { %>
                        <a class="page-link" href="<%=request.getContextPath()%>/home?letter=<%= letter %>&<%= baseQuery %>&page=<%= currentPage - 1 %>">Trang trước</a>
                    <% } %>

                    <% for (int p = 1; p <= totalPages; p++) { %>
                        <a class="page-link <%= p == currentPage ? "active" : "" %>"
                           href="<%=request.getContextPath()%>/home?letter=<%= letter %>&<%= baseQuery %>&page=<%= p %>"><%= p %></a>
                    <% } %>

                    <% if (currentPage < totalPages) { %>
                        <a class="page-link" href="<%=request.getContextPath()%>/home?letter=<%= letter %>&<%= baseQuery %>&page=<%= currentPage + 1 %>">Trang sau</a>
                    <% } %>
                </div>
            <% } %>
        </main>

        <aside class="sidebar-right">
            <div class="section-title">Phiếu mượn hiện tại</div>
            <% if (holds == null || holds.isEmpty()) { %>
                <div class="empty-box">Không có sách đang mượn.</div>
            <% } else { %>
                <% for (Borrow hold : holds) { %>
                    <div class="hold-card">
                        <h3>Phiếu mượn #<%= hold.getBorrowID() %></h3>
                        <div class="hold-meta">
                            Ngày mượn: <%= hold.getBorrowDate() %><br>
                            Hạn trả: <%= hold.getDueDate() %>
                        </div>
                        <span class="hold-status <%= "Overdue".equalsIgnoreCase(hold.getStatus()) ? "overdue" : "borrowing" %>">
                            <%= "Overdue".equalsIgnoreCase(hold.getStatus()) ? "Quá hạn" : ("Borrowing".equalsIgnoreCase(hold.getStatus()) ? "Đang mượn" : hold.getStatus()) %>
                        </span>
                    </div>
                <% } %>
            <% } %>
        </aside>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>
