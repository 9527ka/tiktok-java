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

<style>
	td {
		word-wrap: break-word; /* 让内容自动换行 */
		max-width: 200px; /* 设置最大宽度，以防止内容过长 */
	}
</style>

<div class="ifr-dody">

	<input type="hidden" name="session_token" id="session_token" value="${session_token}"/>

	<div class="ifr-con">
		<h3>用户基础管理</h3>

		<%@ include file="include/alert.jsp"%>

		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-default">

					<div class="panel-title">查询条件</div>
					<div class="panel-body">

						<form class="form-horizontal"
							  action="<%=basePath%>normal/adminUserAction!list.action"
							  method="post" id="queryForm">

							<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="name_para" name="name_para"
												   class="form-control " placeholder="用户名、UID" value="${name_para}"/>
										</div>
									</div>
								</fieldset>
							</div>

							<c:if test="${loginPartyId == ''}">
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="agentUserCode" name="agentUserCode"
													   class="form-control " placeholder="代理ID" value="${agentUserCode}"/>
											</div>
										</div>
									</fieldset>
								</div>
							</c:if>
							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="phone" name="phone"
												   class="form-control " placeholder="手机号" value="${phone}"/>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="loginIp_para" name="loginIp_para"
												   class="form-control " placeholder="最后登陆IP" value="${loginIp_para}"/>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2">
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

						<%-- <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                     || security.isResourceAccessible('OP_USER_OPERATE')}">

                            <div class="col-md-12 col-lg-12 " style="margin-top: 25px;margin-left: -15px;">
                                <div class="panel-title">操作</div>
                                <div class="col-md-12 col-lg-3">
                                    <button type="button" onclick="sycn_balance()" class="btn btn-light btn-block " >
                                        全局同步区块链余额
                                    </button>
                                </div>
                            </div>

                        </c:if> --%>

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

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_AGENT')
									 || security.isResourceAccessible('OP_USER_OPERATE')}">

						<a href="<%=basePath%>normal/adminUserAction!toAdd.action?registerType=phone" class="btn btn-light" style="margin-bottom: 10px">
							<i class="fa fa-pencil"></i>新增演示账号</a>

					</c:if>

					<a href="<%=basePath%>normal/adminUserAction!addGuest.action" class="btn btn-light" style="margin-bottom: 10px">
						<i class="fa fa-pencil"></i>账号导入</a>

