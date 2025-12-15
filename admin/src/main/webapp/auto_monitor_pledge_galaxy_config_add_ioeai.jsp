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
			<h3>质押2.0配置</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminPledgeGalaxyConfigAction!list.action"
				method="post" id="queryForm">

				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" >
				<input type="hidden" name="name_para" id="name_para" value="${name_para}" >
				
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							新增 质押2.0配置
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminPledgeGalaxyConfigAction!add.action"
								method="post" name="mainForm" id="mainForm">
								
								<input type="hidden" name="id" id="id" value="${id}">
								<input type="hidden" name="title_img" id="title_img" value="${title_img}">
								<input type="hidden" name="content_img" id="content_img" value="${content_img}">
								
								<h5>基础信息</h5>
								<p class="ballon color1">
									代理商或用户(UID) 
									<br /> 全局只设置代理商,表示代理线下所有用户质押配置。 
									<br /> 优先级为个人>代理>全局
								</p>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">代理商或用户UID(*)</label>
									<div class="col-sm-3">
										<input id="usercode" name="usercode" class="form-control " value="${usercode}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">参与金额最小值(*)</label>
									<div class="col-sm-3">
										<input id="pledge_amount_min" name="pledge_amount_min" class="form-control " value="${pledge_amount_min}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">参与金额最大值(*)</label>
									<div class="col-sm-3">
										<input id="pledge_amount_max" name="pledge_amount_max" class="form-control " value="${pledge_amount_max}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">有效下级质押金额最小值(*)</label>
									<div class="col-sm-3">
										<input id="valid_recom_pledge_amount_min" name="valid_recom_pledge_amount_min" class="form-control " value="${valid_recom_pledge_amount_min}" />
									</div>
								</div>
								
								<p class="ballon color1">
									静态收益原力值 
									<br /> 格式示范：
									<br /> 50-5000:1#0.006;7#0.007;15#0.008;30#0.009;90#0.01|5001-30000:1#0.01;7#0.011;15#0.012;30#0.013;90#0.014|
									<br /> 30001-50000:1#0.011;7#0.012;15#0.013;30#0.014;90#0.015|50001-100000:1#0.014;7#0.015;15#0.016;30#0.017;90#0.018|
									<br /> 100001-1000000:1#0.016;7#0.017;15#0.018;30#0.019;90#0.02
									<br /> 举例说明：50-5000:1#0.006;7#0.007;15#0.008;30#0.009;90#0.01
									<br /> 如果用户质押金额为 50到5000 USDT，选择1天的质押周期，每次结算可以获得质押金额0.6%的利润
								</p>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">静态收益原力值(*)</label>
									<div class="col-sm-7">
										<input id="static_income_force_value" name="static_income_force_value" class="form-control " value="${static_income_force_value}" />
									</div>
								</div>
								
								<p class="ballon color1">
									动态收益助力值 
									<br /> 格式示范：
									<br /> 3;5000-20000;0.001|5;20001-50000;0.002|10;50001-100000;0.003|15;100001-200000;0.004|20;200001-1000000;0.005
									<br /> 青铜级|白银级|黄金级|铂金级|钻石级
									<br /> 举例说明：3;5000-20000;0.001
									<br /> 青铜级，如果用户直属下线人数大于等于3人，并且质押总金额达到5000-20000 USDT，增加质押金额的0.1%助力值收益比例
									<br /> 下级质押金额超过 配置的[有效下级质押金额最小值] 才算有效下级
								</p>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">动态收益助力值(*)</label>
									<div class="col-sm-7">
										<input id="dynamic_income_assist_value" name="dynamic_income_assist_value" class="form-control " value="${dynamic_income_assist_value}" />
									</div>
								</div>
								
								<p class="ballon color1">
									团队收益利润率 
									<br /> 格式示范： 
									<br /> 3;5000-20000;0.03#5;20001-50000;0.05#10;50001-100000;0.07#15;100001-200000;0.09#20;200001-1000000;0.12
									<br /> 青铜级|白银级|黄金级|铂金级|钻石级
									<br /> 举例说明：3;5000-20000;0.03
									<br /> 青铜级，如果用户直属下线人数大于等于3人，并且质押总金额达到5000-20000 USDT，每天获得团队静态收益总利润的3%；
								</p>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">团队收益利润率(*)</label>
									<div class="col-sm-7">
										<input id="team_income_profit_ratio" name="team_income_profit_ratio" class="form-control " value="${team_income_profit_ratio}" />
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
														<input id="login_safeword" type="password"
															name="login_safeword" class="login_safeword"
															placeholder="请输入登录人资金密码" style="width: 250px;">
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
										<a href="javascript:goUrl(${pageNo})" class="btn">取消</a> 
										<a href="javascript:submit()" class="btn btn-default">保存</a>
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
