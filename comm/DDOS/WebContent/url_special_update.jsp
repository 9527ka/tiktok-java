<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="include/head.jsp"%>
</head>
<body>
	<%@ include file="include/loading.jsp"%>
	<%@ include file="include/top.jsp"%>
	<%@ include file="include/menu_left.jsp"%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="content">



		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="container-default">
			<h3>特殊URL管理</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminUrlSpecialAction!list.action"
				method="post" id="queryForm">
				<s:hidden name="pageNo" id="pageNo"></s:hidden>
				<s:hidden name="ip_para" id="ip_para"></s:hidden>
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改URL
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminUrlSpecialAction!update.action"
								method="post" name="mainForm" id="mainForm">
								<s:hidden name="ip_para" id="ip_para"></s:hidden>
								<s:hidden name="id" id="id"></s:hidden>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">URL</label>
									<div class="col-sm-5">
										<s:textfield id="url" name="url"
											cssClass="form-control " />
									</div>
								</div>
								
								<div class="form-group">
									<label for="input002" class="col-sm-2 control-label form-label">备注</label>
									<div class="col-sm-6">

										<s:textarea name="remarks" id="remarks"
											cssClass="form-control  input-lg" rows="4" cols="20" />

									</div>
								</div>
								<div class="col-sm-1">
									<!-- 模态框（Modal） -->
									<div class="modal fade" id="modal_succeeded" tabindex="-1"
										role="dialog" aria-labelledby="myModalLabel"
										aria-hidden="true">
										<div class="modal-dialog">
											<div class="modal-content" >
												<div class="modal-header">
													<button type="button" class="close"
														data-dismiss="modal" aria-hidden="true">&times;</button>
													<h4 class="modal-title" id="myModalLabel">确认修改</h4>
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
															<button id="email_code_button" 
																	class="btn btn-light " onClick="sendCode();" >获取验证码</button>
															<a id="email_code_button" href="javascript:sendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
														</div>
													</div>
													<div class="form-group" >
														<label for="input002" class="col-sm-3 control-label form-label">超级谷歌验证码</label>
														<div class="col-sm-4">
															<input id="super_google_auth_code"  name="super_google_auth_code"
																 placeholder="请输入超级谷歌验证码" >
														</div>
													</div> -->
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
										<a href="javascript:goUrl(<s:property value="pageNo" />)"
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