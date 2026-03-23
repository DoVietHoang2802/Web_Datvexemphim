import { clearAuth, getToken } from "./api.js";

// ==================== TOAST NOTIFICATIONS ====================
let toastContainer = null;

function getToastContainer() {
  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.id = 'toast-container';
    toastContainer.style.cssText = `
      position: fixed;
      top: 80px;
      right: 20px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 10px;
      max-width: 380px;
      width: 100%;
    `;
    document.body.appendChild(toastContainer);
  }
  return toastContainer;
}

export function showToast(message, type = 'success', duration = 4000) {
  const container = getToastContainer();
  const toast = document.createElement('div');
  const icons = {
    success: 'fa-check-circle',
    error: 'fa-exclamation-circle',
    warning: 'fa-exclamation-triangle',
    info: 'fa-info-circle'
  };
  const colors = {
    success: { bg: 'rgba(16, 185, 129, 0.95)', border: '#10b981', icon: '#fff', text: '#fff' },
    error: { bg: 'rgba(239, 68, 68, 0.95)', border: '#ef4444', icon: '#fff', text: '#fff' },
    warning: { bg: 'rgba(245, 158, 11, 0.95)', border: '#f59e0b', icon: '#fff', text: '#fff' },
    info: { bg: 'rgba(59, 130, 246, 0.95)', border: '#3b82f6', icon: '#fff', text: '#fff' }
  };
  const color = colors[type] || colors.success;

  toast.style.cssText = `
    background: ${color.bg};
    backdrop-filter: blur(10px);
    border: 1px solid ${color.border};
    border-radius: 12px;
    padding: 14px 18px;
    display: flex;
    align-items: center;
    gap: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
    animation: slideInRight 0.3s ease;
    color: ${color.text};
    font-size: 0.9rem;
    line-height: 1.4;
  `;

  toast.innerHTML = `
    <i class="fas ${icons[type] || icons.success}" style="font-size: 1.2rem; color: ${color.icon}; flex-shrink: 0;"></i>
    <span style="flex: 1;">${message}</span>
    <button onclick="this.parentElement.remove()" style="
      background: none;
      border: none;
      color: rgba(255,255,255,0.7);
      cursor: pointer;
      padding: 0;
      font-size: 1rem;
      line-height: 1;
    ">×</button>
  `;

  container.appendChild(toast);

  // Auto remove
  setTimeout(() => {
    toast.style.animation = 'fadeOutLeft 0.3s ease forwards';
    setTimeout(() => toast.remove(), 300);
  }, duration);

  return toast;
}

// Add animation keyframes
const styleSheet = document.createElement('style');
styleSheet.textContent = `
  @keyframes slideInRight {
    from { transform: translateX(100%); opacity: 0; }
    to { transform: translateX(0); opacity: 1; }
  }
  @keyframes fadeOutLeft {
    from { transform: translateX(0); opacity: 1; }
    to { transform: translateX(-100%); opacity: 0; }
  }
`;
document.head.appendChild(styleSheet);

// ==================== UTILITY FUNCTIONS ====================
export function qs(name) {
  return new URLSearchParams(location.search).get(name);
}

export function formatVnd(v) {
  try {
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(v);
  } catch {
    return `${v} VND`;
  }
}

export function formatTime(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleString("vi-VN");
}

export function formatDate(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleDateString("vi-VN", { day: '2-digit', month: '2-digit', year: 'numeric' });
}

export function formatDateTime(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleString("vi-VN", {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  });
}

export function requireAuth() {
  if (!getToken()) {
    location.href = "login.html";
  }
}

// ==================== NAVBAR SETUP ====================
export function setupNavbar() {
  const el = document.querySelector("#authArea");
  if (!el) return;
  const token = getToken();
  const fullName = localStorage.getItem("userFullName") || "User";
  const role = localStorage.getItem("userRole") || "USER";

  if (!token) {
    el.innerHTML = `
      <a class="btn btn-outline-light btn-sm me-2" href="login.html">Đăng nhập</a>
      <a class="btn btn-brand btn-sm" href="register.html">Đăng ký</a>
    `;
    return;
  }

  const adminLink = role === "ADMIN" ? `<a class="btn btn-outline-light btn-sm me-2" href="admin/dashboard.html">Admin</a>` : "";
  el.innerHTML = `
    ${adminLink}
    <a class="btn btn-outline-light btn-sm me-2" href="profile.html"><i class="fas fa-user-circle me-1"></i>Hồ sơ</a>
    <a class="btn btn-outline-light btn-sm me-2" href="tickets.html">Vé của tôi</a>
    <span class="text-muted small me-2">Xin chào, <b>${fullName}</b></span>
    <button class="btn btn-sm btn-outline-danger" id="btnLogout">Đăng xuất</button>
  `;
  document.querySelector("#btnLogout")?.addEventListener("click", () => {
    clearAuth();
    location.href = "index.html";
  });
}

// ==================== BACK BUTTON HELPER ====================
export function addBackButton(returnUrl = 'index.html') {
  const backBtn = document.createElement('a');
  backBtn.href = returnUrl;
  backBtn.className = 'btn btn-outline-light btn-sm';
  backBtn.innerHTML = '<i class="fas fa-arrow-left me-1"></i> Quay về';
  return backBtn;
}

// ==================== LOADING SPINNER ====================
export function showLoading(container) {
  const spinner = document.createElement('div');
  spinner.className = 'loading-spinner';
  spinner.style.cssText = `
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 40px;
  `;
  spinner.innerHTML = `
    <div style="
      width: 40px;
      height: 40px;
      border: 3px solid rgba(255,255,255,0.1);
      border-top-color: var(--brand);
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    "></div>
  `;
  container.innerHTML = '';
  container.appendChild(spinner);

  // Add spin animation if not exists
  if (!document.querySelector('#spin-style')) {
    const style = document.createElement('style');
    style.id = 'spin-style';
    style.textContent = '@keyframes spin { to { transform: rotate(360deg); } }';
    document.head.appendChild(style);
  }
  return spinner;
}

