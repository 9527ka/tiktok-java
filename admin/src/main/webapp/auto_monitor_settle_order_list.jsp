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

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">
	<input type="hidden" name="session_token" id="session_token" value="${session_token}"/>
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>清算订单记录</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminAutoMonitorSettleOrderAction!list.action"
								method="post" id="queryForm">
							
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
						        <input type="hidden" name="succeeded_para" id="succeeded_para"/>
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">	
												<input id="order_para" name="order_para" class="form-control" 
												placeholder="订单号(完整)" value = "${order_para}"/>
											</div>
										</div>
									</fieldset>
								</div>
						
								<div class="col-md-12 col-lg-2" >
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>
								<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li><a href="javascript:setState('')"> 全部</a></li>
												<li><a href="javascript:setState('-1')">挂起</a></li>
												<li><a href="javascript:setState('0')">处理中</a></li>
												<li><a href="javascript:setState('1')">成功</a></li>
												<li><a href="javascript:setState('2')">失败</a></li>
											</ul>
										</div>
									</div>
								</div>
							</form>
								
						</div>
						<div style="overflow: hidden;">
							<div class="panel-title" style="margin-top: 1px;">操作</div>
							<div class="col-md-12 col-lg-2">
										<a href="javascript:collectLast()"  class="btn  btn-block btn-light">清算剩余未结算订单</a>
								</div>
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
										<td>订单号</td>
										<td>交易哈希值</td>
										<td>发起地址</td>
										<td>转账金额</td>
										<td>到账地址</td>
										<td>状态</td>
										<td>失败原因</td>
										<td>时间</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.order_no}</td>

											<td>
											
											<c:if test="${item.txn_hash != ''}">
												<a href="https://cn.etherscan.com/tx/${item.txn_hash}" target="_blank">
												${item.txn_hash_hide}
												</a>
											</c:if>
											
											</td> 
											<td>
												<a href="javascript:address_about('${item.from_address}')">
												${item.from_address_hide}</a>
											</td>
											<td>${item.volume}</td>
											<td>
												<a href="javascript:address_about('${item.to_address}')">
												${item.to_address_hide}
												</a>
											</td>
											<td><c:if test="${item.succeeded == '-1'}">
													<span class="right label label-default">挂起</span>
												</c:if>
												<c:if test="${item.succeeded == '0'}">
													<span class="right label label-warning">处理中</span>
												</c:if>
												<c:if test="${item.succeeded == '1'}">
													<span class="right label label-success">成功</span>
												</c:if>
												<c:if test="${item.succeeded == '2'}">
													<span class="right label label-danger">失败</span>
												</c:if>
											</td>
											<td>${item.error}</td>
											<td><fmt:formatDate value="${item.created}" pattern="MM-dd HH:mm:ss"/></td>
											
											<td>
												<c:if test="${item.succeeded == '-1'}">
												<a href="javascript:transferOne('${item.id}')"  class="btn  btn-block btn-light">转账发起</a>
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
						<h4 class="modal-title" >交易哈希</h4>
					</div>
					<div class="modal-header">
							<h4 class="modal-title" name="tx_hash" id="tx_hash"  readonly="true" style="display: inline-block;"></h4>
							<a href="" id="tx_hash_a" sytle="cursor:pointer;" target="_blank">在Etherscan上查看</a>
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
												

		<%@ include file="include/footer.jsp"%>
			<div class="form-group">
				<form action="<%=basePath%>normal/adminAutoMonitorSettleOrderAction!CollectAll111.action"
					method="post" id="subCollectForm">
					<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
					<input type="hidden" name="order_para"/>
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
										<h4 class="modal-title" id="myModalLabel">转账发起</h4>
									</div>
									<div class="modal-body">
										<div class="">
										<span class="help-block">发起地址</span>
										    <input id="from_address_collection" name="from_address_collection" class="form-control" readonly="readonly"/>
										</div>
									</div>
									<div class="modal-body">
										<div class="">
										<span class="help-block">到账地址</span>
											<input id="to_address_collection" name="to_address_collection" class="form-control" readonly="readonly"/>
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
										<button type="submit"  class="btn btn-default" >确认</button>
									</div>
								</div>
								<!-- /.modal-content -->
							</div>
							<!-- /.modal -->
						</div>
					</div>
				</form>
			</div>
			
			<div class="form-group">
				<form action="<%=basePath%>normal/adminAutoMonitorSettleOrderAction!transferLast.action"
					method="post" id="subCollectLastForm">
					<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
					<input type="hidden" name="order_para"/>
					<input type="hidden" name="session_token" id="session_token_collection_last" value="${session_token}"/>		
					
					<div class="col-sm-1">
						<!-- 模态框（Modal） -->
						<div class="modal fade" id="modal_collection_last" tabindex="-1"
							role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
							<div class="modal-dialog">
								<div class="modal-content">
									<div class="modal-header">
										<button type="button" class="close" data-dismiss="modal"
											aria-hidden="true">&times;</button>
										<h4 class="modal-title" id="myModalLabel">清算剩余未结算订单</h4>
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
										<button type="submit"  class="btn btn-default" >确认</button>
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
	<div class="modal fade" id="modal_address" tabindex="-1"
	role="dialog" aria-labelledby="myModalLabel"
	aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close"
					data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" >地址信息</h4>
			</div>
			<div class="modal-header">
					<h4 class="modal-title" name="info_address" id="info_address"  readonly="true" style="display: inline-block;"></h4>
					<a href="" id="info_address_a" sytle="cursor:pointer;" target="_blank">在Etherscan上查看</a>
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

	<%@ include file="include/js.jsp"%>
	<form action="<%=basePath%>normal/adminAutoMonitorSettleOrderAction!transferOne.action"
					method="post" id="subTransferForm">
					<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
					<input type="hidden" name="order_para"/>
					<input type="hidden" name="id" id="id_transfer_one"/>	
					<input type="hidden" name="session_token" id="session_token_transfer_one" value="${session_token}"/>	
	</form>

	
	<script type="text/javascript">
	function setState(state){
		document.getElementById("succeeded_para").value=state;
		document.getElementById("queryForm").submit();
	}
	function getCollectAddress(id,from_address,to_address){
//		 $("#money_address_one").val(tmp.collectAddress);
		 var session_token = $("#session_token").val();
		 $("#session_token_collection_one").val(session_token);
		$("#id_collection_one").val(id);
		$("#to_address_collection").val(to_address);
		$("#from_address_collection").val(from_address);
		
		$('#modal_collection_one').modal("show");
	}
	function collectLast(){
//		 $("#money_address_one").val(tmp.collectAddress);
		 var session_token = $("#session_token").val();
		 $("#session_token_collection_one").val(session_token);
// 		$("#id_collection_one").val(id);
// 		$("#to_address_collection").val(to_address);
// 		$("#from_address_collection").val(from_address);
		
		$('#modal_collection_last').modal("show");
	}
	function sub_confirm() {
		
		swal({
			title : "是否确认清算剩余未结算订单?",
			text : "清算剩余未结算归集订单",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			document.getElementById("subCollectForm").submit();
		});

	};
	
	function transferOne(id) {
		swal({
			title : "是否确认发起转账?",
			text : "",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			 var session_token = $("#session_token").val();
			 $("#session_token_transfer_one").val(session_token);
			$("#id_transfer_one").val(id);
			document.getElementById("subTransferForm").submit();
		});

	};	
	</script>
					
	
		
				

<script type="text/javascript">
function tx_about(hash){
	 $("#tx_hash").html(hash);
	 $("#tx_hash_a").attr("href","https://etherscan.io/tx/"+hash);
	$('#modal_tx').modal("show");
	 
}
function address_about(address){
	 $("#info_address").html(address);
	 $("#info_address_a").attr("href","https://etherscan.io/address/"+address);
	 
	$('#modal_address').modal("show");
	 
}
	</script>

	
</body>
</html>