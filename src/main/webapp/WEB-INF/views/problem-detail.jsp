<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>题目详情 - 题炼星程</title>
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
    <div id="problemDetail" class="problem-detail">加载中...</div>
    <div class="action-buttons" style="margin-top: 2rem;">
        <a id="submitBtn" href="#" class="btn-primary">提交代码</a>
        <a href="${pageContext.request.contextPath}/problems" class="btn-secondary">返回题库</a>
    </div>
</div>
<script>
    (function() {
        // 获取上下文路径
        var path = window.location.pathname;
        var parts = path.split('/');
        var CONTEXT_PATH = (parts.length >= 2 && parts[1]) ? '/' + parts[1] : '';

        function getToken() {
            return localStorage.getItem('token');
        }

        // 检查登录
        if (!getToken()) {
            window.location.href = CONTEXT_PATH + '/login';
            return;
        }

        var problemId = new URLSearchParams(window.location.search).get('id');
        if (!problemId) {
            document.getElementById('problemDetail').innerHTML = '错误：缺少题目ID';
            return;
        }

        fetch(CONTEXT_PATH + '/api/problems/' + problemId, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        })
            .then(function(res) {
                if (!res.ok) throw new Error('HTTP ' + res.status);
                return res.json();
            })
            .then(function(result) {
                if (result.code === 200 && result.data) {
                    var p = result.data;
                    // 简单转义HTML特殊字符
                    function escape(s) {
                        if (s == null) return '';
                        return String(s).replace(/[&<>]/g, function(m) {
                            if (m === '&') return '&amp;';
                            if (m === '<') return '&lt;';
                            if (m === '>') return '&gt;';
                            return m;
                        });
                    }
                    // 组装HTML
                    var html = '<h1>' + escape(p.title) + '</h1>' +
                        '<div class="problem-meta">' +
                        '<span class="badge badge-' + p.difficulty + '">' + (p.difficulty === 'easy' ? '简单' : (p.difficulty === 'medium' ? '中等' : '困难')) + '</span>' +
                        '<span>标签: ' + (escape(p.tags) || '无') + '</span>' +
                        '</div>' +
                        '<div class="problem-description">' +
                        '<h3>题目描述</h3><p>' + (escape(p.description) || '暂无描述') + '</p>';
                    if (p.inputDescription) html += '<h3>输入格式</h3><p>' + escape(p.inputDescription) + '</p>';
                    if (p.outputDescription) html += '<h3>输出格式</h3><p>' + escape(p.outputDescription) + '</p>';
                    // 示例
                    if (p.examples) {
                        try {
                            var exArr = JSON.parse(p.examples);
                            if (Array.isArray(exArr)) {
                                for (var i = 0; i < exArr.length; i++) {
                                    html += '<div class="code-block"><strong>示例 ' + (i+1) + ':</strong><br>' +
                                        '输入: ' + escape(JSON.stringify(exArr[i].input)) + '<br>' +
                                        '输出: ' + escape(JSON.stringify(exArr[i].output)) + '</div>';
                                }
                            } else {
                                html += '<div class="code-block">' + escape(p.examples) + '</div>';
                            }
                        } catch(e) {
                            html += '<div class="code-block">' + escape(p.examples) + '</div>';
                        }
                    }
                    if (p.hint) html += '<h3>提示</h3><p>' + escape(p.hint) + '</p>';
                    if (p.constraints) html += '<h3>约束条件</h3><p>' + escape(p.constraints) + '</p>';
                    html += '</div>';
                    document.getElementById('problemDetail').innerHTML = html;
                    document.getElementById('submitBtn').href = CONTEXT_PATH + '/submit?id=' + p.id;
                } else {
                    document.getElementById('problemDetail').innerHTML = '加载失败：' + (result.message || '未知错误');
                }
            })
            .catch(function(err) {
                console.error(err);
                alert('错误详情：' + err.message);
                document.getElementById('problemDetail').innerHTML = '网络错误，请稍后重试';
            });
    })();
</script>
</body>
</html>