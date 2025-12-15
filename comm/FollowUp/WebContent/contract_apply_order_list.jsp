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
			<h3>合约委托单</h3>
			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminContractApplyOrderAction!list.action"
								method="post" id="queryForm">
								<s:hidden name="status_para"></s:hidden>
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<s:select id="rolename_para" cssClass="form-control "
													name="rolename_para"
													list="#{'MEMBER':'正式账号','GUEST':'演示账号'}" listKey="key"
													listValue="value" headerKey="" headerValue="所有账号"
													value="rolename_para" />
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>

								<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li><a href="javascript:setState('')"> 全部</a></li>
												<li><a href="javascript:setState('submitted')"> 已提交</a></li>
												<li><a href="javascript:setState('canceled')"> 已撤销</a></li>
												<li><a href="javascript:setState('created')"> 委托完成</a></li>

											</ul>
										</div>
									</div>
								</div>

							</form>

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
										<td>用户</td>
										<td>品种</td>
										<td>操作</td>
										<td>委托张数</td>
										<td>杠杆</td>
										<td>报价类型</td>
										<td>限价</td>
										<td>止盈止损</td>
										<td>状态</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
											<td><s:property value="username" /></td>
											<td><s:property value="itemname" /></td>
											<td><s:if test='offset=="open"'>开</s:if><s:if test='offset=="close"'>平</s:if><s:if test='direction=="buy"'>多</s:if><s:if
													test='direction=="sell"'>空</s:if></td>
											<td><fmt:formatNumber value="${volume_open}" pattern="#0.00" /></td>
											<td><fmt:formatNumber value="${lever_rate}" pattern="#0.00" /></td>
											<td><s:if test='order_price_type=="limit"'>限价</s:if><s:if test='order_price_type=="opponent"'>市价</s:if></td>
											<td><fmt:formatNumber
													value="${price}" pattern="#0.00" /></td>
											<td><s:property value="stop_price_profit" />/<s:property value="stop_price_loss" /></td>
											<td><s:if test='state=="submitted"'>
													已提交
												</s:if> <s:if test='state=="canceled"'>已撤销</s:if> <s:if test='state=="created"'>
													<span class="right label label-success">委托完成</span>
												</s:if> </td>
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
														<li><a
															href="javascript:onclose('<s:property value="order_no" />')">撤销</a></li>
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

	

<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
	<form action="normal/adminContractApplyOrderAction!close.action" method="post"
		id="onclose">
		<input type="hidden" name="pageNo" id="pageNo" value="${param.pageNo}">
		<s:hidden name="status_para"></s:hidden>
		<s:hidden name="rolename_para"></s:hidden>
	</form>
	<script type="text/javascript">
		function onclose(order_no) {
			$("#order_no").val(order_no);
			swal({
				title : "是否确认撤销?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("onclose").submit();
			});

		}
	</script>
</sec:authorize>
	<script type="text/javascript">
		function setState(state){
    		document.getElementById("status_para").value=state;
    		document.getElementById("queryForm").submit();
		}
	</script>


</body>
</html>