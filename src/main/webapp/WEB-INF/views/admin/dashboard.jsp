<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理后台 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/inc/admin-navbar.jsp">
    <jsp:param name="activePage" value="dashboard"/>
</jsp:include>

<div class="container">
    <h2 style="color: #22d3ee; margin-bottom: 2rem;">系统数据概览</h2>

    <div class="stats-grid">
        <div class="stat-card">
            <h3>总用户数</h3>
            <p id="totalUsers">0</p>
        </div>
        <div class="stat-card">
            <h3>总题目数</h3>
            <p id="totalProblems">0</p>
        </div>
        <div class="stat-card">
            <h3>总提交数</h3>
            <p id="totalSubmissions">0</p>
        </div>
        <div class="stat-card">
            <h3>今日活跃</h3>
            <p id="todayActive">0</p>
        </div>
    </div>

    <div class="quick-actions" style="margin-top: 2rem;">
        <h2>管理操作</h2>
        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/admin/problems" class="btn-primary">管理题目</a>
            <a href="${pageContext.request.contextPath}/admin/users" class="btn-secondary">管理用户</a>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    checkAuth();
    checkAdmin();
    loadDashboard();

    async function loadDashboard() {
        try {
            const response = await fetch('${pageContext.request.contextPath}/api/admin/dashboard', {
                headers: {
                    'Authorization': 'Bearer ' + getToken()
                }
            });
            const result = await response.json();
            if (result.code === 200) {
                document.getElementById('totalUsers').textContent = result.data.totalUsers || 0;
                document.getElementById('totalProblems').textContent = result.data.totalProblems || 0;
                document.getElementById('totalSubmissions').textContent = result.data.totalSubmissions || 0;
                document.getElementById('todayActive').textContent = result.data.todayActive || 0;
            }
        } catch (error) {
            console.error('加载失败:', error);
            toast('加载数据失败', 'error');
        }
    }
</script>
</body>
</html>