<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 03/03/2026
  Time: 1:34 SA
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="entities.Staff, java.util.List, java.util.Map" %>
<%
    Staff _staff = (Staff) session.getAttribute("staff");
    Map<Integer,Integer> _cart = (Map<Integer,Integer>) session.getAttribute("cart");
    int _cartCount = _cart != null ? _cart.size() : 0;
%>
<nav class="top-nav">
    <a href="<%=request.getContextPath()%>/home" class="brand">
        <i class="fa-solid fa-swatchbook"></i>
        Count's Library
    </a>
    <div class="nav-right">
        <% if (_staff != null) { %>

        <%-- Cart button — chỉ hiện khi có sách --%>
        <% if (_cartCount > 0) { %>
        <a href="<%=request.getContextPath()%>/home" class="cart-btn">
            <i class="fa-solid fa-basket-shopping"></i> Giỏ mượn
            <span class="cart-badge"><%= _cartCount %></span>
        </a>
        <% } %>

        <div class="user-info">
            <div class="user-avatar">
                <%= _staff.getStaffName().substring(0,1).toUpperCase() %>
            </div>
            <span class="user-name"><%= _staff.getStaffName() %></span>
        </div>
        <a href="<%=request.getContextPath()%>/LogoutURL" class="btn-logout">
            <i class="fa-solid fa-right-from-bracket me-1"></i>Đăng xuất
        </a>

        <% } else { %>
        <a href="<%=request.getContextPath()%>/LoginURL" class="btn-login">
            <i class="fa-solid fa-right-to-bracket me-1"></i>Đăng nhập
        </a>
        <% } %>
    </div>
</nav>