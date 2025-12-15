<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="include/head.jsp"%>
</head>
<body>
	<%@ include file="include/loading.jsp"%>

	<div class="ifr-dody">
		<div class="ifr-con">
			<h3>验证码发送日志</h3>
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminCodeLogAction!list.action"
								method="post" id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								 
								<div class="col-md-12 col-lg-4" >
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="target" name="target"
													class="form-control " placeholder="手机号或邮箱号" value="${target}"/>
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-2" >
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>

							</form>

						</div>

					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>手机号或邮箱号</td>
										<td>日志</td>
										<td width="150px">时间</td>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.target}</td>
											<td><a href="#" onClick="getlog('${item.log_id}');">点击查看</a></td>
											<td>${item.createTime}</td>
										</tr>
									</c:forEach>
										
								</tbody>
							</table>
							<%@ include file="include/page_simple.jsp"%>
							<nav>
						</div>

					</div>
					<!-- End Panel -->

				</div>
			</div>

		</div>
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		
		
		<!-- 模态框 -->
		<!-- <s:if test='isResourceAccessible("OP_ADMIN_FINANCE")'> -->
			<div class="form-group">
				<form action="<%=basePath%>normal/adminCodeLogAction!get_code.action"
					method="post" id="resetForm">
					<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
					<input type="hidden" name="log_id" id="log_id" value="${log_id}">
					<div class="col-sm-1">
						<!-- 模态框（Modal） -->
						<div class="modal fade" id="modal_log" tabindex="-1"
							role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
							<div class="modal-dialog">
								<div class="modal-content">
									<div class="modal-header">
										<button type="button" class="close" data-dismiss="modal"
											aria-hidden="true">&times;</button>
									
									<div class="modal-header">

										<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
									</div>
									<div class="modal-body">
										<div class="">
											<input id="login_safeword" type="password" name="login_safeword"
												class="form-control" placeholder="请输入登录人资金密码">
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
		<!-- </s:if> -->
		
		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->


	<%@ include file="include/js.jsp"%>
	
<script type="text/javascript">
	$(document).ready(function() {
	  $('#date_para').daterangepicker({
			timePicker : true, //显示时间
			timePicker24Hour : true, //时间制
			timePickerSeconds : true, //时间显示到秒
			format : 'YYYY-MM-DD HH:mm:ss',
			startDate : new Date(),
			endDate : new Date(),
			minDate : 1999 - 12 - 12,
			maxDate : 2050 - 12 - 30,
			timePicker : true,
			timePickerIncrement : 1,
	
			locale : {
				applyLabel : "确认",
				cancelLabel : "取消",
				resetLabel : "重置",
	
			}
		}, function(start, end, label) {
	    console.log(start.toISOString(), end.toISOString(), label);
	  });
	});

	function getlog(id) {
		$("#log_id").val(id);
		$('#modal_log').modal("show");

	}

</script>
</body>
</html>