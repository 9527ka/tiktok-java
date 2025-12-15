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
			<h3>行情品种管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<form class="form-horizontal" action="<%=basePath%>normal/adminItemAction!listConfig.action"
				method="post" id="queryForm">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
				
			</form>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">
						<div class="panel-title">查询条件</div>
						
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminItemAction!listConfig.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />

								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="query_symbol" name="para_symbol" class="form-control " placeholder="合约代码" value="${para_symbol}" />
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>
									
							</form>
				
							<%-- <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
										 || security.isResourceAccessible('OP_FOREVER_CONTRACT_OPERATE')}"> --%>
							<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
							
								<div class="col-md-12 col-lg-12 " style="margin-top: 20px; margin-left: -15px;">
									<div class="panel-title">操作</div>
									<div class="col-md-12 col-lg-3">
										<button type="button" onclick="init_data()" class="btn btn-light btn-block">初始化K线图数据</button>
									</div>
								</div>
								
							</c:if>

						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						<div>
				
							<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
										 || security.isResourceAccessible('OP_FOREVER_CONTRACT_OPERATE')}">
							
								<a href="<%=basePath%>normal/adminItemAction!toAddConfig.action" class="btn btn-light" style="margin-bottom: 10px">
									<i class="fa fa-pencil"></i>新增</a>
									
							</c:if>

						</div>
						
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>名称</td>
										<td>代码</td>
										<td>交易对</td>
										<td>精度(位)</td>
										<td>交易量倍数</td>
										<td>借贷利率</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
									
										<tr>
										
											<td>${item.name}</td>
											<td>${item.symbol}</td>
											<td>${item.symbol_data}</td>
											<td>${item.decimals}</td>
											<td>${item.multiple}</td>
											<td>${item.borrowing_rate*100}%</td>
																						
											<td>
											
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
										 					|| security.isResourceAccessible('OP_FOREVER_CONTRACT_OPERATE')}">
												
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
														
															<li><a href="<%=basePath%>normal/adminItemAction!toUpdateConfig.action?id=${item.id}">修改</a></li>
															
															<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
										 					
																<li><a href="javaScript:init_data('${item.symbol}')">初始化K线图</a></li>
									
															</c:if>
															
														</ul>
													</div>
													
												</c:if>
												
											</td>
											
										</tr>
									</c:forEach>
								</tbody>
								
							</table>
							
							<%@ include file="include/page_simple.jsp"%>
							
							<!-- <nav> -->
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

	<form action="<%=basePath%>normal/adminItemAction!klineInitConfig.action" method="post" id="init_data">
		<input type="hidden" name="para_init_symbol" id="para_init_symbol" value="${para_init_symbol}"/>
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

</body>

</html>
