# 🧪 API Testing Documentation - DatVeXemPhim (LOCAL)

> **Last updated**: 2026-03-20
> **Base URL**: `http://localhost:9090/api`
> **Frontend**: `http://localhost:5500` (hoặc Vercel)
> **Backend local port**: `9090`

---

## 📋 Mục lục

1. [Cách chạy backend local](#cách-chạy-backend-local)
2. [Authentication APIs](#1-authentication-apis)
3. [User Profile APIs](#2-user-profile-apis)
4. [Movie APIs](#3-movie-apis)
5. [Showtime APIs](#4-showtime-apis)
6. [Seat Map APIs](#5-seat-map-apis)
7. [Booking APIs](#6-booking-apis)
8. [Payment APIs](#7-payment-apis)
9. [Food & Drink APIs](#8-food--drink-apis)
10. [Voucher APIs](#9-voucher-apis)
11. [Movie Review APIs](#10-movie-review-apis)
12. [Admin APIs](#11-admin-apis)

---

## ⚙️ Cách chạy Backend Local

### Bước 1: Tạo Database

Truy cập **MySQL** (Railway hoặc local), tạo database:

```sql
CREATE DATABASE IF NOT EXISTS datvexemphim CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Bước 2: Cấu hình biến môi trường

Tạo file `backend/src/main/resources/application-dev.yml` (hoặc dùng biến môi trường):

```yaml
# Cách 1: Chạy với Railway MySQL (giữ nguyên)
# Không cần sửa gì, backend sẽ dùng biến Railway

# Cách 2: Chạy với MySQL local (XAMPP/MAMP)
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/datvexemphim?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh
    username: root
    password: ""
```

### Bước 3: Build & Run

```bash
cd backend

# Build
mvn clean package -DskipTests

# Chạy
mvn spring-boot:run

# Hoặc chạy file JAR đã build
java -jar target/datvexemphim-0.0.1-SNAPSHOT.jar
```

Backend sẽ chạy tại: **`http://localhost:9090`**

### Bước 4: Kiểm tra

```bash
curl http://localhost:9090/api/movies
```

**Expected**: `200 OK` → Array movies (hoặc empty `[]` nếu chưa có data)

> **Lưu ý**: DataSeeder sẽ tự động tạo dữ liệu mẫu (movies, rooms, seats, admin account) khi chạy lần đầu.

---

## 🔐 Cách lấy Token

### Đăng nhập để lấy Token

```bash
curl -X POST "http://localhost:9090/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@local", "password": "123456"}'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzM4NCJ9...",
  "tokenType": "Bearer",
  "email": "admin@local",
  "fullName": "Admin User",
  "role": "ADMIN"
}
```

### Sử dụng Token trong requests

Thêm header:
```
Authorization: Bearer <accessToken>
```

---

## 1. Authentication APIs

### 1.1 POST `/api/auth/register` - Đăng ký tài khoản

```bash
curl -X POST "http://localhost:9090/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "123456",
    "fullName": "Nguyễn Văn Test"
  }'
```

**Success Response (201):**
```json
{
  "accessToken": "eyJhbGciOiJIUzM4NCJ9...",
  "tokenType": "Bearer",
  "email": "testuser@example.com",
  "fullName": "Nguyễn Văn Test",
  "role": "USER"
}
```

**Error Response (400 - Email exists):**
```json
{
  "error": "Email already registered"
}
```

---

### 1.2 POST `/api/auth/login` - Đăng nhập

```bash
curl -X POST "http://localhost:9090/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@local",
    "password": "123456"
  }'
```

**Success Response (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzM4NCJ9...",
  "tokenType": "Bearer",
  "email": "admin@local",
  "fullName": "Admin User",
  "role": "ADMIN"
}
```

**Error Response (401):**
```json
{
  "error": "Invalid email or password"
}
```

---

### 1.3 POST `/api/auth/forgot-password` - Quên mật khẩu

**Request (Tài khoản thường):**
```bash
curl -X POST "http://localhost:9090/api/auth/forgot-password" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@local",
    "fullName": "Admin User"
  }'
```

**Request (Tài khoản Google):**
```bash
curl -X POST "http://localhost:9090/api/auth/forgot-password" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "googleuser@gmail.com"
  }'
```

**Success Response (200):**
```json
{
  "message": "Password reset link sent. Check console for the link."
}
```

**Console Log:**
```
===========================================
PASSWORD RESET LINK (Check Console)
===========================================
Email: admin@local
Token: abc123...
Reset URL: http://localhost:5500/reset-password.html?token=abc123...
===========================================
```

---

### 1.4 POST `/api/auth/reset-password` - Đặt lại mật khẩu (Tài khoản thường)

```bash
curl -X POST "http://localhost:9090/api/auth/reset-password" \
  -H "Content-Type: application/json" \
  -d '{
    "token": "abc123...",
    "newPassword": "newpassword123"
  }'
```

**Success Response (200):**
```json
{
  "message": "Password reset successful"
}
```

---

## 2. User Profile APIs

### 2.1 GET `/api/user/profile` - Lấy thông tin hồ sơ

```bash
curl -X GET "http://localhost:9090/api/user/profile" \
  -H "Authorization: Bearer <token>"
```

**Success Response (200):**
```json
{
  "id": 1,
  "email": "admin@local",
  "fullName": "Admin User",
  "phone": "0123456789",
  "avatarUrl": "https://example.com/avatar.jpg",
  "role": "ADMIN",
  "authProvider": "LOCAL",
  "createdAt": "2026-03-01T10:00:00"
}
```

**Error Response (401):**
```json
{
  "error": "Unauthorized"
}
```

---

### 2.2 PUT `/api/user/profile` - Cập nhật hồ sơ

```bash
curl -X PUT "http://localhost:9090/api/user/profile" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Admin Mới",
    "phone": "0987654321",
    "avatarUrl": "https://example.com/new-avatar.jpg"
  }'
```

**Success Response (200):**
```json
{
  "id": 1,
  "email": "admin@local",
  "fullName": "Admin Mới",
  "phone": "0987654321",
  "avatarUrl": "https://example.com/new-avatar.jpg",
  "role": "ADMIN"
}
```

---

### 2.3 POST `/api/user/change-password` - Đổi mật khẩu

```bash
curl -X POST "http://localhost:9090/api/user/change-password" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "oldpassword",
    "newPassword": "newpassword123",
    "confirmPassword": "newpassword123"
  }'
```

**Success Response (200):**
```json
{
  "message": "Đổi mật khẩu thành công"
}
```

**Error Response (400 - Wrong current password):**
```json
{
  "error": "Current password is incorrect"
}
```

---

## 3. Movie APIs

### 3.1 GET `/api/movies` - Danh sách phim đang chiếu

```bash
curl -X GET "http://localhost:9090/api/movies"
```

**Success Response (200):**
```json
[
  {
    "id": 1,
    "title": "Avengers: Endgame",
    "durationMinutes": 181,
    "posterUrl": "https://example.com/poster.jpg",
    "rating": "PG-13",
    "genre": "Hành động",
    "description": "Phim siêu anh hùng...",
    "isActive": true
  },
  {
    "id": 2,
    "title": "Inception",
    "durationMinutes": 148,
    "posterUrl": "https://example.com/inception.jpg",
    "rating": "PG-13",
    "genre": "Khoa học viễn tưởng",
    "description": "Phim về giấc mơ...",
    "isActive": true
  }
]
```

---

### 3.2 GET `/api/movies/{id}` - Chi tiết phim

```bash
curl -X GET "http://localhost:9090/api/movies/1"
```

**Success Response (200):**
```json
{
  "id": 1,
  "title": "Avengers: Endgame",
  "durationMinutes": 181,
  "posterUrl": "https://example.com/poster.jpg",
  "rating": "PG-13",
  "genre": "Hành động",
  "genreId": 1,
  "description": "Phim siêu anh hùng...",
  "trailerUrl": "https://youtube.com/...",
  "isActive": true,
  "avgRating": 4.5,
  "reviewCount": 120
}
```

---

### 3.3 GET `/api/movies/genres` - Danh sách thể loại

```bash
curl -X GET "http://localhost:9090/api/movies/genres"
```

**Success Response (200):**
```json
[
  {
    "id": 1,
    "name": "Hành động",
    "description": "Phim hành động",
    "isActive": true
  },
  {
    "id": 2,
    "name": "Tình cảm",
    "description": "Phim tình cảm",
    "isActive": true
  },
  {
    "id": 3,
    "name": "Khoa học viễn tưởng",
    "description": "Phim sci-fi",
    "isActive": true
  }
]
```

---

### 3.4 GET `/api/movies/past` - Phim đã chiếu (hết lịch)

```bash
curl -X GET "http://localhost:9090/api/movies/past"
```

**Success Response (200):**
```json
[
  {
    "id": 5,
    "title": "Old Movie",
    "durationMinutes": 120,
    "posterUrl": "https://example.com/old.jpg",
    "rating": "PG",
    "genre": "Hài hước",
    "isActive": false
  }
]
```

---

## 4. Showtime APIs

### 4.1 GET `/api/showtimes` - Tất cả suất chiếu

```bash
curl -X GET "http://localhost:9090/api/showtimes"
```

**Success Response (200):**
```json
[
  {
    "id": 1,
    "movieId": 1,
    "movieTitle": "Avengers: Endgame",
    "roomId": 1,
    "roomName": "Phòng 1",
    "posterUrl": "https://example.com/poster.jpg",
    "genre": "Hành động",
    "startTime": "2026-03-20T14:00:00Z",
    "endTime": "2026-03-20T17:01:00Z",
    "price": 80000,
    "availableSeats": 45,
    "totalSeats": 50
  }
]
```

---

### 4.2 GET `/api/showtimes/movie/{movieId}` - Suất chiếu theo phim

```bash
curl -X GET "http://localhost:9090/api/showtimes/movie/1"
```

**Success Response (200):**
```json
[
  {
    "id": 1,
    "movieId": 1,
    "movieTitle": "Avengers: Endgame",
    "roomName": "Phòng 1",
    "startTime": "2026-03-20T14:00:00Z",
    "price": 80000,
    "availableSeats": 45
  },
  {
    "id": 2,
    "movieId": 1,
    "movieTitle": "Avengers: Endgame",
    "roomName": "Phòng 2",
    "startTime": "2026-03-20T18:00:00Z",
    "price": 100000,
    "availableSeats": 50
  }
]
```

---

## 5. Seat Map APIs

### 5.1 GET `/api/seatmap/{showtimeId}` - Sơ đồ ghế

```bash
curl -X GET "http://localhost:9090/api/seatmap/1"
```

**Success Response (200):**
```json
{
  "showtimeId": 1,
  "roomName": "Phòng 1",
  "totalRows": 5,
  "totalCols": 10,
  "seats": [
    {
      "seatId": 1,
      "seatCode": "A1",
      "row": "A",
      "col": 1,
      "seatType": "STANDARD",
      "price": 80000,
      "booked": false
    },
    {
      "seatId": 2,
      "seatCode": "A2",
      "row": "A",
      "col": 2,
      "seatType": "STANDARD",
      "price": 80000,
      "booked": true
    }
  ]
}
```

---

## 6. Booking APIs

### 6.1 POST `/api/bookings` - Đặt vé

```bash
curl -X POST "http://localhost:9090/api/bookings" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "showtimeId": 1,
    "seatIds": [1, 2]
  }'
```

**Success Response (201):**
```json
{
  "ticketIds": [10, 11],
  "showtimeId": 1,
  "totalAmount": 160000,
  "seats": [
    {
      "seatId": 1,
      "seatCode": "A1",
      "price": 80000
    },
    {
      "seatId": 2,
      "seatCode": "A2",
      "price": 80000
    }
  ]
}
```

**Error Response (409 - Ghế đã đặt):**
```json
{
  "error": "Ghế A2 đã được đặt"
}
```

**Error Response (400 - Chưa chọn ghế):**
```json
{
  "error": "Vui lòng chọn ít nhất 1 ghế"
}
```

---

### 6.2 GET `/api/tickets/me` - Lịch sử vé của tôi

```bash
curl -X GET "http://localhost:9090/api/tickets/me" \
  -H "Authorization: Bearer <token>"
```

**Success Response (200):**
```json
[
  {
    "ticketId": 10,
    "status": "CONFIRMED",
    "bookedAt": "2026-03-20T10:00:00Z",
    "cancelledAt": null,
    "showtimeId": 1,
    "startTime": "2026-03-20T14:00:00Z",
    "movieTitle": "Avengers: Endgame",
    "roomName": "Phòng 1",
    "seatCode": "A1",
    "price": 80000,
    "bookingCode": "BK-ABC123DEF4",
    "foodOrderId": 5,
    "foodTotalPrice": 150000,
    "foodOrderStatus": "CONFIRMED",
    "foodItems": [
      {
        "name": "Bắp rang bơ lớn",
        "quantity": 2,
        "priceAtOrder": 50000,
        "subtotal": 100000
      },
      {
        "name": "Coca lớn",
        "quantity": 1,
        "priceAtOrder": 50000,
        "subtotal": 50000
      }
    ]
  }
]
```

---

### 6.3 POST `/api/tickets/{id}/cancel` - Hủy vé

```bash
curl -X POST "http://localhost:9090/api/tickets/10/cancel" \
  -H "Authorization: Bearer <token>"
```

**Success Response (200):**
```json
{
  "message": "Hủy vé thành công"
}
```

**Error Response (400 - Quá thời hạn):**
```json
{
  "error": "Chỉ có thể hủy vé trước giờ chiếu 30 phút"
}
```

---

### 6.4 POST `/api/tickets/transfer` - Chuyển vé

```bash
curl -X POST "http://localhost:9090/api/tickets/transfer" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketId": 10,
    "toEmail": "friend@example.com"
  }'
```

**Success Response (200):**
```json
{
  "message": "Chuyển vé thành công"
}
```

**Error Response (404 - Email không tồn tại):**
```json
{
  "error": "User nhận vé không tồn tại"
}
```

---

## 7. Payment APIs

### 7.1 POST `/api/payments/simulate` - Thanh toán mô phỏng

**Request (Thành công):**
```bash
curl -X POST "http://localhost:9090/api/payments/simulate" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketIds": [10, 11],
    "success": true
  }'
```

**Success Response (200):**
```json
{
  "paymentId": 5,
  "bookingCode": "BK-XYZ789ABC0",
  "status": "SUCCESS",
  "ticketAmount": 160000
}
```

**Request (Thất bại):**
```bash
curl -X POST "http://localhost:9090/api/payments/simulate" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketIds": [10, 11],
    "success": false
  }'
```

**Response (200):**
```json
{
  "paymentId": 6,
  "bookingCode": "BK-FAILED123",
  "status": "FAILED",
  "ticketAmount": 160000
}
```

---

### 7.2 POST `/api/payments/vnpay/create` - Tạo URL thanh toán VNPay

```bash
curl -X POST "http://localhost:9090/api/payments/vnpay/create" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 160000,
    "ticketIds": "10,11"
  }'
```

**Success Response (200):**
```json
{
  "paymentUrl": "https://sandbox.vnpayment.vn/...",
  "paymentId": 7
}
```

---

## 8. Food & Drink APIs

### 8.1 GET `/api/food/categories` - Danh mục đồ ăn

```bash
curl -X GET "http://localhost:9090/api/food/categories"
```

**Success Response (200):**
```json
[
  {
    "id": 1,
    "name": "Bắp rang",
    "icon": "fa-coins",
    "isActive": true
  },
  {
    "id": 2,
    "name": "Nước uống",
    "icon": "fa-wine-bottle",
    "isActive": true
  },
  {
    "id": 3,
    "name": "Combo",
    "icon": "fa-box",
    "isActive": true
  }
]
```

---

### 8.2 GET `/api/food/items/category/{categoryId}` - Sản phẩm theo danh mục

```bash
curl -X GET "http://localhost:9090/api/food/items/category/1"
```

**Success Response (200):**
```json
[
  {
    "id": 1,
    "name": "Bắp rang bơ nhỏ",
    "price": 35000,
    "imageUrl": "https://example.com/popcorn-s.jpg",
    "categoryId": 1,
    "isAvailable": true
  },
  {
    "id": 2,
    "name": "Bắp rang bơ lớn",
    "price": 50000,
    "imageUrl": "https://example.com/popcorn-l.jpg",
    "categoryId": 1,
    "isAvailable": true
  }
]
```

---

### 8.3 POST `/api/food/orders` - Đặt đồ ăn

```bash
curl -X POST "http://localhost:9090/api/food/orders" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketId": 10,
    "paymentId": 5,
    "items": [
      {"foodItemId": 1, "quantity": 2},
      {"foodItemId": 5, "quantity": 1}
    ]
  }'
```

**Success Response (201):**
```json
{
  "id": 5,
  "ticketId": 10,
  "paymentId": 5,
  "totalPrice": 120000,
  "foodOrderStatus": "PENDING",
  "items": [
    {
      "id": 1,
      "foodItemId": 1,
      "foodItemName": "Bắp rang bơ nhỏ",
      "quantity": 2,
      "priceAtOrder": 35000,
      "subtotal": 70000
    },
    {
      "id": 2,
      "foodItemId": 5,
      "foodItemName": "Coca lớn",
      "quantity": 1,
      "priceAtOrder": 50000,
      "subtotal": 50000
    }
  ]
}
```

---

## 9. Voucher APIs

### 9.1 POST `/api/vouchers/validate` - Kiểm tra & áp dụng voucher

```bash
curl -X POST "http://localhost:9090/api/vouchers/validate" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "voucherCode": "SUMMER2024",
    "orderAmount": 300000
  }'
