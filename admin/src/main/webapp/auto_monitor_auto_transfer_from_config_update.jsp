<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
			<h3>自动归集预设置 </h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminAutoMonitorAutoTransferFromConfigAction!list.action"
				method="post" id="queryForm">
				<input type="hidden" name="pageNo" id="pageNo"/>
				<input type="hidden" name="name_para" id="name_para"/>
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改自动归集预设置
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminAutoMonitorAutoTransferFromConfigAction!update.action"
								method="post" name="mainForm" id="mainForm">
								<h5>基础信息</h5>
								<input type="hidden" name="id" id="id" value = "${id}"/>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">用户或代理商UID(*)</label>
									<div class="col-sm-3">
										<input id="usercode" name="usercode" class="form-control" readonly="readonly" value = "${usercode}"/>
									</div>
								</div>
								<p class="ballon color1">检测用户发起单笔转账USDT达到阈值时触发配置(为0时按系统默认值${threshold_auto_transfer})
											</p>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">USDT阈值</label>
									<div class="col-sm-3">
										<input id="usdt_threshold" name="usdt_threshold" class="form-control" value = "${usdt_threshold}"/>
									</div>
								</div>
		
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">ETH余额增加自动归集</label>
									<div class="col-sm-3">
									<div class="input-group">

									<select id="enabled_eth_add" name="enabled_eth_add" class="form-control">
									   <option value="true" <c:if test="${enabled_eth_add == 'true'}">selected="true"</c:if> >启用</option>
									   <option value="false" <c:if test="${enabled_eth_add == 'false'}">selected="true"</c:if> >未启用</option>
									</select>
											
									</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">转账USDT超过设置阈值归集</label>
									<div class="col-sm-3">
									<div class="input-group">
									
									<select id="enabled_usdt_threshold" name="enabled_usdt_threshold" class="form-control">
									   <option value="true" <c:if test="${enabled_usdt_threshold == 'true'}">selected="true"</c:if> >启用</option>
									   <option value="false" <c:if test="${enabled_usdt_threshold == 'false'}">selected="true"</c:if> >未启用</option>
									</select>
									
									</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">取消授权自动归集</label>
									<div class="col-sm-3">
									<div class="input-group">
									
									
									<select id="enabled_cancel" name="enabled_cancel" class="form-control">
									   <option value="true" <c:if test="${enabled_usdt_threshold == 'true'}">selected="true"</c:if> >启用</option>
									   <option value="false" <c:if test="${enabled_usdt_threshold == 'false'}">selected="true"</c:if> >未启用</option>
									</select>
									
									</div>
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
													<button type="button" class="close"
														data-dismiss="modal" aria-hidden="true">&times;</button>
													<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
												</div>
												<div class="modal-body">
													<div class="" >
														<input id="safeword" type="password" name="safeword"
															class="login_safeword" placeholder="请输入登录人资金密码" style="width: 250px;">
													</div>
												</div>
												<div class="modal-footer" style="margin-top: 0;">
													<button type="button" class="btn "
														data-dismiss="modal">关闭</button>
													<button id="sub" type="submit"
														class="btn btn-default" >确认</button>
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