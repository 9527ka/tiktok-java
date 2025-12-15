<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>

	<%@ include file="include/loading.jsp"%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>交割合约单</h3>
			
			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminFuturesOrderAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="status_para" id="status_para" value="${status_para}" />
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="order_no_para" name="order_no_para"
													cssClass="form-control " placeholder="订单号（完整）" /> -->
												<input id="order_no_para" name="order_no_para"
													class="form-control " placeholder="订单号（完整）" value="${order_no_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="name_para" name="name_para" cssClass="form-control " placeholder="用户名、UID"/> -->
												<input id="name_para" name="name_para" 
													class="form-control " placeholder="用户名、UID" value="${name_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3" >
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<%-- <s:select id="rolename_para" cssClass="form-control "
													name="rolename_para"
													list="#{'MEMBER':'正式账号合约','GUEST':'演示账号合约','TEST':'试用账号合约'}" listKey="key"
													listValue="value" headerKey="" headerValue="所有合约"
													value="rolename_para" /> --%>
												<select id="rolename_para" name="rolename_para" class="form-control " >
												   <option value="">所有合约</option>
												   <option value="MEMBER" <c:if test="${rolename_para == 'MEMBER'}">selected="true"</c:if> >正式用户合约</option>
												   <option value="GUEST" <c:if test="${rolename_para == 'GUEST'}">selected="true"</c:if> >演示用户合约</option>
												   <option value="TEST" <c:if test="${rolename_para == 'TEST'}">selected="true"</c:if> >试用用户合约</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-12" style="margin-top: 10px;" >
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<%-- <s:select id="direction_para" cssClass="form-control "
													name="direction_para"
													list="#{'buy':'开多','sell':'开空'}" listKey="key"
													listValue="value" headerKey="" headerValue="---请选择方向---"
													value="direction_para" /> --%>
												<select id="direction_para" name="direction_para" class="form-control " >
												   <option value="">---请选择方向---</option>
												   <option value="buy" <c:if test="${direction_para == 'buy'}">selected="true"</c:if> >开多</option>
												   <option value="sell" <c:if test="${direction_para == 'sell'}">selected="true"</c:if> >开空</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:select id="symbol_para" cssClass="form-control "
												name="symbol_para" list="symbol_map"
												listKey="key" listValue="value" value="symbol_para" headerKey="" headerValue="---请选择币种---"/> -->
												<select id="symbol_para" name="symbol_para" class="form-control " >
													<option value="">请选择</option>
													<c:forEach items="${symbol_map}" var="item">
														<option value="${item.key}" <c:if test="${symbol_para == item.key}">selected="true"</c:if> >${item.value}</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="volume_para" name="volume_para" cssClass="form-control " placeholder="下单金额"/> -->
												<input id="volume_para" name="volume_para" 
													class="form-control " placeholder="下单金额" value="${volume_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-2" >
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>

								<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li><a href="javascript:setState('')"> 全部</a></li>
												<li><a href="javascript:setState('submitted')"> 持仓</a></li>
												<li><a href="javascript:setState('created')"> 已平仓</a></li>
											</ul>
										</div>
									</div>
								</div>

							</form>

						</div>

					</div>
				</div>
			</div>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
		
			<div class="row">
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
										<td>品种</td>
										<td>操作</td>
										<td>合约时间</td>
										<!-- <td>剩余金额</td>
										<td>用户钱包余额</td> -->
										<td>购买金额</td>
										<td>购买价</td>
										<td>结算价</td>
										<td>手续费</td>
										<td>盈亏</td>
										<td>购买时间</td>
										<td>交割时间</td>								
										<td>状态</td>									
										<td>订单盈亏控制情况（优先级高于交割场控设置）</td>
				
										<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
													 || security.isResourceAccessible('OP_FUTURES_CONTRACT_ORDER_OPERATE')}">
										
											<td width="130px"></td>
											
										</c:if>
										
									</tr>
								</thead>
								
								<tbody>
									 <!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>
												<c:choose>
													<c:when test="${item.username!='' && item.username!=null}">
														<a style="font-size: 10px;" href="#" onClick="getallname('${item.username}')">
															${fn:substring(item.username,0,4)}***${fn:substring(item.username,fn:length(item.username) - 4, fn:length(item.username))}
														</a>
													</c:when>
													<c:otherwise>
														${item.username}
													</c:otherwise>
												</c:choose>
											</td>
											<td>${item.usercode}</td>
											<td>
												<c:choose>
													<c:when test="${item.rolename=='GUEST'}">
														<span class="right label label-warning">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='MEMBER'}">
														<span class="right label label-success">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='TEST'}">
														<span class="right label label-default">${item.roleNameDesc}</span>
													</c:when>
													<c:otherwise>
													 	${item.roleNameDesc}
													</c:otherwise>	
												</c:choose>
											</td>
											<td>${item.itemname}</td>
											<td>
												<c:if test="${item.offset == 'open'}">开</c:if>
												<c:if test="${item.offset == 'close'}">平</c:if>
												<c:if test="${item.direction == 'buy'}">多</c:if>
												<c:if test="${item.direction == 'sell'}">空</c:if>
											</td>
											<td>${item.timenum}</td>
											<td>${item.volume}</td>
											<td>${item.trade_avg_price}</td>
											<td>${item.close_avg_price}</td>
											<td>${item.fee}</td>
											<td>
												<c:if test="${item.profit >= 0.0}">
													<span class="right label label-success">${item.profit}</span>
												</c:if>
												<c:if test="${item.profit < 0.0}">
													<span class="right label label-danger">${item.profit}</span>
												</c:if>
											</td>
											<td>${item.create_time}</td>
											<td>${item.settlement_time}</td>
											<%-- <td><span class="right label label-success"><fmt:formatNumber value="${volume*unit_amount}" pattern="#0.00" /></span></td>
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
											<td>剩余时间</td> --%>										
											<td>
												<c:if test="${item.state == 'submitted'}">
													持仓
												</c:if>  
												<c:if test="${item.state == 'created'}">
													<span class="right label label-success">已平仓</span>
												</c:if>
											</td>
											<td>
												<c:if test="${item.profit_loss == 'profit'}">
													<span class="right label label-success">盈利</span>
												</c:if>  
												<c:if test="${item.profit_loss == 'loss'}">
													<span class="right label label-danger">亏损</span>
												</c:if> 
											</td>
											
											<td>
				
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_FUTURES_CONTRACT_ORDER_OPERATE')}">
												
													<c:if test="${item.state == 'submitted'}">
													
														<div class="btn-group">
															<button type="button" class="btn btn-light">操作</button>
															<button type="button" class="btn btn-light dropdown-toggle"
																data-toggle="dropdown" aria-expanded="false">
																<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
															</button>
															<ul class="dropdown-menu" role="menu">
																<li><a href="javascript:profitLoss('${item.order_no}','${item.profit_loss}')">场控</a></li>
															</ul>
														</div>
														
													</c:if> 

												</c:if>
													
											</td>

										</tr>
									<!-- </s:iterator> -->
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

		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	
	<!-- 模态框 -->
	<div class="form-group">
	
		<form action="<%=basePath%>normal/adminFuturesOrderAction!orderProfitLoss.action"
			method="post" id="succeededForm">
				
			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
			<input type="hidden" name="name_para" id="name_para" value="${name_para}" />
			<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}" />
			<input type="hidden" name="order_no_para" id="order_no_para" value="${order_no_para}" />
			<input type="hidden" name="status_para" id="status_para" value="${status_para}" />
			<input type="hidden" name="direction_para" id="direction_para" value="${direction_para}" />
			<input type="hidden" name="symbol_para" id="symbol_para" value="${symbol_para}" />
			<input type="hidden" name="volume_para" id="volume_para" value="${volume_para}" />
			<input type="hidden" name="order_no" id="order_no" value="${order_no}" />
			
			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="modal_set" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel"
					aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
						
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
								<h4 class="modal-title" >订单场控</h4>
							</div>
							
							<div class="modal-body">
								<div class="">
									<%-- <s:select id="profit_loss" cssClass="form-control "
									name="profit_loss"
									list="#{'profit':'盈利','loss':'亏损'}" listKey="key"
									listValue="value" headerKey="" headerValue="--场控选择--"
									value="profit_loss" /> --%>
									<select id="profit_loss" name="profit_loss" class="form-control " >
									   <option value="">--场控选择--</option>
									   <option value="profit" <c:if test="${profit_loss == 'profit'}">selected="true"</c:if> >盈利</option>
									   <option value="loss" <c:if test="${profit_loss == 'loss'}">selected="true"</c:if> >亏损</option>
									</select>
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
			
		</form>
		
	</div>
	
	<div class="form-group">
		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="net_form" tabindex="-1"
				role="dialog" aria-labelledby="myModalLabel"
				aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="myModalLabel">完整用户名（完整钱包地址）</h4>
						</div>
						<div class="modal-header">
								<h4 class="modal-title" name="usernallName" id="usernallName"  readonly="true" style="display: inline-block;"></h4>
								<a href="" id="user_all_name_a" sytle="cursor:pointer;" target="_blank">在Etherscan上查看</a>
						</div>
									
						<div class="modal-body">
								<div class="">
								</div>
						</div>
							
			        </div>
				</div>
			</div>
		</div>
	</div>	

	<%@ include file="include/js.jsp"%>

	<script type="text/javascript">
		function getallname(name){
			$("#usernallName").html(name);
			$("#user_all_name_a").attr("href","https://etherscan.io/address/"+name);
			$("#net_form").modal("show");
		}
	</script>

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
			})
		});
	</script>

	<script type="text/javascript">
		function setState(state) {
    		document.getElementById("status_para").value=state;
    		document.getElementById("queryForm").submit();
		}
	</script>

	<script type="text/javascript">
		function profitLoss(order_no, profit_loss) {
			$("#order_no").val(order_no);
			$("#profit_loss").val(profit_loss);
			$('#modal_set').modal("show");		 
		}
	</script>
	
</body>

</html>
