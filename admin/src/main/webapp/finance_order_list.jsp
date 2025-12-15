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
			<h3>理财订单</h3>
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>normal/adminFinanceOrderAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="status_para" id="status_para" value="${status_para}" />
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="order_no_para" name="order_no_para"
													class="form-control " placeholder="订单号（完整）" value="${order_no_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="name_para" name="name_para"
													class="form-control " placeholder="用户名、UID" value="${name_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="finance_para" name="finance_para"
													class="form-control " placeholder="理财产品名称" value="${finance_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
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
								
								<div class="col-md-12 col-lg-2" style="margin-top: 10px;">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>

								<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li><a href="javascript:setState('')"> 全部</a></li>
												<li><a href="javascript:setState('0')">赎回</a></li>
												<li><a href="javascript:setState('1')"> 托管中</a></li>
												<li><a href="javascript:setState('2')">违约</a></li>
											</ul>
										</div>
									</div>
								</div>

							</form>
							
							<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_FINANCE_ORDER_OPERATE')}">
							
								<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="panel-title" style="padding-left: 5px; margin-top: -10px;">操作</div>
										<div class="mailbox-menu" style="border-bottom: hidden;">
											<ul class="menu">

											</ul>
										</div>
									</div>
								</div>
								
								<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
								
									<div class="col-md-12 col-lg-12">
									
										<form class="form-horizontal" action="<%=basePath%>normal/adminFinanceOrderAction!addProfit.action"
											method="post" id="add_profit_form">
											
											<input type="hidden" name="status_para" id="status_para" value="${status_para}" />
											<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
												
											<div class="col-md-12 col-lg-3">
												<fieldset>
													<div class="control-group">
														<div class="controls" style="margin-left: -15px;">
															<input id="system_time" name="system_time"
																class="form-control " placeholder="系统时间" value="${system_time}" />
														</div>
													</div>
												</fieldset>
											</div>
											
											<div class="col-md-12 col-lg-2">
												<button type="button" class="btn btn-light btn-block" data-toggle="modal" data-target="#myModal3"
													onClick="addProfit();">利息重计</button>
											</div>
											
										</form>
										
									</div>
			
								</c:if>
			
							</c:if>

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
										<td>用户名</td>
										<td>UID</td>
										<td>账户名称</td>
										<!-- <td>订单号</td> -->
										<td>产品名称</td>
										<td>产品名称(英文)</td>
										<td>金额</td>
										<td>收益</td>
										<td>买入时间</td>
										<td>赎回时间</td>
										<td>状态</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
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
													<c:otherwise>
													 	${item.roleNameDesc}
													</c:otherwise>	
												</c:choose>
											</td>
											<td>${item.finance_name}</td>
											<td>${item.finance_name_en}</td>
											<td><fmt:formatNumber value="${item.amount}" pattern="#0.00" /></td>
											<td>
												<c:if test="${item.profit < 0}">
													<span class="right label label-danger"><fmt:formatNumber value="${item.profit}" pattern="#0.00" /> </span>
												</c:if> 
												<c:if test="${item.profit >= 0}">
													<span class="right label label-success"><fmt:formatNumber value="${item.profit}" pattern="#0.00" /> </span>
												</c:if> 
											</td>
											<td>${item.create_time}</td>
											<td>${item.close_time}</td>
											<td>
												<c:if test="${item.state=='2'}">
													<span class="right label label-danger">违约(提前赎回)</span>
												</c:if> 
												<c:if test="${item.state=='1'}">
													<span class="right label label-success">托管中</span>
												</c:if> 
												<c:if test="${item.state=='0'}">
													赎回
												</c:if>
											</td>
											
											<td>
							
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
													 || security.isResourceAccessible('OP_FINANCE_ORDER_OPERATE')}">
												
													<c:if test="${item.state=='1'}">
												
														<div class="btn-group">
															<button type="button" class="btn btn-light">操作</button>
															<button type="button"
																class="btn btn-light dropdown-toggle"
																data-toggle="dropdown" aria-expanded="false">
																<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
															</button>
															<ul class="dropdown-menu" role="menu">
															
																<li><a href="javascript:onclose('${item.id}')">赎回</a></li>
																
															</ul>
														</div>
													
													</c:if>
													
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
			})
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
			})
		});
	</script>

	<form action="<%=basePath%>normal/adminFinanceOrderAction!close.action" method="post" id="onclose">
		<input type="hidden" name="id" id="id" value="${id}" />
	</form>
		
	<script type="text/javascript">
		function onclose(id) {
			$("#id").val(id);
			swal({
				title : "是否确认赎回?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("onclose").submit();
			});
		}
	</script>

	<script type="text/javascript">
		function setState(state) {
			document.getElementById("status_para").value = state;
			document.getElementById("queryForm").submit();
		}
	</script>
	
	<script type="text/javascript">
		$('#system_time').datetimepicker({
			format : 'yyyy-mm-dd hh:ii:00',
			minuteStep : 1,
			language : 'zh',
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 2,
			clearBtn : true
		//		minView : 2,
		//		pickerPosition: "bottom-left"
		});
		function addProfit() {
			swal({
				title : "确认重新计算补上理财收益?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				$("#add_profit_form").submit();
			});
		}
	</script>

</body>

</html>
