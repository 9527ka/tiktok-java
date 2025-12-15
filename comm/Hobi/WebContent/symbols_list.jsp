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
			<h3>可交易品种库</h3>
			<%@ include file="include/alert.jsp"%>
			
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminSymbolsAction!list.action"
								method="post" id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
<div class="col-md-12 col-lg-8">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<s:textfield id="quote_currency" name="quote_currency" cssClass="form-control " placeholder="报价币种"/>
											</div>
										</div>
									</fieldset>
								</div>

	

								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>
</form>
<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
								<div class="col-md-12 col-lg-4" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li>
												<button type="button" onclick="reload()" class="btn btn-default ">同步远程数据库</button></li>

											</ul>
										</div>
									</div>
								</div>
</sec:authorize>
							

						</div>

					</div>
				</div>
			</div>
			<!-- END queryForm -->
			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
									<td>交易对</td>
										<td>基础币种</td>
										<td>报价币种</td>
										<td>报价精度(小数位)</td>
										<td>交易对杠杆最大倍数</td>
										<td>状态</td>
										<td></td>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
										<td><s:property value="symbol" /></td>
											<td><s:property value="base_currency" /></td>
											<td><s:property value="quote_currency" /></td>
											<td><s:property value="price_precision" /></td>
											<td><s:property value="leverage_ratio" /></td>
											<td><s:property value="state" /></td>
											<td>
											<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<li><a
															href="<%=basePath%>normal/adminItemAction!toAdd.action?symbol_data=<s:property value="symbol" />&symbol=<s:property value="base_currency" />">添加到交易品种</a></li>
													</ul>
												</div>
												</sec:authorize>
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

</body>
<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
	<form action="<%=basePath%>normal/adminSymbolsAction!reload.action" method="post"
		id="reload">
	</form>
	<script type="text/javascript">
		function reload() {
			swal({
				title : "是否同步远程数据库?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("reload").submit();
			});

		}
	</script>
</sec:authorize>
</html>