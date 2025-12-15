<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>账变记录</h3>
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminMoneyLogAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
									
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="name_para" name="name_para"
													cssClass="form-control " placeholder="用户名、UID(完整)" /> -->
												<input id="name_para" name="name_para"
													class="form-control " placeholder="用户名、UID(完整)" value="${name_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="log_para" name="log_para"
													cssClass="form-control " placeholder="日志(关键字查询)" /> -->
												<input id="log_para" name="log_para"
													class="form-control " placeholder="日志(关键字查询)" value="${log_para}" />
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
																
								<div class="col-md-12 col-lg-3" style="margin-top:10px;">
									<!-- <s:textfield id="start_time" name="start_time"
										cssClass="form-control " placeholder="开始日期" /> -->
									<input id="start_time" name="start_time"
										class="form-control " placeholder="开始日期" value="${start_time}" />
								</div>
								
								<div class="col-md-12 col-lg-3" style="margin-top:10px;">
									<!-- <s:textfield id="end_time" name="end_time"
										cssClass="form-control " placeholder="结束日期" /> -->
									<input id="end_time" name="end_time"
										class="form-control " placeholder="结束日期" value="${end_time}" />
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
												<select id="freeze" name="freeze" class="form-control " style="margin-top:10px;">
													<option value="0" <c:if test="${freeze == '0'}">selected="true"</c:if> >正常资金</option>
													<option value="1" <c:if test="${freeze == '1'}">selected="true"</c:if> >冻结资金</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-2" style="margin-top:10px;" >
<%-- 									<c:if test="${security.isResourceAccessible('ADMIN_MONEY_LOG_LIST')}"> --%>
										<button type="submit" class="btn btn-light btn-block">查询</button>
<%-- 									</c:if> --%>
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
										<td>用户名</td>
										<td>UID</td>
										<td>推荐人</td>
										<td>账户类型</td>
										<td>冻结资金</td>
										<td>日志</td>
										<td>币种</td>
										<td>金额</td>
										<td>变更前</td>
										<td>变更后</td>
										<td width="150px">时间</td>
										<td width="150px">备注</td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>
											<c:choose>
												<c:when test="${item.username!='' && item.username!=null}">
													<a style="font-size: 10px;" href="#" onClick="getallname('${item.username}')">
														${fn:substring(item.username,0,4)}***${fn:substring(item.username,fn:length(item.username) - 4, fn:length(item.username))}
													</a>
												</c:when>
												<c:otherwise>
													${item.username}
												</c:otherwise>
											</c:choose>
											</td>
											<td>${item.usercode}</td>
											<td>
											<c:choose>
												<c:when test="${item.username_parent!='' && item.username_parent!=null}">
													<a style="font-size: 10px;" href="#" onClick="getallname('${item.username_parent}')">
														${fn:substring(item.username_parent,0,4)}***${fn:substring(item.username_parent,fn:length(item.username_parent) - 4, fn:length(item.username_parent))}
													</a>
												</c:when>
												<c:otherwise>
													${item.username_parent}
												</c:otherwise>
											</c:choose>
											</td>
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
											<td>
												<c:choose>
													<c:when test="${item.freeze=='1'}">
														是
													</c:when>
													<c:when test="${item.freeze=='0'}">
													 否
													</c:when>
												</c:choose>
											</td>
											<td>${item.log}</td>
											<td>${item.wallettype}</td>
											<td><fmt:formatNumber value="${item.amount}" pattern="#0.00" /></td>
											<td><fmt:formatNumber value="${item.amount_before}" pattern="#0.00" /></td>
											<td><fmt:formatNumber value="${item.amount_after}" pattern="#0.00" /></td>
											<td>${item.createTime}</td>
											<td>${item.remarks}</td>
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
		
	    <div class="form-group">
			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="net_form" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel"
					aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">完整用户名（完整钱包地址）</h4>
							</div>
							<div class="modal-header">
									<h4 class="modal-title" name="usernallName" id="usernallName"  readonly="true" style="display: inline-block;"></h4>
									<a href="" id="user_all_name_a" sytle="cursor:pointer;" target="_blank">在Etherscan上查看</a>
							</div>
										
							<div class="modal-body">
									<div class="">
									</div>
							</div>
								
				        </div>
					</div>
				</div>
			</div>
		</div>	

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
		
		function getallname(name){
			$("#usernallName").html(name);
			$("#user_all_name_a").attr("href","https://etherscan.io/address/"+name);
			$("#net_form").modal("show");
		}
	</script>
	
</body>

</html>
