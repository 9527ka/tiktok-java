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

		<input type="hidden" name="session_token" id="session_token" value="${session_token}" />

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>退货订单</h3>

			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>mall/order/refundList.action"
								method="post" id="queryForm">

								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								<input type="hidden" name="returnStatus" id="returnStatus" value="${returnStatus}">

								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="id" name="id"
													class="form-control " placeholder="订单号（完整）" value="${id}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="userCode" name="userCode"
													class="form-control " placeholder="用户ID" value="${userCode}" />
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3" >
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="orderStatus" name="orderStatus" class="form-control">
													<option value="-2">订单类型</option><!-- 0=待开奖1=已经开奖 2-已派奖 3-退本金 -->
													<option value="0" <c:if test="${orderStatus == '0'}">selected="true"</c:if> >真实订单</option>
													<option value="1" <c:if test="${orderStatus == '1'}">selected="true"</c:if> >内部订单</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3" style="margin-top: 10px;">
									<input id="startTime" name="startTime"
										class="form-control " placeholder="开始日期" value="${startTime}" />
								</div>
								<div class="col-md-12 col-lg-3" style="margin-top: 10px;">
									<input id="endTime" name="endTime"
										class="form-control " placeholder="结束日期" value="${endTime}" />
								</div>

								<!-- <div class="col-md-12 col-lg-2" >
									<button type="submit" class="btn  btn-default btn-block">查询</button>
								</div> -->
								<div class="col-md-12 col-lg-2" style="margin-top: 10px;">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>
								<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li><a href="javascript:setState(-2)"> 全部</a></li>
												<li><a href="javascript:setState(1)"> 待处理</a></li>
												<li><a href="javascript:setState(2)"> 已退款</a></li>
												<li><a href="javascript:setState(3)"> 已拒绝</a></li>
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
										<td>服务单号</td>
										<td>店铺名称</td>
										<td>订单类型</td>
										<td>申请时间</td>
										<td>用户ID</td>
										<td>退款金额</td>
										<td>申请状态</td>
										<td>处理时间</td>
										<td>拒绝原因</td>
										<!-- <td width="130px"></td> -->
										<td width="200px"></td>
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>
													${item.id}
											</td>
											<td>${item.sellerName}</td>
											<td>
												<c:if test="${item.orderStatus=='1'}">
													<span class="right label label-danger">虚拟订单</span>
												</c:if>
												<c:if test="${item.orderStatus=='0'}">
													<span class="right label label-success">真实订单</span>
												</c:if>
											</td>

											<td>${item.refundTime}</td>
											<td>${item.userCode}</td>
											<td><span class="label label-danger">${item.prizeReal}</span></td>

											<td>
												<c:if test="${item.returnStatus=='1'}">
													<span class="right label label-warning">待处理</span>
												</c:if>
												<c:if test="${item.returnStatus=='2'}">
													<span class="right label label-success">已退款</span>
												</c:if>
												<c:if test="${item.returnStatus=='3'}">
													<span class="right label label-danger">已拒绝</span>
												</c:if>
											</td>

											<td>${item.refundDealTime}</td>
											<td>${item.refundRemark}</td>

											<td>

												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_MALL_RORDER_OPERATE')}">
												
													<c:if test="${item.returnStatus == 1}">
													
														<div class="btn-group">
															<button type="button" class="btn btn-light">操作</button>
															<button type="button" class="btn btn-light dropdown-toggle"
																data-toggle="dropdown" aria-expanded="false">
																<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
															</button>
															<ul class="dropdown-menu" role="menu">
															
																<li><a href="javascript:onsucceeded('${item.id}')">确认退款</a></li>
																<li><a href="javascript:reject('${item.id}')">驳回申请</a></li>
																
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
		
		<!-- 模态框 -->		
		<div class="form-group">
		
			<form action="<%=basePath%>mall/order/refund.action"
				method="post" id="succeededForm">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="orderId" id="orderId" value="${orderId}">

				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_set" tabindex="-1" role="dialog"
						aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
							

								<!--  -->
								<div class="modal-header">
									<h4 class="modal-title" id="myModalLabel">资金密码</h4>
								</div>
								
								<div class="modal-body">
									<div class="">
										<input id="safeword" type="password" name="safeword"
											class="form-control" placeholder="请输入资金密码" value="${safeword}" >
									</div>
								</div>

								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
									<button id="sub" type="submit" class="btn btn-default">确认</button>
								</div>
								
							</div>
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
				
			</form>
			
		</div>


		<%@ include file="include/footer.jsp"%>

	</div>
	
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<%@ include file="include/js.jsp"%>

	<script type="text/javascript">

		function setState(state){
			document.getElementById("returnStatus").value=state;
			document.getElementById("queryForm").submit();
		}

		function reject(id) {		
			$("#orderIds").val(id);
			$('#modal_reject').modal("show");
		};


		function onsucceeded(id){
			$("#orderId").val(id);
			$('#modal_set').modal("show");
		}


		function reject_confirm() {
			swal({
				title : "是否确认驳回?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("onreject").submit();
			});
		};
	

		$(function() {
			$('#startTime').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			});
			$('#endTime').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			});
		});

	</script>
	
	<!-- Modal -->	
	<div class="modal fade" id="modal_reject" tabindex="-1" role="dialog"
		aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
			
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">请输入驳回原因</h4>
				</div>
				
				<div class="modal-body">
					<form action="<%=basePath%>mall/order/reject.action"
						method="post" id="onreject">
						<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
						<input type="hidden" name="orderIds" id="orderIds" value="${orderIds}">
						<textarea name="failure_msg" id="failure_msg" class="form-control  input-lg" rows="2" cols="10" placeholder="驳回原因" >${failure_msg}</textarea>
					</form>
				</div>
				
				<div class="modal-footer">
					<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-default" onclick="reject_confirm()">驳回退款订单</button>
				</div>
				
			</div>
		</div>
	</div>
	<!-- End Moda Code -->

	<script type="text/javascript">
	</script>
	
</body>

</html>
