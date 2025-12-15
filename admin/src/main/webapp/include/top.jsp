<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<div id="top" class="clearfix">

	<div class="autoplay-btn">
		<div>请点击打开消息声音提醒</div>
	</div>

<style>
.autoplay-btn {
	display: none;
	position: fixed;
	width: 100%;
	height: 100%;
	z-index: 1000;
	top: 0;
	left: 0;
	background-color: rgba(0, 0, 0, 0.2)
}
.autoplay-btn div {
	color: #0798ff;
	margin: 200px auto 0;
	width: 200px;
	height: 50px;
	line-height: 50px;
	text-align: center;
	cursor: pointer;
	background: #fff;
	border-radius: 6px;
	text-decoration: underline;
}
</style>

	<!-- Start App Logo -->
	<div class="applogo">
		<a href="<%=basePath%>normal/indexAction!view.action" class="logo">
			<%@ include file="sitename.jsp"%>
		</a>
	</div>
	<!-- End App Logo -->

	<!-- Start Sidebar Show Hide Button -->
	<a href="#" class="sidebar-open-button"><i class="fa fa-bars"></i></a>
	<a href="#" class="sidebar-open-button-mobile"><i class="fa fa-bars"></i></a>
	<!-- End Sidebar Show Hide Button  -->

	<ul style="display: none">
		<li>
			<span class="business_untreated_count badge label-danger" style="display: none">0</span>
			<span class="user_untreated_count badge label-danger" style="display: none">0</span>
			<span class="money_untreated_count badge label-danger" style="display: none">0</span>

			<span class="automonitor_approve_order_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="automonitor_threshold_order_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="contract_order_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="futures_order_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="automonitor_pledge_galaxy_order_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="kyc_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="credit_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="complaint_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="activity_lottery_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="marketing_activity_lottery_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="kyc_high_level_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="user_safeword_apply_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="automonitor_withdraw_order_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="withdraw_order_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="recharge_blockchain_order_untreated_cout badge label-danger" style="display: none">0</span>
			<span class="goods_order_return_count badge label-danger" style="display: none">0</span>
			<span class="goods_order_waitdeliver_count badge label-danger" style="display: none">0</span>
		</li>
	</ul>

	<!-- Start Top Menu -->
	<ul class="topmenu" style="display: block">

	<!-- dapp+交易所 菜单 ######################################################################################################## -->

		<c:choose>
			<c:when test="${security.isRolesAccessible('ROLE_AGENT')}">
				<li class="dropdown-parent">
					<a href="<%=basePath%>normal/adminUserAction!list.action">
						<i class="fa falist fa-file-text-o"></i>
						<span class="sp-title">用户基础管理</span>
					</a>
				</li>
			</c:when>
			<c:otherwise>
				<li class="dropdown-parent">
					<a href="<%=basePath%>brush/index/list.action">
						<i class="fa falist fa-home"></i>
						<span class="sp-title">综合查询</span>
					</a>
				</li>
			</c:otherwise>
	    </c:choose>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
					|| security.isResourceListAccessible('OP_PLATFORM_CHECK,OP_PLATFORM_OPERATE,OP_AUTHORIZE_CHECK,OP_AUTHORIZE_OPERATE,OP_COLLECT_CHECK,OP_COLLECT_OPERATE,OP_TIP_CHECK,OP_PLEDGE_CHECK,OP_PLEDGE_OPERATE,OP_ACTIVITY_CHECK,OP_ACTIVITY_OPERATE,OP_FOREVER_CONTRACT_ORDER_CHECK,OP_FOREVER_CONTRACT_ORDER_OPERATE,OP_FOREVER_CONTRACT_APPLY_ORDER_CHECK,OP_FOREVER_CONTRACT_APPLY_ORDER_OPERATE,OP_FUTURES_CONTRACT_ORDER_CHECK,OP_FUTURES_CONTRACT_ORDER_OPERATE')
					|| security.isResourceListAccessible('OP_PROFIT_AND_LOSS_CONFIG_CHECK,OP_PROFIT_AND_LOSS_CONFIG_OPERATE,OP_EXCHANGE_APPLY_ORDER_CHECK,OP_EXCHANGE_APPLY_ORDER_OPERATE,OP_FINANCE_ORDER_CHECK,OP_FINANCE_ORDER_OPERATE,OP_MINER_ORDER_CHECK,OP_MINER_ORDER_OPERATE,OP_PLEDGE_GALAXY_ORDER_CHECK,OP_PLEDGE_GALAXY_ORDER_OPERATE,OP_PLEDGE_GALAXY_PROFIT_CHECK,OP_PLEDGE_GALAXY_PROFIT_OPERATE')
					|| security.isResourceListAccessible('OP_GOODS_CHECK,OP_GOODS_OPERATE,OP_VIP_CHECK,OP_VIP_OPERATE')
					}">

			<li class="dropdown">
				<a href="#" data-toggle="dropdown" class="dropdown-toggle">业务
					<span class="business_untreated_count badge label-danger" style="display: none">0</span>
					<span class="caret"></span>
				</a>
				<ul class="dropdown-menu dropdown-menu-list">

