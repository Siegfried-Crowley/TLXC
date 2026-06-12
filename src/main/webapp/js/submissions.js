checkAuth();
loadSubmissions();

async function loadSubmissions() {
    try {
        const response = await fetch(apiSubmissions, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        const result = await response.json();
        if (result.code === 200) {
            displaySubmissions(result.data);
        }
    } catch (error) {
        console.error('加载失败:', error);
    }
}

function displaySubmissions(submissions) {
    const listDiv = document.getElementById('submissionList');
    if (submissions.length === 0) {
        listDiv.innerHTML = '<div class="loading">暂无提交记录</div>';
        return;
    }
    listDiv.innerHTML = submissions.map(sub => {
        const statusClass = sub.status === 'accepted' ? 'status-accepted' :
            sub.status === 'wrong_answer' ? 'status-wrong_answer' : 'status-error';
        return `
            <div class="submission-item">
                <div class="submission-info">
                    <div><strong>题目ID:</strong> ${sub.problemId}</div>
                    <div><strong>提交时间:</strong> ${formatDate(sub.createdAt)}</div>
                    <div><strong>运行时间:</strong> ${sub.runtimeMs} ms</div>
                    <div><strong>测试用例:</strong> ${sub.passedCases} / ${sub.totalCases}</div>
                </div>
                <div><span class="submission-status ${statusClass}">${getStatusText(sub.status)}</span></div>
            </div>
        `;
    }).join('');
}