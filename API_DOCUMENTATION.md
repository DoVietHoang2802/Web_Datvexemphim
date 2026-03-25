# API Documentation - Đặt Vé Xem Phim

> **Base URL:** `https://webdatvexemphim-production.up.railway.app`
>
> Endpoints có ghi `✅ Auth` cần thêm header: `Authorization: Bearer <JWT_TOKEN>`
>
> JWT token nhận được từ `/api/auth/login` hoặc `/api/auth/register`

---

## 🔐 1. AUTH - Xác thực

### Đăng ký
```
POST /api/auth/register
Body: {
  "email": "user@example.com",
  "password": "123456",
  "fullName": "Nguyen Van A"
}
```

### Đăng nhập
```
POST /api/auth/login
Body: {
  "email": "user@example.com",
  "password": "123456"
}
Response: {
  "token": "eyJhbG...",
  "userId": 1,
  "email": "...",
  "fullName": "..."
}
```

### Đăng nhập Google
```
POST /api/auth/google-login
Body: {
  "idToken": "...",
  "email": "user@gmail.com",
  "displayName": "Nguyen Van A",
  "photoURL": "https://..."
}
```

### Đặt lại mật khẩu (quên mk)
```
POST /api/auth/reset-password
Body: {
  "email": "user@example.com",
  "fullName": "Nguyen Van A",
  "newPassword": "newpassword123"
}
```

---

## 👤 2. USER - Người dùng

### Lấy thông tin profile
```
GET /api/user/profile
Header: ✅ Auth
```

### Cập nhật profile
```
PUT /api/user/profile
Header: ✅ Auth
Body: {
  "fullName": "Nguyen Van B",
  "phone": "0909123456",
  "avatarBase64": "data:image/png;base64,..."
}
```

### Đổi mật khẩu
```
POST /api/user/change-password
Header: ✅ Auth
Body: {
  "currentPassword": "old123",
  "newPassword": "new123"
}
```

---

## 🎬 3. MOVIE - Phim

### Danh sách phim đang chiếu
```
GET /api/movies
```

### Phim đã chiếu
```
GET /api/movies/past
```

### Chi tiết phim
```
GET /api/movies/{id}
```

### Danh sách thể loại
```
GET /api/movies/genres
```

---

## 🎭 4. SHOWTIME - Lịch chiếu

### Tất cả lịch chiếu sắp tới
```
GET /api/showtimes
```

### Lịch chiếu theo phim
```
GET /api/showtimes/movie/{movieId}
```

---

## 💺 5. SEAT - Ghế

### Bản đồ ghế theo suất chiếu
```
GET /api/seatmap/{showtimeId}
Response: {
  "showtimeId": 1,
  "roomName": "Phòng 1",
  "seats": [
    {
      "id": 1,
      "row": "A",
      "number": 1,
      "type": "NORMAL",
      "price": 80000,
      "active": true
    },
    ...
  ]
}
```

---

## 🎟️ 6. BOOKING & TICKET - Đặt vé

### Tạo vé chờ thanh toán
```
POST /api/bookings
Header: ✅ Auth
Body: {
  "showtimeId": 1,
  "seatIds": [1, 2, 3],
  "voucherCode": "GIAM10"
}
```

### Vé của tôi
```
GET /api/tickets/me
Header: ✅ Auth
```

### Lấy vé theo IDs
```
GET /api/tickets?ids=1,2,3
Header: ✅ Auth
```

### Hủy vé
```
POST /api/tickets/{ticketId}/cancel
Header: ✅ Auth
```

### Chuyển vé
```
POST /api/tickets/transfer
Header: ✅ Auth
Body: {
  "ticketId": 1,
  "recipientEmail": "friend@example.com"
}
```

### Xóa vé (chỉ vé đang chờ)
```
DELETE /api/tickets/{ticketId}
Header: ✅ Auth
```

---

## 💳 7. PAYMENT - Thanh toán

