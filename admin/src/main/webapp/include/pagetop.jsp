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


	// 商品库/营销链接(dmUrl/adminUrl)跟随当前请求协议, 与其它相对路径菜单一致.
	// 不再用 AutoConfig 硬编码的 https —— admin 隔离端口 1188 是 http-only, 硬编码 https 会握手失败打不开.
	String _scheme = request.getHeader("X-Forwarded-Proto");
	if (_scheme == null || _scheme.trim().length() == 0) { _scheme = request.getScheme(); }
	String _host = request.getHeader("HOST");
	String adminUrl;
	String dmUrl;
	if (_host == null || _host.length() == 0 || _host.contains("localhost") || _host.contains("127.0.0.1")) {
		adminUrl = AutoConfig.getAdminUrl();
		dmUrl = AutoConfig.getBaseUrl();
	} else {
		dmUrl = _scheme + "://" + _host;
		adminUrl = dmUrl + "/admin";
	}


%>
