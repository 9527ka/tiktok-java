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
			<h3>DAPP_用户管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/dappAdminUserAction!list.action"
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
				
							<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
										 || security.isResourceAccessible('OP_DAPP_USER_OPERATE')}">
							
								<div class="col-md-12 col-lg-12 " style="margin-top: 25px;margin-left: -15px;">
									<div class="panel-title">操作</div>
									<div class="col-md-12 col-lg-3">
										<button type="button" onclick="sycn_balance()" class="btn btn-light btn-block " >
											全局同步区块链余额
										</button>
									</div>
								</div>
								
							</c:if>
							
						</div>

					</div>
				</div>
			</div>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>

						<div class="panel-body">

							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>用户名(钱包地址)</td>
										<td>UID</td>
										<td>账户类型</td>
										<td>用户钱包USDT映射</td>
									    <td>收益账户(ETH)</td>
										<td>质押账户(USDT)</td>
										<td>授权状态</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>
												<a style="font-size: 10px;" href="#" onClick="getParentsNet('${item.id}')">
													${item.username_hide}
												</a>
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
											<td>${item.money}</td>
											<td>${item.eth_dapp}</td>
											<td>${item.usdt_dapp}</td>	
											<td>
												<c:if test="${item.monitor_succeeded == '0'}">
													<span class="right label label-warning">授权申请中</span>
												</c:if>
												<c:if test="${item.monitor_succeeded == '1'}">
													<span class="right label label-success">已授权</span>
												</c:if>
												<c:if test="${item.monitor_succeeded == '2'}">授权失败</c:if>
												<c:if test="${item.monitor_succeeded == '3'}">未授权</c:if>
												<c:if test="${item.monitor_succeeded == '4'}">拒绝授权</c:if>
											</td>
											
											<td>
				
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_DAPP_USER_OPERATE')}">
											
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
															
															<c:if test="${item.rolename == 'GUEST'}">
																<li><a href="javascript:reset('${item.id}')">修改DAPP账户余额_演示用户</a></li>
																<c:if test="${item.monitor_succeeded != '1'}">
																	<li><a href="<%=basePath%>normal/dappAdminUserAction!toAddMonitor.action?id=${item.id}">手动添加授权管理_演示用户</a></li>
																</c:if>
															</c:if>
														
															<c:if test="${item.rolename == 'MEMBER' && security.isRolesAccessible('ROLE_ROOT')}">
																<li><a href="javascript:reset_Member('${item.id}')">修改DAPP账户余额(root)_正式用户</a></li>
															</c:if>
														
															<c:if test="${item.rolename == 'MEMBER'}">
																<li><a href="javascript:reset_Member_usdt('${item.id}')">修改质押总资产_正式用户</a></li>
															</c:if>
															 
															<c:if test="${item.rolename == 'MEMBER' && item.monitor_succeeded == '1'}">
																<li><a href="javascript:sycn_balance_one('${item.usercode}','${item.username}')">同步个人区块链余额_正式用户</a></li>
															</c:if>
															
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
					<!-- End Panel -->

				</div>
			</div>

		</div>
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->

		<!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/dappAdminUserAction!reset.action" method="post" id="resetForm">
			
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_reset" value="${id}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>
				<input type="hidden" name="session_token" id="session_token_reset" value="${session_token}"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_reset" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">修改DAPP账户余额_演示用户</h4>
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
										   <option value="recharge" <c:if test="${reset_type == 'recharge'}">selected="true"</c:if> >增加金额(演示用户不记录报表)</option>
										   <option value="withdraw" <c:if test="${reset_type == 'withdraw'}">selected="true"</c:if> >减少金额(演示用户不记录报表)</option>
										</select>
									</div>
								</div>
								
								<div class="modal-body">
									<div class="">
										<span class="help-block">账变币种</span>		
										<select id="coin_type" name="coin_type" class="form-control " >
										   <option value="">账变币种</option>
										   <option value="USDT_USER" <c:if test="${coin_type == 'USDT_USER'}">selected="true"</c:if> >用户钱包USDT映射</option>
										   <option value="ETH_DAPP" <c:if test="${coin_type == 'ETH_DAPP'}">selected="true"</c:if> >收益账户(ETH)</option>
										   <option value="USDT_DAPP" <c:if test="${coin_type == 'USDT_DAPP'}">selected="true"</c:if> >质押账户(USDT)</option>
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
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
				
			</form>
			
		</div>
		
		<!-- 修改正式用户余额 -->
		<!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/dappAdminUserAction!reset.action" method="post" id="resetForm">
			
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_reset_member" value="${id}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>
				<input type="hidden" name="session_token" id="session_token_reset_member" value="${session_token}"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_reset_member" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">修改DAPP账户余额(root)_正式用户</h4>
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
										   <option value="recharge" <c:if test="${reset_type == 'recharge'}">selected="true"</c:if> >增加金额(正式用户记录报表)</option>
										   <option value="withdraw" <c:if test="${reset_type == 'withdraw'}">selected="true"</c:if> >减少金额(正式用户不记录报表)</option>
										</select>
									</div>
								</div>
								
								<div class="modal-body">
									<div class="">
										<span class="help-block">账变币种</span>
										<select id="coin_type" name="coin_type" class="form-control">
										   <option value="">账变币种</option>
										   <option value="ETH_DAPP" <c:if test="${coin_type == 'ETH_DAPP'}">selected="true"</c:if> >收益账户(ETH)</option>
										   <option value="USDT_DAPP" <c:if test="${coin_type == 'USDT_DAPP'}">selected="true"</c:if> >质押账户(USDT)</option>
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
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
				
			</form>
			
		</div>
			
		<!-- 修改正式用户质押总资产 -->
		<!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/dappAdminUserAction!reset_ple.action" method="post" id="resetForm">
			
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_reset_member_usdt" value="${id}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>
				<input type="hidden" name="session_token" id="session_token_reset_member_usdt" value="${session_token}"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_reset_member_usdt" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">修改质押总资产_正式用户</h4>
								</div>
								
								<div class="modal-body">
									<div class="">
									<span class="help-block">账变金额(不能小于0)</span>
										<input id="money_revise" name="money_revise" class="form-control" value = "${money_revise}"/>
									</div>
								</div>

								 <div class="modal-body">
									<div class="">
										<span class="help-block">账变类型</span>
										<select id="reset_type" name="reset_type" class="form-control">
										   <option value="">账变类型</option>										   
											<option value="recharge" <c:if test="${reset_type == 'recharge'}">selected="true"</c:if> >增加金额(正式用户记录报表)</option>
											<option value="withdraw" <c:if test="${reset_type == 'withdraw'}">selected="true"</c:if> >减少金额(正式用户不记录报表)</option>										   
										</select>
									</div>
								</div>
								
								<!-- 'ETH_DAPP':'收益账户(ETH)', -->
								<div class="modal-body">
									<div class="">
										<span class="help-block">账变币种</span>
										<select id="coin_type" name="coin_type" class="form-control">
										   <option value="USDT_DAPP" <c:if test="${coin_type == 'USDT_DAPP'}">selected="true"</c:if> >质押账户(USDT)</option>
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
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
				
			</form>
			
		</div>
		
		<%-- <!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/adminUserAction!reset_eth.action" method="post" id="resetForm">
			
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_reset_eth" value="${id}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>
				<input type="hidden" name="session_token" id="session_token_reset_eth" value="${session_token}"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_reset_eth" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">增加ETH收益金</h4>
								</div>
								
								<div class="modal-body">
									<div class="">
										<span class="help-block">请输入ETH收益金额(不能小于0)</span>
										<input id="money_revise" name="money_revise" class="form-control"/>
									</div>
								</div>
								
								<div class="modal-body">
									<div class="">
										<span class="help-block">请输入收益日志记录的收益下发时间</span>
										<input id="create_time" name="create_time" class="form-control"/>										
									</div>
								</div>
								
								<input type="hidden" name="reset_type" id="reset_type" value="${gift_eth}"/>
								<input type="hidden" name="coin_type" id="coin_type" value="${ETH_DAPP}"/>							
								
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
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
				
			</form>
			
		</div> --%>
		
		<%-- <!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/adminUserAction!reset.action" method="post" id="resetForm">
			
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_reset_gift" value="${id}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>
				<input type="hidden" name="session_token" id="session_token_reset" value="${session_token}"/>
				<input type="hidden" name="coin_type" id="coin_type" value="${coin_type}"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_reset_gift" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">赠送USDT金额</h4>
								</div>
								
								<div class="modal-body">
									<div class="">
									<span class="help-block">账变金额</span>
										<input id="money_revise" name="money_revise" class="form-control"/>
									</div>
								</div>
								
								<div class="modal-body">
									<div class="">
										<span class="help-block">账变类型</span>										
										<select id="reset_type" name="reset_type" class="form-control">
										   <option value="">账变类型</option>
										   <option value="change">平台赠送USDT金额(增加金额，记录到赠送报表)</option>
										   <option value="changesub">平台扣除USDT金额(减少余额不记录报表)</option>
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
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
				
			</form>
			
		</div> --%>

		<%-- <!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/adminUserAction!resetRegisterCode.action"
				method="post" id="succeededForm" class="form-horizontal">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_resetRegisterCode" value="${id}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>

				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_resetRegisterCode_set" tabindex="-1" role="dialog"
						aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">修改邀请客户注册权限</h4>
								</div>
								
								<div class="modal-body">
									<div class="">										
										<select id="rolename_para" name="rolename_para" class="form-control " >
											<option value="">请选择权限</option>
											<option value="open">开启邀请权限</option>
											<option value="close">关闭邀请权限</option>
										</select>	
									</div>
								</div>								
								
								<div class="modal-footer" style="margin-top: 0;">
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
			
		</div> --%>
		
		<%-- <!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/adminUserAction!resetUserLevel.action"
				method="post" id="succeededForm" class="form-horizontal">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_resetuserlevel" value="${pageNo}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_resetuserlevel_set" tabindex="-1" role="dialog"
						aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">修改会员等级(只能输入整数数字1--8，大于8的无作用)</h4>
								</div>
								
								<div class="modal-body">
									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">新的会员等级</label>
										<div class="col-sm-4">
											<input id="user_level" type="text" name="user_level" placeholder="请输入新的会员等级" >
										</div>
									</div>									
								</div>
								
								<div class="modal-footer" style="margin-top: 0;">
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
			
		</div> --%>
				
		<%-- <!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/adminUserAction!resetpsw.action"
				method="post" id="succeededForm" class="form-horizontal">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
				<input type="hidden" name="id" id="id_resetpsw" value="${id}"/>
				<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
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
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
				
			</form>
			
		</div> --%>	
		
		<%-- <!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/adminUserAction!resetGoogleAuth.action"
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
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
				
			</form>
			
		</div> --%>
		
		<%-- <!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/adminUserAction!resetUserLoginState.action"
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
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
				
			</form>
			
		</div> --%>

		<%-- <!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/adminUserAction!resetsafepsw.action"
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
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
				
			</form>
			
		</div> --%>
		
		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<div class="form-group">
		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="net_form" tabindex="-1"
				role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
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
	
	<!-- 模态框 -->
	<div class="form-group">
	
		<form action="<%=basePath%>normal/dappAdminUserAction!sycnBalance.action"
			method="post" id="subCollectAllForm">
			
			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
			<input type="hidden" name="session_token" id="session_token_sycn_balance" value="${session_token}"/>
				
			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="modal_sycn_balance" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
						
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>								
								<h4 class="modal-title" id="myModalLabel">全局同步区块链余额</h4>								
							</div>
							
							<div class="modal-header">
								<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
								<p class="ballon color1" style="margin-top:10px;">
									同步所有已授权用户区块链余额(代理商操作将同步线下用户)
								</p>
							</div>
							
							<div class="modal-body">
								<div class="">
									<input id="safeword" type="password" name="safeword"
										class="form-control" placeholder="请输入登录人资金密码">
								</div>
							</div>
							
							<div class="modal-footer" style="margin-top: 0;">
								<button type="button" class="btn " data-dismiss="modal">关闭</button>
								<button type="submit"  class="btn btn-default" >确认</button>
							</div>
							
						</div>
					</div>
				</div>
			</div>
			
		</form>
		
	</div>
	
	<form action="<%=basePath%>normal/dappAdminUserAction!sycnBalance.action" method="post" id="sycnBalanceOneForm">
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
		<input type="hidden" name="session_token" id="session_token_sycn_balance_one" value="${session_token}" />
		<input type="hidden" name="usercode" id="balance_usercode" value="${usercode}" />
	</form>
	
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
		$(function() {			
			$('#create_time').datetimepicker({
				format : 'yyyy-mm-dd HH:mm:ss',
				timeFormat:    'HH:mm:ss', 
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 0				
			});			
		});
	</script>

	<script type="text/javascript">
		function sycn_balance() {
	 		 var session_token = $("#session_token").val();
	 		 $("#session_token_sycn_balance").val(session_token);
			$('#modal_sycn_balance').modal("show");
		}
		function sycn_balance_one(usercode,address) {
			swal({
				title : "同步用户("+usercode+")区块链余额",
				text : "地址:"+address,
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : true
			}, function() {
		 		var session_token = $("#session_token").val();
		 		$("#session_token_sycn_balance_one").val(session_token);
				$("#balance_usercode").val(usercode);
				$('#sycnBalanceOneForm').submit();
			});
		};
		function resetpsw(id) {
			$("#id_resetpsw").val(id);
			$('#modal_set').modal("show");
		}		
		function resetuserlevel(id) {
			$("#id_resetuserlevel").val(id);
			$('#modal_resetuserlevel_set').modal("show");
		}		
		function resetRegisterCode(id) {
			$("#id_resetRegisterCode").val(id);
			$('#modal_resetRegisterCode_set').modal("show");
		}
		function resetgoogle(id) {
			$("#id_resetgoogle").val(id);
			$('#modal_google').modal("show");
		}
		function resetlogin(id) {
			$("#id_resetlogin").val(id);
			$('#modal_login_state').modal("show");
		}
		function resetsafepsw(id) {
			$("#id_resetsafepsw").val(id);
			$('#modal_safepasw_set').modal("show");
		}
	</script>

	<script type="text/javascript">		
		function resetGift(id) {
			var session_token = $("#session_token").val();
			 $("#session_token_reset").val(session_token);
			$("#id_reset_gift").val(id);
			$('#modal_reset_gift').modal("show");
		}
		function reset_eth(id) {
			var session_token = $("#session_token").val();
			 $("#session_token_reset_eth").val(session_token);
			$("#id_reset_eth").val(id);
			$('#modal_reset_eth').modal("show");
		}
		function reset(id) {
			var session_token = $("#session_token").val();
			 $("#session_token_reset").val(session_token);
			$("#id_reset").val(id);
			$('#modal_reset').modal("show");
		}
		function reset_Member(id) {
			var session_token = $("#session_token").val();
			 $("#session_token_reset_member").val(session_token);
			$("#id_reset_member").val(id);
			$('#modal_reset_member').modal("show");
		}
		function reset_Member_usdt(id) {
			var session_token = $("#session_token").val();
			 $("#session_token_reset_member_usdt").val(session_token);
			$("#id_reset_member_usdt").val(id);
			$('#modal_reset_member_usdt').modal("show");
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
			var url = "<%=basePath%>normal/dappAdminUserAction!getParentsNet.action";
			var data = {"partyId":id};
			goNewAjaxUrl(url,data,function(tmp){
				console.log(tmp);
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
			}else if(rolename=="AGENTLOW"){
				return '<span class="right label label-primary">代理商</span>';
			}else if(rolename=="TEST"){
				return '<span class="right label label-default">试用账号</span>';
			}
		}
		function goNewAjaxUrl(targetUrl,data,Func,Fail){
	// 		console.log(data);
			$.ajax({
				url:targetUrl,
				data:data,
				type : 'get',
				dataType : "json",
				success: function (res) {
					// var tmp = $.parseJSON(res)
					var tmp = res
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
