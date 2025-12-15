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
			<ul class="nav nav-tabs">
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=en&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">英文</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=vi&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">越南语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=hi&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">印度语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=id&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">印度尼西亚语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=de&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">德语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=fr&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">法语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=ru&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">俄语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=es&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">西班牙语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=pt&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">葡萄牙语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=it&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">意大利语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=ms&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">马来西亚语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=af&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">南非荷兰语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=el&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">希腊语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=tw&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">中文繁体</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=cn&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">中文简体</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=tr&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">土耳其语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=ja&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">日语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=ko&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">韩语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=th&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">泰语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=ph&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">菲律宾语</a></li>
				<li><a href="<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=ar&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}">阿拉伯语</a></li>
			</ul>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form  action="<%=basePath%>mall/goodAttr/value/list.action"
				   method="post" id="queryForm" >
				<input type="hidden" name="pageNo" id="pageNo"/>
				<input type="hidden" name="categoryId" id="categoryIds" value = "${categoryId}"/>
				<input type="hidden" name="attrId" id="attrIds" value = "${attrId}"/>
				<input type="hidden" name="categoryName" id="categoryNames" value = "${categoryName}"/>
			</form>

			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							编辑规格项
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>mall/goodAttr/value/update.action"
								method="post" name="mainForm" id="mainForm">
								<input type="hidden" name="attrId" id="attrId" value = "${attrId}"/>
								<input type="hidden" name="categoryId" id="categoryId" value = "${categoryId}"/>
				                <input type="hidden" name="lang" id="lang" value = "${lang}"/>
				                <input type="hidden" name="attrValueId" id="attrValueId" value = "${attrValueId}"/>
				                <input type="hidden" name="pageNo" id="pageNo" value = "${pageNo}"/>
				                <input type="hidden" name="categoryName" id="categoryName" value = "${categoryName}"/>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label" style="color: red">规格项名称</label>
									<div class="col-sm-3">
										<input id="name" name="name" class="form-control" value="${name}" placeholder="请输入规格名称"/>
									</div>
								</div>
<%--								<div class="form-group">--%>
<%--									<label class="col-sm-2 control-label form-label">所属属性</label>--%>
<%--									<div class="col-sm-3">--%>
<%--										<input id="categoryName" name="categoryName" class="form-control" value="${categoryName}" placeholder="所属属性" readonly = true/>--%>
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
				let name = $("#name").val();
				if(name == ""){
					swal({
						title: "规格项名称不能为空!",
						timer: 2000,
						showConfirmButton: false
					})
					return false;
				}
				document.getElementById("mainForm").submit();
			});

		}
		//初始化执行一次

		$(function(){
			$('.nav-tabs a').filter(function() {
				var b = document.URL;
				var a = "<%=bases%>/mall/category/toUpdate.action?lang=${lang}&categoryId=${categoryId}";
				return this.href == "<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=${lang}&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}";  //获取当前页面的地址
			}).closest('li').addClass('active');  //给当前最靠近的li（其实是现在进入的li）添加‘active’样式

		})

	</script>




</body>
</html>