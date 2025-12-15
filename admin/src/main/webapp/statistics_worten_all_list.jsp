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
			<h3>运营数据</h3>
			
			<%@ include file="include/alert.jsp"%>

			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
								<form class="form-horizontal"
									action="<%=basePath%>normal/exchangeAdminAllStatisticsAction!list.action"
									method="post" id="queryForm">
									
									<input type="hidden" name="status_para"/>
									<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"> 
									<input type="hidden" name="para_time" id="para_time" value="${para_time}">

									<div class="col-md-12 col-lg-3">
										<input id="start_time" name="start_time" class="form-control " placeholder="开始日期" value="${start_time}"/>	
									</div>
									
									<div class="col-md-12 col-lg-3">
										<input id="end_time" name="end_time" class="form-control " placeholder="结束日期" value="${end_time}"/>
									</div>

									<div class="col-md-12 col-lg-2">
										<button type="submit" class="btn btn-light btn-block">查询</button>
									</div>

									<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
										<div class="mailbox clearfix">
											<div class="mailbox-menu">
												<ul class="menu">
													<li><a href="javascript:setTime('day')"> 当天</a></li>
													<li><a href="javascript:setTime('week')"> 当周</a></li>
													<li><a href="javascript:setTime('month')"> 当月</a></li>
													<li><a href="javascript:setTime('all')"> 全部</a></li>
												</ul>
											</div>
										</div>
									</div>
								</form>
						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">数据汇总</div>
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">赠送金额</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">店铺销售总额</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">已发放佣金</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">总充值BTC</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">总充值EHT</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">总充值USDT</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">总充值USDC</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">总提现BTC</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">总提现EHT</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">总提现USDT</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">总提现USDC</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">存取差额BTC</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">存取差额EHT</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">存取差额USDT</td>
										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">存取差额USDC</td>
									</tr>
								</thead>
								
								<tbody>
									<tr>
										<td><fmt:formatNumber value="${sumdata.gift_money}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.sellerTotalSales}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.translate}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.rechargeBtc}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.rechargeEth}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.rechargUsdt}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.rechargeUsdc}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.withdrawBtc}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.withdrawEth}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.withdrawUsdt}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.withdrawUsdc}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.totalBtc}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.totalEth}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.totalUsdt}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${sumdata.totalUsdc}" pattern="#0.0000" /></td>
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

						<div class="panel-title">查询结果</div>
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
								<tr>
									<td colspan="5" rowspan="1" style="text-align:center;vertical-align: middle;">数据</td>
									<td colspan="12" style="text-align: center;">充提</td>
								</tr>
								<tr style="text-align:center;">
									<td rowspan="2" style="text-align:center;vertical-align: middle;">日期</td>
									<td rowspan="2" style="text-align:center;vertical-align: middle;min-width: 112px;">赠送金额</td>
									<td rowspan="2" style="text-align:center;vertical-align: middle;min-width: 112px;">销售总额</td>
									<td rowspan="2" style="text-align:center;vertical-align: middle;">已发放佣金</td>

									<td colspan="4">充值</td>
									<td colspan="4">提现</td>
									<td colspan="4">充值差额</td>
								<tr style="text-align: center;">
									<td>USDT</td>
									<td>USDC</td>
									<td>ETH</td>
									<td>BTC</td>
									<td>USDT</td>
									<td>USDC</td>
									<td>ETH</td>
									<td>BTC</td>
									<td>USDT</td>
									<td>USDC</td>
									<td>ETH</td>
									<td>BTC</td>
								</tr>
								</tr>
								</thead>
								
								<tbody>								
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<td>${item.date}</td>
										<td>${item.gift_money}</td>
										<td><fmt:formatNumber value="${item.sellerTotalSales}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.translate}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.recharge_usdt}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.recharge_usdc}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.recharge_eth}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.recharge_btc}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.withdraw_usdt}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.withdraw_usdc}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.withdraw_eth}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.withdraw_btc}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.totalUsdt}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.totalUsdc}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.totalEth}" pattern="#0.0000" /></td>
										<td><fmt:formatNumber value="${item.totalBtc}" pattern="#0.0000" /></td>
										</tr>
									</c:forEach>
								</tbody>
								
							</table>
							
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
