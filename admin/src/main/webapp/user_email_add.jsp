<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
			<h3>用户基础管理</h3>

			<ul class="nav nav-tabs">
				<li><a href="<%=basePath%>normal/adminUserAction!toAdd.action?registerType=phone">新增手机号演示用户</a></li>
				<li><a href="<%=basePath%>normal/adminUserAction!toAdd.action?registerType=email">新增邮箱演示用户</a></li>
			</ul>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminUserAction!list.action" method="post" id="queryForm">
			    <input type="hidden" name="pageNo" id="pageNo"/>
				<input type="hidden" name="name_para" id="name_para"/>
				<input type="hidden" name="rolename_para" id="rolename_para"/>				
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							新增邮箱演示用户
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminUserAction!add.action"
								method="post" name="mainForm" id="mainForm">
								
								<input type="hidden" name="id" id="id"/>
								<input type="hidden" name="registerType" id="registerType" value="${registerType}"/>
								
								<!-- dapp+交易所 菜单 ######################################################################################################## -->
								<c:if test="${!security.isDappOrExchange()}">
																
									<div class="form-group">
										<label class="col-sm-2 control-label form-label">钱包地址</label>
										<div class="col-sm-5">
											<input id="address" name="address" class="form-control"/>
										</div>
									</div>
								
								</c:if>
								
								<!-- 交易所 菜单 ############################################################################################################# -->
								<c:if test="${security.isDappOrExchange()}">
									
									<div class="form-group">
										<label class="col-sm-2 control-label form-label">用户名</label>
										<div class="col-sm-5">
											<input id="username" name="username" class="form-control" maxlength="100" placeholder="最大长度为100"/>
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-2 control-label form-label">登录密码</label>
										<div class="col-sm-5">
											<input id="password" name="password" class="form-control" maxlength="12" placeholder="最大长度为12"/>
										</div>
									</div>
								
									<div class="form-group">
										<label class="col-sm-2 control-label form-label"></label>
										<div class="col-sm-5">
											演示账号资金密码默认为000000，可登录后修改
										</div>
									</div>
			
								</c:if>
								
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">上级用户或上级代理商UID(选填)</label>
									<div class="col-sm-3">
										<!-- <s:textfield id="parents_usercode" name="parents_usercode"
											cssClass="form-control" placeholder="上级用户或上级代理商UID"  /> -->											
											<input id="parents_usercode" name="parents_usercode" class="form-control" placeholder="上级用户或上级代理商UID"/>
									</div>
								</div>
<%--								--%>
<%--								<div class="form-group">--%>
<%--									<label class="col-sm-2 control-label form-label">手动派单</label>--%>
<%--									<div class="col-sm-4">--%>
<%--										<select id="manualDispatch" name="manualDispatch" class="form-control " >--%>
<%--										   <option value="0">关闭</option>--%>
<%--										   <option value="1">开启</option>--%>
<%--										</select>--%>
<%--									</div>--%>
<%--								</div>--%>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">登录权限</label>
									<div class="col-sm-4">
<%-- 									<s:select id="login_authority" cssClass="form-control "
										name="login_authority" list="#{true:'正常',false:'限制登录'}"
										listKey="key" listValue="value" value="login_authority" /> --%>
										<select id="login_authority" name="login_authority" class="form-control " >
										   <option value="true">正常</option>
										   <option value="false">限制登录</option>
										</select>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">是否锁定</label>
									<div class="col-sm-4">									
<%-- 									<s:select id="enabled" cssClass="form-control "
										name="enabled" list="#{true:'正常',false:'业务锁定（登录不受影响）'}"
										listKey="key" listValue="value" value="enabled" /> --%>
										<select id="enabled" name="enabled" class="form-control " >
										   <option value="true">正常</option>
										   <option value="false">业务锁定（登录不受影响）</option>
										</select>											
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">自动评价</label>
									<div class="col-sm-4">
										<select id="autoComment" name="autoComment" class="form-control " >
											<option value="true">开启</option>
											<option value="false">关闭</option>
										</select>
									</div>
								</div>

								<div class="form-group">
									<label for="input002" class="col-sm-2 control-label form-label">备注</label>
									<div class="col-sm-5">
<!-- 									<s:textarea name="remarks" id="remarks"
										cssClass="form-control  input-lg" rows="3" cols="10" /> -->											
										<input id="remarks" name="remarks" class="form-control input-lg" rows="3" cols="10"/>
									</div>
								</div>

<%--								<h3>用户收货地址</h3>--%>

<%--								<div class="form-group">--%>
<%--									<label class="col-sm-2 control-label form-label">收货人</label>--%>
<%--									<div class="col-sm-3">--%>
<%--										<input id="contacts" name="contacts" class="form-control" value="${contacts}" placeholder="请输入收货人姓名"/>--%>
<%--									</div>--%>
<%--								</div>--%>
<%--								<div class="form-group">--%>
<%--									<label class="col-sm-2 control-label form-label">手机号</label>--%>
<%--									<div class="col-sm-3">--%>
<%--										<input id="phone" name="phone" class="form-control" value="${phone}" placeholder="请输入收货人手机号"/>--%>
<%--									</div>--%>
<%--								</div>--%>
<%--								<div class="form-group">--%>
<%--									<label class="col-sm-2 control-label form-label">详细地址</label>--%>
<%--									<div class="col-sm-3">--%>
<%--										<input id="useraddress" name="useraddress" class="form-control" value="${useraddress}" placeholder="请输入收货人详细地址"/>--%>
<%--									</div>--%>
<%--								</div>--%>

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
			}, function() {
				checkParameter();
				if (!a){
					a = true;
					return false;
				} else {
					a = true;
					document.getElementById("mainForm").submit();
				}
			});
		}

		function checkParameter(){
			let username = $("#username").val();
			let password = $("#password").val();

			if(username == ""){
				swal({
					title: "请输入用户名!",
					timer: 1500,
					showConfirmButton: false
				})
				a = false;
			}

			let emailRegExp = /^(\w{3,}-*\.*)+@(\w-?)+(\.\w{2,})+$/;
			let ok = emailRegExp.test(username);


			if(!ok){
				swal({
					title: "请输入格式正确的邮箱!",
					timer: 1500,
					showConfirmButton: false
				})
				a = false;
			}


			if(password.length > 12 || password.length < 6){
				swal({
					title: "密码长度必须在6到12位之间",
					timer: 1500,
					showConfirmButton: false
				})
				a = false;
			}

		}

		//判断是不是邮箱
		// $(function(str){
		// 	var string = str.replace(/\s|&nbsp;/g, '') //先去除用户输入的无效字符
		// 	var reg = /^[a-zA-Z0-9_-]+@([a-zA-Z0-9]+\.)+(com|cn|net|org)$/;
		// })

		$(function(){
			$('.nav-tabs a').filter(function() {
				var b = document.URL;
				return this.href == "<%=basePath%>normal/adminUserAction!toAdd.action?registerType=${registerType}";  //获取当前页面的地址
			}).closest('li').addClass('active');  //给当前最靠近的li（其实是现在进入的li）添加‘active’样式

		})
	</script>
	
</body>

</html>
