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
			<h3>用户端内容管理</h3>
			
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">
						
							<form class="form-horizontal" action="<%=basePath%>normal/adminCmsAction!list.action" 
								method="post" id="queryForm">
								
								<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
								
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<input id="para_title" name="para_title"
													class="form-control " placeholder="标题" value="${para_title}"/>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="language" name="language"
														class="form-control ">
													<option value="">语言</option>
													<option value="cn" <c:if test="${cms.language == 'cn'}">selected="true"</c:if> >中文</option>
													<option value="en" <c:if test="${cms.language == 'en'}">selected="true"</c:if> >英文</option>
													<option value="vi" <c:if test="${cms.language == 'vi'}">selected="true"</c:if> >越南语</option>
													<option value="hi" <c:if test="${cms.language == 'hi'}">selected="true"</c:if> >印度语</option>
													<option value="id" <c:if test="${cms.language == 'id'}">selected="true"</c:if> >印度尼西亚语</option>
													<option value="tw" <c:if test="${cms.language == 'tw'}">selected="true"</c:if> >繁体中文</option>
													<option value="de" <c:if test="${cms.language == 'de'}">selected="true"</c:if> >德语</option>
													<option value="fr" <c:if test="${cms.language == 'fr'}">selected="true"</c:if> >法语</option>
													<option value="ja" <c:if test="${cms.language == 'ja'}">selected="true"</c:if> >日语</option>
													<option value="ko" <c:if test="${cms.language == 'ko'}">selected="true"</c:if> >韩语</option>
													<option value="th" <c:if test="${cms.language == 'th'}">selected="true"</c:if> >泰语</option>
													<option value="ru" <c:if test="${cms.language == 'ru'}">selected="true"</c:if> >俄语</option>
													<option value="ms" <c:if test="${cms.language == 'ms'}">selected="true"</c:if> >马来西亚语</option>
													<option value="pt" <c:if test="${cms.language == 'pt'}">selected="true"</c:if> >葡萄牙语</option>
													<option value="es" <c:if test="${cms.language == 'es'}">selected="true"</c:if> >西班牙语</option>
													<option value="el" <c:if test="${cms.language == 'el'}">selected="true"</c:if> >希腊语</option>
													<option value="it" <c:if test="${cms.language == 'it'}">selected="true"</c:if> >意大利语</option>
													<option value="tr" <c:if test="${cms.language == 'tr'}">selected="true"</c:if> >土耳其语</option>
													<option value="af" <c:if test="${cms.language == 'af'}">selected="true"</c:if> >南非荷兰语</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>
								<div class="col-md-12 col-lg-3">
									<fieldset>
										<div class="control-group">
											<div class="controls">
												<select id="status" name="status"
														class="form-control ">
													<option value="-2">状态</option>
													<option value="0" <c:if test="${status == '0'}">selected="true"</c:if>>启用</option>
													<option value="1" <c:if test="${status == '1'}">selected="true"</c:if>>禁用</option>
												</select>
											</div>
										</div>
									</fieldset>
								</div>

								<div class="col-md-12 col-lg-12" style="margin-top:10px;"></div>
								<div class="col-md-12 col-lg-3">
									<input id="startTime" name="startTime" class="form-control "
										   placeholder="开始日期" value="${startTime}" />
								</div>
								<div class="col-md-12 col-lg-3">

									<input id="endTime" name="endTime" class="form-control "
										   placeholder="结束日期" value="${endTime}" />
								</div>

								<div class="col-md-12 col-lg-2" style="margin-top:15px;">
									<button type="submit" class="btn  btn-block btn-light">查询</button>
								</div>
								
							</form>
							
						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
					<div class="panel panel-default">
					<div class="panel-title">查询结果</div>
					
					<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')}">
					
						<a href="<%=basePath%>normal/adminCmsAction!toAdd.action" class="btn btn-light" style="margin-bottom: 10px">
							<i class="fa fa-pencil"></i>新增公告</a>
								
					</c:if>
					
					<div class="panel-body">
					
						<table class="table table-bordered table-striped">
						
							<thead>
								<tr>
									<td>标题</td>
<%--									<td>模块</td>--%>
									<td>语言</td>
									<td>状态</td>
									<td>创建日期</td>
								</tr>
							</thead>
							
							<tbody>
								<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
									<tr>
										<td>${item.title}</td>
