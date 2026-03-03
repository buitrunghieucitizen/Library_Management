<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
            grid-template-columns: 240px 1fr;
            min-height: calc(100vh - 64px);
        }
        .sidebar-left {
            border-right: 1px solid #e2e8f0;
            background: #fff;
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
            padding: 32px;
        }
        .back-link {
            text-decoration: none;
            color: #2563eb;
            font-weight: 700;
        }
        .detail-card {
            margin-top: 18px;
            background: #fff;
            border: 1px solid #e2e8f0;
            border-radius: 28px;
            padding: 28px;
            display: grid;
            grid-template-columns: 300px 1fr;
            gap: 26px;
        }
        .book-cover {
            background: linear-gradient(135deg, #1e293b, #2563eb);
            border-radius: 22px;
            aspect-ratio: 4 / 5;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
            color: #fff;
            font-size: 72px;
            font-weight: 700;
        }
        .book-cover img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .meta-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 14px;
            margin: 18px 0;
        }
        .meta-card {
            background: #f8fafc;
            border-radius: 18px;
            padding: 14px;
        }
        .meta-card span {
            display: block;
            color: #64748b;
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.08em;
            margin-bottom: 6px;
        }
        .meta-card strong {
            font-size: 15px;
        }
        .status-pill {
            display: inline-flex;
            padding: 8px 12px;
            border-radius: 999px;
            font-size: 13px;
            font-weight: 700;
        }
        .status-pill.ok {
            background: #dcfce7;
            color: #166534;
        }
        .status-pill.out {
            background: #fee2e2;
            color: #991b1b;
        }
        .action-row {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            margin-top: 20px;
        }
        .action-row form {
            margin: 0;
        }
        .action-row button, .action-row a {
            height: 48px;
            padding: 0 18px;
            border-radius: 14px;
            font-weight: 700;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }
        .btn-primary {
            border: none;
            background: #0f172a;
            color: #fff;
        }
        .btn-secondary {
            border: none;
            background: #2563eb;
            color: #fff;
        }
        .btn-ghost {
            background: #eef2ff;
            color: #1d4ed8;
        }
        .main-footer {
            background: #fff;
            border-top: 1px solid #e2e8f0;
            padding: 12px 18px;
            text-align: center;
            color: #64748b;
            font-size: 12px;
        }
        @media (max-width: 960px) {
            .layout {
                grid-template-columns: 1fr;
            }
            .sidebar-left {
                display: none;
            }
            .detail-card {
                grid-template-columns: 1fr;
            }
            .content {
                padding: 18px;
            }
        }
    </style>
</head>
<body>
    <%@ include file="../_header.jsp" %>

    <div class="layout">
        <%@ include file="../_sidebar.jsp" %>

        <main class="content">
            <a class="back-link" href="<%=request.getContextPath()%>/home">Back to student home</a>

            <section class="detail-card">
                <div class="book-cover">
                    <% if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) { %>
                        <img src="<%= book.getImageUrl() %>" alt="<%= book.getBookName() %>">
                    <% } else { %>
                        <span><%= book.getBookName().substring(0, 1).toUpperCase() %></span>
                    <% } %>
                </div>

                <div>
                    <h1 style="margin:0 0 10px;font-size:32px;"><%= book.getBookName() %></h1>
                    <p style="margin:0;color:#2563eb;font-weight:700;">
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
                            <input type="hidden" name="action" value="buy">
                            <input type="hidden" name="bookID" value="<%= book.getBookID() %>">
                            <button class="btn-secondary" type="submit">Dat mua</button>
                        </form>
                        <a class="btn-ghost" href="<%=request.getContextPath()%>/borrows?action=list">Mo borrow center</a>
                    </div>

                    <div style="margin-top:24px;background:#f8fafc;border-radius:18px;padding:18px;line-height:1.7;color:#475569;">
                        Book detail view from Huynh branch has been adapted to the current master flow. Borrow actions still go through the existing master controller so student requests stay compatible with the current database and admin approval screens.
                    </div>
                </div>
            </section>
        </main>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>
