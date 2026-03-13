/**
 * ⚠️ CẬP NHẬT 2 LINK NGROK TẠI ĐÂY NỦA LẦN THAY ĐỔI
 */

const CONFIG = {
  // ========== NGROK URLs (UPDATE HERE) ==========
  FRONTEND_URL: "https://web-datvexemphim.vercel.app",  // ngrok http 5500 (Frontend)
  API_BASE: "https://poly-gemmological-agnes.ngrok-free.dev/api",  // ngrok http 9090 (Backend) "https://abc1-ngrok.ngrok.io/api"
  
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
