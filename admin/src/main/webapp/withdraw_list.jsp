<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
	<%@ include file="include/head.jsp"%>

	<style>
		.black_overlay {
			display: none;
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			background-color: rgba(0, 0, 0, 0.7);
			z-index: 100;
		}

		.enlargeContainer {
			display: none;
		}

		.enlargePreviewImg {
			/*这里我设置的是：预览后放大的图片相对于整个页面定位*/
			position: absolute;
			top: 50%;
			left: 50%;
			transform: translate(-50%, -50%);

			/*宽度设置为页面宽度的70%，高度自适应*/
			width: 85%;
			z-index: 200;
		}

		/*关闭预览*/
		.close {
			position: absolute;
			top: 20px;
			right: 20px;
			width: 20px;
			height: 20px;
			cursor: pointer;
			z-index: 200;
		}

		td {
			word-wrap: break-word; /* 让内容自动换行 */
			max-width: 200px; /* 设置最大宽度，以防止内容过长 */
		}
	</style>

</head>

<body>

<%@ include file="include/loading.jsp"%>
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTENT -->
<div class="ifr-dody">

	<input type="hidden" name="session_token" id="session_token" value="${session_token}" />

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTAINER -->
	<div class="ifr-con">
		<h3>提现订单</h3>

		<%@ include file="include/alert.jsp"%>

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-default">

					<div class="panel-title">查询条件</div>
					<div class="panel-body">

						<form class="form-horizontal"
							  action="<%=basePath%>normal/adminWithdrawAction!list.action"
							  method="post" id="queryForm">

							<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
							<!-- <s:hidden name="succeeded_para"></s:hidden> -->
							<input type="hidden" name="succeeded_para" id="succeeded_para" value="${succeeded_para}">

							<div class="col-md-12 col-lg-4">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<!-- <s:textfield id="order_no_para" name="order_no_para"
													cssClass="form-control " placeholder="订单号（完整）" /> -->
											<input id="order_no_para" name="order_no_para"
												   class="form-control " placeholder="订单号（完整）" value="${order_no_para}" />
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<!-- <s:textfield id="name_para" name="name_para"
													cssClass="form-control " placeholder="用户名、UID" /> -->
											<input id="name_para" name="name_para" class="form-control "
												   placeholder="用户名、UID" value="${name_para}" />
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<%-- <s:select id="rolename_para" cssClass="form-control "
                                                name="rolename_para"
                                                list="#{'MEMBER':'正式账号','GUEST':'演示账号'}" listKey="key"
                                                listValue="value" headerKey="" headerValue="所有账号"
                                                value="rolename_para" /> --%>
											<select id="rolename_para" name="rolename_para"
													class="form-control ">
												<option value="">所有账号</option>
												<option value="MEMBER"
														<c:if test="${rolename_para == 'MEMBER'}">selected="true"</c:if>>正式账号</option>
												<option value="GUEST"
														<c:if test="${rolename_para == 'GUEST'}">selected="true"</c:if>>演示账号</option>
											</select>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-3">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<%-- <s:select id="rolename_para" cssClass="form-control "
                                                name="rolename_para"
                                                list="#{'MEMBER':'正式账号','GUEST':'演示账号'}" listKey="key"
                                                listValue="value" headerKey="" headerValue="所有账号"
                                                value="rolename_para" /> --%>
											<select id="method" name="method"
													class="form-control ">
												<option value="">订单类型</option>
												<option value="USDT"
														<c:if test="${method == 'USDT'}">selected="true"</c:if>>USDT</option>
												<option value="ETH"
														<c:if test="${method == 'ETH'}">selected="true"</c:if>>ETH</option>
												<option value="bank"
														<c:if test="${method == 'bank'}">selected="true"</c:if>>银行卡</option>
											</select>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2" style="margin-top: 10px;">
								<input id="start_time" name="start_time"
									   class="form-control " placeholder="创建开始时间" value="${start_time}" />
							</div>
							<div class="col-md-12 col-lg-2" style="margin-top: 10px;">
								<input id="end_time" name="end_time"
									   class="form-control " placeholder="创建结束日期" value="${end_time}" />
							</div>

							<div class="col-md-12 col-lg-2" style="margin-top: 10px;">
								<input id="reviewStartTime" name="reviewStartTime"
									   class="form-control " placeholder="审核开始日期" value="${reviewStartTime}" />
							</div>
							<div class="col-md-12 col-lg-2" style="margin-top: 10px;">
								<input id="reviewEndTime" name="reviewEndTime"
									   class="form-control " placeholder="审核结束日期" value="${reviewEndTime}" />
							</div>

							<div class="col-md-12 col-lg-2" style="margin-top: 10px;">
								<button type="submit" class="btn btn-light btn-block">查询</button>
							</div>

							<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
								<div class="mailbox clearfix">
									<div class="mailbox-menu">
										<ul class="menu">
											<li><a href="javascript:setState('')"> 全部</a></li>
											<li><a href="javascript:setState(0)"> 未处理</a></li>
											<li><a href="javascript:setState(1)"> 通过申请</a></li>
											<li><a href="javascript:setState(2)"> 驳回申请</a></li>
										</ul>
									</div>
								</div>
							</div>

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

						<table class="table table-bordered table-striped">

							<thead>
							<tr>
								<td>订单号</td>
								<td>用户名</td>
								<td>UID</td>
								<td>账户类型</td>
								<td>推荐人</td>
								<td>提现币链</td>
								<td>提现数量</td>
								<td>到账数量</td>
								<td>到账数量(USDT)</td>
								<c:if test="${isOpen == '1'}">
									<td>真实客损</td>
								</c:if>
								<td>状态</td>
								<td>提现地址</td>
								<td>驳回原因</td>
								<td>创建时间</td>
								<td>审核时间</td>
								<td>用户备注</td>
								<td width="150px"></td>
							</tr>
							</thead>

							<tbody style="font-size: 13px;">
							<!-- <s:iterator value="page.elements" status="stat"> -->
							<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
							<tr>
								<td>
										${item.order_no}
								</td>
								<td>
										${item.username}
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
										<c:when test="${item.rolename=='AGENT'}">
											<span class="right label label-primary">${item.roleNameDesc}</span>
										</c:when>
										<c:otherwise>
											${item.roleNameDesc}
										</c:otherwise>
									</c:choose>
								</td>
								<td>${item.username_parent}</td>
								<td>
									<c:if test="${method_map.get(item.method) == null}">
										${item.method}
									</c:if>
									<c:if test="${method_map.get(item.method) != null}">
										${method_map.get(item.method)}
									</c:if>
								</td>
								<td>${item.volume}</td>

								<td><span class="label label-danger">${item.amount}</span></td>
								<td><span class="label label-danger">${item.usdtAmount}</span></td>
								<c:if test="${isOpen == '1'}">
									<td><span class="label label-danger">
									<fmt:formatNumber value="${item.withdrawCommission}" pattern="#0.00"/>
								</span></td>
								</c:if>
								<td>
									<c:if test="${item.succeeded == 0}">处理中</c:if>
									<c:if test="${item.succeeded == 3}">处理中...</c:if>
									<c:if test="${item.succeeded == 1}"> <span class="right label label-success">已处理</span> </c:if>
									<c:if test="${item.succeeded == 2}">驳回</c:if>
								</td>
								<c:choose>
									<c:when test="${item.method=='BANK'}">
										<td><a href="javascript:withdraw_blank_about('${item.amount}','${item.bankCardNo}','${item.bankUserName}','${item.bank}',
																					'${item.routingNum}','${item.accountAddress}','${item.bankAddress}',
																					'${platformName}','${item.countryName}')">查看信息</a></td>
									</c:when>
									<c:otherwise>
										<td><a href="javascript:withdraw_about('${item.amount}','${item.address}','${item.qdcode}','${item.tx}')">查看信息</a></td>
									</c:otherwise>
								</c:choose>

								<td>
										${item.failure_msg}
								</td>

								<td>${item.createTime}</td>
								<td>${item.reviewTime}</td>
								<td>${item.remark}</td>

								<td>

									<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_FINANCE')
														 || security.isResourceAccessible('OP_DAPP_WITHDRAW_OPERATE')
														 || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_OPERATE')
														 || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_OPERATE')}">

										<div class="btn-group">
											<button type="button" class="btn btn-light">操作</button>
											<button type="button" class="btn btn-light dropdown-toggle"
													data-toggle="dropdown" aria-expanded="false">
												<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
											</button>
											<ul class="dropdown-menu" role="menu">
												<c:if test="${item.succeeded == 0}">
													<li><a href="javascript:onsucceeded('${item.id}','${item.withdrawCommission}','${succeeded_para}')">通过申请（手动打款）</a></li>
													<%-- <c:if test="">
                                                        <li><a href="javascript:onsucceededThird('${item.id}')">通过申请（三方渠道）</a></li>
                                                    </c:if> --%>
													<li><a href="javascript:reject('${item.id}','${succeeded_para}')">驳回申请</a></li>
												</c:if>

												<c:choose>
													<c:when test="${item.method=='USDT'}">
														<li><a href="javascript:onchangeAddress('${item.id}','${item.address}','${succeeded_para}','${item.method}')">修改用户收款地址</a></li>
													</c:when>
													<c:otherwise>
														<li><a href="javascript:onchangeAddress('${item.id}','${item.bankCardNo}','${succeeded_para}','${item.method}')">修改用户收款地址</a></li>
													</c:otherwise>
												</c:choose>
								</ul>
										</div>

									</c:if>

									<!-- 模态框 -->
									<div class="form-group">

										<form action="<%=basePath%>normal/adminWithdrawAction!success.action"
											  method="post" id="succeededForm">

											<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
											<input type="hidden" name="name_para" id="name_para" value="${name_para}">
											<input type="hidden" name="succeeded_para" id="succeeded_paras" value="${succeeded_para}">
											<input type="hidden" name="id" id="id_success" value="${id}">
											<input type="hidden" name="session_token" id="session_token_success" value="${session_token}">

											<div class="col-sm-1">
												<!-- 模态框（Modal） -->
												<div class="modal fade" id="modal_set" tabindex="-1"
													 role="dialog" aria-labelledby="myModalLabel"
													 aria-hidden="true">
													<div class="modal-dialog">
														<div class="modal-content">

															<div class="modal-header">
																<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
																<h4 class="modal-title" id="myModalLabel">提现通过申请（手动打款）</h4>
															</div>
															<c:if test="${isOpen == '1'}">
																<div class="modal-header">
																	<h4 class="modal-title">真实客损</h4>
																</div>

																<div class="modal-body">
																	<div class="">
																		<input id="withdrawCommission" name="withdrawCommission"
																			   class="form-control"  value="${withdrawCommission}"
																			   onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')" >
																	</div>
																</div>
															</c:if>

															<div class="modal-header">
																<h4 class="modal-title" id="myModalLabel">资金密码</h4>
															</div>

															<div class="modal-body">
																<div class="">
																	<input id="safeword" type="password" name="safeword"
																		   class="form-control" placeholder="请输入资金密码">
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
											</c:forEach>
										</form>

									</div>

									<!-- 模态框 -->
									<div class="form-group">

										<form action="<%=basePath%>normal/adminWithdrawAction!changeAddress.action"
											  method="post" id="succeededForm">

											<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
											<input type="hidden" name="name_para" id="name_para" value="${name_para}">
											<input type="hidden" name="succeeded_para" id="onchangeAddress_succeeded_para" value="${onchangeAddress_succeeded_para}">
											<input type="hidden" name="id" id="id_changeAddress" value="${id}">
											<input type="hidden" name="session_token" id="session_token_success" value="${session_token}">
											<input type="hidden" name="method" id="methods" value="${method}">

											<div class="col-sm-1">
												<!-- 模态框（Modal） -->
												<div class="modal fade" id="modal_set_changeAddress"
													 tabindex="-1" role="dialog"
													 aria-labelledby="myModalLabel" aria-hidden="true">
													<div class="modal-dialog">
														<div class="modal-content">

															<div class="modal-header">
																<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
																<h4 class="modal-title" id="myModalLabel">修改用户提现订单收款地址</h4>
															</div>

															<div class="modal-body">
																<div class="">
																	用户旧提现地址
																	<input id="changeAddress" type="text" name="changeAddress" class="form-control"
																		   readonly="readonly" value="${changeAddress}" />
																</div>
															</div>

															<div class="modal-body">
																<div class="">
																	修改后用户新提现地址
																	<input id="changeAfterAddress" type="text"
																		   name="changeAfterAddress" class="form-control" value="${changeAfterAddress}" />
																</div>
															</div>

															<div class="modal-body">
																<div class="">
																	<input id="safeword" type="password" name="safeword"
																		   class="form-control" placeholder="请输入资金密码">
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

								</td>

							</tr>
							<!-- </s:iterator> -->
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

	<!-- 模态框（Modal） -->
	<div class="modal fade" id="modal_withdraw" tabindex="-1"
		 role="dialog" aria-labelledby="myModalLabel"
		 aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
					<h4 class="modal-title">提现地址</h4>
				</div>

				<div class="modal-body">
					<div class="">
						到账数量
						<input id="withdraw_amount" type="text"
							   name="withdraw_amount" class="form-control"
							   readonly="readonly" />
					</div>
					<div class="">
						提现地址
						<input id="withdraw_address" type="text"
							   name="withdraw_address" class="form-control"
							   readonly="readonly" />
					</div>
				</div>

				<div class="modal-header">
					<h4 class="modal-title" id="myModalLabel">提现地址二维码</h4>
				</div>

				<div class="modal-body">
					<div class="">
						<%--																<a id="withdraw_img_a" href="#" name="withdraw_img_a" target="_blank"> --%>
						<img width="200px" height="200px" id="withdraw_img" name="withdraw_img" src="" onclick="openImg()"/>
						<%--																</a>--%>
					</div>
				</div>

