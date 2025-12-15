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

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>项目列表</h3>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>invest/project/list.action" method="post"
								id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${pageNo}">
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="name" name="name" class="form-control"
													   placeholder="项目名称" value = "${name}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="PName" name="PName" class="form-control"
													   placeholder="项目分类" value = "${PName}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="status" name="status"
														class="form-control ">
													<option value="">商品状态</option>
													<option value="0" <c:if test="${status == '0'}">selected="true"</c:if>>启用</option>
													<option value="1" <c:if test="${status == '1'}">selected="true"</c:if>>禁用</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="ending" name="ending"
														class="form-control ">
													<option value="">投资状态</option>
													<option value="0" <c:if test="${status == '0'}">selected="true"</c:if>>投资中</option>
													<option value="1" <c:if test="${status == '1'}">selected="true"</c:if>>已结束</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-12" style="margin-top:10px;"></div>
								<div class="col-md-12 col-lg-3">
									<input id="startTime" name="startTime" class="form-control "
										   placeholder="开始日期" value="${startTime}" />
								</div>
								<div class="col-md-12 col-lg-3">

									<input id="endTime" name="endTime" class="form-control "
										   placeholder="结束日期" value="${endTime}" />
								</div>

								<div class="col-md-12 col-lg-2">
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
				
						<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
									 || security.isResourceAccessible('OP_PROJECT_OPERATE')}">
						
							<a href="<%=basePath%>invest/project/toAdd.action?pageNo=${pageNo}" class="btn btn-light" style="margin-bottom: 10px">
								<i class="fa fa-pencil"></i>新增项目</a>

							<a href="javascript:award()"
							   class="btn btn-light" style="margin-bottom: 12px"><i
									class="fa fa-pencil"></i>更新进度</a>
						</c:if>
						
						<div class="panel-body">
								
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>项目分类</td>
										<td>项目名称</td>
										<td>担保机构</td>
										<td>项目规模</td>
										<td>实际售出</td>
										<td>投资进度</td>
										<td>起投金额</td>
										<td>上限金额</td>
										<td>投资收益率(%)</td>
										<td>锁仓期限</td>
<%--										<td>已售出百分比</td>--%>
										<td>投资状态</td>
										<td>首页推荐</td>
										<td>状态</td>
										<td>创建时间</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<input type="hidden" name="iconImg" id="iconImg" value = "${item.iconImg}"/>
										<tr>
<%--											<td>--%>
<%--												<img width="90px" height="90px" id="show_img" src="<%=basePath%>normal/showImg.action?imagePath=${item.iconImg}"/> 　　--%>
<%--											</td>--%>
									    <td>${item.PName}</td>
										<td>${item.name}</td>
										<td>${item.guarantyAgency}</td>
										<td>${item.investSize}</td>
										<td>${item.investProgress}%</td>
										<td>${item.investProgressMan}%</td>
										<td>${item.investMin}</td>
										<td>${item.investMax}</td>
										<td>${item.bonusRate}</td>
										<td>
											<c:choose>
												<c:when test="${item.type == '1' || item.type == '2'}">
														${item.bonus}/小时
												</c:when>
												<c:otherwise>
														${item.bonus}/天
												</c:otherwise>
											</c:choose>
										</td>
