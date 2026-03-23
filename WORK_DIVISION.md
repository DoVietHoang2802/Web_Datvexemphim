# 👥 Phân Chia Công Việc - DatVeXemPhim

> **Dự án**: Website Đặt Vé Xem Phim Trực Tuyến
> **Team size**: 5 người | **Base URL API**: `https://web-datvexemphim-production.up.railway.app/api`
> **Last updated**: 2026-03-20

---

## 📋 Mục lục

1. [Tổng quan phân công](#tổng-quan-phân-công)
2. [Member 1 - Xác thực & Quản lý người dùng](#member-1---xác-thực--quản-lý-người-dùng)
3. [Member 2 - Phim & Lịch chiếu](#member-2---phim--lịch-chiếu)
4. [Member 3 - Đặt vé & Sơ đồ ghế](#member-3---đặt-vé--sơ-đồ-ghế)
5. [Member 4 - Thanh toán & Đồ ăn](#member-4---thanh-toán--đồ-ăn)
6. [Member 5 - Admin Dashboard & Hạ tầng](#member-5---admin-dashboard--hạ-tầng)

---

## Tổng quan phân công

| # | Thành viên | Phạm vi |
|---|-----------|---------|
| 1 | **Xác thực & Người dùng** | Login, Register, Forgot Password, Profile, Admin Users |
| 2 | **Phim & Lịch chiếu** | Movies, Genres, Showtimes, Reviews |
| 3 | **Đặt vé & Ghế** | Booking, SeatMap, Rooms |
| 4 | **Thanh toán & Đồ ăn** | Payment, VNPay, Food, Voucher |
| 5 | **Admin Dashboard & Hạ tầng** | Dashboard stats, Upload, CSS, JS, Config |

---

## MEMBER 1 - Xác thực & Quản lý Người dùng

### Mô tả
Xây dựng hệ thống đăng nhập/đăng ký (local + Google OAuth), quên mật khẩu, hồ sơ người dùng và quản lý tài khoản admin.

### Backend Files

```
backend/src/main/java/com/datvexemphim/
├── api/controller/
│   ├── AuthController.java          ⭐ Đăng ký / Đăng nhập / Google OAuth
│   └── PasswordResetController.java  ⭐ Quên mật khẩu
│   └── UserController.java          ⭐ Hồ sơ người dùng
├── service/
│   ├── AuthService.java             ⭐ Logic đăng ký, login, Google upsert
│   └── UserProfileService.java      ⭐ Cập nhật hồ sơ, đổi mật khẩu
├── security/
│   ├── JwtService.java              ⭐ Tạo & verify JWT token
│   └── JwtAuthFilter.java            ⭐ Filter xác thực JWT
│   └── CustomUserDetailsService.java ⭐ Load user chi tiết cho Security
├── domain/entity/
│   ├── User.java                     ⭐ Entity User (fullName, email, password, role, provider)
│   └── PasswordResetToken.java       ⭐ Entity token reset mật khẩu
├── domain/repository/
│   └── UserRepository.java           ⭐ JPA queries cho User
└── api/dto/
    ├── auth/
    │   ├── AuthResponse.java         ⭐ Token response
    │   ├── LoginRequest.java
    │   ├── RegisterRequest.java
    │   ├── GoogleLoginRequest.java
    │   └── ResetPasswordByInfoRequest.java
    └── user/
        ├── UserProfileDto.java       ⭐ Trả về thông tin hồ sơ
        ├── UpdateProfileRequest.java ⭐ Cập nhật hồ sơ
        └── ChangePasswordRequest.java ⭐ Đổi mật khẩu
```

### Frontend Files

```
frontend/
├── login.html           ⭐ Trang đăng nhập (email/password + Google)
├── register.html        ⭐ Trang đăng ký
├── forgot-password.html ⭐ Trang quên mật khẩu
├── reset-password.html  ⭐ Trang reset mật khẩu (từ email link)
├── profile.html         ⭐ Trang hồ sơ cá nhân
└── admin/
    └── users.html       ⭐ Admin: quản lý tài khoản (đổi role, xóa, reset password)
```

### API Test Commands (Postman)

#### 1.1 - Đăng ký tài khoản mới
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@local",
    "password": "123456",
    "fullName": "Nguyen Van A"
  }'
```
**Expected**: `201 Created` → `{ "accessToken": "...", "email": "test@local", "role": "USER" }`

#### 1.2 - Đăng nhập
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@local",
    "password": "123456"
  }'
```
**Expected**: `200 OK` → `{ "accessToken": "eyJ...", "role": "ADMIN", "fullName": "Admin User" }`

#### 1.3 - Lấy thông tin hồ sơ (cần token)
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/user/profile" \
  -H "Authorization: Bearer <TOKEN>"
```
**Expected**: `200 OK` → `{ "id": 1, "email": "...", "fullName": "...", "phone": "..." }`

#### 1.4 - Cập nhật hồ sơ
```bash
curl -X PUT "https://web-datvexemphim-production.up.railway.app/api/user/profile" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Nguyen Van B",
    "phone": "0912345678"
  }'
```
**Expected**: `200 OK` → `{ "message": "Cập nhật hồ sơ thành công" }`

#### 1.5 - Đổi mật khẩu
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/user/change-password" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "123456",
    "newPassword": "654321"
  }'
```
**Expected**: `200 OK` → `{ "message": "Đổi mật khẩu thành công" }`

#### 1.6 - Quên mật khẩu (tài khoản LOCAL - cần email + fullName)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/auth/reset-password" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@local",
    "fullName": "Admin User"
  }'
```
**Expected**: `200 OK` → Token reset được in ra console backend

#### 1.7 - Lấy danh sách users (Admin)
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/admin/users" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```
**Expected**: `200 OK` → Array users

#### 1.8 - Xóa user (Admin)
```bash
curl -X DELETE "https://web-datvexemphim-production.up.railway.app/api/admin/users/2" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```
**Expected**: `200 OK`

---

## MEMBER 2 - Phim & Lịch chiếu

### Mô tả
Quản lý phim (CRUD), thể loại phim, suất chiếu, đánh giá phim và giao diện hiển thị cho người dùng.

### Backend Files

```
backend/src/main/java/com/datvexemphim/
├── api/controller/
│   ├── MovieController.java          ⭐ API lấy danh sách phim, chi tiết, thể loại
│   ├── MovieReviewController.java    ⭐ API đánh giá phim (1-5 sao + bình luận)
│   └── admin/
│       └── AdminMovieController.java  ⭐ CRUD phim, thể loại (Admin)
├── service/
│   ├── MovieService.java             ⭐ Logic lấy phim, lọc phim đang chiếu
│   └── MovieReviewService.java       ⭐ Logic đánh giá, tính rating trung bình
├── domain/entity/
│   ├── Movie.java                    ⭐ Entity Movie (title, duration, poster, genreId...)
│   └── MovieReview.java              ⭐ Entity đánh giá (movie, user, rating, comment)
├── domain/repository/
│   ├── MovieRepository.java
│   └── MovieReviewRepository.java
└── api/dto/
    ├── publicapi/
    │   ├── MovieSummaryDto.java      ⭐ DTO danh sách phim (summary)
    │   ├── MovieDetailDto.java       ⭐ DTO chi tiết phim
    │   ├── MovieReviewDto.java       ⭐ DTO đánh giá
    │   └── CreateReviewRequest.java  ⭐ Request tạo đánh giá
    └── admin/
        ├── MovieUpsertRequest.java   ⭐ Request tạo/cập nhật phim
        ├── MovieGenreDTO.java        ⭐ DTO thể loại
        └── MovieGenreUpsertRequest.java
```

### Frontend Files

```
frontend/
├── index.html           ⭐ Trang chủ - danh sách phim đang chiếu, lọc thể loại, sắp xếp, tìm kiếm
├── movie.html           ⭐ Chi tiết phim - poster, trailer, đánh giá sao, bình luận, suất chiếu
├── showtimes.html       ⭐ Lịch chiếu - suất hiện có / đã hết, lọc theo ngày & thể loại
└── admin/
    ├── movies.html      ⭐ Admin: CRUD phim, upload poster
    └── genres.html      ⭐ Admin: CRUD thể loại phim
```

### API Test Commands (Postman)

#### 2.1 - Lấy danh sách phim đang chiếu
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/movies"
```
**Expected**: `200 OK` → Array MovieSummaryDto

#### 2.2 - Lấy chi tiết phim
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/movies/1"
```
**Expected**: `200 OK` → MovieDetailDto

#### 2.3 - Lấy danh sách thể loại
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/movies/genres"
```
**Expected**: `200 OK` → Array genres

#### 2.4 - Lấy đánh giá của phim
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/movies/1/reviews"
```
**Expected**: `200 OK` → Array reviews

#### 2.5 - Lấy thống kê rating phim
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/movies/1/reviews/stats"
```
**Expected**: `200 OK` → `{ "averageRating": 4.5, "totalReviews": 10, "distribution": {...} }`

#### 2.6 - Gửi đánh giá phim (cần login)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/movies/reviews" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "movieId": 1,
    "rating": 5,
    "comment": "Phim rất hay!"
  }'
```
**Expected**: `201 Created` → Review object

#### 2.7 - Xóa đánh giá (chủ đánh giá hoặc admin)
```bash
curl -X DELETE "https://web-datvexemphim-production.up.railway.app/api/movies/reviews/1" \
  -H "Authorization: Bearer <TOKEN>"
```
**Expected**: `200 OK`

#### 2.8 - Tạo phim mới (Admin)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/admin/movies" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Avengers: Endgame",
    "description": "Phim siêu anh hùng",
    "durationMinutes": 180,
    "posterUrl": "https://example.com/poster.jpg",
    "trailerUrl": "https://youtube.com/watch?v=xxx",
    "rating": "PG-13",
    "genreId": 1,
    "active": true
  }'
```
**Expected**: `201 Created`

#### 2.9 - Cập nhật phim (Admin)
```bash
curl -X PUT "https://web-datvexemphim-production.up.railway.app/api/admin/movies/1" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Avengers: Endgame - Updated",
    "durationMinutes": 185
  }'
```
**Expected**: `200 OK`

#### 2.10 - Xóa phim (Admin)
```bash
curl -X DELETE "https://web-datvexemphim-production.up.railway.app/api/admin/movies/1" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```
**Expected**: `200 OK`

#### 2.11 - Tạo thể loại phim (Admin)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/admin/movies/genres" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Hành Động",
    "description": "Phim hành động gay cấn",
    "isActive": true
  }'
```
**Expected**: `201 Created`

#### 2.12 - Lấy tất cả thể loại (Admin)
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/admin/movies/genres" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```
**Expected**: `200 OK` → Array genres

---

## MEMBER 3 - Đặt vé & Sơ đồ ghế

### Mô tả
Xây dựng quy trình đặt vé, sơ đồ ghế tương tác, quản lý phòng chiếu và ghế ngồi. Đảm bảo chống đặt trùng ghế.

### Backend Files

```
backend/src/main/java/com/datvexemphim/
├── api/controller/
│   ├── BookingController.java        ⭐ API đặt vé (tạo PENDING tickets)
│   ├── SeatMapController.java        ⭐ API sơ đồ ghế theo suất chiếu
│   ├── TicketController.java         ⭐ API xem vé, hủy vé, chuyển vé
│   └── admin/
│       └── AdminRoomController.java  ⭐ CRUD phòng chiếu, tạo ghế
├── service/
│   ├── BookingService.java           ⭐ Logic đặt vé, validate ghế trống, chống trùng
│   ├── SeatMapService.java          ⭐ Logic lấy sơ đồ ghế
│   └── TicketService.java           ⭐ Logic xem vé, hủy, chuyển
├── domain/entity/
│   ├── Room.java                    ⭐ Entity phòng chiếu
│   ├── Seat.java                    ⭐ Entity ghế (row, col, seatType, available)
│   ├── Showtime.java                ⭐ Entity suất chiếu
│   ├── Ticket.java                  ⭐ Entity vé (PENDING/CONFIRMED/CANCELLED)
│   └── TransferHistory.java         ⭐ Entity lịch sử chuyển vé
├── domain/repository/
│   ├── RoomRepository.java
│   ├── SeatRepository.java
│   ├── ShowtimeRepository.java
│   └── TicketRepository.java        ⭐ Query kiểm tra ghế đã đặt chưa
└── api/dto/
    ├── booking/
    │   └── CreateBookingRequest.java ⭐ Request đặt vé
    ├── publicapi/
    │   ├── SeatMapResponse.java      ⭐ DTO sơ đồ ghế
    │   ├── SeatMapSeatDto.java        ⭐ DTO thông tin từng ghế
    │   └── ShowtimeDto.java          ⭐ DTO suất chiếu
    └── ticket/
        ├── TicketHistoryDto.java     ⭐ DTO lịch sử vé
        └── TransferTicketRequest.java
```

### Frontend Files

```
frontend/
├── seatmap.html        ⭐ Sơ đồ ghế tương tác - grid ghế, legend, chọn ghế → đặt vé
└── admin/
    └── rooms.html      ⭐ Admin: CRUD phòng, tạo ghế tự động, quản lý ghế
```

### API Test Commands (Postman)

#### 3.1 - Lấy danh sách suất chiếu theo phim
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/showtimes/movie/1"
```
**Expected**: `200 OK` → Array ShowtimeDto

#### 3.2 - Lấy tất cả suất chiếu
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/showtimes"
```
**Expected**: `200 OK` → Array ShowtimeDto

#### 3.3 - Lấy sơ đồ ghế theo suất chiếu
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/seatmap/1"
```
**Expected**: `200 OK` → `{ "room": {...}, "seats": [{ "id", "row", "col", "seatType", "available" }] }`

#### 3.4 - Đặt vé (tạo PENDING tickets)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/bookings" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "showtimeId": 1,
    "seatIds": [1, 2]
  }'
```
**Expected**: `201 Created` → `{ "ticketIds": [5, 6], "totalAmount": 200000 }`

#### 3.5 - Lấy lịch sử vé của tôi
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/tickets/me" \
  -H "Authorization: Bearer <TOKEN>"
```
**Expected**: `200 OK` → Array TicketHistoryDto (kèm food order details)

#### 3.6 - Hủy vé
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/tickets/1/cancel" \
  -H "Authorization: Bearer <TOKEN>"
```
**Expected**: `200 OK` → `{ "message": "Hủy vé thành công" }`
**Lưu ý**: Chỉ hủy được khi `now < startTime - 30 phút`

#### 3.7 - Chuyển vé cho người khác
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/tickets/transfer" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketId": 1,
    "toEmail": "friend@example.com"
  }'
