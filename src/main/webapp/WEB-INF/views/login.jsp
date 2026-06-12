<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>登录 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="login-container">
    <div class="login-box">
        <h1>题炼星程</h1>
        <p class="subtitle">算法实训平台</p>
        <form id="loginForm" onsubmit="return handleLogin(event)">
            <div class="form-group">
                <label for="username">用户名</label>
                <input type="text" id="username" required placeholder="请输入用户名">
            </div>
            <div class="form-group">
                <label for="password">密码</label>
                <input type="password" id="password" required placeholder="请输入密码">
            </div>
            <button type="submit" class="btn-primary">登录</button>
        </form>
        <div class="login-footer">
            <p>还没有账号？<a href="${pageContext.request.contextPath}/register">立即注册</a></p>
        </div>
    </div>
</div>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    async function handleLogin(event) {
        event.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch(CONTEXT_PATH + '/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            const result = await response.json();
            if (result.code === 200) {
                let user = result.data.user;
                if (!user.role) user.role = (username === 'admin' ? 'admin' : 'user');
                localStorage.setItem('token', result.data.token);
                localStorage.setItem('user', JSON.stringify(user));
                window.location.href = CONTEXT_PATH + '/home';
            } else {
                alert(result.message || '登录失败');
            }
        } catch (error) {
            console.error('登录错误:', error);
            alert('网络错误，请稍后重试');
        }
        return false;
    }
</script>
</body>
</html>