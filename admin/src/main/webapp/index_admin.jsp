<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
	<%@ include file="include/head.jsp"%>
	<style>
		.so-button {
			display: inline-block;
			line-height: 1;
			white-space: nowrap;
			cursor: pointer;
			background: #fff;
			border: 1px solid #dcdfe6;
			color: #606266;
			-webkit-appearance: none;
			text-align: center;
			box-sizing: border-box;
			outline: none;
			margin: 0;
			transition: .1s;
			font-weight: 500;
			-moz-user-select: none;
			-webkit-user-select: none;
			-ms-user-select: none;
			padding: 9px 15px;
			font-size: 12px;
			border-radius: 3px
		}

		.so-button.active {
			color: #fff;
			background-color: #409eff;
			border-color: #409eff;
		}
		.container-default{
			padding: 0;
		}


		.mar-b30 {
			display: flex;
			flex-wrap: wrap;
			align-items: center;
			justify-content: space-between;
		}
		.car-panel {
			align-items: center;
			border-radius: 8px;
			width: 19% !important;
			margin: 10px 0 !important;
			border: none !important;
			padding: 0 20px !important;
		}
		.car-panel .car-t4 {
			font-size: 28px !important;
			font-weight: 700;
			font-family: 'Arial Negreta', 'Arial Normal', 'Arial', sans-serif;
		}
		.car-panel .car-t3 {
			font-size: 16px !important;
			margin-top: 4px;
		}
		.car-panels {
			align-items: center !important;
			border: none !important;
			width: 24.25% !important;
			padding: 0 20px !important;
			border-radius: 8px !important;
		}
		.car-panels .car-t4 {
			font-size: 28px !important;
			font-weight: 700;
			font-family: 'Arial Negreta', 'Arial Normal', 'Arial', sans-serif;
		}
		.car-panels .car-t3s {
			font-size: 16px !important;
			margin-top: 4px;
		}

		.car-all {
			margin-right: 1% !important;

		}
		.mr-0 {
			margin-right: 0% !important;
		}
		.row {
			margin-top: 10px !important;
		}

		.ifr-dody {
			background: #f6f6f6 !important;
		}
		.new-col{
			padding-right: 10px !important;
		}
		.new-col1{
			padding-left: 0.22% !important;
		}


		.panel-title{
			font-size: 16px !important;
		}
		.panel-body tr{
			height: 40px;
		}

		.panel-body tr td{
			line-height: 30px !important;
		}
		.panel-defaults {
			border-color: #fff !important;
		}
		.tab{margin-top:10px;}

		.panel {
			box-shadow: none !important;
			border: 1px solid #fff !important;
			background: #fff !important;
			padding: 20px !important;
			margin-bottom: 20px !important;
			position: relative !important;
		}

		.section{ border-radius:7px; }

		.section2{
		    background: #FFF;
		    padding: 20px;
		}
		.section1{
		    background: #FFF;
		    padding: 20px;
		    margin-top:15px;
		}
		.section__block{
		   display: flex;
           gap: 5px;
           flex-basis: 25%;
           margin-top:5px;
        }
	</style>
	<%@ include file="include/head.jsp"%>
</head>

<body class="ifr-dody">

<%@ include file="include/loading.jsp"%>

<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTENT -->

