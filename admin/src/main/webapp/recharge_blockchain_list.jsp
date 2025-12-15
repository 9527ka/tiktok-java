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
			width: 90%;
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

<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTENT -->
<div class="ifr-dody">

	<input type="hidden" name="session_token" id="session_token" value="${session_token}" />

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTAINER -->
	<div class="ifr-con">
		<h3>充值订单</h3>

		<%@ include file="include/alert.jsp"%>

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-default">

					<div class="panel-title">查询条件</div>
					<div class="panel-body">

						<form class="form-horizontal"
							  action="<%=basePath%>normal/adminRechargeBlockchainOrderAction!list.action"
							  method="post" id="queryForm">

							<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
							<!-- <s:hidden name="state_para"></s:hidden> -->
							<input type="hidden" name="state_para" id="state_para" value="${state_para}">

							<div class="col-md-12 col-lg-3">
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
											<input id="name_para" name="name_para"
												   class="form-control " placeholder="用户名、UID" value="${name_para}" />
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
											<select id="rolename_para" name="rolename_para" class="form-control " >
												<option value="">所有账号</option>
												<option value="MEMBER" <c:if test="${rolename_para == 'MEMBER'}">selected="true"</c:if> >正式账号</option>
												<option value="GUEST" <c:if test="${rolename_para == 'GUEST'}">selected="true"</c:if> >演示账号</option>
											</select>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2" >
								<input id="start_time" name="start_time"
									   class="form-control " placeholder="创建开始时间" value="${start_time}" />
							</div>
							<div class="col-md-12 col-lg-2" >
								<input id="end_time" name="end_time"
									   class="form-control " placeholder="创建结束日期" value="${end_time}" />
							</div>

							<div class="col-md-12 col-lg-3" style="margin-top: 10px;">
								<input id="reviewStartTime" name="reviewStartTime"
									   class="form-control " placeholder="审核开始日期" value="${reviewStartTime}" />
							</div>
							<div class="col-md-12 col-lg-3" style="margin-top: 10px;">
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
											<li><a href="javascript:setState(0)"> 未支付</a></li>
											<li><a href="javascript:setState(1)"> 支付成功</a></li>
											<li><a href="javascript:setState(2)"> 失败</a></li>
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
								<td>充值币链</td>
								<td>充值数量</td>
								<td>实际到账金额</td>
								<c:if test="${isOpen == '1'}">
									<td>真实客损</td>
								</c:if>
								<td>充值凭证</td>
								<td>状态</td>
								<td>驳回原因</td>
								<td>创建时间</td>
								<td>审核时间</td>
								<td>用户备注</td>
								<!-- <td width="130px"></td> -->
								<td width="130px"></td>


							</tr>
							</thead>

							<tbody>
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
									<c:choose>
										<c:when test="${item.coin == 'eth'}">
													<span class="label label-info">
														${item.coin}
														<c:if test="${item.blockchanin_name != ''}">_</c:if>
														${item.blockchanin_name}
													</span>
										</c:when>
										<c:when test="${item.coin == 'btc'}">
													<span class="label label-warning">
														${item.coin}
														<c:if test="${item.blockchanin_name != ''}">_</c:if>
														${item.blockchanin_name}
													</span>
										</c:when>
										<c:when test="${item.coin == 'ht'}">
													<span class="label label-primary">
														${item.coin}
														<c:if test="${item.blockchanin_name != ''}">_</c:if>
														${item.blockchanin_name}
													</span>
										</c:when>
										<c:when test="${item.coin == 'usdc'}">
													<span class="label label-default">
														${item.coin}
														<c:if test="${item.blockchanin_name != ''}">_</c:if>
														${item.blockchanin_name}
													</span>
										</c:when>
										<c:otherwise>
											${item.coin}
											<c:if test="${item.blockchanin_name != ''}">_</c:if>
											${item.blockchanin_name}
										</c:otherwise>
									</c:choose>
								</td>

								<td><span class="label label-danger">${item.channelAmount}</span></td>

								<td><span class="label label-danger">
									<fmt:formatNumber value="${item.amount}" pattern="#0.00"/>
								</span></td>
								<c:if test="${isOpen == '1'}">
									<td><span class="label label-danger">
									<fmt:formatNumber value="${item.rechargeCommission}" pattern="#0.00"/>
								</span></td>
								</c:if>

								<td><a href="javascript:recharge_about('${item.channelAmount}','${item.address}','${item.img}','${item.hash}','${item.channel_address}')">查看信息</a></td>
								<td>
									<c:if test="${item.succeeded == 0}">未支付</c:if>
									<c:if test="${item.succeeded == 1}">
										<span class="right label label-success">支付成功</span>
									</c:if>
									<c:if test="${item.succeeded == 2}">失败</c:if>
								</td>
								<td>${item.description}</td>
								<!-- <td><s:date name="created" format="MM-dd HH:mm:ss " /></td>
											<td><s:date name="reviewTime" format="MM-dd HH:mm:ss " /></td> -->
								<td>${item.created}</td>
								<td>${item.reviewTime}</td>
								<td>${item.remarks}</td>

								<td>
									<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_FINANCE')
														 || security.isResourceAccessible('OP_DAPP_WITHDRAW_OPERATE')
														 || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_OPERATE')
														 || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_OPERATE')}">


										<c:if test="${item.succeeded == 0}">
											<div class="btn-group">
												<button type="button" class="btn btn-light">操作</button>
												<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
													<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
												</button>
												<ul class="dropdown-menu" role="menu">

													<li><a href="javascript:onsucceeded('${item.order_no}','${item.coin}','${item.channel_amount}','${item.amount}','${item.rechargeCommission}','${state_para}')">手动到账</a></li>
													<!-- <li><a href="javascript:onChangeImg('<s:property value="id" />')">手动修改充值图片</a></li> -->
													<li><a href="javascript:reject('${item.id}','${state_para}')">驳回申请</a></li>

														<%--																<li><a href="javascript:remark('${item.id}')">备注</a></li>--%>
												</ul>
											</div>

										</c:if>


									</c:if>
								</td>

								</c:forEach>
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
	<!-- END CONTAINER -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<!-- 模态框 -->
	<div class="form-group">

		<form action="<%=basePath%>normal/adminRechargeBlockchainOrderAction!onsucceeded.action"
			  method="post" id="succeededForm">

			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
			<input type="hidden" name="name_para" id="name_para" value="${name_para}">
			<input type="hidden" name="state_para" id="state_paras" value="${state_para}">
			<input type="hidden" name="order_no" id="order_no" value="${order_no}">
			<input type="hidden" name="session_token" id="session_token_success" value="${session_token}">
			<input type="hidden" name="rechargeCommission" id="rechargeCommission" value="${rechargeCommission}">

			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="modal_set" tabindex="-1" role="dialog"
					 aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">

							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
								<h4 class="modal-title">用户充值币种</h4>
							</div>

							<div class="modal-body">
								<div class="">
									<input id="success_coin" name="success_coin" type="text"
										   class="form-control" readonly="readonly" value="${success_coin}">
								</div>
							</div>

							<div class="modal-header">
								<h4 class="modal-title">充值币种数量</h4>
							</div>

							<div class="modal-body">
								<div class="">
									<input id="success_amount" name="success_amount" type="text"
										   class="form-control" readonly="readonly" value="${success_amount}" >
									<!-- readonly="true" -->
								</div>
							</div>

							<div class="modal-header">
								<h4 class="modal-title">币种汇率</h4>
							</div>

							<div class="modal-body">
								<div class="">
									<input id="exchange_rate" name="exchange_rate" type="text"
										   class="form-control" readonly="readonly" value="${exchange_rate}" >
									<!-- readonly="true" -->
								</div>
							</div>

							<div class="modal-header">
								<h4 class="modal-title">充值币种USDT价值</h4>
							</div>

							<div class="modal-body">
								<div class="">
									<c:choose>
										<c:when test="${rechargeIsOpen=='0'}">
											<input id="transfer_usdt" name="transfer_usdt" type="text"
												   class="form-control"  value="${transfer_usdt}" readonly="true" >
										</c:when>
										<c:otherwise>
											<input id="transfer_usdt" name="transfer_usdt" type="text"
												   class="form-control"  value="${transfer_usdt}" >
										</c:otherwise>
									</c:choose>

								</div>
							</div>
							<%--							<c:if test="${isOpen == '1'}">--%>
							<div class="modal-header">
								<h4 class="modal-title">备注</h4>
							</div>

							<div class="modal-body">
								<div class="">
									<input id="remarks" name="remarks"
										   class="form-control"  value="${remarks}" maxlength="260" placeholder="备注字数260位">
								</div>
							</div>
							<%--							</c:if>--%>
							<!--  -->
							<div class="modal-header">
								<h4 class="modal-title" id="myModalLabel">资金密码</h4>
							</div>

							<div class="modal-body">
								<div class="">
									<input id="safeword" type="password" name="safeword"
										   class="form-control" placeholder="请输入资金密码" value="${safeword}" >
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

	<div class="col-sm-1">
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="modal_recharge" tabindex="-1"
			 role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">

					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
						<h4 class="modal-title">充值凭证</h4>
					</div>

					<div class="modal-body">
						<div class="">
							充值数量
							<input id="recharge_channel_amount" type="text"
								   name="recharge_channel_amount" class="form-control"
								   readonly="readonly" />
						</div>
						<div class="">
							币链地址
							<input id="channelAddress" type="text"
								   name="channelAddress" class="form-control"
								   readonly="readonly" />
						</div>
						<%--							<div class="">--%>
						<%--								用户地址--%>
						<%--								<input id="recharge_address" type="text"--%>
						<%--									name="recharge_address" class="form-control" --%>
						<%--									readonly="readonly" />--%>
						<%--							</div>--%>
					</div>

					<div class="modal-header">
						<h4 class="modal-title" id="myModalLabel">充值截图</h4>
					</div>
					<div class="modal-body">
						<div class="">
							<%--							<a id="recharge_img_a" name="recharge_img_a" target="_blank">--%>
							<img width="200px" height="200px" id="recharge_img"
								 name="recharge_img" src="" onclick="openImg()"/>
							<%--							</a>--%>
						</div>
					</div>

					<%--						<div class="modal-body">--%>
					<%--							<div class="">--%>
					<%--								充值hash值--%>
					<%--								<input id="recharge_hash" type="text"--%>
					<%--									name="recharge_hash" class="form-control" --%>
					<%--									readonly="readonly" />--%>
					<%--							</div>--%>
					<%--						</div>--%>


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
	</div>

	<%@ include file="include/footer.jsp"%>

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
<!-- End Content -->
<!-- //////////////////////////////////////////////////////////////////////////// -->

