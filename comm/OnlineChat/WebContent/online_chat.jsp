<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
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
			.panel-body-chat .chat .conv{height: calc( 100% - 223px);overflow: auto;}
			.panel-body-chat .chat .conv .ballon {
			    max-width: 80%;
			    word-break: break-all;
					margin: 5px 0;
					padding: 4px 20px
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
			.panel-body-chat .img-loading{width: 40px;}
			.panel-body-chat .msg-list{width: 100%;height: calc( 100% - 85px);overflow: auto;}
			.panel-body-chat .right-msg{text-align: right; padding-right: 70px; padding-left: 0px!important;}
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
		</style>
</head>
<body>
	<%@ include file="include/loading.jsp"%>
	<%@ include file="include/alert.jsp"%>
	<input type="hidden" value="" id="partyid" />
		<input type="hidden" value="1" id="pageno" />
		<input type="hidden" value="" id="message_id" />
		
		<input type="hidden" value="0" id="isScroll" /><!-- 是否滚动底部 0到底/1没到底 -->
		<input type="hidden" value="1" id="hasmore" /><!-- 是否有上一页 0无/1有 -->
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
						<!-- <h1>Index Design and Some Advice <small>( mail@jonathandoe.com )</small></h1> -->
						<!-- <p><b>To:</b> Me</p> -->
						<div class="btn-group" role="group" aria-label="..." style="right:40px;">
							<!-- <button type="button" class="btn btn-icon btn-sm btn-light"><i class="fa fa-share"></i></button> -->
							<!-- <button type="button" class="btn btn-icon btn-sm btn-light"><i class="fa fa-star-o"></i></button> -->
							<button onclick="deleteUser()" type="button" class="btn btn-icon btn-sm btn-light"><i class="fa fa-trash"></i></button>
						</div>
					</div>
					<!-- End Title -->

					<!-- Start Conv -->
					<ul class="conv">

					</ul>
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

							<button onclick="sendMsg('text')" style="float: right;" class="btn btn-default margin-l-5">发送</button>
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
			<!-- <div class="un-num">0</div> -->
			<i class="fa fa-angle-down"></i>
		</div>
<%-- 		<%@ include file="include/js.jsp"%> --%>
		<script type="text/javascript" src="<%=basePath%>js/jquery.min.js"></script>

<%-- <script src="<%=basePath%>js/bootstrap/bootstrap.min.js"></script> --%>
<%-- <script type="text/javascript" src="<%=basePath%>js/plugins.js"></script> --%>
		<script>
			var chatHeight = $(".conv").height() + 40;
			$(window).resize(function() {
				chatHeight = $(".conv").height() + 40;
			});
			
			var setInt = null;//定时器
			// 用户列表
			function getMsgList(partyid) {
				$.ajax({
					type: 'GET',
					data: {
					},
					url: "<%=basePath%>normal/adminOnlineChatAction!userlist.action",
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
								return
							}
							var liLen = $(".mailbox-inbox .msg-list li").length;
							if(liLen==0){
								for (var i = 0; i < msgList.length; i++) {
									msgContent += '<li class="item-li '
										+ msgList[i].username +'" partyid="'
										+ msgList[i].partyid +'"><a href="#" class="item clearfix"><img src="'
										+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><span class="from">'
										+ msgList[i].username + '</span><span class="chat_content">'
										+ msgList[i].content + '</span><span class="date">'
										+ msgList[i].updatetime + '</span>';
									if(msgList[i].unreadmsg){
										msgContent += '<span class="unread">'
										+ msgList[i].unreadmsg + '</span>'
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
										var msgContent = '<li class="item-li '
											+ msgList[i].username +'" partyid="'
											+ msgList[i].partyid +'"><a href="#" class="item clearfix"><img src="'
											+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><span class="from">'
											+ msgList[i].username + '</span><span class="chat_content">'
											+ msgList[i].content + '</span><span class="date">'
											+ msgList[i].updatetime + '</span>';
										if(msgList[i].unreadmsg){
											msgContent += '<span class="unread">'
											+ msgList[i].unreadmsg + '</span>';
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
									}else{
										$(".mailbox-inbox .msg-list li").eq(i).find(".unread").addClass("unreadhide");
									}
									$(".mailbox-inbox .msg-list li").eq(i).find(".from").html(msgList[i].username);
									$(".mailbox-inbox .msg-list li").eq(i).find(".chat_content").html(msgList[i].content);
									$(".mailbox-inbox .msg-list li").eq(i).find(".date").html(msgList[i].updatetime);
									var newClass = "item-li "+msgList[i].username
									$(".mailbox-inbox .msg-list li").eq(i).attr("class", newClass);
									$(".mailbox-inbox .msg-list li").eq(i).attr("partyid", msgList[i].partyid);
								}
							}
							var onClass = "."+$(".title h1").html();
							$(".item-li").removeClass("item-li-on");
							$(onClass).addClass("item-li-on");
							
							if(partyid){
								
								var offetTop = $(".msg-list li[partyid="+partyid+"]").offset().top-120;
								$(".msg-list").animate({
									scrollTop: offetTop
								}, 500);
								$(".msg-list li[partyid="+partyid+"]").click();
								
							}
						}
					},
					error: function(err) {}
				})
			}
			getMsgList();
			
			//创建新用户消息列表
			function searchMsg() {
				var username = $("#mailboxsearch").val();
				$.ajax({
					type: 'GET',
					data: {
						uid: username
					},
					url: "<%=basePath%>normal/adminOnlineChatAction!create.action",
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

			// 聊天记录列表
			function getMsgDetailList(partyid,show_img) {
				$.ajax({
					type: 'GET',
					data: {
						message_id: '',
						partyid: partyid,
						show_img: show_img
					},
					url: "<%=basePath%>normal/adminOnlineChatAction!list.action",
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
								var msgDetailList = res.data.reverse();
								var msgDetailContent = '';
								if(msgDetailList.length>0){
									$("#message_id").val(msgDetailList[0].id);
								}
								for (var i = 0; i < msgDetailList.length; i++) {
									var divid = "#" + msgDetailList[i].id;
									if($(divid).html()){continue}
									// if ('时间') {
										msgDetailContent += '<div id="'
										+ msgDetailList[i].id + '"><li class="date">'
										+ msgDetailList[i].createtime + '</li>'
									// }
									if (msgDetailList[i].send_receive == 'receive') {
										
										if (msgDetailList[i].type == 'img') {
											if(msgDetailList[i].content){//如果图片存在就显示图片
												msgDetailContent += '<li class="right-msg"><img src="'
												+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><br><img src="'
												+ msgDetailList[i].content +'" alt="img" class="img-show '
												+ msgDetailList[i].id +'"></li></div>'
											}else{//如果图片不存在就显示加载中图片
												msgDetailContent += '<li class="right-msg"><img src="'
												+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><br><img src="'
												+ '<%=basePath%>img/chat/loadin<%=basePath%>img/chat/f' +'" alt="img" class="img-show img-loading '
												+ msgDetailList[i].id +'"></li></div>'
											}
										}else{
											msgDetailContent += '<li class="right-msg"><img src="'
											+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><p class="ballon color1">'
											+ msgDetailList[i].content + '</p></li></div>'
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
												+ '<%=basePath%>img/chat/loading.gif' +'" alt="img" class="img-show img-loading '
												+ msgDetailList[i].id +'"></li></div>'
											}
										}else{
											msgDetailContent += '<li><img src="'
											+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><p class="ballon color2">'
											+ msgDetailList[i].content + '</p></li></div>'
										}
								
									}
									$(".conv").append(msgDetailContent);
									msgDetailContent = ''
								}
								
							}
							

						}
					},
					error: function(err) {}
				})
			}
			
			
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
						show_img: show_img
					},
					url: "<%=basePath%>normal/adminOnlineChatAction!list.action",
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
								var msgDetailList = res.data.reverse();
								var msgDetailContent = '';
								if(msgDetailList.length==0){
									$("#hasmore").val(0);
									return
								}
								$("#message_id").val(msgDetailList[0].id);
								for (var i = 0; i < msgDetailList.length; i++) {
									var divid = "#" + msgDetailList[i].id;
									if($(divid).html()){continue}
									// if ('时间') {
										msgDetailContent += '<div id="'
										+ msgDetailList[i].id + '"><li class="date">'
										+ msgDetailList[i].createtime + '</li>'
									// }
									if (msgDetailList[i].send_receive == 'receive') {
										if (msgDetailList[i].type == 'img') {
											if(msgDetailList[i].content){
												msgDetailContent += '<li class="right-msg"><img src="'
												+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><br><img src="'
												+ msgDetailList[i].content +'" alt="img" class="img-show '
												+ msgDetailList[i].id +'"></li></div>'
											}else{
												msgDetailContent += '<li class="right-msg"><img src="'
												+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><br><img src="'
												+ '<%=basePath%>img/chat/loading.gif' +'" alt="img" class="img-show img-loading '
												+ msgDetailList[i].id +'"></li></div>'
											}
										}else{
											msgDetailContent += '<li class="right-msg"><img src="'
											+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><p class="ballon color1">'
											+ msgDetailList[i].content + '</p></li></div>'
										}
				
									} else if (msgDetailList[i].send_receive == 'send') {
				
										if (msgDetailList[i].type == 'img') {
											if(msgDetailList[i].content){
												msgDetailContent += '<li class="right-msg"><img src="'
												+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><br><img src="'
												+ msgDetailList[i].content +'" alt="img" class="img-show '
												+ msgDetailList[i].id +'"></li></div>'
											}else{
												msgDetailContent += '<li class="right-msg"><img src="'
												+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><br><img src="'
												+ '<%=basePath%>img/chat/loading.gif' +'" alt="img" class="img-show img-loading '
												+ msgDetailList[i].id +'"></li></div>'
											}
										}else{
											msgDetailContent += '<li><img src="'
											+'<%=basePath%>img/chat/my-ico2.png' + '" alt="img" class="img"><p class="ballon color2">'
											+ msgDetailList[i].content + '</p></li></div>'
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
			
			$(".msg-list").on("click",".item-li",function(){
				// 更改用户聊天记录时，清除定时器
				clearInterval(setInt);
				setInt = null;
				
				$("#isScroll").val(0);
				$("#pageno").val(1);
				$(".conv").html('');
				var partyid = $(this).attr("partyid");
				$("#partyid").val(partyid);
				$(".title h1").html($(this).find(".from").html());
				
				$(".item-li").removeClass("item-li-on");
				$(this).addClass("item-li-on");
				
				// 重新打开定时器，定时刷新用户列表与聊天记录
				setInt = setInterval(function(){
					getMsgList();
					if($("#partyid").val()&&$("#isScroll").val()==0){
						//聊天记录在第一页 滚动条在底部 当前有聊天时，刷新聊天记录
						getMsgDetailList($("#partyid").val(),false)
						getMsgDetailList($("#partyid").val(),true)
					}
				},1000)
				
				getMsgDetailList(partyid,false);
				getMsgDetailList(partyid,true);
			})
			
			
			// 定时刷新用户列表与聊天记录
			setInt = setInterval(function(){
				getMsgList();
				if($("#partyid").val()&&$("#isScroll").val()==0){
					//聊天记录在第一页 滚动条在底部 当前有聊天时，刷新聊天记录
					getMsgDetailList($("#partyid").val(),false)
					getMsgDetailList($("#partyid").val(),true)
				}
			},1000)
			
			$(document).keypress(function (e){
				var stat = false;
				if(e.keyCode == 10){
					sendMsg("text");
				}
			}) 

			// 发送消息
			function sendMsg(type) {
					$("#isScroll").val(0)
					$.ajax({
						type: 'POST',
						data: {
							type: type,
							content: $("#send-textarea").val(),
							partyid: $("#partyid").val()
						},
						url: "<%=basePath%>normal/adminOnlineChatAction!send.action",
						success: function(data) {
							var res = $.parseJSON(data);
							if (res.code == 0) {
								getMsgDetailList($("#partyid").val(),false);
								getMsgDetailList($("#partyid").val(),true);
								$("#send-textarea").val('');
								$("#send-textarea").focus();
							}else if (res.code == 1) {
								swal(res.msg);
							}
						},
						error: function(err) {}
					})
			}
			function goBottom(){
				// 滚动到底部
				var scrollHeight = $(".conv")[0].scrollHeight
				$(".conv").animate({
					scrollTop: scrollHeight
				}, 500);
				getMsgDetailList($("#partyid").val(),false)
				getMsgDetailList($("#partyid").val(),true)
				$("#isScroll").val(0)
			}

			// 清除输入框文字
			function clearSendWord() {
				$("#send-textarea").val('');
			}
			
			// 删除聊天
			function deleteUser(){
				swal({
					title : "是否确认删除?",
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
							data: {
								partyid: $("#partyid").val()
							},
							url: "<%=basePath%>normal/adminOnlineChatAction!del.action",
							success: function(data) {
								var res = $.parseJSON(data);
								if (res.code == 0) {
									swal("已删除!");
                
									$("#partyid").val('');
									$(".title h1").html('');
									clearInterval(setInt);
									setInt = null;
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
				
				console.log(file,"file")
				
				if(!file){return}
				
				
				var loadingImg = '<li class="right-msg" id="loadingimg"><img src="'
				+'<%=basePath%>img/chat/my-ico.png' + '" alt="img" class="img"><br><img src="'
				+ '<%=basePath%>img/chat/loading.gif' +'" alt="img" class="img-show img-loading"></li>'
				$(".conv").append(loadingImg);
				goBottom();
				
				if (!/image\/\w+/.test(file.type)) {
					alert("只能选择图片");
					return false;
				}
				if(file.size<102400){
					sendFile(file);
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
			              sendFile(fileSrc);
			            })
				    };
					
					
				}
				if (file) {
					reader.readAsDataURL(file);
				}
			});
			
			function sendFile(file){

				var formData = new FormData();
				formData.append("file",file);
				/* formData.append("content",reader.result); */
				formData.append("partyid",$("#partyid").val());
				formData.append("type","img");
				$.ajax({
						url: "<%=basePath%>normal/adminOnlineChatAction!send.action",
						dataType:'json',
						type:'POST',
						async: false,
						data: formData,
						processData : false, // 使数据不做处理
						contentType : false, // 不要设置Content-Type请求头
						success: function(data){
							var res = $.parseJSON(data);
							if (res.code == 0) {
								getMsgDetailList($("#partyid").val(),false);
								getMsgDetailList($("#partyid").val(),true);
								// $(".item-image").attr("src", "<%=basePath%>img/chat/icon-add-old.png");
							}else if (res.code == 1) {
								swal(res.msg);
							}
							$("#loadingimg").remove();
							$('#upload').val("");
						},
						error:function(response){
							$("#loadingimg").remove();
							$('#upload').val("");
								// console.log(response);
						}
				});
			}
			
		</script>
</body>
</html>