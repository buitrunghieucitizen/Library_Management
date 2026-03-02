<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 03/03/2026
  Time: 1:37 SA
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="java.util.List, java.util.Map, entities.*" %>
<%
    Book book = (Book) request.getAttribute("book");
    List<String> authors = (List<String>) request.getAttribute("authors");
    Category category = (Category) request.getAttribute("category");
    Publisher publisher = (Publisher) request.getAttribute("publisher");

    Map<Integer, Integer> _borrowCart = (Map<Integer, Integer>) session.getAttribute("borrowCart");
    Map<Integer, Integer> _buyCart = (Map<Integer, Integer>) session.getAttribute("buyCart");
    if (_borrowCart == null) _borrowCart = new java.util.LinkedHashMap<>();
    if (_buyCart == null) _buyCart = new java.util.LinkedHashMap<>();

    boolean inBorrow = _borrowCart.containsKey(book.getBookID());
    boolean inBuy = _buyCart.containsKey(book.getBookID());
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Library | <%= book.getBookName() %>
    </title>
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

        /* copy lại các style chung từ studentHome.jsp (top-nav, sidebar-left, footer, divider, section-title, nav-item...) */
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

        .btn-logout {
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

        .main-layout {
            display: grid;
            grid-template-columns:220px 1fr 320px;
            min-height: calc(100vh - 56px - 40px);
        }

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

        .nav-item a:hover {
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

        /* ── Detail content ── */
        .content-area {
            padding: 2rem;
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            gap: .5rem;
            color: #475569;
            text-decoration: none;
            font-weight: 600;
            font-size: .9rem;
            margin-bottom: 1.5rem;
        }

        .back-link:hover {
            color: #2563eb;
        }

        .detail-card {
            background: #fff;
            border-radius: 1.25rem;
            border: 1.5px solid #e2e8f0;
            padding: 2rem;
            display: flex;
            gap: 2rem;
        }

        .detail-cover {
            width: 240px;
            min-width: 240px;
            aspect-ratio: 2/3;
            border-radius: .85rem;
            background: linear-gradient(135deg, #0f172a 0%, #1e3a5f 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 3.5rem;
        }

        .detail-info {
            flex: 1;
        }

        .detail-info h1 {
            font-size: 1.5rem;
            font-weight: 700;
            margin-bottom: .5rem;
        }

        .detail-authors {
            color: #2563eb;
            font-size: .9rem;
            font-weight: 600;
            margin-bottom: 1.25rem;
        }

        .meta-grid {
            display: grid;
            grid-template-columns:1fr 1fr;
            gap: .75rem;
            margin-bottom: 1.5rem;
        }

        .meta-item {
            background: #f8fafc;
            border-radius: .6rem;
            padding: .65rem .85rem;
        }

        .meta-label {
            font-size: .7rem;
            font-weight: 700;
            color: #94a3b8;
            text-transform: uppercase;
            letter-spacing: .06em;
            margin-bottom: .2rem;
        }

        .meta-value {
            font-size: .9rem;
            font-weight: 600;
            color: #0f172a;
        }

        .avail-large {
            display: inline-flex;
            align-items: center;
            gap: .4rem;
            padding: .4rem 1rem;
            border-radius: 999px;
            font-size: .9rem;
            font-weight: 700;
            margin-bottom: 1.5rem;
        }

        .avail-large.ok {
            background: #dcfce7;
            color: #16a34a;
        }

        .avail-large.out {
            background: #fee2e2;
            color: #dc2626;
        }

        .description-box {
            margin-top: 1.25rem;
        }

        .description-box h3 {
            font-size: .85rem;
            font-weight: 700;
            color: #94a3b8;
            text-transform: uppercase;
            letter-spacing: .06em;
            margin-bottom: .5rem;
        }

        .description-box p {
            font-size: .9rem;
            color: #475569;
            line-height: 1.7;
        }

        /* ── Cart sidebar ── */
        .sidebar-right {
            background: #fff;
            border-left: 1px solid #e2e8f0;
            padding: 1.5rem 1rem;
            position: sticky;
            top: 56px;
            height: calc(100vh - 56px);
            overflow-y: auto;
            display: flex;
            flex-direction: column;
        }

        .cart-title {
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

        .cart-item {
            display: flex;
            gap: .6rem;
            align-items: flex-start;
            padding: .65rem;
            border-radius: .7rem;
            border: 1.5px solid #e2e8f0;
            margin-bottom: .6rem;
        }

        .cart-item-cover {
            width: 36px;
            height: 48px;
            border-radius: .35rem;
            flex-shrink: 0;
            background: linear-gradient(135deg, #0f172a, #1e3a5f);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: .75rem;
        }

        .cart-item-name {
            font-size: .8rem;
            font-weight: 700;
            color: #0f172a;
            line-height: 1.3;
            flex: 1;
        }

        .cart-item-remove {
            background: none;
            border: none;
            color: #94a3b8;
            cursor: pointer;
            font-size: .75rem;
            padding: 0;
        }

        .cart-item-remove:hover {
            color: #dc2626;
        }

        .btn-add-cart {
            width: 100%;
            height: 48px;
            border: none;
            border-radius: .75rem;
            font-weight: 700;
            font-size: .9rem;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: .5rem;
            margin-bottom: .75rem;
        }

        .btn-add-cart.add {
            background: #0f172a;
            color: #fff;
        }

        .btn-add-cart.add:hover {
            background: #1e293b;
        }

        .btn-add-cart.remove {
            background: #fee2e2;
            color: #dc2626;
        }

        .btn-add-cart.remove:hover {
            background: #fecaca;
        }

        .btn-checkout {
            width: 100%;
            height: 48px;
            background: #2563eb;
            color: #fff;
            border: none;
            border-radius: .75rem;
            font-weight: 700;
            font-size: .9rem;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: .5rem;
        }

        .btn-checkout:hover {
            background: #1d4ed8;
        }

        .btn-checkout:disabled {
            background: #e2e8f0;
            color: #94a3b8;
            cursor: not-allowed;
        }

        .cart-empty {
            color: #94a3b8;
            font-size: .85rem;
            text-align: center;
            padding: 2rem 0;
            flex: 1;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
        }

        .main-footer {
            background: #fff;
            border-top: 1px solid #e2e8f0;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
    </style>
</head>
<body>

<%-- HEADER --%>
<%@ include file="../_header.jsp" %>

<div class="main-layout">

    <%-- SIDEBAR TRÁI --%>
    <%@ include file="../_sidebar.jsp" %>

    <%-- CONTENT --%>
    <main class="content-area">
        <a href="<%=request.getContextPath()%>/home" class="back-link">
            <i class="fa-solid fa-arrow-left"></i> Quay lại Collections
        </a>

        <div class="detail-card">
            <div class="detail-cover">
                <% if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) { %>
                <img src="<%= book.getImageUrl() %>" alt="<%= book.getBookName() %>"
                     style="width:100%; height:100%; object-fit:cover; border-radius:.85rem;">
                <% } else { %>
                <i class="fa-solid fa-book"></i>
                <% } %>
            </div>

            <div class="detail-info">
                <h1><%= book.getBookName() %>
                </h1>
                <div class="detail-authors">
                    <i class="fa-solid fa-pen-nib me-1"></i>
                    <%
                        if (authors != null && !authors.isEmpty()) {
                            out.print(String.join(", ", authors));
                        } else {
                            out.print("Không rõ tác giả");
                        }
                    %>
                </div>

                <div class="meta-grid">
                    <div class="meta-item">
                        <div class="meta-label">Thể loại</div>
                        <div class="meta-value">
                            <%= category != null ? category.getCategoryName() : "—" %>
                        </div>
                    </div>
                    <div class="meta-item">
                        <div class="meta-label">Nhà xuất bản</div>
                        <div class="meta-value">
                            <%= publisher != null ? publisher.getPublisherName() : "—" %>
                        </div>
                    </div>
                    <div class="meta-item">
                        <div class="meta-label">Tổng số</div>
                        <div class="meta-value"><%= book.getQuantity() %> cuốn</div>
                    </div>
                    <div class="meta-item">
                        <div class="meta-label">Còn lại</div>
                        <div class="meta-value"><%= book.getAvailable() %> cuốn</div>
                    </div>
                </div>

                <span class="avail-large <%= book.getAvailable() > 0 ? "ok" : "out" %>">
                    <i class="fa-solid <%= book.getAvailable() > 0 ? "fa-circle-check" : "fa-circle-xmark" %>"></i>
                    <%= book.getAvailable() > 0 ? "Còn sách có thể mượn" : "Hết sách" %>
                </span>

                <%-- Nút add/remove ngay trong content --%>
                <% if (book.getAvailable() > 0) { %>
                <div style="display:flex; gap:.75rem; margin-bottom:1.5rem;">
                    <form method="post" action="<%=request.getContextPath()%>/cart">
                        <input type="hidden" name="bookId" value="<%= book.getBookID() %>">
                        <input type="hidden" name="type" value="borrow">
                        <input type="hidden" name="action" value="<%= inBorrow ? "remove" : "add" %>">
                        <input type="hidden" name="redirect" value="/home/book?id=<%= book.getBookID() %>">
                        <button type="submit" style="height:40px; padding:0 1.25rem; border:none; border-radius:.65rem;
                                cursor:pointer; font-weight:700; font-size:.85rem; display:flex; align-items:center; gap:.4rem;
                                background:<%= inBorrow ? "#fee2e2" : "#0f172a" %>;
                                color:<%= inBorrow ? "#dc2626" : "#fff" %>;">
                            <i class="fa-solid <%= inBorrow ? "fa-minus" : "fa-bookmark" %>"></i>
                            <%= inBorrow ? "Bỏ mượn" : "Mượn" %>
                        </button>
                    </form>
                    <form method="post" action="<%=request.getContextPath()%>/cart">
                        <input type="hidden" name="bookId" value="<%= book.getBookID() %>">
                        <input type="hidden" name="type" value="buy">
                        <input type="hidden" name="action" value="<%= inBuy ? "remove" : "add" %>">
                        <input type="hidden" name="redirect" value="/home/book?id=<%= book.getBookID() %>">
                        <button type="submit" style="height:40px; padding:0 1.25rem; border:none; border-radius:.65rem;
                                cursor:pointer; font-weight:700; font-size:.85rem; display:flex; align-items:center; gap:.4rem;
                                background:<%= inBuy ? "#fee2e2" : "#2563eb" %>;
                                color:<%= inBuy ? "#dc2626" : "#fff" %>;">
                            <i class="fa-solid <%= inBuy ? "fa-minus" : "fa-cart-shopping" %>"></i>
                            <%= inBuy ? "Bỏ mua" : "Mua" %>
                        </button>
                    </form>
                </div>
                <% } %>

                <div class="description-box">
                    <h3>Mô tả</h3>
                    <p>Chưa có mô tả cho cuốn sách này.</p>
                </div>
            </div>
        </div>
    </main>

    <%-- SIDEBAR PHẢI – CART / WISHLIST --%>
    <% request.setAttribute("cartRedirect", "/home/book?id=" + book.getBookID()); %>
    <%@ include file="../_cartSidebar.jsp" %>

</div>

<%-- FOOTER --%>
<%@ include file="../_footer.jsp" %>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
