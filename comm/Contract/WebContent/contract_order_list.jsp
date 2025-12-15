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
			<h3>当前永续持仓单</h3>
			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
							<s:if test='isResourceAccessible("ADMIN_CONTRACT_ORDER_CONTENT")'>
							<input type="hidden" value="<%=basePath%>normal/adminContractOrderAction!content.action" id='ajaxUrl'/>
							</s:if>
							<s:if test='isResourceAccessible("ADMIN_CONTRACT_ORDER_LIST")'>
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminContractOrderAction!list.action"
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


							</form>
							</s:if>
							<s:if test='isResourceAccessible("ADMIN_CONTRACT_ORDER_SHOWMODAL")'>
							<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
					                <div class="mailbox clearfix">
					                  <div class="panel-title" style="padding-left: 5px;margin-top: -10px;">操作</div>
					                  <div class="mailbox-menu" style="border-bottom: hidden;">
					                    <ul class="menu">
					
					                     
					                    </ul>
					                  </div>
					                </div>
					              </div>
							
								<div class="col-md-12 col-lg-12" >
									<!-- <div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li><a href="javascript:setState('')"> 全部</a></li>
												<li><a href="javascript:setState('submitted')"> 持仓</a></li>
												<li><a href="javascript:setState('created')"> 已平仓</a></li>


											</ul>
										</div>
									</div> -->
									<div class="col-md-12 col-lg-3">
										<fieldset>
											<div class="control-group">
												<div class="controls" style="margin-left: -15px;">
													<s:select id="para_symbol" cssClass="form-control "
														name="para_symbol"
														list="symbols" listKey="key"
														listValue="value" 
														/>
												</div>
											</div>
										</fieldset>
									</div>
									<div class="col-md-12 col-lg-2">
										<button type="button" class="btn btn-light btn-block"  data-toggle="modal"
															data-target="#myModal3" onClick="showModal($('#para_symbol').val(),$('#para_symbol').find('option:selected').text());">调整</button>
									</div>
									<div class="modal fade" id="myModal3" tabindex="-1"
										role="dialog" aria-hidden="true" style="display: none;z-index: 99999;">
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
														<s:if test='isResourceAccessible("ADMIN_CONTRACT_ORDER_ADJUST")'>
														<button type="submit" class="btn btn-danger">确定</button>
														</s:if>
													</div>
												</form>
											</div>
										</div>
									</div>
								</div>
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

									
								</div>

							</div>

						</div>
					</div>
				</div>
			</sec:authorize> --%>
			<div class="row" >
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">
							<div class="col-md-12 col-lg-12" style="margin-top: 10px;margin-bottom: 10px;display:none;" id="sysmbol_tool">
									<s:if test='isResourceAccessible("ADMIN_CONTRACT_ORDER_SHOWMODAL")'>
									<div class="col-md-12 col-lg-3">
										<fieldset>
											<div class="control-group">
												<div class="controls" style="margin-left: -15px;">
													<s:select id="para_symbol_2" cssClass="form-control "
														name="para_symbol_2"
														list="symbols" listKey="key"
														listValue="value" 
														/>
												</div>
											</div>
										</fieldset>
									</div>
									<div class="col-md-12 col-lg-2">
										<button type="button" class="btn btn-light btn-block"  data-toggle="modal"
															data-target="#myModal3" onClick="showModal($('#para_symbol_2').val(),$('#para_symbol_2').find('option:selected').text());">调整</button>
									</div>
									</s:if>
							</div>
						<div class="panel-title">查询结果</div>
						<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool" onClick="$('#sysmbol_tool').toggle();"><i class="fa fa-expand"></i></a></li>
						</ul>
						<div class="panel-body" id="list_content">
								<%@ include file="contract_order_list_content.jsp"%>
						</div>

					</div>
					<!-- End Panel -->

				</div>
			</div>
			<%-- <div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>用户</td>
										<td>订单号</td>
										<td>品种</td>
										<td>操作</td>
										<td>成交均价</td>
										<td>止盈止损</td>
										<td>剩余/委托金额</td>
										<td>剩余/委托保证金</td>
										<td>用户钱包余额</td>
										<td>盈亏</td>
										<td>状态</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
											<td><s:property value="username" /></td>
											<td><s:property value="order_no" /></td>
											<td><s:property value="itemname" /></td>
											<td><s:if test='offset=="open"'>开</s:if><s:if test='offset=="close"'>平</s:if><s:if test='direction=="buy"'>多</s:if><s:if
													test='direction=="sell"'>空</s:if></td>
											<td><s:property value="trade_avg_price" /></td>
											<td><s:property value="stop_price_profit" />/<s:property value="stop_price_loss" /></td>
											<td><span class="right label label-success"><fmt:formatNumber value="${volume*unit_amount}" pattern="#0.00" /></span>/<fmt:formatNumber value="${volume_open*unit_amount}" pattern="#0.00" /></td>
											<td><span class="right label label-success"><fmt:formatNumber value="${deposit}" pattern="#0.00" /></span>/<fmt:formatNumber value="${deposit_open}" pattern="#0.00" /></td>
											<td><fmt:formatNumber value="${money}" pattern="#0.00" /></td>
											<td>	
											<s:if test='state=="submitted"'>	
											<s:if test="(amount_close+profit+deposit) >=deposit_open">
												<span class="right label label-danger"><fmt:formatNumber
														value="${amount_close+profit+deposit-deposit_open}" pattern="#0.00" /> </span>
											</s:if>
											<s:else>
												<span class="right label label-success"><fmt:formatNumber
														value="${amount_close+profit+deposit-deposit_open}" pattern="#0.00" /> </span>
											</s:else>
											</s:if>
											<s:else>
											<s:if test="(amount_close+deposit) >=deposit_open">
												<span class="right label label-danger"><fmt:formatNumber
														value="${amount_close+deposit-deposit_open}" pattern="#0.00" /> </span>
											</s:if>
											<s:else>
												<span class="right label label-success"><fmt:formatNumber
														value="${amount_close+deposit-deposit_open}" pattern="#0.00" /> </span>
											</s:else>
											</s:else>
											</td>
											<td><s:if test='state=="submitted"'>
													持仓
												</s:if>  <s:if test='state=="created"'>
													<span class="right label label-success">已平仓</span>
												</s:if> </td>
											<td>
											<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<li><a
															href="javascript:onclose('<s:property value="order_no" />')">平仓</a></li>
													</ul>
													
												</div>
												</sec:authorize>
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
			</div> --%>

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
<s:if test='isResourceAccessible("ADMIN_CONTRACT_ORDER_CLOSE")'>
	<form action="<%=basePath%>normal/adminContractOrderAction!close.action" method="post"
		id="onclose">
		<input type="hidden" name="pageNo" id="pageNo" value="${param.pageNo}">
		<s:hidden name="order_no" id="order_no"></s:hidden>
		<s:hidden name="status_para"></s:hidden>
		<s:hidden name="start_time"></s:hidden>
		<s:hidden name="end_time"></s:hidden>
		<s:hidden name="rolename_para"></s:hidden>
	</form>
	<script type="text/javascript">
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
	</script>
	</s:if>
