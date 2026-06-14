checkAuth();
loadProfile();

async function loadProfile() {
    try {
        const user = getUser();
        if (!user) {
            showMessage('用户信息丢失，请重新登录', 'error');
            logout();
            return;
        }

        document.getElementById('username').textContent = user.username;
        document.getElementById('nickname').textContent = user.nickname || '未设置昵称';
        document.getElementById('role').textContent = user.role === 'admin' ? '管理员' : '普通用户';
        document.getElementById('userAvatar').textContent = (user.nickname || user.username).charAt(0).toUpperCase();
        document.getElementById('createdAt').textContent = formatDate(user.createdAt);
        document.getElementById('totalPoints').textContent = user.totalPoints || 0;
        document.getElementById('streakDays').textContent = user.streakDays || 0;

        const statsResponse = await fetch(apiStatsOverview, {
            headers: {
                'Authorization': 'Bearer ' + getToken()
            }
        });

        const statsResult = await statsResponse.json();

        if (statsResult.code === 200) {
            document.getElementById('submissionCount').textContent = statsResult.data.totalSubmissions || 0;
            document.getElementById('acceptedCount').textContent = statsResult.data.acceptedCount || 0;
        }
    } catch (error) {
        console.error('加载失败:', error);
        showMessage('加载个人信息失败', 'error');
    }
}