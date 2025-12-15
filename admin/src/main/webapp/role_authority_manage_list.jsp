<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>

	<%@ include file="include/loading.jsp"%>
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>角色管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">
						<div class="panel-title">查询结果</div>
						
						<a
							href="<%=basePath%>normal/adminRoleAuthorityAction!toAdd.action?username_para=${username_para}"
							class="btn btn-light" style="margin-bottom: 10px"><i
							class="fa fa-pencil"></i>新增角色</a>
							
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>角色</td>
										<td>权限</td>
										<td width="150px"></td>
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="datas" status="stat"> -->
									<c:forEach items="${datas}" var="item" varStatus="stat">
										<tr>
											<td>${item.roleName}</td>
											<td>${item.names}</td>
											
											<td>
											
												<c:if test="${item.is_default_role == '0'}">
													<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_MAINTAINER')}">
											
														<div class="btn-group">
															<button type="button" class="btn btn-light">操作</button>
															<button type="button" class="btn btn-light dropdown-toggle"
																data-toggle="dropdown" aria-expanded="false">
																<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
															</button>
															<ul class="dropdown-menu" role="menu">
																
																<li><a href="javascript:updateResource('${item.id}')">配置权限</a></li>
																
																<li><a href="javascript:del('${item.id}')">删除</a></li>
		
															</ul>
														</div>
													
													</c:if>		
												</c:if>
											
												<c:if test="${item.is_default_role == '1'}">
													<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')}">
											
														<div class="btn-group">
															<button type="button" class="btn btn-light">操作</button>
															<button type="button" class="btn btn-light dropdown-toggle"
																data-toggle="dropdown" aria-expanded="false">
																<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
															</button>
															<ul class="dropdown-menu" role="menu">
																
																<li><a href="javascript:updateResource('${item.id}')">配置权限</a></li>
		
															</ul>
														</div>
													
													</c:if>		
												</c:if>
												
											</td>
											
										</tr>
									<!-- </s:iterator> -->
									</c:forEach>
								</tbody>
								
							</table>
							
							<%@ include file="include/page_simple.jsp"%>
							
							<!-- <nav> -->
						</div>

					</div>
					<!-- End Panel -->

				</div>
			</div>

		</div>
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->

		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	
	<div class="form-group">
	
		<form action="<%=basePath%>normal/adminRoleAuthorityAction!update.action"
			method="post" id="mainform">
			
			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"> 
			<input type="hidden" name="id" id="update_role_id" value="${id}" />
			
			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="resources_form" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
						
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="myModalLabel">配置权限</h4>
							</div>
							
							<div class="modal-body"
								style="max-height: 400px; overflow-y: scroll;">
								<table class="table table-bordered table-striped">
									<thead>
										<tr>
											<td>权限</td>
										</tr>
									</thead>
									<tbody id="modal_table">
										<%@ include file="include/loading.jsp"%>
									</tbody>
								</table>
							</div>
							
							<div class="col-sm-1 form-horizontal">
								<!-- 模态框（Modal） -->
								<div class="modal fade" id="modal_succeeded" tabindex="-1"
									role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
									<div class="modal-dialog">
										<div class="modal-content">
										
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal"
													aria-hidden="true">&times;</button>
												<h4 class="modal-title" id="myModalLabel">确认新增</h4>
											</div>
											
											<div class="modal-body">
											
												<div class="form-group">
													<label for="input002"
														class="col-sm-3 control-label form-label">登录人资金密码</label>
													<div class="col-sm-4">
														<input id="login_safeword" type="password"
															name="login_safeword" class="login_safeword"
															placeholder="请输入登录人资金密码">
													</div>
												</div>
												
												<!-- <div class="form-group" style="">
												
													<label for="input002" class="col-sm-3 control-label form-label">验证码</label>
													<div class="col-sm-4">
														<input id="email_code" type="text" name="email_code"
														class="login_safeword" placeholder="请输入验证码" >
													</div>
													<div class="col-sm-4">
														<a id="update_email_code_button" href="javascript:updateSendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
													</div>
												</div> -->
												
												<div class="form-group">
													<label for="input002"
														class="col-sm-3 control-label form-label">超级谷歌验证码</label>
													<div class="col-sm-4">
														<input id="super_google_auth_code"
															name="super_google_auth_code" placeholder="请输入超级谷歌验证码">
													</div>
												</div>
												
											</div>
											
											<div class="modal-footer" style="margin-top: 0;">
												<button type="button" class="btn " data-dismiss="modal">关闭</button>
												<button id="sub" type="submit" class="btn btn-default">确认</button>
											</div>
											
										</div>
										<!-- /.modal-content -->
									</div>
									<!-- /.modal -->
								</div>
							</div>
							
							<div class="modal-footer" style="margin-top: 0;">
								<button type="button" class="btn " data-dismiss="modal">关闭</button>
								<a href="javascript:submit()" class="btn btn-default">保存</a>
								<!-- <button id="sub" type="" class="btn btn-default"  onClick="submit();" >保存</button> -->
							</div>
							
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal -->
				</div>
			</div>
		</form>
		
	</div>

	<%@ include file="include/js.jsp"%>

	<script type="text/javascript">
		function update_value(code,snotes,svalue){
			document.getElementById("change_value").value = svalue;
			document.getElementById("titlediv").innerText = snotes;
			 $("#code").val(code);
			 
			$('#modal_set').modal("show");			 
		}
	</script>
	
	<form action="<%=basePath%>normal/adminRoleAuthorityAction!delete.action"
		method="post" id="deleteform">
		
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="id" id="delete_id" value="${id}"/>
		
		<div class="col-sm-1 form-horizontal">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_delete" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
					
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">确认删除</h4>
						</div>
						
						<div class="modal-body">
						
							<div class="form-group">
								<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
								<div class="col-sm-4">
									<input id="login_safeword" type="password"
										name="login_safeword" class="login_safeword"
										placeholder="请输入登录人资金密码">
								</div>
							</div>
							
							<!-- <div class="form-group" style="">
							
								<label for="input002" class="col-sm-3 control-label form-label">验证码</label>
								<div class="col-sm-4">
									<input id="email_code" type="text" name="email_code"
									class="login_safeword" placeholder="请输入验证码" >
								</div>
								<div class="col-sm-4">
									<a id="delete_email_code_button" href="javascript:deleteSendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
								</div>
							</div> -->
							
							<div class="form-group">
								<label for="input002" class="col-sm-3 control-label form-label">超级谷歌验证码</label>
								<div class="col-sm-4">
									<input id="super_google_auth_code"
										name="super_google_auth_code" placeholder="请输入超级谷歌验证码">
								</div>
							</div>
							
						</div>
						
						<div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn " data-dismiss="modal">关闭</button>
							<button id="sub" type="submit" class="btn btn-default">确认</button>
						</div>
						
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal -->
			</div>
		</div>
	</form>

	<script>
		function del(id) {
			$("#delete_id").val(id);
			$('#modal_delete').modal("show");	
		}
	</script>
	
	<script type="text/javascript">
		function updateResource(id){
			$("#resources_form").modal("show");
			$("#update_role_id").val(id);
			
			var url = "<%=basePath%>normal/adminRoleAuthorityAction!resources.action";
			var data = {"id":id};
			goAjaxUrl(url,data,function(tmp){

				var str='';
				var content='';
				/* tmp.all_resources */
				console.log(tmp);
				for(var i=0;i<tmp.all_resources.length;i++){
					
					
					content = '<div class="checkbox checkbox-success checkbox-inline">'
		                	+ '<input type="checkbox" id="inlineCheckbox12_'+tmp.all_resources[i].set_id+'" value="'+tmp.all_resources[i].set_id+'" name="role_resource" >'
		                	+ '<label for="inlineCheckbox12_'+tmp.all_resources[i].set_id+'">'+tmp.all_resources[i].name+'</label>'
		            		+ '</div>';
					str += '<tr>'
						+'<td>'+content+'</td>'
						+'</tr>';
				}
				$("#modal_table").html(str);
				
				$.each(tmp.checked_resources.split(","),function(index,value){//默认选中
					$("#inlineCheckbox12_"+value).prop("checked","checked");
				});
			},function(){
	// 			$("#coin_value").val(0);
			});
		}
		function goAjaxUrl(targetUrl,data,Func,Fail){
			console.log(data);
			$.ajax({
				url:targetUrl,
				data:data,
				type : 'get',
				dataType : "json",
				success: function (res) {
// 					var tmp = $.parseJSON(res)
					var tmp = res;
					console.log(tmp);
				    if(tmp.code==200){
				    	Func(tmp);
				    }else if(tmp.code==500){
				    	Fail();
				    	swal({
							title : tmp.message,
							text : "",
							type : "warning",
							showCancelButton : true,
							confirmButtonColor : "#DD6B55",
							confirmButtonText : "确认",
							closeOnConfirm : false
						});
				    }
				  },
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						console.log("请求错误");
					}
			});
		}
	</script>
	
	<script type="text/javascript">
		function submit() {
			$('#modal_succeeded').modal("show");
		}
	</script>
	
	<script type="text/javascript">
		var setInt = null;//定时器		
		clearInterval(setInt);
		
		function updateSendCode(){
			var url = "<%=basePath%>normal/adminEmailCodeAction!sendCode.action";
	 		var data = {"code_context":"updateRoleResource","isSuper":true};
	 		goAjaxUrl(url,data,function(tmp){
 			
	 			$("#update_email_code_button").attr("disabled","disabled");
	 			var timeout = 60;
	 			setInt = setInterval(function(){
		 				if(timeout<=0){
		 					clearInterval(setInt);
		 					timeout=60;
		 					$("#update_email_code_button").removeAttr("disabled");
		 					$("#update_email_code_button").html("获取超级签验证码");
		 					return;
		 				}
		 				timeout--;
		 				$("#update_email_code_button").html("获取超级签验证码  "+timeout);
	 				},1000);
		 		},function(){
	 		}); 
	 	}
		function deleteSendCode(){
			var url = "<%=basePath%>normal/adminEmailCodeAction!sendCode.action";
			var data = {
				"code_context" : "deleteRole",
				"isSuper" : true
			};
			goAjaxUrl(url, data,
					function(tmp) {

						$("#delete_email_code_button").attr("disabled", "disabled");
						var timeout = 60;
						setInt = setInterval(
								function() {
									if (timeout <= 0) {
										clearInterval(setInt);
										timeout = 60;
										$("#delete_email_code_button").removeAttr("disabled");
										$("#delete_email_code_button").html("获取超级签验证码");
										return;
									}
									timeout--;
									$("#delete_email_code_button").html("获取超级签验证码  " + timeout);
								}, 1000);
					}, function() {
					});
		}
		function goAjaxUrl(targetUrl, data, Func, Fail) {
			// 		console.log(data);
			$.ajax({
				url : targetUrl,
				data : data,
				type : 'get',
				dataType : "json",
				success : function(res) {
// 					var tmp = $.parseJSON(res)
					var tmp = res;
					console.log(tmp);
					if (tmp.code == 200) {
						Func(tmp);
					} else if (tmp.code == 500) {
						Fail();
						swal({
							title : tmp.message,
							text : "",
							type : "warning",
							showCancelButton : true,
							confirmButtonColor : "#DD6B55",
							confirmButtonText : "确认",
							closeOnConfirm : false
						});
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					swal({
						title : "请求错误",
						text : "请检查管理员邮箱是否配置",
						type : "warning",
						showCancelButton : true,
						confirmButtonColor : "#DD6B55",
						confirmButtonText : "确认",
						closeOnConfirm : false
					});
					console.log("请求错误");
				}
			});
		}
	</script>
	
</body>

</html>
