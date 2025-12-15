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
</head>
<body>
	<%@ include file="include/loading.jsp"%>
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>商品列表</h3>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>brush/goods/goodsList.action" method="post"
								id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${pageNo}">
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<input id="name" name="name" class="form-control"
											placeholder="商品名称" value = "${name}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<input id="PName" name="PName" class="form-control"
											placeholder="平台名称" value = "${PName}"/>
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
	
						<div class="panel-title">查询结果</div>

						<form class="form-horizontal" action="<%=basePath%>adminOrder/toDispatchOrder.action" method="post"
							  id="queryForms">
						<div class="panel-body">
								
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td></td>
										<td>封面图</td>
										<td>商品名称</td>
										<td>商品价格</td>
										<td>平台分类</td>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<input type="hidden" name="orderId" id="orderId" value="${id}">
										<input type="hidden" name="iconImg" id="iconImg" value = "${item.iconImg}"/>
										<input type="hidden" name="pageNo1" id="pageNo1" value="${pageNo}">
										<tr>
											<td>
												<input id="check" onclick="choose(this)" name="checkbox" type="checkbox" value="${item.id}">
											</td>
											<td>
												<img width="45px" height="40px" id="show_img" src="<%=basePath%>normal/showImg.action?imagePath=${item.iconImg}"/> 　　
											</td>
									    <td>${item.name}</td>
										<td>${item.prize}</td>
											<td>${item.platformName}</td>

										</tr>
										
									</c:forEach>

								</tbody>
							</table>
							<%@ include file="include/page_simple.jsp"%>
							<nav>
						</div>
						<div class="modal-footer" style="margin-top: 0;">
							<label>　<a href="<%=basePath%>adminOrder/toDispatch.action?id=${order.id}" class="btn btn-light">关闭</a></label> 　　
<%--							<button type="button" class="btn " href="<%=basePath%>brush/goods/goodsList.action?id=${order.id}" data-dismiss="modal">关闭</button>--%>
							<button id="confirm-2" onclick="sure()" class="btn btn-default">确定</button>
						</div>
					</div>
					</form>
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

	<%@ include file="include/js.jsp"%><script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>

	<script type="text/javascript">

		function choose(obj){
			var a = document.getElementsByName("checkbox");
			for(var i = 0; i< a.length; i ++){
				a[i].checked = false;
			}
			obj.checked = true;
		}

		function sure(){
			let $checkbox = $(":checkbox:checked");
			if ($checkbox.length === 0) {
				window.alert("请选中一种商品");
				return;
			}
			let orderId = $("#orderId").val();
			let arr = new Array($checkbox.length);
			for (let i = 0; i < $checkbox.length; i++) {
				arr[i] = $($checkbox[i]).val();
			}
			id = arr[0];
			document.getElementById("queryForms").submit();
		}

		<%--setTimeout(function() {--%>
		<%--	start();--%>
		<%--}, 100);--%>

		<%--function start(){--%>
		<%--	var img = $("#iconImg").val();--%>
		<%--	var show_img = document.getElementById('show_img');--%>
		<%--	show_img.src="<%=basePath%>normal/showImg.action?imagePath="+img;--%>
		<%--}--%>

		function toDelete(id,pageNo){
			$('#id').val(id);
			$('#pageNo').val(pageNo);
			$('#myModalLabel').html("删除");
			$('#mainform').attr("action","<%=basePath%>brush/goods/delete.action");

			$('#modal_succeeded').modal("show");

		}

		$(function() {
			$('#startTime').datetimepicker({
				format : 'yyyy-mm-dd hh:ii:00',
				minuteStep:1,
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true
			});
			$('#endTime').datetimepicker({
				format : 'yyyy-mm-dd hh:ii:00',
				minuteStep:1,
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true
			});

		});
	</script>
</body>
</html>