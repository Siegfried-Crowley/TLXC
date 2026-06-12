checkAuth();
loadProblemDetail();

async function loadProblemDetail() {
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
            displayProblem(result.data);
        } else {
            showMessage(result.message || '加载失败', 'error');
        }
    } catch (error) {
        console.error('加载题目失败:', error);
        showMessage('网络错误', 'error');
    }
}

function displayProblem(problem) {
    let examplesHtml = '';
    if (problem.examples) {
        try {
            const examples = JSON.parse(problem.examples);
            examplesHtml = examples.map((ex, idx) => `
                <div class="code-block">
                    <strong>示例 ${idx + 1}:</strong><br>
                    输入: ${JSON.stringify(ex.input)}<br>
                    输出: ${JSON.stringify(ex.output)}
                </div>
            `).join('');
        } catch (e) {
            examplesHtml = `<div class="code-block">${escapeHtml(problem.examples)}</div>`;
        }
    }
    let html = `
        <h1>${escapeHtml(problem.title)}</h1>
        <div class="problem-meta">
            <span class="badge badge-${problem.difficulty}">${getDifficultyText(problem.difficulty)}</span>
            <span>标签: ${escapeHtml(problem.tags) || '无'}</span>
        </div>
        <div class="problem-description">
            <h3>题目描述</h3>
            <p>${escapeHtml(problem.description)}</p>
    `;
    if (problem.inputDescription) {
        html += `<h3>输入格式</h3><p>${escapeHtml(problem.inputDescription)}</p>`;
    }
    if (problem.outputDescription) {
        html += `<h3>输出格式</h3><p>${escapeHtml(problem.outputDescription)}</p>`;
    }
    if (examplesHtml) {
        html += `<h3>示例</h3>${examplesHtml}`;
    }
    if (problem.hint) {
        html += `<h3>提示</h3><p>${escapeHtml(problem.hint)}</p>`;
    }
    if (problem.constraints) {
        html += `<h3>约束条件</h3><p>${escapeHtml(problem.constraints)}</p>`;
    }
    html += `</div>`;
    document.getElementById('problemDetail').innerHTML = html;
    document.getElementById('submitBtn').href = `/submit?id=${problem.id}`;
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