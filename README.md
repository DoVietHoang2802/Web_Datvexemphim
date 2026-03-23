# 🎬 DatVeXemPhim - Hệ thống Đặt Vé Xem Phim Trực Tuyến

> **Version**: 1.0.0 | **Updated**: 2026-03-21
> **Team**: 5 members | **Framework**: Spring Boot 3.3.4 + Vanilla HTML/JS

---

## 📋 Mục lục

1. [Giới thiệu](#1-giới-thiệu)
2. [Kiến trúc hệ thống](#2-kiến-trúc-hệ-thống)
3. [Công nghệ sử dụng](#3-công-nghệ-sử-dụng)
4. [Cấu trúc dự án](#4-cấu-trúc-dự-án)
5. [Database Schema](#5-database-schema)
6. [Tính năng chính](#6-tính-năng-chính)
7. [Cách hoạt động](#7-cách-hoạt-động)
8. [API Endpoints](#8-api-endpoints)
9. [Hướng dẫn cài đặt & chạy](#9-hướng-dẫn-cài-đặt--chạy)
10. [Deploy](#10-deploy)
11. [Tài khoản mặc định](#11-tài-khoản-mặc-định)
12. [Liên kết nhanh](#12-liên-kết-nhanh)

---

## 1. Giới thiệu

**DatVeXemPhim** là hệ thống website đặt vé xem phim trực tuyến đầy đủ chức năng.

### Người dùng (User)
- Đăng ký, đăng nhập (email + Google OAuth)
- Xem phim, lọc theo thể loại, tìm kiếm
- Đặt vé tương tác (chọn ghế), thanh toán VNPay
- Đặt đồ ăn/nước uống kèm vé
- Hủy vé, xóa vé đã hủy, quay lại thanh toán
- Chuyển vé, đánh giá phim, áp dụng voucher

### Quản trị (Admin)
- Dashboard thống kê doanh thu
- CRUD phim, thể loại, phòng chiếu, suất chiếu
- Quản lý vé, users, voucher, đồ ăn

---

## 2. Kiến trúc hệ thống

```
┌──────────────────────┐
│      CLIENTS          │
│   (Browser/Mobile)    │
└──────────┬────────────┘
           │ HTTPS
┌──────────▼──────────────────────────┐
│     VERCEL FRONTEND                 │
│  https://web-datvexemphim.vercel.app│
│  Static HTML + CSS + JavaScript     │
│  Glassmorphism UI Design             │
└──────────┬──────────────────────────┘
           │ REST API
┌──────────▼──────────────────────────┐
│    RAILWAY BACKEND                  │
│  https://web-datvexemphim-produc... │
│  Spring Boot 3.3.4 (Java 17)         │
│  JWT Authentication                  │
└──────────┬──────────────────────────┘
           │ JDBC
┌──────────▼──────────────────────────┐
│      RAILWAY MYSQL                  │
│      15 tables (JPA/Hibernate)      │
└─────────────────────────────────────┘
```

---

## 3. Công nghệ sử dụng

### Backend
| Công nghệ | Phiên bản | Mô tả |
|-----------|-----------|-------|
| Java | 17 | Ngôn ngữ lập trình |
| Spring Boot | 3.3.4 | Framework backend |
| Spring Security | 6.x | Xác thực & phân quyền |
| JWT | - | Token xác thực |
| Spring Data JPA | - | ORM |
| MySQL | 8.x | Database |
| Maven | 3.x | Build tool |

### Frontend
| Công nghệ | Mô tả |
|-----------|-------|
| HTML5 + CSS3 | Giao diện (Glassmorphism design) |
| JavaScript ES6+ | Tương tác |
| Bootstrap | 5.3.3 - CSS framework |
| Font Awesome | 6.4.0 - Icons |
| Firebase SDK | Google OAuth login |

### Deployment
| Nền tảng | Mục đích |
|---------|---------|
| Vercel | Frontend hosting |
| Railway | Backend API + Database |
| VNPay Sandbox | Thanh toán test |

---

## 4. Cấu trúc dự án

```
DatVeXemPhim/
│
├── backend/                              # Spring Boot API (Java 17)
│   ├── src/main/java/com/datvexemphim/
│   │   ├── MovieTicketBookingApplication.java   # Entry point + @EnableScheduling
│   │   │
│   │   ├── api/
│   │   │   ├── GlobalExceptionHandler.java     # Xử lý lỗi tập trung
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java         # Login/Register/Google OAuth
│   │   │   │   ├── BookingController.java      # Đặt vé (PENDING tickets)
│   │   │   │   ├── MovieController.java        # Danh sách phim
│   │   │   │   ├── MovieReviewController.java   # Đánh giá 1-5 sao
│   │   │   │   ├── PasswordResetController.java # Quên mật khẩu
│   │   │   │   ├── PaymentController.java       # Thanh toán mô phỏng
│   │   │   │   ├── SeatMapController.java       # Sơ đồ ghế
│   │   │   │   ├── ShowtimeController.java     # Suất chiếu
│   │   │   │   ├── TicketController.java       # Xem, hủy, xóa, chuyển vé
│   │   │   │   ├── UploadController.java       # Upload ảnh
│   │   │   │   ├── UserController.java        # Hồ sơ user
│   │   │   │   ├── VnpayController.java       # VNPay sandbox
│   │   │   │   ├── VoucherController.java     # Validate voucher
│   │   │   │   ├── FoodController.java         # Đồ ăn/nước uống
│   │   │   │   └── admin/
│   │   │   │       ├── AdminDashboardController.java  # Thống kê
│   │   │   │       ├── AdminMovieController.java     # CRUD phim
│   │   │   │       ├── AdminRoomController.java      # CRUD phòng
│   │   │   │       ├── AdminSeatController.java     # Tạo ghế
│   │   │   │       ├── AdminShowtimeController.java  # CRUD suất chiếu
│   │   │   │       ├── AdminTicketController.java   # Quản lý vé
│   │   │   │       ├── AdminUserController.java    # Quản lý users
│   │   │   │       ├── AdminVoucherController.java # CRUD voucher
│   │   │   │       └── AdminFoodController.java   # CRUD đồ ăn
│   │   │   │
│   │   │   └── dto/                    # Data Transfer Objects
│   │   │       ├── auth/              # Login, Register, ResetPassword DTOs
│   │   │       ├── booking/           # Booking request/response
│   │   │       ├── publicapi/         # Movie, Showtime, SeatMap DTOs
│   │   │       ├── food/               # Food order DTOs
│   │   │       ├── payment/            # Payment DTOs
│   │   │       ├── ticket/             # Ticket history DTOs
│   │   │       ├── user/               # User profile DTOs
│   │   │       ├── common/             # ApiError
│   │   │       └── admin/              # Admin CRUD request/response DTOs
│   │   │
│   │   ├── config/
│   │   │   ├── CorsConfig.java        # CORS cho Vercel
│   │   │   ├── DataSeeder.java         # Tạo dữ liệu mẫu khi khởi chạy
│   │   │   ├── VnpayConfig.java       # Cấu hình VNPay sandbox
│   │   │   └── WebConfig.java
│   │   │
│   │   ├── domain/
│   │   │   ├── entity/               # JPA Entities (15 bảng)
│   │   │   │   ├── User.java          # Người dùng
│   │   │   │   ├── Movie.java          # Phim
│   │   │   │   ├── MovieGenre.java    # Thể loại phim
│   │   │   │   ├── MovieReview.java   # Đánh giá
│   │   │   │   ├── Room.java          # Phòng chiếu
│   │   │   │   ├── Seat.java          # Ghế ngồi
│   │   │   │   ├── Showtime.java      # Suất chiếu
│   │   │   │   ├── Ticket.java        # Vé (PENDING/CONFIRMED/CANCELLED)
│   │   │   │   ├── Payment.java       # Thanh toán
│   │   │   │   ├── Voucher.java       # Mã giảm giá
│   │   │   │   ├── TransferHistory.java # Lịch sử chuyển vé
│   │   │   │   ├── FoodCategory.java  # Danh mục đồ ăn
│   │   │   │   ├── FoodItem.java      # Sản phẩm đồ ăn
│   │   │   │   ├── FoodOrder.java     # Đơn đồ ăn
│   │   │   │   ├── FoodOrderItem.java # Chi tiết đơn đồ ăn
│   │   │   │   └── PasswordResetToken.java
│   │   │   ├── enums/                 # Role, TicketStatus, PaymentStatus, etc.
│   │   │   └── repository/           # JPA Repositories (15 repos)
│   │   │
│   │   ├── security/                 # JWT & Security
│   │   │   ├── JwtService.java       # Tạo & verify JWT
│   │   │   ├── JwtAuthFilter.java    # Filter xác thực
│   │   │   ├── SecurityConfig.java   # Spring Security config
│   │   │   ├── CustomUserDetailsService.java
│   │   │   └── UserPrincipal.java
│   │   │
│   │   └── service/                  # Business Logic
│   │       ├── AuthService.java
│   │       ├── MovieService.java
│   │       ├── ShowtimeService.java
│   │       ├── BookingService.java   # Tạo vé PENDING
│   │       ├── SeatMapService.java
│   │       ├── TicketService.java    # Hủy, xóa, chuyển vé
│   │       ├── TicketCleanupJob.java # Auto-hủy PENDING sau 10 phút
│   │       ├── PaymentService.java
│   │       ├── VnpayService.java     # SHA512 HMAC
│   │       ├── FoodOrderService.java
│   │       ├── MovieReviewService.java
│   │       ├── VoucherService.java
│   │       ├── UserProfileService.java
│   │       └── admin/
│   │           ├── AdminMovieService.java
│   │           ├── AdminRoomService.java
│   │           ├── AdminSeatService.java
│   │           ├── AdminShowtimeService.java
│   │           ├── AdminTicketService.java
│   │           ├── AdminUserService.java
│   │           ├── AdminVoucherService.java
│   │           ├── AdminStatsService.java
│   │           └── MovieGenreService.java
│   │
│   └── src/main/resources/
│       └── application.yml            # Port 9090, MySQL config
│
├── frontend/                            # Static HTML/JS/CSS
│   ├── index.html                     # Trang chủ - phim đang chiếu
│   ├── movie.html                     # Chi tiết phim + đánh giá + trailer
│   ├── showtimes.html                 # Lịch chiếu (hiện có / đã hết)
│   ├── seatmap.html                   # Sơ đồ ghế tương tác
│   ├── food.html                      # Đặt đồ ăn/nước uống
│   ├── checkout.html                  # Thanh toán VNPay / mô phỏng
│   ├── tickets.html                   # Lịch sử vé + hủy + xóa + thanh toán lại
│   ├── profile.html                   # Hồ sơ người dùng
│   ├── login.html                     # Đăng nhập (email + Google)
│   ├── register.html                  # Đăng ký
│   ├── forgot-password.html           # Quên mật khẩu
│   ├── reset-password.html           # Reset (từ email link)
│   │
│   ├── admin/                        # Trang quản trị
│   │   ├── dashboard.html           # Dashboard thống kê
│   │   ├── movies.html              # CRUD phim
│   │   ├── genres.html              # CRUD thể loại
│   │   ├── rooms.html               # CRUD phòng + tạo ghế
│   │   ├── showtimes.html           # CRUD suất chiếu
│   │   ├── tickets.html             # Quản lý vé
│   │   ├── users.html               # Quản lý users
│   │   ├── vouchers.html            # CRUD voucher
│   │   └── food.html                # CRUD đồ ăn
│   │
│   ├── partials/
│   │   ├── navbar.html              # Navigation bar
│   │   └── contact-widget.html     # Contact info
│   │
│   └── assets/
│       ├── css/
│       │   └── style.css           # Glassmorphism design system
│       └── js/
│           ├── api.js              # apiGet, apiPost, apiDelete, apiUpload
│           ├── app.js              # Toast, formatVnd, requireAuth, setupNavbar
│           ├── config.js           # API_BASE, FRONTEND_URL
│           ├── firebase.js         # Firebase Google Sign-In
│           └── admin/
│               ├── admin.js       # Admin auth guard
│               └── modals.js       # Bootstrap modals
│
├── database/                           # SQL scripts
│   ├── README_IMPORT_SQL.md
│   ├── init.sql                      # Schema chính
│   ├── 01_create_food_tables.sql
│   └── 02_insert_sample_food_data.sql
│
├── API_TESTING.md                      # Test API (Production - Railway)
├── API_TESTING_LOCAL.md                # Test API (Localhost:9090)
├── WORK_DIVISION.md                    # Phân chia công việc 5 thành viên
├── PROJECT_STATUS.md                   # Trạng thái dự án
├── Dockerfile                          # Docker config
├── railway.json                        # Railway deployment
└── render.yaml                        # Render deployment
```

---

## 5. Database Schema

### 15 Tables

| # | Bảng | Quan hệ | Mô tả |
|---|------|---------|-------|
| 1 | `users` | - | Người dùng (USER/ADMIN) |
| 2 | `movies` | `movie_genres` | Phim |
| 3 | `movie_genres` | 1:N movies | Thể loại |
| 4 | `movie_reviews` | users, movies | Đánh giá phim |
| 5 | `rooms` | 1:N seats, showtimes | Phòng chiếu |
| 6 | `seats` | rooms | Ghế ngồi |
| 7 | `showtimes` | movies, rooms | Suất chiếu |
| 8 | `tickets` | showtimes, seats, users, payments | Vé |
| 9 | `payments` | users | Thanh toán |
| 10 | `vouchers` | - | Mã giảm giá |
| 11 | `transfer_histories` | tickets, users | Lịch sử chuyển vé |
| 12 | `food_categories` | 1:N food_items | Danh mục đồ ăn |
| 13 | `food_items` | food_categories | Sản phẩm đồ ăn |
| 14 | `food_orders` | payments, tickets | Đơn đồ ăn |
| 15 | `food_order_items` | food_orders, food_items | Chi tiết đơn đồ ăn |

### Enums

| Enum | Giá trị |
|------|---------|
| `Role` | USER, ADMIN |
| `TicketStatus` | PENDING, CONFIRMED, CANCELLED |
| `PaymentStatus` | PENDING, SUCCESS, FAILED |
| `ShowtimeStatus` | SCHEDULED, CANCELLED |
| `AuthProvider` | LOCAL, GOOGLE |

---

## 6. Tính năng chính

### Người dùng
- Đăng ký / Đăng nhập (email + Google OAuth)
- Xem phim đang chiếu (lọc thể loại, sắp xếp, tìm kiếm)
- Xem chi tiết phim (poster, trailer YouTube, đánh giá sao)
- Xem lịch chiếu (hiện có / đã hết)
- Chọn ghế tương tác (grid, legend ghế VIP/Couple/Standard)
- Đặt đồ ăn/nước uống kèm vé
- Thanh toán VNPay sandbox hoặc mô phỏng
- Quay lại thanh toán vé PENDING (hiển thị đầy đủ thông tin)
- Hủy vé (trước giờ chiếu 30 phút) → ghế giải phóng
- Xóa vé đã hủy (dọn database)
- Chuyển vé cho người khác
- Áp dụng mã giảm giá (voucher)
- Quên / Reset mật khẩu
- Hồ sơ cá nhân, đổi mật khẩu

### Admin
- Dashboard thống kê doanh thu
- CRUD phim (upload poster, trailer)
- CRUD thể loại phim
- CRUD phòng chiếu + tạo ghế tự động
- CRUD suất chiếu
- Quản lý vé (xem, hủy)
- Quản lý users (đổi role, reset password, xóa)
- CRUD voucher (bật/tắt)
- CRUD danh mục & sản phẩm đồ ăn

### Hệ thống
- **Auto-hủy vé PENDING sau 10 phút** (giải phóng ghế tự động)
- JWT Authentication (24 giờ)
- Chống đặt trùng ghế (database unique constraint)
- Toast notification (thay alert())
- Glassmorphism UI + Responsive
- VNPay Sandbox (SHA512 HMAC)

---

## 7. Cách hoạt động

### Luồng đặt vé
```
Đăng nhập → Chọn phim → Chọn suất → Chọn ghế
    → Đặt đồ ăn (tùy chọn) → Checkout → Chọn thanh toán
    → VNPay / Mô phỏng → Xác nhận → Vé CONFIRMED ✅
```

### Luồng hủy & xóa vé
```
Vé CONFIRMED → Bấm "Hủy vé" → CANCELLED + ghế giải phóng
Vé CANCELLED → Bấm "Xóa vé" → Xóa hẳn khỏi DB
Ghế A1 trống → User khác đặt được ✅
```

### Luồng auto-cleanup (10 phút)
```
TicketCleanupJob chạy mỗi 60 giây
    → Tìm vé PENDING có bookedAt < now - 10 phút
    → CANCELLED + giải phóng ghế
```

### Luồng thanh toán lại (PENDING)
```
Vé PENDING → Bấm "Thanh toán" → checkout.html
    → Load chi tiết vé (tên phim, giờ, phòng, ghế, giá)
    → Thanh toán → CONFIRMED ✅
```

---

## 8. API Endpoints

### Authentication
| Method | Endpoint | Mô tả |
|--------|----------|--------|
| POST | `/api/auth/register` | Đăng ký |
| POST | `/api/auth/login` | Đăng nhập |
| POST | `/api/auth/google-login` | Google OAuth |
| POST | `/api/auth/forgot-password` | Quên mật khẩu |
| POST | `/api/auth/reset-password` | Reset mật khẩu |

### User
| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/api/user/profile` | Lấy hồ sơ |
| PUT | `/api/user/profile` | Cập nhật hồ sơ |
| POST | `/api/user/change-password` | Đổi mật khẩu |

### Movies
| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/api/movies` | Danh sách phim đang chiếu |
| GET | `/api/movies/{id}` | Chi tiết phim |
| GET | `/api/movies/genres` | Danh sách thể loại |
| GET | `/api/movies/past` | Phim đã chiếu |
| GET | `/api/movies/{id}/reviews` | Đánh giá phim |
| GET | `/api/movies/{id}/reviews/stats` | Thống kê rating |
| POST | `/api/movies/reviews` | Gửi đánh giá |
| DELETE | `/api/movies/reviews/{id}` | Xóa đánh giá |

### Showtimes & Seats
| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/api/showtimes` | Tất cả suất chiếu |
| GET | `/api/showtimes/movie/{id}` | Suất theo phim |
| GET | `/api/seatmap/{showtimeId}` | Sơ đồ ghế |

### Booking & Tickets
| Method | Endpoint | Mô tả |
|--------|----------|--------|
| POST | `/api/bookings` | Đặt vé (tạo PENDING) |
| GET | `/api/tickets/me` | Lịch sử vé của tôi |
| GET | `/api/tickets?ticketIds=` | Chi tiết vé theo IDs |
| POST | `/api/tickets/{id}/cancel` | Hủy vé |
| DELETE | `/api/tickets/{id}` | Xóa vé đã hủy |
| POST | `/api/tickets/transfer` | Chuyển vé |

### Payment
| Method | Endpoint | Mô tả |
|--------|----------|--------|
| POST | `/api/payments/simulate` | Thanh toán mô phỏng |
| POST | `/api/payments/vnpay/create` | Tạo URL VNPay |

### Food
| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/api/food/categories` | Danh mục đồ ăn |
| GET | `/api/food/items?categoryId=` | Sản phẩm theo danh mục |
| POST | `/api/food/orders` | Đặt đồ ăn |
| GET | `/api/food/orders` | Đơn đồ ăn của tôi |
| DELETE | `/api/food/orders/{id}` | Hủy đơn đồ ăn |

### Voucher
| Method | Endpoint | Mô tả |
|--------|----------|--------|
| POST | `/api/vouchers/validate` | Kiểm tra & áp dụng voucher |

### Admin
| Method | Endpoint | Mô tả |
|--------|----------|--------|
| GET | `/api/admin/stats/revenue` | Thống kê doanh thu |
| CRUD | `/api/admin/movies` | Quản lý phim |
| CRUD | `/api/admin/movies/genres` | Quản lý thể loại |
| CRUD | `/api/admin/rooms` | Quản lý phòng |
| CRUD | `/api/admin/seats` | Quản lý ghế |
| CRUD | `/api/admin/showtimes` | Quản lý suất chiếu |
| CRUD | `/api/admin/tickets` | Quản lý vé |
| CRUD | `/api/admin/users` | Quản lý users |
| CRUD | `/api/admin/vouchers` | Quản lý voucher |
| CRUD | `/api/admin/food/categories` | Danh mục đồ ăn |
| CRUD | `/api/admin/food/items` | Sản phẩm đồ ăn |

---

## 9. Hướng dẫn cài đặt & chạy

### Yêu cầu
- Java 17+
- Maven 3.8+
- MySQL 8.x

### Local Backend

```bash
# 1. Clone
git clone https://github.com/DoVietHoang2802/Web_Datvexemphim.git
cd Web_Datvexemphim

# 2. Tạo database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS datvexemphim CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. Chỉnh sửa application.yml (backend/src/main/resources/)
# Đổi spring.datasource.url về local:
# jdbc:mysql://localhost:3306/datvexemphim?...

# 4. Build
cd backend
mvn clean package -DskipTests

# 5. Chạy
mvn spring-boot:run
# Hoặc: java -jar target/datvexemphim-0.0.1-SNAPSHOT.jar
```

Backend chạy tại: **`http://localhost:9090`**

### Local Frontend

```bash
# Cách 1: Live Server (VS Code)
# Mở folder frontend → Right click → Open with Live Server

# Cách 2: Python
cd frontend
python -m http.server 5500

# Cách 3: Node.js
npx serve frontend -p 5500
```

Frontend chạy tại: **`http://localhost:5500`**

> DataSeeder tự động tạo dữ liệu mẫu ở lần chạy đầu tiên.

---

## 10. Deploy

### Backend → Railway
1. Push code lên GitHub
2. Vào railway.app → New Project → Deploy from GitHub
3. Thêm biến môi trường:
   ```
   MYSQLHOST = mysql.railway.internal
   MYSQLPORT = 3306
   MYSQL_DATABASE = railway
   MYSQLUSER = root
   MYSQLPASSWORD = <password>
   APP_JWT_SECRET = <long-random-secret>
   APP_BASE_URL = https://web-datvexemphim-production.up.railway.app
   ```
4. Build: `cd backend && mvn clean package -DskipTests`
5. Start: `cd backend && java -jar target/datvexemphim-0.0.1-SNAPSHOT.jar`

### Frontend → Vercel
1. Vào vercel.com → Import GitHub repo
2. Root Directory: `frontend`
3. Framework: `Other`
4. Deploy

---

## 11. Tài khoản mặc định

| Role | Email | Mật khẩu |
|------|-------|-----------|
| **Admin** | admin@local | 123456 |
| **User** | user@example.com | 123456 |

### Cách lấy Token
```bash
curl -X POST "http://localhost:9090/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@local", "password": "123456"}'
# Copy accessToken → dùng header: Authorization: Bearer <token>
```

---

## 12. Liên kết nhanh

| Mục | Đường dẫn |
|------|-----------|
| 🌐 Frontend (Vercel) | https://web-datvexemphim.vercel.app |
| 🔧 Backend API (Railway) | https://web-datvexemphim-production.up.railway.app |
| 📖 Test API Production | Xem file `API_TESTING.md` |
| 📖 Test API Localhost | Xem file `API_TESTING_LOCAL.md` |
| 👥 Phân chia công việc | Xem file `WORK_DIVISION.md` |
| 📊 Trạng thái dự án | Xem file `PROJECT_STATUS.md` |

---

## ⚠️ Lưu ý quan trọng

| Quy tắc | Chi tiết |
|---------|---------|
| **PENDING timeout** | Tự động hủy sau **10 phút** chưa thanh toán |
| **Hủy vé** | Chỉ được hủy trước giờ chiếu **30 phút** |
| **Chuyển vé** | Chỉ vé **CONFIRMED** mới chuyển được |
| **Xóa vé** | Chỉ vé **CANCELLED** mới xóa được |
| **JWT Token** | Hết hạn sau **24 giờ** |
| **VNPay** | Chỉ hoạt động trên sandbox |
| **Database** | DDL auto update (không cần migration) |

---

*Cập nhật: 2026-03-21*
