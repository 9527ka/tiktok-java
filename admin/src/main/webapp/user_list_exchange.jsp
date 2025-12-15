<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>

	<%@ include file="include/loading.jsp"%>

	<div class="ifr-dody">
	
        <input type="hidden" name="session_token" id="session_token" value="${session_token}"/>
        
		<div class="ifr-con">
			<h3>交易所_用户管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
								<form class="form-horizontal"
									action="<%=basePath%>normal/exchangeAdminUserAction!list.action"
									method="post" id="queryForm">
									
									<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
									
									<div class="col-md-12 col-lg-4">
										<fieldset>
											<div class="control-group">
												<div class="controls">
													<input id="name_para" name="name_para"
													class="form-control " placeholder="用户名(钱包地址)、UID" value="${name_para}"/>
												</div>
											</div>
										</fieldset>
									</div>

									<div class="col-md-12 col-lg-3">
										<fieldset>
											<div class="control-group">
												<div class="controls">
													<select id="rolename_para" name="rolename_para" class="form-control " >
													    <option value="">所有账号</option>
														<option value="MEMBER" <c:if test="${rolename_para == 'MEMBER'}">selected="true"</c:if> >正式账号</option>
														<option value="GUEST" <c:if test="${rolename_para == 'GUEST'}">selected="true"</c:if> >演示账号</option>
													</select>	
												</div>
											</div>
										</fieldset>
									</div>

									<div class="col-md-12 col-lg-2"  >
										<button type="submit" class="btn btn-light btn-block">查询</button>
									</div>
									
								</form>
						</div>

					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>

						<div class="panel-body">

							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>用户名</td>
										<td>UID</td>
										<td>账户类型</td>
										<td>USDT账户余额</td>
										<td>资产</td>
										<td>提现限制流水</td> 
										<td>用户当前流水</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>
											<c:choose>
												<c:when test="${item.username!='' && item.username!=null}">
													<a style="font-size: 10px;" href="#" onClick="getParentsNet('${item.id}')">
														${fn:substring(item.username,0,4)}***${fn:substring(item.username,fn:length(item.username) - 4, fn:length(item.username))}
													</a>
												</c:when>
												<c:otherwise>
													${item.username}
												</c:otherwise>
											</c:choose>
											</td>
											<td>${item.usercode}</td>
											<td>
												<c:choose>
													<c:when test="${item.rolename=='GUEST'}">
														<span class="right label label-warning">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='MEMBER'}">
														<span class="right label label-success">${item.roleNameDesc}</span>
													</c:when>
													<c:otherwise>
													 	${item.roleNameDesc}
													</c:otherwise>	
												</c:choose>
											</td>											
											<td><a href="#" onClick="getAllMoney('${item.id}');"><fmt:formatNumber value="${item.money}" pattern="#0.00" /></a></td>
											<td><a href="#" onClick="getAssetsAll('${item.id}');">查看资产</a></td>
											<td><fmt:formatNumber value="${item.withdraw_limit_amount}" pattern="#0.00" /></td>	
											<td><fmt:formatNumber value="${item.userdata_turnover}" pattern="#0.00" /></td>

											<td>
																									
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN') || security.isResourceAccessible('OP_EXCHANGE_USER_OPERATE')}">
																	 
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">

															<li><a href="javascript:reset('${item.id}')">修改账户余额</a></li>
															<li><a href="javascript:resetWithdraw('${item.id}','${item.withdraw_limit_amount}')">修改提现限制流水</a></li>
															<li><a href="javascript:resetpsw('${item.id}')">重置登录密码</a></li>
															<li><a href="javascript:resetgoogle('${item.id}')">解绑谷歌验证器</a></li>
															<li><a href="javascript:resetsafepsw('${item.id}')">重置资金密码</a></li>
															<li><a href="javascript:resetlogin('${item.id}')">强制用户退出登录状态</a></li>
														</ul>
													</div>
													
												</c:if>
												
											</td>
											
										</tr>
									</c:forEach>
								</tbody>
								
							</table>
							
							<%@ include file="include/page_simple.jsp"%>
							
							<!-- <nav> -->
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<!-- 交易所修改账户余额 模态框 -->
		<div class="form-group">
			<form action="<%=basePath%>normal/exchangeAdminUserAction!reset_exchange.action"
				method="post" id="resetForm">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="id" id="id_reset" value="${id}">
				<input type="hidden" name="name_para" id="name_para" value="${name_para}">
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}">
				<input type="hidden" name="session_token" id="session_token_reset" value="${session_token}">
				
				<div class="col-sm-1">
					<div class="modal fade" id="modal_reset" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">修改账户余额</h4>
								</div>
								
								<div class="modal-body">
									<div class="">
									<span class="help-block">账变金额(不能小于0)</span>
										<input id="money_revise" name="money_revise" class="form-control"/>
									</div>
								</div>
								
								 <div class="modal-body">
									<div class="">
										<span class="help-block">账变类型</span>
										<select id="reset_type" name="reset_type" class="form-control">
										   <option value="">账变类型</option>
										   <option value="recharge">平台充值金额(正式用户记录报表)</option>
										   <option value="withdraw">平台扣除金额(不记录报表)</option>
										</select>
									</div>
								</div>
								
								<div class="modal-body">
									<div class="">
										<span class="help-block">账变币种</span>
										<select id="reset_type" name="coin_type" class="form-control">
											   <option value="">账变币种</option>
											   <option value="usdt">USDT</option>
											   <option value="btc">BTC</option>
											   <option value="eth">ETH</option>
										</select>			
									</div>
								</div>
								
								<div class="modal-header">
									<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
								</div>							
								<div class="modal-body">
									<div class="">
										<input id="login_safeword" type="password" name="login_safeword"
											class="form-control" placeholder="请输入登录人资金密码">
									</div>								
								</div>
								
								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
									<button id="sub" type="submit" class="btn btn-default" >确认</button>
								</div>
								
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
	
		<!-- 修改提现限制流水 模态框 -->
		<div class="form-group">		
			<form action="<%=basePath%>normal/exchangeAdminUserAction!resetWithdraw.action" method="post" id="resetWithdrawForm">
			
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_resetWithdraw" value="${id}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>
				<input type="hidden" name="session_token" id="session_token_resetWithdraw" value="${session_token}"/>
				<input type="hidden" name="bofore_withdraw_amount" id="bofore_withdraw_amount" value="${bofore_withdraw_amount}"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_resetWithdraw" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">修改提现限制流水</h4>
								</div>
								
								<div class="modal-body">
									<div class="">
										<input id="withdraw_limit_amount" name="money_withdraw" class="form-control"/>
										<span class="help-block">增加清输入正数，减少请输入负数</span>
									</div>
								</div>								
							
								<div class="modal-footer" style="margin-top: 0;">								
									<button onclick="resetWithdrawZero()" type="button" class="btn btn-default" data-dismiss="modal">一键清0</button>
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
									<button id="sub" type="submit" class="btn btn-default" >确认</button>
								</div>
								
							</div>
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
				
			</form>
			
		</div>
	
	    <!-- 重置登录密码  -->
		<div class="form-group">
			<form action="<%=basePath%>normal/exchangeAdminUserAction!resetpsw.action"
				method="post" id="succeededForm" class="form-horizontal">
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_resetpsw" value="${id}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>
				<div class="col-sm-1">
					<div class="modal fade" id="modal_set" tabindex="-1" role="dialog"
						aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">重置用户登录密码</h4>
								</div>
								
								<div class="modal-body">
								
									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">用户重置密码</label>
										<div class="col-sm-4">
											<input id="password" type="password" name="password" placeholder="请输入用户重置密码" >
										</div>
									</div>
									
									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
										<div class="col-sm-4">
											<input id="login_safeword" type="password" name="login_safeword"
												class="login_safeword" placeholder="请输入登录人资金密码" >
										</div>
									</div>

									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>
										<div class="col-sm-4">
											<input id="google_auth_code"  name="google_auth_code" placeholder="请输入谷歌验证码" >
										</div>
									</div>
									
								</div>
								
								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
									<button id="sub" type="submit" class="btn btn-default" >确认</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>	
		
		<!-- 解绑谷歌验证器 -->
		<div class="form-group">
			<form action="<%=basePath%>normal/exchangeAdminUserAction!resetGoogleAuth.action"
				method="post" id="succeededForm" class="form-horizontal">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_resetgoogle" value="${id}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_google" tabindex="-1" role="dialog"
						aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">解绑用户谷歌验证器</h4>
								</div>
								
								<div class="modal-body">
									
									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
										<div class="col-sm-4">
											<input id="login_safeword" type="password" name="login_safeword"
												class="login_safeword" placeholder="请输入登录人资金密码" >
										</div>
									</div>

									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">登录人谷歌验证码</label>
										<div class="col-sm-4">
											<input id="google_auth_code"  name="google_auth_code" placeholder="请输入谷歌验证码" >
										</div>
									</div>
								</div>
								
								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
									<button id="sub" type="submit" class="btn btn-default" >确认</button>
								</div>
								
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		
		<!-- 重置资金密码 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/exchangeAdminUserAction!resetsafepsw.action"
				method="post" id="succeededForm" class="form-horizontal">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_resetsafepsw" value="${id}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_safepasw_set" tabindex="-1" role="dialog"
						aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">重置资金密码</h4>
								</div>
								
								<div class="modal-body">
								
									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">用户重置资金密码</label>
										<div class="col-sm-4">
											<input id="safeword" type="password" name="safeword" placeholder="请输入用户重置资金密码" >
										</div>
									</div>
									
									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
										<div class="col-sm-4">
											<input id="login_safeword" type="password" name="login_safeword"
												class="login_safeword" placeholder="请输入登录人资金密码" >
										</div>
									</div>

									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>
										<div class="col-sm-4">
											<input id="google_auth_code"  name="google_auth_code" placeholder="请输入谷歌验证码" >
										</div>
									</div>
									
								</div>
								
								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
									<button id="sub" type="submit" class="btn btn-default" >确认</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		
		<!-- 强制用户退出登录状态 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/exchangeAdminUserAction!resetUserLoginState.action"
				method="post" id="succeededForm" class="form-horizontal">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_resetlogin" value="${id}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_login_state" tabindex="-1" role="dialog"
						aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">强制用户退出登录状态</h4>
								</div>
								
								<div class="modal-body">
									
									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
										<div class="col-sm-4">
											<input id="login_safeword" type="password" name="login_safeword"
												class="login_safeword" placeholder="请输入登录人资金密码" >
										</div>
									</div>
	
									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">登录人谷歌验证码</label>
										<div class="col-sm-4">
											<input id="google_auth_code"  name="google_auth_code" placeholder="请输入谷歌验证码" >
										</div>
									</div>
									
								</div>
								
								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
									<button id="sub" type="submit" class="btn btn-default" >确认</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		
		<%@ include file="include/footer.jsp"%>

	</div>
	
	<div class="form-group">
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="modal_money" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content" style="height:500px;">
				
					 <div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h4 class="modal-title" id="myModalLabel">钱包</h4>
					</div>
					
					<div class="modal-body" id="wallet_get" style="height:370px;">
						<%@ include file="statistics_user_all_money.jsp"%>
					</div>
					
					 <div class="modal-footer" style="margin-top: 0;">
						<button type="button" class="btn " data-dismiss="modal" >关闭</button>
					</div> 
					
				</div>
			</div>
		</div>
	</div>
	
	<div class="form-group">
		<div class="modal fade" id="modal_asset" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content" style="height:500px;">
				
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h4 class="modal-title" id="myModalLabel">总资产</h4>
					</div>
					
					<div class="modal-body" id="asset_get" style="height:370px;">
						<%@ include file="statistics_user_all_asset.jsp"%>
					</div>
					
					 <div class="modal-footer" style="margin-top: 0;">
						<button type="button" class="btn " data-dismiss="modal" >关闭</button>
					</div> 
					
				</div>
			</div>
		</div>
	</div>
	
	<div class="form-group">
		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="net_form" tabindex="-1"
				role="dialog" aria-labelledby="myModalLabel"
				aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
					
						<div class="modal-header">
							<button type="button" class="close"
								data-dismiss="modal" aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">推荐网络</h4>
						</div>
						
						<div class="modal-body" style="max-height: 400px;overflow-y: scroll;">
							<table class="table table-bordered table-striped" >
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
						
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<%@ include file="include/js.jsp"%>
	
	<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
	
	<script>
		$(function() {
			var data = ${result};
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
		
		function resetpsw(id) {
			$("#id_resetpsw").val(id);
			$('#modal_set').modal("show");
		}
		
		function resetgoogle(id) {
			$("#id_resetgoogle").val(id);
			$('#modal_google').modal("show");
		}
		
		function resetsafepsw(id) {
			$("#id_resetsafepsw").val(id);
			$('#modal_safepasw_set').modal("show");
		}
		function resetlogin(id) {
			$("#id_resetlogin").val(id);
			$('#modal_login_state').modal("show");
		}
	</script>

	<script type="text/javascript">		
		function getAllMoney(id){
 			$("#modal_money").modal("show");
 			var data = {"para_wallet_party_id":id};
 			goAjaxUrl("<%=basePath%>normal/adminUserAllStatisticsAction!walletExtendsAll.action",data);
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
		function getParentsNet(id){
			$("#net_form").modal("show");		
			var url = "<%=basePath%>normal/adminUserAction!getParentsNet.action";
			var data = {"partyId":id};
			goNewAjaxUrl(url,data,function(tmp){
				var str='';
				var content='';
				for(var i=0;i<tmp.user_parents_net.length;i++){
					str += '<tr>'
						+'<td>'+(i+1)+'</td>'
						+'<td>'+tmp.user_parents_net[i].username+'</td>'
						+'<td>'+tmp.user_parents_net[i].usercode+'</td>'
						+'<td>'+getRoleDom(tmp.user_parents_net[i].rolename)+'</td>'
						+'</tr>';
				}
				$("#modal_net_table").html(str);			
			},function(){
	//				$("#coin_value").val(0);
			});
		}
		function getRoleDom(rolename){
			if(rolename=="GUEST"){
				return '<span class="right label label-warning">演示账号</span>';
			}else if(rolename=="MEMBER"){
				return '<span class="right label label-success">正式账号</span>';
			}else if(rolename=="AGENT"){
				return '<span class="right label label-primary">代理商</span>';
			}else if(rolename=="TEST"){
				return '<span class="right label label-default">试用账号</span>';
			}
		}
		function goNewAjaxUrl(targetUrl,data,Func,Fail){
			$.ajax({
				url:targetUrl,
				data:data,
				type : 'get',
				dataType : "json",
				success: function (res) {
					// var tmp = $.parseJSON(res)
					var tmp = res;
					// console.log(tmp);
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
