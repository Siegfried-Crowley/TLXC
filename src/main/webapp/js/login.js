async function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch(apiLogin, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const result = await response.json();

        if (result.code === 200) {
            localStorage.setItem('token', result.data.token);
            localStorage.setItem('user', JSON.stringify(result.data.user));
            window.location.href = contextPath + '/home';
        } else {
            toast(result.message || '登录失败', 'error');
        }
    } catch (error) {
        console.error('登录错误:', error);
        toast('网络错误，请稍后重试', 'error');
    }

    return false;
}