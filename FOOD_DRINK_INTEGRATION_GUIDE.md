# 🍿 FOOD & DRINK FEATURE - BACKEND INTEGRATION GUIDE

## **OVERVIEW**
This document describes the complete Food & Drink ordering system integrated with the Movie Ticket Booking system. The system allows users to order food/drinks when booking tickets and ensures food orders are preserved when tickets are transferred to other users.

---

## **1. DATABASE SETUP**

### Step 1: Run SQL Scripts in MySQL
```bash
# Using MySQL Command Line or Laragon
mysql -u root -p movie_ticket_booking < database/01_create_food_tables.sql
mysql -u root -p movie_ticket_booking < database/02_insert_sample_food_data.sql
```

### Step 2: Verify Tables Created
```sql
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'movie_ticket_booking' AND TABLE_NAME LIKE 'food_%';
```

**Expected tables:**
- `food_category` (4 categories)
- `food_item` (40+ products)
- `food_order`
- `food_order_item`

### Step 3: Check Sample Data
```sql
SELECT COUNT(*) as total_categories FROM food_category;
SELECT COUNT(*) as total_items FROM food_item;
```

---

## **2. BACKEND STRUCTURE - FILES CREATED**

### A. Entities (JPA Models)
```
src/main/java/com/datvexemphim/domain/entity/
├── FoodCategory.java       # Category of food/drinks
├── FoodItem.java           # Individual food product
├── FoodOrder.java          # User's food order (linked to payment)
└── FoodOrderItem.java      # Individual items in an order
```

**Key Relationships:**
```
Payment (1) ──────→ (N) FoodOrder
Ticket (1) ──────→ (N) FoodOrder
FoodOrder (1) ──────→ (N) FoodOrderItem
FoodItem (1) ──────→ (N) FoodOrderItem
```

### B. Repositories
```
src/main/java/com/datvexemphim/domain/repository/
├── FoodCategoryRepository.java
├── FoodItemRepository.java
├── FoodOrderRepository.java
└── FoodOrderItemRepository.java
```

### C. DTOs (Request/Response)
```
src/main/java/com/datvexemphim/api/dto/food/
├── FoodCategoryDTO.java
├── FoodItemDTO.java
├── FoodOrderDTO.java
├── FoodOrderItemDTO.java
└── CreateFoodOrderRequest.java
```

### D. Services
```
src/main/java/com/datvexemphim/service/
├── FoodCategoryService.java     # CRUD for food categories
├── FoodItemService.java         # CRUD for food items
├── FoodOrderService.java        # Core business logic (create, transfer, confirm)
├── TicketService.java           # UPDATED: calls FoodOrderService on transfer
└── PaymentService.java          # UPDATED: confirms food orders on payment success
```

### E. Controllers
```
src/main/java/com/datvexemphim/api/controller/
├── FoodController.java                    # User endpoints for food
└── admin/AdminFoodController.java         # Admin CRUD endpoints
```

---

## **3. API ENDPOINTS**

### User APIs - `/api/food`

#### Food Categories & Items (Browsing)
```
GET /api/food/categories                    # Get all active categories
GET /api/food/categories/{id}              # Get category by ID
GET /api/food/items                         # Get all available items
GET /api/food/items/category/{categoryId}  # Get items by category
GET /api/food/items/{id}                   # Get item by ID
```

#### Food Orders (User Can Create/View/Cancel)
```
POST   /api/food/orders                 # Create food order
GET    /api/food/orders                 # Get current user's food orders
GET    /api/food/orders/{id}            # Get food order details
GET    /api/food/orders/ticket/{ticketId} # Get food order by ticket
DELETE /api/food/orders/{id}            # Cancel food order
```

### Admin APIs - `/api/admin/food`

#### Categories Management
```
GET    /api/admin/food/categories           # Get all categories
GET    /api/admin/food/categories/{id}      # Get by ID
POST   /api/admin/food/categories           # Create new category
PUT    /api/admin/food/categories/{id}      # Update category
DELETE /api/admin/food/categories/{id}      # Delete category
```

#### Items Management
```
GET    /api/admin/food/items                    # Get all items
GET    /api/admin/food/items/{id}              # Get by ID
GET    /api/admin/food/items/category/{catId}  # Get by category
POST   /api/admin/food/items                    # Create new item
PUT    /api/admin/food/items/{id}              # Update item
DELETE /api/admin/food/items/{id}              # Delete item
```

