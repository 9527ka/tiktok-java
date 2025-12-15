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
		<h3>首页轮播</h3>
		<%@ include file="include/alert.jsp"%>
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<form  action="<%=basePath%>mall/banner/list.action?type=${type}"
			   method="post" id="queryForm">
			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
			<input type="hidden" name="type" id="type" value="${type}"/>
		</form>
		<!-- END queryForm -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<div class="row">
			<div class="col-md-12 col-lg-12">
				<div class="panel panel-default">

					<div class="panel-title">
						<c:choose>
							<c:when test="${type == 'pc'}">
								修改PC轮播
							</c:when>
							<c:otherwise>
								修改H5轮播
							</c:otherwise>
						</c:choose>
						<ul class="panel-tools">
							<li><a class="icon minimise-tool"><i
									class="fa fa-minus"></i></a></li>
							<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
						</ul>
					</div>

					<div class="panel-body">
						<form class="form-horizontal"
							  action="<%=basePath%>mall/banner/update.action"
							  method="post" name="mainForm" id="mainForm">
							<input type="hidden" name="type" id="type" value = "${type}"/>
							<input type="hidden" name="imgUrl" id="imgUrl" value = "${banner.imgUrl}"/>
							<input type="hidden" name="pageNo" id="pageNo" value = "${pageNo}"/>
							<input type="hidden" name="id" id="id" value = "${banner.id}"/>
<%--							<input type="hidden" name="id" id="id" value = "${id}"/>--%>



							<div class="form-group">
								<label class="col-sm-2 control-label form-label">封面图(*)</label>

								<div class="col-sm-3" style="display: flex;">
									<input type="file" id="fileName" name="fileName"  value="${fileName}" onchange="upload();"  style="position:absolute;opacity:0;">
									<label for="fileName">　　
										　　　　　　
										　　　　									<img width="90px" height="90px" id="show_img"

																					 src="<%=base%>/image/add.png"  alt="点击上传图片" /> 　　
										　　
										　										　</label>
									<c:if test="${type == 'h5'}">
										<div style="float: left;width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px * 340px ）</div>　　
									</c:if>

								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">跳转地址</label>
								<div class="col-sm-3">
									<input id="link" name="link" class="form-control" value="${banner.link}" placeholder="请输入跳转地址"/>
								</div>
							</div>


							<div class="form-group">
								<label class="col-sm-2 control-label form-label">排序</label>
								<div class="col-sm-3">
									<input id="sort" name="sort" class="form-control" value="${banner.sort}" placeholder="请输入排序" oninput="value=value.replace(/[^\d]/g,'')"/>
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">备注</label>
								<div class="col-sm-3">
									<input id="remarks" name="remarks" class="form-control" value="${banner.remarks}" placeholder="请输入备注"/>
								</div>
							</div>

							<c:if test="${type == 'pc'}">
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">分类</label>
									<div class="col-sm-3 ">
										<div class="input-group">

											<select id="imgType" name="imgType"
													class="form-control ">
												<option value="1">大图（700*310）</option>
												<option value="0">小图（242*152）</option>
											</select>

										</div>
									</div>
								</div>
							</c:if>



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
		let imgUrl = $("#imgUrl").val();
		let sort = $("#sort").val();
		if(imgUrl == ""){
			swal({
				title: "请选择banner图片!",
				timer: 1500,
				showConfirmButton: false
			})
			return false;
		}
		if(sort == ""){
			swal({
				title: "请填写序号!",
				timer: 1500,
				showConfirmButton: false
			})
			return false;
		}
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

	setTimeout(function() {
		start();
	}, 100);

	function start(){
		var img = $("#imgUrl").val();
		var show_img = document.getElementById('show_img');
		show_img.src=img;
	}


	function upload(){
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
				$("#imgUrl").val(data.data)
				var show_img = document.getElementById('show_img');
				show_img.src=data.data;

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});

	}


</script>




</body>
</html>