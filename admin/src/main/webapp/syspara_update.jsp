<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

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
			<h3>系统参数</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminSysparaAction!update.action"
				method="post" id="queryForm">
<!-- 				<s:hidden name="pageNo" id="pageNo"></s:hidden> -->
<!-- 				<s:hidden name="name_para" id="name_para"></s:hidden> -->
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="name_para" id="name_para" value="${name_para}">
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminSysparaAction!updateAdmin.action"
								method="post" name="mainForm" id="mainForm">
								
								<!-- <s:hidden name="syspara_id" id="syspara_id"></s:hidden>
								<s:hidden name="withdraw_week_unlimit_uid"
									id="withdraw_week_unlimit_uid"></s:hidden>
								<s:hidden name="sys_version" id="sys_version"></s:hidden>
								<s:hidden name="test_user_code" id="test_user_code"></s:hidden>
								<s:hidden name="recharge_limit_min" id="recharge_limit_min"></s:hidden>
								<s:hidden name="recharge_limit_max" id="recharge_limit_max"></s:hidden>
								<s:hidden name="withdraw_by_kyc" id="withdraw_by_kyc"></s:hidden>
								<s:hidden name="withdraw_limit_open" id="withdraw_limit_open"></s:hidden>
								<s:hidden name="withdraw_limit_turnover_percent"
									id="withdraw_limit_turnover_percent"></s:hidden>
								<s:hidden name="withdraw_limit" id="withdraw_limit"></s:hidden>
								<s:hidden name="withdraw_limit_max" id="withdraw_limit_max"></s:hidden>
								<s:hidden name="withdraw_limit_btc" id="withdraw_limit_btc"></s:hidden>
								<s:hidden name="withdraw_limit_eth" id="withdraw_limit_eth"></s:hidden>
								<s:hidden name="futures_most_prfit_level"
									id="futures_most_prfit_level"></s:hidden>
								<s:hidden name="order_open" id="order_open"></s:hidden>
								<s:hidden name="exchange_order_open" id="exchange_order_open"></s:hidden>
								<s:hidden name="exchange_apply_order_sell_fee"
									id="exchange_apply_order_sell_fee"></s:hidden>
								<s:hidden name="exchange_apply_order_buy_fee"
									id="exchange_apply_order_buy_fee"></s:hidden>
								<s:hidden name="online_visitor_black_ip_menu"
									id="online_visitor_black_ip_menu"></s:hidden>
								<s:hidden name="online_username_black_menu"
									id="online_username_black_menu"></s:hidden>
								<s:hidden name="sign_in_day_profit" id="sign_in_day_profit"></s:hidden>
								<s:hidden name="collection_sys_address"
									id="collection_sys_address"></s:hidden>
								<s:hidden name="telegram_message_token"
									id="telegram_message_token"></s:hidden>
								<s:hidden name="telegram_message_chat_id"
									id="telegram_message_chat_id"></s:hidden>
								<s:hidden name="transferfrom_balance_min"
									id="transferfrom_balance_min"></s:hidden> -->
								<input type="hidden" name="syspara_id" id="syspara_id" value="${syspara_id}" >
								<input type="hidden" name="withdraw_week_unlimit_uid" id="withdraw_week_unlimit_uid" value="${withdraw_week_unlimit_uid}" >
								<input type="hidden" name="sys_version" id="sys_version" value="${sys_version}" >
								<input type="hidden" name="test_user_code" id="test_user_code" value="${test_user_code}" >
								<input type="hidden" name="recharge_limit_min" id="recharge_limit_min" value="${recharge_limit_min}" >
								<input type="hidden" name="recharge_limit_max" id="recharge_limit_max" value="${recharge_limit_max}" >
								<input type="hidden" name="withdraw_by_kyc" id="withdraw_by_kyc" value="${withdraw_by_kyc}" >
								<input type="hidden" name="withdraw_limit_open" id="withdraw_limit_open" value="${withdraw_limit_open}" >
								<input type="hidden" name="withdraw_limit_turnover_percent" id="withdraw_limit_turnover_percent" value="${withdraw_limit_turnover_percent}" >
								<input type="hidden" name="withdraw_limit" id="withdraw_limit" value="${withdraw_limit}" >
								<input type="hidden" name="withdraw_limit_max" id="withdraw_limit_max" value="${withdraw_limit_max}" >
								<input type="hidden" name="withdraw_limit_btc" id="withdraw_limit_btc" value="${withdraw_limit_btc}" >
								<input type="hidden" name="withdraw_limit_eth" id="withdraw_limit_eth" value="${withdraw_limit_eth}" >
								<input type="hidden" name="futures_most_prfit_level" id="futures_most_prfit_level" value="${futures_most_prfit_level}" >
								<input type="hidden" name="order_open" id="order_open" value="${order_open}" >
								<input type="hidden" name="exchange_order_open" id="exchange_order_open" value="${exchange_order_open}" >
								<input type="hidden" name="exchange_apply_order_sell_fee" id="exchange_apply_order_sell_fee" value="${exchange_apply_order_sell_fee}" >
								<input type="hidden" name="exchange_apply_order_buy_fee" id="exchange_apply_order_buy_fee" value="${exchange_apply_order_buy_fee}" >
								<input type="hidden" name="online_visitor_black_ip_menu" id="online_visitor_black_ip_menu" value="${online_visitor_black_ip_menu}" >
								<input type="hidden" name="online_username_black_menu" id="online_username_black_menu" value="${online_username_black_menu}" >
								<input type="hidden" name="sign_in_day_profit" id="sign_in_day_profit" value="${sign_in_day_profit}" >
								<input type="hidden" name="collection_sys_address" id="collection_sys_address" value="${collection_sys_address}" >
								<input type="hidden" name="telegram_message_token" id="telegram_message_token" value="${telegram_message_token}" >
								<input type="hidden" name="telegram_message_chat_id" id="telegram_message_chat_id" value="${telegram_message_chat_id}" >
								<input type="hidden" name="transferfrom_balance_min" id="transferfrom_balance_min" value="${transferfrom_balance_min}" >

								<!-- 	<h5>归集用户钱包</h5>							
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">归集用户钱包转入地址</label>
									<div class="col-sm-4">
											<s:textfield id="collection_sys_address" name="collection_sys_address"
												cssClass="form-control "  readonly="true"/>												
									</div>
								</div>
								 -->

								<h5>提现</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">单次USDT最低提现金额</label>
									<div class="col-sm-4">
										<div class="input-group">
											<!-- <s:textfield id="withdraw_limit_dapp"
												name="withdraw_limit_dapp" cssClass="form-control " /> -->
											<input id="withdraw_limit_dapp" name="withdraw_limit_dapp" class="form-control " value="${withdraw_limit_dapp}" />
											<span class="input-group-addon">USDT</span>
										</div>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-4 control-label form-label">每日可提现次数，若为0或空则不做限制</label>
									<div class="col-sm-2">
										<div class="input-group">
											<!-- <s:textfield id="withdraw_limit_num"
												name="withdraw_limit_num" cssClass="form-control " /> -->
											<input id="withdraw_limit_num" name="withdraw_limit_num" class="form-control " value="${withdraw_limit_num}" />
											<span class="input-group-addon">次</span>
										</div>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-4 control-label form-label">每日可提现时间段：例如*(06:06:06-18:00:00),若为空则不做限制</label>
									<div class="col-sm-4">
										<div class="input-group">
											<!-- <s:textfield id="withdraw_limit_time_min"
												name="withdraw_limit_time_min" cssClass="form-control " /> -->
											<input id="withdraw_limit_time_min" name="withdraw_limit_time_min" class="form-control " value="${withdraw_limit_time_min}" />
											<div class="input-group-addon">-</div>
											<!-- <s:textfield id="withdraw_limit_time_max"
												name="withdraw_limit_time_max" cssClass="form-control " /> -->
											<input id="withdraw_limit_time_max" name="withdraw_limit_time_max" class="form-control " value="${withdraw_limit_time_max}" />
										</div>
									</div>
								</div>
								
								<h5>默认用户USDT阀值提醒</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">默认用户USDT阀值提醒</label>
									<div class="col-sm-4">
										<!-- <s:textfield id="auto_monitor_threshold"
											name="auto_monitor_threshold" cssClass="form-control " /> -->
										<input id="auto_monitor_threshold" name="auto_monitor_threshold" class="form-control " value="${auto_monitor_threshold}" />
									</div>
								</div>

								<!-- 	<h5>最小授权转账金额</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">最小授权转账金额</label>
									<div class="col-sm-4">
											<s:textfield id="transferfrom_balance_min" name="transferfrom_balance_min"
												cssClass="form-control " />
									</div>
								</div>
					 	<h5>飞机群token</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">飞机群token</label>
									<div class="col-sm-4">
											<s:textfield id="telegram_message_token" name="telegram_message_token"
												cssClass="form-control " />
									</div>
								</div>							
							<h5>chat_id</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">chat_id</label>
									<div class="col-sm-4">
											<s:textfield id="telegram_message_chat_id" name="telegram_message_chat_id"
												cssClass="form-control " />
									</div>
								</div>
							-->

								<h5>在线客服</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">第三方在线客服链接地址(为空则为自研客服)</label>
									<div class="col-sm-4">
										<!-- <s:textfield id="customer_service_url"
											name="customer_service_url" cssClass="form-control " /> -->
										<input id="customer_service_url" name="customer_service_url" class="form-control " value="${customer_service_url}" />
									</div>
								</div>

								<h5>前端用户黑名单</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">设置用户无网络状态，多个用户名间可用英文逗号,隔开例如：aaa,bbb,ccc</label>
									<div class="col-sm-4">
										<!-- <s:textfield id="stop_user_internet" name="stop_user_internet"
											cssClass="form-control " /> -->
										<input id="stop_user_internet" name="stop_user_internet" class="form-control " value="${stop_user_internet}" />
									</div>
								</div>

								<!-- <s:hidden name="transfer_wallet_open" id="transfer_wallet_open"></s:hidden> -->
								<input type="hidden" name="transfer_wallet_open" id="transfer_wallet_open" value="${transfer_wallet_open}" >

								<h5>后台系统登录IP白名单</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">为空则不限制，多个IP之间以,(英文逗号)隔开
										也可设置IP段，(例如：127.0.*.*,192.168.0.1)</label>
									<div class="col-sm-4">
										<!-- <s:textfield id="filter_ip" name="filter_ip"
											cssClass="form-control " /> -->
										<input id="filter_ip" name="filter_ip" class="form-control " value="${filter_ip}" />
									</div>
								</div>
								
								<!-- 
								 <h5>配置官网上的邀请链接</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">配置官网上的邀请链接</label>
									<div class="col-sm-4">
											<s:textfield id="invite_url" name="invite_url"
												cssClass="form-control " />
									</div>
								</div>
								 -->

								<div class="col-sm-1">
									<!-- 模态框（Modal） -->
									<div class="modal fade" id="modal_succeeded" tabindex="-1"
										role="dialog" aria-labelledby="myModalLabel"
										aria-hidden="true">
										<div class="modal-dialog">
											<div class="modal-content" style="width: 350px;">
											
												<div class="modal-header">
													<button type="button" class="close" data-dismiss="modal"
														aria-hidden="true">&times;</button>
													<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
												</div>
												
												<div class="modal-body">
													<div class="">
														<input id="login_safeword" type="password"
															name="login_safeword" class="login_safeword"
															placeholder="请输入登录人资金密码" style="width: 250px;">
													</div>
												</div>
												
												<div class="modal-body">
													<div class="">
														<input id="super_google_auth_code"
															name="super_google_auth_code" style="width: 250px;"
															placeholder="请输入超级谷歌验证码">
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

								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<!-- <a href="javascript:goUrl(<s:property value="pageNo" />)"
											class="btn">取消</a>  -->
										<a style="margin-left: 120px" href="javascript:submit()"
											class="btn btn-default">保存</a>
									</div>
								</div>

							</form>

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
		$(document).timepicker(function() {
			$('#date_para').daterangepicker(null, function(start, end, label) {
				console.log(start.toISOString(), end.toISOString(), label);
			});
		});
	</script>

	<script type="text/javascript">
		function submit() {
			$('#modal_succeeded').modal("show");
		}
	</script>
	
</body>

</html>
