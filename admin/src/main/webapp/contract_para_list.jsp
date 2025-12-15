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
			<h3>交割合约 交易参数管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminContractManageAction!listPara.action"
				method="post" id="queryForm">
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
				<input type="hidden" name="query_symbol" id="query_symbol" value="${query_symbol}" />
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}" />
			</form> 
			<!-- END queryForm -->
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
 								action="<%=basePath%>normal/adminContractManageAction!listPara.action" 
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="query_symbol" name="query_symbol" cssClass="form-control " placeholder="代码"/> -->
												<input id="query_symbol" name="query_symbol" class="form-control " placeholder="代码" value="${query_symbol}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<!-- <sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN"> -->
								<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')}">
								
									<div class="col-md-12 col-lg-2">
										<button type="submit" class="btn  btn-block btn-light">查询</button>
									</div>
								
								<!-- </sec:authorize> -->
								</c:if>
								
							</form>
							
						</div>
						
					</div>
				</div>				
			</div>
			
			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
												
						<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
									 || security.isResourceAccessible('OP_FUTURES_CONTRACT_OPERATE')}">
						
							<a href="<%=basePath%>normal/adminContractManageAction!toAddInstall.action?query_symbol=${query_symbol}"
								class="btn btn-light" style="margin-bottom: 10px"><i class="fa fa-pencil"></i>新增</a>
												
						</c:if>
							
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<!-- <td>名称</td> -->
										<td>代码</td>
										<td>时间</td>
										<td>交割收益（%）</td>
										<td>最低金额</td>
										<!-- <td>最高金额（为0则表示不限制）</td> -->
										<td>手续费（%）</td>
										
										<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
									 				|| security.isResourceAccessible('OP_FUTURES_CONTRACT_OPERATE')}">
										
											<td width="130px"></td>
											
										</c:if>
										
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="contractResult.futures.elements" status="stat"> -->
									<c:forEach items="${contractResult.futures.elements}" var="item" varStatus="stat">
										<tr>
											<!-- <td><s:property value="name" /></td> -->
											<td>${item.symbol}</td>
											<td>${item.timeNum}
												(
													<c:if test="${item.timeUnit == 'second'}">秒</c:if> 
													<c:if test="${item.timeUnit == 'minute'}">分</c:if> 
													<c:if test="${item.timeUnit == 'hour'}">时</c:if> 
													<c:if test="${item.timeUnit == 'day'}">天</c:if> 											
												)
											</td>
											<td>
												${item.profit_ratio}
												~
												${item.profit_ratio_max}
											</td>
											<td>${item.unit_amount}</td>
											<!-- <td><s:property value="unit_max_amount" /></td> -->	
											<td>${item.unit_fee}</td>
											
											<td>
											
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
									 				|| security.isResourceAccessible('OP_FUTURES_CONTRACT_OPERATE')}">
													
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
														
															<li><a href="<%=basePath%>normal/adminContractManageAction!toAddInstall.action?futuresId=${item.id}&query_symbol=${query_symbol}">修改</a></li>
															<li><a href="javascript:del('${item.id}','${item.query_symbol}');" >删除</a></li>
														
														</ul>
													</div>
												
												</c:if>
												
											</td>

										</tr>
									<!-- </s:iterator> -->
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
	
	<form class="form-horizontal" action="<%=basePath%>normal/adminContractManageAction!toDeleteFuturesPara.action"
		method="post" id="succeededForm">
		
		<input type="hidden" name="futuresId" id="futuresId" value="${futuresId}" />
		<input type="hidden" name="query_symbol" id="query_symbol" value="${query_symbol}" />
		
		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_succeeded" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content" >
					
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">确认删除</h4>
						</div>
						
						<div class="modal-body">
						
							<div class="form-group" >
								<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
								<div class="col-sm-4">
									<input id="login_safeword" type="password" name="login_safeword"
										class="login_safeword" placeholder="请输入登录人资金密码" >
								</div>
							</div>
							
							<div class="form-group" >
								<label for="input002" class="col-sm-3 control-label form-label">超级谷歌验证码</label>
								<div class="col-sm-4">
									<input id="super_google_auth_code"  name="super_google_auth_code" placeholder="请输入超级谷歌验证码" >
								</div>
							</div>
							
						</div>
						
						<div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn " data-dismiss="modal">关闭</button>
							<button id="sub" type="submit" class="btn btn-default" >确认</button>
						</div>
						
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal -->
			</div>
		</div>
		
	</form>
	
</body>

<script type="text/javascript">
	function del(id,symbol) {
		$("#futuresId").val(id);
		$("#query_symbol").val(symbol);
		$('#modal_succeeded').modal("show");
	};
</script>

</html>
