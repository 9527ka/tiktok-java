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
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>推荐关系</h3>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminUserRecomAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
									
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="query_username2" name="query_username2"
													cssClass="form-control " placeholder="用户名、UID" /> -->
												<input id="query_username2" name="query_username2"
													class="form-control " placeholder="用户名、UID" value="${query_username2}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<%-- <div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<s:textfield id="usercode_para" name="usercode_para"
													cssClass="form-control " placeholder="UID" />
											</div>
										</div>
									</fieldset>
								</div> --%>
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="query_username" name="query_username"
													cssClass="form-control " placeholder="推荐用户" /> -->
												<input id="query_username" name="query_username" class="form-control " placeholder="推荐用户" value="${query_username}" />
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

						<div class="panel-body">

							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>用户名</td>
										<td>UID</td>
										<td>账户类型</td>
										<td>推荐用户</td>
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
											<td>${item.parent_username}</td>
											<td>

												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_USER_RECOM_OPERATE')}">

													<a href="<%=basePath%>normal/adminUserRecomAction!toUpdate.action?id=${item.id}&name_para=${item.name_para}&username=${item.username}&partyId=${item.partyId}"
														class="btn btn-light">修改</a>
														
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
	</script>

</body>

</html>
