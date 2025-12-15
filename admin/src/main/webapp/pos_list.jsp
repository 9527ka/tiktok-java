<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

<head>
	<%@ include file="include/head.jsp"%>
</head>

<body>

<%@ include file="include/loading.jsp"%>

<style>
	.sweet-alert{
		top: 25%;
	}
	.productDialog {
		display: flex;
		/*align-items: center;*/
	}
	.productDialog .left {
		width: 50%;
		margin-right: 10px;
	}
	.productDialog .right {
		width: 50%;
	}

	.disabled {
		background-color: #f2f2f2 !important;
		color: #999 !important;
	}
	::-webkit-scrollbar-track {
		background-color: #F5F5F5;
	}

	::-webkit-scrollbar {
		width: 6px;
		background-color: #F5F5F5;
	}

	::-webkit-scrollbar-thumb {
		background-color: #999;
	}
	.productDialogBodyBox {
		padding: 20px;
		border: 1px solid #ccc;
		height: 565px;
		overflow: auto;
	}
	.productDialogBody {
		display: flex;
		padding: 20px;
		border: 1px solid #ccc;
	}
	.productDialogBody .productRight {
		display: flex;
		flex-direction: column;
	}
	.productDialogBody .productImg {
		margin-right: 10px;
	}
	.productDialogBody .productImg img {
		width: 80px;
		height: 80px;
	}

	.productDialogBody .productName {
		font-size: 14px;
		margin-bottom: 10px;

	}
	.productDialogBody .productBox {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	.productDialogBody .dialogPrice {
		display: flex;
	}
	.productDialogBody .productNumberBox {
		display: flex;
		align-items: center;
		justify-content: end;
	}
	.productDialogBody .productNumberBox input {
		width: 60%;
		height: 20px;
	}
	.customerDialogBody {
		padding: 20px;
	}
	.customerDialogBody .customerItem {
		margin-bottom: 10px;
		font-size: 14px;
	}
	.customerSelect {
		-webkit-appearance: none;
		background-color: #fff;
		background-image: none;
		border-radius: 4px;
		border: 1px solid #dcdfe6;
		box-sizing: border-box;
		color: #606266;
		display: inline-block;
		font-size: inherit;
		height: 30px;
		line-height: 30px;
		outline: none;
		padding: 0 15px;
		transition: border-color .2s cubic-bezier(.645,.045,.355,1);
		width: 100%;
	}
	.customerItem input {
		-webkit-appearance: none;
		background-color: #fff;
		background-image: none;
		border-radius: 4px;
		border: 1px solid #dcdfe6;
		box-sizing: border-box;
		color: #606266;
		display: inline-block;
		font-size: inherit;
		height: 30px;
		line-height: 30px;
		outline: none;
		padding: 0 15px;
		transition: border-color .2s cubic-bezier(.645,.045,.355,1);
		width: 100%;
	}
	.customerItem textarea {
		-webkit-appearance: none;
		background-color: #fff;
		background-image: none;
		border-radius: 4px;
		border: 1px solid #dcdfe6;
		box-sizing: border-box;
		color: #606266;
		display: inline-block;
		font-size: inherit;
		outline: none;
		padding: 0 15px;
		transition: border-color .2s cubic-bezier(.645,.045,.355,1);
		width: 100%;
	}
	#timePicker {
		width: 200px;
		margin: 0 20px;
	}

	.modalProductFooter {
		display: flex;
		padding: 0 20px;
	}
	.modalProductFooter .methodText {
		margin-right: 10px;
		/*width: -webkit-fill-available;*/
	}
	.modalProductFooter .method {
		display: flex;
		align-items: center;
	}
	.modal-header {
		display: flex;
		justify-content: space-between;
	}
</style>
<body>

<div class="loading"><img src="<%=basePath%>img/loading.gif" alt="loading-img"></div>

