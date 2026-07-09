<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="include/head.jsp"%>
	<style>
		td {
			word-wrap: break-word;
			max-width: 240px;
		}
	</style>
</head>
<body>
	<%@ include file="include/loading.jsp"%>
	<script src="include/top.jsp"></script>

	<!-- START CONTENT -->
	<div class="ifr-dody">

		<input type="hidden" name="session_token" id="session_token" value="${session_token}" />

		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>商家注销申请审核</h3>

			<%@ include file="include/alert.jsp"%>

			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">
						<div class="panel-title">查询条件</div>
						<div class="panel-body">
							<form class="form-horizontal" action="<%=basePath%>normal/adminSellerDestroyApplyAction!list.action" method="post" id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								<div class="col-md-12 col-lg-3">
									<fieldset><div class="control-group"><div class="controls">
										<input id="name_para" name="name_para" class="form-control" placeholder="账号/用户ID/会员ID" value="${name_para}"/>
									</div></div></fieldset>
								</div>
								<div class="col-md-12 col-lg-3">
									<fieldset><div class="control-group"><div class="controls">
										<select id="status_para" name="status_para" class="form-control">
											<option value="">全部状态</option>
											<option value="0" <c:if test="${status_para == '0'}">selected="true"</c:if> >待审核</option>
											<option value="1" <c:if test="${status_para == '1'}">selected="true"</c:if> >已通过</option>
											<option value="2" <c:if test="${status_para == '2'}">selected="true"</c:if> >已拒绝</option>
										</select>
									</div></div></fieldset>
								</div>
								<div class="col-md-12 col-lg-2" style="margin-top: 15px;">
									<input id="start_time" name="start_time" class="form-control" placeholder="开始日期" value="${start_time}" />
								</div>
								<div class="col-md-12 col-lg-2" style="margin-top: 15px;">
									<input id="end_time" name="end_time" class="form-control" placeholder="结束日期" value="${end_time}" />
								</div>
								<div class="col-md-12 col-lg-2" style="margin-top: 15px;">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
			<!-- END queryForm -->

			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">
						<div class="panel-title">查询结果</div>
						<div class="panel-body">
							<table class="table table-bordered table-striped" border="1">
								<thead>
									<tr>
										<td>账号</td>
										<td>店铺名称</td>
										<td>用户ID</td>
										<td>申请原因</td>
										<td>状态</td>
										<td>申请时间</td>
										<td>审核时间</td>
										<td>审核备注/拒绝原因</td>
										<td>审核人</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody style="font-size: 13px;">
								<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
									<tr>
										<td>${item.account}</td>
										<td>${item.shopName}</td>
										<td>${item.usercode}</td>
										<td>${item.applyReason}</td>
										<td>
											<c:if test="${item.status == 0}"><span class="right label label-danger">待审核</span></c:if>
											<c:if test="${item.status == 1}"><span class="right label label-success">已通过</span></c:if>
											<c:if test="${item.status == 2}"><span class="right label label-warning">已拒绝</span></c:if>
										</td>
										<td>${item.applyTime}</td>
										<td>${item.reviewTime}</td>
										<td>${item.reviewRemark}</td>
										<td>${item.reviewerName}</td>
										<td>
											<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
														|| security.isResourceAccessible('OP_SELLER_DESTROY_APPLY_OPERATE')}">
												<c:if test="${item.status == 0}">
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
															<li><a href="javascript:approve('${item.id}')">通过(注销账号)</a></li>
															<li><a href="javascript:reject('${item.id}')">拒绝</a></li>
														</ul>
													</div>
												</c:if>
											</c:if>
										</td>
									</tr>
								</c:forEach>
								</tbody>
							</table>
							<%@ include file="include/page_simple.jsp"%>
						</div>
					</div>
				</div>
			</div>

		</div>
		<!-- END CONTAINER -->

		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->

	<!-- 通过 表单 -->
	<form action="<%=basePath%>normal/adminSellerDestroyApplyAction!approve.action" method="post" id="approveForm">
		<input type="hidden" name="session_token" id="session_token_approve" value="${session_token}">
		<input type="hidden" name="id" id="id_approve" value="">
		<input type="hidden" name="status_para" value="${status_para}">
	</form>

	<!-- 拒绝 模态框 -->
	<div class="modal fade" id="modal_reject" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title">请输入拒绝原因</h4>
				</div>
				<div class="modal-body">
					<form action="<%=basePath%>normal/adminSellerDestroyApplyAction!reject.action" method="post" id="rejectForm">
						<input type="hidden" name="session_token" id="session_token_reject" value="${session_token}">
						<input type="hidden" name="id" id="id_reject" value="">
						<input type="hidden" name="status_para" value="${status_para}">
						<textarea name="review_remark" id="review_remark" class="form-control input-lg" rows="2" placeholder="请输入拒绝原因，200字符以内" maxlength="200"></textarea>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-default" onclick="reject_confirm()">确认拒绝</button>
				</div>
			</div>
		</div>
	</div>

	<%@ include file="include/js.jsp"%>

	<script type="text/javascript">
		function approve(id) {
			$("#id_approve").val(id);
			swal({
				title : "确认通过并注销该账号?",
				text : "通过后将真正注销该商家账号(可由DB恢复)，操作不可在前台撤销",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("approveForm").submit();
			});
		}

		function reject(id) {
			$("#id_reject").val(id);
			$("#review_remark").val("");
			$('#modal_reject').modal("show");
		}

		function reject_confirm() {
			var remark = $("#review_remark").val();
			if (remark.trim() === '') {
				swal({ title: "请输入拒绝原因!", timer: 2000, showConfirmButton: false });
				return false;
			}
			swal({
				title : "是否确认拒绝?",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("rejectForm").submit();
			});
		}

		$(function() {
			$('#start_time').datetimepicker({ format:'yyyy-mm-dd hh:ii:00', minuteStep:1, language:'zh', weekStart:1, todayBtn:1, autoclose:1, todayHighlight:1, startView:2, clearBtn:true });
			$('#end_time').datetimepicker({ format:'yyyy-mm-dd hh:ii:00', minuteStep:1, language:'zh', weekStart:1, todayBtn:1, autoclose:1, todayHighlight:1, startView:2, clearBtn:true });
		});
	</script>
</body>
</html>
