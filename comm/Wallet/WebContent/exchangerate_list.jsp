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
			<h3>货币汇率配置</h3>
			<%@ include file="include/alert.jsp"%>
								
								
								<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
							<s:if test='isResourceAccessible("ADMIN_EXCHANGE_RATE_LIST")'>
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminExchangeRateAction!list.action"
								method="post" id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
								<div class="col-md-12 col-lg-6">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<s:textfield id="currency" name="currency"
													cssClass="form-control " placeholder="货币代码" />
											</div>
										</div>
									</fieldset>
								</div>
							

								<!-- <div class="col-md-12 col-lg-2" >
									<button type="submit" class="btn  btn-default btn-block">查询</button>
								</div> -->
								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>

							</form>
							</s:if>
						
							

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
										<td>货币</td>
										<td>汇入汇出</td>
										<td>汇率</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
										<td><s:property value="name" />(<s:property value="currency" />)</td>
										<td><s:if test='out_or_in=="in"'>汇入</s:if><s:if test='out_or_in=="out"'>汇出</s:if></td>
										<td><s:property value="rata" /></td>

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
														<s:if test='isResourceAccessible("ADMIN_EXCHANGE_RATE_TOUPDATE")'>
														<li><a
															href="<%=basePath%>normal/adminExchangeRateAction!toUpdate.action?id=<s:property value="id" />">修改</a></li>
														</s:if>
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