<%--					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
<%--								|| security.isResourceListAccessible('OP_PLATFORM_CHECK,OP_PLATFORM_OPERATE,OP_AUTHORIZE_CHECK,OP_AUTHORIZE_OPERATE,OP_COLLECT_CHECK,OP_COLLECT_OPERATE,OP_TIP_CHECK,OP_PLEDGE_CHECK,OP_PLEDGE_OPERATE,OP_ACTIVITY_CHECK,OP_ACTIVITY_OPERATE,OP_PLEDGE_GALAXY_ORDER_CHECK,OP_PLEDGE_GALAXY_ORDER_OPERATE,OP_PLEDGE_GALAXY_PROFIT_CHECK,OP_PLEDGE_GALAXY_PROFIT_OPERATE')--%>
<%--								|| security.isResourceListAccessible('OP_GOODS_CHECK,OP_GOODS_OPERATE,OP_VIP_CHECK,OP_VIP_OPERATE')}">--%>
<%--								--%>
<%--						<li role="presentation" class="dropdown-header">连单</li>--%>
<%--			--%>
<%--					</c:if>--%>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_PLATFORM_CHECK')
								 || security.isResourceAccessible('OP_PLATFORM_OPERATE')}">

						<li>
							<a href="<%=basePath%>mall/category/list.action">
								<i class="fa falist fa-laptop"></i>
								<span class="sp-title">商品分类</span>
								<span class="automonitor_approve_order_untreated_cout badge label-danger" style="display: none">0</span>
							</a>
						</li>

					</c:if>
					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_GOODS_CHECK')
								 || security.isResourceAccessible('OP_GOODS_OPERATE')}">

						<li>
							<a href="<%=dmUrl%>/download/#/commodity-library?url=<%=adminUrl%>">
								<i class="fa falist fa-columns"></i>
								<span class="sp-title">商品管理</span>
								<span class="automonitor_approve_order_untreated_cout badge label-danger" style="display: none">0</span>
							</a>
						</li>

					</c:if>
					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
												 || security.isResourceAccessible('OP_GOODS_CHECK')
												 || security.isResourceAccessible('OP_GOODS_OPERATE')}">

					<!--	<li>
							<a href="<%=basePath%>mall/ext/goods/gather.action">
								<i class="fa falist fa-columns"></i>
								<span class="sp-title">商品采集</span>
								<span class="automonitor_approve_order_untreated_cout badge label-danger" style="display: none">0</span>
							</a>
						</li>
                        -->
					</c:if>


				</ul>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_AGENT')
					 || security.isResourceListAccessible('OP_AGENT_CHECK,OP_AGENT_OPERATE,OP_USER_RECOM_CHECK,OP_USER_RECOM_OPERATE,OP_USER_KYC_CHECK,OP_USER_KYC_OPERATE,OP_USER_KYC_HIGH_LEVEL_CHECK,OP_USER_KYC_HIGH_LEVEL_OPERATE,OP_USER_SAFEWORD_APPLY_CHECK,OP_USER_SAFEWORD_APPLY_OPERATE,OP_USER_CHECK,OP_USER_OPERATE,OP_DAPP_USER_CHECK,OP_DAPP_USER_OPERATE,OP_EXCHANGE_USER_CHECK,OP_EXCHANGE_USER_OPERATE')}">

			<li class="dropdown">
				<a href="#" data-toggle="dropdown" class="dropdown-toggle">用户
					<span class="user_untreated_count badge label-danger" style="display: none">0</span>
					<span class="caret"></span>
				</a>
				<ul class="dropdown-menu dropdown-menu-list">

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_AGENT_CHECK')
								 || security.isResourceAccessible('OP_AGENT_OPERATE')}">

						<li>
							<a href="<%=basePath%>normal/adminAgentAction!list.action">
								<i class="fa falist fa-group"></i>
								<span class="sp-title">代理商</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_USER_RECOM_CHECK')
								 || security.isResourceAccessible('OP_USER_RECOM_OPERATE')}">

						<li>
							<a href="<%=basePath%>normal/adminUserRecomAction!list.action">
								<i class="fa falist fa-user"></i>
								<span class="sp-title">推荐关系</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_USER_KYC_CHECK')
								 || security.isResourceAccessible('OP_USER_KYC_OPERATE')}">

						<li>
							<a href="<%=basePath%>normal/adminKycAction!list.action">
								<i class="fa falist fa-credit-card"></i>
								<span class="sp-title">店铺审核</span>
								<span class="kyc_untreated_cout badge label-danger" style="display: none">0</span>
							</a>
						</li>

					</c:if>

<%--					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
<%--								 || security.isResourceAccessible('OP_USER_KYC_HIGH_LEVEL_CHECK')--%>
<%--								 || security.isResourceAccessible('OP_USER_KYC_HIGH_LEVEL_OPERATE')}">--%>

<%--						<li>--%>
<%--							<a href="<%=basePath%>normal/adminKycHighLevelAction!list.action">--%>
<%--								<i class="fa falist fa-credit-card"></i>--%>
<%--								<span class="sp-title">用户高级认证</span>--%>
<%--								<span class="kyc_high_level_untreated_cout badge label-danger" style="display: none">0</span>--%>
<%--							</a>--%>
<%--						</li>--%>

