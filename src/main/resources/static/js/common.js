// 获取token
function getToken() {
    return localStorage.getItem('token');
}

// 获取用户信息
function getUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
}

// 检查认证状态
function checkAuth() {
    const token = getToken();
    if (!token) {
        window.location.href = '/login.jsp';
        return false;
    }
    return true;
}

// 退出登录
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login.jsp';
}

// API请求封装
async function apiRequest(url, options = {}) {
    const token = getToken();

    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': token ? `Bearer ${token}` : ''
        }
    };

    const config = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers
        }
    };

    try {
        const response = await fetch(url, config);
        const data = await response.json();

        if (response.status === 401) {
            logout();
            return null;
        }

        return data;
    } catch (error) {
        console.error('API请求失败:', error);
        throw error;
    }
}

// 格式化日期
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

// 获取难度文本
function getDifficultyText(difficulty) {
    const map = {
        'easy': '简单',
        'medium': '中等',
        'hard': '困难'
    };
    return map[difficulty] || difficulty;
}

// 获取状态文本
function getStatusText(status) {
    const map = {
        'accepted': '通过',
        'wrong_answer': '答案错误',
        'error': '执行错误',
        'timeout': '超时'
    };
    return map[status] || status;
}

// 显示提示消息
function showMessage(message, type = 'info') {
    const colors = {
        'success': '#22c55e',
        'error': '#ef4444',
        'warning': '#fbbf24',
        'info': '#22d3ee'
    };

    const div = document.createElement('div');
    div.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        background: ${colors[type]};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        z-index: 9999;
        animation: slideIn 0.3s ease;
    `;
    div.textContent = message;

    document.body.appendChild(div);

    setTimeout(() => {
        div.remove();
    }, 3000);
}

// 添加CSS动画
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
`;
document.head.appendChild(style);

// 复制文本到剪贴板
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        showMessage('已复制到剪贴板', 'success');
        return true;
    } catch (error) {
        console.error('复制失败:', error);
        showMessage('复制失败', 'error');
        return false;
    }
}

// 防抖函数
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 节流函数
function throttle(func, limit) {
    let inThrottle;
    return function(...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 可以在这里添加全局初始化逻辑
    console.log('题炼星程系统已加载');
});
