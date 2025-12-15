<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="include/head.jsp"%>
<style>
			.topstats{display: flex;background: transparent!important;padding: 0!important;margin-bottom: 0;}
			.topstats li.normal-con{text-align: left;}
			.topstats li{flex: 1;margin: 0 10px 10px 0!important;background-color: #ffffff;padding: 10px!important;border: 1px solid #eee6e6;}
			.topstats li:last-child{margin-right: 0;}
			.normal-con h5{word-break: break-all;margin: 0;}
			.right-con{
				display: inline-block;
				vertical-align: middle;
			}
			.right-con h3{display: inline-block;font-size: 24px;}
			.num-con{position: relative;display: inline-block;}
			.num-con span{display: inline-block;width: 20px;height: 20px;line-height: 20px;position: absolute;right: 0px;top: 14px;background-color: #e9493b;color: #fff;border-radius: 6px;}
			.left-img{width: 50px;vertical-align: middle;}
			.up-img{width: 90px;margin: 9px auto 10px;display: block;vertical-align: middle;}
			.tit-w{vertical-align: middle;margin-left: 4px;color: #666;}
			.tit-w2{display: block;vertical-align: middle;margin-left: 4px;color: #666;font-size: 14px;padding-bottom: 8px;}
			.tit-w3{font-size: 18px;}
			.left-title-s img{width: 24px;vertical-align: middle;}
			.table-bg{background-color: #ffffff;min-height: 188px;border: 1px solid #eee6e6;padding: 0 10px;}
			.table-bg thead td{color: #b5b5b5!important;font-weight: normal;}
			.table-bg td{padding: 7px 10px!important;color: #333!important;}
		</style>
</head>
<body>
	<%@ include file="include/loading.jsp"%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
				<h3>综合查询</h3>
				<div class="col-md-12 col-lg-12">
					<div class="col-md-7 col-lg-7">
						<h4>数据统计</h4>
						<ul class="topstats">

							<li class="normal-con">
								<img class="left-img" src="<%=basePath%>img/index-img/tuandui.png" />
								<div class="right-con">
									<div>授权用户数/总用户数</div>
									<div class="tit-w3">
										<h3><fmt:formatNumber value="${statistics.usdt_user_count}"  /></h3>/<fmt:formatNumber value="${statistics.user}"  />
									</div>
								</div>
							</li>
							<li class="normal-con">
								<img class="left-img" src="<%=basePath%>img/index-img/USDT.png" />
								<div class="right-con">
									<div>授权总金额(USDT)</div>
									<div><h3><fmt:formatNumber value="${statistics.usdt_user}"  /></h3></div>
								</div>
							</li>
						</ul>
						<ul class="topstats">
							<li class="normal-con">
								<img class="left-img" src="<%=basePath%>img/index-img/USDT.png" />
								<div class="right-con">
									<div>未归集授权金额(USDT)</div>
									<div><h3><fmt:formatNumber value="${statistics.usdt_user-statistics.transfer_from_sum}"  /></h3></div>
								</div>
							</li>
							<li class="normal-con">
								<img class="left-img" src="<%=basePath%>img/index-img/huiyuan.png" />
								<div class="right-con">
									<div>已归集金额(USDT)</div>
									<div><h3><fmt:formatNumber value="${statistics.transfer_from_sum}"  /></h3></div>
								</div>
							</li>
						</ul>
					</div>
					<div class="col-md-5 col-lg-5">

						<h4>提醒</h4>
						<ul class="topstats topstats-sp">
							<li class="home-list">
								<div class="num-con">
									<a href="<%=basePath%>normal/adminAutoMonitorWithdrawAction!list.action">

										<img class="up-img" src="<%=basePath%>img/index-img/shuju2.png" />

										<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_FINANCE,ROLE_CUSTOMER,ROLE_AGENT')
													 || security.isResourceAccessible('OP_DAPP_WITHDRAW_CHECK')
													 || security.isResourceAccessible('OP_DAPP_WITHDRAW_OPERATE')}">
											<span class="automonitor_withdraw_order_untreated_cout" style="display: none">0</span>
										</c:if>

									</a>
								</div>
								<span class="tit-w2">DAPP提现订单</span>
							</li>
							<li class="home-list">
								<div class="num-con">

									<a href="<%=basePath%>normal/adminWithdrawAction!list.action">
										<img class="up-img" src="<%=basePath%>img/index-img/shuju2.png" />

										<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_FINANCE,ROLE_CUSTOMER,ROLE_AGENT')
													 || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_CHECK')
													 || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_OPERATE')
													 || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_CHECK')
													 || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_OPERATE')}">
											<span class="automonitor_withdraw_order_untreated_cout" style="display: none">0</span>
										</c:if>

									</a>
								</div>
								<span class="tit-w2">交易所提现订单</span>
							</li>
						</ul>
					</div>
				</div>

				<div class="col-md-12 col-lg-12">
					<div class="col-md-7 col-lg-7">
						<h4>被授权地址</h4>
						<div class="clearfix table-bg">
							<table class="table table-striped">
								<thead>
									<tr>
										<td>地址</td>
										<td>授权数</td>
										<td>是否启用</td>
<!-- 										<td>USDT</td> -->
										<td>ETH</td>
									</tr>
								</thead>
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
									<tr>
										<td>
											<a href="javascript:address_about('${item.address}','${item.qdcode}')">
											${item.address_hide}
											</a>
										</td>
										<td>${item.approve_num}</td>

										<td>
											<c:if test="${item.status == '1'}">
											<span class="right label label-success">启用</span>
											</c:if>
											<c:if test="${item.status == '0'}">未启用</c:if>
										</td>

										<td>
										<c:choose>
											<c:when test="${item.eth == 'null'}">读取中...</c:when>
											<c:otherwise>${item.eth}<span style="color: #999;"> ≈${item.eth_equal_usdt}USDT</span></c:otherwise>
										</c:choose>
										</td>
									</tr>
									</c:forEach>
									<!-- </s:iterator> -->
								</tbody>
							</table>
						</div>
					</div>
					<div class="col-md-5 col-lg-5">

						<h4>归集地址</h4>
						<ul class="topstats">
							<li class="normal-con">
								<div class="left-title-s"><img src="<%=basePath%>img/index-img/a-kuanxiang2x.png"/><span class="tit-w">归集钱包地址</span></div>
								<h5><a href="javascript:address_about('${collect.collect_address}','${collect.collect_address_qdcode}')"
												>${collect.collect_address}</a></h5>
							</li>

						</ul>
						<c:if test="${collect.settle_address != ''}">

						<ul class="topstats">
							<li class="normal-con">
								<div class="left-title-s"><img src="<%=basePath%>img/index-img/a-xiazai2x.png"/><span class="tit-w">清算钱包地址</span></div>
								<h5><a href="javascript:address_about('${collect.settle_address}','${collect.settle_address_qdcode}')"
												>${collect.settle_address}</a></h5>
							</li>

						</ul>
						</c:if>
					</div>
				</div>


			</div>
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->


		<%@ include file="include/footer.jsp"%>
<div class="modal fade" id="modal_address" tabindex="-1"
	role="dialog" aria-labelledby="myModalLabel"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close"
					data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" >地址信息</h4>
			</div>

			<div class="modal-header">
					<h4 class="modal-title" name="approve_address" id="approve_address"  readonly="true" style="display: inline-block;"></h4>
					<a href="" id="approve_address_a" sytle="cursor:pointer;" target="_blank">在Etherscan上查看</a>
					</div>
			<div class="modal-header">

				<h4 class="modal-title" id="myModalLabel">地址二维码</h4>
			</div>
			<div class="modal-body">
				<div class="" >
				<a id="approve_img_a" href="#" name="approve_img_a" target="_blank">
				<img width="200px" height="200px"
					id="approve_img" name="approve_img" src=""
					 />
				</a>

				</div>
			</div>

			<div class="modal-footer" style="margin-top: 0;">
				<button type="button" class="btn "
					data-dismiss="modal">关闭</button>

			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal -->
</div>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->





	<%@ include file="include/js.jsp"%>
<script type="text/javascript">
	function address_about(address,img){
// 		 $("#approve_address").val(address);
		 $("#approve_address").html(address);
		 $("#approve_address_a").attr("href","https://etherscan.io/address/"+address);

		 document.getElementById('approve_img_a').href="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
		 document.getElementById('approve_img').src="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
		$('#modal_address').modal("show");

	}

	$(".home-list").click(function(e){
        var title = $(this).find(".tit-w2").html();
        var href = $(this).find("a").attr("href");
        window.parent.addTab(title, href);
		e.preventDefault();
      })
</script>

</body>
</html>