```
**Expected**: `200 OK` → `{ "message": "Chuyển vé thành công" }`

#### 3.8 - Tạo phòng chiếu mới (Admin)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/admin/rooms" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Phòng 3",
    "totalRows": 10,
    "totalCols": 12
  }'
```
**Expected**: `201 Created`

#### 3.9 - Tạo ghế tự động cho phòng (Admin)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/admin/rooms/1/seats/generate" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "totalRows": 10,
    "totalCols": 12,
    "seatTypeMap": {
      "A": "VIP",
      "B-C": "COUPLE",
      "D-J": "STANDARD"
    }
  }'
```
**Expected**: `201 Created` → Số ghế đã tạo

#### 3.10 - Cập nhật trạng thái ghế (Admin)
```bash
curl -X PUT "https://web-datvexemphim-production.up.railway.app/api/admin/rooms/1/seats/5" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "active": false
  }'
```
**Expected**: `200 OK`

---

## MEMBER 4 - Thanh toán & Đồ ăn

### Mô tả
Xây dựng hệ thống thanh toán (mô phỏng + VNPay), đặt đồ ăn/nước uống kèm vé và mã giảm giá (voucher).

### Backend Files

```
backend/src/main/java/com/datvexemphim/
├── api/controller/
│   ├── PaymentController.java       ⭐ API thanh toán mô phỏng
│   ├── VnpayController.java          ⭐ API tích hợp VNPay sandbox
│   ├── FoodController.java          ⭐ API danh mục, sản phẩm, đặt đồ ăn
│   ├── VoucherController.java       ⭐ API kiểm tra & áp dụng voucher
│   └── admin/
│       └── AdminFoodController.java  ⭐ CRUD danh mục & sản phẩm đồ ăn
│       └── AdminVoucherController.java ⭐ CRUD voucher (Admin)
├── service/
│   ├── PaymentService.java          ⭐ Xác nhận vé, giải phóng ghế khi thất bại
│   ├── VnpayService.java           ⭐ Build VNPay URL + SHA512 HMAC verify
│   ├── FoodOrderService.java       ⭐ Tạo đơn, hủy đơn, xác nhận khi thanh toán
│   ├── FoodCategoryService.java    ⭐ CRUD danh mục đồ ăn
│   ├── FoodItemService.java        ⭐ CRUD sản phẩm đồ ăn
│   └── VoucherService.java         ⭐ Validate voucher, tính giảm giá
├── domain/entity/
│   ├── Payment.java                ⭐ Entity thanh toán (amount, method, status)
│   ├── Voucher.java                ⭐ Entity voucher (code, discount%, expiry)
│   ├── FoodCategory.java           ⭐ Entity danh mục (name, icon)
│   ├── FoodItem.java               ⭐ Entity sản phẩm (name, price, image)
│   ├── FoodOrder.java              ⭐ Entity đơn đồ ăn (PENDING/CONFIRMED/CANCELLED)
│   └── FoodOrderItem.java          ⭐ Entity chi tiết đơn đồ ăn
├── domain/repository/
│   ├── PaymentRepository.java
│   ├── VoucherRepository.java
│   ├── FoodCategoryRepository.java
│   ├── FoodItemRepository.java
│   ├── FoodOrderRepository.java
│   └── FoodOrderItemRepository.java
└── api/dto/
    ├── payment/
    │   ├── PaymentSimulateRequest.java
    │   └── PaymentSimulateResponse.java
    ├── food/
    │   ├── FoodCategoryDTO.java
    │   ├── FoodItemDTO.java
    │   ├── FoodOrderDTO.java
    │   ├── FoodOrderItemDTO.java
    │   └── CreateFoodOrderRequest.java
    ├── booking/
    │   └── ApplyVoucherRequest.java
    └── admin/
        ├── VoucherDTO.java
        └── VoucherUpsertRequest.java