### Mô phỏng thanh toán thành công
```
POST /api/payments/simulate
Header: ✅ Auth
Body: {
  "ticketIds": [1, 2],
  "success": true
}
```

### Tạo URL thanh toán VNPay
```
POST /api/payments/vnpay/create
Header: ✅ Auth
Body: {
  "amount": 240000,
  "ticketIds": [1, 2]
}
Response: {
  "paymentUrl": "https://sandbox.vnpayment.vn/...",
  "orderId": "202501011200001234"
}
```

---

## 🎫 8. VOUCHER - Mã giảm giá

### Kiểm tra & áp dụng voucher
```
POST /api/vouchers/validate
Body: {
  "voucherCode": "GIAM10",
  "orderAmount": 240000
}
Response (thành công): {
  "valid": true,
  "code": "GIAM10",
  "discountAmount": 24000,
  "message": "Áp dụng thành công!"
}
Response (thất bại): {
  "valid": false,
  "message": "Mã voucher không hợp lệ hoặc đã hết hạn"
}
```

---

## 🍿 9. FOOD - Đồ ăn

### Danh sách danh mục
```
GET /api/food/categories
```

### Danh mục theo ID
```
GET /api/food/categories/{id}
```

### Tất cả món
```
GET /api/food/items
```

### Món theo danh mục
```
GET /api/food/items/category/{categoryId}
```

### Món theo ID
```
GET /api/food/items/{id}
```

### Tạo đơn đồ ăn
```
POST /api/food/orders
Header: ✅ Auth
Body: {
  "ticketId": 1,
  "items": [
    { "foodItemId": 1, "quantity": 2 },
    { "foodItemId": 3, "quantity": 1 }
  ]
}
```

### Đơn đồ ăn của tôi
```
GET /api/food/orders
Header: ✅ Auth
```

### Đơn theo ID
```
GET /api/food/orders/{id}
Header: ✅ Auth
```

### Đơn theo ticket
```
GET /api/food/orders/ticket/{ticketId}
Header: ✅ Auth
```

### Hủy đơn
```
DELETE /api/food/orders/{id}
Header: ✅ Auth
```

---

## ⭐ 10. REVIEW - Đánh giá phim

### Lấy đánh giá phim
```
GET /api/movies/{movieId}/reviews
```

### Thống kê đánh giá
```
GET /api/movies/{movieId}/reviews/stats
Response: {
  "averageRating": 4.5,
  "totalReviews": 120
}
```

### Tạo / Cập nhật đánh giá
```
POST /api/movies/reviews
Header: ✅ Auth
Body: {
  "movieId": 1,
  "rating": 5,
  "comment": "Phim rất hay!"
}
```

### Xóa đánh giá
```
DELETE /api/movies/reviews/{reviewId}
Header: ✅ Auth
```

---

## 💬 11. CHAT - Chat vé

### Gửi tin nhắn
```
POST /api/chat/send
Header: ✅ Auth
Body: {
  "ticketId": 1,
  "content": "Tôi muốn hỏi về vé này"
}
```

### Lịch sử chat vé
```
GET /api/chat/history/{ticketId}
Header: ✅ Auth
```

---

## 👥 12. COMMUNITY - Chat cộng đồng

### Lấy tin nhắn (100 tin mới nhất)
```
GET /api/community/messages
```

### Stream realtime (SSE)
```
GET /api/community/stream
Produces: text/event-stream
```

### Gửi tin nhắn
```
POST /api/community/messages
Header: ✅ Auth
Body: {
  "content": "Chào mọi người!"
}
```

### Số user online
```
GET /api/community/online
```

---

## 🛒 13. TICKET MARKET - Chợ vé

### Tất cả vé đang bán
```
GET /api/market/tickets
```

### Vé đang bán của tôi
```
GET /api/market/my-listed
Header: ✅ Auth
```

### Yêu cầu nhận vé của tôi
```
GET /api/market/my-requests
Header: ✅ Auth
```

