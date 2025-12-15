<%@ page language="java" pageEncoding="utf-8"%>

<nav>
			
	<ul class="pager" style="text-align: left;">
		
	    <li><a href="javascript:goUrl('1')">首页</a></li>
	    <li><a href="javascript:goUrl('${pageNo - 1}')">上一页</a></li>	
	    	    
		<li><a style="color:red">${pageNo}</a></li>
	
		<li><a  href="javascript:goUrl('${pageNo + 1}')">下一页</a></li>
	    <li><a  href="javascript:goUrl('${1000000}')">尾页</a></li>
	    
	</ul>
	
</nav>          