---

## **4. CRITICAL DESIGN - TICKET TRANSFER WITH FOOD ORDERS**

### The Problem
When a ticket is transferred to another user, the food order should remain accessible to the new ticket owner, but the original buyer should still be able to track/refund their purchase.

### The Solution
**Food Order tracks TWO users:**
```java
FoodOrder {
    originalBuyer,    // Who placed the order (never changes)
    currentOwner,     // Who has the ticket now (changes on transfer)
    transferHistoryId // Links to transfer record
}
```

### Flow Diagram
```
User A books ticket + orders food
    ↓
FoodOrder {originalBuyer=A, currentOwner=A, status=PENDING}
    ↓
Payment succeeds
    ↓
FoodOrder {originalBuyer=A, currentOwner=A, status=CONFIRMED}
    ↓
User A transfers ticket to User B
    ↓ (TicketService calls FoodOrderService.handleTicketTransfer)
    ↓
FoodOrder {originalBuyer=A, currentOwner=B, status=CONFIRMED}
    ↓
Result: User B can pickup food with ticket, User A can still see order history
```

### Code Integration Points

**In TicketService.transfer():**
```java
// After transferring ticket ownership
TransferHistory savedTransferHistory = transferHistoryRepository.save(th);

// Update food order's current owner (CRITICAL!)
foodOrderService.handleTicketTransfer(
    t.getId(), 
    toUser.getId(), 
    savedTransferHistory.getId()
);
```

**In FoodOrderService.handleTicketTransfer():**
```java
@Transactional
public void handleTicketTransfer(Long ticketId, Long newOwnerId, Long transferHistoryId) {
    FoodOrder foodOrder = foodOrderRepository.findByTicket(...).orElse(null);
    if (foodOrder == null) return; // No food order for this ticket
    
    User newOwner = userRepository.findById(newOwnerId)...;
    
    // UPDATE: current owner = new owner, but keep original buyer unchanged
    foodOrder.setCurrentOwner(newOwner);
    foodOrder.setTransferHistoryId(transferHistoryId);
    foodOrderRepository.save(foodOrder);
}
```

---

## **5. PAYMENT & TOTAL CALCULATION**

### Payment Structure
```
Payment {
    amount,             # Total: tickets + food
    hasFoodOrder,       # Boolean flag
    foodOrderTotal,     # Food subtotal
    bookingCode,
    status,
    ...
}
```

### Payment Flow with Food
```
1. User creates food order
   FoodOrder {status=PENDING, totalPrice=200000}
   
2. Payment.hasFoodOrder = true
   Payment.foodOrderTotal = 200000
   
3. User pays
   PaymentService.simulate(success=true)
   
4. Payment succeeds
   amount = ticketAmount + foodOrderTotal
   FoodOrderService.confirmFoodOrder(paymentId)
   FoodOrder {status=CONFIRMED}
```

---

## **6. BUILDING & RUNNING**

### Step 1: Compile Backend
```bash
cd backend
.\mvnw.cmd clean compile
```

### Step 2: Build JAR
```bash
.\mvnw.cmd package -DskipTests
```

### Step 3: Run Backend
```bash
java -jar target/movie-ticket-booking-0.0.1-SNAPSHOT.jar
```

Backend starts at: `http://127.0.0.1:9090`

### Step 4: Test API
```bash
# Get food categories
curl http://127.0.0.1:9090/api/food/categories

# Should return 4 categories (Bắp & Snack, Nước Uống, etc.)
```

---

## **7. TESTING CHECKLIST**

### Test 1: Browse Food Menu
- [ ] GET `/api/food/categories` returns 4 categories
- [ ] GET `/api/food/items` returns 40+ products
- [ ] GET `/api/food/items/category/1` returns category items

### Test 2: Create Food Order (User)
```bash
POST /api/food/orders
{
    "paymentId": 1,
    "ticketId": 1,
    "items": [
        {"foodItemId": 1, "quantity": 2},
        {"foodItemId": 5, "quantity": 1}
    ],
    "notes": "No sugar in drink"
}
```
- [ ] Returns FoodOrderDTO with PENDING status
- [ ] Items calculated correctly
- [ ] Total price = (price1 * qty1) + (price2 * qty2)

### Test 3: Payment with Food Order
- [ ] Create food order (PENDING)
- [ ] Payment succeeds
- [ ] FoodOrder status changes to CONFIRMED
- [ ] Payment.amount includes food total

