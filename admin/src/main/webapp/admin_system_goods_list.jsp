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
			<h3>店铺商品</h3>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>mall/goods/sellerGoodsList.action" method="post"
								id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${pageNo}">
								<input type="hidden" name="messages" id="messages"
									value="${messages}">
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<input id="goodName" name="goodName" class="form-control"
											placeholder="商品名称" value = "${goodName}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<input id="goodId" name="goodId" class="form-control"
											placeholder="商品ID" value = "${goodId}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<input id="sellerName" name="sellerName" class="form-control"
											placeholder="店铺名称" value = "${sellerName}"/>
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2" >
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
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="categoryId" name="categoryId"
														class="form-control ">
													<option value="0">一级分类</option>
													<c:forEach var = "item" items = "${categoryLevel1Map}">
														<option value="${item.key}" <c:if test="${categoryId == item.key}">selected="true"</c:if> >${item.value}</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="secondaryCategoryId" name="secondaryCategoryId"
														class="form-control ">
													<option value="0">二级分类</option>
													<c:forEach var = "item" items = "${categoryLevel2Map}">
														<option value="${item.key}" <c:if test="${secondaryCategoryId == item.key}">selected="true"</c:if> >${item.value}</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-2" style="margin-top: 15px;">
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
									 || security.isResourceAccessible('OP_GOODS_OPERATE')}">
							<a href="javascript:shelfBatch(1)"
							   class="btn btn-light" style="margin-bottom: 12px"><i
									class="fa fa-pencil"></i>批量上架</a>
							<a href="javascript:shelfBatch(0)"
							   class="btn btn-light" style="margin-bottom: 12px"><i
									class="fa fa-pencil"></i>批量下架</a>
                                 <c:if test="${autoShelveUrl != null && autoShelveUrl != ''}">
						            <a href="${autoShelveUrl}" class="btn btn-light" style="margin-bottom: 12px"><i
                                            class="fa fa-pencil"></i>自动上架商品设置</a>
                            </c:if>
						</c:if>

						<div class="panel-body">
								
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td>
											<input id="selAll" type="checkbox" />
										</td>
										<td>商品ID</td>
										<td>商品名称</td>
										<td>所属店铺</td>
										<td>店铺类型</td>
										<td>商品分类</td>
										<td>进货价格</td>
										<td>销售价格</td>
										<td>是否新品</td>
										<td>是否推荐</td>
										<td>是否热销</td>
										<td>是否上架</td>
										<td>下架审核</td>
										<td>销量</td>
<%--										<td>最小购买数量</td>--%>
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
										<td>
											<input name="checkbox" type="checkbox" value="${item.id}">
										</td>
									    <td>${item.goodsId}</td>
<%--									    <td>${item.name}</td>--%>
										<td style="width: 160px;">
											<div style="width:120px; margin-left: 20px; text-align: center; overflow:hidden;text-overflow:ellipsis;white-space:nowrap;"  title="${item.name}">

													${item.name}
											</div>
										</td>
									    <td>${item.sellerName}</td>
										<td>
											<c:if test="${item.sellerRoleName=='GUEST'}">
												<span class="right label label-danger">演示店铺</span>
											</c:if>
											<c:if test="${item.sellerRoleName=='MEMBER'}">
												<span class="right label label-success">真实店铺</span>
											</c:if>
										</td>
										<td>${item.PName}</td>
										<td>${item.systemPrice}</td>
										<td>${item.sellingPrice}</td>
										<td>
											<c:choose>
												<c:when test="${item.systemNewTime == '0'}">
<%--													<span class="right label label-danger">非新品</span>--%>
													<span style="color: red">非新品</span>
												</c:when>
												<c:otherwise>
													<a href="#" onClick="getSysNewDuration('${item.systemNewTime}','${item.sysNewDuration}')">新品</a>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.systemRecTime == '0'}">
<%--													<span class="right label label-danger">不推荐</span>--%>
													<span style="color: red">不推荐</span>
												</c:when>
												<c:otherwise>
													<a href="#" onClick="getSysRecDuration('${item.systemRecTime}','${item.sysRecDuration}')">推荐</a>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.sellWellTime == '0'}">
