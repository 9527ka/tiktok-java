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
	<script src="include/top.jsp"></script>
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>彩金审核</h3>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>mall/seller/invitelist.action" method="post"
								id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${pageNo}">
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="userName" name="userName" class="form-control"
													   placeholder="用户账号" value = "${userName}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="userCode" name="userCode" class="form-control"
													   placeholder="店铺ID" value = "${userCode}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="sellerName" name="sellerName" class="form-control"
													   placeholder="店铺名称" value = "${sellerName}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="state" name="state" class="form-control " >
													<option value="">是否派发</option>
													<option value="1" <c:if test="${state == '1'}">selected="true"</c:if> >已派发</option>
													<option value="0" <c:if test="${state == '0'}">selected="true"</c:if> >未派发</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3" style="margin-top: 15px;">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="lotteryName" name="lotteryName" class="form-control " >
													<option value="">活动类型</option>
													<option value="首充活动" <c:if test="${lotteryName == '首充活动'}">selected="true"</c:if> >首充活动</option>
													<option value="拉人活动" <c:if test="${lotteryName == '拉人活动'}">selected="true"</c:if> >拉人活动</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-2" style="margin-top: 15px;">
									<input id="startTime" name="startTime" class="form-control "
										   placeholder="开始日期" value="${startTime}" />
								</div>
								<div class="col-md-12 col-lg-2" style="margin-top: 15px;">

									<input id="endTime" name="endTime" class="form-control "
										   placeholder="结束日期" value="${endTime}" />
								</div>

								<div class="col-md-12 col-lg-3" style="margin-top: 15px;">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>
								<div class="col-md-12 col-lg-12" style="margin-top:10px;"></div>


							</form>

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

							<table class="table table-bordered table-striped" border="1">
								<thead>
									<tr>
										<td>账号</td>
										<td>会员ID</td>
										<td>手机号</td>
										<td>邮箱</td>
										<td>店铺名称</td>
										<td>活动类型</td>
										<td>奖品类型</td>
										<td>奖品价值</td>
										<td>推荐人</td>
										<td>是否派发</td>
										<td>领取时间</td>
										<td>派发时间</td>
										<td>操作人</td>
										<td>备注</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