```

### Frontend Files

```
frontend/
├── checkout.html        ⭐ Trang thanh toán - tổng hợp vé, đồ ăn, nhập voucher, chọn thanh toán
├── food.html            ⭐ Trang đặt đồ ăn - danh mục, grid sản phẩm, giỏ hàng
└── admin/
    ├── food.html        ⭐ Admin: CRUD danh mục & sản phẩm đồ ăn
    └── vouchers.html     ⭐ Admin: CRUD voucher, bật/tắt active, thống kê
```

### API Test Commands (Postman)

#### 4.1 - Lấy danh mục đồ ăn
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/food/categories"
```
**Expected**: `200 OK` → Array FoodCategoryDTO

#### 4.2 - Lấy sản phẩm đồ ăn theo danh mục
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/food/items?categoryId=1"
```
**Expected**: `200 OK` → Array FoodItemDTO

#### 4.3 - Đặt đồ ăn
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/food/orders" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketIds": [1, 2],
    "items": [
      { "foodItemId": 1, "quantity": 2 },
      { "foodItemId": 3, "quantity": 1 }
    ]
  }'
```
**Expected**: `201 Created` → `{ "foodOrderId": 1, "totalPrice": 85000 }`

#### 4.4 - Lấy đơn đồ ăn của tôi
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/food/orders" \
  -H "Authorization: Bearer <TOKEN>"
