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
	<style>
		td {
			word-wrap: break-word; /* 让内容自动换行 */
			max-width: 200px; /* 设置最大宽度，以防止内容过长 */
		}
		.copy-link {
			color: #007bff;
			text-decoration: underline;
			cursor: pointer; /* 添加此样式，显示小手光标 */
			word-wrap: break-word;
		}
		.copied {
			color: green;
		}
	</style>
</head>
<body>
<%@ include file="include/loading.jsp"%>
<script src="include/top.jsp"></script>
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

<!-- //////////////////////////////////////////////////////////////////////////// -->
<!-- START CONTENT -->
<div class="ifr-dody">

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTAINER -->
	<div class="ifr-con">
		<h3>店铺管理</h3>
		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<%@ include file="include/alert.jsp"%>
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-default">

					<div class="panel-title">查询条件</div>
					<div class="panel-body">

						<form class="form-horizontal" action="<%=basePath%>mall/seller/list.action" method="post"
							  id="queryForm">
							<input type="hidden" name="pageNo" id="pageNo"
								   value="${pageNo}">
							<div class="col-md-12 col-lg-4">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="name_para" name="name_para" class="form-control"
												   placeholder="用户名、UID" value = "${name_para}"/>
										</div>
									</div>
								</fieldset>
							</div>
							<%--								<div class="col-md-12 col-lg-3">--%>
							<%--									<fieldset>--%>
							<%--										<div class="control-group">--%>
							<%--											<div class="controls">--%>
							<%--											<input id="sellerId" name="sellerId" class="form-control"--%>
							<%--											placeholder="店铺ID" value = "${sellerId}"/>--%>
							<%--											</div>--%>
							<%--										</div>--%>
							<%--									</fieldset>--%>
							<%--								</div>--%>
							<div class="col-md-12 col-lg-4">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="sellerName" name="sellerName" class="form-control"
												   placeholder="店铺名称" value = "${sellerName}"/>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-3">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<select id="roleName" name="roleName" class="form-control " >
												<option value="">店铺类型</option>
												<option value="MEMBER" <c:if test="${roleName == 'MEMBER'}">selected="true"</c:if> >真实店铺</option>
												<option value="GUEST" <c:if test="${roleName == 'GUEST'}">selected="true"</c:if> >演示店铺</option>
											</select>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2" style="margin-top: 15px;">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="username_parent" name="username_parent" class="form-control"
												   placeholder="推荐人" value = "${username_parent}"/>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-2" style="margin-top: 15px;">
								<input id="startTime" name="startTime" class="form-control "
									   placeholder="开始日期" value="${startTime}" />
							</div>
							<div class="col-md-12 col-lg-2" style="margin-top: 15px;">

								<input id="endTime" name="endTime" class="form-control "
									   placeholder="结束日期" value="${endTime}" />
							</div>

							<div class="col-md-12 col-lg-2" style="margin-top: 15px;">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<select id="level" name="level" class="form-control " >
												<option value="">店铺等级</option>
												<option value="0" <c:if test="${level == '0'}">selected="true"</c:if> >0</option>
												<c:forEach items="${levels}" var="levelOption">
													<option value="${levelOption}" <c:if test="${level == levelOption}">selected="true"</c:if> >${levelOption}</option>
												</c:forEach>
											</select>
										</div>
									</div>
								</fieldset>
							</div>


							<div class="col-md-12 col-lg-3" style="margin-top: 15px;">
								<button type="submit" class="btn btn-light btn-block">查询</button>
							</div>
							<div class="col-md-12 col-lg-12" style="margin-top:10px;"></div>


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

					<%--						<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')--%>
					<%--									 || security.isResourceAccessible('OP_GOODS_OPERATE')}">--%>
					<%--						--%>
					<%--							<a href="<%=basePath%>mall/goods/toAdd.action?pageNo=${pageNo}" class="btn btn-light" style="margin-bottom: 10px">--%>
					<%--								<i class="fa fa-pencil"></i>新增商品</a>--%>
					<%--								--%>
					<%--						</c:if>--%>
					<%--						--%>
					<div class="panel-body">

						<table class="table table-bordered table-striped" border="1">
							<thead>
							<tr>
								<td>店铺ID</td>
								<td>店铺账号</td>
								<c:if test="${platformName == 'TikTokMall'}">
									<td>店铺logo</td>
								</c:if>
								<td>店铺名称</td>
								<td>店铺等级</td>
								<td>直属下级（分店数）</td>
								<td>有效团队人数</td>
								<td>店铺类型</td>
								<td>商品数量</td>
								<td>店铺关注人数</td>
								<td>钱包余额</td>
								<td>冻结余额</td>
								<td>推荐人</td>
								<td>好评率</td>
								<td width="70px">首页推荐</td>
								<td>是否冻结</td>
								<td>是否拉黑</td>
								<td>店铺自动流量</td>
								<td>访客/待到账</td>
								<td>注册日期</td>
								<td>用户备注</td>
								<td width="130px"></td>
							</tr>
							</thead>
							<tbody>
							<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
								<%--										<input type="hidden" name="iconImg" id="iconImg" value = "${item.iconImg}"/>--%>
								<tr>

									<td>${item.userCode}</td>
									<td>${item.userName}</td>
									<c:if test="${platformName == 'TikTokMall'}">
										<td>
											<img width="60px" height="60px" id="show_img" src="${item.avatar}"/> 　　
										</td>
									</c:if>
									<td>${item.sellerName}</td>
									<td>${item.level}</td>
									<td>${item.childNum}</td>
									<td>${item.teamNum}</td>
									<td>
										<c:choose>
											<c:when test="${item.rolename=='GUEST'}">
												<span class="right label label-warning">演示店铺</span>
											</c:when>
											<c:when test="${item.rolename=='MEMBER'}">
												<span class="right label label-success">真实店铺</span>
											</c:when>
										</c:choose>
									</td>
									<td><a href="#" onClick="getGoodsNumBySellerIds('${item.sellerId}')">点击查看</a></td>
									<td>${item.reals + item.fake}</td>

									<td>
										<c:if test="${item.frozenState == 1}">
											<fmt:formatNumber value="${item.moneyAfterFrozen}" pattern="#0.00" />
										</c:if>
										<c:if test="${item.frozenState == 0}">
											<fmt:formatNumber value="${item.money}" pattern="#0.00" />
										</c:if>
									</td>
									<td>
										<c:if test="${item.frozenState == 1}">
										 <fmt:formatNumber value="${item.money}" pattern="#0.00" />
										</c:if>
										<c:if test="${item.frozenState == 0}">
										 <fmt:formatNumber value="${item.moneyAfterFrozen}" pattern="#0.00" />
										</c:if>
									</td>
									<td>${item.username_parent}</td>
									<td>${item.highOpinion}</td>
									<td>
										<c:choose>
											<c:when test="${item.recTime == '0'}">
												<span style="color: red">不推荐</span>
											</c:when>
											<c:otherwise>
									        <a href="#" onClick="getSellerRec('${item.recTime}','${item.recommendDuration}')">店铺推荐</a>
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${item.frozenState == '1'}">
												<span class="right label label-danger">已冻结</span>
											</c:when>
											<c:otherwise>
												<span class="right label label-success">未冻结</span>
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${item.black == '1'}">
												<span class="right label label-danger">已经拉黑</span>
											</c:when>
											<c:otherwise>
												<span class="right label label-success">未拉黑</span>
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${item.flow == '1'}">
												<span class="right label label-danger">关闭</span>
											</c:when>
											<c:otherwise>
												<span class="right label label-success">开启</span>
											</c:otherwise>
										</c:choose>
									</td>
									<td><a href="#" onClick="getViewNumsBySellerIds('${item.sellerId}')">点击查看</a></td>
									<td>${item.createTime}</td>
									<td>${item.remarks}</td>
									<td>
										<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_MALL_SELLER_OPERATE')}">

											<div class="btn-group">
												<button type="button" class="btn btn-light">操作</button>
												<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
													<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
												</button>
												<ul class="dropdown-menu" role="menu" style="overflow:scroll;height:240px;">
													<li><a href="javascript:reject('${item.sellerId}','${item.remarks}')">备注</a></li>
													<li><a href="javascript:setUp('${item.sellerId}','${item.baseTraffic}','${item.autoStart}','${item.autoEnd}')">设置访问量</a></li>
													<li><a href="javascript:setAttention('${item.sellerId}','${item.reals}','${item.fake}')">设置关注人数</a></li>
													<c:choose>
														<c:when test="${item.recTime == '0'}">
															<li><a href="javascript:updateStatus('${item.sellerId}')">店铺推荐</a></li>
