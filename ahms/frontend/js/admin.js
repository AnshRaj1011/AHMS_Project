// ─── AHMS Admin Panel ────────────────────────────────────────────────────────
requireAdmin();

document.getElementById('userName').textContent = localStorage.getItem('fullName');

// ─── Dashboard stats ─────────────────────────────────────────────────────────
async function loadDashboard() {
  try {
    const res = await api.get('/admin/dashboard');
    const s   = res.data;
    document.getElementById('sPatients').textContent = s.totalPatients;
    document.getElementById('sDoctors').textContent  = s.totalDoctors;
    document.getElementById('sAppts').textContent    = s.todayAppointments;
    document.getElementById('sBeds').textContent     = s.availableBeds;
    document.getElementById('sIcu').textContent      = s.availableIcuBeds;
    document.getElementById('sAmb').textContent      = s.pendingAmbulance;
    document.getElementById('sLabs').textContent     = s.pendingLabTests;
  } catch (e) { console.error('Dashboard stats failed:', e); }
}

// ─── Patients ─────────────────────────────────────────────────────────────────
async function loadPatients() {
  const el = document.getElementById('patientTable');
  try {
    const res = await api.get('/admin/patients');
    el.innerHTML = table(
      ['Reg ID', 'Name', 'Email', 'Phone', 'Blood Group', 'Gender', 'Registered'],
      res.data.map(p => [
        `<b>${p.registrationId}</b>`,
        p.user?.fullName || '–',
        p.user?.email || '–',
        p.user?.phoneNumber || '–',
        p.bloodGroup || '–',
        p.gender || '–',
        fmtDate(p.registeredAt),
      ])
    );
  } catch (e) { el.innerHTML = err(e); }
}

// ─── Doctors ──────────────────────────────────────────────────────────────────
async function loadDoctors() {
  const el = document.getElementById('doctorTable');
  try {
    const res = await api.get('/admin/doctors');
    el.innerHTML = table(
      ['Name', 'Specialization', 'Dept', 'Cabin', 'Fee', 'Available', 'Actions'],
      res.data.map(d => [
        `Dr. ${d.user?.fullName || '–'}`,
        d.specialization, d.department || '–', d.cabinNumber || '–',
        `₹${d.consultationFee || '–'}`,
        badge(d.isAvailable ? 'AVAILABLE' : 'OCCUPIED'),
        `<button class="btn-outline btn-sm" onclick="toggleDoc(${d.id})">Toggle</button>`,
      ])
    );
  } catch (e) { el.innerHTML = err(e); }
}

async function toggleDoc(id) {
  await api.patch('/doctors/' + id + '/toggle', {});
  loadDoctors();
}

async function addDoctor() {
  const msg = document.getElementById('docMsg');
  msg.textContent = '';
  try {
    await api.post('/doctors', {
      userId:         parseInt(document.getElementById('docUserId').value),
      specialization: document.getElementById('docSpec').value,
      qualification:  document.getElementById('docQual').value,
      licenseNumber:  document.getElementById('docLicense').value,
      department:     document.getElementById('docDept').value,
      cabinNumber:    document.getElementById('docCabin').value,
      consultationFee: parseFloat(document.getElementById('docFee').value),
      availableDays:  document.getElementById('docDays').value,
      availableFrom:  document.getElementById('docFrom').value,
      availableTo:    document.getElementById('docTo').value,
    });
    msg.textContent = '✅ Doctor added!'; msg.style.color = 'var(--success)';
    setTimeout(() => { closeModal('addDoctorModal'); loadDoctors(); }, 1200);
  } catch (e) { msg.textContent = '❌ ' + e.message; msg.style.color = 'var(--danger)'; }
}

// ─── Appointments ─────────────────────────────────────────────────────────────
async function loadAppointments() {
  const el = document.getElementById('apptTable');
  try {
    const res = await api.get('/appointments');
    el.innerHTML = table(
      ['Patient', 'Doctor', 'Date', 'Time', 'Type', 'Status', 'Actions'],
      res.data.map(a => [
        a.patient?.user?.fullName || '–',
        `Dr. ${a.doctor?.user?.fullName || '–'}`,
        fmtDate(a.appointmentDate),
        a.appointmentTime,
        a.type,
        badge(a.status),
        a.status === 'PENDING'
          ? `<button class="btn-primary btn-sm" onclick="confirmAppt(${a.id})">Confirm</button>
             <button class="btn-outline btn-sm" onclick="cancelAppt(${a.id})">Cancel</button>`
          : '–',
      ])
    );
  } catch (e) { el.innerHTML = err(e); }
}

