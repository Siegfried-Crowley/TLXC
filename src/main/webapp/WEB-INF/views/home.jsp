<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>首页 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">题炼星程</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/home" class="active">首页</a>
        <a href="${pageContext.request.contextPath}/problems">题库</a>
        <a href="${pageContext.request.contextPath}/submissions">提交记录</a>
        <a href="${pageContext.request.contextPath}/wrongbook">错题本</a>
        <a href="${pageContext.request.contextPath}/rankings">排行榜</a>
        <a href="${pageContext.request.contextPath}/stats">学习统计</a>
        <a href="${pageContext.request.contextPath}/ai">AI助手</a>
        <a href="${pageContext.request.contextPath}/profile">个人中心</a>
        <a href="${pageContext.request.contextPath}/admin/dashboard">管理后台</a>   <!-- 新增 -->
        <a href="/algorithm_training_system_war/login" onclick="localStorage.removeItem('token');localStorage.removeItem('user');">退出</a>
    </div>
</nav>
<div class="container">
    <div class="hero-section">
        <h1>欢迎使用题炼星程</h1>
        <p>提升你的算法技能，从这里开始</p>
    </div>
    <div class="stats-grid">
        <div class="stat-card"><h3>今日题目</h3><p id="todayProblems">加载中...</p></div>
        <div class="stat-card"><h3>我的积分</h3><p id="myPoints">0</p></div>
        <div class="stat-card"><h3>提交次数</h3><p id="submissionCount">0</p></div>
        <div class="stat-card"><h3>通过题目</h3><p id="acceptedCount">0</p></div>
    </div>
    <div class="quick-actions">
        <h2>快速开始</h2>
        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/problems" class="btn-primary">浏览题库</a>
            <a href="${pageContext.request.contextPath}/contest" class="btn-secondary">参加每日一题</a>
        </div>
    </div>
</div>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    checkAuth();
    async function loadStats() {
        try {
            const response = await fetch(CONTEXT_PATH + '/api/stats/overview', {
                headers: { 'Authorization': 'Bearer ' + getToken() }
            });
            const result = await response.json();
            if (result.code === 200) {
                document.getElementById('myPoints').textContent = result.data.totalPoints || 0;
                document.getElementById('submissionCount').textContent = result.data.totalSubmissions || 0;
                document.getElementById('acceptedCount').textContent = result.data.acceptedCount || 0;
            }
        } catch (error) {
            console.error('加载统计失败:', error);
        }
    }
    loadStats();
</script>
</body>
</html>