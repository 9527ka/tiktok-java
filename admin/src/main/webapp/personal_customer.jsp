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
	<div class="ifr-dody">
		<div class="ifr-con">
			<h3>客服个人中心</h3>
			<%@ include file="include/alert.jsp"%>
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

						<div class="panel-body form-horizontal">
<!-- 							<form class="form-horizontal" action="" method="post" name="mainForm" id="mainForm"> -->
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">用户名</label>
									<div class="col-sm-4">
										<input id="username" name="username" class="form-control" 
										readOnly="readOnly" value = "${username}"/>
										
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">当前在线状态:</label>
									<div class="col-sm-4">
									
									<c:choose>
										<c:when test="${online_state==1}">
											<input id="online_state" name="online_state"  value="上线" 
											class="form-control"  readOnly="readOnly"/>
										</c:when>
										<c:otherwise>
											<input id="online_state" name="online_state"  value="下线" 
											class="form-control" readOnly="readOnly"/>
										</c:otherwise>
									</c:choose>
									</div>
									
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">最后上线时间</label>
									<div class="col-sm-4">
                                         <input id="last_online_time" name="last_online_time"
											class="form-control" readOnly="readOnly"  value = "${last_online_time}"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">最后下线时间</label>
									<div class="col-sm-4">
										
										<input id="last_offline_time" name="last_offline_time"
										class="form-control" readOnly="readOnly"  value = "${last_offline_time}"/>
											
									</div>
								</div>
								<form class="form-horizontal" action="<%=basePath%>normal/adminPersonalCustomerAction!personalUpdateAutoAnswer.action" method="post" name="mainForm" id="mainForm">
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">首次访问自动回复</label>
									<div class="col-sm-4">
										<input id="auto_answer" name="auto_answer" class="form-control" value = "${auto_answer}"/>
									</div>
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
	
													</div>
													<div class="modal-footer" style="margin-top: 0;">
														<button type="button" class="btn "
															data-dismiss="modal">关闭</button>
														<button id="sub" type="submit"
															class="btn btn-default downLineBtn">确认</button>
													</div>
												</div>
												<!-- /.modal-content -->
											</div>
											<!-- /.modal -->
										</div>
									<div class="col-sm-2">
									<a href="javascript:updateAuto();" class="btn-light btn btn-block">保存修改</a>
									</div>
								</div>
								</form>
								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
									<c:choose>
										<c:when test="${online_state==1}">
											<a href="javascript:offline()" class="btn">下线</a>					
										</c:when>
										<c:otherwise>
									 		<a id="online-chat" href="<%=basePath%>normal/adminPersonalCustomerAction!personalOnline.action"  class="btn btn-default" >上线</a>
									 		
										</c:otherwise>
									</c:choose>			
									</div>
								</div>

<!-- 							</form> -->

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

	$('#online-chat').on("click", function(){
		window.parent.location.reload();
	})

   function updateAuto(){
	   $('#modal_succeeded').modal("show");
  }

	</script>
<script type="text/javascript">
// 	onlineTip();
// 	function onlineTip(){
// 		if("${off_to_online}"=="true"){
// 			var data = {"time_stamp":null,"model":"OP_ADMIN_ONLINECHAT"};
<%-- 			goNewTipsAjaxUrl('<%=basePath%>normal/adminTipAction!getNewTips.action', data); --%>
// 		}
// 	}


	function offline(){
		swal({
			title : "是否下线?",
			text : "下线后将不会收到消息，如有新消息，用户将分配给其他在线客服",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
//				document.getElementById("mainForm").submit();
			location.href="<%=basePath%>normal/adminPersonalCustomerAction!personalOffline.action";
			window.parent.location.reload();
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