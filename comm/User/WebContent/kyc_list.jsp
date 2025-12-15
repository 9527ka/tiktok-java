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
			<h3>用户基础认证管理</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
							<s:if test='isResourceAccessible("ADMIN_KYC_LIST")'>
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminKycAction!list.action"
								method="post" id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
									<s:hidden name="state_para"></s:hidden>
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<s:textfield id="name_para" name="name_para"
													cssClass="form-control " placeholder="用户名、UID" />
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3">
										<fieldset>
											<div class="control-group">
												<div class="controls">
													<s:select id="rolename_para" cssClass="form-control "
														name="rolename_para"
														list="#{'MEMBER':'正式账号','GUEST':'演示账号'}" listKey="key"
														listValue="value" headerKey="" headerValue="所有账号"
														value="rolename_para" />
												</div>
											</div>
										</fieldset>
									</div>
							

								<!-- <div class="col-md-12 col-lg-2" >
									<button type="submit" class="btn  btn-default btn-block">查询</button>
								</div> -->
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
<!-- 										<td>ID名称</td> -->
<!-- 										<td>证件号码</td> -->
										<td>实名姓名</td>
<!-- 										<td>国籍</td> -->
<!-- 										<td>证件正面照</td> -->
<!-- 										<td>证件背面照</td> -->
<!-- 										<td>手持正面照</td> -->
										<td>手机绑定</td>
										<td>邮箱绑定</td>
										<td>认证状态</td>
										<td>原因</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
									
										<tr>
											<td><a href="#" onClick="detail('<s:property value="name" />','<s:property value="idnumber" />','<s:property value="nationality" />','<s:property value="idimg_1" />','<s:property value="idimg_2" />','<s:property value="idimg_3" />','<s:property value="idname" />')"><s:property value="username" /></a></td>
											<td><s:property value="usercode" /></td>
											<td><s:if test='rolename=="GUEST"'>
													<span class="right label label-warning">演示账号</span>
												</s:if>
												<s:if test='rolename=="MEMBER"'>
													<span class="right label label-success">正式账号</span>
												</s:if>
											</td>
											
<%-- 											<td><s:property value="idname" /></td> --%>
<%-- 											<td><s:property value="idnumber" /></td> --%>
											<td><s:property value="name" /></td>
