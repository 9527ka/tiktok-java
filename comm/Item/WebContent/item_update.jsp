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
			<h3>交易品种</h3>
				<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminItemAction!list.action" method="post" id="queryForm">
				 <s:hidden name="pageNo" id="pageNo"></s:hidden>
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改交易品种
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
								<form class="form-horizontal" action="<%=basePath%>normal/adminItemAction!update.action" method="post" name="mainForm" id="mainForm">
								  <s:hidden name="id" id="id"></s:hidden>
								<h5>基础信息</h5>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">名称</label>
									<div class="col-sm-5">
										<s:textfield id="name" name="name" cssClass="form-control " />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">代码</label>
									<div class="col-sm-5">
									<s:textfield id="symbol" name="symbol" cssClass="form-control " readonly="true"/>
									</div>
								</div>
							
							    <div class="form-group">
									<label class="col-sm-2 control-label form-label">交易对</label>
									<div class="col-sm-5">
									<s:textfield id="symbol_data" name="symbol_data" cssClass="form-control " readonly="true"/>
									</div>
								</div>
								<h5>交易信息</h5>
							<p class="ballon color1">盈亏公式：(合约总金额 /每张金额)*(涨跌点数/最小变动单位*最小变动单位的盈亏金额)。</p>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">金额/张</label>
									<div class="col-sm-3">
											<s:textfield id="unit_amount" name="unit_amount" cssClass="form-control " placeholder="合约每张金额"/>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">手续费/张</label>
									<div class="col-sm-3">
									
									<s:textfield id="unit_fee" name="unit_fee" cssClass="form-control " placeholder="合约每张金额"/>
									
											
									</div>
								</div>

							<div class="form-group">
									<label class="col-sm-2 control-label form-label">最小变动单位</label>
									<div class="col-sm-3">
											<s:textfield id="pips" name="pips" cssClass="form-control " placeholder="报价变动的最小幅度"/>
											<p>报价变动的最小幅度，行情低于设置单位不会计价盈亏"</p>
									</div>
									
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">最小变动单位的盈亏金额</label>
									<div class="col-sm-3">
											<s:textfield id="pips_amount" name="pips_amount" cssClass="form-control " placeholder="最小变动单位的盈亏金额"/>
									</div>
								</div>
								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<a href="javascript:goUrl(<s:property value="pageNo" />)" class="btn">取消</a> <a
											href="javascript:submit()"  class="btn btn-default">保存</a>
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
</body>
</html>