<%@ include file="include/js.jsp"%>

<script type="text/javascript">

	function reject(id,state_para) {
		$("#id_reject").val(id);
		$("#reject_state_para").val(state_para);
		$('#modal_reject').modal("show");
	};

	function remark(id) {
		$("#id_remark").val(id);
		$('#modal_remark').modal("show");
	};

	function reject_confirm() {
		swal({
			title : "是否确认驳回?",
			text : "驳回订单充值失败",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			document.getElementById("onreject").submit();
		});
	};

	function remark_confirm() {
		swal({
			title : "是否确认?",
			text : "提交订单备注",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			document.getElementById("onremark").submit();
		});
	};

	function onsucceeded(order_no,coin,channel_amount,amount,rechargeCommission,state_para){
		var exchanges;
		var exchangeRate;
		exchanges = amount / channel_amount;
		exchangeRate = Math.floor(exchanges*100)/100;
		var session_token = $("#session_token").val();
		$("#session_token_success").val(session_token);
		$("#order_no").val(order_no);
		$("#success_amount").val(channel_amount);
		$("#success_coin").val(coin);
		$("#transfer_usdt").val(amount);
		$("#exchange_rate").val(exchangeRate);
		$("#rechargeCommission").val(rechargeCommission);
		$("#state_paras").val(state_para);
		$('#modal_set').modal("show");
	}

	/* function onChangeImg(order_no){
         $("#order_no_img").val(order_no);
         var session_token = $("#session_token").val();
         $("#session_token_img").val(session_token);
        $('#modal_set_img').modal("show");
    } */

	<%--function getValue(coin,amount){--%>
	<%--	var code = '';--%>
	<%--	if(coin == "BTC"){--%>
	<%--		code = 'btc_exchange_usdt';--%>
	<%--	}--%>
	<%--	if(coin == "ETH"){--%>
	<%--		code = 'eth_exchange_usdt';--%>
	<%--	}--%>

	<%--  $.ajax({--%>
	<%--      type: "get",--%>
	<%--      url: "<%=basePath%>normal/adminSysparaAction!findModal.action",--%>
	<%--		dataType : "json",--%>
	<%--		data : {--%>
	<%--			"code" : code--%>
	<%--		},--%>
	<%--		success : function(data) {--%>
	<%--			var tmp = data;--%>
	<%--			var codeValue = tmp.codeValue;--%>
	<%--			var lumpSum = codeValue * amount + "";--%>
	<%--			var rule = /([0-9]+.[0-9]{2})[0-9]*/;--%>
	<%--			lumpSum = lumpSum.replace(rule,"$1");--%>
	<%--			 $("#transfer_usdt").val(lumpSum);--%>
	<%--			 $("#exchange_rate").val(codeValue);--%>
	<%--		},--%>
	<%--		error : function(XMLHttpRequest, textStatus,--%>
	<%--				errorThrown) {--%>
	<%--			console.log("请求错误");--%>
	<%--		}--%>
	<%--	});--%>
	<%--}--%>

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