<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTAINER -->
<div class="ifr-con">
	<div class="container-defaults">
		<div class="tab">
			<c:choose>
				<c:when test="${timeType == 'day'}">
					<button class="so-button active" data-attr="day">今日</button>
				</c:when>
				<c:otherwise>
					<button class="so-button " data-attr="day">今日</button>
				</c:otherwise>
			</c:choose>

			<c:choose>
				<c:when test="${timeType == 'week'}">
					<button class="so-button active" data-attr="week">本周</button>
				</c:when>
				<c:otherwise>
					<button class="so-button " data-attr="week">本周</button>
				</c:otherwise>
			</c:choose>

			<c:choose>
				<c:when test="${timeType == 'month'}">
					<button class="so-button active" data-attr="month">本月</button>
				</c:when>
				<c:otherwise>
					<button class="so-button " data-attr="month">本月</button>
				</c:otherwise>
			</c:choose>


			<%--			<button class="so-button" data-attr="week">本周</button>--%>
			<%--			<button class="so-button" data-attr="month">本月</button>--%>
		</div>

		<div class="mar-b30 section section1">
			<div class="car-panel car-one">
				<div class="car-t-l float-l">
					<div class="car-t4 viewHead" ></div>
					<div class="car-t3 margin-t-0" >新客充值</div>
				</div>
			</div>
			<div class="car-panel car-two">
				<div class="car-t-l float-l">
					<div class="car-t4 viewHead"></div>
					<div class="car-t3 margin-t-0">新客充值人数</div>
				</div>
			</div>
			<div class="car-panel car-three">
				<div class="car-t-l float-l">
					<div class="car-t4 viewHead"></div>
					<div class="car-t3 margin-t-0">新客提现</div>
				</div>
			</div>
			<div class="car-panel car-for">
				<div class="car-t-l float-l">
					<div class="car-t4 viewHead"></div>
					<div class="car-t3 margin-t-0">新客提现人数</div>
				</div>
			</div>
			<div class="car-panel car-five mar-0">
				<div class="car-t-l float-l">
					<div class="car-t4 viewHead"></div>
					<div class="car-t3 margin-t-0">新客充值差额</div>
				</div>
			</div>
			<div class="car-panel car-six">
				<div class="car-t-l float-l">
					<div class="car-t4 viewHead" ></div>
					<div class="car-t3 margin-t-0" >充值</div>
				</div>
			</div>
			<div class="car-panel car-seven">
				<div class="car-t-l float-l">
					<div class=" car-t4 viewHead"></div>
					<div class="car-t3 margin-t-0">充值人数</div>
				</div>
			</div>
			<div class="car-panel car-eight">
				<div class="car-t-l float-l">
					<div class="car-t4 viewHead"></div>
					<div class="car-t3 margin-t-0">提现</div>
				</div>
			</div>
			<div class="car-panel car-long">
				<div class="car-t-l float-l">
					<div class="car-t4 viewHead"></div>
					<div class="car-t3 margin-t-0">提现人数</div>
				</div>
			</div>
			<div class="car-panel car-ten mar-0">
				<div class="car-t-l float-l">
					<div class="car-t4 viewHead"></div>
					<div class="car-t3 margin-t-0">充值差额</div>
				</div>
			</div>


		</div>

	<div class="mar-b30_ section section2">
          <div class="section__block">
	            <div class="car-panels">
        				<div class="car-t-l float-l">
        					<div class="car-t4 viewMiddle"></div>
        					<div class="car-t3s margin-t-0">订单数量</div>
        				</div>
        			</div>
        			<div class="car-panels">
        				<div class="car-t-l float-l">
        					<div class="car-t4 viewMiddle"></div>
        					<div class="car-t3s margin-t-0">销售总额</div>
        				</div>
        			</div>
        			<div class="car-panels">
        				<div class="car-t-l float-l">
        					<div class="car-t4 viewMiddle"></div>
        					<div class="car-t3s margin-t-0">佣金发放</div>
        				</div>
        			</div>
        			<div class="car-panels">
        				<div class="car-t-l float-l">
        					<div class="car-t4 viewMiddle"></div>
        					<div class="car-t3s margin-t-0">新增用户</div>
        				</div>
        			</div>
        </div>

  <div class="section__block">
		<div class="car-panels">
			<div class="car-t-l float-l">
				<div class="car-t4 viewMiddle"></div>
				<div class="car-t3s margin-t-0">新增店铺</div>
			</div>
		</div>
		<div class="car-panels">
			<div class="car-t-l float-l">
				<div class="car-t4 viewMiddle"></div>
				<div class="car-t3s margin-t-10">活跃人数</div>
			</div>
		</div>
    
		<div class="car-panels ">
        			<div class="car-t-l float-l">
        				<div class="car-t4 viewMiddle"></div>
        				<div class="car-t3s margin-t-0">总用户</div>
        			</div>
        		</div>
        		<div class="car-panels">
        			<div class="car-t-l float-l">
        				<div class="car-t4 viewMiddle"></div>
        				<div class="car-t3s margin-t-0">总店铺</div>
        			</div>
        		</div>

		</div>
    </div>

	</div>

	<div class="row new-row" style="transform: translateY(10px);">
		<div class="col-md-9 new-col">
			<div class="panel panel-defaults">
				<div class="panel-title">店铺销售TOP10</div>
				<div class="panel-body">
					<table class="table table-striped ">
						<thead>
						<tr>
							<td>店铺名称</td>
							<td>销售总额</td>
							<td>销量</td>
						</tr>
						</thead>
						<tbody class="top10-sellers">
