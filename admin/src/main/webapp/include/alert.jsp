<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${message != null&&message != ''}">
	<div class="kode-alert kode-alert-icon kode-alert-click alert3">
		<i class="fa fa-check"></i>
		${message}
	</div>
</c:if>

<c:if test="${error != null&&error != ''}">
	<div class="kode-alert kode-alert-icon alert6">
		<i class=" fa fa-warning"></i> <a href="#" class="closed">×</a>
		${error}
	</div>
</c:if>

<!-- Start an Alert -->
<div id="alerttop"
	class="kode-alert kode-alert-icon kode-alert-click alert6 kode-alert-top">
	<div id="alerttop_msg"></div>
</div>
<!-- End an Alert -->

<script>
	function alerttop(msg) {
		$("#alerttop_msg").html(msg);
		$("#alerttop").fadeToggle(350);
	}
	
	function sweet(msg) {
		swal(msg);
	}

	function sweet_warning(msg) {
		swal({
			title : msg,
			text : "",
			type : "warning",
			confirmButtonText : "确定"
		});
	}
	function sweet_success(msg) {
		swal({
			title : msg,
			text : "",
			type : "success",
			confirmButtonText : "确定"
		}, function() {
			window.location.reload();
		});
	}
</script>
