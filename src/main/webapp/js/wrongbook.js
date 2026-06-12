checkAuth();
loadWrongBooks();

async function loadWrongBooks() {
    const status = document.getElementById('statusFilter').value;
    try {
        const url = status ? `${apiWrongbook}?status=${status}` : apiWrongbook;
        const response = await fetch(url, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        const result = await response.json();
        if (result.code === 200) {
            displayWrongBooks(result.data);
        }
    } catch (error) {
        console.error('加载失败:', error);
    }
}

function displayWrongBooks(wrongBooks) {
    const listDiv = document.getElementById('wrongBookList');
    if (wrongBooks.length === 0) {
        listDiv.innerHTML = '<div class="loading">暂无错题记录</div>';
        return;
    }
    listDiv.innerHTML = wrongBooks.map(wb => `
        <div class="wrongbook-item">
            <div class="wrongbook-header">
                <div>
                    <h3><a href="/problem-detail?id=${wb.problemId}">题目 ${wb.problemId}</a></h3>
                    <p style="color: #8aa1bd; margin-top: 0.5rem;">
                        错误次数: <strong style="color: #ef4444;">${wb.wrongCount}</strong> |
                        状态: <span class="badge badge-${wb.status === 'mastered' ? 'easy' : 'hard'}">
                            ${wb.status === 'mastered' ? '已掌握' : '未掌握'}
                        </span>
                    </p>
                </div>
                <div class="wrongbook-actions">
                    <a href="/problem-detail?id=${wb.problemId}" class="btn-small">查看题目</a>
                    <a href="/submit?id=${wb.problemId}" class="btn-small btn-primary">重新提交</a>
                    ${wb.status !== 'mastered' ? `
                        <button onclick="markAsMastered(${wb.id})" class="btn-small btn-secondary">标记为已掌握</button>
                    ` : ''}
                </div>
            </div>
            <p style="color: #8aa1bd; font-size: 0.9rem;">最后更新: ${formatDate(wb.updatedAt)}</p>
        </div>
    `).join('');
}

async function markAsMastered(id) {
    try {
        const response = await fetch(`${apiMaster}/${id}/master`, {
            method: 'POST',
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        const result = await response.json();
        if (result.code === 200) {
            showMessage('已标记为掌握', 'success');
            loadWrongBooks();
        } else {
            showMessage(result.message || '操作失败', 'error');
        }
    } catch (error) {
        console.error('操作失败:', error);
        showMessage('网络错误', 'error');
    }
}