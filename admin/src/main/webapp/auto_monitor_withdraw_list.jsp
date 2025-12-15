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
			<h3>DAPP_提现订单</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
	
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminAutoMonitorWithdrawAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								<input type="hidden" name="succeeded_para" id="succeeded_para"/>
                                 
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="name_para" name="name_para" class="form-control " 
												placeholder="用户名(钱包地址)、UID" value = "${name_para}"/>
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="rolename_para" name="rolename_para" class="form-control " >
													<option value="">所有账号</option>													   
													<option value="MEMBER" <c:if test="${rolename_para == 'MEMBER'}">selected="true"</c:if> >正式账号</option>
													<option value="GUEST" <c:if test="${rolename_para == 'GUEST'}">selected="true"</c:if> >演示账号</option>													   
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
												<li><a href="javascript:setState(0)"> 未处理</a></li>
												<li><a href="javascript:setState(1)"> 通过申请</a></li>
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
										<td>用户地址</td>
										<td>UID</td>
										<td>账户类型</td>
										<td>推荐人</td>
										<td>转换ETH数量</td>
										<td>到账USDT数量</td>
										<td>状态</td>
										<td>到账地址信息</td>
										<td>创建时间</td>
										<td>审核时间</td>
										<td>备注</td>
										<td width="150px"></td>
									</tr>
								</thead>
								
								<tbody style="font-size: 13px;">
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
											<td>${item.username_parent}</td>
											<td>${item.volume}</td>
											<td><span class="label label-danger">${item.amount}</span></td>
											<td>
												<c:if test="${item.succeeded=='0'}">
													处理中
												</c:if>
												<c:if test="${item.succeeded=='3'}">
													处理中...
												</c:if> 
												<c:if test="${item.succeeded=='1'}">
													<span class="right label label-success">已处理</span>
												</c:if>
												<c:if test="${item.succeeded=='2'}">
													驳回
												</c:if>											
											</td>
											<td>
												<a href="javascript:withdraw_about('${item.amount}','${item.address}','${item.qdcode}')">查看信息</a>
											</td>
											<td>${item.createTime}</td>
											<td>${item.reviewTime}</td>											
											<td>${item.remarks}</td>
												
											<td>
									
												<c:if test="${item.succeeded=='0'}">
													<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_FINANCE')
																 || security.isResourceAccessible('OP_DAPP_WITHDRAW_OPERATE')
																 || security.isResourceAccessible('OP_EXCHANGE_WITHDRAW_OPERATE')
																 || security.isResourceAccessible('OP_EXCHANGE_RECHARGE_OPERATE')}">
															
															<div class="btn-group">
																<button type="button" class="btn btn-light">操作</button>
																<button type="button" class="btn btn-light dropdown-toggle"
																	data-toggle="dropdown" aria-expanded="false">
																	<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
																</button>
																<ul class="dropdown-menu" role="menu">
																	<li><a href="javascript:onsucceeded('${item.id}')">通过申请（手动打款）</a></li>
																	<li><a href="javascript:onsucceeded_collection('${item.id}')">通过申请（加入质押总资产）</a></li>		
																	<li><a href="javascript:reject_confirm('${item.id}')">驳回申请</a></li>
																</ul>
															</div>
															
													</c:if>													
												</c:if>
										
												<!-- 模态框 -->
												<div class="form-group">
												
													<form action="<%=basePath%>normal/adminAutoMonitorWithdrawAction!success.action"
														method="post" id="succeededForm">
														
														<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
														<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
														<input type="hidden" name="succeeded_para" value="${succeeded_para}"/>
														<input type="hidden" name="id" id="id_success" value="${id}"/>
														<input type="hidden" name="session_token" id="session_token_success" value="${session_token}"/>
														
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
																			<h4 class="modal-title" id="myModalLabel">提现通过申请（手动打款）</h4>
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
												</div>
											
												<!-- 通过申请并加入质押总资产 -->
												<!-- 模态框 -->
												<div class="form-group">
												
													<form action="<%=basePath%>normal/adminAutoMonitorWithdrawAction!success_collection.action"
														method="post" id="succeededForm">
														
														<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
														<input type="hidden" name="name_para" id="name_para" value="${name_para}">
														<input type="hidden" name="succeeded_para" value="${succeeded_para}">
														<input type="hidden" name="id" id="id_success_collection" value="${id}">
														<input type="hidden" name="session_token" id="session_token_success_collection" value="${session_token}">
														
														<div class="col-sm-1">
															<!-- 模态框（Modal） -->
															<div class="modal fade" id="modal_set_collection" tabindex="-1"
																role="dialog" aria-labelledby="myModalLabel"
																aria-hidden="true">
																<div class="modal-dialog">
																	<div class="modal-content">
																		
																		<div class="modal-header">
																		<button type="button" class="close"
																				data-dismiss="modal" aria-hidden="true">&times;</button>
																			<h4 class="modal-title" id="myModalLabel">提现通过申请（并将USDT金额加入质押总资产）</h4>
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
													
												</div>
											
												<div class="form-group">
												
													<form action="<%=basePath%>normal/adminAutoMonitorWithdrawAction!successThird.action"
														method="post" id="succeededForm">
														
														<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">															
														<input type="hidden" name="name_para" id="name_para" value="${name_para}"/>
														<input type="hidden" name="succeeded_para" value="${succeeded_para}"/>
														<input type="hidden" name="id" id="id_success_third" value="${id}"/>
														<input type="hidden" name="session_token" id="session_token_success_third" value="${session_token}"/>
														
														<div class="col-sm-1">
															<!-- 模态框（Modal） -->
															<div class="modal fade" id="modal_set_third" tabindex="-1"
																role="dialog" aria-labelledby="myModalLabel"
																aria-hidden="true">
																<div class="modal-dialog">
																	<div class="modal-content">
																		
																		<div class="modal-header">
																			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
																			<h4 class="modal-title" id="myModalLabel">提现通过申请（三方渠道）</h4>
																		</div>
																		
																		<div class="modal-body">
																			<div class="">
																				<input id="safeword" type="password" name="safeword"
																					class="form-control" placeholder="请输入资金密码">
																			</div>
																		</div>
																		
																		<div class="modal-footer" style="margin-top: 0;">
																			<button type="button" class="btn " data-dismiss="modal">关闭</button>
																			<button id="sub" type="submit" class="btn btn-default" >确认</button>
																		</div>
																		
																	</div>
																	<!-- /.modal-content -->
																</div>
																<!-- /.modal -->
															</div>
														</div>
														
													</form>
												</div>
											
												<!-- 模态框（Modal） -->
												<div class="modal fade" id="modal_withdraw" tabindex="-1"
													role="dialog" aria-labelledby="myModalLabel"
													aria-hidden="true">
													<div class="modal-dialog">
														<div class="modal-content">
														
															<div class="modal-header">
																<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
																<h4 class="modal-title" >到账地址信息</h4>
															</div>
															
															<div class="modal-body">
																<div class="" >
																	到账USDT数量
																	<input id="withdraw_amount" type="text" name="withdraw_amount" class="form-control" readonly="true"/>
																</div>
																<div class="" >
																	到账地址
																	<input id="withdraw_address" type="text" name="withdraw_address" class="form-control" readonly="true"/>
																</div>																		
															</div>
															
															<div class="modal-header">																	
																<h4 class="modal-title" id="myModalLabel">到账地址二维码</h4>
															</div>
															
															<div class="modal-body">
																<div class="" >
																	<a id="withdraw_img_a" href="#" name="withdraw_img_a" target="_blank">
																		<img width="200px" height="200px" id="withdraw_img" name="withdraw_img" src="" />
																	</a>																			
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
										
											</td>
											
										</tr>
									</c:forEach>
									<!-- </s:iterator> -->

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
		
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->

		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<%@ include file="include/js.jsp"%>

	<script type="text/javascript">
		function reject_confirm(id) {
			var session_token = $("#session_token").val();
			$("#session_token_reject").val(session_token);
			$("#id_reject").val(id);			
			swal({
				title : "是否确认驳回?",
				text : "驳回后款项返回账户",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("onreject").submit();
			});
		};
		function reject(id) {			
			var session_token = $("#session_token").val();
			 $("#session_token_reject").val(session_token);
			$("#id_reject").val(id);
			$('#modal_reject').modal("show");
		};
	</script>

	<!-- Modal -->
	<div class="modal fade" id="modal_reject" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
			
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">请输入驳回原因</h4>
				</div>
				
				<div class="modal-body">
					<form action="<%=basePath%>normal/adminAutoMonitorWithdrawAction!reject.action"
						method="post" id="onreject">
						<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
						<input type="hidden" name="name_para" id="name_para" value="${name_para}">
						<input type="hidden" name="succeeded_para" value="${succeeded_para}">
						<input type="hidden" name="id" id="id_reject" value="${id}">
						<input type="hidden" name="session_token" id="session_token_reject" value="${session_token}">
						<input id="failure_msg" name="failure_msg" class="form-control  input-lg" rows="2" cols="10" placeholder="驳回原因" value="${failure_msg}">
					</form>
				</div>
				
				<div class="modal-footer">
					<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-default" onclick="reject_confirm()">驳回代付申请</button>
				</div>
				
			</div>
		</div>
	</div>
	<!-- End Moda Code -->

	<script type="text/javascript">
		function withdraw_about(amount,address,img){
			 $("#withdraw_amount").val(amount);
			 $("#withdraw_address").val(address);		 
			 document.getElementById('withdraw_img_a').href="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
			 document.getElementById('withdraw_img').src="<%=base%>wap/public/showimg!showImg.action?imagePath="+img;
			$('#modal_withdraw').modal("show");		 
		}
		function onsucceeded(id){
			var session_token = $("#session_token").val();
			 $("#session_token_success").val(session_token);
			 $("#id_success").val(id);
			 $('#modal_set').modal("show");		 
		}	
		function onsucceeded_collection(id){
			var session_token = $("#session_token").val();
			 $("#session_token_success_collection").val(session_token);
			 $("#id_success_collection").val(id);
			$('#modal_set_collection').modal("show");		 
		}
		function onsucceededThird(id){
			var session_token = $("#session_token").val();
			 $("#session_token_success_third").val(session_token);
			 $("#id_success_third").val(id);
			$('#modal_set_third').modal("show");		 
		}	
		function handel(id) {
			$("#id_success").val(id);
			swal({
				title : "是否确认通过申请?",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("success").submit();
			});
		}
	</script>
	
	<script type="text/javascript">
		function setState(state){
    		document.getElementById("succeeded_para").value=state;
    		document.getElementById("queryForm").submit();
		}
		
		function getallname(name){
			$("#usernallName").html(name);
			$("#user_all_name_a").attr("href","https://etherscan.io/address/"+name);
			$("#net_form").modal("show");
		}
		
	</script>

</body>

</html>
