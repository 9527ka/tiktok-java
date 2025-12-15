<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

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
			<h3>交易所_用户管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminUserAction!list.action" method="post" id="queryForm">
				<input type="hidden" name="pageNo" id="pageNo"/>
				<input type="hidden" name="name_para" id="name_para"/>
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改用户
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminUserAction!update.action"
								method="post" name="mainForm" id="mainForm">
								
								<input type="hidden" name="id" id="id" value="${id}"/>
				                <input type="hidden" name="name_para" id="name_para"/>
				                <input type="hidden" name="rolename_para" id="rolename_para"/>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">用户名</label>
									<div class="col-sm-5">
										<input id="username" name="username" class="form-control" readonly="readonly" value="${username}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">登录权限</label>
									<div class="col-sm-4">
										<select id="login_authority" name = "login_authority" class="form-control" >
										   <option value="true" <c:if test="${login_authority == 'true'}">selected="true"</c:if> >正常</option>
										   <option value="false" <c:if test="${login_authority == 'false'}">selected="true"</c:if> >限制登录</option>
										</select>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">提现权限</label>
									<div class="col-sm-4">
										<select id="withdraw_authority" name="withdraw_authority" class="form-control">
										   <option value="true" <c:if test="${withdraw_authority == 'true'}">selected="true"</c:if> >正常</option>
										   <option value="false" <c:if test="${withdraw_authority == 'false'}">selected="true"</c:if> >限制提现</option>
										</select>											
										<span class="help-block">演示账号该设置不生效，默认无提现权限</span>
									</div>									
								</div>
							
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">是否业务锁定</label>									
									<div class="col-sm-4">									
										<select id="enabled" name="enabled" class="form-control">
										   <option value="true">正常</option>
										   <option value="false">业务锁定（登录不受影响，锁定后无法购买订单和提现）</option>
										</select>									
									</div>
								</div>
																
								<div class="form-group">
									<label for="input002" class="col-sm-2 control-label form-label">备注</label>
									<div class="col-sm-5">											
										<input id="remarks" name="remarks" class="form-control  input-lg" rows="3" cols="10" value="${remarks}"/>
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
