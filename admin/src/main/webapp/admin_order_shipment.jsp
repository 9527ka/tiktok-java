<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page" />

<%@ include file="include/pagetop.jsp"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="include/head.jsp"%>
</head>
<body class="ifr-dody">
	<%@ include file="include/loading.jsp"%>

	<!-- //////////////////////////////////////////////////////////////////////////// -->
	<!-- START CONTENT -->
	<div class="ifr-con">

<div class="form-group form form-order-shipment">
            <div class="form">
                <div class="flex item margin-bx-4">
                    <label for="input002" class="col-sm-3 control-label form-label">物流服务商</label>
                    <div class="col-sm-4">
                        <select name="shipmentProvider" style="width:400px">
                            <option value="">请选择物流服务商</option>
                            <option value="FEDEX">FedEx</option>
                            <option value="DHL">DHL</option>
                            <option value="UPS">UPS</option>
                            <option value="TNT">TNT</option>
                            <option value="EMS">EMS</option>
                            <option value="OTHER">其他</option>
                        </select>
                    </div>
                </div>

                <div class="flex item margin-bx-4">
                    <label for="input002" class="col-sm-3 control-label form-label">运单号</label>
                    <div class="col-sm-4">
                        <input name="shipmentTradeNo" placeholder="请输入运单号" style="width:400px">${shipmentTradeNo}</textarea>
                    </div>
                </div>

               <div class="flex item margin-bx-4">
                    <label for="input002" class="col-sm-3 control-label form-label">自动收货时间(天)</label>
                    <div class="col-sm-4">
                        <input type="number" name="confirmDays" placeholder="请输入自动收货时间" value="${confirmDays}"/>
                    </div>
                </div>
            </div>
            <div class="modal-footer" style="margin-top:30px;">
                <button type="button" class="btn btn-close"
                    data-dismiss="modal">关闭</button>
                <button type="submit"
                    class="btn btn-default btn-submit">发货</button>
            </div>
	</div>


		</div>


	</div>



	<%@ include file="include/js.jsp"%>


<script type="text/javascript">
     $(document).ready(function(){
        const modal = $('.form-order-shipment');
        modal.find(".btn-submit").click(function(e){
            e.preventDefault();
            const formData = getFormData(modal);
            formData.orderNo = '${orderNo}';
            ajaxPost('<%=basePath%>mall/order/shipment.action',formData,function(data){
                if(data.code == 0){
                    $(".modal-footer .btn-close").click();
                    location.href = "<%=basePath%>mall/order/list.action";
                }
            })
        });
    });


</script>

	
</body>
</html>