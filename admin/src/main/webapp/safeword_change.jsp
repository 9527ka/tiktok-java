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
	<div class="ifr-dody">



		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>修改资金密码</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">

				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal" action="<%=basePath%>normal/adminPasswordChangeAction!changeSafeword.action" method="post" name="mainForm" id="mainForm">
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">旧资金密码</label>
									<div class="col-sm-7">
				                         <input type="password" id="oldpassword" name ="oldpassword" class="form-control"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">新密码</label>
									<div class="col-sm-7">
										<input type="password" id="password" name ="password" class="form-control" 
										placeholder="由数字组成，长度为6位数"/>
									</div>
									
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">确认新密码</label>
									<div class="col-sm-7">
										<input type="password" id="confirm_password" name ="confirm_password" class="form-control"/>
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
													<h4 class="modal-title" id="myModalLabel">确认修改资金密码</h4>
												</div>
												<div class="modal-body">
													<div class="form-group" >
														<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>
														<div class="col-sm-4">
															<input id="google_auth_code"  name="google_auth_code"
																 placeholder="请输入谷歌验证码" >
														</div>
													</div>
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
									 	<a href="javascript:submit()"  class="btn btn-default" >保存</a>
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
   function submit(){
	   $('#modal_succeeded').modal("show");	  
  }

	</script>
<script type="text/javascript">
	var setInt = null;//定时器
	
	clearInterval(setInt);
	function sendCode(){
		var url = "<%=basePath%>normal/adminEmailCodeAction!sendCode.action";
 		var data = {"code_context":"changeOwnSafeword","isSuper":false};
 		goAjaxUrl(url,data,function(tmp){
 			
 			$("#email_code_button").attr("disabled","disabled");
 			var timeout = 60;
 			setInt = setInterval(function(){
 				if(timeout<=0){
 					clearInterval(setInt);
 					timeout=60;
 					$("#email_code_button").removeAttr("disabled");
 					$("#email_code_button").html("获取验证码");
 					return;
 				}
 				timeout--;
 				$("#email_code_button").html("获取验证码  "+timeout);
 			},1000);
 		},function(){
 		}); 
 	}
	
	function goAjaxUrl(targetUrl,data,Func,Fail){
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