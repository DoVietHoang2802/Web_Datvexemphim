# 🎬 Website Đặt Vé Xem Phim - DatVeXemPhim

## 📋 Tổng quan dự án

Đây là hệ thống website đặt vé xem phim trực tuyến, cho phép người dùng:
- Đăng ký/Đăng nhập tài khoản
- Xem danh sách phim và lịch chiếu
- Chọn ghế ngồi trong rạp
- Đặt vé và thanh toán trực tuyến (mô phỏng)
- Hủy vé và chuyển vé cho người khác
- Đặt đồ ăn/thức uống kèm theo vé
- Đánh giá phim sau khi xem
- Sử dụng mã giảm giá (voucher)
- Quản lý hồ sơ cá nhân

---

## 🛠 Công nghệ sử dụng

### Backend
| Công nghệ | Phiên bản | Mô tả |
|-----------|-----------|-------|
| Java | 17 | Ngôn ngữ lập trình |
| Spring Boot | 3.3.4 | Framework phát triển ứng dụng Java |
| Spring Security | 6.x | Bảo mật & Xác thực người dùng |
| JWT | - | JSON Web Token cho xác thực |
| JPA/Hibernate | 6.x | ORM để tương tác database |
| REST API | - | Giao diện lập trình |

### Frontend
| Công nghệ | Mô tả |
|-----------|-------|
| HTML5 | Cấu trúc trang web |
| CSS3 | Styling giao diện |
| JavaScript (ES6+) | Xử lý logic phía client |
| Bootstrap 5.3 | Framework CSS cho giao diện |
| Fetch API | Gọi API từ server |

### Database & Deployment
| Service | Mô tả |
|---------|-------|
| MySQL | Hệ quản trị cơ sở dữ liệu |
| Railway | Nền tảng deploy MySQL & Backend |
| Vercel | Nền tảng deploy Frontend |
| GitHub | Quản lý mã nguồn |

---

## 📁 Cấu trúc dự án

```
web_datvexemphim/
├── backend/                    # Spring Boot API
│   ├── src/main/java/com/datvexemphim/
│   │   ├── api/
│   │   │   ├── controller/    # REST Controllers
│   │   │   ├── dto/          # Data Transfer Objects
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── config/
│   │   │   └── DataSeeder.java
│   │   ├── domain/
│   │   │   ├── entity/       # JPA Entities
│   │   │   ├── repository/   # JPA Repositories
│   │   │   └── enums/        # Enumerations
│   │   ├── security/         # JWT & Security
│   │   │   ├── JwtService.java
│   │   │   ├── JwtAuthFilter.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── CustomUserDetailsService.java
│   │   └── service/          # Business Logic
│   │       ├── admin/        # Admin services
│   │       └── *.java        # User services
│   └── src/main/resources/
│       └── application.yml   # Cấu hình ứng dụng
│
├── frontend/                  # Static HTML/JS/CSS
│   ├── index.html            # Trang chủ
│   ├── movie.html            # Chi tiết phim + Đánh giá
│   ├── showtimes.html        # Lịch chiếu
│   ├── seatmap.html          # Chọn ghế
│   ├── checkout.html         # Thanh toán + Voucher
│   ├── tickets.html          # Lịch sử vé
│   ├── food.html             # Đồ ăn/thức uống
│   ├── profile.html          # Hồ sơ người dùng ⭐ NEW
│   ├── login.html            # Đăng nhập
│   ├── register.html         # Đăng ký
│   ├── admin/               # Trang quản trị
│   │   ├── dashboard.html
│   │   ├── movies.html
│   │   ├── genres.html      # Quản lý thể loại
│   │   ├── rooms.html
│   │   ├── showtimes.html
│   │   ├── tickets.html
│   │   ├── users.html
│   │   ├── food.html
│   │   └── vouchers.html    # Quản lý voucher ⭐ NEW
│   └── assets/
│       ├── css/              # Styles
│       ├── js/               # JavaScript
│       │   └── config.js     # Cấu hình API
│       └── images/
│
├── database/                  # SQL scripts
│   └── *.sql                 # Database migrations
│
├── Dockerfile                 # Docker configuration
├── render.yaml               # Render deployment config
├── railway.json              # Railway deployment config
└── README.md                # Tài liệu này
```

