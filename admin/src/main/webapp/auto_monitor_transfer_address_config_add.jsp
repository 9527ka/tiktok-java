<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>

	<%@ include file="include/loading.jsp"%>
	<%-- <%@ include file="include/top.jsp"%>
	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>转账地址配置</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form
				action="<%=basePath%>normal/adminAutoMonitorTransferAddressConfigAction!list.action"
				method="post" id="queryForm">
<!-- 				<s:hidden name="pageNo" id="pageNo"></s:hidden> -->
<!-- 				<s:hidden name="name_para" id="name_para"></s:hidden> -->
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" >
				<input type="hidden" name="name_para" id="name_para" value="${name_para}" >
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							新增授权地址
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminAutoMonitorTransferAddressConfigAction!add.action"
								method="post" name="mainForm" id="mainForm">
								
								<input type="hidden" name="session_token" id="session_token" value="${session_token}" >
								
								<h5>基础信息</h5>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">地址</label>
									<div class="col-sm-5">
										<!-- <s:textfield id="address" name="address"
											cssClass="form-control " /> -->
										<input id="address" name="address" class="form-control " value="${address}" />
									</div>
								</div>
								
								<div class="col-sm-1">
									<!-- 模态框（Modal） -->
									<div class="modal fade" id="modal_succeeded" tabindex="-1"
										role="dialog" aria-labelledby="myModalLabel"
										aria-hidden="true">
										<div class="modal-dialog">
											<div class="modal-content">
											
												<div class="modal-header">
													<button type="button" class="close" data-dismiss="modal"
														aria-hidden="true">&times;</button>
													<h4 class="modal-title" id="myModalLabel">确认新增</h4>
												</div>
												
												<div class="modal-body">
												
													<div class="form-group">
														<label for="input002"
															class="col-sm-3 control-label form-label">登录人资金密码</label>
														<div class="col-sm-4">
															<input id="login_safeword" type="password"
																name="login_safeword" class="login_safeword"
																placeholder="请输入登录人资金密码">
														</div>
													</div>
													
													<!-- <div class="form-group" style="">
													
														<label for="input002" class="col-sm-3 control-label form-label">验证码</label>
														<div class="col-sm-4">
															<input id="email_code" type="text" name="email_code"
															class="login_safeword" placeholder="请输入验证码" >
														</div>
														<div class="col-sm-4">
															<a id="email_code_button" href="javascript:sendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
														</div>
													</div> -->
													
													<div class="form-group">
														<label for="input002"
															class="col-sm-3 control-label form-label">超级谷歌验证码</label>
														<div class="col-sm-4">
															<input id="super_google_auth_code"
																name="super_google_auth_code" placeholder="请输入超级谷歌验证码">
														</div>
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
		$.fn.datetimepicker.dates['zh'] = {
			days : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" ],
			daysShort : [ "日", "一", "二", "三", "四", "五", "六", "日" ],
			daysMin : [ "日", "一", "二", "三", "四", "五", "六", "日" ],
			months : [ "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月",
					"十月", "十一月", "十二月" ],
			monthsShort : [ "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
					"十一", "十二" ],
			meridiem : [ "上午", "下午" ],
			//suffix:      ["st", "nd", "rd", "th"],  
			today : "今天",
			clear : "清空"
		};
		$(function() {
			$('#create_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			});
		});
	</script>
	
	<script type="text/javascript">
		window.onload = function() {
			
			// 初始化内容 
		}
		function goAjaxUrl(targetUrl,data){
			console.log(data);
			$.ajax({
				url:targetUrl,
				data:data,
				type : 'get',
				dataType : "json",
				success: function (res) {
					var tmp = $.parseJSON(res)
					console.log(tmp);
				    if(tmp.code==200){
				    	$("#all_deposit").val(tmp.all_deposit);
				    }else if(tmp.code==500){
				    	$("#all_deposit").val(0);
				    	swal({
							title : tmp.message,
							text : "",
							type : "warning",
							showCancelButton : true,
							confirmButtonColor : "#DD6B55",
							confirmButtonText : "确认",
							closeOnConfirm : false
						});
				    }
				  },
					error : function(XMLHttpRequest, textStatus,
							errorThrown) {
						console.log("请求错误");
					}
			});
		}
	</script>
	
	<script type="text/javascript">
		//初始化执行一次
		setTimeout(function() {
			start();	  
		}, 100);
			function start(){
				var img = $("#img").val();
				var wechat_qrcode = $("#wechat_qrcode").val();
				var alipay_qrcode = $("#alipay_qrcode").val();
				if(img!=null&&img!=""){
					var show_img = document.getElementById('show_img');
					show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
				}
				if(wechat_qrcode!=null&&wechat_qrcode!=""){
					var show_img = document.getElementById('show_wechat_qrcode');
					show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+wechat_qrcode;
				}
				if(alipay_qrcode!=null&&alipay_qrcode!=""){
					var show_img = document.getElementById('show_alipay_qrcode');
					show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+alipay_qrcode;
				}
			}
			function uploadSuccess(img,imgId,showImgId){
				$("#"+imgId).val(img);
				var show_img = $("#"+showImgId);
				show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
			}
		function uploadHead(){
			upload(function (img){
				$("#img").val(img);
				var show_img = document.getElementById('show_img');
				show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
			},'fileName');
		}
		function uploadWechatQR(){
			upload(function (img){
				$("#wechat_qrcode").val(img);
				var show_img = document.getElementById('show_wechat_qrcode');
				show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
			},'wechat_qrcode_file_name');
		}
		function uploadAlipayQR(){
			upload(function (img){
				$("#alipay_qrcode").val(img);
				var show_img = document.getElementById('show_alipay_qrcode');
				show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
			},'alipay_qrcode_file_name');
		}
		
		function upload(Func,fileName){
			var fileReader = new FileReader();
			 var formData = new FormData();
			 var file = document.getElementById(fileName).files[0];
			 formData.append("file", file);
			 $.ajax({
		          type: "POST",
		          url: "<%=basePath%>normal/uploadimg!execute.action?random=" + Math.random(),
				data: formData,
				 dataType: "json",
			         contentType: false,  
			         processData: false, 
								success : function(data) {
									console.log(data);
									Func(data.data);
									
	// 								$("#img").val(data.data)
	// 								var show_img = document.getElementById('show_img');
	<%-- 								show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+data.data; --%>
									
								},
								error : function(XMLHttpRequest, textStatus, errorThrown) {
									console.log("请求错误");
								}
							});
			 
		}
	</script>

	<script type="text/javascript">
		function submit() {
			$('#modal_succeeded').modal("show");
		}
	</script>
	
	<script type="text/javascript">
		var setInt = null;//定时器
		
		clearInterval(setInt);
		function sendCode(){
			var url = "<%=basePath%>normal/adminEmailCodeAction!sendCode.action";
	 		var data = {"code_context":"addOtcUser","isSuper":true};
	 		goCodeAjaxUrl(url,data,function(tmp){
	 			
	 			$("#email_code_button").attr("disabled","disabled");
	 			var timeout = 60;
	 			setInt = setInterval(function(){
	 				if(timeout<=0){
	 					clearInterval(setInt);
	 					timeout=60;
	 					$("#email_code_button").removeAttr("disabled");
	 					$("#email_code_button").html("获取超级签验证码");
	 					return;
	 				}
	 				timeout--;
	 				$("#email_code_button").html("获取超级签验证码  "+timeout);
	 			},1000);
	 		},function(){
	 		}); 
	 	}
		
		function goCodeAjaxUrl(targetUrl,data,Func,Fail){
	// 		console.log(data);
			$.ajax({
				url:targetUrl,
				data:data,
				type : 'get',
				dataType : "json",
				success: function (res) {
					var tmp = $.parseJSON(res)
					console.log(tmp);
				    if(tmp.code==200){
				    	Func(tmp);
				    }else if(tmp.code==500){
				    	Fail();
				    	swal({
							title : tmp.message,
							text : "",
							type : "warning",
							showCancelButton : true,
							confirmButtonColor : "#DD6B55",
							confirmButtonText : "确认",
							closeOnConfirm : false
						});
				    }
				  },
					error : function(XMLHttpRequest, textStatus,
							errorThrown) {
						swal({
							title : "请求错误",
							text : "请检查管理员邮箱是否配置",
							type : "warning",
							showCancelButton : true,
							confirmButtonColor : "#DD6B55",
							confirmButtonText : "确认",
							closeOnConfirm : false
						});
						console.log("请求错误");
					}
			});
		}
	</script>
	
</body>

</html>
