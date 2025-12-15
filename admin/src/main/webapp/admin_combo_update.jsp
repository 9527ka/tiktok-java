<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
	<style>
		.sweet-alert{
			top:20%!important;
		}
	</style>
	<div class="ifr-dody">



		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<ul class="nav nav-tabs">
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=en&comboId=${comboId}">英文</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=vi&comboId=${comboId}">越南语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=hi&comboId=${comboId}">印度语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=id&comboId=${comboId}">印度尼西亚语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=de&comboId=${comboId}">德语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=fr&comboId=${comboId}">法语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=ru&comboId=${comboId}">俄语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=es&comboId=${comboId}">西班牙语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=pt&comboId=${comboId}">葡萄牙语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=it&comboId=${comboId}">意大利语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=ms&comboId=${comboId}">马来西亚语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=af&comboId=${comboId}">南非荷兰语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=el&comboId=${comboId}">希腊语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=tw&comboId=${comboId}">中文繁体</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=cn&comboId=${comboId}">中文简体</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=tr&comboId=${comboId}">土耳其语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=ja&comboId=${comboId}">日语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=ko&comboId=${comboId}">韩语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=th&comboId=${comboId}">泰语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=ph&comboId=${comboId}">菲律宾语</a></li>
				<li><a href="<%=bases%>/mall/combo/toUpdate.action?lang=ar&comboId=${comboId}">阿拉伯语</a></li>
			</ul>
			<h3>店铺直通车</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form  action="<%=basePath%>mall/combo/list.action"
				method="post" id="queryForm">
				<input type="hidden" name="pageNo" id="pageNo"/>
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							编辑店铺直通车
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal"
								action="<%=basePath%>mall/combo/update.action"
								method="post" name="mainForm" id="mainForm">
								<input type="hidden" name="id" id="id" value = "${id}"/>
				                <input type="hidden" name="comboId" id="comboId" value = "${comboId}"/>
								<input type="hidden" name="iconImg" id="iconImg" value = "${iconImg}"/>
				                <input type="hidden" name="comboLanId" id="comboLanId" value = "${comboLanId}"/>
				                <input type="hidden" name="lang" id="lang" value = "${lang}"/>
				                <input type="hidden" name="pageNo" id="pageNo" value = "${pageNo}"/>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">封面图(*)</label>

									<div class="col-sm-3" style="display: flex;">
										<input type="file" id="fileName" name="fileName"  value="${fileName}" onchange="upload();" multiple="multiple"  style="position:absolute;opacity:0;">
										<label for="fileName">　　
											　　　　　　
											<div class="avatar">
												<img width="90px" height="90px" id="show_img" src="<%=base%>/image/add.png" /> 　
											</div>
											　　
										</label> 　　
										<div style="float: left;width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label" style="color: red">套餐名称</label>
									<div class="col-sm-3" style="display: flex;">
										<input id="name" name="name" class="form-control" value="${name}" placeholder="请输入套餐名称" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label" >可推广产品数</label>
									<div class="col-sm-3" style="display: flex;">
										<input id="promoteNum" name="promoteNum" class="form-control" value="${promoteNum}" placeholder="请输入可推广产品数" oninput="value=value.replace(/[^\d]/g,'')"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label" style="color: red">简介</label>
									<div class="col-sm-3" style="display: flex;">
										<input id="content" name="content" class="form-control" value="${content}" placeholder="请输入简介" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">价格</label>
									<div class="col-sm-3" style="display: flex;">
										<input id="amount" name="amount" class="form-control" value="${amount}" placeholder="请输入价格 单位：美元" /><span style="width: 10%">美元</span>

									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">有效期</label>
									<div class="col-sm-3" style="display: flex;">
										<input id="day" name="day" class="form-control" value="${day}" placeholder="请输入有效期 单位：天"  oninput="value=value.replace(/[^\d]/g,'')"/>
										<span style="width: 10%">天</span>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">每小时最小流量</label>
									<div class="col-sm-3" style="display: flex;">
										<input id="baseAccessNum" name="baseAccessNum" class="form-control" value="${baseAccessNum}" placeholder="请输入基础访问量"  oninput="value=value.replace(/[^\d]/g,'')"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">每小时流量波动范围</label>
									<div class="col-sm-5">
										<input id="autoAccMin" style="width: 193px; float: left; margin-right: 10px;" name="autoAccMin" class="form-control "  value="${autoAccMin}" oninput="value=value.replace(/[^\d]/g,'')" />
										<span style="margin-top: 5px;float: left;margin-right: 10px;">-</span>
										<input id="autoAccMax"  style="width: 192px;float: left;" name="autoAccMax" class="form-control "
											    value="${autoAccMax}" oninput="value=value.replace(/[^\d]/g,'')"/>

									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">预计浏览量</label>
									<div class="col-sm-3">
										<input id="estimatedVisits" name="estimatedVisits" class="form-control" readonly = true value="${(baseAccessNum + 1 + autoAccMin ) * 24} ~ ${(baseAccessNum + 1 + autoAccMax) * 24}"/>
									</div>
								</div>

