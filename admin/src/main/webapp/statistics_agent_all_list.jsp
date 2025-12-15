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
			<h3>代理商充提报表</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">
						<div class="panel-title">查询条件</div>
							<div class="panel-body">
							
								<form class="form-horizontal"
									action="<%=basePath%>normal/exchangeAdminAgentAllStatisticsAction!list.action"
									method="post" id="queryForm">
									
									<input type="hidden" name="status_para" value="${status_para}">
									<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
									<input type="hidden" name="para_time" id="para_time" value="${para_time}">
									<input type="hidden" name="para_party_id" id="para_party_id" value="${para_party_id}">
									<input type="hidden" name="all_party_id" id="all_party_id" value="${all_party_id}">
									<input type="hidden" name="para_agent_view" id="para_agent_view" value="${para_agent_view}">
									
									<div class="col-md-12 col-lg-12" style="margin-top:10px;">
									
									<div class="col-md-12 col-lg-3">
										<input id="para_username" name="para_username" class="form-control " placeholder="用户名、UID" value="${para_username}"/>	
									</div>
								
									<div class="col-md-12 col-lg-3">
										<input id="start_time" name="start_time" class="form-control " placeholder="开始日期" value="${start_time}"/>
									</div>
									
									<div class="col-md-12 col-lg-3">
										<input id="end_time" name="end_time" class="form-control " placeholder="结束日期" value="${end_time}"/>
									</div>
									
									<div class="col-md-12 col-lg-2">
										<button type="button" class="btn btn-light btn-block" onClick="formSubmit()">查询</button>
									</div>
									
								</div>
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
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>								
									<tr>
										<td colspan="6" rowspan="1" style="text-align:center;vertical-align: middle;">用户</td>
										<td colspan="4" style="text-align:center;">充提</td>
										<c:if test="${isOpen == '1'}">
											<td colspan="2" style="text-align:center;">业务员</td>
										</c:if>
<%--										<td colspan="1" rowspan="3" style="text-align:center;vertical-align: middle;">交易盈亏</td>--%>
<%--										<td colspan="1" rowspan="3" style="text-align:center; vertical-align: middle;">手续费</td>--%>
<%--										<td colspan="1" rowspan="3" style="text-align:center; vertical-align: middle;">总收益</td>--%>
									</tr>
									<tr style="text-align:center;">
										<td rowspan="2" style="text-align:center;vertical-align: middle;">团队用户名</td>
										<td rowspan="2" style="text-align:center;vertical-align: middle;">UID</td>
										<td rowspan="2" style="text-align:center;vertical-align: middle;min-width: 112px;">团队用户数</td>
										<td rowspan="2" style="text-align:center;vertical-align: middle;min-width: 112px;">直属用户数</td>
										<td rowspan="2" style="text-align:center;vertical-align: middle;">直属代理数</td>
										<td rowspan="2" style="text-align:center;vertical-align: middle;">团队代理</td>

										<td rowspan="2" style="text-align:center;vertical-align: middle;">充值</td>
										<td rowspan="2" style="text-align:center;vertical-align: middle;">提现</td>
										<td rowspan="2" style="text-align:center;vertical-align: middle;">充值差额</td>
										<td rowspan="2" style="text-align:center;vertical-align: middle;">赠送礼金</td>
										<c:if test="${isOpen == '1'}">
											<td rowspan="2" style="text-align:center;vertical-align: middle;">真实客损</td>
										</c:if>
									</tr>
<%--									<tr style="text-align: center;">--%>
<%--										<td>USDT</td>--%>
<%--										<td>ETH</td>--%>
<%--										<td>BTC</td>--%>
<%--										<td>USDT</td>--%>
<%--										<td>ETH</td>--%>
<%--										<td>BTC</td>--%>
<%--									</tr>--%>
								</thead>
								
								<tbody>								
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.username}</td>
											<td>${item.UID}</td>
											<td>
												<a href="#" onClick="getUserNextLvelAll('${item.partyId}')">
													${item.reco_member}
												</a>
												<a class="btn btn-light btn-block" style="float: right;width: 40px;padding: 2px 0px;font-size: 10px;" href="#" onClick="getReconNumNet('${item.partyId}')">网络</a>
											</td>
											<td>
												<a href="#" onClick="getUserNextLvel2('${item.partyId}')">
													${item.all_member}
												</a>
											</td>
											<td><a href="#" onClick="getNextLvel('${item.partyId}')">${item.all_agent}</a></td>
											<td><a href="#" onClick="getall_party_idNextLvel('${item.partyId}')">${item.reco_agent}</a></td>
											<td>${item.recharge_usdt}</td>
