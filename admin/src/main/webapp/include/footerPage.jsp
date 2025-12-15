<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<nav>
			
	<ul class="pager" style="text-align: left;">
          
	<%-- <c:if test="page.thisPageNumber<=1">
	    <li><a style="color: #ddd" >首页</a></li>
	    <li><a style="color: #ddd">上一页</a></li>
	</c:if>
	<s:else>
	 	  <li><a href="javascript:goUrl('1')">首页</a></li>
	    <li><a href="javascript:goUrl('${page.thisPageNumber-1}')">上一页</a></li>
	</s:else> --%>
	
	<c:choose>
	<c:when test="${page.thisPageNumber <= 1}">
	    <li><a style="color: #ddd" >首页</a></li>
	    <li><a style="color: #ddd">上一页</a></li>
	</c:when>
	<c:otherwise>
	 	<li><a href="javascript:goUrl('1')">首页</a></li>
	    <li><a href="javascript:goUrl('${page.thisPageNumber - 1}')">上一页</a></li>
	</c:otherwise>	
	</c:choose>
    
	<!-- 分页数字标签开始 -->
	 <!-- <s:iterator value="tabs" var="item"> -->
	 <c:forEach items="${tabs}" var="item">
	 	<c:choose>
		<c:when test="${item == page.thisPageNumber}">
		 	<li><a style="color: #ddd">${item}</a></li>
		</c:when>
		<c:otherwise>
		 	<li><a href="javascript:goUrl('${item}')">${item}</a></li>
		</c:otherwise>	
		</c:choose>
     <!-- </s:iterator> -->
     </c:forEach>    
	<!-- 分页数字标签结束 -->
	
	<c:choose>
	<c:when test="${page.lastPage}">
	    <li><a style="color: #ddd" >下一页</a></li>
	    <li><a style="color: #ddd" >尾页</a></li>
	</c:when>
	<c:otherwise>
	 	<li><a  href="javascript:goUrl('${page.thisPageNumber + 1}')">下一页</a></li>
	    <li><a  href="javascript:goUrl('${page.totalPage}')">尾页</a></li>
	</c:otherwise>	
	</c:choose>

	</ul>
            
</nav>