</sec:authorize>
	<!-- <script type="text/javascript">
		function setState(state){
    		document.getElementById("status_para").value=state;
    		document.getElementById("queryForm").submit();
		}
	</script> -->

<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
<s:if test='isResourceAccessible("ADMIN_CONTRACT_ORDER_SHOWMODAL")'>
	<script type="text/javascript">
    function showModal(symbol,name){
//     	var name = $("#para_symbol").find("option:selected").text();
//     	var symbol = $("#para_symbol").val();
 	   $("#adjust_symbol").val(symbol);
 	   $("#adjust").val(0);
 	   $("#item_name").html("调整"+name);
 	   $.ajax({
            type: "get",
            url: "<%=basePath%>normal/adminContractOrderAction!showModal.action?random=" + Math.random(),
            dataType: "json",
            data: {"symbol":symbol}, 
            success: function(data) {
           var temp =  $.parseJSON(data)
            $("#adjust_add_button").html("加" +temp.pips);
          	$("#adjust_sub_button").html("减" +temp.pips);
         	$("#adjust_value_before").html(temp.new_price);
          	$("#adjust_value_after").html(temp.new_price+temp.adjust_value);
         	$("#adjust_value").html(temp.adjust_value);
         	$("#adjust_delay").html(temp.delay_value);
         	$("#adjust_second").html(temp.delay_second);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.log("请求错误");
            }
        });
   }
    </script>
    </s:if>
    <s:if test='isResourceAccessible("ADMIN_CONTRACT_ORDER_GETVALUE")'>
    <script type="text/javascript">
    function getValue(type){
		var value = $("#adjust").val(); 
		var symbol = $("#adjust_symbol").val(); 
	  $.ajax({
          type: "get",
          url: "<%=basePath%>normal/adminContractOrderAction!getValue.action?random="
								+ Math.random(),
						dataType : "json",
						data : {
							"symbol" : symbol,
							"type" : type,
							"value" : value
						},
						success : function(data) {
							var temp = $.parseJSON(data);
							$("#adjust").val(temp.adjust_current_value);
							$("#adjust_value_before").html(temp.new_price);
							$("#adjust_value_after").html(
									temp.adjust_value_after);
							$("#adjust_value").html(temp.adjust_value);
							$("#adjust_delay").html(temp.delay_value);
							$("#adjust_second").html(temp.delay_second);
						},
						error : function(XMLHttpRequest, textStatus,
								errorThrown) {
							console.log("请求错误");
						}
					});

		}
    
   
	</script>
	</s:if>
	</sec:authorize>
	<s:if test='isResourceAccessible("ADMIN_CONTRACT_ORDER_CONTENT")'>
	<script type="text/javascript">
		/*5轮询读取函数*/
	    setInterval(function() {
		   var data = {"pageNo":$("#pageNo").val()
				   ,"rolename_para":$("#rolename_para").val()
				   ,"start_time":$("#start_time").val()
				   ,"end_time":$("#end_time").val()
				   ,"name_para":$("#name_para").val()
				   ,"order_no_para":$("#order_no_para").val()};
			goAjaxUrl($("#ajaxUrl").val(),data);	  
		}, 3000); 
	    function csPage(pageNo){
	    	$("#pageNo").val(pageNo);
			var url = $("#ajaxUrl").val();
			pageNo = Number(pageNo)<=0?1:pageNo;
			var data = {"pageNo":pageNo
					   ,"rolename_para":$("#rolename_para").val()
					   ,"start_time":$("#start_time").val()
					   ,"end_time":$("#end_time").val()
					   ,"name_para":$("#name_para").val()
					   ,"order_no_para":$("#order_no_para").val()};
			goAjaxUrl(url,data);
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
				    $("#list_content").html(res);
				  }
			});
		}
	</script>
	</s:if>
</body>
</html>