<%--										<input type="hidden" name="iconImg" id="iconImg" value = "${item.iconImg}"/>--%>
										<tr>

											<td>${item.partyName}</td>
											<td>${item.userCode}</td>
											<td>${item.phone}</td>
											<td>${item.email}</td>
											<td>${item.sellerName}</td>
											<td>${item.lotteryName}</td>
											<td>彩金</td>
											<td>${item.prizeAmount}</td>
											<td>${item.agentName}</td>
											<td>
												<c:choose>
													<c:when test="${item.state == '0'}">
														<span class="right label label-danger">未派发</span>
													</c:when>
													<c:when test="${item.state == '1'}">
														<span class="right label label-success">已派发</span>
													</c:when>
												</c:choose>
											</td>
											<td>${item.applyTime}</td>
											<td>
												<c:choose>
													<c:when test="${item.issueTime == null}">
															--
													</c:when>
													<c:when test="${item.issueTime != null}">
														${item.issueTime}
													</c:when>
												</c:choose>
											</td>
											<td>${item.createUser}</td>
											<td>${item.remark}</td>
											<td>
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_INVITE_OPERATE')}">

													<c:if test="${item.state == 0}">
														<a href="javascript:freezeSellerMoney(`${item.UUID}`,`${item.partyId}`,`${item.userCode}`,`${item.sellerName}`,
														`${item.lotteryName}`,`${item.prizeAmount}`)" class="btn btn-light">派发</a>
													</c:if>

												</c:if>
											</td>

										</tr>

									</c:forEach>

								</tbody>
							</table>
							<%@ include file="include/page_simple.jsp"%>
							<nav>
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
			<form action=""
					method="post" id="mainform">
				<input type="hidden" name="pageNo" id="pageNo"
					   value="${pageNo}">
				<input type="hidden" name="id" id="id"/>
				<div class="col-sm-1 form-horizontal">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_succeeded" tabindex="-1"
						 role="dialog" aria-labelledby="myModalLabel"
						 aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content" >
								<div class="modal-header">
									<button type="button" class="close"
											data-dismiss="modal" aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">确认调整</h4>
								</div>
								<div class="modal-body">
									<div class="form-group" >
										<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
										<div class="col-sm-4">
											<input id="login_safeword" type="password" name="login_safeword"
												   class="login_safeword" placeholder="请输入登录人资金密码" >
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
<%--									<div class="form-group" >--%>
<%--										<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>--%>
<%--										<div class="col-sm-4">--%>
<%--											<input id="google_auth_code"  name="google_auth_code"--%>
<%--												   placeholder="请输入谷歌验证码" >--%>
<%--										</div>--%>
<%--									</div>--%>
								</div>
								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn "
											data-dismiss="modal">关闭</button>
									<button id="sub" type="submit"
											class="btn btn-default">确认</button>
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


	<div class="form-group">

		<form action="<%=basePath%>mall/seller//distribute.action"
			  method="post" id="succeededForm2">

			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
			<input type="hidden" name="partyId" id="partyId" value="${partyId}">
			<input type="hidden" name="uuid" id="uuid" value="${uuid}">

			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="modal_set4" tabindex="-1" role="dialog"
					 aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
								<h4 class="modal-title">用户ID</h4>
							</div>

							<div class="modal-body">
								<div class="">
									<input id="userCodes" name="userCode"
										   class="form-control" value="${userCodes}"  readonly="true">
								</div>
							</div>

							<div class="modal-header">
								<h4 class="modal-title">店铺名</h4>
							</div>
							<div class="modal-body">
								<div class="">
									<input id="sellerNames" name="sellerName"
										   class="form-control" value="${sellerName}"  readonly="true">
								</div>
							</div>

							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true"></button>
								<h4 class="modal-title">彩金金额</h4>
							</div>

							<div class="modal-body">
								<div class="">
									<input id="money" name="money"
										   class="form-control" value="${prizeAmount}"  readonly="true">
								</div>
							</div>
<%--							<div class="modal-header">--%>
<%--								<button type="button" class="close" data-dismiss="modal"--%>
<%--										aria-hidden="true"></button>--%>
<%--								<h4 class="modal-title">实际发放</h4>--%>
<%--							</div>--%>

<%--								<div class="modal-body">--%>
<%--									<div class="">--%>
<%--										<input id="prizeAmount" name="prizeAmount"--%>
<%--											   class="form-control" value="${prizeAmount}" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')">--%>
<%--									</div>--%>
<%--								</div>--%>
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
											aria-hidden="true"></button>
									<h4 class="modal-title">备注</h4>
								</div>

							<div class="modal-body">
								<div class="">
									<input id="remark" name="remark"
										   class="form-control" value="${remark}" maxlength="250">
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




	<%@ include file="include/js.jsp"%><script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>

	<script type="text/javascript">

		function freezeSellerMoney(uuid,partyId,userCode,sellerName,lotteryName,prizeAmount){

			if (/^[+-]?\d+(\.\d+)?[eE][+-]?\d+$/.test(prizeAmount)) {
				// 如果是科学计数法，将其转换为常规数字
				prizeAmount = parseFloat(prizeAmount).toString();
			}
			$("#partyId").val(partyId);
			$("#uuid").val(uuid);
			$("#userCodes").val(userCode);
			$("#prizeAmount").val(prizeAmount);
			$("#money").val(prizeAmount);
			$("#sellerNames").val(sellerName);
			$("#lotteryName").val(lotteryName);
			$('#modal_set4').modal("show");
		}


		$(function() {
			$('#startTime').datetimepicker({
				format : 'yyyy-mm-dd hh:ii:00',
				minuteStep:1,
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true
			});
			$('#endTime').datetimepicker({
				format : 'yyyy-mm-dd hh:ii:00',
				minuteStep:1,
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true
			});

		});
	</script>
</body>
</html>