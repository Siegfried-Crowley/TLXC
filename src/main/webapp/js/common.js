// 项目上下文路径（硬编码，确保退出和认证跳转正确）
var CONTEXT_PATH = '/algorithm_training_system_war';

function getUser() {
    const userStr = localStorage.getItem('user');
    if (!userStr) return null;
    try {
        return JSON.parse(userStr);
    } catch (e) {
        return null;
    }
}

function getToken() {
    return localStorage.getItem('token');
}

function checkAuth() {
    if (!getToken() || !getUser()) {
        window.location.href = CONTEXT_PATH + '/login';
    }
}

function showMessage(msg, type) {
    alert(msg);
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = CONTEXT_PATH + '/login';
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return dateStr;
    return date.toLocaleString('zh-CN');
}

function getDifficultyText(difficulty) {
    const map = { 'easy': '简单', 'medium': '中等', 'hard': '困难' };
    return map[difficulty] || difficulty;
}

function getStatusText(status) {
    const map = { 'accepted': '通过', 'wrong_answer': '答案错误', 'error': '运行错误' };
    return map[status] || status;
}