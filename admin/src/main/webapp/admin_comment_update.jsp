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



<%--  <%@ include file="include/top.jsp"%> --%>
<%--  <%@ include file="include/menu_left.jsp"%> --%>

<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTENT -->


<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTAINER -->
<div class="ifr-con">
	<h3>评论库</h3>
	<%@ include file="include/alert.jsp"%>
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START queryForm -->
	<form  action="<%=basePath%>mall/comment/list.action"
		   method="post" id="queryForm">
		<input type="hidden" name="pageNo" id="pageNo"/>
	</form>
	<!-- END queryForm -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<div class="row">
		<div class="col-md-12 col-lg-12">
			<div class="panel panel-default">

				<div class="panel-title">
					评论详情
					<ul class="panel-tools">
						<li><a class="icon minimise-tool"><i
								class="fa fa-minus"></i></a></li>
						<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
					</ul>
				</div>

				<div class="panel-body">
					<form class="form-horizontal"
						  style="position: relative;"
						  action="<%=basePath%>mall/comment/add.action"
						  method="post" name="mainForm" id="mainForm">
						<%--                <div style="position: absolute;left:900px;top: 90px;padding: 40px 80px;border-radius: 5%;background: #B9F98E;color: #0A451D;">黑色标题属于公共部分</div>--%>
						<input type="hidden" name="id" id="id" value = "${comment.id}"/>
						<input type="hidden" name="imgUrl1" id="imgUrl1" value = "${comment.imgUrl1}"/>
						<input type="hidden" name="imgUrl2" id="imgUrl2" value = "${comment.imgUrl2}"/>
						<input type="hidden" name="imgUrl3" id="imgUrl3" value = "${comment.imgUrl3}"/>
						<input type="hidden" name="imgUrl4" id="imgUrl4" value = "${comment.imgUrl4}"/>
						<input type="hidden" name="imgUrl5" id="imgUrl5" value = "${comment.imgUrl5}"/>
						<input type="hidden" name="imgUrl6" id="imgUrl6" value = "${comment.imgUrl6}"/>
						<input type="hidden" name="imgUrl7" id="imgUrl7" value = "${comment.imgUrl7}"/>
						<input type="hidden" name="imgUrl8" id="imgUrl8" value = "${comment.imgUrl8}"/>
						<input type="hidden" name="imgUrl9" id="imgUrl9" value = "${comment.imgUrl9}"/>
						<input type="hidden" name="pageNo" id="pageNo" value = "${pageNo}"/>

						<div class="form-group" style="">
							<label class="col-sm-2 control-label form-label">评论图片(*)</label>

							<div class="" style="display: flex;justify-content: start;">
								<div>
									<input type="file" id="fileName1" name="fileName1"  value="${fileName1}" onchange="upload1();"  style="position:absolute;opacity:0;"  disabled>
									<label for="fileName1">　　
										　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img1" src="<%=base%>/image/add.png"   /></div> 　　
										　                   　</label> 　　
								</div>
								<div>
									<input type="file" id="fileName2" name="fileName2"  value="${fileName2}" onchange="upload2();"  style="position:absolute;opacity:0;"  disabled>
									<label for="fileName2">　　
										　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img2" src="<%=base%>/image/add.png"   /></div> 　　
										　                   　</label> 　　
								</div>
								<div>
									<input type="file" id="fileName3" name="fileName3"  value="${fileName3}" onchange="upload3();"  style="position:absolute;opacity:0;"  disabled>
									<label for="fileName3">　　
										　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img3" src="<%=base%>/image/add.png"   /></div> 　　
										　                   　</label> 　　
								</div>
								<div>
									<input type="file" id="fileName4" name="fileName"  value="${fileName4}" onchange="upload4();"  style="position:absolute;opacity:0;"  disabled>
									<label for="fileName4">　　
										　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img4" src="<%=base%>/image/add.png"   /></div> 　　
										　                   　</label> 　　
								</div>
								<div>
									<input type="file" id="fileName5" name="fileName"  value="${fileName5}" onchange="upload5();"  style="position:absolute;opacity:0;"  disabled>
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
									<input type="file" id="fileName6" name="fileName6"  value="${fileName6}" onchange="upload6();"  style="position:absolute;opacity:0;"  disabled>
									<label for="fileName6">　　
										　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img6" src="<%=base%>/image/add.png"   /></div> 　　
										　                   　</label> 　　
								</div>
								<div>
									<input type="file" id="fileName7" name="fileName7"  value="${fileName7}" onchange="upload7();"  style="position:absolute;opacity:0;"  disabled>
									<label for="fileName7">　　
										　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img7" src="<%=base%>/image/add.png"   /></div> 　　
										　                   　</label> 　　
								</div>
								<div>
									<input type="file" id="fileName8" name="fileName"  value="${fileName8}" onchange="upload8();"  style="position:absolute;opacity:0;"  disabled>
									<label for="fileName8">　　
										　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img8" src="<%=base%>/image/add.png"   /></div> 　　
										　                   　</label> 　　
								</div>
								<div>
									<input type="file" id="fileName9" name="fileName9"  value="${fileName9}" onchange="upload9();"  style="position:absolute;opacity:0;"  disabled>
									<label for="fileName9">　　
										　　　　                  <div class="avatar"><img width="90px" height="90px" id="show_img9" src="<%=base%>/image/add.png"   /></div> 　　
										　                   　</label> 　　
								</div>

							</div>

							<%--                  <div style="width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</div>--%>
							<%--									<label class="col-sm-2 control-label form-label" style="margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</label>--%>
						</div>

						<div class="form-group">
							<label class="col-sm-2 control-label form-label" style="color: red">评价内容</label>
							<div class="col-sm-4">
								<textarea class="form-control" rows="7" id="content_text" name="content_text" placeholder="请输入内容" readonly="true">${comment.content}</textarea>
								<input type="hidden" name="content" id="content" value="${comment.content}" readonly="true"/>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label form-label">会员评分</label>
							<div class="col-sm-3">
								<input id="score" readonly="true" name="score" class="form-control" value="${comment.score}"  placeholder="请输入1-5分" oninput="if(!/^[0-9]+$/.test(value)) value=value.replace(/\D/g,'');if(value>5)value=5;if(value<1)value=null"/>
							</div>
						</div>

						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<a href="javascript:goUrl(${pageNo})"
								   class="btn">返回</a>
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

	<%--$(function(){--%>
	<%--	$('.nav-tabs a').filter(function() {--%>
	<%--		var a = document.URL;--%>
	<%--		&lt;%&ndash;var a = "<%=basePath%>mall/goods/toUpdate.action?lang=${lang}&goodsId=${goodsId}";&ndash;%&gt;--%>
	<%--		return this.href == "<%=bases%>/mall/goods/toUpdate.action?lang=${lang}&goodsId=${goodsId}";  //获取当前页面的地址--%>
	<%--	}).closest('li').addClass('active');  //给当前最靠近的li（其实是现在进入的li）添加‘active’样式--%>

	<%--})--%>



</script>




</body>
</html>