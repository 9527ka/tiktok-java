<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>

	<%@ include file="include/loading.jsp"%>

	<div class="ifr-dody">

		<div class="ifr-con">
			<h3>理财产品配置</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>normal/adminFinanceAction!list.action" method="post"
								id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<input id="name_para" name="name_para"
												class="form-control " placeholder="产品名称" value="${name_para}"/>
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
										<td>日利率(%)(显示在APP端给客户看的)</td>
										<td>今日利率(%)(实际结算收益时候的使用的)</td>
										<td>违约结算比例(%)</td>
										<td>投资金额区间(USDT)</td>
										<td>状态</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.name}</td>
											<td>${item.name_en}</td>
											<td>
												<img width="40px" height="40px" src="<%=base%>wap/public/showimg!showImg.action?imagePath=${item.img}" />
											</td>
											<td>${item.cycle}</td>
											<td>${item.daily_rate}--${item.daily_rate_max}</td>
											<td>${item.today_rate}</td>
											<td>${item.default_ratio}</td>
											<td>${item.investment_min}--${item.investment_max}</td>
											<td>
											    <c:if test="${item.state == '1'}"><span class="right label label-success">启用</span></c:if> 
												<c:if test="${item.state == '0'}"><span class="right label label-danger">停用</span></c:if>
											</td>
											
											<td>
											
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_FINANCE_OPERATE')}">
														 
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
														
															<li><a href="<%=basePath%>normal/adminFinanceAction!toUpdate.action?id=${item.id}">修改</a></li>
														
														</ul>
													</div>
					
												</c:if>
													
											</td>
											
										</tr>
									</c:forEach>
								</tbody>
								
							</table>
							
							<%@ include file="include/page_simple.jsp"%>
							
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
            var data = ${result};
            $("#treeview4").treeview({
                color: "#428bca",
                enableLinks:true,
                nodeIcon: "glyphicon glyphicon-user",
                data: data,
                levels: 4,
            });
        });
	</script>

</body>

</html>