async function confirmAppt(id) {
  await api.patch('/appointments/' + id + '/status', { status: 'CONFIRMED' });
  loadAppointments();
}
async function cancelAppt(id) {
  await api.patch('/appointments/' + id + '/status', { status: 'CANCELLED' });
  loadAppointments();
}

// ─── Beds ─────────────────────────────────────────────────────────────────────
async function loadBeds() {
  const el = document.getElementById('bedTable');
  try {
    const res = await api.get('/admin/beds');
    el.innerHTML = table(
      ['Bed No.', 'Ward', 'Type', 'Floor', 'Rate', 'Patient', 'Status', 'Actions'],
      res.data.map(b => [
        `<b>${b.bedNumber}</b>`, b.wardName, b.bedType, b.floorNumber || '–',
        `₹${b.dailyRate || '–'}`,
        b.currentPatient?.user?.fullName || '–',
        badge(b.status),
        b.status === 'OCCUPIED'
          ? `<button class="btn-outline btn-sm" onclick="discharge(${b.id})">Discharge</button>`
          : b.status === 'AVAILABLE'
          ? `<button class="btn-primary btn-sm" onclick="setMaintenance(${b.id})">Maintenance</button>`
          : `<button class="btn-outline btn-sm" onclick="setAvailable(${b.id})">Set Available</button>`,
      ])
    );
  } catch (e) { el.innerHTML = err(e); }
}

async function addBed() {
  const msg = document.getElementById('bedMsg');
  msg.textContent = '';
  try {
    await api.post('/beds', {
      bedNumber:   document.getElementById('bedNum').value,
      wardName:    document.getElementById('bedWard').value,
      bedType:     document.getElementById('bedType').value,
      floorNumber: parseInt(document.getElementById('bedFloor').value),
      dailyRate:   parseFloat(document.getElementById('bedRate').value),
    });
    msg.textContent = '✅ Bed added!'; msg.style.color = 'var(--success)';
    setTimeout(() => { closeModal('addBedModal'); loadBeds(); }, 1200);
  } catch (e) { msg.textContent = '❌ ' + e.message; msg.style.color = 'var(--danger)'; }
}

async function discharge(id)       { await api.patch('/beds/' + id + '/discharge', {}); loadBeds(); }
async function setMaintenance(id)  { await api.patch('/beds/' + id + '/status', { status: 'MAINTENANCE' }); loadBeds(); }
async function setAvailable(id)    { await api.patch('/beds/' + id + '/status', { status: 'AVAILABLE' }); loadBeds(); }

// ─── ICU Requests ─────────────────────────────────────────────────────────────
async function loadIcu() {
  const el = document.getElementById('icuTable');
  try {
    const res = await api.get('/icu');
    el.innerHTML = table(
      ['Patient', 'Condition', 'Priority', 'Reason', 'Requested', 'Status', 'Actions'],
      res.data.map(r => [
        r.patient?.user?.fullName || '–',
        r.medicalCondition || '–',
        badge(r.priority),
        r.reason?.substring(0, 40) + (r.reason?.length > 40 ? '…' : ''),
        fmtDateTime(r.requestedAt),
        badge(r.status),
        r.status === 'PENDING'
          ? `<button class="btn-primary btn-sm" onclick="approveIcu(${r.id})">Approve</button>
             <button class="btn-outline btn-sm" onclick="rejectIcu(${r.id})">Reject</button>`
          : '–',
      ])
    );
  } catch (e) { el.innerHTML = err(e); }
}

async function approveIcu(id) {
  const bedId = prompt('Enter ICU Bed ID to assign:');
  if (!bedId) return;
  const notes = prompt('Admin notes (optional):') || '';
  try {
    await api.patch('/icu/' + id + '/approve', { bedId: parseInt(bedId), adminNotes: notes });
    loadIcu();
    loadDashboard();
  } catch (e) { alert('Error: ' + e.message); }
}