<%--													<span class="right label label-danger">非热销</span>--%>
													<span style="color: red">非热销</span>
												</c:when>
												<c:otherwise>
													<a href="#" onClick="getSellWellDuration('${item.sellWellTime}','${item.sellWellDuration}')">热销</a>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.isShelf == '0'}">
													<span class="right label label-danger">否</span>
												</c:when>
												<c:otherwise>
													<span class="right label label-success">是</span>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.checkFlag == '0'}">
													<span class="right label label-danger">请审核！</span>
												</c:when>
												<c:when test="${item.checkFlag == '1'}">
													<span class="right label label-success">已审核</span>
												</c:when>
												<c:when test="${item.checkFlag == '2'}">
													<span class="right label label-danger">已驳回</span>
												</c:when>
												<c:otherwise>
													<span class="right label label-success"></span>
												</c:otherwise>
											</c:choose>
										</td>
										<td>${item.soldNum}</td>
<%--										<td>${item.buyMin}</td>--%>
											<td>
												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_GOODS_OPERATE')}">
											
													<div class="btn-group">
														<button type="button" class="btn btn-light">操作</button>
														<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
															<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
														</button>
														<ul class="dropdown-menu" role="menu">
															<li><a href="<%=basePath%>mall/goods/evaluation.action?&sellerGoodsId=${item.id}&sellerId=${item.sellerId}">商品评论</a></li>
															<c:choose>
																<c:when test="${item.systemNewTime == '0'}">
<%--																	<li>	<a href="<%=basePath%>mall/goods/updateStatus.action?id=${item.id}&status=1&type=2&pageNo=${pageNo}">新品</a></li>--%>
																	<li><a href="javascript:updateStatusTime('${item.id}',2)">新品</a></li>
																</c:when>
																<c:otherwise>
																	<li><a href="<%=basePath%>mall/goods/updateStatus.action?id=${item.id}&status=0&type=2&pageNo=${pageNo}">非新品</a></li>
																</c:otherwise>
															</c:choose>
															<c:choose>
																<c:when test="${item.systemRecTime == '0'}">
<%--																	<li><a href="<%=basePath%>mall/goods/updateStatus.action?id=${item.id}&status=1&type=1&pageNo=${pageNo}">推荐</a></li>--%>
																	<li><a href="javascript:updateStatusTime('${item.id}',1)">推荐</a></li>
																</c:when>
																<c:otherwise>
																	<li><a href="<%=basePath%>mall/goods/updateStatus.action?id=${item.id}&status=0&type=1&pageNo=${pageNo}">不推荐</a></li>
																</c:otherwise>
															</c:choose>
															<c:choose>
																<c:when test="${item.sellWellTime == '0'}">
<%--																	<li><a href="<%=basePath%>mall/goods/updateStatus.action?id=${item.id}&status=1&type=3&pageNo=${pageNo}">热销推荐</a></li>--%>
																	<li><a href="javascript:updateStatusTime('${item.id}',3)">热销推荐</a></li>
																</c:when>
																<c:otherwise>
																	<li><a href="<%=basePath%>mall/goods/updateStatus.action?id=${item.id}&status=0&type=3&pageNo=${pageNo}">非热销</a></li>
																</c:otherwise>
															</c:choose>
															<li><a href="<%=basePath%>mall/comment//toAdd.action?&sellerGoodsId=${item.id}&sellerId=${item.sellerId}&pageNo=${pageNo}">进店评价</a></li>
