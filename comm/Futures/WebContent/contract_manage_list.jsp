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
			<h3>交割合约配置</h3>
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
 								action="<%=basePath%>normal/adminContractManageAction!list.action" 
								method="post" id="queryForm">
								<s:hidden name="pageNo" id="pageNo"></s:hidden>
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<s:textfield id="query_symbol" name="para_symbol" cssClass="form-control " placeholder="合约代码"/>
											</div>
										</div>
									</fieldset>
								</div>
								<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn  btn-block btn-light">查询</button>
								</div>
								
								</sec:authorize>
							</form>
							
						</div>
						
					</div>
				</div>
				
			</div>
			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						<!-- <a href="<%=basePath%>normal/adminContractManageAction!toAdd.action"
							class="btn btn-light" style="margin-bottom: 10px"><i
							class="fa fa-pencil"></i>新增</a> -->
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>名称</td>
										<td>代码</td>
										<td>交易对</td>
										
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="contractResult.contractItems" status="stat">
										<tr>
											<!-- <td><a href="<%=basePath%>normal/adminContractManageAction!list.action?para_symbol=<s:property value="symbol" />"><s:property value="name" /></a></td> -->
											<td><s:property value="name" /></td>
											<td><s:property value="symbol" /></td>
											<td><s:property value="symbol_data" /></td>
											
											<td>
												<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<!--  <li>
															<a href="<%=basePath%>normal/adminContractManageAction!toAdd.action?itemId=<s:property value="id" />"
																class="btn btn-light" style="margin-bottom: 10px">修改</a>
														</li>-->
														<li>
															<a href="<%=basePath%>normal/adminContractManageAction!listPara.action?query_symbol=<s:property value="symbol" />"
																>交易参数管理</a>
														</li>
													</ul>
												</div>
												</sec:authorize>
											</td>
										

										</tr>
									</s:iterator>

								</tbody>
							</table>
							
							
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

<sec:authorize ifAnyGranted="ROLE_ROOT">
	<form action="<%=basePath%>normal/adminItemAction!klineInit.action" method="post"
		id="init_data">
		<s:hidden name="para_init_symbol" id="para_init_symbol"></s:hidden>
	</form>
	<script type="text/javascript">
		function init_data(symbol) {
			swal({
				title : "是否初始化K线图数据?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				$("#para_init_symbol").val(symbol);
				document.getElementById("init_data").submit();
			});

		}
	</script>
</sec:authorize>
</html>