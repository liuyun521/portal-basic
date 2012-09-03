<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../../base.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="${basePath}">
    
    <title><p:msg key="jsp-set_locale.header"/></title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
  </head>
  
  <body>
  <br>
  <div align="right">
  	<a href="index.action"><p:msg key="jsp-set_locale.back-to-index"/></a>
  </div>
    <br><br><br><br>
    <div align="center">
    
    	<c:set var="en" value="english" />
    	<c:set var="cn" value="chinese" />

    	<c:url var="to_en" value="test/testI18N.action">
    		<c:param name="lan" value="${en}" />
    	</c:url>
    	
    	<c:url var="to_cn" value="test/testI18N.action">
    		<c:param name="lan" value="${cn}" />
    	</c:url>
    	
    	<a href="${to_en}"><p:msg key="jsp-set_locale.english"/></a>
    	&nbsp;&nbsp;&nbsp;&nbsp;
    	<a href="${to_cn}"><p:msg key="jsp-set_locale.chinese"/></a>
    	<br><br>
    	<p:msg key="jsp-set_locale.test" params="${__action.serial}"/>
    	
    </div>
  </body>
</html>
