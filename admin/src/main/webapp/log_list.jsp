<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>

	<%@ include file="include/loading.jsp"%>
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>操作日志</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminLogAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								 
								<div class="col-md-12 col-lg-4" >
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="name_para" name="name_para"
													class="form-control " placeholder="用户名、UID(完整)" value="${name_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-6">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="log_para" name="log_para"
													class="form-control " placeholder="日志" value="${log_para}" />
											</div>
										</div>
									</fieldset>
								</div>
																
								<div class="col-md-12 col-lg-3" style="margin-top: 10px;">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="operator" name="operator"
													cssClass="form-control " placeholder="操作者" /> -->
												<input id="operator" name="operator"
													class="form-control " placeholder="操作者" value="${operator}" />
											</div>
										</div>
									</fieldset>
								</div>
																
								<div class="col-md-12 col-lg-3" style="margin-top: 10px;">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:select id="category" cssClass="form-control " headerValue="分类"  headerKey=""
													name="category" list="category_map"
													listKey="key" listValue="value" value="category" /> -->
													<select id="category" name="category" class="form-control " >
														<option value="">分类</option>
														<c:forEach items="${category_map}" var="item">
															<option value="${item.key}" <c:if test="${category == item.key}">selected="true"</c:if> >${item.value}</option>
														</c:forEach>
													</select>
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-2" style="margin-top: 10px;">
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
										<!--  <td>名称</td>-->
										<td>用户名</td>
										<td>UID</td>
										<td>账户类型</td>
										<td>日志</td>
										<td>操作者</td>
										<td width="150px">时间</td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
										<!-- <td><s:property value="name" /></td>-->
											<td>${item.username}</td>
											<td>${item.usercode}</td>
											<td>
												<c:choose>
													<c:when test="${item.rolename=='GUEST'}">
														<span class="right label label-warning">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='MEMBER'}">
														<span class="right label label-success">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='TEST'}">
														<span class="right label label-default">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='AGENT'}">
														<span class="right label label-primary">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='AGENTLOW'}">
														<span class="right label label-primary">${item.roleNameDesc}</span>
													</c:when>
													<c:otherwise>
													 	${item.roleNameDesc}
													</c:otherwise>	
												</c:choose>
											</td>
											<td>${item.log}</td>
											<td>${item.operator}</td>
											<!-- <td><s:date name="createTime" format="yyyy-MM-dd HH:mm " /></td> -->
											<%-- <td><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm " /></td> --%>
											<td>${item.createTime}</td>										
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
	
	<script type="text/javascript">
		$(document).ready(function() {
		  $('#date_para').daterangepicker({
				timePicker : true, //显示时间
				timePicker24Hour : true, //时间制
				timePickerSeconds : true, //时间显示到秒
				format : 'YYYY-MM-DD HH:mm:ss',
				startDate : new Date(),
				endDate : new Date(),
				minDate : 1999 - 12 - 12,
				maxDate : 2050 - 12 - 30,
				timePicker : true,
				timePickerIncrement : 1,
		
				locale : {
					applyLabel : "确认",
					cancelLabel : "取消",
					resetLabel : "重置",
		
				}
			}, function(start, end, label) {
		    console.log(start.toISOString(), end.toISOString(), label);
		  });
		});
	</script>
	
</body>

</html>
