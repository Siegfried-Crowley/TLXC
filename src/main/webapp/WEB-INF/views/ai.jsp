<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>AI助手 - 题炼星程</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* 修复布局，确保两个输入框正常显示 */
        .ai-chat-container {
            display: flex;
            flex-direction: column;
            height: 80vh;
        }
        .chat-messages {
            flex: 1;
            overflow-y: auto;
            border: 1px solid #22d3ee;
            margin-bottom: 1rem;
            padding: 1rem;
            background: #0f172a;   /* 深色背景 */
            border-radius: 0.5rem;
        }
        /* 消息框样式 - 清晰可见 */
        .message {
            padding: 0.75rem;
            margin: 0.5rem 0;
            border-radius: 0.5rem;
            line-height: 1.5;
        }
        .message-ai {
            background: #1e293b;      /* 深灰背景 */
            color: #e2e8f0;           /* 浅灰文字 */
            border-left: 4px solid #22d3ee;
        }
        .message-user {
            background: #0f172a;      /* 更深的背景 */
            color: #f1f5f9;           /* 几乎白色 */
            border-left: 4px solid #f59e0b;
        }
        .chat-input-area {
            margin-top: 1rem;
        }
        .chat-input-area textarea {
            width: 100%;
            padding: 0.75rem;
            font-size: 1rem;
            border: 1px solid #22d3ee;
            border-radius: 0.5rem;
            background: #0f172a;
            color: #e2e8f0;
            resize: vertical;
        }
        #codeInput {
            height: 150px;
        }
        #questionInput {
            height: 80px;
        }
        .btn-primary {
            margin-top: 0.5rem;
            width: auto !important;
            padding: 0.5rem 2rem;
        }
    </style>
</head>
<body>
<nav class="navbar">
    <div class="nav-brand">题炼星程</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/home">首页</a>
        <a href="${pageContext.request.contextPath}/problems">题库</a>
        <a href="${pageContext.request.contextPath}/ai" class="active">AI助手</a>
        <a href="${pageContext.request.contextPath}/profile">个人中心</a>
        <a href="/algorithm_training_system_war/login" onclick="localStorage.removeItem('token');localStorage.removeItem('user');">退出</a>
    </div>
</nav>

<div class="container">
    <div class="ai-chat-container">
        <h2 style="color: #22d3ee; margin-bottom: 1rem;">AI代码分析助手</h2>

        <div class="chat-messages" id="chatMessages">
            <div class="message message-ai">
                <strong>AI助手:</strong><br>
                你好！我是AI代码分析助手。你可以粘贴你的代码，然后告诉我你想了解什么，例如：<br>
                - 这段代码的时间复杂度是多少？<br>
                - 如何优化这段代码？<br>
                - 代码有什么潜在问题？<br>
                - 能否解释一下这段代码的逻辑？
            </div>
        </div>

        <div class="chat-input-area">
            <textarea id="codeInput" placeholder="在此粘贴你的代码..."></textarea>
        </div>

        <div class="chat-input-area">
            <textarea id="questionInput" placeholder="输入你的问题..."></textarea>
            <button onclick="askAI()" class="btn-primary">发送</button>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script>
    var CONTEXT_PATH = '${pageContext.request.contextPath}';

    function addMessage(type, content) {
        var messagesDiv = document.getElementById('chatMessages');
        var cls = type === 'user' ? 'message-user' : 'message-ai';
        var sender = type === 'user' ? '我' : 'AI助手';
        var div = document.createElement('div');
        div.className = 'message ' + cls;
        div.innerHTML = '<strong>' + sender + ':</strong><br>' + content.replace(/\n/g, '<br>');
        messagesDiv.appendChild(div);
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
    }

    async function askAI() {
        var code = document.getElementById('codeInput').value;
        var question = document.getElementById('questionInput').value;
        if (!code.trim()) { alert('请输入代码'); return; }
        if (!question.trim()) { alert('请输入问题'); return; }
        addMessage('user', code + '\n\n问题: ' + question);
        document.getElementById('questionInput').value = '';
        try {
            var res = await fetch(CONTEXT_PATH + '/api/ai/analyze', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + getToken() },
                body: JSON.stringify({ code: code, question: question })
            });
            var result = await res.json();
            if (result.code === 200) {
                addMessage('ai', result.data);
            } else {
                addMessage('ai', '分析失败: ' + (result.message || '未知错误'));
            }
        } catch(err) {
            console.error(err);
            addMessage('ai', '网络错误，请稍后重试');
        }
    }

    document.getElementById('questionInput').addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.key === 'Enter') askAI();
    });
    if (typeof checkAuth === 'function') checkAuth();
</script>
</body>
</html>