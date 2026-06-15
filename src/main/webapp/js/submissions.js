var currentPage = 1;

checkAuth();
loadSubmissions();

async function loadSubmissions(page) {
    if (page) currentPage = page;
    try {
        var result = await apiGet(CONTEXT_PATH + '/api/submissions/my?page=' + currentPage + '&pageSize=15');
        if (result.code === 200) {
            displaySubmissions(result.data);
        } else {
            showError(result.message || '加载失败');
        }
    } catch (error) {
        console.error(error);
        showError('网络错误，请刷新重试');
    }
}

function displaySubmissions(data) {
    var list = data.list || [];
    var listDiv = document.getElementById('submissionList');
    if (!list || list.length === 0) {
        listDiv.innerHTML = '<div class="empty-state"><div class="empty-icon">📝</div><p>暂无提交记录</p></div>';
        document.getElementById('pagination').innerHTML = '';
        return;
    }
    var html = '';
    for (var i = 0; i < list.length; i++) {
        var s = list[i];
        html += '<div class="submission-item">' +
            '<div class="submission-info">' +
            '<div><strong>题目 #' + s.problemId + '</strong> | ' +
            '<span class="' + ('status-' + s.status) + '">' + getStatusText(s.status) + '</span></div>' +
            '<div style="color:var(--text-muted);font-size:0.85rem;margin-top:0.3rem;">' +
            getLanguageName(s.language || 'python') + ' | ' +
            (s.runtimeMs || 0) + 'ms | ' +
            (s.passedCases || 0) + '/' + (s.totalCases || 0) + ' 通过 | ' +
            timeAgo(s.createdAt) +
            '</div></div>' +
            '<div><span style="color:' + (s.scoreDelta > 0 ? 'var(--green)' : 'var(--text-muted)') + ';font-weight:bold;">' +
            (s.scoreDelta > 0 ? '+' : '') + (s.scoreDelta || 0) + '</span></div>' +
            '</div>';
    }
    listDiv.innerHTML = html;

    renderPagination(document.getElementById('pagination'), data.totalPages, data.page, function(p) {
        loadSubmissions(p);
    });
}

function showError(msg) {
    document.getElementById('submissionList').innerHTML = '<div class="empty-state"><div class="empty-icon">⚠</div><p>' + escapeHtml(msg) + '</p></div>';
}
