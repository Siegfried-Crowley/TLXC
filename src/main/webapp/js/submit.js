checkAuth();
loadProblemInfo();

async function loadProblemInfo() {
    const urlParams = new URLSearchParams(window.location.search);
    const problemId = urlParams.get('id');
    if (!problemId) {
        showMessage('缺少题目ID', 'error');
        return;
    }
    try {
        const response = await fetch(`${apiProblems}/${problemId}`, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        const result = await response.json();
        if (result.code === 200) {
            displayProblemInfo(result.data);
        }
    } catch (error) {
        console.error('加载失败:', error);
    }
}

function displayProblemInfo(problem) {
    document.getElementById('problemInfo').innerHTML = `
        <h1>${problem.title}</h1>
        <div class="problem-meta">
            <span class="badge badge-${problem.difficulty}">${getDifficultyText(problem.difficulty)}</span>
        </div>
    `;
    if (problem.starterCode) {
        document.getElementById('codeEditor').value = problem.starterCode;
    }
}

async function submitCode() {
    const urlParams = new URLSearchParams(window.location.search);
    const problemId = urlParams.get('id');
    const code = document.getElementById('codeEditor').value;
    if (!code.trim()) {
        showMessage('请输入代码', 'warning');
        return;
    }
    showMessage('正在提交...', 'info');
    try {
        const response = await fetch(apiSubmissions, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getToken()
            },
            body: JSON.stringify({ problemId: parseInt(problemId), code: code })
        });
        const result = await response.json();
        if (result.code === 200) {
            displayResult(result.data);
            showMessage('提交成功！', 'success');
        } else {
            showMessage(result.message || '提交失败', 'error');
        }
    } catch (error) {
        console.error('提交失败:', error);
        showMessage('网络错误', 'error');
    }
}

function displayResult(submission) {
    const resultDiv = document.getElementById('submitResult');
    const contentDiv = document.getElementById('resultContent');
    const statusClass = submission.status === 'accepted' ? 'status-accepted' :
        submission.status === 'wrong_answer' ? 'status-wrong_answer' : 'status-error';
    let html = `
        <div class="result-item"><span class="result-label">状态</span><span class="submission-status ${statusClass}">${getStatusText(submission.status)}</span></div>
        <div class="result-item"><span class="result-label">运行时间:</span><span class="result-value">${submission.runtimeMs} ms</span></div>
        <div class="result-item"><span class="result-label">测试用例:</span><span class="result-value">${submission.passedCases} / ${submission.totalCases}</span></div>
    `;
    if (submission.errorMessage) {
        html += `<div class="result-item"><span class="result-label">错误信息:</span><div class="code-block" style="color: #ef4444;">${escapeHtml(submission.errorMessage)}</div></div>`;
    }
    html += `
        <div class="result-item"><span class="result-label">积分变化:</span><span class="result-value">+${submission.scoreDelta}</span></div>
        <div class="result-item"><span class="result-label">提交时间:</span><span class="result-value">${formatDate(submission.createdAt)}</span></div>
    `;
    contentDiv.innerHTML = html;
    resultDiv.style.display = 'block';
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function(m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}