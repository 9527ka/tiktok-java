<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="include/head.jsp"%>
</head>
<body>
	<%@ include file="include/loading.jsp"%>
	<%@ include file="include/top.jsp"%>
	<%@ include file="include/menu_left.jsp"%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="content">



		<!-- //////////////////////////////////////////////////////////////////////////// -->
		<!-- START CONTAINER -->
		<div class="container-default">
			<h3>角色权限管理</h3>
			<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<!-- END queryForm -->
			<!-- //////////////////////////////////////////////////////////////////////////// -->

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">
						<div class="panel-title">查询结果</div>
						<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
						<a href="<%=basePath%>normal/adminRoleAuthorityManageAction!toAdd.action?username_para=<s:property value="username_para" />" class="btn btn-light" style="margin-bottom: 10px" ><i class="fa fa-pencil"></i>新增角色</a>
						</sec:authorize>
						<div class="panel-body">
							<table class="table table-bordered table-striped"  >
								<thead>
									<tr>
										<td>角色</td>
										<td>权限</td>
										<td width="150px"></td>
									</tr>
								</thead>
								<tbody>
									<s:iterator value="datas" status="stat">
										<tr>
											<td ><s:property value="roleName" /></td>
											<td ><s:property value="names" /></td>

											<td>
												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
													<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
															<li>
																<a href="javascript:updateResource('<s:property value="id" />')" >配置权限</a>
															</li>
															<li>
																<a href="javascript:del('<s:property value="id" />')" >删除</a>
															</li>
													</sec:authorize>
													</ul>
												</div>
											</td>

										</tr>
									</s:iterator>

								</tbody>
							</table>
							 <%@ include file="include/page_simple.jsp"%>
							<nav>
						</div>

					</div>
					<!-- End Panel -->

				</div>
			</div>

		</div>
		<!-- END CONTAINER -->
		<!-- //////////////////////////////////////////////////////////////////////////// -->


		<%@ include file="include/footer.jsp"%>


	</div>
	<!-- End Content -->
	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
<div class="form-group">
		<form
			action="<%=basePath%>normal/adminRoleAuthorityManageAction!update.action"
			method="post" id="mainform">
			<input type="hidden" name="pageNo" id="pageNo"
				value="${param.pageNo}">
			<input type="hidden" name="id" id="update_role_id"/>
		<div class="col-sm-1">
				<!-- 模态框（Modal） -->
				<div class="modal fade" id="resources_form" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel"
					aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close"
									data-dismiss="modal" aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="myModalLabel">配置权限</h4>
							</div>
							<div class="modal-body" style="max-height: 400px;overflow-y: scroll;">
							<table class="table table-bordered table-striped" >
								<thead>
									<tr>
										<td>权限</td>
									</tr>
								</thead>
								<tbody id="modal_table">
									
								</tbody>
							</table>
							</div>
							
							<div class="modal-footer" style="margin-top: 0;">
								<button type="button" class="btn "
									data-dismiss="modal">关闭</button>
									<button id="sub" type="submit" class="btn btn-default" >保存</button>
																			
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal -->
				</div>
			</div>
			</form>
	</div>
</sec:authorize>

	<%@ include file="include/js.jsp"%>
	
	
	<script type="text/javascript">
		function update_value(code,snotes,svalue){
			document.getElementById("change_value").value = svalue;
			document.getElementById("titlediv").innerText = snotes;
			 $("#code").val(code);
			$('#modal_set').modal("show");
			 
		}

	</script>
	<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
	<script>
	function del(id) {
		swal({
			title : "确认删除角色?",
			text : "",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "确认",
			closeOnConfirm : false
		}, function() {
			window.location.href = "<%=basePath%>normal/adminRoleAuthorityManageAction!delete.action?id="+id;
		});

	}
	</script>
	<script type="text/javascript">
	function updateResource(id){
		$("#resources_form").modal("show");
		$("#update_role_id").val(id);
		
		var url = "<%=basePath%>normal/adminRoleAuthorityManageAction!resources.action";
		var data = {"id":id};
		goAjaxUrl(url,data,function(tmp){
			var str='';
			var content='';
			tmp.all_resources
			console.log(tmp);
			for(var i=0;i<tmp.all_resources.length;i++){
				
				
				content = '<div class="checkbox checkbox-success checkbox-inline">'
	                	+ '<input type="checkbox" id="inlineCheckbox12_'+tmp.all_resources[i].set_id+'" value="'+tmp.all_resources[i].set_id+'" name="role_resource" >'
	                	+ '<label for="inlineCheckbox12_'+tmp.all_resources[i].set_id+'">'+tmp.all_resources[i].name+'</label>'
	            		+ '</div>';
				str += '<tr>'
					+'<td>'+content+'</td>'
					+'</tr>';
			}
			$("#modal_table").html(str);
			
			$.each(tmp.checked_resources.split(","),function(index,value){//默认选中
				$("#inlineCheckbox12_"+value).prop("checked","checked");
			});
		},function(){
// 			$("#coin_value").val(0);
		});
	}
	function goAjaxUrl(targetUrl,data,Func,Fail){
		console.log(data);
		$.ajax({
			url:targetUrl,
			data:data,
			type : 'get',
			dataType : "json",
			success: function (res) {
				var tmp = $.parseJSON(res)
				console.log(tmp);
			    if(tmp.code==200){
			    	Func(tmp);
			    }else if(tmp.code==500){
			    	Fail();
			    	swal({
						title : tmp.message,
						text : "",
						type : "warning",
						showCancelButton : true,
						confirmButtonColor : "#DD6B55",
						confirmButtonText : "确认",
						closeOnConfirm : false
					});
			    }
			  },
				error : function(XMLHttpRequest, textStatus,
						errorThrown) {
					console.log("请求错误");
				}
		});
	}
	</script>
	 </sec:authorize>
</body>
</html>