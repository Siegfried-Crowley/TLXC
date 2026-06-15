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
        <a href="#" onclick="logout()">退出</a>
    </div>
</nav>

<div class="container">
    <div class="filter-bar">
        <input type="text" id="keywordInput" class="search-input" placeholder="搜索题目名称..." onkeyup="if(event.key==='Enter')loadProblems()">
        <select id="difficultyFilter" onchange="loadProblems()">
            <option value="">全部难度</option>
            <option value="easy">简单</option>
            <option value="medium">中等</option>
            <option value="hard">困难</option>
        </select>
        <button onclick="loadProblems()" class="btn btn-primary btn-sm">搜索</button>
    </div>

    <div class="table-container">
        <table>
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
            <tr><td colspan="5"><div class="loading"><span class="spinner"></span><br>加载中...</div></td></tr>
            </tbody>
        </table>
    </div>
    <div class="pagination" id="pagination"></div>
</div>

<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
    var currentPage = 1;
    checkAuth();
    loadProblems();

    async function loadProblems(page) {
        if (page) currentPage = page;
        var difficulty = document.getElementById('difficultyFilter').value;
        var keyword = document.getElementById('keywordInput').value.trim();
        var params = '?page=' + currentPage + '&pageSize=15';
        if (difficulty) params += '&difficulty=' + difficulty;
        if (keyword) params += '&keyword=' + encodeURIComponent(keyword);

        document.getElementById('problemList').innerHTML = '<tr><td colspan="5"><div class="loading"><span class="spinner"></span></div></td></tr>';

        try {
            var result = await apiGet(CONTEXT_PATH + '/api/problems' + params);
            if (result.code === 200) {
                displayProblems(result.data);
            } else {
                toast(result.message || '加载失败', 'error');
            }
        } catch (error) {
            console.error(error);
            toast('网络错误', 'error');
        }
    }

    function displayProblems(data) {
        var problems = data.list || [];
        var tbody = document.getElementById('problemList');
        if (problems.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5"><div class="empty-state">暂无题目</div></td></tr>';
            document.getElementById('pagination').innerHTML = '';
            return;
        }
        var html = '';
        for (var i = 0; i < problems.length; i++) {
            var p = problems[i];
            // Render tags
            var tagsHtml = '-';
            if (p.tagList && p.tagList.length > 0) {
                tagsHtml = p.tagList.map(function(t) {
                    return '<span class="tag" style="border-color:' + (t.color || '#22d3ee') + ';color:' + (t.color || '#22d3ee') + '">' + escapeHtml(t.name) + '</span>';
                }).join(' ');
            } else if (p.tags) {
                try {
                    var parsed = JSON.parse(p.tags);
                    if (Array.isArray(parsed)) tagsHtml = parsed.map(function(t) { return '<span class="tag" style="border-color:#22d3ee;color:#22d3ee">' + escapeHtml(t) + '</span>'; }).join(' ');
                } catch(e) { tagsHtml = escapeHtml(p.tags); }
            }
            html += '<tr>' +
                '<td>' + escapeHtml(p.id) + '</td>' +
                '<td><a href="' + CONTEXT_PATH + '/problem-detail?id=' + p.id + '">' + escapeHtml(p.title) + '</a></td>' +
                '<td><span class="badge badge-' + p.difficulty + '">' + getDifficultyText(p.difficulty) + '</span></td>' +
                '<td>' + tagsHtml + '</td>' +
                '<td>' +
                '<a href="' + CONTEXT_PATH + '/problem-detail?id=' + p.id + '" class="btn btn-sm btn-secondary">查看</a> ' +
                '<a href="' + CONTEXT_PATH + '/submit?id=' + p.id + '" class="btn btn-sm btn-primary">提交</a>' +
                '</td></tr>';
        }
        tbody.innerHTML = html;

        renderPagination(document.getElementById('pagination'), data.totalPages, data.page, function(p) {
            loadProblems(p);
        });
    }
</script>
</body>
</html>
