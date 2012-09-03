<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="base.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="${basePath}">

    <title><p:msg key="jsp-index.header"/></title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
  </head>

  <body>
  	<br>
    <p:msg key="jsp-index.sayhello" p0="${times}"/>
    <br>
    <br>
    <ol>
    	<li><a href="test/testBean_1.action"><p:msg key="jsp-index.TestBean" p0="1"/></a></li>
    	<li><a href="test/testBean_2.action"><p:msg key="jsp-index.TestBean" p0="2"/></a></li>
    	<li><a href="test/testBean_3.action"><p:msg key="jsp-index.TestBean" p0="3"/></a></li>
    	<li><a href="test/testValidate.action"><p:msg key="jsp-index.TestValidate"/></a></li>
    	<li><a href="test/testI18N.action"><p:msg key="jsp-index.TestI18N"/></a></li>
    	<li><a href="test/testUpload.action">测试文件上传</a></li>
    	<li><a href="test/testDownload.action">测试文件下载</a></li>
    	<li><a href="test/dao/testJdbc.action">测试 DAO (JDBC)</a></li>
    	<li><a href="test/dao/testMyBatis.action">测试 DAO (MyBatis)</a></li>
    	<li><a href="test/dao/testHibernate.action">测试 DAO (Hibernate)</a></li>
    	<!-- <li><a href="test/page/queryInterest.action">测试页面静态化</a></li> -->
    	<!-- <li><a href="page/interest-0-0.html">测试页面静态化</a></li> -->
    	<!-- <li><a href="
	    	<c:url value="/test/page/queryInterest.action">
	    		<c:param name="gender" value="0" />
	    		<c:param name="experience" value="0" />
	    	</c:url>
    	">测试页面静态化</a></li> -->
    	<li><a href="<c:url value="/test/page/queryInterest.action?gender=0&experience=0" />">测试页面静态化</a></li>
    </ol>
  </body>
</html>