---

## 🗄 Thiết kế Database

### Sơ đồ các bảng

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    users    │     │   movies    │     │    rooms    │
├─────────────┤     ├─────────────┤     ├─────────────┤
│ id          │     │ id          │     │ id          │
│ email       │     │ title       │     │ name        │
│ password    │     │ duration    │     │ totalSeats  │
│ fullName    │     │ posterUrl   │     │ rows        │
│ phone       │     │ rating      │     │ cols        │
│ avatarUrl   │     │ genreId     │     └──────┬──────┘
│ role        │     │ description │            │
│ createdAt   │     │ isActive   │            │
└──────┬──────┘     └──────┬──────┘            │
       │                    │                    │
       ▼                    ▼                    ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│ movie_genres│     │ showtimes   │     │   seats     │
├─────────────┤     ├─────────────┤     ├─────────────┤
│ id          │     │ id          │     │ id          │
│ name        │     │ movieId (FK)│     │ roomId  (FK)│
│ description │     │ roomId  (FK)│     │ row         │
│ isActive    │     │ startTime   │     │ col         │
└─────────────┘     │ price       │     │ seatType    │
                    │ status      │     │ isAvailable │
                    └──────┬──────┘     └─────────────┘
                           │
                           ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   tickets   │     │  payments   │     │    users    │
├─────────────┤     ├─────────────┤     ├─────────────┤
│ id          │     │ id          │     │ id          │
│ showtimeId(FK)│   │ ticketId(FK)│     │ fullName    │
│ seatId   (FK)│    │ amount      │     │ email       │
│ userId   (FK)│    │ method      │     │ phone       │
│ status       │     │ status      │     │ avatarUrl   │
│ createdAt    │     │ paidAt      │     │ role       │
└──────┬──────┘     └─────────────┘     └─────────────┘
       │
       ▼
┌─────────────┐     ┌─────────────┐
│transferHistory          │ vouchers   │ ⭐ NEW
├─────────────┤     ├─────────────┤
│ id          │     │ id          │
│ ticketId(FK)│     │ code        │
│ fromUserId  │     │ description │
│ toUserId    │     │ discountPercent
│ transferredAt│     │ maxDiscount │
└─────────────┘     │ minOrderAmount
                    │ validFrom
┌─────────────┐     │ validUntil
│ movie_reviews│ ⭐ NEW     │ usageLimit
├─────────────┤     │ usedCount   │
│ id          │     │ isActive    │
│ movieId (FK)│     └─────────────┘
│ userId (FK) │
│ rating      │
│ comment     │
│ createdAt   │
└─────────────┘
```

### Danh sách các bảng

| Bảng | Mô tả |
|------|-------|
| `users` | Thông tin người dùng (user/admin) |
| `movies` | Danh sách phim |
| `movie_genres` | Thể loại phim |
| `rooms` | Các phòng chiếu |
| `seats` | Ghế ngồi trong từng phòng |
| `showtimes` | Suất chiếu (phim + phòng + giờ) |
| `tickets` | Vé đã đặt |
| `payments` | Thông tin thanh toán |
| `transfer_history` | Lịch sử chuyển vé |
| `movie_reviews` | Đánh giá phim ⭐ NEW |
| `vouchers` | Mã giảm giá ⭐ NEW |
| `food_category` | Danh mục đồ ăn |
| `food_item` | Sản phẩm đồ ăn |
| `food_order` | Đơn đồ ăn |
| `food_order_item` | Chi tiết đơn đồ ăn |

---

## 🚀 Cách chạy dự án

### Chạy Local (Development)

#### 1. Yêu cầu
- JDK 17 trở lên
- Maven 3.6+
- MySQL (Laragon hoặc cài đặt riêng)

#### 2. Cấu hình Database

Mở `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/datvexemphim
    username: root
    password: YOUR_PASSWORD
```

#### 3. Chạy Backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend chạy tại: `http://localhost:9090`

#### 4. Chạy Frontend

