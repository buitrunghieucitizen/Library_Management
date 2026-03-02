<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 03/03/2026
  Time: 1:36 SA
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="java.util.List, entities.*" %>
<%
    List<Book> books = (List<Book>) request.getAttribute("books");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    List<Publisher> publishers = (List<Publisher>) request.getAttribute("publishers");
    List<Borrow> holds = (List<Borrow>) request.getAttribute("holds");

    String search = (String) request.getAttribute("search");
    String letter = (String) request.getAttribute("letter");
    String categoryId = (String) request.getAttribute("categoryId");
    String publisherId = (String) request.getAttribute("publisherId");
    String author = (String) request.getAttribute("author");

    // Dùng borrowCount từ controller
    int cartCount = (request.getAttribute("borrowCount") != null ? (int) request.getAttribute("borrowCount") : 0)
            + (request.getAttribute("buyCount") != null ? (int) request.getAttribute("buyCount") : 0);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Library | Collections</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap"
          rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            font-family: 'Plus Jakarta Sans', sans-serif;
            box-sizing: border-box;
        }

        body {
            background: #f8fafc;
            color: #0f172a;
            margin: 0;
        }

        /* ── Navbar ── */
        .top-nav {
            background: #0f172a;
            padding: 0 2rem;
            height: 56px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: sticky;
            top: 0;
            z-index: 100;
        }

        .top-nav .brand {
            color: #fff;
            font-weight: 700;
            font-size: 1.1rem;
            display: flex;
            align-items: center;
            gap: 10px;
            text-decoration: none;
        }

        .top-nav .brand i {
            color: #2563eb;
        }

        .top-nav .nav-right {
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        .user-info {
            display: flex;
            align-items: center;
            gap: .6rem;
        }

        .user-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: #2563eb;
            color: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: .85rem;
            font-weight: 700;
        }

        .user-name {
            color: #e2e8f0;
            font-size: .875rem;
            font-weight: 600;
        }

        .btn-logout, .btn-login {
            background: #2563eb;
            color: #fff !important;
            border: none;
            border-radius: .5rem;
            padding: .35rem 1rem;
            font-size: .85rem;
            font-weight: 700;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
        }

        .cart-btn {
            position: relative;
            background: #1e293b;
            color: #fff;
            border: none;
            border-radius: .5rem;
            padding: .35rem .9rem;
            font-size: .85rem;
            font-weight: 700;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: .4rem;
        }

        .cart-btn .cart-badge {
            position: absolute;
            top: -6px;
            right: -6px;
            background: #2563eb;
            color: #fff;
            font-size: .65rem;
            font-weight: 700;
            width: 18px;
            height: 18px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        /* ── Layout 3 cột ── */
        .main-layout {
            display: grid;
            grid-template-columns: 220px 1fr 280px;
            min-height: calc(100vh - 56px - 40px);
        }

        /* ── Sidebar trái ── */
        .sidebar-left {
            background: #fff;
            border-right: 1px solid #e2e8f0;
            padding: 1.5rem 1rem;
            position: sticky;
            top: 56px;
            height: calc(100vh - 56px);
            overflow-y: auto;
        }

        .section-title {
            font-size: .7rem;
            font-weight: 700;
            color: #94a3b8;
            letter-spacing: .08em;
            text-transform: uppercase;
            margin-bottom: .75rem;
            padding-left: .25rem;
        }

        .nav-item a {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: .55rem .75rem;
            border-radius: .6rem;
            color: #475569;
            text-decoration: none;
            font-weight: 600;
            font-size: .9rem;
            transition: .15s;
        }

        .nav-item a:hover, .nav-item a.active {
            background: #eff6ff;
            color: #2563eb;
        }

        .nav-item a i {
            width: 16px;
            text-align: center;
        }

        .divider {
            border-top: 1px solid #e2e8f0;
            margin: 1rem 0;
        }

        /* ── Content ── */
        .content-area {
            padding: 1.5rem;
            overflow-y: auto;
        }

        .search-bar {
            background: #fff;
            border: 1.5px solid #e2e8f0;
            border-radius: .85rem;
            display: flex;
            align-items: center;
            padding: 0 1rem;
            height: 48px;
            margin-bottom: 1.25rem;
        }

        .search-bar i {
            color: #94a3b8;
            margin-right: .75rem;
        }

        .search-bar input {
            border: none;
            outline: none;
            flex: 1;
            font-family: 'Plus Jakarta Sans', sans-serif;
            font-size: .95rem;
            background: transparent;
        }

        .search-bar button {
            background: #0f172a;
            color: #fff;
            border: none;
            border-radius: .6rem;
            padding: .35rem .9rem;
            font-size: .85rem;
            font-weight: 700;
            cursor: pointer;
        }

        .filter-bar {
            display: flex;
            gap: .75rem;
            flex-wrap: wrap;
            margin-bottom: 1.25rem;
        }

        .filter-bar select {
            height: 40px;
            border: 1.5px solid #e2e8f0;
            border-radius: .6rem;
            padding: 0 .75rem;
            font-family: 'Plus Jakarta Sans', sans-serif;
            font-size: .85rem;
            font-weight: 500;
            background: #fff;
            color: #0f172a;
            cursor: pointer;
        }

        .filter-bar select:focus {
            outline: none;
            border-color: #2563eb;
        }

        .btn-filter-apply {
            height: 40px;
            background: #2563eb;
            color: #fff;
            border: none;
            border-radius: .6rem;
            padding: 0 1.25rem;
            font-weight: 700;
            font-size: .85rem;
            cursor: pointer;
        }

        .btn-filter-reset {
            height: 40px;
            background: #f1f5f9;
            color: #475569;
            border: none;
            border-radius: .6rem;
            padding: 0 1rem;
            font-weight: 700;
            font-size: .85rem;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
        }

        .letter-filter {
            display: flex;
            flex-wrap: wrap;
            gap: .3rem;
            margin-bottom: 1.5rem;
        }

        .letter-filter a {
            width: 32px;
            height: 32px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: .4rem;
            font-size: .8rem;
            font-weight: 700;
            color: #475569;
            text-decoration: none;
            background: #fff;
            border: 1.5px solid #e2e8f0;
            transition: .15s;
        }

        .letter-filter a:hover {
            border-color: #2563eb;
            color: #2563eb;
        }

        .letter-filter a.active {
            background: #0f172a;
            color: #fff;
            border-color: #0f172a;
        }

        .section-heading {
            font-size: 1.1rem;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            gap: .5rem;
        }

        .section-heading::before {
            content: '';
            width: 4px;
            height: 20px;
            background: #2563eb;
            border-radius: 2px;
            display: inline-block;
        }

        .book-grid {
            display: grid;
            grid-template-columns:repeat(auto-fill, minmax(140px, 1fr));
            gap: 1.25rem;
        }

        .book-card {
            background: #fff;
            border-radius: 1rem;
            border: 1.5px solid #e2e8f0;
            overflow: hidden;
            transition: .2s;
            cursor: pointer;
            text-decoration: none;
            color: inherit;
        }

        .book-card:hover {
            border-color: #2563eb;
            box-shadow: 0 4px 20px rgba(37, 99, 235, .1);
            transform: translateY(-2px);
        }

        .book-cover {
            width: 100%;
            aspect-ratio: 2/3;
            background: linear-gradient(135deg, #0f172a 0%, #1e3a5f 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 2rem;
        }

        .book-info {
            padding: .75rem;
        }

        .book-title {
            font-size: .8rem;
            font-weight: 700;
            color: #0f172a;
            line-height: 1.3;
            margin-bottom: .25rem;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .avail-badge {
            display: inline-block;
            margin-top: .4rem;
            font-size: .68rem;
            font-weight: 700;
            padding: .15rem .5rem;
            border-radius: 999px;
        }

        .avail-badge.ok {
            background: #dcfce7;
            color: #16a34a;
        }

        .avail-badge.out {
            background: #fee2e2;
            color: #dc2626;
        }

        /* ── Sidebar phải – Holds ── */
        .sidebar-right {
            background: #fff;
            border-left: 1px solid #e2e8f0;
            padding: 1.5rem 1rem;
            position: sticky;
            top: 56px;
            height: calc(100vh - 56px);
            overflow-y: auto;
        }

        .holds-title {
            font-size: .7rem;
            font-weight: 700;
            color: #94a3b8;
            letter-spacing: .08em;
            text-transform: uppercase;
            margin-bottom: 1rem;
        }

        .count-badge {
            background: #e2e8f0;
            color: #475569;
            font-size: .75rem;
            font-weight: 700;
            padding: .1rem .5rem;
            border-radius: 999px;
            margin-left: .4rem;
        }

        .hold-card {
            display: flex;
            gap: .75rem;
            align-items: flex-start;
            padding: .75rem;
            border-radius: .75rem;
            border: 1.5px solid #e2e8f0;
            margin-bottom: .75rem;
        }

        .hold-cover {
            width: 44px;
            height: 60px;
            border-radius: .4rem;
            flex-shrink: 0;
            background: linear-gradient(135deg, #0f172a, #1e3a5f);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: .9rem;
        }

        .hold-info .hold-name {
            font-size: .82rem;
            font-weight: 700;
            color: #0f172a;
            line-height: 1.3;
        }

        .hold-info .hold-due {
            font-size: .75rem;
            color: #94a3b8;
            margin-top: .25rem;
        }

        .hold-status {
            font-size: .68rem;
            font-weight: 700;
            padding: .15rem .5rem;
            border-radius: 999px;
            margin-top: .3rem;
            display: inline-block;
        }

        .hold-status.borrowing {
            background: #dbeafe;
            color: #1d4ed8;
        }

        .hold-status.overdue {
            background: #fee2e2;
            color: #dc2626;
        }

        .holds-empty {
            color: #94a3b8;
            font-size: .85rem;
            text-align: center;
            padding: 1.5rem 0;
        }

        /* ── Footer ── */
        .main-footer {
            background: #fff;
            border-top: 1px solid #e2e8f0;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .empty-state {
            text-align: center;
            padding: 4rem 1rem;
            color: #94a3b8;
        }

        .empty-state i {
            font-size: 3rem;
            margin-bottom: 1rem;
            display: block;
        }
    </style>
</head>
<body>

<%-- HEADER --%>
<%@ include file="../_header.jsp" %>

<%-- CART button trong nav – override thêm vào nav-right bằng cách nhúng thủ công --%>
<script>
    // Thêm cart icon vào nav-right sau khi DOM load
    document.querySelector('.nav-right').insertAdjacentHTML('afterbegin',
        '<a href="<%=request.getContextPath()%>/cart" class="cart-btn">' +
        '<i class="fa-solid fa-basket-shopping"></i> Giỏ' +
        '<% if(cartCount > 0) { %><span class="cart-badge"><%= cartCount %></span><% } %>' +
        '</a>');
</script>

<div class="main-layout">

    <%-- SIDEBAR TRÁI --%>
    <%@ include file="../_sidebar.jsp" %>

    <%-- CONTENT --%>
    <main class="content-area">
        <form method="get" action="<%=request.getContextPath()%>/home">

            <div class="search-bar">
                <i class="fa-solid fa-magnifying-glass"></i>
                <input type="text" name="search" placeholder="Tìm kiếm tên sách..." value="<%= search %>">
                <button type="submit">Tìm</button>
            </div>

            <div class="filter-bar">
                <select name="categoryId">
                    <option value="">-- Thể loại --</option>
                    <% for (Category c : categories) { %>
                    <option value="<%= c.getCategoryID() %>"
                            <%= categoryId.equals(String.valueOf(c.getCategoryID())) ? "selected" : "" %>>
                        <%= c.getCategoryName() %>
                    </option>
                    <% } %>
                </select>

                <select name="publisherId">
                    <option value="">-- Nhà xuất bản --</option>
                    <% for (Publisher p : publishers) { %>
                    <option value="<%= p.getPublisherID() %>"
                            <%= publisherId.equals(String.valueOf(p.getPublisherID())) ? "selected" : "" %>>
                        <%= p.getPublisherName() %>
                    </option>
                    <% } %>
                </select>

                <input type="hidden" name="letter" value="<%= letter %>">
                <button type="submit" class="btn-filter-apply">
                    <i class="fa-solid fa-filter me-1"></i>Lọc
                </button>
                <a href="<%=request.getContextPath()%>/home" class="btn-filter-reset">
                    <i class="fa-solid fa-rotate-left me-1"></i>Reset
                </a>
            </div>
        </form>

        <%-- A-Z --%>
        <div class="letter-filter">
            <a href="<%=request.getContextPath()%>/home?letter=ALL&search=<%= search %>&categoryId=<%= categoryId %>&publisherId=<%= publisherId %>"
               class="<%= "ALL".equals(letter) ? "active" : "" %>">ALL</a>
            <% for (char c = 'A'; c <= 'Z'; c++) { %>
            <a href="<%=request.getContextPath()%>/home?letter=<%= c %>&search=<%= search %>&categoryId=<%= categoryId %>&publisherId=<%= publisherId %>"
               class="<%= letter.equals(String.valueOf(c)) ? "active" : "" %>"><%= c %>
            </a>
            <% } %>
        </div>

        <div class="section-heading">
            <%= "ALL".equals(letter) ? "Tất cả sách" : "Sách bắt đầu bằng \"" + letter + "\"" %>
        </div>

        <% if (books == null || books.isEmpty()) { %>
        <div class="empty-state">
            <i class="fa-solid fa-book-open"></i>
            Không tìm thấy sách nào
        </div>
        <% } else { %>
        <div class="book-grid">
            <% for (Book b : books) { %>
            <a class="book-card" href="<%=request.getContextPath()%>/home/book?id=<%= b.getBookID() %>">
                <div class="book-cover">
                    <% if (b.getImageUrl() != null && !b.getImageUrl().isEmpty()) { %>
                    <img src="<%= b.getImageUrl() %>" alt="<%= b.getBookName() %>"
                         style="width:100%; height:100%; object-fit:cover;">
                    <% } else { %>
                    <i class="fa-solid fa-book"></i>
                    <% } %>
                </div>
                <div class="book-info">
                    <div class="book-title"><%= b.getBookName() %>
                    </div>
                    <span class="avail-badge <%= b.getAvailable() > 0 ? "ok" : "out" %>">
                        <%= b.getAvailable() > 0 ? b.getAvailable() + " còn" : "Hết sách" %>
                    </span>
                </div>
            </a>
            <% } %>
        </div>
        <% } %>
    </main>

    <%-- SIDEBAR PHẢI – HOLDS --%>
    <%-- Set redirect về home --%>
    <% request.setAttribute("cartRedirect", "/home"); %>
    <%@ include file="../_cartSidebar.jsp" %>
</div>

<%-- FOOTER --%>
<%@ include file="../_footer.jsp" %>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
