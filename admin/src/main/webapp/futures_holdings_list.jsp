<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
			<h3>当前交割持仓单</h3>
			
			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminFuturesOrderAction!holdings_list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="status_para" id="status_para" value="${status_para}" />
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="order_no_para" name="order_no_para"
													cssClass="form-control " placeholder="订单号（完整）" /> -->
												<input id="order_no_para" name="order_no_para"
													class="form-control " placeholder="订单号（完整）" value="${order_no_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="name_para" name="name_para" cssClass="form-control " placeholder="用户名、UID"/> -->
												<input id="name_para" name="name_para" 
													class="form-control " placeholder="用户名、UID" value="${name_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3" >
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<%-- <s:select id="rolename_para" cssClass="form-control "
													name="rolename_para"
													list="#{'MEMBER':'正式账号合约','GUEST':'演示账号合约'}" listKey="key"
													listValue="value" headerKey="" headerValue="所有合约"
													value="rolename_para" /> --%>
												<select id="rolename_para" name="rolename_para" class="form-control " >
												   <option value="">所有合约</option>
												   <option value="MEMBER" <c:if test="${rolename_para == 'MEMBER'}">selected="true"</c:if> >正式用户合约</option>
												   <option value="GUEST" <c:if test="${rolename_para == 'GUEST'}">selected="true"</c:if> >演示用户合约</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2" >
									<a class="btn btn-light btn-block" href="javascript:selectValues()">查询</a>									
								</div>
								
							</form>

						</div>

					</div>
				</div>
			</div>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
		
			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">查询结果</div>
						<div class="panel-body">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td>用户</td>
										<td>UID</td>
										<td>账户类型</td>
										<td>品种</td>
										<td>操作</td>
										<td>合约时间</td>
										<!-- <td>剩余金额</td>
										<td>用户钱包余额</td> -->
										<td>购买金额</td>
										<td>购买价</td>
										<td>现价</td>
										<td>手续费</td>
										<td>盈亏</td>
										<td>剩余时间</td>
										<td>购买时间</td>
										<td>交割时间</td>									
										<td>状态</td>
										<!-- <td>盈亏控制</td>
										<td width="130px"></td> -->
									</tr>
								</thead>
								
								<tbody id="pageValue">
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.username}</td>
											<td>${item.usercode}</td>
											<td>
												<c:choose>
													<c:when test="${item.rolename=='GUEST'}">
														<span class="right label label-warning">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='MEMBER'}">
														<span class="right label label-success">${item.roleNameDesc}</span>
													</c:when>
													<c:otherwise>
													 	${item.roleNameDesc}
													</c:otherwise>	
												</c:choose>
											</td>
											<td>${item.itemname}</td>
											<td>
												<c:if test="${item.offset == 'open'}">开</c:if>
												<c:if test="${item.offset == 'close'}">平</c:if>
												<c:if test="${item.direction == 'buy'}">多</c:if>
												<c:if test="${item.direction == 'sell'}">空</c:if>
											</td>
											<td>${item.timenum}</td>
											<td>${item.volume}</td>
											<td>${item.trade_avg_price}</td>
											<td>${item.close_avg_price}</td>
											<td>${item.fee}</td>
											<td>
												<c:if test="${item.profit >= 0}">
													<span class="right label label-success"><fmt:formatNumber value="${item.profit}" pattern="#0.00" /></span>
												</c:if>
												<c:if test="${item.profit < 0}">
													<span class="right label label-danger"><fmt:formatNumber value="${item.profit}" pattern="#0.00" /></span>
												</c:if>
											</td>
											<td>${item.remain_time}</td>
											<td>${item.create_time}</td>
											<td>${item.settlement_time}</td>
											<%-- <td><s:if test='profit_loss=="profit"'>
												盈利
											</s:if>  <s:if test='profit_loss=="loss"'>
												<span class="right label label-success">亏损</span>
											</s:if> 
											</td>--%>
											<td>
												<c:if test="${item.state == 'submitted'}">
													持仓
												</c:if>  
												<c:if test="${item.state == 'created'}">
													<span class="right label label-success">已平仓</span>
												</c:if> 
											</td>
											<%-- <td><s:property value="username" /></td>
											<td><s:property value="itemname" /></td>
											<td><s:if test='offset=="open"'>开</s:if><s:if test='offset=="close"'>平</s:if><s:if test='direction=="buy"'>多</s:if><s:if
													test='direction=="sell"'>空</s:if></td>
											<td><s:property value="trade_avg_price" /></td>
											<td><span class="right label label-success"><fmt:formatNumber value="${volume*unit_amount}" pattern="#0.00" /></span></td>
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
											<td>剩余时间</td>
											<td><s:if test='state=="submitted"'>
													持仓
												</s:if>  <s:if test='state=="created"'>
													<span class="right label label-success">已平仓</span>
												</s:if> 
											</td>
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
											</td> --%>
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
	</script>

	<form action="normal/adminFuturesOrderAction!close.action" method="post" id="onclose">
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
		<input type="hidden" name="order_no" id="order_no" value="${order_no}" />
		<input type="hidden" name="status_para" id="status_para" value="${status_para}" />
		<input type="hidden" name="start_time" id="start_time" value="${start_time}" />
		<input type="hidden" name="end_time" id="end_time" value="${end_time}" />
		<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}" />
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

	<script type="text/javascript">
		function setState(){
    		document.getElementById("status_para").value='submitted';
    		document.getElementById("queryForm").submit();
		}
	</script>

	<script type="text/javascript">
		var rolename_para = $("#rolename_para").val();
		var name_para = $("#name_para").val();
 		/*5轮询读取函数*/
		setInterval(function() {
		   getValue();	  
		}, 3000); 	
		function selectValues() {
			rolename_para = $("#rolename_para").val();
			name_para= $("#name_para").val();
			console.log("rolename_para:"+rolename_para+"-----name_para:"+name_para);
			getValue();
	 	}    
		function getValue() {		
			//var symbol = $("#adjust_symbol").val();
			$.ajax({
				type: "get",
				url: "<%=basePath%>normal/adminFuturesOrderAction!getValue.action?random=" + Math.random(),
				dataType : "json",
				data : {
					"rolename_para" : rolename_para,
					"name_para" : name_para,
					"pageNo" : $("#pageNo").val()
				},
				success : function(data) {
// 					var temp = $.parseJSON(data);
					var temp = data;
					//console.log(temp[0].id);
					var str=''; 
					var direction_name='';
					var color_text='';
					var state_name='';
					var role_name='';							
					for(var i=0;i<temp.length;i++){
						if(temp[i].direction=='buy'){
							direction_name='多';
						}
						if(temp[i].direction=='sell'){
							direction_name='空';
						}
						if(temp[i].state=='submitted'){
							state_name='持仓';
						}
						//盈利计算
						if(temp[i].profit >= 0){
							color_text='<span class="right label label-success">';
						}
						if(temp[i].profit < 0){
							color_text='<span class="right label label-danger">';
						}
						//角色
						if(temp[i].rolename == 'GUEST'){
							role_name = '<span class="right label label-warning">演示账号</span>';
						}
						if(temp[i].rolename == 'MEMBER'){
							role_name = '<span class="right label label-success">正式账号</span>';
						}
						str += '<tr>'
							+'<td>'+temp[i].username+'</td>'
							+'<td>'+temp[i].usercode+'</td>'
							+'<td>'+role_name+'</td>'
							+'<td>'+temp[i].itemname+'</td>'
							+'<td>'+direction_name+'</td>'
							+'<td>'+temp[i].timenum+'</td>'
							+'<td>'+temp[i].volume+'</td>'
							+'<td>'+temp[i].trade_avg_price+'</td>'
							+'<td>'+temp[i].close_avg_price+'</td>'
							+'<td>'+temp[i].fee+'</td>'
							+'<td> '+color_text+parseFloat(temp[i].profit).toFixed(2)+'</span></td>'
							+'<td>'+temp[i].remain_time+'</td>'
							+'<td>'+temp[i].create_time+'</td>'
							+'<td>'+temp[i].settlement_time+'</td>'
							+'<td>'+state_name+'</td>'
							+'</tr>';
						/* str += '<tr>'
							+'<td>'+temp[i].username+'</td>'
							+'<td>'+temp[i].itemname+'</td>'
							+'<td>'+direction_name+'</td>'
							+'<td>'+temp[i].trade_avg_price+'</td>'
							+'<td><span class="right label label-success">'+(temp[i].volume*temp[i].unit_amount).toFixed(2)+'</span></td>'
							+'<td>'+temp[i].money.toFixed(2)+'</td>'
							+'<td> '+color_text+temp[i].profit.toFixed(2)+'</span></td>'
							+'<td>剩余时间</td>'
							+'<td>'+state_name+'</td>'
							+'<td>'
							+'			<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">'
							+'				<div class="btn-group">'
							+'					<button type="button" class="btn btn-light">操作</button>'
							+'					<button type="button" class="btn btn-light dropdown-toggle"'
							+'						data-toggle="dropdown" aria-expanded="false">'
							+'						<span class="caret"></span> <span class="sr-only">Toggle'
							+'							Dropdown</span>'
							+'					</button>'
							+'					<ul class="dropdown-menu" role="menu">'
							+'						<li><a'
							+'							href="javascript:onclose('+temp[0].order_no+')">平仓</a></li>'
							+'					</ul>'					
							+'				</div>'
							+'				</sec:authorize>'
							+'			</td>'
						+'</tr>'; */					
					}
					$("#pageValue").html(str);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					console.log("请求错误");
				}
			});
		}
	</script>
	
</body>

</html>