```

**Success Response (200):**
```json
{
  "valid": true,
  "code": "SUMMER2024",
  "discountAmount": 30000,
  "description": "Giảm 10% tối đa 50.000đ"
}
```

**Error Response (400 - Không hợp lệ):**
```json
{
  "valid": false,
  "error": "Mã voucher không tồn tại"
}
```

**Error Response (400 - Đã hết hạn):**
```json
{
  "valid": false,
  "error": "Mã voucher đã hết hạn"
}
```

**Error Response (400 - Đã sử dụng hết):**
```json
{
  "valid": false,
  "error": "Mã voucher đã hết lượt sử dụng"
}
```

**Error Response (400 - Đơn hàng chưa đủ):**
```json
{
  "valid": false,
  "error": "Đơn hàng tối thiểu 200.000đ để sử dụng mã này"
}
```

---

## 10. Movie Review APIs

### 10.1 GET `/api/movies/{id}/reviews` - Lấy đánh giá của phim

```bash
curl -X GET "http://localhost:9090/api/movies/1/reviews"
```

**Success Response (200):**
```json
[
  {
    "id": 1,
    "movieId": 1,
    "userId": 2,
    "userFullName": "Nguyễn Văn Test",
    "rating": 5,
    "comment": "Phim quá hay!",
    "createdAt": "2026-03-19T15:00:00Z"
  }
]
```

---

### 10.2 GET `/api/movies/{id}/reviews/stats` - Thống kê rating

```bash
curl -X GET "http://localhost:9090/api/movies/1/reviews/stats"
```

**Success Response (200):**
```json
{
  "movieId": 1,
  "avgRating": 4.5,
  "totalReviews": 120,
  "distribution": {
    "5": 60,
    "4": 30,
    "3": 20,
    "2": 5,
    "1": 5
  }
}
```

---

### 10.3 POST `/api/movies/reviews` - Gửi đánh giá

```bash
curl -X POST "http://localhost:9090/api/movies/reviews" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "movieId": 1,
    "rating": 5,
    "comment": "Phim quá hay! Khuyến khích xem!"
  }'
