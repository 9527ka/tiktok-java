<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page contentType="text/html; charset=UTF-8" import="java.util.ResourceBundle" %>
<%@ page contentType="text/html; charset=UTF-8" import="java.util.ResourceBundle" %>
<%@ page contentType="text/html; charset=UTF-8" import="project.mall.auto.AutoConfig" %>


<%
String path = request.getContextPath();
//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
//String basePath = "https://"+request.getServerName()+":443"+path+"/";
String basePath = path+"/";

//String base = "http://" + request.getServerName() + ":"+request.getServerPort()+"/";
String base = basePath;
String bases = "https://"+request.getServerName()+path+"/";
//    String username = SecurityAppUserHolder.gettUsername();


	String adminUrl = AutoConfig.getAdminUrl();
	String dmUrl =  AutoConfig.getBaseUrl();


%>
