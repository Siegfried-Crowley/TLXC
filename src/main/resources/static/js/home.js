checkAuth();
loadStats();

async function loadStats() {
    try {
        const response = await fetch(apiStatsOverview, {
            headers: {
                'Authorization': 'Bearer ' + getToken()
            }
        });

        const result = await response.json();

        if (result.code === 200) {
            document.getElementById('myPoints').textContent = result.data.totalPoints || 0;
            document.getElementById('submissionCount').textContent = result.data.totalSubmissions || 0;
            document.getElementById('acceptedCount').textContent = result.data.acceptedCount || 0;
        }
    } catch (error) {
        console.error('加载统计失败:', error);
    }
}