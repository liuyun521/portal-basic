<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*, com.bruce.util.*" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ taglib prefix="p" uri="http://www.bruce.com/jsp/tags" %><%

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