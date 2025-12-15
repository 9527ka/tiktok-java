<%@ page language="java" pageEncoding="utf-8" isELIgnored="false" deferredSyntaxAllowedAsLiteral="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="">

	<%@ include file="include/alert.jsp"%>

	<div>
		<div class="col-md-3"></div>
		<div class="control-group col-md-3">
			<div class="controls">
				<input id="base_currency" name="base_currency" class="form-control "
					placeholder="基础币种" value="${base_currency}" onblur="csPage('1')" />
			</div>
		</div>
	</div>
	<!-- END queryForm -->

	<div class="row">
		<div class="col-md-12">
		
			<div class="col-md-3">
				<div class="panel-body">

					<table class="table table-bordered table-striped">

						<thead>
							<tr>
								<td style="text-align: center;">报价币种</td>
							</tr>
						</thead>

						<tbody>
							<c:forEach items="${quoteList}" var="item" varStatus="stat">
								<tr id="tr_${item}" class="tr_quote">
									<td style="padding: 5px; text-align: center;">
										<a href="javascript:csPage('1','${item}')">${item}</a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
						
					</table>

					<!-- <nav> -->
				</div>
			</div>
			
			<div class="col-md-9">
				<div class="panel-body ">
				
					<table class="table table-bordered table-striped">
					
						<thead>
							<tr>
								<td></td>
								<td style="text-align: center;">基础币种</td>
								<td style="text-align: center;">交易对</td>
							</tr>
						</thead>
						
						<tbody>
							<!-- <s:iterator value="page.elements" status="stat"> -->
							<c:forEach items="${page.getElements()}" var="item" varStatus="stat">
								<tr>
									<td style="padding: 5px; text-align: center;">
										<!-- <div class="checkbox checkbox-success checkbox-circle"><input type="checkbox" name="choseSymbol" ></input></div> -->
										<div class="checkbox checkbox-success checkbox-circle"
											style="padding-left: 27px; height: 0px; margin-top: 3px;">
											<input id="checkbox${stat.index}" type="checkbox" class="symbolCheck"
												onClick="checkToRadio(this)" value="${item.symbol}"> 
												<label for="checkbox${stat.index}"> </label>
										</div>
									</td>
									<td style="padding: 5px; text-align: center;">${item.base_currency}</td>
									<td style="padding: 5px; text-align: center;">${item.symbol}</td>
								</tr>
							<!-- </s:iterator> -->
							</c:forEach>
						</tbody>
						
					</table>

					<ul class="pager" style="text-align: right;">
						<li><a href="javascript:csPage('${pageNo-1}')">上一页</a></li>
						<li><a href="javascript:csPage('${pageNo+1}')">下一页</a></li>
					</ul>
				</div>

			</div>

		</div>
	</div>
	
	<input type="hidden" value="${basePath}/normal/adminContractSymbolsAction!list.action" id='csUrl' />
		
</div>

<script>
	function checkToRadio(e) {
		className = $(e).attr("class");
		console.log();
		$("." + className).prop("checked", false);
		$(e).prop("checked", true);
	}
</script>
