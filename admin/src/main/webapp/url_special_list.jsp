<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>

	<%@ include file="include/loading.jsp"%>
	<%-- <%@ include file="include/top.jsp"%>
	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>特殊url管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminUrlSpecialAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="url_para" name="url_para"
													cssClass="form-control " placeholder="url" /> -->
												<input id="url_para" name="url_para" class="form-control " placeholder="url" value="${url_para}" />
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
						
						<a
							href="<%=basePath%>normal/adminUrlSpecialAction!toAdd.action?url_para=${url_para}"
							class="btn btn-light" style="margin-bottom: 10px"><i
							class="fa fa-pencil"></i>新增特殊URL</a>
							
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>URL</td>
										<td>备注</td>
										<!--                                 <td>添加时间</td> -->
										<td style="width: 130px;"></td>
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.url}</td>
											<td>${item.remarks}</td>
											<%-- <td><s:date name="create_time" format="yyyy-MM-dd HH:mm" /></td> --%>
											<td>
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<li><a
															href="<%=basePath%>normal/adminUrlSpecialAction!toUpdate.action?id=${item.id}">修改</a></li>
														<li><a
															href="javascript:del('${item.id}')">删除</a></li>
													</ul>
												</div>
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
	
	<form
		action="<%=basePath%>normal/adminUrlSpecialAction!toDelete.action"
		method="post" id="succeededForm">
		
		<!-- <s:hidden name="id" id="id"></s:hidden> -->
		<input type="hidden" name="id" id="id" value="${id}">
		<%-- 	<s:hidden name="query_symbol" id="query_symbol"></s:hidden> --%>
		
		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_succeeded" tabindex="-1"
				role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
					
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">确认删除</h4>
						</div>
						
						<div class="modal-body">
						
							<div class="form-group">
								<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
								<div class="col-sm-4">
									<input id="login_safeword" type="password"
										name="login_safeword" class="login_safeword"
										placeholder="请输入登录人资金密码">
								</div>
							</div>
							
							<!-- <div class="form-group" style="">						
							<label for="input002" class="col-sm-3 control-label form-label">验证码</label>
							<div class="col-sm-4">
								<input id="email_code" type="text" name="email_code"
								class="login_safeword" placeholder="请输入验证码" >
							</div>
							<div class="col-sm-4">
								<button id="email_code_button" 
										class="btn btn-light " onClick="sendCode();" >获取验证码</button>
								<a id="email_code_button" href="javascript:sendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
							</div>
							</div> -->
							<!-- <div class="form-group" >
							<label for="input002" class="col-sm-3 control-label form-label">超级谷歌验证码</label>
							<div class="col-sm-4">
								<input id="super_google_auth_code"  name="super_google_auth_code"
									 placeholder="请输入超级谷歌验证码" >
							</div>
							</div> -->
							
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
	
</body>

<script type="text/javascript">
	function del(id) {

		$("#id").val(id);
		// 		$("#query_symbol").val(symbol);
		$('#modal_succeeded').modal("show");
	};
</script>

</html>
