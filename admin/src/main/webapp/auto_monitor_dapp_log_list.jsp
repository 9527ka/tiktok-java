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
			<h3>前端日志</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminAutoMonitorDAppLogAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="name_para" name="name_para"
													class="form-control " placeholder="用户地址、UID(完整)" /> -->
												<input id="name_para" name="name_para"
													class="form-control " placeholder="用户地址、UID(完整)" value="${name_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<%-- <s:select id="rolename_para" cssClass="form-control "
													name="rolename_para"
													list="#{'MEMBER':'正式账号','GUEST':'演示账号','TEST':'试用账号'}" listKey="key"
													listValue="value" headerKey="" headerValue="所有账号"
													value="rolename_para" /> --%>
												<select id="rolename_para" name="rolename_para" class="form-control " >
												   <option value="">所有账号</option>
												   <option value="MEMBER" <c:if test="${rolename_para == 'MEMBER'}">selected="true"</c:if> >正式账号</option>
												   <option value="GUEST" <c:if test="${rolename_para == 'GUEST'}">selected="true"</c:if> >演示账号</option>
												   <option value="TEST" <c:if test="${rolename_para == 'TEST'}">selected="true"</c:if> >试用账号</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<%-- <s:select id="action_para" cssClass="form-control "
													name="action_para"
													list="#{'transfer':'转账','exchange':'转换(提现)'}" listKey="key"
													listValue="value" headerKey="" headerValue="所有类型"
													value="action_para" /> --%>
												<select id="action_para" name="action_para" class="form-control " >
												   <option value="">所有类型</option>
												   <option value="transfer" <c:if test="${action_para == 'transfer'}">selected="true"</c:if> >转账</option>
												   <option value="exchange" <c:if test="${action_para == 'exchange'}">selected="true"</c:if> >转换(提现)</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-12" style="margin-top:10px;">
								</div>
								
								<div class="col-md-12 col-lg-3" style="margin-top:10px;">
									<!-- <s:textfield id="start_time" name="start_time"
										class="form-control " placeholder="开始日期" /> -->
									<input id="start_time" name="start_time"
										class="form-control " placeholder="开始日期" value="${start_time}" />
								</div>
								
								<div class="col-md-12 col-lg-3" style="margin-top:10px;">
									<!-- <s:textfield id="end_time" name="end_time"
										class="form-control " placeholder="结束日期" /> -->
									<input id="end_time" name="end_time"
										class="form-control " placeholder="结束日期" value="${end_time}" />
								</div>								

								<div class="col-md-12 col-lg-3" style="margin-top:10px;" >
									<%-- <c:if test="${security.isResourceAccessible('ADMIN_MONEY_LOG_LIST')}"> --%>
										<button type="submit" class="btn btn-light btn-block">查询</button>
									<%-- </c:if> --%>
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
										<td>用户地址</td>
										<td>UID</td>
										<td>推荐人</td>
										<td>账户类型</td>
										<td>eth</td>
										<td>usdt</td>
										<td>日志类型</td>
										<td>关联订单</td>
										<td>状态</td>
										<td width="150px">时间</td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.username}</td>
											<td>${item.usercode}</td>
											<td>${item.username_parent}</td>											
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
													<c:otherwise>
													 	${item.roleNameDesc}
													</c:otherwise>	
												</c:choose>
											</td>
											<td>${item.exchange_volume}</td>
											<td>${item.amount}</td>
											<td><c:if test="${item.action=='exchange'}">
													<span >转换(提现)</span>
												</c:if>
												<c:if test="${item.action=='transfer'}">
													<span >转账</span>
												</c:if>
												<c:if test="${item.action=='redeem'}">
													<span >赎回</span>
												</c:if>
											</td>
											<td>${item.order_no}</td>
											<td><c:if test="${item.status=='0'}">
													<span class="right label label-warning">处理中</span>
												</c:if>
												<c:if test="${item.status=='1'}">
													<span class="right label label-success">成功</span>
												</c:if>
												<c:if test="${item.status=='2'}">
													<span class="right label label-danger">失败</span>
												</c:if>
											</td>
											<!-- <td><fmt:formatNumber value="${amount}" pattern="#0.0000" /></td>
											<td><fmt:formatNumber value="${amount_before}" pattern="#0.0000" /></td>
											<td><fmt:formatNumber value="${amount_after}" pattern="#0.0000" /></td> -->
											<!-- <td><date name="create_time" format="yyyy-MM-dd HH:mm:ss " /></td> -->
											<td>${item.create_time}</td>
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
		$.fn.datetimepicker.dates['zh'] = {
			days : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" ],
			daysShort : [ "日", "一", "二", "三", "四", "五", "六", "日" ],
			daysMin : [ "日", "一", "二", "三", "四", "五", "六", "日" ],
			months : [ "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月",
					"十月", "十一月", "十二月" ],
			monthsShort : [ "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
					"十一", "十二" ],
			meridiem : [ "上午", "下午" ],
			//suffix:      ["st", "nd", "rd", "th"],  
			today : "今天",
			clear : "清空"
		};

		$(function() {
			$('#start_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			}).on('changeDate',function(ev){
				$("#para_time").val("");
			});
				       

			$('#end_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			}).on('changeDate',function(ev){
				$("#para_time").val("");
			});
			$("#para_time").val("");
		});
	</script>
	
</body>

</html>
