checkAuth();
loadStats();

async function loadStats() {
    try {
        var overviewResponse = await fetch(apiStatsOverview, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        var overviewResult = await overviewResponse.json();
        if (overviewResult.code === 200) {
            var data = overviewResult.data || {};
            var totalSubmissions = data.totalSubmissions || 0;
            var acceptedCount = data.acceptedCount || 0;
            var acceptRate = totalSubmissions > 0 ? (acceptedCount / totalSubmissions * 100).toFixed(2) : 0;
            document.getElementById('totalSubmissions').textContent = totalSubmissions;
            document.getElementById('acceptedProblems').textContent = acceptedCount;
            document.getElementById('acceptRate').textContent = acceptRate + '%';
            document.getElementById('totalPoints').textContent = data.totalPoints || 0;
        }
    } catch (error) {
        console.error('加载统计失败:', error);
    }
    try {
        var logsResponse = await fetch(apiPointsLogs, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        var logsResult = await logsResponse.json();
        if (logsResult.code === 200) {
            displayPointLogs(logsResult.data);
        }
    } catch (error) {
        console.error('加载积分记录失败:', error);
    }
}

function displayPointLogs(logs) {
    var logsDiv = document.getElementById('pointLogs');
    if (!logs || logs.length === 0) {
        logsDiv.innerHTML = '<div class="loading">暂无积分记录</div>';
        return;
    }
    logsDiv.innerHTML = logs.map(function(log) {
        return '<div class="submission-item">' +
            '<div class="submission-info">' +
            '<div><strong>' + (log.reason || '') + '</strong></div>' +
            '<div style="color: #8aa1bd; font-size: 0.9rem;">' +
            '题目ID: ' + (log.problemId || '-') + ' | 时间: ' + formatDate(log.createdAt) +
            '</div>' +
            '</div>' +
            '<div style="color: #22c55e; font-weight: bold; font-size: 1.2rem;">+' + (log.points || 0) + '</div>' +
            '</div>';
    }).join('');
}
