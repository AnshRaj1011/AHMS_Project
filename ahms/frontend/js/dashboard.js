// ─── AHMS Patient Dashboard ──────────────────────────────────────────────────
requireAuth();

const userId    = localStorage.getItem('userId');
const fullName  = localStorage.getItem('fullName');
let   patientId = null;
let   allDoctors = [];
let   sessionId = null;

document.getElementById('userName').textContent    = fullName;
document.getElementById('welcomeName').textContent = fullName;
document.getElementById('apptDate').min = new Date().toISOString().split('T')[0];

// ─── Init ─────────────────────────────────────────────────────────────────────
async function init() {
  try {
    const res = await api.get('/patients/user/' + userId);
    const p   = res.data;
    patientId = p.id;
    loadHomeStats();
    loadAppointments();
    loadDoctors();
    renderProfile(p);
  } catch (e) {
    console.error('Init failed:', e);
  }
}

// ─── Home stats ───────────────────────────────────────────────────────────────
async function loadHomeStats() {
  try {
    const [appts, beds, icuBeds, labs] = await Promise.all([
      api.get('/appointments/patient/' + patientId),
      api.get('/beds/available'),
      api.get('/beds/available/icu'),
      api.get('/lab-tests/patient/' + patientId),
    ]);
    document.getElementById('statAppts').textContent = appts.data.length;
    document.getElementById('statBeds').textContent  = beds.data.length;
    document.getElementById('statIcu').textContent   = icuBeds.data.length;
    document.getElementById('statLabs').textContent  = labs.data.length;
  } catch (e) { console.error(e); }
}

// ─── Appointments ─────────────────────────────────────────────────────────────
async function loadAppointments() {
  const el = document.getElementById('apptList');
  try {
    const res = await api.get('/appointments/patient/' + patientId);
    const list = res.data;
    if (!list.length) { el.innerHTML = '<p class="text-muted">No appointments yet.</p>'; return; }
    el.innerHTML = list.map(a => `
      <div class="card-item">
        <div class="card-info">
          <div class="card-title">Dr. ${a.doctor?.user?.fullName || '–'} · ${a.doctor?.specialization || ''}</div>
          <div class="card-sub">${fmtDate(a.appointmentDate)} at ${a.appointmentTime} · ${a.type}</div>
          <div class="card-sub" style="margin-top:.25rem">${a.reason || ''}</div>
        </div>
        <div class="card-actions">
          ${badge(a.status)}
          ${a.status === 'PENDING' || a.status === 'CONFIRMED'
            ? `<button class="btn-outline btn-sm" onclick="cancelAppt(${a.id})">Cancel</button>` : ''}
        </div>
      </div>`).join('');
  } catch (e) { el.innerHTML = '<p class="error-msg">Failed to load appointments.</p>'; }
}

async function cancelAppt(id) {
  if (!confirm('Cancel this appointment?')) return;
  await api.patch('/appointments/' + id + '/status', { status: 'CANCELLED' });
  loadAppointments();
}

async function bookAppointment() {
  const msg = document.getElementById('apptMsg');
  msg.textContent = '';
  try {
    await api.post('/appointments', {
      patientId: patientId,
      doctorId:  parseInt(document.getElementById('apptDoctor').value),
      appointmentDate: document.getElementById('apptDate').value,
      appointmentTime: document.getElementById('apptTime').value,
      type:   document.getElementById('apptType').value,
      reason: document.getElementById('apptReason').value,
    });
    msg.textContent = '✅ Appointment booked successfully!';
    setTimeout(() => { closeModal('bookApptModal'); loadAppointments(); }, 1200);
  } catch (e) { msg.textContent = '❌ ' + e.message; msg.style.color = 'var(--danger)'; }
}

