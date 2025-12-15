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
			<h3>代理商</h3>
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminAgentAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"> 
								<input type="hidden" name="para_party_id" id="para_party_id" value="${para_party_id}">
									
								<div class="col-md-12 col-lg-4">
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
													<select id="view_para" name="view_para" class="form-control " >
													   <option value="">切换视图</option>
													   <option value="level"<c:if test="${view_para == 'level'}">selected="true"</c:if> >层级视图</option>
													   <option value="list"<c:if test="${view_para == 'list'}">selected="true"</c:if> >列表视图</option>
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
									 || security.isResourceAccessible('OP_AGENT_OPERATE')}">
							 
							<a href="<%=basePath%>normal/adminAgentAction!toAdd.action" class="btn btn-light" style="margin-bottom: 10px">
								<i class="fa fa-pencil"></i>新增代理商</a>
								
						</c:if>
						
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>用户名</td>
										<td>UID(推荐码)</td>
										<td>上级推荐人</td>
										<td>分享地址</td>
										<td>备注</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>
												<c:choose>
												   <c:when test="${view_para != 'list'}">
												     <a href="#" onClick="getNextLvel('${item.partyId}')">
														${item.username} （网络代理数：${item.reco_agent}）
													</a>
												   </c:when>												   
												   <c:otherwise>
												     	${item.username}
												   </c:otherwise>												  
												</c:choose>
											</td>
											<td>${item.usercode}</td>
											<td>${item.username_parent}</td>
											<td>${item.share_url}</td>
											<td>${item.remarks}</td>
											<td>
						
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_AGENT_OPERATE')}">
									 
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
															<li><a href="<%=basePath%>normal/adminAgentAction!toUpdate.action?id=${item.id}&name_para=${item.username}">修改</a></li>
															<li><a href="javascript:resetpsw('${item.id}')">重置登录密码</a></li>
															<li>
															   <a href="<%=basePath%>normal/adminGoogleAuthAction!toUpdateGoogleAuth.action?username=${item.username}&from_page=agent">谷歌验证器</a>
															</li>
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

		<!-- 模态框 -->
		<div class="form-group">
		
			<form action="<%=basePath%>normal/adminAgentAction!resetpsw.action"
				method="post" id="succeededForm">
				
				<input type="hidden" name="pageNo" id="pageNo">
				<!-- <s:hidden name="id" id="id_resetpsw"></s:hidden> -->
				<input type="hidden" id="id_resetpsw" name="id"/>
				<!-- <s:hidden name="name_para" id="name_para"></s:hidden> -->
				<input type="hidden" id="name_para" name="name_para"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_set" tabindex="-1" role="dialog"
						aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">重置代理商登录密码</h4>
								</div>
								
								<div class="modal-body">
									<div class="">
										<!-- <s:password id="password" name="password"
											cssClass="form-control " /> -->
										<input type="password" id="password" name ="password" class="form-control " />
									</div>
								</div>
								
								<div class="modal-header">
									<h4 class="modal-title" id="myModalLabel">管理员资金密码验证</h4>
								</div>
								
								<div class="modal-body">
									<div class="">
										<input id="safeword" type="password" name="safeword"
											class="form-control" placeholder="请输入管理员资金密码">
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

	<%@ include file="include/js.jsp"%>
	
	<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
	
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
		function resetpsw(id) {
			$("#id_resetpsw").val(id);
			$('#modal_set').modal("show");
		}

		function modifyBlockChain(userename){
		    location.href = '<%=basePath%>normal/adminChannelBlockchainAction!personList.action?username='+userename+'&from_page=agent';
		}

		function getNextLvel(id) {
			$("#para_party_id").val(id);
			$("#para_agent_view").val($("#para_agent_view_checkbox").is(':checked'));
			$("#queryForm").submit();
		}
	</script>
	
</body>

</html>
