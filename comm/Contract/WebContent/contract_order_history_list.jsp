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
			<h3>历史永续合约单</h3>
			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">
						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						<s:if test='isResourceAccessible("ADMIN_HISTORY_CONTRACT_ORDER_LIST")'>

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminHistoryContractOrderAction!list.action"
								method="post" id="queryForm">
								<s:hidden name="status_para"></s:hidden>
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
								
								<div class="col-md-12 col-lg-12">
									<div class="col-md-12 col-lg-3">
										<fieldset>
											<div class="control-group">
												<div class="controls">
													<s:textfield id="order_no_para" name="order_no_para"
														cssClass="form-control " placeholder="订单号（完整）" />
												</div>
											</div>
										</fieldset>
									</div>
									<div class="col-md-12 col-lg-3">
										<fieldset>
											<div class="control-group">
												<div class="controls">
												<s:textfield id="name_para" name="name_para" cssClass="form-control " placeholder="用户名、UID"/>
												</div>
											</div>
										</fieldset>
									</div>
									<div class="col-md-12 col-lg-3">
										<fieldset>
											<div class="control-group">
												<div class="controls">
													<s:select id="rolename_para" cssClass="form-control "
														name="rolename_para"
														list="#{'MEMBER':'正式账号合约','GUEST':'演示账号合约'}" listKey="key"
														listValue="value" headerKey="" headerValue="所有合约"
														value="rolename_para" />
												</div>
											</div>
										</fieldset>
									</div>
								</div>
								<div class="col-md-12 col-lg-12" style="margin-top:10px;">
									<div class="col-md-12 col-lg-3">
										<s:textfield id="start_time" name="start_time"
											cssClass="form-control " placeholder="开始日期" />
									</div>
									<div class="col-md-12 col-lg-3">
										<s:textfield id="end_time" name="end_time"
											cssClass="form-control " placeholder="结束日期" />
									</div>
	
									<div class="col-md-12 col-lg-2">
										<button type="submit" class="btn btn-light btn-block">查询</button>
									</div>
								</div>

								 <div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li><a href="javascript:setState('')"> 全部</a></li>
												<li><a href="javascript:setState('submitted')"> 持仓</a></li>
												<li><a href="javascript:setState('created')"> 已平仓</a></li>


											</ul>
										</div>
									</div>
								</div>

							</form>
						</s:if>

						</div>

					</div>
				</div>
			</div>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<%-- <sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
				<div class="row">
					<div class="col-md-12">
						<div class="panel panel-default">
							<div class="panel-title">调整</div>
							<div class="panel-body">

								<div class="col-md-12 col-lg-12">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<s:iterator value="items" status="stat">
													<li><a href="#" data-toggle="modal"
														data-target="#myModal3"
														onclick="showModal('<s:property value="symbol" />','<s:property value="name" />');"
														id='<s:property value="symbol" />'> <s:property
																value="name" /></a></li>
												</s:iterator>
											</ul>
										</div>
									</div>

									<div class="modal fade" id="myModal3" tabindex="-1"
										role="dialog" aria-hidden="true" style="display: none;">
										<div class="modal-dialog modal-sm">
											<div class="modal-content">
												<div class="modal-header">
													<button type="button" class="close" data-dismiss="modal"
														aria-label="Close">
														<span aria-hidden="true">×</span>
													</button>
													<h4 class="modal-title" id="item_name">调整</h4>
												</div>
												<form class="form-horizontal"
													action="<%=basePath%>normal/adminContractOrderAction!adjust.action"
													method="post" id="mainForm">
													<input type="hidden" id="adjust_symbol" name="symbol" />
													<s:hidden name="pageNo"></s:hidden>
													<s:hidden name="status_para"></s:hidden>
													<s:hidden name="start_time"></s:hidden>
													<s:hidden name="end_time"></s:hidden>
													<s:hidden name="rolename_para"></s:hidden>
													<div class="modal-body">
														<div class="form-area">
															请输入正负调整值 <input type="text" id="adjust" name="value"
																class="form-control" placeholder="正负调整值"
																onchange="getValue(2);">
															<button type="button"
																class="btn btn-default btn-icon btn-sm"
																style="margin-top: 6px;" id="adjust_add_button"
																onclick="getValue(0);"></button>
															&nbsp;
															<button type="button"
																class="btn btn-default btn-icon btn-sm"
																style="margin-top: 6px;" id="adjust_sub_button"
																onclick="getValue(1);"></button>
															<div class="form-area">
																生效趋势（秒，0秒为即时生效） <input type="text" id="second"
																	name="second" class="form-control" value="0">
															</div>
														</div>
														<div class="form-area">
															调整值
															<table class="table table-bordered table-striped">
																<thead>
																	<tr>
																		<td>原值</td>
																		<td>调整后</td>
																		<td>累计修正值</td>
																	</tr>
																</thead>
																<tbody>
																	<tr>
																		<td id="adjust_value_before"><span
																			class="label label-danger" id="adjust_value_before"></span></td>
																		<td><span class="label label-danger"
																			id="adjust_value_after"></span></td>
																		<td id="adjust_value"></td>
																	</tr>
															</table>
														</div>
														<div class="form-area">
															生效趋势
															<table class="table table-bordered table-striped">
																<thead>
																	<tr>
																		<td>待生效值</td>
																		<td>时间(秒)</td>
																	</tr>
																</thead>
																<tbody>
																	<tr>
																		<td id="adjust_delay"></td>
																		<td id="adjust_second"></td>
																	</tr>
															</table>
														</div>
													</div>
													<div class="modal-footer">
														<button type="button" class="btn btn-white"
															data-dismiss="modal">取消</button>
														<button type="submit" class="btn btn-danger">确定</button>
													</div>
												</form>
											</div>
										</div>
									</div>
								</div>

							</div>

						</div>
					</div>
				</div>
			</sec:authorize> --%>

			<div class="row" id="history_content">
				<%@ include file="contract_order_history_list_content.jsp"%>
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
		$.fn.datetimepicker.dates['zh'] = {
			days : [ "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" ],
			daysShort : [ "日", "一", "二", "三", "四", "五", "六", "日" ],
			daysMin : [ "日", "一", "二", "三", "四", "五", "六", "日" ],
			months : [ "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月",
					"十月", "十一月", "十二月" ],
			monthsShort : [ "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
					"十一", "十二" ],
			meridiem : [ "上午", "下午" ],
			//suffix:      ["st", "nd", "rd", "th"],  
			today : "今天",
			clear : "清空"
		};

		$(function() {
			$('#start_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			})

			$('#end_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2
			})

		});
	</script>