// ─── Doctors ──────────────────────────────────────────────────────────────────
async function loadDoctors() {
  const el  = document.getElementById('doctorList');
  const sel = document.getElementById('apptDoctor');
  try {
    const res = await api.get('/doctors/available');
    allDoctors = res.data;
    renderDoctors(allDoctors);
    sel.innerHTML = allDoctors.map(d =>
      `<option value="${d.id}">Dr. ${d.user?.fullName} – ${d.specialization}</option>`
    ).join('');
  } catch (e) { el.innerHTML = '<p class="error-msg">Failed to load doctors.</p>'; }
}

function renderDoctors(list) {
  const el = document.getElementById('doctorList');
  if (!list.length) { el.innerHTML = '<p class="text-muted">No doctors found.</p>'; return; }
  el.innerHTML = list.map(d => `
    <div class="doc-card">
      <div class="doc-avatar">👨‍⚕️</div>
      <div class="doc-name">Dr. ${d.user?.fullName || '–'}</div>
      <div class="doc-spec">${d.specialization}</div>
      <div class="doc-meta">${d.qualification || ''} · ${d.experienceYears || 0} yrs exp</div>
      <div class="doc-meta">🏥 ${d.department || '–'} · Cabin ${d.cabinNumber || '–'}</div>
      <div class="doc-meta">⏰ ${d.availableFrom || ''} – ${d.availableTo || ''}</div>
      <div class="doc-meta">💰 ₹${d.consultationFee || '–'}</div>
      <button class="btn-primary btn-sm" style="margin-top:.5rem"
        onclick="openModal('bookApptModal'); document.getElementById('apptDoctor').value=${d.id}">
        Book Appointment
      </button>
    </div>`).join('');
}

function filterDoctors() {
  const q = document.getElementById('docSearch').value.toLowerCase();
  renderDoctors(allDoctors.filter(d =>
    d.specialization?.toLowerCase().includes(q) ||
    d.user?.fullName?.toLowerCase().includes(q) ||
    d.department?.toLowerCase().includes(q)
  ));
}

// ─── Beds ─────────────────────────────────────────────────────────────────────
async function loadBeds(type = 'ALL') {
  const el = document.getElementById('bedList');
  document.querySelectorAll('.btn-filter').forEach(b => b.classList.remove('active'));
  event?.target?.classList.add('active');
  try {
    const path = type === 'ALL' ? '/beds/available' : `/beds/available/${type}`;
    const res  = await api.get(path);
    const beds = res.data;
    if (!beds.length) { el.innerHTML = '<p class="text-muted">No beds available for this type.</p>'; return; }
    el.innerHTML = beds.map(b => `
      <div class="bed-card ${b.status.toLowerCase()}">
        <div class="bed-number">${b.bedNumber}</div>
        <div class="bed-ward">${b.wardName}</div>
        <div class="bed-ward">Floor ${b.floorNumber || '–'}</div>
        <div class="bed-status">${badge(b.status)}</div>
        <div class="bed-ward" style="margin-top:.3rem">₹${b.dailyRate || '–'}/day</div>
      </div>`).join('');
  } catch (e) { el.innerHTML = '<p class="error-msg">Failed to load beds.</p>'; }
}

// ─── Lab Tests ────────────────────────────────────────────────────────────────
async function loadLabTests() {
  const el = document.getElementById('labList');
  try {
    const res = await api.get('/lab-tests/patient/' + patientId);
    const list = res.data;
    if (!list.length) { el.innerHTML = '<p class="text-muted">No lab tests booked yet.</p>'; return; }
    el.innerHTML = list.map(t => `
      <div class="card-item">
        <div class="card-info">
          <div class="card-title">${t.testName} · <span class="text-muted">${t.testCategory}</span></div>
          <div class="card-sub">Scheduled: ${fmtDateTime(t.scheduledAt)} · ₹${t.price}</div>
          ${t.result ? `<div class="card-sub" style="color:var(--success)">Result: ${t.result}</div>` : ''}
        </div>
        ${badge(t.status)}
      </div>`).join('');
  } catch (e) { el.innerHTML = '<p class="error-msg">Failed to load lab tests.</p>'; }
}

