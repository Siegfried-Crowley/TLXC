checkAuth();

async function askAI() {
    const code = document.getElementById('codeInput').value;
    const question = document.getElementById('questionInput').value;

    if (!code.trim()) {
        showMessage('请输入代码', 'warning');
        return;
    }
    if (!question.trim()) {
        showMessage('请输入问题', 'warning');
        return;
    }

    addMessage('user', code + '\n\n问题: ' + question);
    document.getElementById('questionInput').value = '';

    try {
        const response = await fetch(CONTEXT_PATH + '/api/ai/analyze', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getToken()
            },
            body: JSON.stringify({ code, question })
        });
        const result = await response.json();
        if (result.code === 200) {
            addMessage('ai', result.data);
        } else {
            addMessage('ai', '分析失败: ' + (result.message || '未知错误'));
        }
    } catch (error) {
        console.error('请求失败:', error);
        addMessage('ai', '网络错误，请稍后重试');
    }
}

function addMessage(type, content) {
    const messagesDiv = document.getElementById('chatMessages');
    const messageClass = type === 'user' ? 'message-user' : 'message-ai';
    const sender = type === 'user' ? '我' : 'AI助手';
    const messageDiv = document.createElement('div');
    messageDiv.className = 'message ' + messageClass;
    messageDiv.innerHTML = '<strong>' + sender + ':</strong><br>' + content.replace(/\n/g, '<br>');
    messagesDiv.appendChild(messageDiv);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

// Ctrl+Enter 发送
document.getElementById('questionInput').addEventListener('keydown', function(e) {
    if (e.ctrlKey && e.key === 'Enter') {
        askAI();
    }
});