<%-- 											<td><s:property value="nationality" /></td> --%>
<%-- 											<td><a href="${basePath}/public/showimg!showImg.action?imagePath=<s:property value="idimg_1" />" target="_blank">查看照片</a></td> --%>
<%-- 											<td><a href="${basePath}/public/showimg!showImg.action?imagePath=<s:property value="idimg_2" />" target="_blank">查看照片</a></td> --%>
<%-- 											<td><a href="<s:property value="idimg_1" />" target="_blank">查看照片</a></td> --%>
<%-- 											<td><a href="<s:property value="idimg_2" />" target="_blank">查看照片</a></td> --%>
<%-- 											<td><a href="<s:property value="idimg_3" />" target="_blank">查看照片</a></td> --%>
											<td><s:property value="phone" /></td>
											<td><s:property value="email" /></td>
											<td><s:if test="status==0">未审核</s:if>
											<s:if test="status==1">审核中</s:if>
											<s:if test="status==2"><span class="right label label-success">审核通过</span></s:if>
											<s:if test="status==3">未通过</s:if></td>
											<td><s:property value="msg" /></td>
											<td>
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
													
													
														<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
														<s:if test='isResourceAccessible("ADMIN_KYC_SAVEPASSED")'>
														<li><a href="javascript:savePassed('<s:property value="partyId" />')">审核通过</a></li>
														</s:if>
														<s:if test='isResourceAccessible("ADMIN_KYC_SAVEFAILED")'>
														<li><a href="javascript:saveFailed('<s:property value="partyId" />')">驳回</a></li>
														</s:if>
														</sec:authorize>
														
													</ul>
												</div>
											</td>
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
<!-- 模态框（Modal） -->
	<div class="modal fade" id="modal_detail" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel"
		aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content" style="width:725px;">
				<div class="modal-header">
					<button type="button" class="close"
						data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title" >详细信息</h4>
				</div>
				<div class="modal-body">
					<div class="" >
						实名姓名<input id="modal_name" type="text" name="modal_name"
							class="form-control" readonly="true" />
					</div>
					<div class="" >
						证件名称<input id="modal_idname" type="text" name="modal_idname"
							class="form-control" readonly="true"/>
					</div>
					<div class="" >
						证件号码<input id="modal_idnumber" type="text" name="modal_idnumber"
							class="form-control" readonly="true"/>
					</div>
					<div class="" >
						国籍<input id="modal_nationality" type="text" name="modal_nationality"
							class="form-control" readonly="true"/>
					</div>
					
				</div>
				<div class="modal-header">
				
					<h4 class="modal-title" id="myModalLabel">证件照</h4>
				</div>
				<div class="modal-body col-md-12">
					<div class="col-md-12 col-lg-4" >证件正面照
					<a  href="#"  target="_blank">
					<img width="200px" height="200px"
						id="modal_idimg_1" name="modal_idimg_1" src=""
						 />
					</a>
					</div>
					<div class="col-md-12 col-lg-4" >证件背面照
					<a  href="#"  target="_blank">
					<img width="200px" height="200px"
						id="modal_idimg_2" name="modal_idimg_2" src=""
						 />
					</a>
					</div>
					<div class="col-md-12 col-lg-4" >手持正面照
					<a  href="#"  target="_blank">
					<img width="200px" height="200px"
						id="modal_idimg_3" name="modal_idimg_3" src=""
						 />
					</a>	
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

	<%@ include file="include/js.jsp"%>
	

	<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
	<s:if test='isResourceAccessible("ADMIN_KYC_SAVEPASSED")'>
														
	<form action="<%=basePath%>normal/adminKycAction!savePassed.action" method="post" id="savePassed">
		<input type="hidden" name="pageNo" id="pageNo" value="${param.pageNo}">
		<s:hidden name="name_para" id="name_para"></s:hidden>
		<s:hidden name="state_para" id="state_para"></s:hidden>
		<s:hidden name="partyId" id="partyId_savePassed"></s:hidden>
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
	</s:if>
	<s:if test='isResourceAccessible("ADMIN_KYC_SAVEFAILED")'>
														
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
	</s:if>
	<!-- Modal -->
	<s:if test='isResourceAccessible("ADMIN_KYC_SAVEFAILED")'>
	<div class="modal fade" id="modal_saveFailed" tabindex="-1" role="dialog"
		aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">请输入驳回原因</h4>
				</div>
				<div class="modal-body">
				<form action="<%=basePath%>normal/adminKycAction!saveFailed.action" method="post" id="saveFailed">
				<s:hidden name="session_token" id="session_token_reject"></s:hidden>
		<input type="hidden" name="pageNo" id="pageNo" value="${param.pageNo}">
		<s:hidden name="name_para" id="name_para"></s:hidden>
		<s:hidden name="state_para" id="state_para"></s:hidden>
		<s:hidden name="partyId" id="partyId_saveFailed"></s:hidden>
		<s:textarea name="msg" id="msg" cssClass="form-control  input-lg" rows="2" cols="10" placeholder="驳回原因"/> 
	</form>
				 	</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-default" onclick="saveFailed_confirm()">确认驳回</button>
				</div>
			</div>
		</div>
	</div>
	</s:if>
</sec:authorize>

<script type="text/javascript">
		function setState(state){
    		document.getElementById("state_para").value=state;
    		document.getElementById("queryForm").submit();
		}
		function detail(name,idnumber,nationality,idimg_1,idimg_2,idimg_3,idname){
// 			 $("#id_success").val(id);
			$("#modal_name").val(name);
			$("#modal_idname").val(idname);
			$("#modal_idnumber").val(idnumber);
			$("#modal_nationality").val(nationality);
			$("#modal_idimg_1").attr("src",idimg_1);
			$("#modal_idimg_1").parent().attr("href",idimg_1);
			$("#modal_idimg_2").attr("src",idimg_2);
			$("#modal_idimg_2").parent().attr("href",idimg_2);
			$("#modal_idimg_3").attr("src",idimg_3);
			$("#modal_idimg_3").parent().attr("href",idimg_3);
			$('#modal_detail').modal("show");
			 
		}
	</script>
	
	
	
	
	
	

</body>
</html>