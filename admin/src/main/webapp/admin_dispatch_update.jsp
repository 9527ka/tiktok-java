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
			<h3>派单</h3>

			<%@ include file="include/alert.jsp"%>


			<form  action="<%=basePath%>adminOrder/dispatchList.action"
				  method="post" id="queryForm">
				<input type="hidden" id="pageNo" name="pageNo" value="${pageNo}" />
			</form>

			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							派单
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">

							<form class="form-horizontal"
								action="<%=basePath%>adminOrder/makeOrder.action"
								method="post" name="mainForm" id="mainForm">

								<input type="hidden" name="id" id="id" value="${order.id}" />
								<input type="hidden" name="goodsId" id="goodsId" value="${order.goodsId}" />
								<input type="hidden" name="prize" id="prize" value="${order.prize}" />
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">等级ID</label>
									<div class="col-sm-5">
										<input id="userCode" name="userCode" class="form-control "  value="${order.userCode}" readonly="readonly"/>
									</div>
								</div>


								<div class="form-group">
									<label class="col-sm-2 control-label form-label">用户等级</label>
									<div class="col-sm-5">
										<input id="vipName" name="vipName" class="form-control "   value="${order.vipName}" readonly="readonly"/>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">账户余额</label>
									<div class="col-sm-5">
										<input id="money" name="money" class="form-control " value="${order.money}" readonly="readonly"/>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">商品名称</label>
									<div class="col-sm-5">
										<input id="goodsName" name="goodsName" value="${order.goodsName}" class="form-control" readonly="readonly"/>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">选择商品</label>

									<div class="col-sm-3">
										<label>　<a style="width: 300px" href="<%=basePath%>brush/goods/goodsList.action?id=${order.id}&pageNo=${pageNo}" class="btn btn-light">+请选择商品</a> 　　</label> 　　
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">封面图(*)</label>

									<div class="col-sm-3">
										　<img width="90px" height="90px" id="show_img" src="<%=basePath%>normal/showImg.action?imagePath=${order.iconImg}"/>
									</div>

								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">单价</label>
									<div class="col-sm-5">
										<input id="discountPrice" name="discountPrice" class="form-control" oninput="change()" value="${order.prize}" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">数量</label>
									<div class="col-sm-5">
										<input id="orderNum" name="orderNum" class="form-control"  oninput="change()"  onkeyup="if(this.value.length==1){this.value=this.value.replace(/[^1-9]/g,'')}else{this.value=this.value.replace(/\D/g,'')}"
											   onafterpaste="if(this.value.length==1){this.value=this.value.replace(/[^1-9]/g,'0')}else{this.value=this.value.replace(/\D/g,'')}" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">总价</label>
									<div class="col-sm-5">
										<input id="totalPrice" name="totalPrice" class="form-control" readonly="readonly" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">是否连单</label>
									<div class="col-sm-4">

										<select id="mustDo" name="mustDo" class="form-control">
											<option value="false">关闭</option>
											<option value="true">开启</option>
										</select>
									</div>
								</div>


								<div class="col-sm-1 form-horizontal">
									<!-- 模态框（Modal） -->
									<div class="modal fade" id="modal_save" tabindex="-1"
										 role="dialog" aria-labelledby="myModalLabel"
										 aria-hidden="true">
										<div class="modal-dialog">
											<div class="modal-content" >
												<div class="modal-header">
													<button type="button" class="close"
															data-dismiss="modal" aria-hidden="true">&times;</button>
													<h4 class="modal-title" id="myModalLabel">确认保存修改</h4>
												</div>
												<div class="modal-body">
													<div class="form-group" >
														<label for="input002" class="col-sm-3 control-label form-label">登录人资金密码</label>
														<div class="col-sm-4">
															<input id="login_safeword" type="password" name="login_safeword"
																   class="login_safeword" placeholder="请输入登录人资金密码" >
														</div>
													</div>
													<!-- <div class="form-group" style="">

														<label for="input002" class="col-sm-3 control-label form-label">验证码</label>
														<div class="col-sm-4">
															<input id="email_code" type="text" name="email_code"
															class="login_safeword" placeholder="请输入验证码" >
														</div>
														<div class="col-sm-4">
															<a id="delete_email_code_button" href="javascript:deleteSendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
														</div>
													</div> -->
<%--													<div class="form-group" >--%>
<%--														<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>--%>
<%--														<div class="col-sm-4">--%>
<%--															<input id="google_auth_code"  name="google_auth_code"--%>
<%--																   placeholder="请输入谷歌验证码" >--%>
<%--														</div>--%>
<%--													</div>--%>
												</div>
												<div class="modal-footer" style="margin-top: 0;">
													<button type="button" class="btn "
															data-dismiss="modal">关闭</button>
													<button id="sub" type="submit"
															class="btn btn-default">确认</button>
												</div>
											</div>
											<!-- /.modal-content -->
										</div>
										<!-- /.modal -->
									</div>
								</div>


								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<a href="javascript:goUrl(${pageNo})"
										   class="btn">取消</a> <a href="javascript:save()"
																 class="btn btn-default">确认</a>
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
		window.onload = function (){
			let discountPrice = document.getElementById("discountPrice").value;
			let orderNum = document.getElementById("orderNum").value;
			if(null == discountPrice || discountPrice ==0){
				document.getElementById("discountPrice").value = 0;
			}
			if(null == orderNum || orderNum ==0){
				document.getElementById("orderNum").value = 1;
				orderNum = 1;
			}
			var totalPrice = discountPrice * orderNum;
			document.getElementById("totalPrice").value = totalPrice;
		}

	</script>

	<script type="text/javascript">

		function change(){
			var discountPrice = document.getElementById("discountPrice").value;
			var orderNum = document.getElementById("orderNum").value;
			var totalPrice = discountPrice * orderNum;
			document.getElementById("totalPrice").value = totalPrice;
		}


		function save() {
			$('#modal_save').modal("show");
		};

		function upload(){
			var fileReader = new FileReader();
			var formData = new FormData();
			var file = document.getElementById('fileName').files[0];
			formData.append("file", file);
			$.ajax({
				type: "POST",
				url: "<%=basePath%>normal/uploadimg!execute.action?random="
						+ Math.random(),
				data: formData,
				dataType: "json",
				contentType: false,
				processData: false,
				success : function(data) {
					console.log(data);
					$("#iconImg").val(data.data)
					var show_img = document.getElementById('show_img');
					show_img.src="<%=basePath%>normal/showImg.action?imagePath="+data.data;

				},
				error : function(XMLHttpRequest, textStatus,
								 errorThrown) {
					console.log("请求错误");
				}
			});
		}

	</script>
</body>

</html>
