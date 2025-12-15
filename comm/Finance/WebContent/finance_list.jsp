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
			<h3>理财产品配置</h3>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>normal/adminFinanceAction!list.action" method="post"
								id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<s:textfield id="name_para" name="name_para" cssClass="form-control " placeholder="产品名称"/>
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>

							</form>

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
						<a href="<%=basePath%>normal/adminFinanceAction!toAdd.action" class="btn btn-light"
							style="margin-bottom: 10px"><i class="fa fa-pencil"></i>新增</a>
						<div class="panel-body">
								
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
									
										<td>产品名称</td>
										<td>产品名称(英文)</td>
										<td>图片</td>
										<td>周期(天)</td>
										<td>日利率(%)</td>
										<td>今日利率(%)</td>
										<td>违约结算比例(%)</td>
										<td>投资金额区间(USDT)</td>
										<td>状态</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
											<td><s:property value="name" /></td>
											<td><s:property value="name_en" /></td>
											<!-- <td>
												<a href="<%=base%>wap/public/showimg!showImg.action?imagePath=<s:property value="img" />" target="_blank">查看照片</a>
											
											</td> -->
											<td>
											<img width="40px" height="40px" src="<%=base%>wap/public/showimg!showImg.action?imagePath=<s:property value="img" />" />
											</td>
											<td><s:property value="cycle" /></td>
											<td><s:property value="daily_rate" />--<s:property value="daily_rate_max" /></td>
											<td><s:property value="today_rate" /></td>
											<td><s:property value="default_ratio" /></td>
											<td><s:property value="investment_min" />--<s:property value="investment_max" /></td>
											<td><s:if test='state=="1"'><span class="right label label-success">启用</span></s:if> 
												<s:if test='state=="0"'>
													<span class="right label label-danger">停用</span>
												</s:if></td>
											
											<td>
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<li><a href="<%=basePath%>normal/adminFinanceAction!toUpdate.action?id=<s:property value="id" />">修改</a></li>
														<!-- 
														<li><a href="<%=basePath%>normal/adminFinanceAction!toDelete.action?id=<s:property value="id" />">删除</a></li>
														 -->
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