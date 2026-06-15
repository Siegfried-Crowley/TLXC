<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>学习统计 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">题炼星程</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/home">首页</a>
        <a href="${pageContext.request.contextPath}/problems">题库</a>
        <a href="${pageContext.request.contextPath}/stats" class="active">学习统计</a>
        <a href="${pageContext.request.contextPath}/profile">个人中心</a>
        <a href="/algorithm_training_system_war/login" onclick="localStorage.removeItem('token');localStorage.removeItem('user');">退出</a>
    </div>
</nav>
<div class="container">
    <h2 style="color: #22d3ee; margin-bottom: 2rem;">学习统计概览</h2>
    <div class="stats-grid">
        <div class="stat-card"><h3>总提交数</h3><p id="totalSubmissions">0</p></div>
        <div class="stat-card"><h3>通过题目数</h3><p id="acceptedProblems">0</p></div>
        <div class="stat-card"><h3>通过率</h3><p id="acceptRate">0%</p></div>
        <div class="stat-card"><h3>总积分</h3><p id="totalPoints">0</p></div>
    </div>
    <div class="profile-card" style="margin-top: 2rem;">
        <h3 style="color: #22d3ee; margin-bottom: 1.5rem;">积分获取记录</h3>
        <div id="pointLogs"><div class="loading">加载中</div></div>
    </div>
</div>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var contextPath = '${pageContext.request.contextPath}';
    var apiStatsOverview = contextPath + '/api/stats/overview';
    var apiPointsLogs = contextPath + '/api/stats/points/logs';
</script>
<script src="${pageContext.request.contextPath}/js/stats.js"></script>
</body>
</html>