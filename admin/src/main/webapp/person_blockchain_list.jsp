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
	<!-- START CONTENT -->
	<div class="ifr-dody">
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>代理商充值地址维护</h3>
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<c:if test="${from_page != 'agent'}">

			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form id="queryForm" class="form-horizontal" action="<%=basePath%>normal/adminChannelBlockchainAction!personList.action" method="post">
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
																
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="order_no_para" name="address" class="form-control " placeholder="充值地址（完整）" value="${address}" />
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="name_para" name="userName" class="form-control " placeholder="用户名、UID" value="${username}" />
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="roleName" name="roleName" class="form-control ">
													<option value="">所有账号</option>
													<option value="MEMBER" <c:if test="${roleName=='MEMBER'}">selected="true"</c:if>>正式账号</option>
													<option value="GUEST" <c:if test="${roleName=='GUEST'}">selected="true"</c:if>>演示账号</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>
							</form>

						</div>

					</div>
				</div>
			</div>
			</c:if>
			
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">
						<div class="panel-title">查询结果</div>

<!--
						<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
							<a href="<%=basePath%>normal/adminChannelBlockchainAction!toAdd.action" class="btn btn-light" style="margin-bottom: 10px">
								<i class="fa fa-pencil">新增</i>
							</a>
						</c:if> 
-->
						
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>用户UID</td>
										<td>用户</td>
										<td>用户类型</td>
										<td>币种</td>
										<td>链名</td>
										<td>币种</td>
										<td>地址</td>
									<!--	<td>自动</td>-->
										<!-- <td>二维码</td> -->
										<td>创建时间</td>

										<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
											<td width="130px"></td>
										</c:if> 

									</tr>
								</thead>
								
								<tbody style="font-size: 13px;">
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.USERCODE}</td>
											<td>${item.USER_NAME}</td>
											 <td>
										     <c:choose>
					                            <c:when test="${item.ROLENAME=='GUEST'}">
					                              <span class="right label label-warning">演示用户</span>
					                            </c:when>
					                            <c:when test="${item.ROLENAME=='MEMBER'}">
					                              <span class="right label label-success">正式用户</span>
					                            </c:when>
					                              <c:when test="${item.ROLENAME=='AGENT'}">
                                                  <span class="right label label-success">代理商</span>
                                                </c:when>
					                          </c:choose>
					                        </td>

											<td>${item.COIN_SYMBOL}</td>
											<td>${item.CHAIN_NAME}</td>
											<td>${item.COIN_SYMBOL}</td>
											<td>${item.ADDRESS}</td>

											<%--
											<td>${item.AUTO}</td>
											<td>
												<a href="<%=base%>wap/public/showimg!showImg.action?imagePath=${QR_IMAGE}" target="_blank">查看二维码</a>
											</td> 
											--%>
											<td>${item.CREATE_TIME}</td>

											<td>
												<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
															<li><a href="<%=basePath%>normal/adminChannelBlockchainAction!personUpdate.action?username=${item.USER_NAME}&from_page=${item.ROLENAME}&id=${item.ID}">修改</a></li>
															<!--<li><a href="javascript:todelete('${item.ID}')">删除</a></li>-->
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
		<%@ include file="include/footer.jsp"%>
	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<%@ include file="include/js.jsp"%>
	<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
	<script type="text/javascript">
        $(function () {
            var data = <s:property value="result" escape='false' />;
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
		var setInt = null;//定时器			
		clearInterval(setInt);
		function sendCode(){
		var url = "<%=basePath%>normal/adminEmailCodeAction!sendCode.action";
			var data = {
				"code_context" : "deleteChannelBlockchain",
				"isSuper" : true
			};
			goAjaxUrl(url, data, function(tmp) {
				$("#email_code_button").attr("disabled", "disabled");
				var timeout = 60;
				setInt = setInterval(function() {
					if (timeout <= 0) {
						clearInterval(setInt);
						timeout = 60;
						$("#email_code_button").removeAttr("disabled");
						$("#email_code_button").html("获取超级签验证码");
						return;
					}
					timeout--;
					$("#email_code_button").html("获取超级签验证码  " + timeout);
				}, 1000);
			}, function() {
			});
		}
		function goAjaxUrl(targetUrl, data, Func, Fail) {
			$.ajax({
				url : targetUrl,
				data : data,
				type : 'get',
				dataType : "json",
				success : function(res) {
					var tmp = $.parseJSON(res)
					if (tmp.code == 200) {
						Func(tmp);
					} else if (tmp.code == 500) {
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
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					swal({
						title : "请求错误",
						text : "请检查管理员邮箱是否配置",
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
	</script>
	
	<form action="<%=basePath%>normal/adminChannelBlockchainAction!toDelete.action"
		method="post" id="deleteForm" class="form-horizontal">
		
		<!-- <s:hidden name="id" id="id_delete"></s:hidden> -->
		<input type="hidden" name="id" id="id_delete" value="${id}" />
		
		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_succeeded" tabindex="-1"
				role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
					
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">确认删除</h4>
						</div>
						
						<div class="modal-body">
						
							<div class="form-group">
								<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
								<div class="col-sm-4">
									<input id="safeword" type="password" name="safeword"
										class="safeword" placeholder="请输入登录人资金密码">
								</div>
							</div>
							
							<!-- <div class="form-group" style="">							
							<label for="input002" class="col-sm-3 control-label form-label">验证码</label>
							<div class="col-sm-4">
								<input id="email_code" type="text" name="email_code"
								class="login_safeword" placeholder="请输入验证码" >
							</div>
							<div class="col-sm-4">
								<a id="email_code_button" href="javascript:sendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
							</div>
							</div> -->
							
							<div class="form-group">
								<label for="input002" class="col-sm-3 control-label form-label">超级谷歌验证码</label>
								<div class="col-sm-4">
									<input id="super_google_auth_code" name="super_google_auth_code" placeholder="请输入超级谷歌验证码">
								</div>
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

	<script type="text/javascript">
		function todelete(id) {
			$("#id_delete").val(id);
			$('#modal_succeeded').modal("show");
			/* swal({
				title : "是否确认删除充值链地址?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("deleteForm").submit();
			}); */
		}
	</script>
	
</body>

</html>