```
**Expected**: `200 OK` → Array FoodOrderDTO

#### 4.5 - Hủy đơn đồ ăn
```bash
curl -X DELETE "https://web-datvexemphim-production.up.railway.app/api/food/orders/1" \
  -H "Authorization: Bearer <TOKEN>"
```
**Expected**: `200 OK` → `{ "message": "Hủy đơn thành công" }`

#### 4.6 - Kiểm tra & áp dụng voucher
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/vouchers/validate" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "WELCOME20",
    "orderAmount": 200000
  }'
```
**Expected**: `200 OK` → `{ "valid": true, "discountAmount": 40000, "finalAmount": 160000 }`
**Error**: `400 Bad Request` → `{ "error": "Mã giảm giá đã hết hạn" }`

#### 4.7 - Thanh toán mô phỏng - Thành công
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/payments/simulate" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketIds": [1, 2],
    "success": true,
    "foodOrderId": 1
  }'
```
**Expected**: `200 OK` → `{ "success": true, "transactionId": "TXN_...", "message": "Thanh toán thành công" }`

#### 4.8 - Thanh toán mô phỏng - Thất bại (ghế được giải phóng)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/payments/simulate" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketIds": [1, 2],
    "success": false
  }'
```
**Expected**: `200 OK` → `{ "success": false, "message": "Thanh toán thất bại" }`
**Kiểm tra**: Ghế đã được giải phóng, vé PENDING đã bị xóa

