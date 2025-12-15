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
	
    <input type="hidden" name="session_token" id="session_token" value="${session_token}"/>

		<div class="ifr-con">
			<h3>币币交易单</h3>
			
			<%@ include file="include/alert.jsp"%>

			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">
					
						<input type="hidden" value="${basePath}/normal/adminExchangeApplyOrderAction!content.action" id='ajaxUrl'/>
						
						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminExchangeApplyOrderAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="status_para" id="status_para" value="${status_para}" />
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="order_no_para" name="order_no_para"
													class="form-control " placeholder="订单号（完整）" value="${order_no_para}"/>
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="name_para" name="name_para"
													class="form-control " placeholder="用户名(钱包地址)、UID" value="${name_para}"/>
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="rolename_para" name="rolename_para" class="form-control " >
												    <option value="">所有合约</option>
													<option value="MEMBER" <c:if test="${rolename_para == 'MEMBER'}">selected="true"</c:if> >正式账号合约</option>
													<option value="GUEST" <c:if test="${rolename_para == 'GUEST'}">selected="true"</c:if> >演示账号合约</option>
												</select>	
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>

								 <div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li><a href="javascript:setState('')"> 全部</a></li>
												<li><a href="javascript:setState('submitted')"> 已提交</a></li>
												<li><a href="javascript:setState('canceled')"> 已撤销</a></li>
												<li><a href="javascript:setState('created')"> 委托完成</a></li>
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
										<td>推荐人</td>
										<td>品种</td>
										<td>操作</td>
										<td>委托数量</td>
										<td>报价类型</td>
										<td>限价</td>
										<td>成交价格</td>
										<td>状态</td>
										<td>创建时间</td>
										<td>成交时间</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
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
											<td>${item.username_parent}</td>
											<td>${item.itemname}</td>
											<td>
												<c:if test="${item.offset=='open'}">买入</c:if>
												<c:if test="${item.offset=='close'}">卖出</c:if>
											</td>
											<td>${item.volume}</td>
											<td>
												<c:if test="${item.order_price_type=='limit'}">限价</c:if>
												<c:if test="${item.order_price_type=='opponent'}">市价</c:if>
											</td>
											<td>${item.price}</td>
											<td>${item.close_price}</td>
											<td>
											    <c:if test="${item.state=='submitted'}">已提交 </c:if>
											    <c:if test="${item.state=='canceled'}">已撤销</c:if> 
											    <c:if test="${item.state=='created'}">
													<span class="right label label-success">委托完成</span>
												</c:if> 
											</td>
											<td>${item.createTime}</td>
											<td>${item.closeTime}</td>
											
											<td>
											
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
								 							|| security.isResourceAccessible('OP_EXCHANGE_APPLY_ORDER_OPERATE')}">
														
													<c:if test="${item.state=='submitted'}">
												
														<div class="btn-group">
															<button type="button" class="btn btn-light">操作</button>
															<button type="button" class="btn btn-light dropdown-toggle"
																data-toggle="dropdown" aria-expanded="false">
																<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
															</button>
															<ul class="dropdown-menu" role="menu">
																
																<li><a href="javascript:onclose('${item.order_no}')">撤销</a></li>
															
															</ul>
														</div>
															
													</c:if>
													
												</c:if>
												
											</td>
											
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

		<%@ include file="include/footer.jsp"%>

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

    <%-- <!-- 该模态框暂时没用到 -->
	<div class="form-group">
		<form
			action="<%=basePath%>normal/adminExchangeApplyOrderAction!success.action"
			method="post" id="succeededForm">
			<input type="hidden" name="pageNo" id="pageNo"
				value="${param.pageNo}">
				<s:hidden name="order_no" id="order_no"></s:hidden>
				<s:hidden name="status_para"></s:hidden>
				<s:hidden name="start_time"></s:hidden>
				<s:hidden name="end_time"></s:hidden>
				<s:hidden name="rolename_para"></s:hidden>
				<s:hidden name="session_token" id="session_token_success"></s:hidden>
		<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="modal_set" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel"
					aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							
							<div class="modal-header">
							<button type="button" class="close"
									data-dismiss="modal" aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="myModalLabel">限价成交</h4>
							</div>
							<div class="modal-body">
								<div class="">
									<input id="safeword" type="password" name="safeword"
										class="form-control" placeholder="请输入资金密码">
								</div>
							</div>
							
							<div class="modal-footer" style="margin-top: 0;">
								<button type="button" class="btn "
									data-dismiss="modal">关闭</button>
								<button id="sub" type="submit"
									class="btn btn-default" >确认</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal -->
				</div>
			</div>
			</form>
	</div> --%>

	<form action="${basePath}normal/adminExchangeApplyOrderAction!close.action" method="post" id="onclose">
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="order_no" id="order_no" value="${order_no}">
		<input type="hidden" name="status_para" id="status_para" value="${status_para}">
		<input type="hidden" name="start_time" id="start_time" value="${start_time}">
		<input type="hidden" name="end_time" id="end_time" value="${end_time}">
		<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}">
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
		function onsucceeded(order_no){
			var session_token = $("#session_token").val();
			 $("#session_token_success").val(session_token);
			 $("#order_no").val(order_no);
			$('#modal_set').modal("show");
		}
		function onclose(order_no) {
			$("#order_no").val(order_no);
			swal({
				title : "是否确认撤销?",
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
		function setState(state){
    		document.getElementById("status_para").value=state;
    		document.getElementById("queryForm").submit();
		}
	</script>

</body>

</html>