```

**Success Response (201):**
```json
{
  "id": 2,
  "movieId": 1,
  "userId": 1,
  "userFullName": "Admin User",
  "rating": 5,
  "comment": "Phim quá hay! Khuyến khích xem!",
  "createdAt": "2026-03-20T16:00:00Z"
}
```

**Error Response (400 - Đã đánh giá):**
```json
{
  "error": "Bạn đã đánh giá phim này rồi"
}
```

---

### 10.4 DELETE `/api/movies/reviews/{id}` - Xóa đánh giá

```bash
curl -X DELETE "http://localhost:9090/api/movies/reviews/2" \
  -H "Authorization: Bearer <token>"
```

**Success Response (200):**
```json
{
  "message": "Xóa đánh giá thành công"
}
```

---

## 11. Admin APIs

> **Lưu ý**: Tất cả Admin APIs đều cần header `Authorization: Bearer <admin_token>`

### 11.1 Movies

#### GET `/api/admin/movies` - Danh sách phim (Admin)
```bash
curl -X GET "http://localhost:9090/api/admin/movies" \
  -H "Authorization: Bearer <admin_token>"
```

#### POST `/api/admin/movies` - Tạo phim mới
```bash
curl -X POST "http://localhost:9090/api/admin/movies" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Phim mới",
    "durationMinutes": 120,
    "posterUrl": "https://example.com/poster.jpg",
    "rating": "PG-13",
    "genreId": 1,
    "description": "Mô tả phim",
    "trailerUrl": "https://youtube.com/...",
    "isActive": true
  }'