<%--															<li><a href="<%=basePath%>mall/seller/updateStatus.action?id=${item.sellerId}&status=1&pageNo=${pageNo}">店铺推荐</a></li>--%>
														</c:when>
														<c:otherwise>
															<li><a href="<%=basePath%>mall/seller/updateStatus.action?id=${item.sellerId}&status=0&pageNo=${pageNo}&recommendDuration=0">不推荐</a></li>
														</c:otherwise>
													</c:choose>
													<c:choose>
														<c:when test="${item.frozenState == '1'}">
															<li><a href="javascript:unFreezeMoney('${item.sellerId}')">解冻店铺</a></li>
														</c:when>
														<c:otherwise>
															<li><a href="javascript:freezeSellerMoney(`${item.sellerId}`,`${item.money}`,`${item.sellerName}`)">冻结店铺</a></li>
														</c:otherwise>
													</c:choose>
													<c:choose>
														<c:when test="${item.black == '1'}">
															<li><a href="<%=basePath%>mall/seller/updateBlack.action?id=${item.sellerId}&isBlack=0&pageNo=${pageNo}">取消拉黑</a></li>
														</c:when>
														<c:otherwise>
															<li><a href="<%=basePath%>mall/seller/updateBlack.action?id=${item.sellerId}&isBlack=1&pageNo=${pageNo}">拉黑</a></li>
														</c:otherwise>
													</c:choose>
													<c:choose>
														<c:when test="${item.flow == '1'}">
															<li><a href="<%=basePath%>mall/seller/updateFlow.action?id=${item.sellerId}&isFlow=0&pageNo=${pageNo}">开启自动流量</a></li>
														</c:when>
														<c:otherwise>
															<li><a href="<%=basePath%>mall/seller/updateFlow.action?id=${item.sellerId}&isFlow=1&pageNo=${pageNo}">关闭自动流量</a></li>
														</c:otherwise>
													</c:choose>
													<li><a href="javascript:LoginFree('${item.sellerId}')">以卖家的身份登录</a></li>

													<li><a href="javascript:setCreditScore('${item.sellerId}','${item.creditScore}')">设置信誉分</a></li>
													<li><a href="javascript:setSoldNum('${item.sellerId}')">设置销量</a></li>
													<li><a href="javascript:updateLevel('${item.sellerId}')">修改等级</a></li>

													<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_CUSTOMER')}">
														<li class="link">
															<a href="javascript:void(0);" onclick="parent.chat('${item.usercode}')">联系商家</a>
														</li>
													</c:if>
													<li><a href="javascript:updateHighOpinion('${item.sellerId}')">修改好评率</a></li>
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

		<form id="succeededForm1">

			<input type="hidden" name="id" id="aid" value="${id}">

			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="modal_set3" tabindex="-1" role="dialog"
					 aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">

							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
								<h4 class="modal-title" style="text-align: center;">登录卖家</h4>
							</div>


							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true"></button>
								<h4 class="modal-title copy-link" id="tokenLink" name="tokenLink"></h4>

							</div>


							<div class="modal-footer" style="margin-top: 0;">
								<button id="sub" type="submit" class="btn btn-default" style="margin-right: 200px;" onclick="submitForms()">以卖家身份登录</button>
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



