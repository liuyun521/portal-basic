<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*, com.bruce.util.*, com.bruce.util.http.HttpHelper, com.bruce.mvc.Action" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%@ taglib prefix="p" uri="http://www.bruce.com/jsp/tags" %><%

String __base = (String)request.getAttribute(Action.Constant.REQ_ATTR_BASE_PATH);

if(__base == null)
{
	__base = HttpHelper.getRequestBasePath(request);
	pageContext.setAttribute(Action.Constant.REQ_ATTR_BASE_PATH, __base);
}
%>