/**
 * ⚠️ CẬP NHẬT  LINK BACKEND TẠI ĐÂY
 */

const CONFIG = {
  // ========== PRODUCTION (Vercel + Railway) ==========
  FRONTEND_URL: "https://web-datvexemphim.vercel.app",  // vercel (Frontend)
  API_BASE: "https://webdatvexemphim-production.up.railway.app/api",  // Railway Backend

  // ========== DEV Mode (Local) ==========
  // Uncomment để dùng local:
  // FRONTEND_URL: "http://localhost:5500",
  // API_BASE: "http://localhost:9090/api",
};

// Validate config
if (!CONFIG.API_BASE) {
  console.error("⚠️ CONFIG.API_BASE chưa được set!");
}

console.log("🔧 Config loaded:", CONFIG);
