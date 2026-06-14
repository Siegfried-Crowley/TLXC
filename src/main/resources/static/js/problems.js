checkAuth();
loadProblems();

async function loadProblems() {
    const difficulty = document.getElementById('difficultyFilter').value;
    try {
        const url = difficulty ? `${apiProblems}?difficulty=${difficulty}` : apiProblems;
        const response = await fetch(url, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        const result = await response.json();
        if (result.code === 200) {
            displayProblems(result.data);
        }
    } catch (error) {
        console.error('加载题目失败:', error);
    }
}

function displayProblems(problems) {
    const tbody = document.getElementById('problemList');
    if (problems.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5">暂无题目</td></tr>';
        return;
    }
    tbody.innerHTML = problems.map(problem => `
        <tr>
            <td>${problem.id}</td>
            <td><a href="/problem-detail?id=${problem.id}">${problem.title}</a></td>
            <td><span class="badge badge-${problem.difficulty}">${getDifficultyText(problem.difficulty)}</span></td>
            <td>${problem.tags || '-'}</td>
            <td>
                <a href="/problem-detail?id=${problem.id}" class="btn-small">查看</a>
                <a href="/submit?id=${problem.id}" class="btn-small btn-primary">提交</a>
            </td>
        </tr>
    `).join('');
}

function getDifficultyText(difficulty) {
    const map = { 'easy': '简单', 'medium': '中等', 'hard': '困难' };
    return map[difficulty] || difficulty;
}