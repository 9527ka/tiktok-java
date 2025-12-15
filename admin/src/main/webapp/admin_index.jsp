<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ include file="include/basePath.jsp"%>
<%@ page language="java" import="security.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Language" content="zh-CN">
    <meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=gb2312">
    <meta http-equiv="refresh" content="0.1;url=<%=basePath%>web/loginSuccess">
    <title></title>
</head>
<body>
<script type="text/javascript">
    location.replace("normal/LoginSuccessAction!view.action?v=ag");
</script>
</body>
</html>