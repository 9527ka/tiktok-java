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
			<h3>订单详情</h3>
			
			<%@ include file="include/alert.jsp"%>

			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">

						<div class="panel-body">

								<form class="form-horizontal"
									action="<%=basePath%>mall/order/detailsList.action"
									method="post" id="queryForm">
									
									<input type="hidden" name="status_para"/>
									<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"> 
									<input type="hidden" name="id" id="id" value="${id}">

								</form>
						</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">基本信息</div>
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">订单编号</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">下单时间</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">用户ID</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">支付状态</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">订单状态</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">订单类型</td>
									</tr>
								</thead>
								
								<tbody>
									<tr>
										<td style="width: 278px; text-align:center;vertical-align: middle;">${data.id}</td>
										<td style="width: 278px; text-align:center;vertical-align: middle;">${data.createTime}</td>
										<td style="width: 278px; text-align:center;vertical-align: middle;">${data.userCode}</td>
										<td style="width: 278px; text-align:center;vertical-align: middle;">
											<c:if test="${data.payStatus=='0'}">
												未支付
											</c:if>
											<c:if test="${data.payStatus=='1'}">
												已支付
											</c:if>
										</td>
										<td style="width: 278px; text-align:center;vertical-align: middle;">
											<c:if test="${data.status =='-1'}">
												已取消
											</c:if>
											<c:if test="${data.status =='0'}">
												待付款
											</c:if>
											<c:if test="${data.status =='1'}">
												待发货
											</c:if>
											<c:if test="${data.status =='2'}">
												已确定
											</c:if>
											<c:if test="${data.status =='3'}">
												待收货
											</c:if>
											<c:if test="${data.status =='4'}">
												已收货
											</c:if>
											<c:if test="${data.status =='5'}">
												已评价
											</c:if>
											<c:if test="${data.status =='6'}">
												退款
											</c:if>
										</td>
										<td style="width: 278px; text-align:center;vertical-align: middle;">
											<c:if test="${data.orderStatus=='1'}">
												<span class="right label label-danger">虚拟订单</span>
											</c:if>
											<c:if test="${data.orderStatus=='0'}">
												<span class="right label label-success">真实订单</span>
											</c:if>
										</td>

									</tr>
								</tbody>
								
							</table>
							<table class="table table-bordered table-striped">

								<thead>
								<tr>
									<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">订单总额</td>
									<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">税收总额</td>
									<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">运费</td>
									<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">利润</td>
									<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">优惠总额</td>
									<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">应付金额</td>
								</tr>
								</thead>

								<tbody>
								<tr>
									<td style="width: 278px; text-align:center;vertical-align: middle;">${data.prizeOriginal}</td>
									<td style="width: 278px; text-align:center;vertical-align: middle;">${data.tax}</td>
									<td style="width: 278px; text-align:center;vertical-align: middle;">${data.fees}</td>
									<td style="width: 278px; text-align:center;vertical-align: middle;">${data.profit}</td>
									<td style="width: 278px; text-align:center;vertical-align: middle;"> <fmt:formatNumber value="${data.prizeOriginal - data.prizeReal}" pattern="#0.00" /></td>
									<td style="width: 278px; text-align:center;vertical-align: middle;">${data.prizeReal}</td>

								</tr>
								</tbody>

							</table>
							<table class="table table-bordered table-striped">

								<thead>
								<tr>
									<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">采购时间</td>
									<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">商家类型</td>
								</tr>
								</thead>

								<tbody>
								<tr>
									<td style="width: 278px; text-align:center;vertical-align: middle;">${purchTime}</td>


									<td style="width: 278px; text-align:center;vertical-align: middle;">
										<c:if test="${sellerRoleName=='GUEST'}">
											<span class="right label label-danger">演示店铺</span>
										</c:if>
										<c:if test="${sellerRoleName=='MEMBER'}">
											<span class="right label label-success">真实店铺</span>
										</c:if>
									</td>
								</tr>
								</tbody>

							</table>

						</div>

					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">收货人信息</div>
						<div class="panel-body">

							<table class="table table-bordered table-striped">

								<thead>
									<tr>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">收货人</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">地址</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">电话</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">邮箱</td>
									</tr>
								</thead>

								<tbody>
									<tr>
										<td style="width: 278px; text-align:center;vertical-align: middle;">${data.contacts}</td>
										<td style="width: 278px; text-align:center;vertical-align: middle;">${data.address}</td>
										<td style="width: 278px; text-align:center;vertical-align: middle;">${data.phone}</td>
										<td style="width: 278px; text-align:center;vertical-align: middle;">${data.email}</td>
									</tr>
								</tbody>

							</table>
							<table class="table table-bordered table-striped">

								<thead>
								<tr>
									<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">国家</td>
									<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">州</td>
									<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">城市</td>
									<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;width: 278px;">邮编</td>
								</tr>
								</thead>

								<tbody>
								<tr>
									<td style="width: 278px; text-align:center;vertical-align: middle;">${data.country}</td>
									<td style="width: 278px; text-align:center;vertical-align: middle;">${data.province}</td>
									<td style="width: 278px; text-align:center;vertical-align: middle;">${data.city}</td>
									<td style="width: 278px; text-align:center;vertical-align: middle;">${data.postcode}</td>
								</tr>
								</tbody>

							</table>

						</div>

					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">商品信息</div>
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td colspan="1" rowspan="3" style="text-align: center; vertical-align: middle;">商品Id</td>
										<td colspan="1" rowspan="3" style="text-align: center; vertical-align: middle;">商品名称</td>
										<td colspan="1" rowspan="3" style="text-align: center; vertical-align: middle;">属性</td>
										<td colspan="1" rowspan="2" style="text-align: center; vertical-align: middle;">价格</td>
										<td colspan="1" rowspan="2" style="text-align: center; vertical-align: middle;">数量</td>
										<td colspan="1" rowspan="2" style="text-align:center;vertical-align: middle;">小计</td>
									</tr>
								</thead>
								
								<tbody>								
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
										<tr>
											<td>${item.goodsId}</td>
											<td>${item.goodsName}</td>
										    <td>${item.attrs}</td>
											<td>${item.goodsReal}</td>
											<td>${item.goodsNum}</td>
											<td>${item.countAmount}</td>
										</tr>
									</c:forEach>
								</tbody>
								
							</table>
							<div class="modal-footer" style="margin-top: 0;">
								<label>　<a href="<%=basePath%>mall/order/list.action" class="btn btn-light">返回</a></label> 　　
							</div>
							<%@ include file="include/page_simple.jsp"%>


							<!-- <nav> -->
						</div>


					</div>
					<!-- End Panel -->

				</div>
			</div>

		</div>
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		
		<div class="form-group">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_recharge" tabindex="-1"
				role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content" style="height: 500px;">
						<div class="modal-content">
						
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
								<h4 class="modal-title">用户充值币种分类</h4>
							</div>
							
							<div class="modal-header">
								<h4 class="modal-title">充值USDT数量</h4>
							</div>
							
							<div class="modal-body">
								<div class="">
									<input id="recharge_usdt" name="recharge_usdt" type="text"
										class="form-control" readonly="readonly">
								</div>
							</div>
							
							<div class="modal-header">
								<h4 class="modal-title">充值ETH数量</h4>
							</div>
							
							<div class="modal-body">
								<div class="">
									<input id="recharge_eth" name="recharge_eth" type="text"
										class="form-control" readonly="readonly">
								</div>
							</div>
							
							<div class="modal-header">
								<h4 class="modal-title">充值BTC数量</h4>
							</div>
							
							<div class="modal-body">
								<div class="">
									<input id="recharge_btc" name="recharge_btc" type="text"
										class="form-control" readonly="readonly">
								</div>
							</div>

							<div class="modal-footer" style="margin-top: 0;">
								<button type="button" class="btn " data-dismiss="modal">关闭</button>
							</div>
							
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal -->
				</div>
			</div>
		</div>

		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<%@ include file="include/js.jsp"%>

	<script type="text/javascript">
		$.fn.datetimepicker.dates['zh'] = {
			days : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" ],
			daysShort : [ "日", "一", "二", "三", "四", "五", "六", "日" ],
			daysMin : [ "日", "一", "二", "三", "四", "五", "六", "日" ],
			months : [ "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月",
					"九月", "十月", "十一月", "十二月" ],
			monthsShort : [ "一", "二", "三", "四", "五", "六", "七", "八", "九",
					"十", "十一", "十二" ],
			meridiem : [ "上午", "下午" ],
			//suffix:      ["st", "nd", "rd", "th"],  
			today : "今天",
			clear : "清空"
		};

		$(function() {
			$('#start_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			}).on('changeDate', function(ev) {
				$("#para_time").val("");
			});
			$('#statistics_start_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			})
			$('#end_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			}).on('changeDate', function(ev) {
				$("#para_time").val("");
			});
			$('#statistics_end_time').datetimepicker(
					{
						format : 'yyyy-mm-dd',
						language : 'zh',
						weekStart : 1,
						// 				todayBtn : 1,
						autoclose : 1,
						todayHighlight : 1,
						startView : 2,
						clearBtn : true,
						minView : 2,
						endDate : new Date(new Date().getTime() - 24 * 60 * 60 * 1000)
					})
			$("#para_time").val("");
		});
	</script>

	<script type="text/javascript">
		function setTime(time) {
			document.getElementById("para_time").value = time;
			document.getElementById("queryForm").submit();
		}
		function message(title) {
			swal({
				title : title,
				text : "",
				type : "warning",
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : true
			});
		}
	</script>
		
	<form
		action="<%=basePath%>normal/exchangeAdminAllStatisticsAction!exportData.action"
		method="post" id="exportData">
		<input type="hidden" name="pageNo" value="${pageNo}">
		<input type="hidden" name="end_time">
		<input type="hidden" name="start_time">
		<input type="hidden" name="para_time">
	</form>
	
	<script type="text/javascript">
		function exportData_confirm() {
			swal({
				title : "确认导出订单数据到文件?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : true
			}, function() {
				$('input[name="end_time"]').val(
						$("#end_time").val());
				$('input[name="start_time"]').val(
						$("#start_time").val());
				$('input[name="para_time"]').val(
						$("#para_time").val());
				document.getElementById("exportData").submit();
			});
		};
		function getAllRecharge(recharge, recharge_usdt,recharge_eth,recharge_btc) {
			 $("#recharge_usdt").val(recharge_usdt);
			 $("#recharge_eth").val(recharge_eth);
			 $("#recharge_btc").val(recharge_btc);
			$("#modal_recharge").modal("show");
		}
	</script>
		
</body>

</html>
