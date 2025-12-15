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

	<div class="ifr-dody">
	
		<div class="ifr-con">
			<h3>矿机配置</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<form action="<%=basePath%>normal/adminMinerAction!list.action" method="post" id="queryForm">
			
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="name_para" id="name_para" value="${name_para}">
				
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改矿机配置
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminMinerAction!update.action"
								method="post" name="mainForm" id="mainForm">
								
								<input type="hidden" name="id" id="id" value="${id}" />
								<input type="hidden" name="img" id="img" value="${img}" />
								
								<h5>基础信息</h5>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(简体中文)</label>
									<div class="col-sm-4">
										<input id="name" name="name" class="form-control " value="${name}"/>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(繁体中文)</label>
									<div class="col-sm-4">
										<input id="name_cn" name="name_cn" class="form-control " value="${name_cn}"/>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">产品名称(英文)</label>
									<div class="col-sm-4">
										<input id="name_en" name="name_en" class="form-control " value="${name_en}"/>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">适用算法</label>
									<div class="col-sm-3">
										<div class="input-group">									
											<select id="algorithm" name="algorithm" class="form-control " >
												<option value="EtHash" <c:if test="${algorithm == 'EtHash'}">selected="true"</c:if> >EtHash</option>
												<option value="EquiHash" <c:if test="${algorithm == 'EquiHash'}">selected="true"</c:if> >EquiHash</option>
												<option value="Kadena" <c:if test="${algorithm == 'Kadena'}">selected="true"</c:if> >Kadena</option>
												<option value="EthHash" <c:if test="${algorithm == 'EthHash'}">selected="true"</c:if> >EthHash</option>
											</select>	
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">网络连接</label>
									<div class="col-sm-3">
										<div class="input-group">
											<select id="internet" name="internet" class="form-control " >
												<option value="Ethernet" <c:if test="${internet == 'Ethernet'}">selected="true"</c:if> >Ethernet</option>
											</select>	
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">生产厂家</label>
									<div class="col-sm-4">
										<input id="product_factory" name="product_factory" class="form-control " value="${product_factory}"/>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">外箱尺寸</label>
									<div class="col-sm-4">
										<input id="product_size" name="product_size" class="form-control " value="${product_size}"/>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">额定算力</label>
									<div class="col-sm-2">
										<input id="computing_power" name="computing_power" class="form-control " value="${computing_power}"/>
									</div>
									<div class="col-sm-2">
										<select id="computing_power_unit" name="computing_power_unit" class="form-control " >
											<option value="MH/s" <c:if test="${computing_power_unit == 'MH/s'}">selected="true"</c:if> >MH/s</option>
											<option value="Ksol/s" <c:if test="${computing_power_unit == 'Ksol/s'}">selected="true"</c:if> >Ksol/s</option>
											<option value="Th/s" <c:if test="${computing_power_unit == 'Th/s'}">selected="true"</c:if> >Th/s</option>
											<option value="Gh/s" <c:if test="${computing_power_unit == 'Gh/s'}">selected="true"</c:if> >Gh/s</option>
										</select>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">官方功耗</label>
									<div class="col-sm-2">
										<div class="input-group">
											<input id="power" name="power" class="form-control " value="${power}"/>
											<div class="input-group-addon">w</div>
										</div>	
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">整机重量</label>
									<div class="col-sm-2">
										<div class="input-group">
											<input id="weight" name="weight" class="form-control " value="${weight}"/>
											<div class="input-group-addon">kg</div>
										</div>	
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">工作温度</label>
									<div class="col-sm-4">
										<div class="input-group">
											<input id="work_temperature_min" name="work_temperature_min" class="form-control " value="${work_temperature_min}"/>
										 	<div class="input-group-addon">--</div>
										 	<input id="work_temperature_max" name="work_temperature_max" class="form-control " value="${work_temperature_max}"/>
										 	<div class="input-group-addon">℃</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">工作湿度</label>
									<div class="col-sm-4">
										<div class="input-group">
										    <input id="work_humidity_min" name="work_humidity_min" class="form-control " value="${work_humidity_min}"/>
										 	<div class="input-group-addon">--</div>
										 	<input id="work_humidity_max" name="work_humidity_max" class="form-control " value="${work_humidity_max}"/>
										 	<div class="input-group-addon">%RH</div>
										</div>
									</div>
								</div>
								
								<h5>交易信息</h5>
								<p class="ballon color1">投资金额区间: 最大金额为空则表示无限制（非体验矿机）。</p>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">投资金额区间</label>
									<div class="col-sm-4">
										<div class="input-group">
										
											<input id="investment_min" name="investment_min" class="form-control " value="${investment_min}"/>
										 	<div class="input-group-addon">--</div>
										 	
										 	<c:choose>
												<c:when test="${test=='Y'}">
													<input id="investment_max" name="investment_max" class="form-control " value="${investment_max}"/>
												</c:when>
												<c:otherwise>
													<c:choose>
														<c:when test="${investment_max=='0'}">
														    <input id="investment_max" name="investment_max" class="form-control " value="${investment_max}"/>
														</c:when>
														<c:otherwise>
														    <input id="investment_max" name="investment_max" class="form-control " value="${investment_max}"/>
														</c:otherwise>	
													</c:choose>
												</c:otherwise>	
											</c:choose>
											
										 	<div class="input-group-addon">USDT</div>
										 
										</div>
									</div>									
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">可解锁周期</label>
									<div class="col-sm-2">
										<div class="input-group">
											<input id="cycle_close" name="cycle_close" class="form-control " value="${cycle_close}"/>
											<div class="input-group-addon">天</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">日利率</label>
									<div class="col-sm-2">
										<div class="input-group">
											<input id="show_daily_rate" name="show_daily_rate" class="form-control " value="${show_daily_rate}"/>
											<div class="input-group-addon">%</div>
										</div>	
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">今日利率</label>
									<div class="col-sm-2">
										<div class="input-group">
											<input id="daily_rate" name="daily_rate" class="form-control " value="${daily_rate}"/>
											<div class="input-group-addon">%</div>
										</div>	
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">在售</label>
									<div class="col-sm-3">
										<div class="input-group">
											<select id="on_sale" name="on_sale" class="form-control " >
												<option value="0" <c:if test="${on_sale == '下架'}">selected="true"</c:if> >下架</option>
												<option value="1" <c:if test="${on_sale == '上架'}">selected="true"</c:if> >上架</option>
											</select>
										</div>
									</div>
								</div>
								
								<div class="col-sm-1">
									<!-- 模态框（Modal） -->
									<div class="modal fade" id="modal_succeeded" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
										<div class="modal-dialog">
											<div class="modal-content" style="width: 350px;">
											
												<div class="modal-header">
													<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
													<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
												</div>
												
												<div class="modal-body">
													<div class="" >
														<input id="login_safeword" type="password" name="login_safeword"
															class="login_safeword" placeholder="请输入登录人资金密码" style="width: 250px;">
													</div>
												</div>
												
												<div class="modal-footer" style="margin-top: 0;">
													<button type="button" class="btn " data-dismiss="modal">关闭</button>
													<button id="sub" type="submit" class="btn btn-default" >确认</button>
												</div>
												
											</div>
											<!-- /.modal-content -->
										</div>
										<!-- /.modal -->
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

		<%@ include file="include/footer.jsp"%>

	</div>

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
