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

<script charset="UTF-8" src="<%=basePath%>js/kindeditor/kindeditor-all-min.js"></script>
<script charset="UTF-8" src="<%=basePath%>js/kindeditor/lang/zh-CN.js"></script>
<script type="text/javascript">
	var editor;
	var editor1;
	KindEditor.ready(function (k) {
		editor = k.create('textarea[name="content"]',{
			resizeType: 1,
			uploadJson : "<%=basePath%>normal/uploadimg!execute1.action",
			filePostName: "file",
			allowImageUpload: true,
			formatUploadUrl:false,
			afterupload : function (url){
				alert(url);
			}
		});
	});
	KindEditor.ready(function (k) {
		// 
		editor1 = k.create('textarea[name="content1"]',{
			resizeType: 1,
			uploadJson : "<%=basePath%>normal/uploadimg!execute1.action",
			filePostName: "file",
			allowImageUpload: true,
			formatUploadUrl:false,
			afterupload : function (url){
				alert(url);
			}
		});
	});

</script>


<%--  <%@ include file="include/top.jsp"%> --%>
<%--  <%@ include file="include/menu_left.jsp"%> --%>

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
			<li><a href="<%=bases%>/mall/goods/toUpdate.action?lang=cn&goodsId=${goodsId}">中文</a></li>
			<li><a href="<%=bases%>/mall/goods/toUpdate.action?lang=en&goodsId=${goodsId}">英文</a></li>
			<li><a href="<%=bases%>/mall/goods/toUpdate.action?lang=tw&goodsId=${goodsId}">繁体中文</a></li>
		</ul>
		<h3>商品管理</h3>
		<%@ include file="include/alert.jsp"%>
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<form  action="<%=basePath%>mall/goods/list.action"
			   method="post" id="queryForm">
			<input type="hidden" name="pageNo" id="pageNo"/>
		</form>
		<!-- END queryForm -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<div class="row">
			<div class="col-md-12 col-lg-12">
				<div class="panel panel-default">

					<div class="panel-title">
						编辑商品
						<ul class="panel-tools">
							<li><a class="icon minimise-tool"><i
									class="fa fa-minus"></i></a></li>
							<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
						</ul>
					</div>

					<div class="panel-body">
						<form class="form-horizontal"
							  style="position: relative;"
							  action="<%=basePath%>mall/goods/update.action"
							  method="post" name="mainForm" id="mainForm">
							<%--                <div style="position: absolute;left:900px;top: 90px;padding: 40px 80px;border-radius: 5%;background: #B9F98E;color: #0A451D;">黑色标题属于公共部分</div>--%>
							<input type="hidden" name="id" id="id" value = "${goods.id}"/>
							<input type="hidden" name="imgUrl1" id="imgUrl1" value = "${goods.imgUrl1}"/>
							<input type="hidden" name="imgUrl2" id="imgUrl2" value = "${goods.imgUrl2}"/>
							<input type="hidden" name="imgUrl3" id="imgUrl3" value = "${goods.imgUrl3}"/>
							<input type="hidden" name="imgUrl4" id="imgUrl4" value = "${goods.imgUrl4}"/>
							<input type="hidden" name="imgUrl5" id="imgUrl5" value = "${goods.imgUrl5}"/>
							<input type="hidden" name="imgUrl6" id="imgUrl6" value = "${goods.imgUrl6}"/>
							<input type="hidden" name="imgUrl7" id="imgUrl7" value = "${goods.imgUrl7}"/>
							<input type="hidden" name="imgUrl8" id="imgUrl8" value = "${goods.imgUrl8}"/>
							<input type="hidden" name="imgUrl9" id="imgUrl9" value = "${goods.imgUrl9}"/>
							<input type="hidden" name="imgUrl10" id="imgUrl10" value = "${goods.imgUrl10}"/>
							<input type="hidden" name="goodsId" id="goodsId" value = "${goodsId}"/>
							<input type="hidden" name="lang" id="lang" value = "${lang}"/>
							<input type="hidden" name="goodsLanId" id="goodsLanId" value = "${goodsLan}"/>
							<input type="hidden" name="pageNo" id="pageNo" value = "${pageNo}"/>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label" style="color: red">产品名称</label>
								<div class="col-sm-3">
									<input id="name" name="name" class="form-control" value="${name}" placeholder="请输入产品名称"/>
								</div>
							</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品分类</label>
									<div class="col-sm-3 " style = width:800px;>
										<div class="input-group">

											<select id="categoryId" name="categoryId"
													class="form-control ">
												<c:forEach var = "item" items = "${categoryList}">
													<option value = "${item.key}"> ${item.value} </option>>
												</c:forEach>
											</select>

										</div>
									</div>
								</div>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">排序</label>
								<div class="col-sm-3">
									<input id="goodsSort" name="goodsSort" class="form-control" value="${goods.goodsSort}" placeholder="数字越小越靠前，数字可重复" oninput="value=value.replace(/[^\d]/g,'')"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label" style="color: red">单位</label>
								<div class="col-sm-3">
									<input id="unit" name="unit" class="form-control" value="${unit}" placeholder="请输入计量单位" />
								</div>
							</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品属性</label>
									<div class="col-sm-3 " style = width:800px;>
										<div class="input-group">

											<select id="attributeId" name="attributeId"
													class="form-control ">
												<option value="">无</option>
												<c:forEach var = "item" items = "${attributeList}">
													<option value="${item.key}" <c:if test="${attributeId == item.key}">selected="true"</c:if> >${item.value}</option>
												</c:forEach>
											</select>


										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">可否退款</label>
									<div class="col-sm-3 ">
										<div class="input-group">

											<select id="isRefund" name="isRefund"
													class="form-control ">
												<option value="0" <c:if test="${goods.isRefund == '0'}">selected="true"</c:if>>是</option>
												<option value="1" <c:if test="${goods.isRefund == '1'}">selected="true"</c:if>>否</option>
											</select>

										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">进货价格</label>
									<div class="col-sm-3">
										<input id="systemPrice" name="systemPrice" class="form-control" value="${goods.systemPrice}" placeholder="进货价格" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')"/>
									</div>
								</div>
								<div class="form-group" style="">
									<label class="col-sm-2 control-label form-label">封面图(*)</label>

									<div class="" style="display: flex;justify-content: start;">
										<div>
											<input type="file" id="fileName1" name="fileName1"  value="${fileName1}" onchange="upload1();"  style="position:absolute;opacity:0;">
											<label for="fileName1">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img1" src="<%=base%>/image/add.png"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName2" name="fileName2"  value="${fileName2}" onchange="upload2();"  style="position:absolute;opacity:0;">
											<label for="fileName2">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img2" src="<%=base%>/image/add.png"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName3" name="fileName3"  value="${fileName3}" onchange="upload3();"  style="position:absolute;opacity:0;">
											<label for="fileName3">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img3" src="<%=base%>/image/add.png"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName4" name="fileName"  value="${fileName4}" onchange="upload4();"  style="position:absolute;opacity:0;">
											<label for="fileName4">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img4" src="<%=base%>/image/add.png"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName5" name="fileName"  value="${fileName5}" onchange="upload5();"  style="position:absolute;opacity:0;">
											<label for="fileName5">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img5" src="<%=base%>/image/add.png"   /></div> 　　
												　                   　</label> 　　
										</div>

									</div>

									<%--                  <div style="width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</div>--%>
									<label class="col-sm-2 control-label form-label" style="margin-top: 5px; color: red"></label>
								</div>

								<div class="form-group" style="">
									<label class="col-sm-2 control-label form-label"></label>

									<div class="" style="display: flex;justify-content: start;">
										<div>
											<input type="file" id="fileName6" name="fileName6"  value="${fileName6}" onchange="upload6();"  style="position:absolute;opacity:0;">
											<label for="fileName6">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img6" src="<%=base%>/image/add.png"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName7" name="fileName7"  value="${fileName7}" onchange="upload7();"  style="position:absolute;opacity:0;">
											<label for="fileName7">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img7" src="<%=base%>/image/add.png"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName8" name="fileName"  value="${fileName8}" onchange="upload8();"  style="position:absolute;opacity:0;">
											<label for="fileName8">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img8" src="<%=base%>/image/add.png"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName9" name="fileName9"  value="${fileName9}" onchange="upload9();"  style="position:absolute;opacity:0;">
											<label for="fileName9">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img9" src="<%=base%>/image/add.png"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName10" name="fileName10"  value="${fileName10}" onchange="upload10();"  style="position:absolute;opacity:0;">
											<label for="fileName10">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img10" src="<%=base%>/image/add.png"   /></div> 　　
												　                   　</label> 　　
										</div>

									</div>

									<%--                  <div style="width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</div>--%>
									<label class="col-sm-2 control-label form-label" style="margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</label>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">运费设置</label>
									<div class="col-sm-3 ">
										<div class="input-group">

											<select id="freightType" name="freightType"
													class="form-control ">
												<option value="0" <c:if test="${goods.freightType == '0'}">selected="true"</c:if>>开启</option>
												<option value="1" <c:if test="${goods.freightType == '1'}">selected="true"</c:if>>关闭</option>
											</select>

										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">上下架</label>
									<div class="col-sm-3 ">
										<div class="input-group">

											<select id="isShelf" name="isShelf"
													class="form-control ">
												<option value="1" <c:if test="${goods.isShelf == '1'}">selected="true"</c:if>>上架</option>
												<option value="0" <c:if test="${goods.isShelf == '0'}">selected="true"</c:if>>下架</option>
											</select>

										</div>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">运输费用</label>
									<div class="col-sm-3">
										<input id="freightAmount" name="freightAmount" class="form-control" value="${goods.freightAmount}" placeholder="运输费" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')"/>
									</div>
								</div>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">预警数量</label>
								<div class="col-sm-3">
									<input id="remindNum" name="remindNum" class="form-control" value="${goods.remindNum}" placeholder="输入预警库存" oninput="value=value.replace(/[^\d]/g,'')"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">总库存</label>
								<div class="col-sm-3">
									<input id="lastAmount" name="lastAmount" class="form-control" value="${goods.lastAmount}" placeholder="输入总库存" oninput="value=value.replace(/[^\d]/g,'')"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">税收费用</label>
								<div class="col-sm-3">
									<input id="goodsTax" name="goodsTax" class="form-control" value="${goods.goodsTax}" placeholder="输入税收金额" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">外部商品链接</label>
								<div class="col-sm-3">
									<input id="link" name="link" class="form-control" value="${goods.link}" placeholder="输入外部商品链接" />
								</div>
							</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">最小购买数量</label>
									<div class="col-sm-3">
										<input id="buyMin" name="buyMin" class="form-control" value="${goods.buyMin}" placeholder="数字越小越靠前，数字可重复"   oninput="value=value.replace(/[^\d]/g,'');if(value<=0)value=1" />
									</div>
								</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label" style="color: red">商品介绍</label>
								<div class="col-sm-4 ">
									<textarea class="form-control" style="height: 480px;" rows="7" id="addeditor_id" name="content" placeholder="请输入内容...">${des}</textarea>
									<input type="hidden"  name="content" cols="" id="schmlnr" />
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label" style="color: red">图片介绍</label>
								<div class="col-sm-4 ">
									<textarea class="form-control" style="height: 480px;" rows="7" id="addeditor_id1" name="content1" placeholder="请输入内容...">${imgDes}</textarea>
									<input type="hidden"  name="content1" cols="" id="schmlnr1" />
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
	//  $('#modal_succeeded').modal("show");
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
			
			save();
			document.getElementById("mainForm").submit();
		});

	}
	//初始化执行一次
	setTimeout(function() {
		start();
	}, 100);

	function start(){
		
		var img1 = $("#imgUrl1").val();
		var show_img1 = document.getElementById('show_img1');
		show_img1.src=img1;

		var img2 = $("#imgUrl2").val();
		var show_img2 = document.getElementById('show_img2');
		show_img2.src=img2;

		var img3 = $("#imgUrl3").val();
		var show_img3 = document.getElementById('show_img3');
		show_img3.src=img3;

		var img4 = $("#imgUrl4").val();
		var show_img4 = document.getElementById('show_img4');
		show_img4.src=img4;

		var img5 = $("#imgUrl5").val();
		var show_img5 = document.getElementById('show_img5');
		show_img5.src=img5;

		var img6 = $("#imgUrl6").val();
		var show_img6 = document.getElementById('show_img6');
		show_img6.src=img6;

		var img7 = $("#imgUrl7").val();
		var show_img7 = document.getElementById('show_img7');
		show_img7.src=img7;

		var img8 = $("#imgUrl8").val();
		var show_img8 = document.getElementById('show_img8');
		show_img8.src=img8;

		var img9 = $("#imgUrl9").val();
		var show_img9 = document.getElementById('show_img9');
		show_img9.src=img9;

		var img10 = $("#imgUrl10").val();
		var show_img10 = document.getElementById('show_img10');
		show_img10.src=img10;
	}
	function upload1(){
		var fileReader = new FileReader();
		var formData = new FormData();
		var file = document.getElementById('fileName1').files[0];
		formData.append("file", file);
		formData.append("moduleName","goods");
		$.ajax({
			type: "POST",
			url: "<%=basePath%>normal/uploadimg!execute.action",
			data: formData,
			dataType: "json",
			contentType: false,
			processData: false,
			success : function(data) {
				
				console.log(data);
				$("#imgUrl1").val(data.data)
				var show_img = document.getElementById('show_img1');
				show_img.src=data.data;

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});

	}
	function upload2(){
		var fileReader = new FileReader();
		var formData = new FormData();
		var file = document.getElementById('fileName2').files[0];
		formData.append("file", file);
		formData.append("moduleName","goods");
		$.ajax({
			type: "POST",
			url: "<%=basePath%>normal/uploadimg!execute.action",
			data: formData,
			dataType: "json",
			contentType: false,
			processData: false,
			success : function(data) {
				
				console.log(data);
				$("#imgUrl2").val(data.data)
				var show_img = document.getElementById('show_img2');
				show_img2.src=data.data;

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});

	}
	function upload3(){
		var fileReader = new FileReader();
		var formData = new FormData();
		var file = document.getElementById('fileName3').files[0];
		formData.append("file", file);
		formData.append("moduleName","goods");
		$.ajax({
			type: "POST",
			url: "<%=basePath%>normal/uploadimg!execute.action",
			data: formData,
			dataType: "json",
			contentType: false,
			processData: false,
			success : function(data) {
				
				console.log(data);
				$("#imgUrl3").val(data.data)
				var show_img = document.getElementById('show_img3');
				show_img3.src=data.data;

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});

	}
	function upload4(){
		var fileReader = new FileReader();
		var formData = new FormData();
		var file = document.getElementById('fileName4').files[0];
		formData.append("file", file);
		formData.append("moduleName","goods");
		$.ajax({
			type: "POST",
			url: "<%=basePath%>normal/uploadimg!execute.action",
			data: formData,
			dataType: "json",
			contentType: false,
			processData: false,
			success : function(data) {
				
				console.log(data);
				$("#imgUrl4").val(data.data)
				var show_img = document.getElementById('show_img4');
				show_img4.src=data.data;

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});

	}
	function upload5(){
		var fileReader = new FileReader();
		var formData = new FormData();
		var file = document.getElementById('fileName5').files[0];
		formData.append("file", file);
		formData.append("moduleName","goods");
		$.ajax({
			type: "POST",
			url: "<%=basePath%>normal/uploadimg!execute.action",
			data: formData,
			dataType: "json",
			contentType: false,
			processData: false,
			success : function(data) {
				
				console.log(data);
				$("#imgUrl5").val(data.data)
				var show_img = document.getElementById('show_img5');
				show_img5.src=data.data;

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
	}
	function upload6(){
		var fileReader = new FileReader();
		var formData = new FormData();
		var file = document.getElementById('fileName6').files[0];
		formData.append("file", file);
		formData.append("moduleName","goods");
		$.ajax({
			type: "POST",
			url: "<%=basePath%>normal/uploadimg!execute.action",
			data: formData,
			dataType: "json",
			contentType: false,
			processData: false,
			success : function(data) {
				console.log(data);
				$("#imgUrl6").val(data.data)
				var show_img = document.getElementById('show_img6');
				show_img6.src=data.data;

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
	}
	function upload7(){
		var fileReader = new FileReader();
		var formData = new FormData();
		var file = document.getElementById('fileName7').files[0];
		formData.append("file", file);
		formData.append("moduleName","goods");
		$.ajax({
			type: "POST",
			url: "<%=basePath%>normal/uploadimg!execute.action",
			data: formData,
			dataType: "json",
			contentType: false,
			processData: false,
			success : function(data) {
				console.log(data);
				$("#imgUrl7").val(data.data)
				var show_img = document.getElementById('show_img7');
				show_img7.src=data.data;

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
	}
	function upload8(){
		var fileReader = new FileReader();
		var formData = new FormData();
		var file = document.getElementById('fileName8').files[0];
		formData.append("file", file);
		formData.append("moduleName","goods");
		$.ajax({
			type: "POST",
			url: "<%=basePath%>normal/uploadimg!execute.action",
			data: formData,
			dataType: "json",
			contentType: false,
			processData: false,
			success : function(data) {
				console.log(data);
				$("#imgUrl8").val(data.data)
				var show_img = document.getElementById('show_img8');
				show_img8.src=data.data;

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
	}
	function upload9(){
		var fileReader = new FileReader();
		var formData = new FormData();
		var file = document.getElementById('fileName9').files[0];
		formData.append("file", file);
		formData.append("moduleName","goods");
		$.ajax({
			type: "POST",
			url: "<%=basePath%>normal/uploadimg!execute.action",
			data: formData,
			dataType: "json",
			contentType: false,
			processData: false,
			success : function(data) {
				console.log(data);
				$("#imgUrl9").val(data.data)
				var show_img = document.getElementById('show_img9');
				show_img9.src=data.data;

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
	}
	function upload10(){
		var fileReader = new FileReader();
		var formData = new FormData();
		var file = document.getElementById('fileName10').files[0];
		formData.append("file", file);
		formData.append("moduleName","goods");
		$.ajax({
			type: "POST",
			url: "<%=basePath%>normal/uploadimg!execute.action",
			data: formData,
			dataType: "json",
			contentType: false,
			processData: false,
			success : function(data) {
				console.log(data);
				$("#imgUrl10").val(data.data)
				var show_img = document.getElementById('show_img10');
				show_img10.src=data.data;

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
	}

	$(function(){
		$('.nav-tabs a').filter(function() {
			var a = document.URL;
			<%--var a = "<%=basePath%>mall/goods/toUpdate.action?lang=${lang}&goodsId=${goodsId}";--%>
			return this.href == "<%=bases%>/mall/goods/toUpdate.action?lang=${lang}&goodsId=${goodsId}";  //获取当前页面的地址
		}).closest('li').addClass('active');  //给当前最靠近的li（其实是现在进入的li）添加‘active’样式

	})

	function save(){
		var html;
		var html1;
		editor.sync();
		editor1.sync();
		html = document.getElementById('addeditor_id').value ;
		html1 = document.getElementById('addeditor_id1').value ;
		$("#schmlnr"). val(html);
		$("#schmlnr1"). val(html1);
		return true
	}


</script>




</body>
</html>