<%--						<c:forEach items="${statistics.top10SellerList}" var="seller">--%>
<%--							<tr>--%>
<%--								<td>${seller.sellerName}</td>--%>
<%--								<td><fmt:formatNumber value="${seller.amount}" pattern="#0.00" /></td>--%>
<%--								<td>${seller.goodsCount}</td>--%>
<%--							</tr>--%>
<%--						</c:forEach>--%>
						</tbody>
					</table>
				</div>

			</div>
		</div>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceListAccessible('OP_EXCHANGE_WITHDRAW_CHECK,OP_EXCHANGE_WITHDRAW_OPERATE,OP_EXCHANGE_RECHARGE_CHECK,OP_EXCHANGE_RECHARGE_OPERATE,OP_ORDERS_CHECK,OP_GOODS_CHECK,OP_GOODS_OPERATE')}">

			<div class="col-md-3 new-col1">
				<ul class="panel quick-menu clearfix">

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
										 || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_CHECK')
										 || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_OPERATE')}">

						<li class="col-sm-6 home-list" style="position:relative;">
							<a href="<%=basePath%>normal/adminRechargeBlockchainOrderAction!list.action">
								<i class="fa fa-qrcode"></i> <span class="tit-w2">充值订单</span>
								<span class="recharge_blockchain_order_untreated_cout badge label-danger" style="display: none;position: absolute;bottom: 18px;right: 40px;">0</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
										 || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_CHECK')
										 || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_OPERATE')}">

						<li class="col-sm-6 home-list" style="position:relative;">
							<a href="<%=basePath%>normal/adminWithdrawAction!list.action">
								<i class="fa fa-credit-card"></i> <span class="tit-w2">提现订单</span>
								<span class="withdraw_order_untreated_cout badge label-danger" style="display: none;position: absolute;bottom: 18px;right: 40px;">0</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
										 || security.isResourceAccessible('OP_ORDERS_CHECK')
										 || security.isResourceAccessible('OP_ORDERS_OPERATE')}">

						<li class="col-sm-6 home-list" style="position:relative;">
							<a href="<%=basePath%>normal/adminKycAction!list.action">
								<i class="fa fa-trash"></i> <span class="tit-w2">店铺审核</span>
								<span class="kyc_untreated_cout badge label-danger" style="display: none;position: absolute;bottom: 18px;right: 40px;">0</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                 			|| security.isResourceAccessible('OP_CHAT_CHECK')
                                		 	|| security.isResourceAccessible('OP_CHAT_OPERATE')}">
						<li class="col-sm-6 home-list" style="position:relative;">
							<a href="<%=basePath%>chat/chatsList.action">
								<i class="fa fa-star-o"></i> <span class="tit-w2">虚拟卖家对话</span>
								<span class="chat_untreated_cout badge label-danger"  style="display: none;position: absolute;bottom: 18px;right: 40px;">0</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                						 || security.isResourceAccessible('OP_MALL_ORDER_CHECK') || security.isResourceAccessible('OP_MALL_ORDER_OPERATE')}">

						<li class="col-sm-6 home-list" style="position:relative;">
							<a href="<%=basePath%>mall/order/list.action">
								<i class="fa fa-star"></i> <span class="tit-w2">订单列表</span>
								<span class="goods_order_waitdeliver_count badge label-danger" style="display: none;position: absolute;bottom: 18px;right: 40px;">0</span>
							</a>
						</li>

					</c:if>
					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
               						  || security.isResourceAccessible('OP_MALL_RORDER_CHECK') || security.isResourceAccessible('OP_MALL_RORDER_OPERATE')}">
						<li class="col-sm-6 home-list" style="position:relative;">
							<a href="<%=basePath%>mall/order/refundList.action">
								<i class="fa fa-angle-down"></i> <span class="tit-w2">退货订单</span>
								<span class="goods_order_return_count badge label-danger" style="display: none;position: absolute;bottom: 18px;right: 40px;">0</span>
							</a>
						</li>

					</c:if>

				</ul>
			</div>

		</c:if>

	</div>
	<!-- END CONTAINER -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<%@ include file="include/footer.jsp"%>
