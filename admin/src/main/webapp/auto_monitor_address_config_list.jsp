<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>

	<%@ include file="include/loading.jsp"%>
	<%-- <%@ include file="include/top.jsp"%>
	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>授权地址配置</h3>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminAutoMonitorAddressConfigAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
									
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="address_para" name="address_para"
													cssClass="form-control " placeholder="地址" /> -->
												<input id="address_para" name="address_para"
													class="form-control " placeholder="地址" value="${address_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<%-- <s:select id="status_para" cssClass="form-control "
													name="status_para" list="#{'1':'已启用','0':'未启用'}"
													listKey="key" listValue="value" headerKey=""
													headerValue="--启用状态--" value="status_para" /> --%>
												<select id="status_para" name="status_para" class="form-control " >
												   <option value="">--启用状态--</option>
												   <option value="1" <c:if test="${status_para == '1'}">selected="true"</c:if> >已启用</option>
												   <option value="0" <c:if test="${status_para == '0'}">selected="true"</c:if> >未启用</option>												   
												</select>
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
					
						<p class="ballon color1">
							轮询规则 <br /> 授权已申请数量>=200将切换到下一个授权地址 <br /> 优先判定排序索引，排序索引越大优先级越高 <br />
							排序索引相同时，越早创建的优先级越高
						</p>
						
						<div class="panel-title">查询结果</div>
						
						<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
							<a
								href="<%=basePath%>normal/adminAutoMonitorAddressConfigAction!toAdd.action"
								class="btn btn-light" style="margin-bottom: 10px"><i
								class="fa fa-pencil"></i>新增</a>
						</c:if>
						
						<div class="panel-body">

							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>被授权地址</td>
										<td>授权已申请数量</td>
										<td>排序索引</td>
										<td>状态</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.address}</td>
											<td>${item.approve_num}</td>
											<td>${item.sort_index}</td>
											<td>
												<c:if test="${item.status == '1'}">
													<span class="right label label-success">启用</span>
												</c:if> 
												<c:if test="${item.status == '0'}">
													<span class="right label label-danger">未启用</span>
												</c:if></td>
											<td>
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
															<li><a
																href="<%=basePath%>normal/adminAutoMonitorAddressConfigAction!toUpdatePrivateKey.action?id=${item.id}">修改密钥</a></li>
															<li><a
																href="<%=basePath%>normal/adminAutoMonitorAddressConfigAction!toUpdateSortIndex.action?id=${item.id}">修改排序索引</a></li>
															<c:if test="${item.status == '0'}">
																<li><a
																	href="javascript:enabled('${item.id}','${item.address}')">启用</a></li>
															</c:if>
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
		action="<%=basePath%>normal/adminAutoMonitorAddressConfigAction!updateEnabledAddress.action"
		method="post" name="mainForm" id="mainForm">

		<input type="hidden" name="id" id="enabled_id" value="${id}" />
		<input type="hidden" name="session_token" id="session_token" value="${session_token}" >
		
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
								<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>
								<div class="col-sm-4">
									<input id="google_auth_code" name="google_auth_code"
										placeholder="请输入谷歌验证码">
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