<%--					</c:if>--%>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_USER_SAFEWORD_APPLY_CHECK')
								 || security.isResourceAccessible('OP_USER_SAFEWORD_APPLY_OPERATE')}">

						<li>
							<a href="<%=basePath%>normal/adminUserSafewordApplyAction!list.action">
								<i class="fa falist fa-star-o"></i>
								<span class="sp-title">人工重置管理</span>
								<span class="user_safeword_apply_untreated_cout badge label-danger" style="display: none">0</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_AGENT')
								 || security.isResourceAccessible('OP_USER_CHECK')
								 || security.isResourceAccessible('OP_USER_OPERATE')}">

						<li>
							<a href="<%=basePath%>normal/adminUserAction!list.action">
								<i class="fa falist fa-file-text-o"></i>
								<span class="sp-title">用户基础管理</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								|| security.isResourceListAccessible('OP_DAPP_USER_CHECK,OP_DAPP_USER_OPERATE')}">

						<li class="divider"></li>


					</c:if>

				</ul>
			</li>

		</c:if>
		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                     || security.isResourceListAccessible('OP_EVENTS_CHECK,OP_PRIZEMANAGEMENT_CHECK,OP_LOTTERYRECEIVE_CHECK,OP_LOTTERYRECEIVE_CHECK')}">

			<li class="dropdown">
				<a href="#" data-toggle="dropdown" class="dropdown-toggle">活动
					<span class="marketing_activity_lottery_untreated_cout badge label-danger" style="display: none">0</span>
					<span class="caret"></span>
				</a>
				<ul class="dropdown-menu dropdown-menu-list">

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                         || security.isResourceAccessible('OP_EVENTS_CHECK')}">

						<li>
							<a href="<%=dmUrl%>/download/#/marketing/EventsList?url=<%=adminUrl%>">
								<i class="fa falist fa-group"></i>
								<span class="sp-title">活动列表</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                         || security.isResourceAccessible('OP_PRIZEMANAGEMENT_CHECK')}">
						<li>
							<a href="<%=dmUrl%>/download/#/marketing/PrizeManagement?url=<%=adminUrl%>">
								<i class="fa falist fa-user"></i>
								<span class="sp-title">奖品管理</span>
							</a>
						</li>
					</c:if>
					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                         || security.isResourceAccessible('OP_LOTTERYRECEIVE_CHECK')}">

						<li>
							<a href="<%=dmUrl%>/download/#/marketing/GetRecords?url=<%=adminUrl%>">
								<i class="fa falist fa-credit-card"></i>
								<span class="sp-title">领奖记录</span>
								<span class="marketing_activity_lottery_untreated_cout badge label-danger" style="display: none">0</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
                                         || security.isResourceAccessible('OP_LOTTERYRECORD_CHECK')}">

						<li>
							<a href="<%=dmUrl%>/download/#/marketing/WinningRecord?url=<%=adminUrl%>">
								<i class="fa falist fa-star-o"></i>
								<span class="sp-title">中奖记录</span>
							</a>
						</li>

					</c:if>

				</ul>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
					 || security.isResourceListAccessible('OP_DAPP_WITHDRAW_CHECK,OP_DAPP_WITHDRAW_OPERATE,OP_EXCHANGE_WITHDRAW_CHECK,OP_EXCHANGE_WITHDRAW_OPERATE,OP_EXCHANGE_RECHARGE_CHECK,OP_EXCHANGE_RECHARGE_OPERATE')}">

			<li class="dropdown">
				<a href="#" data-toggle="dropdown" class="dropdown-toggle">财务
					<span class="money_untreated_count badge label-danger" style="display: none">0</span>
					<span class="caret"></span>
				</a>
				<ul class="dropdown-menu dropdown-menu-list">


					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceListAccessible('OP_EXCHANGE_WITHDRAW_CHECK,OP_EXCHANGE_WITHDRAW_OPERATE,OP_EXCHANGE_RECHARGE_CHECK,OP_EXCHANGE_RECHARGE_OPERATE')}">


					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_CHECK')
								 || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_OPERATE')}">

						<li>
							<a href="<%=basePath%>normal/adminWithdrawAction!list.action">
								<i class="fa falist fa-credit-card"></i>
								<span class="sp-title">提现订单</span>
								<span class="withdraw_order_untreated_cout badge label-danger" style="display: none">0</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_CHECK')
								 || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_OPERATE')}">

						<li>
							<a href="<%=basePath%>normal/adminRechargeBlockchainOrderAction!list.action">
								<i class="fa falist fa-credit-card"></i>
								<span class="sp-title">充值订单</span>
								<span class="recharge_blockchain_order_untreated_cout badge label-danger" style="display: none">0</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">

						<li>
							<a href="<%=basePath%>normal/adminChannelBlockchainAction!list.action">
								<i class="fa fa-bars falist"></i>
								<span class="sp-title">系统充值地址维护</span>
							</a>
						</li>

					</c:if>
					<!--
					<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">

						<li>
							<a href="<%=basePath%>normal/adminChannelBlockchainAction!personList.action">
								<i class="fa fa-bars falist"></i>
								<span class="sp-title">个人充值地址维护</span>
							</a>
						</li>
                    </c:if>
                    -->
				</ul>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
					 || security.isResourceListAccessible('OP_DAPP_ALL_STATISTICS_CHECK,OP_DAPP_AGENT_ALL_STATISTICS_CHECK,OP_EXCHANGE_ALL_STATISTICS_CHECK,OP_EXCHANGE_AGENT_ALL_STATISTICS_CHECK,OP_EXCHANGE_USER_ALL_STATISTICS_CHECK')}">

			<li class="dropdown">
				<a href="#" data-toggle="dropdown" class="dropdown-toggle">报表<span class="caret"></span></a>
				<ul class="dropdown-menu dropdown-menu-list">


					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_EXCHANGE_ALL_STATISTICS_CHECK')}">

						<li>
							<a href="<%=basePath%>normal/exchangeAdminAllStatisticsAction!list.action">
								<i class="fa falist fa-pie-chart"></i>
								<span class="sp-title">运营数据</span>
							</a>
						</li>
					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_EXCHANGE_AGENT_ALL_STATISTICS_CHECK')}">

						<li>
							<a href="<%=basePath%>normal/exchangeAdminAgentAllStatisticsAction!list.action">
								<i class="fa falist fa-sitemap"></i>
								<span class="sp-title">代理商充提报表</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_EXCHANGE_USER_ALL_STATISTICS_CHECK')}">
						<li>
							<a href="<%=basePath%>normal/adminUserAllStatisticsAction!exchangeList.action">
								<i class="fa falist fa-align-left"></i>
								<span class="sp-title">用户报表</span>
							</a>
						</li>
					</c:if>
				</ul>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
					 || security.isResourceListAccessible('OP_POOL_DATA_CHECK,OP_POOL_DATA_OPERATE,OP_MINING_CONFIG_CHECK,OP_MINING_CONFIG_OPERATE,OP_MARKET_CHECK,OP_MARKET_OPERATE,OP_FOREVER_CONTRACT_CHECK,OP_FOREVER_CONTRACT_OPERATE,OP_EXCHANGE_RATE_CHECK,OP_EXCHANGE_RATE_OPERATE,OP_ITEM_CHECK,OP_ITEM_OPERATE')
					 || security.isResourceListAccessible('OP_PLEDGE_CONFIG_CHECK,OP_IPMENU_CHECK,OP_IPMENU_OPERATE,OP_PLEDGE_CONFIG_OPERATE,OP_ACTIVITY_MANAGE_CHECK,OP_ACTIVITY_MANAGE_OPERATE,OP_FUTURES_CONTRACT_CHECK,OP_FUTURES_CONTRACT_OPERATE,OP_FINANCE_CHECK,OP_FINANCE_OPERATE,OP_MINER_CHECK,OP_MINER_OPERATE,OP_CMS_CHECK,OP_CMS_OPERATE,OP_NEWS_CHECK,OP_NEWS_OPERATE')}">
			<li class="dropdown link">
				<a href="#" data-toggle="dropdown" class="dropdown-toggle">配置<span class="caret"></span></a>
				<ul class="dropdown-menu dropdown-menu-list dropdown-menu-right">



<%--					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
<%--								 || security.isResourceAccessible('OP_ACTIVITY_MANAGE_CHECK')--%>
<%--								 || security.isResourceAccessible('OP_ACTIVITY_MANAGE_OPERATE')}">--%>

<%--						<li>--%>
<%--							<a href="<%=basePath%>normal/adminActivityAction!list.action">--%>
<%--								<i class="fa falist fa-globe"></i>--%>
<%--								<span class="sp-title">全局活动管理</span>--%>
<%--							</a>--%>
<%--						</li>--%>

<%--					</c:if>--%>



					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_EXCHANGE_RATE_CHECK')
								 || security.isResourceAccessible('OP_EXCHANGE_RATE_OPERATE')}">

						<li>
						    <a href="<%=basePath%>normal/adminExchangeRateAction!list.action">
						    	<i class="fa falist fa-h-square"></i>
								<span class="sp-title">汇率配置</span>
						    </a>
						</li>

					</c:if>


					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_NEWS_CHECK')
								 || security.isResourceAccessible('OP_NEWS_OPERATE')}">

						<li>
							<a href="<%=basePath%>normal/adminNewsAction!list.action">
								<i class="fa falist fa-book"></i>
								<span class="sp-title">新闻管理</span>
							</a>
						</li>

					</c:if>