### Đăng vé lên chợ
```
POST /api/market/list
Header: ✅ Auth
Body: { "ticketId": 1 }
```

### Gỡ vé khỏi chợ
```
POST /api/market/unlist
Header: ✅ Auth
Body: { "ticketId": 1 }
```

### Gửi yêu cầu nhận vé
```
POST /api/market/request
Header: ✅ Auth
Body: { "ticketId": 1 }
```

### Xem request chờ duyệt (chủ vé)
```
GET /api/market/requests/{ticketId}
Header: ✅ Auth
```

### Duyệt / Từ chối request
```
POST /api/market/respond
Header: ✅ Auth
Body: {
  "requestId": 1,
  "accept": true
}
```

### Số thông báo chờ duyệt
```
GET /api/market/notifications
Header: ✅ Auth
```

---

## 🖼️ 14. UPLOAD - Upload ảnh

### Upload ảnh (trả về Base64)
```
POST /api/upload/image
Content-Type: multipart/form-data
Body: file (field name: "file")
Response: {
  "url": "data:image/png;base64,...",
  "type": "image/png",
  "size": 102400
}
```

---

## 🛠️ 15. ADMIN - Quản trị

> Tất cả endpoint admin đều cần: `Header: Authorization: Bearer <ADMIN_TOKEN>`
> Token admin nhận được khi đăng nhập tài khoản có role = ADMIN

---

### 15.1 MOVIES & GENRES

```
GET    /api/admin/movies
GET    /api/admin/movies/{id}
POST   /api/admin/movies
PUT    /api/admin/movies/{id}
DELETE /api/admin/movies/{id}

GET    /api/admin/movies/genres
GET    /api/admin/movies/genres/active
GET    /api/admin/movies/genres/{id}
POST   /api/admin/movies/genres
PUT    /api/admin/movies/genres/{id}
DELETE /api/admin/movies/genres/{id}
```

---

### 15.2 SHOWTIMES - Lịch chiếu

```
GET    /api/admin/showtimes
GET    /api/admin/showtimes/{id}
POST   /api/admin/showtimes
PUT    /api/admin/showtimes/{id}
DELETE /api/admin/showtimes/{id}
POST   /api/admin/showtimes/{id}/end
```

> Lưu ý:
> - Không sửa được suất chiếu đã ngừng (status = ENDED)
> - Không sửa được suất chiếu đã có vé được đặt
> - Chỉ có thể kết thúc (end) hoặc xóa suất chiếu đã có người đặt

---

### 15.3 ROOMS - Phòng chiếu

```
GET    /api/admin/rooms
GET    /api/admin/rooms/{id}
POST   /api/admin/rooms
PUT    /api/admin/rooms/{id}
DELETE /api/admin/rooms/{id}
POST   /api/admin/rooms/generate-seats
```

---

### 15.4 SEATS - Ghế

```
GET    /api/admin/seats/room/{roomId}
GET    /api/admin/seats/{id}
POST   /api/admin/seats
PUT    /api/admin/seats/{id}
DELETE /api/admin/seats/{id}
```

---

### 15.5 TICKETS - Vé (Admin)

```
GET    /api/admin/tickets
GET    /api/admin/tickets/{id}
POST   /api/admin/tickets
PUT    /api/admin/tickets/{id}
DELETE /api/admin/tickets/{id}
POST   /api/admin/tickets/{id}/cancel
```

---

### 15.6 USERS - Người dùng

```
GET    /api/admin/users
GET    /api/admin/users/{id}
POST   /api/admin/users
PUT    /api/admin/users/{id}
DELETE /api/admin/users/{id}
POST   /api/admin/users/{id}/change-role?role=ADMIN
POST   /api/admin/users/{id}/reset-password?newPassword=xxx
```

---

### 15.7 VOUCHERS - Mã giảm giá

