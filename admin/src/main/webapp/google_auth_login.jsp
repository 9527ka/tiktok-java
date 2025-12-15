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
	
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>${username}谷歌验证器
			</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">
						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminGoogleAuthAction!googleLoginAuthBind.action"
								method="post" name="mainForm" id="mainForm">
								
								<c:choose>
									<c:when test="${!google_auth_bind}">
									<div class="form-group">
										<label class="col-sm-2 control-label form-label">密匙</label>
										<div class="col-sm-4">
											<input id="google_auth_secret" name="google_auth_secret" class="form-control" readOnly="readOnly"/>
											<input type="hidden" name="google_auth_url" id="google_auth_url"/>
										</div>
										<c:if test="${!google_auth_bind}">
										<div class="col-sm-1">
											<a href="javascript:picture();" class="btn btn-light" style="margin-bottom: 10px">生成</a>
										</div>
										</c:if>
									</div>
									<div class="form-group" style="display:none;" id="show_img">
										<label class="col-sm-2 control-label form-label"></label>
										<div class="col-sm-4" >
											<img width="90px" height="90px" id="load_img" style="float: left;"
								 			src="<%=basePath%>img/chat/loading.gif"  alt="加载中..." />
											<a href="#" target="_blank" ><img width="90px" height="90px" id="show_thumb" style="float: left;"
								 			src=""  alt="资讯图片" /> </a>　　
										</div>
									</div>
									
									<div class="form-group">
										<label class="col-sm-2 control-label form-label">谷歌验证码</label>
										<div class="col-sm-4">
												
											<input id="google_auth_code" name="google_auth_code" class="form-control" placeholder="谷歌验证码"/>
										</div>
									</div>
									</c:when>
								   
									<c:otherwise>
									<label class="col-sm-2 control-label form-label"></label><p class="ballon color1">已绑定</p>
									</c:otherwise>
								  
								</c:choose>

								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
											<c:if test="${!google_auth_bind}">
												<a href="javascript:bind()" class="btn btn-default">绑定</a>
											</c:if>
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
		$('#report_time').datetimepicker({
			format : 'yyyy-mm-dd hh:ii:00',
			minuteStep:1,
			language : 'zh',
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 2,
			clearBtn : true
//				minView : 2,
//				pickerPosition: "bottom-left"
		});
		init();
	});
	</script>
	<script type="text/javascript">
		function bind() {
			swal({
				title : "是否绑定?",
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
		function init(){
			if($("#google_auth_url").val()){
				
				$("#show_thumb").attr("src",$("#google_auth_url").val());
				$("#show_thumb").parents("a").attr("href",$("#google_auth_url").val());
				$("#load_img").hide();
				$("#show_thumb").show();
				$("#show_img").show();
			}
		}
		function picture(){
			$("#show_img").show();
			$("#show_thumb").hide();
			
// 			$("#show_thumb").attr("src",$("#thumb").val());
// 			$("#show_thumb").parents("a").attr("href",$("#thumb").val());
			getSecret();
			$("#show_thumb").load(function(){
				$("#load_img").hide();
				$("#show_thumb").show();
			});
			$("#show_thumb").error(function(){
				$("#show_thumb").attr("src","");
				$("#load_img").hide();
				$("#show_thumb").show();
			});
			$("#show_img").show();
		}
		
	</script>
<script type="text/javascript">
	
	function getSecret(){
		var url = "<%=basePath%>normal/adminGoogleAuthAction!getLoginSecret.action";
		// console.log(${username});
 		var data = {"username":"${username}"};
 		goAjaxUrl(url,data,function(tmp){
 			$("#google_auth_secret").val(tmp.google_auth_secret);
 			$("#google_auth_url").val(tmp.google_auth_url+"&v="+new Date().getTime());
 			$("#show_thumb").attr("src",tmp.google_auth_url+"&v="+new Date().getTime());
			$("#show_thumb").parents("a").attr("href",tmp.google_auth_url+"&v="+new Date().getTime());
			
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
				// var tmp = $.parseJSON(res)
				var tmp = res;
				// console.log(tmp);
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
						text : "",
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