<%--								<div class="col-sm-1">--%>
<%--									<!-- 模态框（Modal） -->--%>
<%--									<div class="modal fade" id="modal_succeeded" tabindex="-1"--%>
<%--										role="dialog" aria-labelledby="myModalLabel"--%>
<%--										aria-hidden="true">--%>
<%--										<div class="modal-dialog">--%>
<%--											<div class="modal-content" style="width: 350px;">--%>
<%--												<div class="modal-header">--%>
<%--													<button type="button" class="close"--%>
<%--														data-dismiss="modal" aria-hidden="true">&times;</button>--%>
<%--													<h4 class="modal-title" id="myModalLabel">登录人资金密码</h4>--%>
<%--												</div>--%>
<%--												<div class="modal-body">--%>
<%--													<div class="" >--%>
<%--														<input id="login_safeword" type="password" name="login_safeword"--%>
<%--															class="login_safeword" placeholder="请输入登录人资金密码" style="width: 250px;">--%>
<%--													</div>--%>
<%--												</div>--%>
<%--												<div class="modal-footer" style="margin-top: 0;">--%>
<%--													<button type="button" class="btn "--%>
<%--														data-dismiss="modal">关闭</button>--%>
<%--													<button id="sub" type="submit"--%>
<%--														class="btn btn-default" >确认</button>--%>
<%--												</div>--%>
<%--											</div>--%>
<%--											<!-- /.modal-content -->--%>
<%--										</div>--%>
<%--										<!-- /.modal -->--%>
<%--									</div>--%>
<%--								</div>--%>
<%--								--%>
								
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

		// function submit() {
		// 	$('#modal_succeeded').modal("show");
		// }

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
		//初始化执行一次
		setTimeout(function() {
			start();
		}, 100);

		function start(){
			var img = $("#iconImg").val();
			var show_img = document.getElementById('show_img');
			show_img.src=img;
			console.log(img)
		}



		function upload(){
			var c = document.getElementById('fileName');
			var fileReader = new FileReader();
			var formData = new FormData();
			var file = document.getElementById('fileName').files[0];
			formData.append("file", file);
			formData.append("moduleName","type");
			$.ajax({
				type: "POST",
				url: "<%=basePath%>normal/uploadimg!execute.action",
				data: formData,
				dataType: "json",
				contentType: false,
				processData: false,
				success : function(data) {
					console.log(data);
					$("#iconImg").val(data.data)
					var show_img = document.getElementById('show_img');
					show_img.src=data.data;

				},
				error : function(XMLHttpRequest, textStatus,
								 errorThrown) {
					console.log("请求错误");
				}
			});

		}

		$(function(){
			$('.nav-tabs a').filter(function() {
				var b = document.URL;
				var a = "<%=bases%>/mall/combo/toUpdate.action?lang=${lang}&comboId=${comboId}";
				return this.href == "<%=bases%>/mall/combo/toUpdate.action?lang=${lang}&comboId=${comboId}";  //获取当前页面的地址
			}).closest('li').addClass('active');  //给当前最靠近的li（其实是现在进入的li）添加‘active’样式

		})

	</script>




</body>
</html>