checkAuth();
loadRankings();

async function loadRankings() {
    try {
        var response = await fetch(apiRankings, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        var result = await response.json();
        if (result.code === 200) {
            displayRankings(result.data);
        } else {
            showError('加载排行榜失败: ' + (result.message || ''));
        }
    } catch (error) {
        console.error('加载排行榜失败:', error);
        showError('网络错误，请刷新重试');
    }
}

function displayRankings(users) {
    var tbody = document.getElementById('rankingList');
    if (!users || users.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">暂无数据</td></tr>';
        return;
    }
    tbody.innerHTML = users.map(function(user, index) {
        var rank = index + 1;
        var medal = rank === 1 ? '🥇' : rank === 2 ? '🥈' : rank === 3 ? '🥉' : '';
        return '<tr>' +
            '<td><strong>' + medal + ' ' + rank + '</strong></td>' +
            '<td>' + (user.username || '-') + '</td>' +
            '<td>' + (user.nickname || '-') + '</td>' +
            '<td><strong style="color: #22d3ee;">' + (user.totalPoints || 0) + '</strong></td>' +
            '<td>' + (user.streakDays || 0) + ' 天</td>' +
            '</tr>';
    }).join('');
}

function showError(msg) {
    var tbody = document.getElementById('rankingList');
    if (tbody) tbody.innerHTML = '<tr><td colspan="5" style="color:#ef4444;">' + msg + '</td></tr>';
}
