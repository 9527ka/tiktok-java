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
			<h3>合约产品</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminContractManageAction!list.action"
				method="post" id="queryForm">
				<s:hidden name="pageNo" id="pageNo"></s:hidden>
<%-- 				<s:hidden name="name_para" id="name_para"></s:hidden> --%>
<%-- 				<s:hidden name="rolename_para" id="rolename_para"></s:hidden> --%>
			</form> 
			<!-- END queryForm -->
			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">$名称 合约设置</div>
						<a href="javascript:goUrl(<s:property value="pageNo" />)"
							class="btn btn-light" style="margin-bottom: 10px"><i
							class="fa fa-mail-reply"></i>返回</a>
						<a href="<%=basePath%>normal/adminContractManageAction!toAddInstall.action"
							class="btn btn-light" style="margin-bottom: 10px"><i
							class="fa fa-pencil"></i>新增合约</a>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
									
										<td>时间</td>
										<td>类型</td>
										<td>收益(%)</td>
										<td>最低金额</td>
										<td>手续费</td>
										<!-- <td>每张手续费</td>
										<td>最小变动单位</td>
										<td>最小变动单位的盈亏金额</td> -->
										<!-- 
										<td>市场</td> -->
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td>30</td>
										<td>秒</td>
										<td>20</td>
										<td>100</td>
										<td>1</td>
										<td><div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<li>
															<a href="">修改</a>
														</li>
														<%-- <li>
															<a href="<%=basePath%>normal/adminItemLeverageAction!list.action?itemid=<s:property value="id" />">交易杠杆</a>
														</li> --%>
													</ul>
												</div>
										</tr>
									<s:iterator value="page.elements" status="stat">
										<tr>
											<%-- <td><s:property value="name" /></td>
											<td><s:property value="symbol" /></td>
											<td><s:property value="symbol_data" /></td>
											<td><s:property value="unit_amount" /></td>
											<td><s:property value="unit_fee" /></td>
											<td><s:property value="pips" /></td>
											<td><s:property value="pips_amount" /></td> --%>
											<!-- 
											<td><s:if test='market=="1"'>币币交易</s:if><s:if test='market=="2"'>合约交易</s:if></td>
											 -->
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
														<li>
															<a href="">修改</a>
														</li>
														<%--<li>
															<a href="<%=basePath%>normal/adminItemLeverageAction!list.action?itemid=<s:property value="id" />">交易杠杆</a>
														</li> --%>
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
</html>