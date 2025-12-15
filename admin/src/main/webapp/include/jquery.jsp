<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
String path = request.getContextPath();
//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String basePath = "https://"+request.getServerName()+":443"+path+"/";
%>
%
<script type="text/javascript">
	//显示加载进度
	var loading_id;
	function loading(){
		loading_id=layer.load(1,{
	    shade: [0.1,'#222'] //0.1透明度的白色背景
		});   
	}
	function loading_close(){
		layer.close(loading_id);
	}
	var subForm=document.getElementById('mainForm');
	function del(url){
	 	confirmF(url,"你确定要删除吗？");
	}
	function confirmF(url,msg){
		parent.layer.confirm(msg, {icon: 3}, function(index){
			parent.layer.close(index);
	        subForm.action=url;
	        subForm.submit();
	    });
	}
</script>

<script type="text/javascript">
	var index = parent.layer.getFrameIndex(window.name); //获取当前窗体索引 
	$('#layer_close_button').on('click', function(){ 
	
	    parent.layer.close(index); //执行关闭 
	
	});
</script>