```
GET    /api/admin/vouchers
GET    /api/admin/vouchers/{id}
POST   /api/admin/vouchers
PUT    /api/admin/vouchers/{id}
DELETE /api/admin/vouchers/{id}
PATCH  /api/admin/vouchers/{id}/toggle
```

---

### 15.8 FOOD - Đồ ăn (Admin)

```
# Categories
GET    /api/admin/food/categories
GET    /api/admin/food/categories/{id}
POST   /api/admin/food/categories
PUT    /api/admin/food/categories/{id}
DELETE /api/admin/food/categories/{id}

# Items
GET    /api/admin/food/items
GET    /api/admin/food/items/{id}
POST   /api/admin/food/items
PUT    /api/admin/food/items/{id}
DELETE /api/admin/food/items/{id}
```

---

### 15.9 STATS - Thống kê

```
GET /api/admin/stats/revenue
Response: {
  "totalRevenue": 15000000
}
```

---

## 📝 Request Body Models

### MovieUpsertRequest
```json
{
  "title": "Phim Mới 2025",
  "description": "Mô tả phim",
  "duration": 120,
  "director": "Đạo diễn A",
  "cast": "Diễn viên B, Diễn viên C",
  "genreId": 1,
  "releaseDate": "2025-01-15",
  "endDate": "2025-02-15",
  "posterBase64": "data:image/png;base64,...",
  "trailerUrl": "https://youtube.com/...",
  "active": true
}
```

### ShowtimeUpsertRequest
```json
{
  "movieId": 1,
  "roomId": 1,
  "startTime": "2025-01-15T14:00:00",
  "endTime": "2025-01-15T16:00:00",
  "price": 80000
}
```

### RoomUpsertRequest
```json
{
  "name": "Phòng 1",
  "rows": 10,
  "seatsPerRow": 12,
  "active": true
}
```

### GenerateSeatsRequest
```json
{
  "roomId": 1,
  "rows": 10,
  "seatsPerRow": 12,
  "seatTypes": [
    { "row": "A", "type": "VIP", "priceExtra": 20000 }
  ]
}
```

### VoucherUpsertRequest
```json
{
  "code": "GIAM10",
  "description": "Giảm 10%",
  "discountPercent": 10,
  "maxDiscount": 20000,
  "minOrderAmount": 50000,
  "maxUsage": 100,
  "startDate": "2025-01-01",
  "endDate": "2025-12-31",
  "active": true
}
```

### FoodCategoryUpsertRequest
```json
{
  "name": "Nước uống",
  "displayOrder": 1,
  "active": true
}
```

### FoodItemUpsertRequest
```json
{
  "categoryId": 1,
  "name": "Coca Cola",
  "price": 15000,
  "description": "Lon 330ml",
  "imageBase64": "data:image/png;base64,...",
  "available": true
}
```

### AdminUserUpsertRequest
```json
{
  "email": "user@example.com",
  "fullName": "Nguyen Van A",
  "password": "password123",
  "role": "USER"
}
```

### MovieGenreUpsertRequest
```json
{
  "name": "Hành động",
  "active": true
}
```

### SeatUpsertRequest
```json
{
  "roomId": 1,
  "row": "A",
  "number": 1,
  "type": "VIP",
  "price": 100000,
  "active": true
}
```

### AdminTicketUpsertRequest
```json
{
  "showtimeId": 1,
  "seatId": 1,
  "userId": 1,
  "price": 80000,
  "status": "PENDING"
}
```

---

## 🔄 Luồng đặt vé đầy đủ

```
1. GET /api/movies                    → Xem danh sách phim
2. GET /api/showtimes/movie/{id}     → Xem lịch chiếu phim đó
3. GET /api/seatmap/{showtimeId}     → Xem ghế trống
4. POST /api/bookings                → Tạo vé chờ thanh toán
5. POST /api/payments/simulate       → Thanh toán mô phỏng (ghế = đã đặt)
6. GET /api/tickets/me               → Xem vé đã đặt
```