async function bookLabTest() {
  const msg = document.getElementById('labMsg');
  msg.textContent = '';
  try {
    await api.post('/lab-tests', {
      patientId:   patientId,
      testName:    document.getElementById('labName').value,
      testCategory: document.getElementById('labCategory').value,
      scheduledAt: document.getElementById('labDateTime').value,
      orderedBy:   'Self',
    });
    msg.textContent = '✅ Lab test booked!';
    msg.style.color = 'var(--success)';
    setTimeout(() => { closeModal('bookLabModal'); loadLabTests(); }, 1200);
  } catch (e) { msg.textContent = '❌ ' + e.message; msg.style.color = 'var(--danger)'; }
}

// ─── ICU Request ──────────────────────────────────────────────────────────────
async function submitIcuRequest() {
  const msg = document.getElementById('icuMsg');
  msg.textContent = '';
  try {
    await api.post('/icu/request', {
      patientId:        patientId,
      reason:           document.getElementById('icuReason').value,
      medicalCondition: document.getElementById('icuCondition').value,
      priority:         document.getElementById('icuPriority').value,
    });
    msg.textContent = '✅ ICU request submitted. Admin will review shortly.';
    msg.style.color = 'var(--success)';
    loadIcuRequests();
  } catch (e) { msg.textContent = '❌ ' + e.message; msg.style.color = 'var(--danger)'; }
}

async function loadIcuRequests() {
  const el = document.getElementById('icuList');
  try {
    const res = await api.get('/icu/patient/' + patientId);
    const list = res.data;
    if (!list.length) { el.innerHTML = ''; return; }
    el.innerHTML = list.map(r => `
      <div class="card-item">
        <div class="card-info">
          <div class="card-title">${r.medicalCondition || 'ICU Request'}</div>
          <div class="card-sub">${r.reason}</div>
          <div class="card-sub">${fmtDateTime(r.requestedAt)}</div>
          ${r.adminNotes ? `<div class="card-sub" style="color:var(--primary)">Note: ${r.adminNotes}</div>` : ''}
        </div>
        <div>${badge(r.priority)} ${badge(r.status)}</div>
      </div>`).join('');
  } catch (e) {}
}

// ─── Ambulance ────────────────────────────────────────────────────────────────
async function requestAmbulance() {
  const msg = document.getElementById('ambMsg');
  msg.textContent = '';
  try {
    await api.post('/ambulance/public/request', {
      requesterName:  document.getElementById('ambName').value,
      requesterPhone: document.getElementById('ambPhone').value,
      pickupAddress:  document.getElementById('ambAddress').value,
      emergencyType:  document.getElementById('ambType').value,
      notes:          document.getElementById('ambNotes').value,
    });
    msg.textContent = '✅ Ambulance requested! Our team will contact you shortly.';
    msg.style.color = 'var(--success)';
  } catch (e) { msg.textContent = '❌ ' + e.message; msg.style.color = 'var(--danger)'; }
}

// ─── Chat / Bot ───────────────────────────────────────────────────────────────
function switchChatTab(tab) {
  document.querySelectorAll('.chat-tab').forEach(t => t.classList.remove('active'));
  event.target.classList.add('active');
  document.getElementById('botChat').classList.toggle('hidden', tab !== 'bot');
  document.getElementById('doctorChat').classList.toggle('hidden', tab !== 'doctor');
}

async function sendBotMsg() {
  const input = document.getElementById('botInput');
  const text  = input.value.trim();
  if (!text) return;
  appendMsg('botMessages', text, 'user');
  input.value = '';
  try {
    const res = await api.post('/chat/bot', { message: text });
    appendMsg('botMessages', res.reply, 'bot');
  } catch (e) {
    appendMsg('botMessages', 'Sorry, I am unavailable right now.', 'bot');
  }
}

