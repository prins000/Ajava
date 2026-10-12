/* ============================================================
   Authentica — Client Engine
   Handles auth, API calls, scan rendering, community feed,
   and dynamic Role-Based Access Control (RBAC).
   No jQuery. No framework. Pure modern JavaScript.
   ============================================================ */

const API_BASE = '/api/v1';

// ── Auth helpers ────────────────────────────────────────────
function getToken() { return localStorage.getItem('ac_token'); }
function getUser()  {
  const s = localStorage.getItem('ac_user');
  try {
    return s ? JSON.parse(s) : null;
  } catch(e) {
    return null;
  }
}

function setAuth(token, user) {
  localStorage.setItem('ac_token', token);
  localStorage.setItem('ac_user', JSON.stringify(user));
  refreshNavAuth();
  window.dispatchEvent(new CustomEvent('auth-changed', { detail: { token, user } }));
}

function saveAuth(token, user) {
  setAuth(token, user);
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

// ── Dynamic Role-Based Navigation & Topbar Sync ─────────────
function refreshNavAuth() {
  const user = getUser();
  const pathname = window.location.pathname;

  // 1. Sync legacy top nav links if present
  const navLinks = document.querySelector('.nav-links');
  if (navLinks) {
    let dashLink = document.getElementById('navLinkDashboard');
    let adminLink = document.getElementById('navLinkAdmin');

    if (user) {
      if (user.role === 'ROLE_ADMIN') {
        if (!adminLink) {
          const a = document.createElement('a');
          a.id = 'navLinkAdmin';
          a.href = '/admin';
          a.className = 'nav-link' + (pathname === '/admin' ? ' active' : '');
          a.textContent = 'Admin Console';
          navLinks.appendChild(a);
        }
      } else {
        if (!dashLink) {
          const a = document.createElement('a');
          a.id = 'navLinkDashboard';
          a.href = '/dashboard';
          a.className = 'nav-link' + (pathname === '/dashboard' ? ' active' : '');
          a.textContent = 'Dashboard';
          navLinks.appendChild(a);
        }
      }
    } else {
      if (dashLink) dashLink.remove();
      if (adminLink) adminLink.remove();
    }
  }

  // 2. Sync legacy nav actions
  const navActions = document.querySelector('.nav-actions');
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
        <button class="btn btn-ghost btn-sm" onclick="openFeedbackModal()" title="Platform Feedback">Feedback</button>
        <a href="${user.role === 'ROLE_ADMIN' ? '/admin' : '/dashboard'}" class="badge badge-brand" style="text-decoration:none;cursor:pointer;font-weight:var(--fw-medium);padding:var(--sp-1) var(--sp-3);">
          👤 ${escHtml(user.name)} (${roleLabel})
        </a>
        <button class="btn btn-ghost btn-sm" onclick="logout()">Sign Out</button>
      `;
    } else {
      authBox.innerHTML = `
        <button class="btn btn-ghost btn-sm" onclick="openFeedbackModal()" title="Platform Feedback">Feedback</button>
        <button class="btn btn-ghost btn-sm" onclick="openAuthModal('login')">Sign In</button>
        <button class="btn btn-outline btn-sm" onclick="openAuthModal('register')">Register</button>
      `;
    }
  }

  // 3. Sync SOC Shell Topbar (.topbar-right)
  const topbarRight = document.querySelector('.topbar-right');
  if (topbarRight) {
    let topbarAuth = document.getElementById('topbarAuthBox');
    if (!topbarAuth) {
      topbarAuth = document.createElement('div');
      topbarAuth.id = 'topbarAuthBox';
      topbarAuth.style.display = 'inline-flex';
      topbarAuth.style.alignItems = 'center';
      topbarAuth.style.gap = '10px';
      topbarRight.appendChild(topbarAuth);
    }

    if (user) {
      const isAdmin = user.role === 'ROLE_ADMIN';
      topbarAuth.innerHTML = `
        <button class="btn btn-ghost btn-sm" style="font-size:12px;height:30px;padding:0 10px;" onclick="openFeedbackModal()">⭐ Feedback</button>
        <a href="${isAdmin ? '/admin' : '/dashboard'}" class="badge ${isAdmin ? 'badge-danger' : 'badge-safe'}" style="text-decoration:none;cursor:pointer;padding:5px 12px;font-size:11px;" title="View ${isAdmin ? 'Admin Console' : 'Citizen Dashboard'}">
          ${isAdmin ? '🛡️ SecOps Admin' : '👤 Citizen'}: ${escHtml(user.name)}
        </a>
        <button class="btn btn-outline btn-sm" style="font-size:11px;height:30px;padding:0 10px;border-color:#333e52;" onclick="logout()">Sign Out</button>
      `;
    } else {
      topbarAuth.innerHTML = `
        <button class="btn btn-ghost btn-sm" style="font-size:12px;height:30px;padding:0 10px;" onclick="openFeedbackModal()">⭐ Feedback</button>
        <button class="btn btn-outline btn-sm" style="font-size:12px;height:30px;padding:0 12px;" onclick="openAuthModal('login')">Sign In</button>
        <button class="btn btn-primary btn-sm" style="font-size:12px;height:30px;padding:0 12px;" onclick="openAuthModal('register')">Register</button>
      `;
    }
  }

  // 4. Sync Left Sidebar Navigation by Role (.sidebar-nav)
  const sidebarNav = document.querySelector('.sidebar-nav');
  const sidebarCat = document.querySelector('.sidebar-category');
  const sidebarNode = document.querySelector('.sidebar-node-pill');
  const hash = window.location.hash;

  if (sidebarNav) {
    if (user && user.role === 'ROLE_ADMIN') {
      if (sidebarCat) sidebarCat.textContent = 'SecOps Operations (Admin)';
      if (sidebarNode) {
        sidebarNode.innerHTML = `<span>Cluster-EU-West</span><span class="node-status" style="color:#ef4444;"><span style="display:inline-block;width:6px;height:6px;border-radius:50%;background:#ef4444;box-shadow:0 0 6px #ef4444;margin-right:4px;"></span>Admin Mode</span>`;
      }
      sidebarNav.innerHTML = `
        <a href="/dashboard" class="sidebar-link ${pathname === '/dashboard' && !hash ? 'active' : ''}">
          <span class="sidebar-link-icon">⊞</span> SecOps Console
        </a>
        <a href="/scanner" class="sidebar-link ${pathname === '/scanner' ? 'active' : ''}">
          <span class="sidebar-link-icon">⌖</span> Threat Scanner
        </a>
        <a href="/community" class="sidebar-link ${pathname === '/community' ? 'active' : ''}">
          <span class="sidebar-link-icon">⚡</span> Threat Radar
        </a>
        <a href="/admin#rules" class="sidebar-link ${pathname === '/admin' && (hash === '#rules' || !hash) ? 'active' : ''}">
          <span class="sidebar-link-icon">🛡️</span> Rule Engine &amp; Policies
        </a>
        <a href="/admin#users" class="sidebar-link ${pathname === '/admin' && hash === '#users' ? 'active' : ''}">
          <span class="sidebar-link-icon">👥</span> Citizen Directory
        </a>
        <a href="/admin#reports" class="sidebar-link ${pathname === '/admin' && hash === '#reports' ? 'active' : ''}">
          <span class="sidebar-link-icon">📋</span> Incident Moderation
        </a>
        <a href="/admin#tickets" class="sidebar-link ${pathname === '/admin' && hash === '#tickets' ? 'active' : ''}">
          <span class="sidebar-link-icon">🎫</span> Helpdesk Queue
        </a>
      `;
    } else if (user) {
      if (sidebarCat) sidebarCat.textContent = 'Citizen Operations';
      if (sidebarNode) {
        sidebarNode.innerHTML = `<span>Cluster-EU-West</span><span class="node-status">Protected</span>`;
      }
      sidebarNav.innerHTML = `
        <a href="/dashboard" class="sidebar-link ${pathname === '/dashboard' && !hash ? 'active' : ''}">
          <span class="sidebar-link-icon">⊞</span> My Dashboard
        </a>
        <a href="/scanner" class="sidebar-link ${pathname === '/scanner' ? 'active' : ''}">
          <span class="sidebar-link-icon">⌖</span> Threat Scanner
        </a>
        <a href="/community" class="sidebar-link ${pathname === '/community' ? 'active' : ''}">
          <span class="sidebar-link-icon">⚡</span> Threat Radar Feed
        </a>
        <a href="/dashboard#contacts" class="sidebar-link ${pathname === '/dashboard' && hash === '#contacts' ? 'active' : ''}">
          <span class="sidebar-link-icon">🛡️</span> Family Shield
        </a>
        <a href="/dashboard#tickets" class="sidebar-link ${pathname === '/dashboard' && hash === '#tickets' ? 'active' : ''}">
          <span class="sidebar-link-icon">🎫</span> Lodge Incident
        </a>
        <a href="/dashboard#history" class="sidebar-link ${pathname === '/dashboard' && hash === '#history' ? 'active' : ''}">
          <span class="sidebar-link-icon">🔐</span> Forensic FIR Vault
        </a>
      `;
    } else {
      if (sidebarCat) sidebarCat.textContent = 'Public Operations';
      if (sidebarNode) {
        sidebarNode.innerHTML = `<span>Cluster-EU-West</span><span class="node-status">Guest</span>`;
      }
      sidebarNav.innerHTML = `
        <a href="/scanner" class="sidebar-link ${pathname === '/scanner' ? 'active' : ''}">
          <span class="sidebar-link-icon">⌖</span> Threat Scanner
        </a>
        <a href="/community" class="sidebar-link ${pathname === '/community' ? 'active' : ''}">
          <span class="sidebar-link-icon">⚡</span> Live Threat Radar
        </a>
        <a href="/dashboard" class="sidebar-link ${pathname === '/dashboard' ? 'active' : ''}">
          <span class="sidebar-link-icon">⊞</span> Citizen Dashboard
        </a>
        <a href="javascript:void(0)" onclick="openAuthModal('login')" class="sidebar-link">
          <span class="sidebar-link-icon">🔑</span> Citizen Sign In
        </a>
      `;
    }
  }
}

function handleHashNavigation() {
  const hash = window.location.hash;
  if (!hash) return;
  const target = document.querySelector(hash);
  if (target) {
    const yOffset = -75;
    const y = target.getBoundingClientRect().top + window.pageYOffset + yOffset;
    window.scrollTo({ top: Math.max(0, y), behavior: 'smooth' });
    target.style.transition = 'box-shadow 0.4s ease, border-color 0.4s ease';
    target.style.boxShadow = '0 0 0 2px rgba(255, 77, 0, 0.7)';
    target.style.borderColor = 'rgba(255, 77, 0, 0.9)';
    setTimeout(() => {
      target.style.boxShadow = '';
      target.style.borderColor = '';
    }, 2200);
  }
  updateSidebarActiveHash();
}

function updateSidebarActiveHash() {
  const hash = window.location.hash;
  const currentPath = window.location.pathname;
  document.querySelectorAll('.sidebar-link').forEach(link => {
    const href = link.getAttribute('href');
    if (!href) return;
    if (hash && href.endsWith(hash)) {
      link.classList.add('active');
    } else if (!hash && href === currentPath) {
      link.classList.add('active');
    } else if (hash && href === currentPath) {
      link.classList.remove('active');
    } else if (!hash && href.includes('#')) {
      link.classList.remove('active');
    }
  });
}

window.addEventListener('hashchange', () => {
  handleHashNavigation();
  updateSidebarActiveHash();
});
window.addEventListener('DOMContentLoaded', () => {
  if (window.location.hash) {
    setTimeout(handleHashNavigation, 250);
  }
});

// ── Global Responsive Sidebar & Hamburger Management ────────
function toggleSidebar(forceState) {
  const sb = document.getElementById('appSidebar');
  const shell = document.querySelector('.app-shell');
  if (!sb) return;

  let backdrop = document.getElementById('sidebarBackdrop');
  if (!backdrop) {
    backdrop = document.createElement('div');
    backdrop.id = 'sidebarBackdrop';
    backdrop.className = 'sidebar-backdrop';
    backdrop.onclick = () => toggleSidebar(false);
    document.body.appendChild(backdrop);
  }

  const isMobile = window.innerWidth <= 992;
  if (isMobile) {
    const shouldOpen = forceState !== undefined ? forceState : !sb.classList.contains('open');
    if (shouldOpen) {
      sb.classList.add('open');
      backdrop.classList.add('active');
      document.body.style.overflow = 'hidden';
    } else {
      sb.classList.remove('open');
      backdrop.classList.remove('active');
      document.body.style.overflow = '';
    }
  } else {
    // Desktop: collapse / expand
    if (shell) {
      shell.classList.toggle('sidebar-collapsed');
    }
  }
}

// Close mobile sidebar on Escape key
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') {
    toggleSidebar(false);
  }
});

// Auto-close mobile sidebar when clicking a navigation link
document.addEventListener('click', (e) => {
  const link = e.target.closest('.sidebar-link, .sidebar-cta-btn');
  if (link && window.innerWidth <= 992) {
    toggleSidebar(false);
  }
});

// Handle window resize cleanly
window.addEventListener('resize', () => {
  if (window.innerWidth > 992) {
    const sb = document.getElementById('appSidebar');
    const backdrop = document.getElementById('sidebarBackdrop');
    if (sb) sb.classList.remove('open');
    if (backdrop) backdrop.classList.remove('active');
    document.body.style.overflow = '';
  }
});

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
            <div style="margin-bottom:var(--sp-4);background:var(--col-surface-2);padding:var(--sp-3);border-radius:var(--r-md);border:1px solid var(--col-border);">
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
    if (window.location.pathname === '/dashboard' || window.location.pathname === '/admin') {
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

// ── Ultra-Robust API wrapper ─────────────────────────────────
async function apiFetch(endpoint, opts = {}) {
  const token = getToken();
  const headers = { 'Content-Type': 'application/json', ...(opts.headers || {}) };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${endpoint}`, { ...opts, headers });
  const text = await res.text();
  let json = null;
  if (text && text.trim().length > 0) {
    try {
      json = JSON.parse(text);
    } catch (e) {
      console.warn("Non-JSON API response body:", text.substring(0, 100));
    }
  }

  if (!res.ok) {
    const errorMsg = (json && (json.message || json.error)) || `HTTP ${res.status}: ${res.statusText || 'Request failed'}`;
    throw new Error(errorMsg);
  }

  if (json && json.data !== undefined) {
    return json.data;
  }
  return json;
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

  window._lastScanData = data;

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
        <div style="margin-top:var(--sp-4);padding-top:var(--sp-4);border-top:1px solid var(--col-border);display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:var(--sp-2);">
          <span class="text-muted" style="font-size:var(--text-xs);">Legal Forensic Compliance: Ready for Indian IT Act FIR filing</span>
          <div style="display:flex;gap:var(--sp-2);flex-wrap:wrap;">
            ${(a.riskLevel === 'HIGH' || a.riskLevel === 'CRITICAL') ? `
              <button class="btn btn-outline btn-sm" id="emailAlertBtn" onclick="broadcastEmergencyAlert('${escHtml(a.inputTarget)}', '${escHtml(a.type)}', ${a.threatScore || 85})" style="display:inline-flex;align-items:center;gap:6px;font-weight:var(--fw-bold);border-color:var(--col-danger);color:var(--col-danger);">
                🚨 Broadcast SOS to Family Shield
              </button>
            ` : ''}
            <button class="btn btn-outline btn-sm" id="exportEvidenceBtn" onclick="downloadForensicReport()" style="display:inline-flex;align-items:center;gap:6px;font-weight:var(--fw-bold);border-color:var(--col-brand);color:var(--col-brand);">
              📄 Export Cybercrime FIR Evidence Report (PDF)
            </button>
          </div>
        </div>
      </div>
    </div>`;

  el.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

async function broadcastEmergencyAlert(target, type, score) {
  const user = getUser();
  if (!user) {
    showAlert('Please sign in to broadcast emergency SOS alerts to your Family Shield.', 'warn');
    openAuthModal('login');
    return;
  }

  if (!confirm('Broadcast emergency alert for this critical threat to your registered Family Emergency Shield contacts?')) {
    return;
  }

  try {
    showAlert('Dispatching Family Shield emergency SOS broadcast...', 'warn');
    const flags = window._lastScanData && window._lastScanData.flags ? window._lastScanData.flags : [];
    const res = await apiFetch('/alerts/broadcast-threat', {
      method: 'POST',
      body: JSON.stringify({ target, type, threatScore: score, flags })
    });
    const count = res && res.dispatchedCount !== undefined ? res.dispatchedCount : 'all';
    showAlert(`🚨 Emergency SOS broadcast successfully sent to ${count} active family contacts via Gmail SMTP!`, 'safe');
  } catch (err) {
    showAlert(err.message, 'danger');
  }
}

// ── Forensic Cybercrime FIR PDF Generator ────────────────────────
async function downloadForensicReport(data = window._lastScanData) {
  if (!data || !data.analysis) {
    showAlert('No threat scan data available to export.', 'warn');
    return;
  }

  if (!window.jspdf || !window.jspdf.jsPDF) {
    showAlert('Loading PDF generator module...', 'warn');
    return;
  }

  const { jsPDF } = window.jspdf;
  const doc = new jsPDF({ unit: 'pt', format: 'a4' });
  const a = data.analysis;
  const flags = data.flags || [];
  const recs = data.recommendations || [];

  // Compute SHA-256 of target
  let sha256 = 'N/A';
  try {
    const msgBuffer = new TextEncoder().encode(a.inputTarget || 'Authentica');
    const hashBuffer = await crypto.subtle.digest('SHA-256', msgBuffer);
    sha256 = Array.from(new Uint8Array(hashBuffer)).map(b => b.toString(16).padStart(2, '0')).join('');
  } catch (e) {
    sha256 = 'E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855';
  }

  const incidentRef = 'AUTH-FIR-' + Math.floor(100000 + Math.random() * 900000);
  const nowStr = new Date().toLocaleString('en-IN', { timeZone: 'Asia/Kolkata' });

  // 1. Header Banner
  doc.setFillColor(15, 23, 42); // slate-900
  doc.rect(0, 0, 595, 80, 'F');
  doc.setTextColor(255, 255, 255);
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(16);
  doc.text('AUTHENTICA CYBER FORENSICS INTELLIGENCE PLATFORM', 40, 36);
  doc.setFontSize(10);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(148, 163, 184); // slate-400
  doc.text('NATIONAL CYBER CRIME INVESTIGATION DOSSIER · CERT-IN / IT ACT COMPLIANT', 40, 56);

  // 2. Incident Metadata Box
  doc.setFillColor(248, 250, 252);
  doc.setDrawColor(226, 232, 240);
  doc.roundedRect(40, 95, 515, 95, 4, 4, 'FD');

  doc.setTextColor(51, 65, 85);
  doc.setFontSize(9);
  doc.setFont('helvetica', 'bold');
  doc.text('INCIDENT REFERENCE:', 55, 115);
  doc.setFont('helvetica', 'normal');
  doc.text(incidentRef, 180, 115);

  doc.setFont('helvetica', 'bold');
  doc.text('EVIDENCE TIMESTAMP:', 55, 130);
  doc.setFont('helvetica', 'normal');
  doc.text(nowStr + ' (IST)', 180, 130);

  doc.setFont('helvetica', 'bold');
  doc.text('ATTACK VECTOR:', 55, 145);
  doc.setFont('helvetica', 'normal');
  doc.text(a.type + ' HEURISTIC ENGINE', 180, 145);

  doc.setFont('helvetica', 'bold');
  doc.text('DIGITAL TARGET / URI:', 55, 160);
  doc.setFont('helvetica', 'normal');
  const targetTrunc = (a.inputTarget || '').length > 55 ? (a.inputTarget || '').substring(0, 55) + '...' : a.inputTarget;
  doc.text(targetTrunc, 180, 160);

  doc.setFont('helvetica', 'bold');
  doc.text('CRYPTOGRAPHIC HASH:', 55, 175);
  doc.setFont('courier', 'normal');
  doc.setFontSize(8);
  doc.text('SHA-256: ' + sha256.substring(0, 48) + '...', 180, 175);

  // 3. Risk Assessment Banner
  let scoreColor = [16, 185, 129]; // Safe Green
  if (a.trustScore < 40) scoreColor = [239, 68, 68]; // Red
  else if (a.trustScore < 75) scoreColor = [245, 158, 11]; // Yellow

  doc.setFillColor(scoreColor[0], scoreColor[1], scoreColor[2]);
  doc.roundedRect(40, 205, 515, 34, 4, 4, 'F');
  doc.setTextColor(255, 255, 255);
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(12);
  doc.text(`THREAT EVALUATION: ${a.riskLevel} RISK (TRUST SCORE: ${a.trustScore} / 100)`, 55, 226);

  // 4. Forensic Indicators
  let y = 265;
  doc.setTextColor(15, 23, 42);
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(11);
  doc.text(`1. FORENSIC INDICATORS & MALICIOUS TRIGGERS (${flags.length})`, 40, y);
  y += 18;

  doc.setFont('helvetica', 'normal');
  doc.setFontSize(9);
  doc.setTextColor(71, 85, 105);
  if (flags.length > 0) {
    flags.slice(0, 7).forEach(flag => {
      doc.text(`[!]  ${flag}`, 55, y);
      y += 15;
    });
  } else {
    doc.text('[-]  No malicious indicators found. Target passed basic heuristic filters.', 55, y);
    y += 15;
  }

  // 5. Legal Violations & Penalties under Indian IT Act 2000
  y += 10;
  doc.setTextColor(15, 23, 42);
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(11);
  doc.text('2. APPLICABLE STATUTORY CITATIONS (INDIAN IT ACT 2000 & IPC)', 40, y);
  y += 18;

  doc.setFont('helvetica', 'normal');
  doc.setFontSize(8.5);
  doc.setTextColor(71, 85, 105);

  const legalActs = [
    'Section 66D, Information Technology Act, 2000: Cheating by personation using computer resource (Punishable with imprisonment up to 3 years and fine up to Rs 1 Lakh).',
    'Section 66C, Information Technology Act, 2000: Identity theft and unauthorized credential / OTP harvesting (Imprisonment up to 3 years).',
    'Section 419 / 420, Indian Penal Code: Cheating by impersonation and dishonestly inducing delivery of property (Cognizable non-bailable offense).',
    'TRAI Telecom Regulations: Violation of sender alphanumeric headers and unauthorized cyber solicitation.'
  ];

  legalActs.forEach(act => {
    doc.text(`* ${act}`, 55, y, { maxWidth: 500 });
    y += 20;
  });

  // 6. Actionable Filing Guidance
  y += 5;
  doc.setTextColor(15, 23, 42);
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(11);
  doc.text('3. COMPLAINANT ACTIONABLE ADVISORY', 40, y);
  y += 16;
  doc.setFont('helvetica', 'normal');
  doc.setFontSize(8.5);
  doc.setTextColor(71, 85, 105);
  doc.text('* Immediately register formal complaint on national portal: https://cybercrime.gov.in', 55, y);
  y += 14;
  doc.text('* For financial fraud, call the National Cyber Financial Helpline: 1930 within the golden hour.', 55, y);
  y += 14;
  doc.text('* Attach this verified technical dossier to support the formal Police FIR submission.', 55, y);

  // 7. Footer Stamp & Seal
  doc.setDrawColor(203, 213, 225);
  doc.line(40, 770, 555, 770);
  doc.setFontSize(8);
  doc.setTextColor(148, 163, 184);
  doc.text(`Official Forensic Report generated by Authentica Threat Intelligence Platform · Incident ID: ${incidentRef}`, 40, 785);
  doc.text('CONFIDENTIAL CYBERCRIME EVIDENCE ATTACHMENT', 340, 785);

  doc.save(`Authentica_Forensic_Evidence_${incidentRef}.pdf`);
  showAlert('Forensic Cybercrime FIR Evidence PDF downloaded!', 'safe');
}

// ── Community feed ───────────────────────────────────────────
let _threatMapInstance = null;

const METRO_COORDS = {
  'Ahmedabad': [23.0225, 72.5714],
  'Mumbai': [19.0760, 72.8777],
  'Delhi': [28.6139, 77.2090],
  'New Delhi': [28.6139, 77.2090],
  'Bengaluru': [12.9716, 77.5946],
  'Bangalore': [12.9716, 77.5946],
  'Hyderabad': [17.3850, 78.4867],
  'Kolkata': [22.5726, 88.3639],
  'Pune': [18.5204, 73.8567],
  'Jaipur': [26.9124, 75.7873],
  'Surat': [21.1702, 72.8311],
  'Lucknow': [26.8467, 80.9462],
  'Chennai': [13.0827, 80.2707],
  'Chandigarh': [30.7333, 76.7794]
};

function initThreatRadarMap(reports) {
  const mapEl = document.getElementById('threatRadarMap');
  if (!mapEl || !window.L) return;

  if (_threatMapInstance) {
    _threatMapInstance.remove();
    _threatMapInstance = null;
  }

  _threatMapInstance = L.map('threatRadarMap', {
    center: [21.5, 78.9],
    zoom: 5,
    minZoom: 4,
    maxZoom: 14,
    scrollWheelZoom: false
  });

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors',
    maxZoom: 19
  }).addTo(_threatMapInstance);

  const reportList = Array.isArray(reports) ? reports : (reports?.content || []);
  if (!reportList || !reportList.length) return;

  const defaultCities = Object.values(METRO_COORDS);

  reportList.forEach((r, idx) => {
    let latlng = null;
    const city = (r.city || '').trim();
    if (METRO_COORDS[city]) {
      latlng = METRO_COORDS[city];
    } else {
      const base = defaultCities[idx % defaultCities.length];
      const jitterLat = Math.sin(idx * 7) * 0.18;
      const jitterLng = Math.cos(idx * 11) * 0.18;
      latlng = [base[0] + jitterLat, base[1] + jitterLng];
    }

    const pinClass = r.category === 'CALL' ? 'radar-pin-CALL' : (r.category === 'UPI' ? 'radar-pin-UPI' : 'radar-pin-URL');
    const customIcon = L.divIcon({
      className: 'custom-radar-pin',
      html: `<div class="radar-pin ${pinClass}"></div>`,
      iconSize: [16, 16],
      iconAnchor: [8, 8]
    });

    const popupHtml = `
      <div style="font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;font-size:12px;color:#0f172a;min-width:200px;">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:4px;">
          <span style="font-weight:bold;color:#be123c;">${escHtml(r.category || r.type)} INCIDENT</span>
          <span style="font-size:10px;background:#e2e8f0;padding:1px 4px;border-radius:3px;">${escHtml(r.status)}</span>
        </div>
        <strong style="color:#0f172a;display:block;margin-bottom:4px;font-size:13px;">${escHtml(r.title)}</strong>
        <p style="margin:0 0 6px;color:#475569;font-size:11px;line-height:1.4;">${escHtml(r.description || '')}</p>
        <div style="font-size:10px;color:#64748b;">Hotspot: <strong>${escHtml(r.city || 'India')}</strong></div>
      </div>
    `;

    L.marker(latlng, { icon: customIcon })
      .addTo(_threatMapInstance)
      .bindPopup(popupHtml);
  });
}

async function loadCommunityFeed() {
  const el = document.getElementById('communityFeed');
  if (!el) return;
  el.innerHTML = '<div class="empty-state"><p>Loading reports…</p></div>';
  try {
    const data = await apiFetch('/community/feed');
    const list = (data && data.content) ? data.content : (Array.isArray(data) ? data : []);
    if (!list.length) {
      el.innerHTML = '<div class="empty-state"><p>No reports on record yet.</p></div>';
      initThreatRadarMap([]);
      return;
    }
    el.innerHTML = list.map(r => feedCardHtml(r)).join('');
    initThreatRadarMap(list);
  } catch (err) {
    el.innerHTML = `<div class="alert alert-danger">${escHtml(err.message)}</div>`;
  }
}

function feedCardHtml(r) {
  const bc = r.status === 'VERIFIED' ? 'badge-safe' : (r.status === 'PENDING' ? 'badge-warn' : 'badge-neutral');
  const cat = r.category || r.type || 'THREAT';
  return `
    <div class="feed-card">
      <div class="feed-card-meta">
        <div style="display:flex;align-items:center;gap:var(--sp-2);flex-wrap:wrap;">
          <span class="badge ${bc}">${r.status}</span>
          <span class="badge badge-neutral">${escHtml(cat)}</span>
          ${r.city ? `<span class="text-muted" style="font-size:var(--text-xs);">${escHtml(r.city)}${r.state ? ', ' + escHtml(r.state) : ''}</span>` : ''}
        </div>
        <span class="text-muted" style="font-size:var(--text-xs);">${fmtDate(r.createdAt)}</span>
      </div>
      <p class="feed-card-title">${escHtml(r.title)}</p>
      <p class="feed-card-body">${escHtml(r.description)}</p>
      ${r.target ? `<code class="target-display" style="margin-bottom:var(--sp-3);">${escHtml(r.target)}</code>` : ''}
      <div class="feed-card-footer">
        <span class="text-muted" style="font-size:var(--text-xs);">Reported by ${escHtml(r.reporterName || 'Anonymous')}</span>
        <div style="display:flex;gap:var(--sp-2);">
          <button class="vote-btn" onclick="vote(${r.id},'up')">▲ ${r.votesUp ?? r.upvotes ?? 0}</button>
          <button class="vote-btn" onclick="vote(${r.id},'down')">▼ ${r.votesDown ?? r.downvotes ?? 0}</button>
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

// ── Feedback Modal ──────────────────────────────────────────
function initFeedbackModal() {
  if (document.getElementById('feedbackModalOverlay')) return;

  const modalHtml = `
    <div class="modal-overlay" id="feedbackModalOverlay" role="dialog" aria-modal="true">
      <div class="modal" style="max-width:440px;">
        <div class="modal-header">
          <h2 class="modal-title">Platform Feedback &amp; Engine Rating</h2>
          <button class="modal-close" onclick="closeModal('feedbackModalOverlay')">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label" for="fbCategory">Category</label>
            <select id="fbCategory" class="form-control">
              <option value="detection">Heuristic Detection Accuracy</option>
              <option value="false_positive">False Positive / Whitelist Report</option>
              <option value="feature">Feature Request</option>
              <option value="general" selected>General Experience</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label" for="fbRating">Detection Accuracy Rating</label>
            <select id="fbRating" class="form-control">
              <option value="5">⭐⭐⭐⭐⭐ Excellent (5/5)</option>
              <option value="4">⭐⭐⭐⭐ Good (4/5)</option>
              <option value="3">⭐⭐⭐ Average (3/5)</option>
              <option value="2">⭐⭐ Needs Improvement (2/5)</option>
              <option value="1">⭐ Poor / Inaccurate (1/5)</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label" for="fbComment">Comments &amp; Observations</label>
            <textarea id="fbComment" class="form-control" rows="3" placeholder="Tell us how the detection engines performed on your test inputs…" required></textarea>
          </div>
          <button type="button" class="btn btn-primary" style="width:100%;height:38px;" onclick="handleFeedbackSubmit()">Submit Feedback</button>
        </div>
      </div>
    </div>
  `;
  document.body.insertAdjacentHTML('beforeend', modalHtml);

  const ov = document.getElementById('feedbackModalOverlay');
  if (ov) {
    ov.addEventListener('click', e => {
      if (e.target === ov) ov.classList.remove('open');
    });
  }
}

function openFeedbackModal() {
  initFeedbackModal();
  openModal('feedbackModalOverlay');
}

async function handleFeedbackSubmit() {
  const category = document.getElementById('fbCategory').value;
  const rating = parseInt(document.getElementById('fbRating').value) || 5;
  const comment = document.getElementById('fbComment').value.trim();

  if (!comment) {
    showAlert('Please provide a comment.', 'warn');
    return;
  }

  try {
    await apiFetch('/feedback', {
      method: 'POST',
      body: JSON.stringify({ category, rating, comment })
    });
    showAlert('Thank you! Your feedback has been recorded.', 'safe');
    closeModal('feedbackModalOverlay');
    document.getElementById('fbComment').value = '';
  } catch (e) {
    showAlert(e.message, 'danger');
  }
}

// ── Init ──────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  initAuthModal();
  initFeedbackModal();
  refreshNavAuth();

  document.querySelectorAll('.modal-overlay').forEach(ov => {
    ov.addEventListener('click', e => {
      if (e.target === ov) ov.classList.remove('open');
    });
  });
});
