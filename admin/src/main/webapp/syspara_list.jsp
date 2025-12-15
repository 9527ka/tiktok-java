<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>

	<style>
		.truncate-text {
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
			max-width: 150px; /* 调整最大宽度根据需要 */
			cursor: pointer; /* 鼠标悬停时显示手形光标 */
		}

		.truncate-text:hover {
			overflow: visible; /* 鼠标悬停时显示完整文本 */
			max-width: none;   /* 鼠标悬停时取消最大宽度限制 */
			white-space: normal; /* 鼠标悬停时允许文本换行 */
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

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>系统参数（ROOT）</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>normal/adminSysparaAction!list.action"
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
									
								<div class="col-md-12 col-lg-6">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<!-- <s:textfield id="notes_para" name="notes_para"
													cssClass="form-control " placeholder="名称" /> -->
												<input id="notes_para" name="notes_para"
													class="form-control " placeholder="名称" value="${notes_para}" />
											</div>
										</div>
									</fieldset>
								</div>
								
								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>
								
							</form>
							
							<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
							
								<div class="col-md-12 col-lg-12" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="panel-title"
											style="padding-left: 5px; margin-top: -10px;">操作</div>
										<div class="mailbox-menu" style="border-bottom: hidden;">
											<ul class="menu">
											</ul>
										</div>
									</div>
								</div>
								
								<div class="col-md-12 col-lg-12">
								
									<form class="form-horizontal"
										action="<%=basePath%>normal/adminGoogleAuthAction!toUpdateSuperGoogleAuth.action"
										method="post" id="add_profit_form">
										<!-- <s:hidden name="status_para"></s:hidden> -->
										<input type="hidden" name="status_para">
										<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
										<div class="col-md-12 col-lg-3">
											<button type="submit" class="btn btn-light btn-block"
												data-toggle="modal" data-target="#myModal3">超级谷歌验证器</button>
										</div>
									</form>
									
									<form class="form-horizontal"
										action="<%=basePath%>normal/adminGoogleAuthAction!toUpdateAdminGoogleAuth.action"
										method="post" id="add_profit_form">
										<!-- <s:hidden name="status_para"></s:hidden> -->
										<input type="hidden" name="status_para">
										<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
										<div class="col-md-12 col-lg-3">
											<button type="submit" class="btn btn-light btn-block"
												data-toggle="modal" data-target="#myModal3">admin谷歌验证器</button>
										</div>
									</form>
									
									<form class="form-horizontal"
										action="<%=basePath%>normal/adminSysparaAction!backup.action"
										method="post" id="add_profit_form">
										<!-- <s:hidden name="status_para"></s:hidden> -->
										<input type="hidden" name="status_para">
										<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
										<div class="col-md-12 col-lg-3">
											<button type="submit" class="btn btn-light btn-block"
												data-toggle="modal" data-target="#myModal3">备份数据库</button>
										</div>
									</form>
									<form class="form-horizontal"
										action="<%=basePath%>normal/adminSysparaAction!cacheUpdate.action"
										method="post" id="add_profit_form">
										<!-- <s:hidden name="status_para"></s:hidden> -->
										<input type="hidden" name="status_para">
										<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
										<div class="col-md-12 col-lg-3">
											<button type="submit" class="btn btn-light btn-block"
												data-toggle="modal" data-target="#myModal3">重置缓存</button>
										</div>
									</form>

									<%-- <form class="form-horizontal"
										action="<%=basePath%>normal/adminSysparaAction!sycnThirdSupportCoin.action"
										method="post" id="add_profit_form">
										<!-- <s:hidden name="status_para"></s:hidden> -->
										<input type="hidden" name="status_para">
										<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
										<div class="col-md-12 col-lg-3">
											<button type="submit" class="btn btn-light btn-block"
												data-toggle="modal" data-target="#myModal3">同步三方支持币种</button>
										</div>
									</form> --%>
									
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
										<td>名称</td>
										<td>值</td>
										<td width="150px"></td>
									</tr>
								</thead>
								
								<tbody>								
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<tr>
											<td>${item.notes}</td>
<%--											${item.value}</td>--%>
											<td>
												<div class="truncate-text" title="${item.value}">${item.value}</div>
											</td>


											<td>
											
												<c:if test="${security.isRolesAccessible('ROLE_ROOT')}">
												
													<c:if test="${item.modify == 0}">
												
														<div class="btn-group">
															<button type="button" class="btn btn-light">操作</button>
															<button type="button" class="btn btn-light dropdown-toggle"
																data-toggle="dropdown" aria-expanded="false">
																<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
															</button>
															<ul class="dropdown-menu" role="menu">
																<li><a
																	href="javascript:update_value('${item.code}','${item.notes}','${item.value}')">修改</a></li>
																<li>
															</ul>
														</div>
													
													</c:if>
													
												</c:if>
													
												<div class="form-group">
												
													<form
														action="<%=basePath%>normal/adminSysparaAction!update.action"
														method="post" id="succeededForm">
														<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
														<!-- <s:hidden name="code" id="code"></s:hidden> -->
														<input type="hidden" name="code" id="code" value="${code}">
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
																			<h4 class="modal-title" id="myModalLabel">
																				<div id="titlediv" ></div>
																			</h4>
																		</div>
																		
																		<div class="modal-body">
																			<div class="">
																				<input id="change_value" name="value" type="text"
																					class="form-control" value="${value}">
																			</div>
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
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->

		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<%@ include file="include/js.jsp"%>

	<script type="text/javascript">
		function update_value(code, snotes, svalue) {
			document.getElementById("change_value").value = svalue;
			document.getElementById("titlediv").innerText = snotes;
			$("#code").val(code);
			$('#modal_set').modal("show");
		}
	</script>

</body>

</html>
