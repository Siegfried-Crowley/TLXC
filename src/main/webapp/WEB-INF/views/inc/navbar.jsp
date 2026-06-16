<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar">
    <div class="nav-brand">题炼星程</div>
    <div class="nav-links" id="navLinks">
        <a href="${pageContext.request.contextPath}/home" ${param.activePage == 'home' ? 'class="active"' : ''}>首页</a>
        <a href="${pageContext.request.contextPath}/problems" ${param.activePage == 'problems' ? 'class="active"' : ''}>题库</a>
        <a href="${pageContext.request.contextPath}/submissions" ${param.activePage == 'submissions' ? 'class="active"' : ''}>提交记录</a>
        <a href="${pageContext.request.contextPath}/wrongbook" ${param.activePage == 'wrongbook' ? 'class="active"' : ''}>错题本</a>
        <a href="${pageContext.request.contextPath}/rankings" ${param.activePage == 'rankings' ? 'class="active"' : ''}>排行榜</a>
        <a href="${pageContext.request.contextPath}/stats" ${param.activePage == 'stats' ? 'class="active"' : ''}>学习统计</a>
        <a href="${pageContext.request.contextPath}/contest" ${param.activePage == 'contest' ? 'class="active"' : ''}>每日一题</a>
        <a href="${pageContext.request.contextPath}/ai" ${param.activePage == 'ai' ? 'class="active"' : ''}>AI助手</a>
        <a href="${pageContext.request.contextPath}/profile" ${param.activePage == 'profile' ? 'class="active"' : ''}>个人中心</a>
        <a href="${pageContext.request.contextPath}/admin/dashboard" id="navAdminLink" style="display:none">管理后台</a>
        <a href="#" onclick="logout()">退出</a>
    </div>
</nav>
<script>
    // Show admin link only for admin users
    (function() {
        try {
            var u = JSON.parse(localStorage.getItem('user') || 'null');
            if (u && u.role === 'admin') {
                var link = document.getElementById('navAdminLink');
                if (link) link.style.display = '';
            }
        } catch(e) {}
    })();
</script>