<!-- 	$("#seller_Id").val(sellerId);
        $("#base_traffic").val(baseTraffic);
        $("#auto_start").val(autoStart);
        $("#auto_end").val(autoEnd); -->
<div class="form-group">

	<form action="<%=basePath%>mall/seller/updateUp.action"
		  method="post" id="succeededForm">

		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="seller_Id" id="seller_Id" value="${seller_Id}">

		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set" tabindex="-1" role="dialog"
				 aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
							<h4 class="modal-title">每小时最小流量</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="base_traffic" name="base_traffic"
									   class="form-control" value="${base_traffic}" oninput="value=value.replace(/[^\d]/g,'')">
							</div>
						</div>

						<div class="modal-header">
							<h4 class="modal-title">每小时流量波动范围</h4>
						</div>

						<div class="modal-body">

							<input id="auto_start" style="width: 255px; float: left; margin-right: 10px;" name="auto_start" class="form-control "  value="${auto_start}" oninput="value=value.replace(/[^\d]/g,'')" />
							<span style="margin-top: 5px;float: left;margin-right: 10px;">-</span>
							<input id="auto_end"  style="width: 255px;float: left;" name="auto_end" class="form-control "
								   value="${auto_end}" oninput="value=value.replace(/[^\d]/g,'')"/>
						</div>

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
							<h4 class="modal-title">是否生效</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<div class="controls">
									<select id="auto_valid" name="auto_valid" class="form-control " >
										<option value="1">生效</option>
										<option value="0">不生效</option>
									</select>
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

