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
<nav class="navbar">
    <div class="nav-brand">题炼星程 - 管理后台</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="active">数据看板</a>
        <a href="${pageContext.request.contextPath}/admin/problems">题目管理</a>
        <a href="${pageContext.request.contextPath}/admin/users">用户管理</a>
        <a href="${pageContext.request.contextPath}/home">返回前台</a>
        <a href="/algorithm_training_system_war/login" onclick="localStorage.removeItem('token');localStorage.removeItem('user');">退出</a>
    </div>
</nav>

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

<script>
    // ==================== 公共函数定义（不依赖 common.js） ====================
    function getUser() {
        const userStr = localStorage.getItem('user');
        if (!userStr) return null;
        try {
            return JSON.parse(userStr);
        } catch (e) {
            return null;
        }
    }

    function getToken() {
        return localStorage.getItem('token');
    }

    function checkAuth() {
        if (!getToken() || !getUser()) {
            window.location.href = '${pageContext.request.contextPath}/login';
        }
    }

    function showMessage(msg, type) {
        alert(msg);  // 简单实现，可替换为自定义弹窗
    }

    function logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '${pageContext.request.contextPath}/login';
    }

    function checkAdmin() {
        const user = getUser();
        if (!user || user.role !== 'admin') {
            showMessage('无权访问管理后台', 'error');
            setTimeout(() => {
                window.location.href = '${pageContext.request.contextPath}/home';
            }, 2000);
        }
    }

    // ==================== 页面业务逻辑 ====================
    checkAuth();
    checkAdmin();
    loadDashboard();

    async function loadDashboard() {
        try {
            const usersResponse = await fetch('${pageContext.request.contextPath}/api/admin/users', {
                headers: {
                    'Authorization': 'Bearer ' + getToken()
                }
            });
            const usersResult = await usersResponse.json();
            if (usersResult.code === 200) {
                document.getElementById('totalUsers').textContent = usersResult.data.length;
            }

            const problemsResponse = await fetch('${pageContext.request.contextPath}/api/admin/problems', {
                headers: {
                    'Authorization': 'Bearer ' + getToken()
                }
            });
            const problemsResult = await problemsResponse.json();
            if (problemsResult.code === 200) {
                document.getElementById('totalProblems').textContent = problemsResult.data.length;
            }

            document.getElementById('totalSubmissions').textContent = '-';
            document.getElementById('todayActive').textContent = '-';
        } catch (error) {
            console.error('加载失败:', error);
            showMessage('加载数据失败', 'error');
        }
    }
</script>
</body>
</html>