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

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>永续合约管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<form class="form-horizontal" action="<%=basePath%>normal/adminItemAction!list.action" method="post" id="queryForm">
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
			</form>
			
			 <div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminItemAction!list.action"
								method="post" id="queryForm">
								
                                <input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
                                
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="query_symbol" name="para_symbol" class="form-control " placeholder="合约代码" value="${para_symbol}"/>
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
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						<div>
						</div>				
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
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.name}</td>
											<td>${item.symbol}</td>
											<td>${item.symbol_data}</td>
											<td>${item.unit_amount}</td>
											<td>${item.unit_fee}</td>
											<td>${item.pips_str}</td>
											<td>${item.pips_amount_str}</td>
											
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
															<li><a href="<%=basePath%>normal/adminItemAction!toUpdate.action?id=${item.id}">修改</a></li>
															<li><a href="<%=basePath%>normal/adminItemLeverageAction!list.action?itemid=${item.id}">交易杠杆</a></li>
															<%-- <li><a href="<%=basePath%>normal/adminExchangeLeverAction!rateList.action?symbol=${item.symbol}">币币交易杠杆</a></li> --%>
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
				</div>
			</div>

		</div>
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->

		<%@ include file="include/footer.jsp"%>

	</div>

	<%@ include file="include/js.jsp"%>
		
	<form action="<%=basePath%>normal/adminItemAction!klineInit.action" method="post" id="init_data">
		<input type="hidden" name="para_init_symbol" id="para_init_symbol" value="${para_init_symbol}">
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