</div>

<div class="form-group">

	<form action="<%=basePath%>mall/seller/updateAttention.action"
		  method="post" id="succeededForm">

		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="seller_Ids" id="seller_Ids" value="${seller_Ids}">

		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set1" tabindex="-1" role="dialog"
				 aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
							<h4 class="modal-title">真实关注</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="realAttention" name="base_traffic"
									   class="form-control" value="${realAttention}" oninput="value=value.replace(/[^\d]/g,'')" readonly="true">
							</div>
						</div>
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">虚假关注</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="fakeAttention" name="fakeAttention"
									   class="form-control" value="${fakeAttention}" oninput="value=value.replace(/[^\d]/g,'')">
							</div>
						</div>
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">总关注</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="allAttention" name="base_traffic"
									   class="form-control" value="${allAttention}" oninput="value=value.replace(/[^\d]/g,'')" readonly="true">
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

	<form action="<%=basePath%>mall/seller//freezeMoney.action"
		  method="post" id="succeededForm2">

		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="seller_Id4" id="seller_Id4" value="${seller_Id4}">

		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set4" tabindex="-1" role="dialog"
				 aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
							<h4 class="modal-title">商家名称</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="sellerNames" name="sellerNames"
									   class="form-control" value="${sellerNames}"  readonly="true">
							</div>
						</div>
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">商家余额</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="money" name="fakeAttention"
									   class="form-control" value="${money}"  readonly="true">
							</div>
						</div>
<%--						<c:if test="${platformName != 'TikTokWholesale'}">--%>
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true"></button>
								<h4 class="modal-title">冻结金额 <span style="color: red;">（输入0意味金额被全部冻结）</span></h4>

							</div>

							<div class="modal-body">
								<div class="">
									<input id="amount" name="amount"
										   class="form-control" value="${amount}" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')">
								</div>
							</div>
<%--							<div class="modal-header">--%>
<%--								<button type="button" class="close" data-dismiss="modal"--%>
<%--										aria-hidden="true"></button>--%>
<%--								<h4 class="modal-title">冻结天数</h4>--%>
<%--							</div>--%>

