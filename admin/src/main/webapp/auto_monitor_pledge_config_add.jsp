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

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>全局质押配置</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminPledgeConfigAction!list.action"
				method="post" id="queryForm">

				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" >
				<input type="hidden" name="name_para" id="name_para" value="${name_para}" >
				
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							新增 全局质押配置
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminPledgeConfigAction!add.action"
								method="post" name="mainForm" id="mainForm">
								
								<!-- <s:hidden name="id" id="id"></s:hidden>
								<s:hidden name="title_img" id="title_img"></s:hidden>
								<s:hidden name="content_img" id="content_img"></s:hidden> -->
								<input type="hidden" name="id" id="id" value="${id}">
								<input type="hidden" name="title_img" id="title_img" value="${title_img}">
								<input type="hidden" name="content_img" id="content_img" value="${content_img}">
								
								<h5>基础信息</h5>
								<p class="ballon color1">
									代理商(UID) <br /> 全局只设置代理商,表示代理线下所有用户质押配置。 <br /> 优先级为个人>代理>全局
								</p>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">代理商UID(*)</label>
									<div class="col-sm-3">
										<!-- <s:textfield id="usercode" name="usercode"
											cssClass="form-control " /> -->
										<input id="usercode" name="usercode" class="form-control " value="${usercode}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">限制天数(*)</label>
									<div class="col-sm-3">
										<!-- <s:textfield id="limit_days" name="limit_days"
											cssClass="form-control " /> -->
										<input id="limit_days" name="limit_days" class="form-control " value="${limit_days}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">标题文本(*)</label>
									<div class="col-sm-3">
										<!-- <s:textfield id="title" name="title" cssClass="form-control " /> -->
										<input id="title" name="title" class="form-control " value="${title}" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">标题图片(*)</label>
									<div class="col-sm-3">
										<input type="file" id="fileName" name="fileName" value="${fileName}"
											onchange="upload();" style="position: absolute; opacity: 0;">
										<label for="fileName"> <img width="90px" height="90px"
											id="show_img" src="<%=base%>/image/add.png" alt="点击上传图片" />
										</label>
									</div>
								</div>

								<div class="form-group">
									<label for="input002" class="col-sm-2 control-label form-label">内容文本</label>
									<div class="col-sm-5">
										<!-- <s:textarea name="content" id="content"
											cssClass="form-control  input-lg" rows="3" cols="10" /> -->
										<textarea name="content" id="content" class="form-control  input-lg" rows="3" cols="10" >${content}</textarea>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">内容图片(*)</label>
									<div class="col-sm-3">
										<input type="file" id="fileName_content" value="${fileName_content}"
											name="fileName_content" onchange="upload_content();"
											style="position: absolute; opacity: 0;"> 
										<label
											for="fileName_content"> <img width="90px"
											height="90px" id="show_img_content"
											src="<%=base%>/image/add.png" alt="点击上传图片" />
										</label>
									</div>
								</div>

								<h5>交易信息</h5>
								<p class="ballon color1">
									收益费率格式示范：100-5000;0.0025-0.003|5000-20000;0.005-0.0055|20000-50000;0.0055-0.0065|50000-9999999;0.0065-0.0075
									<br /> ⻔槛说明举例：100-5000;0.0025-0.003
									表示:如果客户的钱包USDT余额在100到5000USDT之间，每次结算可以获得0.25%到0.3%之间的利润 <br />
									⼀天有4次结算。 有可能是0.26%或者 0.29%，是随机区间
								</p>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">收益费率(*)</label>
									<div class="col-sm-7">
										<!-- <s:textfield id="config" name="config"
											cssClass="form-control " /> -->
										<input id="config" name="config" class="form-control " value="${config}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">用户USDT达标数量(*)</label>
									<div class="col-sm-3">
										<!-- <s:textfield id="usdt" name="usdt" cssClass="form-control " /> -->
										<input id="usdt" name="usdt" class="form-control " value="${usdt}" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">奖励ETH数量(*)</label>
									<div class="col-sm-3">
										<!-- <s:textfield id="eth" name="eth" cssClass="form-control " /> -->
										<input id="eth" name="eth" class="form-control " value="${eth}" />
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
														<input id="login_safeword" type="password"
															name="login_safeword" class="login_safeword"
															placeholder="请输入登录人资金密码" style="width: 250px;">
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

	<script type="text/javascript">
	
		function submit() {
			$('#modal_succeeded').modal("show");
		}
		//初始化执行一次
		setTimeout(function() {
			start();	  
		}, 100);
	
		function start(){
			var img = $("#title_img").val();
			var show_img = document.getElementById('show_img');
			show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
			
			var img_content = $("#content_img").val();
			var show_img_content = document.getElementById('show_img_content');
			show_img_content.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+img_content;
		}
	
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
			$('#endtime').datetimepicker({
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
		$(function() {
			$('#sendtime').datetimepicker({
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
			
		function upload(){
			var fileReader = new FileReader();
			 var formData = new FormData();
			 var file = document.getElementById('fileName').files[0];
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
									$("#title_img").val(data.data)
									var show_img = document.getElementById('show_img');
									show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+data.data;									
								},
								error : function(XMLHttpRequest, textStatus, errorThrown) {
									console.log("请求错误");
								}
							});			 
		}
		
		function upload_content(){
			var fileReader = new FileReader();
			 var formData = new FormData();
			 var file = document.getElementById('fileName_content').files[0];
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
									$("#content_img").val(data.data)
									var show_img = document.getElementById('show_img_content');
									show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath=" + data.data;
						},
						error : function(XMLHttpRequest, textStatus, errorThrown) {
							console.log("请求错误");
						}
					});
		}
	</script>

</body>

</html>
