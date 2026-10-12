/* ============================================================
   Authentica — Client Engine
   Handles auth, API calls, scan rendering, community feed.
   No jQuery. No framework. Pure modern JavaScript.
   ============================================================ */

const API_BASE = '/api/v1';

// ── Auth helpers ────────────────────────────────────────────
function getToken() { return localStorage.getItem('ac_token'); }
function getUser()  {
  const s = localStorage.getItem('ac_user');
  return s ? JSON.parse(s) : null;
}

function setAuth(token, user) {
  localStorage.setItem('ac_token', token);
  localStorage.setItem('ac_user', JSON.stringify(user));
  refreshNavAuth();
  window.dispatchEvent(new CustomEvent('auth-changed', { detail: { token, user } }));
}

function clearAuth() {
  localStorage.removeItem('ac_token');
  localStorage.removeItem('ac_user');
  refreshNavAuth();
  window.dispatchEvent(new CustomEvent('auth-changed', { detail: { token: null, user: null } }));
}

function logout() {
  clearAuth();
  showAlert('Signed out successfully.', 'neutral');
  if (window.location.pathname === '/dashboard' || window.location.pathname === '/admin') {
    window.location.href = '/';
  } else {
    window.location.reload();
  }
}

function refreshNavAuth() {
  const user = getUser();
  const navActions = document.querySelector('.nav-actions');
  const navLinks = document.querySelector('.nav-links');

  // Sync nav links
  if (navLinks) {
    let dashLink = document.getElementById('navLinkDashboard');
    let adminLink = document.getElementById('navLinkAdmin');

    if (user) {
      if (user.role === 'ROLE_ADMIN') {
        if (!adminLink) {
          const a = document.createElement('a');
          a.id = 'navLinkAdmin';
          a.href = '/admin';
          a.className = 'nav-link' + (window.location.pathname === '/admin' ? ' active' : '');
          a.textContent = 'Admin Portal';
          navLinks.appendChild(a);
        }
      } else {
        if (!dashLink) {
          const a = document.createElement('a');
          a.id = 'navLinkDashboard';
          a.href = '/dashboard';
          a.className = 'nav-link' + (window.location.pathname === '/dashboard' ? ' active' : '');
          a.textContent = 'Dashboard';
          navLinks.appendChild(a);
        }
      }
    } else {
      if (dashLink) dashLink.remove();
      if (adminLink) adminLink.remove();
    }
  }

  // Sync nav actions
  if (navActions) {
    let authBox = document.getElementById('navAuthBox');
    if (!authBox) {
      authBox = document.createElement('div');
      authBox.id = 'navAuthBox';
      authBox.style.display = 'inline-flex';
      authBox.style.alignItems = 'center';
      authBox.style.gap = 'var(--sp-2)';
      navActions.prepend(authBox);
    }

    if (user) {
      const roleLabel = user.role === 'ROLE_ADMIN' ? 'Admin' : (user.trustScore ? `${user.trustScore} pts` : 'Citizen');
      authBox.innerHTML = `
        <a href="${user.role === 'ROLE_ADMIN' ? '/admin' : '/dashboard'}" class="badge badge-brand" style="text-decoration:none;cursor:pointer;font-weight:var(--fw-medium);padding:var(--sp-1) var(--sp-3);" title="Click to open dashboard">
          👤 ${escHtml(user.name)} (${roleLabel})
        </a>
        <button class="btn btn-ghost btn-sm" onclick="logout()">Sign Out</button>
      `;
    } else {
      authBox.innerHTML = `
        <button class="btn btn-ghost btn-sm" onclick="openAuthModal('login')">Sign In</button>
        <button class="btn btn-outline btn-sm" onclick="openAuthModal('register')">Register</button>
      `;
    }
  }
}

