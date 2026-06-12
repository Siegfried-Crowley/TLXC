<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>题库 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">题炼星程</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/home">首页</a>
        <a href="${pageContext.request.contextPath}/problems" class="active">题库</a>
        <a href="${pageContext.request.contextPath}/submissions">提交记录</a>
        <a href="${pageContext.request.contextPath}/wrongbook">错题本</a>
        <a href="${pageContext.request.contextPath}/rankings">排行榜</a>
        <a href="${pageContext.request.contextPath}/stats">学习统计</a>
        <a href="${pageContext.request.contextPath}/ai">AI助手</a>
        <a href="${pageContext.request.contextPath}/profile">个人中心</a>
        <!-- 普通用户看不到管理后台链接，但即使有也不影响，因为后台有权限校验 -->
        <a href="#" onclick="logout()">退出</a>
    </div>
</nav>

<div class="container">
    <div class="filter-bar">
        <select id="difficultyFilter" onchange="loadProblems()">
            <option value="">全部难度</option>
            <option value="easy">简单</option>
            <option value="medium">中等</option>
            <option value="hard">困难</option>
        </select>
    </div>

    <table class="problem-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>题目名称</th>
            <th>难度</th>
            <th>标签</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody id="problemList">
        <tr>
            <td colspan="5">加载中...</td>
        </tr>
        </tbody>
    </table>
</div>

<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
    checkAuth();
    loadProblems();

    async function loadProblems() {
        const difficulty = document.getElementById('difficultyFilter').value;
        try {
            const url = difficulty ? CONTEXT_PATH + '/api/problems?difficulty=' + difficulty : CONTEXT_PATH + '/api/problems';
            const response = await fetch(url, {
                headers: { 'Authorization': 'Bearer ' + getToken() }
            });
            const result = await response.json();
            if (result.code === 200) {
                displayProblems(result.data);
            } else {
                alert(result.message || '加载失败');
            }
        } catch (error) {
            console.error('加载失败:', error);
            alert('网络错误');
        }
    }

    function displayProblems(problems) {
        const tbody = document.getElementById('problemList');
        if (problems.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5">暂无题目</td></tr>';
            return;
        }
        let html = '';
        for (let i = 0; i < problems.length; i++) {
            const p = problems[i];
            const difficultyText = { easy:'简单', medium:'中等', hard:'困难' }[p.difficulty] || p.difficulty;
            html += '<tr>' +
                '<td>' + escapeHtml(p.id) + '</td>' +
                '<td><a href="' + CONTEXT_PATH + '/problem-detail?id=' + p.id + '">' + escapeHtml(p.title) + '</a></td>' +
                '<td><span class="badge badge-' + p.difficulty + '">' + difficultyText + '</span></td>' +
                '<td>' + (escapeHtml(p.tags) || '-') + '</td>' +
                '<td>' +
                '<a href="' + CONTEXT_PATH + '/problem-detail?id=' + p.id + '" class="btn-small">查看</a> ' +
                '<a href="' + CONTEXT_PATH + '/submit?id=' + p.id + '" class="btn-small btn-primary">提交</a>' +
                '</td>' +
                '</tr>';
        }
        tbody.innerHTML = html;
    }

    function escapeHtml(str) {
        if (!str) return '';
        return String(str).replace(/[&<>]/g, function(m) {
            if (m === '&') return '&amp;';
            if (m === '<') return '&lt;';
            if (m === '>') return '&gt;';
            return m;
        });
    }
</script>
</body>
</html>