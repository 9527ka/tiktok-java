<%@ page language="java" pageEncoding="utf-8"%>
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
			<h3>${fileName}</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>adminPlatform/list.action"
				method="post" id="queryForm">
				<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}" />
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							${fileName}
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>adminPlatform/addOrModify.action"
								method="post" name="mainForm" id="mainForm">
								<input type="hidden" name="id" id="id" value="${id}" />
								<input type="hidden" name="fileName" id="fileName" value="${fileName}" />
								<input type="hidden" name="createTime" id="createTime" value="${createTime}" />
<!-- 								<h5>基础信息</h5> -->

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">名称</label>
									<div class="col-sm-4">
									<input id="name" name="name" class="form-control" value="${name}" />

									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">状态</label>
									<div class="col-sm-4">
										<select id="status" name="status" class="form-control " >
											<option value="0" <c:if test="${status == '0'}">selected="true"</c:if>>启用</option>
											<option value="1" <c:if test="${status == '1'}">selected="true"</c:if>>禁用</option>
										</select>
									</div>
								</div>
							<div class="col-sm-1">
									<!-- 模态框（Modal） -->
									<div class="modal fade" id="modal_succeeded" tabindex="-1"
										role="dialog" aria-labelledby="myModalLabel"
										aria-hidden="true">
										<div class="modal-dialog">
											<div class="modal-content" style="width: 350px;">
											
												<div class="modal-header">
													<button type="button" class="close" data-dismiss="modal"
														aria-hidden="true">&times;</button>
													<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
												</div>
												
												<div class="modal-body">
													<div class="">
														<input id="safeword" type="password" name="login_safeword"
															class="login_safeword" placeholder="请输入登录人资金密码"
															style="width: 250px;">
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
								
								
								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<a href="javascript:goUrl(${pageNo})"
											class="btn">取消</a>
										<a href="javascript:submit()"
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
			var img = $("#img").val();
			var show_img = document.getElementById('show_img');
			show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
		}
		
		
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
									$("#img").val(data.data)
									var show_img = document.getElementById('show_img');
									show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+data.data;
									
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