function quickAsk(text) {
  document.getElementById('botInput').value = text;
  sendBotMsg();
}

function appendMsg(containerId, text, sender) {
  const box = document.getElementById(containerId);
  const div = document.createElement('div');
  div.className = `msg ${sender === 'user' ? 'user-msg' : 'bot-msg'}`;
  div.innerHTML = `<span>${sender === 'user' ? '👤' : '🤖'}</span><p>${text}</p>`;
  box.appendChild(div);
  box.scrollTop = box.scrollHeight;
}

// ─── WebSocket (doctor chat) ──────────────────────────────────────────────────
let stompClient = null;
let currentDoctorSessionId = null;

function connectWs() {
  if (typeof SockJS === 'undefined' || stompClient) return;
  const socket = new SockJS('http://localhost:8080/api/ws');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, () => {
    if (currentDoctorSessionId) {
      stompClient.subscribe('/topic/chat/' + currentDoctorSessionId, (msg) => {
        const m = JSON.parse(msg.body);
        if (m.senderId !== parseInt(userId)) {
          appendMsg('doctorMessages', m.message, 'bot');
        }
      });
    }
  });
}

function sendDoctorMsg() {
  const input = document.getElementById('doctorInput');
  const text  = input.value.trim();
  if (!text) return;
  appendMsg('doctorMessages', text, 'user');
  input.value = '';
  if (stompClient && currentDoctorSessionId) {
    stompClient.send('/app/chat.send', {}, JSON.stringify({
      sessionId: currentDoctorSessionId,
      senderId: parseInt(userId),
      senderName: fullName,
      senderType: 'PATIENT',
      message: text,
    }));
  }
}

// ─── Profile ──────────────────────────────────────────────────────────────────
function renderProfile(p) {
  const el = document.getElementById('profileCard');
  el.innerHTML = `
    <div style="text-align:center;margin-bottom:1.5rem">
      <div style="font-size:4rem">👤</div>
      <h3 style="font-size:1.2rem;margin-top:.5rem">${p.user?.fullName || '–'}</h3>
      <p class="text-muted">${p.registrationId}</p>
      ${p.qrCodePath
        ? `<img src="http://localhost:5001/qr-image/${p.registrationId}"
              alt="QR" style="width:120px;margin-top:.75rem;border:2px solid var(--border);border-radius:8px"/>`
        : ''}
    </div>
    <table style="width:100%;border-collapse:collapse">
      ${row('Email', p.user?.email)} ${row('Phone', p.user?.phoneNumber)}
      ${row('Gender', p.gender)} ${row('Blood Group', p.bloodGroup)}
      ${row('Date of Birth', fmtDate(p.dateOfBirth))} ${row('Address', p.address)}
      ${row('Emergency Contact', p.emergencyContact)} ${row('Allergies', p.allergies)}
    </table>
    <div style="margin-top:1.25rem">
      <button class="btn-outline" onclick="editProfile()">✏️ Edit Profile</button>
    </div>`;
}

function row(label, value) {
  return `<tr>
    <td style="padding:.5rem;font-weight:600;width:40%;color:var(--muted)">${label}</td>
    <td style="padding:.5rem">${value || '–'}</td>
  </tr>`;
}

function editProfile() {
  // Simple inline edit — can be extended
  alert('Profile editing coming soon! Use the API: PUT /api/patients/' + patientId);
}

// ─── Section change hook to lazy-load data ────────────────────────────────────
const _origShowSection = showSection;
window.showSection = function(name) {
  _origShowSection(name);
  if (name === 'beds')        loadBeds('ALL');
  if (name === 'labTests')    loadLabTests();
  if (name === 'appointments') loadAppointments();
  if (name === 'icu')         loadIcuRequests();
};

// ─── Boot ─────────────────────────────────────────────────────────────────────
init();
