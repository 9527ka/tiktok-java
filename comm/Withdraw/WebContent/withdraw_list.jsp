3<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="include/head.jsp"%>
</head>
<body>
	<%@ include file="include/loading.jsp"%>
	<%@ include file="include/top.jsp"%>
	<%@ include file="include/menu_left.jsp"%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="content">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="container-default">
			<h3>提现</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminWithdrawAction!list.action"
								method="post" id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
                                 <s:hidden name="succeeded_para"></s:hidden>
								<div class="col-md-12 col-lg-6">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<s:textfield id="name_para" name="name_para"
													cssClass="form-control " placeholder="用户" />
											</div>
										</div>
									</fieldset>
								</div>


								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>
								
								<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li><a href="javascript:setState('')"> 全部</a></li>
												<li><a href="javascript:setState(0)"> 未处理</a></li>
												<li><a href="javascript:setState(1)"> 通过申请</a></li>
												<li><a href="javascript:setState(2)"> 驳回申请</a></li>


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
										<td>用户</td>
										<td>数量</td>
										<td>手续费</td>
										<td>提现方式</td>
										<td>货币（OTC）</td>
										<td>到账金额（兑换货币）</td>
										<td>账号信息</td>
										<td>收款码</td>
										<td>状态</td>
										<td>驳回原因</td>
										<td>时间</td>
										<td width="150px"></td>

									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
											<td><s:property value="username" /></td>
											<td><fmt:formatNumber value="${volume}" pattern="#0.00" /></td>
											<td><fmt:formatNumber value="${amount_fee}"
													pattern="#0.00" /></td>
											<td><s:property value="method_map.get(method)" /></td>
											<td><s:property value="currency" /></td>
											<td><fmt:formatNumber value="${amount}" pattern="#0.00" /></td>


											<td><s:if test="method=='bank'">
													<s:property
														value="'[银行]'+bank+'[银行卡号]'+account+'[账户]'+username+'[开户行]'+deposit_bank" />
												</s:if> <s:if test="method=='alipay'">
													<s:property value="'[账户]'+account+'[姓名]'+username" />
												</s:if> <s:if test="method=='weixin'">
													<s:property value="'[账户]'+account+'[姓名]'+username" />
												</s:if> <s:if test="method=='paypal'">
													<s:property value="'[账户]'+account+'[姓名]'+username" />
												</s:if> <s:if test="channel=='usdt'">
                                    [区块链地址]<s:property value="address" />
												</s:if></td>
											<td></td>

											<td><s:if test="succeeded==0">处理中</s:if> <s:if
													test="succeeded==3">处理中...</s:if> <s:if test="succeeded==1">
													<span class="right label label-success">已处理</span>
												</s:if>
												<s:if test="succeeded==2">驳回</s:if></td>
											<td><s:property value="failure_msg" /></td>
											<td><s:date name="createTime" format="yyyy-MM-dd " /></td>
											<td>
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<s:if test="succeeded == 0">
															<li><a
																href="javascript:handel('<s:property value="id" />')">通过申请（手动打款）</a></li>
														</s:if>

														<s:if test="succeeded != 2">
															<li><a
																href="javascript:reject('<s:property value="id" />')">驳回申请</a></li>
														</s:if>
													</ul>
												</div>
											</td>
										</tr>
									</s:iterator>

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





	<%@ include file="include/js.jsp"%>



	<script type="text/javascript">
		function reject_confirm() {
			swal({
				title : "是否确认驳回?",
				text : "驳回后款项返回账户",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("onreject").submit();
			});

		};

		function reject(id) {
			$("#id_reject").val(id);
			$('#modal_reject').modal("show");
		};
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
					<form
						action="<%=basePath%>normal/adminWithdrawAction!reject.action"
						method="post" id="onreject">
						<input type="hidden" name="pageNo" id="pageNo"
							value="${param.pageNo}">
						<s:hidden name="name_para" id="name_para"></s:hidden>
						<s:hidden name="succeeded_para"></s:hidden>
						<s:hidden name="id" id="id_reject"></s:hidden>
						<s:textarea name="failure_msg" id="failure_msg"
							cssClass="form-control  input-lg" rows="2" cols="10"
							placeholder="驳回原因" />
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-default"
						onclick="reject_confirm()">驳回代付申请</button>
				</div>
			</div>
		</div>
	</div>

	<!-- End Moda Code -->


	<form
		action="<%=basePath%>normal/adminWithdrawAction!success.action"
		method="post" id="success">
		<input type="hidden" name="pageNo" id="pageNo" value="${param.pageNo}">
		<s:hidden name="name_para" id="name_para"></s:hidden>
		<s:hidden name="succeeded_para"></s:hidden>
		<s:hidden name="id" id="id_success"></s:hidden>

	</form>
	<script type="text/javascript">
		function handel(id) {
			$("#id_success").val(id);
			swal({
				title : "是否确认通过申请?",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("success").submit();
			});

		}
	</script>
<script type="text/javascript">
		function setState(state){
    		document.getElementById("succeeded_para").value=state;
    		document.getElementById("queryForm").submit();
		}
	</script>

</body>
</html>