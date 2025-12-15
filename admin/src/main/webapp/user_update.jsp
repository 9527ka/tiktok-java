<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
	<%@ include file="include/head.jsp"%>
	<style>
		p {
			margin: 20px 0px 10px;
		}
	</style>
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
		<h3>用户基础管理</h3>

		<%@ include file="include/alert.jsp"%>

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<form action="<%=basePath%>normal/adminUserAction!list.action" method="post" id="queryForm">
			<input type="hidden" name="pageNo" id="pageNo"/>
			<input type="hidden" name="name_para" id="name_para"/>
		</form>
		<!-- END queryForm -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->

		<div class="row">
			<div class="col-md-12 col-lg-12">
				<div class="panel panel-default">

					<div class="panel-title">
						修改用户
						<ul class="panel-tools">
							<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
							<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
						</ul>
					</div>

					<div class="panel-body">

						<form class="form-horizontal"
							  action="<%=basePath%>normal/adminUserAction!update.action"
							  method="post" name="mainForm" id="mainForm">

							<input type="hidden" name="id" id="id" value="${id}"/>
							<input type="hidden" name="name_para" id="name_para"/>
							<input type="hidden" name="rolename_para" id="rolename_para"/>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">用户名</label>
								<div class="col-sm-5">
									<input id="username" name="username" class="form-control" readonly="readonly" value="${username}" />
								</div>
							</div>

							<%--								<div class="form-group">--%>
							<%--									<label class="col-sm-2 control-label form-label">手动派单</label>--%>
							<%--									<div class="col-sm-4">--%>
							<%--										<select id="manualDispatch" name="manualDispatch" class="form-control " >--%>
							<%--											<option value="0" <c:if test="${manualDispatch == '0'}">selected="true"</c:if>>关闭</option>--%>
							<%--											<option value="1" <c:if test="${manualDispatch == '1'}">selected="true"</c:if>>开启</option>--%>
							<%--										</select>--%>
							<%--									</div>--%>
							<%--								</div>--%>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">登录权限</label>
								<div class="col-sm-4">
									<select id="login_authority" name = "login_authority" class="form-control" >
										<option value="true" <c:if test="${login_authority == 'true'}">selected="true"</c:if> >正常</option>
										<option value="false" <c:if test="${login_authority == 'false'}">selected="true"</c:if> >限制登录</option>
									</select>
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">提现权限</label>
								<div class="col-sm-4">
									<select id="withdraw_authority" name="withdraw_authority" class="form-control">
										<option value="true" <c:if test="${withdraw_authority == 'true'}">selected="true"</c:if> >正常</option>
										<option value="false" <c:if test="${withdraw_authority == 'false'}">selected="true"</c:if> >限制提现</option>
									</select>
									<span class="help-block">演示账号该设置不生效，默认无提现权限</span>
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">是否业务锁定</label>
								<div class="col-sm-4">
									<select id="enabled" name="enabled" class="form-control">
										<option value="true" <c:if test="${enabled == 'true'}">selected="true"</c:if> >正常</option>
										<option value="false" <c:if test="${enabled == 'false'}">selected="true"</c:if> >业务锁定（登录不受影响，锁定后无法购买订单、采购、充值、提现）</option>
									</select>
								</div>
							</div>
							<c:if test="${roleName=='GUEST'}">
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">自动评价</label>
									<div class="col-sm-4">
										<select id="autoComment" name="autoComment" class="form-control">
											<option value="true" <c:if test="${autoComment == 'true'}">selected="true"</c:if> >开启</option>
											<option value="false" <c:if test="${autoComment == 'false'}">selected="true"</c:if> >关闭</option>
										</select>
											<%--										<span class="help-block">历史如有未评价的订单，请谨慎作开启自动评价（开启后，如有未评价的订单，系统将全部自动评价）</span>--%>
										<p class="ballon color1">
											1、历史如有未评价的订单，请谨慎作开启自动评价
											<br>
											2、开启后，如有未评价的订单，系统将全部自动评价
											<br>
										</p>
									</div>
								</div>

							</c:if>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">收款币种</label>
								<div class="col-sm-4">
									<select id="withdrawCoinType" name="withdrawCoinType" class="form-control" oninput="withdrawCoinTypeChange()">
										<c:forEach var = "item" items = "${withdrawCoinTypes}">
											<option value="${item.key}" <c:if test="${withdrawCoinType == item.key}">selected="true"</c:if> >${item.value}</option>
										</c:forEach>
									</select>
								</div>
							</div>

							<div class="form-group">
								<label class="col-sm-2 control-label form-label">收款链接协议</label>
								<div class="col-sm-4">
									<select id="withdrawChainName" name="withdrawChainName" class="form-control">
										<c:forEach var = "item" items = "${withdrawChainNames}">
											<option value="${item.key}" <c:if test="${withdrawChainName == item.key}">selected="true"</c:if> >${item.value}</option>
										</c:forEach>
									</select>
								</div>
							</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">收款地址</label>
									<div class="col-sm-5">
										<input id="withdrawAddress" name="withdrawAddress" class="form-control"  value="${withdrawAddress}" placeholder="提现收款地址"/>
									</div>
								</div>

