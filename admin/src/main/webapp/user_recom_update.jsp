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
<%-- 	<%@ include file="include/top.jsp"%> --%>
<%-- 	<%@ include file="include/menu_left.jsp"%> --%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>用户推荐关系管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminUserRecomAction!list.action"
				method="post" id="queryForm">
				<!-- <s:hidden name="pageNo" id="pageNo"></s:hidden> -->
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
				<!-- <s:hidden name="name_para" id="name_para"></s:hidden> -->
				<input type="hidden" name="name_para" id="name_para" value="${name_para}">
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改推荐关系
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminUserRecomAction!update.action"
								method="post" name="mainForm" id="mainForm">
								
<!-- 								<s:hidden name="id" id="id"></s:hidden> -->
<!-- 								<s:hidden name="partyId" id="partyId"></s:hidden> -->
<!-- 								<s:hidden name="name_para" id="name_para"></s:hidden> -->
<!-- 								<s:hidden name="rolename_para" id="rolename_para"></s:hidden> -->
								<input type="hidden" name="id" id="id" value="${id}" />
								<input type="hidden" name="partyId" id="partyId" value="${partyId}" />
								<input type="hidden" name="name_para" id="name_para" value="${name_para}" />
								<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}" />

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">用户名</label>
									<div class="col-sm-5">
										<!-- <s:textfield id="username" name="username"
											cssClass="form-control " readonly="true" /> -->
										<input id="username" name="username" class="form-control " readonly="readonly" value="${username}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">原推荐人用户名</label>
									<div class="col-sm-5">
										<!-- <s:textfield id="reco_username" name="reco_username"
											cssClass="form-control " readonly="true" /> -->
										<input id="reco_username" name="reco_username" class="form-control " readonly="readonly" value="${reco_username}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">原推荐人UID</label>
									<div class="col-sm-5">
										<!-- <s:textfield id="reco_usercode" name="reco_usercode"
											cssClass="form-control " readonly="true" /> -->
										<input id="reco_usercode" name="reco_usercode" class="form-control " readonly="readonly" value="${reco_usercode}" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">新推荐人UID</label>
									<div class="col-sm-5">
										<!-- <s:textfield id="parent_usercode" name="parent_usercode"
											cssClass="form-control " /> -->
										<input id="parent_usercode" name="parent_usercode" class="form-control " value="${parent_usercode}" />
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">资金密码</label>
									<div class="col-sm-5">
										<!-- <s:textfield type="password" id="login_safeword"
											name="login_safeword" cssClass="form-control " /> -->
										<input type="password" id="login_safeword" name="login_safeword" class="form-control " value="${login_safeword}" />
									</div>
								</div>

								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<a href="javascript:goUrl(${pageNo})"
											class="btn">取消</a> <a href="javascript:submit()"
											class="btn btn-default">保存</a>
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
				document.getElementById("mainForm").submit();
			});
		}
	</script>

</body>

</html>