</div>
<!-- End Content -->
<!-- //////////////////////////////////////////////////////////////////////////// -->
<form action="<%=basePath%>normal/adminIndexAction!viewNew.action" method="post"
	  id="queryForm">
	<input type="hidden" id="timeType" name="timeType" value="${day}"/>
</form>
<%@ include file="include/js.jsp"%>
<script src="path/to/numeral.min.js"></script>
<script>

	var timeType = '${timeType}';

	// 获取充值数据并填充到页面
	$.get('<%=basePath%>normal/adminIndexAction!viewHead.action', { timeType: timeType }, function(data) {
		var newRechargeAmount = data.newRechargeAmount !== 0 ? data.newRechargeAmount.toFixed(2) : "0.00";
		$('.viewHead').eq(0).text(newRechargeAmount);
		$('.viewHead').eq(1).text(data.newRechargeNum);

		var newWithdrawAmount = data.newWithdrawAmount !== 0 ? data.newWithdrawAmount.toFixed(2) : "0.00";
		$('.viewHead').eq(2).text(newWithdrawAmount);
		$('.viewHead').eq(3).text(data.newWithdrawNum);

		var newChargeBalanceAmount = data.newChargeBalanceAmount !== 0 ? data.newChargeBalanceAmount.toFixed(2) : "0.00";
		$('.viewHead').eq(4).text(newChargeBalanceAmount);

		var rechargeAmount = data.rechargeAmount !== 0 ? data.rechargeAmount.toFixed(2) : "0.00";
		$('.viewHead').eq(5).text(rechargeAmount);
		$('.viewHead').eq(6).text(data.rechargeNum);

		var withdrawAmount = data.withdrawAmount !== 0 ? data.withdrawAmount.toFixed(2) : "0.00";
		$('.viewHead').eq(7).text(withdrawAmount);
		$('.viewHead').eq(8).text(data.withdrawNum);

		var chargeBalanceAmount = data.chargeBalanceAmount !== 0 ? data.chargeBalanceAmount.toFixed(2) : "0.00";
		$('.viewHead').eq(9).text(chargeBalanceAmount);
		// ... 其他统计数据填充
	});
	$.get('<%=basePath%>normal/adminIndexAction!viewMiddle.action', { timeType: timeType }, function(data) {
		$('.viewMiddle').eq(0).text(data.orderNum);
		var totalSalesAmount = data.totalSalesAmount !== 0 ? data.totalSalesAmount.toFixed(2) : "0.00";
		$('.viewMiddle').eq(1).text(totalSalesAmount);
		var rebateAmount = data.rebateAmount !== 0 ? data.rebateAmount.toFixed(2) : "0.00";
		$('.viewMiddle').eq(2).text(rebateAmount);
		$('.viewMiddle').eq(3).text(data.registerNum);
		$('.viewMiddle').eq(4).text(data.registerSellerNum);
		$('.viewMiddle').eq(5).text(data.loginNum);
		$('.viewMiddle').eq(6).text(data.allUserNum);
		$('.viewMiddle').eq(7).text(data.allSellerNum);
		// ... 其他统计数据填充
	});

	// 获取销售TOP10数据并填充到表格
	$.get('<%=basePath%>normal/adminIndexAction!viewSellerTop.action', { timeType: timeType }, function(data) {
		const top10Sellers = $('.top10-sellers');
		$.each(data, function(index, seller) {
			const row = '<tr><td>' + seller.sellerName + '</td><td>' + seller.amount.toFixed(2) + '</td><td>' + seller.goodsCount + '</td></tr>';
			top10Sellers.append(row);
		});
	});

