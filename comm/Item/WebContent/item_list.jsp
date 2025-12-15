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
			<h3>交易品种</h3>
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">操作</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminItemAction!order_open.action"
								method="post" id="queryForm">
								<s:hidden name="pageNo" id="pageNo"></s:hidden>
<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<s:select id="order_open" cssClass="form-control "
													name="order_open" list="#{'true':'交易开启','false':'交易关闭(关闭后，所有品种不可开仓和平仓)'}"
													listKey="key" listValue="value" 
													value="order_open" />
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn  btn-block btn-default">确定</button>
								</div>
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
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>名称</td>
										<td>代码</td>
										<td>交易对</td>
										<td>每张金额</td>
										<td>每张手续费</td>
										<td>最小变动单位</td>
										<td>最小变动单位的盈亏金额</td>
										<!-- 
										<td>市场</td> -->
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
											<td><s:property value="name" /></td>
											<td><s:property value="symbol" /></td>
											<td><s:property value="symbol_data" /></td>
											<td><s:property value="unit_amount" /></td>
											<td><s:property value="unit_fee" /></td>
											<td><s:property value="pips" /></td>
											<td><s:property value="pips_amount" /></td>
											<!-- 
											<td><s:if test='market=="1"'>币币交易</s:if><s:if test='market=="2"'>合约交易</s:if></td>
											 -->
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
															href="<%=basePath%>normal/adminItemAction!toUpdate.action?id=<s:property value="id" />">修改</a></li>
														<li><a
															href="<%=basePath%>normal/adminItemLeverageAction!list.action?itemid=<s:property value="id" />">交易杠杆</a></li>
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

</body>
</html>