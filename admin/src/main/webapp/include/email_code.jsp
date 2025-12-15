<%@ page language="java" pageEncoding="utf-8"%>

<%
String path = request.getContextPath();
String basePath = "https://"+request.getServerName()+":443"+path+"/";
    String base = "http://" + request.getServerName() + ":"+request.getServerPort()+"/";
//    String username = SecurityAppUserHolder.gettUsername();
%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title></title>
		<style>
			*, *:before, *:after {
			    -webkit-box-sizing: border-box;
			    -moz-box-sizing: border-box;
			    box-sizing: border-box;
			}
			a {text-decoration: none;color: #58666e;}
			.title-yz{font-size: 28px;font-weight: 600;text-align: center;margin-top: 60px;}
			.form-yz{width: 80%;max-width: 600px;margin: 0 auto;padding-top: 80px;}
			.form-yz .form-group {margin-bottom: 18px;}
			.form-yz .col-sm-2{display: inline-block;width: 20%;}
			.form-yz .col-sm-3{display: inline-block;width: 30%;text-align: right;font-size: 12px;}
			.form-yz .col-sm-4{display: inline-block;width: 40%;margin: 0 4%;}
			.form-yz input{
				width: 100%;
				height: 34px;
				border-radius: 3px;
				padding-left: 10px;
				font-size: 14px;
				background: #fff;
				border: 1px solid #BDC4C9;
				box-shadow: inset 0px 1px 0px #F1F0F1;
				outline: none;
			}
			.btn-confirm {
				text-align: center;
				width: 200px;
				height: 34px;
				line-height: 34px;
				margin: 60px auto;
				font-size: 14px;
				border: none;
				background: #e4e4e4;
				border-radius: 3px;
				background-color: #399bff;
				color: #fff;
			}
			.btn-light {
				display: inline-block;
				text-align: center;
				width: 100px;
				background-color: #fff;
				border: 1px solid #BDC4C9;
				font-size: 14px;
				padding: 5px 0;
				border-radius: 3px;
			}
			.btn-disabled{
		        background-color: #cccccc!important;
		        color: #999999;
		        pointer-events:none;
		     }
		     .word-yz{color: #f00;font-size: 12px;margin: 0 0 4px 35%;}
		</style>
	<script type="text/javascript" src="<%=basePath%>js/jquery.min.js"></script>
	</head>
	<body>
		<div>
			<div class="title-yz">邮箱验证</div>
			<form class="form-yz" action="<%=basePath%>normal/adminEmailCodeAction!checkCode.action"
			 method="post" name="mainForm" id="mainForm">
			 
				<div class="word-yz">此次操作ip与上次登录ip不相符</div>
				<div class="form-group">
					<label for="input002" class="col-sm-3 control-label form-label">登录人账户</label>
					<div class="col-sm-4 btn-disabled">
						<input id="username" type="text" name="username" class="btn-disabled" value="${username}" disabled>
					</div>
				</div>
				
				<div class="form-group" style="">

					<label for="input002" class="col-sm-3 control-label form-label">验证码</label>
					<div class="col-sm-4">
						<input id="email_code" type="text" name="email_code" class="login_safeword" placeholder="请输入验证码">
					</div>
					<div class="col-sm-2">
						<a id="email_code_button" href="javascript:sendCode();" class="btn btn-light" style="margin-bottom: 10px">获取验证码</a>
					</div>
				</div>
				
				<div class="btn-confirm" onClick="submit();" style="cursor:pointer;">确定</div>
				
			</form>
		</div>
	</body>
	<script type="text/javascript">
	function submit(){
		$("#mainForm").submit();
	}
	var setInt = null;//定时器
	
	clearInterval(setInt);
	function sendCode(){
		var url = "<%=basePath%>normal/adminEmailCodeAction!sendCode.action";
 		var data = {"code_context":"operaIpDiff","isSuper":false};
 		goAjaxUrl(url,data,function(tmp){
 			
//  			$("#email_code_button").attr("disabled","disabled");
//  			$("#email_code_button").css("pointer-events","none");
			$("#email_code_button").addClass("btn-disabled");
 			var timeout = 60;
 			setInt = setInterval(function(){
 				if(timeout<=0){
 					clearInterval(setInt);
 					timeout=60;
//  					$("#email_code_button").removeAttr("disabled");
 					$("#email_code_button").removeClass("btn-disabled");
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
			    	alert(tmp.message);
			    	/* swal({
						title : tmp.message,
						text : "",
						type : "warning",
						showCancelButton : true,
						confirmButtonColor : "#DD6B55",
						confirmButtonText : "确认",
						closeOnConfirm : false
					}); */
			    }
			  },
				error : function(XMLHttpRequest, textStatus,
						errorThrown) {
					alert("请检查管理员邮箱是否配置");
					/* swal({
						title : "请求错误",
						text : "请检查管理员邮箱是否配置",
						type : "warning",
						showCancelButton : true,
						confirmButtonColor : "#DD6B55",
						confirmButtonText : "确认",
						closeOnConfirm : false
					});
					console.log("请求错误"); */
				}
		});
	}
	</script>
</html>
