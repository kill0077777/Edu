<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>강의 목록</title>
</head>
<body>
    <h2>강의 목록</h2>
    <a href="${contextPath}/lecture/form">강의 등록</a>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>번호</th>
            <th>제목</th>
            <th>강사</th>
            <th>카테고리</th>
            <th>가격</th>
            <th>상태</th>
            <th>관리</th>
        </tr>
        <c:forEach var="lecture" items="${lectures}">
            <tr>
                <td>${lecture.lectureId}</td>
                <td>
                    <a href="${contextPath}/lecture/detail/${lecture.lectureId}">
                        ${lecture.title}
                    </a>
                </td>
                <td>${lecture.instructorName}</td>
                <td>${lecture.category}</td>
                <td>
                    <c:choose>
                        <c:when test="${lecture.price == 0}">무료</c:when>
                        <c:otherwise>${lecture.price}원</c:otherwise>
                    </c:choose>
                </td>
                <td>${lecture.status}</td>
                <td>
                    <a href="${contextPath}/lecture/form?id=${lecture.lectureId}">수정</a>
                    <a href="${contextPath}/lecture/delete/${lecture.lectureId}"
                       onclick="return confirm('삭제할까요?');">삭제</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>