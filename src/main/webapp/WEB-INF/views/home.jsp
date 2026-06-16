<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>首页 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/inc/navbar.jsp">
    <jsp:param name="activePage" value="home"/>
</jsp:include>
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
            <a href="${pageContext.request.contextPath}/problems" class="btn btn-primary" style="padding: 1rem 2.5rem; font-size: 1.1rem;">浏览题库</a>
            <a href="${pageContext.request.contextPath}/contest" class="btn btn-secondary" style="padding: 1rem 2.5rem; font-size: 1.1rem;">参加每日一题</a>
        </div>
    </div>
</div>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    checkAuth();
    async function loadTodayContest() {
        try {
            const res = await fetch(CONTEXT_PATH + '/api/contests/today', {
                headers: { 'Authorization': 'Bearer ' + getToken() }
            });
            const result = await res.json();
            if (result.code === 200 && result.data) {
                document.getElementById('todayProblems').textContent = '今日题目已发布';
            } else {
                document.getElementById('todayProblems').textContent = '暂无今日题目';
            }
        } catch (e) {
            document.getElementById('todayProblems').textContent = '暂无今日题目';
        }
    }
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
    loadTodayContest();
    loadStats();
</script>
</body>
</html>