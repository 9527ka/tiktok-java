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
									<br /> 100-1000:1#0.008;10#0.009;30#0;90#0;180#0;300#0|1000-3000:1#0.008;10#0.009;30#0.01;90#0;180#0;300#0|
									<br /> 3000-6000:1#0.008;10#0.009;30#0.01;90#0.013;180#0;300#0|6000-10000:1#0.008;10#0.009;30#0.01;90#0.013;180#0.017;300#0|
									<br /> 10000-1000000:1#0.008;10#0.009;30#0.01;90#0.013;180#0.017;300#0.02
									<br /> &1#100;10#100;30#1000;90#3000;180#6000;300#10000
									<br /> 举例说明：100-1000:1#0.008;10#0.009;30#0;90#0;180#0;300#0
									<br /> 如果用户质押金额为 100到1000 USDT，选择1天的质押周期，每次结算可以获得质押金额0.8%的利润
									<br /> 举例说明（&号后的配置）：&1#100;10#100;30#1000;90#3000;180#6000;300#10000
									<br /> 质押1天和10天的最小质押金额为 100；质押30天的最小质押金额为 1000
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
									<br /> 3;0.002|6;0.004|12;0.006|24;0.008|48;0.01
									<br /> 举例说明：3;0.002
									<br /> 如果用户直属下线人数大于等于3人，增加质押金额的0.2%助力值收益比例
									<br /> 无论直属下级拥有多少人，但是选择质押1天都不能享受额外的动态收益
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
									<br /> 12:0.1|0.05|0.02#24:0.2|0.1|0.05#48:0.3|0.15|0.1
									<br /> 说明：
									<br /> 一星会员标准：邀请直属12人每日可获得 直属一级下级质押收益的 10% 、直属二级下级质押收益的 5% 、直属三级下级质押收益的 2% ；
									<br /> 二星会员标准：邀请直属24人每日可获得 直属一级下级质押收益的 20% 、直属二级下级质押收益的 10% 、直属三级下级质押收益的 5% ； 
									<br /> 三星会员标准：邀请直属48人每日可获得 直属一级下级质押收益的 30% 、直属二级下级质押收益的 15% 、直属三级下级质押收益的 10% ；
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
