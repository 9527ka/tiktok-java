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
<body class="ifr-dody">
<%@ include file="include/loading.jsp"%>
<script src="include/top.jsp"></script>

<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTENT -->
<div class="ifr-con">

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTAINER -->
	<div class="container-default">
		<h3>虚拟买家对话</h3>
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<%@ include file="include/alert.jsp"%>

		<form action="<%=basePath%>chat/chatsList.action" method="post"
			  id="queryForms">
			<%--				<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}"/>--%>
			<input type="hidden" id="partyId" name="partyId" value="${partyId}"/>
		</form>

		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-default">

					<div class="panel-title">查询条件</div>
					<div class="panel-body">

						<form class="form-horizontal" action="<%=basePath%>chat/chatsList.action" method="post"
							  id="queryForm">
							<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="userCode" name="userCode" class="form-control"
												   placeholder="会员ID" value = "${userCode}"/>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-3">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="email" name="email" class="form-control"
												   placeholder="用户邮箱" value = "${email}"/>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-3">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="phone" name="phone" class="form-control"
												   placeholder="手机号" value = "${phone}"/>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-12" style="margin-top:10px;"></div>
							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="sellerCode" name="sellerCode" class="form-control"
												   placeholder="卖家ID" value = "${sellerCode}"/>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-3">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="sellerName" name="sellerName" class="form-control"
												   placeholder="店铺名称" value = "${sellerName}"/>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<select id="sellerRoleName" name="sellerRoleName" class="form-control">
												<option value="">店铺类型</option>
												<option value="MEMBER" <c:if test="${sellerRoleName == 'MEMBER'}">selected="true"</c:if> >真实店铺</option>
												<option value="GUEST" <c:if test="${sellerRoleName == 'GUEST'}">selected="true"</c:if> >演示店铺</option>
											</select>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-2">
								<button type="submit" class="btn btn-light btn-block">查询</button>
							</div>
							<%--								<div class="col-md-12 col-lg-1">--%>
							<%--									<button type="submit" class="btn btn-light btn-block">刷新页面</button>--%>
							<%--								</div>--%>

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
<%--					<a href="<%=basePath%>mall/combo/toAdd.action?pageNo=${pageNo}" class="btn btn-light" style="margin-bottom: 10px">--%>
<%--						<i class="fa fa-pencil"></i>新增套餐</a>--%>

					<button type="button" onclick="submitFrom();"
							class="btn btn-default " style="margin-bottom: 10px; margin-bottom: 10px"><i class="fa fa-pencil"></i>刷新</button>
					<div class="panel-body">

						<table class="table table-bordered table-striped">
							<thead>
							<tr>
								<td>用户ID</td>
								<td>用户邮箱</td>
								<td>手机号</td>
								<td>账号类型</td>
								<td>卖家ID</td>
								<td>店铺名称</td>
								<td>店铺类型</td>
								<td>会话更新时间</td>

								<td>买家备注</td>
								<td width="130px"></td>
							</tr>
							</thead>
							<tbody>
							<c:forEach items="${page.getElements()}" var="item"
									   varStatus="stat">
								<tr>
									<td>${item.usercode}
										<c:if test="${item.UNREAD > 0 }">
											<span class="right label label-danger" style="margin-left: 30px">${item.unread}</span>
										</c:if>
									</td>
									<td>${item.email}</td>
									<td>${item.phone}</td>
									<td>
										<c:if test="${item.roleName == 'GUEST'}">
											<span class="right label label-warning">演示账号</span>
										</c:if>
										<c:if test="${item.roleName == 'MEMBER'}">
											<span class="right label label-success">正式账号</span>
										</c:if>
									</td>
									<td>${item.sellerCode}</td>
									<td>${item.name}</td>
									<td>
										<c:choose>
											<c:when test="${item.sellerRoleName=='GUEST'}">
												<span class="right label label-warning">演示店铺</span>
											</c:when>
											<c:when test="${item.sellerRoleName=='MEMBER'}">
												<span class="right label label-success">真实店铺</span>
											</c:when>
										</c:choose>
									</td>
									<td>${item.updatetime}</td>
									<td>${item.remarks}</td>
									<td>
										<div class="btn-group">
											<button type="button" class="btn btn-light">操作</button>
											<button type="button" class="btn btn-light dropdown-toggle"
													data-toggle="dropdown" aria-expanded="false">
												<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
											</button>
											<ul class="dropdown-menu" role="menu">
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
																|| security.isResourceAccessible('OP_CHAT_OPERATE')}">
													<li><a href="javascript:reject(`${item.partyId}`,`${item.remarks}`)">备注</a></li>
												</c:if>
												<li><a href="javascript:openwin(`${item.chat_id}`,`${item.name}`)">聊天记录</a></li>

													<%--															<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_CUSTOMER')}">--%>
													<%--																<li class="link">--%>
													<%--&lt;%&ndash;																<a href="javascript:void(0);" class="notifications" onclick="parent.chat('${item.usercode}');">&ndash;%&gt;--%>

													<%--&lt;%&ndash;																	<i class="fa fa fa-comments-o" style="font-size: 15px;"></i>&ndash;%&gt;--%>
													<%--&lt;%&ndash;																	<span class="badge label-danger" style="margin-left: 5px;" id="online_chat_unread"></span>&ndash;%&gt;--%>
													<%--&lt;%&ndash;																</a>&ndash;%&gt;--%>
													<%--																	<a href="javascript:void(0);" onclick="parent.chat('${item.usercode}')">联系商家</a>--%>
													<%--																</li>--%>
													<%--															</c:if>--%>
													<%--															<li>--%>
													<%--																<a href="http://127.0.0.1:41879/chat/#/h5/backh5/yellow?chatid=${item.chatid}" target="_blank">--%>
													<%--																	聊天记录--%>
													<%--																</a>--%>
													<%--																<a href="javascrip:void(0)" οnclick="window.open('http://127.0.0.1:41879/chat/#/h5/backh5/yellow?chatid','','height=529, width=700, top=265,left=645, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no')" target="_self" >123</a>--%>
													<%--															</li>--%>

											</ul>
										</div>

									</td>
								</tr>
							</c:forEach>

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

