<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

`<%@ include file="include/pagetop.jsp"%>`
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
				<li><a href="<%=bases%>/invest/project/toUpdate.action?lang=cn&projectId=${projectId}">中文</a></li>
				<li><a href="<%=bases%>/invest/project/toUpdate.action?lang=en&projectId=${projectId}">英文</a></li>
				<li><a href="<%=bases%>/invest/project/toUpdate.action?lang=tw&projectId=${projectId}">繁体中文</a></li>
			</ul>
			<h3>项目管理</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<form  action="<%=basePath%>invest/project/list.action"
				method="post" id="queryForm">
				<input type="hidden" name="pageNo" id="pageNo"/>
			</form>
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							编辑项目
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
							<form class="form-horizontal"
								  style="position: relative;"
								action="<%=basePath%>invest/project/update.action"
								method="post" name="mainForm" id="mainForm">
<%--								<div style="position: absolute;left:900px;top: 90px;padding: 40px 80px;border-radius: 5%;background: #B9F98E;color: #0A451D;">黑色标题属于公共部分</div>--%>
								<input type="hidden" name="id" id="id" value = "${project.id}"/>
				                <input type="hidden" name="iconImg" id="iconImg" value = "${project.iconImg}"/>
				                <input type="hidden" name="projectId" id="projectId" value = "${projectId}"/>
				                <input type="hidden" name="lang" id="lang" value = "${lang}"/>
				                <input type="hidden" name="projectLanId" id="projectLanId" value = "${projectLanId}"/>
				                <input type="hidden" name="pageNo" id="pageNo" value = "${pageNo}"/>
				                <input type="hidden" name="ending" id="ending" value = "${project.ending}"/>
				                <input type="hidden" name="recTime" id="recTime" value = "${project.recTime}"/>


								<div class="form-group">
									<label class="col-sm-2 control-label form-label">平台分类</label>
									<div class="col-sm-3 " style = width:800px;>
										<div class="input-group">

											<select id="baseId" name="baseId"
													class="form-control ">
												<c:forEach var = "item" items = "${categoryList}">
													<option value = "${item.key}"> ${item.value} </option>>
												</c:forEach>
											</select>

										</div>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label" style="color: red">项目名称</label>
									<div class="col-sm-3">
										<input id="name" name="name" class="form-control" value="${name}" placeholder="请输入商品名称"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">分红比例</label>
									<div class="col-sm-3">
										<input id="bonusRate" name="bonusRate" class="form-control" value="${project.bonusRate}" placeholder="请输入分红比例" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')"/>
									</div>
									<span style="float: left;margin-left: 5px;margin-top: 5px;">%</span>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">排序</label>
									<div class="col-sm-3">
										<input id="sort" name="sort" class="form-control" value="${project.sort}" placeholder="数字越小越靠前，数字可重复" oninput="value=value.replace(/[^\d]/g,'')"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">封面图(*)</label>

									<div class="col-sm-3">
										<input type="file" id="fileName" name="fileName"  value="${fileName}" onchange="upload();"  style="position:absolute;opacity:0;">
										<label for="fileName">　　
　　　　　　
　　　　									<img width="90px" height="90px" id="show_img" src="<%=base%>/image/add.png"  alt="点击上传图片" /> 　　
　　
　										　</label> 　　
										<div style="float: left;width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</div>

									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label" style="color: red">担保机构</label>
									<div class="col-sm-3">
										<input id="guarantyAgency" name="guarantyAgency" class="form-control" value="${guarantyAgency}" placeholder="请输入担保机构" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">项目规模</label>
									<div class="col-sm-3">
										<input id="investSize" name="investSize" class="form-control" value="${project.investSize}" placeholder="请输入正整数，保留小数点两位" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">投资进度</label>
									<div class="col-sm-3">
										<input id="investProgressMan" name="investProgressMan" class="form-control" value="${project.investProgressMan}" placeholder="请输入0-100的数字" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')"/>
									</div>
									<span style="float: left;margin-left: 5px;margin-top: 5px;">%</span>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">已售增量</label>
									<div class="col-sm-3">
										<input id="investSellAdd" name="investSellAdd" class="form-control" value="${project.investSellAdd}" placeholder="请输入已售增量" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')"/>
										<div style="float: left;width: 100%;margin-top: 5px;">用户更新项目进度增量系数</div>
									</div>
									<span style="float: left;margin-left: 5px;margin-top: 5px;">%</span>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">起投金额</label>
									<div class="col-sm-3">
										<input id="investMin" name="investMin" class="form-control" value="${project.investMin}" placeholder="起投金额" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">最大投资</label>
									<div class="col-sm-3">
										<input id="investMax" name="investMax" class="form-control" value="${project.investMax}" placeholder="最大投资" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')"/>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">投资方式</label>
									<div class="col-sm-3 ">
										<div class="input-group">
											<select id="type" name="type"
													class="form-control" oninput="change()">
												<option value="1" <c:if test="${project.type == '1'}">selected="true"</c:if>>按小时付收益，每小时结息，到期返本</option>
												<option value="2" <c:if test="${project.type == '2'}">selected="true"</c:if>>按小时算收益，到期返本金+分红</option>
												<option value="3" <c:if test="${project.type == '3'}">selected="true"</c:if>>按天付收益，每个自然日结息，到期返本</option>
												<option value="4" <c:if test="${project.type == '4'}">selected="true"</c:if>>按天算收益，到期返本+分红</option>
											</select>
										</div>
									</div>
								</div>
