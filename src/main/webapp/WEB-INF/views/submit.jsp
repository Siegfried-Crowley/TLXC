<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>提交代码 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.18/codemirror.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.18/theme/dracula.min.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/inc/navbar.jsp">
    <jsp:param name="activePage" value="submissions"/>
</jsp:include>
<div class="container">
    <div class="problem-detail" id="problemInfo"><div class="loading">加载题目信息...</div></div>
    <div class="code-editor-container">
        <h2><span id="problemTitle">代码编辑器</span></h2>
        <div class="editor-toolbar">
            <select id="languageSelect" class="language-select">
                <option value="python">Python</option>
                <option value="java">Java</option>
                <option value="cpp">C++</option>
                <option value="javascript">JavaScript</option>
            </select>
            <span id="editorStatus" style="color:var(--text-muted);font-size:0.85rem;"></span>
        </div>
        <textarea id="codeEditor" placeholder="在此编写代码..."></textarea>
        <div style="display:flex; gap:1rem; align-items:center;">
            <button onclick="submitCode()" class="btn btn-primary" id="submitBtn">提交评测</button>
            <span id="submitLoading" style="display:none;color:var(--text-secondary);">
                <span class="spinner" style="width:18px;height:18px;border-width:2px;vertical-align:middle;margin-right:6px;"></span> 评测中...
            </span>
        </div>
        <div id="submitResult" class="submit-result" style="display: none;">
            <h3 style="color:var(--accent);margin-bottom:1rem;">评测结果</h3>
            <div id="resultContent"></div>
        </div>
    </div>
