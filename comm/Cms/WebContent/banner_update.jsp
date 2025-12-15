<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="include/head.jsp"%>
</head>
<body>
	<%@ include file="include/loading.jsp"%>
	<%@ include file="include/top.jsp"%>
	<%@ include file="include/menu_left.jsp"%>
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="content">



		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="container-default">
			<h3>横幅管理
			</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminBannerAction!list.action"
				method="post" id="queryForm">
				<input type="hidden" name="pageNo" id="pageNo"
					value="${param.pageNo}">
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改公告
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminBannerAction!update.action"
								method="post" name="mainForm" id="mainForm" enctype="multipart/form-data">
								  <s:hidden name="id" id="id"></s:hidden>
								  <s:hidden name="img" id="img"></s:hidden>
								  <sec:authorize ifAnyGranted="ROLE_ROOT">
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">业务代码</label>
									<div class="col-sm-4">
										<s:textfield id="content_code" name="content_code" cssClass="form-control "
											placeholder="业务代码(同种内容不同语言代码相同)" />
									</div>
								</div>
								</sec:authorize>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">图片</label>
									
									<div class="col-sm-3">	
										<input type="file" id="fileName" name="fileName" onchange="upload();"  style="position:absolute;opacity:0;">	
										<label for="fileName">　　 
　　　　　　
　　　　									<img width="150px" height="55px" id="show_img" style="float: left;"
												
							 			src="<%=base%>/image/add.png"  alt="点击上传图片" /> 　　
　　
　										　</label> 　　

									</div>	
								</div>
								<sec:authorize ifAnyGranted="ROLE_ROOT">
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">路径</label>
									<div class="col-sm-4">
										<s:textfield id="url" name="url" cssClass="form-control "
											placeholder="路径" />
									</div>
								</div>
								</sec:authorize>
								<p class="ballon color1">排序索引，数字越小越靠前</p>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">排序索引</label>
									<div class="col-sm-4">
										<s:textfield id="sort_index" name="sort_index" cssClass="form-control "
											placeholder="排序索引" />
									</div>
								</div>
								<sec:authorize ifAnyGranted="ROLE_ROOT">
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">模块</label>
									<div class="col-sm-2">
									<s:select id="model" cssClass="form-control "
											name="model" list="modelMap"
											listKey="key" listValue="value" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">语言</label>
									<div class="col-sm-2">
									<s:select id="language" cssClass="form-control "
											name="language" list="languageMap"
											listKey="key" listValue="value" />
									</div>
								</div>
								</sec:authorize>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">是否可点击</label>
									<div class="col-sm-3">
									<div class="input-group">
									<s:select id="click" cssClass="form-control "
											name="click" list="#{'0':'否','1':'是'}"
											listKey="key" listValue="value"  />
									</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">是否展示</label>
									<div class="col-sm-3">
									<div class="input-group">
									<s:select id="on_show" cssClass="form-control "
											name="on_show" list="#{'0':'否','1':'是'}"
											listKey="key" listValue="value"  />
									</div>
									</div>
								</div>

								
				                <div class="col-sm-1">
									<!-- 模态框（Modal） -->
									<div class="modal fade" id="modal_succeeded" tabindex="-1"
										role="dialog" aria-labelledby="myModalLabel"
										aria-hidden="true">
										<div class="modal-dialog">
											<div class="modal-content" >
												<div class="modal-header">
													<button type="button" class="close"
														data-dismiss="modal" aria-hidden="true">&times;</button>
													<h4 class="modal-title" id="myModalLabel">确认新增</h4>
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
															<button id="email_code_button" 
																	class="btn btn-light " onClick="sendCode();" >获取验证码</button>
															<a id="email_code_button" href="javascript:sendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
														</div>
													</div> 
													<div class="form-group" >
														<label for="input002" class="col-sm-3 control-label form-label">超级谷歌验证码</label>
														<div class="col-sm-4">
															<input id="super_google_auth_code"  name="super_google_auth_code"
																 placeholder="请输入超级谷歌验证码" >
														</div>
													</div>-->
												</div>
												<div class="modal-footer" style="margin-top: 0;">
													<button type="button" class="btn "
														data-dismiss="modal">关闭</button>
													<button id="sub" type="submit"
														class="btn btn-default" >确认</button>
												</div>
											</div>
											<!-- /.modal-content -->
										</div>
										<!-- /.modal -->
									</div>
								</div>



								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<a href="javascript:goUrl(<s:property value="pageNo" />)"
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
	<script src="<%=basePath%>js/util.js" type="text/javascript"></script>
	
	<script type="text/javascript">
		function submit() {
			/* swal({
				title : "是否保存?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				var sHTML = $('#content_text').val();
				$("#content").val(sHTML);
				document.getElementById("mainForm").submit();
			}); */
			$('#modal_succeeded').modal("show");
		}
	</script>
<script type="text/javascript">
	//初始化执行一次
	setTimeout(function() {
		start();	  
	}, 100);
		function start(){
			var img = $("#img").val();
			if(img!=null&&img!=""){
				var show_img = document.getElementById('show_img');
<%-- 				show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+img; --%>
				show_img.src=img;
			}
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
// 								$("#img").val(data.data)
// 								var show_img = document.getElementById('show_img');
<%-- 								show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+data.data; --%>
								
								$("#img").val("<%=base%>wap/public/showimg!showImg.action?imagePath="+data.data)
								var show_img = document.getElementById('show_img');
								show_img.src=$("#img").val();
								
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