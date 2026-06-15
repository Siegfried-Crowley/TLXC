// ==========================================
// 题炼星程 - 公共工具库 v2.0
// ==========================================

var CONTEXT_PATH = '/algorithm_training_system_war';

// ---- 认证 ----
function getUser() {
  var userStr = localStorage.getItem('user');
  if (!userStr) return null;
  try { return JSON.parse(userStr); } catch (e) { return null; }
}

function getToken() { return localStorage.getItem('token'); }

function checkAuth() {
  if (!getToken() || !getUser()) {
    window.location.href = CONTEXT_PATH + '/login';
  }
}

function checkAdmin() {
  var user = getUser();
  if (!user || user.role !== 'admin') {
    toast('无权访问管理后台', 'error');
    setTimeout(function() { window.location.href = CONTEXT_PATH + '/home'; }, 1000);
    return false;
  }
  return true;
}

function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  window.location.href = CONTEXT_PATH + '/login';
}

// ---- Toast 通知 ----
function toast(message, type) {
  type = type || 'info';
  var container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  var icons = { success: '✓', error: '✕', warning: '⚠', info: 'ℹ' };
  var el = document.createElement('div');
  el.className = 'toast toast-' + type;
  el.innerHTML = '<span style="font-size:1.1rem;font-weight:bold">' + (icons[type] || '') + '</span> ' + escapeHtml(message);
  el.onclick = function() { el.remove(); };

  container.appendChild(el);
  setTimeout(function() { if (el.parentNode) el.remove(); }, 3200);
}

// ---- Modal 弹窗 ----
function showModal(options) {
  var overlay = document.createElement('div');
  overlay.className = 'modal-overlay';
  overlay.innerHTML = '<div class="modal">' +
    '<h2>' + escapeHtml(options.title || '') + '</h2>' +
    '<div class="modal-body">' + (options.body || '') + '</div>' +
    '<div class="modal-actions">' +
      (options.showCancel !== false ? '<button class="btn btn-secondary btn-sm" onclick="closeModal(this)">取消</button>' : '') +
      (options.confirmText ? '<button class="btn btn-primary btn-sm" id="modalConfirmBtn">' + escapeHtml(options.confirmText) + '</button>' : '') +
    '</div></div>';
  document.body.appendChild(overlay);

  if (options.onConfirm) {
    document.getElementById('modalConfirmBtn').onclick = function() {
      options.onConfirm();
      overlay.remove();
    };
  }
  return overlay;
}

function closeModal(el) {
  var overlay = el.closest('.modal-overlay');
  if (overlay) overlay.remove();
}

// ---- 确认对话框（替换 confirm） ----
function confirmDialog(message, onConfirm) {
  showModal({
    title: '确认操作',
    body: '<p style="color:var(--text-primary)">' + escapeHtml(message) + '</p>',
    confirmText: '确定',
    onConfirm: onConfirm
  });
}

// ---- API 统一请求 ----
function apiGet(url) {
  return fetch(url, {
    headers: { 'Authorization': 'Bearer ' + getToken() }
  }).then(function(r) { return r.json(); });
}

function apiPost(url, data) {
  return fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + getToken()
    },
    body: JSON.stringify(data)
  }).then(function(r) { return r.json(); });
}

function apiPut(url, data) {
  return fetch(url, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + getToken()
    },
    body: JSON.stringify(data)
  }).then(function(r) { return r.json(); });
}

function apiDelete(url) {
  return fetch(url, {
    method: 'DELETE',
    headers: { 'Authorization': 'Bearer ' + getToken() }
  }).then(function(r) { return r.json(); });
}

// ---- 格式化 ----
function formatDate(dateStr) {
  if (!dateStr) return '-';
  var date = new Date(dateStr);
  if (isNaN(date.getTime())) return dateStr;
  return date.toLocaleString('zh-CN');
}

function formatDateShort(dateStr) {
  if (!dateStr) return '-';
  var date = new Date(dateStr);
  if (isNaN(date.getTime())) return dateStr;
  return date.toLocaleDateString('zh-CN');
}

function getDifficultyText(d) {
  return { easy: '简单', medium: '中等', hard: '困难' }[d] || d;
}

function getStatusText(s) {
  return { accepted: '通过', wrong_answer: '答案错误', error: '运行错误' }[s] || s;
}

function escapeHtml(str) {
  if (!str) return '';
  var div = document.createElement('div');
  div.textContent = str;
  return div.innerHTML;
}

function timeAgo(dateStr) {
  if (!dateStr) return '';
  var diff = Date.now() - new Date(dateStr).getTime();
  var mins = Math.floor(diff / 60000);
  if (mins < 1) return '刚刚';
  if (mins < 60) return mins + '分钟前';
  var hours = Math.floor(mins / 60);
  if (hours < 24) return hours + '小时前';
  var days = Math.floor(hours / 24);
  if (days < 30) return days + '天前';
  return formatDateShort(dateStr);
}

// ---- 分页渲染 ----
function renderPagination(container, totalPages, currentPage, onPageChange) {
  container.innerHTML = '';
  if (totalPages <= 1) return;

  var prevBtn = document.createElement('button');
  prevBtn.textContent = '‹';
  prevBtn.disabled = currentPage <= 1;
  prevBtn.onclick = function() { onPageChange(currentPage - 1); };
  container.appendChild(prevBtn);

  var start = Math.max(1, currentPage - 2);
  var end = Math.min(totalPages, currentPage + 2);

  if (start > 1) {
    var first = document.createElement('button');
    first.textContent = '1';
    first.onclick = function() { onPageChange(1); };
    container.appendChild(first);
    if (start > 2) {
      var dots = document.createElement('span');
      dots.textContent = '…';
      container.appendChild(dots);
    }
  }

  for (var i = start; i <= end; i++) {
    var btn = document.createElement('button');
    btn.textContent = i;
    if (i === currentPage) btn.className = 'active';
    btn.onclick = function(p) { return function() { onPageChange(p); }; }(i);
    container.appendChild(btn);
  }

  if (end < totalPages) {
    if (end < totalPages - 1) {
      var dots2 = document.createElement('span');
      dots2.textContent = '…';
      container.appendChild(dots2);
    }
    var last = document.createElement('button');
    last.textContent = totalPages;
    last.onclick = function() { onPageChange(totalPages); };
    container.appendChild(last);
  }

  var nextBtn = document.createElement('button');
  nextBtn.textContent = '›';
  nextBtn.disabled = currentPage >= totalPages;
  nextBtn.onclick = function() { onPageChange(currentPage + 1); };
  container.appendChild(nextBtn);
}

// ---- 语言名称映射 ----
function getLanguageName(lang) {
  return { python: 'Python', java: 'Java', cpp: 'C++', javascript: 'JavaScript' }[lang] || lang;
}
