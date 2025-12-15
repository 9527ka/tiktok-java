<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
	<style>
		.sweet-alert{
			top:20%!important;
		}
	</style>
	<div class="ifr-dody">



		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>属性规格</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form  action="<%=basePath%>mall/goodAttr/list.action"
				method="post" id="queryForm" >
				<input type="hidden" name="pageNo" id="pageNo"/>
				<input type="hidden" name="categoryId" id="categoryIds" value = "${categoryId}"/>
				<input type="hidden" name="categoryName" id="categoryNames" value = "${categoryName}"/>
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							添加中文属性规格
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>mall/goodAttr/add.action"
								method="post" name="mainForm" id="mainForm">
								<input type="hidden" name="pageNo" id="pageNo" value = "${pageNo}"/>
								<input type="hidden" name="categoryId" id="categoryId" value = "${categoryId}"/>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">规格名称</label>
									<div class="col-sm-3">
										<input id="name" name="name" class="form-control" value="${param.name}" placeholder="请输入规格名称"/>
									</div>
								</div>
<%--								<div class="form-group">--%>
<%--									<label class="col-sm-2 control-label form-label">所属属性</label>--%>
<%--									<div class="col-sm-3">--%>
<%--										<input id="categoryName" name="categoryName" class="form-control" value="${categoryName}" placeholder="所属属性" readonly = true/>--%>
<%--									</div>--%>
<%--								</div>--%>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label" style="color: red">规格项</label>
									<div class="col-sm-3">
										<textarea class="form-control" rows="7" id="values" name="values" placeholder="请输入内容">${param.values}</textarea>
										<input type="hidden" name="des" id="des" value="${param.values}" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">序号</label>
									<div class="col-sm-3">
										<input id="sort" name="sort" class="form-control" value="${param.sort}" placeholder="请输入排序" oninput="value=value.replace(/[^\d]/g,'')"/>
									</div>
								</div>



								
<%--								<div class="col-sm-1">--%>
<%--									<!-- 模态框（Modal） -->--%>
<%--									<div class="modal fade" id="modal_succeeded" tabindex="-1"--%>
<%--										role="dialog" aria-labelledby="myModalLabel"--%>
<%--										aria-hidden="true">--%>
<%--										<div class="modal-dialog">--%>
<%--											<div class="modal-content" style="width: 350px;">--%>
<%--												<div class="modal-header">--%>
<%--													<button type="button" class="close"--%>
<%--														data-dismiss="modal" aria-hidden="true">&times;</button>--%>
<%--													<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>--%>
<%--												</div>--%>
<%--												<div class="modal-body">--%>
<%--													<div class="" >--%>
<%--														<input id="login_safeword" type="password" name="login_safeword"--%>
<%--															class="login_safeword" placeholder="请输入登录人资金密码" style="width: 250px;">--%>
<%--													</div>--%>
<%--												</div>--%>
<%--												<div class="modal-footer" style="margin-top: 0;">--%>
<%--													<button type="button" class="btn "--%>
<%--														data-dismiss="modal">关闭</button>--%>
<%--													<button id="sub" type="submit"--%>
<%--														class="btn btn-default" >确认</button>--%>
<%--												</div>--%>
<%--											</div>--%>
<%--											<!-- /.modal-content -->--%>
<%--										</div>--%>
<%--										<!-- /.modal -->--%>
<%--									</div>--%>
<%--								</div>--%>
								
								
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

		// function submit() {
		// 	$('#modal_succeeded').modal("show");
		// }
		var a = true;

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
				inputNull();
				if (!a){
					a = true;
					return false;
				} else {
					a = true;
					document.getElementById("mainForm").submit();
				}
			});

		}

		function inputNull(){
			let sort = $("#sort").val();
			let name = $("#name").val();
			let values = $("#values").val();

			if(sort == ""){
				swal({
					title: "序号不能为空!",
					timer: 1500,
					showConfirmButton: false
				})
				a =  false;
			}

			if(name == ""){
				swal({
					title: "属性名称不能为空!",
					timer: 1500,
					showConfirmButton: false
				})
				a  = false;
			}

			if(values == ""){
				swal({
					title: "规格项不能为空!",
					timer: 1500,
					showConfirmButton: false
				})
				a = false;
			}
		}


	</script>




</body>
</html>