<div class="form-group">
	<form
			action=""
			method="post" id="mainform">
		<input type="hidden" name="pageNo" id="pageNo" value="${param.pageNo}">
		<input type="hidden" name="baseId" id="baseId" />
		<div class="col-sm-1 form-horizontal">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_succeeded" tabindex="-1"
				 role="dialog" aria-labelledby="myModalLabel"
				 aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content" >
						<div class="modal-header">
							<button type="button" class="close"
									data-dismiss="modal" aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">确认调整</h4>
						</div>
						<div class="modal-body">
							<div class="form-group" >
								<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
								<div class="col-sm-4">
									<input id="login_safeword" type="password" name="login_safeword"
										   class="login_safeword" placeholder="请输入登录人资金密码" >
								</div>
							</div>
							<!-- <div class="form-group" style="">

                                <label for="input002" class="col-sm-3 control-label form-label">验证码</label>
                                <div class="col-sm-4">
                                    <input id="email_code" type="text" name="email_code"
                                    class="login_safeword" placeholder="请输入验证码" >
                                </div>
                                <div class="col-sm-4">
                                    <a id="update_email_code_button" href="javascript:updateSendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
                                </div>
                            </div> -->
							<%--												<div class="form-group" >--%>
							<%--													<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>--%>
							<%--													<div class="col-sm-4">--%>
							<%--														<input id="google_auth_code"  name="google_auth_code"--%>
							<%--															 placeholder="请输入谷歌验证码" >--%>
							<%--													</div>--%>
							<%--												</div>--%>
						</div>
						<div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn "
									data-dismiss="modal">关闭</button>
							<button id="sub" type="submit"
									class="btn btn-default">确认</button>
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

	<form action="<%=basePath%>chat/upremarks.action"
		  method="post" id="succeededForm">

		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="partyId" id="partyIds" value="${partyIds}">

		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set2" tabindex="-1" role="dialog"
				 aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
							<h4 class="modal-title">备注</h4>
						</div>

						<textarea name="remarks" id="remarks" class="form-control  input-lg" rows="6" cols="6" placeholder="备注信息" >${remarks}</textarea>


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

<!-- Modal -->
<%@ include file="include/js.jsp"%>
<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
<script>


	$(function () {
		var data = <s:property value="result" escape='false' />;
		console.log(data);
		$("#treeview4").treeview({
			color: "#428bca",
			enableLinks:true,
			nodeIcon: "glyphicon glyphicon-user",
			data: data,
			levels: 4,
		});
	});


