<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>用户管理 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/inc/admin-navbar.jsp">
    <jsp:param name="activePage" value="admin-users"/>
</jsp:include>
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
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';

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