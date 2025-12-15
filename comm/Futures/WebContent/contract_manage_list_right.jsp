<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">合约交易参数</div>
<%-- 						<a href="javascript:goUrl(<s:property value="pageNo" />)" --%>
<!-- 							class="btn btn-light" style="margin-bottom: 10px"><i -->
<!-- 							class="fa fa-mail-reply"></i>返回</a> -->
						<a href="<%=basePath%>normal/adminContractManageAction!toAddInstall.action"
							class="btn btn-light" style="margin-bottom: 10px"><i
							class="fa fa-pencil"></i>新增</a>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
									
										<td>代码</td>
										<td>时间</td>
										<td>交割收益（%）</td>
										<td>每手金额</td>
										<td width="130px"></td>
									</tr>
								</thead>
								<tbody>
									
									<s:iterator value="contractResult.futures.elements" status="stat">
										<tr>
											
											<td><s:property value="symbol" /></td>
											<td><s:property value="timeNum" />(<s:property value="timeUnitCn" />)</td>
											<td><s:property value="profit_ratio" /></td>
											<td><s:property value="unit_amount" /></td>
											<td>
												<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
													<a href="<%=basePath%>normal/adminContractManageAction!toAddInstall.action?futuresId=<s:property value="id" />"
												class="btn btn-light" style="margin-bottom: 10px">修改</a>
												</sec:authorize>
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
          