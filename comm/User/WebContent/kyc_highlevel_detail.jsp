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
			<h3>用户高级认证管理</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<s:if test='isResourceAccessible("ADMIN_KYC_HIGH_LEVEL_LIST")'>
			<form  action="<%=basePath%>normal/adminContractManageAction!list.action"
				method="post" id="queryForm">
				<s:hidden name="pageNo" id="pageNo"></s:hidden>
			</form> 
			</s:if>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							
								用户高级认证详情
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>
						<div class="panel-body form-horizontal">
						<s:if test='isResourceAccessible("ADMIN_KYC_HIGH_LEVEL_DETAIL")'>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">用户</label>
									<div class="col-sm-10">
									<s:textfield id="username" name="kycHighLevel.username" cssClass="form-control " readOnly="true" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">工作地址</label>
									<div class="col-sm-10">
									<s:textfield id="work_place" name="kycHighLevel.work_place" cssClass="form-control " readOnly="true" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">家庭地址</label>
									<div class="col-sm-10">
									<s:textfield id="home_place" name="kycHighLevel.home_place" cssClass="form-control " readOnly="true" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">亲属关系</label>
									<div class="col-sm-10">
									<s:textfield id="relatives_relation" name="kycHighLevel.relatives_relation" cssClass="form-control " readOnly="true" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">亲属名称</label>
									<div class="col-sm-10">
									<s:textfield id="relatives_name" name="kycHighLevel.relatives_name" cssClass="form-control " readOnly="true" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">亲属地址</label>
									<div class="col-sm-10">
									<s:textfield id="relatives_place" name="kycHighLevel.relatives_place" cssClass="form-control " readOnly="true" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">亲属电话</label>
									<div class="col-sm-10">
									<s:textfield id="relatives_phone" name="kycHighLevel.relatives_phone" cssClass="form-control " readOnly="true" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">认证状态</label>
									<div class="col-sm-10">
										<label>
											<s:if test="kycHighLevel.status==0">未审核</s:if>
											<s:if test="kycHighLevel.status==1">审核中</s:if>
											<s:if test="kycHighLevel.status==2"><span class="right label label-success">审核通过</span></s:if>
											<s:if test="kycHighLevel.status==3">未通过</s:if>
										</label>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">原因</label>
									<div class="col-sm-10">
									<s:textfield id="msg" name="kycHighLevel.msg" cssClass="form-control " readOnly="true" />
									</div>
								</div>
							
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


	<script type="text/javascript">
	/* $(function(){
		var id = $('#futures_id').val();
		if(typeof(id)!='undefined'&&id.length>0){
			$("#symbol").attr("disabled", "disabled");
		}
	})
		function submit() {
		$("#symbol").removeAttr("disabled");
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

		} */
	</script>

	
</body>
</html>