```

#### PUT `/api/admin/movies/{id}` - Cập nhật phim
```bash
curl -X PUT "http://localhost:9090/api/admin/movies/1" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Phim đã cập nhật",
    "durationMinutes": 130,
    "genreId": 2
  }'
```

#### DELETE `/api/admin/movies/{id}` - Xóa phim
```bash
curl -X DELETE "http://localhost:9090/api/admin/movies/1" \
  -H "Authorization: Bearer <admin_token>"
```

---

### 11.2 Rooms

#### GET `/api/admin/rooms` - Danh sách phòng
```bash
curl -X GET "http://localhost:9090/api/admin/rooms" \
  -H "Authorization: Bearer <admin_token>"
```

#### POST `/api/admin/rooms` - Tạo phòng mới
```bash
curl -X POST "http://localhost:9090/api/admin/rooms" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Phòng 3",
    "rows": 6,
    "cols": 12
  }'
```

---

### 11.3 Showtimes

#### GET `/api/admin/showtimes` - Danh sách suất chiếu
```bash
curl -X GET "http://localhost:9090/api/admin/showtimes" \
  -H "Authorization: Bearer <admin_token>"
```

#### POST `/api/admin/showtimes` - Tạo suất chiếu
```bash
curl -X POST "http://localhost:9090/api/admin/showtimes" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "movieId": 1,
    "roomId": 1,
    "startTime": "2026-03-25T14:00:00Z",
    "price": 80000
  }'
