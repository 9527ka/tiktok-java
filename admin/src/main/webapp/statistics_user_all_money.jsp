<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="">
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- END queryForm -->
			
			<div class="row">
				<div class="col-md-12">

						<div class="panel-body ">
							<table class="table table-bordered table-striped">
							
								<thead>
									<tr>
										<td style="">币种</td>
										<td style="">存量</td>										
 										<!--<td>报价币种</td>
										<td>报价币种</td>
										<td>报价精度(小数位)</td>
										<td>交易对杠杆最大倍数</td>
										<td>状态</td>
										<td></td> -->
									</tr>
								</thead>
								
								<tbody>
									<!-- <s:iterator value="wallet_data" status="stat"> -->
									<c:forEach items="${wallet_data}" var="item" varStatus="stat">
										<tr>
											<%-- <td style="padding: 5px;text-align: center;">
<!-- 												<div class="checkbox checkbox-success checkbox-circle"><input type="checkbox" name="choseSymbol" ></input></div> -->
												<div class="checkbox checkbox-success checkbox-circle" style="padding-left: 27px;height: 0px;margin-top: 3px;">
							                        <input id="checkbox<s:property value="#stat.index" />" type="checkbox" class="symbolCheck" onClick="checkToRadio(this)" value="<s:property value="symbol" />">
							                        <label for="checkbox<s:property value="#stat.index" />">
							                            
							                        </label>
							                    </div>
											</td> --%>
											<td style="">${item.wallettype}</td>
											<td style="">${item.amount}</td>
										</tr>
									<!-- </s:iterator> -->
									</c:forEach>
								</tbody>
								
							</table>
							
							<%-- <ul class="pager" style="text-align: right;">
<!-- 				           	  <li><a href="javascript:csPage('1')">首页</a></li> -->
				              <li><a href="javascript:csPage('${pageNo-1}')">上一页</a></li>
				          
				           		<li><a  href="javascript:csPage('${pageNo+1}')">下一页</a></li>
				                <li><a  href="javascript:csPage('${1000000}')">尾页</a></li>
				            </ul>	 --%>		
						</div>

					</div>
<!-- 					</div> -->
					<!-- End Panel -->

<!-- 				</div> -->
			</div>
		</div>
		
		<script>
			function checkToRadio(e){
				className=$(e).attr("class");
				console.log();
				$("."+className).prop("checked",false);
				$(e).prop("checked",true);
			}
		</script>