<div class="ifr-dody">

	<input type="hidden" name="session_token" id="session_token" value="${session_token}"/>
	<input type="hidden" name="createUser" id="createUser" value="${security.getUsername_login()}"/>

	<div class="ifr-con">
		<h3>POS下单</h3>

		<%@ include file="include/alert.jsp"%>

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START queryForm -->
		<%@ include file="include/alert.jsp"%>
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-default">

					<div class="panel-title">查询条件</div>

					<div class="panel-body">

						<form class="form-horizontal" action="<%=basePath%>mall/pos/list.action" method="post"
							  id="queryForm">
							<input type="hidden" name="pageNo" id="pageNo"
								   value="1">
							<input type="hidden" name="messages" id="messages"
								   value="">
							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="goodsName" name="goodsName" class="form-control"
												   placeholder="商品名称" value = "${goodsName}"/>
										</div>
									</div>
								</fieldset>
							</div>
							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="goodsId" name="goodsId" class="form-control"
												   placeholder="商品ID" value = "${goodsId}"/>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="sellerId" name="sellerId" class="form-control"
												   placeholder="店铺ID" value = "${sellerId}"/>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2">
								<fieldset>
									<div class="control-group">
										<div class="controls">
											<input id="sellerName" name="sellerName" class="form-control"
												   placeholder="店铺名称" value = "${sellerName}"/>
										</div>
									</div>
								</fieldset>
							</div>

							<div class="col-md-12 col-lg-2">
								<button type="submit" class="btn btn-light btn-block">查询</button>
							</div>

						</form>

					</div>

				</div>
			</div>
		</div>
		<!-- END queryForm -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->

		<div class="row">
			<div class="col-md-12">
				<!-- Start Panel -->
				<div class="panel panel-default">

					<div class="panel-title">查询结果</div>
					<a  id="shelfBatch" class="btn btn-light" style="margin-bottom: 12px"><i
							class="fa fa-pencil"></i>批量下单</a>

					<div class="panel-body">

						<table class="table table-bordered table-striped">

							<thead>
							<tr>
								<td>
									<input id="selAll" type="checkbox" />
								</td>
								<td>ID</td>
								<td>店铺ID</td>
								<td>商品ID</td>
								<td>店铺名称</td>
								<td>商品名称</td>
								<td>商品图</td>
								<td>销售价格</td>
								<td>操作</td>
							</tr>
							</thead>

							<tbody>
							<input type="hidden" name="iconImg" id="iconImg" value = ""/>
							<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
								<tr>
									<td style="width: 50px;align-content: center;">
										<input name="checkbox" type="checkbox" value="${item.id}">
									</td>
									<td style="width: 160px; align-content: center;">${item.id}</td>
									<td style="width: 160px; align-content: center;">${item.sellerId}</td>
									<td style="width: 160px; align-content: center;">${item.goodsId}</td>
									<td style="width: 160px; align-content: center;">${item.sellerName}</td>
									<td style="width: 320px; align-content: center;">${item.goodsName}</td>
									<td style="width: 120px;text-align: center; align-content: center;">
										<img src="${item.imgUrl}" width="120" height="120">
									</td>

									<td style="width: 160px; align-content: center;">${item.sellingPrice}</td>
									<td style="width: 160px; align-content: center;text-align: center;">

										<button type="submit" class="btn btn-success selectGoods">下单</button>

									</td>

								</tr>
							</c:forEach>
							</tbody>

						</table>

						<%@ include file="include/page_simple.jsp"%>

						<!-- <nav> -->
					</div>

				</div>
				<!-- End Panel -->

			</div>
		</div>
		<!-- 模态框 -->
		<div class="modal" id="myModalProduct">
			<div class="modal-dialog" style="width: 994px">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title">确认订单</h4>
						<button type="button" class="close" data-dismiss="modal">&times;</button>
					</div>
					<div class="modal-body productDialog">
						<div class="left">
							商品信息
							<div class="productDialogBodyBox">
							</div>

						</div>
						<div class="right">
							顾客信息
							<div class="customerDialogBody">
								<div class="customerItem">
									<select name="guestUsers" id="guestUsers" placeholder="请选择" class="customerSelect">
										<option value="0">请选择下单账号</option>
									</select>
								</div>

								<div class="customerItem addressLibrary" style="display: none">
									<select name="address" id="addressLibrary" placeholder="请选择" class="customerSelect">
										<option value="0">请选择下单地址</option>
									</select>
								</div>


								<div class="customerItem">
									姓名
									<input id="username" class="form-control" type="text" disabled>
								</div>
								<div class="customerItem">
									电子邮件
									<input id="email" class="form-control" type="text" disabled>
								</div>
								<div class="customerItem">
									手机号
									<input id="mobile" class="form-control" type="text" disabled>
								</div>
								<div class="customerItem">
									国家
									<input id="country" class="form-control" type="text" disabled>
								</div>
								<div class="customerItem">
									州
									<input class="form-control" id="province" type="text" disabled>
								</div>
								<div class="customerItem">
									城市
									<input class="form-control" id="city" type="text" disabled>
								</div>
								<div class="customerItem">
									邮政编码
									<input class="form-control" id="zipcode" type="number" disabled>
								</div>
								<div class="customerItem">
									地址
									<textarea class="form-control" id="homeaddress" cols="25" rows="4" disabled></textarea>
								</div>
							</div>
						</div>
					</div>
					<input type="hidden" id="partyId">
					<input type="hidden" id="addressId">
					<div class="modalProductFooter">
						<div class="method">
							<div class="methodText">下单方式: </div>
							<div >
								<select name="orderMode" id="orderMode" placeholder="请选择" class="customerSelect" style="width: 150px;">
									<option value="1" selected>实时</option>
									<option value="2">定时</option>
								</select>
							</div>

							<div class="methodText" style="margin-left: 20px;">下单时间: </div>
							<div>
								<input type="datetime-local" id="datePicker" class="customerSelect"  style="margin: 0 20px; ">
							</div>

							<div class="methodText" style="margin-left: 20px;">订单数量: </div>
							<div>
								<input class="form-control" id="orderCount" type="number" disabled>
							</div>

						</div>
					</div>

					<div class="modal-footer">
						<button id="submitOrder" type="button" class="btn btn-primary" >确认下单</button>
					</div>
				</div>
			</div>
		</div>



	<%@ include file="include/footer.jsp"%>