<%--				<div class="modal-body">--%>
<%--					<div class="">--%>
<%--						提现hash值--%>
<%--						<input id="withdraw_hash" type="text"--%>
<%--							   name="withdraw_hash" class="form-control"--%>
<%--							   readonly="readonly" />--%>
<%--					</div>--%>
<%--				</div>--%>
				<!--黑色遮罩-->
				<div class="black_overlay" id="black_overlay"></div>

				<!--预览容器，存放点击放大后的图片-->
				<div class="enlargeContainer" id="enlargeContainer" onclick="clonsImg()">
					<!-- 关闭按钮，一个叉号图片 -->
					<img src="./images/close.png" class="close" id="close" onclick="clonsImg()" >
				</div>
				<div class="modal-footer" style="margin-top: 0;">
					<button type="button" class="btn " data-dismiss="modal">关闭</button>
				</div>

			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal -->
	</div>

	<div class="modal fade" id="modal_bank_withdraw" tabindex="-1"
		 role="dialog" aria-labelledby="myModalLabel"
		 aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
					<h4 class="modal-title">提现地址</h4>
				</div>

				<div class="modal-body">
					<div class="">
						到账数量
						<input id="bank_amount" type="text"
							   name="bank_amount" class="form-control"
							   readonly="readonly" />
					</div>
					<div class="">
						姓名
						<input id="bank_name" type="text"
							   name="bank_name" class="form-control"
							   readonly="readonly" />
					</div>
					<div class="">
						卡号
						<input id="bank_account" type="text"
							   name="bank_account" class="form-control"
							   readonly="readonly" />
					</div>
					<div class="">
						开户行
						<input id="bank" type="text"
							   name="bank" class="form-control"
							   readonly="readonly" />
					</div>
					<c:if test="${platformName == 'Argos'}">

						<div class="">
							路由号码
							<input id="routingNum" type="text"
								   name="routingNum" class="form-control"
								   readonly="readonly" />
						</div>
						<div class="">
							账户地址
							<input id="account_address" type="text"
								   name="account_address" class="form-control"
								   readonly="readonly" />
						</div>
						<div class="">
							银行地址
							<input id="bank_address" type="text"
								   name="bank_address" class="form-control"
								   readonly="readonly" />
						</div>

					</c:if>
					<c:if test="${platformName == 'Shop2u'}">

						<div class="">
							国家
							<input id="countryName" type="text"
								   name="countryName" class="form-control"
								   readonly="readonly" />
						</div>
					</c:if>
				</div>


				<div class="modal-footer" style="margin-top: 0;">
					<button type="button" class="btn " data-dismiss="modal">关闭</button>
				</div>

			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal -->
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
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">完整用户名（完整钱包地址）</h4>
						</div>
						<div class="modal-header">
							<h4 class="modal-title" name="usernallName" id="usernallName"  readonly="true" style="display: inline-block;"></h4>
							<a href="" id="user_all_name_a" sytle="cursor:pointer;" target="_blank">在Etherscan上查看</a>
						</div>

						<div class="modal-body">
							<div class="">
							</div>
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- END CONTAINER -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<%@ include file="include/footer.jsp"%>

