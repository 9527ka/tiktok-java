<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<style>
	.divider {
		margin: 4px 0;
		height: 1px;
		margin: 9px 0;
		overflow: hidden;
		background-color: #c8c1c1;
	}
</style>

<div class="sidebar clearfix">

    <div class="sidebar__logo">
        <a href="<%=basePath%>normal/LoginSuccessAction!view.action?v=ag">
            <img src="${siteLogo}" alt="gbmall logo" class="sidebar__logo-img">
        </a>
    </div>
	<ul class="sidebar-panel nav">

		<!-- dapp+交易所 菜单 ######################################################################################################## -->

		<%--        <c:choose>--%>
		<%--            <c:when test="${security.isRolesAccessible('ROLE_AGENT','normal/adminUserAction!list.action')}">--%>

		<%--                <li class="dropdown-parent">--%>
		<%--                    <a href="<%=basePath%>normal/adminUserAction!list.action">--%>
		<%--                        <span class="icon color6"><i class="fa fa-file-text-o"></i></span>--%>
		<%--                        <span class="sp-title">用户基础管理</span>--%>
		<%--                    </a>--%>
		<%--                </li>--%>

		<%--            </c:when>--%>
		<%--            <c:otherwise>--%>

		<li>
			<a href="<%=basePath%>normal/adminIndexAction!viewNew.action">
				<span class="icon color6 icon-activity"><i data-feather="activity"></i></span>
				<span class="sp-title">综合查询</span>
			</a>
		</li>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_AGENT','normal/adminUserAction!list.action')
                             || security.isResourceAccessible('OP_USER_CHECK')
                             || security.isResourceAccessible('OP_USER_OPERATE')}">

			<li>
				<a href="<%=basePath%>normal/adminUserAction!list.action">
					<span class="icon color6 icon-users"><i data-feather="users"></i></span>
					<span class="sp-title">用户管理</span>
				</a>
			</li>

		</c:if>


		<%--                <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','mall/pos/list.action')--%>
		<%--             || security.isResourceAccessible('OP_GOODS_CHECK')--%>
		<%--             || security.isResourceAccessible('OP_GOODS_OPERATE')}">--%>




		<li>
			<a href="<%=basePath%>mall/pos/list.action">
				<span class="icon color6 icon-grid"><i data-feather="grid"></i></span>
				<span class="sp-title">POS下单</span>
			</a>
		</li>
		<li>
			<a href="<%=basePath%>mall/pos/historyList.action">
			    <span class="icon color6 icon-archive"><i data-feather="archive"></i></span>
				<span class="sp-title">POS日志</span>
			</a>
		</li>

		<%--                </c:if>--%>

		<%--            </c:otherwise>--%>
		<%--        </c:choose>--%>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','财务')
                     || security.isResourceListAccessible('OP_EXCHANGE_USER_CHECK,OP_EXCHANGE_USER_OPERATE,OP_MARKET_CHECK,OP_MARKET_OPERATE,OP_EXCHANGE_WITHDRAW_CHECK,OP_EXCHANGE_WITHDRAW_OPERATE,OP_EXCHANGE_RECHARGE_CHECK,OP_EXCHANGE_RECHARGE_OPERATE')}">


			<li class="sidetitle">财务</li>

		</c:if>

        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','normal/adminRechargeBlockchainOrderAction!list.action')
                     || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_CHECK')
                     || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_OPERATE')}">

			<li>
				<a href="<%=basePath%>normal/adminRechargeBlockchainOrderAction!list.action">
				    <span class="icon color6 icon-zap"><i data-feather="zap"></i></span>
					<span class="sp-title">充值订单</span>
					<span class="recharge_blockchain_order_untreated_cout badge label-danger" style="display: none">0</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','normal/adminWithdrawAction!list.action')
                     || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_CHECK')
                     || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_OPERATE')}">

			<li>
				<a href="<%=basePath%>normal/adminWithdrawAction!list.action">
				    <span class="icon color6 icon-credit-card"><i data-feather="credit-card"></i></span>
					<span class="sp-title">提现订单</span>
					<span class="withdraw_order_untreated_cout badge label-danger" style="display: none">0</span>
				</a>
			</li>

		</c:if>


		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','normal/adminKycAction!list.action')
                                 || security.isResourceAccessible('OP_USER_KYC_CHECK')
                                 || security.isResourceAccessible('OP_USER_KYC_OPERATE')}">

			<li>
				<a href="<%=basePath%>normal/adminKycAction!list.action">
				    <span class="icon color6 icon-lock"><i data-feather="lock"></i></span>
					<span class="sp-title">店铺审核</span>
					<span class="kyc_untreated_cout badge label-danger" style="display: none">0</span>
				</a>
			</li>

		</c:if>

        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','mall/seller/invitelist.action')
                             || security.isResourceAccessible('OP_INVITE_CHECK')
                             || security.isResourceAccessible('OP_INVITE_OPERATE')}">
			<li>
				<a href="<%=basePath%>mall/seller/invitelist.action">
				    <span class="icon color6 icon-gift"><i data-feather="gift"></i></span>
					<span class="sp-title">奖金审核</span>
					<span class="activity_lottery_untreated_cout badge label-danger" style="display: none">0</span>
				</a>
			</li>
		</c:if>

		<li class="sidetitle">业务</li>


		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','mall/order/list.action')
                 || security.isResourceAccessible('OP_MALL_ORDER_CHECK') || security.isResourceAccessible('OP_MALL_ORDER_OPERATE')}">

			<li>
				<a href="<%=basePath%>mall/order/list.action">
				    <span class="icon color6 icon-shopping-bag"><i data-feather="shopping-bag"></i></span>
					<span class="sp-title">订单管理</span>
					<span class="goods_order_waitdeliver_count badge label-danger" style="display: none">0</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','/download/#/commodity-library')
             || security.isResourceAccessible('OP_MALL_GOODS_CHECK')
             || security.isResourceAccessible('OP_MALL_GOODS_OPERATE')}">

			<li>
				<a href="<%=dmUrl%>/download/#/commodity-library?url=<%=adminUrl%>">
						<%--                    <a href="<%=basePath%>mall/goods/list.action">--%>
				    <span class="icon color6 icon-server"><i data-feather="server"></i></span>
					<span class="sp-title">商品库</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','mall/category/list.action?level=0')
                             || security.isResourceAccessible('OP_CATEGORY_CHECK')
                             || security.isResourceAccessible('OP_CATEGORY_OPERATE')}">
			<li>
				<a href="<%=basePath%>mall/category/list.action?level=0">
				    <span class="icon color6 icon-book-open"><i data-feather="book-open"></i></span>
					<span class="sp-title">商品分类</span>
				</a>
			</li>
		</c:if>


