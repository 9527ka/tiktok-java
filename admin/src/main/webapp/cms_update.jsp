<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
<%@ include file="include/head.jsp"%>
</head>

<body>

	<%@ include file="include/loading.jsp"%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>公告管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminCmsAction!list.action" method="post" id="queryForm">
			
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />

			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改公告
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminCmsAction!update.action"
								method="post" name="mainForm" id="mainForm">
								
								<input type="hidden" name="id" id="id" value="${cms.id}" />

								<div class="form-group">
									<label class="col-sm-1 control-label form-label">标题</label>
									<div class="col-sm-10">
										<input id="title" name="title" class="form-control " placeholder="标题" value="${cms.title}" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-1 control-label form-label">状态</label>
									<div class="col-sm-10 ">
										<div class="input-group">

											<select id="status" name="status" class="form-control ">
												<option value="0" <c:if test="${cms.status == '0'}">selected="true"</c:if>>启用</option>
												<option value="1" <c:if test="${cms.status == '1'}">selected="true"</c:if>>禁用</option>
											</select>

										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-1 control-label form-label">选择语言</label>
									<div class="col-sm-10 ">
										<div class="input-group">
											<select id="language" name="language"
													class="form-control ">
												<option value="cn" <c:if test="${cms.language == 'cn'}">selected="true"</c:if> >中文</option>
												<option value="en" <c:if test="${cms.language == 'en'}">selected="true"</c:if> >英文</option>
												<option value="vi" <c:if test="${cms.language == 'vi'}">selected="true"</c:if> >越南语</option>
												<option value="hi" <c:if test="${cms.language == 'hi'}">selected="true"</c:if> >印度语</option>
												<option value="id" <c:if test="${cms.language == 'id'}">selected="true"</c:if> >印度尼西亚语</option>
												<option value="tw" <c:if test="${cms.language == 'tw'}">selected="true"</c:if> >繁体中文</option>
												<option value="de" <c:if test="${cms.language == 'de'}">selected="true"</c:if> >德语</option>
												<option value="fr" <c:if test="${cms.language == 'fr'}">selected="true"</c:if> >法语</option>
												<option value="ja" <c:if test="${cms.language == 'ja'}">selected="true"</c:if> >日语</option>
												<option value="ko" <c:if test="${cms.language == 'ko'}">selected="true"</c:if> >韩语</option>
												<option value="th" <c:if test="${cms.language == 'th'}">selected="true"</c:if> >泰语</option>
												<option value="ru" <c:if test="${cms.language == 'ru'}">selected="true"</c:if> >俄语</option>
												<option value="ms" <c:if test="${cms.language == 'ms'}">selected="true"</c:if> >马来西亚语</option>
												<option value="pt" <c:if test="${cms.language == 'pt'}">selected="true"</c:if> >葡萄牙语</option>
												<option value="es" <c:if test="${cms.language == 'es'}">selected="true"</c:if> >西班牙语</option>
												<option value="el" <c:if test="${cms.language == 'el'}">selected="true"</c:if> >希腊语</option>
												<option value="it" <c:if test="${cms.language == 'it'}">selected="true"</c:if> >意大利语</option>
												<option value="tr" <c:if test="${cms.language == 'tr'}">selected="true"</c:if> >土耳其语</option>
												<option value="af" <c:if test="${cms.language == 'af'}">selected="true"</c:if> >南非荷兰语</option>
												<option value="ph" <c:if test="${cms.language == 'ph'}">selected="true"</c:if> >菲律宾语</option>
												<option value="ar" <c:if test="${cms.language == 'ar'}">selected="true"</c:if> >阿拉伯语</option>
											</select>

										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-1 control-label form-label">选择模块</label>
									<div class="col-sm-10 ">
										<div class="input-group">
											<select id="type" name="type"
													class="form-control ">
												<option value="0" <c:if test="${cms.type == '0'}">selected="true"</c:if> >公告管理</option>
											</select>

										</div>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-1 control-label form-label">内容</label>
									<div class="col-sm-10">
										<textarea class="form-control" rows="20" id="content_text" name="content_text" placeholder="请输入内容...">${cms.content}</textarea>
										<input type="hidden" name="content" id="content" value="${cms.content}" />
									</div>
								</div>
								
								<div class="col-sm-1">
									<!-- 模态框（Modal） -->
									<div class="modal fade" id="modal_succeeded" tabindex="-1"
										role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
										<div class="modal-dialog">
											<div class="modal-content">
											
												<div class="modal-header">
													<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
													<h4 class="modal-title" id="myModalLabel">确认新增</h4>
												</div>
												
												<div class="modal-body">												
													<div class="form-group">
														<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
														<div class="col-sm-4">
															<input id="login_safeword" type="password" name="login_safeword" class="login_safeword"
																placeholder="请输入登录人资金密码">
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

								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<a href="javascript:goUrl(${pageNo})" class="btn">取消</a> 
										<a href="javascript:submit()" class="btn btn-default">保存</a>
									</div>
								</div>

							</form>

						</div>

					</div>
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
	
	<script src="<%=basePath%>js/util.js" type="text/javascript"></script>

	<script type="text/javascript">
		function submit() {
			/* swal({
				title : "是否保存?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				var sHTML = $('#content_text').val();
				$("#content").val(sHTML);
				document.getElementById("mainForm").submit();
			}); */
			var sHTML = $('#content_text').val();
			$("#content").val(sHTML);
			$('#modal_succeeded').modal("show");
		}
	</script>

</body>

</html>
