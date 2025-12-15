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

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>质押2.0收益单</h3>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>normal/adminPledgeGalaxyProfitAction!list.action" method="post"
								id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								<input type="hidden" name="status_para" id="status_para" value="${status_para}">
									
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="order_no_para" name="order_no_para"
													class="form-control " placeholder="订单号" value="${order_no_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<input id="name_para" name="name_para" class="form-control" 
											placeholder="用户名(钱包地址)、UID" value = "${name_para}"/>
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
												   <%-- <option value="TEST" <c:if test="${rolename_para == 'TEST'}">selected="true"</c:if> >试用账号</option> --%>
												</select>		
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>

								<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li><a href="javascript:setState('')"> 全部</a></li>
												<li><a href="javascript:setState('0')"> 待领取</a></li>
												<li><a href="javascript:setState('1')"> 待审核</a></li>
												<li><a href="javascript:setState('2')"> 已通过</a></li>
												<li><a href="javascript:setState('3')"> 不通过</a></li>
												<li><a href="javascript:setState('4')"> 已过期</a></li>
											</ul>
										</div>
									</div>
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
										<td>账户类型</td>
										<td>推荐人</td>
										<td>订单号</td>
										<td>收益类型</td>
										<td>收益金额</td>
										<td>收益状态</td>
										<td>审核时间</td>
										<td>收益到期时间</td>
										<td>收益记录创建时间</td>
										<td>关联质押订单号</td>
										<td width="130px"></td>
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
											<td>${item.uuid}</td>
											<td>
												<c:if test="${item.type == '1'}">静态收益</c:if>
												<c:if test="${item.type == '2'}">助力收益</c:if>
												<c:if test="${item.type == '3'}">团队收益</c:if>
											</td>
											<td>${item.amount}</td>
											<td>
												<c:if test="${item.status == '0'}">待领取</c:if>
												<c:if test="${item.status == '1'}">待审核</c:if>
												<c:if test="${item.status == '2'}">已通过</c:if>
												<c:if test="${item.status == '3'}">不通过</c:if>
												<c:if test="${item.status == '4'}">已过期</c:if>
											</td>
											<td>${item.audit_time}</td>
											<td>${item.expire_time}</td>
											<td>${item.create_time}</td>
											<td>${item.relation_order_no}</td>
											
											<td>
				
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 							|| security.isResourceAccessible('OP_PLEDGE_GALAXY_PROFIT_OPERATE')}">
															 
													<c:if test="${item.status == '1'}">
												
														<div class="btn-group">
															<button type="button" class="btn btn-light">操作</button>
															<button type="button" class="btn btn-light dropdown-toggle"
																data-toggle="dropdown" aria-expanded="false">
																<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
															</button>
															<ul class="dropdown-menu" role="menu">
																															
																<li><a href="javascript:savePassed('${item.uuid}')">审核通过</a></li>
																<li><a href="javascript:saveFailed('${item.uuid}')">驳回</a></li>
																
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
								<div class=""> </div>
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
	
	<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>

	<form action="<%=basePath%>normal/adminPledgeGalaxyProfitAction!savePassed.action"
		method="post" id="savePassed">
		<input type="hidden" name="partyId" id="partyId_savePassed"/>
	</form>

	<script type="text/javascript">
		function savePassed(partyId) {
			$("#partyId_savePassed").val(partyId);
			swal({
				title : "是否确认审核通过?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("savePassed").submit();
			});
		}
	</script>
	
	<!-- Modal -->
	<div class="modal fade" id="modal_saveFailed" tabindex="-1"
		role="dialog" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
			
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">请输入驳回原因</h4>
				</div>
				
				<div class="modal-body">
					<form action="<%=basePath%>normal/adminPledgeGalaxyProfitAction!saveFailed.action"
						method="post" id="saveFailed">
						<input type="hidden" name="partyId" id="partyId_saveFailed">
						<input id="msg" name="msg" class="form-control " placeholder="驳回原因">
					</form>
				</div>
				
				<div class="modal-footer">
					<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-default" onclick="saveFailed_confirm()">确认驳回</button>
				</div>
				
			</div>
		</div>
	</div>

	<script type="text/javascript">
		function saveFailed_confirm() {
			swal({
				title : "是否确认驳回?",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("saveFailed").submit();
			});
		};
		function saveFailed(partyId) {
			$("#partyId_saveFailed").val(partyId);
			$('#modal_saveFailed').modal("show");
		};
	</script>

 	<script>
        $(function () {
            /* var data = <s:property value="result" escapeHtml='false' />; */
            var data = ${result};
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
	
	<script type="text/javascript">
		function getallname(name){
			$("#usernallName").html(name);
			$("#user_all_name_a").attr("href","https://etherscan.io/address/"+name);
			$("#net_form").modal("show");
		}
	</script>

	<script type="text/javascript">
		function setState(state) {
    		document.getElementById("status_para").value=state;
    		document.getElementById("queryForm").submit();
		}
	</script>
	
</body>

</html>