</div>
<!-- End Content -->
<!-- //////////////////////////////////////////////////////////////////////////// -->

<%@ include file="include/js.jsp"%>

<script type="text/javascript">
	function reject_confirm() {
		swal({
			title : "是否确认驳回?",
			text : "驳回后款项返回账户",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			document.getElementById("onreject").submit();
		});
	};

	function reject(id,succeeded_para) {
		var session_token = $("#session_token").val();
		$("#session_token_reject").val(session_token);
		$("#id_reject").val(id);
		$("#reject_succeeded_para").val(succeeded_para);
		$('#modal_reject').modal("show");
	};
</script>

<!-- Modal -->
<div class="modal fade" id="modal_reject" tabindex="-1" role="dialog"
	 aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">

			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">请输入驳回原因</h4>
			</div>

			<div class="modal-body">
				<form action="<%=basePath%>normal/adminWithdrawAction!reject.action"
					  method="post" id="onreject">
					<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
					<input type="hidden" name="name_para" id="name_para" value="${name_para}">
					<input type="hidden" name="succeeded_para" id="reject_succeeded_para" value="${reject_succeeded_para}">
					<input type="hidden" name="id" id="id_reject" value="${id}">
					<input type="hidden" name="session_token" id="session_token_reject" value="${session_token}">

					<textarea name="failure_msg" id="failure_msg" class="form-control  input-lg" rows="2" cols="10" placeholder="驳回原因" >${failure_msg}</textarea>
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title"></h4>
					</div>

					<textarea name="remarks" id="remarks" class="form-control  input-lg" rows="2" cols="10" placeholder="资金日志备注-最大字数260位" >${remarks}</textarea>
				</form>
			</div>

			<div class="modal-footer">
				<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
				<button type="button" class="btn btn-default" onclick="reject_confirm()">驳回代付申请</button>
			</div>

		</div>
	</div>
