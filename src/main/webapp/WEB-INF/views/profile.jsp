<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>个人中心 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">题炼星程</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/home">首页</a>
        <a href="${pageContext.request.contextPath}/problems">题库</a>
        <a href="${pageContext.request.contextPath}/profile" class="active">个人中心</a>
        <a href="/algorithm_training_system_war/login" onclick="localStorage.removeItem('token');localStorage.removeItem('user');">退出</a>
    </div>
</nav>

<div class="container">
    <div class="profile-card">
        <div class="profile-header">
            <div class="profile-avatar" id="userAvatar">U</div>
            <div class="profile-info">
                <h2 id="username">加载中...</h2>
                <p style="color: #8aa1bd;" id="nickname">-</p>
                <p style="color: #8aa1bd; font-size: 0.9rem;">
                    角色: <span id="role">user</span> |
                    注册时间: <span id="createdAt">-</span>
                </p>
            </div>
        </div>

        <div class="profile-stats">
            <div class="profile-stat-item">
                <div class="profile-stat-value" id="totalPoints">0</div>
                <div class="profile-stat-label">总积分</div>
            </div>
            <div class="profile-stat-item">
                <div class="profile-stat-value" id="streakDays">0</div>
                <div class="profile-stat-label">连续打卡</div>
            </div>
            <div class="profile-stat-item">
                <div class="profile-stat-value" id="submissionCount">0</div>
                <div class="profile-stat-label">提交次数</div>
            </div>
            <div class="profile-stat-item">
                <div class="profile-stat-value" id="acceptedCount">0</div>
                <div class="profile-stat-label">通过题目</div>
            </div>
        </div>
    </div>

    <div class="quick-actions">
        <h2>快速操作</h2>
        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/stats" class="btn-primary">查看详细统计</a>
            <a href="${pageContext.request.contextPath}/submissions" class="btn-secondary">查看提交记录</a>
            <a href="${pageContext.request.contextPath}/wrongbook" class="btn-secondary">查看错题本</a>
        </div>
    </div>
</div>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
</script>
<script>
    // ========== 公共辅助函数 ==========
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

    function formatDate(dateStr) {
        if (!dateStr) return '-';
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) return dateStr;
        return date.toLocaleString('zh-CN');
    }

    function showMessage(msg, type) {
        alert(msg); // 简单实现，可替换为更好看的提示
    }

    function logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '${pageContext.request.contextPath}/login';
    }

    // ========== 页面业务逻辑 ==========
    checkAuth();
    loadProfile();

    async function loadProfile() {
        try {
            const user = getUser();
            if (!user) {
                showMessage('用户信息丢失，请重新登录', 'error');
                logout();
                return;
            }

            document.getElementById('username').textContent = user.username;
            document.getElementById('nickname').textContent = user.nickname || '未设置昵称';
            document.getElementById('role').textContent = user.role === 'admin' ? '管理员' : '普通用户';
            document.getElementById('userAvatar').textContent = (user.nickname || user.username).charAt(0).toUpperCase();
            document.getElementById('createdAt').textContent = formatDate(user.createdAt);
            document.getElementById('totalPoints').textContent = user.totalPoints || 0;
            document.getElementById('streakDays').textContent = user.streakDays || 0;

            // 获取提交统计
            const statsResponse = await fetch('${pageContext.request.contextPath}/api/stats/overview', {
                headers: {
                    'Authorization': 'Bearer ' + getToken()
                }
            });
            const statsResult = await statsResponse.json();

            if (statsResult.code === 200) {
                document.getElementById('submissionCount').textContent = statsResult.data.totalSubmissions || 0;
                document.getElementById('acceptedCount').textContent = statsResult.data.acceptedCount || 0;
            }
        } catch (error) {
            console.error('加载个人信息失败:', error);
            showMessage('加载个人信息失败', 'error');
        }
    }
</script>
</body>
</html>