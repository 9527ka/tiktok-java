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
			<h3>借贷配置</h3>
			
			<%@ include file="include/alert.jsp"%>


			<form action="<%=basePath%>mall/loan/config/list.action"
				  method="post" id="queryForm">
				<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}" />
			</form>

			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>mall/loan/config/updateLoanConfig.action"
								method="post" name="mainForm" id="mainForm">

								<input type="hidden" name="id" id="id" value = "${loanConfig.id}"/>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">贷款金额范围</label>
									<div class="col-sm-5">
										<input id="amountMin" style="width: 200px; float: left; margin-right: 10px;" name="amountMin" class="form-control "  placeholder="最小百分比" value="${loanConfig.amountMin}" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')" />
											<span style="margin-top: 5px;float: left;margin-right: 10px;">-</span>
										<input id="amountMax"  style="width: 200px;float: left;" name="amountMax" class="form-control "
											   placeholder="最大百分比" value="${loanConfig.amountMax}"
											   onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')"
										/>
										<span style="float: left;margin-left: 5px;margin-top: 5px;">usdt</span>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">贷款每日利率</label>
									<div class="col-sm-5">
										<input id="rate" style="width: 200px; float: left; margin-right: 10px;" name="rate" class="form-control "  placeholder="最小百分比" value="${loanConfig.rate}" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')" />
										<span style="float: left;margin-left: 5px;margin-top: 5px;">%</span>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label"> 违约每日利率</label>
									<div class="col-sm-5">
										<input id="defaultRate" style="width: 200px; float: left; margin-right: 10px;" name="defaultRate" class="form-control "  placeholder="最小百分比" value="${loanConfig.defaultRate}" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')" />
										<span style="float: left;margin-left: 5px;margin-top: 5px;">%</span>
									</div>
								</div>
								<div class="form-group" style="display: flex; align-items: center;">
									<label class="col-sm-2 control-label form-label" style="float: left;margin-left: 5px;">可配置天数</label>

									<div style="display: flex; align-items: center;margin-right: 12px;">
										<input name='day' type="checkbox" value="1">1天
									</div>
									<div style="display: flex; align-items: center;margin-right: 12px;">
										<input name='day' type="checkbox" value="10">10天
									</div>
									<div style="display: flex; align-items: center;margin-right: 12px;">
										<input name='day' type="checkbox"  value="20" >20天
									</div>
									<div style="display: flex; align-items: center;margin-right: 12px;">
										<input name='day' type="checkbox"  value="30"/>30天
									</div>
									<div style="display: flex; align-items: center;margin-right: 12px;">
										<input name='day' type="checkbox"  value="60"/>60天
									</div>
									<div style="display: flex; align-items: center;margin-right: 12px;">
										<input name='day' type="checkbox"  value="90"/>90天
									</div>
									<div style="display: flex; align-items: center;margin-right: 12px;">
										<input name='day' type="checkbox"  value="120"/>120天
									</div>
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
<%--										<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
<%--															|| security.isResourceAccessible('OP_LOANCOFIG_OPERATE')}">--%>
														<a href="javascript:save()"
																	 class="btn btn-default">保存</a>
<%--										</c:if>--%>
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

		var day = '${loanConfig.lendableDays}';

		var a = true;
		function save() {
			inputNull();
			if (!a){
				a = true;
				return false;
			} else {
				a = true;
				$('#modal_save').modal("show");
			}

		}

		function inputNull(){
			let amountMin = parseFloat($("#amountMin").val()); // 将输入的值转换为浮点数
			let amountMax = parseFloat($("#amountMax").val());

			if(amountMin >= amountMax){
				swal({
					title: "贷款金额最小值不能大于等于贷款金额最大值!",
					timer: 1500,
					showConfirmButton: false
				})
				a =  false;
			}

		}

		$(":checkbox[name='day']").prop("checked",false);
		var ck_val = day.split(",");
		$.each(ck_val,function (index,val){
			$(":checkbox[name='day'][value="+val+"]").prop("checked",true);
		});



	</script>

</body>

</html>
