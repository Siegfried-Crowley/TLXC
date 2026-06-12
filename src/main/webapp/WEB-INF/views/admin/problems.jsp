<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>题目管理 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">题炼星程 - 管理后台</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard">数据看板</a>
        <a href="${pageContext.request.contextPath}/admin/problems" class="active">题目管理</a>
        <a href="${pageContext.request.contextPath}/admin/users">用户管理</a>
        <a href="${pageContext.request.contextPath}/home">返回前台</a>
        <a href="#" onclick="logout()">退出</a>
    </div>
</nav>
<div class="container">
    <div style="display: flex; justify-content: space-between; margin-bottom: 2rem;">
        <h2 style="color: #22d3ee;">题目管理</h2>
        <button onclick="showCreateForm()" class="btn-primary">新建题目</button>
    </div>
    <table class="problem-table">
        <thead>
        <tr><th>ID</th><th>标题</th><th>难度</th><th>状态</th><th>创建时间</th><th>操作</th></tr>
        </thead>
        <tbody id="problemList">
        <tr><td colspan="6" class="loading">加载中</td></tr>
        </tbody>
    </table>
</div>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';

    function getUser() {
        var s = localStorage.getItem('user');
        try { return s ? JSON.parse(s) : null; } catch(e) { return null; }
    }
    function getToken() { return localStorage.getItem('token'); }
    function checkAuth() { if (!getToken() || !getUser()) window.location.href = CONTEXT_PATH + '/login'; }
    function logout() { localStorage.removeItem('token'); localStorage.removeItem('user'); window.location.href = CONTEXT_PATH + '/login'; }
    function checkAdmin() {
        var u = getUser();
        if (!u || u.role !== 'admin') {
            alert('无权访问');
            window.location.href = CONTEXT_PATH + '/home';
        }
    }
    function formatDate(d) {
        if (!d) return '-';
        var date = new Date(d);
        return isNaN(date.getTime()) ? d : date.toLocaleString('zh-CN');
    }
    function getDifficultyText(diff) {
        var map = { 'easy':'简单', 'medium':'中等', 'hard':'困难' };
        return map[diff] || diff;
    }
    function escapeHtml(str) {
        if (str == null) return '';
        str = String(str);
        return str.replace(/[&<>]/g, function(m) {
            if (m === '&') return '&amp;';
            if (m === '<') return '&lt;';
            if (m === '>') return '&gt;';
            return m;
        });
    }
    function showCreateForm() { alert('请使用API或数据库添加题目'); }

    checkAuth();
    checkAdmin();

    fetch(CONTEXT_PATH + '/api/admin/problems', {
        headers: { 'Authorization': 'Bearer ' + getToken() }
    })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.code === 200) {
                var tbody = document.getElementById('problemList');
                var problems = data.data;
                if (!problems || problems.length === 0) {
                    tbody.innerHTML = '<td><td colspan="6">暂无题目</td></tr>';
                    return;
                }
                var html = '';
                for (var i = 0; i < problems.length; i++) {
                    var p = problems[i];
                    html += '<tr>' +
                        '<td>' + escapeHtml(p.id) + '</td>' +
                        '<td>' + escapeHtml(p.title) + '</td>' +
                        '<td><span class="badge badge-' + p.difficulty + '">' + getDifficultyText(p.difficulty) + '</span></td>' +
                        '<td>' + (escapeHtml(p.status) || '正常') + '</td>' +
                        '<td>' + formatDate(p.createdAt) + '</td>' +
                        '<td>' +
                        '<a href="' + CONTEXT_PATH + '/problem-detail?id=' + p.id + '" class="btn-small">查看</a> ' +
                        '<button onclick="deleteProblem(' + p.id + ')" class="btn-small">删除</button>' +
                        '</td>' +
                        '</tr>';
                }
                tbody.innerHTML = html;
            } else {
                alert(data.message || '加载失败');
            }
        })
        .catch(function(err) {
            console.error(err);
            alert('网络错误');
        });

    function deleteProblem(id) {
        if (!confirm('确定删除？')) return;
        fetch(CONTEXT_PATH + '/api/admin/problems/' + id, {
            method: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + getToken() }
        })
            .then(function(res) { return res.json(); })
            .then(function(data) {
                if (data.code === 200) {
                    alert('删除成功');
                    location.reload();
                } else {
                    alert(data.message || '删除失败');
                }
            })
            .catch(function(err) { alert('网络错误'); });
    }
</script>
</body>
</html>