</script>

<!-- Modal -->
<div class="modal fade" id="modal_reject" tabindex="-1" role="dialog"
	 aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">

			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">请输入驳回原因</h4>
			</div>

			<div class="modal-body">
				<form action="<%=basePath%>normal/adminRechargeBlockchainOrderAction!reject.action"
					  method="post" id="onreject">
					<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
					<input type="hidden" name="name_para" id="name_para" value="${name_para}">
					<input type="hidden" name="id" id="id_reject" value="${id}">
					<input type="hidden" name="state_para" id="reject_state_para" value="${reject_state_para}">
					<textarea name="failure_msg" id="failure_msg" class="form-control  input-lg" rows="2" cols="10" placeholder="驳回原因" >${failure_msg}</textarea>
				</form>
			</div>

			<div class="modal-footer">
				<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
				<button type="button" class="btn btn-default" onclick="reject_confirm()">驳回充值申请</button>
			</div>

		</div>
	</div>
</div>
<div class="modal fade" id="modal_remark" tabindex="-1" role="dialog"
	 aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">

			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">请输入备注</h4>
			</div>

			<div class="modal-body">
				<form action="<%=basePath%>normal/adminRechargeBlockchainOrderAction!remark.action"
					  method="post" id="onremark">
					<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
					<input type="hidden" name="id" id="id_remark" value="${id}">
					<textarea name="remark" id="remark" class="form-control  input-lg" rows="2" cols="10" placeholder="备注内容" >${remark}</textarea>
				</form>
			</div>

			<div class="modal-footer">
				<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
				<button type="button" class="btn btn-default" onclick="remark_confirm()">提交备注内容</button>
			</div>

		</div>
	</div>
