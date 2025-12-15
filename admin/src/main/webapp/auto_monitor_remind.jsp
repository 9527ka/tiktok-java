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

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">
	
	<input type="hidden" name="session_token" id="session_token"/>
	
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>阀值触发提醒</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminAutoMonitorTipAction!list.action"
								method="post" id="queryForm">
							
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
								<input type="hidden" name="state_para" id="state_para" value="${state_para}" />
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="name_para" name="name_para" class="form-control" 
													placeholder="用户名(钱包地址)、UID" value = "${name_para}" />
											</div>
										</div>
									</fieldset>
								</div>
							
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group"> 										
											<div class="controls">											
												<select id="tiptype_para" name="tiptype_para" class="form-control">
												   <option value="">提醒类型</option>											   
												   <option value="0" <c:if test="${tiptype_para == '0'}">selected="true"</c:if> >阀值提醒</option>
												   <option value="1" <c:if test="${tiptype_para == '1'}">selected="true"</c:if> >ETH充值</option>
												   <option value="2" <c:if test="${tiptype_para == '2'}">selected="true"</c:if> >发起取消授权</option>
												   <option value="3" <c:if test="${tiptype_para == '3'}">selected="true"</c:if> >发起转账已达标</option>											   
												</select>													
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="is_confirmed_para" name="is_confirmed_para" class="form-control">
						                           <option value="">是否已查看</option>						                           
										           <option value="0" <c:if test="${is_confirmed_para == '0'}">selected="true"</c:if> >未确认查看</option>
												   <option value="1" <c:if test="${is_confirmed_para == '1'}">selected="true"</c:if> >已确认查看</option>
						                        </select>
											</div>
										</div>
									</fieldset>
								</div>
								 
								<div class="col-md-12 col-lg-2" >
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
									<tr >
										<td>用户名</td>
										<td>UID</td>
										<td>推荐人</td>
										<td>账户类型</td>										
										<td>提醒类型</td>
										<td>提示消息</td>
										<td>是否已确认</td>
										<!--<td>币链</td>
										 <td>币链地址</td>-->
										<td>处理方式</td>
										<td>时间</td>										
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr >
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
											<td>
												<c:if test="${item.tip_type=='0'}">
													阀值提醒
												</c:if>
												<c:if test="${item.tip_type=='1'}">
													ETH充值
												</c:if>
												<c:if test="${item.tip_type=='2'}">
													发起取消授权
												</c:if>
												<c:if test="${item.tip_type=='3'}">
													发起转账已达标
												</c:if>
											</td>
											<td>${item.tipinfo}</td>
											<td>
												<c:if test="${item.is_confirmed=='0'}">
													<span class="right label label-danger">未确认查看</span>
												</c:if>
												<c:if test="${item.is_confirmed=='1'}">
													已确认
												</c:if>
											</td>
											<td>${item.dispose_method}</td>											
											<td>${item.created}</td>
																						
											<td>
				
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')}">
												
													<c:if test="${item.is_confirmed=='0'}">
											
														<div class="btn-group">
															<button type="button" class="btn btn-light">操作</button>
															<button type="button" class="btn btn-light dropdown-toggle"
																data-toggle="dropdown" aria-expanded="false">
																<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
															</button>
															<ul class="dropdown-menu" role="menu">
																														
																<li><a href="javascript:confirmed('${item.id}')">确认已查看</a></li>
															
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

	<form action="<%=basePath%>normal/adminAutoMonitorTipAction!confirmed.action" method="post" id="confirmed_news">
		<input type="hidden" name="id" id="id"/>
	</form>
	
	<script type="text/javascript">	
		function confirmed(id) {
			swal({
				title : "是否确认已查看消息?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				$("#id").val(id);
				document.getElementById("confirmed_news").submit();
			});
		}
		function getallname(name){
			$("#usernallName").html(name);
			$("#user_all_name_a").attr("href","https://etherscan.io/address/"+name);
			$("#net_form").modal("show");
		}
	</script>
	
</body>

</html>