```

---

### 11.4 Vouchers

#### GET `/api/admin/vouchers` - Danh sách voucher
```bash
curl -X GET "http://localhost:9090/api/admin/vouchers" \
  -H "Authorization: Bearer <admin_token>"
```

#### POST `/api/admin/vouchers` - Tạo voucher
```bash
curl -X POST "http://localhost:9090/api/admin/vouchers" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "SUMMER2024",
    "description": "Giảm 10% tối đa 50.000đ",
    "discountPercent": 10,
    "maxDiscount": 50000,
    "minOrderAmount": 200000,
    "validFrom": "2026-03-01",
    "validUntil": "2026-12-31",
    "usageLimit": 100,
    "isActive": true
  }'
```

#### PUT `/api/admin/vouchers/{id}` - Cập nhật voucher
```bash
curl -X PUT "http://localhost:9090/api/admin/vouchers/1" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "usageLimit": 200,
    "isActive": true
  }'
```

#### DELETE `/api/admin/vouchers/{id}` - Xóa voucher
```bash
curl -X DELETE "http://localhost:9090/api/admin/vouchers/1" \
  -H "Authorization: Bearer <admin_token>"
```

---

### 11.5 Food Categories

#### GET `/api/admin/food/categories` - Danh sách danh mục
```bash
curl -X GET "http://localhost:9090/api/admin/food/categories" \
  -H "Authorization: Bearer <admin_token>"
