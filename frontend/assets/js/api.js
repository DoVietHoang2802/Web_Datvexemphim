// Use CONFIG from config.js (load in HTML before api.js)
// Fallback to localhost if CONFIG not available
const API_BASE = (typeof CONFIG !== 'undefined' && CONFIG.API_BASE) 
  ? CONFIG.API_BASE 
  : "http://localhost:9090/api";

export function getToken() {
  return localStorage.getItem("accessToken");
}

export function setAuth(auth) {
  localStorage.setItem("accessToken", auth.accessToken);
  localStorage.setItem("userEmail", auth.email);
  localStorage.setItem("userRole", auth.role);
  localStorage.setItem("userFullName", auth.fullName);
}

export function clearAuth() {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("userEmail");
  localStorage.removeItem("userRole");
  localStorage.removeItem("userFullName");
}

export function authHeaders() {
  const token = getToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function parseOrThrow(res) {
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (!res.ok) {
    const msg = data?.message || data?.error || `HTTP ${res.status}`;
    throw new Error(msg);
  }
  return data;
}

export async function apiGet(path) {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { 
      "Content-Type": "application/json", 
      "ngrok-skip-browser-warning": "true",
      ...authHeaders() 
    },
  });
  return parseOrThrow(res);
}

export async function apiPost(path, body) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: "POST",
    headers: { 
      "Content-Type": "application/json", 
      "ngrok-skip-browser-warning": "true",
      ...authHeaders() 
    },
    body: JSON.stringify(body),
  });
  return parseOrThrow(res);
}

export async function apiPut(path, body) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: "PUT",
    headers: { 
      "Content-Type": "application/json", 
      "ngrok-skip-browser-warning": "true",
      ...authHeaders() 
    },
    body: JSON.stringify(body),
  });
  return parseOrThrow(res);
}

export async function apiDelete(path) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: "DELETE",
    headers: { 
      "Content-Type": "application/json", 
      "ngrok-skip-browser-warning": "true",
      ...authHeaders() 
    },
  });
  return parseOrThrow(res);
}

// ===== FOOD API =====
export async function getFoodCategories() {
  return apiGet("/food/categories");
}

export async function getFoodItems(categoryId) {
  if(categoryId) return apiGet(`/food/items/category/${categoryId}`);
  return apiGet("/food/items");
}

export async function createFoodOrder(foodItems, ticketId, paymentId) {
  return apiPost("/food/orders", {
    ticketId: ticketId,
    paymentId: paymentId,
    items: foodItems.map(item => ({
      foodItemId: item.id,
      quantity: item.quantity
    }))
  });
}