</div>
<!-- End Moda Code -->

<script type="text/javascript">
	function withdraw_about(amount, address, img, hash){
		$("#withdraw_amount").val(amount);
		$("#withdraw_address").val(address);
		<%--document.getElementById('withdraw_img_a').href="<%=basePath%>public/showimg!showImg.action?imagePath="+img;--%>
		document.getElementById('withdraw_img').src="<%=basePath%>public/showimg!showImg.action?imagePath=" + img;
		$("#withdraw_hash").val(hash);
		black_overlay.style.display = 'none';
		enlargeContainer.style.display = 'none';
		$('#modal_withdraw').modal("show");
	}

	function withdraw_blank_about(amount, bankCardNo, bankUserName, bank, routingNum, accountAddress, bankAddress, platformName, countryName){
		$("#bank_amount").val(amount);
		$("#bank_account").val(bankCardNo);
		$("#bank_name").val(bankUserName);
		$("#bank").val(bank);
		$("#routingNum").val(routingNum);
		$("#account_address").val(accountAddress);
		$("#bank_address").val(bankAddress);
		$("#platformName").val(platformName);
		$("#countryName").val(countryName);
		$('#modal_bank_withdraw').modal("show");
	}

	function onsucceeded(id,withdrawCommission,succeeded_para) {
		var session_token = $("#session_token").val();
		$("#session_token_success").val(session_token);
		$("#id_success").val(id);
		$("#withdrawCommission").val(withdrawCommission);
		$("#succeeded_paras").val(succeeded_para);
		$('#modal_set').modal("show");
	}

	function onchangeAddress(id, adress,succeeded_para,method) {
		var session_token = $("#session_token").val();
		$("#session_token_success").val(session_token);
		$("#id_changeAddress").val(id);
		$("#changeAddress").val(adress);
		$("#onchangeAddress_succeeded_para").val(succeeded_para);
		$("#method").val(method);
		$('#modal_set_changeAddress').modal("show");
	}

	function onsucceededThird(id) {
		var session_token = $("#session_token").val();
		$("#session_token_success_third").val(session_token);
		$("#id_success_third").val(id);
		$('#modal_set_third').modal("show");
	}

	function handel(id) {
		$("#id_success").val(id);
		swal({
			title : "是否确认通过申请?",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			document.getElementById("success").submit();
		});
	}


	$(function() {
		$('#reviewStartTime').datetimepicker({
			format : 'yyyy-mm-dd',
			language : 'zh',
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 2,
			clearBtn : true,
			minView : 2
		});
		$('#reviewEndTime').datetimepicker({
			format : 'yyyy-mm-dd',
			language : 'zh',
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 2,
			clearBtn : true,
			minView : 2
		});
	});
	$(function() {
		$('#start_time').datetimepicker({
			format : 'yyyy-mm-dd',
			language : 'zh',
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 2,
			clearBtn : true,
			minView : 2
		});
		$('#end_time').datetimepicker({
			format : 'yyyy-mm-dd',
			language : 'zh',
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 2,
			clearBtn : true,
			minView : 2
		});
	});
