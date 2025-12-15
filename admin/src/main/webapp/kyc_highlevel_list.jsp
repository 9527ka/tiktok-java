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

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>用户高级认证</h3>

			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminKycHighLevelAction!list.action"
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
										<td>推荐人</td>
										<td>账户类型</td>
										<!-- <td>工作地址</td> -->
										<!-- <td>家庭地址</td> -->
										<td>亲属姓名</td>
										<td>亲属关系</td>
										<!-- <td>亲属地址</td> -->
										<td>亲属电话</td>
										<td>认证状态</td>
										<td>客户提交时间</td>
										<td>审核操作时间</td>
										<td>原因</td>
										<td width="130px"></td>
									</tr>
								</thead>
								
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>
												<a href="#" onClick="detail('${item.username}',
													'${item.work_place}',
													'${item.home_place}',
													'${item.relatives_place}>',
													'${item.msg}',
													'${item.relatives_name_encode}',
													'${item.relatives_relation}',
													'${item.relatives_phone}',
													'${item.idimg_1}',
													'${item.idimg_2}',
													'${item.idimg_3}')">
													${item.username}
												</a>
											</td>
											<td>${item.usercode}</td>
											<td>${item.username_parent}</td>
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
											<td>${item.relatives_name}</td>
											<td>${item.relatives_relation}</td>
											<td>${item.relatives_phone}</td>
											<td>
												<c:if test="${item.status==0}">未审核</c:if> 
												<c:if test="${item.status==1}">审核中</c:if> 
												<c:if test="${item.status==2}"> <span class="right label label-success">审核通过</span> </c:if> 
												<c:if test="${item.status==3}">未通过</c:if>
											</td>
											<td>${item.apply_time}</td>
											<td>${item.operation_time}</td>
											<td>${item.msg}</td>
											
											<td>
				
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															 || security.isResourceAccessible('OP_USER_KYC_HIGH_LEVEL_OPERATE')}">
												
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button"
															class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
														
															<li><a href="javascript:savePassed('${item.partyId}')">审核通过</a></li>
															<li><a href="javascript:saveFailed('${item.partyId}')">驳回</a></li>
														
														</ul>
													</div>
													
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

	<%@ include file="include/js.jsp"%>
	
	<!-- 模态框（Modal） -->
	<div class="modal fade" id="modal_detail" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content" style="width: 725px;">
			
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">详细信息</h4>
				</div>
				
				<div class="modal-body">
					<div class="">
						工作地址
						<input id="modal_work_place" type="text" name="modal_work_place" class="form-control" readonly="readonly" />
					</div>
					<div class="">
						家庭地址
						<input id="modal_home_place" type="text" name="modal_home_place" class="form-control" readonly="readonly" />
					</div>
					<div class="">
						原因
						<input id="modal_msg" type="text" name="modal_msg" class="form-control" readonly="readonly" />
					</div>
				</div>
				
				<div class="modal-header">
					<h4 class="modal-title" id="myModalLabel">亲属信息</h4>
				</div>
				
				<div class="modal-body">
					<div class="">
						亲属关系
						<input id="modal_relatives_relation" type="text" name="modal_relatives_relation" class="form-control" readonly="readonly" />
					</div>
					<div class="">
						亲属姓名
						<input id="modal_relatives_name" type="text" name="modal_relatives_name" class="form-control" readonly="readonly" />
					</div>
					<div class="">
						亲属电话
						<input id="modal_relatives_phone" type="text" name="modal_relatives_phone" class="form-control" readonly="readonly" />
					</div>
					<div class="">
						亲属地址
						<input id="modal_relatives_place" type="text" name="modal_relatives_place" class="form-control" readonly="readonly" />
					</div>
				</div>
				
				<div class="modal-footer" style="margin-top: 0;">
					<button type="button" class="btn " data-dismiss="modal">关闭</button>
				</div>
				
			</div>
		</div>
	</div>

	<form action="<%=basePath%>normal/adminKycHighLevelAction!savePassed.action"
		method="post" id="savePassed">
		
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="name_para" id="name_para" value="${name_para}">
		<input type="hidden" name="state_para" id="state_para" value="${state_para}">
		<input type="hidden" name="partyId" id="partyId_savePassed" value="${partyId}">
		
	</form>

	<script type="text/javascript">
		function savePassed(partyId) {
			$("#partyId_savePassed").val(partyId);
			swal({
				title : "是否确认审核通过?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("savePassed").submit();
			});
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
		function saveFailed(partyId) {
			$("#partyId_saveFailed").val(partyId);
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
					<form action="<%=basePath%>normal/adminKycHighLevelAction!saveFailed.action"
						method="post" id="saveFailed">
						
						<input type="hidden" name="session_token" id="session_token_reject" value="${session_token}">
						<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
						<input type="hidden" name="name_para" id="name_para" value="${name_para}">
						<input type="hidden" name="state_para" id="state_para" value="${state_para}">
						<input type="hidden" name="partyId" id="partyId_saveFailed" value="${partyId}">
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
		function detail(username, work_place, home_place, relatives_place, msg, relatives_name, relatives_relation, relatives_phone, idimg_1, idimg_2, idimg_3) {
			// $("#modal_username").val(username);
			$("#modal_work_place").val(work_place);
			$("#modal_home_place").val(home_place);
			$("#modal_relatives_place").val(relatives_place);
			$("#modal_relatives_name").val(relatives_name);
			$("#modal_relatives_relation").val(relatives_relation);
			$("#modal_relatives_phone").val(relatives_phone);
			$("#modal_msg").val(msg);
			$("#modal_idimg_1").attr("src", idimg_1);
			$("#modal_idimg_1").parent().attr("href", idimg_1);
			$("#modal_idimg_2").attr("src", idimg_2);
			$("#modal_idimg_2").parent().attr("href", idimg_2);
			$("#modal_idimg_3").attr("src", idimg_3);
			$("#modal_idimg_3").parent().attr("href", idimg_3);
			$('#modal_detail').modal("show");
		}
	</script>

</body>

</html>
