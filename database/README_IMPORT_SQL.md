# 🗄️ HƯỚNG DẪN IMPORT SQL VÀO LARAGON & MYSQL

## **Phần 1: Chuẩn Bị Môi Trường**

### Yêu Cầu:
- Laragon đã được cài đặt và chạy
- MySQL đang hoạt động trong Laragon
- Database có tên `movie_ticket_booking` (hoặc tên khác của bạn) đã được tạo

---

## **Phần 2: Phương Pháp 1 - Import Qua MySQL Workbench**

### Bước 1: Mở MySQL Workbench
1. Mở Laragon
2. Nhấp đúp **MySQL 8.0** trong tab **Services**
3. Hoặc mở MySQL Workbench từ Start menu

### Bước 2: Kết Nối Database
1. Chọn kết nối MySQL: `localhost:3306 (root/root)` hoặc tùy cấu hình của bạn
2. Nếu chưa có kết nối, tạo mới:
   - **+** → New Connection
   - **Hostname**: `127.0.0.1`
   - **Port**: `3306`
   - **Username**: `root`
   - **Password**: (bỏ trống hoặc `root` - tùy cấu hình Laragon)

### Bước 3: Chọn Database
1. Sau khi kết nối, chọn database: `movie_ticket_booking`
2. Hoặc tạo mới nếu chưa có:
   ```sql
   CREATE DATABASE IF NOT EXISTS movie_ticket_booking 
   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   USE movie_ticket_booking;
   ```

### Bước 4: Import SQL Script
1. Chọn **File** → **Open SQL Script**
2. Duyệt đến file: `d:\Web_datvexemphim\database\01_create_food_tables.sql`
3. Click **Open** → Script sẽ hiển thị trong editor
4. Nhấn **Execute** (⚡ icon) hoặc `Ctrl+Shift+Enter`
5. Kiểm tra **Output** panel → "Query executed successfully"

### Bước 5: Import Sample Data
1. Lặp lại với file: `02_insert_sample_food_data.sql`
2. Nhấn **Execute**
3. Xác nhận dữ liệu:
   ```sql
   SELECT * FROM food_category;
   SELECT * FROM food_item;
   ```

---

## **Phần 3: Phương Pháp 2 - Import Qua Command Line**

### Bước 1: Mở Command Prompt
- **Windows**: `Win + R` → gõ `cmd` → Enter
- Hoặc mở Terminal từ Laragon

### Bước 2: Điều Hướng Đến Thư Mục Database
```bash
cd d:\Web_datvexemphim\database
```

### Bước 3: Kết Nối MySQL và Import
```bash
mysql -u root -p movie_ticket_booking < 01_create_food_tables.sql
mysql -u root -p movie_ticket_booking < 02_insert_sample_food_data.sql
```

**Chú ý:**
- Nếu MySQL không yêu cầu password, bỏ `-p`
- Nếu yêu cầu password, gõ vào sau khi prompt xuất hiện

### Bước 4: Xác Nhận (Optional)
```bash
mysql -u root -p movie_ticket_booking
```
Sau đó gõ:
```sql
SHOW TABLES;
SELECT COUNT(*) FROM food_category;
SELECT COUNT(*) FROM food_item;
```

---

## **Phần 4: Phương Pháp 3 - Import Qua Laragon UI**

### Bước 1: Mở Laragon Database Tools
1. Nhấp chuột phải vào **MySQL 8.0** trong Laragon
2. Chọn **Edit MySQL**

### Bước 2: Hoặc Dùng HeidiSQL (Laragon Tools)
1. Mở Laragon
2. Nhấp vào **Tools** icon
3. Chọn **HeidiSQL**
4. Kết nối với localhost, root, password=""

### Bước 3: Import Trong HeidiSQL
1. Chọn database `movie_ticket_booking`
2. **File** → **Load SQL File**
3. Chọn `01_create_food_tables.sql`
4. **F9** (Execute)

---

## **Phần 5: Kiểm Tra Kết Quả**

### Query Xác Nhận Tất Cả Bảng Đã Tạo:
```sql
-- Kiểm tra tất cả bảng
SHOW TABLES LIKE 'food_%';

-- Kết quả mong đợi:
-- food_category
-- food_item
-- food_order
-- food_order_item
```

### Query Xác Nhận Dữ Liệu Mẫu:
```sql
-- Đếm danh mục
SELECT COUNT(*) as total_categories FROM food_category;
-- Kỳ vọng: 4

-- Đếm sản phẩm
SELECT COUNT(*) as total_items FROM food_item;
-- Kỳ vọng: 40+

-- Xem sản phẩm theo danh mục
SELECT 
    fc.name as category,
    COUNT(fi.id) as item_count,
    MIN(fi.price) as min_price,
    MAX(fi.price) as max_price
FROM food_category fc
LEFT JOIN food_item fi ON fc.id = fi.category_id
GROUP BY fc.id
ORDER BY fc.display_order;
```

### Query Xác Nhận Relationship:
```sql
-- Kiểm tra foreign key constraints
SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_NAME LIKE 'food_%' AND REFERENCED_TABLE_NAME IS NOT NULL;
```

---

## **Phần 6: Troubleshooting**

### ❌ Lỗi: "Table 'payment' doesn't exist"
**Giải pháp:**
1. Đảm bảo `payment` table đã được tạo từ migration/script ban đầu
2. Chạy migration backend trước: `mvnw.cmd flyway:migrate`
3. Hoặc chạy Hibernate auto-update: `spring.jpa.hibernate.ddl-auto=update`

### ❌ Lỗi: "Duplicate entry for key 'name'"
**Giải pháp:**
```sql
-- Xóa dữ liệu cũ (nếu cần)
DELETE FROM food_item;
DELETE FROM food_category;

-- Rồi import lại
```

### ❌ Lỗi: "Connection refused"
**Giải pháp:**
1. Kiểm tra MySQL đang chạy: Laragon → MySQL icon (xanh)
2. Kiểm tra port 3306 không bị chiếm
3. Khởi động lại MySQL: `mysql restart`

### ❌ Lỗi: "charset utf8mb4 is not valid"
**Giải pháp:**
- Cập nhật MySQL version hoặc:
```sql
ALTER DATABASE movie_ticket_booking CHARACTER SET utf8 COLLATE utf8_general_ci;
```

---

## **Phần 7: Các Bước Tiếp Theo**

1. ✅ SQL tables đã tạo
2. ⏭️ **Tiếp theo:** Tạo Java Entities (JPA)
3. ⏭️ **Sau đó:** Tạo Repositories
4. ⏭️ **Rồi:** Tạo Services
5. ⏭️ **Cuối cùng:** Tạo Controllers & APIs

---

## **Kiểm Danh Sách Nhanh**

```
✅ Database `movie_ticket_booking` đã tạo
✅ Bảng `food_category` đã tạo (4 danh mục)
✅ Bảng `food_item` đã tạo (40+ sản phẩm)
✅ Bảng `food_order` đã tạo
✅ Bảng `food_order_item` đã tạo
✅ Foreign keys & indexes đã thiết lập
✅ Dữ liệu mẫu đã insert
✅ Bảng `payment` đã update (has_food_order, food_order_total)
```

**Hệ thống food & drink đã sẵn sàng cho backend implementation! 🎉**
