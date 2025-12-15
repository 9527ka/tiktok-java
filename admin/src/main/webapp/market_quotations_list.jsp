<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

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
			<h3>行情管理</h3>
			<%@ include file="include/alert.jsp"%>
			
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
							action="<%=basePath%>normal/adminMarketQuotationsManageAction!adjust.action"
							method="post" id="mainForm">
							<input type="hidden" id="adjust_symbol" name="symbol" />
							<%-- <input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"> --%>
							<input type="hidden" name="status_para">
							<input type="hidden" name="start_time">
							<input type="hidden" name="end_time">
							<input type="hidden" name="rolename_para">
							
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
								<button type="button" class="btn btn-white" data-dismiss="modal">取消</button>
								
								<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
											 || security.isResourceAccessible('OP_MARKET_OPERATE')}">
								
									<button type="submit" class="btn btn-danger">确定</button>
									
								</c:if>
								
							</div>
						</form>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>产品名称</td>
										<td>原值</td>
										<td>调整后</td>
										<!-- <td>行情链接</td> -->
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
								    <c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.name}</td>
											<td>${item.new_price}</td>
											<td>${item.after_value}</td>
											<%-- <td><a href="${item.url}" target="_blank">查看K线图</a></td> --%>
											<td>
											<a href="#" class="btn btn-light" data-toggle="modal" data-target="#myModal3" 
													onclick="showModal('${item.symbol}','${item.name}');" id='${item.symbol}'> 
													调整</a>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<%-- <%@ include file="include/page_simple.jsp"%> --%>
							<nav>
						</div>

					</div>
					<!-- End Panel -->

				</div>
			</div>

		</div>
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->

		    <form class="form-horizontal" action="<%=basePath%>normal/adminMarketQuotationsManageAction!list.action"
				method="post" id="queryForm">
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
				<%@ include file="include/page_simple.jsp"%>
			</form>
			
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

	<script type="text/javascript">
		function setState(state){
    		document.getElementById("status_para").value=state;
    		document.getElementById("queryForm").submit();
		}
	</script>

	<script type="text/javascript">
    function showModal(symbol,name){
 	   $("#adjust_symbol").val(symbol);
 	   $("#adjust").val(0);
 	   $("#item_name").html("调整"+name);
 	   $.ajax({
            type: "get",
            url: "<%=basePath%>normal/adminMarketQuotationsManageAction!showModal.action?random=" + Math.random(),
            dataType: "json",
            data: {"symbol":symbol}, 
            success: function(data) {
            var temp = data;
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
          url: "<%=basePath%>normal/adminMarketQuotationsManageAction!getValue.action?random=" + Math.random(),
						dataType : "json",
						data : {
							"symbol" : symbol,
							"type" : type,
							"value" : value
						},
						success : function(data) {
							var temp = data;
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
</body>
</html>