<%--								<p class="ballon color1">1、投资方式如果按小时付收益，到期返本 或 按小时算收益，到期返本金  锁仓期限单位：小时--%>
<%--									<br/>--%>
<%--									2、投资方式如果按天付收益，到期返本 或 按天算收益，到期返本金+分红 锁仓期限单位：天--%>
<%--									<br/>--%>
<%--								</p>--%>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">锁仓期限</label>
									<div class="col-sm-3">
										<input id="bonus" name="bonus" class="form-control" value="${project.bonus}" placeholder="请输入项目期限" oninput="value=value.replace(/[^\d]/g,'')"/>
									</div>
									<span id="suoqiang-qixian-time" style="float: left;margin-left: 5px;margin-top: 5px;">${dstime}</span>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">积分赠送比例</label>
									<div class="col-sm-3">
										<input id="pointRate" name="pointRate" class="form-control" value="${project.pointRate}" placeholder="积分赠送比例" onkeyup="this.value=this.value.replace(/[^\d.]/g, '').replace(/\.{2,}/g, '.').replace('.', '$#$').replace(/\./g, '').replace('$#$', '.').replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3').replace(/^\./g, '')"/>
									</div>
									<span style="float: left;margin-left: 5px;margin-top: 5px;">%</span>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">是否启用</label>
									<div class="col-sm-3 ">
										<div class="input-group">
											<select id="status" name="status"
													class="form-control ">
												<option value="0" <c:if test="${project.status == '0'}">selected="true"</c:if>>启用</option>
												<option value="1" <c:if test="${project.status == '1'}">selected="true"</c:if>>禁用</option>
											</select>
										</div>
									</div>
								</div>
<%--								<div class="form-group">--%>
<%--									<label class="col-sm-2 control-label form-label">首页推荐</label>--%>
<%--									<div class="col-sm-3 ">--%>
<%--										<div class="input-group">--%>
<%--											<select id="recTime" name="recTime"--%>
<%--													class="form-control ">--%>
<%--												<option value="0" <c:if test="${project.recTime == '0'}">selected="true"</c:if>>是</option>--%>
<%--												<option value="1" <c:if test="${project.recTime == '1'}">selected="true"</c:if>>否</option>--%>
<%--											</select>--%>
<%--										</div>--%>
<%--									</div>--%>
<%--								</div>--%>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">是否重投</label>
									<div class="col-sm-3 ">
										<div class="input-group">
											<select id="repeating" name="repeating"
													class="form-control ">
												<option value="true" <c:if test="${project.repeating == 'true'}">selected="true"</c:if>>可以重复投资</option>
												<option value="false" <c:if test="${project.repeating == 'false'}">selected="true"</c:if>>不可以重复投资</option>
											</select>
										</div>
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-2 control-label form-label" style="color: red">结算时间</label>
									<div class="col-sm-4">
										<textarea class="form-control" rows="7" id="desSettle_text" name="desSettle_text" placeholder="请输入内容">${desSettle}</textarea>
										<input type="hidden" name="desSettle" id="desSettle" value="${desSettle}" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label" style="color: red">资金用途</label>
									<div class="col-sm-4">
										<textarea class="form-control" rows="7" id="desUse_text" name="desUse_text" placeholder="请输入内容">${desUse}</textarea>
										<input type="hidden" name="desUse" id="desUse" value="${desUse}" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label" style="color: red">安全保障</label>
									<div class="col-sm-4">
										<textarea class="form-control" rows="7" id="desSafe_text" name="desSafe_text" placeholder="请输入内容">${desSafe}</textarea>
										<input type="hidden" name="desSafe" id="desSafe" value="${desSafe}" />
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


		window.onload = function (){
			var dstime;
			let type = document.getElementById("type").value;
			if(type == 1 || type == 2){
				dstime = "小时";
			} else {
				dstime = "天";
			}
			// document.getElementById("dstime").value = dstime;
			//suoqiang-qixian-time
			var y =  document.getElementById("suoqiang-qixian-time");
			y.innerHTML = dstime
		}

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
			show_img.src="<%=basePath%>normal/showImg.action?imagePath="+img;
		}



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


		$(function(){
			$('.nav-tabs a').filter(function() {
				var a = document.URL;
				<%--var b = "<%=basePath%>invest/project/toUpdate.action?lang=${lang}&projectId=${projectId}"--%>
				return this.href == "<%=bases%>/invest/project/toUpdate.action?lang=${lang}&projectId=${projectId}";  //获取当前页面的地址
			}).closest('li').addClass('active');  //给当前最靠近的li（其实是现在进入的li）添加‘active’样式

		})

	</script>


	<script type="text/javascript">

		function change(){
			var dstime;
			let type = document.getElementById("type").value;
			if(type == 1 || type == 2){
				dstime = "小时";
			} else {
				dstime = "天";
			}
			var y =  document.getElementById("suoqiang-qixian-time");
			y.innerHTML = dstime;
		}

	</script>

</body>
</html>