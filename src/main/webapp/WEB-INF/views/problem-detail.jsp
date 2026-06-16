<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>题目详情 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
</head>
<body>
<jsp:include page="/WEB-INF/views/inc/navbar.jsp">
    <jsp:param name="activePage" value="problems"/>
</jsp:include>
<div class="container">
    <div class="skeleton" style="height: 24px; width: 60%; margin-bottom: 1rem;"></div>
    <div class="skeleton skeleton-card"></div>
    <div id="problemDetail" class="problem-detail" style="display:none;"></div>
    <div class="action-buttons" style="margin-top: 2rem;">
        <a id="submitBtn" href="#" class="btn btn-primary">提交代码</a>
        <a href="${pageContext.request.contextPath}/problems" class="btn btn-secondary">返回题库</a>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
(function() {
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
    var problemId = new URLSearchParams(window.location.search).get('id');
    if (!problemId) {
        document.getElementById('problemDetail').style.display = 'block';
        document.getElementById('problemDetail').innerHTML = '错误：缺少题目ID';
        return;
    }

    fetch(CONTEXT_PATH + '/api/problems/' + problemId, {
        headers: { 'Authorization': 'Bearer ' + getToken() }
    })
    .then(function(res) { return res.json(); })
    .then(function(result) {
        document.querySelectorAll('.skeleton').forEach(function(el) { el.remove(); });
        var el = document.getElementById('problemDetail');
        el.style.display = 'block';

        if (result.code === 200 && result.data) {
            var p = result.data;

            // Render tags
            var tagsHtml = '';
            if (p.tagList && p.tagList.length > 0) {
                tagsHtml = p.tagList.map(function(t) {
                    return '<span class="tag" style="border-color:' + (t.color || '#22d3ee') + ';color:' + (t.color || '#22d3ee') + '">' + escapeHtml(t.name) + '</span>';
                }).join('');
            } else if (p.tags) {
                try {
                    var parsed = JSON.parse(p.tags);
                    if (Array.isArray(parsed)) tagsHtml = parsed.map(function(t) { return '<span class="tag" style="border-color:#22d3ee;color:#22d3ee">' + escapeHtml(t) + '</span>'; }).join('');
                } catch(e) { tagsHtml = escapeHtml(p.tags); }
            }

            var html = '<h1>' + escapeHtml(p.title) + '</h1>' +
                '<div class="problem-meta">' +
                '<span class="badge badge-' + p.difficulty + '">' + getDifficultyText(p.difficulty) + '</span>' +
                '<span>时间限制: ' + (p.timeLimitMs || 1000) + 'ms</span>' +
                '<span>内存限制: ' + (p.memoryLimitMb || 128) + 'MB</span>' +
                (tagsHtml ? '<span>' + tagsHtml + '</span>' : '') +
                '</div>' +
                '<div class="problem-description">' +
                '<h3>📖 题目描述</h3>' + marked.parse(escapeHtml(p.description || '暂无描述'));

            if (p.inputDescription) html += '<h3>📥 输入格式</h3>' + marked.parse(escapeHtml(p.inputDescription));
            if (p.outputDescription) html += '<h3>📤 输出格式</h3>' + marked.parse(escapeHtml(p.outputDescription));

            // Examples
            if (p.examples) {
                html += '<h3>📝 示例</h3>';
                try {
                    var exArr = JSON.parse(p.examples);
                    if (Array.isArray(exArr)) {
                        for (var i = 0; i < exArr.length; i++) {
                            html += '<div class="code-block"><strong>示例 ' + (i+1) + ':</strong><br>' +
                                '<span style="color:#8aa1bd">输入:</span> ' + escapeHtml(JSON.stringify(exArr[i].input, null, 2)) + '<br>' +
                                '<span style="color:#8aa1bd">输出:</span> ' + escapeHtml(JSON.stringify(exArr[i].output, null, 2)) + '</div>';
                        }
                    }
                } catch(e) {
                    html += '<div class="code-block">' + escapeHtml(p.examples) + '</div>';
                }
            }

            if (p.hint) html += '<h3>💡 提示</h3>' + marked.parse(escapeHtml(p.hint));
            if (p.constraints) html += '<h3>🔒 约束条件</h3>' + marked.parse(escapeHtml(p.constraints));
            html += '</div>';

            // Language support
            var langs = ['python', 'java', 'cpp', 'javascript'];
            html += '<div style="margin-top: 1rem; display:flex; gap:0.5rem; align-items:center; flex-wrap:wrap;">' +
                '<span style="color:var(--text-secondary)">可用语言:</span>';
            langs.forEach(function(l) {
                html += '<span class="badge badge-info">' + getLanguageName(l) + '</span>';
            });
            html += '</div>';

            el.innerHTML = html;
            document.getElementById('submitBtn').href = CONTEXT_PATH + '/submit?id=' + p.id;
        } else {
            el.innerHTML = '<div class="empty-state"><div class="empty-icon">⚠</div><p>' + (result.message || '加载失败') + '</p></div>';
        }
    })
    .catch(function(err) {
        console.error(err);
        document.querySelectorAll('.skeleton').forEach(function(el) { el.remove(); });
        var el = document.getElementById('problemDetail');
        el.style.display = 'block';
        el.innerHTML = '<div class="empty-state"><div class="empty-icon">⚠</div><p>网络错误，请刷新重试</p></div>';
    });
})();
</script>
</body>
</html>
