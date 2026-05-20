<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<nav>

	<ul class="pager" style="text-align: left;">

		<c:choose>
			<c:when test="${page.thisPageNumber <= 1}">
				<li><a style="color: #ddd">首页</a></li>
				<li><a style="color: #ddd">上一页</a></li>
			</c:when>
			<c:otherwise>
				<li><a href="javascript:goUrl('1')">首页</a></li>
				<li><a href="javascript:goUrl('${page.thisPageNumber - 1}')">上一页</a></li>
			</c:otherwise>
		</c:choose>

		<c:forEach items="${tabs}" var="item">
			<c:choose>
				<c:when test="${item == -1}">
					<li><a style="color: #ddd; cursor: default;">...</a></li>
				</c:when>
				<c:when test="${item == page.thisPageNumber}">
					<li><a style="color:red">${item}</a></li>
				</c:when>
				<c:otherwise>
					<li><a href="javascript:goUrl('${item}')">${item}</a></li>
				</c:otherwise>
			</c:choose>
		</c:forEach>

		<c:choose>
			<c:when test="${page.lastPage or page.totalPage <= 1}">
				<li><a style="color: #ddd">下一页</a></li>
				<li><a style="color: #ddd">尾页</a></li>
			</c:when>
			<c:otherwise>
				<li><a href="javascript:goUrl('${page.thisPageNumber + 1}')">下一页</a></li>
				<li><a href="javascript:goUrl('${page.totalPage}')">尾页</a></li>
			</c:otherwise>
		</c:choose>

		<li style="margin-left:8px;color:#888;"><a style="color:#888;cursor:default;">共 ${page.totalPage} 页 / ${page.totalElements} 条</a></li>

	</ul>

</nav>
