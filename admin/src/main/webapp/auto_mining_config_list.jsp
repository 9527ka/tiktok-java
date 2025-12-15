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
	
		<input type="hidden" name="session_token" id="session_token" value="${session_token}">
		
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>矿池收益规则</h3>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminMiningConfigAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								<!-- <s:hidden name="online"></s:hidden> -->
								<input type="hidden" name="online" value="${online}">
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="name_para" name="name_para"
													cssClass="form-control " placeholder="用户名(钱包地址)、UID" /> -->
												<input id="name_para" name="name_para"
													class="form-control " placeholder="用户名(钱包地址)、UID" value="${name_para}" />
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>

							</form>
						</div>

					</div>
				</div>
			</div>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->

			<div class="row">
				<!-- 
				<div class="col-md-3">
					<div class="panel panel-default">
						<div class="panel-title">代理商层级</div>

						<div class="panel-body dataTables_wrapper">

							<div class="ibox float-e-margins">

								<div class="ibox-content">
									<div id="treeview4" class="test"></div>
								</div>
							</div>

						</div>

					</div>
				</div>
 -->
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						
						<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_MAINTAINER')
									 || security.isResourceAccessible('OP_MINING_CONFIG_OPERATE')}">
							
							<a href="<%=basePath%>normal/adminMiningConfigAction!toAdd.action" class="btn btn-light" style="margin-bottom: 10px">
								<i class="fa fa-pencil"></i>新增收益费率配置</a>
							
						</c:if>
						
						<div class="panel-body">

							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>用户名(钱包地址)</td>
										<td>UID</td>
										<td>推荐人</td>
										<td>账户类型</td>
										<td>收益费率</td>
										<td>上级返佣费率</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.username}</td>
											<td>${item.usercode}</td>
											<td>${item.username_parent}</td>
											<!--  <td><a href="#" onClick="getAssetsAll('<s:property value="id" />');">查看资产</a></td>-->
											<td>
												<c:if test="${item.rolename == 'GUEST'}">
													<span class="right label label-warning">演示账号</span>
												</c:if> 
												<c:if test="${item.rolename == 'MEMBER'}">
													<span class="right label label-success">正式账号</span>
												</c:if>
											</td>
											<td>${item.config}</td>
											<td>${item.config_recom}</td>
						
											<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_MAINTAINER')
														 || security.isResourceAccessible('OP_MINING_CONFIG_OPERATE')}">

												 <td>
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
															<li><a href="<%=basePath%>normal/adminMiningConfigAction!toUpdate.action?id=${item.id}">修改</a></li>
															<c:if test="${item.id != '2c948a827cccd850017cccde6f220001'}">
																<li><a href="javascript:delete_to('${item.id}')">删除</a></li>
															</c:if>
														</ul>
													</div>
												</td>
												
											</c:if>
											
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
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="modal_money" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content" style="height: 500px;">
				
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h4 class="modal-title" id="myModalLabel">钱包</h4>
					</div>
					
					<div class="modal-body" id="wallet_get" style="height: 370px;">
						<%@ include file="statistics_user_all_money.jsp"%>
					</div>
					
					<div class="modal-footer" style="margin-top: 0;">
						<button type="button" class="btn " data-dismiss="modal">关闭</button>
						<!-- <button id="sub" type="submit" class="btn btn-default" onclick="modalConfirm(this)">确认</button> -->
					</div>
					
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal -->
		</div>
	</div>
	
	<div class="form-group">
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="modal_asset" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content" style="height: 500px;">
				
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h4 class="modal-title" id="myModalLabel">总资产</h4>
					</div>
					
					<div class="modal-body" id="asset_get" style="height: 370px;">
						<%@ include file="statistics_user_all_asset.jsp"%>
					</div>
					
					<div class="modal-footer" style="margin-top: 0;">
						<button type="button" class="btn " data-dismiss="modal">关闭</button>
						<!-- <button id="sub" type="submit" class="btn btn-default" onclick="modalConfirm(this)">确认</button> -->
					</div>
					
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal -->
		</div>
	</div>
	<!-- 			</form> -->

	<div class="form-group">
		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="net_form" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
					
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">推荐网络</h4>
						</div>
						
						<div class="modal-body"
							style="max-height: 400px; overflow-y: scroll;">
							
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>层级</td>
										<td>用户名</td>
										<td>UID</td>
										<td>账户类型</td>
									</tr>
								</thead>
								<tbody id="modal_net_table">
									<%@ include file="include/loading.jsp"%>
								</tbody>
							</table>
							
						</div>
						
						<!-- <div class="modal-footer" style="margin-top: 0;">
								<button type="button" class="btn "
									data-dismiss="modal">关闭</button>
									<button id="sub" type="submit" class="btn btn-default" >保存</button>
																			
							</div> -->
							
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal -->
			</div>
		</div>
	</div>

	<!-- 模态框 -->
	<div class="form-group">
	
		<form
			action="<%=basePath%>normal/adminMiningConfigAction!toDelete.action"
			method="post" id="deleteForm">
			
			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
			<input type="hidden" name="id" id="id_delete" value="${id}">
			<input type="hidden" name="session_token" id="session_token_delete" value="${session_token}">
			
			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="modal_delete" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
						
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="myModalLabel">删除矿池收益规则</h4>
							</div>
							
							<div class="modal-header">
								<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
							</div>
							
							<div class="modal-body">
								<div class="">
									<input id="safeword" type="password" name="safeword"
										class="form-control" placeholder="请输入登录人资金密码">
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
		
	</div>

	<!-- 模态框 -->
	<div class="form-group">
	
		<form
			action="<%=basePath%>normal/adminActivityAction!toDelete.action" method="post" id="deleteForm">
			
			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
			<input type="hidden" name="id" id="id_delete" value="${id}">
			<input type="hidden" name="session_token" id="session_token_delete" value="${session_token}">
			
			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="modal_delete" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
						
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="myModalLabel">删除市场活动</h4>
							</div>
							
							<div class="modal-header">
								<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
							</div>
							
							<div class="modal-body">
								<div class="">
									<input id="login_safeword" type="password"
										name="login_safeword" class="form-control"
										placeholder="请输入登录人资金密码">
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
		
	</div>
	
	<%@ include file="include/js.jsp"%>
	
	<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
	
	<script>
		$(function() {
			/* var data = <s:property value="result" escapeHtml='false' />; */
			var data = ${result};
// 			console.log(data);
			$("#treeview4").treeview({
				color : "#428bca",
				enableLinks : true,
				nodeIcon : "glyphicon glyphicon-user",
				data : data,
				levels : 4,
			});
		});
	</script>

	<script type="text/javascript">
	 	function delete_to(id) {
			var session_token = $("#session_token").val();
			 $("#session_token_delete").val(session_token);
			$("#id_delete").val(id);
			$('#modal_delete').modal("show");

		}
	</script>

	<script type="text/javascript">		
		function resetGift(id) {
			var session_token = $("#session_token").val();
			 $("#session_token_reset").val(session_token);
			$("#id_reset_gift").val(id);
			$('#modal_reset_gift').modal("show");
		}
		function reset(id) {
			var session_token = $("#session_token").val();
			 $("#session_token_reset").val(session_token);
			$("#id_reset").val(id);
			$('#modal_reset').modal("show");

		}
		function resetWithdraw(id,withdraw_amount) {
			var session_token = $("#session_token").val();
			 $("#session_token_resetWithdraw").val(session_token);
			$("#withdraw_limit_amount").val(withdraw_amount);
			$("#bofore_withdraw_amount").val(withdraw_amount);
			
			$("#id_resetWithdraw").val(id);
			$('#modal_resetWithdraw').modal("show");
		}		
		function resetWithdrawZero(){
			swal({
				title : "是否确认修改?",
				text : "一键清零提现限制流水",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				var withdraw_amount = $("#bofore_withdraw_amount").val();
				var amount = 0 - withdraw_amount;
				$("#withdraw_limit_amount").val(amount);
				document.getElementById("resetWithdrawForm").submit();
			});			
		}
	</script>
	<script type="text/javascript">
		function setOnline(online) {
			document.getElementById("online").value = online;
			document.getElementById("queryForm").submit();
		}		
		
 		function goAjaxUrl(targetUrl,data){
			$.ajax({
				url:targetUrl,
				data:data,
				type:'get',
				success: function (res) {
//					    $(".loading").hide();
				    $("#wallet_get").html(res);
				  }
			});
		}
 		function getAssetsAll(id){
 			$("#modal_asset").modal("show");
 			var data = {"para_wallet_party_id":id};
 			goAjaxUrlAsset("<%=basePath%>normal/adminUserAllStatisticsAction!assetsAll.action",data);
 		} 		
 		function goAjaxUrlAsset(targetUrl,data){
 			$("#asset_get").html("数据加载中...");
			$.ajax({
				url:targetUrl,
				data:data,
				type:'get',
				success: function (res) {
//					    $(".loading").hide();
				    $("#asset_get").html(res);
				  }
			});
		} 
	</script>
	
	<script type="text/javascript">
		function pwdSendCode(){
			var url = "<%=basePath%>normal/adminEmailCodeAction!sendCode.action";
	 		var data = {"code_context":"resetUserPwd","isSuper":false};
	 		goNewAjaxUrl(url,data,function(tmp){
	 			var setInt = null;//定时器
	 			$("#pwd_email_code_button").attr("disabled","disabled");
	 			var timeout = 60;
	 			setInt = setInterval(function(){
	 				if(timeout<=0){
	 					clearInterval(setInt);
	 					timeout=60;
	 					$("#pwd_email_code_button").removeAttr("disabled");
	 					$("#pwd_email_code_button").html("获取验证码");
	 					return;
	 				}
	 				timeout--;
	 				$("#pwd_email_code_button").html("获取验证码  "+timeout);
	 			},1000);
	 		},function(){
	 		}); 
	 	}
		function safewordSendCode(){
			var url = "<%=basePath%>normal/adminEmailCodeAction!sendCode.action";
	 		var data = {"code_context":"resetUserSafeword","isSuper":false};
	 		goNewAjaxUrl(url,data,function(tmp){
	 			var setInt = null;//定时器
	 			$("#safe_email_code_button").attr("disabled","disabled");
	 			var timeout = 60;
	 			setInt = setInterval(function(){
	 				if(timeout<=0){
	 					clearInterval(setInt);
	 					timeout=60;
	 					$("#safe_email_code_button").removeAttr("disabled");
	 					$("#safe_email_code_button").html("获取验证码");
	 					return;
	 				}
	 				timeout--;
	 				$("#safe_email_code_button").html("获取验证码  "+timeout);
	 			},1000);
	 		},function(){
	 		}); 
	 	}
		function getParentsNet(id){
			$("#net_form").modal("show");			
			var url = "<%=basePath%>normal/adminUserAction!getParentsNet.action";
			var data = {
				"partyId" : id
			};
			goNewAjaxUrl(url, data, function(tmp) {
				var str = '';
				var content = '';
				for (var i = 0; i < tmp.user_parents_net.length; i++) {
					str += '<tr>' + '<td>' + (i + 1) + '</td>' + '<td>'
							+ tmp.user_parents_net[i].username + '</td>'
							+ '<td>' + tmp.user_parents_net[i].usercode
							+ '</td>' + '<td>'
							+ getRoleDom(tmp.user_parents_net[i].rolename)
							+ '</td>' + '</tr>';
				}
				$("#modal_net_table").html(str);

			}, function() {
				//				$("#coin_value").val(0);
			});
		}
		function getRoleDom(rolename) {
			if (rolename == "GUEST") {
				return '<span class="right label label-warning">演示账号</span>';
			} else if (rolename == "MEMBER") {
				return '<span class="right label label-success">正式账号</span>';
			} else if (rolename == "AGENT") {
				return '<span class="right label label-primary">代理商</span>';
			} else if (rolename == "TEST") {
				return '<span class="right label label-default">试用账号</span>';
			}
		}
		function goNewAjaxUrl(targetUrl, data, Func, Fail) {
			// 		console.log(data);
			$.ajax({
				url : targetUrl,
				data : data,
				type : 'get',
				dataType : "json",
				success : function(res) {
					var tmp = $.parseJSON(res)
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
						text : "",
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
