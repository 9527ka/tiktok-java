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
			<h3>会员评价</h3>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>


<%--							<a href="javascript:fanhui()"--%>
<%--							   class="btn btn-light" style="margin-bottom: 12px"><i--%>
<%--									class="fa fa-pencil"></i>返回</a>--%>

						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>mall/goods/evaluation.action" method="post"
								id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${pageNo}">
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<input id="userName" name="userName" class="form-control"
											placeholder="会员昵称" value = "${userName}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-2">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="isShelf" name="isShelf"
														class="form-control ">
													<option value="-2">评价</option>
													<option value="1" <c:if test="${evaluationType == '1'}">selected="true"</c:if>>好评</option>
													<option value="2" <c:if test="${evaluationType == '2'}">selected="true"</c:if>>中评</option>
													<option value="3" <c:if test="${evaluationType == '3'}">selected="true"</c:if>>差评</option>
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
										<td>会员昵称</td>
										<td>评价</td>
										<td>评分</td>
										<td>所在店铺</td>
										<td>评价内容</td>
										<td>评价时间</td>
										<td>状态</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<!-- <s:iterator value="page.elements" status="stat"> -->
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
										<input type="hidden" name="iconImg" id="iconImg" value = "${item.iconImg}"/>
										<tr>
									    <td>${item.userName}</td>
												<td>
												<c:choose>
													<c:when test="${item.evaluationType == '1'}">
														<span class="right label label-success">好评</span>
													</c:when>
													<c:when test="${item.evaluationType == '2'}">
														<span class="right label label-warning">中评</span>
													</c:when>
													<c:otherwise>
														<span class="right label label-danger">差评</span>
													</c:otherwise>
												</c:choose>
											</td>
										<td>${item.rating}</td>
										<td>${item.sellerName}</td>
										<td>${item.content}</td>

										<td>${item.createTime}</td>
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

															<li><a href="<%=basePath%>mall/goods/deleteEvaluation.action?id=${item.id}">删除</a></li>
															<c:choose>
																<c:when test="${item.status == '1'}">
																	<li><a href="<%=basePath%>mall/goods/updateEvaluationStatus.action?id=${item.id}&status=0&pageNo=${pageNo}">启用</a></li>
																</c:when>
																<c:otherwise>
																	<li><a href="<%=basePath%>mall/goods/updateEvaluationStatus.action?id=${item.id}&status=1&pageNo=${pageNo}">禁用</a></li>
																</c:otherwise>
															</c:choose>
															<li><a href="javascript:setUp(
															'${item.imgUrl1}',
															'${item.imgUrl2}',
															'${item.imgUrl3}',
															'${item.imgUrl4}',
															'${item.imgUrl5}',
															'${item.imgUrl6}',
															'${item.imgUrl7}',
															'${item.imgUrl8}',
															'${item.imgUrl9}')">查看图片</a></li>
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

			<form action="<%=basePath%>mall/seller//refreshCredit.action"
				  method="post" id="succeededForm">

				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">

				<div class="col-sm-1">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_set2" tabindex="-1" role="dialog"
						 aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">

								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
											aria-hidden="true">&times;</button>
									<h4 class="modal-title">评论图片</h4>
								</div>

								<div class="form-group" style="">
									<label class="col-sm-2 control-label form-label"></label>

									<div class="" style="display: flex;justify-content: start;">
										<div>
											<input type="file" id="fileName1" name="fileName2"  value="${fileName2}" onchange="upload2();"  style="position:absolute;opacity:0;"  disabled>
											<label for="fileName2">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="img1" src="${img1}"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName2" name="fileName2"  value="${fileName2}" onchange="upload2();"  style="position:absolute;opacity:0;"  disabled>
											<label for="fileName2">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="img2" src="${img2}"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName3" name="fileName3"  value="${fileName3}" onchange="upload3();"  style="position:absolute;opacity:0;"  disabled>
											<label for="fileName3">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="img3" src="${img3}"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName4" name="fileName"  value="${fileName4}" onchange="upload4();"  style="position:absolute;opacity:0;"  disabled>
											<label for="fileName4">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="img4" src="${img4}"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName5" name="fileName"  value="${fileName5}" onchange="upload5();"  style="position:absolute;opacity:0;"  disabled>
											<label for="fileName5">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="img5" src="${img5}"   /></div> 　　
												　                   　</label> 　　
										</div>

									</div>

										<%--                  <div style="width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</div>--%>
									<label class="col-sm-2 control-label form-label" style="margin-top: 5px; color: red"></label>
								</div>

								<div class="form-group" style="">
									<label class="col-sm-2 control-label form-label"></label>

									<div class="" style="display: flex;justify-content: start;">
										<div>
											<input type="file" id="fileName6" name="fileName6"  value="${fileName6}" onchange="upload6();"  style="position:absolute;opacity:0;"  disabled>
											<label for="fileName6">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="img6" src="${img6}"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName7" name="fileName7"  value="${fileName7}" onchange="upload7();"  style="position:absolute;opacity:0;"  disabled>
											<label for="fileName7">　　
												　　　　                 <div class="avatar"><img width="90px" height="90px" id="img7" src="${img7}"   /></div> 　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName8" name="fileName"  value="${fileName8}" onchange="upload8();"  style="position:absolute;opacity:0;"  disabled>
											<label for="fileName8">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="img8" src="${img8}"   /></div> 　　　　
												　                   　</label> 　　
										</div>
										<div>
											<input type="file" id="fileName9" name="fileName9"  value="${fileName9}" onchange="upload9();"  style="position:absolute;opacity:0;"  disabled>
											<label for="fileName9">　　
												　　　　                  <div class="avatar"><img width="90px" height="90px" id="img9" src="${img9}"   /></div> 　　　
												　                   　</label> 　　
										</div>

									</div>

										<%--                  <div style="width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</div>--%>
										<%--									<label class="col-sm-2 control-label form-label" style="margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</label>--%>
								</div>
								<div class="modal-footer" style="margin-top: 0;">
									<button type="button" class="btn " data-dismiss="modal">关闭</button>
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

		function toDelete(id,pageNo){
			$('#id').val(id);
			$('#pageNo').val(pageNo);
			$('#myModalLabel').html("删除");
			$('#mainform').attr("action","<%=basePath%>mall/goods/delete.action");

			$('#modal_succeeded').modal("show");

		}

		function setUp(imgUrl1,imgUrl2,imgUrl3,imgUrl4,imgUrl5,imgUrl6,imgUrl7,imgUrl8,imgUrl9){
			$("#img1").attr("src",imgUrl1);
			$("#img2").attr("src",imgUrl2);
			$("#img3").attr("src",imgUrl3);
			$("#img4").attr("src",imgUrl4);
			$("#img5").attr("src",imgUrl5);
			$("#img6").attr("src",imgUrl6);
			$("#img7").attr("src",imgUrl7);
			$("#img8").attr("src",imgUrl8);
			$("#img9").attr("src",imgUrl9);
			$('#modal_set2').modal("show");

		}


		function fanhui() {
				window.location.href="<%=basePath%>mall/goods/sellerGoodsList.action?pageNo=" + ${pageNo};
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
	</script>
</body>
</html>