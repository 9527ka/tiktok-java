<%@ page language="java" pageEncoding="utf-8"%>
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
			<h3>货币汇率配置</h3>
				<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminExchangeRateAction!list.action" method="post" id="queryForm">
				 <s:hidden name="pageNo" id="pageNo"></s:hidden>
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改货币汇率
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
								<form class="form-horizontal" action="<%=basePath%>normal/adminExchangeRateAction!update.action" method="post" name="mainForm" id="mainForm">
								  <s:hidden name="id" id="id"></s:hidden>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">汇率</label>
									<div class="col-sm-3">
											<s:textfield id="rata" name="rata" cssClass="form-control " />
									</div>
								</div>
								
								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
									<s:if test='isResourceAccessible("ADMIN_EXCHANGE_RATE_UPDATE")'>
										<a href="javascript:goUrl(<s:property value="pageNo" />)" class="btn">取消</a> <a
											href="javascript:submit()"  class="btn btn-default">保存</a>
									</s:if>
									</div>
								</div>

							</form>

						</div>

					</div>
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
	<s:if test='isResourceAccessible("ADMIN_EXCHANGE_RATE_UPDATE")'>
<script type="text/javascript">
   function submit(){
	   swal({
		   title : "是否保存?",
			text : "",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			document.getElementById("mainForm").submit();
		});
	  
  }

	</script>
	</s:if>
</body>
</html>