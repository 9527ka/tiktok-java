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
			<h3>矿池收益规则</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form
				action="<%=basePath%>normal/adminMiningConfigAction!list.action"
				method="post" id="queryForm">
				<!-- <s:hidden name="pageNo" id="pageNo"></s:hidden> -->
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<!-- <s:hidden name="name_para" id="name_para"></s:hidden> -->
				<input type="hidden" name="name_para" id="name_para" value="${name_para}">
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改矿池收益规则
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminMiningConfigAction!update.action"
								method="post" name="mainForm" id="mainForm">
								
								<h5>基础信息</h5>
								<!-- <s:hidden name="id" id="id"></s:hidden> -->
								<input type="hidden" name="id" id="id" value="${id}">
								
								<p class="ballon color1">
									用户名(UID) <br /> 如果为空，则是全局默认参数，如果是代理商UID
									则表示代理线下所有用户参数。如果是用户UID，则代表单个用户参数。 <br /> 优先级为个人>代理>全局
								</p>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">用户或代理商UID(*)</label>
									<div class="col-sm-3">
										<!-- <s:textfield id="usercode" name="usercode"
											cssClass="form-control " readonly="true" /> -->
										<input id="usercode" name="usercode" class="form-control " readonly="readonly" value="${usercode}" />
									</div>
								</div>
								
								<p class="ballon color1">
									收益费率格式示范：100-5000;0.0025-0.003|5000-20000;0.005-0.0055|20000-50000;0.0055-0.0065|50000-9999999;0.0065-0.0075
									<br /> ⻔槛说明举例：100-5000;0.0025-0.003
									表示:如果客户的钱包USDT余额在100到5000USDT之间，每次结算可以获得0.25%到0.3%之间的利润 <br />
									⼀天有4次挖矿结算。 有可能是0.26%或者 0.29%，是随机区间
								</p>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">收益费率(*))</label>
									<div class="col-sm-7">
										<!-- <s:textfield id="config" name="config"
											cssClass="form-control " /> -->
										<input id="config" name="config" class="form-control " value="${config}" />
									</div>
								</div>
								
								<p class="ballon color1">
									上级返佣费率格式示范：0.0055-0.0065|0.005-0.0055|0.0025-0.003 <br />
									1级|2级|3级
								</p>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">上级返佣费率(*)</label>
									<div class="col-sm-7">
										<!-- <s:textfield id="config_recom" name="config_recom"
											cssClass="form-control " /> -->
										<input id="config_recom" name="config_recom" class="form-control " value="${config_recom}" />
									</div>
								</div>

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
														<input id="safeword" type="password" name="safeword"
															class="login_safeword" placeholder="请输入登录人资金密码"
															style="width: 250px;">
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
										<a href="javascript:goUrl(${pageNo})"
											class="btn">取消</a> <a href="javascript:submit()"
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
		function submit() {
			$('#modal_succeeded').modal("show");
		}
	</script>

</body>

</html>
