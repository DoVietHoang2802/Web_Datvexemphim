// Use CONFIG already loaded by config.js
const API_BASE = typeof CONFIG !== 'undefined' && CONFIG.API_BASE
  ? CONFIG.API_BASE
  : 'https://webdatvexemphim-production.up.railway.app/api';

let allTickets = [];
let currentFilter = 'all';

function showToast(msg, type = 'error') {
  const el = document.createElement('div');
  el.style.cssText = 'position:fixed;top:20px;right:20px;z-index:99999;padding:12px 20px;border-radius:8px;color:#fff;font-weight:600;animation:slideIn 0.2s';
  el.style.background = type === 'success' ? '#22c55e' : '#ef4444';
  el.textContent = msg;
  document.body.appendChild(el);
  setTimeout(() => { el.style.opacity = 0; setTimeout(() => el.remove(), 200); }, 2500);
}

async function loadNavbar(){
  const html = await fetch('./partials/navbar.html').then(r=>r.text());
  document.querySelector('#navbar').innerHTML = html;
  if (typeof setupNavbar === 'function') setupNavbar();
}

function hasFood(t) {
  return t.foodOrderId && t.foodItems && t.foodItems.length > 0;
}

function formatFoodVnd(amount) {
  if (!amount) return '0 ₫';
  return new Intl.NumberFormat('vi-VN').format(amount) + ' ₫';
}

function getStatusClass(status) {
  switch(status) {
    case 'CONFIRMED': return 'status-confirmed';
    case 'PENDING': return 'status-pending';
    case 'CANCELLED': return 'status-cancelled';
    case 'AVAILABLE': return 'status-available';
    case 'SOLD': return 'status-sold';
    default: return '';
  }
}

function getStatusBadge(status) {
  switch(status) {
    case 'CONFIRMED': return '<span class="status-badge confirmed"><i class="fas fa-check me-1"></i>Đã đặt</span>';
    case 'PENDING': return '<span class="status-badge pending"><i class="fas fa-clock me-1"></i>Chờ TT</span>';
    case 'CANCELLED': return '<span class="status-badge cancelled"><i class="fas fa-ban me-1"></i>Đã hủy</span>';
    case 'AVAILABLE': return '<span class="status-badge available"><i class="fas fa-store me-1"></i>Đang bán</span>';
    case 'SOLD': return '<span class="status-badge sold"><i class="fas fa-handshake me-1"></i>Đã bán</span>';
    default: return '';
  }
}

function formatTime(iso) {
  if (!iso) return '--:--';
  return new Date(iso).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
}

