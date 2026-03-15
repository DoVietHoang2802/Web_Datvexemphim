# 🎬 Website Đặt Vé Xem Phim - DatVeXemPhim

## 📋 Tổng quan dự án

Đây là hệ thống website đặt vé xem phim trực tuyến, cho phép người dùng:
- Đăng ký/Đăng nhập tài khoản
- Xem danh sách phim và lịch chiếu
- Chọn ghế ngồi trong rạp
- Đặt vé và thanh toán trực tuyến
- Hủy vé và chuyển vé cho người khác
- Đặt đồ ăn/thức uống kèm theo vé

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
| Railway | Nền tảng deploy MySQL |
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
│   ├── movie.html            # Chi tiết phim
│   ├── showtimes.html        # Lịch chiếu
│   ├── seatmap.html          # Chọn ghế
│   ├── checkout.html         # Thanh toán
│   ├── tickets.html          # Lịch sử vé
│   ├── food.html             # Đồ ăn/thức uống
│   ├── login.html            # Đăng nhập
│   ├── register.html         # Đăng ký
│   ├── admin/                # Trang quản trị
│   │   ├── dashboard.html
│   │   ├── movies.html
│   │   ├── rooms.html
│   │   ├── showtimes.html
│   │   ├── tickets.html
│   │   ├── users.html
│   │   └── food.html
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
│ role        │     │ description │     └──────┬──────┘
│ createdAt   │     │ isActive   │            │
└──────┬──────┘     └─────────────┘            │
       │                                      │
       ▼                                      ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   seats     │     │ showtimes   │     │   tickets   │
├─────────────┤     ├─────────────┤     ├─────────────┤
│ id          │     │ id          │     │ id          │
│ roomId  (FK)│◄───│ movieId (FK)│     │ showtimeId(FK)
│ row         │     │ roomId  (FK)│     │ seatId   (FK)│
│ col         │     │ startTime   │     │ userId   (FK)│
│ seatType    │     │ price       │     │ status       │
│ isAvailable │     │ status      │     │ createdAt    │
└─────────────┘     └─────────────┘     └──────┬──────┘
                                               │
┌─────────────┐     ┌─────────────┐            │
│  payments   │     │transferHistory          │
├─────────────┤     ├─────────────┤            │
│ id          │     │ id          │            │
│ ticketId(FK)│◄───┤ ticketId(FK)│            │
│ amount      │     │ fromUserId  │            │
│ method      │     │ toUserId    │            │
│ status      │     │ transferredAt            │
│ paidAt      │     └─────────────┘            │
└─────────────┘                                │
                                                ▼
                                        ┌─────────────┐
                                        │ food_order  │
                                        ├─────────────┤
                                        │ id          │
                                        │ ticketId(FK)│
                                        │ userId   (FK)│
                                        │ totalAmount │
                                        │ status      │
                                        └─────────────┘
```

### Danh sách các bảng

| Bảng | Mô tả |
|------|-------|
| `users` | Thông tin người dùng (user/admin) |
| `movies` | Danh sách phim |
| `rooms` | Các phòng chiếu |
| `seats` | Ghế ngồi trong từng phòng |
| `showtimes` | Suất chiếu (phim + phòng + giờ) |
| `tickets` | Vé đã đặt |
| `payments` | Thông tin thanh toán |
| `transfer_history` | Lịch sử chuyển vé |
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

### Movies
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/movies` | Lấy danh sách phim |
| GET | `/api/movies/{id}` | Chi tiết phim |

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
| GET/POST/PUT/DELETE | `/api/admin/rooms` | Quản lý phòng |
| GET/POST/PUT/DELETE | `/api/admin/showtimes` | Quản lý suất chiếu |
| GET/POST/PUT/DELETE | `/api/admin/food/categories` | Quản lý danh mục |
| GET/POST/PUT/DELETE | `/api/admin/food/items` | Quản lý sản phẩm |
| GET | `/api/admin/stats/revenue` | Thống kê doanh thu |

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

---

## 📝 Ghi chú kỹ thuật

- **Transaction**: Các thao tác tạo vé và thanh toán đều chạy trong transaction để đảm bảo tính toàn vẹn dữ liệu
- **JWT**: Sử dụng JWT cho xác thực người dùng, token lưu trong localStorage
- **CORS**: Cấu hình CORS cho phép frontend gọi API từ các domain khác nhau
- **Security**: Phân quyền user/admin thông qua Spring Security

---

## 📞 Thông tin liên hệ

- **Tác giả**: Đỗ Việt Hoàng
- **Email**: aboys16t@gmail.com
- **GitHub**: https://github.com/your-username/web_datvexemphim

---

## 📄 License

Dự án được tạo cho mục đích học tập và demo.
