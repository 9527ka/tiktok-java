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
	<style>
		td {
			word-wrap: break-word; /* 让内容自动换行 */
			max-width: 200px; /* 设置最大宽度，以防止内容过长 */
		}
	</style>
</head>

<body>

	<%@ include file="include/loading.jsp"%>
	<%-- 	<%@ include file="include/top.jsp"%> --%>
	<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<input type="hidden" name="session_token" id="session_token" value="${session_token}" />

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>借贷记录</h3>

			<%@ include file="include/alert.jsp"%>

			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>credit/history.action"
								method="post" id="queryForm">

								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">

								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="userCode" name="userCode"
													class="form-control " placeholder="用户ID" value="${userCode}" />
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="name_para" name="name_para"
													cssClass="form-control " placeholder="用户名、UID" /> -->
												<input id="userName" name="userName" class="form-control "
													placeholder="用户名、UID" value="${userName}" />
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="identification" name="identification" class="form-control "
													placeholder="证件号查询" value="${identification}" />
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-3" >
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="status" name="status" class="form-control">
													<option value="">审核状态</option><!-- 状态(1待审核,2审核通过,3已逾期,4未通过,5已还款)', -->
													<option value="1" <c:if test="${status == '1'}">selected="true"</c:if> >待审核</option>
													<option value="4" <c:if test="${status == '4'}">selected="true"</c:if> >未通过</option>
													<option value="2" <c:if test="${status == '2'}">selected="true"</c:if> >审核通过</option>
													<option value="5" <c:if test="${status == '5'}">selected="true"</c:if> >已还款</option>
													<option value="3" <c:if test="${status == '3'}">selected="true"</c:if> >已逾期</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2" style="margin-top: 10px">
									<input id="customerSubmitTime_start" name="customerSubmitTime_start" class="form-control "
										   placeholder="开始日期" value="${customerSubmitTime_start}" />
								</div>
								<div class="col-md-12 col-lg-2" style="margin-top: 10px">

									<input id="customerSubmitTime_end" name="customerSubmitTime_end" class="form-control "
										   placeholder="结束日期" value="${customerSubmitTime_end}" />
								</div>
								<div class="col-md-12 col-lg-2" style="margin-top: 10px">
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
										<td>会员ID</td>
										<td>推荐人</td>
										<td>账户类型</td>
										<td>认证信息</td>
										<td>申请人</td>
										<td>审核状态</td>
										<td>贷款期限</td>
										<td>申请金额</td>
										<td>贷款利率</td>
										<td>总利息</td>
										<td>总还款金额</td>
										<td>实际还款</td>
										<td>驳回原因</td>
										<td>客户提交时间</td>
										<td>系统审核时间</td>
										<td>最后还款时间</td>
										<td width="150px"></td>
									</tr>
								</thead>

								<tbody style="font-size: 13px;">
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
									<tr>
									    <td>${item.usercode}</td>
										<td>${item.username_parent}</td>
										<td>
											<c:choose>
												<c:when test="${item.rolename=='MEMBER'}">
													<span class="right label label-success">真实账号</span>
												</c:when>
												<c:when test="${item.rolename=='GUEST'}">
													<span class="right label label-warning">演示账号</span>
												</c:when>
											</c:choose>
										</td>
										<td>
											<a href="#" onClick="detail('${item.creditId}',
													'${item.realName}',
													'${item.identification}',
													'${item.countryId}',
													'${item.imgCertificateFace}',
													'${item.imgCertificateBack}',
													'${item.imgCertificateHand}')">
													查看
											</a>
										</td>
										<td>${item.username}</td>

										<td>
											<c:if test="${item.status =='1'}">

												<span class="right label label-warning">待审核</span>
											</c:if>
											<c:if test="${item.status =='2'}">
												<span class="right label label-success">审核通过</span>
											</c:if>
											<c:if test="${item.status =='3'}">
												<span class="right label label-danger">已逾期</span>
											</c:if>
											<c:if test="${item.status =='4'}">
												<span class="right label label-danger">未通过</span>
											</c:if>
											<c:if test="${item.status =='5'}">
												<span class="right label label-default">已还款</span>
											</c:if>
										</td>

										<td>${item.creditPeriod}天</td>
										<td> <fmt:formatNumber value="${item.applyAmount}" pattern="#0.00"/></td>
										<td> <fmt:formatNumber value="${item.creditRate}" pattern="#0.00"/></td>
										<td> <fmt:formatNumber value="${item.totalInterest}" pattern="#0.00"/></td>
										<td> <fmt:formatNumber value="${item.totalRepayment}" pattern="#0.00"/></td>
										<td> <fmt:formatNumber value="${item.actualRepayment}" pattern="#0.00"/></td>
										<td>${item.rejectReason}</td>
										<td>${item.customerSubmitTime}</td>
										<td>${item.systemAuditTime}</td>
										<td>${item.finalRepayTime}</td>

										<td>

											<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_CREDIT_OPERATE')}">
												<c:if test="${item.status == 1 || item.status == 2 || item.status == 3}">
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">

															<c:if test="${item.status == 1}">
																<li><a href="javascript:onsucceeded('${item.creditId}')">通过</a></li>
																<li><a href="javascript:reject('${item.creditId}','4')">驳回申请</a></li>
															</c:if>

															<c:if test="${item.status == 2 || item.status == 3 }">
																<li><a href="javascript:manualRepayment('${item.creditId}','${item.username}')">手动还款</a></li>
															</c:if>

														</ul>
													</div>

												</c:if>
											</c:if>
											<!-- 模态框 -->
											<div class="form-group">
											
												<form action="<%=basePath%>credit/pass.action"
													method="post" id="succeededForm">
													
													<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
													<input type="hidden" name="creditId" id="creditId" value="${creditId}">
													<input type="hidden" name="session_token" id="session_token_success" value="${session_token}">

													<div class="col-sm-1">
														<!-- 模态框（Modal） -->
														<div class="modal fade" id="modal_set" tabindex="-1"
															role="dialog" aria-labelledby="myModalLabel"
															aria-hidden="true">
															<div class="modal-dialog">
																<div class="modal-content">

																	<div class="modal-header">
																		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
																		<h4 class="modal-title" id="myModalLabel">提现借贷申请（手动打款）</h4>
																	</div>
																	
																	<div class="modal-body">
																		<div class="">
																			<input id="safeword" type="password" name="safeword"
																				class="form-control" placeholder="请输入资金密码">
																		</div>
																	</div>

																	<div class="modal-footer" style="margin-top: 0;">
																		<button type="button" class="btn " data-dismiss="modal">关闭</button>
																		<button id="sub" type="submit" class="btn btn-default">确认</button>
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

												<form action="<%=basePath%>credit/operate.action"
													  method="post" id="succeededForms">

													<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
													<input type="hidden" name="creditId" id="creditId_repayment" value="${creditId}">
													<input type="hidden" name="operateType" id="operateType_repayment" value="${operateType}">
													<input type="hidden" name="session_token" id="session_token_success" value="${session_token}">

													<div class="col-sm-1">
														<!-- 模态框（Modal） -->
														<div class="modal fade" id="modal_set_manual_repayment" tabindex="-1" role="dialog"
															 aria-labelledby="myModalLabel" aria-hidden="true">
															<div class="modal-dialog">
																<div class="modal-content">

																	<div class="modal-header">
																		<button type="button" class="close" data-dismiss="modal"
																				aria-hidden="true">&times;</button>
																		<h4 class="modal-title">申请人</h4>
																	</div>

																	<div class="modal-body">
																		<div class="">
																			<input id="usernames" name="username" type="text"
																				   class="form-control" readonly="readonly" value="${username}">
																		</div>
																	</div>

																	<div class="modal-header">
																		<h4 class="modal-title">已贷款天数</h4>
																	</div>

																	<div class="modal-body">
																		<div class="">
																			<input id="creditPeriod" name="creditPeriod" type="text"
																				   class="form-control" readonly="readonly" value="${creditPeriod}" >
																			<!-- readonly="true" -->
																		</div>
																	</div>

																	<div class="modal-header">
																		<h4 class="modal-title">应还款金额</h4>
																	</div>

																	<div class="modal-body">
																		<div class="">
																			<input id="totalRepayment" name="exchange_rate" type="text"
																				   class="form-control" readonly="totalRepayment" value="${totalRepayment}" >
																			<!-- readonly="true" -->
																		</div>
																	</div>

																	<div class="modal-header">
																		<h4 class="modal-title" >还款金额</h4>
																	</div>

																	<div class="modal-body">
																		<div class="">
																			<input id="manualRepay" name="manualRepay"
																				   class="form-control" placeholder="请输入资金密码" value="${manualRepay}" >
																		</div>
																	</div>

																	<div class="modal-header">
																		<h4 class="modal-title" id="myModalLabel">资金密码</h4>
																	</div>

																	<div class="modal-body">
																		<div class="">
																			<input id="safeword" type="password" name="safeword"
																				   class="form-control" placeholder="请输入资金密码" value="${safeword}" >
																		</div>
																	</div>

																	<div class="modal-footer" style="margin-top: 0;">
																		<button type="button" class="btn " data-dismiss="modal">关闭</button>
																		<button id="sub" type="submit" class="btn btn-default">确认</button>
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

										</td>
										
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
		<div class="modal fade" id="modal_detail" tabindex="-1" role="dialog"
			 aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content" style="width: 725px;">

					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
						<h4 class="modal-title">详细信息</h4>
					</div>

					<input type="hidden" name="partyId_modal_detail" id="partyId_modal_detail"/>
					<input type="hidden" name="img_idimg_1" id="img_idimg_1"/>
					<input type="hidden" name="img_idimg_2" id="img_idimg_2"/>
					<input type="hidden" name="img_idimg_3" id="img_idimg_3"/>

					<div class="modal-body">
						<div class="">
							实名姓名<input id="modal_name" type="text" name="modal_name"
										   class="form-control" readonly="readonly" />
						</div>
						<div class="">
							证件号码<input id="modal_idnumber" type="text" name="modal_idnumber"
										   class="form-control" readonly="readonly" />
						</div>
						<div class="">
							国籍<input id="modal_nationality" type="text"
									   name="modal_nationality" class="form-control" readonly="readonly" />
						</div>
					</div>

					<div class="modal-header">
						<h4 class="modal-title" id="myModalLabel">证件照</h4>
					</div>

					<div class="modal-body col-md-12">
						<div class="col-md-12 col-lg-4">
							证件正面照
							<a href="#" target="_blank">
								<img width="200px" height="200px" id="modal_idimg_1" name="modal_idimg_1" src="" />
							</a>
							<div class="col-md-6">
								<input type="file" id="fileName_1" name="fileName"  value="${fileName}" onchange="upload_idimg('1');" style="position:absolute;opacity:0;">
								<label for="fileName">
									<button type="button" class="btn btn-light btn-block">修改</button>
									　　							</label>
							</div>
							<div class="col-md-6">
								<button type="button" class="btn btn-light btn-block" onclick="submit_idimg('1')">提交</button>
							</div>
						</div>
						<div class="col-md-12 col-lg-4">
							证件背面照
							<a href="#" target="_blank">
								<img width="200px" height="200px" id="modal_idimg_2" name="modal_idimg_2" src="" />
							</a>
							<div class="col-md-6">
								<input type="file" id="fileName_2" name="fileName"  value="${fileName}" onchange="upload_idimg('2');" style="position:absolute;opacity:0;">
								<label for="fileName">
									<button type="button" class="btn btn-light btn-block">修改</button>
									　　							</label>
							</div>
							<div class="col-md-6">
								<button type="button" class="btn btn-light btn-block" onclick="submit_idimg('2')">提交</button>
							</div>
						</div>
						<div class="col-md-12 col-lg-4">
							手持正面照
							<a href="#" target="_blank">
								<img width="200px" height="200px" id="modal_idimg_3" name="modal_idimg_3" src="" />
							</a>
							<div class="col-md-6">
								<input type="file" id="fileName_3" name="fileName"  value="${fileName}" onchange="upload_idimg('3');" style="position:absolute;opacity:0;">
								<label for="fileName">
									<button type="button" class="btn btn-light btn-block">修改</button>
									　　							</label>
							</div>
							<div class="col-md-6">
								<button type="button" class="btn btn-light btn-block" onclick="submit_idimg('3')">提交</button>
							</div>
						</div>
					</div>

					<div class="modal-footer" style="margin-top: 0;">
						<button type="button" class="btn " data-dismiss="modal">关闭</button>
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

	<form action="<%=basePath%>credit/updateCreditPic.action" method="post" id="updateCreditPic">
		<input type="hidden" name="creditId_updateCreditPic" id="creditId_updateCreditPic">
		<input type="hidden" name="img_id_updateCreditPic" id="img_id_updateCreditPic">
		<input type="hidden" name="img_updateCreditPic" id="img_updateCreditPic">
	</form>



	<script type="text/javascript">
		function reject_confirm() {
			swal({
				title : "是否确认驳回?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("onreject").submit();
			});	
		};

		function reject(creditId,operateType) {
			var session_token = $("#session_token").val();
			$("#session_token_reject").val(session_token);
			$("#creditIds").val(creditId);
			$("#operateType").val(operateType);
			$('#modal_reject').modal("show");
		};
	</script>

	<!-- Modal -->
	<div class="modal fade" id="modal_reject" tabindex="-1" role="dialog"
		aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
			
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">请输入驳回原因</h4>
				</div>
				
				<div class="modal-body">
					<form action="<%=basePath%>credit/operate.action"
						method="post" id="onreject">
						<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
						<input type="hidden" name="creditId" id="creditIds" value="${creditId}">
						<input type="hidden" name="session_token" id="session_token_reject" value="${session_token}">
						<input type="hidden" name="operateType" id="operateType" value="${operateType}">

						<textarea name="rejectReason" id="rejectReason" class="form-control  input-lg" rows="2" cols="10" placeholder="驳回原因,限制300字符" maxlength="300">${rejectReason}</textarea>
					</form>
				</div>
				
				<div class="modal-footer">
					<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-default" onclick="reject_confirm()">驳回申请</button>
				</div>
				
			</div>
		</div>
	</div>
	<!-- End Moda Code -->

	<script type="text/javascript">

		$(function() {
			$('#customerSubmitTime_start').datetimepicker({
				format : 'yyyy-mm-dd hh:ii:00',
				minuteStep:1,
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true
			});
			$('#customerSubmitTime_end').datetimepicker({
				format : 'yyyy-mm-dd hh:ii:00',
				minuteStep:1,
				language : 'zh',
				weekStart : 1,
				todayBtn : 1,
				autoclose : 1,
				todayHighlight : 1,
				startView : 2,
				clearBtn : true
			});

		});


		function detail(partyId, name, idnumber, nationality, idimg_1, idimg_2, idimg_3) {
			$("#partyId_modal_detail").val(partyId);
			$("#img_idimg_1").val(idimg_1);
			$("#img_idimg_2").val(idimg_2);
			$("#img_idimg_3").val(idimg_3);

			// $("#id_success").val(id);
			$("#modal_name").val(name);
			// $("#modal_idname").val(idname);
			$("#modal_idnumber").val(idnumber);
			getValue(nationality);
			$("#modal_idimg_1").attr("src", idimg_1);
			$("#modal_idimg_1").parent().attr("href", idimg_1);
			$("#modal_idimg_2").attr("src", idimg_2);
			$("#modal_idimg_2").parent().attr("href", idimg_2);
			$("#modal_idimg_3").attr("src", idimg_3);
			$("#modal_idimg_3").parent().attr("href", idimg_3);


			$('#modal_detail').modal("show");
		}

		function getValue(code){
			$.ajax({
				type: "get",
				url: "<%=basePath%>credit/findCode.action",
				dataType : "json",
				data : {
					"code" : code
				},
				success : function(data) {
					var tmp = data;
					var countryNameCn = tmp.countryNameCn;
					$("#modal_nationality").val(countryNameCn);
				},
				error : function(XMLHttpRequest, textStatus,
								 errorThrown) {
					console.log("请求错误");
				}
			});
		}

		function upload_idimg(img_id){
			var fileReader = new FileReader();
			var formData = new FormData();
			var file = document.getElementById('fileName_' + img_id).files[0];
			formData.append("file", file);
			formData.append("moduleName","goods");
			$.ajax({
				type: "POST",
				url: "<%=basePath%>normal/uploadimg!execute.action",
				data: formData,
				dataType: "json",
				contentType: false,
				processData: false,
				success : function(data) {
					console.log(data);
					$("#img_idimg_" + img_id).val(data.data)
					var show_img = document.getElementById('modal_idimg_' + img_id);
					show_img.src=data.data;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					console.log("请求错误");
				}
			});
		}
		function submit_idimg(img_id) {
			swal({
				title : "确认修改借贷认证图片?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : true
			}, function() {
				$('input[name="creditId_updateCreditPic"]').val($("#partyId_modal_detail").val());
				$('input[name="img_id_updateCreditPic"]').val(img_id);
				$('input[name="img_updateCreditPic"]').val($("#img_idimg_" + img_id).val());
				document.getElementById("updateCreditPic").submit();
			});
		};



		function manualRepayment(creditId,username){
			var session_token = $("#session_token").val();
			$("#session_token_success").val(session_token);
			$("#creditId_repayment").val(creditId);
			$("#usernames").val(username);
			getCredit(creditId);
			$("#operateType_repayment").val(5);
			$('#modal_set_manual_repayment').modal("show");
		}

		function getCredit(creditId){
			$.ajax({
				type: "get",
				url: "<%=basePath%>credit/findCreditById.action",
				dataType : "json",
				data : {
					"creditId" : creditId
				},
				success : function(data) {
					var tmp = data;
					var alreadyCreditDays = tmp.alreadyCreditDays;
					var estimatePayment = tmp.estimatePayment;

					$("#totalRepayment").val(estimatePayment);
					$("#manualRepay").val(estimatePayment);
					$("#creditPeriod").val(alreadyCreditDays);
				},
				error : function(XMLHttpRequest, textStatus,
								 errorThrown) {
					console.log("请求错误");
				}
			});
		}

		function onsucceeded(id) {
			var session_token = $("#session_token").val();
			$("#session_token_success").val(session_token);
			$("#creditId").val(id);
			$('#modal_set').modal("show");
		}
		
		function onchangeAddress(id, adress) {
			var session_token = $("#session_token").val();
			$("#session_token_success").val(session_token);
			$("#id_changeAddress").val(id);
			$("#changeAddress").val(adress);
			$('#modal_set_changeAddress').modal("show");
		}

		function onsucceededThird(id) {
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
		function setState(state) {
			document.getElementById("succeeded_para").value = state;
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
