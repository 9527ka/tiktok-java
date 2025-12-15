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

		.modal-content {
			background-color: #ffffff;
			/* 可以根据需要添加其他样式属性 */
		}

		.avatar img {
			border: 2px solid black; /* 设置2像素宽度的黑色实线边框 */
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
			<h3>店铺投诉</h3>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<%@ include file="include/alert.jsp"%>
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal" action="<%=basePath%>mall/seller//compliants.action" method="post"
								id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${pageNo}">
								<div class="col-md-12 col-lg-3">
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
												<input id="storeCode" name="storeCode" class="form-control"
													   placeholder="店铺ID" value = "${storeCode}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<input id="storeName" name="storeName" class="form-control"
													   placeholder="店铺名称" value = "${storeName}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="auditStatus" name="auditStatus" class="form-control " >
													<option value="">审核状态</option>
													<option value="0" <c:if test="${auditStatus == '0'}">selected="true"</c:if> >未审核</option>
													<option value="1" <c:if test="${auditStatus == '1'}">selected="true"</c:if> >通过</option>
													<option value="-1" <c:if test="${auditStatus == '-1'}">selected="true"</c:if> >驳回</option>
												</select>
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
						<div class="panel-body">

							<table class="table table-bordered table-striped" border="1">
								<thead>
									<tr>
										<td>投诉人ID</td>
										<td>投诉人账号</td>
										<td>店铺ID</td>
										<td>店铺名称</td>
										<td>投诉类型</td>
										<td>截图凭证</td>
										<td>投诉原因</td>
										<td>状态</td>
										<td>驳回原因</td>
										<td>投诉日期</td>
										<td>审核日期</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
<%--										<input type="hidden" name="iconImg" id="iconImg" value = "${item.iconImg}"/>--%>
										<tr>

											<td>${item.user_code}</td>
											<td>${item.username}</td>
											<td>${item.store_code}</td>
											<td>${item.name}</td>
											<td>
												<c:choose>
													<c:when test="${item.complaint_status == '1'}">
														长时间不发货
													</c:when>
													<c:when test="${item.complaint_status == '2'}">
														卖家服务态度恶劣
													</c:when>
													<c:when test="${item.complaint_status == '3'}">
														卖家交易欺诈
													</c:when>
													<c:when test="${item.complaint_status == '4'}">
														卖家承诺不履行
													</c:when>
													<c:when test="${item.complaint_status == '0'}">
														其他
													</c:when>
												</c:choose>
											</td>
											<td><a href="javascript:setUp(
															'${item.img_url_1}',
															'${item.img_url_2}',
															'${item.img_url_3}',
															'${item.img_url_4}',
															'${item.img_url_5}',
															'${item.img_url_6}',
															'${item.img_url_7}',
															'${item.img_url_8}',
															'${item.img_url_9}')">${item.total}</a></td>
											<td>${item.content}</td>
											<td>
												<c:choose>
													<c:when test="${item.audit_status == '0'}">
														<span class="right label label-danger">待处理</span>
													</c:when>
													<c:when test="${item.audit_status == '1'}">
														<span class="right label label-success">通过</span>
													</c:when>
													<c:when test="${item.audit_status == '-1'}">
														<span class="right label label-danger">驳回</span>
													</c:when>
												</c:choose>
											</td>
											<td>${item.remark}</td>
											<td>${item.create_time}</td>
											<td>${item.auditTime}</td>
											<td>

												<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
															|| security.isResourceAccessible('OP_COMPLIANT_OPERATE')}">
													<c:if test="${item.audit_status == '0'}">
														<div class="btn-group">
															<button type="button" class="btn btn-light">操作</button>
															<button type="button" class="btn btn-light dropdown-toggle"
																	data-toggle="dropdown" aria-expanded="false">
																<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
															</button>
															<ul class="dropdown-menu" role="menu">
																	<li><a href="javascript:setComplainPass(`${item.uuid}`,`${item.name}`,`${item.complaint_status}`)">通过</a></li>
																	<li><a href="javascript:setComplainDown(`${item.uuid}`)">驳回</a></li>
															</ul>
														</div>

													</c:if>
												</c:if>


											</td>

										</tr>

									</c:forEach>

								</tbody>
							</table>
							<%@ include file="include/page_simple.jsp"%>
							<nav>
					</div>
					<!-- End Panel -->

				</div>
			</div>

		</div>
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->

		<!-- 模态框 -->

		<%@ include file="include/footer.jsp"%>

	</div>



	<div class="form-group">

		<form action="<%=basePath%>mall/seller//refreshCredit.action"
			  method="post" id="succeededForm3">

			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">

			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="modal_set2" tabindex="-1" role="dialog"
					 aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content" style="width: 800px;">

							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
								<h4 class="modal-title">截图凭证</h4>
							</div>

