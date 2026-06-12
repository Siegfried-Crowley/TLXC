<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>提交代码 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">题炼星程</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/home">首页</a>
        <a href="${pageContext.request.contextPath}/problems">题库</a>
        <a href="${pageContext.request.contextPath}/submissions">提交记录</a>
        <a href="${pageContext.request.contextPath}/profile">个人中心</a>
        <a href="/algorithm_training_system_war/login" onclick="localStorage.removeItem('token');localStorage.removeItem('user');">退出</a>
    </div>
</nav>
<div class="container">
    <div class="problem-detail" id="problemInfo"><div class="loading">加载题目信息</div></div>
    <div class="code-editor-container">
        <h2>代码编辑器</h2>
        <textarea id="codeEditor" placeholder="请在此输入Python代码..."></textarea>
        <button onclick="submitCode()" class="btn-primary">提交评测</button>
        <div id="submitResult" class="submit-result" style="display: none;">
            <h3>AI 评测结果</h3>
            <div id="resultContent"></div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
    var apiProblems = CONTEXT_PATH + '/api/problems';
    var apiSubmissions = CONTEXT_PATH + '/api/submissions';

    function showMessage(msg, type) { alert(msg); }
    function escapeHtml(str) { if (!str) return ''; return String(str).replace(/[&<>]/g, function(m) { if (m === '&') return '&amp;'; if (m === '<') return '&lt;'; if (m === '>') return '&gt;'; return m; }); }
    function getDifficultyText(diff) { var map = { easy:'简单', medium:'中等', hard:'困难' }; return map[diff] || diff; }
    function formatDate(d) { if (!d) return '-'; var date = new Date(d); return isNaN(date.getTime()) ? d : date.toLocaleString('zh-CN'); }

    if (typeof checkAuth === 'function') checkAuth(); else if (!localStorage.getItem('token')) window.location.href = CONTEXT_PATH + '/login';

    loadProblemInfo();

    async function loadProblemInfo() {
        var urlParams = new URLSearchParams(window.location.search);
        var problemId = urlParams.get('id');
        if (!problemId) { showMessage('缺少题目ID', 'error'); return; }
        try {
            var res = await fetch(apiProblems + '/' + problemId, { headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') } });
            var result = await res.json();
            if (result.code === 200) displayProblemInfo(result.data);
            else showMessage(result.message || '加载失败', 'error');
        } catch(e) { console.error(e); showMessage('网络错误', 'error'); }
    }

    function displayProblemInfo(problem) {
        document.getElementById('problemInfo').innerHTML = '<h1>' + escapeHtml(problem.title) + '</h1><div class="problem-meta"><span class="badge badge-' + problem.difficulty + '">' + getDifficultyText(problem.difficulty) + '</span></div>';
        if (problem.starterCode) document.getElementById('codeEditor').value = problem.starterCode;
    }

    async function submitCode() {
        var problemId = new URLSearchParams(window.location.search).get('id');
        var code = document.getElementById('codeEditor').value;
        if (!code.trim()) { showMessage('请输入代码', 'warning'); return; }
        showMessage('正在提交AI评测...', 'info');
        try {
            var res = await fetch(apiSubmissions, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + localStorage.getItem('token') },
                body: JSON.stringify({ problemId: parseInt(problemId), code: code })
            });
            var result = await res.json();
            if (result.code === 200) {
                displayResult(result.data);
                showMessage('评测完成', 'success');
            } else {
                showMessage(result.message || '提交失败', 'error');
            }
        } catch(e) { console.error(e); showMessage('网络错误', 'error'); }
    }

    function displayResult(data) {
        var html = '<div class="result-item"><span class="result-label">AI 分析</span><div class="result-value" style="white-space: pre-wrap;">' + escapeHtml(data.analysis) + '</div></div>';
        if (data.correct) {
            html += '<div class="result-item"><span class="result-label">结论</span><span style="color: #22c55e;">正确</span></div>';
        } else {
            html += '<div class="result-item"><span class="result-label">结论</span><span style="color: #ef4444;">不正确</span></div>';
        }
        document.getElementById('resultContent').innerHTML = html;
        document.getElementById('submitResult').style.display = 'block';
    }
</script>
</body>
</html>