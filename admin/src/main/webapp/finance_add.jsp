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
			<h3>理财产品配置 </h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminFinanceAction!list.action" method="post" id="queryForm">
			
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
				<input type="hidden" name="name_para" id="name_para" value="${name_para}" />
				
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							新增理财产品
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>
						
						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminFinanceAction!add.action"
								method="post" name="mainForm" id="mainForm">
								
								<input type="hidden" name="id" id="id" value="${id}" />
								<input type="hidden" name="img" id="img" value="${img}" />
								
								<h5>基础信息</h5>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(简体中文)</label>
									<div class="col-sm-3">
										<input id="name" name="name" class="form-control" value="${name}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(繁体中文)</label>
									<div class="col-sm-3">
										<input id="name_cn" name="name_cn" class="form-control" value="${name_cn}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(英文)</label>
									<div class="col-sm-3">
										<input id="name_en" name="name_en" class="form-control" value="${name_en}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(韩语)</label>
									<div class="col-sm-3">
										<input id="name_kn" name="name_kn" class="form-control" value="${name_kn}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(日语)</label>
									<div class="col-sm-3">
										<input id="name_jn" name="name_jn" class="form-control" value="${name_jn}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品图片</label>
									<div class="col-sm-3">	
										<input type="file" id="fileName" name="fileName" value="${fileName}" onchange="upload();"  style="position:absolute;opacity:0;">	
										<label for="fileName">　　　　　　
　　　　										<img width="90px" height="90px" id="show_img" src="<%=base%>/image/add.png"  alt="点击上传图片" />　　
　										</label>
									</div>										
								</div>
								
								<h5>交易信息</h5>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">状态</label>
									<div class="col-sm-3">
										<div class="input-group">
											<select id="state" name="state" class="form-control " >
											   <option value="0" <c:if test="${state == '0'}">selected="true"</c:if> >停用</option>
											   <option value="1" <c:if test="${state == '1'}">selected="true"</c:if> >启用</option>
											</select>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">周期</label>
									<div class="col-sm-3">
										<div class="input-group">
											<input id="cycle" name="cycle" class="form-control" value="${cycle}" />
											<div class="input-group-addon">天</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">日利率</label>
									<div class="col-sm-3">
									<div class="input-group">										 	
										<input id="daily_rate" name="daily_rate" class="form-control" value="${daily_rate}" />
										<div class="input-group-addon">--</div>
											<input id="daily_rate_max" name="daily_rate_max" class="form-control" value="${daily_rate_max}"/>
										<div class="input-group-addon">%</div>
									</div>	
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">今日利率</label>
									<div class="col-sm-3">
									<div class="input-group">
										<input id="today_rate" name="today_rate" class="form-control" value="${today_rate}" />
										<div class="input-group-addon">%</div>
									</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">违约结算比例</label>
									<div class="col-sm-3">
									<div class="input-group">
										<input id="default_ratio" name="default_ratio" class="form-control" value="${default_ratio}" />
										<div class="input-group-addon">%</div>
									</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">投资金额区间</label>
									<div class="col-sm-4">
										<div class="input-group">
											<input id="investment_min" name="investment_min" class="form-control" value="${investment_min}" />
										 	<div class="input-group-addon">--</div>
										 	<input id="investment_max" name="investment_max" class="form-control" value="${investment_max}" />
										 	<div class="input-group-addon">USDT</div>
										</div>
									</div>
								</div>								
								
								<div class="col-sm-1">
									<!-- 模态框（Modal） -->
									<div class="modal fade" id="modal_succeeded" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
										aria-hidden="true">
										<div class="modal-dialog">
											<div class="modal-content" style="width: 350px;">
											
												<div class="modal-header">
													<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
													<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
												</div>
												
												<div class="modal-body">
													<div class="" >
														<input id="login_safeword" type="password" name="login_safeword" value="${login_safeword}"
															class="login_safeword" placeholder="请输入登录人资金密码" style="width: 250px;">
													</div>
												</div>
												
												<div class="modal-footer" style="margin-top: 0;">
													<button type="button" class="btn " data-dismiss="modal">关闭</button>
													<button id="sub" type="submit" class="btn btn-default" >确认</button>
												</div>
												
											</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<a href="javascript:goUrl(${pageNo})" class="btn">取消</a> 
										<a href="javascript:submit()" class="btn btn-default">保存</a>
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
					$("#img").val(data.data)
					var show_img = document.getElementById('show_img');
					show_img.src="<%=base%>wap/public/showimg!showImg.action?imagePath="+data.data;						
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					console.log("请求错误");
				}
			});			 
		}		
	</script>
	
</body>

</html>
