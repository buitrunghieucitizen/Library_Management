<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Entities.Staff" %>
<%
    Staff currentStaff = (Staff) session.getAttribute("staff");
%>
<nav class="top-nav">
    <a href="<%=request.getContextPath()%>/home" class="brand">
        <span class="brand-mark">LM</span>
        Cổng thư viện
    </a>
    <div class="nav-right">
        <% if (currentStaff != null) { %>
            <div class="user-chip">
                <span class="user-avatar"><%= currentStaff.getStaffName().substring(0, 1).toUpperCase() %></span>
                <span class="user-name"><%= currentStaff.getStaffName() %></span>
            </div>
            <a href="<%=request.getContextPath()%>/logout" class="nav-button">Đăng xuất</a>
        <% } %>
    </div>
</nav>


