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
			<h3>交割合约交易参数管理</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminContractManageAction!listPara.action"
				method="post" id="queryForm">
				<s:hidden name="pageNo" id="pageNo"></s:hidden>
				<s:hidden name="query_symbol" id="query_symbol"></s:hidden>
				<s:hidden name="rolename_para" id="rolename_para"></s:hidden>
			</form> 
			<!-- END queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
 								action="<%=basePath%>normal/adminContractManageAction!listPara.action" 
								method="post" id="queryForm">
								<s:hidden name="pageNo" id="pageNo"></s:hidden>
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<s:textfield id="query_symbol" name="query_symbol" cssClass="form-control " placeholder="代码"/>
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
						<!--  <a href="javascript:goUrl(<s:property value="pageNo" />)" 
							class="btn btn-light" style="margin-bottom: 10px"><i 
							class="fa fa-mail-reply"></i>返回</a> -->
						<a href="<%=basePath%>normal/adminContractManageAction!toAddInstall.action?query_symbol=<s:property value="query_symbol" />"
							class="btn btn-light" style="margin-bottom: 10px"><i
							class="fa fa-pencil"></i>新增</a>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<!-- <td>名称</td> -->
										<td>代码</td>
										<td>时间</td>
										<td>交割收益（%）</td>
										<td>最低金额</td>
										<td>手续费（%）</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									
									<s:iterator value="contractResult.futures.elements" status="stat">
										<tr>
											<!--<td><s:property value="name" /></td>  -->
											<td><s:property value="symbol" /></td>
											<td><s:property value="timeNum" />
											(

											<s:if test='timeUnit=="second"'>秒</s:if> 
											<s:if test='timeUnit=="minute"'>分</s:if> 
											<s:if test='timeUnit=="hour"'>时</s:if> 
											<s:if test='timeUnit=="day"'>天</s:if> 
											
											)
											</td>
											<td>
												<s:property value="profit_ratio" />
												~
												<s:property value="profit_ratio_max"/>
											</td>
											<td><s:property value="unit_amount" /></td>
											<td><s:property value="unit_fee" /></td>
											<td>
												<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
													<a href="<%=basePath%>normal/adminContractManageAction!toAddInstall.action?futuresId=<s:property value="id" />&query_symbol=<s:property value="query_symbol" />"
												class="btn btn-light" style="margin-bottom: 10px">修改</a>
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