function formatDate(iso) {
  if (!iso) return '--/--/----';
  return new Date(iso).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

function formatVnd(v) {
  if (v == null) return '0 ₫';
  return new Intl.NumberFormat('vi-VN').format(v) + ' ₫';
}

function ticketCard(t) {
  const statusClass = getStatusClass(t.status);
  const statusBadge = getStatusBadge(t.status);
  const canCancel = t.status === 'CONFIRMED';
  const canList = t.status === 'CONFIRMED';
  const canUnlist = t.status === 'AVAILABLE';
  const canDelete = t.status === 'CANCELLED' || t.status === 'SOLD';
  const canPay = t.status === 'PENDING';
  const totalPrice = (t.price || 0) + (t.foodTotalPrice || 0);

  const foodHtml = hasFood(t) ? '<div class="food-order-section"><div class="food-order-title"><i class="fas fa-utensils"></i> Đồ ăn & nước uống</div>' +
    t.foodItems.map(item => '<div class="food-item"><span>' + item.name + ' <span class="text-muted">x' + item.quantity + '</span></span><span>' + formatFoodVnd(item.subtotal) + '</span></div>').join('') +
    '<div class="d-flex justify-content-between mt-2 pt-2 border-top"><span class="fw-bold">Tổng đồ ăn:</span><span class="fw-bold text-warning">' + formatFoodVnd(t.foodTotalPrice) + '</span></div></div>' : '';

  const qrBtn = (t.status === 'CONFIRMED' && t.ticketCode)
    ? '<button class="btn btn-sm qr-btn" onclick="showQrModal(' + escapeJson(t) + ')"><i class="fas fa-qrcode me-1"></i> Hiện QR</button>' : '';

  const payBtn = canPay
    ? '<a class="btn btn-sm btn-success" href="checkout.html?ticketIds=' + t.ticketId + '"><i class="fas fa-credit-card me-1"></i> Thanh toán</a><button class="btn btn-sm btn-outline-danger" data-action="cancel" data-id="' + t.ticketId + '"><i class="fas fa-times me-1"></i> Hủy vé</button>' : '';

  const listBtn = canList
    ? '<button class="btn btn-sm btn-outline-warning" data-action="list" data-id="' + t.ticketId + '"><i class="fas fa-tags me-1"></i> Đăng bán vé</button>' : '';

  const unlistBtn = canUnlist
    ? '<button class="btn btn-sm btn-outline-info" data-action="unlist" data-id="' + t.ticketId + '"><i class="fas fa-undo me-1"></i> Gỡ khỏi chợ</button>' : '';

  const cancelBtn = (canCancel && !canPay)
    ? '<button class="btn btn-sm btn-outline-danger" data-action="cancel" data-id="' + t.ticketId + '"><i class="fas fa-times me-1"></i> Hủy vé</button>' : '';

  const deleteBtn = canDelete
    ? '<button class="btn btn-sm btn-danger" data-action="delete" data-id="' + t.ticketId + '"><i class="fas fa-trash me-1"></i> Xóa vé</button>' : '';

  return '<div class="ticket-card ' + statusClass + '" data-ticket-id="' + t.ticketId + '">' +
    '<div class="ticket-header" onclick="toggleDetails(this)">' +
      '<div style="flex:1;">' +
        '<div class="ticket-movie">' + (t.movieTitle || '') + '</div>' +
        '<div class="ticket-meta">' +
          '<div class="ticket-meta-item"><i class="fas fa-clock"></i> ' + formatDate(t.startTime) + ' · ' + formatTime(t.startTime) + '</div>' +
          '<div class="ticket-meta-item"><i class="fas fa-door-open"></i> ' + (t.roomName || '') + '</div>' +
          '<div class="ticket-meta-item"><i class="fas fa-couch"></i> <span class="ticket-seat-badge">' + (t.seatCode || '') + '</span></div>' +
        '</div>' +
      '</div>' +
      '<div class="ticket-price">' +
        '<div class="ticket-total">' + formatVnd(totalPrice) + '</div>' +
        statusBadge +
        '<div class="ticket-details-toggle"><i class="fas fa-chevron-down"></i></div>' +
      '</div>' +
    '</div>' +
    '<div class="ticket-details">' +
      '<div class="row">' +
        '<div class="col-6"><p class="mb-1 text-muted small">ID: ' + t.ticketId + '</p></div>' +
        '<div class="col-6 text-end"><p class="mb-1 text-muted small">Mã booking: ' + (t.bookingCode || '') + '</p></div>' +
      '</div>' +
      '<p class="mb-1 text-muted small">Giá vé: ' + formatVnd(t.price) + '</p>' +
      (t.ticketCode ? '<p class="mb-1 text-muted small">Mã vé: <strong>' + t.ticketCode + '</strong></p>' : '') +
      foodHtml +
      '<div class="ticket-actions mt-2">' + qrBtn + payBtn + listBtn + unlistBtn + cancelBtn + deleteBtn + '</div>' +
    '</div>' +
  '</div>';
}

function escapeJson(obj) {
  return JSON.stringify(obj).replace(/'/g, "&#39;");
}

window.showQrModal = function(t) {
  if (!t.ticketCode) {
    showToast('Vé chưa có mã QR. Vui lòng thanh toán trước.', 'error');
    return;
  }
  document.getElementById('qrTicketTitle').textContent = t.movieTitle || '';
  document.getElementById('qrTicketCode').textContent = t.ticketCode;
  document.getElementById('qrTicketSeat').textContent = 'Phòng ' + (t.roomName || '') + ' · Ghế ' + (t.seatCode || '');
  document.getElementById('qrTicketTime').textContent = formatDate(t.startTime) + ' · ' + formatTime(t.startTime);
  document.getElementById('qrModal').classList.add('show');

  // Generate QR using qrcodejs
  var container = document.getElementById('qrCanvas');
  container.innerHTML = '';
  if (typeof QRCode !== 'undefined') {
    new QRCode(container, {
      text: t.ticketCode,
      width: 220,
      height: 220,
      colorDark: '#000000',
      colorLight: '#ffffff',
      correctLevel: QRCode.CorrectLevel ? QRCode.CorrectLevel.H : 3
    });
  } else {
    container.innerHTML = '<div style="color:#ef4444;padding:20px;">Chưa tải được thư viện QR. Hãy refresh lại trang.</div>';
  }
};

window.closeQrModal = function() {
  document.getElementById('qrModal').classList.remove('show');
};

document.addEventListener('click', function(e) {
  if (e.target.classList.contains('qr-modal-overlay')) window.closeQrModal();
});

window.toggleDetails = function(header) {
  var details = header.nextElementSibling;
  var toggle = header.querySelector('.ticket-details-toggle');
  details.classList.toggle('open');
  toggle.classList.toggle('open');
};

function renderTickets(tickets) {
  var container = document.querySelector('#ticketsList');
  if (!tickets || tickets.length === 0) {
    var emptyMsg = 'Chưa có vé nào.';
    if (currentFilter === 'CONFIRMED') emptyMsg = 'Chưa có vé nào được đặt.';
    else if (currentFilter === 'PENDING') emptyMsg = 'Không có vé chờ thanh toán.';
    else if (currentFilter === 'CANCELLED') emptyMsg = 'Không có vé nào bị hủy.';
    else if (currentFilter === 'AVAILABLE') emptyMsg = 'Không có vé nào đang được bán.';
    else if (currentFilter === 'SOLD') emptyMsg = 'Không có vé nào đã bán.';
    container.innerHTML = '<div class="empty-state"><i class="fas fa-ticket-alt"></i><h5>' + emptyMsg + '</h5><p class="small">Đặt vé để xem phim tại rạp</p>' +
      '<a class="btn btn-brand mt-3" href="showtimes.html"><i class="fas fa-film me-2"></i>Đặt vé ngay</a></div>';
    return;
  }
  container.innerHTML = tickets.map(ticketCard).join('');
}

function filterTickets(filter) {
  currentFilter = filter;
  if (filter === 'all') {
    renderTickets(allTickets);
  } else {
    var filtered = allTickets.filter(function(t) { return t.status === filter; });
    renderTickets(filtered);
  }
}

async function apiGet(path) {
  var token = localStorage.getItem('token');
  var res = await fetch(API_BASE + path, {
    headers: token ? { 'Authorization': 'Bearer ' + token } : {}
  });
  if (!res.ok) throw new Error('Lỗi ' + res.status);
  return res.json();
}

async function apiDelete(path) {
  var token = localStorage.getItem('token');
  var res = await fetch(API_BASE + path, {
    method: 'DELETE',
    headers: token ? { 'Authorization': 'Bearer ' + token } : {}
  });
  if (!res.ok) throw new Error('Lỗi ' + res.status);
  return res.json();
}

async function apiPost(path, body) {
  var token = localStorage.getItem('token');
  var res = await fetch(API_BASE + path, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token ? 'Bearer ' + token : ''
    },
    body: JSON.stringify(body)
  });
  if (!res.ok) throw new Error('Lỗi ' + res.status);
  return res.json();
}

