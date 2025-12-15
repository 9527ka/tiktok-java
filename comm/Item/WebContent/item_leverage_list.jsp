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
			<h3>交易杠杆</h3>
			<%@ include file="include/alert.jsp"%>

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						 <a href="<%=basePath%>normal/adminItemLeverageAction!toAdd.action?itemid=<s:property value="itemid" />" class="btn btn-light" style="margin-bottom: 10px" >新增杠杆参数</a>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>杠杆(倍)</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
											<td><fmt:formatNumber value="${leverage}"
													pattern="#0.00" /></td>

											<td>
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														
														<li><a
																href="javascript:ondelete('<s:property value="id" />')">删除</a></li>
													</ul>
												</div>
											</td>

										</tr>
									</s:iterator>

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
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->


		<%@ include file="include/footer.jsp"%>


	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->



	<%@ include file="include/js.jsp"%>
	
	<form action="normal/adminItemLeverageAction!delete.action"
			method="post" id="ondelete">
			<input type="hidden" name="pageNo" id="pageNo"
				value="${param.pageNo}">
			<s:hidden name="id" id="id"></s:hidden>
			<s:hidden name="itemid" id="itemid"></s:hidden>
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