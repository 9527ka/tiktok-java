<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

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
			<h3>永续持仓单 历史单</h3>

			<ul class="nav nav-tabs nav-line" role="tablist">
				<li role="presentation"><a
					href="<%=basePath%>normal/adminContractOrderAction!list.action">当前单</a>
				</li>
				<li role="presentation" class="active"><a href="#"
					aria-controls="profile5" role="tab" data-toggle="tab">历史单</a></li>
			</ul>

			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">
						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminHistoryContractOrderAction!list.action"
								method="post" id="queryForm">
								
								<%-- <s:hidden name="status_para"></s:hidden> --%>
								<input type="hidden" name="status_para" id="status_para" value="${status_para}">
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">

								<div class="col-md-12 col-lg-12">
								
									<div class="col-md-12 col-lg-3">
										<fieldset>
											<div class="control-group">
												<div class="controls">
													<%-- <s:textfield id="order_no_para" name="order_no_para"
														cssClass="form-control " placeholder="订单号" /> --%>
													<input id="order_no_para" name="order_no_para"
														class="form-control " placeholder="订单号" value="${order_no_para}"/>
												</div>
											</div>
										</fieldset>
									</div>
									
									<div class="col-md-12 col-lg-3">
										<fieldset>
											<div class="control-group">
												<div class="controls">
													<%-- <s:textfield id="name_para" name="name_para"
														cssClass="form-control " placeholder="用户名、UID" /> --%>
													<input id="name_para" name="name_para"
														class="form-control " placeholder="用户名、UID" value="${name_para}"/>
												</div>
											</div>
										</fieldset>
									</div>
									
									<div class="col-md-12 col-lg-3">
										<fieldset>
											<div class="control-group">
												<div class="controls">
													<%-- <s:select id="rolename_para" cssClass="form-control "
														name="rolename_para"
														list="#{'MEMBER':'正式账号合约','GUEST':'演示账号合约','TEST':'试用账号合约'}"
														listKey="key" listValue="value" headerKey=""
														headerValue="所有合约" value="rolename_para" /> --%>
													<select id="rolename_para" name="rolename_para" class="form-control " >
													   <option value="">所有合约</option>
													   <option value="MEMBER" <c:if test="${rolename_para == 'MEMBER'}">selected="true"</c:if> >正式账号合约</option>
													   <option value="GUEST" <c:if test="${rolename_para == 'GUEST'}">selected="true"</c:if> >演示账号合约</option>
													   <option value="TEST" <c:if test="${rolename_para == 'TEST'}">selected="true"</c:if> >试用账号合约</option>
													</select>
												</div>
											</div>
										</fieldset>
									</div>
									
								</div>
								
								<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="col-md-12 col-lg-3">
										<%-- <s:textfield id="start_time" name="start_time"
											cssClass="form-control " placeholder="开始日期" /> --%>
										<input id="start_time" name="start_time"
											class="form-control " placeholder="开始日期" value="${start_time}" />
									</div>
									<div class="col-md-12 col-lg-3">
										<%-- <s:textfield id="end_time" name="end_time"
											cssClass="form-control " placeholder="结束日期" /> --%>
										<input id="end_time" name="end_time"
											class="form-control " placeholder="结束日期" value="${end_time}" />
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

						</div>

					</div>
				</div>
			</div>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->

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
	
	<form action="<%=basePath%>normal/adminHistoryContractOrderAction!close.action"
		method="post" id="onclose">
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="order_no" id="order_no" value="${order_no}">
		<input type="hidden" name="status_para" value="${status_para}">
		<input type="hidden" name="start_time" value="${start_time}">
		<input type="hidden" name="end_time" value="${end_time}">
		<input type="hidden" name="rolename_para" value="${rolename_para}">
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
	
	<script type="text/javascript">
	    function getValue(type){
			var value = $("#adjust").val(); 
			var symbol = $("#adjust_symbol").val(); 
			$.ajax({
			       type: "get",
			       url: "<%=basePath%>normal/adminHistoryContractOrderAction!getValue.action?random=" + Math.random(),
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
						$("#adjust_value_after").html(temp.adjust_value_after);
						$("#adjust_value").html(temp.adjust_value);
						$("#adjust_delay").html(temp.delay_value);
						$("#adjust_second").html(temp.delay_second);
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						console.log("请求错误");
					}
				});
		}
	</script>
	
	<script type="text/javascript">
		function setState(state) {
			document.getElementById("status_para").value = state;
			document.getElementById("queryForm").submit();
		}

	</script>

</body>

</html>
