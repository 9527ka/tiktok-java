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

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>全局质押配置</h3>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminPledgeConfigAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
									
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="name_para" name="name_para"
													cssClass="form-control " placeholder="用户名(钱包地址)、UID" /> -->
												<input id="name_para" name="name_para"
													class="form-control " placeholder="用户名(钱包地址)、UID" value="${name_para}" />
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="title_para" name="title_para"
													cssClass="form-control " placeholder="质押标题" /> -->
												<input id="title_para" name="title_para"
													class="form-control " placeholder="活动标题" value="${title_para}" />
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
						
						<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
						
							<a href="<%=basePath%>normal/adminPledgeConfigAction!toAdd.action"
								class="btn btn-light" style="margin-bottom: 10px"><i
								class="fa fa-pencil"></i>新增</a>
					
						</c:if>
							
						<div class="panel-body">

							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>用户名</td>
										<td>UID</td>
										<td>账户类型</td>
										<td>推荐人</td>
										<td>收益费率</td>
										<td>活动标题</td>
										<td>活动标题图片</td>
										<td>用户USDT限制数量</td>
										<td>奖励ETH数量</td>
										<!-- 										<td>活动准入结束时间</td> -->
										<!-- 										<td>活动奖励派发时间</td> -->
										<!-- 										<td>首页弹出活动</td> -->
										<!-- 										<td>状态</td> -->
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.username}</td>
											<td>${item.usercode}</td>																						
											<td>
												<c:choose>
													<c:when test="${item.rolename=='GUEST'}">
														<span class="right label label-warning">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='MEMBER'}">
														<span class="right label label-success">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='AGENT'}">
														<span class="right label label-primary">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='AGENTLOW'}">
														<span class="right label label-primary">${item.roleNameDesc}</span>
													</c:when>
													<c:otherwise>
													 	${item.roleNameDesc}
													</c:otherwise>	
												</c:choose>
											</td>
											<td>${item.username_parent}</td>
											<td>${item.config}</td>
											<td>${item.title}</td>
											<td><img width="40px" height="40px"
												src="<%=base%>wap/public/showimg!showImg.action?imagePath=${item.title_img}" />
											</td>
											<!-- <td>
												<a href="<%=base%>wap/public/showimg!showImg.action?imagePath=<s:property value="img" />" target="_blank">查看照片</a>
											</td> -->
											<td>${item.usdt}</td>
											<td>${item.eth}</td>
	
											<td>
											
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')}">
											
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
														
															<li><a href="<%=basePath%>normal/adminPledgeConfigAction!toUpdate.action?id=${item.id}">修改</a></li>
															<c:if test="${item.id != '2c948a827cd5f779017cd2322f5d0001'}">
																<li><a href="javascript:delete_to('${item.id}')">删除</a></li>
															</c:if>
															
														</ul>
													</div>
												
												</c:if>
												
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
		
		<!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/adminPledgeConfigAction!toDelete.action"
				method="post" id="deleteForm">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="id" id="id_delete">
				<input type="hidden" name="session_token" id="session_token_delete" value="${session_token}">
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_delete" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">删除 全局质押配置</h4>
								</div>
								
								<div class="modal-header">
									<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
								</div>
								
								<div class="modal-body">
									<div class="">
										<input id="login_safeword" type="password"
											name="login_safeword" class="form-control"
											placeholder="请输入登录人资金密码">
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

		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

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
		function delete_to(id) {
			var session_token = $("#session_token").val();
			$("#session_token_delete").val(session_token);
			$("#id_delete").val(id);
			$('#modal_delete').modal("show");
		}
	</script>
	
</body>

</html>
