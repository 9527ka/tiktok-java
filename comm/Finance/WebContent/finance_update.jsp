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
			<h3>理财产品配置</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminFinanceAction!list.action"
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
							修改理财产品
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminFinanceAction!update.action"
								method="post" name="mainForm" id="mainForm">
								<s:hidden name="id" id="id"></s:hidden>
								<s:hidden name="img" id="img"></s:hidden>
								
								<h5>基础信息</h5>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(简体中文)</label>
									<div class="col-sm-3">
										<s:textfield id="name" name="name" cssClass="form-control " />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(繁体中文)</label>
									<div class="col-sm-3">
										<s:textfield id="name_cn" name="name_cn" cssClass="form-control " />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(英文)</label>
									<div class="col-sm-3">
										<s:textfield id="name_en" name="name_en" cssClass="form-control " />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(韩语)</label>
									<div class="col-sm-3">
										<s:textfield id="name_kn" name="name_kn" cssClass="form-control " />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(日语)</label>
									<div class="col-sm-3">
										<s:textfield id="name_jn" name="name_jn" cssClass="form-control " />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品图片</label>
									
									<div class="col-sm-3">	
										<input type="file" id="fileName" name="fileName" onchange="upload();"  style="position:absolute;opacity:0;">	
										<label for="fileName">　　 
　　　　　　
　　　　									<img width="90px" height="90px" id="show_img"
												
							 			src="<%=base%>/image/add.png"  alt="点击上传图片" /> 　　
　　
　										　</label> 　　

									</div>	
								</div>
								<h5>交易信息</h5>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">状态</label>
									<div class="col-sm-3">
									<div class="input-group">
									<s:select id="state" cssClass="form-control "
											name="state" list="#{'0':'停用','1':'启用'}"
											listKey="key" listValue="value" value="state" />
									</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">周期</label>
									<div class="col-sm-3">
									<div class="input-group">
										<s:textfield id="cycle" name="cycle" cssClass="form-control " />
										 <div class="input-group-addon">天</div>
									</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">日利率</label>
									<div class="col-sm-3">
									<div class="input-group">
										<s:textfield id="daily_rate" name="daily_rate" cssClass="form-control " />
										
										<div class="input-group-addon">--</div>
										 <s:textfield id="daily_rate_max" name="daily_rate_max" cssClass="form-control " />
										 <div class="input-group-addon">%</div>
									</div>	
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">今日利率</label>
									<div class="col-sm-3">
									<div class="input-group">
										<s:textfield id="today_rate" name="today_rate" cssClass="form-control " />
										 <div class="input-group-addon">%</div>
									</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">违约结算比例</label>
									<div class="col-sm-3">
									<div class="input-group">
										<s:textfield id="default_ratio" name="default_ratio" cssClass="form-control " />
										 <div class="input-group-addon">%</div>
									</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">投资金额区间</label>
									<div class="col-sm-4">
										<div class="input-group">
											<s:textfield id="investment_min" name="investment_min" cssClass="form-control " />
										 	<div class="input-group-addon">--</div>
										 	<s:textfield id="investment_max" name="investment_max" cssClass="form-control " />
										 	<div class="input-group-addon">USDT</div>
										 
										</div>
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