#### 4.9 - Tạo thanh toán VNPay
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/payments/vnpay/create" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketIds": [1, 2],
    "amount": 200000
  }'
```
**Expected**: `200 OK` → `{ "paymentUrl": "https://sandbox.vnpayment.vn/..." }`

#### 4.10 - Tạo voucher mới (Admin)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/admin/vouchers" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "SUMMER2026",
    "description": "Giảm 20% cho đơn hàng từ 100K",
    "discountPercent": 20,
    "maxDiscount": 50000,
    "minOrderAmount": 100000,
    "validFrom": "2026-03-01",
    "validUntil": "2026-12-31",
    "usageLimit": 100,
    "isActive": true
  }'
```
**Expected**: `201 Created`

#### 4.11 - Cập nhật voucher (Admin)
```bash
curl -X PUT "https://web-datvexemphim-production.up.railway.app/api/admin/vouchers/1" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "usageLimit": 200
  }'
```
**Expected**: `200 OK`

#### 4.12 - Xóa voucher (Admin)
```bash
curl -X DELETE "https://web-datvexemphim-production.up.railway.app/api/admin/vouchers/1" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```
**Expected**: `200 OK`

#### 4.13 - Bật/tắt voucher (toggle active)
```bash
curl -X PUT "https://web-datvexemphim-production.up.railway.app/api/admin/vouchers/1/toggle" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```
**Expected**: `200 OK` → `{ "isActive": false }`

