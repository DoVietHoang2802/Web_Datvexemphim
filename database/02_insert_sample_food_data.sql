-- ============================================================================
-- FOOD & DRINK FEATURE - INSERT SAMPLE DATA
-- ============================================================================

-- ============================================================================
-- 1. INSERT FOOD CATEGORIES
-- ============================================================================

INSERT IGNORE INTO food_category (name, description, icon, display_order, is_active) VALUES
('Bắp & Snack', 'Bắp rang, snack mặn, đồ ăn nhẹ', 'fas fa-popcorn', 1, TRUE),
('Nước Uống', 'Nước ngọt, nước tự nhiên, cà phê, trà', 'fas fa-glass-water', 2, TRUE),
('Bánh & Tráng Miệng', 'Bánh mì, bánh ngọt, kem, chocolate', 'fas fa-ice-cream', 3, TRUE),
('Combo Tiết Kiệm', 'Các combo giảm giá kết hợp', 'fas fa-gift', 4, TRUE);

-- ============================================================================
-- 2. INSERT FOOD ITEMS - BẮP & SNACK
-- ============================================================================

INSERT INTO food_item (category_id, name, description, price, image_url, stock, is_available, display_order) VALUES
(1, 'Bắp Rang Bơ Vừa', 'Bắp rang bơ Clarified, vừa (350g)', 60000, '/images/food/popcorn-butter-m.jpg', 500, TRUE, 1),
(1, 'Bắp Rang Bơ Lớn', 'Bắp rang bơ Clarified, lớn (500g)', 80000, '/images/food/popcorn-butter-l.jpg', 480, TRUE, 2),
(1, 'Bắp Rang Caramel Vừa', 'Bắp rang caramel mặn ngọt, vừa', 70000, '/images/food/popcorn-caramel-m.jpg', 450, TRUE, 3),
(1, 'Bắp Rang Caramel Lớn', 'Bắp rang caramel, lớn', 90000, '/images/food/popcorn-caramel-l.jpg', 400, TRUE, 4),

(1, 'Khoai Tây Chiên Vừa', 'Khoai tây chiên giòn, vừa', 50000, '/images/food/fries-m.jpg', 300, TRUE, 5),
(1, 'Khoai Tây Chiên Lớn', 'Khoai tây chiên giòn, lớn', 70000, '/images/food/fries-l.jpg', 280, TRUE, 6),
(1, 'Hạt Dẻ Rang Muối', 'Hạt dẻ rang muối', 45000, '/images/food/cashew-salty.jpg', 350, TRUE, 7),
(1, 'Snack Khoai Tây', 'Snack khoai tây giòn', 55000, '/images/food/potato-chip.jpg', 320, TRUE, 8);

-- ============================================================================
-- 3. INSERT FOOD ITEMS - NƯỚC UỐNG
-- ============================================================================

INSERT INTO food_item (category_id, name, description, price, image_url, stock, is_available, display_order) VALUES
(2, 'Coca Cola Vừa', 'Coca Cola lạnh 300ml', 35000, '/images/food/coca-m.jpg', 600, TRUE, 1),
(2, 'Coca Cola Lớn', 'Coca Cola lạnh 500ml', 50000, '/images/food/coca-l.jpg', 580, TRUE, 2),
(2, 'Sprite Vừa', 'Sprite lạnh 300ml', 35000, '/images/food/sprite-m.jpg', 550, TRUE, 3),
(2, 'Sprite Lớn', 'Sprite lạnh 500ml', 50000, '/images/food/sprite-l.jpg', 520, TRUE, 4),
(2, 'Fanta Cam Vừa', 'Fanta cam lạnh', 30000, '/images/food/fanta-m.jpg', 500, TRUE, 5),
(2, 'Fanta Cam Lớn', 'Fanta cam lớn', 45000, '/images/food/fanta-l.jpg', 480, TRUE, 6),

(2, 'Nước Cam Tươi', 'Nước cam ép tươi', 55000, '/images/food/orange-juice.jpg', 200, TRUE, 7),
(2, 'Nước Dưa Hấu', 'Nước dưa hấu', 40000, '/images/food/watermelon-juice.jpg', 250, TRUE, 8),

(2, 'Cà Phê Đen Đá', 'Cà phê đen đá', 45000, '/images/food/coffee-black.jpg', 350, TRUE, 9),
(2, 'Cà Phê Sữa Đá', 'Cà phê sữa đá', 50000, '/images/food/coffee-milk.jpg', 380, TRUE, 10),

(2, 'Trà Xanh Lạnh', 'Trà xanh đá lạnh', 30000, '/images/food/green-tea.jpg', 300, TRUE, 11);

-- ============================================================================
-- 4. INSERT FOOD ITEMS - BÁNH & TRÁNG MIỆNG
-- ============================================================================

INSERT INTO food_item (category_id, name, description, price, image_url, stock, is_available, display_order) VALUES
(3, 'Bánh Mì Kẹp Phô Mai', 'Bánh mì kẹp phô mai', 65000, '/images/food/sandwich-cheese.jpg', 150, TRUE, 1),
(3, 'Bánh Mì Kẹp Gà', 'Bánh mì kẹp gà', 70000, '/images/food/sandwich-chicken.jpg', 140, TRUE, 2),

(3, 'Chocolate Almond', 'Chocolate Almond', 60000, '/images/food/chocolate-almond.jpg', 200, TRUE, 3),
(3, 'Chocolate Snickers', 'Chocolate Snickers', 55000, '/images/food/chocolate-snickers.jpg', 220, TRUE, 4),

(3, 'Kem Socola Vani', 'Kem socola + vani', 75000, '/images/food/ice-cream.jpg', 180, TRUE, 5),
(3, 'Kem Dâu', 'Kem dâu tươi', 70000, '/images/food/ice-cream-strawberry.jpg', 170, TRUE, 6),

(3, 'Bánh Brownie', 'Bánh brownie socola', 50000, '/images/food/brownie.jpg', 250, TRUE, 7),
(3, 'Bánh Cheesecake', 'Cheesecake New York', 65000, '/images/food/cheesecake.jpg', 120, TRUE, 8);

-- ============================================================================
-- 5. INSERT COMBO
-- ============================================================================

INSERT INTO food_item (category_id, name, description, price, image_url, stock, is_available, display_order) VALUES
(4, 'Combo Xem Phim Vừa', 'Bắp vừa + Coca vừa + bánh', 140000, '/images/food/combo-small.jpg', 999, TRUE, 1),
(4, 'Combo Xem Phim Lớn', 'Bắp lớn + Coca lớn + kem', 190000, '/images/food/combo-large.jpg', 999, TRUE, 2),
(4, 'Combo Đôi', 'Bắp x2 + Nước x2 + bánh', 280000, '/images/food/combo-couple.jpg', 500, TRUE, 3),
(4, 'Combo Gia Đình', 'Bắp x2 + Nước x4 + bánh x2', 480000, '/images/food/combo-family.jpg', 300, TRUE, 4);