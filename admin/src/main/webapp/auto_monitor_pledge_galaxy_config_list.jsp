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
			<h3>质押2.0配置</h3>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminPledgeGalaxyConfigAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
									
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="name_para" name="name_para"
													class="form-control " placeholder="用户名(钱包地址)、UID" value="${name_para}" />
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
													<option value="AGENT" <c:if test="${rolename_para == 'AGENT'}">selected="true"</c:if> >代理商</option>
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
						
						<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_PLEDGE_GALAXY_CONFIG_OPERATE')}">
						
							<a href="<%=basePath%>normal/adminPledgeGalaxyConfigAction!toAdd.action"
								class="btn btn-light" style="margin-bottom: 10px"><i
								class="fa fa-pencil"></i>新增</a>
					
						</c:if>
							
						<div class="panel-body">

							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>用户名</td>
										<td>UID</td>
										<td>账户类型</td>
										<td>推荐人</td>
										<td>参与金额最小值</td>
										<td>参与金额最大值</td>
										<td>有效下级质押金额最小值</td>
										<td>静态收益原力值</td>
										<td>动态收益助力值</td>
										<td>团队收益利润率</td>
										<td>创建时间</td>
										<td>更新时间</td>
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
											<td>${item.pledge_amount_min}</td>
											<td>${item.pledge_amount_max}</td>
											<td>${item.valid_recom_pledge_amount_min}</td>
											<td>${item.static_income_force_value}</td>
											<td>${item.dynamic_income_assist_value}</td>
											<td>${item.team_income_profit_ratio}</td>
											<td>${item.created}</td>
											<td>${item.updated}</td>
	
											<td>
											
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 							|| security.isResourceAccessible('OP_PLEDGE_GALAXY_CONFIG_OPERATE')}">
											
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
														
															<li><a href="<%=basePath%>normal/adminPledgeGalaxyConfigAction!toUpdate.action?id=${item.id}">修改</a></li>
															<c:if test="${item.id != '2c948a827cd5f779017cd2322f5d0001'}">
																<li><a href="javascript:delete_to('${item.id}')">删除</a></li>
															</c:if>
															
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
					<!-- End Panel -->

				</div>
			</div>

		</div>
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		
		<!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/adminPledgeGalaxyConfigAction!toDelete.action"
				method="post" id="deleteForm">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="id" id="id_delete">
				<input type="hidden" name="session_token" id="session_token_delete" value="${session_token}">
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_delete" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">删除 质押2.0配置</h4>
								</div>
								
								<div class="modal-header">
									<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
								</div>
								
								<div class="modal-body">
									<div class="">
										<input id="login_safeword" type="password"
											name="login_safeword" class="form-control"
											placeholder="请输入登录人资金密码">
									</div>
								</div>
								
								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
									<button id="sub" type="submit" class="btn btn-default">确认</button>
								</div>
								
							</div>
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
			</form>
			
		</div>

		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	
	<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
	
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

	<%@ include file="include/js.jsp"%>

	<script type="text/javascript">
		function getallname(name){
			$("#usernallName").html(name);
			$("#user_all_name_a").attr("href","https://etherscan.io/address/"+name);
			$("#net_form").modal("show");
		}
	</script>
	
	<script>
		$(function() {
			/* var data = <s:property value="result" escapeHtml='false' />; */
			var data = ${result};
			console.log(data);
			$("#treeview4").treeview({
				color : "#428bca",
				enableLinks : true,
				nodeIcon : "glyphicon glyphicon-user",
				data : data,
				levels : 4,
			});
		});
	</script>

	<script type="text/javascript">
		function delete_to(id) {
			var session_token = $("#session_token").val();
			$("#session_token_delete").val(session_token);
			$("#id_delete").val(id);
			$('#modal_delete').modal("show");
		}
	</script>
	
</body>

</html>
