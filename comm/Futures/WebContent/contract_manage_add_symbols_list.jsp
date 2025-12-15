<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<div class="">
			
			<%@ include file="include/alert.jsp"%>
			
			<!-- START queryForm -->
			<%-- <div class="row">
				<div class="col-md-12">
					<div class="panel panel-default">

						<div class="panel-title">查询条件</div>
						<div class="panel-body">

							<form class="form-horizontal"
								action="${basePath}/normal/adminContractSymbolsAction!list.action"
								method="post" id="queryForm">
								<input type="hidden" name="pageNo" id="pageNo"
									value="${param.pageNo}">
									
									<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<s:textfield id="quote_currency" name="quote_currency" cssClass="form-control " placeholder="报价币种"/>
											</div>
										</div>
									</fieldset>
								</div>
<div class="col-md-12 col-lg-4">
									<fieldset>
										<div class="control-group">
											<div class="controls">
											<s:textfield id="base_currency" name="base_currency" cssClass="form-control " placeholder="基础币种"/>
											
											</div>
										</div>
									</fieldset>
								</div>

	

								<div class="col-md-12 col-lg-2">
									<button type="button" class="btn btn-light btn-block" onClick="csPage('1')">查询</button>
								</div>
</form>
<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
								<div class="col-md-12 col-lg-4" style="margin-top: 10px;">
									<div class="mailbox clearfix">
										<div class="mailbox-menu">
											<ul class="menu">
												<li>
												<button type="button" onclick="reload()" class="btn btn-default ">同步远程数据库</button></li>

											</ul>
										</div>
									</div>
								</div>
</sec:authorize>
							

						</div>

					</div>
				</div>
			</div> --%>
			<div>
				<div class="col-md-3" >
											
				</div>
				<div class="control-group col-md-3">
					<div class="controls">
											<s:textfield id="base_currency" name="base_currency" cssClass="form-control " placeholder="基础币种" value="" onblur="csPage('1')"/>
											
											</div>
<!-- 									<button type="button" class="btn btn-light btn-block" onClick="csPage('1')">查询</button> -->
								</div>
			</div>
			<!-- END queryForm -->
			
			<div class="row">
				<div class="col-md-12">
					<!-- Start Panel -->
<!-- 					<div class="panel panel-default panel-body"> -->
					<div class="col-md-3">
<!-- 						<div class="panel-title">查询结果</div> -->
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
									<!-- <td>交易对</td>
										<td>基础币种</td> -->
										 <td style="text-align: center;">报价币种</td>
<!-- 										 <td></td> -->
										<!--<td>报价精度(小数位)</td>
										<td>交易对杠杆最大倍数</td>
										<td>状态</td>
										<td></td> -->
									</tr>
								</thead>
								<tbody>
									
									<s:iterator value="quoteList" status="stat" id="char">
										<tr id="tr_<s:property value="char" />" class="tr_quote">
<%-- 										<td><s:property value="symbol" /></td> --%>
<%-- 											<td><s:property value="base_currency" /></td> --%>
											<td style="padding: 5px;text-align: center;"><a href="javascript:csPage('1','<s:property value="char" />')"><s:property value="char" /></a></td>
<%-- 											<td><s:property value="price_precision" /></td> --%>
<%-- 											<td><s:property value="leverage_ratio" /></td> --%>
<%-- 											<td><s:property value="state" /></td> --%>
											<%-- <td>
											<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
												<div class="btn-group">
														<a href="javascript:csPage('1','<s:property value="char" />')"
													class="btn btn-light" style="margin-bottom: 10px">查看</a>
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<li><a
															href="${basePath}normal/adminItemAction!toAdd.action?symbol_data=<s:property value="symbol" />&symbol=<s:property value="base_currency" />">添加到交易品种</a></li>
													</ul>
												</div>
												</sec:authorize>
											</td> --%>
										</tr>
									</s:iterator>

								</tbody>
							</table>
							
							<nav>
						</div>
						</div>
						<div class="col-md-9">
<!-- 						<div class="panel-title">交易对查询结果(默认第一个报价币种)</div> -->
						<div class="panel-body ">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
										<td></td>
										<td style="text-align: center;">基础币种</td>
										<td style="text-align: center;">交易对</td>
										
<!-- 										<td>报价币种</td> -->
										<!-- <td>报价币种</td>
										<td>报价精度(小数位)</td>
										<td>交易对杠杆最大倍数</td>
										<td>状态</td>
										<td></td> -->
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
											<td style="padding: 5px;text-align: center;">
