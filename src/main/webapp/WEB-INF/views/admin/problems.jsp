<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>题目管理 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/inc/admin-navbar.jsp">
    <jsp:param name="activePage" value="admin-problems"/>
</jsp:include>
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
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';

    function showCreateForm() {
        showProblemForm(null);
    }

    function showEditForm(id) {
        fetch(CONTEXT_PATH + '/api/problems/' + id, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        })
            .then(function(res) { return res.json(); })
            .then(function(data) {
                if (data.code === 200) showProblemForm(data.data);
                else toast('加载题目失败', 'error');
            })
            .catch(function(err) { toast('网络错误', 'error'); });
    }

    function showProblemForm(problem) {
        var isEdit = problem && problem.id;
        var title = isEdit ? '编辑题目' : '新建题目';
        var html = '<div id="problemFormOverlay" style="position:fixed;top:0;left:0;right:0;bottom:0;background:rgba(0,0,0,0.6);z-index:1000;display:flex;align-items:center;justify-content:center;">' +
            '<div style="background:#1a2a3a;padding:2rem;border-radius:8px;width:700px;max-height:80vh;overflow-y:auto;">' +
            '<h3 style="color:#22d3ee;margin-bottom:1rem;">' + title + '</h3>' +
            '<form id="problemForm">' +
            '<div style="display:grid;grid-template-columns:1fr 1fr;gap:1rem;">' +
            '<div><label>标题</label><input name="title" value="' + escapeHtml(problem ? problem.title : '') + '" style="width:100%;padding:8px;background:#0f1a2a;border:1px solid #2a3a52;color:#fff;"></div>' +
            '<div><label>难度</label><select name="difficulty" style="width:100%;padding:8px;background:#0f1a2a;border:1px solid #2a3a52;color:#fff;">' +
            '<option value="easy"' + (problem && problem.difficulty === 'easy' ? ' selected' : '') + '>简单</option>' +
            '<option value="medium"' + (problem && problem.difficulty === 'medium' ? ' selected' : '') + '>中等</option>' +
            '<option value="hard"' + (problem && problem.difficulty === 'hard' ? ' selected' : '') + '>困难</option>' +
            '</select></div>' +
            '<div><label>标签</label><input name="tags" value="' + escapeHtml(problem ? problem.tags : '') + '" style="width:100%;padding:8px;background:#0f1a2a;border:1px solid #2a3a52;color:#fff;"></div>' +
            '<div><label>状态</label><select name="status" style="width:100%;padding:8px;background:#0f1a2a;border:1px solid #2a3a52;color:#fff;">' +
            '<option value="published"' + (problem && problem.status === 'published' ? ' selected' : '') + '>发布</option>' +
            '<option value="draft"' + (problem && problem.status === 'draft' ? ' selected' : '') + '>草稿</option>' +
            '</select></div>' +
            '</div>' +
            '<div style="margin-top:0.5rem;"><label>题目描述</label><textarea name="description" rows="4" style="width:100%;padding:8px;background:#0f1a2a;border:1px solid #2a3a52;color:#fff;">' + escapeHtml(problem ? problem.description : '') + '</textarea></div>' +
            '<div style="margin-top:0.5rem;"><label>输入格式</label><textarea name="inputDescription" rows="2" style="width:100%;padding:8px;background:#0f1a2a;border:1px solid #2a3a52;color:#fff;">' + escapeHtml(problem ? problem.inputDescription : '') + '</textarea></div>' +
            '<div style="margin-top:0.5rem;"><label>输出格式</label><textarea name="outputDescription" rows="2" style="width:100%;padding:8px;background:#0f1a2a;border:1px solid #2a3a52;color:#fff;">' + escapeHtml(problem ? problem.outputDescription : '') + '</textarea></div>' +
            '<div style="margin-top:0.5rem;"><label>示例</label><textarea name="examples" rows="2" style="width:100%;padding:8px;background:#0f1a2a;border:1px solid #2a3a52;color:#fff;">' + escapeHtml(problem ? problem.examples : '') + '</textarea></div>' +
            '<div style="margin-top:0.5rem;"><label>starter_code</label><textarea name="starterCode" rows="3" style="width:100%;padding:8px;background:#0f1a2a;border:1px solid #2a3a52;color:#fff;font-family:monospace;">' + escapeHtml(problem ? problem.starterCode : '') + '</textarea></div>' +
            '<div style="margin-top:1rem;display:flex;gap:0.5rem;justify-content:flex-end;">' +
            '<button type="button" onclick="closeProblemForm()" class="btn-small">取消</button>' +
            '<button type="submit" class="btn-small btn-primary">' + (isEdit ? '保存' : '创建') + '</button>' +
            '</div>' +
            '</form></div></div>';
        var div = document.createElement('div');
        div.innerHTML = html;
        document.body.appendChild(div);

        document.getElementById('problemForm').onsubmit = function(e) {
            e.preventDefault();
            var form = this;
            var data = {};
            for (var i = 0; i < form.elements.length; i++) {
                var el = form.elements[i];
                if (el.name) data[el.name] = el.value;
            }
            var method = isEdit ? 'PUT' : 'POST';
            var url = isEdit ? CONTEXT_PATH + '/api/admin/problems/' + problem.id : CONTEXT_PATH + '/api/admin/problems';
            fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + getToken() },
                body: JSON.stringify(data)
            })
                .then(function(res) { return res.json(); })
                .then(function(result) {
                    if (result.code === 200) {
                        toast(isEdit ? '保存成功' : '创建成功', 'success');
                        closeProblemForm();
                        location.reload();
                    } else {
                        toast(result.message || '操作失败', 'error');
                    }
                })
                .catch(function(err) { toast('网络错误', 'error'); });
        };
    }

    function closeProblemForm() {
        var overlay = document.getElementById('problemFormOverlay');
        if (overlay) overlay.remove();
    }

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
                        '<button onclick="showEditForm(' + p.id + ')" class="btn-small">编辑</button> ' +
                        '<button onclick="deleteProblem(' + p.id + ')" class="btn-small">删除</button>' +
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

    function deleteProblem(id) {
        confirmDialog('确定删除？', function() {
            fetch(CONTEXT_PATH + '/api/admin/problems/' + id, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + getToken() }
            })
                .then(function(res) { return res.json(); })
                .then(function(data) {
                    if (data.code === 200) {
                        toast('删除成功', 'success');
                        location.reload();
                    } else {
                        toast(data.message || '删除失败', 'error');
                    }
                })
                .catch(function(err) { toast('网络错误', 'error'); });
        });
    }
</script>
</body>
</html>
