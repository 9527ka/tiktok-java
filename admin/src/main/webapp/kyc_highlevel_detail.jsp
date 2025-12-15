<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

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
			<h3>用户高级认证</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminContractManageAction!list.action"
				method="post" id="queryForm">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
				
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							用户高级认证详情
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>
						
						<div class="panel-body form-horizontal">

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">用户</label>
								<div class="col-sm-10">
									<input id="username" name="kycHighLevel.username"
										class="form-control " readOnly="readOnly" value="${username}" />
								</div>
							</div>
							
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">工作地址</label>
								<div class="col-sm-10">
									<input id="work_place" name="kycHighLevel.work_place"
										class="form-control " readOnly="readOnly" value="${work_place}" />
								</div>
							</div>
							
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">家庭地址</label>
								<div class="col-sm-10">
									<input id="home_place" name="kycHighLevel.home_place"
										class="form-control " readOnly="readOnly" value="${home_place}" />
								</div>
							</div>
							
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">亲属关系</label>
								<div class="col-sm-10">
									<input id="relatives_relation" name="kycHighLevel.relatives_relation"
										class="form-control " readOnly="readOnly" value="${relatives_relation}" />
								</div>
							</div>
							
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">亲属名称</label>
								<div class="col-sm-10">
									<input id="relatives_name" name="kycHighLevel.relatives_name" 
										class="form-control " readOnly="readOnly" value="${relatives_name}" />
								</div>
							</div>
							
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">亲属地址</label>
								<div class="col-sm-10">
									<input id="relatives_place" name="kycHighLevel.relatives_place" 
										class="form-control " readOnly="readOnly" value="${relatives_place}" />
								</div>
							</div>
							
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">亲属电话</label>
								<div class="col-sm-10">
									<input id="relatives_phone" name="kycHighLevel.relatives_phone" 
										class="form-control " readOnly="readOnly" value="${relatives_phone}" />
								</div>
							</div>
							
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">认证状态</label>
								<div class="col-sm-10">
									<label> 
										<c:if test="${kycHighLevel.status==0}">未审核</c:if>
										<c:if test="${kycHighLevel.status==1}">审核中</c:if> 
										<c:if test="${kycHighLevel.status==2}"> <span class="right label label-success">审核通过</span> </c:if> 
										<c:if test="${kycHighLevel.status==3}">未通过</c:if>
									</label>
								</div>
							</div>
							
							<div class="form-group">
								<label class="col-sm-2 control-label form-label">原因</label>
								<div class="col-sm-10">
									<input id="msg" name="kycHighLevel.msg"
										class="form-control " readOnly="readOnly" value="${msg}" />
								</div>
							</div>

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

</body>

</html>
