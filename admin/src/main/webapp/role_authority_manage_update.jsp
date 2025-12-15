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
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<form action="<%=basePath%>normal/adminRoleAuthorityAction!list.action"
			method="post" id="queryForm">
			<!-- <s:hidden name="pageNo" id="pageNo"></s:hidden> -->
			<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}"/>
			<!-- <s:hidden name="username_para" id="username_para"></s:hidden> -->
			<input type="hidden" id="username_para" name="username_para" value="${username_para}"/>
		</form>
		
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>角色权限配置</h3>
			
			<%@ include file="include/alert.jsp"%>

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">
						<div class="panel-title">权限列表</div>
						<div class="panel-body ">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminRoleAuthorityAction!update.action"
								method="post" name="mainForm" id="mainForm">
								
								<!-- <s:hidden name="username_para" id="username_para"></s:hidden> -->
								<input type="hidden" id="username_para" name="username_para" value="${username_para}"/>
								<!-- <s:hidden name="id" id="id"></s:hidden> -->
								<input type="hidden" id="id" name="id" value="${id}"/>
								<input type="hidden" id="role_resource_checked" value="${role_resource}" />
								
								<div class="form-group">
								
									<table class="table table-bordered table-striped">
									
										<thead>
											<tr>
												<td width="200px">模块</td>
												<td>权限</td>
											</tr>
										</thead>
										
										<tbody>
											<!-- <s:iterator value="auth_datas" status="stat"> -->
											<c:forEach items="${auth_datas}" var="item" varStatus="stat">
												<tr>
													<td>
														<div class="checkbox checkbox-success checkbox-inline">
															<input type="checkbox"
																id="inlineCheckbox12_${item.model}"
																value="${item.model}"
																onClick="modelChecked(this)"> 
																<label for="inlineCheckbox12_${item.model}">
																	${item.model_name}
																</label>
														</div>
													</td>
													<td data-model="${item.model}" id="model_content_${item.model}">
															<!-- <s:iterator value="contents" status="content_stat" id="content"> -->
															<c:forEach items="${contents}" var="item" varStatus="content_stat">
																<div class="checkbox checkbox-success checkbox-inline">
																	<input type="checkbox"
																		id="inlineCheckbox12_${#item.res_string}"
																		name="role_resource"
																		value="${#item.res_string}"
																		onClick="resourceChecked(this)"> 
																	<label for="inlineCheckbox12_${#item.res_string}">
																		${#item.res_string}
																	</label>
																</div>
															<!-- </s:iterator> -->
															</c:forEach>
														</td>
												</tr>
											<!-- </s:iterator> -->
											</c:forEach>
										</tbody>
										
									</table>
									
								</div>
								
								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<a href="javascript:goUrl(${pageNo})"
											class="btn">取消</a> <a href="javascript:submit()"
											class="btn btn-default">保存</a>
									</div>
								</div>
								
							</form>
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

	<script type="text/javascript">
		function update_value(code, snotes, svalue) {
			document.getElementById("change_value").value = svalue;
			document.getElementById("titlediv").innerText = snotes;
			$("#code").val(code);
			$('#modal_set').modal("show");
		}
	</script>

	<script type="text/javascript">
		function submit() {
			swal({
				title : "是否保存?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("mainForm").submit();
			});

		}
		init();
		function init() {
			var resources = $("#role_resource_checked").val();
			$.each(resources.split(","), function(index, value) {//默认选中
				$("#inlineCheckbox12_" + value).prop("checked", "checked");
			});
			$.each($("td[name='model_content']"), function(index, value) {//默认选中
				modelContentChecked(value);
			});
		}
		function modelContentChecked(value) {
			var input_size = $(value).find("input").size();//未选中长度
			var input_checked_size = $(value).find("input:checked").size();//已选中长度
			var model = $(value).attr("data-model");
			if (input_size == input_checked_size) {
				$("#inlineCheckbox12_" + model).prop("checked", "checked");
			} else {
				$("#inlineCheckbox12_" + model).removeAttr("checked");
			}
		}
		function modelChecked(obj) {
			var model = $(obj).val();
			if ($(obj).is(":checked")) {
				$.each($("#model_content_" + model).find("input"), function(index, value) {
					$(value).prop("checked", "checked");
					;
				});
			} else {
				$.each($("#model_content_" + model).find("input"), function(index, value) {
					$(value).removeAttr("checked");
				});
			}
		}
		function resourceChecked(obj) {
			modelContentChecked($(obj).parents("td").first());
		}
	</script>
	
</body>

</html>
