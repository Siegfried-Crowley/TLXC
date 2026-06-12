checkAuth();
loadRankings();

async function loadRankings() {
    try {
        const response = await fetch(apiRankings, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        const result = await response.json();
        if (result.code === 200) {
            displayRankings(result.data);
        }
    } catch (error) {
        console.error('加载失败:', error);
    }
}

function displayRankings(users) {
    const tbody = document.getElementById('rankingList');
    if (users.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">暂无数据</td></tr>';
        return;
    }
    tbody.innerHTML = users.map((user, index) => {
        const rank = index + 1;
        const medal = rank === 1 ? '🥇' : rank === 2 ? '🥈' : rank === 3 ? '🥉' : '';
        return `
            <tr>
                <td><strong>${medal} ${rank}</strong></td>
                <td>${user.username}</td>
                <td>${user.nickname || '-'}</td>
                <td><strong style="color: #22d3ee;">${user.totalPoints}</strong></td>
                <td>${user.streakDays} 天</td>
            </tr>
        `;
    }).join('');
}