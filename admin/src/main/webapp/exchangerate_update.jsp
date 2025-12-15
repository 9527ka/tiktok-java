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

	<div class="ifr-dody">
		<div class="ifr-con">
			<h3>汇率配置</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<form action="<%=basePath%>normal/adminExchangeRateAction!list.action" method="post" id="queryForm">
				 <input type="hidden" name="pageNo" id="pageNo" value="${pageNo}"/>
			</form>
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							修改货币汇率
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminExchangeRateAction!update.action" method="post" name="mainForm" id="mainForm">
							
								<input type="hidden" name="id" id="id" value="${exchangeRate.id}"/>
								<input type="hidden" name="out_or_in" id="out_or_in" value="${exchangeRate.out_or_in}"/>
								<input type="hidden" name="iconImg" id="iconImg" value = "${exchangeRate.iconImg}"/>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">出售币种</label>
									<div class="col-sm-3">
										<input id="s" name="s" class="form-control " value="USDT" readonly="true"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">币种名称</label>
									<div class="col-sm-3">
										<input id="name" name="s" class="form-control " readonly="true" value="${exchangeRate.name} "/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">币种单位</label>
									<div class="col-sm-3">
										<input id="currency" name="currency" class="form-control "  readonly="true" value="${exchangeRate.currency}"/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">币种符号</label>
									<div class="col-sm-3">
										<input id="currency_symbol" name="currency_symbol" class="form-control " readonly="true" value="${exchangeRate.currency_symbol} "/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">国旗图(*)</label>

									<div class="col-sm-3">
										<input type="file" id="fileName" name="fileName"  value="${fileName}" onchange="upload();"  style="position:absolute;opacity:0;">
										<label for="fileName">　　
											　　　　　　
											　　　　									<img width="90px" height="90px" id="show_img"

																						 src="<%=base%>/image/add.png"  alt="点击上传图片" /> 　　
											　　
											　										　</label> 　　
										<div style="float: left;width: 100%;margin-top: 5px; color: red">图片尺寸：（ 750px * 750px ）</div>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">汇率价格</label>
									<div class="col-sm-3">
										<input id="rata" name="rata" class="form-control "  value="${exchangeRate.rata} "/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">序号</label>
									<div class="col-sm-3">
										<input id="sort" name="sort" class="form-control " value="${exchangeRate.sort} "/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">最小兑换</label>
									<div class="col-sm-3">
										<input id="excMin" name="excMin" class="form-control " value="${exchangeRate.excMin} "/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">最大兑换</label>
									<div class="col-sm-3">
										<input id="excMax" name="excMax" class="form-control " value="${exchangeRate.excMax} "/>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-2 control-label form-label">状态</label>
									<div class="col-sm-3 ">
										<div class="input-group">

											<select id="status" name="status"
													class="form-control ">
												<option value="0" <c:if test="${exchangeRate.status == '0'}">selected="true"</c:if>>启用</option>
												<option value="1" <c:if test="${exchangeRate.status == '1'}">selected="true"</c:if>>禁用</option>
											</select>

										</div>
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
	   function submit(){
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
	   }, 100)
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

	</script>
	
</body>

</html>