<%--					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
<%--							 || security.isResourceAccessible('OP_LOANCOFIG_CHECK')}">--%>

						<li>
							<a href="<%=basePath%>mall/banner/list.action?type=pc">
								<i class="fa falist fa-book"></i>
								<span class="sp-title">首页轮播</span>
							</a>

						</li>

<%--					</c:if>--%>

<%--					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
<%--								 || security.isResourceAccessible('OP_NEWS_CHECK')--%>
<%--								 || security.isResourceAccessible('OP_NEWS_OPERATE')}">--%>

<%--						<li>--%>
<%--							<a href="<%=basePath%>invest/expert/list.action">--%>
<%--								<i class="fa falist fa-book"></i>--%>
<%--								<span class="sp-title">专家介绍</span>--%>
<%--							</a>--%>
<%--						</li>--%>

<%--					</c:if>--%>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_CMS_CHECK')
								 || security.isResourceAccessible('OP_CMS_OPERATE')}">

						<li>
							<a href="<%=basePath%>normal/adminCmsAction!list.action">
								<i class="fa falist fa-book"></i>
								<span class="sp-title">系统公告</span>
							</a>
						</li>

					</c:if>
					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_IPMENU_CHECK')
								 || security.isResourceAccessible('OP_IPMENU_OPERATE')}">
						<li>
							<a href="<%=basePath%>normal/adminIpMenuAction!list.action">
								<i class="fa falist fa-map-marker"></i>
								<span class="sp-title">ip黑名单</span>
							</a>
						</li>

					</c:if>

				</ul>
			</li>

		</c:if>



		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_MAINTAINER')
			 		|| security.isResourceListAccessible('OP_SYSPARA_CHECK,OP_SYSPARA_OPERATE,OP_CUSTOMER_CHECK,OP_MONEY_LOG_CHECK,OP_LOG_CHECK,OP_DAPP_LOG_CHECK,OP_CODE_LOG_CHECK')}">

			<li class="dropdown">
				<a href="#" data-toggle="dropdown" class="dropdown-toggle">系统<span class="caret"></span></a>
				<ul class="dropdown-menu dropdown-menu-list">

					<%-- <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_MAINTAINER')}"> --%>
					<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">

						<li>
							<a href="<%=basePath%>normal/adminSysparaAction!list.action">
								<i class="fa fa-list-alt falist"></i>
								<span class="sp-title">系统参数（ROOT）</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')}">

						<li>
							<a href="<%=basePath%>normal/adminSysparaAction!listAdmin.action">
								<i class="fa fa-list-alt falist"></i>
								<span class="sp-title">系统参数（ADMIN）</span>
							</a>
						</li>

					</c:if>

					<%-- <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_MAINTAINER')
								 || security.isResourceAccessible('OP_SYSPARA_CHECK')
								 || security.isResourceAccessible('OP_SYSPARA_OPERATE')}"> --%>
					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_SYSPARA_CHECK')
								 || security.isResourceAccessible('OP_SYSPARA_OPERATE')}">

						<li>
							<a href="<%=basePath%>normal/adminSysparaAction!toUpdate.action">
								<i class="fa fa-list-alt falist"></i>
								<span class="sp-title">系统参数</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
						 		|| security.isResourceAccessible('OP_CUSTOMER_CHECK')}">

						<li>
							<a href="<%=basePath%>normal/adminCustomerAction!list.action">
								<i class="fa falist fa-user"></i>
								<span class="sp-title">客服管理</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
						 		|| security.isResourceAccessible('OP_CUSTOMER_CHECK')}">

						<li>
							<a href="<%=basePath%>mall/subscribe/list.action">
								<i class="fa falist fa-user"></i>
								<span class="sp-title">订阅</span>
							</a>
						</li>

					</c:if>

					<li class="divider"></li>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')}">

						<li role="presentation" class="dropdown-header">系统用户</li>

						<li>
							<a href="<%=basePath%>normal/adminRoleAuthorityAction!list.action">
								<i class="fa falist fa-child"></i>
								<span class="sp-title">角色管理</span>
							</a>
						</li>

						<li>
							<a href="<%=basePath%>normal/adminSystemUserAction!list.action">
								<i class="fa fa-bullhorn falist"></i>
								<span class="sp-title">系统用户管理</span>
							</a>
						</li>

						<li class="divider"></li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceListAccessible('OP_MONEY_LOG_CHECK,OP_LOG_CHECK,OP_DAPP_LOG_CHECK,OP_CODE_LOG_CHECK')}">

						<li role="presentation" class="dropdown-header">日志</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_MONEY_LOG_CHECK')}">

						<li>
							<a href="<%=basePath%>normal/adminMoneyLogAction!list.action?freeze=0">
								<i class="fa falist fa-inbox"></i>
								<span class="sp-title">账变记录</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_LOG_CHECK')}">

						<li>
							<a href="<%=basePath%>normal/adminLogAction!list.action">
								<i class="fa falist fa-file-o"></i>
								<span class="sp-title">操作日志</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_DAPP_LOG_CHECK')}">

						<li>
							<a href="<%=basePath%>normal/adminAutoMonitorDAppLogAction!list.action">
								<i class="fa falist fa-asterisk"></i>
								<span class="sp-title">前端日志</span>
							</a>
						</li>

					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 || security.isResourceAccessible('OP_CODE_LOG_CHECK')}">
						<li>
						   <a href="<%=basePath%>normal/adminCodeLogAction!list.action">
							   <i class="fa falist fa-envelope"></i>
							   <span class="sp-title">验证码发送日志</span>
						   </a>
						</li>
					</c:if>

				</ul>
			</li>

		</c:if>



	</ul>
	<!-- End Top Menu -->

	<!-- Start Top Right -->
	<ul class="top-right">

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_CUSTOMER')}">

			<li class="link">
				<a href="#" class="notifications" onclick="chat();">
					<i class="fa fa fa-comments-o" style="font-size: 15px;"></i>
					<span class="badge label-danger" style="margin-left: 5px;" id="online_chat_unread"></span>
				</a>
			</li>

		</c:if>

		<li class="dropdown link">

		<a href="#" data-toggle="dropdown" class="dropdown-toggle profilebox">

			   <c:if test="${security.isRolesAccessible('ROLE_CUSTOMER')}">

					<c:if test="${security.customerOnlineState() == 1}">
						<span style="display: inline-block; width: 9px; height: 9px; border: 1px solid #fff; background: #12f381; border-radius: 50%;"></span>
					</c:if>
					<c:if test="${security.customerOnlineState() == 0}">
						<span style="display: inline-block; width: 9px; height: 9px; border: 1px solid #fff; background: #828b87; border-radius: 50%;"></span>
					</c:if>

				</c:if>

				<b>${username_login}</b>
				<span class="caret"></span>

		 </a>

			<ul class="dropdown-menu dropdown-menu-list dropdown-menu-right">

				<c:if test="${security.isRolesAccessible('ROLE_CUSTOMER')}">

					<li>
						<a href="<%=basePath%>normal/adminPersonalCustomerAction!personalCustomer.action">
							<i class="fa falist fa-user"></i><span class="sp-title">客服个人中心</span></a>
					</li>

				</c:if>

				<li role="presentation" class="dropdown-header">密码</li>

				<li><a href="<%=basePath%>normal/adminPasswordChangeAction!view.action"><i
						class="fa falist fa-wrench"></i> <span class="sp-title">修改登录密码</span></a></li>

				<li><a href="<%=basePath%>normal/adminPasswordChangeAction!viewSafeword.action"><i
						class="fa falist fa-lock"></i><span class="sp-title">修改资金密码</span></a></li>

				<c:if test="${security.getUsername_login() != 'admin'}">
					<li><a
						href="<%=basePath%>normal/adminGoogleAuthAction!toUpdateLoginGoogleAuth.action"><i
							class="fa falist fa-google-plus-square"></i><span
							class="sp-title">谷歌验证器</span></a></li>
					<li class="divider"></li>
				</c:if>

				<li>
					<a href="<%=basePath%>public/logout.action"><i class="fa falist fa-power-off"></i><span class="sp-title">退出登录</span></a>
				</li>

			</ul>

		</li>

	</ul>
	<!-- End Top Right -->

