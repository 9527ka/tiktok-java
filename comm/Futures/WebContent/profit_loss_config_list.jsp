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
			<h3>交割场控设置</h3>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						<s:if test='isResourceAccessible("ADMIN_PROFIT_LOSS_CONFIG_LIST")'>

							<form class="form-horizontal" action="<%=basePath%>normal/adminProfitAndLossConfigAction!list.action" method="post"
								id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<s:textfield id="name_para" name="name_para" cssClass="form-control " placeholder="用户名、UID"/>
											</div>
										</div>
									</fieldset>
								</div>

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
						<s:if test='isResourceAccessible("ADMIN_PROFIT_LOSS_CONFIG_TOADD")'>
						<a href="<%=basePath%>normal/adminProfitAndLossConfigAction!toAdd.action" class="btn btn-light"
							style="margin-bottom: 10px"><i class="fa fa-pencil"></i>新增</a>
						</s:if>
						<div class="panel-body">
								
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
									
										<td>用户名</td>
										<td>UID</td>
										<td>账户类型</td>
										<td>类型</td>
										<td>备注</td>
										<td width="130px"></td>
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
											<td><s:property value="type" /></td>
											<%-- <td><s:if test='type=="1"'>盈利</s:if>
											<s:if test='type=="2"'>亏损</s:if>
											<s:if test='type=="3"'>买多盈利</s:if>
											<s:if test='type=="4"'>买空盈利</s:if> --%>
											</td>
											
											<td><s:property value="remark" /></td>
											
											
											<td>
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
													<s:if test='isResourceAccessible("ADMIN_PROFIT_LOSS_CONFIG_TOUPDATE")'>
														<li><a href="<%=basePath%>normal/adminProfitAndLossConfigAction!toUpdate.action?id=<s:property value="id" />">修改</a></li>
													</s:if>
													<s:if test='isResourceAccessible("ADMIN_PROFIT_LOSS_CONFIG_TODELETE")'>
														<li><a href="<%=basePath%>normal/adminProfitAndLossConfigAction!toDelete.action?id=<s:property value="id" />">删除</a></li>
													</s:if>
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
<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
 <script>
        $(function () {
            var data = <s:property value="result" escape='false' />;
            console.log(data);
            $("#treeview4").treeview({
                color: "#428bca",
                enableLinks:true,
                nodeIcon: "glyphicon glyphicon-user",
                data: data,
                levels: 4,
            });
        });
</script>

	
	<script type="text/javascript">
		

	</script>
</body>
</html>