-- ============================================================================
-- FOOD & DRINK FEATURE - SQL SCRIPT
-- ============================================================================

-- ============================================================================
-- 1. FOOD CATEGORY
-- ============================================================================

CREATE TABLE IF NOT EXISTS food_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon VARCHAR(50),
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_active (is_active),
    INDEX idx_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- 2. FOOD ITEM
-- ============================================================================

CREATE TABLE IF NOT EXISTS food_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(500),
    stock INT DEFAULT 999,
    is_available BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_food_item_category FOREIGN KEY (category_id)
        REFERENCES food_category(id) ON DELETE RESTRICT,

    INDEX idx_category (category_id),
    INDEX idx_available (is_available),
    INDEX idx_price (price)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- 3. FOOD ORDER
-- ============================================================================

CREATE TABLE IF NOT EXISTS food_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    ticket_id BIGINT NOT NULL,
    original_buyer_id BIGINT NOT NULL,
    current_owner_id BIGINT NOT NULL,
    transfer_history_id BIGINT,
    total_price DECIMAL(10,2) NOT NULL,
    food_order_status ENUM('PENDING','CONFIRMED','CANCELLED') DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_food_order_payment FOREIGN KEY (payment_id)
        REFERENCES payments(id) ON DELETE CASCADE,

    CONSTRAINT fk_food_order_ticket FOREIGN KEY (ticket_id)
        REFERENCES tickets(id) ON DELETE CASCADE,

    CONSTRAINT fk_food_order_original_buyer FOREIGN KEY (original_buyer_id)
        REFERENCES users(id) ON DELETE RESTRICT,

    CONSTRAINT fk_food_order_owner FOREIGN KEY (current_owner_id)
        REFERENCES users(id) ON DELETE RESTRICT,

    INDEX idx_payment (payment_id),
    INDEX idx_ticket (ticket_id),
    INDEX idx_original_buyer (original_buyer_id),
    INDEX idx_current_owner (current_owner_id),
    INDEX idx_status (food_order_status),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- 4. FOOD ORDER ITEM
-- ============================================================================

CREATE TABLE IF NOT EXISTS food_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    food_order_id BIGINT NOT NULL,
    food_item_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price_at_order DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) GENERATED ALWAYS AS (quantity * price_at_order) STORED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_food_order_item_order FOREIGN KEY (food_order_id)
        REFERENCES food_order(id) ON DELETE CASCADE,

    CONSTRAINT fk_food_order_item_item FOREIGN KEY (food_item_id)
        REFERENCES food_item(id) ON DELETE RESTRICT,

    INDEX idx_food_order (food_order_id),
    INDEX idx_food_item (food_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- 5. UPDATE PAYMENTS TABLE
-- ============================================================================

ALTER TABLE payments 
ADD COLUMN has_food_order BOOLEAN DEFAULT FALSE;

ALTER TABLE payments 
ADD COLUMN food_order_total DECIMAL(10,2) DEFAULT 0;