<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>

	<%@ include file="include/loading.jsp"%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>交割合约管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
 								action="<%=basePath%>normal/adminContractManageAction!list.action" 
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="query_symbol" name="para_symbol" cssClass="form-control " placeholder="合约代码"/> -->
												<input id="query_symbol" name="para_symbol" 
													class="form-control " placeholder="合约代码" value="${para_symbol}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn  btn-block btn-light">查询</button>
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
						<!-- <a href="<%=basePath%>normal/adminContractManageAction!toAdd.action"
							class="btn btn-light" style="margin-bottom: 10px"><i
							class="fa fa-pencil"></i>新增</a> -->
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>名称</td>
										<td>代码</td>
										<td>交易对</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="contractResult.contractItems" status="stat"> -->
									<c:forEach items="${contractResult.contractItems}" var="item" varStatus="stat">
										<tr>
											<!-- <td><a href="<%=basePath%>normal/adminContractManageAction!list.action?para_symbol=<s:property value="symbol" />"><s:property value="name" /></a></td> -->
											<td>${item.name}</td>
											<td>${item.symbol}</td>
											<td>${item.symbol_data}</td>
											
											<td>
												
												<%-- <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_FUTURES_CONTRACT_OPERATE')}"> --%>
													
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
														
															<li>
																<a href="<%=basePath%>normal/adminContractManageAction!listPara.action?query_symbol=${item.symbol}">交易参数管理</a>
															</li>
															
														</ul>
													</div>
													
												<%-- </c:if> --%>
												
											</td>
											
										</tr>
									<!-- </s:iterator> -->
									</c:forEach>
								</tbody>
								
							</table>
							
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

</body>

	<form action="<%=basePath%>normal/adminItemAction!klineInit.action" method="post" id="init_data">
		<input type="hidden" name="para_init_symbol" id="para_init_symbol" value="${para_init_symbol}" />
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
	
</html>
