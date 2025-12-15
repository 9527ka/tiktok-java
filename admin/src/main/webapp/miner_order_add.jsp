<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>
	<%@ include file="include/loading.jsp"%>
	<div class="ifr-dody">
		<div class="ifr-con">
			<h3>矿机订单</h3>
			<%@ include file="include/alert.jsp"%>
			<form action="<%=basePath%>normal/adminMinerOrderAction!list.action" method="post" id="queryForm">
			
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="name_para" id="name_para" value="${name_para}">
				
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							矿机订单新增
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminMinerOrderAction!addOrder.action"
								method="post" name="mainForm" id="mainForm">
								
								<input type="hidden" name="id" id="id" value="${id}" />
								<input type="hidden" name="session_token" id="session_token" value="${session_token}" />
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">矿机</label>
									<div class="col-sm-4">
	                                    <select id="para_minerid" name="para_minerid" class="form-control " >
	                                    	<option value="">--选择矿机--</option>
											<c:forEach items="${miner_name_map}" var="item">
												<option value="${item.key}">${item.value}</option>
											</c:forEach>
										</select>
									</div>
								</div>
								
								<c:forEach items="${miner_list}" var="item" varStatus="stat">
									<div class="form-group miner_range" style="display:none" id="${item.id}">
									
										<label class="col-sm-2 control-label form-label">投资金额区间</label>
										
										<div class="col-sm-4">
											<div class="input-group ">
											
												<input readOnly id="investment_min" name="investment_min" class="form-control " value="${item.investment_min}" />
											 	<div class="input-group-addon">--</div>
											 	
											 	<c:choose>
													<c:when test="${item.test}">
														<input disabled id="investment_max" name="investment_max" class="form-control " value="${item.investment_max}" />
													</c:when>
													<c:otherwise>
														<c:if test="${item.investment_max=='0'}">
														<input disabled id="investment_max" name="investment_max" class="form-control " value="" />
														</c:if>
														<c:if test="${item.investment_max!='0'}">
														<input disabled id="investment_max" name="investment_max" class="form-control " value="${item.investment_max}" />
														</c:if>
													</c:otherwise>	
												</c:choose>
												
											 	<div class="input-group-addon">USDT</div>
											 	
											</div>
										</div>
										
									</div>
								</c:forEach>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">用户uid</label>
									<div class="col-sm-4">
										<input id="para_uid" name="para_uid" class="form-control " value="${para_uid}"/>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">投资金额</label>
									<div class="col-sm-4">
										<div class="input-group">
										 	<input id="para_amount" name="para_amount" class="form-control " value="${para_amount}"/>
										 	<div class="input-group-addon">USDT</div>
										</div>
									</div>
								</div>
								
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
// 		function minerSelect(obj){
// 			var key = $(obj).val();
// 			$(".miner_range").hide();
// 			$("#"+key).show();			
// 		}
		$('#para_minerid').change(function(event) {
			var key = $("select#para_minerid").val();
			$(".miner_range").hide();
			$("#"+key).show();
	    });
	</script>
	
</body>

</html>