</div>
<!-- End Content -->
<!-- //////////////////////////////////////////////////////////////////////////// -->




<%@ include file="include/js.jsp"%>

<script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
<script>
</script>


<script type="text/javascript">
	function initializeGuestUsers() {
		// 发送AJAX请求获取顾客列表
		$.ajax({
			url: "<%=basePath%>mall/pos/userList.action", // 服务器端提供的获取顾客列表的URL
			type: 'GET',
			dataType: 'json', // 期望服务器返回JSON格式的数据
			success: function (data) {
				if (data.code == '0' && data.data) {
					// 清空下拉菜单除了第一个选项以外的所有选项
					$('#guestUsers').find('option').not(':first').remove();

					// 遍历顾客数据并添加到下拉菜单中
					data.data.forEach(function(guestUser) {
						var option = $('<option>')
								.text(guestUser.contacts)
								.val(guestUser.partyId);
						$('#guestUsers').append(option);
					});
				} else {
					// 处理错误情况，例如显示消息提示用户
					console.error("Failed to load guest users:", data.message);
				}
			},
			error: function (xhr, status, error) {
				// 处理AJAX请求失败的情况
				console.error("AJAX request failed:", status, error);
			}
		});
	}

	// 在页面加载完成后调用初始化函数
	$(document).ready(function() {
		initializeGuestUsers();
	});

	function initParams() {
		$('#username').val('');
		$('#email').val('');
		$('#country').val('');
		$('#province').val('');
		$('#zipcode').val('');
		$('#homeaddress').val('');
		$('#mobile').val('');
		$('#city').val('');
		$('#partyId').val('');
		$('#addressId').val('');
	}
	$('#addressLibrary').change(function(){
		var selectedAddress = $(this).val();
		if (selectedAddress == 0){
			initParams();
			return;
		}
		$.ajax({
			url: "<%=basePath%>mall/pos/address_info.action?id=" + selectedAddress,
			type: 'GET',
			success: function (data) {
				if (data.code == '0' && data.data) {
					var info = data.data;
					$('#username').val(info.contacts);
					$('#email').val(info.email);
					$('#country').val(info.country);
					$('#province').val(info.province);
					$('#zipcode').val(info.postcode);
					$('#homeaddress').val(info.address);
					$('#mobile').val(info.phone);
					$('#city').val(info.city);
					$('#partyId').val(info.partyId);
					$('#addressId').val(info.id);
				}else{
					errorMsg("获取失败,请重试");
					return;
				}
			}
		});
	});

	$('#guestUsers').change(function(){
		var partyId = $(this).val();
		var firstOption = $('#addressLibrary option:first');
		$('#addressLibrary').find('option').not(':first').remove();
		$('#addressLibrary').append(firstOption);
		if (partyId == 0){
			$('.addressLibrary').hide();
			initParams();
			return;
		}else{
			$.ajax({
				url: "<%=basePath%>mall/pos/address.action?partyId=" + partyId,
				type: 'GET',
				success: function (data) {
					if (data.code == '0'){
						data.data.forEach(function(element) {
							var newOption = $('<option>').text(element.contacts + '-' +element.country +  '-' + element.phone).val(element.id);
							$('#addressLibrary').append(newOption);
						});
						$('.addressLibrary').show();
						if(data.data.length > 0){
						    $('#addressLibrary').val(data.data[0].id);
						    $('#addressLibrary').change();
						}
					}else{
						errorMsg("该账号下最少要有一个收货地址");
						return;
					}

				}
			});
		}
	});



	$('.selectGoods').on('click', function () {
		initParams();
		var $tr = $(this).closest('tr')
		var itemID = $tr.find('td:nth-child(2)').html()
		var itemName = $tr.find('td:nth-child(6)').html()
		var sellingPrice = $tr.find('td:nth-child(8)').html()
		var imageUrl = $tr.find('td:nth-child(7) img').attr('src')
		var selectedItems = [{productName: itemName, price: sellingPrice, imageUrl,itemID}]
		renderProduct(selectedItems)
	})

	//提交订单
	$('#submitOrder').on('click', function () {
		var ShopingCart = getShopingCart();
		console.log(ShopingCart)
		if (!ShopingCart){
			errorMsg("商品数量有误");
			return;
		}
		var username = $('#username').val();
		var email = $('#email').val();
		var country = $('#country').val();
		var province = $('#province').val();
		var zipcode = $('#zipcode').val();
		var homeaddress = $('#homeaddress').val();
		var orderMode = $('#orderMode').val();
		var datePicker = $('#datePicker').val();
		var orderCount = $('#orderCount').val();
		var mobile = $('#mobile').val();
		var city = $('#city').val();
		var addressId = $('#addressId').val();
		var partyId = $('#partyId').val();
		var createUser = $('#createUser').val();
		if(!username || username.trim() === '') {
			errorMsg("请输入收货人姓名");
			return;
		}

		if(!email || email.trim() === '') {
			errorMsg("请输入邮箱");
			return;
		}

		if(!mobile || mobile.trim() === '') {
			errorMsg("请输入收货人手机号");
			return;
		}

		if (!validateEmail(email)){
			errorMsg("邮箱不格式不正确");
			return;
		}

		if(!country || country.trim() === '') {
			errorMsg("请输入国家");
			return;
		}

		if(!province || province.trim() === '') {
			errorMsg("请输入省份");
			return;
		}

		if(!city || city.trim() === '') {
			errorMsg("请输入城市");
			return;
		}

		if(!zipcode || zipcode === '') {
			errorMsg("请输入邮编");
			return;
		}

		var zipcodePattern = /^[0-9]+$/;

		if (!zipcodePattern.test(zipcode)){
			errorMsg("邮编格式不正确");
			return;
		}

		if(!homeaddress || homeaddress.trim() === '') {
			errorMsg("请输入地址");
			return;
		}
		if (orderMode == 2){
			if(!datePicker) {
				errorMsg("请设置下单时间");
				return;
			}
			if(!orderCount) {
				errorMsg("请设置下单数量");
				return;
			}
		}
		var formData = {
			partyId,
			addressId,
			orderMode,
			datePicker,
			orderCount,
			createUser,
			order: ShopingCart
		};
		var formDataJSON = JSON.stringify(formData);
		$.ajax({
			url: "<%=basePath%>mall/pos/create_task.action",
			type: 'POST',
			contentType: 'application/json',
			data: formDataJSON,
			success: function (data) {
				if (data.code == -1) {
					errorMsg(data.msg);
					return;
				}
				swal({
					title: '下单成功',
					text: "",
					type: "success",
					confirmButtonText: "确认",
				}, function() {
					// 关闭模态框的代码
					$('#myModalProduct').modal('hide');
					// 可能还需要重置一些表单数据或状态
					initParams();
				});

			}
		});


	});
	$('#shelfBatch').click(function () {

		var selectedItems = []
		$('input[name="checkbox"]:checked').each(function () {
			var $tr = $(this).closest('tr')
			var itemID = $tr.find('td:nth-child(2)').text()
			var itemName = $tr.find('td:nth-child(6)').text()
			var sellingPrice = $tr.find('td:nth-child(8)').text()
			var imageUrl = $tr.find('td:nth-child(7) img').attr('src')
			selectedItems.push({ productName: itemName, price: sellingPrice, imageUrl: imageUrl,itemID})
		})
		if (selectedItems.length == 0){
			errorMsg("最少选择一个商品");
			return false;
		}

		renderProduct(selectedItems)
	})
	function getShopingCart(){
		var isValid = [];
		$('.productDialogBody').each(function() {
			var dataId = $(this).data('id');
			var quantity = $(this).find('.productNumberBox').find('#num').val();
			var price = $(this).find('.dialogPrice').find('#sellingPrice').text();
			if (!quantity || quantity <= 0){
				isValid = false;
				return false;
			}
			if (!(/^\d+$/.test(quantity) && parseInt(quantity, 10) > 0)){
				isValid = false;
				return false;
			}
			const goods = { itemId: dataId, count: quantity,price:price };
			isValid.push(goods);
		});
		return isValid;
	}

	function validateEmail(email) {
		var pattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
		return pattern.test(email);
	}
	function errorMsg(msg){
		swal({
			title : msg,
			text : "",
			type : "warning",
			confirmButtonText : "确认",
		});
	}
	function renderProduct (selectedItems) {
		var modalContent = '';
		selectedItems.forEach(function(item) {
			modalContent += '<div class="productDialogBody" data-id="'+item.itemID+'">' +
					'<div class="productImg">' +
					'<img src="' + item.imageUrl + '" alt="' + item.productName + '">' +
					'</div>' +
					'<div class="productRight">' +
					'<div class="productName">' + item.productName + '</div>' +
					'<div class="productBox">' +
					'<div class="dialogPrice">$ <span id="sellingPrice">' + item.price + '</span> </div>' +
					'<div class="productNumberBox" >' +
					'× <input id="num" type="number" value="1">' +
					'</div>' +
					'</div>' +
					'</div>' +
					'</div>';
		});
		$('#myModalProduct .productDialogBodyBox').html(modalContent)
		$('#myModalProduct').modal('show')
	}



	$('#datePicker').prop('disabled', true).addClass('disabled');
	$('#orderCount').prop('disabled', true).addClass('disabled');
	// 监听选择框变化事件
	$('#orderMode').change(function() {
		console.log('$(this).val()', $(this).val())
		if ($(this).val() === '2') {
			$('#datePicker').prop('disabled', false).removeClass('disabled');
			$('#orderCount').prop('disabled', false).removeClass('disabled');
		} else {

			$('#datePicker').prop('disabled', true).addClass('disabled');
			$('#orderCount').prop('disabled', true).addClass('disabled');
		}
	});



	function toDelete(id,pageNo){
		$('#id').val(id);
		$('#pageNo').val(pageNo);
		$('#myModalLabel').html("删除");
		$('#mainform').attr("action","<%=basePath%>mall/goods/delete.action");

		$('#modal_succeeded').modal("show");

	}

	function addFakeComment(sellerId,creditScore){
		$("#sellerId1").val(sellerId);
		$("#NowCreditScore").val(creditScore);
		$('#modal_set2').modal("show");
	}


	function onsucceeded(id) {
		// var session_token = $("#session_token").val();
		// $("#session_token_success").val(session_token);
		$("#sellerGoodsId").val(id);
		$('#modal_set').modal("show");
	}

	$(function() {
		$('#startTime').datetimepicker({
			format : 'yyyy-mm-dd hh:ii:00',
			minuteStep:1,
			language : 'zh',
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 2,
			clearBtn : true
		});
		$('#endTime').datetimepicker({
			format : 'yyyy-mm-dd hh:ii:00',
			minuteStep:1,
			language : 'zh',
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 2,
			clearBtn : true
		});

	});



	$("#selAll").on("click", function(){
		var che=$("#selAll").prop('checked');
		if(che){
			$("input[name='checkbox']").prop('checked',true);
		} else {
			$("input[name='checkbox']").prop('checked',false);
		}
	})


	$("input[name='checkbox']").on("click", function(){
		var setFalse=false;// 默认不给全选按钮设置false
		$.each($("input[name='checkbox']"),function(index,item){
			// 如果在普通多选框的循环中发现有false,就需要将全选按钮设置为false
			if(item.checked==false){
				setFalse=true;
			}
		})
		if(setFalse){
			$("#selAll").prop('checked',false);
		} else {// 如果普通按钮都为true, 则全选按钮也赋值为true
			$("#selAll").prop('checked',true);
		}
	})

</script>

</body>

</html>
