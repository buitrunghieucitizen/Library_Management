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
            <a class="back-link" href="<%=request.getContextPath()%>/home">Quay ve trang sinh vien</a>

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
                        <%= (authors != null && !authors.isEmpty()) ? String.join(", ", authors) : "Khong co thong tin tac gia" %>
                    </p>

                    <div class="meta-grid">
                        <div class="meta-card">
                            <span>The loai</span>
                            <strong><%= category != null ? category.getCategoryName() : "-" %></strong>
                        </div>
                        <div class="meta-card">
                            <span>Nha xuat ban</span>
                            <strong><%= publisher != null ? publisher.getPublisherName() : "-" %></strong>
                        </div>
                        <div class="meta-card">
                            <span>So luong</span>
                            <strong><%= book.getQuantity() %> cuon</strong>
                        </div>
                        <div class="meta-card">
                            <span>Co san</span>
                            <strong><%= book.getAvailable() %> cuon</strong>
                        </div>
                    </div>

                    <span class="status-pill <%= book.getAvailable() > 0 ? "ok" : "out" %>">
                        <%= book.getAvailable() > 0 ? "Co the muon" : "Tam het sach" %>
                    </span>

                    <div class="action-row">
                        <% if (book.getAvailable() > 0) { %>
                            <form method="post" action="<%=request.getContextPath()%>/borrows">
                                <input type="hidden" name="action" value="borrow">
                                <input type="hidden" name="bookID" value="<%= book.getBookID() %>">
                                <button class="btn-primary" type="submit">Muon sach</button>
                            </form>
                        <% } %>

                        <form method="post" action="<%=request.getContextPath()%>/borrows">
                            <input type="hidden" name="action" value="addBuyList">
                            <input type="hidden" name="bookID" value="<%= book.getBookID() %>">
                            <button class="btn-secondary" type="submit">Them vao danh sach can mua</button>
                        </form>

                        <form method="post" action="<%=request.getContextPath()%>/borrows">
                            <input type="hidden" name="action" value="buy">
                            <input type="hidden" name="bookID" value="<%= book.getBookID() %>">
                            <button class="btn-buy" type="submit">Mua nhanh</button>
                        </form>

                        <a class="btn-ghost" href="<%=request.getContextPath()%>/borrows?action=list">Mo trung tam muon tra</a>
                    </div>

                    <div class="detail-note">
                        Ban co the them sach vao danh sach can mua de gui duyet tung quyen hoac gui duyet tat ca
                        tai man hinh Trung tam muon va mua sach.
                    </div>
                </div>
            </section>
        </main>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>
