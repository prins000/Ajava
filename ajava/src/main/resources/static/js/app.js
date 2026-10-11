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
}

function clearAuth() {
  localStorage.removeItem('ac_token');
  localStorage.removeItem('ac_user');
  refreshNavAuth();
}

function logout() { clearAuth(); window.location.reload(); }

function refreshNavAuth() {
  const el = document.getElementById('navAuth');
  if (el) el.innerHTML = '';
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
    const data = await apiFetch('/analysis/scan-public', { method: 'POST', body: JSON.stringify(payload) });
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
  refreshNavAuth();

  // Close modal on overlay click
  document.querySelectorAll('.modal-overlay').forEach(ov => {
    ov.addEventListener('click', e => {
      if (e.target === ov) ov.classList.remove('open');
    });
  });
});