<%--					<a href="<%=basePath%>normal/adminUserAction!updateUser.action" class="btn btn-light" style="margin-bottom: 10px">--%>
<%--						<i class="fa fa-pencil"></i>修改账号</a>--%>

					<div class="panel-body">

						<table class="table table-bordered table-striped">

							<thead>
							<tr>
								<td>用户名</td>
								<td>UID</td>
								<td>所属代理</td>
								<td>代理Id</td>
								<td>账户类型</td>
								<td>钱包余额</td>
								<td>冻结金额</td>
								<td>用户积分</td>
								<td>手机号</td>
								<td>邮箱</td>
								<c:if test="${platformName == 'SM' || platformName == 'FamilyShop'}">
									<td>whatsApp</td>
								</c:if>
								<%--										<td>推荐人</td>--%>
								<td>注册时间</td>
								<td>最后登录IP</td>
								<td>备注</td>
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
									<td>${item.agentName}</td>
									<td>${item.agentCode}</td>
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
									<td>
										<c:if test="${item.frozen_state == 1}">
											<fmt:formatNumber value="${item.moneyAfterFrozen}" pattern="#0.00" />
										</c:if>
										<c:if test="${item.frozen_state == 0}">
											<fmt:formatNumber value="${item.money}" pattern="#0.00" />
										</c:if>
									</td>
									<td>
										<c:if test="${item.frozen_state == 1}">
											<fmt:formatNumber value="${item.money}" pattern="#0.00" />
										</c:if>
										<c:if test="${item.frozen_state == 0}">
											<fmt:formatNumber value="${item.moneyAfterFrozen}" pattern="#0.00" />
										</c:if>
									</td>
									<td>${item.activityPoints}</td>
									<td>${item.phone}</td>
									<td>${item.email}</td>
									<c:if test="${platformName == 'SM' || platformName == 'FamilyShop'}">
										<td>${item.whatApp}</td>
									</c:if>

										<%--											<td>${item.username_parent}</td>--%>
									<td>${item.create_time}</td>
									<td>${item.login_ip}</td>
									<td>${item.remarks}</td>

									<td>

										<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_AGENT')
															 || security.isResourceAccessible('OP_USER_OPERATE') || security.isResourceAccessible('OP_EXCHANGE_USER_OPERATE')}">

											<div class="btn-group">
												<button type="button" class="btn btn-light">操作</button>
												<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
													<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
												</button>
												<ul class="dropdown-menu" role="menu" style="overflow:scroll;height:180px;">

													<li><a href="<%=basePath%>normal/adminUserAction!toUpdate.action?id=${item.id}&name_para=${item.name_para}&rolename_para=${item.rolename_para}">修改</a></li>
													<li><a href="<%=basePath%>normal/adminUserAction!toUpdateUserAddress.action?id=${item.id}">编辑收货地址</a></li>
													<li><a href="javascript:reset('${item.id}')">修改账户余额</a></li>
													<li><a href="javascript:integral('${item.id}','${item.usercode}','${item.username_hide}','${item.activityPoints}')">修改积分</a></li>
													<li><a href="javascript:updatePhone('${item.id}')">修改手机账号</a></li>
													<li><a href="javascript:updateEmail('${item.id}','${item.email}')">修改邮箱账号</a></li>
														<%--															<li><a href="javascript:resetGift('${item.id}')">赠送彩金</a></li>--%>
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
				<!-- End Panel -->

			</div>
		</div>

	</div>


	<!-- 交易所修改账户余额 模态框 -->
	<div class="form-group">
		<form action="<%=basePath%>normal/adminUserAction!reset_exchange.action"
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
										<option value="recharge">平台充值金额</option>
										<option value="withdraw">平台扣除金额</option>
										<option value="change">平台赠送彩金</option>
										<option value="changesub">平台扣除彩金</option>
									</select>
								</div>
							</div>

							<div class="modal-body">
								<div class="">
									<span class="help-block">账变币种</span>
									<select id="reset_type" name="coin_type" class="form-control">
										<option value="">账变币种</option>
										<option value="usdt">USDT</option>
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
	<div class="form-group">
		<form action="<%=basePath%>normal/adminUserAction!addActivityPoint.action"
			  method="post" id="resetForm">

			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
			<input type="hidden" name="partyId" id="id_reset_integral" value="${id}">
			<input type="hidden" name="session_token" id="session_token_reset_integral" value="${session_token}">
			<div class="col-sm-1">
				<div class="modal fade" id="modal_reset_integral" tabindex="-1"
					 role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">

							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="myModalLabel2">修改积分</h4>
							</div>

							<div class="modal-body">
								<div class="">
									<span class="help-block">用户ID</span>
									<input id="userCode" name="userCode" class="form-control" readonly="true"/>
								</div>
							</div>
							<div class="modal-body">
								<div class="">
									<span class="help-block">账号</span>
									<input id="userNameiIntegral" name="userName" class="form-control" readonly="true"/>
								</div>
							</div>

							<div class="modal-body">
								<div class="">
									<span class="help-block">账变类型</span>
									<select id="accType" name="accType" class="form-control">
										<option value="1">增加积分</option>
										<option value="-1">扣除积分</option>
									</select>
								</div>
							</div>
							<div class="modal-body">
								<div class="">
									<span class="help-block">当前积分</span>
									<input id="activityPoints" name="activityPoints" class="form-control"  maxlength="8" readonly="true"/>
								</div>
							</div>

							<div class="modal-body">
								<div class="">
									<span class="help-block">积分</span>
									<input id="accPoint" name="accPoint" class="form-control" oninput="value=value.replace(/[^\d]/g,'')" maxlength="8"/>
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


	<%--		<!-- 赠送彩金 -->--%>
	<%--		<div class="form-group">--%>
	<%--			<form action="<%=basePath%>normal/adminUserAction!reset_exchange.action"--%>
	<%--				  method="post" id="resetForms">--%>

	<%--				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">--%>
	<%--				<input type="hidden" name="id" id="id_reset_gift" value="${id}">--%>
	<%--				<input type="hidden" name="name_para" id="name_para" value="${name_para}">--%>
	<%--				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}">--%>
	<%--				<input type="hidden" name="session_token" id="session_token_reset" value="${session_token}">--%>
	<%--				<input type="hidden" name="reset_type" id="reset_type" value="change">--%>
	<%--				<input type="hidden" name="coin_type" id="coin_type" value="usdt">--%>
	<%--				<div class="col-sm-1">--%>
	<%--					<div class="modal fade" id="modal_reset_gift" tabindex="-1"--%>
	<%--						 role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">--%>
	<%--						<div class="modal-dialog">--%>
	<%--							<div class="modal-content">--%>

	<%--								<div class="modal-header">--%>
	<%--									<button type="button" class="close" data-dismiss="modal"--%>
	<%--											aria-hidden="true">&times;</button>--%>
	<%--									<h4 class="modal-title" id="myModalLabel">赠送彩金</h4>--%>
	<%--								</div>--%>

	<%--								<div class="modal-body">--%>
	<%--									<div class="">--%>
	<%--										<span class="help-block">账变金额(不能小于0)</span>--%>
	<%--										<input id="money_revise" name="money_revise" class="form-control"/>--%>
	<%--									</div>--%>
	<%--								</div>--%>



	<%--								<div class="modal-header">--%>
	<%--									<h4 class="modal-title" id="myModalLabel">备注</h4>--%>
	<%--								</div>--%>
	<%--								<div class="modal-body">--%>
	<%--									<div class="">--%>
	<%--										<textarea name="gift_remarks" id="gift_remarks" class="form-control  input-lg" rows="2" cols="10" placeholder="备注" >${gift_remarks}</textarea>--%>
	<%--									</div>--%>
	<%--								</div>--%>

	<%--								<div class="modal-header">--%>
	<%--									<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>--%>
	<%--								</div>--%>
	<%--								<div class="modal-body">--%>
	<%--									<div class="">--%>
	<%--										<input id="login_safeword" type="password" name="login_safeword"--%>
	<%--											   class="form-control" placeholder="请输入登录人资金密码">--%>
	<%--									</div>--%>
	<%--								</div>--%>

	<%--								<div class="modal-footer" style="margin-top: 0;">--%>
	<%--									<button type="button" class="btn " data-dismiss="modal">关闭</button>--%>
	<%--									<button id="sub" type="submit" class="btn btn-default" >确认</button>--%>
	<%--								</div>--%>

	<%--							</div>--%>
	<%--						</div>--%>
	<%--					</div>--%>
	<%--				</div>--%>
	<%--			</form>--%>
	<%--		</div>--%>



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
										<input id="password" type="password" name="password" placeholder="请输入用户重置密码" minlength="6" maxlength="12">
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


	<!-- 模态框 -->
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

	</div>

	<!-- 模态框 -->
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

	</div>

	<%@ include file="include/footer.jsp"%>