<%--										<td>${item.investProgress}</td>--%>
											<td>
												<c:choose>
													<c:when test="${item.ending == '0'}">
														<span class="right label label-success">进行中</span>
													</c:when>
													<c:otherwise>
														<span class="right label label-danger">禁用</span>
													</c:otherwise>
												</c:choose>
											</td>
											<td>
												<c:choose>
													<c:when test="${item.recTime == '0'}">
														<span class="right label label-danger">否</span>

													</c:when>
													<c:otherwise>
														<span class="right label label-success ">是</span>
													</c:otherwise>
												</c:choose>
											</td>
											<td>
												<c:choose>
													<c:when test="${item.status == '0'}">
														<span class="right label label-success">启用</span>
													</c:when>
													<c:otherwise>
														<span class="right label label-danger">禁用</span>
													</c:otherwise>
												</c:choose>
											</td>
											
											<td>${item.createTime}</td>
											<td>
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_PROJECT_OPERATE')}">
											
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
														
															<li><a href="<%=basePath%>invest/project/toUpdate.action?lang=cn&projectId=${item.id}">修改</a></li>
															<li><a href="javascript:toDelete('${item.id}')">删除</a></li>

															<c:choose>
																<c:when test="${item.recTime == '0'}">
															<li>	<a href="<%=basePath%>invest/project/updateStatus.action?&id=${item.id}&status=1">首页推荐</a></li>
																</c:when>
																<c:otherwise>
															<li><a href="<%=basePath%>invest/project/updateStatus.action?&id=${item.id}&status=0">关闭首页推荐</a></li>
																</c:otherwise>
															</c:choose>
														</ul>
													</div>
												</c:if>
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
		
		<!-- 模态框 -->
		<div class="form-group">
			<form action=""
					method="post" id="mainform">
				<input type="hidden" name="pageNo" id="pageNo"
					   value="${pageNo}">
				<input type="hidden" name="id" id="id"/>
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
<%--									<div class="form-group" >--%>
<%--										<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>--%>
<%--										<div class="col-sm-4">--%>
<%--											<input id="google_auth_code"  name="google_auth_code"--%>
<%--												   placeholder="请输入谷歌验证码" >--%>
<%--										</div>--%>
<%--									</div>--%>
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

			<form action="<%=basePath%>invest/project/renew.action"
				  method="post" id="succeededForm">
				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_set"
						 tabindex="-1" role="dialog"
						 aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">

								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabels">更新项目进度</h4>

								</div>

								<div class="modal-body">
									<select id="typeValue" name="typeValue" class="form-control" oninput="change()">
										<option value="1">手动输入</option>
										<option value="2">更新已售增量</option>
									</select>
									<div id="beizhu" style="float: left;width: 100%;margin-top: 5px;color: red">
										注意：更新所有已售项目，已售进度加上已售增量系数；</div>
								</div>

								<div id="setting">
									<div class="modal-header">
										<h4 class="modal-title" id="myModalLabel2">更新比例</h4>
									</div>

									<div class="modal-body">
										<div class="" style="width: 97%;float: left;">
											<input id="proportion" name="proportion"
												   class="form-control"  value="${proportion}" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')">
										</div>
										<span style="float: left;margin-left: 5px;margin-top: 5px;">%</span>
										<div style="float: left;width: 100%;margin-top: 5px;color: red">注意：更新进度将在原有进度加上更新比例</div>
									</div>
								</div>

								<div class="modal-header">
									<h4 class="modal-title" id="myModalLabel3">请输入资金密码</h4>
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

		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<%@ include file="include/js.jsp"%><script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
	<script>
	</script>


	<script type="text/javascript">


		function toDelete(id,pageNo){
			$('#id').val(id);
			$('#pageNo').val(pageNo);
			$('#myModalLabel').html("删除");
			$('#mainform').attr("action","<%=basePath%>invest/project/delete.action");

			$('#modal_succeeded').modal("show");

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
		function change(){
				let typeValue = document.getElementById("typeValue").value;
				if(typeValue == 2){
					document.getElementById("setting").style.display="none";
					document.getElementById("beizhu").style.display="";
				} else {
					document.getElementById("beizhu").style.display="none";
					document.getElementById("setting").style.display="";
				}
				// var y =  document.getElementById("suoqiang-qixian-time");
				// y.innerHTML = dstime;
		}


		function award(){
			var session_token = $("#session_token").val();
			$("#session_token_success").val(session_token);
			document.getElementById("beizhu").style.display="none";
			$("#typeValue").val(1);
			$('#modal_set').modal("show");
		}
	</script>
</body>
</html>