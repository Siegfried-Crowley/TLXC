checkAuth();
loadWrongBooks();

async function loadWrongBooks() {
    var statusEl = document.getElementById('statusFilter');
    var status = statusEl ? statusEl.value : '';
    try {
        var url = status ? (apiWrongbook + '?status=' + status) : apiWrongbook;
        var response = await fetch(url, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        var result = await response.json();
        if (result.code === 200) {
            displayWrongBooks(result.data);
        } else {
            showError('加载错题本失败: ' + (result.message || ''));
        }
    } catch (error) {
        console.error('加载错题本失败:', error);
        showError('网络错误，请刷新重试');
    }
}

function displayWrongBooks(wrongBooks) {
    var listDiv = document.getElementById('wrongBookList');
    if (!wrongBooks || wrongBooks.length === 0) {
        listDiv.innerHTML = '<div class="loading">暂无错题记录</div>';
        return;
    }
    listDiv.innerHTML = wrongBooks.map(function(wb) {
        var mastered = wb.status === 'mastered';
        return '<div class="wrongbook-item">' +
            '<div class="wrongbook-header">' +
            '<div>' +
            '<h3><a href="' + CONTEXT_PATH + '/problem-detail?id=' + (wb.problemId || '') + '">题目 ' + (wb.problemId || '-') + '</a></h3>' +
            '<p style="color: #8aa1bd; margin-top: 0.5rem;">' +
            '错误次数: <strong style="color: #ef4444;">' + (wb.wrongCount || 0) + '</strong> | ' +
            '状态: <span class="badge badge-' + (mastered ? 'easy' : 'hard') + '">' +
            (mastered ? '已掌握' : '未掌握') + '</span>' +
            '</p>' +
            '</div>' +
            '<div class="wrongbook-actions">' +
            '<a href="' + CONTEXT_PATH + '/problem-detail?id=' + (wb.problemId || '') + '" class="btn-small">查看题目</a>' +
            '<a href="' + CONTEXT_PATH + '/submit?id=' + (wb.problemId || '') + '" class="btn-small btn-primary">重新提交</a>' +
            (!mastered ? '<button onclick="markAsMastered(' + wb.id + ')" class="btn-small btn-secondary">标记为已掌握</button>' : '') +
            '</div>' +
            '</div>' +
            '<p style="color: #8aa1bd; font-size: 0.9rem;">最后更新: ' + formatDate(wb.updatedAt) + '</p>' +
            '</div>';
    }).join('');
}

async function markAsMastered(id) {
    try {
        var response = await fetch(apiMaster + '/' + id + '/master', {
            method: 'POST',
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
        var result = await response.json();
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

function showError(msg) {
    var listDiv = document.getElementById('wrongBookList');
    if (listDiv) listDiv.innerHTML = '<div style="color:#ef4444;padding:2rem;text-align:center;">' + msg + '</div>';
}