<!--
		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','/download/#/commodity-library')
             || security.isResourceAccessible('OP_MALL_GOODS_CHECK')
             || security.isResourceAccessible('OP_MALL_GOODS_OPERATE')}">

			<li>
				<a href="<%=basePath%>mall/ext/goods/gather.action">
				    <span class="icon color6 icon-coffee"><i data-feather="coffee"></i></span>
					<span class="sp-title">商品采集</span>
				</a>
			</li>

		</c:if>
-->

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','mall/seller/list.action')
                                 || security.isResourceAccessible('OP_MALL_SELLER_CHECK')
                                 || security.isResourceAccessible('OP_MALL_SELLER_OPERATE')}">

			<li>
				<a href="<%=basePath%>mall/seller/list.action">
				    <span class="icon color6 icon-layers"><i data-feather="layers"></i></span>
					<span class="sp-title">店铺管理</span>
				</a>
			</li>

		</c:if>
		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','mall/seller/compliants.action')
                                 || security.isResourceAccessible('OP_COMPLIANT_CHECK')
                                 || security.isResourceAccessible('OP_COMPLIANT_OPERATE')}">

			<li>
				<a href="<%=basePath%>mall/seller/compliants.action">
				    <span class="icon color6 icon-clock1"><i data-feather="clock"></i></span>
					<span class="sp-title">店铺投诉</span>
					<span class="complaint_untreated_cout badge label-danger" style="display: none">0</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','mall/order/refundList.action')
                 || security.isResourceAccessible('OP_MALL_RORDER_CHECK') || security.isResourceAccessible('OP_MALL_RORDER_OPERATE')}">

            <li>
                <a href="<%=basePath%>mall/order/refundList.action">
                    <span class="icon color6 icon-package"><i data-feather="package"></i></span>
                    <span class="sp-title">退货订单</span>
                    <span class="goods_order_return_count badge label-danger" style="display: none">0</span>
                </a>
            </li>

        </c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','mall/combo/list.action')
                                 || security.isResourceAccessible('OP_COMBO_CHECK')
                                 || security.isResourceAccessible('OP_COMBO_OPERATE')}">

			<li>
				<a href="<%=basePath%>mall/combo/list.action">
				    <span class="icon color6 icon-radio"><i data-feather="radio"></i></span>
					<span class="sp-title">店铺直通车</span>
				</a>
			</li>

		</c:if>
		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','mall/combo/recordList.action')
                                 || security.isResourceAccessible('OP_COMBORECORD_CHECK')}">

			<li>
				<a href="<%=basePath%>mall/combo/recordList.action">
				    <span class="icon color6 icon-menu"><i data-feather="menu"></i></span>
					<span class="sp-title">直通车购买记录</span>
				</a>
			</li>

		</c:if>



