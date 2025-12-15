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
			<h3>汇率配置</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminExchangeRateAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="name" name="name"
													class="form-control " placeholder="币种名称" value="${name}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="status" name="status"
														class="form-control ">
													<option value="-2">状态</option>
													<option value="0" <c:if test="${status == '0'}">selected="true"</c:if>>启用</option>
													<option value="1" <c:if test="${status == '1'}">selected="true"</c:if>>禁用</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-3">
									<input id="startTime" name="startTime" class="form-control "
										   placeholder="开始日期" value="${startTime}" />
								</div>
								<div class="col-md-12 col-lg-3">

									<input id="endTime" name="endTime" class="form-control "
										   placeholder="结束日期" value="${endTime}" />
								</div>
								<div class="col-md-12 col-lg-12" style="margin-top:10px;"></div>
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
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>出售币种</td>
										<td>币种名称</td>
										<td>币种单位</td>
										<td>币种符号</td>
										<td>汇率价格</td>
										<td>序号</td>
										<td>最小兑换</td>
										<td>最大兑换</td>
										<td>状态</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
								    <c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>USDT</td>
											<td>${item.name}</td>
											<td>${item.currency}</td>
											<td>${item.currency_symbol}</td>
											<td>${item.rata}</td>
											<td>${item.sort}</td>
											<td>${item.exc_min}</td>
											<td>${item.exc_max}</td>
											<td>
												<c:choose>
													<c:when test="${item.status == '0'}">
														<span class="right label label-success">启用</span>
													</c:when>
													<c:otherwise>
														<span class="right label label-danger">禁用</span>
													</c:otherwise>
												</c:choose>
											</td>
											<td>
											
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_EXCHANGE_RATE_OPERATE')}">
												
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
														
															<li><a href="<%=basePath%>normal/adminExchangeRateAction!toUpdate.action?id=${item.uuid}">修改</a></li>
															
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
	
</body>

</html>