<%--							<div class="modal-body">--%>
<%--								<div class="">--%>
<%--									<input id="freezeDays" name="freezeDays"--%>
<%--										   class="form-control" value="${freezeDays}" oninput="value=value.replace(/[^\d]/g,'')">--%>
<%--								</div>--%>
<%--							</div>--%>
<%--						</c:if>--%>

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

	<form action="<%=basePath%>mall/seller//refreshCredit.action"
		  method="post" id="succeededForm">

		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="sellerId1" id="sellerId1" value="${sellerId1}">

		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set2" tabindex="-1" role="dialog"
				 aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
							<h4 class="modal-title">当前信誉分</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="NowCreditScore" name="NowCreditScore"
									   class="form-control" value="${NowCreditScore}" readonly="true" oninput="if(value>200){value=200}else{value=value.replace(/[^\d]/g,'')}if(value.indexOf(0)==0){value=0}">
							</div>
						</div>



						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">加减分</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<div class="controls">
									<select id="type" name="type" class="form-control " >
										<option value="1">加分</option>
										<option value="0">减分</option>
									</select>
								</div>
							</div>
						</div>


						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">操作分数</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="score" name="score"
									   class="form-control" value="${score}" oninput="if(value>200){value=200}else{value=value.replace(/[^\d]/g,'')}if(value.indexOf(0)==0){value=0}">
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

	<form action="<%=basePath%>mall/seller/updateStatus.action"
		  method="post" id="succeededForma">

		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="id" id="sellerId19" value="${sellerId19}">
		<input type="hidden" name="status" id="status" value="1">

		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set11" tabindex="-1" role="dialog"
				 aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">店铺推荐天数</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="recommendDuration" name="recommendDuration"
									   class="form-control" oninput="if(value>100000){value=100000}else{value=value.replace(/[^\d]/g,'')}if(value.indexOf(0)==0){value=1}"  maxlength="6"  required>
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

	<form action="<%=basePath%>mall/seller/updateHighOpinion.action"
		  method="post" id="succeededForma">

		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="id" id="sellerId19h" value="${sellerId19}">

		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set11h" tabindex="-1" role="dialog"
				 aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">店铺好评率</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="recommendDurationh" name="recommendDurationh"
									   class="form-control"
									   oninput="this.value = this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\..*/g, '$1'); if (this.value && parseFloat(this.value) > 100000) { this.value = '100000'; } if (this.value === '') { this.value = '1'; }"
									   maxlength="10" required>

							</div>
						</div>

						<div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn " data-dismiss="modal">关闭</button>
							<button id="subh" type="submit" class="btn btn-default">确认</button>
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

	<form action="<%=basePath%>mall/seller//updateStoreLevel.action"
		  method="post" id="succeededForm" onSubmit="return inputNull(this)">

		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="sellerId" id="sellerId15" value="${sellerId}">
		<input type="hidden" name="selectLevel" id="selectLevel" value="${selectLevel}">

		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set15" tabindex="-1" role="dialog"
				 aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">


						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">当前等级</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<div class="controls">
									<select id="updateLevel" name="level" class="form-control" >
									</select>
								</div>
							</div>
						</div>


						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">当前累计充值金额</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="rechargeAmount" name="rechargeAmount"
									   class="form-control" value="${rechargeAmount}" oninput="value=value.replace(/[^\d]/g,'')"  maxlength="9">
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



<form action="<%=basePath%>mall/seller//unFreezeMoney.action"
	  method="post" id="succeededForm3">

	<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
	<input type="hidden" name="seller_Id2" id="seller_Id2" value="${seller_Id2}">


</form>

<div class="form-group">

	<form action="<%=basePath%>mall/seller/setSoldNum.action"
		  method="post" id="succeededForm5">

		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="sellerId5" id="sellerId5" value="${sellerId5}">

		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set5" tabindex="-1" role="dialog"
				 aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">

						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
							<h4 class="modal-title">真实销量</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="realSoldNum" name="realSoldNum"
									   class="form-control" value="${realSoldNum}" oninput="value=value.replace(/[^\d]/g,'')" readonly="true">
							</div>
						</div>
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">虚拟销量</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="fakeSoldNum" name="fakeSoldNum"
									   class="form-control" value="${fakeSoldNum}" oninput="value=value.replace(/[^\d]/g,'')">
							</div>
						</div>
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true"></button>
							<h4 class="modal-title">总销量</h4>
						</div>

						<div class="modal-body">
							<div class="">
								<input id="allSoldNum" name="allSoldNum"
									   class="form-control" value="${allSoldNum}" oninput="value=value.replace(/[^\d]/g,'')" readonly="true">
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

	<form action="<%=basePath%>mall/seller/updateRemarks.action"
		  method="post" id="succeededForm">

		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="sellerId" id="sellerId" value="${sellerId}">

		<div class="col-sm-1">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set9" tabindex="-1" role="dialog"
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

<!-- 模态框（Modal） -->
<div class="modal fade" id="modal_rec" tabindex="-1" role="dialog"

	 aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content" style="width: 725px;">

			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
				<h4 class="modal-title">店铺推荐时间/推荐时长</h4>
			</div>

			<div class="modal-body">
				<div class="">
					店铺推荐时间<input id="recTime" type="text" name="recTime"
								   class="form-control" readonly="readonly" />
				</div>
			</div>

			<div class="modal-body">
				<div class="">
					推荐时长（天）<input id="recommendDurations" type="text" name="recommendDurations"
									 class="form-control" readonly="readonly" />
				</div>
			</div>


		</div>
	</div>