<%--		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
<%--                     || security.isResourceListAccessible('OP_EVENTS_CHECK,OP_PRIZEMANAGEMENT_CHECK,OP_LOTTERYRECEIVE_CHECK,OP_LOTTERYRECEIVE_CHECK')}">--%>
<%--			<li class="sidetitle">营销活动</li>--%>
<%--		</c:if>--%>
<%--		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','/download/#/marketing/EventsList')--%>
<%--                                         || security.isResourceAccessible('OP_EVENTS_CHECK')}">--%>

<%--			<li>--%>
<%--				<a href="<%=dmUrl%>/download/#/marketing/EventsList?url=<%=adminUrl%>">--%>
<%--						&lt;%&ndash;                    <a href="<%=basePath%>mall/goods/list.action">&ndash;%&gt;--%>
<%--					<span class="icon color6"><i class="fa falist fa-columns"></i></span>--%>
<%--					<span class="sp-title">活动列表</span>--%>
<%--				</a>--%>
<%--			</li>--%>
<%--		</c:if>--%>

<%--		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','/download/#/marketing/PrizeManagement')--%>
<%--                                         || security.isResourceAccessible('OP_PRIZEMANAGEMENT_CHECK')}">--%>
<%--			<li>--%>
<%--				<a href="<%=dmUrl%>/download/#/marketing/PrizeManagement?url=<%=adminUrl%>">--%>
<%--						&lt;%&ndash;                    <a href="<%=basePath%>mall/goods/list.action">&ndash;%&gt;--%>
<%--					<span class="icon color6"><i class="fa falist fa-columns"></i></span>--%>
<%--					<span class="sp-title">奖品管理</span>--%>
<%--				</a>--%>
<%--			</li>--%>
<%--		</c:if>--%>
<%--		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','/download/#/marketing/GetRecords')--%>
<%--                                         || security.isResourceAccessible('OP_LOTTERYRECEIVE_CHECK')}">--%>
<%--			<li>--%>
<%--				<a href="<%=dmUrl%>/download/#/marketing/GetRecords?url=<%=adminUrl%>">--%>
<%--					<span class="icon color6"><i class="fa falist fa-columns"></i></span>--%>
<%--					<span class="sp-title">领奖记录</span>--%>
<%--					<span class="marketing_activity_lottery_untreated_cout badge label-danger" style="display: none">0</span>--%>

