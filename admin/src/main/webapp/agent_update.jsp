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
			<h3>代理商</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminAgentAction!list.action"
				method="post" id="queryForm">
				<!-- <s:hidden name="pageNo" id="pageNo"></s:hidden> -->
				<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}" />
				<!-- <s:hidden name="name_para" id="name_para"></s:hidden> -->
				<input type="hidden" id="name_para" name="name_para" value="${name_para}" />
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改代理商
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminAgentAction!update.action"
								method="post" name="mainForm" id="mainForm">
								
								<!-- <s:hidden name="id" id="id"></s:hidden> -->
								<input type="hidden" id="id" name="id" value="${id}"/>
								<!-- <s:hidden name="name_para" id="name_para"></s:hidden> -->
								<input type="hidden" id="name_para" name="name_para" value="${name_para}"/>

								<%-- <div class="form-group">
									<label class="col-sm-2 control-label form-label">名称</label>
									<div class="col-sm-5">
										<!-- <s:textfield id="name" name="name" cssClass="form-control " /> -->
										<input id="name" name="name" class="form-control " readonly="readonly" value="${name}" />
									</div>
								</div> --%>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">登录权限</label>
									<div class="col-sm-2">
										<%-- <s:select id="login_authority" cssClass="form-control "
											name="login_authority" list="#{true:'有权限',false:'无'}"
											listKey="key" listValue="value" value="login_authority" /> --%>
											<select id="login_authority" name="login_authority" class="form-control " >
											   <option value="true" <c:if test="${login_authority == 'true'}">selected="true"</c:if> >有权限</option>
											   <option value="false" <c:if test="${login_authority == 'false'}">selected="true"</c:if> >无</option>
											</select>
									</div>
								</div>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">操作权限</label>
									<div class="col-sm-2">
										<%-- <s:select id="opera_authority" cssClass="form-control "
											name="opera_authority" list="#{true:'有权限',false:'无'}"
											listKey="key" listValue="value" value="opera_authority" /> --%>
											<select id="opera_authority" name="opera_authority" class="form-control " >
											   <option value="true" <c:if test="${opera_authority == 'true'}">selected="true"</c:if> >有权限</option>
											   <option value="false" <c:if test="${opera_authority == 'false'}">selected="true"</c:if> >无</option>
											</select>
									</div>
								</div>
								
								<div class="form-group">
									<label for="input002" class="col-sm-2 control-label form-label">备注</label>
									<div class="col-sm-5">
										<!-- <s:textarea name="remarks" id="remarks"
											cssClass="form-control  input-lg" rows="3" cols="10" /> -->
										<textarea name="remarks" id="remarks" class="form-control  input-lg" rows="3" cols="10" >${remarks}</textarea>
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