#### 4.14 - Tạo danh mục đồ ăn (Admin)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/admin/food/categories" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bắp rang",
    "description": "Bắp rang bơ các loại",
    "icon": "fas fa-cookie"
  }'
```
**Expected**: `201 Created`

#### 4.15 - Tạo sản phẩm đồ ăn (Admin)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/admin/food/items" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 1,
    "name": "Bắp rang bơ lớn",
    "description": "Size lớn 200g",
    "price": 55000,
    "imageUrl": "https://example.com/bap.jpg",
    "stock": 50,
    "isAvailable": true
  }'
```
**Expected**: `201 Created`

---

## MEMBER 5 - Admin Dashboard & Hạ tầng

### Mô tả
Xây dựng trang dashboard quản trị với thống kê doanh thu, quản lý suất chiếu, quản lý vé. Cấu hình hệ thống: CORS, Security, Upload ảnh, Data Seeder. Phát triển các shared components: CSS, JS utilities, Navbar.

### Backend Files

```
backend/src/main/java/com/datvexemphim/
├── api/controller/
│   ├── AdminDashboardController.java  ⭐ API thống kê dashboard
│   ├── AdminShowtimeController.java   ⭐ CRUD suất chiếu (Admin)
│   ├── AdminTicketController.java     ⭐ Quản lý vé (Admin - hủy, xóa)
│   └── UploadController.java          ⭐ Upload ảnh (Multipart → Base64)
├── service/
│   └── AdminDashboardService.java    ⭐ Logic thống kê doanh thu
├── config/
│   ├── SecurityConfig.java           ⭐ Cấu hình Spring Security, phân quyền
│   ├── CorsConfig.java                ⭐ CORS cho frontend Vercel
│   └── DataSeeder.java                ⭐ Tạo dữ liệu mẫu khi khởi chạy
└── api/
    └── GlobalExceptionHandler.java   ⭐ Xử lý exception tập trung, trả về ApiError
```

