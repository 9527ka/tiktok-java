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
			<h3>交割场控设置</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminProfitAndLossConfigAction!list.action"
				method="post" id="queryForm">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
				<input type="hidden" name="name_para" id="name_para" value="${name_para}" />
				
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							产品交割方向盈亏控制
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminProfitAndLossConfigAction!updateProductProfitLoss.action"
								method="post" name="mainForm" id="mainForm">
								
								<input type="hidden" name="id" id="id" value="${id}" />

								<div class="form-group">
									<label class="col-sm-4 control-label form-label">交割场控指定币种</label>
									<div class="col-sm-3">
										<!-- <s:select id="profit_loss_symbol" cssClass="form-control "
											name="profit_loss_symbol" list="symbol_map" listKey="key"
											listValue="value" value="profit_loss_symbol" headerKey=""
											headerValue="---请选择场控币种---" /> -->
										<select id="profit_loss_symbol" name="profit_loss_symbol" class="form-control " >
											<option value="">---请选择场控币种---</option>
											<c:forEach items="${symbol_map}" var="item">
												<option value="${item.key}" <c:if test="${profit_loss_symbol == item.key}">selected="true"</c:if> >${item.value}</option>
											</c:forEach>
										</select>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-4 control-label form-label">币种交割场控类型(交割场控指定币种未配置则不生效)</label>
									<div class="col-sm-2">
										<%-- <s:select id="profit_loss_type" cssClass="form-control"
											name="profit_loss_type"
											list="#{'buy_profit_sell_loss':'买多盈利并且买空亏损'
											,'sell_profit_buy_loss':'买空盈利并且买多亏损'}"
											listKey="key" listValue="value" value="profit_loss_type"
											headerKey="" headerValue="---请选择场控类型---" /> --%>
										<select id="profit_loss_type" name="profit_loss_type" class="form-control " >
										   <option value="">---请选择场控类型---</option>
										   <option value="buy_profit_sell_loss" <c:if test="${profit_loss_type == 'buy_profit_sell_loss'}">selected="true"</c:if> >买多盈利并且买空亏损</option>
										   <option value="sell_profit_buy_loss" <c:if test="${profit_loss_type == 'sell_profit_buy_loss'}">selected="true"</c:if> >买空盈利并且买多亏损</option>
										</select>
									</div>
								</div>

								<div class="form-group">
									<div class="col-sm-offset-4 col-sm-10">
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
		function submit() {
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