</div>

<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_CUSTOMER')}">

	<style>
	.chat_close {
		position: fixed;
		right: 20px;
		top: 44px;
		z-index: 899;
		padding: 2px 7px;
		border-radius: 5px;
		cursor: pointer;
		color: black;
	}
	.chat_close:hover {
	 	color: rgba(0, 0, 0, 0.6);
		background: rgba(0, 0, 0, 0.09);
	}
	</style>

	<div class="panel panel-default"
		style="display: none; margin-top: 40px; z-index: 898; position: fixed; width: 100%; height: 100%; padding: 0;"
		id="chat_panel">
		<div class="icon expand-tool chat_close" onMouseDown="chat();">
			<i class="fa fa-close"></i>
		</div>
		<div class="panel-body panel-body-chat"
			style="height: calc(100% - 45px);">
			<%@ include file="../online_chat_new.jsp"%>
		</div>
	</div>

</c:if>

<div id="tip_alert" style=""></div>

<input type="hidden" id="top_ajax_url" value="<%=basePath%>normal/adminIndexAction!getUntreatedNum.action" />

<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_CUSTOMER')}">

	<script type="text/javascript">
		function chat(usercode) {
			if($("#chat_panel").is(":hidden")) {
				$("#chat_panel").show();
				getChatToken();//获取token
				$("#isScroll").val(0);//消息置底，开始轮训
				if (typeof(usercode) != "undefined" && usercode!=null){
					searchMsg(usercode)
				}
				chat_interval();//开启聊天获取
				$("body").css("overflow","hidden");
				$("#chat_panel").find(".expand-tool").click();
			} else {
				$("#chat_panel").hide();
				$("#isScroll").val(1);
				chat_interval_clear();
				$("body").css("overflow","auto");
				$("#chat_panel").find(".expand-tool").click();
			}
	// 			$("#chat_panel").toggle();
		}
		function openNewChat() {//打开客服面板，展示最新消息
			if($("#chat_panel").is(":hidden")) {
				$("#chat_panel").show();
				getChatToken();//获取token
				$("#isScroll").val(0);//消息置底，开始轮训
				chat_interval();//开启聊天获取
				$("body").css("overflow","hidden");
				$("#chat_panel").find(".expand-tool").click();
			} else {
				$(".msg-list").find(".item-li")[0].click();
			}
		}

		//setTimeout(function() {
		//	var data = {token:$("#chat_token").val()};
		//	goChatAjaxUrl('<%=basePath%>public/newAdminOnlineChatAction!unread.action', data);
		//}, 5000);

		function goChatAjaxUrl(targetUrl, data) {
			$.ajax({
				url : targetUrl,
				data : data,
				type : 'get',
				dataType : "json",
                error: function(){
                    setTimeout(function(){
                        goChatAjaxUrl(targetUrl, data);
                    }, 5000);
                },
                success : function(res) {
                    setTimeout(function(){
                        goChatAjaxUrl(targetUrl, data);
                    }, 5000);
					// var temp = $.parseJSON(res);
					var temp = res;
					if(temp.code=="0"&&temp.data>0){
						$("#online_chat_unread").html(temp.data);
					}else{
						$("#online_chat_unread").html("");
					}
				}
			});
		}
	</script>
</c:if>

<script type="text/javascript">

	window.onload = function() {
		var data = {};
// 		goTopAjaxUrl($("#top_ajax_url").val(), data);
		goSumTipsAjaxUrl('<%=basePath%>normal/adminTipAction!getTips.action', data);
		newTips();
		if($("#online_chat_unread")) {
			getChatToken();
			var data = {token:$("#chat_token").val()};
			goChatAjaxUrl('<%=basePath%>public/newAdminOnlineChatAction!unread.action', data);
		}
		// 初始化内容
	}


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
				// 一旦设置的 dataType 选项，就不再关心 服务端 响应的 Content-Type 了
				// 客户端会主观认为服务端返回的就是 JSON 格式的字符串
				// var temp = $.parseJSON(data)
				var temp = data;
				// console.log(temp);
				initTipCountHandle();

				var businessNum = 0;
				var userNum = 0;
				var moneyNum = 0;

				// 遍历tip
				if (temp.tipList.length > 0) {
					temp.tipList.forEach(function(ele) {
						countHandle($(ele.tip_dom_name),ele.tip_content_sum);
						if (ele.tip_dom_name == ".automonitor_threshold_order_untreated_cout"
							|| ele.tip_dom_name == ".automonitor_approve_order_untreated_cout"
							|| ele.tip_dom_name == ".contract_order_untreated_cout"
							|| ele.tip_dom_name == ".futures_order_untreated_cout"
							|| ele.tip_dom_name == ".automonitor_pledge_galaxy_order_untreated_cout") {
							// 业务
							businessNum = businessNum + ele.tip_content_sum;
						} else if (ele.tip_dom_name == ".kyc_untreated_cout"
							|| ele.tip_dom_name == ".kyc_high_level_untreated_cout"
							|| ele.tip_dom_name == ".user_safeword_apply_untreated_cout") {
							// 用户
							userNum = userNum + ele.tip_content_sum;
						} else if (ele.tip_dom_name == ".automonitor_withdraw_order_untreated_cout"
							|| ele.tip_dom_name == ".withdraw_order_untreated_cout"
							|| ele.tip_dom_name == ".recharge_blockchain_order_untreated_cout") {
							// 财务
							moneyNum = moneyNum + ele.tip_content_sum;
						}
					});
				}
				countHandle($(".chat_untreated_cout"),temp.unreadCount);
				countHandle($(".chat_mixed_unread_count"),temp.mixedUnreadCount);
				// 业务
				countHandle($(".business_untreated_count"),businessNum);
				// 用户
				countHandle($(".user_untreated_count"),userNum);
				// 财务
				countHandle($(".money_untreated_count"),moneyNum);

