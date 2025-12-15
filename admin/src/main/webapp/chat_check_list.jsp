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

	<style>
		.truncate-text {
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
			max-width: 150px; /* 调整最大宽度根据需要 */
			cursor: pointer; /* 鼠标悬停时显示手形光标 */
		}

		.truncate-text:hover {
			overflow: visible; /* 鼠标悬停时显示完整文本 */
			max-width: none;   /* 鼠标悬停时取消最大宽度限制 */
			white-space: normal; /* 鼠标悬停时允许文本换行 */
		}

	</style>
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
		<h3>买家对话审核</h3>
		<%@ include file="include/alert.jsp"%>

		<form action="<%=basePath%>chat/auditList.action" method="post"
			  id="submitFrom">
			<%--				<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}"/>--%>
			<input type="hidden" id="partyId" name="partyId" value="${partyId}"/>
		</form>

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-default">

					<div class="panel-title">查询条件</div>
					<div class="panel-body">

						<form class="form-horizontal"
							  action="<%=basePath%>chat/auditList.action"
							  method="post" id="queryForm">

							<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
							<input type="hidden" name="returnStatus" id="returnStatus" value="${returnStatus}">

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="userCode" name="userCode"
												   class="form-control " placeholder="用户ID" value="${userCode}" />
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="email" name="email"
												   class="form-control " placeholder="邮箱" value="${email}" />
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="phone" name="phone"
												   class="form-control " placeholder="手机号" value="${phone}" />
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<select id="auditStatus" name="auditStatus" class="form-control">
												<option value="">审核状态</option>
												<option value="0" <c:if test="${auditStatus == '0'}">selected="true"</c:if> >未审核</option>
												<option value="1" <c:if test="${auditStatus == '1'}">selected="true"</c:if> >白名单</option>
												<option value="-1" <c:if test="${auditStatus == '-1'}">selected="true"</c:if> >黑名单</option>
											</select>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-2">
								<button type="submit" class="btn btn-light btn-block">查询</button>
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
					<button type="button" onclick="submitFrom();"
							class="btn btn-default " style="margin-bottom: 10px; margin-bottom: 10px"><i class="fa fa-pencil"></i>刷新</button>

					<div class="panel-body">

						<table class="table table-bordered table-striped">

							<thead>
							<tr>
								<td>用户ID</td>
								<td>用户邮箱</td>
								<td>手机号</td>
								<td>账号类型</td>
								<td>审核状态</td>
								<td>店铺名称</td>
								<td>店铺ID</td>
								<td>消息内容</td>
								<td>会话发起时间</td>
								<!-- <td width="130px"></td> -->
								<td width="200px"></td>
							</tr>
							</thead>

							<tbody>
							<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
								<tr>
									<td>
											${item.usercode}
									</td>
									<td>${item.email}</td>
									<td>${item.phone}</td>
									<td>
										<c:if test="${item.roleName == 'GUEST'}">
											<span class="right label label-warning">演示账号</span>
										</c:if>
										<c:if test="${item.roleName == 'MEMBER'}">
											<span class="right label label-success">正式账号</span>
										</c:if>
									</td>
									<td>
										<c:if test="${item.chat_audit=='-1'}">
											<span class="right label label-warning">已加入黑名单</span>
										</c:if>
										<c:if test="${item.chat_audit=='1'}">
											<span class="right label label-success">已加入白名单</span>
										</c:if>
										<c:if test="${item.chat_audit=='0'}">
											<span class="right label label-danger">未审核</span>
										</c:if>
									</td>

									<td>${item.sellerName}</td>
									<td>${item.sellerCode}</td>
									<td>
										<div class="truncate-text" title="${item.content}">${item.content}</div>
									</td>

									<td>${item.create_time}</td>
									<td>

										<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_CHAT_AUDIT_OPERATE')}">


											<div class="btn-group">
												<button type="button" class="btn btn-light">操作</button>
												<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
													<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
												</button>
												<ul class="dropdown-menu" role="menu">
													<li><a href="<%=basePath%>chat/auditchat.action?uuid=${item.uuid}&chatAudit=1&pageNo=${pageNo}">加入白名单</a></li>
													<li><a href="<%=basePath%>chat/auditchat.action?uuid=${item.uuid}&chatAudit=-1&pageNo=${pageNo}">加入黑名单</a></li>
												</ul>
											</div>

										</c:if>

									</td>

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

	<!-- 模态框 -->
	<div class="form-group">

		<form action="<%=basePath%>chat/auditchat.action"
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

	function submitFrom(){
		$("#queryForm").submit();
	}

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
