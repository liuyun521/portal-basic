<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, com.bruce.util.*" %>
<%-- 导入 JSTL 常用标签 --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- 导入 Portal-Basic 标签 --%>
<%@ taglib prefix="p" uri="http://www.bruce.com/jsp/tags" %>

<%
/* 'basePath' 设置为 ${WebRoot} 根目录 */
StringBuilder sb =	new StringBuilder(request.getScheme())
					.append("://")
					.append(request.getServerName())
					.append(":")
					.append(request.getServerPort())
					.append(request.getContextPath())
					.append("/");

String basePath = sb.toString();
pageContext.setAttribute("basePath", basePath);
%>