<%--											<td>${item.recharge_eth}</td>--%>
<%--											<td>${item.recharge_btc}</td>--%>
											<td><fmt:formatNumber value="${item.withdraw}" pattern="#0.0000" /></td>
											<td>
												<c:if test="${item.difference < 0}">
												<span class="right label label-danger">
													<fmt:formatNumber value="${item.difference}" pattern="#0.0000" />
												</span>
												</c:if>
												<c:if test="${item.difference > 0}">
												<span class="right label label-success">
													<fmt:formatNumber value="${item.difference}" pattern="#0.0000" />
												</span>
												</c:if>
												<c:if test="${item.difference == 0}">
													<fmt:formatNumber value="${item.difference}" pattern="#0.0000" />
												</c:if>
											</td>
											<td>${item.gift_money}</td>
											<c:if test="${isOpen == '1'}">
											<td>${item.commission}</td>
											</c:if>
<%--											<td>${item.withdraw_eth}</td>--%>
<%--											<td>${item.withdraw_btc}</td>--%>
<%--											<td>--%>
<%--												<c:if test="${item.business_profit < 0}">--%>
<%--													<span class="right label label-danger">--%>
<%--														<fmt:formatNumber value="${item.business_profit}" pattern="#0.0000" />--%>
<%--													</span>--%>
<%--												</c:if>--%>
<%--												<c:if test="${item.business_profit > 0}">--%>
<%--													<span class="right label label-success">--%>
<%--														<fmt:formatNumber value="${item.business_profit}" pattern="#0.0000" />--%>
<%--													</span>--%>
<%--												</c:if>--%>
<%--												<c:if test="${item.business_profit == 0}">--%>
<%--														<fmt:formatNumber value="${item.business_profit}" pattern="#0.0000" />--%>
<%--												</c:if>--%>
<%--											</td>											--%>
<%--											<td>--%>
<%--												<c:if test="${item.totle_fee < 0}">--%>
<%--													<span class="right label label-danger">--%>
<%--														<fmt:formatNumber value="${item.totle_fee}" pattern="#0.0000" />--%>
<%--													</span>--%>
<%--												</c:if>--%>
<%--												<c:if test="${item.totle_fee > 0}">--%>
<%--													<span class="right label label-success">--%>
<%--														<fmt:formatNumber value="${item.totle_fee}" pattern="#0.0000" />--%>
<%--													</span>--%>
<%--												</c:if>--%>
<%--												<c:if test="${item.totle_fee == 0}">--%>
<%--														<fmt:formatNumber value="${item.totle_fee}" pattern="#0.0000" />--%>
<%--												</c:if>											--%>
<%--											</td>--%>
<%--											<td>--%>
<%--												<c:if test="${item.totle_income < 0}">--%>
<%--													<span class="right label label-danger">--%>
<%--														<fmt:formatNumber value="${item.totle_income}" pattern="#0.0000" />--%>
<%--													</span>--%>
<%--												</c:if>--%>
<%--												<c:if test="${item.totle_income > 0}">--%>
<%--													<span class="right label label-success">--%>
<%--														<fmt:formatNumber value="${item.totle_income}" pattern="#0.0000" />--%>
<%--													</span>--%>
<%--												</c:if>--%>
<%--												<c:if test="${item.totle_income == 0}">--%>
<%--														<fmt:formatNumber value="${item.totle_income}" pattern="#0.0000" />--%>
<%--												</c:if>--%>
<%--											</td>--%>
										</tr>
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
								<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
								<h4 class="modal-title">用户充值币种分类</h4>
							</div>
							
							<div class="modal-header">
								<h4 class="modal-title">充值USDT数量</h4>
							</div>
							
							<div class="modal-body">
								<div class="">
									<input id="recharge_usdt" name="recharge_usdt" type="text"
										class="form-control" readonly="readonly" value="${recharge_usdt}">
								</div>
							</div>
							
							<div class="modal-header">
								<h4 class="modal-title">充值ETH数量</h4>
							</div>
							
							<div class="modal-body">
								<div class="">
									<input id="recharge_eth" name="recharge_eth" type="text"
										class="form-control" readonly="readonly" value="${recharge_eth}">
								</div>
							</div>
							
							<div class="modal-header">
								<h4 class="modal-title">充值BTC数量</h4>
							</div>
							
							<div class="modal-body">
								<div class="">
									<input id="recharge_btc" name="recharge_btc" type="text"
										class="form-control" readonly="readonly" value="${recharge_btc}">
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


		<form class="form-horizontal"
			  action="<%=basePath%>normal/adminUserAllStatisticsAction!exchangeList.action"
			  method="post" id="queryForms">
			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
			<input type="hidden" name="para_party_id" id="para_party_id" value="${para_party_id}">
		</form>

	</div>

	</div>
	</div>
	</div>
		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	
	<div class="form-group">
	
		<input type="hidden" name="quote_currency" id="quote_currency" value="">
		
		<div class="col-sm-2">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content" style="height:500px;">
					
						 <div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">钱包</h4>
						</div>
						
						<div class="modal-body" id="wallet_get" style="height:370px;">
							<%@ include file="statistics_user_all_money.jsp"%>
						</div>
						
						 <div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn " data-dismiss="modal" >关闭</button>
						</div>
						
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal -->
			</div>				
		</div>
			
		<!-- </form> -->
	</div>

	<div class="form-group">
		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="net_form" tabindex="-1"
				role="dialog" aria-labelledby="myModalLabel"
				aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
					
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">用户网络</h4>
						</div>
						
						<div class="modal-body" style="max-height: 400px;overflow-y: scroll;">
						
							<table class="table table-bordered table-striped" >
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
						
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal -->
			</div>
		</div>
		<!-- </form> -->
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
	
	<form action="<%=basePath%>normal/exchangeAdminAgentAllStatisticsAction!exportData.action" method="post" id="exportData">
		<input type="hidden" name="pageNo" value="${pageNo}">
		<input type="hidden" name="end_time" value="${end_time}">
		<input type="hidden" name="start_time" value="${start_time}">
		<input type="hidden" name="para_time" value="${para_time}">
		<input type="hidden" name="para_rolename" value="${para_rolename}">
		<input type="hidden" name="para_username" value="${para_username}">
		<input type="hidden" name="para_party_id" value="${para_party_id}">
		<input type="hidden" name="para_agent_view" value="${para_agent_view}">
	</form>
	
	<form action="<%=basePath%>normal/exchangeAdminAllStatisticsAction!list.action" method="post" id="exportData">
		<input type="hidden" name="pageNo" value="${pageNo}">
		<input type="hidden" id="user_para_party_id" name="para_party_id" value="${para_party_id}">
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


		 function getall_party_idNextLvel(id){
			 $("#all_party_id").val(id);
			 $("#pageNo").val(1);
			 $("#para_agent_view").val($("#para_agent_view_checkbox").is(':checked'));
			 $("#queryForm").submit();
		 }
		 function getUserNextLvel2(id){
			 window.parent.addTab('用户收益报表', '<%=basePath%>normal/adminUserAllStatisticsAction!exchangeList.action?para_party_id=' + id,true);
			 // $("iframe").hide();
			 // e.preventDefault();

		 }
		 function getUserNextLvelAll(id){
			 window.parent.addTab('用户收益报表', '<%=basePath%>normal/adminUserAllStatisticsAction!exchangeList.action?all_para_party_id=' + id,true);
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
				  }
			});
		}
		function getAllRecharge(recharge, recharge_usdt,recharge_eth,recharge_btc) {
			 $("#recharge_usdt").val(recharge_usdt);
			 $("#recharge_eth").val(recharge_eth);
			 $("#recharge_btc").val(recharge_btc);
			 $("#modal_recharge").modal("show");
		}

		 // $(".home-list").click(function(e){
			//  debugger
			//  var href = $(this).find("a").attr("href");
			//  window.parent.addTab('用户收益报表', href);
			//  e.preventDefault();
		 // })
	</script>

	<script type="text/javascript">
		function getReconNumNet(id){
			$("#net_form").modal("show");			
			var url = "<%=basePath%>normal/exchangeAdminAgentAllStatisticsAction!getReconNumNet.action";
			var data = {"net_party_id":id};
			goAjaxUrl(url,data,function(tmp){
				var str='';
				var content='';
				for(var i=0;i<tmp.user_reco_net.length;i++){
					str += '<tr>'
						+'<td>'+(i+1)+'</td>'
						+'<td>'+tmp.user_reco_net[i]+'</td>'
						+'</tr>';
				}
				$("#modal_net_table").html(str);				
			},function(){
		//			$("#coin_value").val(0);
			});
		}
		function goAjaxUrl(targetUrl,data,Func,Fail){
			console.log(data);
			$.ajax({
				url:targetUrl,
				data:data,
				type : 'get',
				dataType : "json",
				success: function (res) {
					/* var tmp = $.parseJSON(res) */
					var tmp = res;
					console.log(tmp);
				    if(tmp.code==200){
				    	Func(tmp);
				    }else if(tmp.code==500){
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
