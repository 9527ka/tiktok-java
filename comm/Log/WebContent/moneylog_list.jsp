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
			<h3>账变记录</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
							<s:if test='isResourceAccessible("ADMIN_MONEY_LOG_LIST")'>
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminMoneyLogAction!list.action"
								method="post" id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<s:textfield id="name_para" name="name_para"
													cssClass="form-control " placeholder="用户名、UID(完整)" />
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-6">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<s:textfield id="log_para" name="log_para"
													cssClass="form-control " placeholder="日志" />
											</div>
										</div>
									</fieldset>
								</div>
								

								<div class="col-md-12 col-lg-2" >
									<s:if test='isResourceAccessible("ADMIN_MONEY_LOG_LIST")'>
									<button type="submit" class="btn btn-light btn-block">查询</button>
									</s:if>
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
										<td>用户名</td>
										<td>UID</td>
										<td>账户名称</td>
										<td>日志</td>
										<td>币种</td>
										<td>金额</td>
										<td>变更前</td>
										<td>变更后</td>
										<td width="150px">时间</td>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
											<td><s:property value="username" /></td>
											<td><s:property value="usercode" /></td>
											<td><s:if test='rolename=="GUEST"'>
													<span class="right label label-warning">演示账号</span>
												</s:if>
												<s:if test='rolename=="MEMBER"'>
													<span class="right label label-success">正式账号</span>
												</s:if>
											</td>
											<td><s:property value="log" /></td>
											<td><s:property value="wallettype" /></td>
											<td><fmt:formatNumber value="${amount}" pattern="#0.00" /></td>
											<td><fmt:formatNumber value="${amount_before}" pattern="#0.00" /></td>
											<td><fmt:formatNumber value="${amount_after}" pattern="#0.00" /></td>
											<td><s:date name="createTime" format="yyyy-MM-dd HH:mm " /></td>

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