<%--								<div class="form-group">--%>
<%--									<label class="col-sm-2 control-label form-label">用户名</label>--%>
<%--									<div class="col-sm-5">--%>
<%--										<input id="username" name="username" class="form-control" readonly="readonly" value="${username}" />--%>
<%--									</div>--%>
<%--								</div>--%>

									<div class="form-group">
										<label for="input002" class="col-sm-2 control-label form-label">备注</label>
										<div class="col-sm-5">
											<input id="remarks" name="remarks" class="form-control  input-lg" rows="3" cols="10" value="${remarks}"/>
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

<script type="text/javascript">

	var a = true;


	function submit() {
		swal({
			title : "是否保存?",
			text : "",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		},  function() {
			checkWithdrawAddress();

		});
	}

	function withdrawCoinTypeChange() {
		let withdrawCoinType = document.getElementById("withdrawCoinType").value;
		$("#withdrawChainName").find("option").remove();
		findWithdrawNameByCoin(withdrawCoinType);
	}

	function findWithdrawNameByCoin(withdrawCoinType){
		var withdrawChainNames;
		$.ajax({
			async:false,
			type: "get",
			url: "<%=basePath%>address/findWithdrawNameByCoin.action",
			dataType : "json",
			data : {
				"withdrawCoinType" : withdrawCoinType
			},
			success : function(data) {
				var tmp = data;
				withdrawChainNames = tmp.withdrawChainNames;

				console.log(withdrawChainNames);
				if (Object.keys(withdrawChainNames).length === 0) {
					document.getElementById("withdrawChainName").style.display="none";

				} else {
					for(var key in withdrawChainNames){
						$("#withdrawChainName").append('<option value='+key+'>'+withdrawChainNames[key]+'</option>');
						document.getElementById("withdrawChainName").style.display="";
					}
				}
				return withdrawChainNames;
			},
			error : function(XMLHttpRequest, textStatus,
							 errorThrown) {
				console.log("请求错误");
			}

		});
		return withdrawChainNames;

	}


	function checkWithdrawAddress(){
		let withdrawAddress = $("#withdrawAddress").val();
		let chainName = $("#withdrawChainName").val();

		bindWithdrawAddress(a);

		function bindWithdrawAddress(a){
			$.ajax({
				type: "get",
				url: "<%=basePath%>normal/adminUserAction!checkWithdrawAddress.action",
				dataType : "json",
				data : {
					"withdrawAddress" : withdrawAddress,
					"chainName" : chainName
				},
				success : function(data) {
					if (data.code === 200) {
						document.getElementById("mainForm").submit();
						return;
					}

					if (data.code == 500){
						swal({
							title: data.error,
							timer: 2500,
							showConfirmButton: false
						})
						a =  false;
					}

				},
				error : function(XMLHttpRequest, textStatus,
								 errorThrown) {
					console.log("请求错误");
				}
			});
		}

	}
</script>

</body>

</html>
