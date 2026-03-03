<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
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
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Portal</title>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { box-sizing: border-box; }
        body {
            margin: 0;
            font-family: 'Plus Jakarta Sans', sans-serif;
            background: #f8fafc;
            color: #0f172a;
        }
        .top-nav {
            height: 64px;
            background: #0f172a;
            color: #fff;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 28px;
            position: sticky;
            top: 0;
            z-index: 20;
        }
        .brand {
            color: #fff;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 12px;
            font-weight: 700;
        }
        .brand-mark {
            width: 36px;
            height: 36px;
            border-radius: 12px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            background: #2563eb;
        }
        .nav-right {
            display: flex;
            align-items: center;
            gap: 12px;
        }
        .user-chip {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            color: #e2e8f0;
        }
        .user-avatar {
            width: 34px;
            height: 34px;
            border-radius: 999px;
            background: #1d4ed8;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
        }
        .nav-button {
            color: #fff;
            text-decoration: none;
            background: rgba(255,255,255,0.12);
            padding: 10px 14px;
            border-radius: 12px;
        }
        .layout {
            display: grid;
            grid-template-columns: 240px 1fr 320px;
            min-height: calc(100vh - 64px);
        }
        .sidebar-left, .sidebar-right {
            background: #fff;
            border-color: #e2e8f0;
        }
        .sidebar-left {
            border-right: 1px solid #e2e8f0;
            padding: 24px 16px;
        }
        .sidebar-right {
            border-left: 1px solid #e2e8f0;
            padding: 24px 16px;
        }
        .section-title {
            font-size: 12px;
            font-weight: 700;
            color: #94a3b8;
            text-transform: uppercase;
            letter-spacing: 0.08em;
            margin-bottom: 12px;
        }
        .nav-item a {
            display: block;
            padding: 12px 14px;
            border-radius: 12px;
            text-decoration: none;
            color: #334155;
            font-weight: 600;
            margin-bottom: 8px;
        }
        .nav-item a:hover, .nav-item a.active {
            background: #eff6ff;
            color: #2563eb;
        }
        .divider {
            border-top: 1px solid #e2e8f0;
            margin: 16px 0;
        }
        .content {
            padding: 28px;
        }
        .hero {
            background: linear-gradient(135deg, #0f172a, #1d4ed8);
            color: #fff;
            padding: 24px;
            border-radius: 24px;
            margin-bottom: 24px;
        }
        .hero h1 {
            margin: 0 0 8px;
            font-size: 28px;
        }
        .hero p {
            margin: 0;
            color: #dbeafe;
            max-width: 720px;
            line-height: 1.7;
        }
        .search-form {
            background: #fff;
            border: 1px solid #e2e8f0;
            border-radius: 24px;
            padding: 18px;
            margin-bottom: 20px;
        }
        .search-row {
            display: grid;
            grid-template-columns: 1.2fr repeat(4, minmax(0, 1fr));
            gap: 12px;
            margin-bottom: 14px;
        }
        .search-row input, .search-row select {
            width: 100%;
            height: 48px;
            border: 1px solid #dbe4f0;
            border-radius: 14px;
            padding: 0 14px;
            font: inherit;
            background: #f8fafc;
        }
        .search-row button, .search-row a {
            height: 48px;
            border-radius: 14px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            font-weight: 700;
        }
        .btn-apply {
            border: none;
            background: #0f172a;
            color: #fff;
            width: 100%;
        }
        .btn-reset {
            background: #eef2ff;
            color: #1d4ed8;
        }
        .letter-strip {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
        }
        .letter-strip a {
            width: 36px;
            height: 36px;
            border-radius: 10px;
            background: #fff;
            border: 1px solid #dbe4f0;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            color: #334155;
            font-size: 13px;
            font-weight: 700;
        }
        .letter-strip a.active {
            background: #0f172a;
            border-color: #0f172a;
            color: #fff;
        }
        .section-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin: 22px 0 16px;
        }
        .section-header h2 {
            margin: 0;
            font-size: 20px;
        }
        .book-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
            gap: 16px;
        }
        .book-card {
            background: #fff;
            border: 1px solid #e2e8f0;
            border-radius: 22px;
            overflow: hidden;
            text-decoration: none;
            color: inherit;
            transition: transform 0.18s ease, box-shadow 0.18s ease;
        }
        .book-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 18px 34px rgba(15, 23, 42, 0.08);
        }
        .book-visual {
            aspect-ratio: 4 / 5;
            background: linear-gradient(135deg, #1e293b, #2563eb);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 34px;
            font-weight: 700;
        }
        .book-meta {
            padding: 16px;
        }
        .book-title {
            margin: 0 0 8px;
            font-size: 15px;
            line-height: 1.45;
            min-height: 44px;
        }
        .pill {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 700;
            padding: 6px 10px;
        }
        .pill.ok {
            background: #dcfce7;
            color: #166534;
        }
        .pill.out {
            background: #fee2e2;
            color: #991b1b;
        }
        .hold-card {
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 18px;
            padding: 14px;
            margin-bottom: 12px;
        }
        .hold-card h3 {
            margin: 0 0 8px;
            font-size: 14px;
        }
        .hold-meta {
            font-size: 13px;
            color: #475569;
            line-height: 1.6;
        }
        .hold-status {
            display: inline-flex;
            margin-top: 10px;
            padding: 6px 10px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 700;
        }
        .hold-status.borrowing {
            background: #dbeafe;
            color: #1d4ed8;
        }
        .hold-status.overdue {
            background: #fee2e2;
            color: #b91c1c;
        }
        .empty-box {
            background: #fff;
            border: 1px dashed #cbd5e1;
            border-radius: 18px;
            padding: 28px 20px;
            text-align: center;
            color: #64748b;
        }
        .main-footer {
            background: #fff;
            border-top: 1px solid #e2e8f0;
            padding: 12px 18px;
            text-align: center;
            color: #64748b;
            font-size: 12px;
        }
        @media (max-width: 1200px) {
            .layout {
                grid-template-columns: 220px 1fr;
            }
            .sidebar-right {
                display: none;
            }
            .search-row {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }
        }
        @media (max-width: 768px) {
            .layout {
                grid-template-columns: 1fr;
            }
            .sidebar-left {
                display: none;
            }
            .content {
                padding: 18px;
            }
            .search-row {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <%@ include file="../_header.jsp" %>

    <div class="layout">
        <%@ include file="../_sidebar.jsp" %>

        <main class="content">
            <section class="hero">
                <h1>Student Library Portal</h1>
                <p>Student view from Huynh branch has been integrated here and adapted to the current borrow and order flow on master.</p>
            </section>

            <form class="search-form" method="get" action="<%=request.getContextPath()%>/home">
                <div class="search-row">
                    <input type="text" name="search" placeholder="Tim theo ten sach" value="<%= search %>">
                    <input type="text" name="author" placeholder="Tac gia" value="<%= author %>">
                    <select name="categoryId">
                        <option value="">Tat ca the loai</option>
                        <% for (Category category : categories) { %>
                            <option value="<%= category.getCategoryID() %>" <%= categoryId.equals(String.valueOf(category.getCategoryID())) ? "selected" : "" %>>
                                <%= category.getCategoryName() %>
                            </option>
                        <% } %>
                    </select>
                    <select name="publisherId">
                        <option value="">Tat ca nha xuat ban</option>
                        <% for (Publisher publisher : publishers) { %>
                            <option value="<%= publisher.getPublisherID() %>" <%= publisherId.equals(String.valueOf(publisher.getPublisherID())) ? "selected" : "" %>>
                                <%= publisher.getPublisherName() %>
                            </option>
                        <% } %>
                    </select>
                    <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;">
                        <button class="btn-apply" type="submit">Loc</button>
                        <a class="btn-reset" href="<%=request.getContextPath()%>/home">Reset</a>
                    </div>
                </div>

                <div class="letter-strip">
                    <a href="<%=request.getContextPath()%>/home?letter=ALL&search=<%= search %>&author=<%= author %>&categoryId=<%= categoryId %>&publisherId=<%= publisherId %>"
                       class="<%= "ALL".equalsIgnoreCase(letter) ? "active" : "" %>">All</a>
                    <% for (char c = 'A'; c <= 'Z'; c++) { %>
                        <a href="<%=request.getContextPath()%>/home?letter=<%= c %>&search=<%= search %>&author=<%= author %>&categoryId=<%= categoryId %>&publisherId=<%= publisherId %>"
                           class="<%= letter.equalsIgnoreCase(String.valueOf(c)) ? "active" : "" %>"><%= c %></a>
                    <% } %>
                </div>
            </form>

            <div class="section-header">
                <h2><%= "ALL".equalsIgnoreCase(letter) ? "Book collection" : "Books starting with " + letter %></h2>
                <a href="<%=request.getContextPath()%>/borrows?action=list" style="text-decoration:none;font-weight:700;color:#2563eb;">Open borrow center</a>
            </div>

            <% if (books == null || books.isEmpty()) { %>
                <div class="empty-box">Khong tim thay sach phu hop voi bo loc hien tai.</div>
            <% } else { %>
                <div class="book-grid">
                    <% for (Book book : books) { %>
                        <a class="book-card" href="<%=request.getContextPath()%>/home/book?id=<%= book.getBookID() %>">
                            <div class="book-visual">
                                <% if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) { %>
                                    <img src="<%= book.getImageUrl() %>" alt="<%= book.getBookName() %>" style="width:100%;height:100%;object-fit:cover;">
                                <% } else { %>
                                    <span><%= book.getBookName().substring(0, 1).toUpperCase() %></span>
                                <% } %>
                            </div>
                            <div class="book-meta">
                                <h3 class="book-title"><%= book.getBookName() %></h3>
                                <span class="pill <%= book.getAvailable() > 0 ? "ok" : "out" %>">
                                    <%= book.getAvailable() > 0 ? (book.getAvailable() + " available") : "Out of stock" %>
                                </span>
                            </div>
                        </a>
                    <% } %>
                </div>
            <% } %>
        </main>

        <aside class="sidebar-right">
            <div class="section-title">Current borrows</div>
            <% if (holds == null || holds.isEmpty()) { %>
                <div class="empty-box">Khong co sach dang muon.</div>
            <% } else { %>
                <% for (Borrow hold : holds) { %>
                    <div class="hold-card">
                        <h3>Borrow #<%= hold.getBorrowID() %></h3>
                        <div class="hold-meta">
                            Borrow date: <%= hold.getBorrowDate() %><br>
                            Due date: <%= hold.getDueDate() %>
                        </div>
                        <span class="hold-status <%= "Overdue".equalsIgnoreCase(hold.getStatus()) ? "overdue" : "borrowing" %>">
                            <%= hold.getStatus() %>
                        </span>
                    </div>
                <% } %>
            <% } %>
        </aside>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>
