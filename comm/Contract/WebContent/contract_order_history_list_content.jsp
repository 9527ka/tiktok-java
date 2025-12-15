<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>用户</td>
										<td>UID</td>
										<td>账户类型</td>
<!-- 										<td >订单号</td> -->
										<td>品种</td>
										<td>操作</td>
										<td>成交均价</td>
										<td>止盈止损</td>
										<td>剩余/委托金额</td>
										<td>剩余/委托保证金</td>
										<td>用户钱包余额</td>
										<td>盈亏</td>
										<td>状态</td>
										<td>创建时间</td>
										<td>平仓时间</td>
										<s:if test='isResourceAccessible("ADMIN_HISTORY_CONTRACT_ORDER_CLOSE")'>
										<td width="130px"></td>
										</s:if>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
											<td><s:property value="username" /></td>
											<td><s:property value="usercode" /></td>
											<td><s:if test='rolename=="GUEST"'>
													<span class="right label label-warning">演示账号</span>
												</s:if>
												<s:if test='rolename=="MEMBER"'>
													<span class="right label label-success">正式账号</span>
												</s:if>
											</td>
<%-- 											<td ><s:property value="order_no" /></td> --%>
											<td><s:property value="itemname" /></td>
											<td><s:if test='offset=="open"'>开</s:if><s:if test='offset=="close"'>平</s:if><s:if test='direction=="buy"'>多</s:if><s:if
													test='direction=="sell"'>空</s:if></td>
											<td><s:property value="trade_avg_price" /></td>
											<td><s:property value="stop_price_profit" />/<s:property value="stop_price_loss" /></td>
											<td><span class="right label label-success"><fmt:formatNumber value="${volume*unit_amount}" pattern="#0.00" /></span>/<fmt:formatNumber value="${volume_open*unit_amount}" pattern="#0.00" /></td>
											<td><span class="right label label-success"><fmt:formatNumber value="${deposit}" pattern="#0.00" /></span>/<fmt:formatNumber value="${deposit_open}" pattern="#0.00" /></td>
											<td><fmt:formatNumber value="${money}" pattern="#0.00" /></td>
											<td>	
											<s:if test='state=="submitted"'>	
											<s:if test="(amount_close+profit+deposit) >=deposit_open">
												<span class="right label label-danger"><fmt:formatNumber
														value="${amount_close+profit+deposit-deposit_open}" pattern="#0.00" /> </span>
											</s:if>
											<s:else>
												<span class="right label label-success"><fmt:formatNumber
														value="${amount_close+profit+deposit-deposit_open}" pattern="#0.00" /> </span>
											</s:else>
											</s:if>
											<s:else>
											<s:if test="(amount_close+deposit) >=deposit_open">
												<span class="right label label-danger"><fmt:formatNumber
														value="${amount_close+deposit-deposit_open}" pattern="#0.00" /> </span>
											</s:if>
											<s:else>
												<span class="right label label-success"><fmt:formatNumber
														value="${amount_close+deposit-deposit_open}" pattern="#0.00" /> </span>
											</s:else>
											</s:else>
											</td>
											<td><s:if test='state=="submitted"'>
													持仓
												</s:if>  <s:if test='state=="created"'>
													<span class="right label label-success">已平仓</span>
												</s:if> 
											</td>
											<td><s:date name="createTime" format="MM-dd HH:mm:ss " /></td>
											<td><s:date name="closeTime" format="MM-dd HH:mm:ss " /></td>
											 
											<s:if test='isResourceAccessible("ADMIN_HISTORY_CONTRACT_ORDER_CLOSE")'>								
											<td>
											<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
													<s:if test='state=="submitted"'>
													
														<li><a
															href="javascript:onclose('<s:property value="order_no" />')">平仓</a></li>
													
													</s:if>
													</ul>
													
												</div>
												</sec:authorize>
											</td>
											</s:if>

										</tr>
									</s:iterator>

								</tbody>
							</table>
							<%@ include file="include/page_simple.jsp"%>
							<nav>
						</div>

					</div>
					<!-- End Panel -->

				</div>