<!-- 												<div class="checkbox checkbox-success checkbox-circle"><input type="checkbox" name="choseSymbol" ></input></div> -->
												<div class="checkbox checkbox-success checkbox-circle" style="padding-left: 27px;height: 0px;margin-top: 3px;">
							                        <input id="checkbox<s:property value="#stat.index" />" type="checkbox" class="symbolCheck" onClick="checkToRadio(this)" value="<s:property value="symbol" />">
							                        <label for="checkbox<s:property value="#stat.index" />">
							                            
							                        </label>
							                    </div>
											</td>
											
											<td style="padding: 5px;text-align: center;"><s:property value="base_currency" /></td>
											<td style="padding: 5px;text-align: center;"><s:property value="symbol" /></td>
											
<%-- 											<td><s:property value="quote_currency" /></td> --%>
<%-- 											<td><s:property value="price_precision" /></td> --%>
<%-- 											<td><s:property value="leverage_ratio" /></td> --%>
<%-- 											<td><s:property value="state" /></td> --%>
											<%-- <td>
											<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
												 <div class="btn-group">
												<a href=""
													class="btn btn-light" style="margin-bottom: 10px">选中</a>
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<li><a
															href="${basePath}normal/adminItemAction!toAdd.action?symbol_data=<s:property value="symbol" />&symbol=<s:property value="base_currency" />">添加到交易品种</a></li>
													</ul>
												</div> 
												</sec:authorize>
											</td> --%>
										</tr>
									</s:iterator>

								</tbody>
							</table>
							
							<ul class="pager" style="text-align: right;">
<!-- 				           	  <li><a href="javascript:csPage('1')">首页</a></li> -->
				              <li><a href="javascript:csPage('${pageNo-1}')">上一页</a></li>
				          
				           		<li><a  href="javascript:csPage('${pageNo+1}')">下一页</a></li>
<%-- 				                <li><a  href="javascript:csPage('${1000000}')">尾页</a></li> --%>
				            </ul>			
						</div>

					</div>
<!-- 					</div> -->
					<!-- End Panel -->

				</div>
				
				<!-- right table -->
				
				<%-- <div class="col-md-7">
					<!-- Start Panel -->
					<div class="panel panel-default">

						<div class="panel-title">交易对查询结果(默认第一个报价币种)</div>
						<div class="panel-body">
							<table class="table table-bordered table-striped">
								<thead>
									<tr>
									<td>交易对</td>
										<td>基础币种</td>
<!-- 										<td>报价币种</td> -->
										<td></td>
										<!-- <td>报价币种</td>
										<td>报价精度(小数位)</td>
										<td>交易对杠杆最大倍数</td>
										<td>状态</td>
										<td></td> -->
									</tr>
								</thead>
								<tbody>
									<s:iterator value="page.elements" status="stat">
										<tr>
										<td><s:property value="symbol" /></td>
											<td><s:property value="base_currency" /></td>
											<td><s:property value="quote_currency" /></td>
											<td><s:property value="price_precision" /></td>
											<td><s:property value="leverage_ratio" /></td>
											<td><s:property value="state" /></td>
											<td>
											<sec:authorize ifAnyGranted="ROLE_ROOT,ROLE_ADMIN">
												<div class="btn-group">
												<a href=""
													class="btn btn-light" style="margin-bottom: 10px">选中</a>
													<button type="button" class="btn btn-light">操作</button>
													<button type="button" class="btn btn-light dropdown-toggle"
														data-toggle="dropdown" aria-expanded="false">
														<span class="caret"></span> <span class="sr-only">Toggle
															Dropdown</span>
													</button>
													<ul class="dropdown-menu" role="menu">
														<li><a
															href="${basePath}normal/adminItemAction!toAdd.action?symbol_data=<s:property value="symbol" />&symbol=<s:property value="base_currency" />">添加到交易品种</a></li>
													</ul>
												</div>
												</sec:authorize>
											</td>
										</tr>
									</s:iterator>

								</tbody>
							</table>
							
							<ul class="pager" style="text-align: left;">
				           	  <li><a href="javascript:csPage('1')">首页</a></li>
				              <li><a href="javascript:csPage('${pageNo-1}')">上一页</a></li>
				          
				           		<li><a  href="javascript:csPage('${pageNo+1}')">下一页</a></li>
				                <li><a  href="javascript:csPage('${1000000}')">尾页</a></li>
				            </ul>			
						</div>

					</div>
					<!-- End Panel -->

				</div> --%>
			</div>
				<input type="hidden" value="${basePath}/normal/adminContractSymbolsAction!list.action" id='csUrl'/>
		</div>
		<script>
			function checkToRadio(e){
				className=$(e).attr("class");
				console.log();
				$("."+className).prop("checked",false);
				$(e).prop("checked",true);
			}
		</script>
			
		