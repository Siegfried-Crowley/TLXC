<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>用户管理 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">题炼星程 - 管理后台</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard">数据看板</a>
        <a href="${pageContext.request.contextPath}/admin/problems">题目管理</a>
        <a href="${pageContext.request.contextPath}/admin/users" class="active">用户管理</a>
        <a href="${pageContext.request.contextPath}/home">返回前台</a>
        <a href="#" onclick="logout()">退出</a>
    </div>
</nav>
<div class="container">
    <h2 style="color: #22d3ee;">用户管理</h2>
    <table class="problem-table">
        <thead>
        <tr><th>ID</th><th>用户名</th><th>昵称</th><th>角色</th><th>积分</th><th>状态</th><th>注册时间</th><th>操作</th></tr>
        </thead>
        <tbody id="userList">
        <tr><td colspan="8" class="loading">加载中</td></tr>
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
            toast('无权访问', 'error');
            window.location.href = CONTEXT_PATH + '/home';
        }
    }
    function formatDate(d) {
        if (!d) return '-';
        var date = new Date(d);
        return isNaN(date.getTime()) ? d : date.toLocaleString('zh-CN');
    }
    // 修复后的 escapeHtml：处理非字符串参数
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

    checkAuth();
    checkAdmin();

    fetch(CONTEXT_PATH + '/api/admin/users', {
        headers: { 'Authorization': 'Bearer ' + getToken() }
    })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.code === 200) {
                var tbody = document.getElementById('userList');
                var users = data.data;
                if (!users || users.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="7">暂无用户</td></table>';
                    return;
                }
                var html = '';
                for (var i = 0; i < users.length; i++) {
                    var u = users[i];
                    var roleText = (u.role === 'admin') ? '管理员' : '用户';
                    var roleClass = (u.role === 'admin') ? 'medium' : 'easy';
                    var isSelf = u.id === (function(){ try{ return JSON.parse(localStorage.getItem('user')).id; }catch(e){return null;} })();
                    html += '<tr>' +
                        '<td>' + escapeHtml(u.id) + '</td>' +
                        '<td>' + escapeHtml(u.username) + '</td>' +
                        '<td>' + (escapeHtml(u.nickname) || '-') + '</td>' +
                        '<td><span class="badge badge-' + roleClass + '">' + roleText + '</span></td>' +
                        '<td><strong style="color:#22d3ee;">' + (u.totalPoints || 0) + '</strong></td>' +
                        '<td>' + (escapeHtml(u.status) || '正常') + '</td>' +
                        '<td>' + formatDate(u.createdAt) + '</td>' +
                        '<td>' +
                        (isSelf ? '<span style="color:#8aa1bd;">当前用户</span>' :
                            '<button onclick="toggleRole(' + u.id + ',\'' + u.role + '\')" class="btn-small">' + (u.role === 'admin' ? '降为普通' : '升为管理员') + '</button> ' +
                            '<button onclick="deleteUser(' + u.id + ',\'' + escapeHtml(u.username) + '\')" class="btn-small">删除</button>') +
                        '</td>' +
                        '</tr>';
                }
                tbody.innerHTML = html;
            } else {
                toast(data.message || '加载失败', 'error');
            }
        })
        .catch(function(err) {
            console.error(err);
            toast('网络错误', 'error');
        });

    function toggleRole(id, currentRole) {
        var newRole = currentRole === 'admin' ? 'user' : 'admin';
        var msg = newRole === 'admin' ? '确定将该用户提升为管理员？' : '确定将该用户降为普通用户？';
        confirmDialog(msg, function() {
            fetch(CONTEXT_PATH + '/api/admin/users/' + id + '/role', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + getToken() },
                body: JSON.stringify({ role: newRole })
            })
                .then(function(res) { return res.json(); })
                .then(function(data) {
                    if (data.code === 200) { toast('操作成功', 'success'); location.reload(); }
                    else { toast(data.message || '操作失败', 'error'); }
                })
                .catch(function(err) { toast('网络错误', 'error'); });
        });
    }

    function deleteUser(id, username) {
        confirmDialog('确定删除用户 "' + username + '"？此操作不可恢复！', function() {
            fetch(CONTEXT_PATH + '/api/admin/users/' + id, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + getToken() }
            })
                .then(function(res) { return res.json(); })
                .then(function(data) {
                    if (data.code === 200) { toast('删除成功', 'success'); location.reload(); }
                    else { toast(data.message || '删除失败', 'error'); }
                })
                .catch(function(err) { toast('网络错误', 'error'); });
        });
    }
</script>
</body>
</html>