<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="include/head.jsp"%>
</head>
<body>
	<%@ include file="include/loading.jsp"%>
	<%@ include file="include/top.jsp"%>
	<%@ include file="include/menu_left.jsp"%>
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="content">



		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="container-default">
			<h3>新闻管理
			</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<s:if test='isResourceAccessible("ADMIN_NEWS_LIST")'>
			<form action="<%=basePath%>normal/adminNewsAction!list.action"
				method="post" id="queryForm">
				<input type="hidden" name="pageNo" id="pageNo"
					value="${param.pageNo}">
			</form>
			</s:if>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							添加新闻
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<s:if test='isResourceAccessible("ADMIN_NEWS_ADD")'>
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminNewsAction!add.action"
								method="post" name="mainForm" id="mainForm">
								  <s:hidden name="id" id="id"></s:hidden>
								<div class="form-group">
									<label class="col-sm-1 control-label form-label">标题</label>
									<div class="col-sm-10">
										<s:textfield id="title" name="title" cssClass="form-control "
											placeholder="标题" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-1 control-label form-label">语言</label>
									<div class="col-sm-2">
									<s:select id="language" cssClass="form-control "
											name="language" list="languageMap"
											listKey="key" listValue="value" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-1 control-label form-label">是否置顶</label>
									<div class="col-sm-2">

										<s:select id="index" cssClass="form-control "
											name="index" list="#{true:'置顶',false:'不置顶'}"
											listKey="key" listValue="value" value="index" />
									</div>
								</div>
							

								<div class="form-group">
									<div class="col-sm-11">
										<div id="summernote"></div>
									</div>
									  <s:hidden name="content" id="content"></s:hidden>
								</div>



								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<a href="javascript:goUrl(<s:property value="pageNo" />)"
											class="btn">取消</a> <a href="javascript:submit()"
											class="btn btn-default">保存</a>
									</div>
								</div>
							
							</form>
							</s:if>	
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
	<script type="text/javascript"
		src="<%=basePath%>js/summernote/summernote.min.js"></script>

	<script>
		/* SUMMERNOTE*/
		$(document).ready(function() {
			$('#summernote').summernote();
			  var content=$("#content").val();
			  $('#summernote').code(content)
		});
	</script>
<s:if test='isResourceAccessible("ADMIN_NEWS_ADD")'>
	<script type="text/javascript">
		function submit() {
			swal({
				title : "是否保存?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : false
			}, function() {
				var sHTML = $('#summernote').code();
				$("#content").val(sHTML);
				document.getElementById("mainForm").submit();
			});

		}
	</script>
</s:if>

</body>
</html>