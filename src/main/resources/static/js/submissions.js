checkAuth();
loadSubmissions();

async function loadSubmissions() {
    try {
        var url = apiSubmissions;
        var response = await fetch(url, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        var result = await response.json();
        if (result.code === 200) {
            displaySubmissions(result.data);
        } else {
            showError('加载提交记录失败: ' + (result.message || ''));
        }
    } catch (error) {
        console.error('加载提交记录失败:', error);
        showError('网络错误，请刷新重试');
    }
}

function displaySubmissions(submissions) {
    var listDiv = document.getElementById('submissionList');
    if (!submissions || submissions.length === 0) {
        listDiv.innerHTML = '<div class="loading">暂无提交记录</div>';
        return;
    }
    listDiv.innerHTML = submissions.map(function(sub) {
        var statusClass = sub.status === 'accepted' ? 'status-accepted' :
            sub.status === 'wrong_answer' ? 'status-wrong_answer' : 'status-error';
        return '<div class="submission-item">' +
            '<div class="submission-info">' +
            '<div><strong>题目ID:</strong> ' + (sub.problemId || '-') + '</div>' +
            '<div><strong>提交时间:</strong> ' + formatDate(sub.createdAt) + '</div>' +
            '<div><strong>运行时间:</strong> ' + (sub.runtimeMs || 0) + ' ms</div>' +
            '<div><strong>测试用例:</strong> ' + (sub.passedCases || 0) + ' / ' + (sub.totalCases || 0) + '</div>' +
            '</div>' +
            '<div><span class="submission-status ' + statusClass + '">' + getStatusText(sub.status) + '</span></div>' +
            '</div>';
    }).join('');
}

function showError(msg) {
    var listDiv = document.getElementById('submissionList');
    if (listDiv) listDiv.innerHTML = '<div style="color:#ef4444;padding:2rem;text-align:center;">' + msg + '</div>';
}
