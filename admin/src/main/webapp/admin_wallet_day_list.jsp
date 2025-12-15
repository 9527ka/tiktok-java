<%@ page language="java" pageEncoding="utf-8"%>
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
	<input type="hidden" name="session_token" id="session_token" value="${session_token}" >
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTAINER -->
	<div class="container-default">
		<h3>每日用户存量</h3>
		<%@ include file="include/alert.jsp"%>
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<div class="row">
			<div class="col-md-12">
				<div class=" panel-default">

<%--					<div class="panel-title">查询条件</div>--%>
					<div class="panel-body">

						<form class="form-horizontal"
							  action="<%=basePath%>brush/userMoney/walletDayList.action"
							  method="post" id="queryForm">
							<input type="hidden" name="pageNo" id="pageNo"
								   value="${param.pageNo}">
						</form>

					</div>

<%--				</div>--%>
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
								<td>金额</td>
								<td>创建时间</td>

							</tr>
							</thead>
							<tbody style="font-size: 13px;">
							<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
								<tr>
									<td>${item.amount}</td>
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


	<%@ include file="include/footer.jsp"%>


</div>
<!-- End Content -->
<!-- //////////////////////////////////////////////////////////////////////////// -->





<%@ include file="include/js.jsp"%>

<s:if test='isResourceAccessible("OP_ADMIN_FINANCE")'>
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

			var session_token = $("#session_token").val();
			$("#session_token_reject").val(session_token);
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
							action="<%=basePath%>normal/adminBtc28WithdrawAction!reject.action"
							method="post" id="onreject">
						<input type="hidden" name="pageNo" id="pageNo"
							   value="${param.pageNo}">
						<s:hidden name="name_para" id="name_para"></s:hidden>
						<s:hidden name="succeeded_para"></s:hidden>
						<s:hidden name="id" id="id_reject"></s:hidden>
						<s:hidden name="session_token" id="session_token_reject"></s:hidden>
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
</s:if>
<!-- End Moda Code -->






<script type="text/javascript">
	function withdraw_about(amount,address,img){
		$("#withdraw_amount").val(amount);
		$("#withdraw_address").val(address);

		document.getElementById('withdraw_img_a').href="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
		document.getElementById('withdraw_img').src="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
		$('#modal_withdraw').modal("show");

	}

	function onsucceeded(id){
		var session_token = $("#session_token").val();
		$("#session_token_success").val(session_token);
		$("#id_success").val(id);
		$('#modal_set').modal("show");

	}
	function onsucceededThird(id){
		var session_token = $("#session_token").val();
		$("#session_token_success_third").val(session_token);
		$("#id_success_third").val(id);
		$('#modal_set_third').modal("show");

	}

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