</div>
<!-- End Content -->
<!-- //////////////////////////////////////////////////////////////////////////// -->

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
			<!-- /.modal-content -->
		</div>
		<!-- /.modal -->
	</div>
</div>
<!-- </form> -->

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
								<td>基础认证</td>
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

	<form action="<%=basePath%>normal/adminUserAction!sycnBalance.action"
		  method="post" id="subCollectAllForm">

		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
		<input type="hidden" name="session_token" value="${session_token}"/>

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

		<form action="<%=basePath%>normal/adminUserAction!sycnBalance.action"
			  method="post" id="sycnBalanceOneForm">
			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
			<input type="hidden" name="session_token" value="${session_token}"/>
			<input type="hidden" name="usercode" id="balance_usercode"/>
		</form>

</div>

<form action="<%=basePath%>normal/adminUserAction!sycnBalance.action" method="post" id="sycnBalanceOneForm">
	<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
	<input type="hidden" name="session_token"/>
	<input type="hidden" name="usercode" id="balance_usercode"/>
</form>


<!-- 手机号账号修改  -->
<div class="form-group">
	<form action="<%=basePath%>normal/adminUserAction!updateUserName.action"
		  method="post" id="succeededForm" class="form-horizontal">
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
		<input type="hidden" name="partyId" id="phone_Id" value="${partyId}"/>
		<input type="hidden" name="registerType" id="registerTypes" value="phone"/>
		<div class="col-sm-1">
			<div class="modal fade" id="phone_update" tabindex="-1" role="dialog"
				 aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">用户手机号账号</h4>
						</div>

						<div class="modal-body">

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">用户名</label>
								<label class="col-sm-2 control">
									<select id="mobilePrefix" name="mobilePrefix" class="form-control" >
									</select>
								</label>
								<div class="col-sm-5">
									<input id="mobileTail" name="mobileTail" class="form-control" maxlength="100" oninput="value=value.replace(/[^\d]/g,'')" placeholder="最大长度为100" value="${mobileTail}"/>
								</div>
							</div>

							<div class="form-group" >
								<label for="input002" class="col-sm-3 control-label form-label">用户密码</label>
								<div class="col-sm-4">
									<input id="password" type="password" name="password"
										   class="login_safeword" placeholder="请入用户密码" style="width: 256px; max-width: 400px" minlength="6" maxlength="12" >
								</div>
							</div>
							<div class="form-group" >
								<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
								<div class="col-sm-4">
									<input id="login_safeword" type="password" name="login_safeword"
										   class="login_safeword" placeholder="请输入登录人资金密码" style="width: 256px; max-width: 400px">
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

