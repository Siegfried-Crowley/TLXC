checkAuth();
loadContest();

async function loadContest() {
    try {
        const response = await fetch(apiToday, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        const result = await response.json();
        if (result.code === 200 && result.data) {
            displayContest(result.data);
            loadContestRankings(result.data.id);
        } else {
            document.getElementById('contestInfo').innerHTML = `
                <div class="hero-section">
                    <h2>今日比赛暂未生成</h2>
                    <p>请稍后再来查看</p>
                </div>
            `;
        }
    } catch (error) {
        console.error('加载失败:', error);
    }
}

function displayContest(contest) {
    document.getElementById('contestInfo').innerHTML = `
        <div class="hero-section">
            <h1>${contest.title}</h1>
            <p>比赛日期: ${formatDate(contest.contestDate)}</p>
            <p>状态: <span class="badge badge-easy">${contest.status}</span></p>
        </div>
    `;
    document.getElementById('contestActions').style.display = 'block';
}

async function joinContest() {
    try {
        const response = await fetch(apiJoin, {
            method: 'POST',
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        const result = await response.json();
        if (result.code === 200) {
            showMessage('加入比赛成功！', 'success');
            window.location.href = contextPath + '/problems';
        } else {
            showMessage(result.message || '加入失败', 'error');
        }
    } catch (error) {
        console.error('加入失败:', error);
        showMessage('网络错误', 'error');
    }
}

async function loadContestRankings(contestId) {
    try {
        const response = await fetch(contextPath + '/api/contests/' + contestId + '/rankings', {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        const result = await response.json();
        if (result.code === 200 && result.data.length > 0) {
            displayRankings(result.data);
            document.getElementById('contestRankings').style.display = 'block';
        }
    } catch (error) {
        console.error('加载排行榜失败', error);
    }
}

function displayRankings(rankings) {
    const tbody = document.getElementById('rankingTable');
    tbody.innerHTML = rankings.map((r, index) => {
        const rank = index + 1;
        const medal = rank === 1 ? '🥇' : rank === 2 ? '🥈' : rank === 3 ? '🥉' : '';
        return `
            <tr>
                <td><strong>${medal} ${rank}</strong></td>
                <td>用户 ${r.userId}</td>
                <td><strong style="color: #22d3ee;">${r.totalScore}</strong></td>
                <td>${r.acceptedCount}</td>
                <td>${r.totalRuntimeMs} ms</td>
            </tr>
        `;
    }).join('');
}