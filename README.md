# Web đặt vé xem phim (Spring Boot + MySQL + Bootstrap)

## 1) Công nghệ

- **Backend**: Java 17, Spring Boot, Spring Security (JWT), JPA/Hibernate, REST API
- **Database**: MySQL (Laragon local)
- **Frontend**: HTML/CSS/JavaScript + Bootstrap (gọi API qua `fetch`)

## 2) Cấu trúc project

```
backend/   (Spring Boot - Controller → Service → Repository → Entity)
frontend/  (static HTML/CSS/JS/Bootstrap)
```

## 3) Thiết kế database (JPA tạo bảng)

Các bảng chính (tương ứng entity):

- `users`
- `movies`
- `rooms`
- `seats`
- `showtimes`
- `tickets`
- `payments`
- `transfer_history`

Điểm quan trọng:

- **Chống đặt trùng ghế**: bảng `tickets` có **unique constraint** `uk_ticket_showtime_seat(showtime_id, seat_id)`.
- **Giải phóng ghế khi thanh toán FAIL**: hệ thống **xóa ticket PENDING** tương ứng.
- **Hủy vé trước 30 phút**: chỉ hủy được khi `now < startTime - 30 phút`.

## 4) Chạy MySQL bằng Laragon

1. Mở Laragon → Start (Apache + MySQL)
2. Mặc định MySQL Laragon:
   - host: `localhost`
   - port: `3306`
   - user: `root`
   - password: *(trống)*
3. Backend sẽ tự tạo DB `datvexemphim` nhờ:
   - `createDatabaseIfNotExist=true`
   - `spring.jpa.hibernate.ddl-auto=update`

Nếu máy bạn có mật khẩu root, sửa trong:

- `backend/src/main/resources/application.yml`

## 5) Chạy Backend

Yêu cầu:

- Cài **JDK 17**
- Có `java` trong PATH

Chạy:

```bat
cd backend
.\mvnw.cmd spring-boot:run
```

Backend chạy tại:



Seeder (tự chạy lần đầu):

- Tạo admin: **`admin@local / 123456`**
- Tạo 1 room + seats + 1 movie + 1 showtime sắp tới

## 6) Chạy Frontend

Frontend là static nên cần chạy qua web server (không nên mở file `.html` trực tiếp vì có `fetch`).

Cách dễ nhất:

- Dùng VSCode extension **Live Server**
- Mở `frontend/index.html` bằng Live Server

Hoặc dùng Python:

```bash
cd frontend
python -m http.server 5500
```

chay du an
mo laragon 
.\mvnw spring-boot:run
 lenh dong tat ca cong 
 taskkill /F /IM java.exe
 chaynrok
 huong dan nam trong file config.js nằm trong frontend /assets/js/configs
Sau đó mở:

- `http://localhost:5500/index.html`

## 7) Luồng chức năng chính (User)

1. **Đăng ký** → `/api/auth/register`
2. **Đăng nhập** → `/api/auth/login` (nhận JWT, frontend lưu `localStorage`)
3. **Xem phim** → `/api/movies`
4. **Chi tiết phim** → `/api/movies/{id}`
5. **Xem lịch chiếu** → `/api/showtimes` hoặc `/api/showtimes/movie/{movieId}`
6. **Xem sơ đồ ghế** → `/api/seatmap/{showtimeId}`
7. **Đặt vé (giữ ghế PENDING)** → `POST /api/bookings`
8. **Thanh toán mô phỏng** → `POST /api/payments/simulate`
   - `success=true` → ticket `CONFIRMED`
   - `success=false` → ticket PENDING bị xóa (mở lại ghế)
9. **Lịch sử vé** → `/api/tickets/me`
10. **Hủy vé** → `POST /api/tickets/{ticketId}/cancel` (trước giờ chiếu 30 phút)
11. **Chuyển vé** → `POST /api/tickets/transfer`

## 8) Admin

Đăng nhập admin để gọi các API `/api/admin/**` (role ADMIN).

Frontend admin:

- `frontend/admin/dashboard.html`

Các API chính:

- Movies: `/api/admin/movies`
- Rooms: `/api/admin/rooms` + `/api/admin/rooms/generate-seats`
- Seats: `/api/admin/seats/room/{roomId}`
- Showtimes: `/api/admin/showtimes`
- Tickets: `/api/admin/tickets`
- Users: `/api/admin/users`
- Revenue: `/api/admin/stats/revenue`

## 9) Ghi chú kỹ thuật

- **Transaction**: tạo tickets PENDING và simulate payment đều chạy trong transaction.
- **Chống trùng ghế**: có cả check sớm + ràng buộc unique ở DB (chống race-condition khi nhiều người đặt cùng lúc).
- **CORS**: controller có `@CrossOrigin` để frontend local gọi được API.

