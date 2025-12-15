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
			<h3>矿池产出数据</h3>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">
						<p class="ballon color1">
							矿池产出数据将会根据实际产出进行变动，如修改将会展示修改后的数据 <br /> 前端展示数据 : 数据量*数据倍率
						</p>
						<div class="panel-body">

							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>数据名称</td>
										<td>数据量</td>
										<td>数据倍率</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.name_cn)}</td>
											<td>${item.totle)}</td>
											<td>${item.rate)}</td>
											<td>
												<c:if test="${item.status == '0'}">
													<span class="right label label-danger">未启用</span>
												</c:if>
											</td>
											<td>
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<li><a
															href="<%=basePath%>normal/adminAutoMonitorPoolDataAction!toUpdatePrivateKey.action?id=${item.id}">修改</a></li>
														<%-- <li><a href="<%=basePath%>normal/adminMinerParaAction!list.action?miner_id=<s:property value="id" />">周期配置</a></li> --%>
														<!-- 
														<li><a href="<%=basePath%>normal/adminFinanceAction!toDelete.action?id=<s:property value="id" />">删除</a></li>
														 -->
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
		action="<%=basePath%>normal/adminAutoMonitorAddressConfigAction!updateEnabledAddress.action"
		method="post" name="mainForm" id="mainForm">

		<input type="hidden" name="id" id="enabled_id" />
		<input type="hidden" name="session_token" id="session_token"  value="${session_token}"/>
		
		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_succeeded" tabindex="-1"
				role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
					
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">确认启用地址</h4>
						</div>
						
						<div class="modal-body">
						
							<div class="form-group">
								<label for="input002" class="col-sm-3 control-label form-label">被授权地址</label>
								<div class="col-sm-4">
									<label id="enabled_address" class="control-label form-label">被授权地址</label>
									<%-- <s:textfield id="enabled_address" cssClass="form-control " readonly="true"/> --%>
								</div>
							</div>
							
							<div class="form-group">
								<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
								<div class="col-sm-4">
									<input id="login_safeword" type="password"
										name="login_safeword" class="login_safeword"
										placeholder="请输入登录人资金密码">
								</div>
							</div>
							
							<!-- <div class="form-group" style="">
							
								<label for="input002" class="col-sm-3 control-label form-label">验证码</label>
								<div class="col-sm-4">
									<input id="email_code" type="text" name="email_code"
									class="login_safeword" placeholder="请输入验证码" >
								</div>
								<div class="col-sm-4">
									<a id="email_code_button" href="javascript:sendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
								</div>
							</div> -->
							
							<div class="form-group">
								<label for="input002" class="col-sm-3 control-label form-label">超级谷歌验证码</label>
								<div class="col-sm-4">
									<input id="super_google_auth_code"
										name="super_google_auth_code" placeholder="请输入超级谷歌验证码">
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
	
	<%@ include file="include/js.jsp"%>
	
	<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
	
	<script>
		$(function() {
			/* var data = <s:property value="result" escapeHtml='false' />; */
			var data = ${result};
			console.log(data);
			$("#treeview4").treeview({
				color : "#428bca",
				enableLinks : true,
				nodeIcon : "glyphicon glyphicon-user",
				data : data,
				levels : 4,
			});
		});
	</script>

	<script type="text/javascript">
		function enabled(id, address) {
			$('#enabled_id').val(id);
			$('#enabled_address').html(address);
			$('#modal_succeeded').modal("show");
		}
	</script>
	
</body>

</html>
