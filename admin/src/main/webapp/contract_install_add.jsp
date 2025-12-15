<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>

	<%@ include file="include/loading.jsp"%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>交割合约管理</h3>

			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form
				<%-- action="<%=basePath%>normal/adminContractManageAction!contractInstallList.action" --%>
				action="<%=basePath%>normal/adminContractManageAction!listPara.action"
				method="post" id="queryForm">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
				<input type="hidden" name="query_symbol" id="query_symbol" value="${query_symbol}" />
				
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							<c:if test="${futuresPara_id != null}">
								修改合约参数
							</c:if>
							<c:if test="${futuresPara_id == null}">
								新增合约参数
							</c:if>
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>
						
						<p class="ballon color1">交割收益：交割收益计算方式(0~100百分制)。</p>
						
						<div class="panel-body">
							
							<form class="form-horizontal" action="<%=basePath%>normal/adminContractManageAction!addFutures.action"
								method="post" name="mainForm" id="mainForm">
								
								<input type="hidden" name="futuresId" id="futuresId" value="${futuresId}" />
								<input type="hidden" name="futuresPara_id" id="futures_id" value="${futuresPara_id}" />
								<input type="hidden" name="query_symbol" id="query_symbol" value="${query_symbol}" />
								<input type="hidden" name="futuresPara_timeUnitCn" id="futuresPara_timeUnitCn" value="${futuresPara_timeUnitCn}" />
								<input type="hidden" name="futuresPara_unit_max_amount" id="futuresPara_unit_max_amount" value="${futuresPara_unit_max_amount}" />

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">合约代码</label>
									<div class="col-sm-2">
										<!-- <s:select id="symbol" cssClass="form-control "
											name="futuresPara.symbol" list="symbolMap" listKey="key"
											listValue="value" headerKey="" headerValue="--选择合约产品--" /> -->
										<select id="symbol" name="futuresPara_symbol" class="form-control " >
											<option value="">--选择合约产品--</option>
											<c:forEach items="${symbolMap}" var="item">
												<option value="${item.key}" <c:if test="${futuresPara_symbol == item.key}">selected="true"</c:if> >${item.value}</option>
											</c:forEach>
										</select>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">时间</label>
									<div class="col-sm-2">
										<!-- <s:textfield id="time_num" name="futuresPara.timeNum"
											cssClass="form-control " /> -->
										<input id="time_num" name="futuresPara_timeNum" class="form-control " value="${futuresPara_timeNum}" />
									</div>
									<div class="col-sm-2">
										<%-- <s:select id="time_unit" cssClass="form-control "
											style="width: 60px;" name="futuresPara.timeUnit"
											list="#{'second':'秒','minute':'分','hour':'时','day':'时'}"
											listKey="key" listValue="value" /> --%>
										<select id="time_unit" name="futuresPara_timeUnit" class="form-control " style="width: 60px;" >
										   <option value="second" <c:if test="${futuresPara_timeUnit == 'second'}">selected="true"</c:if> >秒</option>
										   <option value="minute" <c:if test="${futuresPara_timeUnit == 'minute'}">selected="true"</c:if> >分</option>
										   <option value="hour" <c:if test="${futuresPara_timeUnit == 'hour'}">selected="true"</c:if> >时</option>
										   <option value="day" <c:if test="${futuresPara_timeUnit == 'day'}">selected="true"</c:if> >天</option>
										</select>
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
											<!-- <s:textfield id="profit_ratio" name="futuresPara.profit_ratio" cssClass="form-control " /> -->
											<input id="profit_ratio" name="futuresPara_profit_ratio" class="form-control " value="${futuresPara_profit_ratio}" />
											<div class="input-group-addon">--</div>
											<!-- <s:textfield id="profit_ratio_max" name="futuresPara.profit_ratio_max" cssClass="form-control " /> -->
											<input id="profit_ratio_max" name="futuresPara_profit_ratio_max" class="form-control " value="${futuresPara_profit_ratio_max}" />
											<div class="input-group-addon">%</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">最低购买金额</label>
									<div class="col-sm-2">
										<!-- <s:textfield id="unit_amount" name="futuresPara.unit_amount" cssClass="form-control " /> -->
										<input id="unit_amount" name="futuresPara_unit_amount" class="form-control " value="${futuresPara_unit_amount}" />
									</div>
								</div>
								
								<!--  <p class="ballon color1">最高购买金额为0时表示不限制。</p>
								<div class="form-group">
								<label class="col-sm-2 control-label form-label">最高购买金额</label>
								<div class="col-sm-2">
								<s:textfield id="unit_max_amount" name="futuresPara.unit_max_amount" cssClass="form-control "/>
								</div>
								</div>
								-->
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">手续费</label>
									<div class="col-sm-2">
										<div class="input-group">
											<!-- <s:textfield id="unit_fee" name="futuresPara.unit_fee" cssClass="form-control " /> -->
											<input id="unit_fee" name="futuresPara_unit_fee" class="form-control " value="${futuresPara_unit_fee}" />
											<div class="input-group-addon">%</div>
										</div>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">资金密码</label>
									<div class="col-sm-3">
										<!-- <s:textfield id="login_safeword" name="login_safeword"
											type="password" cssClass="form-control "
											placeholder="请输入登录人资金密码" /> -->
										<input id="login_safeword" name="login_safeword"
											type="password" class="form-control "
											placeholder="请输入登录人资金密码" />
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
										<a href="javascript:goUrl(${pageNo})" class="btn">取消</a> 
										<a href="javascript:submit()" class="btn btn-default">保存</a>
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
		$(function() {
			var id = $('#futures_id').val();
			if (typeof (id) != 'undefined' && id.length > 0) {
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