// ── Auth Modal (Login & Register) ────────────────────────────
function initAuthModal() {
  if (document.getElementById('authModalOverlay')) return;

  const modalHtml = `
    <div class="modal-overlay" id="authModalOverlay" role="dialog" aria-modal="true" aria-labelledby="authModalTitle">
      <div class="modal" style="max-width:440px;">
        <div class="modal-header">
          <div style="display:flex;gap:var(--sp-2);">
            <button type="button" class="btn btn-sm btn-primary" id="btnTabLogin" onclick="switchAuthTab('login')">Sign In</button>
            <button type="button" class="btn btn-sm btn-ghost" id="btnTabRegister" onclick="switchAuthTab('register')">Create Account</button>
          </div>
          <button class="modal-close" onclick="closeModal('authModalOverlay')" aria-label="Close">&times;</button>
        </div>
        <div class="modal-body">
          <!-- Sign In Form -->
          <form id="formLogin" onsubmit="handleLoginSubmit(event)">
            <div class="form-group">
              <label class="form-label" for="loginEmail">Email Address</label>
              <input type="email" id="loginEmail" class="form-control" placeholder="user@authentica.io" required autocomplete="email">
            </div>
            <div class="form-group">
              <label class="form-label" for="loginPassword">Password</label>
              <input type="password" id="loginPassword" class="form-control" placeholder="••••••••" required autocomplete="current-password">
            </div>
            <div style="margin-bottom:var(--sp-4);background:var(--col-bg);padding:var(--sp-3);border-radius:var(--r-md);border:1px solid var(--col-border);">
              <p class="form-hint" style="margin-bottom:var(--sp-2);color:var(--col-muted);">Quick 1-Click Demo Logins:</p>
              <div style="display:flex;gap:var(--sp-2);flex-wrap:wrap;">
                <button type="button" class="btn btn-ghost btn-sm" style="font-size:var(--text-xs);" onclick="quickFillAuth('user@authentica.io','User@1234')">Citizen User</button>
                <button type="button" class="btn btn-ghost btn-sm" style="font-size:var(--text-xs);" onclick="quickFillAuth('admin@authentica.io','Admin@1234')">Administrator</button>
              </div>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%;height:38px;" id="loginSubmitBtn">Sign In</button>
          </form>

          <!-- Register Form -->
          <form id="formRegister" style="display:none;" onsubmit="handleRegisterSubmit(event)">
            <div class="form-group">
              <label class="form-label" for="regName">Full Name</label>
              <input type="text" id="regName" class="form-control" placeholder="e.g. Aarav Sharma" required autocomplete="name">
            </div>
            <div class="form-group">
              <label class="form-label" for="regEmail">Email Address</label>
              <input type="email" id="regEmail" class="form-control" placeholder="aarav@example.com" required autocomplete="email">
            </div>
            <div class="form-group">
              <label class="form-label" for="regPhone">Phone Number</label>
              <input type="tel" id="regPhone" class="form-control" placeholder="+91 98765 43210" autocomplete="tel">
            </div>
            <div class="form-group">
              <label class="form-label" for="regPassword">Password</label>
              <input type="password" id="regPassword" class="form-control" placeholder="Min 6 characters" required minlength="6" autocomplete="new-password">
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%;height:38px;" id="registerSubmitBtn">Create Account</button>
          </form>
        </div>
      </div>
    </div>
  `;
  document.body.insertAdjacentHTML('beforeend', modalHtml);

  const ov = document.getElementById('authModalOverlay');
  if (ov) {
    ov.addEventListener('click', e => {
      if (e.target === ov) ov.classList.remove('open');
    });
  }
}

function openAuthModal(tab = 'login') {
  initAuthModal();
  switchAuthTab(tab);
  openModal('authModalOverlay');
}

function switchAuthTab(tab) {
  const formLogin = document.getElementById('formLogin');
  const formRegister = document.getElementById('formRegister');
  const btnLogin = document.getElementById('btnTabLogin');
  const btnRegister = document.getElementById('btnTabRegister');

  if (tab === 'login') {
    if (formLogin) formLogin.style.display = 'block';
    if (formRegister) formRegister.style.display = 'none';
    if (btnLogin) { btnLogin.className = 'btn btn-sm btn-primary'; }
    if (btnRegister) { btnRegister.className = 'btn btn-sm btn-ghost'; }
  } else {
    if (formLogin) formLogin.style.display = 'none';
    if (formRegister) formRegister.style.display = 'block';
    if (btnLogin) { btnLogin.className = 'btn btn-sm btn-ghost'; }
    if (btnRegister) { btnRegister.className = 'btn btn-sm btn-primary'; }
  }
}

function quickFillAuth(email, password) {
  const eInput = document.getElementById('loginEmail');
  const pInput = document.getElementById('loginPassword');
  if (eInput) eInput.value = email;
  if (pInput) pInput.value = password;
}

