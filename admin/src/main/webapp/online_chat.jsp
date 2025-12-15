<%@page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
<%@ include file="include/head.jsp"%>
<!-- ========== Css Files ========== -->
		<link href="css/root.css" rel="stylesheet">
		<style>
			.panel-body-chat .mailbox-inbox .searchbutton {
			  right: 22px;
				top: 23px;
				padding-left: 10px;
				padding-right: 10px;
				cursor: pointer;
				line-height: 32px;
				border-radius: 0 999px 999px 0;
			}
			.panel-body-chat .mailbox-inbox .searchbutton:after{
				content: '';
				position: absolute;
				top: 5px;
				bottom: 5px;
				left: 0;
				width: 1px;
				background-color: rgba(0, 0, 0, 0.5);
			}
			.panel-body-chat .mailbox-inbox .searchbutton:hover i{
				color: rgba(0, 0, 0, 0.9);
			}
			.panel-body-chat .bigImg{overflow: auto;display: none;text-align: center;position: fixed;top: 40px;bottom: 0;left: 0;width: 100%;background-color: rgba(0,0,0,0.4);z-index: 1000;}
			.panel-body-chat .bigImg img{width: 300px;margin-top: 30px;}
			.panel-body-chat .unreadhide{display: none;}
			.panel-body-chat .chat .conv .img{width: 40px;height: 40px;}
			.panel-body-chat .item-li-on{background: #D5EAFF;}
			.panel-body-chat .chat .conv{height: calc( 100% - 223px);overflow: auto;color: black;}
			.conv-loading{position:fixed;top: 175px;left:25%;right: 0;height: 100%;text-align: center;}
			.conv-loading img{width: 50px;margin-top: 100px;}
			.panel-body-chat .chat .conv .ballon {
			    max-width: 80%;
			    word-break: break-all;
					margin: 5px 0;
					padding: 4px 20px;
					white-space: pre-line;
			}
			.panel-body-chat .chat .conv .date{border:0;padding: 0;}
			.panel-body-chat .chat .conv li{margin: 0;}
			.panel-body-chat .container-mailbox{
/* 				width: 1000px; */
				height: 100%;
				margin: 0 auto;
				background-color: #fff;
				overflow: hidden;
			}
			.panel-body-chat .mailbox-inbox .unread {
			    position: absolute;
			    font-size: 10px;
			    right: 20px;
			    bottom: 20px;
			    color: #fff;
					background-color: #f00;
					width: 20px;
					height: 20px;
					line-height: 20px;
					text-align: center;
					border-radius: 30px;
			}
			.panel-body-chat .fixed-right-b{
				position: fixed;
				right: 60px;
				bottom: 180px;
				width: 50px;
				height: 50px;
				line-height: 50px;
				background-color: #fff;
				border-radius: 50px;
				box-shadow: 0 0 10px rgba(0,0,0,0.5);
				text-align: center;
				color: #999;
				cursor: pointer;
			}
			.panel-body-chat .fixed-right-b i{
				font-size: 50px;
				font-weight: 600;
			}
			.panel-body-chat .un-num{
				position: absolute;
				top: -20px;
				left: 10px;
				width: 30px;
				height: 30px;
				line-height: 30px;
				border-radius: 30px;
				text-align: center;
				background-color: #0066ff;
				color: #fff;
				font-size: 16px;
				font-weight: 600;
			}
			.panel-body-chat .title h1{line-height: 55px;}
			.panel-body-chat .img-show{width: 260px;cursor: pointer;}
			.panel-body-chat .img-loading{width: 100px;}
			.panel-body-chat .msg-list{width: 100%;height: calc( 100% - 85px);overflow: auto;}
			.panel-body-chat .right-msg{text-align: right; padding-right: 70px; padding-left: 0px!important;color:black}
			.panel-body-chat .right-msg .img{right:0;left: unset!important;}
			.panel-body-chat .mailbox .write{padding: 0 20px;}
			.panel-body-chat .mailbox,.panel-body-chat .padding-0,.panel-body-chat .mailbox-inbox{height: 100%;}
			/*滚动条样式*/
			.panel-body-chat .conv::-webkit-scrollbar {width: 4px;}
			.panel-body-chat .conv::-webkit-scrollbar-thumb {
					border-radius: 10px;
					-webkit-box-shadow: inset 0 0 5px rgba(0,0,0,0.2);
					background: rgba(0,0,0,0.2);
			}
			.panel-body-chat .conv::-webkit-scrollbar-track {
					-webkit-box-shadow: inset 0 0 5px rgba(0,0,0,0.2);
					border-radius: 0;
					background: rgba(0,0,0,0.1);

			}
			.panel-body-chat .msg-list::-webkit-scrollbar {width: 4px;}
			.panel-body-chat .msg-list::-webkit-scrollbar-thumb {
					border-radius: 10px;
					-webkit-box-shadow: inset 0 0 5px rgba(0,0,0,0.2);
					background: rgba(0,0,0,0.2);
			}
			.panel-body-chat .msg-list::-webkit-scrollbar-track {
					-webkit-box-shadow: inset 0 0 5px rgba(0,0,0,0.2);
					border-radius: 0;
					background: rgba(0,0,0,0.1);

			}
			.write{position: relative;}
			.photo-add {
				position: absolute;
				right: 30px;
				top: 5px;
				width: 30px;
				height: 30px;
				cursor: pointer;
			}
			
			.item-image {
			    display: block;
			    width: 100%;
			    /* height: 100%; */
			}
			
			.uploader-file {
			    opacity: 0;
			    position: absolute;
			    top: 0;
			    left: 0;
			    width: 100%;
			    height: 100%;
			    cursor: pointer;
			    z-index:-1;
			}
			.panel-body-chat .remarks{
				color: #76747a;
				font-size: 10px;
				margin-left: 3px;
			}
			.chat .title{padding: 12px 20px 0;}
		    .panel-body-chat .chat .title h1{display: inline-block;line-height: 25px;}
		    .panel-body-chat .title-bz{
		        color: #0066FF;
		        font-size: 12px;
		        text-decoration: underline;
		        margin-left: 2px;
		        cursor: pointer;
		    }
		    .panel-body-chat .user-bz{
                color:black;
		        display: none;
		        border: 1px solid #ccc;
		        font-size: 12px;
		        height: 24px;
		        border-radius: 5px;
		    }
		    .panel-body-chat .user-detail{display: inline-block;}
 		    .panel-body-chat .user-detail>span{margin-left: 30px; color:black}
		    .panel-body-chat .user-detail .label{font-size: 13px;}
		    .panel-body-chat .user-detail-time>span{margin-right: 30px; color:black}
		    #unsend-btn{filter: grayscale(80%);cursor: not-allowed;}
		    .ballon{position: relative;}
		    .msg-img{
		    	display: inline-block;
			    position: absolute;
			    top: 5px;
			    left: -24px;
			    width: 20px;
			    height: 20px;
			    background:url("<%=basePath%>img/chat/loading.gif");
			    background-size: 100% 100%;
			}
			.msg-img-null{
			    background:url("<%=basePath%>img/chat/gth.png");
			    background-size: 100% 100%;
			}
			.bot-list-loading{text-align: center;padding: 0 0 10px 0;font-size: 12px;color: #666;}
			.bot-list-loading img{width: 20px;margin: 0 10px 0 0;}
			.login_status-success{
				background-color: #26a65b;
				padding: 1px 6px;
			    font-weight: 600;
			    border-radius: 4px;
			    color: #ffffff;
			}
			.right-msg{position: relative;}
			.msg-back{
				cursor: pointer;
				position: absolute;
				top: 0px;
			    right: 46px;
 			    background-color: #ffffff;
			    padding: 6px;
			    box-shadow: 0px 0px 3px 0 #333333;
			    z-index: 10;
			}
			.right-msg.color-gray{position: relative;}
			.right-msg.color-gray p{background-color: #cdcbcb!important;}
			.right-msg.color-gray img:nth-child(2){filter: brightness(0.7);margin: 5px 0;}
			.right-msg.color-gray:before{
				content: "↶";
				font-size: 16px;
    			position: absolute;
    			right: 74px;
    			top: -12px;
			}
		</style>
</head>
<body>
	<%@ include file="include/loading.jsp"%>
	<input type="hidden" value="" id="partyid" />
		<input type="hidden" value="1" id="pageno" />
		<input type="hidden" value="" id="message_id" />
		
		<input type="hidden" value="1" id="isScroll" /><!-- 是否滚动底部 0到底/1没到底 -->
		<input type="hidden" value="1" id="hasmore" /><!-- 是否有上一页 0无/1有 -->
		<input type="hidden" id="chat_token"/>
		<!-- Start Page Loading -->
		<div class="loading"><img src="<%=basePath%>img/chat/loading.gif" alt="loading-img"></div>
		<!-- End Page Loading -->
		
		<!-- Start Page BigImg -->
		<div class="bigImg"><img src="" alt="img"></div>
		<!-- End Page BigImg -->


		<!-- Start Mailbox -->
		<div class="mailbox clearfix">



			<!-- Start Mailbox Container -->
			<div class="container-mailbox">

				<!-- Start Mailbox Inbox -->
				<div class="col-md-3 col-sm-3 col-xs-3 padding-0">
					<ul class="mailbox-inbox">

						<li class="search">
							<form>
								<input type="text" class="mailbox-search" id="mailboxsearch" placeholder="Search">
								<span onclick="searchMsg()" class="searchbutton"><i class="fa fa-search"></i></span>
							</form>
							<div style="margin: 3px 0 0 10px;color:#f88282;">搜索成功后将接手该用户</div>
						</li>
						<!-- 消息列表 -->
						<div class="msg-list">

						</div>


					</ul>
				</div>
				<!-- End Mailbox Inbox -->

				<!-- Start Chat -->
				<div class="chat col-md-9 col-sm-9 col-xs-9 padding-0">

					<!-- Start Title -->
					<div class="title">
						<h1></h1>
						<small class="t_bz" style="color:black">(这是备注)</small>
			            <span onclick="openBz()" class="title-bz">设置备注</span>
			            <input onblur="setBz()" onKeyPress="if(window.event.keyCode==13) this.blur()" type="text" maxlength="50" value="" placeholder="请输入备注" class="user-bz" />
			            <div class="user-detail">
			              <span>UID：<span class="usercode"></span></span>
			              <span>账户类型：<span class="role_name"></span></span>
			              <span>推荐人：<span class="recom_parent_name"></span></span>
			              <span>登录IP：<span class="login_ip"></span></span>
			            </div>
			            <div class="user-detail-time">
			              <span>注册时间：<span class="create_time"></span></span>
			              <span>最后登录时间：<span class="last_login_time"></span></span>
			            </div>
						<div class="btn-group" role="group" aria-label="..." style="right:40px;color:black">
							<button onclick="deleteUser()" type="button" class="btn btn-icon btn-sm btn-light"><i class="fa fa-trash "></i></button>
						</div>
					</div>
					<!-- End Title -->

					<!-- Start Conv -->
					<div class="conv-loading"><img src="<%=basePath%>img/chat/loading.gif" alt="loading-img"></div>
					<ul class="conv" id="conv-all">

					</ul>
					<div class="bot-list-loading"><img src="<%=basePath%>img/chat/loading.gif" alt="loading-img">加载中...</div>
					<!-- End Conv -->

					<div class="write">
						<div class="photo-add">
							<img class="item-image" src="<%=basePath%>img/chat/icon-add-old.png" alt="">
					
							<input type="file" accept="image/*"
									name="uploader-input" 
									class="uploader-file"
									id="upload">
						</div>
						<div class="">
							<p><textarea id="send-textarea" class="textarea form-control" placeholder="ctrl+enter发送消息"
								 style="height:100px; width:100%;"></textarea></p>
<!-- 							<p>	 			  -->
<!--                       <input id="send-textarea" name="money_revise" class="textarea form-control" -->
<!--                              placeholder="ctrl+enter发送消息" style="height:100px; width:100%;"/> -->
<!-- 								 </p> -->

							<button id="send-btn" onclick="sendMsg('text')" style="float: right;" class="btn btn-default margin-l-5">发送</button>
							<button id="unsend-btn" style="float: right;" class="btn btn-default margin-l-5">发送</button>
							<button onclick="clearSendWord()" style="float: right;" type="reset" class="btn">清空</button>
						</div>
					</div>


				</div>
				<!-- End Chat -->

			</div>
			<!-- End Mailbox Container -->

		</div>
		<!-- End Mailbox -->


		<div class="fixed-right-b" onclick="goBottom()">
			<div class="un-num">0</div>
			<i class="fa fa-angle-down"></i>
		</div>
		<script type="text/javascript" src="<%=basePath%>js/jquery.min.js"></script>

<c:if test="${security.isRolesAccessible('ROLE_ROOT,ROLE_ADMIN,ROLE_CUSTOMER')}">

		<script>
			function userInfo(id){
				$('#modal_chat_user_info').modal("show");	
			}
		
		</script>
		<script>
			function htmlEncodeJQ ( str ) {
			  return $('<span/>').text( str ).html();
			}
			
			
			var chatHeight = $(".conv").height() + 40;
			$(window).resize(function() {
				chatHeight = $(".conv").height() + 40;
			});
			var setInt = null;//定时器
			// 用户列表
			function getMsgList(partyid) {
				$.ajax({
					type: 'GET',
					data: {token:$("#chat_token").val()
					},
					url: "<%=basePath%>public/adminOnlineChatAction!userlist.action",
					success: function(data) {
						var res = $.parseJSON(data);
						if (res.code == 0) {
							var msgList = res.data;
							var msgContent = '';
							if(msgList.length>0){
								if($("#pageno").val()==1){
									if(!$(".title h1").html()){
										$(".title h1").html(msgList[0].username);
									}
									if(!$("#partyid").val()){
										console.log(747464)
										$("#partyid").val(msgList[0].partyid);
										getMsgDetailList(msgList[0].partyid,false);
										getMsgDetailList(msgList[0].partyid,true);
									}
								}
							}else{
								$(".title h1").html('');
								$(".mailbox-inbox .msg-list").html('');
								$(".conv").html('');
								$("#partyid").val("");
								$(".conv-loading").hide();
								clearChatList();
								return
							}

							// 获取用户详细信息
							if(!$(".usercode").html()){
								getUserInfo();
							}
							
							
							
							var liLen = $(".mailbox-inbox .msg-list li").length;
							if(liLen==0){
								for (var i = 0; i < msgList.length; i++) {
									var remarks='';
									if(msgList[i].remarks){
										remarks = '('+msgList[i].remarks+')'
									}
									msgContent += '<li class="item-li '
										+ msgList[i].username +'" partyid="'
										+ msgList[i].partyid +'"><a href="#" class="item clearfix"><img src="'
										+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><span class="from"><span class="fromname">'
										+ msgList[i].username + '</span><span class="remarks">'
										+ remarks +'</span></span><span class="chat_content">'
										+ htmlEncodeJQ(msgList[i].content) + '</span><span class="date">'
										+ msgList[i].updatetime + '</span>';
									if(msgList[i].unreadmsg){
										msgContent += '<span class="unread">'
										+ msgList[i].unreadmsg + '</span>'
										
										//有未读消息时刷新列表
										if(msgList[i].partyid == $("#partyid").val()&&$("#isScroll").val()==0){
											getMsgDetailList($("#partyid").val(),false)
											getMsgDetailList($("#partyid").val(),true)
										}
									}else{
										msgContent += '<span class="unread unreadhide">'
										+ msgList[i].unreadmsg + '</span>';
									}
									msgContent += '</a></li>'
								}
								$(".mailbox-inbox .msg-list").html(msgContent);
							}else{
								var tiLi = '';
								var jianNum = Math.abs(liLen - msgList.length);
								for (var i = 0; i < jianNum; i++){
									if(msgList.length<liLen){
										//减
										$(".mailbox-inbox .msg-list li:last-child").remove();
									}else{
										//加
										var remarks='';
										if(msgList[i].remarks){
											remarks = '('+msgList[i].remarks+')'
										}
										var msgContent = '<li class="item-li '
											+ msgList[i].username +'" partyid="'
											+ msgList[i].partyid +'"><a href="#" class="item clearfix"><img src="'
											+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><span class="from"><span class="fromname">'
											+ msgList[i].username + '</span><span class="remarks">'
											+ remarks +'</span></span><span class="chat_content">'
											+ htmlEncodeJQ(msgList[i].content) + '</span><span class="date">'
											+ msgList[i].updatetime + '</span>';
										if(msgList[i].unreadmsg){
											msgContent += '<span class="unread">'
											+ msgList[i].unreadmsg + '</span>';

											//有未读消息时刷新列表
											if(msgList[i].partyid == $("#partyid").val()&&$("#isScroll").val()==0){
												getMsgDetailList($("#partyid").val(),false)
												getMsgDetailList($("#partyid").val(),true)
											}
										}else{
											msgContent += '<span class="unread unreadhide">'
											+ msgList[i].unreadmsg + '</span>';
										}
										msgContent += '</a></li>'
										$(".mailbox-inbox .msg-list").append(msgContent);
									}
								}
								for (var i = 0; i < msgList.length; i++) {
									if(msgList[i].unreadmsg){
										$(".mailbox-inbox .msg-list li").eq(i).find(".unread").html(msgList[i].unreadmsg);
										$(".mailbox-inbox .msg-list li").eq(i).find(".unread").removeClass("unreadhide");

										
										//有未读消息时刷新列表
										if(msgList[i].partyid == $("#partyid").val()&&$("#isScroll").val()==0){
											getMsgDetailList($("#partyid").val(),false)
											getMsgDetailList($("#partyid").val(),true)
										}
									}else{
										$(".mailbox-inbox .msg-list li").eq(i).find(".unread").addClass("unreadhide");
										$(".mailbox-inbox .msg-list li").eq(i).find(".unread").html(0);
									}
									$(".mailbox-inbox .msg-list li").eq(i).find(".fromname").html(msgList[i].username);
									var remarks='';
									if(msgList[i].remarks){
										remarks = '('+msgList[i].remarks+')'
									}
									$(".mailbox-inbox .msg-list li").eq(i).find(".remarks").html(remarks);
									$(".mailbox-inbox .msg-list li").eq(i).find(".chat_content").html(htmlEncodeJQ(msgList[i].content));
									$(".mailbox-inbox .msg-list li").eq(i).find(".date").html(msgList[i].updatetime);
									var newClass = "item-li "+msgList[i].username
									$(".mailbox-inbox .msg-list li").eq(i).attr("class", newClass);
									$(".mailbox-inbox .msg-list li").eq(i).attr("partyid", msgList[i].partyid);
								}
							}
							var onClass = "."+$(".title h1").html();
							$(".item-li").removeClass("item-li-on");
							var partyid_old = $("#partyid").val();
							var partyDiv = $(".item-li[partyid='"+partyid_old+"']");
							if(partyDiv.length==0){
								$(".item-li").eq(0).click();
							}else{
								$(".item-li[partyid='"+partyid_old+"']").addClass("item-li-on");
							}
							
							if(partyid){
								
								var offetTop = $(".msg-list li[partyid="+partyid+"]").offset().top-120;
								$(".msg-list").animate({
									scrollTop: offetTop
								}, 500);
								$(".msg-list li[partyid="+partyid+"]").click();
								
							}
							
							// 未读数
							$(".un-num").html($(".item-li-on .unread").html());
							console.log($(".item-li-on .unread").html())
							if($(".item-li-on .unread").html()>0){
								$(".un-num").show();
							}else{
								$(".un-num").hide();
							}
							
						}
					},
					error: function(err) {}
				})
			}
			
			
			//创建新用户消息列表
			function searchMsg(usercode) {
				var username = $("#mailboxsearch").val();

				if (typeof(usercode) != "undefined" && usercode != null){
					username = usercode
				}
				$.ajax({
					type: 'GET',
					data: {
						uid: username,
						token:$("#chat_token").val()
					},
					url: "<%=basePath%>public/adminOnlineChatAction!create.action",
					success: function(data) {
						var res = $.parseJSON(data);
						if (res.code == 0) {
							getMsgList(res.data);
						}else if (res.code == 1) {
							swal({title:res.msg,timer:2000});
						}
						$("#mailboxsearch").val('');
					},
					error: function(err) {}
				})
			}
			
			//点击查看图片
			$(".conv").on("click",".img-loading",function(e){
				var _this = $(this);
				_this.attr("src", '<%=basePath%>img/chat/loading.gif');
				$.ajax({
					type: 'GET',
					data: {
						message_id: $(this).attr("id"),
						token:$("#chat_token").val()
					},
					url: "<%=basePath%>public/adminOnlineChatAction!getOnlineChatMessage.action",
					success: function(data) {
						var res = $.parseJSON(data);
						_this.attr("src", res.data.content);
						_this.removeClass("img-loading");
					},
					error: function(err) {}
				})
				e.stopPropagation();
			})

			
			var isOpenBot = false;//是否初始化置底操作，每次进入用户聊天页面初始一次
			function bottomSetInterval(){
				// 定时检查是否需要滚动条置底操作
				setInterval(function(){
					var scrollHeight = $(".conv")[0].scrollHeight
					if($("#isScroll").val()!=1){
						$(".fixed-right-b").hide();
						$(".conv").animate({
							scrollTop: scrollHeight
						}, 500);
					}else{
						$(".fixed-right-b").show();
					}
				},700)
			}
			
			function goBottom(){
				$(".bot-list-loading").show();
				// 滚动到底部
				setTimeout(function(){
					// 滚动到底部
					var scrollHeight = $(".conv")[0].scrollHeight
					$(".conv").animate({
						scrollTop: scrollHeight
					}, 500);
				},1000);
				getMsgDetailList($("#partyid").val(),false)
				getMsgDetailList($("#partyid").val(),true)
				
				$("#isScroll").val(0);
				
				// 未读数
				$(".un-num").html(0);
				$(".un-num").hide();
			}
			
			// 聊天记录列表
			function getMsgDetailList(partyid,show_img) {
				if(show_img){return}
				$.ajax({
					type: 'GET',
					data: {
						message_id: '',
						partyid: partyid,
						show_img: show_img,
						token:$("#chat_token").val()
					},
					url: "<%=basePath%>public/adminOnlineChatAction!list.action",
					success: function(data) {
						$(".conv-loading").hide();
						var res = $.parseJSON(data);
						if (res.code == 0) {
							if(show_img){
								var msgDetailList = res.data;
								for (var i = 0; i < msgDetailList.length; i++) {
									var classId = "." + msgDetailList[i].id;
									if($(classId).hasClass("img-show")){
										$(classId).attr('src',msgDetailList[i].content);
										$(classId).removeClass("img-loading");
									}
								}
							}else{
								var msgDetailList = res.data.reverse();
								var msgDetailContent = '';
								if(msgDetailList.length>0&&!$("#message_id").val()){
									$("#message_id").val(msgDetailList[0].id);
								}
								for (var i = 0; i < msgDetailList.length; i++) {
									var divid = "#" + msgDetailList[i].id;
									if($(divid).attr("timeid")){continue}
									if($(divid).html()){continue}
									
									// if ('时间') {
										msgDetailContent += '<div id="'
										+ msgDetailList[i].id + '"><li class="date">'
										+ msgDetailList[i].createtime + '</li>'
									// }
									if (msgDetailList[i].send_receive == 'receive') {
										var className = "";
										if (msgDetailList[i].delete_status == -1) {
											className = "color-gray";
										}else{
											className = "";
										}
										if (msgDetailList[i].type == 'img') {
											if(msgDetailList[i].content){//如果图片存在就显示图片
												msgDetailContent += '<li class="right-msg '+className+'"><img src="'
												+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><img src="'
												+ msgDetailList[i].content +'" alt="img" class="img-show '
												+ msgDetailList[i].id +'"></li></div>'
											}else{//如果图片不存在就显示加载中图片
												msgDetailContent += '<li class="right-msg '+className+'"><img src="'
												+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><img src="'
												+ '<%=basePath%>img/chat/clickshow.png' +'" alt="img" class="img-show img-loading" id="'
												+ msgDetailList[i].id +'"></li></div>'
											}
										}else{
											msgDetailContent += '<li class="right-msg '+className+'"><img src="'
											+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><p class="ballon color1">'
											+ htmlEncodeJQ(msgDetailList[i].content) + '</p></li></div>'
										}
								
									} else if (msgDetailList[i].send_receive == 'send') {
								
										if (msgDetailList[i].type == 'img') {
											if(msgDetailList[i].content){
												msgDetailContent += '<li><img src="'
												+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><br><img src="'
												+ msgDetailList[i].content +'" alt="img" class="img-show '
												+ msgDetailList[i].id +'"></li></div>'
											}else{
												msgDetailContent += '<li><img src="'
												+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><br><img src="'
												+ '<%=basePath%>img/chat/clickshow.png' +'" alt="img" class="img-show img-loading" id="'
												+ msgDetailList[i].id +'"></li></div>'
											}
										}else{
											msgDetailContent += '<li><img src="'
											+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><p class="ballon color2">'
											+ htmlEncodeJQ(msgDetailList[i].content) + '</p></li></div>'
										}
								
									}
									if(partyid==$("#partyid").val()){
										$(".conv").append(msgDetailContent);
										msgDetailContent = '';
									}
								}
								
							}
							
							
							if($("#isScroll").val() == 0){
								console.log(7777789877)
								var scrollHeight = $(".conv")[0].scrollHeight
								$(".conv").animate({
									scrollTop: scrollHeight
								}, 500);
								if(!isOpenBot){
									// 滚动到底部
									setTimeout(function(){
										// 滚动到底部
										var scrollHeight = $(".conv")[0].scrollHeight
										$(".conv").animate({
											scrollTop: scrollHeight
										}, 500);
									},1000);
									isOpenBot = true;
								}
							}
							$(".bot-list-loading").hide();
							
						}
					},
					error: function(err) {
						$(".bot-list-loading").hide();
					}
				})
			}
			
			var timeout;//用于存储定时器的变量
		 //鼠标点击事件，用户判断是否用鼠标拖动滚动条
			$(".conv").mousedown(function(e) {
					timeout= setTimeout(function() {
						$(".conv").scroll(function() {
							$("#isScroll").val(1);
							var scroH = $(".conv").scrollTop();  //滚动高度
							var scrollHeight = $(".conv")[0].scrollHeight
							scroH = Math.ceil(scroH);
							chatHeight = $(".conv").height() + 40;
							if (scroH+chatHeight>=scrollHeight){  //滚动条滑到底部啦
								$("#isScroll").val(0);
							} else{
								$("#isScroll").val(1);
							}
						})
						 
					}, 200);//鼠标按下0.2秒后发生事件
			});
			$(document).mouseup(function() {
					clearTimeout(timeout);//清理掉定时器
			});
			$(document).mouseout(function() {
					clearTimeout(timeout);//清理掉定时器
			});
			
			
			// 鼠标滚动事件
			$(document).on("mousewheel DOMMouseScroll", function (e) {
				chatHeight = $(".conv").height() + 40;
				$("#isScroll").val(1);
				var scroH = $(".conv").scrollTop();  //滚动高度
				var scrollHeight = $(".conv")[0].scrollHeight
				scroH = Math.ceil(scroH);
				var delta = (e.originalEvent.wheelDelta && (e.originalEvent.wheelDelta > 0 ? 1 : -1)) ||  // chrome & ie
								(e.originalEvent.detail && (e.originalEvent.detail > 0 ? -1 : 1));              // firefox
				if (delta > 0) {
					// 向上滚
					if(scroH<200&&scrollHeight>chatHeight){
						var page_no = $("#pageno").val();
						page_no ++;
						$("#pageno").val(page_no);
						if($("#hasmore").val() == 1){
							getMsgDetailMoreList($("#partyid").val(),false);
							getMsgDetailMoreList($("#partyid").val(),true);
						}
					}
				} else if (delta < 0) {
					// 向下滚
					if (scroH+chatHeight+100>=scrollHeight){  //滚动条滑到底部啦
						$("#isScroll").val(0);
					} else{
						$("#isScroll").val(1);
					}
				}
			});
			
			// 上一页消息内容列表
			function getMsgDetailMoreList(partyid,show_img) {
				$.ajax({
					type: 'GET',
					data: {
						message_id: $("#message_id").val(),
						partyid: partyid,
						show_img: show_img,
						token:$("#chat_token").val()
					},
					url: "<%=basePath%>public/adminOnlineChatAction!list.action",
					success: function(data) {
						var res = $.parseJSON(data);
						if (res.code == 0) {
							if(show_img){
								var msgDetailList = res.data;
								for (var i = 0; i < msgDetailList.length; i++) {
									var classId = "." + msgDetailList[i].id;
									if($(classId).hasClass("img-show")){
										$(classId).attr('src',msgDetailList[i].content);
										$(classId).removeClass("img-loading");
									}
								}
							}else{
								var msgDetailList = res.data;
								var msgDetailContent = '';
								if(msgDetailList.length==0){
									$("#hasmore").val(0);
									return
								}
								$("#message_id").val(msgDetailList[msgDetailList.length-1].id);
								console.log(msgDetailList[msgDetailList.length-1].id)
								for (var i = 0; i < msgDetailList.length; i++) {
									var divid = "#" + msgDetailList[i].id;
									if($(divid).html()){continue}
									// if ('时间') {
										msgDetailContent += '<div id="'
										+ msgDetailList[i].id + '"><li class="date">'
										+ msgDetailList[i].createtime + '</li>'
									// }
									if (msgDetailList[i].send_receive == 'receive') {
										var className = "";
										if (msgDetailList[i].delete_status == -1) {
											className = "color-gray";
										}else{
											className = "";
										}
										if (msgDetailList[i].type == 'img') {
											if(msgDetailList[i].content){
												msgDetailContent += '<li class="right-msg '+className+'"><img src="'
												+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><br><img src="'
												+ msgDetailList[i].content +'" alt="img" class="img-show '
												+ msgDetailList[i].id +'"></li></div>'
											}else{
												msgDetailContent += '<li class="right-msg '+className+'"><img src="'
												+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><br><img src="'
												+ '<%=basePath%>img/chat/clickshow.png' +'" alt="img" class="img-show img-loading" id="'
												+ msgDetailList[i].id +'"></li></div>'
											}
										}else{
											msgDetailContent += '<li class="right-msg '+className+'"><img src="'
											+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><p class="ballon color1">'
											+ htmlEncodeJQ(msgDetailList[i].content) + '</p></li></div>'
										}
				
									} else if (msgDetailList[i].send_receive == 'send') {
				
										if (msgDetailList[i].type == 'img') {
											if(msgDetailList[i].content){
												msgDetailContent += '<li><img src="'
												+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><img src="'
												+ msgDetailList[i].content +'" alt="img" class="img-show '
												+ msgDetailList[i].id +'"></li></div>'
											}else{
												msgDetailContent += '<li><img src="'
												+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><img src="'
												+ '<%=basePath%>img/chat/clickshow.png' +'" alt="img" class="img-show img-loading" id="'
												+ msgDetailList[i].id +'"></li></div>'
											}
										}else{
											msgDetailContent += '<li><img src="'
											+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><p class="ballon color2">'
											+ htmlEncodeJQ(msgDetailList[i].content) + '</p></li></div>'
										}
				
									}
									$(".conv").prepend(msgDetailContent);
									msgDetailContent = '';
								}
								// $(".conv>li").eq(0).before(msgDetailContent);
							}
			
						}
					},
					error: function(err) {}
				})
			}
			
			
			
			// 图片点击事件
			$(".conv").on("click","img",function(){
				var imgUrl = $(this).attr("src");
				
				if(imgUrl.indexOf("clickshow") >= 0 ||imgUrl.indexOf("loading") >= 0 ){return}
// 				$(".bigImg").show();
// 				$(".bigImg img").attr("src", imgUrl);

				var newwin=window.open()
				var height="height:"+window.screen.height+";";
				var width="width:"+window.screen.width+";";
// 				var height="height:"+window.screen.availHeight+";";
// 				var width="width:"+window.screen.availWidth+";";
				newwin.document.write("<div style='display: table-cell;text-align: center;vertical-align: middle;background: #0e0e0e;"+height+width+"'><img src="+imgUrl+" /></div>")
			})
			$(".bigImg").click(function(){
				$(".bigImg").hide();
			})
			
			function clearChatList(){
				// 更改用户聊天记录时，清除定时器
				clearInterval(setInt);
				setInt = null;
				$(".t_bz").html("");
				$(".usercode").html("");
				$(".recom_parent_name").html("");
// 				$(".login_status").html("");
// 				$(".login_status").removeClass("login_status-success");
				$(".login_ip").html("");
				$(".last_login_time").html("");
				$(".create_time").html("");
				$(".role_name").html("");
				$(".conv").html("");
				
				// 重置是否有上一页 0无/1有
				$("#hasmore").val(1);
				
				
				$("#isScroll").val(0);
				$("#pageno").val(1);
				$(".conv").html('');
			}
			
			//点击左侧用户列表
			$(".msg-list").on("click",".item-li",function(){
				isOpenBot = false;
				$(".conv-loading").show();
				
				clearChatList();
				
				var partyid = $(this).attr("partyid");
				$("#partyid").val(partyid);
				$(".title h1").html($(this).find(".fromname").html());
				
				$(".item-li").removeClass("item-li-on");
				$(this).addClass("item-li-on");
				
				//清空翻页message_id
				$("#message_id").val("");
				
				// 获取用户详细信息
				getUserInfo();
				
				// 重新打开定时器，定时刷新用户列表与聊天记录
				setInt = setInterval(function(){
					getMsgList();
					
					/* var scroH = $(".conv").scrollTop();  //滚动高度
					var scrollHeight = $(".conv")[0].scrollHeight
					if($("#partyid").val()&&$("#isScroll").val()==0&&scroH>0&&(scrollHeight-scroH)<=340){
						//聊天记录在第一页 滚动条在底部 无未读数 当前有聊天时，刷新聊天记录
						getMsgDetailList($("#partyid").val(),false)
						getMsgDetailList($("#partyid").val(),true)
					} */
				},1000)
				
				getMsgDetailList(partyid,false);
				getMsgDetailList(partyid,true);
			})
			
			
			// 定时刷新用户列表与聊天记录
			function chat_interval(){
				getMsgList();
				setInt = setInterval(function(){
					getMsgList();
					
					/* var scroH = $(".conv").scrollTop();  //滚动高度
					var scrollHeight = $(".conv")[0].scrollHeight
					if($("#partyid").val()&&$("#isScroll").val()==0&&scroH>0&&(scrollHeight-scroH)<=340){
						//聊天记录在第一页 滚动条在底部 无未读数 当前有聊天时，刷新聊天记录
						getMsgDetailList($("#partyid").val(),false)
						getMsgDetailList($("#partyid").val(),true)
					} */
				},1000)
			}
			function chat_interval_clear(){
				clearInterval(setInt);
				setInt = null;
			}
			$(".panel-body-chat").keypress(function (e){
				var stat = false;
				if(e.keyCode == 10){
					sendMsg("text");
				}
			}) 
			
			$("#unsend-btn").hide();
			$("#send-btn").show();
			//补全0
		    function completeDate(value) {
		        return value < 10 ? "0"+value:value;
		    }
			// 发送消息
			function sendMsg(type) {
				if(!$("#send-textarea").val()){
					swal("请输入内容");
					return
				}
				if(!$("#send-textarea").val().trim()){
					swal("请输入有效内容");
					return
				}
				/* $("#unsend-btn").show();
				$("#send-btn").hide();
				$("#send-textarea").attr("disabled","disabled") */
				var nowDate = new Date();
				var send_time_stmp = nowDate.getTime();
				console.log(send_time_stmp)
				var mon = nowDate.getMonth() + 1;       
				var day = nowDate.getDate();
				var colon = ":";
		        var h = nowDate.getHours();
		        var m = nowDate.getMinutes();
				var currDate = (mon<10?"0"+mon:mon) + "-"+(day<10?"0"+day:day)+ " " + completeDate(h) + colon + completeDate(m);
				var addCont = '<div id="'
				+ send_time_stmp +'"><li class="date">'
				+ currDate +'</li><li class="right-msg"><img src="'
				+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><p class="ballon color1">'
				+ $("#send-textarea").val() +'<span class="msg-img"></span></p></li></div>';
				
				$(".conv").append(addCont);
				
					$("#isScroll").val(0)
					$.ajax({
						type: 'POST',
// 						timeout: 8000,
						data: {
							type: type,
							content: encodeURI($("#send-textarea").val()),
							partyid: $("#partyid").val(),
							token:$("#chat_token").val(),
							send_time_stmp: send_time_stmp
						},
						url: "<%=basePath%>public/adminOnlineChatAction!send.action",
						success: function(data) {
							var res = $.parseJSON(data);
							var msgId = "#" + res.data.send_time_stmp;
							if (res.code == 0) {
								$(msgId).remove();
								getMsgDetailList($("#partyid").val(),false);
								getMsgDetailList($("#partyid").val(),true);
								$("#send-textarea").val('');
								
							}else if (res.code == 1) {
								$(msgId).find(".msg-img").addClass("msg-img-null");
								swal(res.msg);
							}
							/* $("#unsend-btn").hide();
							$("#send-btn").show();
							$("#send-textarea").removeAttr("disabled") */
							$("#send-textarea").focus();
						},
						error: function(err) {
							var msgId = "#" + send_time_stmp;
							$(msgId).find(".msg-img").addClass("msg-img-null");
							$("#send-textarea").val('');
							/* $("#unsend-btn").hide();
							$("#send-btn").show();
							$("#send-textarea").removeAttr("disabled") */
							$("#send-textarea").focus();
						}
					})
					$("#send-textarea").val('');
			}

			// 清除输入框文字
			function clearSendWord() {
				$("#send-textarea").val('');
			}
			
			// 删除聊天
			function deleteUser(){
				swal({
					title : "是否确认移除?",
					text : "移除当前列表后聊天记录不会删除，产生新消息该用户将重新分配给在线客服",
					type : "warning",
					showCancelButton : true,
					confirmButtonColor : "#DD6B55",
					confirmButtonText : "确认",
					cancelButtonText: "取消",
// 					closeOnConfirm : false
				}, function(isConfirm){
					if (isConfirm) {
						
						$.ajax({
							type: 'GET',
							data: {
								partyid: $("#partyid").val(),
								token:$("#chat_token").val()
							},
							url: "<%=basePath%>public/adminOnlineChatAction!del.action",
							success: function(data) {
								var res = $.parseJSON(data);
								if (res.code == 0) {
									swal("已移除!");
                
									$("#partyid").val('');
									$(".title h1").html('');
// 									clearInterval(setInt);
// 									setInt = null;
									clearChatList();
									getMsgList();
								}
							},
							error: function(err) {}
						})
					} 
				});
				
			}
			
			// 图片上传
			$(".photo-add").click(function(){
				$('#upload').click();
			})
			$('#upload').on('change', function (event) {
				
				
				var $file = event.currentTarget;
				var formData = new FormData();
				var file = $file.files[0];
				var windowURL = window.URL || window.webkitURL;
				var dataURL = windowURL.createObjectURL(file);
				var timeId = new Date().getTime();
				
				
				
				if(!file){return}
				
				
				var loadingImg = '<div timeid="'
					+ timeId +'"><li class="date"></li><li class="right-msg" id="loadingimg"><img src="'
					+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><br><img timeId="'
					+ timeId +'" src="'
					+ '<%=basePath%>img/chat/loading.gif' +'" alt="img" class="img-show img-loading"></li></div>'
				
				
				
				$(".conv").append(loadingImg);
				goBottom();
				
				if (!/image\/\w+/.test(file.type)) {
					alert("只能选择图片");
					return false;
				}
				if(file.size<102400){
					sendFile(file,timeId,dataURL);
					return
				}
				
				var reader = new FileReader();
				reader.onloadend = function () {
					

					// 创建对象
					var imgUp = new Image();
					// 改变图片的src
					imgUp.src = reader.result;
					imgUp.onload = function(){

						let canvasWidth = imgUp.width //图片原始长宽
			            let canvasHeight = imgUp.height
			            /* let img = new Image() */
						/* imgUp.src = res.path */
			            let canvas = document.createElement('canvas');
			            let ctx = canvas.getContext('2d')
			            canvas.width = 500
			            var rateImg = 500*canvasHeight/canvasWidth;
			            canvas.height = rateImg
			            // canvas.width = canvasWidth*0.6
			            // canvas.height = canvasHeight*0.6
			            
			            ctx.drawImage(imgUp, 0, 0, canvas.width, canvas.height)
			            canvas.toBlob(function(fileSrc) {
			              sendFile(fileSrc,timeId,dataURL);
			            })
				    };
					
					
				}
				if (file) {
					reader.readAsDataURL(file);
				}
			});
			
			function sendFile(file,timeId,dataURL){
				console.log(timeId,"timeId")
// 				var send_time_stmp = nowDate.getTime();
				var formData = new FormData();
				formData.append("file",file);
				/* formData.append("content",reader.result); */
				formData.append("partyid",$("#partyid").val());
				formData.append("type","img");
				formData.append("token",$("#chat_token").val());
// 				formData.append("send_time_stmp",send_time_stmp);
				
				$.ajax({
						url: "<%=basePath%>public/adminOnlineChatAction!send.action",
						dataType:'json',
						type:'POST',
// 						async: false,
						data: formData,
						processData : false, // 使数据不做处理
						contentType : false, // 不要设置Content-Type请求头
						success: function(data){
							// var res = $.parseJSON(data);
							var res = data;
							
							if (res.code == 0) {
								var timeChoose = $("[timeid="+ timeId +"]");
								timeChoose.attr("id", res.data.chat_id);
								timeChoose.find(".date").html(res.data.updatetime);
								timeChoose.find(".img-show").attr("src",dataURL);
								timeChoose.find(".img-show").removeClass("img-loading");
								getMsgDetailList($("#partyid").val(),false);
								getMsgDetailList($("#partyid").val(),true);
								// $(".item-image").attr("src", "<%=basePath%>img/chat/icon-add-old.png");
							}else if (res.code == 1) {
// 								var msgId = "#" + res.data.send_time_stmp;
// 								$(msgId).find(".msg-img").addClass("msg-img-null");
								swal(res.msg);
							}
							/* $("#loadingimg").remove(); */
							$('#upload').val("");
						},
						error:function(response){
// 							var msgId = "#" + send_time_stmp;
// 							$(msgId).find(".msg-img").addClass("msg-img-null");
							$("#loadingimg").remove();
							$('#upload').val("");
								// console.log(response);
						}
				});
			}
			
			function getUserInfo(){
				$.ajax({
					type: 'GET',
					data: {
						partyid: $("#partyid").val(),
						token:$("#chat_token").val()
					},
					url: "<%=basePath%>public/adminOnlineChatAction!getUserInfo.action",
					success: function(data) {
						var res = $.parseJSON(data);
						if (res.code == 0) {
							if(res.data.partyId==$("#partyid").val()){

					            var remarks = "";
					            if(res.data.remarks){
					            	remarks = "(" + res.data.remarks + ")";
					            }
								$(".t_bz").html(remarks);
								$(".usercode").html(res.data.usercode);
								$(".recom_parent_name").html(res.data.recom_parent_name);
								$(".login_ip").html(res.data.login_ip);
								$(".last_login_time").html(res.data.last_login_time);
								$(".create_time").html(res.data.create_time);
								var role_name = "";
								if(!res.data.role_name){
									role_name = "游客"
								}
								if(res.data.role_name=="MEMBER"){
									role_name = "正式用户"
								}
								if(res.data.role_name=="GUEST"){
									role_name = "演示用户"
								}
								if(res.data.role_name=="AGENT"){
									role_name = "代理商"
								}
								if(res.data.role_name=="AGENTLOW"){
									role_name = "代理商"
								}
								$(".role_name").html(role_name);
								
							}
						}
					},
					error: function(err) {}
				})
				
			}
			
			function openBz(){
		        $(".title-bz").hide();
		        $(".user-bz").show();
		        $(".user-bz").focus();
		    }
		      
		    function setBz(){
		        $(".title-bz").show();
		        $(".user-bz").hide();
				$.ajax({
					type: 'GET',
					data: {
						partyid: $("#partyid").val(),
						remarks: encodeURI($(".user-bz").val()),
						token:$("#chat_token").val()
					},
					url: "<%=basePath%>public/adminOnlineChatAction!resetRemarks.action",
					success: function(data) {
						var res = $.parseJSON(data);
						if (res.code == 0) {
							$(".user-bz").val("");
							getUserInfo();
						}
					},
					error: function(err) {}
				})
				
			}
			
		    function getChatToken(){
				$.ajax({
					type: 'GET',
					async:false,
					data: {
						
					},
					url: "<%=basePath%>public/adminOnlineChatAction!getChatToken.action",
					success: function(data) {
						var res = $.parseJSON(data);
						if (res.code == 0) {
							$("#chat_token").val(res.data);
						}
					},
					error: function(err) {}
				})
				
			}
		    
		    // 右键事件
		    document.getElementById("conv-all").oncontextmenu = function(e){
	    	　　return false;
	    	}
		    $(document).click(function (e) {
		        var $target = $(e.target);    //点击表情选择按钮和表情选择框以外的地方 隐藏表情选择框
		        if (!$target.is('.msg-back')) {
		        	$(".msg-back").remove();
		        }
		    });
		    $(".conv").on("mousedown",".right-msg .ballon",function(e){
				  if($(this).parent().hasClass("color-gray")){return}
		          if(3 == e.which){
		        	 	$(".msg-back").remove();
						var htmlCon = '<div class="msg-back">撤回</div>'
		          }
		          $(this).parent().append(htmlCon);
			})
			
			$(".conv").on("click",".msg-back",function(e){
				var messageId = $(this).parent().parent().attr("id");
				var _this = $(this);
				swal({
					title : "是否确认撤回?",
					text : "",
					type : "warning",
					showCancelButton : true,
					confirmButtonColor : "#DD6B55",
					confirmButtonText : "确认",
					cancelButtonText: "取消",
// 					closeOnConfirm : false
				}, function(isConfirm){
					if (isConfirm) {
						
						$.ajax({
							type: 'GET',
							async:false,
							data: {
								message_id: messageId,
								token:$("#chat_token").val()
							},
							url: "<%=basePath%>public/adminOnlineChatAction!deleteOnlineChatMessage.action",
							success: function(data) {
								var res = $.parseJSON(data);
								if (res.code == 0) {
									_this.parent().addClass("color-gray");
									$(".msg-back").remove();
									setTimeout(function(){swal("已撤回！");},500)
								}else{
									setTimeout(function(){swal(res.msg);},500)
								}
							},
							error: function(err) {}
						})
						
					} 
				});
				
				
			})
		</script>
</c:if>
</body>
</html>