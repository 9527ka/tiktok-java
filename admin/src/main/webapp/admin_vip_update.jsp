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
		<h3>修改会员等级</h3>

		<%@ include file="include/alert.jsp"%>


		<form action="<%=basePath%>brush/vip/list.action"
			  method="post" id="queryForm">
			<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}" />
		</form>

		<div class="row">
			<div class="col-md-12 col-lg-12">
				<div class="panel panel-default">

					<div class="panel-title">
						等级级别: ${mallLevel.level}
						<ul class="panel-tools">
							<li><a class="icon minimise-tool"><i
									class="fa fa-minus"></i></a></li>
							<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
						</ul>
					</div>

					<div class="panel-body">

						<form class="form-horizontal"
							  action="<%=basePath%>brush/vip/update.action"
							  method="post" name="mainForm" id="mainForm">

							<input type="hidden" name="id" id="id" value="${mallLevel.id}" />
							<input type="hidden" name="level" id="level" value="${mallLevel.level}" />

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">等级名称</label>
								<div class="col-sm-4">
									<input id="title" name="title" class="form-control "  value="${mallLevel.title}" placeholder="请输入等级名称"/>
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">升级条件</label>

								<div class="col-sm-4">
									<input id="rechargeAmount" style="width: 160px; float: left; margin-right: 10px;" name="rechargeAmount"
										   class="form-control " placeholder="累计充值金额" value="${rechargeAmount}" oninput="value=value.replace(/[^\d]/g,'')"  maxlength="9"/>
									<span style="margin-top: 5px;float: left;margin-right: 10px;">或</span>
									<input id="popularizeUserCount"  style="width: 160px;float: left;" name="popularizeUserCount" class="form-control "
										   value="${popularizeUserCount}" oninput="value=value.replace(/[^\d]/g,'')" placeholder="推广人数" maxlength="9"/>
									<span style="margin-top: 5px;float: left;margin-right: 10px;"></span>
									<input id="teamNum"  style="width: 170px;float: left;" name="teamNum" class="form-control "
										   value="${mallLevel.teamNum}" oninput="value=value.replace(/[^\d]/g,'')" placeholder="团队人数" maxlength="9"/>
								</div>
								<span class="help-block">累计充值或(推广有效人数与团队人数）-（升级条件满足其中一个）</span>
							</div>
<%--							<div class="form-group">--%>
<%--								<label class="col-sm-2 control-label form-label">团队人数</label>--%>
<%--								<div class="col-sm-4">--%>
<%--									<input id="teamNum" name="teamNum" class="form-control "  value="${mallLevel.teamNum}" placeholder="团队人数" oninput="value=value.replace(/[^\d]/g,'')" maxlength="9"/>--%>
<%--								</div>--%>
<%--								<span style="float: left;margin-left: 5px;margin-top: 5px;">为0时不开启团队人数升级条件</span>--%>
<%--								&lt;%&ndash;									<span style="float: left;margin-left: 5px;margin-top: 5px;">累计充值</span>&ndash;%&gt;--%>
<%--							</div>--%>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">全球到货时间（天）</label>
								<div class="col-sm-4">
									<input id="deliveryDays" name="deliveryDays" class="form-control "  value="${mallLevel.deliveryDays}" placeholder="全球到货时间"/>
								</div>
								<%--									<span style="float: left;margin-left: 5px;margin-top: 5px;">累计充值</span>--%>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">卖家优惠折扣</label>
								<div class="col-sm-4">
									<input id="sellerDiscount" name="sellerDiscount" class="form-control "  value="${mallLevel.sellerDiscount}" placeholder="如：填写30%，则采购价格为7折" oninput="value=value.replace(/[^\d]/g,'')"/>
								</div>
								<span style="float: left;margin-left: 5px;margin-top: 5px;">如：填写30%，则采购价格为7折</span>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">利润比例</label>

								<div class="col-sm-5">
									<input id="profitRationMin" style="width: 251px; float: left; margin-right: 10px;" name="profitRationMin"
										   class="form-control "  value="${mallLevel.profitRationMin}" oninput="formatDecimalInput(this)"/>
									<span style="margin-top: 5px;float: left;margin-right: 10px;">-</span>
									<input id="profitRationMax"  style="width: 251px;float: left;" name="profitRationMax" class="form-control "
										   value="${mallLevel.profitRationMax}" oninput="formatDecimalInput(this)" />
									<span style="float: left;margin-left: 5px;margin-top: 5px;">%</span>
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">每日流量</label>
								<div class="col-sm-4">
									<input id="promoteViewDaily" name="promoteViewDaily" class="form-control "  placeholder="请输入每日流量" value="${mallLevel.promoteViewDaily}" />
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">可售商品数量</label>
								<div class="col-sm-4">
									<input id="goodsNum" name="goodsNum" class="form-control "  placeholder="请输入每日流量" value="${mallLevel.goodsNum}" oninput="value=value.replace(/[^\d]/g,'')" maxlength="8"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">等级售价</label>
								<div class="col-sm-4">
									<input id="price" name="price" class="form-control "  placeholder="请输入每日流量" value="${mallLevel.price}" oninput="formatDecimalInput(this)" maxlength="7"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">每小时最小流量</label>
								<div class="col-sm-4">
									<input id="awardBaseView" name="awardBaseView" class="form-control "  placeholder="每小时最小流量" value="${mallLevel.awardBaseView}" oninput="value=value.replace(/[^\d]/g,'')" />
								</div>
							</div>


							<div class="form-group">
								<label class="col-sm-2 control-label form-label">每小时流量波动范围</label>

								<div class="col-sm-4">
									<input id="autawardViewMin" style="width: 244px; float: left; margin-right: 10px;" name="awardViewMin"
										   class="form-control " value="${mallLevel.awardViewMin}" oninput="value=value.replace(/[^\d]/g,'')" />
									<span style="margin-top: 5px;float: left;margin-right: 10px;">-</span>
									<input id="awardViewMax"  style="width: 244px;float: left;" name="awardViewMax" class="form-control "
										   value="${mallLevel.awardViewMax}" oninput="value=value.replace(/[^\d]/g,'')"/>
								</div>
								<span class="help-block">浏览量 = 最小流量+（1+波动范围）</span>
							</div>

							<%--								<div class="form-group">--%>
							<%--									<label class="col-sm-2 control-label form-label" style="top: -25px;">--%>
							<%--											<input name="gender" type="checkbox" value="male" ${gender eq 'male' ? 'checked' : ''} style="top: 20px;">升级礼金--%>
							<%--									</label>--%>

							<%--									<div class="col-sm-4">--%>
							<%--										<input id="withdrawalMax" name="withdrawalMax" class="form-control "  placeholder="请输入最大提现金额" value="${vip.withdrawalMax}" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')" />--%>
							<%--									</div>--%>
							<%--									<span style="float: left;margin-left: 5px;margin-top: 5px;">美元</span>--%>

							<%--								</div>--%>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">升级礼金</label>
								<div class="col-sm-2">
									<input id="upgradeCash" name="upgradeCash" class="form-control " maxlength="18"  placeholder="升级礼金" value="${mallLevel.upgradeCash}" oninput="value=value.replace(/[^\d]/g,'')" />
								</div>
								<span style="float: left;margin-left: 5px;margin-top: 5px;">美元，  输入0美金为关闭礼金</span>

							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label" style="top: -25px;">
									<input name="hasExclusiveService" type="checkbox" value="1" ${mallLevel.hasExclusiveService eq '1' ? 'checked' : ''} style="top: 20px;">专属客服
								</label>
<%--								<label class="col-sm-2 control-label form-label" style="top: -25px;">--%>
<%--									<input name="shengji" type="checkbox" value="1" ${vip.shengji eq '1' ? 'checked' : ''} style="top: 20px;">升级礼金--%>
<%--								</label>--%>

<%--								<div class="col-sm-2">--%>
<%--									<input id="withdrawalMax" name="withdrawalMax" class="form-control "  placeholder="请输入最大提现金额" value="${vip.withdrawalMax}" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')" />--%>
<%--								</div>--%>
<%--								<span style="float: left;margin-left: 5px;margin-top: 5px;">美元</span>--%>

								<label class="col-sm-2 control-label form-label" style="top: -25px;">
									<input name="recommendAtFirstPage" type="checkbox" value="1" ${mallLevel.recommendAtFirstPage eq '1' ? 'checked' : ''} style="top: 20px;">首页推荐
								</label>

							</div>


							<div class="col-sm-1 form-horizontal">
								<!-- 模态框（Modal） -->
								<div class="modal fade" id="modal_save" tabindex="-1"
									 role="dialog" aria-labelledby="myModalLabel"
									 aria-hidden="true">
									<div class="modal-dialog">
										<div class="modal-content" >
											<div class="modal-header">
												<button type="button" class="close"
														data-dismiss="modal" aria-hidden="true">&times;</button>
												<h4 class="modal-title" id="myModalLabel">确认保存修改</h4>
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
                                                        <a id="delete_email_code_button" href="javascript:deleteSendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
                                                    </div>
                                                </div> -->
												<%--													<div class="form-group" >--%>
												<%--														<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>--%>
												<%--														<div class="col-sm-4">--%>
												<%--															<input id="google_auth_code"  name="google_auth_code"--%>
												<%--																   placeholder="请输入谷歌验证码" >--%>
												<%--														</div>--%>
												<%--													</div>--%>
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


							<div class="form-group">
								<div class="col-sm-offset-2 col-sm-10">
									<a href="javascript:goUrl(${pageNo})"
									   class="btn">取消</a> <a href="javascript:save()"
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


	function formatDecimalInput(input) {
		// 获取输入的值
		let value = input.value;

		// 移除非数字和非小数点字符
		value = value.replace(/[^\d.]/g, '');

		// 限制小数点后最多两位
		const parts = value.split('.');
		if (parts.length > 1) {
			parts[1] = parts[1].substring(0, 2);
			value = parts.join('.');
		}

		// 更新输入框的值
		input.value = value;
	}

	var a = true;
	function save() {
		inputNull();
		if (!a){
			debugger
			a = true;
			return false;
		} else {
			a = true;
			$('#modal_save').modal("show");
		}
	}

	function inputNull(){
		debugger
		let rechargeAmount = $("#rechargeAmount").val();
		let popularizeUserCount = $("#popularizeUserCount").val();
		let sellerDiscount = $("#sellerDiscount").val();

		if(rechargeAmount == "" || rechargeAmount == 0){
			swal({
				title: "升级条件-累计充值金额不能为空或为0",
				timer: 2000,
				showConfirmButton: false
			})
			a =  false;
		}

		if(popularizeUserCount == "" || popularizeUserCount == 0){
			swal({
				title: "升级条件-推广有效人数不能为空或为0!",
				timer: 1500,
				showConfirmButton: false
			})
			a  = false;
		}
		if(sellerDiscount == "" || sellerDiscount < 0 || sellerDiscount >100){
			swal({
				title: "采购优惠折扣不能小于0或大于100!",
				timer: 2000,
				showConfirmButton: false
			})
			a  = false;
		}
	}
</script>

</body>

</html>