async function handleLoginSubmit(e) {
  e.preventDefault();
  const btn = document.getElementById('loginSubmitBtn');
  const email = document.getElementById('loginEmail').value.trim();
  const password = document.getElementById('loginPassword').value;

  if (btn) { btn.disabled = true; btn.textContent = 'Authenticating…'; }
  try {
    const data = await apiFetch('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });
    setAuth(data.accessToken, data.user);
    closeModal('authModalOverlay');
    showAlert(`Welcome back, ${data.user.name}!`, 'safe');
    if (window.location.pathname === '/dashboard') {
      window.location.reload();
    }
  } catch (err) {
    showAlert(err.message, 'danger');
  } finally {
    if (btn) { btn.disabled = false; btn.textContent = 'Sign In'; }
  }
}

async function handleRegisterSubmit(e) {
  e.preventDefault();
  const btn = document.getElementById('registerSubmitBtn');
  const name = document.getElementById('regName').value.trim();
  const email = document.getElementById('regEmail').value.trim();
  const phone = document.getElementById('regPhone').value.trim();
  const password = document.getElementById('regPassword').value;

  if (btn) { btn.disabled = true; btn.textContent = 'Creating account…'; }
  try {
    const data = await apiFetch('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ name, email, phone, password })
    });
    setAuth(data.accessToken, data.user);
    closeModal('authModalOverlay');
    showAlert(`Account created! Welcome, ${data.user.name}.`, 'safe');
    if (window.location.pathname === '/dashboard') {
      window.location.reload();
    }
  } catch (err) {
    showAlert(err.message, 'danger');
  } finally {
    if (btn) { btn.disabled = false; btn.textContent = 'Create Account'; }
  }
}

