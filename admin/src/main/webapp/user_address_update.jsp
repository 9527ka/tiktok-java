<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page isELIgnored="false" %>
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
			<h3>用户收货地址</h3>
			
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
							修改用户收货地址
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal"
								action="<%=basePath%>normal/adminUserAction!updateUserAddress.action"
								method="post" name="mainForm" id="mainForm">
								
								<input type="hidden" name="id" id="id" value="${mallAddresses.id}"/>
								<input type="hidden" name="partyId" id="partyId" value="${partyId}"/>


								<div class="form-group">
									<label class="col-sm-2 control-label form-label">姓名</label>
									<div class="col-sm-5">
										<input id="contacts" name="contacts" class="form-control" value="${mallAddresses.contacts}" maxlength="64"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">手机号</label>
									<div class="col-sm-5">
										<input id="phone" name="phone" class="form-control" value="${mallAddresses.phone}" maxlength="20" oninput="value=value.replace(/[^\d]/g,'')"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">邮箱</label>
									<div class="col-sm-5">
										<input id="email" name="email" class="form-control" value="${mallAddresses.email}" maxlength="64"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">详细地址</label>
									<div class="col-sm-5">
										<input id="address" name="address" class="form-control" value="${mallAddresses.address}" maxlength="255"/>
									</div>
								</div>
<%--								<div class="form-group">--%>
<%--									<label class="col-sm-2 control-label form-label">国家</label>--%>
<%--									<div class="col-sm-5">--%>
<%--										<input id="country" name="country" class="form-control" value="${mallAddresses.country}" />--%>
<%--									</div>--%>
<%--								</div>--%>


<%--								<select id="attributeId" name="attributeId"--%>
<%--										class="form-control " >--%>
<%--									<label class="col-sm-2 control-label form-label">国家</label>--%>
<%--									<c:forEach var = "item" items = "${countryIdList}">--%>
<%--										<option value="${item.key}" <c:if test="${attributeId == item.key}">selected="true"</c:if> >${item.value}</option>--%>
<%--									</c:forEach>--%>
<%--								</select>--%>


								<div class="form-group" id="country">
									<label class="col-sm-2 control-label form-label">国家</label>
									<div class="col-sm-3 " style = width:800px;>
										<div class="input-group">
											<select id="countryId" name="countryId"
													class="form-control " oninput="countryChange()">
												<c:forEach var = "item" items = "${countryIdList}">
													<option value="${item.key}" <c:if test="${countryId == item.key}">selected="true"</c:if> >${item.value}</option>
												</c:forEach>
											</select>

										</div>
									</div>
								</div>
										<div class="form-group" id="provinceIds">
											<label class="col-sm-2 control-label form-label">州</label>
											<div class="col-sm-3 " style = width:800px;>
												<div class="input-group">
													<select id="province" name="province"
															class="form-control " oninput="provinceChange()">
														<c:forEach var = "item" items = "${provinceLists}">
															<option value="${item.key}" <c:if test="${province == item.value}">selected="true"</c:if> >${item.value}</option>
														</c:forEach>
													</select>

												</div>
											</div>
										</div>

									<div class="form-group" id="cityIds">
										<label class="col-sm-2 control-label form-label">市</label>
										<div class="col-sm-3 " style = width:800px;>
											<div class="input-group">
												<select id="city" name="city"
														class="form-control ">
													<c:forEach var = "item" items = "${cityLists}">
														<option value="${item.key}" <c:if test="${city == item.value}">selected="true"</c:if> >${item.value}</option>
													</c:forEach>
												</select>

											</div>
										</div>
									</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">邮编</label>
									<div class="col-sm-5">
										<input id="postcode" name="postcode" class="form-control" value="${mallAddresses.postcode}" maxlength="32"/>
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
		<%--window.onload=sw;--%>
		<%--function sw(){--%>
		<%--	debugger;--%>
		<%--	var provinceLists ='<%=request.getAttribute("provinceLists")%>';--%>

		<%--	if (Object.keys(provinceLists).length === 0) {--%>
		<%--		console.log(provinceLists);--%>
		<%--		document.getElementById("cityIds").style.display="none";--%>
		<%--		// docum ent.getElementById("cityIds").style.display="none";--%>
		<%--	}else {--%>
		<%--		console.log(provinceLists)--%>
		<%--	}--%>

		<%--}--%>

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

		function countryChange(){
			// window.open('http://127.0.0.1:41879/wap/api/user!login.action?username=完全&password=我去饿&lang=en','_blank');
			let countryId = document.getElementById("countryId").value;
			$("#province").find("option").remove();
			$("#city").find("option").remove();
			var provinceLists = findStatesByContrtyId(countryId);
			if (Object.keys(provinceLists).length > 0) {
				let num = 0;
				for (var key in provinceLists) {
					num = num + 1;
					if (num === 1) {
						debugger
						findCityByStateId(key)
					}
				}
			}
		}


		function provinceChange(){
			let statesId = document.getElementById("province").value;
			$("#city").find("option").remove();
			var provinceLists = findCityByStateId(statesId);
		}


		function findStatesByContrtyId(contrtyId){
			var provinceLists;
			$.ajax({
				async:false,
				type: "get",
				url: "<%=basePath%>address/findStatesByContrtyId.action",
				dataType : "json",
				data : {
					"countryId" : contrtyId
				},
				success : function(data) {
					var tmp = data;
					provinceLists = tmp.province;

					console.log(provinceLists);
					if (Object.keys(provinceLists).length === 0) {
						document.getElementById("provinceIds").style.display="none";
						document.getElementById("cityIds").style.display="none";

					} else {
						for(var key in provinceLists){
							$("#province").append('<option value='+key+'>'+provinceLists[key]+'</option>');
							document.getElementById("provinceIds").style.display="";
						}
					}
					return provinceLists;
				},
				error : function(XMLHttpRequest, textStatus,
								 errorThrown) {
					console.log("请求错误");
				}

			});
			return provinceLists;

		}


		function findCityByStateId(statesId){
			$.ajax({
				async:false,
				type: "get",
				url: "<%=basePath%>address/findCityByStateId.action",
				dataType : "json",
				data : {
					"statesId" : statesId
				},
				success : function(data) {
					var tmp = data;
					cityList = tmp.city;

					if (Object.keys(cityList).length === 0) {
						document.getElementById("cityIds").style.display="none";

					} else {
						for(var key in cityList){
							$("#city").append('<option value='+key+'>'+cityList[key]+'</option>');
							document.getElementById("cityIds").style.display="";
						}
					}
				},
				error : function(XMLHttpRequest, textStatus,
								 errorThrown) {
					console.log("请求错误");
				}

			});
		}

	</script>
	
</body onload="sw()">

</html>