<!-- 邮箱账号修改  -->
<div class="form-group">
	<form action="<%=basePath%>normal/adminUserAction!updateUserName.action"
		  method="post" id="succeededForm" class="form-horizontal">
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
		<input type="hidden" name="partyId" id="email_Id" value="${partyId}"/>
		<input type="hidden" name="registerType" id="registerType" value="email"/>
		<div class="col-sm-1">
			<div class="modal fade" id="email_update" tabindex="-1" role="dialog"
				 aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">设置用户邮箱账号</h4>
						</div>

						<div class="modal-body">

							<div class="form-group" >
								<label for="input002" class="col-sm-3 control-label form-label">用户邮箱账号 </label>
								<div class="col-sm-5">
									<input id="userName"  name="userName" placeholder="请输入用户邮箱账号" style="width: 300px; max-width: 400px">
								</div>
							</div>

							<div class="form-group" >
								<label for="input002" class="col-sm-3 control-label form-label">用户密码</label>
								<div class="col-sm-4">
									<input id="password" type="password" name="password"
										   class="login_safeword" placeholder="请入用户密码" style="width: 300px; max-width: 400px" minlength="6" maxlength="12">
								</div>
							</div>

							<div class="form-group" >
								<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
								<div class="col-sm-4">
									<input id="login_safeword" type="password" name="login_safeword"
										   class="login_safeword" placeholder="请输入登录人资金密码" style="width: 300px; max-width: 400px" >
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

	document.getElementById("succeededForm").addEventListener("submit", function(event) {
		var passwordInput = document.getElementById("password");
		var password = passwordInput.value;

		if (password.length < 6 || password.length > 12) {
			swal({
				title: "密码长度必须在6到12位之间!",
				timer: 1500,
				showConfirmButton: false
			})
			event.preventDefault(); // 阻止表单提交
		}
	});

</script>

<script type="text/javascript">
	function sycn_balance() {
		// 		 var session_token = $("#session_token").val();
		// 		 $("#session_token_sycn_balance").val(session_token);
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
	function integral(id,userCode,userNameiIntegral,activityPoints) {
		var session_token = $("#session_token").val();
		$("#session_token_reset_integral").val(session_token);
		$("#id_reset_integral").val(id);
		$("#userCode").val(userCode);
		$("#activityPoints").val(activityPoints);
		$("#userNameiIntegral").val(userNameiIntegral);
		getIntegral(id);
		$('#modal_reset_integral').modal("show");
	}

	function getIntegral(id){
		var data = {"partyId":id};
		getUserIntegral("<%=adminUrl%>/activity/getActivityPoint.action",data);
	}
	function getUserIntegral(targetUrl,data){
		$.ajax({
			url:targetUrl,
			data:data,
			type:'get',
			success : function(data) {
				console.log(data)
				if (data.msg === "succeed") {
					var tmp = data;
					var points = tmp.data.points;
					$("#activityPoints").val(points);
				}
				if (data.msg == 500){
					swal({
						title: data.error,
						timer: 2000,
						showConfirmButton: false
					})

				}

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
	}

	function updatePhone(id) {
		var session_token = $("#session_token").val();
		$("#session_token_reset").val(session_token);
		$("#phone_Id").val(id);
		getPhone(id);
	}

	function updateEmail(id,email) {
		$("#userName").val(email);
		$("#email_Id").val(id);
		$('#email_update').modal("show");
	}

	function getPhone(partyId){
		$.ajax({
			type: "get",
			url: "<%=basePath%>normal/adminUserAction!getPhone.action",
			dataType : "json",
			data : {
				"partyId" : partyId
			},
			success : function(data) {
				if (data.code === 200) {
					var tmp = data;
					var mobileMap = tmp.mobileMap;
					var mobilePrefix = tmp.mobilePrefixs;
					var mobileTail = tmp.mobileTails;

					var selectHtml = '';
					for (var key in mobileMap) {
						var selected = (key === mobilePrefix) ? 'selected="selected"' : '';
						selectHtml += '<option value="' + key + '" ' + selected + '>' + mobileMap[key] + '</option>';
					}

					console.log(selectHtml);
					// 将生成的选项设置到下拉列表
					$("#mobilePrefix").html(selectHtml);
					$("#mobileTail").val(mobileTail);
					$('#phone_update').modal("show");
				}
				if (data.code == 500){
					swal({
						title: data.error,
						timer: 2000,
						showConfirmButton: false
					})

				}

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
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

	function getAllMoney(id){
		$("#modal_money").modal("show");
		var data = {"para_wallet_party_id":id};
		goAjaxUrl("<%=basePath%>normal/adminUserAllStatisticsAction!walletExtendsAll.action",data);
	}

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
						+'<td>'+getKycAuthorityDom(tmp.user_parents_net[i].kyc_authority)+'</td>'
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
	function getKycAuthorityDom(kyc_authority){
		if(kyc_authority==true){
			return '<span>已验证</span>';
		}else if(kyc_authority==false){
			return '<span>未验证</span>';
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
