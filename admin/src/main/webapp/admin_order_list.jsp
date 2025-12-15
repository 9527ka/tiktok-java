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
		.sweet-alert p {
			color: #ef4836;
			font-size: 20px;
			position: relative;
			text-align: inherit;
			float: none;
			margin: 0;
			padding: 0;
			line-height: normal;
		}
	</style>
</head>
<body class="ifr-dody">
<%@ include file="include/loading.jsp"%>

<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTENT -->
<div class="ifr-con">

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTAINER -->
	<div class="container-default">
		<h3>订单列表</h3>
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<%@ include file="include/alert.jsp"%>
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-default">

					<div class="panel-title">查询条件</div>
					<div class="panel-body">

						<form class="form-horizontal"
							  action="<%=basePath%>mall/order/list.action"
							  method="post" id="queryForm">
							<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
							<input type="hidden" name="status" id="status" value="${status}">

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">

											<input id="id" name="id" class="form-control "
												   placeholder="订单号" value="${id}" />
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">

											<input id="userCode" name="userCode" class="form-control "
												   placeholder="买家id" value="${userCode}" />
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">

											<input id="sellerCode" name="sellerCode" class="form-control "
												   placeholder="卖家id" value="${sellerCode}" />
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
							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="contacts" name="contacts" class="form-control"
												   placeholder="收货人" value = "${contacts}"/>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2" >
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<select id="orderStatus" name="orderStatus" class="form-control">
												<option value="">订单类型</option>
												<option value="MEMBER" <c:if test="${orderStatus == 'MEMBER'}">selected="true"</c:if> >真实订单</option>
												<option value="GUEST" <c:if test="${orderStatus == 'GUEST'}">selected="true"</c:if> >虚拟订单</option>
											</select>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2" style="margin-top: 10px">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<select id="payStatus" name="payStatus" class="form-control">
												<option value="-2">支付状态</option><!-- 0=待开奖1=已经开奖 2-已派奖 3-退本金 -->
												<option value="0" <c:if test="${payStatus == '0'}">selected="true"</c:if> >待支付</option>
												<option value="1" <c:if test="${payStatus == '1'}">selected="true"</c:if> >已支付</option>
											</select>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2" style="margin-top: 10px">
								<input id="startTime" name="startTime" class="form-control "
									   placeholder="开始日期" value="${startTime}" />
							</div>
							<div class="col-md-12 col-lg-2" style="margin-top: 10px">

								<input id="endTime" name="endTime" class="form-control "
									   placeholder="结束日期" value="${endTime}" />
							</div>


							<div class="col-md-12 col-lg-2" style="margin-top: 10px">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="sellerName" name="sellerName" class="form-control"
												   placeholder="店铺名" value = "${sellerName}"/>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2" style="margin-top: 10px">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<select id="sellerRoleName" name="sellerRoleName" class="form-control">
												<option value="">店铺类型</option>
												<option value="MEMBER" <c:if test="${sellerRoleName == 'MEMBER'}">selected="true"</c:if> >真实店铺</option>
												<option value="GUEST" <c:if test="${sellerRoleName == 'GUEST'}">selected="true"</c:if> >演示店铺</option>
											</select>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-2" style="margin-top: 10px">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<select id="purchTimeOutStatus" name="purchTimeOutStatus" class="form-control">
												<option value="">采购超时</option>
												<option value="0" <c:if test="${purchTimeOutStatus == '0'}">selected="true"</c:if> >未超时</option>
												<option value="1" <c:if test="${purchTimeOutStatus == '1'}">selected="true"</c:if> >已超时</option>
											</select>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-2" style="margin-top: 10px">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<select id="isDelete" name="isDelete" class="form-control">
												<option value="">订单删除</option>
												<option value="0" <c:if test="${isDelete == '0'}">selected="true"</c:if> >未删除</option>
												<option value="1" <c:if test="${isDelete == '1'}">selected="true"</c:if> >已删除</option>
											</select>
										</div>
									</div>
								</fieldset>
							</div>


							<div class="col-md-12 col-lg-2" style="margin-top: 10px;">
								<button type="submit" class="btn btn-light btn-block">查询</button>
							</div>


							<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
								<div class="mailbox clearfix">
									<div class="mailbox-menu">
										<ul class="menu">
											<li><a href="javascript:setStatus(-2)"> 全部订单</a></li>
											<li><a href="javascript:setStatus(0)"> 待付款</a></li>
											<li><a href="javascript:setStatus(1)"> 待采购</a></li>
											<li><a href="javascript:setStatus(2)"> 待发货</a></li>
											<li><a href="javascript:setStatus(3)"> 待收货</a></li>
											<li><a href="javascript:setStatus(4)"> 已收货</a></li>
											<li><a href="javascript:setStatus(5)"> 已评价</a></li>
											<li><a href="javascript:setStatus(6)"> 已退款</a></li>
											<li><a href="javascript:setStatus(-1)"> 已取消</a></li>
										</ul>
									</div>
								</div>
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

					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
									 || security.isResourceAccessible('OP_MALL_ORDER_OPERATE')}">
						<a href="javascript:shipCol()"
						   class="btn btn-light" style="margin-bottom: 12px"><i
								class="fa fa-pencil"></i>批量发货</a>
						<a href="javascript:freedCol()"
						   class="btn btn-light" style="margin-bottom: 12px"><i
								class="fa fa-pencil"></i>释放佣金</a>
						<a href="javascript:receiptCol()"
						   class="btn btn-light" style="margin-bottom: 12px"><i
								class="fa fa-pencil"></i>确认收货</a>
						<a href="javascript:cancelOrder()"
						   class="btn btn-light" style="margin-bottom: 12px"><i
								class="fa fa-pencil"></i>批量退货</a>
						<a href="javascript:manualReceipt()"
						   class="btn btn-light" style="margin-bottom: 12px"><i
								class="fa fa-pencil"></i>标记手动收货</a>
						<a href="javascript:manualShip()"
						   class="btn btn-light" style="margin-bottom: 12px"><i
								class="fa fa-pencil"></i>标记手动发货</a>
						<a href="javascript:isDelete(1)"
						   class="btn btn-light" style="margin-bottom: 12px"><i
								class="fa fa-pencil"></i>标记删除</a>
						<a href="javascript:isDelete(0)"
						   class="btn btn-light" style="margin-bottom: 12px"><i
								class="fa fa-pencil"></i>标记取消删除</a>
					</c:if>

					<div class="panel-body">

						<table class="table table-bordered table-striped">
							<thead>
							<%--									<tr style="height:32px;">--%>
							<td>
								<input id="selAll" type="checkbox" />
							</td>
							<td>订单编号</td>
							<td>店铺名</td>
							<td>收货人姓名</td>
							<td>订单类型</td>
							<td>店铺类型</td>
							<td>买家id</td>
							<td>卖家id</td>
							<td>采购价格</td>
							<td>订单金额</td>
							<td>利润</td>
							<td>支付状态</td>
							<td>订单状态</td>
							<td>利润发放</td>
							<td>手动收货</td>
							<td>手动发货</td>
							<td>采购</td>
							<td style="text-align: center">采购时间</td>
							<td style="text-align: center">创建时间</td>
							<td>订单删除</td>
							<td width="130px"></td>
							</tr>
							</thead>
							<tbody>
							<c:forEach items="${page.getElements()}" var="item"
									   varStatus="stat">
							<tr>
								<td>
									<input name="checkbox" type="checkbox" value="${item.id}">
								</td>
								<td>${item.id}</td>
								<td>${item.sellerName}</td>
								<td>${item.contacts}</td>
								<td>
									<c:if test="${item.roleName=='GUEST'}">
										<span class="right label label-danger">虚拟订单</span>
									</c:if>
									<c:if test="${item.roleName=='MEMBER'}">
										<span class="right label label-success">真实订单</span>
									</c:if>
								</td>
								<td>
									<c:if test="${item.sellerRoleName=='GUEST'}">
										<span class="right label label-danger">演示店铺</span>
									</c:if>
									<c:if test="${item.sellerRoleName=='MEMBER'}">
										<span class="right label label-success">真实店铺</span>
									</c:if>
								</td>
								<td>${item.userCode}</td>
								<td>${item.sellerCode}</td>
								<td>${item.systemPrice}</td>
								<td> <fmt:formatNumber value="${item.prizeReal}" pattern="#0.00"/></td>
								<td> <fmt:formatNumber value="${item.profit}" pattern="#0.00"/></td>
								<td>
									<c:if test="${item.payStatus=='0'}">
										<span class="right label label-danger">未支付</span>
									</c:if>
									<c:if test="${item.payStatus=='1'}">
										<span class="right label label-success">已支付</span>
									</c:if>
								</td>
								<td>
									<c:if test="${item.status =='-1'}">
										已取消
									</c:if>
									<c:if test="${item.status =='0'}">
										待付款
									</c:if>
									<c:if test="${item.status =='1'}">
										待采购
									</c:if>
									<c:if test="${item.status =='2'}">
										待发货
									</c:if>
									<c:if test="${item.status =='3'}">
										待收货
									</c:if>
									<c:if test="${item.status =='4'}">
										已收货
									</c:if>
									<c:if test="${item.status =='5'}">
										已评价
									</c:if>
									<c:if test="${item.status =='6'}">
										已退款
									</c:if>
								</td>
								<td>
									<c:choose>
										<c:when test="${item.upTime==100 && item.profitStatus == '0'}">
											<span class="right label label-warning">释放中</span>
										</c:when>
										<c:otherwise>
											<c:if test="${item.profitStatus=='0'}">
												<span class="right label label-danger">未发放</span>
											</c:if>
											<c:if test="${item.profitStatus=='1'}">
												<span class="right label label-success">已发放</span>
											</c:if>
										</c:otherwise>
									</c:choose>


								</td>
								<td>
									<c:choose>
										<c:when test="${item.manualReceiptStatus==1}">
											<span class="right label label-success">是</span>
										</c:when>
										<c:otherwise>
											<span class="right label label-danger">否</span>
										</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${item.manualShipStatus==1}">
											<span class="right label label-success">是</span>
										</c:when>
										<c:otherwise>
											<span class="right label label-danger">否</span>
										</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${item.purchTimeOutStatus==1}">
											<span class="right label label-danger">已超时</span>
										</c:when>
										<c:otherwise>

											<span class="right label label-success">未超时</span>
										</c:otherwise>
									</c:choose>
								</td>
								<td>采购时间：${item.purchTime}<br>超时时间：${item.purchTimeOutTime}
								<td style="text-align: center">${item.createTime}</td>
								<td>
									<c:choose>
										<c:when test="${item.isDelete==1}">
											<span class="right label label-danger">已删除</span>
										</c:when>
										<c:otherwise>
											<span class="right label label-success">未删除</span>
										</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_MALL_ORDER_OPERATE')}">
										<div class="btn-group">
											<button type="button" class="btn btn-light">操作</button>
											<button type="button" class="btn btn-light dropdown-toggle"
													data-toggle="dropdown" aria-expanded="false">
												<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
											</button>

											<ul class="dropdown-menu" role="menu">
												<c:if test="${item.status == '2'}">
													<li class="home-list"><a href="javascript:faHuo('${item.id}','${pageNo}')"><span class="tit-w2">发货</span></a></li>
												</c:if>
												<li class="home-list"><a href="<%=basePath%>mall/order/detailsList.action?id=${item.id}&sellerRoleName=${item.sellerRoleName}&purchTime=${item.purchTime}"><span class="tit-w2">查看订单</span></a></li>
													<%--														<c:if test="${item.income == '0' && item.status == '0'}">--%>
													<%--															<li>--%>
													<%--																<a href="javascript:cancel('${item.id}','${pageNo}','${item.amount}')">取消订单</a>--%>
													<%--															</li>--%>
													<%--														</c:if>--%>
													<%--														<c:if test="${item.status == '0'}">--%>
													<%--															<li>--%>
													<%--																<a href="javascript:thaw('${item.id}','${pageNo}')">关闭订单</a>--%>
													<%--															</li>--%>
													<%--														</c:if>--%>
											</ul>
										</div>
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


	<%--		<form class="form-horizontal"--%>
	<%--			  style="position: relative;"--%>
	<%--			  action="<%=basePath%>mall/order/ship.action"--%>
	<%--			  method="post" name="mainFormss" id="mainFormss">--%>
	<%--			<input type="hidden" name="ids" id="ids" value = "${ids}"/>--%>
	<%--			<input type="hidden" name="pageNo" id="pageNo" value = "${pageNo}"/>--%>

	<%--		</form>--%>
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
						<div class="modal-body">
							<div class="form-group" >
								<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
								<div class="col-sm-4">
									<input id="login_safeword" type="password" name="login_safeword"
										   class="login_safeword" placeholder="请输入登录人资金密码" >
								</div>
							</div>
							<!-- <div class="form-group" style="">

                                <label for="input002" class="col-sm-3 control-label form-label">验证码</label>
                                <div class="col-sm-4">
                                    <input id="email_code" type="text" name="email_code"
                                    class="login_safeword" placeholder="请输入验证码" >
                                </div>
                                <div class="col-sm-4">
                                    <a id="update_email_code_button" href="javascript:updateSendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
                                </div>
                            </div> -->
							<%--								<div class="form-group" >--%>
							<%--									<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>--%>
							<%--									<div class="col-sm-4">--%>
							<%--										<input id="google_auth_code"  name="google_auth_code"--%>
							<%--											   placeholder="请输入谷歌验证码" >--%>
							<%--									</div>--%>
							<%--								</div>--%>
						</div>
						<div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn "
									data-dismiss="modal">关闭</button>
							<button id="sub" type="submit"
									class="btn btn-default">确认</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal -->
			</div>
		</div>
	</form>
