<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>每日一题 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .container { max-width: 1200px; margin: 0 auto; padding: 1rem; }
        .hero-section { background: linear-gradient(135deg, #1e293b, #0f172a); border-radius: 1rem; padding: 2rem; text-align: center; }
        .badge-easy { background: #22c55e; }
        .badge-medium { background: #f59e0b; }
        .badge-hard { background: #ef4444; }
        .problem-table { width: 100%; border-collapse: collapse; }
        .problem-table th, .problem-table td { padding: 0.75rem; text-align: left; border-bottom: 1px solid #334155; }
        .btn-primary, .btn-secondary { display: inline-block; padding: 0.5rem 1rem; border-radius: 0.5rem; text-decoration: none; }
        .btn-primary { background: #22d3ee; color: #0f172a; }
        .btn-secondary { background: #334155; color: #e2e8f0; }
        .loading { text-align: center; padding: 2rem; color: #8aa1bd; }
    </style>
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">题炼星程</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/home">首页</a>
        <a href="${pageContext.request.contextPath}/problems">题库</a>
        <a href="${pageContext.request.contextPath}/contest" class="active">每日一题</a>
        <a href="${pageContext.request.contextPath}/profile">个人中心</a>
        <a href="/algorithm_training_system_war/login" onclick="localStorage.removeItem('token');localStorage.removeItem('user');">退出</a>
    </div>
</nav>

<div class="container">
    <div id="contestInfo"><div class="loading">加载中...</div></div>
    <div id="contestActions" style="margin-top: 2rem; display: none;">
        <button onclick="joinContest()" class="btn-primary" style="font-size: 1.2rem; padding: 1rem 3rem;">参加比赛</button>
    </div>
    <div id="contestRankings" class="profile-card" style="margin-top: 2rem; display: none;">
        <h3 style="color: #22d3ee; margin-bottom: 1.5rem;">比赛排行榜</h3>
        <table class="problem-table">
            <thead><tr><th>排名</th><th>用户</th><th>得分</th><th>通过数</th><th>总用时</th></tr></thead>
            <tbody id="rankingTable"></tbody>
        </table>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
    var apiToday = CONTEXT_PATH + '/api/contests/today';
    var apiJoin = CONTEXT_PATH + '/api/contests/today/join';

    function getToken() { return localStorage.getItem('token'); }
    function formatDate(dateStr) {
        if (!dateStr) return '-';
        var date = new Date(dateStr);
        return isNaN(date.getTime()) ? dateStr : date.toLocaleString('zh-CN');
    }
    function showMessage(msg, type) { toast(msg, type || 'info'); }
    function escapeHtml(str) {
        if (str == null) return '';
        return String(str).replace(/[&<>]/g, function(m) {
            if (m === '&') return '&amp;';
            if (m === '<') return '&lt;';
            if (m === '>') return '&gt;';
            return m;
        });
    }

    if (typeof checkAuth === 'function') checkAuth(); else if (!getToken()) window.location.href = CONTEXT_PATH + '/login';

    loadContest();

    async function loadContest() {
        try {
            var res = await fetch(apiToday, { headers: { 'Authorization': 'Bearer ' + getToken() } });
            var result = await res.json();
            if (result.code === 200 && result.data) {
                displayContest(result.data);
                loadContestRankings(result.data.id);
            } else {
                document.getElementById('contestInfo').innerHTML = '<div class="hero-section"><h2>今日比赛暂未生成</h2><p>请稍后再来查看</p></div>';
            }
        } catch (err) {
            console.error(err);
            document.getElementById('contestInfo').innerHTML = '<div class="loading">加载失败，请刷新重试</div>';
        }
    }

    function displayContest(contest) {
        var html = '<div class="hero-section">' +
            '<h1>' + escapeHtml(contest.title) + '</h1>' +
            '<p>比赛日期: ' + formatDate(contest.contestDate) + '</p>' +
            '<p>状态: <span class="badge badge-easy">' + escapeHtml(contest.status) + '</span></p>' +
            '</div>';
        document.getElementById('contestInfo').innerHTML = html;
        document.getElementById('contestActions').style.display = 'block';
    }

    async function joinContest() {
        try {
            var res = await fetch(apiJoin, {
                method: 'POST',
                headers: { 'Authorization': 'Bearer ' + getToken() }
            });
            var result = await res.json();
            if (result.code === 200) {
                showMessage('加入比赛成功！', 'success');
                window.location.href = CONTEXT_PATH + '/problems';
            } else {
                showMessage(result.message || '加入失败', 'error');
            }
        } catch (err) {
            console.error(err);
            showMessage('网络错误', 'error');
        }
    }

    async function loadContestRankings(contestId) {
        try {
            var res = await fetch(CONTEXT_PATH + '/api/contests/' + contestId + '/rankings', {
                headers: { 'Authorization': 'Bearer ' + getToken() }
            });
            var result = await res.json();
            if (result.code === 200 && result.data && result.data.length > 0) {
                displayRankings(result.data);
                document.getElementById('contestRankings').style.display = 'block';
            }
        } catch (err) {
            console.error('加载排行榜失败', err);
        }
    }

    function displayRankings(rankings) {
        var tbody = document.getElementById('rankingTable');
        var html = '';
        for (var i = 0; i < rankings.length; i++) {
            var r = rankings[i];
            var rank = i + 1;
            var medal = rank === 1 ? '🥇' : rank === 2 ? '🥈' : rank === 3 ? '🥉' : '';
            html += '<tr>' +
                '<td><strong>' + medal + ' ' + rank + '</strong></td>' +
                '<td>用户 ' + escapeHtml(r.userId) + '</td>' +
                '<td><strong style="color: #22d3ee;">' + r.totalScore + '</strong></td>' +
                '<td>' + r.acceptedCount + '</td>' +
                '<td>' + r.totalRuntimeMs + ' ms</td>' +
                '</tr>';
        }
        tbody.innerHTML = html;
    }
</script>
</body>
</html>