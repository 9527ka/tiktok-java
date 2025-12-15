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
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>DAPP_代理商充提报表</h3>
			
			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminAgentAllStatisticsAction!list.action" method="post"
								id="queryForm">

								<!-- <s:hidden name="status_para"></s:hidden> -->
								<input type="hidden" name="status_para">
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"> 
								<input type="hidden" name="para_time" id="para_time" value="${para_time}"> 
								<input type="hidden" name="para_party_id" id="para_party_id" value="${para_party_id}"> 
								<input type="hidden" name="para_agent_view" id="para_agent_view" value="${para_agent_view}">
								
								<%-- <div class="col-md-12 col-lg-12">									
									<div class="col-md-12 col-lg-4">
										<s:textfield id="para_username" name="para_username"
											cssClass="form-control " placeholder="用户名、UID" />
									</div>									
									<div class="col-md-12 col-lg-4">
										<fieldset>
											<div class="control-group">
												<div class="controls">
													<s:select id="para_rolename" cssClass="form-control "
														name="para_rolename"
														list="role_map" listKey="key"
														listValue="value" headerKey="" headerValue="--用户角色--"
														 />
													</div>
												</div>
											</fieldset>
										</div>
									</div> --%>
								
								<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
								
									<div class="col-md-12 col-lg-3">
										<!-- <s:textfield id="para_username" name="para_username"
											cssClass="form-control " placeholder="用户名、UID" /> -->
										<input id="para_username" name="para_username"
											class="form-control " placeholder="用户名、UID" value="${para_username}" />
									</div>
									
									<div class="col-md-12 col-lg-3">
										<!-- <s:textfield id="start_time" name="start_time"
											cssClass="form-control " placeholder="开始日期" /> -->
										<input id="start_time" name="start_time"
											class="form-control " placeholder="开始日期" value="${start_time}" />
									</div>
									
									<div class="col-md-12 col-lg-3">
										<!-- <s:textfield id="end_time" name="end_time"
											cssClass="form-control " placeholder="结束日期" /> -->
										<input id="end_time" name="end_time"
											class="form-control " placeholder="结束日期" value="${end_time}" />
									</div>
									
									<!-- <div class="col-md-12 col-lg-4">
										<div class="checkbox checkbox-primary">
					                        <input id="para_agent_view_checkbox" name="para_agent_view_checkbox" type="checkbox" onClick="checkView()">
					                        <label for="para_agent_view_checkbox">
					                           	 代理对账视图
					                        </label>
						                    </div>
					                    </div> -->
					                    
									<div class="col-md-12 col-lg-2">
										<button type="button" class="btn btn-light btn-block"
											onClick="formSubmit()">查询</button>
									</div>
									
								</div>

								<!-- <div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li><a href="javascript:setTime('all')"> 全部</a></li>
												<li><a href="javascript:setTime('day')"> 当天</a></li>
												<li><a href="javascript:setTime('week')"> 当周</a></li>
												<li><a href="javascript:setTime('month')"> 当月</a></li>
											</ul>
										</div>
									</div>
								</div> -->

							</form>
							
						</div>

					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						<%-- <div class="col-md-12 col-lg-12" style="margin-bottom: 10px;">
							<div class="mailbox clearfix">
								操作
								<div class="mailbox-menu">
								<ul class="menu">
								<li>
								<c:if test="${security.isResourceAccessible('ADMIN_AGENT_ALL_STATISTICS_EXPORTDATA')}">
									<button type="button" onclick="exportData_confirm();" class="btn btn-light">查询结果导出excel文件</button>
								</c:if>
								</li>
								</ul>
								</div>
							</div>
						</div> --%>

						<div class="panel-body">

							<table class="table table-bordered table-striped">

								<thead>
									<tr>
										<td colspan="4" rowspan="1"
											style="text-align: center; vertical-align: middle;">用户</td>
										<td colspan="3" style="text-align: center;">充提</td>
									</tr>
									<tr style="text-align: center;">
										<td rowspan="1"
											style="text-align: center; vertical-align: middle;">用户名</td>
										<td rowspan="1"
											style="text-align: center; vertical-align: middle;">UID</td>
										<!--  <td>账户类型</td>-->
										<td rowspan="1"
											style="text-align: center; vertical-align: middle; min-width: 112px;">用户数</td>
										<td rowspan="1"
											style="text-align: center; vertical-align: middle;">代理数</td>
										<!-- 										<td>usdt</td> -->
										<!-- 										<td>日期</td> -->
										<td colspan="1" rowspan="1"
											style="text-align: center; vertical-align: middle;">授权转账USDT</td>
										<td colspan="1" rowspan="1"
											style="text-align: center; vertical-align: middle;">提现USDT</td>
										<td colspan="1" rowspan="1"
											style="text-align: center; vertical-align: middle;">差额(USDT)</td>
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>

											<td>${item.username}</td>
											<td>${item.UID}</td>

											<td>
												<a target="_blank"
													href="<%=basePath%>normal/adminUserAllStatisticsAction!list.action?para_party_id=${item.partyId}">${item.reco_member}</a> 
												<a class="btn btn-light btn-block"
													style="float: right; width: 40px; padding: 2px 0px; font-size: 10px;"
													href="#" onClick="getReconNumNet('${item.partyId}')">网络</a>
											</td>
											<td>
												<a href="#" onClick="getNextLvel('${item.partyId}')">${item.reco_agent}</a>
											</td>
											<td>${item.recharge_dapp}</td>													
											<td><fmt:formatNumber value="${item.withdraw_dapp}" pattern="#0.0000" /></td>
											<td>
												<c:if test="${item.recharge_dapp - item.withdraw_dapp < 0}">
													<span class="right label label-danger"> 
														<fmt:formatNumber value="${item.recharge_dapp - item.withdraw_dapp}" pattern="#0.0000" />
													</span>
												</c:if> 
												<c:if test="${item.recharge_dapp - item.withdraw_dapp >= 0}">
													<fmt:formatNumber value="${item.recharge_dapp - item.withdraw_dapp}" pattern="#0.0000" />
												</c:if>
											</td>
										</tr>
									<!-- </s:iterator> -->
									</c:forEach>
								</tbody>
								
							</table>
							
							<%@ include file="include/page_simple.jsp"%>
							
							<!-- <nav> -->
						</div>

					</div>
					<!-- End Panel -->

				</div>
			</div>

		</div>
		<!-- END CONTAINER -->
		
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<div class="form-group">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_recharge" tabindex="-1"
				role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content" style="height: 500px;">
						<div class="modal-content">
						
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
								<h4 class="modal-title">用户充值币种分类</h4>
							</div>
							
							<div class="modal-header">
								<h4 class="modal-title">充值USDT数量</h4>
							</div>
							
							<div class="modal-body">
								<div class="">
									<input id="recharge_usdt" name="recharge_usdt" type="text"
										class="form-control" readonly="readonly">
								</div>
							</div>
							
							<div class="modal-header">
								<h4 class="modal-title">充值ETH数量</h4>
							</div>
							
							<div class="modal-body">
								<div class="">
									<input id="recharge_eth" name="recharge_eth" type="text"
										class="form-control" readonly="readonly">
								</div>
							</div>
							
							<div class="modal-header">
								<h4 class="modal-title">充值BTC数量</h4>
							</div>
							
							<div class="modal-body">
								<div class="">
									<input id="recharge_btc" name="recharge_btc" type="text"
										class="form-control" readonly="readonly">
								</div>
							</div>

							<div class="modal-footer" style="margin-top: 0;">
								<button type="button" class="btn " data-dismiss="modal">关闭</button>
							</div>
							
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal -->
				</div>
			</div>
		</div>

		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<div class="form-group">
	
		<input type="hidden" name="quote_currency" id="quote_currency" value="">
		
		<!-- 			<input type="hidden" name="base_currency" id="base_currency" value=""> -->
		<%-- <form action="<%=basePath%>normal/adminSymbolsAction!list.action"
				method="post" id="succeededForm">
				<input type="hidden" name="pageNo" id="pageNo"
					value="${param.pageNo}">
				<s:hidden name="id" id="id_reset"></s:hidden>
				<s:hidden name="name_para" id="name_para"></s:hidden>
				<s:hidden name="rolename_para" id="rolename_para"></s:hidden> --%>
				
		<div class="col-sm-2">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content" style="height: 500px;">
					
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">钱包</h4>
						</div>
						
						<%--<div class="modal-body">
							<div class="">
								<s:textfield id="money_revise" name="money_revise"
									cssClass="form-control " />
									<span  class="help-block">增加请输入正数，扣除请输入负数</span> 
							</div>
						 --%>
								 
						<div class="modal-body" id="wallet_get" style="height: 370px;">
							<%@ include file="statistics_user_all_money.jsp"%>
						</div>
						
						<div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn " data-dismiss="modal">关闭</button>
							<!-- <button id="sub" type="submit" class="btn btn-default" onclick="modalConfirm(this)">确认</button> -->
						</div>
						
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal -->
			</div>
		</div>
		<!-- 			</form> -->
	</div>

	<div class="form-group">
		<%-- <form
			action="<%=basePath%>normal/adminRoleAuthorityManageAction!update.action"
			method="post" id="mainform">
			<input type="hidden" name="pageNo" id="pageNo"
				value="${param.pageNo}">
			<input type="hidden" name="id" id="update_role_id"/> --%>
		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="net_form" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">用户网络</h4>
						</div>
						
						<div class="modal-body"
							style="max-height: 400px; overflow-y: scroll;">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>层级</td>
										<td>用户数</td>
									</tr>
								</thead>
								<tbody id="modal_net_table">
									<%@ include file="include/loading.jsp"%>
								</tbody>
							</table>
						</div>

						<!-- <div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn "
								data-dismiss="modal">关闭</button>
								<button id="sub" type="submit" class="btn btn-default" >保存</button>																		
						</div> -->
							
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal -->
			</div>
		</div>
		<!-- 			</form> -->
	</div>

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
			}).on('changeDate',function(ev){
				$("#para_time").val("");
			});
			$('#statistics_start_time').datetimepicker({
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
			}).on('changeDate',function(ev){
				$("#para_time").val("");
			});
			$('#statistics_end_time').datetimepicker({
				format : 'yyyy-mm-dd',
				language : 'zh',
				weekStart : 1,
// 				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true,
				minView : 2,
				endDate: new Date(new Date().getTime() - 24*60*60*1000)
			})
			
			$("#para_time").val("");
		});
	</script>

	<script type="text/javascript">
		function setTime(time){
    		document.getElementById("para_time").value=time;
    		document.getElementById("queryForm").submit();
		}
		function message(title){
			swal({
				title : title,
				text : "",
				type : "warning",
//					showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : true
			});
		}
	</script>
	
	<form action="<%=basePath%>normal/adminAgentAllStatisticsAction!exportData.action"
		method="post" id="exportData">
		<input type="hidden" name="pageNo" value="${pageNo}">
		<!-- <s:hidden name="end_time"></s:hidden> -->
		<input type="hidden" name="end_time">
		<!-- <s:hidden name="start_time"></s:hidden> -->
		<input type="hidden" name="start_time">
		<!-- <s:hidden name="para_time"></s:hidden> -->
		<input type="hidden" name="para_time">
		<!-- <s:hidden name="para_rolename"></s:hidden> -->
		<input type="hidden" name="para_rolename">
		<!-- <s:hidden name="para_username"></s:hidden> -->
		<input type="hidden" name="para_username"">
		<!-- <s:hidden name="para_party_id"></s:hidden> -->
		<input type="hidden" name="para_party_id">
		<!-- <s:hidden name="para_agent_view"></s:hidden> -->
		<input type="hidden" name="para_agent_view">

	</form>

	<form action="<%=basePath%>normal/adminAllStatisticsAction!list.action" method="post"
		id="exportData">
		<input type="hidden" name="pageNo" value="${pageNo}">
		<!-- <s:hidden id="user_para_party_id" name="para_party_id"></s:hidden> -->
		<input type="hidden" name="para_party_id" id="user_para_party_id">

	</form>

	<script type="text/javascript">
		function exportData_confirm() {
			swal({
				title : "确认导出订单数据到文件?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : true
			}, function() {
				 $('input[name="end_time"]').val($("#end_time").val());
				 $('input[name="start_time"]').val($("#start_time").val());
				 $('input[name="para_time"]').val($("#para_time").val());
				 $('input[name="para_rolename"]').val($("#para_rolename").val());
				 $('input[name="para_username"]').val($("#para_username").val());
				 $('input[name="para_party_id"]').val($("#para_party_id").val());
				 $('input[name="para_agent_view"]').val($("#para_agent_view").val());
				document.getElementById("exportData").submit();			
			});
		};
	</script>
	
	<script>
		$(function () {
			if("true"==$("#para_agent_view").val()){
			 $("#para_agent_view_checkbox").prop("checked","checked");
			}
		});
 		function getNextLvel(id){
 			$("#para_party_id").val(id);
 			$("#pageNo").val(1);
 			$("#para_agent_view").val($("#para_agent_view_checkbox").is(':checked'));
 			$("#queryForm").submit();
 		} 		
 		function checkView(){
 			$("#para_agent_view_checkbox").val($("#para_agent_view").is(':checked'));
 			formSubmit();
 		}
 		function formSubmit(){
 			$("#para_party_id").val("");
 			$("#para_agent_view").val($("#para_agent_view_checkbox").is(':checked'));
 			$("#queryForm").submit();
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
				    $("#wallet_get").html(res);
// 				    $('#quote_currency').val(data.quote_currency);
// 				    $('#base_currency').val(data.base_currency);
				    
// 				    if(null==data.quote_currency||''==data.quote_currency||typeof(data.quote_currency) == "undefined"){
// 					    $('.tr_quote:first').attr('style','background:#39ffff;');
// 				    }else{
// 					    $('#tr_'+data.quote_currency).attr('style','background:#39ffff;');
// 				    }
				  }
			});
		}
		function getAllRecharge(recharge, recharge_usdt,recharge_eth,recharge_btc) {
			 $("#recharge_usdt").val(recharge_usdt);
			 $("#recharge_eth").val(recharge_eth);
			 $("#recharge_btc").val(recharge_btc);
			
			$("#modal_recharge").modal("show");
		}
	</script>

	<script type="text/javascript">
		function getReconNumNet(id){
			$("#net_form").modal("show");
			
			var url = "<%=basePath%>normal/adminAgentAllStatisticsAction!getReconNumNet.action";
			var data = {
				"net_party_id" : id
			};
			goAjaxUrl(url, data, function(tmp) {
				var str = '';
				var content = '';
				for (var i = 0; i < tmp.user_reco_net.length; i++) {
					str += '<tr>' + '<td>' + (i + 1) + '</td>' + '<td>'
							+ tmp.user_reco_net[i] + '</td>' + '</tr>';
				}
				$("#modal_net_table").html(str);

			}, function() {
				//			$("#coin_value").val(0);
			});
		}
		function goAjaxUrl(targetUrl, data, Func, Fail) {
			console.log(data);
			$.ajax({
				url : targetUrl,
				data : data,
				type : 'get',
				dataType : "json",
				success : function(res) {
					/* var tmp = $.parseJSON(res) */
					var tmp = res;
					console.log(tmp);
					if (tmp.code == 200) {
						Func(tmp);
					} else if (tmp.code == 500) {
						Fail();
						swal({
							title : tmp.message,
							text : "",
							type : "warning",
							showCancelButton : true,
							confirmButtonColor : "#DD6B55",
							confirmButtonText : "确认",
							closeOnConfirm : false
						});
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					console.log("请求错误");
				}
			});
		}
	</script>
	
</body>

</html>