### Test 4: Transfer Ticket with Food
- [ ] User A has ticket + food order
- [ ] Transfer ticket to User B
- [ ] foodOrder.currentOwner = User B
- [ ] foodOrder.originalBuyer = User A (unchanged)
- [ ] User B can still see food order under their ticket

### Test 5: Cancel Food Order
- [ ] Create food order (PENDING)
- [ ] DELETE `/api/food/orders/{id}`
- [ ] FoodOrder status = CANCELLED
- [ ] Only original buyer can cancel

### Test 6: Admin CRUD
- [ ] POST `/api/admin/food/items` - create new item
- [ ] PUT `/api/admin/food/items/{id}` - update price/availability
- [ ] DELETE `/api/admin/food/items/{id}` - remove item

---

## **8. FRONTEND INTEGRATION (Next Phase)**

### Expected Frontend Changes
```
checkout.html (NEW)
├── Display food categories & items
├── Add items to cart
├── Show food subtotal
├── Combined total calculation (ticket + food)
└── Submit order with both ticket & food data

tickets.html (UPDATED)
├── Show food order details with ticket
├── Display original buyer (if transferred)
├── Show food pickup info
└── Allow cancel if PENDING
```

### Frontend API Calls
```javascript
// Get food menu
fetch('/api/food/categories').then(r => r.json())

// Create food order during checkout
fetch('/api/food/orders', {
    method: 'POST',
    body: JSON.stringify({
        paymentId: paymentId,
        ticketId: ticketId,
        items: [{foodItemId: 1, quantity: 2}]
    })
})

// Get food order by ticket
fetch(`/api/food/orders/ticket/${ticketId}`).then(r => r.json())
```

---

## **9. DATABASE QUERIES - USEFUL FOR DEBUGGING**

### Find all food orders for a user
```sql
SELECT fo.*, u.name, t.id as ticket_id, p.booking_code
FROM food_order fo
JOIN user u ON fo.original_buyer_id = u.id
JOIN ticket t ON fo.ticket_id = t.id
JOIN payment p ON fo.payment_id = p.id
WHERE fo.original_buyer_id = ? OR fo.current_owner_id = ?;
```

### Find transferred orders
```sql
SELECT fo.*, th.transfer_date, u1.name as from_user, u2.name as to_user
FROM food_order fo
LEFT JOIN transfer_history th ON fo.transfer_history_id = th.id
JOIN user u1 ON fo.original_buyer_id = u1.id
JOIN user u2 ON fo.current_owner_id = u2.id
WHERE fo.current_owner_id != fo.original_buyer_id;
```

### Calculate revenue by category
```sql
SELECT fc.name, SUM(foi.quantity) as total_items, SUM(foi.price_at_order * foi.quantity) as revenue
FROM food_order_item foi
JOIN food_item fi ON foi.food_item_id = fi.id
JOIN food_category fc ON fi.category_id = fc.id
WHERE foi.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY fc.id
ORDER BY revenue DESC;
```

---

## **10. TROUBLESHOOTING**

### Error: "No such table: food_category"
**Solution:** Run SQL create script:
```bash
mysql -u root -p movie_ticket_booking < database/01_create_food_tables.sql
```

### Error: "Duplicate entry for unique constraint"
**Solution:** Reset food data:
```sql
TRUNCATE TABLE food_order_item;
TRUNCATE TABLE food_order;
TRUNCATE TABLE food_item;
TRUNCATE TABLE food_category;
-- Then re-run 02_insert_sample_food_data.sql
```

### Food order not showing after ticket transfer
**Check:**
1. TicketService is calling `foodOrderService.handleTicketTransfer()`
2. TransferHistory is saved BEFORE calling handleTicketTransfer
3. FoodOrder exists in database for the ticket

### Payment amount not including food
**Check:**
1. Payment.hasFoodOrder = true
2. Payment.foodOrderTotal set correctly
3. FoodOrderService.confirmFoodOrder() called after payment SUCCESS

---

## **11. NEXT STEPS**

- [ ] Database tables created
- [ ] Backend compiled & running
- [ ] APIs working (test with curl/Postman)
- [ ] Transfer logic verified
- [ ] Frontend UI created
- [ ] End-to-end testing completed

---

**Status: Ready for Frontend Integration! 🎉**