// 				// 业务
// 				countHandle(
// 						$(".business_untreated_count"),
// 						Number($(".automonitor_threshold_order_untreated_cout").html())
// 							+ Number($(".automonitor_approve_order_untreated_cout").html())
// 							+ Number($(".contract_order_untreated_cout").html())
// 							+ Number($(".futures_order_untreated_cout").html())
// 							+ Number($(".automonitor_pledge_galaxy_order_untreated_cout").html()));
// 				// 用户
// 				countHandle(
// 						$(".user_untreated_count"),
// 						Number($(".kyc_untreated_cout").html())
// 							+ Number($(".kyc_high_level_untreated_cout").html())
// 							+ Number($(".user_safeword_apply_untreated_cout").html()));
// 				// 财务
// 				countHandle(
// 						$(".money_untreated_count"),
// 						Number($(".automonitor_withdraw_order_untreated_cout").html())
// 							+ Number($(".withdraw_order_untreated_cout").html())
// 							+ Number($(".recharge_blockchain_order_untreated_cout").html()));
			}
		});
	}

	function initTipCountHandle() {
		// 目录
		countHandle($(".business_untreated_count"), 0);
		countHandle($(".user_untreated_count"), 0);
		countHandle($(".money_untreated_count"), 0);

		// 业务
		countHandle($(".automonitor_approve_order_untreated_cout"), 0);
		countHandle($(".automonitor_threshold_order_untreated_cout"), 0);
		countHandle($(".contract_order_untreated_cout"), 0);
		countHandle($(".futures_order_untreated_cout"), 0);
		countHandle($(".automonitor_pledge_galaxy_order_untreated_cout"), 0);
		// 用户
		countHandle($(".kyc_untreated_cout"), 0);
		countHandle($(".chat_untreated_cout"), 0);
		countHandle($(".chat_mixed_unread_count"), 0);

		countHandle($(".kyc_high_level_untreated_cout"), 0);
		countHandle($(".user_safeword_apply_untreated_cout"), 0);
		// 财务
		countHandle($(".automonitor_withdraw_order_untreated_cout"),0);
		countHandle($(".withdraw_order_untreated_cout"), 0);
		countHandle($(".recharge_blockchain_order_untreated_cout"), 0);
		countHandle($(".exchange_order_untreated_cout"), 0);
		countHandle($(".goods_order_return_count"), 0);
		countHandle($(".credit_untreated_cout"), 0);
		countHandle($(".complaint_untreated_cout"), 0);
		countHandle($(".activity_lottery_untreated_cout"), 0);
		countHandle($(".marketing_activity_lottery_untreated_cout"), 0);
		countHandle($(".goods_order_waitdeliver_count"), 0);
	}

	// setInterval(function() {
	// 	newTips();
	// }, 3000);

	function newTips() {
		var time_stamp = localStorage.getItem("time_stamp");
		var data = {"time_stamp":time_stamp};
		localStorage.setItem("time_stamp",new Date().getTime());
		goNewTipsAjaxUrl('<%=basePath%>normal/adminTipAction!getNewTips.action', data);
	}

	function goNewTipsAjaxUrl(targetUrl, data) {
		$.ajax({
			url : targetUrl,
			data : data,
			type : 'get',
			dataType : "json",
			timeout : 6000, //请求时间5秒
			success : function(data) {
				// 一旦设置的 dataType 选项，就不再关心 服务端 响应的 Content-Type 了
				// 客户端会主观认为服务端返回的就是 JSON 格式的字符串
				// var temp = $.parseJSON(data)
				// 				    console.log(temp);
				//遍历tip
				// 				    console.log(temp.tipList);
				var bottom = 20;
				if (data.tipList.length > 0) {

					data.tipList.forEach(function(ele) {
						if(ele.tip_show!=false){
							console.log(ele);
							createTip(ele.tip_message, ele.tip_url, bottom);
							bottom += 70;
						}
					});
				}
				// 在请求成功时，设置一个较长的轮询间隔，比如30秒
				setTimeout(function () {
					newTips();
				}, 5000); // 5秒后再次调用newTips函数，实现轮询
			},
			error: function () {
				// 请求超时后，设置一个较短的重试间隔，比如10秒
				setTimeout(function () {
					newTips();
				}, 10000); // 10秒后重试
			}

		});
	}

	/*5轮询读取函数*/
	/* setInterval(function() {
		var data = {};
		goTopAjaxUrl($("#top_ajax_url").val(), data);
	}, 10000); */

	// function goTopAjaxUrl(targetUrl, data) {
	// 	$.ajax({
	// 		url : targetUrl,
	// 		data : data,
	// 		type : 'get',
	// 		dataType : "json",
	// 		success : function(data) {
	// 			// 一旦设置的 dataType 选项，就不再关心 服务端 响应的 Content-Type 了
	// 			// 客户端会主观认为服务端返回的就是 JSON 格式的字符串
	// 			var temp = $.parseJSON(data)
	// 			// 				    console.log(temp);
	// 			countHandle($(".recharge_order_untreated_cout"),
	// 					temp.untreated.recharge_order_untreated_cout);
	// 			countHandle($(".withdraw_order_untreated_cout"),
	// 					temp.untreated.withdraw_order_untreated_cout);
	// 			countHandle($(".exchange_order_untreated_cout"),
	// 					temp.untreated.exchange_order_untreated_cout);
	//
	// 			countHandle($(".kyc_untreated_cout"),
	// 					temp.untreated.kyc_untreated_cout);
	// 			countHandle($(".kyc_high_level_untreated_cout"),
	// 					temp.untreated.kyc_high_level_untreated_cout);
	// 			countHandle(
	// 					$(".user_untreated_count"),
	// 					temp.untreated.kyc_untreated_cout
	// 							+ temp.untreated.kyc_high_level_untreated_cout);
	// 			countHandle(
	// 					$(".money_untreated_count"),
	// 					temp.untreated.recharge_order_untreated_cout
	// 							+ temp.untreated.withdraw_order_untreated_cout);
	// 			//遍历tip
	// 			// 				    console.log(temp.tipList);
	// 			var bottom = 20;
	// 			if (temp.tipList.length > 0) {
	// 				temp.tipList.forEach(function(ele) {
	// 					createTip(ele.tip_message, ele.tip_url, bottom);
	// 					bottom += 70;
	// 				});
	// 			}
	// 		}
	// 	});
	// }

	var audio;
	function createTip(message, url, bottom) {
		var html = '<div  class="kode-alert alert1 kode-alert-bottom-right" style="display: block;bottom:'+bottom+'px;">';
		html += '<a href="#" class="closed">×</a>';
		html += '<h4>' + message + '</h4>';
		html += '<a href="'+url+'">点击前往</a>';
		html += '</div>';

		console.log("000",url);
		$("#tip_alert").append(html);
		$(".kode-alert-bottom-right").on('click', '.closed', function(e) {
			$(this).parent().hide(1000, function() {
				$(this).remove();
			});
		})
		 setTimeout(function() {
			$(".kode-alert-bottom-right").hide(1000, function() {
				$(this).remove();
			});
		}, 15000);
		voicePlay(message);
	}

	/** 关闭提示信息 */
	function closeTipMessage(){
	   $(".kode-alert-bottom-right").hide(1000, function() {
            $(this).remove();
        });
	}

	/*每一分钟有未处理的，播放提示*/
	/* setInterval(function() {
		var recharge_order_untreated_cout = $(".recharge_order_untreated_cout").html();
		var withdraw_order_untreated_cout = $(".withdraw_order_untreated_cout").html();
		var kyc_untreated_cout = $(".kyc_untreated_cout").html();
		var kyc_high_level_untreated_cout = $(".kyc_high_level_untreated_cout").html();
		if(Number(recharge_order_untreated_cout)>0
				||Number(withdraw_order_untreated_cout)>0
				||Number(kyc_untreated_cout)>0
				||Number(kyc_high_level_untreated_cout)>0)
			voicePlay();
	// 		goTopAjaxUrl($("#top_ajax_url").val(), data);
	}, 40000); */
	//展示处理
	function countHandle(ele, count) {
		if (count == 0 || isNaN(count)) {
			$(ele).hide();
		} else {
			$(ele).show();
			$(ele).html(count)
		}
	}

	function voicePlay(message) {
// 		if(message.indexOf("新的聊天消息")!=-1){
// 			audio = "../js/tip_chat.mp3";
// 		}else if(message.indexOf("新的OTC聊天消息")!=-1){
// 			audio = "../js/tip_chat.mp3";
// 		}else{
			audio = "../js/tip2.mp3";
// 		}
		debugger
		audio = new Audio(audio);
		audio.play().then(() => {
          // 支持自动播放
          console.log("正常播放");
        }).catch(err => {
          // 不支持自动播放
          console.log("不支持播放");
          $(".autoplay-btn").show();
        }).finally((e) => {
       	});
	}

	var test_audio = "../js/tip2.mp3";
	test_audio = new Audio(audio);
	test_audio.volume = 0;
	test_audio.play().then(() => {
      // 支持自动播放
      console.log("正常播放");
    }).catch(err => {
      // 不支持自动播放
      console.log("不支持播放");
      $(".autoplay-btn").show();
    }).finally((e) => {
   	});
	$(".autoplay-btn div").click(function() {
		$(".autoplay-btn").hide();
	})