// ── API wrapper ──────────────────────────────────────────────
async function apiFetch(endpoint, opts = {}) {
  const token = getToken();
  const headers = { 'Content-Type': 'application/json', ...(opts.headers || {}) };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${endpoint}`, { ...opts, headers });
  const json = await res.json();
  if (!res.ok) throw new Error(json.message || 'Request failed');
  return json.data;
}

// ── Scan API & rendering ─────────────────────────────────────
async function runScan(type, target, senderNumber = null, durationSeconds = null, transcript = null) {
  const resultEl = document.getElementById('scanResult');
  const btn = document.getElementById('scanSubmitBtn');

  if (!target || !target.trim()) {
    showAlert('Please enter a value to analyse.', 'warn');
    return;
  }

  if (btn) { btn.disabled = true; btn.textContent = 'Analysing…'; }
  if (resultEl) resultEl.innerHTML = '<div class="empty-state"><div class="skeleton" style="height:140px;border-radius:var(--r-lg);"></div></div>';

  try {
    const payload = { type, target, senderNumber, durationSeconds, transcript };
    const scanEndpoint = getToken() ? '/analysis/scan' : '/analysis/scan-public';
    const data = await apiFetch(scanEndpoint, { method: 'POST', body: JSON.stringify(payload) });
    renderScanResult(data);
  } catch (err) {
    if (resultEl) resultEl.innerHTML = `<div class="alert alert-danger">${escHtml(err.message)}</div>`;
  } finally {
    if (btn) { btn.disabled = false; btn.textContent = 'Analyse'; }
  }
}

function riskClass(score) {
  if (score >= 75) return 'safe';
  if (score >= 40) return 'warn';
  return 'danger';
}

function riskBadgeClass(level) {
  const map = { SAFE: 'badge-safe', LOW: 'badge-low', MEDIUM: 'badge-medium', HIGH: 'badge-high', CRITICAL: 'badge-critical' };
  return map[(level || '').toUpperCase()] || 'badge-neutral';
}

function renderScanResult(data) {
  const el = document.getElementById('scanResult');
  if (!el) return;
  const a = data.analysis;
  const flags = data.flags || [];
  const recs  = data.recommendations || [];
  const cls   = riskClass(a.trustScore);

  let flagsHtml = '';
  if (flags.length) {
    flagsHtml = `
      <div class="result-section">
        <p class="result-section-title">Threat indicators (${flags.length})</p>
        <ul class="finding-list">
          ${flags.map(f => `<li class="finding-item flag"><span class="finding-icon">—</span>${escHtml(f)}</li>`).join('')}
        </ul>
      </div>`;
  } else {
    flagsHtml = `<div class="result-section"><div class="alert alert-success">No threat indicators detected.</div></div>`;
  }

  let recsHtml = '';
  if (recs.length) {
    recsHtml = `
      <div class="result-section">
        <p class="result-section-title">Recommended actions</p>
        <ul class="finding-list">
          ${recs.map(r => `<li class="finding-item rec"><span class="finding-icon">→</span>${escHtml(r)}</li>`).join('')}
        </ul>
      </div>`;
  }

  el.innerHTML = `
    <div class="result-panel">
      <div class="result-panel-head">
        <div class="result-score-row">
          <div class="score-ring ${cls}">${a.trustScore}</div>
          <div>
            <p class="result-section-title" style="margin-bottom:var(--sp-1);">Trust score</p>
            <span class="badge ${riskBadgeClass(a.riskLevel)}">${a.riskLevel} RISK</span>
          </div>
        </div>
        <div style="text-align:right;flex-shrink:0;">
          <p class="text-muted" style="font-size:var(--text-xs);margin-bottom:var(--sp-1);">${escHtml(a.type)} · ${a.processingTimeMs} ms</p>
          <code class="target-display">${escHtml(a.inputTarget)}</code>
        </div>
      </div>
      <div class="result-panel-body">
        <div class="result-section">
          <p class="result-section-title">Summary</p>
          <p style="font-size:var(--text-sm);">${escHtml(a.summary)}</p>
        </div>
        ${flagsHtml}
        ${recsHtml}
      </div>
    </div>`;

  el.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

// ── Community feed ───────────────────────────────────────────
async function loadCommunityFeed() {
  const el = document.getElementById('communityFeed');
  if (!el) return;
  el.innerHTML = '<div class="empty-state"><p>Loading reports…</p></div>';
  try {
    const data = await apiFetch('/community/feed');
    const list = data.content || [];
    if (!list.length) {
      el.innerHTML = '<div class="empty-state"><p>No reports on record yet.</p></div>';
      return;
    }
    el.innerHTML = list.map(r => feedCardHtml(r)).join('');
  } catch (err) {
    el.innerHTML = `<div class="alert alert-danger">${escHtml(err.message)}</div>`;
  }
}

function feedCardHtml(r) {
  const bc = r.status === 'VERIFIED' ? 'badge-safe' : (r.status === 'PENDING' ? 'badge-warn' : 'badge-neutral');
  return `
    <div class="feed-card">
      <div class="feed-card-meta">
        <div style="display:flex;align-items:center;gap:var(--sp-2);flex-wrap:wrap;">
          <span class="badge ${bc}">${r.status}</span>
          <span class="badge badge-neutral">${escHtml(r.type)}</span>
          ${r.city ? `<span class="text-muted" style="font-size:var(--text-xs);">${escHtml(r.city)}${r.state ? ', ' + escHtml(r.state) : ''}</span>` : ''}
        </div>
        <span class="text-muted" style="font-size:var(--text-xs);">${fmtDate(r.createdAt)}</span>
      </div>
      <p class="feed-card-title">${escHtml(r.title)}</p>
      <p class="feed-card-body">${escHtml(r.description)}</p>
      ${r.targetIdentifier ? `<code class="target-display" style="margin-bottom:var(--sp-3);">${escHtml(r.targetIdentifier)}</code>` : ''}
      <div class="feed-card-footer">
        <span class="text-muted" style="font-size:var(--text-xs);">Reported by ${escHtml(r.reporterName || 'Anonymous')}</span>
        <div style="display:flex;gap:var(--sp-2);">
          <button class="vote-btn" onclick="vote(${r.id},'up')">▲ ${r.upvotes}</button>
          <button class="vote-btn" onclick="vote(${r.id},'down')">▼ ${r.downvotes}</button>
        </div>
      </div>
    </div>`;
}

async function vote(id, dir) {
  try {
    await apiFetch(`/community/reports/${id}/${dir}vote`, { method: 'POST' });
    loadCommunityFeed();
  } catch (err) { showAlert(err.message, 'danger'); }
}

// ── Modal helpers ─────────────────────────────────────────────
function openModal(id) {
  const el = document.getElementById(id);
  if (el) el.classList.add('open');
}

function closeModal(id) {
  const el = document.getElementById(id);
  if (el) el.classList.remove('open');
}

// ── Alert banner ─────────────────────────────────────────────
function showAlert(msg, type = 'neutral') {
  const el = document.getElementById('globalAlert');
  if (!el) { alert(msg); return; }
  el.className = `alert alert-${type}`;
  el.textContent = msg;
  el.style.display = 'block';
  setTimeout(() => { el.style.display = 'none'; }, 5000);
}

// ── Utility helpers ───────────────────────────────────────────
function escHtml(str) {
  if (!str) return '';
  return str.replace(/[&<>"']/g, m => (
    { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[m]
  ));
}

function fmtDate(iso) {
  if (!iso) return '';
  try {
    const d = new Date(iso);
    return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
      + ' ' + d.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true });
  } catch { return iso; }
}

// ── Init ──────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  initAuthModal();
  refreshNavAuth();

  // Close modal on overlay click
  document.querySelectorAll('.modal-overlay').forEach(ov => {
    ov.addEventListener('click', e => {
      if (e.target === ov) ov.classList.remove('open');
    });
  });
});
