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
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=en&categoryId=${categoryId}">英文</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=vi&categoryId=${categoryId}">越南语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=hi&categoryId=${categoryId}">印度语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=id&categoryId=${categoryId}">印度尼西亚语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=de&categoryId=${categoryId}">德语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=fr&categoryId=${categoryId}">法语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=ru&categoryId=${categoryId}">俄语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=es&categoryId=${categoryId}">西班牙语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=pt&categoryId=${categoryId}">葡萄牙语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=it&categoryId=${categoryId}">意大利语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=ms&categoryId=${categoryId}">马来西亚语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=af&categoryId=${categoryId}">南非荷兰语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=el&categoryId=${categoryId}">希腊语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=tw&categoryId=${categoryId}">中文繁体</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=cn&categoryId=${categoryId}">中文简体</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=tr&categoryId=${categoryId}">土耳其语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=ja&categoryId=${categoryId}">日语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=ko&categoryId=${categoryId}">韩语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=th&categoryId=${categoryId}">泰语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=ph&categoryId=${categoryId}">菲律宾语</a></li>
				<li><a href="<%=bases%>/mall/category/toUpdate.action?lang=ar&categoryId=${categoryId}">阿拉伯语</a></li>
			</ul>
			<h3>商品分类</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>mall/category/list.action?level=0" method="post"
				  id="queryForm">
				<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}"/>
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							编辑分类
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>mall/category/update.action"
								method="post" name="mainForm" id="mainForm">
								<input type="hidden" name="id" id="id" value = "${id}"/>
				                <input type="hidden" name="categoryId" id="categoryId" value = "${categoryId}"/>
								<input type="hidden" name="iconImg" id="iconImg" value = "${iconImg}"/>
				                <input type="hidden" name="categoryLanId" id="categoryLanId" value = "${categoryLanId}"/>
				                <input type="hidden" name="lang" id="lang" value = "${lang}"/>
				                <input type="hidden" name="pageNo" id="pageNo" value = "${pageNo}"/>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">商品分类名称</label>
									<div class="col-sm-3">
										<input id="name" name="name" class="form-control" value="${name}" placeholder="请输入商品分类名称"/>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">上级分类</label>
									<div class="col-sm-3 " style = width:800px;>
										<div class="input-group">

											<select id="parentId" name="parentId"
													class="form-control">
												<option value="0">无</option>
												<c:forEach var = "item" items = "${categoryMap}">
													<option value="${item.key}" <c:if test="${parentId == item.key}">selected="true"</c:if> >${item.value}</option>
												</c:forEach>
											</select>


										</div>
									</div>
								</div>

									<div class="form-group">
										<label class="col-sm-2 control-label form-label">序号</label>
										<div class="col-sm-3">
											<input id="rank" name="rank" class="form-control" value="${rank}" placeholder="请输入排序" oninput="value=value.replace(/[^\d]/g,'')"/>
										</div>
									</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">封面图(*)</label>

									<div class="col-sm-3">
										<input type="file" id="fileName" name="fileName"  value="${fileName}" onchange="upload();"  style="position:absolute;opacity:0;">
										<label for="fileName">　　
											　　　　　　
											<div class="avatar">
												<img width="90px" height="90px" id="show_img" src="<%=base%>/image/add.png" /> 　
											</div>
											　　
										</label> 　　
										<div style="float: left;width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</div>
									</div>
								</div>


								<div class="form-group">
									<label class="col-sm-2 control-label form-label" style="color: red">描述</label>
									<div class="col-sm-4">
										<textarea class="form-control" rows="7" id="des_text" name="des_text" placeholder="请输入内容">${des}</textarea>
										<input type="hidden" name="des" id="des" value="${des}" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">导航栏显示</label>
									<div class="col-sm-3 ">
										<div class="input-group">

											<select id="status" name="status"
													class="form-control ">
												<option value="1" <c:if test="${status == '1'}">selected="true"</c:if>>显示</option>
												<option value="0" <c:if test="${status == '0'}">selected="true"</c:if>>不显示</option>
											</select>

										</div>
									</div>
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
				document.getElementById("mainForm").submit();
			});

		}
		//初始化执行一次
		setTimeout(function() {
			start();
		}, 100);

		function start(){
			var img = $("#iconImg").val();
			var show_img = document.getElementById('show_img');
			show_img.src=img;
			console.log(img)
		}



		function upload(){
			var fileReader = new FileReader();
			var formData = new FormData();
			var file = document.getElementById('fileName').files[0];
			formData.append("file", file);
			formData.append("moduleName","type");
			$.ajax({
				type: "POST",
				url: "<%=basePath%>normal/uploadimg!execute.action",
				data: formData,
				dataType: "json",
				contentType: false,
				processData: false,
				success : function(data) {
					console.log(data);
					$("#iconImg").val(data.data)
					var show_img = document.getElementById('show_img');
					show_img.src=data.data;

				},
				error : function(XMLHttpRequest, textStatus,
								 errorThrown) {
					console.log("请求错误");
				}
			});

		}

		$(function(){
			$('.nav-tabs a').filter(function() {
				var b = document.URL;
				var a = "<%=bases%>/mall/category/toUpdate.action?lang=${lang}&categoryId=${categoryId}";
				return this.href == "<%=bases%>/mall/category/toUpdate.action?lang=${lang}&categoryId=${categoryId}";  //获取当前页面的地址
			}).closest('li').addClass('active');  //给当前最靠近的li（其实是现在进入的li）添加‘active’样式

		})

	</script>




</body>
</html>