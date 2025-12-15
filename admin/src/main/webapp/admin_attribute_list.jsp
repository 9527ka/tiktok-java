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
<body class="ifr-dody">
<%@ include file="include/loading.jsp"%>

<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTENT -->
<div class="ifr-con">

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTAINER -->
	<div class="container-default">
		<h3>规格列表</h3>
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<%@ include file="include/alert.jsp"%>
		<%-- <div class="row">
            <div class="col-md-12">
                <div class="panel panel-default">

                    <div class="panel-title">查询条件</div>
                    <div class="panel-body">

                        <form class="form-horizontal" action="<%=basePath%>normal/adminMinerAction!list.action" method="post"
                            id="queryForm">
                            <input type="hidden" name="pageNo" id="pageNo"
                                value="${param.pageNo}">
                            <div class="col-md-12 col-lg-4">
                                <fieldset>
                                    <div class="control-group">
                                        <div class="controls">
                                        <s:textfield id="name_para" name="name_para" cssClass="form-control " placeholder="产品名称"/>
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
        </div> --%>
		<!-- END queryForm -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->



		<form action="<%=basePath%>mall/goodAttr/value/list.action" method="post"
			  id="queryForms">
			<%--				<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}"/>--%>
			<input type="hidden" id="categoryId" name="categoryId" value="${categoryId}"/>
			<input type="hidden" id="attrId" name="attrId" value="${attrId}"/>
			<input type="hidden" id="categoryName" name="categoryName" value="${categoryName}"/>
		</form>

		<form action="<%=basePath%>mall/goodAttr/list.action" method="post"
			  id="queryForm">
			<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}"/>
			<input type="hidden" id="categoryIds" name="categoryId" value="${categoryId}"/>
			<input type="hidden" id="categoryNames" name="categoryName" value="${categoryName}"/>
		</form>


		<div class="row">


			<div class="col-md-12">
				<!-- Start Panel -->
				<div class="panel panel-default">

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
									 || security.isResourceAccessible('OP_CATEGORY_OPERATE')}">
						<a href="<%=basePath%>mall/goodAttrCategory/list.action" class="btn btn-light" style="margin-bottom: 10px">返回</a>
						<a href="javascript:saveAttribute('${pageNo}','${categoryId}')" class="btn btn-light" style="margin-bottom: 10px">新增英文规格</a>
					</c:if>

					<%-- <a href="<%=basePath%>normal/adminMinerAction!toAdd.action" class="btn btn-light"
                        style="margin-bottom: 10px"><i class="fa fa-pencil"></i>新增</a> --%>
					<div class="panel-body">

						<table class="table table-bordered table-striped">
							<thead>
							<tr>
								<td>规格ID</td>
								<td>规格名称</td>
								<td>规格所属</td>
								<td>排序</td>
								<td>规格项</td>
								<td width="130px"></td>
							</tr>
							</thead>
							<tbody>
							<c:forEach items="${page.getElements()}" var="item"
									   varStatus="stat">
								<tr>
									<td>${item.id}</td>
									<td>${item.attrName}</td>
									<td>${item.categoryName}</td>
									<td>${item.sort}</td>

									<td><a href="#" onClick="getGoodsAttributeValue('${categoryId}','${item.id}','${item.categoryName}')">规格项</a></td>
									<td>
										<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_CATEGORY_OPERATE')}">

											<div class="btn-group">
												<button type="button" class="btn btn-light">操作</button>
												<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
													<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
												</button>
												<ul class="dropdown-menu" role="menu">
													<li><a href="<%=basePath%>mall/goodAttr/toUpdate.action?lang=en&categoryId=${categoryId}&attrId=${item.id}">修改</a></li>
<%--													<li><a href="<%=basePath%>mall/goodAttr/delete.action?id=${item.id}&categoryId=${categoryId}">删除</a></li>--%>
												</ul>
											</div>
										</c:if>
									</td>
								</tr>
							</c:forEach>

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

<div class="form-group">

	<form action="<%=basePath%>mall/goodAttr/add.action" method="post" id="succeededForm" onSubmit="return inputNull(this)">


		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="categoryId" id="categoryId" value="${categoryId}">

		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set1" tabindex="-1" role="dialog"
				 aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">

