<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="include/head.jsp"%>
</head>
<body>
	<%@ include file="include/loading.jsp"%>
	<%@ include file="include/top.jsp"%>
	<%@ include file="include/menu_left.jsp"%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="content">
		
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="container-default">
			<h3>横幅管理
			</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
							<form class="form-horizontal"
 								action="<%=basePath%>normal/adminBannerAction!list.action" 
								method="post" id="queryForm">
								<s:hidden name="pageNo" id="pageNo"></s:hidden>
								<%-- <div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<s:textfield id="para_title" name="para_title" cssClass="form-control " placeholder="标题"/>
											</div>
										</div>
									</fieldset>
								</div> --%>
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<s:select id="para_language" cssClass="form-control "
													name="para_language"
													list="languageMap" listKey="key"
													listValue="value" headerKey="" headerValue="所有语言"
													value="para_language" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn  btn-block btn-light">确定</button>
								</div>
							
							</form>
						</div>
						
					</div>
				</div>
				
			</div>
			<%-- <form class="form-horizontal"
				action="<%=basePath%>normal/adminCmsAction!list.action"
				method="post" id="queryForm">
				<input type="hidden" name="pageNo" id="pageNo"
					value="${param.pageNo}">
			</form> --%>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->


			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">
					<div class="panel-title">查询结果</div>
					<sec:authorize ifAnyGranted="ROLE_ROOT">
							<a href="<%=basePath%>normal/adminBannerAction!toAdd.action"
								class="btn btn-light" style="margin-bottom: 10px"><i
								class="fa fa-pencil"></i>新增横幅</a>
					</sec:authorize>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
									<sec:authorize ifAnyGranted="ROLE_ROOT">
										<td >业务代码</td>
										<td >模块</td>
									</sec:authorize>
										<td class="col-md-2">语言</td>
<!-- 										<td >访问路径</td> -->
										<td >图片</td>
										<td >可否点击</td>
										<td >是否展示</td>
										<td >排序</td>
<!-- 										<td class="col-md-2" width="150px">日期</td> -->
										<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
											<td class="col-md-2"></td>
										</sec:authorize>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
										<sec:authorize ifAnyGranted="ROLE_ROOT">
											<td><s:property value="content_code" /></td>	
											<td><s:property value="model"  /></td>
										</sec:authorize>
											<td class="col-md-2"><s:property value="language"  /></td>
<%-- 											<td class="col-md-2"><s:property value="url"  /></td> --%>
											<td class="col-md-2"><img alt="" width="150px" height="55px" src="<s:property value="image"  />"></td>
											<td>
												<s:if test="click==1"><span class="right label label-success">是</span></s:if>
												<s:else><span class="right label label-danger">否</span></s:else>
											</td>
											<td>
												<s:if test="on_show==1"><span class="right label label-success">是</span></s:if>
												<s:else><span class="right label label-danger">否</span></s:else>
											</td>
											<td><s:property value="sort_index"  /></td>
<%-- 											<td><s:date name="createTime" format="yyyy-MM-dd " /></td> --%>
												<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
												<td>
													<%-- <a href="<%=basePath%>normal/adminCmsAction!toUpdate.action?id=<s:property value="id" />"
												class="btn btn-light" style="margin-bottom: 10px">修改</a> --%>
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button"
															class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle
																Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">

															<li><a
																href="<%=basePath%>normal/adminBannerAction!toUpdate.action?id=<s:property value="id" />">修改</a></li>

															<li><a
																href="javascript:ondelete('<s:property value="id" />')">删除</a></li>

														</ul>
													</div>
												</td>
												</sec:authorize>
										</tr>
									</s:iterator>

								</tbody>
							</table>
							<%@ include file="include/page_simple.jsp"%>
							<nav>
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

		<form action="<%=basePath%>normal/adminBannerAction!delete.action"
			method="post" id="ondelete">
			<input type="hidden" name="pageNo" id="pageNo"
				value="${param.pageNo}">
			<s:hidden name="id" id="news_id"></s:hidden>
			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="modal_succeeded" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel"
					aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content" >
							<div class="modal-header">
								<button type="button" class="close"
									data-dismiss="modal" aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="myModalLabel">确认新增</h4>
							</div>
							<div class="modal-body">
								<div class="form-group" >
									<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
									<div class="col-sm-4">
										<input id="login_safeword" type="password" name="login_safeword"
											class="login_safeword" placeholder="请输入登录人资金密码" >
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
								</div> 
								<div class="form-group" >
									<label for="input002" class="col-sm-3 control-label form-label">超级谷歌验证码</label>
									<div class="col-sm-4">
										<input id="super_google_auth_code"  name="super_google_auth_code"
											 placeholder="请输入超级谷歌验证码" >
									</div>
								</div>-->
							</div>
							<div class="modal-footer" style="margin-top: 0;">
								<button type="button" class="btn "
									data-dismiss="modal">关闭</button>
								<button id="sub" type="submit"
									class="btn btn-default" >确认</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal -->
				</div>
			</div>
		</form>
		<script type="text/javascript">
			function ondelete(id) {
				$("#news_id").val(id);
				$('#modal_succeeded').modal("show");
				/* swal({
					title : "是否确认删除?",
					text : "",
					type : "warning",
					showCancelButton : true,
					confirmButtonColor : "#DD6B55",
					confirmButtonText : "确认",
					closeOnConfirm : false
				}, function() {
					document.getElementById("ondelete").submit();
				}); */

			}
		</script>

</body>
</html>