<%--							<div id = "asd">--%>
<%--								<img width="200px" height="200px" id="modal_idimg_1" name="modal_idimg_1" src="" onclick="openImg(this.id)"/>--%>
<%--								&lt;%&ndash;					</a>&ndash;%&gt;--%>
<%--							</div>--%>
							<!--黑色遮罩-->
							<div class="black_overlay" id="black_overlay"></div>

							<!--预览容器，存放点击放大后的图片-->
							<div class="enlargeContainer" id="enlargeContainer">
								<!-- 关闭按钮，一个叉号图片 -->
								<img src="./images/close.png" class="close" id="close">
							</div>

							<div class="form-group" style="">
								<label class="col-sm-2 control-label form-label"></label>

								<div class="" style="display: flex;justify-content: start;">
									<div>
										<input type="file" id="fileName1" name="fileName1"  value="${fileName1}" onchange="upload2();"  style="position:absolute;opacity:0;"  disabled>
										<label for="fileName1">　　
											　　　　                  <div class="avatar"><img width="200px" height="200px" id="img1" src="${img1}"  onclick="openImg(this.id)" /></div> 　　
											　                   　</label> 　　
									</div>
									<div>
										<input type="file" id="fileName2" name="fileName2"  value="${fileName2}" onchange="upload2();"  style="position:absolute;opacity:0;"  disabled>
										<label for="fileName2">　　
											　　　　                  <div class="avatar"><img width="200px" height="200px" id="img2" src="${img2}"   onclick="openImg(this.id)"/></div> 　　
											　                   　</label> 　　
									</div>
									<div>
										<input type="file" id="fileName3" name="fileName3"  value="${fileName3}" onchange="upload3();"  style="position:absolute;opacity:0;"  disabled>
										<label for="fileName3">　　
											　　　　                  <div class="avatar"><img width="200px" height="200px" id="img3" src="${img3}"   onclick="openImg(this.id)"/></div> 　　
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
										<input type="file" id="fileName4" name="fileName"  value="${fileName4}" onchange="upload4();"  style="position:absolute;opacity:0;"  disabled>
										<label for="fileName4">　　
											　　　　                  <div class="avatar"><img width="200px" height="200px" id="img4" src="${img4}"   onclick="openImg(this.id)"/></div> 　　
											　                   　</label> 　　
									</div>
									<div>
										<input type="file" id="fileName5" name="fileName"  value="${fileName5}" onchange="upload5();"  style="position:absolute;opacity:0;"  disabled>
										<label for="fileName5">　　
											　　　　                  <div class="avatar"><img width="200px" height="200px" id="img5" src="${img5}"   onclick="openImg(this.id)"/></div> 　　
											　                   　</label> 　　
									</div>
									<div>
										<input type="file" id="fileName6" name="fileName6"  value="${fileName6}" onchange="upload6();"  style="position:absolute;opacity:0;"  disabled>
										<label for="fileName6">　　
											　　　　                  <div class="avatar"><img width="200px" height="200px" id="img6" src="${img6}"   onclick="openImg(this.id)"/></div> 　　
											　                   　</label> 　　
									</div>

								</div>

								<%--                  <div style="width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</div>--%>
								<%--									<label class="col-sm-2 control-label form-label" style="margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</label>--%>
							</div>
							<div class="form-group" style="">
								<label class="col-sm-2 control-label form-label"></label>

								<div class="" style="display: flex;justify-content: start;">

									<div>
										<input type="file" id="fileName7" name="fileName7"  value="${fileName7}" onchange="upload7();"  style="position:absolute;opacity:0;"  disabled>
										<label for="fileName7">　　
											　　　　                 <div class="avatar"><img width="200px" height="200px" id="img7" src="${img7}"   onclick="openImg(this.id)"/></div> 　　
											　                   　</label> 　　
									</div>
									<div>
										<input type="file" id="fileName8" name="fileName"  value="${fileName8}" onchange="upload8();"  style="position:absolute;opacity:0;"  disabled>
										<label for="fileName8">　　
											　　　　                  <div class="avatar"><img width="200px" height="200px" id="img8" src="${img8}"   onclick="openImg(this.id)"/></div> 　　　　
											　                   　</label> 　　
									</div>
									<div>
										<input type="file" id="fileName9" name="fileName9"  value="${fileName9}" onchange="upload9();"  style="position:absolute;opacity:0;"  disabled>
										<label for="fileName9">　　
											　　　　                  <div class="avatar"><img width="200px" height="200px" id="img9" src="${img9}"   onclick="openImg(this.id)"/></div> 　　　
											　                   　</label> 　　
									</div>
								</div>

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

	<div class="form-group">

		<form action="<%=basePath%>mall/seller/audit.action"
			  method="post" id="succeededForm1">

			<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
			<input type="hidden" name="id" id="id" value="${id}">
			<input type="hidden" name="auditStatus" id="auditStatus1" value="${1}">

			<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="modal_set3" tabindex="-1" role="dialog"
					 aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">

							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
								<h4 class="modal-title">通过申请</h4>
							</div>

							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true"></button>
								<h4 class="modal-title">确认要审核通过吗? <br> <br>通过后将向卖家发送弹窗通知、站内信通知、短信通知、邮件通知告知</h4>
							</div>

							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true"></button>
								<h4 class="modal-title" id="NowCreditScore" name="NowCreditScore"></h4>
							</div>


							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true"></button>
								<h4 class="modal-title">操作分数</h4>
							</div>

							<div class="modal-body">
								<div class="">
									<input id="score" name="score" class="form-control" value="${score}" oninput="value=value.replace(/[^\d]/g,''); if(value.indexOf('0')===0){value=0;} " minlength="1" maxlength="5" required>
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

	<!-- Modal -->
	<div class="modal fade" id="modal_set4" tabindex="-1" role="dialog"
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
					<form action="<%=basePath%>mall/seller/audit.action"
						  method="post" id="succeededForm2">
						<input type="hidden" name="id" id="id1" value="${id1}">
						<input type="hidden" name="auditStatus" id="auditStatus2" value="${-1}">
						<textarea name="remark" id="remark" class="form-control  input-lg" rows="2" cols="10" placeholder="请输入驳回原因，100个字符以内" maxlength="100";>${remark}</textarea>
					</form>
				</div>

					<h4 class="modal-title" style="text-align: right; margin-right: 397px;">以上信息将发送给买家</h4>
				<div class="modal-footer">
					<button type="button" class="btn btn-white" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-default" onclick="reject_confirm()">驳回申请</button>
				</div>

			</div>
		</div>
	</div>

	<%@ include file="include/js.jsp"%><script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>

	<script type="text/javascript">

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
			black_overlay.style.display = 'none';
			enlargeContainer.style.display = 'none';
			$('#modal_set2').modal("show");

		}

		function setComplainPass(uuid,name,auditStatus){
			$("#id").val(uuid);
			var auditName;
			switch (auditStatus) {
				case '1':
					auditName = "长时间不发货";
					break;
				case '2':
					auditName = "卖家服务态度恶劣";
					break;
				case '3':
					auditName = "卖家交易欺诈";
					break;
				case '4':
					auditName = "卖家承诺不履行";
					break;
				case '0':
					auditName = "其他";
					break;
				default:
					auditName = "异常类型";
			}
			$("#auditStatus").val(auditStatus);
			$("#NowCreditScore").html("尊敬的卖家[" +name+"]" + "您的店铺收到买家投诉，投诉原因：["+auditName+"]，经系统审核，投诉属实。为保障消费者的合法权益，系统将扣除您的信誉积分["+"操作分数"+"]，请保持良好的店铺信誉，以免为您带来更多的不便。");
			$('#modal_set3').modal("show");
		}

		function setComplainDown(uuid){
			$("#id1").val(uuid);
			$('#modal_set4').modal("show");
		}

		function openImg(id) {
			let black_overlay = document.getElementById('black_overlay');
			let enlargeContainer = document.getElementById('enlargeContainer');
			let closeBtn = document.getElementById('close');

			let toEnlargeImg = document.getElementById(id);
			toEnlargeImg.addEventListener('click', function () {
				// 获取当前图片的路径
				let imgUrl = this.src;
				if (imgUrl.includes("compliants.action")) {
					return;
				}
				// 显示黑色遮罩和预览容器
				black_overlay.style.display = 'block';
				enlargeContainer.style.display = 'block';
				let img = new Image();
				img.src = imgUrl;
				img.classList.add('enlargePreviewImg');
				if (closeBtn.nextElementSibling) {
					enlargeContainer.removeChild(closeBtn.nextElementSibling);
				}
				enlargeContainer.appendChild(img);
			});
		}


		$("#enlargeContainer").on("click", function () {
			black_overlay.style.display = 'none';
			enlargeContainer.style.display = 'none';
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

		function reject_confirm() {
			let remark = $("#remark").val();
			if (remark.trim() === '') {
				swal({
					title: "请输入驳回原因!",
					timer: 2000,
					showConfirmButton: false
				})
				return false;
			}

			swal({
				title : "是否确认驳回?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				document.getElementById("succeededForm2").submit();
			});
		};
	</script>

		<style>
			.black_overlay {
				display: none;
				position: absolute;
				top: 0;
				left: 0;
				width: 100%;
				height: 100%;
				background-color: rgba(0, 0, 0, 0.7);
				z-index: 100;
			}

			.enlargeContainer {
				display: none;
			}

			.enlargePreviewImg {
				position: absolute;
				top: 48%;
				left: 53%;
				transform: translate(-50%, -50%);

				/*宽度设置为页面宽度的70%，高度自适应*/
				width: 107%;
				z-index: 200;
			}

			/*关闭预览*/
			.close {
				position: absolute;
				top: 20px;
				right: 20px;
				width: 20px;
				height: 20px;
				cursor: pointer;
				z-index: 200;
			}

			.lazy-img {
				opacity: 0; /* 初始时图片透明 */
				transition: opacity 0.3s ease-in; /* 渐变过渡效果 */
			}
		</style>
</body>
</html>