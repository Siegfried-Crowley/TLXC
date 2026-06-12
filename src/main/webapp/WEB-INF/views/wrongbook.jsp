<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>错题本 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">题炼星程</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/home">首页</a>
        <a href="${pageContext.request.contextPath}/problems">题库</a>
        <a href="${pageContext.request.contextPath}/wrongbook" class="active">错题本</a>
        <a href="${pageContext.request.contextPath}/profile">个人中心</a>
        <a href="/algorithm_training_system_war/login" onclick="localStorage.removeItem('token');localStorage.removeItem('user');">退出</a>
    </div>
</nav>
<div class="container">
    <h2 style="color: #22d3ee; margin-bottom: 2rem;">我的错题本</h2>
    <div class="filter-bar">
        <select id="statusFilter" onchange="loadWrongBooks()">
            <option value="unmastered">未掌握</option>
            <option value="mastered">已掌握</option>
            <option value="">全部</option>
        </select>
    </div>
    <div id="wrongBookList" class="submission-list"><div class="loading">加载中</div></div>
</div>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var contextPath = '${pageContext.request.contextPath}';
    var apiWrongbook = contextPath + '/api/wrongbook';
    var apiMaster = contextPath + '/api/wrongbook';
</script>
<script src="${pageContext.request.contextPath}/js/wrongbook.js"></script>
</body>
</html>