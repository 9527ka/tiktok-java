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
							新增公告
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminCmsAction!add.action"
								method="post" name="mainForm" id="mainForm">
								

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
												<option value="0" >启用</option>
												<option value="1">禁用</option>
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
												<option value="cn">中文</option>
												<option value="en">英文</option>
												<option value="vi">越南语</option>
												<option value="hi">印度语</option>
												<option value="id">印度尼西亚语</option>
												<option value="tw">繁体中文</option>
												<option value="de">德语</option>
												<option value="fr">法语</option>
												<option value="ja">日语</option>
												<option value="ko">韩语</option>
												<option value="th">泰语</option>
												<option value="ru">俄语</option>
												<option value="ms">马来西亚语</option>
												<option value="pt">葡萄牙语</option>
												<option value="es">西班牙语</option>
												<option value="el">希腊语</option>
												<option value="it">意大利语</option>
												<option value="tr">土耳其语</option>
												<option value="af">南非荷兰语</option>
												<option value="ph">菲律宾语</option>
												<option value="ar">阿拉伯语</option>
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
												<option value="0">公告管理</option>
<%--												<option value="1">文章管理</option>--%>
											</select>

										</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-1 control-label form-label">内容</label>
									<div class="col-sm-10">
					                    <%-- <textarea class="form-control" rows="20" id="content_text" name="content_text" placeholder="请输入内容...">${content}</textarea> --%>
					                    <textarea class="form-control" rows="15" id="content_text" name="content_text" placeholder="请输入内容...">${cms.content}</textarea>
					                    <input type="hidden" name="content" id="content" value="${cms.content}" />
									</div>
								</div>
								
								<div class="col-sm-1">
									<!-- 模态框（Modal） -->
									<div class="modal fade" id="modal_succeeded" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
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
