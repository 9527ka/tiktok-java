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

<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTENT -->
<div class="ifr-con">

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTAINER -->
	<div class="container-default">
		<ul class="nav nav-tabs">
			<li><a href="<%=basePath%>mall/banner/list.action?type=pc"><font size="4">PC首页轮播</font></a></li>
			<li><a href="<%=basePath%>mall/banner/list.action?type=h5"><font size="4">H5首页轮播</font></a></li>
		</ul>

<%--		<c:choose>--%>
<%--				<c:when test="${type=='pc'}">--%>
<%--					<h3>PC首页轮播</h3>--%>
<%--				</c:when>--%>
<%--				<c:otherwise>--%>
<%--					<h3>H5首页轮播</h3>--%>
<%--				</c:otherwise>--%>
<%--			</c:choose>--%>
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<%@ include file="include/alert.jsp"%>
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-default">

					<div class="panel-title">查询条件</div>
					<div class="panel-body">

						<form class="form-horizontal"
							  action="<%=basePath%>mall/banner/list.action?type=${type}"
							  method="post" id="queryForm">

							<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">

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

<%--		<form action="<%=basePath%>mall/goodAttrCategory/list.action" method="post"--%>
<%--			  id="queryForm">--%>
<%--							<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}"/>--%>
<%--			<input type="hidden" id="categoryIds" name="categoryId" value="${categoryId}"/>--%>
<%--			<input type="hidden" id="categoryNames" name="categoryName" value="${categoryName}"/>--%>
<%--		</form>--%>


		<div class="row">


			<div class="col-md-12">
				<!-- Start Panel -->
				<div class="panel panel-default">

<%--					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
<%--									 || security.isResourceAccessible('OP_NEWS_OPERATE')}">--%>

						<a href="<%=basePath%>mall/banner/toAdd.action?type=${type}&pageNo=${pageNo}" class="btn btn-light" style="margin-bottom: 10px">
							<i class="fa fa-pencil"></i>新增轮播</a>

<%--					</c:if>--%>

					<div class="panel-body">

						<table class="table table-bordered table-striped">
							<thead>
							<tr>
								<td>排序</td>
								<td>封面图</td>
								<td>跳转地址</td>
								<td>备注</td>
								<c:if test="${type == 'pc'}">
									<td>分类</td>
								</c:if>
								<td>创建日期</td>
								<td width="130px"></td>
							</tr>
							</thead>
							<tbody>
							<c:forEach items="${page.getElements()}" var="item"
									   varStatus="stat">
								<tr>
									<td>${item.sort}</td>
									<td>
										<img width="60px" height="60px" id="show_img" src="${item.imgUrl}"/> 　　
									</td>
									<td>${item.link}</td>
									<td>${item.remarks}</td>


									<c:if test="${type == 'pc'}">
										<td>
											<c:choose>
												<c:when test="${item.imgType == '0'}">
													<span class="right label label-success">小图（242*152）</span>
												</c:when>
												<c:otherwise>
													<span class="right label label-default">大图（700*310）</span>
												</c:otherwise>
											</c:choose>
										</td>
									</c:if>
									<td>${item.createTime}</td>
									<td>
<%--										<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
<%--															|| security.isResourceAccessible('OP_GOODATTRCATEGORY_OPERATE')}">--%>

											<div class="btn-group">
												<button type="button" class="btn btn-light">操作</button>
												<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
													<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
												</button>
												<ul class="dropdown-menu" role="menu">
													<li><a href="<%=basePath%>mall/banner/toUpdate.action?id=${item.id}&type=${type}&pageNo=${pageNo}">修改</a></li>
													<li><a href="javascript:toDelete('${item.id}','${type}','${pageNo}')">删除</a></li>
												</ul>
											</div>
<%--										</c:if>--%>
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
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="bannerId" id="bannerId" />
		<input type="hidden" name="type" id="type"/>
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
<%@ include file="include/js.jsp"%>
<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>

<script type="text/javascript">
	function toDelete(id,type,pageNo){
		$('#bannerId').val(id);
		$('#type').val(type);
		$('#pageNo').val(pageNo);
		$('#myModalLabel').html("删除");
		$('#mainform').attr("action","<%=basePath%>mall/banner/delete.action");

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

	$(function(){
		$('.nav-tabs a').filter(function() {
			var b = document.URL;
			var a = "<%=basePath%>mall/banner/list.action?type=${type}";
			return this.href == "<%=basePath%>mall/banner/list.action?type=${type}";  //获取当前页面的地址
		}).closest('li').addClass('active');  //给当前最靠近的li（其实是现在进入的li）添加‘active’样式

	})


</script>


</body>
</html>