<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
<s:if test='isResourceAccessible("ADMIN_HISTORY_CONTRACT_ORDER_CLOSE")'>
	<form action="normal/adminHistoryContractOrderAction!close.action" method="post"
		id="onclose">
		<input type="hidden" name="pageNo" id="pageNo" value="${param.pageNo}">
		<s:hidden name="order_no" id="order_no"></s:hidden>
		<s:hidden name="status_para"></s:hidden>
		<s:hidden name="start_time"></s:hidden>
		<s:hidden name="end_time"></s:hidden>
		<s:hidden name="rolename_para"></s:hidden>
	</form>
	<script type="text/javascript">
		/*5轮询读取函数*/
	    /* setInterval(function() {
		   var data = {"pageNo":$("#pageNo").val()
				   ,"rolename_para":$("#rolename_para").val()
				   ,"start_time":$("#start_time").val()
				   ,"end_time":$("#end_time").val()};
			goAjaxUrl($("#ajaxUrl").val(),data);	  
		}, 3000);  */
	
		function onclose(order_no) {
			$("#order_no").val(order_no);
			swal({
				title : "是否确认平仓?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("onclose").submit();
			});

		}
		
		function goAjaxUrl(targetUrl,data){
			$.ajax({
				url:targetUrl,
				data:data,
				type:'get',
				success: function (res) {
				    // 一旦设置的 dataType 选项，就不再关心 服务端 响应的 Content-Type 了
				    // 客户端会主观认为服务端返回的就是 JSON 格式的字符串
//						    console.log(res)
//					    $(".loading").hide();
				    $("#history_content").html(res);
				    /* $('#quote_currency').val(data.quote_currency);
				    $('#base_currency').val(data.base_currency);
				    
				    if(null==data.quote_currency||''==data.quote_currency||typeof(data.quote_currency) == "undefined"){
					    $('.tr_quote:first').attr('style','background:#39ffff;');
				    }else{
					    $('#tr_'+data.quote_currency).attr('style','background:#39ffff;');
				    } */
				  }
			});
		}
	</script>
	</s:if>
</sec:authorize>
	<script type="text/javascript">
		function setState(state){
    		document.getElementById("status_para").value=state;
    		document.getElementById("queryForm").submit();
		}
	</script>

</body>
</html>