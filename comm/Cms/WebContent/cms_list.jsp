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
			<h3>用户端内容管理
			</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
							<s:if test='isResourceAccessible("ADMIN_CMS_LIST")'>
							<form class="form-horizontal"
 								action="<%=basePath%>normal/adminCmsAction!list.action" 
								method="post" id="queryForm">
								<s:hidden name="pageNo" id="pageNo"></s:hidden>
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<s:textfield id="para_title" name="para_title" cssClass="form-control " placeholder="标题"/>
											</div>
										</div>
									</fieldset>
								</div>
								
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
									<button type="submit" class="btn  btn-block btn-default">确定</button>
								</div>
							
							</form>
							</s:if>
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
							<s:if test='isResourceAccessible("ADMIN_CMS_TOADD")'>
							<a href="<%=basePath%>normal/adminCmsAction!toAdd.action"
								class="btn btn-light" style="margin-bottom: 10px"><i
								class="fa fa-pencil"></i>新增公告</a>
							</s:if>
						</sec:authorize>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td >标题</td>
<!-- 										<td>内容</td> -->
<!-- 										<td class="col-md-1">模块</td> -->
										<td class="col-md-2">语言</td>
<!-- 										<td class="col-md-2" width="150px">日期</td> -->
										<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
											<td class="col-md-2"></td>
										</sec:authorize>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
											<%-- <td><a
												href="<%=basePath%>normal/adminCmsAction!toAdd.action?id=<s:property value="id" />"
												target="_blank"><s:property value="title" /></a></td> --%>
											<td><s:property value="title" /></td>	
											<%-- <td title="<s:property value="content"/>">
												<s:if test="content.length()>=17">
													<s:property value="content.substring(0,17)+'....'"  />
												</s:if>
												<s:else><s:property value="content"/></s:else>
											</td> --%>
<%-- 											<td><s:property value="model"  /></td> --%>
											<td class="col-md-2"><s:property value="language"  /></td>
<%-- 											<td><s:date name="createTime" format="yyyy-MM-dd " /></td> --%>
												<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
												<td>
												<s:if test='isResourceAccessible("ADMIN_CMS_TOUPDATE")'>
													<a href="<%=basePath%>normal/adminCmsAction!toUpdate.action?id=<s:property value="id" />"
												class="btn btn-light" style="margin-bottom: 10px">修改</a>
												</s:if>	
													<%-- <div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button"
															class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle
																Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">

															<li><a
																href="<%=basePath%>normal/adminCmsAction!toAdd.action?id=<s:property value="id" />">修改</a></li>

															<li><a
																href="javascript:ondelete('<s:property value="id" />')">删除</a></li>

														</ul>
													</div> --%>
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

	<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
		<form action="<%=basePath%>normal/adminCmsAction!delete.action"
			method="post" id="ondelete">
			<input type="hidden" name="pageNo" id="pageNo"
				value="${param.pageNo}">
			<s:hidden name="id" id="news_id"></s:hidden>
		</form>
		<script type="text/javascript">
			function ondelete(id) {
				$("#news_id").val(id);
				swal({
					title : "是否确认删除?",
					text : "",
					type : "warning",
					showCancelButton : true,
					confirmButtonColor : "#DD6B55",
					confirmButtonText : "确认",
					closeOnConfirm : false
				}, function() {
					document.getElementById("ondelete").submit();
				});

			}
		</script>
	</sec:authorize>

</body>
</html>