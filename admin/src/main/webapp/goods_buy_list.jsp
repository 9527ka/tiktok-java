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
<body class="ifr-dody">
<%@ include file="include/loading.jsp"%>

<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTENT -->
<div class="ifr-con">

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTAINER -->
	<div class="container-default">
		<h3>实物兑换记录</h3>
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<%@ include file="include/alert.jsp"%>
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-default">

					<div class="panel-title">查询条件</div>
					<div class="panel-body">


						<form class="form-horizontal"
							  action="<%=basePath%>invest/goodsBuy/list.action"
							  method="post" id="queryForm">

							<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="userCode" name="userCode" class="form-control"
												   placeholder="用户ID" value = "${userCode}"/>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="userName" name="userName" class="form-control"
												   placeholder="用户账号" value = "${userName}"/>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="phone" name="phone" class="form-control"
												   placeholder="手机号" value = "${phone}"/>
										</div>
									</div>
								</fieldset>
							</div>


							<div class="col-md-12 col-lg-3">
								<fieldset>
									<div class="control-group">
										<div class="controls">

											<input id="id" name="id" class="form-control "
												   placeholder="订单号" value="${id}" />
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-3 ">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<select id="status" name="status" class="form-control">
												<option value="-2">订单状态</option>
												<option value="0" <c:if test="${status == '0'}">selected="true"</c:if> >待发货</option>
												<option value="1" <c:if test="${status == '1'}">selected="true"</c:if> >已发货</option>
												<option value="-1" <c:if test="${status == '-1'}">selected="true"</c:if> >已取消</option>
											</select>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-3" style="margin-top: 10px">
								<input id="startTime" name="startTime" class="form-control "
									   placeholder="开始日期" value="${startTime}" />
							</div>
							<div class="col-md-12 col-lg-3" style="margin-top: 10px">

								<input id="endTime" name="endTime" class="form-control "
									   placeholder="结束日期" value="${endTime}" />
							</div>

							<!-- <div class="col-md-12 col-lg-2" >
                                <button type="submit" class="btn  btn-default btn-block">查询</button>
                            </div> -->
							<div class="col-md-12 col-lg-2" style="margin-top: 10px;">
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
					<%-- <a href="<%=basePath%>normal/adminMinerAction!toAdd.action" class="btn btn-light"
                        style="margin-bottom: 10px"><i class="fa fa-pencil"></i>新增</a> --%>
					<div class="panel-body">

						<table class="table table-bordered table-striped">
							<thead>
								<tr>
									<td>订单号</td>
									<td>会员ID</td>
									<td>会员账号</td>
									<td>手机号</td>
									<td>商品名称</td>
									<td>兑换数量</td>
									<td>支付总积分</td>
									<td>状态</td>
									<td>收货人</td>
									<td>收货人手机号</td>
									<td>收货地址</td>
									<td>备注</td>
									<td>下单时间</td>
									<!-- <td width="130px"></td> -->
									<td width="200px"></td>
								</tr>
							</thead>
							<tbody>
							<c:forEach items="${page.getElements()}" var="item"
									   varStatus="stat">
							<tr>
								<td>${item.id}</td>
								<td>${item.userCode}</td>
								<td>${item.userName}</td>
								<td>${item.Uphone}</td>
								<td>${item.goodsName}</td>
								<td>${item.num}</td>
								<td>${item.payPoint}</td>
								<td>
									<c:if test="${item.status =='0'}">
										<span class="right label label-warning">待发货</span>
									</c:if>
									<c:if test="${item.status =='1'}">
										<span class="right label label-success">已发货</span>
									</c:if>
									<c:if test="${item.status =='-1'}">
										<span class="right label label-danger">已取消</span>
									</c:if>
								</td>
								<td>${item.contacts}</td>
								<td>${item.phone}</td>
								<td>${item.address}</td>
								<td>${item.remark}</td>
								<td>${item.createTime}</td>
								<td>
									<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_GOODSBUY_OPERATE')}">
									<c:if test="${item.status == '0'}">
										<div class="btn-group">
											<button type="button" class="btn btn-light">操作</button>
											<button type="button" class="btn btn-light dropdown-toggle"
													data-toggle="dropdown" aria-expanded="false">
												<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
											</button>

											<ul class="dropdown-menu" role="menu">
<%--												<c:if test="${item.status == '0'}">--%>
													<li>
														<a href="javascript:cancel('${item.id}','${pageNo}',1)">发货</a>
													</li>
<%--												</c:if>--%>
<%--												<c:if test="${item.status == '0'}">--%>
													<li>
														<a href="javascript:cancel('${item.id}','${pageNo}',2)">取消订单</a>
													</li>
<%--												</c:if>--%>
											</ul>
										</div>
									</c:if>
									</c:if>
								</td>

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

	<%@ include file="include/footer.jsp"%>



</div>
<!-- End Content -->
<!-- //////////////////////////////////////////////////////////////////////////// -->

<div class="form-group">
	<form
			action=""
			method="post" id="mainform">
		<input type="hidden" name="pageNo" id="pageNo"
			   value="${pageNo}">
		<input type="hidden" name="sid" id="sid" value="${sid}"/>
		<input type="hidden" name="type" id="type" value="${type}"/>
		<div class="col-sm-1 form-horizontal">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_succeeded" tabindex="-1"
				 role="dialog" aria-labelledby="myModalLabel"
				 aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content" >
						<div class="modal-header">
							<button type="button" class="close"
									data-dismiss="modal" aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">确认调整</h4>
						</div>

						<div class="modal-header">
							<h4 class="modal-title">资金密码</h4>
						</div>
						<div class="modal-body">
							<div class="form-group" >
								<div class="col-sm-4">
									<input id="login_safeword" type="password" name="login_safeword"
										   class="login_safeword" placeholder="请输入登录人资金密码" rows="10">
								</div>
							</div>
						</div>

						<div class="modal-header">
							<h4 class="modal-title">备注</h4>
						</div>
						<div class="modal-body">
							<div class="form-group" >
								<div class="col-sm-12">
									<textarea name="remark" id="remark" class="form-control  input-lg" rows="3"  placeholder="备注原因" >${remark}</textarea>
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
<%@ include file="include/js.jsp"%>
<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
<script>
	$(function () {
		var data = <s:property value="result" escape='false' />;
		console.log(data);
		$("#treeview4").treeview({
			color: "#428bca",
			enableLinks:true,
			nodeIcon: "glyphicon glyphicon-user",
			data: data,
			levels: 4,
		});
	});
</script>


<script type="text/javascript">


	function cancel(id,pageNo,type){
		$('#sid').val(id);
		$('#pageNo').val(pageNo);
		$('#type').val(type);
		if(type == 1){
			$('#myModalLabel').html("发货确认");
		} else {
			$('#myModalLabel').html("确定要取消订单吗？");
		}
		$('#mainform').attr("action","<%=basePath%>invest/goodsBuy/updateStatus.action");

		$('#modal_succeeded').modal("show");

	}

	<%--function freeze(id,pageNo){--%>
	<%--	$('#sid').val(id);--%>
	<%--	$('#pageNo').val(pageNo);--%>
	<%--	$('#type').val(0);--%>
	<%--	$('#myModalLabel').html("确认冻结订单吗？");--%>
	<%--	$('#mainform').attr("action","<%=basePath%>adminOrder/freezeOrThaw.action");--%>
	<%--	$('#modal_succeeded').modal("show");--%>
	<%--}--%>


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