</div>
<!-- 模态框（Modal） -->
<div class="modal fade" id="modal_detail1" tabindex="-1" role="dialog"
	 aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content" style="width: 725px;">

			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
				<h4 class="modal-title">访客/待到账</h4>
			</div>

			<div class="modal-body">
				<div class="">
					访问人数<input id="viewsNum" type="text" name="viewsNum"
								   class="form-control" readonly="readonly" />
				</div>
			</div>

			<div class="modal-body">
				<div class="">
					待到账金额<input id="willIncome" type="text" name="willIncome"
									 class="form-control" readonly="readonly" />
				</div>
			</div>


		</div>
	</div>
</div>
<!-- 模态框（Modal） -->
<div class="modal fade" id="modal_detail9" tabindex="-1" role="dialog"
	 aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content" style="width: 725px;">

			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
				<h4 class="modal-title">商品数量</h4>
			</div>

			<div class="modal-body">
				<div class="">
					商品数量<input id="goodsNum" type="text" name="goodsNum"
								   class="form-control" readonly="readonly" />
				</div>
			</div>


		</div>
	</div>
</div>
<!-- End Content -->
<!-- //////////////////////////////////////////////////////////////////////////// -->

<%@ include file="include/js.jsp"%><script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
<script>

	function unFreezeMoney(sellerId) {
		$("#seller_Id2").val(sellerId);
		swal({
			title : "是否解除冻结该商家? 解除冻结后，将释放冻结资金至账商家账户余额",
			text : "",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			document.getElementById("succeededForm3").submit();
		});
	}
</script>


