<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
			<h3>人工重置管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminUserSafewordApplyAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
								<input type="hidden" name="state_para" id="state_para" value="${state_para}" />
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="name_para" name="name_para"
													class="form-control " placeholder="用户名、UID" value="${name_para}" />
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
													<option value="TEST" <c:if test="${rolename_para == 'TEST'}">selected="true"</c:if> >试用账号</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="operate" name="operate" class="form-control " >
													<option value="">操作类型</option>
													<option value="0" <c:if test="${operate == '0'}">selected="true"</c:if> >修改资金密码</option>
													<option value="1" <c:if test="${operate == '1'}">selected="true"</c:if> >取消谷歌绑定</option>
													<option value="2" <c:if test="${operate == '2'}">selected="true"</c:if> >取消手机绑定</option>
													<option value="3" <c:if test="${operate == '3'}">selected="true"</c:if> >取消邮箱绑定</option>
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
												<li><a href="javascript:setState(1)"> 待审核</a></li>
												<li><a href="javascript:setState(2)"> 审核通过</a></li>
												<li><a href="javascript:setState(3)"> 未通过</a></li>
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
										<td>实名姓名</td>
										<td>实名认证状态</td>
										<td>审核状态</td>
										<td>原因</td>
										<td>申请时间</td>
										<td>操作类型</td>
										<td>用户等级</td>
										<td>备注</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>
												<a href="#" onClick="detail('${item.kyc_name}','${item.idimg_1}','${item.idimg_2}','${item.idimg_3}',
													'${item.kyc_idimg_1}','${item.kyc_idimg_2}','${item.kyc_idimg_3}')">
													${item.username}
												</a>
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
													<c:when test="${item.rolename=='TEST'}">
														<span class="right label label-default">${item.roleNameDesc}</span>
													</c:when>
													<c:otherwise>
													 	${item.roleNameDesc}
													</c:otherwise>	
												</c:choose>
											</td>
											<td>${item.kyc_name}</td>
											<td>
												<c:if test="${item.kyc_status==2}"><span class="right label label-success">已认证</span></c:if>
												<c:if test="${item.kyc_status!=2}">未认证</c:if>
											</td>
											<td>
												<c:if test="${item.status==0}">未审核</c:if> 
												<c:if test="${item.status==1}">待审核</c:if> 
												<c:if test="${item.status==2}"><span class="right label label-success">审核通过</span></c:if>
												<c:if test="${item.status==3}">未通过</c:if>
											</td>
											<td>${item.msg}</td>
											<td>${item.create_time}</td>
											<td>
												<c:if test="${item.operate==0}">修改资金密码</c:if> 
												<c:if test="${item.operate==1}">取消谷歌绑定</c:if> 
												<c:if test="${item.operate==2}">取消手机绑定</c:if>
												<c:if test="${item.operate==3}">取消邮箱绑定</c:if>
											</td>
											<td>${item.user_level}</td>
											<td>${item.remark}</td>
											
											<td>
															
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_USER_SAFEWORD_APPLY_OPERATE')}">
															 
													<c:if test="${item.status == 1}">
												
														<div class="btn-group">
															<button type="button" class="btn btn-light">操作</button>
															<button type="button"
																class="btn btn-light dropdown-toggle"
																data-toggle="dropdown" aria-expanded="false">
																<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
															</button>
															<ul class="dropdown-menu" role="menu">
															
																<li><a href="javascript:savePassed('${item.id}')">审核通过</a></li>
																<li><a href="javascript:saveFailed('${item.id}')">驳回</a></li>
																															
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
	
	<!-- 模态框（Modal） -->
	<div class="modal fade" id="modal_detail" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content" style="width: 725px;">
			
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">详细信息</h4>
				</div>
				
				<div class="modal-body">
					<div class="">
						实名姓名
						<input id="modal_name" type="text" name="modal_name" class="form-control" readonly="readonly" />
					</div>
				</div>
				
				<div class="modal-header">
					<h4 class="modal-title" id="myModalLabel">实名认证证件照</h4>
				</div>
				
				<div class="modal-body col-md-12">
				
					<div class="col-md-12 col-lg-4">
						证件正面照 
						<a href="#" target="_blank"> 
							<img width="200px" height="200px" id="modal_kyc_idimg_1" name="modal_kyc_idimg_1" src="" />
						</a>
					</div>
					
					<div class="col-md-12 col-lg-4">
						证件背面照 
						<a href="#" target="_blank"> 
							<img width="200px" height="200px" id="modal_kyc_idimg_2" name="modal_kyc_idimg_2" src="" />
						</a>
					</div>
					
					<div class="col-md-12 col-lg-4">
						手持正面照 
						<a href="#" target="_blank"> 
							<img width="200px" height="200px" id="modal_kyc_idimg_3" name="modal_kyc_idimg_3" src="" />
						</a>
					</div>
					
				</div>
				
				<div class="modal-header">
					<h4 class="modal-title" id="myModalLabel">重置申请证件照</h4>
				</div>
				
				<div class="modal-body col-md-12">
				
					<div class="col-md-12 col-lg-4">
						证件正面照 
						<a href="#" target="_blank"> 
							<img width="200px" height="200px" id="modal_idimg_1" name="modal_idimg_1" src="" />
						</a>
					</div>
					
					<div class="col-md-12 col-lg-4">
						证件背面照 
						<a href="#" target="_blank"> 
							<img width="200px" height="200px" id="modal_idimg_2" name="modal_idimg_2" src="" />
						</a>
					</div>
					
					<div class="col-md-12 col-lg-4">
						手持正面照 
						<a href="#" target="_blank"> 
							<img width="200px" height="200px" id="modal_idimg_3" name="modal_idimg_3" src="" />
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

	<%@ include file="include/js.jsp"%>

	<div class="modal fade" id="modal_succeeded" tabindex="-1" role="dialog" aria-hidden="true">
	
		<form action="<%=basePath%>normal/adminUserSafewordApplyAction!savePassed.action"
			method="post" id="saveSuccess">
			
			<input type="hidden" name="session_token" id="session_token_reject" value="${session_token}">
			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
			<input type="hidden" name="name_para" id="name_para" value="${name_para}">
			<input type="hidden" name="state_para" id="state_para" value="${state_para}">
			<input type="hidden" name="id" id="id_savePassed" value="${id}">
			
			<div class="modal-dialog">
				<div class="modal-content">
				
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title">审核通过</h4>
					</div>
					
					<div class="modal-body">
						<input name="safeword" id="safeword" type="password" class="form-control" placeholder="请输入资金密码" />
					</div>
					
					<div class="modal-footer">
						<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
						<button type="submit" class="btn btn-default">确认通过</button>
					</div>
					
				</div>
			</div>
			
		</form>
		
	</div>

	<script type="text/javascript">
		function savePassed(id) {
			$("#id_savePassed").val(id);
			$("#modal_succeeded").modal("show");
		}
	</script>

	<script type="text/javascript">
		function saveFailed_confirm() {
			swal({
				title : "是否确认驳回?",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("saveFailed").submit();
			});
		};
		function saveFailed(id) {
			$("#id_saveFailed").val(id);
			$('#modal_saveFailed').modal("show");
		};
	</script>
	
	<!-- Modal -->
	<div class="modal fade" id="modal_saveFailed" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
			
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">请输入驳回原因</h4>
				</div>
				
				<div class="modal-body">
				
					<form action="<%=basePath%>normal/adminUserSafewordApplyAction!saveFailed.action"
						method="post" id="saveFailed">
						
						<input type="hidden" name="session_token" id="session_token_reject" value="${session_token}">
						<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
						<input type="hidden" name="name_para" id="name_para" value="${name_para}">
						<input type="hidden" name="state_para" id="state_para" value="${state_para}">
						<input type="hidden" name="id" id="id_saveFailed" value="${id}">
						<textarea name="msg" id="msg" class="form-control  input-lg" rows="2" cols="10" placeholder="驳回原因" >${msg}</textarea>
						
					</form>
					
				</div>
				
				<div class="modal-footer">
					<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-default" onclick="saveFailed_confirm()">确认驳回</button>
				</div>
				
			</div>
		</div>
	</div>

	<script type="text/javascript">
		function setState(state) {
			document.getElementById("state_para").value = state;
			document.getElementById("queryForm").submit();
		}
		function detail(name, idimg_1, idimg_2, idimg_3, kyc_idimg_1, kyc_idimg_2, kyc_idimg_3) {
			// $("#id_success").val(id);
			$("#modal_name").val(name);

			$("#modal_idimg_1").attr("src", idimg_1);
			$("#modal_idimg_1").parent().attr("href", idimg_1);
			$("#modal_idimg_2").attr("src", idimg_2);
			$("#modal_idimg_2").parent().attr("href", idimg_2);
			$("#modal_idimg_3").attr("src", idimg_3);
			$("#modal_idimg_3").parent().attr("href", idimg_3);

			$("#modal_kyc_idimg_1").attr("src", kyc_idimg_1);
			$("#modal_kyc_idimg_1").parent().attr("href", kyc_idimg_1);
			$("#modal_kyc_idimg_2").attr("src", kyc_idimg_2);
			$("#modal_kyc_idimg_2").parent().attr("href", kyc_idimg_2);
			$("#modal_kyc_idimg_3").attr("src", kyc_idimg_3);
			$("#modal_kyc_idimg_3").parent().attr("href", kyc_idimg_3);
			$('#modal_detail').modal("show");
		}
	</script>

</body>

</html>
