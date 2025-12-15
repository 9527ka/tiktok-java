<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
			<h3>矿机配置</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>normal/adminMinerAction!list.action" method="post"
								id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								
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
						
						<%-- <a href="<%=basePath%>normal/adminMinerAction!toAdd.action" class="btn btn-light"
							style="margin-bottom: 10px"><i class="fa fa-pencil"></i>新增</a> --%>
							
						<div class="panel-body">
								
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>产品名称</td>
										<td>产品名称(英文)</td>
										<td>周期(天)</td>
										<td>可解锁周期(天)</td>
										<td>日利率(%)</td>
										<td>今日利率(%)</td>
										<td>投资金额区间(USDT)</td>
										<td>在售</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.name}</td>
											<td>${item.name_en}</td>
											<td>${item.cycle}</td>
											<td>${item.cycle_close}</td>
											<td>${item.show_daily_rate}</td>
											<td>${item.daily_rate}</td>
											<td>											
											   <c:choose>
													<c:when test="${item.test=='Y'}">
														${item.investment_min}
													</c:when>
													<c:otherwise>
													    ${item.investment_min}
													    <c:if test="${item.investment_max=='0'}">及以上</c:if>
													    <c:if test="${item.investment_max!='0'}">
													       --
														   ${item.investment_max}
													    </c:if>
													</c:otherwise>	
												</c:choose>
											</td>
											<td>
											    <c:if test="${item.on_sale=='1'}"><span class="right label label-success">上架</span></c:if> 
												<c:if test="${item.on_sale=='0'}"><span class="right label label-danger">下架</span></c:if>
											</td>
											
											<td>
											
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_MINER_OPERATE')}">
											
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
														
															<li><a href="<%=basePath%>normal/adminMinerAction!toUpdate.action?id=${item.id}">修改</a></li>
														
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
				</div>
			</div>

		</div>

		<%@ include file="include/footer.jsp"%>

	</div>

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

</body>

</html>
