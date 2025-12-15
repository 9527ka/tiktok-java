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
			<h3>系统用户管理</h3>
			
			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminSystemUserAction!list.action" method="post"
								id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
									
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="username_para" name="username_para"
													cssClass="form-control " placeholder="用户名" /> -->
												<input id="username_para" name="username_para" class="form-control " placeholder="用户名" value="${username_para}"/>
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>
								
							</form>

						</div>

					</div>
				</div>
			</div>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						<a
							href="<%=basePath%>normal/adminSystemUserAction!toAdd.action?username_para=${username_para}"
							class="btn btn-light" style="margin-bottom: 10px"><i
							class="fa fa-pencil"></i>新增系统用户</a>

						<a href="javascript:addUser()"
								class="btn btn-light" style="margin-bottom: 10px"><i
								class="fa fa-pencil"></i>批量生成200平台账号</a>


						<div class="panel-body">
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>用户名</td>
										<td>角色</td>
										<td>邮箱</td>
										<td>谷歌验证器</td>
										<td>登录权限</td>
										<td>备注</td>
										<td style="width: 130px;"></td>
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.username}</td>
											<td>
												<c:choose>
												   <c:when test="${item.id == 'SADMIN'}">
												   		管理员
												   </c:when>
												   <c:otherwise>
												   		${role_map.get(item.roleName)}
												   </c:otherwise>
												</c:choose>
											</td>
											<td>${item.email}</td>
											<td>
												<c:if test="${item.google_auth_bind}">
													<span class="right label label-success">已绑定</span>												
												</c:if>
												<c:if test="${!item.google_auth_bind}">
													未绑定
												</c:if>
											</td>
											<td>
												<c:if test="${item.enabled}">开启</c:if>
												<c:if test="${!item.enabled}">
													<span class="right label label-danger">关闭</span>
												</c:if>
											</td>
											<td>${item.remarks}</td>
											
											<c:if test="${item.id != 'SADMIN'}">
												<td>
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> 
															<span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
															<li><a href="<%=basePath%>normal/adminSystemUserAction!toUpdate.action?secAdmin_id=${item.id}&username_para=${username_para}">修改</a></li>
															<li><a href="<%=basePath%>normal/adminSystemUserAction!toUpdatePassword.action?secAdmin_id=${item.id}&username_para=${username_para}">修改密码</a></li>
															<li><a href="<%=basePath%>normal/adminSystemUserAction!toUpdateSafePassword.action?secAdmin_id=${item.id}&username_para=${username_para}">修改资金密码</a></li>
															<li><a href="<%=basePath%>normal/adminGoogleAuthAction!toUpdateGoogleAuth.action?username=${item.username}">谷歌验证器</a></li>
															<li><a href="javascript:del('${item.id}')">删除</a></li>
														</ul>
													</div>
												</td>
											</c:if>
											
										</tr>
									<!-- </s:iterator> -->
									</c:forEach>
								</tbody>
								
							</table>
							
							<%@ include file="include/page_simple.jsp"%>
							
							<!-- <nav> -->
						</div>

					</div>
					<!-- End Panel -->

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
	
	<form action="<%=basePath%>normal/adminSystemUserAction!delete.action"
		method="post" id="deleteform">
		
		<input type="hidden" name="secAdmin_id" id="delete_id" value="${secAdmin_id}"/>
		
		<div class="col-sm-1 form-horizontal">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_delete" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
					
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">确认删除</h4>
						</div>
						
						<div class="modal-body">
						
							<div class="form-group">
								<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
								<div class="col-sm-4">
									<input id="login_safeword" type="password" name="login_safeword" class="login_safeword"
										placeholder="请输入登录人资金密码">
								</div>
							</div>
							
							<div class="form-group">
								<label for="input002" class="col-sm-3 control-label form-label">超级谷歌验证码</label>
								<div class="col-sm-4">
									<input id="super_google_auth_code" name="super_google_auth_code" placeholder="请输入超级谷歌验证码">
								</div>
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
	</form>

	<div class="form-group">

		<form 	action="<%=basePath%>normal/adminSystemUserAction!addUser.action" method="post"
			  method="post" id="succeededForm5">

			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="modal_set" tabindex="-1" role="dialog"
					 aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">

							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
								<h4 class="modal-title">用户密码</h4>
							</div>

							<div class="modal-body">
								<div class="">
									<input id="password" name="password"
										   class="form-control" value="${password}" placeholder="请输入生成的用户密码">
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

		</form>

	</div>

	<script>
		function del(id) {
			$("#delete_id").val(id);
			$('#modal_delete').modal("show");	
		}

		function addUser(){
			$('#modal_set').modal("show");
		}
	</script>
	
</body>

</html>
