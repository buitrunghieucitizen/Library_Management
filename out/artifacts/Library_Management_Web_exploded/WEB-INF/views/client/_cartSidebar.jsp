<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 03/03/2026
  Time: 5:29 SA
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="java.util.List, entities.Book" %>
<%
    int _borrowCount = request.getAttribute("borrowCount") != null ? (int) request.getAttribute("borrowCount") : 0;
    int _buyCount = request.getAttribute("buyCount") != null ? (int) request.getAttribute("buyCount") : 0;
    List<Book> _borrowBooks = (List<Book>) request.getAttribute("borrowBooks");
    List<Book> _buyBooks = (List<Book>) request.getAttribute("buyBooks");
    String _redirect = request.getAttribute("cartRedirect") != null ? (String) request.getAttribute("cartRedirect") : "/home";
    String _cartError = (String) session.getAttribute("cartError");
    String _checkoutOk = (String) session.getAttribute("checkoutSuccess");
    if (_cartError != null) session.removeAttribute("cartError");
    if (_checkoutOk != null) session.removeAttribute("checkoutSuccess");
%>
<aside style="background:#fff; border-left:1px solid #e2e8f0; padding:1.5rem 1rem;
              position:sticky; top:56px; height:calc(100vh - 56px); overflow-y:auto;
              display:flex; flex-direction:column;">

    <div style="font-size:.7rem; font-weight:700; color:#94a3b8;
                letter-spacing:.08em; text-transform:uppercase; margin-bottom:1rem;">
        <i class="fa-solid fa-basket-shopping me-1"></i>Giỏ hàng
    </div>

    <%-- Toast success --%>
    <% if (_checkoutOk != null) { %>
    <div style="background:#f0fdf4; color:#16a34a; padding:.75rem; border-radius:.6rem;
                border:1px solid #bbf7d0; font-size:.82rem; font-weight:600; margin-bottom:1rem;
                display:flex; align-items:center; gap:.5rem;">
        <i class="fa-solid fa-circle-check"></i> <%= _checkoutOk %>
    </div>
    <% } %>

    <%-- Toast error --%>
    <% if (_cartError != null) { %>
    <div style="background:#fff1f2; color:#e11d48; padding:.75rem; border-radius:.6rem;
                border:1px solid #ffe4e6; font-size:.82rem; font-weight:600; margin-bottom:1rem;
                display:flex; align-items:center; gap:.5rem;">
        <i class="fa-solid fa-circle-exclamation"></i> <%= _cartError %>
    </div>
    <% } %>

    <%-- Giỏ MƯỢN --%>
    <div style="font-size:.75rem; font-weight:700; color:#0f172a; margin-bottom:.5rem;">
        <i class="fa-solid fa-bookmark me-1" style="color:#2563eb;"></i>Mượn
        <span style="background:#e2e8f0; color:#475569; font-size:.7rem; font-weight:700;
                     padding:.1rem .45rem; border-radius:999px; margin-left:.3rem;">
            <%= _borrowCount %>/3
        </span>
    </div>
    <% if (_borrowBooks != null && !_borrowBooks.isEmpty()) {
        for (Book _cb : _borrowBooks) { %>
    <div style="display:flex; gap:.6rem; align-items:center; padding:.6rem;
                border-radius:.7rem; border:1.5px solid #e2e8f0; margin-bottom:.5rem;">
        <div style="width:32px; height:44px; border-radius:.3rem; flex-shrink:0; overflow:hidden;
                    background:linear-gradient(135deg,#0f172a,#1e3a5f);
                    display:flex; align-items:center; justify-content:center; color:#fff; font-size:.7rem;">
            <% if (_cb.getImageUrl() != null && !_cb.getImageUrl().isEmpty()) { %>
            <img src="<%= _cb.getImageUrl() %>" style="width:100%;height:100%;object-fit:cover;" alt="">
            <% } else { %><i class="fa-solid fa-book"></i><% } %>
        </div>
        <span style="font-size:.78rem; font-weight:700; color:#0f172a; line-height:1.3; flex:1;
                     display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden;">
            <%= _cb.getBookName() %>
        </span>
        <form method="post" action="<%=request.getContextPath()%>/cart" style="margin:0;">
            <input type="hidden" name="bookId" value="<%= _cb.getBookID() %>">
            <input type="hidden" name="type" value="borrow">
            <input type="hidden" name="action" value="remove">
            <input type="hidden" name="redirect" value="<%= _redirect %>">
            <button type="submit"
                    style="background:none;border:none;color:#94a3b8;cursor:pointer;padding:0;font-size:.8rem;">
                <i class="fa-solid fa-xmark"></i>
            </button>
        </form>
    </div>
    <% }
    } else { %>
    <div style="color:#94a3b8; font-size:.8rem; text-align:center; padding:.5rem 0 .75rem;">
        <i class="fa-solid fa-bookmark d-block mb-1"></i>Giỏ mượn trống
    </div>
    <% } %>

    <div style="border-top:1px solid #e2e8f0; margin:.75rem 0;"></div>

    <%-- Giỏ MUA --%>
    <div style="font-size:.75rem; font-weight:700; color:#0f172a; margin-bottom:.5rem;">
        <i class="fa-solid fa-cart-shopping me-1" style="color:#2563eb;"></i>Mua
        <span style="background:#e2e8f0; color:#475569; font-size:.7rem; font-weight:700;
                     padding:.1rem .45rem; border-radius:999px; margin-left:.3rem;">
            <%= _buyCount %>
        </span>
    </div>
    <% if (_buyBooks != null && !_buyBooks.isEmpty()) {
        for (Book _cb : _buyBooks) { %>
    <div style="display:flex; gap:.6rem; align-items:center; padding:.6rem;
                border-radius:.7rem; border:1.5px solid #e2e8f0; margin-bottom:.5rem;">
        <div style="width:32px; height:44px; border-radius:.3rem; flex-shrink:0; overflow:hidden;
                    background:linear-gradient(135deg,#0f172a,#1e3a5f);
                    display:flex; align-items:center; justify-content:center; color:#fff; font-size:.7rem;">
            <% if (_cb.getImageUrl() != null && !_cb.getImageUrl().isEmpty()) { %>
            <img src="<%= _cb.getImageUrl() %>" style="width:100%;height:100%;object-fit:cover;" alt="">
            <% } else { %><i class="fa-solid fa-book"></i><% } %>
        </div>
        <span style="font-size:.78rem; font-weight:700; color:#0f172a; line-height:1.3; flex:1;
                     display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden;">
            <%= _cb.getBookName() %>
        </span>
        <form method="post" action="<%=request.getContextPath()%>/cart" style="margin:0;">
            <input type="hidden" name="bookId" value="<%= _cb.getBookID() %>">
            <input type="hidden" name="type" value="buy">
            <input type="hidden" name="action" value="remove">
            <input type="hidden" name="redirect" value="<%= _redirect %>">
            <button type="submit"
                    style="background:none;border:none;color:#94a3b8;cursor:pointer;padding:0;font-size:.8rem;">
                <i class="fa-solid fa-xmark"></i>
            </button>
        </form>
    </div>
    <% }
    } else { %>
    <div style="color:#94a3b8; font-size:.8rem; text-align:center; padding:.5rem 0;">
        <i class="fa-solid fa-cart-shopping d-block mb-1"></i>Giỏ mua trống
    </div>
    <% } %>

    <%-- Checkout --%>
    <div style="margin-top:auto; padding-top:1rem; display:flex; flex-direction:column; gap:.5rem;">
        <% if (_borrowCount > 0) { %>
        <form method="post" action="<%=request.getContextPath()%>/cart/checkout">
            <input type="hidden" name="type" value="borrow">
            <button type="submit" style="width:100%; height:44px; border:none; border-radius:.75rem;
                    background:#0f172a; color:#fff; font-weight:700; font-size:.85rem; cursor:pointer;
                    display:flex; align-items:center; justify-content:center; gap:.4rem;">
                <i class="fa-solid fa-bookmark"></i>Xác nhận mượn (<%= _borrowCount %>)
            </button>
        </form>
        <% } %>
        <% if (_buyCount > 0) { %>
        <form method="post" action="<%=request.getContextPath()%>/cart/checkout">
            <input type="hidden" name="type" value="buy">
            <button type="submit" style="width:100%; height:44px; border:none; border-radius:.75rem;
                    background:#16a34a; color:#fff; font-weight:700; font-size:.85rem; cursor:pointer;
                    display:flex; align-items:center; justify-content:center; gap:.4rem;">
                <i class="fa-solid fa-cart-shopping"></i>Xác nhận mua (<%= _buyCount %>)
            </button>
        </form>
        <% } %>
        <% if (_borrowCount == 0 && _buyCount == 0) { %>
        <button disabled style="width:100%; height:44px; border:none; border-radius:.75rem;
                background:#e2e8f0; color:#94a3b8; font-weight:700; font-size:.85rem;">
            Giỏ hàng trống
        </button>
        <% } %>
    </div>
</aside>