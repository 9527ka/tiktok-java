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
			<h3>系统用户管理</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminSystemUserAction!list.action"
				method="post" id="queryForm">
				<s:hidden name="pageNo" id="pageNo"></s:hidden>
				<s:hidden name="name_para" id="name_para"></s:hidden>
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改用户
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminSystemUserAction!update.action"
								method="post" name="mainForm" id="mainForm">
								<s:hidden name="username_para" id="username_para"></s:hidden>
								<s:hidden name="secAdmin_id" id="secAdmin_id"></s:hidden>
								
								<h5>基础信息</h5>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">用户名</label>
									<div class="col-sm-5">
										<s:textfield id="username" name="username"
											cssClass="form-control " readonly="true" />
									</div>
								</div>
								
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">角色</label>
									<div class="col-sm-2">
											<div class="controls">
												<s:select id="roleName" cssClass="form-control " name="roleName" 
													list="role_map" listKey="key" headerKey=""
													headerValue="请选择" listValue="value" value="roleName" />
											</div>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">登录权限</label>
									<div class="col-sm-2">
										<s:select id="enabled" cssClass="form-control "
											name="enabled" list="#{true:'开启',false:'关闭'}"
											listKey="key" listValue="value"  value="enabled" />
											
										
									</div>
								</div>

								<div class="form-group">
									<label for="input002" class="col-sm-2 control-label form-label">备注</label>
									<div class="col-sm-6">

										<s:textarea name="remarks" id="remarks" 
											cssClass="form-control  input-lg" rows="4" cols="20" />

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
			swal({
				title : "是否保存?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("mainForm").submit();
			});

		}
	</script>
</body>
</html>