<%--				</a>--%>
<%--			</li>--%>
<%--		</c:if>--%>
<%--		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','download/#/marketing/WinningRecord')--%>
<%--                                         || security.isResourceAccessible('OP_LOTTERYRECORD_CHECK')}">--%>
<%--			<li>--%>
<%--				<a href="<%=dmUrl%>/download/#/marketing/WinningRecord?url=<%=adminUrl%>">--%>
<%--						&lt;%&ndash;                    <a href="<%=basePath%>mall/goods/list.action">&ndash;%&gt;--%>
<%--					<span class="icon color6"><i class="fa falist fa-columns"></i></span>--%>
<%--					<span class="sp-title">中奖记录</span>--%>
<%--				</a>--%>
<%--			</li>--%>
<%--		</c:if>--%>


        <li class="sidetitle">其他管理</li>
        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','mall/goodAttrCategory/list.action')
             || security.isResourceAccessible('OP_GOODATTRCATEGORY_CHECK')
             || security.isResourceAccessible('OP_GOODATTRCATEGORY_OPERATE')}">

			<li>
				<a href="<%=basePath%>mall/goodAttrCategory/list.action">
				    <span class="icon color6 icon-cpu"><i data-feather="cpu"></i></span>
					<span class="sp-title">属性管理</span>
				</a>
			</li>

		</c:if>

		<%--        <li>--%>
		<%--            <a href="http://127.0.0.1:41879/attribute/#/">--%>
		<%--                <span class="icon color6"><i class="fa falist fa-columns"></i></span>--%>
		<%--                <span class="sp-title">属性管理</span>--%>
		<%--            </a>--%>
		<%--        </li>--%>

		<%--        <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
		<%--             || security.isResourceAccessible('OP_MALL_GOODS_CHECK')--%>
		<%--             || security.isResourceAccessible('OP_MALL_GOODS_OPERATE')}">--%>

		<%--            <li>--%>
		<%--                <a href="<%=basePath%>mall/comment/list.action">--%>
		<%--                    <span class="icon color6"><i class="fa falist fa-columns"></i></span>--%>
		<%--                    <span class="sp-title">评论库</span>--%>
		<%--                </a>--%>
		<%--            </li>--%>

		<%--        </c:if>--%>



		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','brush/vip/list.action')
                 || security.isResourceAccessible('OP_VIP_CHECK')}">

			<li>
				<a href="<%=basePath%>brush/vip/list.action">
				    <span class="icon color6 icon-trello"><i data-feather="trello"></i></span>
					<span class="sp-title">店铺等级</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','mall/goods/sellerGoodsList.action')
             || security.isResourceAccessible('OP_GOODS_CHECK')
             || security.isResourceAccessible('OP_GOODS_OPERATE')}">

			<li>
				<a href="<%=basePath%>mall/goods/sellerGoodsList.action">
				    <span class="icon color6 icon-instagram"><i data-feather="instagram"></i></span>
					<span class="sp-title">店铺商品</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','mall/loan/config/toUpdate.action')
             || security.isResourceAccessible('OP_LOANCOFIG_CHECK')}">

			<li>
				<a href="<%=basePath%>mall/loan/config/toUpdate.action">
				    <span class="icon color6 icon-share-2"><i data-feather="share-2"></i></span>
					<span class="sp-title">借贷配置</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','credit/history.action')
             || security.isResourceAccessible('OP_CREDIT_CHECK')
             || security.isResourceAccessible('OP_CREDIT_OPERATE')}">

			<li>
				<a href="<%=basePath%>credit/history.action">
				    <span class="icon color6 icon-menu"><i data-feather="menu"></i></span>
					<span class="sp-title">借贷记录</span>
					<span class="credit_untreated_cout badge label-danger" style="display: none">0</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','platform/chatsList.action')
                                         || security.isResourceAccessible('OP_PLATFORMCHAT_CHECK')
                                         || security.isResourceAccessible('OP_PLATFORMCHAT_OPERATE')}">
			<li>
				<a href="<%=basePath%>platform/chatsList.action">
				    <span class="icon color6 icon-message-circle"><i data-feather="message-circle"></i></span>
					<span class="sp-title">系统客服对话</span>
				</a>
			</li>


		</c:if>

	    <c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','chat/chatsList.action')
                                 || security.isResourceAccessible('OP_CHAT_CHECK')
                                 || security.isResourceAccessible('OP_CHAT_OPERATE')}">

			<li>
				<a href="<%=basePath%>chat/chatsList.action">
				    <span class="icon color6 icon-user-plus"><i data-feather="user-plus"></i></span>
					<span class="sp-title">虚拟买家对话</span>
					<span class="chat_untreated_cout badge label-danger" style="display: none">0</span>
				</a>
			</li>
		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','chat/auditList.action')
                                         || security.isResourceAccessible('OP_CHAT_AUDIT_CHECK')
                                         || security.isResourceAccessible('OP_CHAT_AUDIT_OPERATE')}">
			<li>
				<a href="<%=basePath%>chat/auditList.action">
				    <span class="icon color6 icon-smile1"><i data-feather="smile"></i></span>
					<span class="sp-title">买家对话审核</span>
					<span class="chat_mixed_unread_count badge label-danger" style="display: none">0</span>

				</a>
			</li>

		</c:if>



		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','对账')
                     || security.isResourceListAccessible('OP_EXCHANGE_ALL_STATISTICS_CHECK,OP_EXCHANGE_AGENT_ALL_STATISTICS_CHECK,OP_EXCHANGE_USER_ALL_STATISTICS_CHECK')}">

			<li class="sidetitle">对账</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','brush/userMoney/list.action')
                            || security.isResourceAccessible('OP_EXCHANGE_ALL_STATISTICS_CHECK')}">

			<li>
				<a href="<%=basePath%>brush/userMoney/list.action">
				    <span class="icon color6 icon-users"><i data-feather="users"></i></span>
					<span class="sp-title">用户存量</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','normal/exchangeAdminAllStatisticsAction')
                            || security.isResourceAccessible('OP_EXCHANGE_ALL_STATISTICS_CHECK')}">

			<li>
				<a href="<%=basePath%>normal/exchangeAdminAllStatisticsAction!list.action">
				    <span class="icon color6 icon-trello"><i data-feather="trello"></i></span>
					<span class="sp-title">运营数据</span>
				</a>
			</li>

		</c:if>

		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','normal/exchangeAdminAgentAllStatisticsAction!list.action')
                            || security.isResourceAccessible('OP_EXCHANGE_AGENT_ALL_STATISTICS_CHECK')}">

			<li>
				<a href="<%=basePath%>normal/exchangeAdminAgentAllStatisticsAction!list.action">
				    <span class="icon color6 icon-trending-up"><i data-feather="trending-up"></i></span>
					<span class="sp-title">代理商充提报表</span>
				</a>
			</li>

		</c:if>
		<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN','normal/adminUserAllStatisticsAction!exchangeList.action')
                            || security.isResourceAccessible('OP_EXCHANGE_USER_ALL_STATISTICS_CHECK')}">

			<li>
				<a href="<%=basePath%>normal/adminUserAllStatisticsAction!exchangeList.action">
				    <span class="icon color6 icon-user-check"><i data-feather="user-check"></i></span>
					<span class="sp-title">用户报表</span>
				</a>
			</li>

		</c:if>


	</ul>

</div>
<script>if(typeof feather !== 'undefined'){feather.replace({ class: 'agx-icon', 'stroke-width': 2, width: 18,height: 18 });}</script>