### Frontend Files

```
frontend/
├── assets/
│   ├── css/
│   │   └── style.css               ⭐ Glassmorphism design system, CSS variables, animations
│   ├── js/
│   │   ├── api.js                  ⭐ API helpers (apiGet, apiPost, apiPut, apiDelete, upload)
│   │   ├── app.js                  ⭐ Utilities (showToast, formatVnd, formatDate, requireAuth)
│   │   ├── config.js               ⭐ API_BASE config (Vercel URL)
│   │   ├── firebase.js             ⭐ Firebase Google Sign-In
│   │   └── admin/
│   │       ├── admin.js            ⭐ Admin auth guard, admin navbar
│   │       └── modals.js           ⭐ Reusable Bootstrap modals (movie, room, showtime, genre...)
├── partials/
│   └── navbar.html                  ⭐ Shared navbar (auth-aware, contact widget)
└── admin/
    ├── dashboard.html               ⭐ Dashboard - thống kê, revenue, quick actions
    └── showtimes.html              ⭐ Admin: CRUD suất chiếu
    └── tickets.html                ⭐ Admin: xem & hủy vé
```

### API Test Commands (Postman)

#### 5.1 - Thống kê dashboard (Admin)
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/admin/stats/revenue" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```
**Expected**: `200 OK` → `{ "totalRevenue": 15000000, "totalTickets": 75, "totalMovies": 5, "totalRooms": 3 }`

#### 5.2 - Thống kê doanh thu theo tháng (Admin)
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/admin/stats/revenue?period=monthly" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```
**Expected**: `200 OK` → Array monthly revenue data

#### 5.3 - Tạo suất chiếu (Admin)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/admin/showtimes" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "movieId": 1,
    "roomId": 1,
    "startTime": "2026-03-25T14:00:00",
    "price": 75000
  }'
```
**Expected**: `201 Created` → Showtime object

#### 5.4 - Cập nhật suất chiếu (Admin)
```bash
curl -X PUT "https://web-datvexemphim-production.up.railway.app/api/admin/showtimes/1" \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "price": 80000
  }'
```
**Expected**: `200 OK`

#### 5.5 - Hủy suất chiếu (Admin - soft delete)
```bash
curl -X DELETE "https://web-datvexemphim-production.up.railway.app/api/admin/showtimes/1" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```
**Expected**: `200 OK` → Set `cancelledAt = now`

#### 5.6 - Lấy danh sách tất cả vé (Admin)
```bash
curl -X GET "https://web-datvexemphim-production.up.railway.app/api/admin/tickets" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```
**Expected**: `200 OK` → Array AdminTicketDto

#### 5.7 - Hủy vé bất kỳ (Admin)
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/admin/tickets/1/cancel" \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```
**Expected**: `200 OK`

