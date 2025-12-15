<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="include/pagetop.jsp"%>

<!DOCTYPE html>
<html>

	<head>
	
		<%@ include file="include/head.jsp"%>
	
		<style>
			.ifr-content {
				padding-top: 0!important;
				padding-left: 0!important;
				padding-right: 0!important;
			}	
			.ifr-refresh {
				position: absolute;
				top: 45px;
				right: 10px;
				z-index: 100;
			}	
			.ifr-tab {
				margin-top: 40px;
				margin-left: 180px;
				overflow: hidden;
				background-color: #ffffff;
			}
			.ifr-tab .fa-close {
				margin-left: 5px;
			}	
			.ifr-tab-list {
				display: inline-block;
				float: left;
				height: 30px;
				line-height: 30px;
				font-size: 12px;
				padding: 0 20px;
				margin: 0 0 0 0;
				color: #acafb1;
				cursor: pointer;
			}	
			.ifr-tab-list-on {
				background-color: #e4e4e4;
				color: #565656 !important
			}
		</style>
	
	</head>

	<body>

		<div id="top1" class="clearfix">
		 	<div>
		 		<%@ include file="include/top.jsp"%>
		 	</div>		 	
		</div>
		
		<div class="sidebar clearfix">
			<%@ include file="include/menu_left.jsp"%>
		</div>
		
		<c:choose>
			<c:when test="${security.isRolesAccessible('ROLE_AGENT')}">
				<div class="ifr-tab marL0">
			         <div link="<%=basePath%>normal/adminUserAction!list.action" class="ifr-tab-list ifr-tab-list-on">用户基础管理</div>
		        </div>
		        <div class="ifr-refresh"><a href="javascript:refresh();"><i class="fa fa-refresh"></i></a></div>
				<div class="content ifr-content marL0_">
					<iframe style="width: 100%;min-height: 1000px;" src="<%=basePath%>normal/adminUserAction!list.action" id="iframepage"
						frameborder="0" scrolling="no" marginheight="0" marginwidth="0" onLoad="iFrameHeight(0)">
					</iframe>
				</div>
			</c:when>
			<c:otherwise>
	
				<!-- dapp+交易所 菜单 ######################################################################################################## -->
				<c:if test="${!security.isDappOrExchange()}">
							
					<div class="ifr-tab marL0">
				         <div link="<%=basePath%>normal/adminIndexAction!viewNew.action" class="ifr-tab-list ifr-tab-list-on">综合查询</div>
			        </div>
					<div class="ifr-refresh"><a href="javascript:refresh();"><i class="fa fa-refresh"></i></a></div>
					<div class="content ifr-content marL0_">
						<iframe style="width: 100%;min-height: 1000px;" src="<%=basePath%>normal/adminIndexAction!viewNew.action" id="iframepage"
						 	frameborder="0" scrolling="no" marginheight="0" marginwidth="0" onLoad="iFrameHeight(0)">
						 </iframe>
					</div>
		
				</c:if>
				
				<!-- 交易所 菜单 ############################################################################################################# -->
				<c:if test="${security.isDappOrExchange()}">
				
					<div class="ifr-tab ">
				         <div link="<%=basePath%>normal/adminIndexAction!viewNew.action" class="ifr-tab-list ifr-tab-list-on">综合查询</div>
			        </div>
					<div class="ifr-refresh"><a href="javascript:refresh();"><i class="fa fa-refresh"></i></a></div>
					<div class="content ifr-content marL0_">
						<iframe style="width: 100%;min-height: 1000px;" src="<%=basePath%>normal/adminIndexAction!viewNew.action" id="iframepage"
						 	frameborder="0" scrolling="no" marginheight="0" marginwidth="0" onLoad="iFrameHeight(0)">
						 </iframe>
					</div>
			
				</c:if>
	
			</c:otherwise>
	    </c:choose>
		
		<script>
			function iFrameHeight(index) {
				console.log(index, "index")
				var ifrId = "iframe" + index;
				if (index > 0) {
					$("iframe").eq(index).attr("id", ifrId);
				} else {
					ifrId = "iframepage"
				}
				var ifm = document.getElementById(ifrId);
				var subWeb = document.frames ? document.frames[ifrId].document : ifm.contentDocument;
				if (ifm != null && subWeb != null) {
					ifm.height = subWeb.body.scrollHeight;
					ifm.width = subWeb.body.scrollWidth;
				}
			}
		</script>

		<%@ include file="include/js.jsp"%>
		
	</body>

	<script>
		$(window).resize(function() {
			var ifm = document.getElementById("iframepage");
			var subWeb = document.frames ? document.frames["iframepage"].document : ifm.contentDocument;
			if (ifm != null && subWeb != null) {
				ifm.height = subWeb.body.scrollHeight + 40;
				ifm.width = subWeb.body.scrollWidth;
			}
		})
	</script>
	
	<script>
		var tabList = [];
		//点击左侧菜单添加tab
		$(".sidebar-panel a").click(function(e) {
			var title = $(this).find(".sp-title").html();
			var href = $(this).attr("href");
			var nowIndex = $(this).parent().index();
			if (nowIndex == 0) {
				$(".ifr-tab-list").removeClass("ifr-tab-list-on");
				$(".ifr-tab .ifr-tab-list").eq(0).addClass("ifr-tab-list-on");
				//iframe无刷新
				$("iframe").hide();
				$("#iframepage").show();
			} else {
				addTab(title, href, true);
			}
			e.preventDefault();
		})
		//点击头部综合查询菜单
		$(".dropdown-parent a").click(function(e) {
			$(".ifr-tab-list").removeClass("ifr-tab-list-on");
			$(".ifr-tab .ifr-tab-list").eq(0).addClass("ifr-tab-list-on");
			//iframe无刷新
			$("iframe").hide();
			$("#iframepage").show();
			e.preventDefault();
		})
		//点击头部下拉框添加tab
		$(".dropdown-menu a").click(function(e) {
			var title = $(this).find(".sp-title").html();
			var href = $(this).attr("href");
			addTab(title, href, true);
			e.preventDefault();
		})

		var menuList = [];
		var sidebarLen = $(".sidebar-panel a").length;
		var dropMenuLen = $(".dropdown-menu a").length;
		for (var i = 0; i < sidebarLen; i++) {
			menuList.push({
				url: $(".sidebar-panel a").eq(i).attr("href"),
				title: $(".sidebar-panel a").eq(i).find(".sp-title").html()
			})
		}
		for (var i = 0; i < dropMenuLen; i++) {
			menuList.push({
				url: $(".dropdown-menu a").eq(i).attr("href"),
				title: $(".dropdown-menu a").eq(i).find(".sp-title").html()
			})
		}
		//点击消息弹窗添加tab
		$("body").on("click", "#tip_alert a", function(e) {
			var title = "";
			var href = $(this).attr("href");
			var hrefNew = href.replace(/^https:\/\/[^/]+/, "");
			menuList.map(function(item) {
				var urlNew = item.url.replace(/^https:\/\/[^/]+/, "");
				if (urlNew == hrefNew) {
					title = item.title
				}
				return item
			})
			if (title) {
				addTab(title, href, true);
				e.preventDefault();
			}
		})
		//点击消息弹窗添加tab
		$("body").on("click", ".add_tab a", function(e) {
			var title = "";
			var href = $(this).attr("href");
			href = href.split("?")[0];
			var hrefNew = href.replace(/^https:\/\/[^/]+/, "");
			menuList.map(function(item) {
				var urlNew = item.url.replace(/^https:\/\/[^/]+/, "");
				if (urlNew == hrefNew) {
					title = item.title
				}
				return item
			})
			if (title) {
				addTab(title, href, true);
				e.preventDefault();
			}
		})

		function addTab(title, href, isFresh) {
			var oldHref = href;

			if(oldHref == '<%=dmUrl%>/download/#/?url=<%=adminUrl%>' || oldHref == '<%=dmUrl%>/download/#/marketing/EventsList?url=<%=adminUrl%>'
					|| oldHref == '<%=dmUrl%>/download/#/marketing/PrizeManagement?url=<%=adminUrl%>'){
				$.ajax({
					url: "<%=basePath%>mall/order/getloginPartyId.action",
					type: 'POST',
					// contentType: "application/json",
					traditional: true,
					async:false,
					success: function (data) {
						if (data.code === 200) {
							var newOldHerf = oldHref + '&loginPartyId=' + data.loginPartyId + '&username=' + data.loginName;
							oldHref = newOldHerf;
						}
					}
				});

			}

			var href = href.split("?").length == 2 ? href.split("?")[0] : href;
			var hasIndex = -1;

			var hrefNew = href.replace(/^https:\/\/[^/]+/, "");
			
			tabList.map(function(item, index) {
				var itemNew = item.replace(/^https:\/\/[^/]+/, "");
				if (itemNew == hrefNew) {
					hasIndex = index
				}
				return item;
			})
			if (hasIndex >= 0) {
				$(".ifr-tab-list").removeClass("ifr-tab-list-on");
				$(".ifr-tab .ifr-tab-list").eq(hasIndex + 1).addClass("ifr-tab-list-on");
				//iframe刷新
				// $("#iframepage").attr("src", tabList[hasIndex]);
				//iframe无刷新
				$("iframe").hide();
				var tabIndex = parseInt(hasIndex) + 1;
				$("iframe").eq(tabIndex).show();
				if (isFresh) {
					$("iframe").eq(tabIndex).attr("src", oldHref);
				}
			} else {
				var newList = '<div class="ifr-tab-list ifr-tab-list-on" link="' +
					oldHref + '">' + title + '<i class="fa fa-close"></i></div>';
				$(".ifr-tab-list").removeClass("ifr-tab-list-on");
				if ($(".ifr-tab .ifr-tab-list").length == 9) {
					$(".ifr-tab .ifr-tab-list").eq(1).remove();
					$("iframe").eq(1).remove();
					tabList.shift();
				}
				$(".ifr-tab").append(newList);
				tabList.push(href);

				//iframe刷新
				// $("#iframepage").attr("src", href);
				//iframe无刷新
				$("iframe").hide();
				var index = $("iframe").length;
				var ifr_html;

				if(oldHref == '<%=dmUrl%>/download/#/commodity-library?url=<%=adminUrl%>'){

					ifr_html = '<iframe style="width: 100%;  min-height: 1000px;" src="' +
							oldHref + '" frameborder="0" scrolling="yes" marginheight="0" marginwidth="0" onLoad="iFrameHeight(' +
							index + ')"></iframe>'
				} else {
					ifr_html = '<iframe style="width: 100%; min-height: 1000px;" src="' +
							oldHref + '" frameborder="0" scrolling="no" marginheight="0" marginwidth="0" onLoad="iFrameHeight(' +
							index + ')"></iframe>'
				}


				// var ifr_html = '<iframe style="width: 100%; min-height: 1000px;" src="' +
				// 		oldHref + '" frameborder="0" scrolling="no" marginheight="0" marginwidth="0" onLoad="iFrameHeight(' +
				// 		index + ')"></iframe>'
				$(".ifr-content").append(ifr_html);
			}
		}

		//切换tab
		$(".ifr-tab").on("click", ".ifr-tab-list", function(e) {
			var src = $(this).attr("link");
			src = src.split("?").length == 2 ? src.split("?")[0] : src;

			$(".ifr-tab-list").removeClass("ifr-tab-list-on");
			$(this).addClass("ifr-tab-list-on");
			//iframe刷新
			// $("#iframepage").attr("src", src);
			//iframe无刷新
			var hasIndex = -1;
			tabList.map(function(item, index) {
				if (item == src) {
					hasIndex = index
				}
				return item;
			})
			var tabIndex = parseInt(hasIndex) + 1;
			$("iframe").hide();
			if (hasIndex == -1) {
				$("#iframepage").show();
			} else {
				$("iframe").eq(tabIndex).show();
			}
		})

		//关闭tab
		$(".ifr-tab").on("click", ".fa-close", function(e) {
			var index = $(this).parent().index();
			var href = $(this).parent().attr("link");
			href = href.split("?").length == 2 ? href.split("?")[0] : href;

			var hasIndex = -1;
			tabList.map(function(item, index) {
				if (item == href) {
					hasIndex = index;
				}
				return item;
			})

			if ($(".ifr-tab .ifr-tab-list").eq(index).hasClass("ifr-tab-list-on")) {
				var src = $(".ifr-tab .ifr-tab-list").eq(index - 1).attr("link");
				src = src.split("?").length == 2 ? src.split("?")[0] : src;

				$(".ifr-tab-list").removeClass("ifr-tab-list-on");
				$(".ifr-tab .ifr-tab-list").eq(index - 1).addClass("ifr-tab-list-on");
				$(".ifr-tab .ifr-tab-list").eq(index).remove();
				//iframe刷新
				// $("#iframepage").attr("src", src);
				//iframe无刷新
				var nowIndex = -1;
				tabList.map(function(item, index) {
					if (item == src) {
						nowIndex = index;
					}
					return item;
				})
				var tabIndex = parseInt(nowIndex) + 2;
				$("iframe").hide();
				if (nowIndex == -1) {
					$("#iframepage").show();
				} else {
					$("iframe").eq(nowIndex + 1).show();
				}
				$("iframe").eq(tabIndex).remove();
			} else {
				var tabIndex = parseInt(hasIndex) + 1;
				$("iframe").eq(tabIndex).remove();
				$(".ifr-tab .ifr-tab-list").eq(index).remove();
			}
			tabList.splice(hasIndex, 1);
			e.stopPropagation();
		})

		function refresh() {
			var href = $(".ifr-tab-list-on").attr("link");
			var hasIndex = -1;
			tabList.map(function(item, index) {
				if (item == href) {
					hasIndex = index
				}
				return item;
			})
			var tabIndex = parseInt(hasIndex) + 1;
			$("iframe").eq(tabIndex).attr("src", href);
		}
	</script>

</html>