async function rejectIcu(id) {
  const notes = prompt('Reason for rejection:');
  if (notes === null) return;
  await api.patch('/icu/' + id + '/reject', { adminNotes: notes });
  loadIcu();
}

// ─── Ambulance ────────────────────────────────────────────────────────────────
async function loadAmbulance() {
  const el = document.getElementById('ambulanceTable');
  try {
    const res = await api.get('/admin/ambulances');
    el.innerHTML = table(
      ['Requester', 'Phone', 'Address', 'Emergency', 'Requested', 'Status', 'Actions'],
      res.data.map(r => [
        r.requesterName, r.requesterPhone,
        r.pickupAddress?.substring(0, 40) + '…',
        r.emergencyType || '–',
        fmtDateTime(r.requestedAt),
        badge(r.status),
        r.status === 'PENDING'
          ? `<button class="btn-danger btn-sm" onclick="dispatch(${r.id})">Dispatch</button>`
          : r.status === 'DISPATCHED'
          ? `<button class="btn-outline btn-sm" onclick="completeAmb(${r.id})">Complete</button>`
          : '–',
      ])
    );
  } catch (e) { el.innerHTML = err(e); }
}

async function dispatch(id) {
  const driver = prompt('Driver name:');
  const num    = prompt('Ambulance number (e.g. UK07-1234):');
  const eta    = prompt('ETA in minutes:');
  if (!driver || !num) return;
  try {
    await api.patch('/ambulance/dispatch/' + id, {
      driverName: driver, ambulanceNumber: num, eta: parseInt(eta)
    });
    loadAmbulance(); loadDashboard();
  } catch (e) { alert('Error: ' + e.message); }
}

async function completeAmb(id) {
  await api.patch('/ambulance/' + id + '/status', { status: 'COMPLETED' });
  loadAmbulance();
}

// ─── Lab Tests ────────────────────────────────────────────────────────────────
async function loadLabTests() {
  const el = document.getElementById('labTable');
  try {
    const res = await api.get('/admin/lab-tests');
    el.innerHTML = table(
      ['Patient', 'Test', 'Category', 'Scheduled', 'Price', 'Status', 'Actions'],
      res.data.map(t => [
        t.patient?.user?.fullName || '–',
        t.testName, t.testCategory,
        fmtDateTime(t.scheduledAt),
        `₹${t.price}`,
        badge(t.status),
        t.status === 'SCHEDULED'
          ? `<button class="btn-primary btn-sm" onclick="updateResult(${t.id})">Add Result</button>`
          : t.result ? `<span class="text-muted">${t.result.substring(0,30)}</span>` : '–',
      ])
    );
  } catch (e) { el.innerHTML = err(e); }
}

async function updateResult(id) {
  const result = prompt('Enter test result:');
  if (!result) return;
  await api.patch('/lab-tests/' + id + '/result', { result });
  loadLabTests();
}

// ─── Table builder ────────────────────────────────────────────────────────────
function table(headers, rows) {
  if (!rows.length) return '<p class="text-muted" style="padding:1rem">No records found.</p>';
  return `<table>
    <thead><tr>${headers.map(h => `<th>${h}</th>`).join('')}</tr></thead>
    <tbody>${rows.map(r => `<tr>${r.map(c => `<td>${c ?? '–'}</td>`).join('')}</tr>`).join('')}</tbody>
  </table>`;
}

function err(e) { return `<p class="error-msg" style="padding:1rem">Failed to load: ${e.message}</p>`; }

// ─── Section hook ─────────────────────────────────────────────────────────────
const _orig = showSection;
window.showSection = function(name) {
  _orig(name);
  const loaders = {
    home: loadDashboard, patients: loadPatients, doctors: loadDoctors,
    appointments: loadAppointments, beds: loadBeds, icu: loadIcu,
    ambulance: loadAmbulance, labTests: loadLabTests,
  };
  if (loaders[name]) loaders[name]();
};

// ─── Boot ─────────────────────────────────────────────────────────────────────
loadDashboard();
