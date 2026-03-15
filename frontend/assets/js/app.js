import { clearAuth, getToken } from "./api.js";

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
  const d = new Date(iso);
  return d.toLocaleString("vi-VN");
}

export function requireAuth() {
  if (!getToken()) {
    location.href = "login.html";
  }
}

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

