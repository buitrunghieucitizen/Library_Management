<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="pageTitle" value="Quản lý mượn trả" />
<c:set var="activeTab" value="borrows" />
<c:set var="pageStylesheet" value="borrow-list.css" />
<c:set var="pageScript" value="borrow-list.js" />
<%-- Keep this page in sync with the included UTF-8 borrow fragment. --%>
<%@ include file="../admin/layout/_admin_header.jsp" %>
<%@ include file="/WEB-INF/views/shared/borrow-list-content.jspf" %>
<%@ include file="../admin/layout/_admin_footer.jsp" %>
