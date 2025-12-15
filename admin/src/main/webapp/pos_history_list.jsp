<%@ page language="java" pageEncoding="utf-8" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="security" class="security.web.BaseSecurityAction" scope="page"/>

<%@ include file="include/pagetop.jsp" %>

<!DOCTYPE html>
<html>

<head>
    <%@ include file="include/head.jsp" %>
</head>

<body>

<%@ include file="include/loading.jsp" %>

<style>
    .black_overlay {
        display: none;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.7);
        z-index: 100;
    }

    .enlargeContainer {
        display: none;
    }

    .enlargePreviewImg {
        /*这里我设置的是：预览后放大的图片相对于整个页面定位*/
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);

        /*宽度设置为页面宽度的70%，高度自适应*/
        width: 85%;
        z-index: 200;
    }

    /*关闭预览*/
    .close {
        position: absolute;
        top: 20px;
        right: 20px;
        width: 20px;
        height: 20px;
        cursor: pointer;
        z-index: 200;
    }

    td {
        word-wrap: break-word; /* 让内容自动换行 */
        max-width: 200px; /* 设置最大宽度，以防止内容过长 */
    }
</style>
<body>

<div class="loading"><img src="<%=basePath%>img/loading.gif" alt="loading-img"></div>

<div class="ifr-dody">

    <input type="hidden" name="session_token" id="session_token" value="${session_token}"/>

    <div class="ifr-con">
        <h3>POS订单记录</h3>

        <%@ include file="include/alert.jsp" %>

        <!-- //////////////////////////////////////////////////////////////////////////// -->
        <!-- START queryForm -->
        <div class="row">
            <div class="col-md-12">
                <div class="panel panel-default">

                    <div class="panel-title">查询条件</div>

                    <div class="panel-body">

                        <form class="form-horizontal" action="<%=basePath%>mall/pos/historyList.action" method="post"
                              id="queryForm">
                            <input type="hidden" name="pageNo" id="pageNo"
                                   value="1">
                            <input type="hidden" name="messages" id="messages"
                                   value="">
                            <div class="col-md-12 col-lg-2">
                                <fieldset>
                                    <div class="control-group">
                                        <div class="controls">
                                            <input id="order_no_para" name="sellerName"
                                                   class="form-control " placeholder="商家名称" value="${sellerName}"/>
                                        </div>
                                    </div>
                                </fieldset>
                            </div>


                            <div class="col-md-12 col-lg-2">
                                <button type="submit" class="btn btn-light btn-block">查询</button>
                            </div>

                        </form>

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
                    <div class="panel-body">

                        <table class="table table-bordered table-striped">

                            <thead>
                            <tr>
                                <td>任务号</td>
                                <td>店铺Id</td>
                                <td>店铺名称</td>
                                <td>商品ID</td>
                                <td>买家ID</td>
                                <td>商品价格</td>
                                <td>下单数量</td>
                                <td>订单价格</td>
                                <td>执行时状态</td>
                                <td>执行时间</td>
                                <td>操作</td>
                            </tr>
                            </thead>

                            <tbody>
                            <input type="hidden" name="iconImg" id="iconImg" value=""/>
                            <c:forEach items="${page.getElements()}" var="item" varStatus="stat">
                                <tr>
                                    <td style="width: 160px; align-content: center;">${item.id}</td>
                                    <td style="width: 160px; align-content: center;">${item.sellerId}</td>
                                    <td style="width: 160px; align-content: center;">${item.sellerName}</td>
                                    <td style="width: 160px; align-content: center;">${item.goodsId}</td>
                                    <td style="width: 160px; align-content: center;">${item.partyId}</td>
                                    <td style="width: 160px; align-content: center;">${item.price}</td>
                                    <td style="width: 160px; align-content: center;">${item.count}</td>
                                    <td style="width: 160px; align-content: center;">${item.amount}</td>
                                    <td style="width: 160px; align-content: center;">
                                        <c:choose>
                                            <c:when test="${item.status == '0'}">
                                                <span class="right label label-hei">待执行</span>
                                            </c:when>
                                            <c:when test="${item.status == '1'}">
                                                <span class="right label label-success">已执行</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="right label label-success"></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="width: 160px; align-content: center;">${item.delay}</td>
                                    <td>


                                            <div class="btn-group">
                                              <!--  <button type="button" class="btn btn-light"><a href="javascript:showOrder('${item.id}')">查看</a></button>

-->
                                                <button type="button" class="btn btn-light" onclick="delete_to('${item.id}')">删除</button>
                                            </div>


                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>

                        </table>

                        <%@ include file="include/page_simple.jsp" %>

                        <!-- <nav> -->
                    </div>

                </div>
                <!-- End Panel -->

            </div>
        </div>

        <%@ include file="include/footer.jsp" %>

    </div>
    <!-- End Content -->
    <!-- //////////////////////////////////////////////////////////////////////////// -->

    <!-- 模态框 -->
    <div class="form-group">

        <form action="<%=basePath%>mall/pos/history!toDelete.action"
              method="post" id="deleteForm">

            <input type="hidden" name="pageNo" id="pageNo" value="${pageNo}">
            <input type="hidden" name="id" id="id_delete">
            <input type="hidden" name="session_token" id="session_token_delete" value="${session_token}">

            <div class="col-sm-1">
                <!-- 模态框（Modal） -->
                <div class="modal fade" id="modal_delete" tabindex="-1"
                     role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">

                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal"
                                        aria-hidden="true">&times;
                                </button>
                                <h4 class="modal-title" id="myModalLabel">删除POS任务</h4>
                            </div>


                            <div class="modal-footer" style="margin-top: 0;">
                                <button type="button" class="btn " data-dismiss="modal">关闭</button>
                                <button id="sub" type="submit" class="btn btn-default">确认</button>
                            </div>

                        </div>
                        <!-- /.modal-content -->
                    </div>
                    <!-- /.modal -->
                </div>
            </div>
        </form>

    </div>


    <%@ include file="include/js.jsp" %>

    <script src="<%=basePath%>js/bootstrap/bootstrap-treeview.js"></script>
    <script type="text/javascript">
        function delete_to(id) {
            var session_token = $("#session_token").val();
            $("#session_token_delete").val(session_token);
            $("#id_delete").val(id);
            $('#modal_delete').modal("show");
        }
    </script>

</body>

</html>
