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
			<h3>会员等级</h3>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			<%-- <div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>normal/adminMinerAction!list.action" method="post"
								id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<s:textfield id="name_para" name="name_para" cssClass="form-control " placeholder="产品名称"/>
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
			</div> --%>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->


			<div class="row">
			
			
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<%-- <a href="<%=basePath%>normal/adminMinerAction!toAdd.action" class="btn btn-light"
							style="margin-bottom: 10px"><i class="fa fa-pencil"></i>新增</a> --%>
						<div class="panel-body">
								
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>买家等级</td>
										<td>累计充值（运行资金）</td>
										<td>团队人数</td>
										<td>推广有效人数</td>
										<td>利润比例</td>
										<td>卖家优惠折扣</td>
										<td>每小时最小流量</td>
										<td>每小时流量波动范围</td>
										<td>升级礼金</td>
										<td>修改时间</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${page}" var="item"
										varStatus="stat">
										<tr>
											<td>${item.level}</td>
											<td>${item.rechargeAmount}</td>
											<td>${item.teamNum}</td>
											<td>${item.popularizeUserCount}</td>
											<td>${item.profitRationMin}-${item.profitRationMax}%</td>
											<td>${item.sellerDiscount}%</td>
											<td>${item.awardBaseView}</td>
											<td>${item.awardViewMin}-${item.awardViewMax}</td>
											<td>${item.upgradeCash}</td>
											<td>${item.updateTime}</td>
											<td>
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_VIP_OPERATE')}">
<%--												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')}">--%>
													<a
															href="<%=basePath%>brush/vip/toUpdate.action?id=${item.id}"
															class="btn btn-light">修改</a>
<%--												</c:if>--%>
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

		<%@ include file="include/footer.jsp"%>



	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

<div class="form-group">
		<form
			action=""
			method="post" id="mainform">
			<input type="hidden" name="pageNo" id="pageNo" value="${param.pageNo}">
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
												<div class="form-group" >
													<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>
													<div class="col-sm-4">
														<input id="google_auth_code"  name="google_auth_code"
															 placeholder="请输入谷歌验证码" >
													</div>
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

	
</body>
</html>