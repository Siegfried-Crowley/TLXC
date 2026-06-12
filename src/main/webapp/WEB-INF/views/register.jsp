<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注册 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="login-container">
    <div class="login-box">
        <h1>注册账号</h1>
        <p class="subtitle">加入题炼星程</p>

        <form id="registerForm" onsubmit="return handleRegister(event)">
            <div class="form-group">
                <label for="username">用户名</label>
                <input type="text" id="username" name="username" required placeholder="至少3个字符" minlength="3">
            </div>

            <div class="form-group">
                <label for="nickname">昵称（可选）</label>
                <input type="text" id="nickname" name="nickname" placeholder="显示名称">
            </div>

            <div class="form-group">
                <label for="password">密码</label>
                <input type="password" id="password" name="password" required placeholder="至少6个字符" minlength="6">
            </div>

            <button type="submit" class="btn-primary">注册</button>
        </form>

        <div class="login-footer">
            <p>已有账号？<a href="${pageContext.request.contextPath}/login">立即登录</a></p>
        </div>
    </div>
</div>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var contextPath = '${pageContext.request.contextPath}';
    var apiRegister = contextPath + '/api/auth/register';
</script>
<script src="${pageContext.request.contextPath}/js/register.js"></script>
</body>
</html>