</div>
<%@ include file="include/js.jsp"%>
<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>


<script type="text/javascript">


	function cancel(id,pageNo,amount){
		$('#sid').val(id);
		$('#pageNo').val(pageNo);
		$('#myModalLabel').html("确认取消订单吗？ 取消后将投资金额 " + amount+ " 回退至用户余额");
		$('#mainform').attr("action","<%=basePath%>adminOrder/cancel.action");

		$('#modal_succeeded').modal("show");

	}
	function setStatus(status) {
		document.getElementById("status").value = status;
		document.getElementById("queryForm").submit();
	}

	<%--function freeze(id,pageNo){--%>
	<%--	$('#sid').val(id);--%>
	<%--	$('#pageNo').val(pageNo);--%>
	<%--	$('#type').val(0);--%>
	<%--	$('#myModalLabel').html("确认冻结订单吗？");--%>
	<%--	$('#mainform').attr("action","<%=basePath%>adminOrder/freezeOrThaw.action");--%>
	<%--	$('#modal_succeeded').modal("show");--%>
	<%--}--%>

	function thaw(id,pageNo){
		$('#sid').val(id);
		$('#pageNo').val(pageNo);
		$('#myModalLabel').html("确定关闭订单吗？ 解除后，关闭订单保留用户投资产生的资金收益");
		$('#mainform').attr("action","<%=basePath%>adminOrder/closure.action");
		$('#modal_succeeded').modal("show");
	}


	function faHuo(id,pageNo) {
        ajaxLoadModal('<%=basePath%>mall/order/shipment.action?ajax=1&orderNo='+id,{
           modalTitle:'订单发货',
           footer: false,
           width: '50%',
        });
       window.parent.scrollTo({ top: 0, behavior: 'smooth' });
       /**
		swal({
			title : "是否确定发货?",
			text : "",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			window.location.href="<%=basePath%>mall/order/ship.action?id="+id + "&" + "pageNo=" + pageNo;
		});
        */
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


	$("#selAll").on("click", function(){
		var che=$("#selAll").prop('checked');
		if(che){
			$("input[name='checkbox']").prop('checked',true);
		} else {
			$("input[name='checkbox']").prop('checked',false);
		}
	})


	$("input[name='checkbox']").on("click", function(){
		var setFalse=false;// 默认不给全选按钮设置false
		$.each($("input[name='checkbox']"),function(index,item){
			// 如果在普通多选框的循环中发现有false,就需要将全选按钮设置为false
			if(item.checked==false){
				setFalse=true;
			}
		})
		if(setFalse){
			$("#selAll").prop('checked',false);
		} else {// 如果普通按钮都为true, 则全选按钮也赋值为true
			$("#selAll").prop('checked',true);
		}
	})

	function shipCol() {
		let $checkbox = $(":checkbox:checked");
		debugger
		if ($checkbox.length === 0) {
			return;
		}
		let num = $checkbox.length;
		let arr = new Array($checkbox.length);
		for (let i = 0; i < arr.length; i++) {
			arr[i] = $($checkbox[i]).val();
			if (arr[i] == "on"){
				num = $checkbox.length - 1;
			}
		}
		swal({
			title : "已选择" + num + "个订单，是否确认发货？",
			text : "",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			let arr = new Array($checkbox.length);
			for (let i = 0; i < $checkbox.length; i++) {
				arr[i] = $($checkbox[i]).val();
			}
			$.ajax({
				url: "<%=basePath%>mall/order/shipCol.action",
				type: 'POST',
				// contentType: "application/json",
				traditional: true,
				data: {
					'ids': arr
				},
				success: function (data) {
					if (data.code === 200) {
						document.getElementById("queryForm").submit();
						return;
					}
					if (data.code == 500){
						swal({
							title: data.error,
							timer: 2500,
							showConfirmButton: false
						})

					}
				}
			});
		});
	}
	function receiptCol() {
		let $checkbox = $(":checkbox:checked");
		if ($checkbox.length === 0) {
			return;
		}
		let num = $checkbox.length;
		let arr = new Array($checkbox.length);
		for (let i = 0; i < arr.length; i++) {
			arr[i] = $($checkbox[i]).val();
			if (arr[i] == "on"){
				num = $checkbox.length - 1;
			}
		}
		swal({
			title : "已选择" + num + "个订单，是否确认收货？",
			text : "",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			let arr = new Array($checkbox.length);
			for (let i = 0; i < $checkbox.length; i++) {
				arr[i] = $($checkbox[i]).val();
			}
			$.ajax({
				url: "<%=basePath%>mall/order/receiptCol.action",
				type: 'POST',
				// contentType: "application/json",
				traditional: true,
				data: {
					'ids': arr
				},
				success: function (data) {
					if (data.code === 200) {
						document.getElementById("queryForm").submit();
						return;
					}
					if (data.code == 500){
						swal({
							title: data.error,
							timer: 2500,
							showConfirmButton: false
						})

					}
				}
			});
		});
	}
	function cancelOrder() {
		let $checkbox = $(":checkbox:checked");
		if ($checkbox.length === 0) {
			return;
		}
		let num = $checkbox.length;
		let arr = new Array($checkbox.length);
		for (let i = 0; i < arr.length; i++) {
			arr[i] = $($checkbox[i]).val();
			if (arr[i] == "on"){
				num = $checkbox.length - 1;
			}
		}
		swal({
			title : "已选择" + num + "个订单，是否确认取消订单？",
			text : "",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			let arr = new Array($checkbox.length);
			for (let i = 0; i < $checkbox.length; i++) {
				arr[i] = $($checkbox[i]).val();
			}
			$.ajax({
				url: "<%=basePath%>mall/order/cancelOrder.action",
				type: 'POST',
				// contentType: "application/json",
				traditional: true,
				data: {
					'ids': arr
				},
				success: function (data) {
					if (data.code === 200) {
						document.getElementById("queryForm").submit();
						return;
					}
					if (data.code == 500){
						swal({
							title: data.error,
							timer: 2500,
							showConfirmButton: false
						})

					}
				}
			});
		});
	}
	function manualReceipt() {

		debugger
		let $checkbox = $(":checkbox:checked");
		if ($checkbox.length === 0) {
			return;
		}
		let num = $checkbox.length;
		let arr = new Array($checkbox.length);
		for (let i = 0; i < arr.length; i++) {
			arr[i] = $($checkbox[i]).val();
			if (arr[i] == "on"){
				num = $checkbox.length - 1;
			}
		}
		swal({
			title : "已选择" + num + "个订单，是否确认手动收货？",
			text : "",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			let arr = new Array($checkbox.length);
			for (let i = 0; i < $checkbox.length; i++) {
				arr[i] = $($checkbox[i]).val();
			}
			$.ajax({
				url: "<%=basePath%>mall/order/manualReceipt.action",
				type: 'POST',
				// contentType: "application/json",
				traditional: true,
				data: {
					'ids': arr
				},
				success: function (data) {
					if (data.code === 200) {
						document.getElementById("queryForm").submit();
						return;
					}
					if (data.code == 500){
						swal({
							title: data.error,
							timer: 2500,
							showConfirmButton: false
						})

					}
				}
			});
		});
	}
	function manualShip() {

		debugger
		let $checkbox = $(":checkbox:checked");
		if ($checkbox.length === 0) {
			return;
		}
		let num = $checkbox.length;
		let arr = new Array($checkbox.length);
		for (let i = 0; i < arr.length; i++) {
			arr[i] = $($checkbox[i]).val();
			if (arr[i] == "on"){
				num = $checkbox.length - 1;
			}
		}
		swal({
			title : "已选择" + num + "个订单，是否标记手动发货？",
			text : "",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			let arr = new Array($checkbox.length);
			for (let i = 0; i < $checkbox.length; i++) {
				arr[i] = $($checkbox[i]).val();
			}
			$.ajax({
				url: "<%=basePath%>mall/order/manualShip.action",
				type: 'POST',
				// contentType: "application/json",
				traditional: true,
				data: {
					'ids': arr
				},
				success: function (data) {
					if (data.code === 200) {
						document.getElementById("queryForm").submit();
						return;
					}
					if (data.code == 500){
						swal({
							title: data.error,
							timer: 2500,
							showConfirmButton: false
						})

					}
				}
			});
		});
	}
	function isDelete(type) {
		let $checkbox = $(":checkbox:checked");
		if ($checkbox.length === 0) {
			return;
		}
		let num = $checkbox.length;
		let arr = new Array($checkbox.length);
		for (let i = 0; i < arr.length; i++) {
			arr[i] = $($checkbox[i]).val();
			if (arr[i] == "on"){
				num = $checkbox.length - 1;
			}
		}
		debugger
		if (type === 1){
			swal({
				title : "已选择" + num + "个订单，是否标记删？",
				text : "(标记删除，买家和卖家端隐藏该订单)",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				let arr = new Array($checkbox.length);
				for (let i = 0; i < $checkbox.length; i++) {
					arr[i] = $($checkbox[i]).val();
				}
				$.ajax({
					url: "<%=basePath%>mall/order/deleteOrders.action",
					type: 'POST',
					// contentType: "application/json",
					traditional: true,
					data: {
						'ids': arr,
						'type':type
					},
					success: function (data) {
						if (data.code === 200) {
							document.getElementById("queryForm").submit();
							return;
						}
						if (data.code == 500){
							swal({
								title: data.error,
								timer: 2500,
								showConfirmButton: false
							})

						}
					}
				});
			});
		} else if (type === 0){
			swal({
				title : "已选择" + num + "个订单，是否取消标记删除",
				text : "(标记取消删除，买家和卖家端显示该订单)",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				let arr = new Array($checkbox.length);
				for (let i = 0; i < $checkbox.length; i++) {
					arr[i] = $($checkbox[i]).val();
				}
				$.ajax({
					url: "<%=basePath%>mall/order/deleteOrders.action",
					type: 'POST',
					// contentType: "application/json",
					traditional: true,
					data: {
						'ids': arr,
						'type':type
					},
					success: function (data) {
						if (data.code === 200) {
							document.getElementById("queryForm").submit();
							return;
						}
						if (data.code == 500){
							swal({
								title: data.error,
								timer: 2500,
								showConfirmButton: false
							})

						}
					}
				});
			});
		}
	}
	function freedCol() {
		let $checkbox = $(":checkbox:checked");
		if ($checkbox.length === 0) {
			return;
		}
		let num = $checkbox.length;
		let arr = new Array($checkbox.length);
		for (let i = 0; i < arr.length; i++) {
			arr[i] = $($checkbox[i]).val();
			if (arr[i] == "on"){
				num = $checkbox.length - 1;
			}
		}
		swal({
			title : "已选择" + num + "个订单，是否释放冻结金？",
			text : "",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			let arr = new Array($checkbox.length);
			for (let i = 0; i < $checkbox.length; i++) {
				arr[i] = $($checkbox[i]).val();
			}
			$.ajax({
				url: "<%=basePath%>mall/order/freedCol.action",
				type: 'POST',
				// contentType: "application/json",
				traditional: true,
				data: {
					'ids': arr
				},
				success: function (data) {
					if (data.code === 200) {
						document.getElementById("queryForm").submit();
						return;
					}
					if (data.code == 500){
						swal({
							title: data.error,
							timer: 2000,
							showConfirmButton: false
						})

					}

				}
			});
		});
	}
	// $(".home-list").click(function(e){
	// 	var title = $(this).find(".tit-w2").html();
	// 	var href = $(this).find("a").attr("href");
	// 	window.parent.addTab(title, href);
	// 	e.preventDefault();
	// })
</script>
</body>
</html>