</div>
<script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.18/codemirror.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.18/mode/python/python.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.18/mode/clike/clike.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.65.18/mode/javascript/javascript.min.js"></script>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
    var apiProblems = CONTEXT_PATH + '/api/problems';
    var apiSubmissions = CONTEXT_PATH + '/api/submissions';

    checkAuth();
    loadProblemInfo();

    // ---- CodeMirror Editor ----
    var codeEditor = CodeMirror.fromTextArea(document.getElementById('codeEditor'), {
        lineNumbers: true,
        mode: 'python',
        theme: 'dracula',
        indentUnit: 4,
        tabSize: 4,
        lineWrapping: false,
        autofocus: false,
        extraKeys: { 'Ctrl-Space': 'autocomplete' }
    });
    codeEditor.setSize(null, 400);

    // ---- Language Switch ----
    var langModes = { python: 'python', java: 'text/x-java', cpp: 'text/x-c++src', javascript: 'javascript' };
    var langPlaceholders = {
        python: '# Python 代码\nclass Solution:\n    def solve(self, input_data):\n        pass',
        java: '// Java 代码\npublic class Solution {\n    public String solve(String input) {\n        return input;\n    }\n}',
        cpp: '// C++ 代码\n#include <string>\nusing namespace std;\n\nstring solve(string input) {\n    return input;\n}',
        javascript: '// JavaScript 代码\nfunction solve(input) {\n    return input;\n}'
    };

    document.getElementById('languageSelect').addEventListener('change', function() {
        var lang = this.value;
        codeEditor.setOption('mode', langModes[lang] || 'python');

        // Set placeholder replacement — if code is empty or default, switch placeholder
        var current = codeEditor.getValue().trim();
        if (!current || current === langPlaceholders[prevLang]) {
            codeEditor.setValue(langPlaceholders[lang] || '');
        }
        prevLang = lang;
        document.getElementById('editorStatus').textContent = '当前: ' + getLanguageName(lang);
    });
    var prevLang = 'python';
    document.getElementById('editorStatus').textContent = '当前: Python';

    // Ctrl+Enter to submit
    codeEditor.setOption('extraKeys', { 'Ctrl-Enter': function() { submitCode(); } });

    // ---- Load Problem ----
    async function loadProblemInfo() {
        var urlParams = new URLSearchParams(window.location.search);
        var problemId = urlParams.get('id');
        if (!problemId) { toast('缺少题目ID', 'error'); return; }
        try {
            var res = await fetch(apiProblems + '/' + problemId, {
                headers: { 'Authorization': 'Bearer ' + getToken() }
            });
            var result = await res.json();
            if (result.code === 200) displayProblemInfo(result.data);
            else toast(result.message || '加载失败', 'error');
        } catch(e) { console.error(e); toast('网络错误', 'error'); }
    }

    function displayProblemInfo(problem) {
        document.getElementById('problemTitle').textContent = problem.title + ' - 代码编辑器';
        var tagHtml = '';
        if (problem.tagList && problem.tagList.length > 0) {
            tagHtml = problem.tagList.map(function(t) {
                return '<span class="tag" style="border-color:' + (t.color || '#22d3ee') + ';color:' + (t.color || '#22d3ee') + '">' + escapeHtml(t.name) + '</span>';
            }).join(' ');
        }
        document.getElementById('problemInfo').innerHTML = '<h1>' + escapeHtml(problem.title) + '</h1>' +
            '<div class="problem-meta">' +
            '<span class="badge badge-' + problem.difficulty + '">' + getDifficultyText(problem.difficulty) + '</span>' +
            (tagHtml ? '<span>' + tagHtml + '</span>' : '') +
            '</div>';
        if (problem.starterCode) {
            codeEditor.setValue(problem.starterCode);
        }
    }

    // ---- Submit ----
    async function submitCode() {
        var problemId = new URLSearchParams(window.location.search).get('id');
        var code = codeEditor.getValue();
        var language = document.getElementById('languageSelect').value;

        if (!code.trim()) { toast('请输入代码', 'warning'); return; }

        var btn = document.getElementById('submitBtn');
        var loader = document.getElementById('submitLoading');
        btn.disabled = true;
        loader.style.display = 'inline';
        document.getElementById('submitResult').style.display = 'none';

        try {
            var res = await fetch(apiSubmissions, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + getToken() },
                body: JSON.stringify({ problemId: parseInt(problemId), code: code, language: language })
            });
            var result = await res.json();
            if (result.code === 200) {
                displayResult(result.data);
                toast('评测完成', 'success');
            } else {
                toast(result.message || '提交失败', 'error');
            }
        } catch(e) {
            console.error(e);
            toast('网络错误', 'error');
        } finally {
            btn.disabled = false;
            loader.style.display = 'none';
        }
    }

    function displayResult(data) {
        var statusClass = 'status-' + data.status;
        var statusText = getStatusText(data.status);
        var html = '<div class="result-item"><span class="result-label">状态</span><span class="' + statusClass + '">' + statusText + '</span></div>' +
            '<div class="result-item"><span class="result-label">语言</span><span class="result-value">' + getLanguageName(data.language || 'python') + '</span></div>' +
            '<div class="result-item"><span class="result-label">运行时间</span><span class="result-value">' + (data.runtimeMs || 0) + ' ms</span></div>' +
            '<div class="result-item"><span class="result-label">测试用例</span><span class="result-value">' + (data.passedCases || 0) + ' / ' + (data.totalCases || 0) + '</span></div>';
        if (data.errorMessage) {
            html += '<div class="result-item"><span class="result-label">错误信息</span><div class="code-block" style="color:#ef4444;">' + escapeHtml(data.errorMessage) + '</div></div>';
        }
        html += '<div class="result-item"><span class="result-label">积分变化</span><span class="result-value">' + (data.scoreDelta > 0 ? '+' : '') + (data.scoreDelta || 0) + '</span></div>' +
            '<div class="result-item"><span class="result-label">提交时间</span><span class="result-value">' + formatDate(data.createdAt) + '</span></div>';
        if (data.analysis) {
            html += '<div style="margin-top:1.2rem;padding-top:1.2rem;border-top:1px solid var(--border);">' +
                '<h3 style="color:var(--accent);margin-bottom:0.8rem;">🤖 AI 分析</h3>' +
                '<div style="white-space:pre-wrap;line-height:1.7;color:var(--text-secondary);font-size:0.95rem;">' + escapeHtml(data.analysis) + '</div></div>';
        }
        document.getElementById('resultContent').innerHTML = html;
        document.getElementById('submitResult').style.display = 'block';
        document.getElementById('submitResult').scrollIntoView({ behavior: 'smooth' });
    }
</script>
</body>
</html>
