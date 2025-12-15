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
			<h3>当前交割持仓单</h3>
			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						<s:if test='isResourceAccessible("ADMIN_FUTURES_ORDER_LIST")'>

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminFuturesOrderAction!holdings_list.action"
								method="post" id="queryForm">
								<s:hidden name="status_para"></s:hidden>
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
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

								<div class="col-md-12 col-lg-3" >
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

								<div class="col-md-12 col-lg-2" >
								<a class="btn btn-light btn-block" href="javascript:selectValues()">查询</a>
									
								</div>

								

							</form>
						</s:if>

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
<!-- 										<td>剩余金额</td> -->
<!-- 										<td>用户钱包余额</td> -->
										<td>购买金额</td>
										<td>购买价</td>
										<td>现价</td>
										<td>手续费</td>
										<td>盈亏</td>
										<td>剩余时间</td>
										<td>购买时间</td>
										<td>交割时间</td>
										<td>状态</td>
<!-- 										<td width="130px"></td> -->
									</tr>
								</thead>
								<tbody id="pageValue">
									<s:iterator value="page.elements" status="stat">
										<tr>
											<td><s:property value="username" /></td>
											<td><s:property value="usercode" /></td>
											<td><s:if test='rolename=="GUEST"'>
													<span class="right label label-warning">演示账号</span>
												</s:if>
												<s:if test='rolename=="MEMBER"'>
													<span class="right label label-success">正式账号</span>
												</s:if>
											</td>
											<td><s:property value="itemname" /></td>
											<td><s:if test='offset=="open"'>开</s:if><s:if test='offset=="close"'>平</s:if><s:if test='direction=="buy"'>多</s:if><s:if
													test='direction=="sell"'>空</s:if></td>
											<td><s:property value="timenum" /></td>
											<td><s:property value="volume" /></td>
											<td><s:property value="trade_avg_price" /></td>
											<td><s:property value="close_avg_price" /></td>
											<td><s:property value="fee" /></td>
											<td>
												<s:if test='profit>=0'>
													<span class="right label label-success"><fmt:formatNumber value="${profit}" pattern="#0.00" /></span>
												</s:if>
												<s:else>
													<span class="right label label-danger"><fmt:formatNumber value="${profit}" pattern="#0.00" /></span>
												</s:else>
											</td>
											<td><s:property value="remain_time" /></td>
											<td><s:property value="create_time" /></td>
											<td><s:property value="settlement_time" /></td>
											<td><s:if test='state=="submitted"'>
													持仓
												</s:if>  <s:if test='state=="created"'>
													<span class="right label label-success">已平仓</span>
												</s:if> 
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
									</s:iterator>

								</tbody>
							</table>
							<%@ include file="include/page_simple.jsp"%>
							<nav>
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

<sec:authorize ifAnyGranted="ROLE_ROOT">
	<form action="normal/adminFuturesOrderAction!close.action" method="post"
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
</sec:authorize>

	<script type="text/javascript">
	

	
		
		function setState(){
    		document.getElementById("status_para").value='submitted';
    		document.getElementById("queryForm").submit();
		}
	</script>

<s:if test='isResourceAccessible("ADMIN_FUTURES_ORDER_LIST")'>
	<script type="text/javascript">
	var rolename_para = $("#rolename_para").val();
	var name_para = $("#name_para").val();

 	/*5轮询读取函数*/
   setInterval(function() {
	   getValue();	  
	}, 3000);
 	
	function selectValues(){
		rolename_para = $("#rolename_para").val();
		name_para= $("#name_para").val();
		console.log("rolename_para:"+rolename_para+"-----name_para:"+name_para);
		getValue();
 	}

    
    function getValue(){
		
		//var symbol = $("#adjust_symbol").val();
	  $.ajax({
          type: "get",
          url: "<%=basePath%>normal/adminFuturesOrderAction!getValue.action?random="
								+ Math.random(),
						dataType : "json",
						data : {
							"rolename_para" : rolename_para,
							"name_para" : name_para,
							"pageNo" : $("#pageNo").val()
						},
						success : function(data) {
							var temp = $.parseJSON(data);
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
						error : function(XMLHttpRequest, textStatus,
								errorThrown) {
							console.log("请求错误");
						}
					});

		}
	</script>
</s:if>
</body>
</html>