Cách 1 - Dùng Live Server (VSCode):
- Cài đặt extension "Live Server"
- Mở `frontend/index.html` → Click "Go Live"

Cách 2 - Dùng Python:
```bash
cd frontend
python -m http.server 5500
```

Frontend chạy tại: `http://localhost:5500`

#### 5. Tài khoản mặc định

| Loại | Email | Mật khẩu |
|------|-------|----------|
| Admin | admin@local | 123456 |

---

### Deploy lên Production

#### 1. Deploy Database (Railway MySQL)

1. Đăng nhập [railway.app](https://railway.app)
2. Tạo **New Project** → **MySQL**
3. Copy các biến môi trường:
   - `MYSQLHOST`
   - `MYSQLPORT`
   - `MYSQL_DATABASE`
   - `MYSQLUSER`
   - `MYSQLPASSWORD`

#### 2. Deploy Backend (Railway)

1. Tạo **New Project** → **Deploy from GitHub**
2. Chọn repo `web_datvexemphim`
3. Thêm các biến môi trường:
   - `MYSQLHOST`: `${{MySQL.MYSQLHOST}}`
   - `MYSQLPORT`: `${{MySQL.MYSQLPORT}}`
   - `MYSQL_DATABASE`: `${{MySQL.MYSQLDATABASE}}`
   - `MYSQLUSER`: `${{MySQL.MYSQLUSER}}`
   - `MYSQLPASSWORD`: `${{MySQL.MYSQLPASSWORD}}`
   - `APP_JWT_SECRET`: (chuỗi bí mật của bạn)
4. Click **Deploy**

#### 3. Deploy Frontend (Vercel)

1. Đăng nhập [vercel.com](https://vercel.com)
2. Import repo `web_datvexemphim`
3. Thư mục deploy: `frontend`
4. Cập nhật `frontend/assets/js/config.js`:
   ```javascript
   API_BASE: "https://your-railway-app.up.railway.app/api"
   ```
5. Deploy

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/auth/register` | Đăng ký tài khoản |
| POST | `/api/auth/login` | Đăng nhập |

### User Profile ⭐ NEW
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/user/profile` | Lấy thông tin hồ sơ |
| PUT | `/api/user/profile` | Cập nhật hồ sơ |
| POST | `/api/user/change-password` | Đổi mật khẩu |

### Movies
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/movies` | Lấy danh sách phim |
| GET | `/api/movies/{id}` | Chi tiết phim |
| GET | `/api/movies/genres` | Danh sách thể loại |

### Movie Reviews ⭐ NEW
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/movies/{id}/reviews` | Lấy đánh giá của phim |
| GET | `/api/movies/{id}/reviews/stats` | Thống kê rating |
| POST | `/api/movies/reviews` | Gửi đánh giá |
| DELETE | `/api/movies/reviews/{id}` | Xóa đánh giá |

### Vouchers ⭐ NEW
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/vouchers/validate` | Kiểm tra & áp dụng voucher |

### Showtimes
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/showtimes` | Tất cả suất chiếu |
| GET | `/api/showtimes/movie/{movieId}` | Suất chiếu theo phim |

### Seat Map
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/seatmap/{showtimeId}` | Sơ đồ ghế |

### Booking
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/bookings` | Đặt vé |
| GET | `/api/tickets/me` | Lịch sử vé của tôi |
| POST | `/api/tickets/{id}/cancel` | Hủy vé |
| POST | `/api/tickets/transfer` | Chuyển vé |

### Payment
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/payments/simulate` | Mô phỏng thanh toán |

### Food & Drink
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/food/categories` | Danh mục đồ ăn |
| GET | `/api/food/items` | Sản phẩm đồ ăn |
| POST | `/api/food/orders` | Đặt đồ ăn |

### Admin APIs
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET/POST/PUT/DELETE | `/api/admin/movies` | Quản lý phim |
| GET/POST/PUT/DELETE | `/api/admin/movies/genres` | Quản lý thể loại |
| GET/POST/PUT/DELETE | `/api/admin/rooms` | Quản lý phòng |
| GET/POST/PUT/DELETE | `/api/admin/showtimes` | Quản lý suất chiếu |
| GET/POST/PUT/DELETE | `/api/admin/food/categories` | Quản lý danh mục |
| GET/POST/PUT/DELETE | `/api/admin/food/items` | Quản lý sản phẩm |
| GET/POST/PUT/DELETE | `/api/admin/vouchers` | Quản lý voucher ⭐ NEW |
| GET | `/api/admin/stats/revenue` | Thống kê doanh thu |

---

## 🎨 UI Effects & Design Patterns

Dưới đây là các hiệu ứng UI được sử dụng trong dự án:

### 1. Gradient Background cho Cards
```css
background: linear-gradient(145deg, #1e1e2f 0%, #252538 100%);
```

### 2. Border transparent mờ
```css
border: 1px solid rgba(255, 255, 255, 0.08);
```

### 3. Box Shadow đa tầng
```css
box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
```

### 4. Cubic-bezier Transition (mượt hơn ease)
```css
transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
```

### 5. Hiệu ứng Shimmer (ánh sáng chạy qua)
```css
.card::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.1), transparent);
}
.card:hover::after {
  left: 100%;
  transition: left 0.5s ease;
}
```

### 6. Gradient Border Top khi Hover
```css
.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2, #667eea);
  background-size: 200% 100%;
  opacity: 0;
  transition: opacity 0.3s ease;
}
.card:hover::before {
  opacity: 1;
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}
```

### 7. Transform Scale + Translate khi Hover
```css
.card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.5);
}
```

### 8. Accent Color qua CSS Variable
```css
.stat-card {
  --accent-color: #f56566;
}
.stat-card::before {
  background: var(--accent-color);
}
```

---

## 🔧 Tính năng đặc biệt

### 1. Chống đặt trùng ghế
- Sử dụng **unique constraint** ở database: `uk_ticket_showtime_seat`
- Kiểm tra trước khi đặt trong code
- Chống race-condition khi nhiều người đặt cùng lúc

### 2. Giải phóng ghế khi thanh toán thất bại
- Khi thanh toán thất bại, hệ thống **xóa ticket PENDING**
- Ghế sẽ được giải phóng để người khác có thể đặt

### 3. Hủy vé linh hoạt
- Chỉ hủy được khi `now < startTime - 30 phút`
- Không hoàn tiền tự động

### 4. Chuyển vé cho người khác
- Cho phép chuyển vé đã mua cho người khác
- **Food order đi kèm cũng được chuyển theo**
- Lưu lịch sử chuyển vé

### 5. Đặt đồ ăn kèm theo vé
- Tích hợp đặt đồ ăn/thức uống trong quá trình thanh toán
- Tính tổng tiền vé + đồ ăn

### 6. Mã giảm giá (Voucher) ⭐ NEW
- Admin tạo/quản lý voucher
- Người dùng nhập mã khi thanh toán
- Hỗ trợ: giảm %, giới hạn sử dụng, thời hạn

### 7. Đánh giá phim ⭐ NEW
- Người dùng đánh giá 1-5 sao
- Bình luận phim
- Hiển thị thống kê rating (trung bình, phân bố)

### 8. Hồ sơ người dùng ⭐ NEW
- Chỉnh sửa thông tin cá nhân
- Thay đổi avatar
- Đổi mật khẩu

---

## 📝 Ghi chú kỹ thuật

- **Transaction**: Các thao tác tạo vé và thanh toán đều chạy trong transaction để đảm bảo tính toàn vẹn dữ liệu
- **JWT**: Sử dụng JWT cho xác thực người dùng, token lưu trong localStorage
- **CORS**: Cấu hình CORS cho phép frontend gọi API từ các domain khác nhau
- **Security**: Phân quyền user/admin thông qua Spring Security
- **Database**: Sử dụng `ddl-auto: update` để tự động tạo/cập nhật bảng

---

## 📞 Thông tin liên hệ

- **Tác giả**: Đỗ Việt Hoàng
- **Email**: doviethoang281202@gmail.com
- **GitHub**: https://github.com/hoangdv2002/web_datvexemphim

---

## 📄 License

Dự án được tạo cho mục đích học tập và demo.
