<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar">
    <div class="nav-brand">题炼星程 - 管理后台</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard" ${param.activePage == 'dashboard' ? 'class="active"' : ''}>数据看板</a>
        <a href="${pageContext.request.contextPath}/admin/problems" ${param.activePage == 'admin-problems' ? 'class="active"' : ''}>题目管理</a>
        <a href="${pageContext.request.contextPath}/admin/users" ${param.activePage == 'admin-users' ? 'class="active"' : ''}>用户管理</a>
        <a href="${pageContext.request.contextPath}/home">返回前台</a>
        <a href="#" onclick="logout()">退出</a>
    </div>
</nav>
