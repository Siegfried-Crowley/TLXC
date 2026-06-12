checkAuth();
loadStats();

async function loadStats() {
    try {
        const response = await fetch(apiStatsOverview, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        const result = await response.json();
        if (result.code === 200) {
            const totalSubmissions = result.data.totalSubmissions || 0;
            const acceptedCount = result.data.acceptedCount || 0;
            const acceptRate = totalSubmissions > 0 ? ((acceptedCount / totalSubmissions) * 100).toFixed(2) : 0;
            document.getElementById('totalSubmissions').textContent = totalSubmissions;
            document.getElementById('acceptedProblems').textContent = acceptedCount;
            document.getElementById('acceptRate').textContent = acceptRate + '%';
            document.getElementById('totalPoints').textContent = result.data.totalPoints || 0;
        }
        const logsResponse = await fetch(apiPointsLogs, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        const logsResult = await logsResponse.json();
        if (logsResult.code === 200) {
            displayPointLogs(logsResult.data);
        }
    } catch (error) {
        console.error('加载失败:', error);
    }
}

function displayPointLogs(logs) {
    const logsDiv = document.getElementById('pointLogs');
    if (logs.length === 0) {
        logsDiv.innerHTML = '<div class="loading">暂无积分记录</div>';
        return;
    }
    logsDiv.innerHTML = logs.map(log => `
        <div class="submission-item">
            <div class="submission-info">
                <div><strong>${log.reason}</strong></div>
                <div style="color: #8aa1bd; font-size: 0.9rem;">
                    题目ID: ${log.problemId || '-'} | 时间: ${formatDate(log.createdAt)}
                </div>
            </div>
            <div style="color: #22c55e; font-weight: bold; font-size: 1.2rem;">+${log.points}</div>
        </div>
    `).join('');
}