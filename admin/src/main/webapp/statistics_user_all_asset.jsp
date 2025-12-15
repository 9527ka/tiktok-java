<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="">
			
			<%@ include file="include/alert.jsp"%>
			
			<div class="row">
				<div class="col-md-12">
						<div class="panel-body ">
						
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td style="">业务</td>
										<td style="">资产(USDT)</td>
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="asset_data" status="stat"> -->
									<c:forEach items="${asset_data}" var="item" varStatus="stat">
										<tr>
											<td style="">${item.name}</td>
											<td style="">${item.value}</td>
										</tr>
									<!-- </s:iterator> -->
									</c:forEach>
								</tbody>
								
							</table>
							
						</div>

					</div>
			</div>
		</div>			
		