<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>排行榜 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/inc/navbar.jsp">
    <jsp:param name="activePage" value="rankings"/>
</jsp:include>
<div class="container">
    <h2 style="color: #22d3ee; margin-bottom: 2rem;">积分排行榜</h2>
    <table class="problem-table">
        <thead><tr><th>排名</th><th>用户名</th><th>昵称</th><th>积分</th><th>连续打卡</th></tr></thead>
        <tbody id="rankingList"><tr><td colspan="5" class="loading">加载中</td></tr></tbody>
    </table>
</div>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var contextPath = '${pageContext.request.contextPath}';
    var apiRankings = contextPath + '/api/rankings?limit=100';
</script>
<script src="${pageContext.request.contextPath}/js/rankings.js"></script>
</body>
</html>