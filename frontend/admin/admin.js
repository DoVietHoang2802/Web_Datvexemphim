import { getToken } from "../assets/js/api.js";

export function requireAdmin() {
  const token = getToken();
  const role = localStorage.getItem("userRole");
  if (!token) location.href = "../login.html";
  if (role !== "ADMIN") location.href = "../index.html";
}

export async function loadAdminNavbar() {
  const html = `
    <nav class="navbar navbar-expand-lg navbar-dark sticky-top">
      <div class="container">
        <a class="navbar-brand brand" href="dashboard.html">ADMIN</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#nav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="nav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item"><a class="nav-link" href="dashboard.html">Bảng điều khiển</a></li>
            <li class="nav-item"><a class="nav-link" href="movies.html">Quản lý phim</a></li>
            <li class="nav-item"><a class="nav-link" href="rooms.html">Quản lý phòng</a></li>
            <li class="nav-item"><a class="nav-link" href="showtimes.html">Quản lý lịch chiếu</a></li>
            <li class="nav-item"><a class="nav-link" href="tickets.html">Quản lý vé</a></li>
            <li class="nav-item"><a class="nav-link" href="users.html">Quản lý người dùng</a></li>
          </ul>
          <a class="btn btn-outline-light btn-sm" href="../index.html">Về site</a>
        </div>
      </div>
    </nav>
    
    <!-- Contact Widget -->
    <div id="contactWidget" class="contact-widget">
      <div class="contact-widget-toggle">
        <button class="contact-toggle-btn" onclick="toggleContactWidget()">
          <i class="fas fa-comments"></i>
        </button>
      </div>
      <div class="contact-widget-popup">
        <div class="contact-widget-header">
          <h5>Liên hệ</h5>
          <button class="close-contact-btn" onclick="toggleContactWidget()">&times;</button>
        </div>
        <div class="contact-widget-content">
          <div class="contact-item">
            <i class="fas fa-envelope"></i>
            <div class="contact-info">
              <p class="contact-label">Email</p>
              <a href="mailto:doviethoang281202@gmail.com">doviethoang281202@gmail.com</a>
            </div>
          </div>
          <div class="contact-item">
            <i class="fas fa-phone"></i>
            <div class="contact-info">
              <p class="contact-label">Điện thoại</p>
              <a href="tel:0866924119">0866924119</a>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <style>
      .contact-widget{position:fixed;bottom:20px;right:20px;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Oxygen,Ubuntu,Cantarell,sans-serif;z-index:9999}
      .contact-widget-toggle{position:relative;z-index:1001}
      .contact-toggle-btn{width:60px;height:60px;border-radius:50%;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;border:none;font-size:24px;cursor:pointer;box-shadow:0 4px 12px rgba(0,0,0,.15);transition:all .3s ease;display:flex;align-items:center;justify-content:center}
      .contact-toggle-btn:hover{transform:scale(1.1);box-shadow:0 6px 16px rgba(0,0,0,.2)}
      .contact-toggle-btn:active{transform:scale(.95)}
      .contact-widget-popup{position:absolute;bottom:80px;right:0;width:320px;background:#fff;border-radius:12px;box-shadow:0 5px 40px rgba(0,0,0,.16);opacity:0;visibility:hidden;transform:translateY(10px);transition:all .3s ease;z-index:1000}
      .contact-widget-popup.active{opacity:1;visibility:visible;transform:translateY(0)}
      .contact-widget-header{display:flex;justify-content:space-between;align-items:center;padding:16px;border-bottom:1px solid #e5e7eb;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff;border-radius:12px 12px 0 0}
      .contact-widget-header h5{margin:0;font-size:16px;font-weight:600}
      .close-contact-btn{background:0;border:none;color:#fff;font-size:24px;cursor:pointer;padding:0;line-height:1}
      .contact-widget-content{padding:16px}
      .contact-item{display:flex;align-items:flex-start;margin-bottom:16px;gap:12px}
      .contact-item:last-child{margin-bottom:0}
      .contact-item i{color:#667eea;font-size:20px;margin-top:2px;flex-shrink:0}
      .contact-info{flex:1}
      .contact-label{margin:0 0 6px 0;font-size:13px;font-weight:600;color:#666}
      .contact-info a{color:#667eea;text-decoration:none;font-size:14px;word-break:break-all;transition:color .2s}
      .contact-info a:hover{color:#764ba2;text-decoration:underline}
      @media (max-width:600px){.contact-widget-popup{width:280px}.contact-toggle-btn{width:50px;height:50px;font-size:20px}}
    </style>
    
    <script>
      function toggleContactWidget(){const e=document.querySelector('.contact-widget-popup');if(e)e.classList.toggle('active')}
      document.addEventListener('click',function(e){const t=document.querySelector('.contact-widget');if(t&&!t.contains(e.target)){const p=document.querySelector('.contact-widget-popup');if(p)p.classList.remove('active')}})
    </script>
  `;
  document.querySelector("#navbar").innerHTML = html;
}