#### 5.8 - Upload ảnh
```bash
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/upload/image" \
  -H "Authorization: Bearer <TOKEN>" \
  -F "file=@poster.jpg"
```
**Expected**: `200 OK` → `{ "url": "data:image/jpeg;base64,..." }`
**Validate**: Chỉ chấp nhận JPEG/PNG/GIF/WEBP, max 5MB

---

## 📌 Ghi chú kỹ thuật chung

### Cách lấy Token cho test
```bash
# Login
curl -X POST "https://web-datvexemphim-production.up.railway.app/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@local", "password": "123456"}'

# Copy accessToken từ response → dùng cho header:
# Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

### Tài khoản mặc định
| Role | Email | Mật khẩu |
|------|-------|----------|
| Admin | admin@local | 123456 |

### Database
- MySQL trên Railway, ddl-auto: update (tự tạo bảng)
- Khi chạy local: tạo database `datvexemphim` trước

### Deploy
- Backend: Railway (auto deploy từ GitHub)
- Frontend: Vercel (auto deploy từ GitHub)
- Sau khi push code mới → Railway tự deploy backend → Vercel tự deploy frontend

### Các ràng buộc quan trọng
1. **Chống đặt trùng ghế**: unique constraint `uk_ticket_showtime_seat` ở database
2. **Hủy vé**: chỉ khi `now < startTime - 30 phút`
3. **Giải phóng ghế khi thanh toán thất bại**: xóa tickets PENDING
4. **Chuyển vé**: food order đi kèm cũng được chuyển cho người nhận
5. **Voucher**: kiểm tra expiry, usageLimit, minOrderAmount

---

## 📋 Checklist Test Toàn Dự Án

| # | API | Method | Endpoint | Owner |
|---|-----|--------|----------|-------|
| 1 | Register | POST | `/api/auth/register` | M1 |
| 2 | Login | POST | `/api/auth/login` | M1 |
| 3 | Profile | GET | `/api/user/profile` | M1 |
| 4 | Update Profile | PUT | `/api/user/profile` | M1 |
| 5 | Change Password | POST | `/api/user/change-password` | M1 |
| 6 | Forgot Password | POST | `/api/auth/reset-password` | M1 |
| 7 | List Movies | GET | `/api/movies` | M2 |
| 8 | Movie Detail | GET | `/api/movies/{id}` | M2 |
| 9 | Genres | GET | `/api/movies/genres` | M2 |
| 10 | Create Movie (Admin) | POST | `/api/admin/movies` | M2 |
| 11 | Movie Reviews | GET | `/api/movies/{id}/reviews` | M2 |
| 12 | Create Review | POST | `/api/movies/reviews` | M2 |
| 13 | Showtimes | GET | `/api/showtimes` | M3 |
| 14 | Seat Map | GET | `/api/seatmap/{id}` | M3 |
| 15 | Book Tickets | POST | `/api/bookings` | M3 |
| 16 | My Tickets | GET | `/api/tickets/me` | M3 |
| 17 | Cancel Ticket | POST | `/api/tickets/{id}/cancel` | M3 |
| 18 | Transfer Ticket | POST | `/api/tickets/transfer` | M3 |
| 19 | Food Categories | GET | `/api/food/categories` | M4 |
| 20 | Food Items | GET | `/api/food/items` | M4 |
| 21 | Create Food Order | POST | `/api/food/orders` | M4 |
| 22 | Validate Voucher | POST | `/api/vouchers/validate` | M4 |
| 23 | Simulate Payment | POST | `/api/payments/simulate` | M4 |
| 24 | VNPay Create | POST | `/api/payments/vnpay/create` | M4 |
| 25 | Dashboard Stats | GET | `/api/admin/stats/revenue` | M5 |
| 26 | Create Showtime (Admin) | POST | `/api/admin/showtimes` | M5 |
| 27 | Upload Image | POST | `/api/upload/image` | M5 |
| 28 | Admin Users | GET | `/api/admin/users` | M1 |

---

*Document này dùng để báo cáo với thầy - mỗi thành viên test đúng API của phần mình.*
