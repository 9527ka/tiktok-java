<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-dody">

		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="ifr-con">
			<h3>交割合约配置</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- //////////////////////////////////////////////////////////////////////////// -->			
			<!-- START queryForm -->
			<form action="<%=basePath%>normal/adminContractManageAction!list.action"
				method="post" id="queryForm">
				
				<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}" />
				<input type="hidden" name="name_para" id="name_para" value="${name_para}" />
				<input type="hidden" name="rolename_para" id="rolename_para" value="${rolename_para}" />
				
			</form> 
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							<c:if test="${itemId != null}">
								修改合约产品
							</c:if>
							<c:if test="${itemId == null}">
								新增合约产品
							</c:if>
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
						
							<%-- <c:if test='isResourceAccessible("ADMIN_CONTRACT_MANAGE_ADDCONTRACTITEM")'> --%>
							
								<form class="form-horizontal" action="<%=basePath%>normal/adminContractManageAction!addContractItem.action"
									method="post" name="mainForm" id="mainForm">
									
									<input type="hidden" name="itemId" id="item_id" value="${itemId}" />
									
									<div class="form-group">
										<label class="col-sm-2 control-label form-label">合约名称</label>
										<div class="col-sm-2">
											<!-- <s:textfield id="name" name="name" cssClass="form-control " /> -->
											<input id="name" name="name" class="form-control " value="${name}" />
										</div>
									</div>
									
									<div class="form-group">
										<label class="col-sm-2 control-label form-label">代码</label>
										<div class="col-sm-2">
											<!-- <s:textfield id="symbol" name="symbol" cssClass="form-control " /> -->
											<input id="symbol" name="symbol" class="form-control " value="${symbol}" />
										</div>
									</div>
									
									<div class="form-group">
										<label class="col-sm-2 control-label form-label">保留精度</label>
										<div class="col-sm-2">
											<div class="input-group">
												<!-- <s:textfield id="decimals" name="decimals" cssClass="form-control " /> -->
												<input id="decimals" name="decimals" class="form-control " value="${decimals}" />
							                    <div class="input-group-addon">位</div>
						                    </div>
					                    </div>
				                    </div>
									
									<div class="form-group">
										<label class="col-sm-2 control-label form-label">交易对</label>
										<div class="col-sm-3">
											<!-- <s:textfield id="symbol_data" name="symbol_data" cssClass="form-control " readonly="true"  /> -->
											<input id="symbol_data" name="symbol_data" class="form-control " readonly="readonly" value="${symbol_data}" />
										</div>
										<a href="javascript:chooseSymbol('${id}')" class="btn btn-light" style="margin-bottom: 10px">交易对选择</a>
									</div>
									
									<div class="form-group">
										<div class="col-sm-offset-2 col-sm-10">
											<a href="javascript:goUrl(${pageNo})" class="btn">取消</a> 
											<a href="javascript:submit()" class="btn btn-default">保存</a>
										</div>
									</div>
	
								</form>
								
							<%-- </c:if> --%>

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

	<!-- 模态框 -->
	<div class="form-group">
	
		<input type="hidden" name="quote_currency" id="quote_currency" value="${quote_currency}" />
		<!-- <input type="hidden" name="base_currency" id="base_currency" value=""> -->
		
		<%-- <form action="<%=basePath%>normal/adminSymbolsAction!list.action"
			method="post" id="succeededForm">
			<input type="hidden" name="pageNo" id="pageNo"
				value="${param.pageNo}">
			<s:hidden name="id" id="id_reset"></s:hidden>
			<s:hidden name="name_para" id="name_para"></s:hidden>
			<s:hidden name="rolename_para" id="rolename_para"></s:hidden> --%>
			
		<div class="col-sm-2">
			<!-- 模态框（Modal） -->
			<div class="modal fade" id="modal_set" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content" style="height:500px;">
						
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">交易对</h4>
						</div>
						
						<%--<div class="modal-body">
							<div class="">
								<s:textfield id="money_revise" name="money_revise"
									cssClass="form-control " />
									<span  class="help-block">增加请输入正数，扣除请输入负数</span> 
						</div> --%>
						
						<div class="modal-body" id="symbols_get" style="height:380px;">
							<%@ include file="contract_manage_add_symbols_list.jsp"%>
						</div>
						
						<div class="modal-footer" style="margin-top: 0;">
							<button type="button" class="btn " data-dismiss="modal" >关闭</button>
							<button id="sub" type="submit" class="btn btn-default" onclick="modalConfirm(this)">确认</button>
						</div>
						
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal -->
			</div>
		</div>
	<!-- </form> -->
	
	</div>

	<%@ include file="include/js.jsp"%>

	<script type="text/javascript">
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
	</script>
	
	<script type="text/javascript">
		$(function(){
			var id = $('#item_id').val();
			if(typeof(id)!='undefined'&&id.length>0){
				$("#symbol").prop("readOnly",true);
				$("#symbol_data").prop("readOnly",true);
			}
		})
		function chooseSymbol(id) {
// 			$("#id_resetpsw").val(id);
			csPage(1);
			$('#modal_set').modal("show");
		}
	</script>
	
	<form action="<%=basePath%>normal/adminSymbolsAction!reload.action" method="post" id="reload">
	</form>
	
	<script type="text/javascript">
		function reload() {
			swal({
				title : "是否同步远程数据库?",
				text : "",
				type : "warning",
				showCancelButton : true,
				confirmButtonColor : "#DD6B55",
				confirmButtonText : "确认",
				closeOnConfirm : true
			}, function() {
				$.ajax({
					url:'<%=basePath%>normal/adminContractSymbolsAction!reload.action',
						type:'post',
						success: function (res) {
						    // 一旦设置的 dataType 选项，就不再关心 服务端 响应的 Content-Type 了
						    // 客户端会主观认为服务端返回的就是 JSON 格式的字符串
							// console.log(res)
							// $(".loading").hide();
						    $("#symbols_get").html(res);
						}
				});
				// document.getElementById("reload").submit();
			});		
		}
		function csPage(pageNo, quoteCurrency, baseCurrency) {
			var url = $("#csUrl").val();
			quoteCurrency = null==quoteCurrency||''==quoteCurrency||typeof(quoteCurrency) == "undefined"?$('#quote_currency').val():quoteCurrency;
			baseCurrency = null==baseCurrency||''==baseCurrency||typeof(baseCurrency) == "undefined"?$('#base_currency').val():baseCurrency;
			pageNo = Number(pageNo)<=0?1:pageNo;
			var data = {"pageNo":pageNo,"quote_currency":quoteCurrency,"base_currency":baseCurrency};
			goAjaxUrl(url,data);
			// $('#quote_currency').val(quoteCurrency);
			// $('#base_currency').val(baseCurrency);
		}
		function goAjaxUrl(targetUrl,data) {
			$.ajax({
				url:targetUrl,
				data:data,
				type:'get',
				success: function (res) {
				    // 一旦设置的 dataType 选项，就不再关心 服务端 响应的 Content-Type 了
				    // 客户端会主观认为服务端返回的就是 JSON 格式的字符串
					// console.log(res)
					// $(".loading").hide();
				    $("#symbols_get").html(res);
				    $('#quote_currency').val(data.quote_currency);
				    $('#base_currency').val(data.base_currency);						    
				    if(null==data.quote_currency||''==data.quote_currency||typeof(data.quote_currency) == "undefined"){
					    $('.tr_quote:first').attr('style','background:#39ffff;');
				    }else{
					    $('#tr_'+data.quote_currency).attr('style','background:#39ffff;');
				    }
				  }
			});
		} 
		function modalConfirm(e) {
			$("#symbol_data").val($(".symbolCheck:checked").val());
			$(e).prev().click();
		}
	</script>
	
</body>

</html>
