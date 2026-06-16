<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>提交记录 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/inc/navbar.jsp">
    <jsp:param name="activePage" value="submissions"/>
</jsp:include>
<div class="container">
    <h2 style="color: var(--accent); margin-bottom: 1.5rem;">我的提交记录</h2>
    <div id="submissionList" class="submission-list"><div class="loading"><span class="spinner"></span></div></div>
    <div class="pagination" id="pagination"></div>
</div>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script src="${pageContext.request.contextPath}/js/submissions.js"></script>
</body>
</html>
