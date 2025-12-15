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

	<div class="ifr-dody">

		<div class="ifr-con">
			<h3>交易杠杆</h3>
				<%@ include file="include/alert.jsp"%>
			<form action="<%=basePath%>normal/adminItemLeverageAction!list.action" method="post" id="queryForm">		  
				  <input type="hidden" name="pageNo" id="pageNo" />
				  <input type="hidden" name="itemid" id="itemid" />
			</form>
			
			<div class="row">
				<div class="col-md-12 col-lg-12">
					<div class="panel panel-default">

						<div class="panel-title">
							新增杠杆
							<ul class="panel-tools">
								<li><a class="icon minimise-tool"><i
										class="fa fa-minus"></i></a></li>
								<li><a class="icon expand-tool"><i class="fa fa-expand"></i></a></li>
							</ul>
						</div>

						<div class="panel-body">
								<form class="form-horizontal" action="<%=basePath%>normal/adminItemLeverageAction!add.action" method="post" name="mainForm" id="mainForm">
								<input type="hidden" name="itemid" id="itemid" value="${itemid}" />

								<div class="form-group">
									<label class="col-sm-2 control-label form-label">杠杆倍数</label>
									<div class="col-sm-3">
									<div class="input-group">
									<input id="leverage" name="leverage" class="form-control " value="${leverage}"/>
										</div>
									</div>
								</div>
							

								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-10">
										<a href="javascript:goUrl(${pageNo})" class="btn">取消</a> <a
											href="javascript:submit()"  class="btn btn-default">保存</a>
									</div>
								</div>

							</form>

						</div>

					</div>
				</div>
			</div>


		</div>

		<%@ include file="include/footer.jsp"%>


	</div>

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

	</script>
	
</body>
</html>