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
			<h3>用户存量资金汇总</h3>
			<%@ include file="include/alert.jsp"%>

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
							<div class="col-md-12 col-lg-12" style="margin-bottom: 10px;">
								<div class="mailbox clearfix">
								</div>
							</div>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>账户</td>
										<td>存量</td>
									</tr>
								</thead>
								<tbody>
<%--									<c:forEach items="${datas}" var="item" varStatus="stat">--%>
										<tr>
<%--											<td>${item.wallettype_cn}</td>--%>
											<td>usdt</td>
											<td>${amount}</td>
											<td>
												<a href="<%=basePath%>brush/userMoney/walletDayList.action" class="btn btn-light">详情</a>
											</td>
										</tr>
<%--									</c:forEach>--%>

								</tbody>
							</table>

						</div>

					</div>
					<!-- End Panel -->

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
			}).on('changeDate',function(ev){
				$("#para_time").val("");
			});
				       

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
			}).on('changeDate',function(ev){
				$("#para_time").val("");
			});
			$("#para_time").val("");
		});
	</script>


	<script type="text/javascript">
		function setTime(time){
    		document.getElementById("para_time").value=time;
    		document.getElementById("queryForm").submit();
		}
	</script>

													
<form action="<%=basePath%>normal/adminUserMoneyStatisticsAction!exportData.action" method="post" id="exportData">
		<input type="hidden" name="pageNo" value="${pageNo}">
		<input type="hidden" name="end_time"/>
		<input type="hidden" name="start_time"/>
		<input type="hidden" name="para_time"/>
	
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
// 			 $('input[name="end_time"]').val($("#end_time").val());
// 			 $('input[name="start_time"]').val($("#start_time").val());
// 			 $('input[name="para_time"]').val($("#para_time").val());
			document.getElementById("exportData").submit();
			
		});

	};
	</script>
</body>
</html>