</div>
<!-- End Moda Code -->

<script type="text/javascript">
	function setState(state){
		document.getElementById("state_para").value=state;
		document.getElementById("queryForm").submit();
	}
	function recharge_about(channel_amount, address, img, hash,channel_address){
		$("#recharge_channel_amount").val(channel_amount);
		$("#channelAddress").val(channel_address);
		// document.getElementById('recharge_img_a').href= img;
		document.getElementById('recharge_img').src=img;
		$("#recharge_hash").val(hash);
		black_overlay.style.display = 'none';
		enlargeContainer.style.display = 'none';
		$('#modal_recharge').modal("show");
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

		let toEnlargeImg = document.getElementById('recharge_img');
		toEnlargeImg.addEventListener('click', function () {
			// 获取当前图片的路径
			let imgUrl = this.src;
			// 显示黑色遮罩和预览容器
			black_overlay.style.display = 'block';
			enlargeContainer.style.display = 'block';
			let img = new Image();
			img.src = imgUrl;

			var originalWidth = img.width;
			var originalHeight = img.height;
			img.classList.add('enlargePreviewImg');
			// 判断图片高度是否超过 800，超过则设置 top 为 110%
			if (originalHeight >= 1200 && originalHeight < 1500) {
				img.style.top = '111%';
			} else if(originalHeight > 1500){
				img.style.top = '133%';
			} else {
				img.style.top = '60%';
			}

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
