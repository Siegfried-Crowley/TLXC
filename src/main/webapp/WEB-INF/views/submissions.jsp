<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>提交记录 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">题炼星程</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/home">首页</a>
        <a href="${pageContext.request.contextPath}/problems">题库</a>
        <a href="${pageContext.request.contextPath}/submissions" class="active">提交记录</a>
        <a href="${pageContext.request.contextPath}/profile">个人中心</a>
        <a href="/algorithm_training_system_war/login" onclick="localStorage.removeItem('token');localStorage.removeItem('user');">退出</a>
    </div>
</nav>
<div class="container">
    <h2 style="color: #22d3ee; margin-bottom: 2rem;">我的提交记录</h2>
    <div id="submissionList" class="submission-list"><div class="loading">加载中</div></div>
</div>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var contextPath = '${pageContext.request.contextPath}';
    var apiSubmissions = contextPath + '/api/submissions/my?limit=100';
</script>
<script src="${pageContext.request.contextPath}/js/submissions.js"></script>
</body>
</html>