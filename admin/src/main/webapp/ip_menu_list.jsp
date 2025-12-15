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
			word-wrap: break-word; /* 让内容自动换行 */
			max-width: 200px; /* 设置最大宽度，以防止内容过长 */
		}
	</style>
</head>
<body>
<%@ include file="include/loading.jsp"%>
<script src="include/top.jsp"></script>
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTENT -->
<div class="ifr-dody">

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTAINER -->
	<div class="ifr-con">
		<h3>ip黑名单</h3>
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<%@ include file="include/alert.jsp"%>
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-default">

					<div class="panel-title">查询条件</div>
					<div class="panel-body">

						<form class="form-horizontal" action="<%=basePath%>normal/adminIpMenuAction!list.action" method="post" id="queryForm">
							<input type="hidden" name="pageNo" id="pageNo"
								   value="${pageNo}">
							<div class="col-md-12 col-lg-4">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="ip" name="ip" class="form-control"
												   placeholder="ip地址" value = "${ip}"/>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-2" >
								<input id="startTime" name="startTime" class="form-control "
									   placeholder="开始日期" value="${startTime}" />
							</div>
							<div class="col-md-12 col-lg-2">

								<input id="endTime" name="endTime" class="form-control "
									   placeholder="结束日期" value="${endTime}" />
							</div>

							<div class="col-md-12 col-lg-3" >
								<button type="submit" class="btn btn-light btn-block">查询</button>
							</div>
							<div class="col-md-12 col-lg-12" style="margin-top:10px;"></div>


						</form>

					</div>

				</div>
			</div>
		</div>
		<!-- END queryForm -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->


		<div class="row">


			<div class="col-md-12">
				<div class="panel panel-default">

					<div class="panel-title">查询结果</div>
					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
														 || security.isResourceAccessible('OP_IPMENU_OPERATE')}">
						<a href="javascript:saveIp('${pageNo}')" class="btn btn-light" style="margin-bottom: 10px">新增ip黑名单</a>
					</c:if>

					<div class="panel-body">

						<table class="table table-bordered table-striped" border="1">
							<thead>
							<tr>
								<td>ip地址</td>
								<td>备注</td>
								<td>创建人</td>
								<td>创建时间</td>
								<td width="130px"></td>
							</tr>
							</thead>
							<tbody>
							<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
								<%--										<input type="hidden" name="iconImg" id="iconImg" value = "${item.iconImg}"/>--%>
								<tr>

									<td>${item.ip}</td>
									<td>${item.remark}</td>
									<td>${item.createName}</td>
									<td>${item.create_time}</td>
									<td>
										<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_IPMENU_OPERATE')}">

											<div class="btn-group">
												<button type="button" class="btn btn-light">操作</button>
												<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
													<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
												</button>
												<ul class="dropdown-menu" role="menu">
													<li><a href="javascript:updateIP('${item.ip}','${item.remark}')">编辑</a></li>
													<li><a href="<%=basePath%>normal/adminIpMenuAction!toDelete.action?ip=${item.ip}&pageNo=${pageNo}">删除</a></li>
												</ul>
											</div>
										</c:if>
									</td>

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

	<%@ include file="include/footer.jsp"%>

</div>


<div class="form-group">
	<form action="<%=basePath%>normal/adminIpMenuAction!update.action" method="post" id="succeededForm" onSubmit="return inputNull1(this)">
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="oldIp" id="oldIp" value="${oldIp}">
		<div class="col-sm-1 form-horizontal">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set9" tabindex="-1"
				 role="dialog" aria-labelledby="myModalLabel"
				 aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content" >
						<div class="modal-header">
							<button type="button" class="close"
									data-dismiss="modal" aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">修改ip黑名单</h4>
						</div>

						<div class="modal-header">
							<h4 class="modal-title">IP地址</h4>
						</div>
						<div class="modal-body">
							<div class="form-group" >
								<div class="col-sm-12">
									<input id="newIp"  name="newIp" placeholder="请输入ip地址" rows="20" style=" width: 535px;!important;" maxlength="100">
								</div>
							</div>
						</div>

						<div class="modal-header">
							<h4 class="modal-title">备注</h4>
						</div>
						<div class="modal-body">
							<div class="form-group" >
								<div class="col-sm-12">
									<textarea name="remark" id="remark" class="form-control  input-lg" rows="3"  placeholder="备注原因" maxlength="300">${remark}</textarea>
								</div>
							</div>
						</div>
						<div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn "
									data-dismiss="modal">关闭</button>
							<button id="sub" type="submit"
									class="btn btn-default">确认</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>

<div class="form-group">
	<form action="<%=basePath%>normal/adminIpMenuAction!add.action" method="post" id="succeededForm" onSubmit="return inputNull(this)">
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<div class="col-sm-1 form-horizontal">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set1" tabindex="-1"
				 role="dialog" aria-labelledby="myModalLabel"
				 aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content" >
						<div class="modal-header">
							<button type="button" class="close"
									data-dismiss="modal" aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">新增ip黑名单</h4>
						</div>

						<div class="modal-header">
							<h4 class="modal-title">IP地址</h4>
						</div>
						<div class="modal-body">
							<div class="form-group" >
								<div class="col-sm-12">
									<input id="ip1"  name="ip" placeholder="请输入ip地址" rows="20" style=" width: 535px;!important;">
								</div>
							</div>
						</div>

						<div class="modal-header">
							<h4 class="modal-title">备注</h4>
						</div>
						<div class="modal-body">
							<div class="form-group" >
								<div class="col-sm-12">
									<textarea name="remark" id="remark1" class="form-control  input-lg" rows="3"  placeholder="备注原因" >${remark}</textarea>
								</div>
							</div>
						</div>
						<div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn "
									data-dismiss="modal">关闭</button>
							<button id="sub" type="submit"
									class="btn btn-default">确认</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<%@ include file="include/js.jsp"%><script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>


	<script type="text/javascript">
		<%--setTimeout(function() {--%>
		<%--	start();--%>
		<%--}, 100);--%>

		<%--function start(){--%>
		<%--	var img = $("#iconImg").val();--%>
		<%--	var show_img = document.getElementById('show_img');--%>
		<%--	show_img.src="<%=basePath%>normal/showImg.action?imagePath="+img;--%>
		<%--}--%>

		function saveIp(pageNo){
			$('#ip1').val('');
			$('#pageNo').val(pageNo);
			$('#modal_set1').modal("show");
		}

		function updateIP(ip,remark) {
			$("#newIp").val(ip);
			$("#oldIp").val(ip);
			$("#remark").val(remark);
			$('#modal_set9').modal("show");
		};

		function toDelete(id,pageNo){
			$('#id').val(id);
			$('#pageNo').val(pageNo);
			$('#myModalLabel').html("删除");
			$('#mainform').attr("action","<%=basePath%>mall/goods/delete.action");
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

		function inputNull(){
			let ip = $("#ip1").val();
			if(ip == ""){
				swal({
					title: "请输入ip地址!",
					timer: 2000,
					showConfirmButton: false
				})
				return false;
			}
		}
		function inputNull1(){
			let ip = $("#newIp").val();
			if(ip == ""){
				swal({
					title: "请输入ip地址!",
					timer: 2000,
					showConfirmButton: false
				})
				return false;
			}
		}
	</script>
</body>
</html>