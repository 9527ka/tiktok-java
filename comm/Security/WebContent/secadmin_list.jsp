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
			<h3>系统用户管理</h3>
				<%@ include file="include/alert.jsp"%>
			<!-- //////////////////////////////////////////////////////////////////////////// -->
			<!-- START queryForm -->
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						<sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">

							<form class="form-horizontal" action="<%=basePath%>normal/adminSystemUserAction!list.action" method="post"
								id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
													<s:textfield id="username_para" name="username_para" cssClass="form-control " placeholder="用户名"/>
											</div>
										</div>
										
									</fieldset>
								</div>
								

								<div class="col-md-12 col-lg-2">
									<button type="submit" class="btn btn-light btn-block">查询</button>
								</div>

							</form>
							</sec:authorize>

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
                    <sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
                    <a href="<%=basePath%>normal/adminSystemUserAction!toAdd.action?username_para=<s:property value="username_para" />" class="btn btn-light" style="margin-bottom: 10px" ><i class="fa fa-pencil"></i>新增用户</a>
                    </sec:authorize>
                    <div class="panel-body">
                        <table class="table table-bordered table-striped">
                            <thead>

                            <tr>
                                <td>用户名</td>
                                <td>角色</td>
                                <td>登录权限</td>
                                <td>备注</td>
                                <td style="width:130px;"></td>
                            </tr>
                            </thead>
                            <tbody>
                            <s:iterator value="page.elements" status="stat">
                                <tr>
                                    <td><s:property value="username" /></td>
                                    
                                    <td>
                                    	<s:property value="role_map.get(roleName)" />
                                    </td>
                                    <td><s:if test='enabled'>开启</s:if><s:if test='!enabled'><span class="right label label-danger">关闭</span></s:if></td>
                                    <td><s:property value="remarks" /></td>
                                    <td>
                                    
						            
                                        <div class="btn-group">
						                    <button type="button" class="btn btn-light">操作</button>
						                    <button type="button" class="btn btn-light dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
						                      <span class="caret"></span>
						                      <span class="sr-only">Toggle Dropdown</span>
						                    </button>
						                    <ul class="dropdown-menu" role="menu">
						                    <sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ROOT">
<%-- 						              			<li><a href="<%=basePath%>manage/adminSystemUserAction!toUpdatePassword.action?secAdmin_id=<s:property value="id" />&username_para=<s:property value="username_para" />">修改密码</a></li> --%>
						                     		<li><a href="<%=basePath%>normal/adminSystemUserAction!toUpdate.action?secAdmin_id=<s:property value="id" />&username_para=<s:property value="username_para" />">修改</a></li>
						                       		<li><a href="<%=basePath%>normal/adminSystemUserAction!toUpdatePassword.action?secAdmin_id=<s:property value="id" />&username_para=<s:property value="username_para" />">修改密码</a></li>
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



<%@ include file="include/js.jsp"%>
</body>
</html>