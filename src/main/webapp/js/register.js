async function handleRegister(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const nickname = document.getElementById('nickname').value;

    try {
        const response = await fetch(apiRegister, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password, nickname })
        });

        const result = await response.json();

        if (result.code === 200) {
            alert('注册成功！请登录');
            window.location.href = contextPath + '/login';
        } else {
            alert(result.message || '注册失败');
        }
    } catch (error) {
        console.error('注册错误:', error);
        alert('网络错误，请稍后重试');
    }

    return false;
}