```

#### POST `/api/admin/food/categories` - Tạo danh mục
```bash
curl -X POST "http://localhost:9090/api/admin/food/categories" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bánh",
    "icon": "fa-cookie"
  }'
```

---

### 11.6 Food Items

#### GET `/api/admin/food/items` - Danh sách sản phẩm
```bash
curl -X GET "http://localhost:9090/api/admin/food/items" \
  -H "Authorization: Bearer <admin_token>"
```

#### POST `/api/admin/food/items` - Tạo sản phẩm
```bash
curl -X POST "http://localhost:9090/api/admin/food/items" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bánh Pizza nhỏ",
    "price": 45000,
    "categoryId": 4,
    "imageUrl": "https://example.com/pizza.jpg",
    "isAvailable": true
  }'
```

---

### 11.7 Statistics

#### GET `/api/admin/stats/revenue` - Thống kê doanh thu
```bash
curl -X GET "http://localhost:9090/api/admin/stats/revenue" \
  -H "Authorization: Bearer <admin_token>"
```

**Success Response (200):**
```json
{
  "totalRevenue": 5000000,
  "ticketCount": 62,
  "foodOrderCount": 25,
  "totalFoodRevenue": 1250000,
  "recentPayments": [
    {
      "id": 1,
      "bookingCode": "BK-ABC123",
      "amount": 160000,
      "status": "SUCCESS",
      "paidAt": "2026-03-20T10:00:00Z"
    }
  ]
}
```

---

## 🧪 Test Checklist

### ✅ Authentication
- [ ] POST `/api/auth/register` - Đăng ký thành công
- [ ] POST `/api/auth/register` - Đăng ký email trùng (lỗi)
- [ ] POST `/api/auth/login` - Đăng nhập thành công
- [ ] POST `/api/auth/login` - Sai mật khẩu (lỗi)
- [ ] POST `/api/auth/forgot-password` - Gửi reset password

### ✅ User Profile
- [ ] GET `/api/user/profile` - Lấy thông tin (cần token)
- [ ] PUT `/api/user/profile` - Cập nhật thông tin
- [ ] POST `/api/user/change-password` - Đổi mật khẩu thành công
- [ ] POST `/api/user/change-password` - Sai mật khẩu cũ (lỗi)

### ✅ Movies
- [ ] GET `/api/movies` - Danh sách phim
- [ ] GET `/api/movies/{id}` - Chi tiết phim
- [ ] GET `/api/movies/genres` - Danh sách thể loại
- [ ] GET `/api/movies/past` - Phim đã chiếu

### ✅ Showtimes
- [ ] GET `/api/showtimes` - Tất cả suất chiếu
- [ ] GET `/api/showtimes/movie/{id}` - Suất chiếu theo phim

### ✅ Seat Map
- [ ] GET `/api/seatmap/{id}` - Sơ đồ ghế (có ghế đã đặt và trống)

### ✅ Booking
- [ ] POST `/api/bookings` - Đặt vé thành công
- [ ] POST `/api/bookings` - Đặt ghế đã đặt (lỗi 409)
- [ ] GET `/api/tickets/me` - Lịch sử vé (cần token)
- [ ] POST `/api/tickets/{id}/cancel` - Hủy vé thành công
- [ ] POST `/api/tickets/transfer` - Chuyển vé

### ✅ Payment
- [ ] POST `/api/payments/simulate` - Thanh toán thành công
- [ ] POST `/api/payments/simulate` - Thanh toán thất bại
- [ ] POST `/api/payments/vnpay/create` - Tạo URL VNPay

### ✅ Food
- [ ] GET `/api/food/categories` - Danh mục đồ ăn
- [ ] GET `/api/food/items/category/{id}` - Sản phẩm theo danh mục
- [ ] POST `/api/food/orders` - Đặt đồ ăn (cần token)

### ✅ Voucher
- [ ] POST `/api/vouchers/validate` - Voucher hợp lệ
- [ ] POST `/api/vouchers/validate` - Voucher không tồn tại (lỗi)
- [ ] POST `/api/vouchers/validate` - Voucher hết hạn (lỗi)

### ✅ Movie Reviews
- [ ] GET `/api/movies/{id}/reviews` - Danh sách đánh giá
- [ ] GET `/api/movies/{id}/reviews/stats` - Thống kê rating
- [ ] POST `/api/movies/reviews` - Gửi đánh giá (cần token)
- [ ] POST `/api/movies/reviews` - Đánh giá trùng (lỗi)
- [ ] DELETE `/api/movies/reviews/{id}` - Xóa đánh giá

---

## 📝 Ghi chú

1. **Token Expiration**: JWT token hết hạn sau 24 giờ
2. **Admin Role**: Chỉ role ADMIN mới truy cập được Admin APIs
3. **Password Reset Token**: Hết hạn sau 15 phút
4. **Booking Conflict**: Không thể đặt ghế đã có ticket PENDING/CONFIRMED
5. **Cancel Policy**: Chỉ hủy được trước giờ chiếu 30 phút
6. **Data Seeder**: Chạy lần đầu sẽ tự tạo movies, rooms, seats, admin account

---

## 🔧 Troubleshooting

### Lỗi 401 Unauthorized
- Kiểm tra token đã được thêm vào header chưa
- Token có thể đã hết hạn, cần đăng nhập lại

### Lỗi 403 Forbidden
- Cần quyền ADMIN để truy cập endpoint này

### Lỗi 409 Conflict
- Ghế đã được đặt bởi người khác
- Refresh trang để lấy danh sách ghế mới nhất

### Lỗi 400 Bad Request
- Dữ liệu gửi lên không hợp lệ
- Kiểm tra format JSON và các trường bắt buộc

### Lỗi kết nối Database
```
Unable to acquire JDBC Connection
```
- Kiểm tra MySQL đang chạy chưa
- Kiểm tra `spring.datasource.url` trong `application.yml`
- Nếu dùng Railway: kiểm tra biến môi trường `MYSQLHOST`, `MYSQLPORT`

### Cổng 9090 đã bị chiếm
```
Port 9090 was already in use
```
```bash
# Windows
netstat -ano | findstr :9090
taskkill /PID <PID> /F

# Hoặc đổi port trong application.yml:
server:
  port: 9091
```

---

## 📂 So sánh Local vs Railway

| Mục | Local | Railway |
|-----|-------|---------|
| **Base URL** | `http://localhost:9090/api` | `https://web-datvexemphim-production.up.railway.app/api` |
| **Database** | MySQL local (XAMPP) | Railway MySQL |
| **CORS** | `http://localhost:5500` | `https://web-datvexemphim.vercel.app` |
| **VNPay** | Sandbox (hoạt động bình thường) | Sandbox (hoạt động bình thường) |
| **JWT Secret** | Giá trị mặc định (nên đổi) | Railway env var |
| **Deploy** | Không cần | Auto từ GitHub |

---

*Document created: 2026-03-20*
*Last updated: 2026-03-20*