<script type="text/javascript">
	<%--setTimeout(function() {--%>
	<%--	start();--%>
	<%--}, 100);--%>

	<%--function start(){--%>
	<%--	var img = $("#iconImg").val();--%>
	<%--	var show_img = document.getElementById('show_img');--%>
	<%--	show_img.src="<%=basePath%>normal/showImg.action?imagePath="+img;--%>
	<%--}--%>

	function setUp(sellerId,baseTraffic,autoStart,autoEnd){
		$("#seller_Id").val(sellerId);
		$("#base_traffic").val(baseTraffic);
		$("#auto_start").val(autoStart);
		$("#auto_end").val(autoEnd);
		$('#modal_set').modal("show");
	}
	function setCreditScore(sellerId,creditScore){
		$("#sellerId1").val(sellerId);
		$("#NowCreditScore").val(creditScore);
		$('#modal_set2').modal("show");
	}


	function freezeSellerMoney(sellerId,money,sellerName){

		if (/^[+-]?\d+(\.\d+)?[eE][+-]?\d+$/.test(money)) {
			// 如果是科学计数法，将其转换为常规数字
			money = parseFloat(money).toString();
		}


		$("#seller_Id4").val(sellerId);
		$("#money").val(money);
		$("#sellerNames").val(sellerName);
		$("#amount").val(money);
		$("#freezeDays").val(1);
		$('#modal_set4').modal("show");
	}

	function setSoldNum(sellerId){
		$("#sellerId5").val(sellerId);
		getSoldNum(sellerId)
		$('#modal_set5').modal("show");
	}

	function updateLevel(sellerId){
		$("#sellerId15").val(sellerId);
		getLevleBySellerId(sellerId)
		$('#modal_set15').modal("show");
	}

	function inputNull(){
		
		let updateLevel = $("#updateLevel").val();
		var selectLevelValue = $("#selectLevel").val();
		var rechargeAmount = $("#rechargeAmount").val();
		var levels = JSON.parse(selectLevelValue);


		if (levels.hasOwnProperty(updateLevel)) {
			var currentLevelAmount = parseFloat(levels[updateLevel]);

			// 获取下一个等级
			var nextLevel = getNextLevel(levels, updateLevel);

			// 如果存在下一个等级
			if (nextLevel) {
				var nextLevelAmount = parseFloat(levels[nextLevel]);

				// 如果 rechargeAmount 不在当前等级和下一个等级之间的范围内，弹出提示框
				if (rechargeAmount < currentLevelAmount || rechargeAmount >= nextLevelAmount) {
					swal({
						title: "当前累计充值不在修改等级范围内!",
						timer: 2000,
						showConfirmButton: false
					});
					return false;
				}
			} else {
				if (rechargeAmount < currentLevelAmount) {
					swal({
						title: "当前累计充值不在修改等级范围内!",
						timer: 2000,
						showConfirmButton: false
					});
					return false;
				}
			}
		}
		return true;
	}

	function getNextLevel(levels, currentLevel) {
		var keys = Object.keys(levels);
		var currentIndex = keys.indexOf(currentLevel);

		// 如果当前等级不是最后一个等级，返回下一个等级的名称
		if (currentIndex < keys.length - 1) {
			return keys[currentIndex + 1];
		}

		return null; // 如果当前等级是最后一个等级，返回null
	}


	function getLevleBySellerId(sellerId){
		$.ajax({
			type: "get",
			url: "<%=basePath%>mall/seller/getLevleBySellerId.action",
			dataType : "json",
			data : {
				"sellerId" : sellerId
			},
			success : function(data) {

				if (data.code === 200) {
					var tmp = data;
					var levels = tmp.levelMap;
					var sellerLevel = tmp.sellerLevel;
					var rechargeAmount = tmp.rechargeAmount;
					var selectHtml = '';
					for (var key in levels) {
						var selected = (key === sellerLevel) ? 'selected="selected"' : '';
						selectHtml += '<option value="' + key + '" ' + selected + '>' + key + '(累计充值：≥' + levels[key] + ')'  + '</option>';
					}
					$("#updateLevel").html(selectHtml);
					$("#selectLevel").val(JSON.stringify(levels));
					$("#rechargeAmount").val(rechargeAmount);
				}
				if (data.code == 500){
					swal({
						title: data.error,
						timer: 2000,
						showConfirmButton: false
					})

				}

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
	}

	function getViewNumsBySellerIds(sellerId){
		$("#sellerId").val(sellerId);
		ajaxViewNum(sellerId)
	}

	function getSellerRec(recTime,recommendDuration){
		$("#recTime").val(recTime);
		$("#recommendDurations").val(recommendDuration);
		$('#modal_rec').modal("show");
	}

	function getGoodsNumBySellerIds(sellerId){
		$("#sellerId").val(sellerId);
		ajaxGoodsNum(sellerId)
	}

	function setAttention(sellerId,real,fake){
		var allAttention = Number(real) + Number(fake);
		$("#seller_Ids").val(sellerId);
		$("#realAttention").val(real);
		$("#fakeAttention").val(fake);
		$("#allAttention").val(allAttention);
		$('#modal_set1').modal("show");
	}

	function updateStatus(sellerId){
		$("#sellerId19").val(sellerId);
		$('#modal_set11').modal("show");
	}

	function updateHighOpinion(sellerId){
		$("#sellerId19h").val(sellerId);
		$('#modal_set11h').modal("show");
	}

	function reject(id,remark) {
		$("#sellerId").val(id);
		$("#remarks").val(remark);
		$('#modal_set9').modal("show");
	};

	function toDelete(id,pageNo){
		$('#id').val(id);
		$('#pageNo').val(pageNo);
		$('#myModalLabel').html("删除");
		$('#mainform').attr("action","<%=basePath%>mall/goods/delete.action");
		$('#modal_succeeded').modal("show");

	}

	function getSoldNum(sellerId){
		$.ajax({
			type: "get",
			url: "<%=basePath%>mall/seller/getSoldNum.action",
			dataType : "json",
			data : {
				"sellerId" : sellerId
			},
			success : function(data) {
				if (data.code === 200) {
					var tmp = data;
					var realSoldNum = tmp.realSoldNums;
					var fakeSoldNum = tmp.fakeSoldNums;
					var allSoldNum = Number(realSoldNum) + Number(fakeSoldNum);
					$("#realSoldNum").val(realSoldNum);
					$("#fakeSoldNum").val(fakeSoldNum);
					$("#allSoldNum").val(allSoldNum);
				}
				if (data.code == 500){
					swal({
						title: data.error,
						timer: 2000,
						showConfirmButton: false
					})

				}

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
	}

	function ajaxViewNum(sellerId){
		$.ajax({
			type: "get",
			url: "<%=basePath%>mall/seller/getViewNumsBySellerIds.action",
			dataType : "json",
			data : {
				"sellerId" : sellerId
			},
			success : function(data) {
				if (data.code === 200) {
					var tmp = data;
					var viewsNum = tmp.viewsNum;
					var willIncome = tmp.willIncome;
					$("#viewsNum").val(viewsNum);
					$("#willIncome").val(willIncome);
					$('#modal_detail1').modal("show");
				}
				if (data.code == 500){
					swal({
						title: data.error,
						timer: 2000,
						showConfirmButton: false
					})

				}

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
	}
	function ajaxGoodsNum(sellerId){
		$.ajax({
			type: "get",
			url: "<%=basePath%>mall/seller/getGoodsNumBySellerIds.action",
			dataType : "json",
			data : {
				"sellerId" : sellerId
			},
			success : function(data) {
				if (data.code === 200) {
					var tmp = data;
					var goodsNum = tmp.goodsNum;
					$("#goodsNum").val(goodsNum);
					$('#modal_detail9').modal("show");
				}
				if (data.code == 500){
					swal({
						title: data.error,
						timer: 2000,
						showConfirmButton: false
					})

				}

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
	}


	function LoginFree(sellerId){

		var a = "";
		$.ajax({
			type: "get",
			url: "<%=basePath%>mall/seller/getTokenLink.action",
			dataType : "json",
			data : {
				"id" : sellerId
			},
			success : function(data) {
				
				if (data.code === 200) {
					var tmp = data;
					a = tmp.url;
					$("#tokenLink").html(a);
					console.log(a)
				}
				if (data.code == 500){
					a = "h5域名链接错误";
					$("#tokenLink").html(a);
				}

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
		$("#aid").val(sellerId);
		$('#modal_set3').modal("show");

	}
	function submitForms() {
		// 获取隐藏输入字段的值
		debugger
		var sellerId = document.getElementById('aid').value;
		$.ajax({
			type: "get",
			url: "<%=basePath%>mall/seller/LoginFree.action",
			dataType : "json",
			data : {
				"id" : sellerId
			},
			success : function(data) {
				if (data.code === 200) {
					var tmp = data;
					var loginUrl = tmp.loginUrl;
					console.log(loginUrl)
					window.open(loginUrl);
					$('#modal_set3').modal('hide');
				}
				if (data.code == 500){
					swal({
						title: data.error,
						timer: 2000,
						showConfirmButton: false
					})

				}

			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}
		});
	}

	document.getElementById('tokenLink').addEventListener('click', function () {
		// 选择要复制的文本
		var textToCopy = this.innerText;



		navigator.clipboard.writeText(textToCopy)
				.then(() => {
					console.log('文本已成功复制到剪贴板');
				})
				.catch((err) => {
					console.error('复制文本到剪贴板时发生错误：', err);
				});

		swal({
			title: "已复制到粘贴板",
			timer: 1000,
			showConfirmButton: false
		})
	});

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
</script>
<script>
	// 页面加载完毕后执行异步加载图片
	window.addEventListener('load', loadLazyImages);

	// 异步加载图片
	function loadLazyImages() {
		// 获取所有带有lazy-img类名的图片元素
		const lazyImages = document.querySelectorAll('.lazy-img');

		// 创建 Intersection Observer 实例
		const observer = new IntersectionObserver((entries, observer) => {
			entries.forEach(entry => {
				if (entry.isIntersecting) {
					// 当图片项进入视口时，将data-src的值赋给src属性，加载图片
					const img = entry.target;
					img.src = img.getAttribute('data-src');
					img.style.opacity = 1; // 设置图片透明度为1，使图片渐显
					observer.unobserve(img); // 停止观察，避免重复加载
				}
			});
		});
		// 遍历所有图片项，开始观察
		lazyImages.forEach(image => {
			observer.observe(image);
		});
	}
</script>
<style>
	/* 样式可根据实际需求自定义 */
	.lazy-img {
		opacity: 0; /* 初始时图片透明 */
		transition: opacity 0.3s ease-in; /* 渐变过渡效果 */
	}
</style>
</body>
</html>