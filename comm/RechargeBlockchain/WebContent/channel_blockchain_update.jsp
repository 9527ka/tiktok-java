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
			<h3>区块链地址管理</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminChannelBlockchainAction!list.action"
				method="post" id="queryForm">
				<s:hidden name="pageNo" id="pageNo"></s:hidden>
				<s:hidden name="name_para" id="name_para"></s:hidden>
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改区块链地址
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminChannelBlockchainAction!update.action"
								method="post" name="mainForm" id="mainForm">
								<s:hidden name="id" id="id"></s:hidden>
								<s:hidden name="img" id="img"></s:hidden>
								
								<h5>基础信息</h5>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">币种</label>
									<div class="col-sm-3">
										<s:textfield id="coin" name="coin" cssClass="form-control " />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">链名称</label>
									<div class="col-sm-3">
										<s:textfield id="blockchain_name" name="blockchain_name" cssClass="form-control " />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">地址图片</label>
									
									<div class="col-sm-3">	
										<input type="file" id="fileName" name="fileName" onchange="upload();"  style="position:absolute;opacity:0;">	
										<label for="fileName">　　 
　　　　　　
　　　　									<img width="90px" height="90px" id="show_img"
												
							 			src="<%=base%>admin/www/img/add.png"  alt="点击上传图片" /> 　　
　　
　										　</label> 　　

									</div>	
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">地址</label>
									<div class="col-sm-3">
										<s:textfield id="address" name="address" cssClass="form-control " />
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