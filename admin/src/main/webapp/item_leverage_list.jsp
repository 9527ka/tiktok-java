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

	<div class="ifr-dody">
		<div class="ifr-con">
			<h3>交易杠杆</h3>
			<%@ include file="include/alert.jsp"%>

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						 <a href="<%=basePath%>normal/adminItemLeverageAction!toAdd.action?itemid=${itemid}" class="btn btn-light" style="margin-bottom: 10px" >新增杠杆参数</a>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>杠杆(倍)</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
								<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
									
										<tr>
											<td><fmt:formatNumber value="${item.lever_rate}" pattern="#0.00" /></td>

											<td>
											<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_FOREVER_CONTRACT_OPERATE')}">
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														
														<li><a
																href="javascript:ondelete('${item.id}')">删除</a></li>
													</ul>
												</div>
												</c:if>
											</td>
										</tr>
									</c:forEach>

								</tbody>
							</table>
							<%@ include file="include/page_simple.jsp"%>
							<nav>
						</div>

					</div>
					<!-- End Panel -->

				</div>
			</div>

		</div>

		<%@ include file="include/footer.jsp"%>


	</div>

	<%@ include file="include/js.jsp"%>
	<form action="<%=basePath%>normal/adminItemLeverageAction!toDelete.action"
			method="post" id="ondelete">
			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
			<input type="hidden" name="id" id="id" value="${id}" />
			<input type="hidden" name="itemid" id="itemid" value="${itemid}" />
		</form>
		<script type="text/javascript">
			function ondelete(id) {
				$("#id").val(id);
				swal({
					title : "是否确认删除?",
					text : "",
					type : "warning",
					showCancelButton : true,
					confirmButtonColor : "#DD6B55",
					confirmButtonText : "确认",
					closeOnConfirm : false
				}, function() {
					document.getElementById("ondelete").submit();
				});

			}
		</script>
</body>
</html>