<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="include/head.jsp"%>

	<script charset="UTF-8" src="<%=basePath%>js/kindeditor/kindeditor-all-min.js"></script>
	<script charset="UTF-8" src="<%=basePath%>js/kindeditor/lang/zh-CN.js"></script>
	<script type="text/javascript">
		var editor;
		KindEditor.ready(function (k) {
			editor = k.create('textarea[name="content"]',{
				resizeType: 1,
				uploadJson : "<%=basePath%>kindeditor/upload",
				filePostName: "file",
				allowImageUpload: true,
				formatUploadUrl:false,
				afterupload : function (url){
					alert(url);
				}
			});
		});

	</script>
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
		<h3>新闻管理</h3>
		<%@ include file="include/alert.jsp"%>
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<form  action="<%=basePath%>normal/adminNewsAction!list.action"
			   method="post" id="queryForm">
			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
		</form>
		<!-- END queryForm -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<div class="row">
			<div class="col-md-12 col-lg-12">
				<div class="panel panel-default">

					<div class="panel-title">
						添加新闻
						<ul class="panel-tools">
							<li><a class="icon minimise-tool"><i
									class="fa fa-minus"></i></a></li>
							<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
						</ul>
					</div>

					<div class="panel-body">
						<form class="form-horizontal"
							  action="<%=basePath%>normal/adminNewsAction!add.action"
							  method="post" name="mainForm" id="mainForm">
							<input type="hidden" name="iconImg" id="iconImg" value = "${iconImg}"/>
							<input type="hidden" name="pageNo" id="pageNo" value = "${pageNo}"/>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">标题</label>
								<div class="col-sm-3">
									<input id="title" name="title" class="form-control" value="${title}"/>
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">新闻状态</label>
								<div class="col-sm-3 ">
									<div class="input-group">

										<select id="status" name="status"
												class="form-control ">
											<option value="0" >禁用</option>
											<option value="1">启用</option>
										</select>

									</div>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">选择语言</label>
								<div class="col-sm-3 ">
									<div class="input-group">

										<select id="lang" name="lang"
												class="form-control ">
											<option value="cn">中文</option>
											<option value="en">英文</option>
											<option value="tw">繁体中文</option>
										</select>

									</div>
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">封面图(*)</label>

								<div class="col-sm-3">
									<input type="file" id="fileName" name="fileName"  value="${fileName}" onchange="upload();"  style="position:absolute;opacity:0;">
									<label for="fileName">　　
										　　　　　　
										　　　　									<img width="90px" height="90px" id="show_img"

																					 src="<%=base%>/image/add.png"  alt="点击上传图片" /> 　　
										　　
										　										　</label> 　　

								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">显示排序</label>
								<div class="col-sm-3">
									<input id="sort" name="sort" class="form-control" value="${sort}" />
								</div>
							</div>


							<div class="form-group">
								<label class="col-sm-2 control-label form-label">新闻发布时间</label>
								<div class="col-sm-3">
									<input id="releaseTime" name="releaseTime" class="form-control" value="${releaseTime}"/>
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">新闻内容</label>
								<div class="col-sm-4 ">
									<textarea class="form-control" style="height: 480px;" rows="7" id="addeditor_id" name="content" placeholder="请输入内容...">${content}</textarea>
									<input type="hidden"  name="content" cols="" id="schmlnr" />
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
			save()
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
		show_img.src="<%=basePath%>normal/showImg.action?imagePath="+img;
	}

	$(function() {
		$('#releaseTime').datetimepicker({
			format : 'yyyy-mm-dd hh:ii:00',
			minuteStep:1,
			language : 'zh',
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 2,
			clearBtn : true
		});

	});

	function upload(){
		var fileReader = new FileReader();
		var formData = new FormData();
		var file = document.getElementById('fileName').files[0];
		formData.append("file", file);
		$.ajax({
			type: "POST",
			url: "<%=basePath%>normal/uploadimg!execute.action?random="
					+ Math.random(),
			data: formData,
			dataType: "json",
			contentType: false,
			processData: false,
			success : function(data) {
				console.log(data);
				$("#iconImg").val(data.data)
				var show_img = document.getElementById('show_img');
				show_img.src="<%=basePath%>normal/showImg.action?imagePath="+data.data;

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});

	}
	function save(){
		var html;
		editor.sync();
		html = document.getElementById('addeditor_id').value ;
		$("#schmlnr"). val(html);
		return true
	}

</script>




</body>
</html>