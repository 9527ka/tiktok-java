<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

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
						<!-- <td >订单号</td> -->
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
						<td width="130px"></td>
					</tr>
				</thead>

				<tbody>
					<%-- <s:iterator value="page.elements" status="stat"> --%>
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
							<%-- <td ><s:property value="order_no" /></td> --%>
							<td>${item.itemname}</td>
							<td>
								<c:if test="${item.offset == 'open'}">开</c:if> 
								<c:if test="${item.offset == 'close'}">平</c:if> 
								<c:if test="${item.direction == 'buy'}">多</c:if>
								<c:if test="${item.direction == 'sell'}">空</c:if>
							</td>
							<td>${item.trade_avg_price}</td>
							<td>${item.stop_price_profit}/${item.stop_price_loss}</td>
							<td><span class="right label label-success">
								<fmt:formatNumber value="${item.volume * item.unit_amount}" pattern="#0.00" /></span>/
								<fmt:formatNumber value="${item.volume_open * item.unit_amount}" pattern="#0.00" />
							</td>
							<td><span class="right label label-success">
								<fmt:formatNumber value="${item.deposit}" pattern="#0.00" /></span>/
								<fmt:formatNumber value="${item.deposit_open}" pattern="#0.00" />
							</td>
							<td><fmt:formatNumber value="${item.money}" pattern="#0.00" /></td>
							<td>
								<c:if test="${item.state == 'submitted'}">
									<c:choose>
									   <c:when test="${(item.amount_close + item.profit + item.deposit) >= item.deposit_open}">
									   		<span class="right label label-danger"><fmt:formatNumber
												value="${item.amount_close + item.profit + item.deposit - item.deposit_open}"
												pattern="#0.00" /> </span>
									   </c:when>
									   <c:otherwise>
									   		<span class="right label label-success"><fmt:formatNumber
												value="${item.amount_close + item.profit + item.deposit - item.deposit_open}"
												pattern="#0.00" /> </span>
									   </c:otherwise>
									</c:choose>
								</c:if> 
								<c:if test="${item.state != 'submitted'}">
									<c:choose>
									   <c:when test="${(item.amount_close + item.deposit) >= item.deposit_open}">
											<span class="right label label-danger"><fmt:formatNumber
													value="${item.amount_close + item.deposit - item.deposit_open}" 
													pattern="#0.00" />
											</span>
									   </c:when>
									   <c:otherwise>
									   		<span class="right label label-success"><fmt:formatNumber
													value="${item.amount_close + item.deposit - item.deposit_open}" 
													pattern="#0.00" />
											</span>
									   </c:otherwise>
									</c:choose>
								</c:if>
							</td>
							<td>
								<c:if test="${item.state == 'submitted'}">
									持仓
								</c:if> 
								<c:if test="${item.state == 'created'}">
									<span class="right label label-success">已平仓</span>
								</c:if>
							</td>
							<!-- <td><s:date name="createTime" format="MM-dd HH:mm:ss " /></td> -->
							<td>${item.createTime}</td>
							<!-- <td><s:date name="closeTime" format="MM-dd HH:mm:ss " /></td> -->
							<td>${item.closeTime}</td>
							
							<td>
							
								<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
											 || security.isResourceAccessible('OP_FOREVER_CONTRACT_ORDER_OPERATE')}">
								
									<div class="btn-group">
										<button type="button" class="btn btn-light">操作</button>
										<button type="button" class="btn btn-light dropdown-toggle"
											data-toggle="dropdown" aria-expanded="false">
											<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
										</button>
										<ul class="dropdown-menu" role="menu">
										
											<c:if test="${item.state == 'submitted'}">
												<li><a href="javascript:onclose('${item.order_no}')">平仓</a></li>
											</c:if>
											
										</ul>
									</div>
									
								</c:if>
								
							</td>
							
						</tr>
						<%-- </s:iterator> --%>
					</c:forEach>
				</tbody>

			</table>
			
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

			<%@ include file="include/page_simple.jsp"%>

			<!-- <nav> -->
		</div>

	</div>
	<!-- End Panel -->

</div>

	<script type="text/javascript">
		function setState(state) {
			document.getElementById("status_para").value = state;
			document.getElementById("queryForm").submit();
		}
		
		function getallname(name){
			$("#usernallName").html(name);
			$("#user_all_name_a").attr("href","https://etherscan.io/address/"+name);
			$("#net_form").modal("show");
		}
	</script>