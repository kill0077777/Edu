<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%-- JSTL 중에서 core 태그를 사용하기 위해 주소를 import --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%-- 한글 인코딩 --%>
<% request.setCharacterEncoding("UTF-8"); %>

<%-- 현재 웹 애플리케이션(프로젝트)의 기본 경로를 가져옵니다. --%>
<%-- 예를 들어, 프로젝트 주소가 http://localhost:8090/moir/review/list.do --%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
<link rel="stylesheet" href="${contextPath}/resources/css/review/star.css">

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

</body>
</html>