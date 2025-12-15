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
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>客服管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">
						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminCustomerAction!list.action" method="post"
								id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="username_para" name="username_para"
													cssClass="form-control " placeholder="用户名" /> -->
												<input id="username_para" name="username_para"
													class="form-control " placeholder="用户名" value="${username_para}" />
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
							href="<%=basePath%>normal/adminCustomerAction!toAdd.action?username_para=${username_para}"
							class="btn btn-light" style="margin-bottom: 10px"><i
							class="fa fa-pencil"></i>新增客服</a>
							
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>后台登录用户名</td>
										<td>在线状态</td>
										<td>最后上线时间</td>
										<td>最后分配时间</td>
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
													<c:when test="${item.online_state == 1}">
														<span class="right label label-success">在线</span>
													</c:when>
													<c:otherwise>
														下线
													</c:otherwise>
												</c:choose>
		                                    </td>
											<!-- <td><s:date name="last_online_time" format="yyyy-MM-dd HH:mm:ss" /></td> -->
											<%-- <td><fmt:formatDate value="${item.last_online_time}" pattern="yyyy-MM-dd HH:mm:ss " /></td> --%>
											<td>${item.last_online_time}</td>
											<!-- <td><s:date name="last_customer_time" format="yyyy-MM-dd HH:mm:ss" /></td> -->
											<%-- <td><fmt:formatDate value="${item.last_customer_time}" pattern="yyyy-MM-dd HH:mm:ss " /></td> --%>
											<td>${item.last_customer_time}</td>
											<td>
												<c:choose>
													<c:when test="${item.google_auth_bind == 'Y'}">
														<span class="right label label-success">已绑定</span>
													</c:when>
													<c:otherwise>
														未绑定
													</c:otherwise>
												</c:choose>
											</td>
											<td>
												<c:choose>
													<c:when test="${item.enabled == 'Y'}">
														开启
													</c:when>
													<c:otherwise>
														<span class="right label label-danger">关闭</span>
													</c:otherwise>
												</c:choose>
											</td>
											<td>${item.remarks}</td>
											<td>
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> 
														<span class="sr-only">Toggle Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<c:if test="${item.id != 'SADMIN'}">
															<li><a
																href="<%=basePath%>normal/adminCustomerAction!toUpdate.action?username=${item.username}&username_para=${item.username_para}">修改</a></li>
															<li><a
																href="<%=basePath%>normal/adminCustomerAction!toUpdatePassword.action?username=${item.username}&username_para=${item.username_para}">修改密码</a></li>
															<li><a
																href="<%=basePath%>normal/adminCustomerAction!toUpdateSafePassword.action?username=${item.username}&username_para=${item.username_para}">修改资金密码</a></li>
															<li><a
																href="javascript:forceOffline('${item.username}');">强制下线</a></li>
															<li><a
																href="<%=basePath%>normal/adminGoogleAuthAction!toUpdateGoogleAuth.action?username=${item.username}&from_page=customer">谷歌验证器</a></li>
														</c:if>
													</ul>
												</div>
											</td>
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

	<form class="form-horizontal"
		action="<%=basePath%>normal/adminCustomerAction!forceOffline.action" method="post"
		name="mainForm" id="mainForm">
		
		<!-- <s:hidden name="username_para" id="username_para"></s:hidden> -->
		<input type="hidden" id="username_para" name="username_para" value="${username_para}"/>
		<!-- <s:hidden name="username" id="offline_username"></s:hidden> -->
		<input type="hidden" id="offline_username" name="username" value="${username}"/>
		
		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_succeeded" tabindex="-1"
				role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
					
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">确认修改密码</h4>
						</div>
						
						<div class="modal-body">
						
							<div class="form-group">
								<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
								<div class="col-sm-4">
									<input id="password" type="password" name="login_safeword"
										class="login_safeword" placeholder="请输入登录人资金密码">
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
							</div> -->
								<!-- <div class="form-group" >
								<label for="input002" class="col-sm-3 control-label form-label">超级谷歌验证码</label>
								<div class="col-sm-4">
									<input id="super_google_auth_code"  name="super_google_auth_code"
										 placeholder="请输入超级谷歌验证码" >
								</div>
							</div> -->
						
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

	<%@ include file="include/js.jsp"%>
	
</body>

<script type="text/javascript">
	function forceOffline(username) {
		$('#offline_username').val(username);
		$('#modal_succeeded').modal("show");
	}
</script>

</html>
