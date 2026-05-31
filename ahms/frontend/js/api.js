// ─── AHMS API Helper ────────────────────────────────────────────────────────
const BASE_URL = 'http://localhost:8080/api';

const api = {
  _token() { return localStorage.getItem('token'); },

  async _request(method, path, body) {
    const headers = { 'Content-Type': 'application/json' };
    const token = this._token();
    if (token) headers['Authorization'] = 'Bearer ' + token;

    const res = await fetch(BASE_URL + path, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    });

    const data = await res.json();
    if (!res.ok || data.success === false) {
      throw new Error(data.message || `HTTP ${res.status}`);
    }
    return data;
  },

  get(path)         { return this._request('GET',    path); },
  post(path, body)  { return this._request('POST',   path, body); },
  put(path, body)   { return this._request('PUT',    path, body); },
  patch(path, body) { return this._request('PATCH',  path, body); },
  del(path)         { return this._request('DELETE', path); },
};

// ─── Shared helpers ──────────────────────────────────────────────────────────
function logout() {
  localStorage.clear();
  window.location.href = 'login.html';
}

function requireAuth() {
  if (!localStorage.getItem('token')) window.location.href = 'login.html';
}

function requireAdmin() {
  requireAuth();
  if (localStorage.getItem('role') !== 'ADMIN') window.location.href = 'dashboard.html';
}

function showSection(name) {
  document.querySelectorAll('.section').forEach(s => s.classList.add('hidden'));
  document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
  const sec = document.getElementById('sec-' + name);
  if (sec) sec.classList.remove('hidden');
  event?.target?.classList.add('active');
  const title = document.getElementById('pageTitle');
  if (title) title.textContent = name.charAt(0).toUpperCase() + name.slice(1);
}

function openModal(id)  { document.getElementById(id).classList.remove('hidden'); }
function closeModal(id) { document.getElementById(id).classList.add('hidden'); }

function toggleSidebar() {
  document.getElementById('sidebar')?.classList.toggle('open');
}

function badge(status) {
  const map = {
    PENDING: 'badge-pending', CONFIRMED: 'badge-confirmed', COMPLETED: 'badge-completed',
    CANCELLED: 'badge-cancelled', AVAILABLE: 'badge-available', OCCUPIED: 'badge-occupied',
    CRITICAL: 'badge-critical', HIGH: 'badge-critical', APPROVED: 'badge-approved',
    REJECTED: 'badge-rejected', DISPATCHED: 'badge-confirmed', SCHEDULED: 'badge-pending',
  };
  return `<span class="badge ${map[status] || 'badge-pending'}">${status}</span>`;
}

function fmtDate(dt) {
  if (!dt) return '–';
  return new Date(dt).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}

function fmtDateTime(dt) {
  if (!dt) return '–';
  return new Date(dt).toLocaleString('en-IN', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' });
}
