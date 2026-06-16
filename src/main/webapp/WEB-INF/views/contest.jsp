<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>每日一题 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
    <style>
        .hero-section { background: linear-gradient(135deg, #1e293b, #0f172a); border-radius: 1rem; padding: 2rem; text-align: center; margin-bottom: 2rem; }
        .hero-section h1 { font-size: 1.8rem; margin-bottom: 0.5rem; }
        .hero-section .date { color: #8aa1bd; font-size: 0.95rem; }
        .badge-easy { background: #22c55e; }
        .badge-medium { background: #f59e0b; }
        .badge-hard { background: #ef4444; }
        .problem-card { background: #1e293b; border-radius: 0.75rem; padding: 1.5rem; margin-bottom: 1rem; border: 1px solid #334155; }
        .problem-card h2 { font-size: 1.2rem; color: #e2e8f0; margin-bottom: 0.75rem; }
        .problem-card .meta { display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap; margin-bottom: 1rem; }
        .problem-card .desc { color: #94a3b8; font-size: 0.9rem; line-height: 1.7; margin-bottom: 1rem; max-height: 4.5rem; overflow: hidden; }
        .btn-primary, .btn-secondary { display: inline-flex; align-items: center; gap: 0.4rem; padding: 0.6rem 1.4rem; border-radius: 0.5rem; text-decoration: none; font-size: 0.95rem; cursor: pointer; border: none; }
        .btn-primary { background: #22d3ee; color: #0f172a; }
        .btn-primary:hover { background: #06b6d4; }
        .btn-secondary { background: #334155; color: #e2e8f0; }
        .btn-secondary:hover { background: #475569; }
        .btn-success { background: #22c55e; color: #0f172a; }
        .btn-success:hover { background: #16a34a; }
        .loading { text-align: center; padding: 3rem; color: #8aa1bd; }
        .empty-state { text-align: center; padding: 3rem; color: #8aa1bd; }
        .empty-state .icon { font-size: 3rem; margin-bottom: 1rem; }
        .countdown { color: #f59e0b; font-weight: bold; }
        .rankings-section { margin-top: 2rem; }
        .rankings-section h3 { color: #22d3ee; margin-bottom: 1rem; }
        .rank-table { width: 100%; border-collapse: collapse; }
        .rank-table th, .rank-table td { padding: 0.75rem; text-align: left; border-bottom: 1px solid #334155; }
        .rank-table th { color: #8aa1bd; font-weight: normal; font-size: 0.85rem; }
        .rank-table .medal { font-size: 1.1rem; }
        .joined-badge { display: inline-flex; align-items: center; gap: 0.3rem; background: #1a3a3a; color: #22d3ee; padding: 0.3rem 0.8rem; border-radius: 2rem; font-size: 0.85rem; }
        .tag { display: inline-block; padding: 0.15rem 0.6rem; border-radius: 1rem; font-size: 0.8rem; border: 1px solid; }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/inc/navbar.jsp">
    <jsp:param name="activePage" value="contest"/>
</jsp:include>

<div class="container">
    <div id="contestInfo"><div class="loading">加载中...</div></div>
    <div id="contestRankings" class="rankings-section" style="display: none;"></div>
</div>

<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';
    var apiToday = CONTEXT_PATH + '/api/contests/today';
    var apiJoin = CONTEXT_PATH + '/api/contests/today/join';

    checkAuth();
    loadContest();

    async function loadContest() {
        try {
            var res = await fetch(apiToday, {
                headers: { 'Authorization': 'Bearer ' + getToken() }
            });
            var result = await res.json();
            if (result.code === 200 && result.data) {
                displayContest(result.data);
            } else {
                document.getElementById('contestInfo').innerHTML =
                    '<div class="empty-state"><div class="icon">📋</div><p>' + (result.message || '今日比赛暂未生成') + '</p></div>';
            }
        } catch (err) {
            console.error(err);
            document.getElementById('contestInfo').innerHTML = '<div class="loading">加载失败，请刷新重试</div>';
        }
    }

    function displayContest(data) {
        var contest = data.contest;
        var problems = data.problems || [];
        var participation = data.participation;

        var dateStr = contest.contestDate ? formatDateShort(contest.contestDate) : '';
        var html = '<div class="hero-section">' +
            '<h1>' + escapeHtml(contest.title) + '</h1>' +
            '<p class="date">' + dateStr + '</p>' +
            '<p style="margin-top:0.5rem;"><span class="badge badge-easy">' + escapeHtml(contest.status) + '</span></p>' +
            '</div>';

        if (problems.length === 0) {
            html += '<div class="empty-state"><div class="icon">📝</div><p>今日比赛暂未关联题目，请等待管理员发布题目后重试</p></div>';
        } else {
            // Display each problem
            for (var i = 0; i < problems.length; i++) {
                var p = problems[i];
                html += renderProblemCard(p, participation);
            }

            // Action buttons
            html += '<div style="text-align:center;margin-top:1.5rem;">';
            if (participation) {
                html += '<span class="joined-badge">✓ 已参加</span> ';
                html += '<a href="' + CONTEXT_PATH + '/submit?id=' + problems[0].id + '" class="btn btn-primary" style="margin-left:0.5rem;">去提交代码</a>';
            } else {
                html += '<button onclick="joinContest()" class="btn btn-primary" style="font-size:1.1rem;padding:0.8rem 3rem;">' +
                    '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="vertical-align:middle;margin-right:6px;"><polygon points="5 3 19 12 5 21 5 3"/></svg>' +
                    '参加今日比赛</button>';
            }
            html += '</div>';
        }

        document.getElementById('contestInfo').innerHTML = html;

        if (contest && contest.id) {
            loadContestRankings(contest.id);
        }
    }

    function renderProblemCard(p, participation) {
        var tagsHtml = '';
        if (p.tagList && p.tagList.length > 0) {
            tagsHtml = p.tagList.map(function(t) {
                return '<span class="tag" style="border-color:' + (t.color || '#22d3ee') + ';color:' + (t.color || '#22d3ee') + '">' + escapeHtml(t.name) + '</span>';
            }).join(' ');
        } else if (p.tags) {
            try {
                var parsed = JSON.parse(p.tags);
                if (Array.isArray(parsed)) {
                    tagsHtml = parsed.map(function(t) {
                        return '<span class="tag" style="border-color:#22d3ee;color:#22d3ee">' + escapeHtml(t) + '</span>';
                    }).join(' ');
                }
            } catch(e) { tagsHtml = escapeHtml(p.tags); }
        }

        // Simple description preview (strip HTML/markdown)
        var descPreview = (p.description || '').replace(/<[^>]+>/g, '').substring(0, 150);

        var html = '<div class="problem-card">' +
            '<h2>' + escapeHtml(p.title) + '</h2>' +
            '<div class="meta">' +
            '<span class="badge badge-' + p.difficulty + '">' + getDifficultyText(p.difficulty) + '</span>' +
            '<span style="color:#8aa1bd;font-size:0.85rem;">时间: ' + (p.timeLimitMs || 1000) + 'ms</span>' +
            '<span style="color:#8aa1bd;font-size:0.85rem;">内存: ' + (p.memoryLimitMb || 128) + 'MB</span>' +
            (tagsHtml ? '<span>' + tagsHtml + '</span>' : '') +
            '</div>' +
            '<div class="desc">' + escapeHtml(descPreview) + '</div>' +
            '<div style="display:flex;gap:0.5rem;">' +
            '<a href="' + CONTEXT_PATH + '/problem-detail?id=' + p.id + '" class="btn btn-secondary" target="_blank">查看详情</a>';

        if (participation) {
            html += '<a href="' + CONTEXT_PATH + '/submit?id=' + p.id + '" class="btn btn-primary">提交代码</a>';
        }

        html += '</div></div>';
        return html;
    }

    async function joinContest() {
        try {
            var res = await fetch(apiJoin, {
                method: 'POST',
                headers: { 'Authorization': 'Bearer ' + getToken() }
            });
            var result = await res.json();
            if (result.code === 200) {
                showMessage('参加比赛成功！', 'success');
                loadContest(); // 刷新页面
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
        var html = '<h3>🏆 比赛排行榜</h3>' +
            '<table class="rank-table">' +
            '<thead><tr><th>排名</th><th>用户</th><th>得分</th><th>通过数</th><th>总用时</th></tr></thead><tbody>';

        for (var i = 0; i < rankings.length; i++) {
            var r = rankings[i];
            var rank = i + 1;
            var medal = rank === 1 ? '🥇' : rank === 2 ? '🥈' : rank === 3 ? '🥉' : '';
            html += '<tr>' +
                '<td class="medal">' + medal + ' ' + rank + '</td>' +
                '<td>用户 ' + escapeHtml(r.userId) + '</td>' +
                '<td><strong style="color:#22d3ee;">' + r.totalScore + '</strong></td>' +
                '<td>' + r.acceptedCount + '</td>' +
                '<td>' + r.totalRuntimeMs + ' ms</td>' +
                '</tr>';
        }
        html += '</tbody></table>';
        document.getElementById('contestRankings').innerHTML = html;
    }
</script>
</body>
</html>