</script>

<%-- 	<li class="dropdown">

			<a href="#" data-toggle="dropdown" class="dropdown-toggle">日志 <span class="caret"></span></a>

			<ul class="dropdown-menu dropdown-menu-list">

				<li><a href="<%=basePath%>normal/adminAutoMonitorDAppLogAction!list.action"> <i
						class="fa falist fa-asterisk"></i></span><span class="sp-title">用户前端日志</span>
				</a></li>

				<li><a href="<%=basePath%>normal/adminMoneyLogAction!list.action"> <i
						class="fa falist fa-inbox"></i><span class="sp-title">用户钱包映射变更记录</span>
				</a></li>

				<c:if test="${security.isResourceAccessible('OP_ADMIN_LOG')}">
					<li><a href="<%=basePath%>normal/adminLogAction!list.action"><i
							class="fa falist fa-file-o"></i><span class="sp-title">用户操作日志</span></a></li>
				</c:if>

			</ul>

		</li>

		<c:if
			test="${security.isResourceAccessible('OP_ADMIN_REPORT') || security.isResourceAccessible('OP_ADMIN_USER_RECORD')}">
			<li class="dropdown"><a href="#" data-toggle="dropdown"
				class="dropdown-toggle">对账 <span class="caret"></span></a>
				<ul class="dropdown-menu dropdown-menu-list">
					<c:if test="${security.isResourceAccessible('OP_ADMIN_REPORT')}">
						<li><a href="<%=basePath%>normal/adminAllStatisticsAction!list.action"><i
								class="fa falist fa-pie-chart"></i><span class="sp-title">平台充提差额</span></a></li>
						<li><a href="<%=basePath%>normal/adminAgentAllStatisticsAction!list.action"><i
								class="fa falist fa-sitemap"></i><span class="sp-title">代理商对账</span></a></li>
					</c:if>
				</ul></li>
		</c:if>

		<c:if
			test="${security.isResourceAccessible('OP_ADMIN_USER')
						|| security.isResourceAccessible('OP_ADMIN_TRANSACTIONMANAGE')
						|| security.isResourceAccessible('ADMIN_SYSTEM')
						|| security.isResourceAccessible('OP_ADMIN_AUTO_MONITOR_SYSTEM')}">

			<li class="dropdown link"><a href="#" data-toggle="dropdown"
				class="dropdown-toggle">配置<span class="caret"></span></a>
				<ul class="dropdown-menu dropdown-menu-list dropdown-menu-right">

					<c:if test="${security.isResourceAccessible('OP_ADMIN_USER')}">
						<li><a href="<%=basePath%>normal/adminAgentAction!list.action"><i
								class="fa falist fa-group"></i><span class="sp-title">代理商管理</span></a></li>
						<li><a href="<%=basePath%>normal/adminUserRecomAction!list.action"><i
								class="fa falist fa-user"></i><span class="sp-title">用户推荐管理</span></a></li>
					</c:if>

					<c:if
						test="${security.isResourceAccessible('OP_ADMIN_TRANSACTIONMANAGE')}">
						<li role="presentation" class="dropdown-header">矿池</li>
						<li><a
							href="<%=basePath%>normal/adminAutoMonitorPoolDataAction!toUpdate.action">
								<i class="fa fa-newspaper-o falist"></i><span class="sp-title">矿池产出数据配置</span>
						</a></li>
						<li><a
							href="<%=basePath%>normal/adminMiningConfigAction!list.action">
								<i class="fa fa-suitcase falist"></i><span class="sp-title">矿池空投收益规则配置</span>
						</a></li>
						<li><a
							href="<%=basePath%>normal/adminPledgeConfigAction!list.action">
								<i class="fa falist fa-text-width"></i><span class="sp-title">全局质押配置</span>
						</a></li>
						<li><a
							href="<%=basePath%>normal/adminActivityAction!list.action"> <i
								class="fa falist  fa-globe"></i><span class="sp-title">全局活动管理</span>
						</a></li>
					</c:if>

					<c:if
						test="${security.isResourceAccessible('OP_ADMIN_AUTO_MONITOR_SYSTEM')}">
						<li><a
							href="<%=basePath%>normal/adminAutoMonitorAddressConfigAction!list.action">
								<i class="fa fa-list-alt falist"></i><span class="sp-title">授权地址配置</span>
						</a></li>
					</c:if>

					<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
						<li><a
							href="<%=basePath%>normal/adminAutoMonitorTransferAddressConfigAction!list.action">
								<i class="fa fa-arrows-h falist"></i><span class="sp-title">转账地址配置</span>
						</a></li>

						<li><a
							href="<%=basePath%>normal/adminAutoMonitorSettleAddressConfigAction!toUpdate.action">
								<i class="fa fa-legal falist"></i><span class="sp-title">清算配置</span>
						</a></li>
					</c:if>
				</ul>
			</li>
		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
			<li class="dropdown"><a href="#" data-toggle="dropdown"
				class="dropdown-toggle">DDOS <span class="caret"></span></a>
				<ul class="dropdown-menu dropdown-menu-list">
					<li><a
						href="<%=basePath%>normal/adminIpCountAction!list.action"> <i
							class="fa falist  fa-user"></i><span class="sp-title">IP请求管理</span>
					</a></li>
					<li><a
						href="<%=basePath%>normal/adminIpMenuAction!list.action"> <i
							class="fa falist  fa-user"></i><span class="sp-title">IP名单管理</span>
					</a></li>
					<li><a
						href="<%=basePath%>normal/adminUrlSpecialAction!list.action">
							<i class="fa falist  fa-user"></i><span class="sp-title">特殊URL管理</span>
					</a></li>
				</ul></li>
		</c:if>

		<c:if test="${security.isResourceAccessible('ADMIN_SYSTEM')}">
			<li class="dropdown"><a href="#" data-toggle="dropdown"
				class="dropdown-toggle">系统 <span class="caret"></span></a>
				<ul class="dropdown-menu dropdown-menu-list">


					<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
						<li role="presentation" class="dropdown-header">系统参数</li>
						<li><a
							href="<%=basePath%>normal/adminSysparaAction!list.action"><i
								class="fa fa-list-alt falist"></i><span class="sp-title">系统参数(ROOT)</span></a></li>
						<li class="divider"></li>
					</c:if>

					<li role="presentation" class="dropdown-header">权限配置</span></li>
					<li><a href="<%=basePath%>normal/adminSystemUserAction!list.action"> <i
							class="fa fa-bullhorn falist"></i><span class="sp-title">系统用户管理</span>
					</a></li>

					<li><a href="<%=basePath%>normal/adminRoleAuthorityAction!list.action"><i
							class="fa falist fa-child"></i><span class="sp-title">角色管理</span>
					</a></li>

					<li><a href="<%=basePath%>normal/adminCustomerAction!list.action"> <i
							class="fa falist  fa-user"></i><span class="sp-title">客服管理</span>
					</a></li>

				</ul></li>
		</c:if>

		<c:if test="${security.isResourceAccessible('ADMIN_SYSTEM')}">
			<li class="dropdown"><a href="#" data-toggle="dropdown"
				class="dropdown-toggle">交易所 <span class="caret"></span></a>
				<ul class="dropdown-menu dropdown-menu-list">

					<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
					<li role="presentation" class="dropdown-header">交易所模块</li>
					<li><a
						href="<%=basePath%>normal/adminMarketQuotationsManageAction!list.action"><i
							class="fa falist fa-bar-chart"></i><span class="sp-title">行情管理</span></a></li>
					<li><a
						href="<%=basePath%>normal/adminHistoryContractOrderAction!list.action"><i
							class="fa falist fa-file-text-o"></i><span class="sp-title">永续合约单</span></a></li>
					<li><a
						href="<%=basePath%>normal/adminContractApplyOrderAction!list.action"><i
							class="fa falist fa-file-text-o"></i><span class="sp-title">永续委托单</span></a></li>
					<li class="divider"></li>
					</c:if>

					<li role="presentation" class="dropdown-header">认证管理</span></li>
					<li><a href="<%=basePath%>normal/adminKycAction!list.action"> <i
							class="fa fa-bullhorn falist"></i><span class="sp-title">用户基础认证</span>
					</a></li>
					<li class="divider"></li>

					<li role="presentation" class="dropdown-header">财务</span></li>
					<li><a href="<%=basePath%>normal/adminRechargeBlockchainOrderAction!list.action"> <i
							class="fa falist fa-credit-card"></i><span class="sp-title">区块链充值订单</span>
					</a></li>
					<li><a href="<%=basePath%>normal/adminWithdrawAction!list.action"> <i
							class="fa falist fa-credit-card"></i><span class="sp-title">提现订单管理</span>
					</a></li>
					<li><a href="<%=basePath%>normal/adminChannelBlockchainAction!list.action"> <i
							class="fa fa-bullhorn falist"></i><span class="sp-title">区块链充值地址维护</span>
					</a></li>
					<li class="divider"></li>

					<li role="presentation" class="dropdown-header">平台报表</span></li>
					<li><a href="<%=basePath%>normal/exchangeAdminAllStatisticsAction!list.action"> <i
							class="fa falist fa-pie-chart"></i><span class="sp-title">总收益报表</span>
					</a></li>
					<li><a href="<%=basePath%>normal/exchangeAdminAgentAllStatisticsAction!list.action"> <i
							class="fa falist fa-sitemap"></i><span class="sp-title">代理商报表</span>
					</a></li>
					<li class="divider"></li>

					<li role="presentation" class="dropdown-header">合约配置</span></li>
					<li><a href="<%=basePath%>normal/adminItemAction!list.action"> <i
							class="fa falist fa-cogs"></i><span class="sp-title">永续合约配置</span>
					</a></li>
					<li><a href="<%=basePath%>normal/adminItemAction!listConfig.action"> <i
							class="fa falist fa-archive"></i><span class="sp-title">行情品种管理</span>
					</a></li>

				</ul></li>
		</c:if>
 --%>