<%--															<li><a href="javascript:onsucceeded('${item.id}')">设置商品最小购买数</a></li>--%>
															<c:choose>
																<c:when test="${item.checkFlag == '0'}">
																	<li><a href="<%=basePath%>mall/goods/updateCheckFlag.action?id=${item.id}&checkFlag=1&pageNo=${pageNo}">同意下架</a></li>
																	<li><a href="<%=basePath%>mall/goods/updateCheckFlag.action?id=${item.id}&checkFlag=2&pageNo=${pageNo}">驳回下架</a></li>
																</c:when>
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

		<!-- 模态框 -->
		<div class="form-group">

			<form action="<%=basePath%>mall/goods/buyMinUpdate.action"
				  method="post" id="succeededForm">

				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="sellerGoodsId" id="sellerGoodsId" value="${sellerGoodsId}">

				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_set" tabindex="-1"
						 role="dialog" aria-labelledby="myModalLabel"
						 aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">

								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">请输入最小购买数量</h4>
								</div>

								<div class="modal-body">
									<div class="">
										<input id="buyMin"  name="buyMin"
											   class="form-control" placeholder="请输入最小购买数量" oninput="value=value.replace(/[^\d]/g,'')">
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
		<div class="modal fade" id="modal_sysNewDuration" tabindex="-1" role="dialog"

			 aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content" style="width: 725px;">

					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
						<h4 class="modal-title">店铺新品时间/新品持续时长</h4>
					</div>

					<div class="modal-body">
						<div class="">
							店铺新品时间<input id="systemNewTime" type="text" name="systemNewTime"
											   class="form-control" readonly="readonly" />
						</div>
					</div>

					<div class="modal-body">
						<div class="">
							新品持续时长（天）<input id="sysNewDuration" type="text" name="sysNewDuration"
											   class="form-control" readonly="readonly" />
						</div>
					</div>


				</div>
			</div>
		</div>
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="modal_sysRecDuration" tabindex="-1" role="dialog"

			 aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content" style="width: 725px;">

					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
						<h4 class="modal-title">店铺商品推荐时间/推荐持续时长</h4>
					</div>

					<div class="modal-body">
						<div class="">
							店铺商品推荐时间<input id="systemRecTime" type="text" name="systemRecTime"
											   class="form-control" readonly="readonly" />
						</div>
					</div>

					<div class="modal-body">
						<div class="">
							推荐持续时长（天）<input id="sysRecDuration" type="text" name="sysRecDuration"
											   class="form-control" readonly="readonly" />
						</div>
					</div>


				</div>
			</div>
		</div>
		<!-- 模态框（Modal） -->
		<div class="modal fade" id="modal_sellWellDuration" tabindex="-1" role="dialog"

			 aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content" style="width: 725px;">

					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
						<h4 class="modal-title">店铺热销时间/热销持续时长</h4>
					</div>

					<div class="modal-body">
						<div class="">
							店铺热销时间<input id="sellWellTime" type="text" name="sellWellTime"
											   class="form-control" readonly="readonly" />
						</div>
					</div>

					<div class="modal-body">
						<div class="">
							热销持续时长（天）<input id="sellWellDuration" type="text" name="sellWellDuration"
											   class="form-control" readonly="readonly" />
						</div>
					</div>


				</div>
			</div>
		</div>

		<div class="form-group">

			<form action="<%=basePath%>mall/goods/updateStatus.action"
				  method="post" id="succeededForma">

				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<input type="hidden" name="id" id="ids" value="${ids}">
				<input type="hidden" name="type" id="type" value="${type}">
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
									<h4 class="modal-title">持续时长（天）</h4>
								</div>

								<div class="modal-body">
									<div class="">
										<input id="duration" name="duration"
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
		
		<%@ include file="include/footer.jsp"%>

	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->

	<%@ include file="include/js.jsp"%><script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
	<script>
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




		function getSysNewDuration(systemNewTime,sysNewDuration){
			$("#systemNewTime").val(systemNewTime);
			$("#sysNewDuration").val(sysNewDuration);
			$('#modal_sysNewDuration').modal("show");
		}

		function getSysRecDuration(systemRecTime,sysRecDuration){
			$("#systemRecTime").val(systemRecTime);
			$("#sysRecDuration").val(sysRecDuration);
			$('#modal_sysRecDuration').modal("show");
		}

		function getSellWellDuration(sellWellTime,sellWellDuration){
			$("#sellWellTime").val(sellWellTime);
			$("#sellWellDuration").val(sellWellDuration);
			$('#modal_sellWellDuration').modal("show");
		}

		function updateStatusTime(id,type){
			$("#ids").val(id);
			$("#type").val(type);
			$('#modal_set11').modal("show");
		}

		function toDelete(id,pageNo){
			$('#id').val(id);
			$('#pageNo').val(pageNo);
			$('#myModalLabel').html("删除");
			$('#mainform').attr("action","<%=basePath%>mall/goods/delete.action");

			$('#modal_succeeded').modal("show");

		}

		function addFakeComment(sellerId,creditScore){
			$("#sellerId1").val(sellerId);
			$("#NowCreditScore").val(creditScore);
			$('#modal_set2').modal("show");
		}


		function onsucceeded(id) {
			// var session_token = $("#session_token").val();
			// $("#session_token_success").val(session_token);
			$("#sellerGoodsId").val(id);
			$('#modal_set').modal("show");
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

		function shelfBatch(isShelf) {
			let $checkbox = $(":checkbox:checked");
			if ($checkbox.length === 0) {
				return;
			}
			let num = $checkbox.length;
			let arr = new Array($checkbox.length);
			for (let i = 0; i < arr.length; i++) {
				arr[i] = $($checkbox[i]).val();
				if (arr[i] == "on"){
					num = $checkbox.length - 1;
				}
			}

			if (isShelf === 1){
				swal({
					title : "确定 批量上架" + num + "个商品吗",
					text : "",
					type : "warning",
					showCancelButton : true,
					confirmButtonColor : "#DD6B55",
					confirmButtonText : "确认",
					closeOnConfirm : false
				}, function() {
					let arr = new Array($checkbox.length);
					for (let i = 0; i < $checkbox.length; i++) {
						arr[i] = $($checkbox[i]).val();
					}
					$.ajax({
						url: "<%=basePath%>mall/goods/shelfBatch.action",
						type: 'POST',
						// contentType: "application/json",
						traditional: true,
						data: {
							'ids': arr,
							'isShelf': isShelf
						},
						success: function (data) {
							if (data.code === 200) {
								let queryForm = document.getElementById("queryForm");
								document.getElementById("messages").value = data.message;
								queryForm.submit();
								return;
							}
							if (data.code == 500){
								swal({
									title: data.error,
									timer: 2000,
									showConfirmButton: false
								})

							}

						}
					});
				});
			} else if (isShelf === 0){
				swal({
					title : "确定 批量下架" + num + "个商品吗",
					text : "",
					type : "warning",
					showCancelButton : true,
					confirmButtonColor : "#DD6B55",
					confirmButtonText : "确认",
					closeOnConfirm : false
				}, function() {
					let arr = new Array($checkbox.length);
					for (let i = 0; i < $checkbox.length; i++) {
						arr[i] = $($checkbox[i]).val();
					}
					$.ajax({
						url: "<%=basePath%>mall/goods/shelfBatch.action",
						type: 'POST',
						// contentType: "application/json",
						traditional: true,
						data: {
							'ids': arr,
							'isShelf': isShelf
						},
						success: function (data) {
							if (data.code === 200) {
								let queryForm = document.getElementById("queryForm");
								document.getElementById("messages").value = data.message;
								queryForm.submit();
								return;
							}
							if (data.code == 500){
								swal({
									title: data.error,
									timer: 2000,
									showConfirmButton: false
								})

							}

						}
					});
				});
			}

		}

		$("#selAll").on("click", function(){
			var che=$("#selAll").prop('checked');
			if(che){
				$("input[name='checkbox']").prop('checked',true);
			} else {
				$("input[name='checkbox']").prop('checked',false);
			}
		})


		$("input[name='checkbox']").on("click", function(){
			var setFalse=false;// 默认不给全选按钮设置false
			$.each($("input[name='checkbox']"),function(index,item){
				// 如果在普通多选框的循环中发现有false,就需要将全选按钮设置为false
				if(item.checked==false){
					setFalse=true;
				}
			})
			if(setFalse){
				$("#selAll").prop('checked',false);
			} else {// 如果普通按钮都为true, 则全选按钮也赋值为true
				$("#selAll").prop('checked',true);
			}
		})

	</script>
</body>
</html>