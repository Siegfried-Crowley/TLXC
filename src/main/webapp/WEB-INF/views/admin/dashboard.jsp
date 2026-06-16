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
        toast(msg, type || 'info');  // 使用 toast 通知
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
            showMessage('加载数据失败', 'error');
        }
    }
</script>
</body>
</html>