async function refresh() {
  try {
    allTickets = await apiGet('/tickets/me');
    window.allTickets = allTickets;
    renderTickets(allTickets);
  } catch(e) {
    document.querySelector('#ticketsList').innerHTML = '<div class="alert alert-danger">Không tải được danh sách vé: ' + e.message + '</div>';
  }
}

async function handleAction(action, id) {
  try {
    if (action === 'cancel') {
      if (!confirm('Hủy vé này?')) return;
      await apiDelete('/tickets/' + id + '/cancel');
      showToast('Đã hủy vé!', 'success');
    } else if (action === 'delete') {
      if (!confirm('Xóa vé này?')) return;
      await apiDelete('/tickets/' + id);
      showToast('Đã xóa vé!', 'success');
    } else if (action === 'list') {
      var price = prompt('Nhập giá bán (VNĐ):', '50000');
      if (!price) return;
      await apiPost('/tickets/' + id + '/list', { price: parseInt(price) });
      showToast('Vé đã được đăng bán!', 'success');
    } else if (action === 'unlist') {
      await apiDelete('/tickets/' + id + '/unlist');
      showToast('Đã gỡ vé khỏi chợ!', 'success');
    }
    await refresh();
  } catch(e) {
    showToast(e.message, 'error');
  }
}

async function main() {
  var token = localStorage.getItem('token');
  if (!token) {
    document.querySelector('#ticketsList').innerHTML = '<div class="empty-state"><i class="fas fa-ticket-alt"></i><h5>Vui lòng đăng nhập để xem vé</h5><a class="btn btn-brand mt-3" href="login.html">Đăng nhập</a></div>';
    return;
  }
  await loadNavbar();
  await refresh();

  document.querySelectorAll('.filter-tabs .btn').forEach(function(btn) {
    btn.addEventListener('click', function() {
      document.querySelectorAll('.filter-tabs .btn').forEach(function(b) { b.classList.remove('active'); });
      btn.classList.add('active');
      filterTickets(btn.dataset.filter);
    });
  });

  document.querySelector('#ticketsList').addEventListener('click', async function(e) {
    var btn = e.target.closest('button[data-action]');
    if (!btn) return;
    e.stopPropagation();
    var action = btn.dataset.action;
    var id = Number(btn.dataset.id);
    await handleAction(action, id);
  });
}

main().catch(function(e) { showToast(e.message, 'error'); });
