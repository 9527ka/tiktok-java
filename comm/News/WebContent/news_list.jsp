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
			<h3>新闻管理
			</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
							<s:if test='isResourceAccessible("ADMIN_NEWS_LIST")'>
							<form class="form-horizontal"
 								action="<%=basePath%>normal/adminNewsAction!list.action" 
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
				action="<%=basePath%>normal/adminNewsAction!list.action"
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
						<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
						<s:if test='isResourceAccessible("ADMIN_NEWS_TOADD")'>
							<a href="<%=basePath%>normal/adminNewsAction!toAdd.action"
								class="btn btn-light" style="margin-bottom: 10px"><i
								class="fa fa-pencil"></i>新增新闻</a>
						</s:if>
						</sec:authorize>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td >标题</td>
										<td >是否置顶</td>
										<td >日期</td> 
										<td class="col-md-2">语言</td>
 										
										<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
											<td class="col-md-2"></td>
										</sec:authorize>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
										
											<td><s:property value="title" /></td>
											<td>
												<s:if test='index_top=="Y"'>
													<span class="right label label-success">
														是
													</span>
												</s:if>
												<s:if test='index_top=="N"'>
													否
												</s:if>
											
											</td>	
											<td><s:date name="create_time" format="YYYY-MM-dd HH:mm:ss " /></td>
											<td class="col-md-2">
											<s:if test='language=="zh-CN"'>
												简体中文
											</s:if>
											<s:if test='language=="CN"'>
												繁体中文
											</s:if>
											<s:if test='language=="en"'>
												英语
											</s:if>
											</td>
											
											
												<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
												<td>
														
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button"
															class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle
																Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
															<s:if test='isResourceAccessible("ADMIN_NEWS_TOUPDATE")'>
															<li><a
																href="<%=basePath%>normal/adminNewsAction!toUpdate.action?id=<s:property value="id" />">修改</a></li>
															</s:if>
															<s:if test='isResourceAccessible("ADMIN_NEWS_DELETE")'>
															<li><a
																href="javascript:ondelete('<s:property value="id" />')">删除</a></li>
															</s:if>

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

	<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
	<s:if test='isResourceAccessible("ADMIN_NEWS_DELETE")'>
															
		<form action="<%=basePath%>normal/adminNewsAction!delete.action"
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
	</s:if>
	</sec:authorize>

</body>
</html>