</script>


<script type="text/javascript">


	// setInterval(function() {
	// 	getChatList();
	// }, 2000);


	// 聊天记录列表
	function getChatList() {
		let param;
		var pageNo = $("#pageNo").val();
		var userCode_para = $("#userCode_para").val();
		var email_para = $("#email_para").val();
		var phone_para = $("#phone_para").val();
		var roleName_para = $("#roleName_para").val();
		var sellerName_para = $("#sellerName_para").val();
		var targetUserName_para = $("#targetUserName_para").val();
		var formData = new FormData();
		formData.append("pageNo", pageNo);
		formData.append("userCode_para", userCode_para);
		formData.append("email_para", email_para);
		formData.append("phone_para", phone_para);
		formData.append("roleName_para", roleName_para);
		formData.append("sellerName_para", sellerName_para);
		formData.append("targetUserName_para", targetUserName_para);


		$.ajax({
			url: "<%=basePath%>chat/chatsPage.action",
			type: 'POST',
			// contentType: "application/json",
			traditional: true,
			data: {
				'pageNo': pageNo,
				'userCode_para': userCode_para,
				'email_para': email_para,
				'phone_para': phone_para,
				'roleName_para': roleName_para,
				'sellerName_para': sellerName_para,
				'targetUserName_para': targetUserName_para
			},
			success: function (data) {
				if (data.code === 200) {
					console.log("---------------------")
					return;
				}
				// if (data.code == 500){
				// 	swal({
				// 		title: data.error,
				// 		timer: 2500,
				// 		showConfirmButton: false
				// 	})
				//
				// }
			}
			// error : function(XMLHttpRequest, textStatus,
			// 				 errorThrown) {
			// 	console.log("请求错误");
			// }
		});
	}

	$(function() {
		$('#startTime').datetimepicker({
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
		$('#endTime').datetimepicker({
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

	function reject(partyId,remarks) {
		debugger
		$("#partyIds").val(partyId);
		$("#remarks").val(remarks);
		$('#modal_set2').modal("show");
	};

	function toDelete(baseId,pageNo){
		$('#baseId').val(baseId);
		$('#pageNo').val(pageNo);
		$('#myModalLabel').html("删除");
		$('#mainform').attr("action","<%=basePath%>mall/combo/delete.action");

		$('#modal_succeeded').modal("show");
	}

	function getGoodsNum(id){
		$("#partyId").val(id);
		$("#pageNo").val(1);
		$("#queryForms").submit();
	}

	function submitFrom(id){
		// $("#partyId").val(id);
		// $("#pageNo").val(1);
		$("#queryForm").submit();
	}

	function openwin(chat_id,shopName){
		window.open('<%=dmUrl%>/chat/#/h5/backh5/yellow?chatid=' + chat_id +'&shopName='+ shopName + '&height=600px',"",'height=779px, width=500, top=50%, left=50%,margin-left:-380,margin-top:-325, toolbar=no, menubar=no, scrollbars=no, resizable=false, location=no, status=no')
		if (window.focus) {

			openNewLink.focus();

		}
	}
	function get_cookie(Name) {
		var search = Name + "="
		var returnvalue = "";
		if (document.cookie.length > 0) {
			offset = document.cookie.indexOf(search)
			if (offset != -1) {
				offset += search.length
				end = document.cookie.indexOf(";", offset);
				if (end == -1)
					end = document.cookie.length;
				returnvalue=unescape(document.cookie.substring(offset, end))
			}
		}
		return returnvalue;
	}
	function loadpopup(){
		if (get_cookie('popped')==''){
			openwin(chat_id);
			document.cookie="popped=yes"
		}
	}

	function test(chatid) {
		var openNewLink = window.open('http://127.0.0.1:41879/chat/#/h5/backh5/yellow?chatid=' + chatid , '路丁博客', 'height=650, width=760, top=50%, left=50%,margin-left:-380,margin-top:-325, toolbar=no, menubar=no, scrollbars=no, resizable=false, location=no, status=no'); //参数： url, 名称, 窗体样式

		if (window.focus) {

			openNewLink.focus();

		}

		return false;

	}
</script>


</body>
</html>