<%--										<td>--%>
<%--											<c:choose>--%>
<%--												<c:when test="${item.type == '0'}">--%>
<%--													公告管理--%>
<%--												</c:when>--%>
<%--												<c:otherwise>--%>
<%--													文章管理--%>
<%--												</c:otherwise>--%>
<%--											</c:choose>--%>
<%--										</td>--%>
										<td>

											<c:choose>
												<c:when test="${item.language == 'cn'}">
													中文
												</c:when>
												<c:when test="${item.language == 'en'}">
													英文
												</c:when>
												<c:when test="${item.language == 'vi'}">
													越南语
												</c:when>
												<c:when test="${item.language == 'hi'}">
													印度语
												</c:when>
												<c:when test="${item.language == 'id'}">
													印度尼西亚语
												</c:when>
												<c:when test="${item.language == 'tw'}">
													繁体中文
												</c:when>
												<c:when test="${item.language == 'de'}">
													德语
												</c:when>
												<c:when test="${item.language == 'fr'}">
													法语
												</c:when>
												<c:when test="${item.language == 'ja'}">
													日语
												</c:when>
												<c:when test="${item.language == 'ko'}">
													韩语
												</c:when>
												<c:when test="${item.language == 'th'}">
													泰语
												</c:when>
												<c:when test="${item.language == 'ru'}">
													俄语
												</c:when>
												<c:when test="${item.language == 'ms'}">
													马来西亚语
												</c:when>
												<c:when test="${item.language == 'pt'}">
													葡萄牙语
												</c:when>
												<c:when test="${item.language == 'es'}">
													西班牙语
												</c:when>
												<c:when test="${item.language == 'el'}">
													希腊语
												</c:when>
												<c:when test="${item.language == 'it'}">
													意大利语
												</c:when>
												<c:when test="${item.language == 'tr'}">
													土耳其语
												</c:when>
												<c:when test="${item.language == 'af'}">
													南非荷兰语
												</c:when>
												<c:when test="${item.language == 'ph'}">
													菲律宾语
												</c:when>
												<c:when test="${item.language == 'ar'}">
													阿拉伯语
												</c:when>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${item.status == '0'}">
													<span class="right label label-success">启用</span>
												</c:when>
												<c:otherwise>
													<span class="right label label-danger">禁用</span>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											${item.createTime}
										</td>
										<td>
									
											<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN')
														 || security.isResourceAccessible('OP_CMS_OPERATE')}">

												<div class="btn-group">
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
															data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">

														<li><a href="<%=basePath%>normal/adminCmsAction!toUpdate.action?id=${item.id}">修改</a></li>
														<li><a href="javascript:toDelete('${item.id}')">删除</a></li>

													</ul>
												</div>
											
											</c:if>
											
										</td>
										
									</tr>
								</c:forEach>
							</tbody>
							
						</table>
						
						<%@ include file="include/page_simple.jsp"%>
						
					</div>

					</div>
				</div>
			</div>

		</div>
		<div class="form-group">
			<form action=""
				  method="post" id="mainform">
				<input type="hidden" name="pageNo" id="pageNo"
					   value="${pageNo}">
				<input type="hidden" name="id" id="id"/>
				<div class="col-sm-1 form-horizontal">
					<!-- 模态框（Modal） -->
					<div class="modal fade" id="modal_succeeded" tabindex="-1"
						 role="dialog" aria-labelledby="myModalLabel"
						 aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content" >
								<div class="modal-header">
									<button type="button" class="close"
											data-dismiss="modal" aria-hidden="true">&times;</button>
									<h4 class="modal-title" id="myModalLabel">确认调整</h4>
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
                                            <a id="update_email_code_button" href="javascript:updateSendCode();" class="btn btn-light" style="margin-bottom: 10px" >获取超级签验证码</a>
                                        </div>
                                    </div> -->
									<%--									<div class="form-group" >--%>
									<%--										<label for="input002" class="col-sm-3 control-label form-label">谷歌验证码</label>--%>
									<%--										<div class="col-sm-4">--%>
									<%--											<input id="google_auth_code"  name="google_auth_code"--%>
									<%--												   placeholder="请输入谷歌验证码" >--%>
									<%--										</div>--%>
									<%--									</div>--%>
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
			</form>
		</div>


		<%@ include file="include/footer.jsp"%>
		
	</div>

	<%@ include file="include/js.jsp"%>

	<form action="<%=basePath%>normal/adminCmsAction!delete.action" method="post" id="ondelete">
	
		<input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
		<input type="hidden" name="news_id" id="id" value="${news_id}">
		
	</form>
	
	<script type="text/javascript">

		function toDelete(id,pageNo){
			$('#id').val(id);
			$('#pageNo').val(pageNo);
			$('#myModalLabel').html("删除");
			$('#mainform').attr("action","<%=basePath%>normal/adminCmsAction!delete.action");

			$('#modal_succeeded').modal("show");

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
	</script>

</body>

</html>
