// Use CONFIG from config.js (load in HTML before api.js)
// Fallback to localhost if CONFIG not available
const API_BASE = (typeof CONFIG !== 'undefined' && CONFIG.API_BASE)
  ? CONFIG.API_BASE
  : "http://localhost:9090/api";

// ============ TOKEN HELPERS ============
// Cookie fallback for browsers that block localStorage (Tracking Prevention)
function setCookie(name, val, days) {
  try {
    var exp = new Date();
    exp.setTime(exp.getTime() + days * 86400000);
    document.cookie = name + '=' + encodeURIComponent(val) + ';expires=' + exp.toUTCString() + ';path=/;SameSite=Lax';
  } catch(e) {}
}
function getCookie(name) {
  try {
    var parts = document.cookie.split(';');
    for (var i = 0; i < parts.length; i++) {
      var kv = parts[i].split('=');
      if (kv[0].trim() === name) return decodeURIComponent(kv[1] || '');
    }
  } catch(e) {}
  return null;
}
function deleteCookie(name) {
  try { document.cookie = name + '=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/'; } catch(e) {}
}

// Get token from localStorage OR cookie fallback
function getStoredToken() {
  var t = localStorage.getItem('token');
  if (t) return t;
  return getCookie('token');
}
function setStoredToken(data) {
  // localStorage
  try { localStorage.setItem('token', data.token || data.accessToken); } catch(e) {}
  try { localStorage.setItem('userId', data.userId || ''); } catch(e) {}
  try { localStorage.setItem('userEmail', data.email || ''); } catch(e) {}
  try { localStorage.setItem('userRole', data.role || 'USER'); } catch(e) {}
  try { localStorage.setItem('userFullName', data.fullName || ''); } catch(e) {}
  // cookie fallback
  setCookie('token', data.token || data.accessToken || '', 7);
  setCookie('userId', data.userId || '', 7);
  setCookie('userEmail', data.email || '', 7);
  setCookie('userRole', data.role || 'USER', 7);
  setCookie('userFullName', data.fullName || '', 7);
}
function clearStoredToken() {
  ['token','userId','userEmail','userRole','userFullName'].forEach(function(k) {
    try { localStorage.removeItem(k); } catch(e) {}
    deleteCookie(k);
  });
}

// ============ EXPORTS (ES Module) ============
export function getToken() { return getStoredToken(); }
export function setAuth(data) { setStoredToken(data); }
export function clearAuth() { clearStoredToken(); }
export function logout() { clearAuth(); location.href = 'login.html'; }
export function authHeaders() {
  var token = getToken();
  return token ? { Authorization: 'Bearer ' + token } : {};
}

// ============ API HELPERS ============
async function parseOrThrow(res) {
  var text = await res.text();
  var data = text ? JSON.parse(text) : null;
  if (!res.ok) {
    var msg = data && data.message ? data.message : (data && data.error ? data.error : 'HTTP ' + res.status);
    throw new Error(msg);
  }
  return data;
}

export async function apiGet(path) {
  var res = await fetch(API_BASE + path, {
    headers: {
      'Content-Type': 'application/json',
      'ngrok-skip-browser-warning': 'true',
      ...authHeaders()
    }
  });
  return parseOrThrow(res);
}

export async function apiPost(path, body) {
  var res = await fetch(API_BASE + path, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'ngrok-skip-browser-warning': 'true',
      ...authHeaders()
    },
    body: JSON.stringify(body)
  });
  return parseOrThrow(res);
}

export async function apiPut(path, body) {
  var res = await fetch(API_BASE + path, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'ngrok-skip-browser-warning': 'true',
      ...authHeaders()
    },
    body: JSON.stringify(body)
  });
  return parseOrThrow(res);
}

export async function apiDelete(path) {
  var res = await fetch(API_BASE + path, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      'ngrok-skip-browser-warning': 'true',
      ...authHeaders()
    }
  });
  return parseOrThrow(res);
}

export async function apiUpload(path, file) {
  var formData = new FormData();
  formData.append('file', file);
  var res = await fetch(API_BASE + path, {
    method: 'POST',
    headers: {
      'ngrok-skip-browser-warning': 'true',
      ...authHeaders()
    },
    body: formData
  });
  var data = await parseOrThrow(res);
  return data.url;
}

// ===== FOOD API =====
export async function getFoodCategories() { return apiGet('/food/categories'); }
export async function getFoodItems(categoryId) {
  if (categoryId) return apiGet('/food/items/category/' + categoryId);
  return apiGet('/food/items');
}
export async function createFoodOrder(foodItems, ticketId, paymentId) {
  return apiPost('/food/orders', {
    ticketId: ticketId,
    paymentId: paymentId,
    items: foodItems.map(function(item) {
      return {
        foodItemId: item.foodItemId || item.id,
        quantity: item.quantity || item.qty
      };
    })
  });
}
