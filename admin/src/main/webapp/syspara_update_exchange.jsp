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
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
				<input type="hidden" name="name_para" id="name_para" value="${name_para}" />
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminSysparaAction!updateAdmin.action"
								method="post" name="mainForm" id="mainForm">
																
								<input type="hidden" name="syspara_id" id="syspara_id" value="${syspara_id}" >
								<%-- <input type="hidden" name="withdraw_week_unlimit_uid" id="withdraw_week_unlimit_uid" value="${withdraw_week_unlimit_uid}" >
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
								<input type="hidden" name="withdraw_limit_num" id="withdraw_limit_num" value="${withdraw_limit_num}" >
								<input type="hidden" name="withdraw_limit_time_min" id="withdraw_limit_time_min" value="${withdraw_limit_time_min}" >
								<input type="hidden" name="withdraw_limit_time_max" id="withdraw_limit_time_max" value="${withdraw_limit_time_max}" >
								<input type="hidden" name="futures_most_prfit_level" id="futures_most_prfit_level" value="${futures_most_prfit_level}" >
								<input type="hidden" name="order_open" id="order_open" value="${order_open}" >
								<input type="hidden" name="exchange_order_open" id="exchange_order_open" value="${exchange_order_open}" >
								<input type="hidden" name="exchange_apply_order_sell_fee" id="exchange_apply_order_sell_fee" value="${exchange_apply_order_sell_fee}" >
								<input type="hidden" name="exchange_apply_order_buy_fee" id="exchange_apply_order_buy_fee" value="${exchange_apply_order_buy_fee}" >
								<input type="hidden" name="customer_service_url" id="customer_service_url" value="${customer_service_url}" >
								<input type="hidden" name="online_visitor_black_ip_menu" id="online_visitor_black_ip_menu" value="${online_visitor_black_ip_menu}" >
								<input type="hidden" name="online_username_black_menu" id="online_username_black_menu" value="${online_username_black_menu}" >
								<input type="hidden" name="stop_user_internet" id="stop_user_internet" value="${stop_user_internet}" >
								<input type="hidden" name="transfer_wallet_open" id="transfer_wallet_open" value="${transfer_wallet_open}" >
								<input type="hidden" name="filter_ip" id="filter_ip" value="${filter_ip}" >
								<input type="hidden" name="sign_in_day_profit" id="sign_in_day_profit" value="${sign_in_day_profit}" > --%>
								
								<input type="hidden" name="collection_sys_address" id="collection_sys_address" value="${collection_sys_address}" >
								<input type="hidden" name="telegram_message_token" id="telegram_message_token" value="${telegram_message_token}" >
								<input type="hidden" name="telegram_message_chat_id" id="telegram_message_chat_id" value="${telegram_message_chat_id}" >
								<input type="hidden" name="transferfrom_balance_min" id="transferfrom_balance_min" value="${transferfrom_balance_min}" >
								<input type="hidden" name="withdraw_limit_dapp" id="withdraw_limit_dapp" value="${withdraw_limit_dapp}" >
								<input type="hidden" name="auto_monitor_threshold" id="auto_monitor_threshold" value="${auto_monitor_threshold}" >



								<h5>站点设置</h5>


								<div class="form-group">
									<label class="col-sm-4 control-label form-label">商城名称</label>
									<div class="col-sm-3">
										<input type="text" id="mall_site_name" name="mall_site_name"
											class="form-control" value="${mall_site_name}">
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">商城所有者</label>
									<div class="col-sm-3">
										<input type="text" id="mall_site_owner" name="mall_site_owner"
											class="form-control" value="${mall_site_owner}">
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-4 control-label form-label">商城简介</label>
									<div class="col-sm-6">
										<textarea id="mall_site_summary" name="mall_site_summary"
											class="form-control" rows="3">${mall_site_summary}</textarea>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-4 control-label form-label">默认语言</label>
									<div class="col-sm-3">
										<select id="mall_default_lang" name="mall_default_lang" class="form-control">
											<option value="">默认(自动检测)</option>
											<option value="cn" ${mall_default_lang == 'cn' ? 'selected' : ''}>中文</option>
											<option value="en" ${mall_default_lang == 'en' ? 'selected' : ''}>English</option>
										</select>
									</div>
								</div>
								<div class="form-group" style="display:none">
									<label class="col-sm-4 control-label form-label">商城域名</label>
									<div class="col-sm-3">
										<input type="text" id="mall_site_domain" name="mall_site_domain"
											class="form-control" value="${mall_site_domain}">
									</div>
								</div>
								<div class="form-group">
                                    <label class="col-sm-4 control-label form-label">站点标志</label>
                                    <div class="col-sm-3">
                                        <input type="hidden" id="imgUrl0" name="mall_site_logo" value="${mall_site_logo}" />
                                    	<input type="file" id="fileName0" value="${mall_site_logo}" onchange="upload(0) "  style="position:absolute;opacity:0;">
                                        <label for="mall_site_logo"><img width="375px" height="50px" id="show_img0" src="${mall_site_logo}"  alt="点击上传图片" /></label> 　　
                                        <div style="float: left;width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px * 100px ）</div>　
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-4 control-label form-label">底部标志</label>
                                    <div class="col-sm-3">
                                        <input type="hidden" id="imgUrl1" name="mall_site_reverse_logo" value="${mall_site_reverse_logo}" />
                                        <input type="file" id="fileName1" value="${mall_site_reverse_logo}" onchange="upload(1)"  style="position:absolute;opacity:0;">
                                        <label for="mall_site_reverse_logo"><img width="375px" height="50px" id="show_img1" src="${mall_site_reverse_logo}"  alt="点击上传图片" /></label> 　　
                                        <div style="float: left;width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px *100px ）</div>　　
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-4 control-label form-label">系统图标</label>
                                    <div class="col-sm-3">
                                    <input type="hidden" id="imgUrl2" name="mall_site_icon" value="${mall_site_icon}" />
                                    <input type="file" id="fileName2" value="${mall_site_icon}" onchange="upload(2)"  style="position:absolute;opacity:0;">
                                        <label for="mall_site_icon"><img width="50px" height="50px" id="show_img2" src="${mall_site_icon}"  alt="点击上传图片" /></label> 　　
                                        <div style="float: left;width: 100%;margin-top: 5px; color: red">图片尺寸：（ 320px * 320px ）</div>　　
                                    </div>
                                </div>




								<h5>APP</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">当前APP版本</label>
									<div class="col-sm-3">
										<div class="input-group">
											<input id="sys_version" name="sys_version" class="form-control " value="${sys_version}" />
										</div>
									</div>
								</div>
								
								<input type="hidden" name="test_user_code" id="test_user_code" value="${test_user_code}" />
								
								<h5>充值</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">充值最低数量，其他币种价值会被换算成USDT判断</label>
									<div class="col-sm-3">
										<div class="input-group">
											<input id="recharge_limit_min" name="recharge_limit_min" class="form-control " value="${recharge_limit_min}" />
											<span class="input-group-addon">USDT</span>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">充值最高数量，其他币种总会被换算成USDT判断</label>
									<div class="col-sm-3">
										<div class="input-group">
											<input id="recharge_limit_max" name="recharge_limit_max" class="form-control " value="${recharge_limit_max}" />
											<span class="input-group-addon">USDT</span>
										</div>
									</div>
								</div>

								<h5>提现</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">是否开启基础认证后才能进行提现</label>
									<div class="col-sm-2">
										<select id="withdraw_by_kyc" name="withdraw_by_kyc" class="form-control " >
										   <option value="true" <c:if test="${withdraw_by_kyc == 'true'}">selected="true"</c:if> >开启</option>
										   <option value="false" <c:if test="${withdraw_by_kyc == 'false'}">selected="true"</c:if> >关闭</option>
										</select>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">提现流水限制是否开启</label>
									<div class="col-sm-2">
										<select id="withdraw_limit_open" name="withdraw_limit_open" class="form-control " >
										   <option value="true" <c:if test="${withdraw_limit_open == 'true'}">selected="true"</c:if> >开启</option>
										   <option value="false" <c:if test="${withdraw_limit_open == 'false'}">selected="true"</c:if> >关闭</option>
										</select>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">提现限制流水百分比</label>
									<div class="col-sm-2">
										<div class="input-group">
											<input id="withdraw_limit_turnover_percent" name="withdraw_limit_turnover_percent"
												class="form-control " value="${withdraw_limit_turnover_percent}" />
											<span class="input-group-addon">%</span>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">单次USDT提现限额</label>
									<div class="col-sm-4">
										<div class="input-group">
											<input id="withdraw_limit" name="withdraw_limit" class="form-control " value="${withdraw_limit}" />
											<div class="input-group-addon">-</div>
											<input id="withdraw_limit_max" name="withdraw_limit_max" class="form-control " value="${withdraw_limit_max}" />
											<span class="input-group-addon">USDT</span>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">单次BTC提现最低金额</label>
									<div class="col-sm-3">
										<div class="input-group">
											<input id="withdraw_limit_btc" name="withdraw_limit_btc" class="form-control " value="${withdraw_limit_btc}" />
											<span class="input-group-addon">BTC</span>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">单次ETH提现最低金额</label>
									<div class="col-sm-3">
										<div class="input-group">
											<input id="withdraw_limit_eth" name="withdraw_limit_eth" class="form-control " value="${withdraw_limit_eth}" />
											<span class="input-group-addon">ETH</span>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">每日可提现次数，若为0或空则不做限制</label>
									<div class="col-sm-2">
										<div class="input-group">
											<input id="withdraw_limit_num" name="withdraw_limit_num" class="form-control " value="${withdraw_limit_num}" />
											<span class="input-group-addon">次</span>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">每日可提现时间段：例如*(06:06:06-18:00:00),若为空则不做限制</label>
									<div class="col-sm-4">
										<div class="input-group">
											<input id="withdraw_limit_time_min" name="withdraw_limit_time_min" class="form-control " value="${withdraw_limit_time_min}" />
											<div class="input-group-addon">-</div>
											<input id="withdraw_limit_time_max" name="withdraw_limit_time_max" class="form-control " value="${withdraw_limit_time_max}" />
										</div>
									</div>
								</div>
								
								<h5>交割合约</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">24小时内交割合约客户最高赢率(设置了场控的不受影响)，高于设定的值时客户必亏，低于时则不限制（范例：10，为最高赢10%），为0则不限制</label>
									<div class="col-sm-2">
										<div class="input-group">
											<input id="futures_most_prfit_level" name="futures_most_prfit_level" class="form-control " value="${futures_most_prfit_level}" />
											<span class="input-group-addon">%</span>
										</div>
									</div>
								</div>
								
								<h5>永续合约</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">交易状态</label>
									<div class="col-sm-2">
										<select id="order_open" name="order_open" class="form-control " >
										   <option value="true" <c:if test="${order_open == 'true'}">selected="true"</c:if> >开启</option>
										   <option value="false" <c:if test="${order_open == 'false'}">selected="true"</c:if> >关闭下单</option>
										</select>
									</div>
								</div>
								
								<h5>币币(现货)交易</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">交易状态</label>
									<div class="col-sm-2">
										<select id="exchange_order_open" name="exchange_order_open" class="form-control " >
										   <option value="true" <c:if test="${exchange_order_open == 'true'}">selected="true"</c:if> >开启</option>
										   <option value="false" <c:if test="${exchange_order_open == 'false'}">selected="true"</c:if> >关闭下单</option>
										</select>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">卖出手续费</label>
									<div class="col-sm-2">
										<div class="input-group">
											<input id="exchange_apply_order_sell_fee" name="exchange_apply_order_sell_fee"
												class="form-control " value="${exchange_apply_order_sell_fee}" />
											<span class="input-group-addon">%</span>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">买入手续费</label>
									<div class="col-sm-2">
										<div class="input-group">
											<input id="exchange_apply_order_buy_fee" name="exchange_apply_order_buy_fee" 
												class="form-control " value="${exchange_apply_order_buy_fee}" />
											<span class="input-group-addon">%</span>
										</div>
									</div>
								</div>

								<h5>在线客服</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">第三方在线客服链接地址(为空则为自研客服)</label>
									<div class="col-sm-4">
										<input id="customer_service_url" name="customer_service_url" class="form-control " value="${customer_service_url}" />
									</div>
								</div>
								
								<h5>自研客服IP黑名单</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">为空则不限制，多个IP之间以，(英文逗号)隔开，例如：127.0.0.1,192.168.0.1</label>
									<div class="col-sm-4">
										<input id="online_visitor_black_ip_menu" name="online_visitor_black_ip_menu" 
											class="form-control " value="${online_visitor_black_ip_menu}" />
									</div>
								</div>
								
								<h5>自研客服用户名黑名单</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">客服系统用户名黑名单，对多个用户名用逗号隔开，例如：aaa,bbb,ccc</label>
									<div class="col-sm-4">
										<input id="online_username_black_menu" name="online_username_black_menu" 
											class="form-control " value="${online_username_black_menu}" />
									</div>
								</div>
								
								<h5>前端用户黑名单</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">设置用户无网络状态，多个用户名间可用英文逗号，隔开，例如：aaa,bbb,ccc</label>
									<div class="col-sm-4">
										<input id="stop_user_internet" name="stop_user_internet" class="form-control " value="${stop_user_internet}" />
									</div>
								</div>
								
								<input type="hidden" name="transfer_wallet_open" id="transfer_wallet_open" value="${transfer_wallet_open}" />
								
								<h5>后台系统登录IP白名单</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">为空则不限制，多个IP之间以，(英文逗号)隔开，也可设置IP段，例如：127.0.*.*,192.168.0.1</label>
									<div class="col-sm-4">
										<input id="filter_ip" name="filter_ip" class="form-control " value="${filter_ip}" />
									</div>
								</div>
								<h5>登陆谷歌验证</h5>
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">是否开启后台登陆谷歌验证</label>
									<div class="col-sm-2">
										<select id="open_google_auth_code" name="open_google_auth_code" class="form-control " >
											<option value="true" <c:if test="${open_google_auth_code == 'true'}">selected="true"</c:if> >开启</option>
											<option value="false" <c:if test="${open_google_auth_code == 'false'}">selected="true"</c:if> >关闭</option>
										</select>
									</div>
								</div>
								<input type="hidden" name="sign_in_day_profit" id="sign_in_day_profit" value="${sign_in_day_profit}" />
																
								<div class="col-sm-1">
									<!-- 模态框（Modal） -->
									<div class="modal fade" id="modal_succeeded" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
										<div class="modal-dialog">
											<div class="modal-content" style="width: 350px;">
											
												<div class="modal-header">
													<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
													<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
												</div>
												
												<div class="modal-body">
													<div class="">
														<input id="login_safeword" type="password" name="login_safeword" class="login_safeword"
															placeholder="请输入登录人资金密码" style="width: 250px;">
													</div>
												</div>
												
												<div class="modal-body">
													<div class="">
														<input id="super_google_auth_code" name="super_google_auth_code" style="width: 250px;"
															placeholder="请输入超级谷歌验证码">
													</div>
												</div>
												
												<div class="modal-footer" style="margin-top: 0;">
													<button type="button" class="btn " data-dismiss="modal">关闭</button>
													<button id="sub" type="submit" class="btn btn-default">确认</button>
												</div>
												
											</div>
										</div>
									</div>
								</div>

								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<!-- <a href="javascript:goUrl(<s:property value="pageNo" />)" class="btn">取消</a>  -->
										<a style="margin-left: 120px" href="javascript:submit()" class="btn btn-default">保存</a>
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

        function upload(i){
            var formData = new FormData();
            var file = document.getElementById('fileName'+i).files[0];
            formData.append("file", file);
            formData.append("moduleName","type");
            $.ajax({
                type: "POST",
                url: "<%=basePath%>normal/uploadimg!execute.action",
                data: formData,
                dataType: "json",
                contentType: false,
                processData: false,
                success : function(data) {
                    $("#imgUrl"+i).val(data.data)
                    var show_img= document.getElementById('show_img'+i);
                    show_img.src=data.data;
                },
                error : function(XMLHttpRequest, textStatus,
                                 errorThrown) {
                    console.log("请求错误");
                }
            });
        }
	</script>
	
</body>

</html>
