<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page language="java" import="security.*"%>
<%@ include file="include/basePath.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>log in</title>
    <style type="text/css">
        /* 全局样式 */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            min-height: 100vh;
            width: 100%;
            background: linear-gradient(135deg, #95baff 0%, #597bff 100%);
            display: flex;
            flex-flow: column nowrap;
            justify-content: center;
            align-items: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            overflow: hidden;
        }

        .l-box1 {
            background: rgba(255, 255, 255, 0.95);
            padding: 40px;
            border-radius: 7px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 480px;
            animation: fadeIn 0.6s ease-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        h1 {
            color: #222;
            text-align: center;
            margin-bottom: 30px;
            font-weight: 600;
            font-size: 18px;
        }

        .tit01 {
            font-size: 24px;
            color: #1890ff;
            margin: 0 auto 30px auto;
            text-align: center;
            font-weight: 600;
        }

        .ip01 {
            width: 100%;
            margin: 0 auto 20px auto;
            position: relative;
        }

        .ip03 {
            width: 100%;
            height: 50px;
            line-height: 50px;
            border: 1px solid #ddd;
            font-size: 16px;
            text-indent: 45px;
            border-radius: 8px;
            color: #333;
            transition: border-color 0.3s ease;
        }

        .ip03:focus {
            outline: none;
            border-color: #1890ff;
            box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
        }

        .ip-n {
            background: url(image/n2.png) no-repeat 15px center #fff;
            background-size: 25px;
        }

        .ip-p {
            background: url(image/p2.png) no-repeat 15px center #fff;
            background-size: 25px;
        }

        .bn01 {
            background: #1890ff;
            color: #fff;
            font-size: 18px;
            font-weight: 500;
            padding: 14px 0;
            width: 100%;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: background 0.3s ease;
        }

        .bn01:hover {
            background: #40a9ff;
        }

        .footer {
            line-height: 30px;
            position: fixed;
            bottom: 20px;
            width: 100%;
            text-align: center;
            color: #fff;
            font-size: 12px;
            letter-spacing: 1px;
            opacity: 0.8;
        }

        .h-t {
            color: #ff4949;
            font-size: 12px;
            line-height: 1.4;
            padding-top: 4px;
            text-align: left;
            display: none;
            transition: opacity 0.3s ease;
        }
        .logo{
            text-align: center;
            margin-bottom: 10px;
       }

       .login-hint {
           position: fixed;
           top:1rem;
           width:480px;
           margin:auto;
           background:#FAFAFA;
           padding:0.5rem 1rem;
           border-radius: 5px;
           font-size: 14px;
           font-weight:600;
           color:#C75252;
           text-align:center;
       }
       .popup-message {
           position: fixed;
           top: 20px;
           right: 20px;
           padding: 15px 25px;
           border-radius: 4px;
           color: #fff;
           font-size: 14px;
           opacity: 0;
           transform: translateY(-20px);
           transition: all 0.3s ease;
           z-index: 1000;
       }
       .popup-message.show {
           opacity: 1;
           transform: translateY(0);
       }
       .popup-message.error {
           background-color: #ff4949;
       }
       .popup-message.success {
           background-color: #67c23a;
       }
       .popup-message.warning {
           background-color: #e6a23c;
       }
    </style>
</head>

<body>
<c:if test="${not empty loginHints}">

<div class="login-hint">
   ${loginHints}
</div>
</c:if>
<div class="l-box1">
    <form action="<%=basePath%>public/login.action"  onsubmit="return toVaild()"
          class="ng-pristine ng-invalid ng-touched" method="post">

         <div class="logo">
            <img src="${siteLogo}" alt="logo" style="width: auto; height: 64px">
         </div>
         <div style="text-align:center">
            <h1>系统管理中心</h1>
         </div>
        <div class="tit01"></div>

        <div class="ip01">
            <input id="j_username" name="j_username" type="text" class="ip03 ip-n" placeholder="User name" />
            <div class="h-t ht-name">请输入您的账号</div>
        </div>
        <div class="ip01">
            <input id="j_password" name="j_password" type="password" class="ip03 ip-p" placeholder="Password" />
            <div class="h-t ht-pwd">请输入您的密码</div>
        </div>
        <c:if test="${enableGoogleAuth}">
            <div class="ip01">
                <input id="googleAuthCode" name="googleAuthCode" type="text" class="ip03 ip-p" placeholder="GoogleAuthCode"/>
            </div>
        </c:if>
        <c:if test="${ error != null}">
            <div class="ip01">
                <input type="hidden" id="error" value="${error}"/>
                <div class="h-t ht-code">${error}</div>
            </div>
        </c:if>

        <div class="ip01"><input name="提交" type="submit" class="bn01" value="Log in"/></div>


    </form>
</div>
<div class="footer">
Copyright © 2018-2025 ${siteOwner} All Rights Reserved.

<c:if test="${showMallCopyright}">
    <div style="font-size:12px;color:#F7F7F7">
        此系统相关源码从网络收集并整理，仅供学习和参考使用！
    </div>
    <br />
</c:if>
 </div>
</body>
</html>

<script type="text/javascript" src="<%=basePath%>js/jquery.min.js"></script>
<script type='text/javascript'>
    var enabledGoogleAuth = "${enableGoogleAuth}" == "true";
    //初始化执行一次
    setTimeout(function() {
        start();
    }, 100);

    function start() {
        var error = $("#error").val();
        if (null == error || "" == error) {
            $(".ht-code").hide();
        } else {
            $(".ht-code").show();
            $(".ht-code").html(error);
            return false;
        }
    }

    var userVal,passVal,codeVal

    function toVaild(){


        userVal = $('#j_username').val()
        passVal = $('#j_password').val()
        codeVal = $('#googleAuthCode').val()

        if(!enabledGoogleAuth){
            // 不需要谷歌验证码
            codeVal = "***"
        }

        if(!userVal && !passVal && !codeVal){
            $(".ht-name").show();
            $(".ht-pwd").show();
            $(".ht-code").show();
            return false;
        }
        if(!userVal){
            $(".ht-name").show();
        }
        if(!passVal){
            $(".ht-pwd").show();
        }
        if(!codeVal){
            $(".ht-code").show();
        }

        if(userVal){
            $(".ht-name").hide();
        }
        if(passVal){
            $(".ht-pwd").hide();
        }
        if(codeVal){
            $(".ht-code").hide();
        }

        if(!userVal || !passVal || !codeVal){
            return false;
        }

    }

    function clickSbbmit(e){
        e.preventDefault();

        console.log(passVal,userVal)

    }
    function showPopup(message, type) {
        const popup = document.createElement('div');
        popup.className = `popup-message ${type}`;
        popup.textContent = message;
        document.body.appendChild(popup);

        setTimeout(() => {
            popup.classList.add('show');
        }, 10);


        setTimeout(() => {
            popup.classList.remove('show');
            setTimeout(() => {
                document.body.removeChild(popup);
            }, 300);
        }, 3000);
    }

</script>