</script>

<script type="text/javascript">
	function goSumTipsAjaxUrl(targetUrl, data) {
		$.ajax({
			url : targetUrl,
			data : data,
			type : 'get',
			dataType : "json",
		    error: function(){
                setTimeout(function(){
                    goSumTipsAjaxUrl(targetUrl, data);
                }, 5000);
            },
            success : function(data) {
                setTimeout(function(){
                    goSumTipsAjaxUrl(targetUrl, data);
                }, 5000);
				var temp = data;
				initTipCountHandle();
				// 遍历tip
				if (temp.tipList.length > 0) {
					temp.tipList.forEach(function(ele) {
						countHandle($(ele.tip_dom_name), ele.tip_content_sum);
					});
				}
			}
		});
	}
    window.onload = function() {
        var data = {};
        goSumTipsAjaxUrl('<%=basePath%>normal/adminTipAction!getTips.action', data);
    }

	function initTipCountHandle() {
		// 业务
// 			countHandle($(".automonitor_approve_order_untreated_cout"), 0);
// 			countHandle($(".automonitor_threshold_order_untreated_cout"), 0);
// 			countHandle($(".contract_order_untreated_cout"), 0);
// 			countHandle($(".futures_order_untreated_cout"), 0);
// 			countHandle($(".automonitor_pledge_galaxy_order_untreated_cout"), 0);
		// 用户
		countHandle($(".kyc_untreated_cout"), 0);
		countHandle($(".kyc_high_level_untreated_cout"), 0);
// 			countHandle($(".user_safeword_apply_untreated_cout"), 0);
		// 财务
// 			countHandle($(".automonitor_withdraw_order_untreated_cout"),0);
		countHandle($(".withdraw_order_untreated_cout"), 0);
		countHandle($(".recharge_blockchain_order_untreated_cout"), 0);
	}

	function countHandle(ele, count) {
// 			if (ele == ".kyc_untreated_cout"
// 				|| ele == ".kyc_high_level_untreated_cout"
// 				|| ele == ".withdraw_order_untreated_cout"
// 				|| ele == ".recharge_blockchain_order_untreated_cout") {
		if (count == 0 || isNaN(count)) {
			$(ele).hide();
		} else {
			$(ele).show();
			$(ele).html(count)
		}
// 			}
	}

	$(".home-list").click(function(e){
		var title = $(this).find(".tit-w2").html();
		var href = $(this).find("a").attr("href");
		window.parent.addTab(title, href);
		e.preventDefault();
	})


	const buttons = document.getElementsByClassName("so-button");

	for (let i = 0; i < buttons.length; i++) {
		buttons[i].addEventListener("click", function () {
			const current = document.getElementsByClassName("active");
			if (current.length > 0) {
				current[0].className = current[0].className.replace(" active", "");
			}
			const dataAttr = this.getAttribute("data-attr");
			console.log(dataAttr);

			//这里写逻辑，dataAttr为当前点击的按钮的值 day / week / month
			$('#timeType').val(dataAttr);
			document.getElementById("queryForm").submit();
		});
	}

	//初始化执行一次

	$(function(){
		$('.tab a').filter(function() {
			var b = document.URL;
			var a = "<%=bases%>/mall/category/toUpdate.action?lang=${lang}&categoryId=${categoryId}";
			return this.href == "<%=bases%>/mall/goodAttr/value/toUpdate.action?lang=${lang}&categoryId=${categoryId}&attrId=${attrId}&attrValueId=${attrValueId}";  //获取当前页面的地址
		}).closest('li').addClass('active');  //给当前最靠近的li（其实是现在进入的li）添加‘active’样式

	})
</script>

</body>

</html>