<%--						<div class="modal-header">--%>
<%--							<button type="button" class="close" data-dismiss="modal"--%>
<%--									aria-hidden="true">&times;</button>--%>
<%--							<h4 class="modal-title">所属属性</h4>--%>
<%--						</div>--%>

<%--						<div class="modal-body">--%>
<%--							<div class="">--%>
<%--								<input id="categoryName" name="categoryName"--%>
<%--									   class="form-control" value="${categoryName}" placeholder="属性名称" readonly="true">--%>
<%--							</div>--%>
<%--						</div>--%>
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">规格名称</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="name" name="name"
									   class="form-control" value="${name}"  placeholder="规格名称">
							</div>
						</div>

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">序号</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="sort" name="sort"
									   class="form-control" value="${sort}" oninput="value=value.replace(/[^\d]/g,'')" placeholder="序号">
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



<!-- End Content -->
<!-- //////////////////////////////////////////////////////////////////////////// -->

<div class="form-group">
	<form
			action=""
			method="post" id="mainform">
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="attributeCategoryId" id="attributeCategoryId" />
		<div class="col-sm-1 form-horizontal">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_succeeded" tabindex="-1"
				 role="dialog" aria-labelledby="myModalLabel"
				 aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content" >
						<div class="modal-header">
							<button type="button" class="close"
									data-dismiss="modal" aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">确认调整</h4>
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
                                    <a id="update_email_code_button" href="javascript:updateSendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
                                </div>
                            </div> -->
							<%--												<div class="form-group" >--%>
							<%--													<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>--%>
							<%--													<div class="col-sm-4">--%>
							<%--														<input id="google_auth_code"  name="google_auth_code"--%>
							<%--															 placeholder="请输入谷歌验证码" >--%>
							<%--													</div>--%>
							<%--												</div>--%>
						</div>
						<div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn "
									data-dismiss="modal">关闭</button>
							<button id="sub" type="submit"
									class="btn btn-default">确认</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal -->
			</div>
		</div>
	</form>
</div>
<%@ include file="include/js.jsp"%>
<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>

<script type="text/javascript">
	function toDelete(attributeCategoryId,pageNo){
		$('#attributeCategoryId').val(attributeCategoryId);
		$('#pageNo').val(pageNo);
		$('#myModalLabel').html("删除");
		$('#mainform').attr("action","<%=basePath%>mall/goodAttrCategory/delete.action");

		$('#modal_succeeded').modal("show");
	}

	function getGoodsAttributeValue(categoryId,attrId,categoryName){
		$("#categoryId").val(categoryId);
		$("#attrId").val(attrId);
		$("#categoryName").val(categoryName);
		$("#pageNo").val(1);
		$("#queryForms").submit();
	}


	function saveAttribute(pageNo,categoryId){
		$('#pageNo').val(pageNo);
		$('#categoryId').val(categoryId);
		$('#modal_set1').modal("show");
	}

	// function updateAttributeCategory(pageNo,name,sort,id){
	// 	$('#pageNo').val(pageNo);
	// 	$('#id').val(id);
	// 	$('#name1').val(name);
	// 	$('#sort1').val(sort);
	// 	$('#modal_set2').modal("show");
	// }


	function inputNull(){
		let sort = $("#sort").val();
		let name = $("#name").val();

		if(sort == ""){
			swal({
				title: "序号不能为空!",
				timer: 2000,
				showConfirmButton: false
			})
			return false;
		}
		if(name === null || name.trim() === ""){
			swal({
				title: "规格名称不能为空!",
				timer: 2000,
				showConfirmButton: false
			})
			return false;
		}
	}
	// function inputNull2(){
	// 	let sort = $("#sort1").val();
	// 	let name = $("#name1").val();
	//
	// 	if(sort == ""){
	// 		swal({
	// 			title: "序号不能为空!",
	// 			timer: 2000,
	// 			showConfirmButton: false
	// 		})
	// 		return false;
	// 	}
	// 	if(name == ""){
	// 		swal({
	// 			title: "规格名称不能为空!",
	// 			timer: 2000,
	// 			showConfirmButton: false
	// 		})
	// 		return false;
	// 	}
	// }


</script>


</body>
</html>