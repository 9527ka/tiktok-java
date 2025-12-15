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
			<h3>交割合约</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form 
<%-- 				action="<%=basePath%>normal/adminContractManageAction!contractInstallList.action" --%>
				action="<%=basePath%>normal/adminContractManageAction!listPara.action"
				method="post" id="queryForm">
				<s:hidden name="pageNo" id="pageNo"></s:hidden>
				<s:hidden name="query_symbol" id="query_symbol"></s:hidden>
			</form> 
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							<s:if test="futuresPara.id!=null">
								修改合约参数
							</s:if>
							<s:else>
								新增合约参数
							</s:else>
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>
						<p class="ballon color1">交割收益：交割收益计算方式(0~100百分制)。</p>
						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminContractManageAction!addFutures.action"
								method="post" name="mainForm" id="mainForm">
								<s:hidden name="futuresPara.id" id="futures_id" ></s:hidden>
								<s:hidden name="query_symbol" id="query_symbol"></s:hidden>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">合约代码</label>
									<div class="col-sm-2">
									<s:select id="symbol" cssClass="form-control "
											name="futuresPara.symbol" list="symbolMap"
											listKey="key" listValue="value" 
											 headerKey="" headerValue="--选择合约产品--" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">时间</label>
									<div class="col-sm-2">
									<s:textfield id="time_num" name="futuresPara.timeNum" cssClass="form-control " />
									</div>
									<div class="col-sm-2">
									<s:select id="time_unit" cssClass="form-control "  style="width: 60px;"
											name="futuresPara.timeUnit" list="#{'second':'秒','minute':'分','hour':'时','day':'天'}"
											listKey="key" listValue="value"  />
									</div>
								</div>
								<%-- <div class="form-group">
									<label class="col-sm-2 control-label form-label">交割收益</label>
									<div class="col-sm-2">
										<div class="input-group">
										<s:textfield id="username" name="username" cssClass="form-control " />
					                      <div class="">
					                      	<s:select id="enabled" cssClass="form-control input-group-addon"  style="width: 60px;"
											name="enabled" list="#{'s':'秒','m':'分','h':'时','d':'天'}"
											listKey="key" listValue="value" value="enabled" />
					                      </div>
					                    </div>
				                    </div>
			                    </div> --%>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">交割收益</label>
									<div class="col-sm-3">
										<div class="input-group">
										<s:textfield id="profit_ratio" name="futuresPara.profit_ratio" cssClass="form-control " />
										<div class="input-group-addon">--</div>
										 <s:textfield id="profit_ratio_max" name="futuresPara.profit_ratio_max" cssClass="form-control " />
					                      <div class="input-group-addon">%</div>
					                    </div>
				                    </div>
			                    </div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">最低购买金额</label>
									<div class="col-sm-2">
									<s:textfield id="unit_amount" name="futuresPara.unit_amount" cssClass="form-control "/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">手续费</label>
									<div class="col-sm-2">
										<div class="input-group">
											<s:textfield id="unit_fee" name="futuresPara.unit_fee" cssClass="form-control "  />
											<div class="input-group-addon">%</div>
					                    </div>
									</div>
								</div>
								
							
								<%-- <div class="form-group">
									<label class="col-sm-2 control-label form-label">是否锁定</label>
									<div class="col-sm-4">
									
									<s:select id="enabled" cssClass="form-control "
											name="enabled" list="#{true:'正常',false:'业务锁定（登录不受影响）'}"
											listKey="key" listValue="value" value="enabled" />
									</div>
								</div>
								
								
									<div class="form-group">
									<label for="input002" class="col-sm-2 control-label form-label">备注</label>
									<div class="col-sm-5">

										<s:textarea name="remarks" id="remarks"
											cssClass="form-control  input-lg" rows="3" cols="10" />

									</div>
								</div> --%>
								
								
								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<a href="javascript:goUrl(<s:property value="pageNo" />)"
											class="btn">取消</a> <a href="javascript:submit()"
											class="btn btn-default">保存</a>
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
	$(function(){
		var id = $('#futures_id').val();
		if(typeof(id)!='undefined'&&id.length>0){
			$("#symbol").attr("disabled", "disabled");
		}
	})
		function submit() {
		$("#symbol").removeAttr("disabled");
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