</script>

<script type="text/javascript">
	function setState(state) {
		document.getElementById("succeeded_para").value = state;
		document.getElementById("queryForm").submit();
	}

	function getallname(name){
		$("#usernallName").html(name);
		$("#user_all_name_a").attr("href","https://etherscan.io/address/"+name);
		$("#net_form").modal("show");
	}


	function openImg(id) {
		let black_overlay = document.getElementById('black_overlay');
		let enlargeContainer = document.getElementById('enlargeContainer');
		let closeBtn = document.getElementById('close');

		let toEnlargeImg = document.getElementById('withdraw_img');
		toEnlargeImg.addEventListener('click', function () {
			// 获取当前图片的路径
			let imgUrl = this.src;
			// 显示黑色遮罩和预览容器
			black_overlay.style.display = 'block';
			enlargeContainer.style.display = 'block';
			let img = new Image();
			img.src = imgUrl;
			img.classList.add('enlargePreviewImg');
			if (closeBtn.nextElementSibling) {
				enlargeContainer.removeChild(closeBtn.nextElementSibling);
			}
			enlargeContainer.appendChild(img);
		});
	}
	function clonsImg() {
		// 关闭预览
		black_overlay.style.display = 'none';
		enlargeContainer.style.display = 'none';
	}
</script>

</body>

</html>
