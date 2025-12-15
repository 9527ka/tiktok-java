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
											<td>${item.log}</td>
											<td>${item.createTime}</td>
										</tr>
									</c:forEach>
										
								</tbody>
							</table>
							<%@ include file="include/page_simple.jsp"%>
							<nav>
						</div>
					</div>
				</div>
			</div>

		</div>

		<%@ include file="include/footer.jsp"%>

	</div>

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

	

</script>
</body>
</html>