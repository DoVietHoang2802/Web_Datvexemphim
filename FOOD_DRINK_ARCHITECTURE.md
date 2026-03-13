# 🏗️ FOOD & DRINK FEATURE - ARCHITECTURE & FLOW DIAGRAMS

## **1. DATABASE ARCHITECTURE**

```sql
┌─────────────────────────────────────────────────────────────────────┐
│                         DATABASE SCHEMA                              │
├─────────────────────────────────────────────────────────────────────┤

food_category (ID: 1-4)
├── id (PK)
├── name (Bắp, Nước, Bánh, Combo)
├── description
├── icon
├── display_order
└── is_active

    ↓ (1 to N)

food_item (ID: 1-40+)
├── id (PK)
├── category_id (FK) → food_category
├── name (Bắp rang bơ, Coca lớn, etc.)
├── price (60000, 80000, etc.)
├── description
├── image_url
├── stock
├── is_available
└── display_order

    ↓ (1 to N)

food_order_item
├── id (PK)
├── food_order_id (FK)
├── food_item_id (FK) → food_item
├── quantity
├── price_at_order
└── subtotal (calculated)

    ↑ (1 to N)

food_order
├── id (PK)
├── payment_id (FK) → payment ⚡ CRITICAL LINK
├── ticket_id (FK) → ticket
├── original_buyer_id (FK) → user (WHO ORDERED - NEVER CHANGES)
├── current_owner_id (FK) → user (WHO HAS TICKET NOW)
├── transfer_history_id
├── total_price
├── food_order_status (PENDING/CONFIRMED/CANCELLED)
├── notes
└── created_at, updated_at

    ↑ (N to 1)

payment
├── id (PK)
├── amount ← INCLUDES food total ⚡ UPDATED
├── has_food_order (Boolean) ⚡ NEW
├── food_order_total (Decimal) ⚡ NEW
└── ...existing fields

ticket ← ALREADY EXISTS
├── id (PK)
├── showtime_id (FK)
├── seat_id (FK)
├── owner_id (FK) → user ← CHANGES ON TRANSFER
├── payment_id (FK) → payment
└── ...existing fields

transfer_history ← ALREADY EXISTS
├── id (PK)
├── ticket_id (FK)
├── from_user_id (FK) → user
├── to_user_id (FK) → user
└── transfer_date

user ← ALREADY EXISTS
├── id (PK)
├── name
├── email
└── ...existing fields
```

---

## **2. SYSTEM ARCHITECTURE**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      FRONTEND (HTML/JavaScript)                          │
├─────────────────────────────────────────────────────────────────────────┤
│
│  checkout.html                   tickets.html
│  ├── Display food menu           ├── Show food order details
│  ├── Add items to cart           ├── Show original buyer
│  ├── Calculate total             ├── Track food history
│  └── Submit order                └── Allow cancel if PENDING
│
└──────────────────────────────────────────────────────────────────────────┘
                                      ↓ API Calls
┌─────────────────────────────────────────────────────────────────────────┐
│                   BACKEND - CONTROLLERS (REST APIs)                      │
├─────────────────────────────────────────────────────────────────────────┤
│
│  FoodController (USER)                AdminFoodController (ADMIN)
│  ├── GET /api/food/categories         ├── CRUD Categories
│  ├── GET /api/food/items              ├── CRUD Items
│  ├── POST /api/food/orders            └── View all items/categories
│  ├── GET /api/food/orders
│  └── DELETE /api/food/orders/{id}
│
└──────────────────────────────────────────────────────────────────────────┘
                                      ↓
┌─────────────────────────────────────────────────────────────────────────┐
│                    BACKEND - SERVICES (Business Logic)                   │
├─────────────────────────────────────────────────────────────────────────┤
│
│  FoodCategoryService              FoodItemService
│  ├── getAllActive()               ├── getAllAvailable()
│  ├── getById()                    ├── getByCategory()
│  ├── create()                     ├── getById()
│  ├── update()                     ├── create()
│  └── delete()                     ├── update()
│                                   └── delete()
│
│  FoodOrderService ⭐ CORE
│  ├── createFoodOrder()            TicketService (UPDATED)
│  ├── confirmFoodOrder()           ├── transfer()
│  ├── cancelFoodOrder()            └── calls: handleTicketTransfer()
│  ├── getByTicket()
│  ├── getUserFoodOrders()          PaymentService (UPDATED)
│  └── handleTicketTransfer() 🔥    ├── simulate()
│      └── Updates currentOwner     └── calls: confirmFoodOrder()
│          on ticket transfer
│
└──────────────────────────────────────────────────────────────────────────┘
                                      ↓
┌─────────────────────────────────────────────────────────────────────────┐
│             BACKEND - REPOSITORIES (Database Access)                    │
├─────────────────────────────────────────────────────────────────────────┤
│
│  FoodCategoryRepository       FoodItemRepository
│  ├── findById()               ├── findById()
│  └── findAllActive()          ├── findAllAvailable()
│                               └── findByCategoryId()
│
│  FoodOrderRepository           FoodOrderItemRepository
│  ├── findByPayment()           ├── findByFoodOrder()
│  ├── findByTicket()            └── findByFoodOrderId()
│  ├── findByOriginalBuyer()
│  ├── findByCurrentOwner()
│  └── findByUserAsOwnerOrBuyer()
│
└──────────────────────────────────────────────────────────────────────────┘
                                      ↓
┌─────────────────────────────────────────────────────────────────────────┐
│                         MYSQL DATABASE                                   │
├─────────────────────────────────────────────────────────────────────────┤
│
│  food_category      food_item          food_order          food_order_item
│  food_order ← payment, ticket, user
│
└──────────────────────────────────────────────────────────────────────────┘
```

---

## **3. BOOKING & FOOD ORDER FLOW**

```
USER ACTION SEQUENCE:
═════════════════════════════════════════════════════════════════════════

Step 1: USER BROWSES SHOWTIMES
    ├─ GET /api/showtimes                    ✓ Existing
    └─ Displays available showtimes

Step 2: USER SELECTS SEATS
    ├─ GET /api/movies/{id}/showtimes/{id}/seatmap    ✓ Existing
    └─ Displays available seats

Step 3: USER CONFIRMS BOOKING
    ├─ POST /api/bookings
    │  └─ Create PENDING tickets (seats reserved)     ✓ Existing
    └─ Response: [Ticket1 (PENDING), Ticket2 (PENDING)]

Step 4: USER CHOOSES FOOD (NEW)
    ├─ GET /api/food/categories
    │  └─ Display all food categories + items
    ├─ GET /api/food/items/category/{id}
    │  └─ Display items in selected category
    └─ User adds items to food cart

Step 5: USER CREATES FOOD ORDER (NEW) ⭐
    ├─ POST /api/food/orders
    │  ├─ body: {paymentId, ticketId, items: [{foodItemId, qty}]}
    │  └─ FoodOrderService.createFoodOrder()
    │          ├─ Validate ticket belongs to user
    │          ├─ Validate food items available
    │          ├─ Calculate total price
    │          ├─ Create FoodOrder (PENDING)
    │          ├─ Create FoodOrderItems
    │          └─ Update Payment.hasFoodOrder = true
    ├─ Response: FoodOrderDTO
    └─ Food order now ATTACHED to payment

Step 6: USER REVIEWS & PAYS
    ├─ Total = Ticket Total + Food Total    ⭐ CALCULATED
    ├─ POST /api/payments/simulate
    │  ├─ body: {success: true, ticketIds}
    │  └─ PaymentService.simulate()
    │          ├─ Validate tickets PENDING
    │          ├─ Calculate amount = tickets + food ⭐ UPDATED
    │          ├─ Create Payment (SUCCESS)
    │          ├─ Update Tickets to CONFIRMED
    │          └─ Call FoodOrderService.confirmFoodOrder() ⭐
    │                  └─ FoodOrder.status = CONFIRMED
    └─ Payment confirmed, seats locked, food confirmed

Step 7: PAYMENT RESULT
    ├─ Success Path:
    │  ├─ Tickets: CONFIRMED ✓
    │  ├─ FoodOrders: CONFIRMED ✓
    │  └─ User can view in "My Tickets" ✓
    │
    └─ Failure Path:
       ├─ Tickets: Deleted (seats released)
       ├─ FoodOrders: Deleted (refunded)
       └─ User can retry booking

Step 8: USER VIEWS TICKET + FOOD
    ├─ GET /api/tickets/me
    │  └─ Lists all user's tickets
    └─ GET /api/food/orders/ticket/{ticketId}
       └─ Shows food order for that ticket
          ├─ originalBuyer: User A
          ├─ currentOwner: User A
          ├─ Items: [item1, item2]
          └─ Status: CONFIRMED

═════════════════════════════════════════════════════════════════════════
```

---

## **4. TICKET TRANSFER WITH FOOD ORDER FLOW**

```
CRITICAL SCENARIO: PASS TICKET TO ANOTHER USER
═════════════════════════════════════════════════════════════════════════

BEFORE TRANSFER:

Ticket (ID: 1)                      FoodOrder (ID: 1)
├─ owner: User A ✓                  ├─ originalBuyer: User A ✓
├─ seat: A1                         ├─ currentOwner: User A ✓
├─ showtime: 2026-03-15 20:00        ├─ items: [Bắp, Coca, Kem]
├─ status: CONFIRMED                ├─ totalPrice: 200,000d
├─ payment: Payment1                └─ status: CONFIRMED
└─ ...existing fields

    ↓ USER ACTION

USER A TRANSFERS TICKET TO USER B:
    POST /api/tickets/transfer
    ├─ body: {toEmail: "userb@example.com", ticketId: 1}
    └─ TicketService.transfer()
           ├─ Validate ticket status = CONFIRMED
           ├─ Validate showtime not started
           ├─ Validate recipient exists
           ├─ UPDATE: Ticket.owner = User B ⭐
           ├─ Create TransferHistory (record)
           │  ├─ ticket: 1
           │  ├─ from_user: User A
           │  ├─ to_user: User B
           │  └─ transfer_date: NOW
           │
           └─ 🔥 CRITICAL CALL:
              FoodOrderService.handleTicketTransfer(
                  ticketId=1,
                  newOwnerId=User B ID,
                  transferHistoryId=xxx
              )
              ├─ Find FoodOrder for ticket
              ├─ UPDATE: FoodOrder.currentOwner = User B ⭐
              ├─ UPDATE: FoodOrder.transferHistoryId = xxx
              └─ KEEP: FoodOrder.originalBuyer = User A ✓

    ↓ TRANSFER COMPLETES

AFTER TRANSFER:

Ticket (ID: 1)                      FoodOrder (ID: 1)
├─ owner: User B ✓ CHANGED!         ├─ originalBuyer: User A ✓ UNCHANGED
├─ seat: A1                         ├─ currentOwner: User B ✓ CHANGED!
├─ showtime: 2026-03-15 20:00        ├─ items: [Bắp, Coca, Kem]
├─ status: CONFIRMED                ├─ totalPrice: 200,000d
├─ payment: Payment1                ├─ transferHistoryId: xxx
└─ ...existing fields               └─ status: CONFIRMED (NOT PENDING!)

    ↓ RESULT

USER B PERSPECTIVE:
    ├─ GET /api/tickets/me
    │  └─ Shows Ticket 1 (CONFIRMED)
    ├─ GET /api/food/orders/ticket/1
    │  └─ Shows Food Order
    │     ├─ originalBuyer: User A (who ordered)
    │     ├─ currentOwner: User B (me - who has ticket now)
    │     ├─ Items: [Bắp, Coca, Kem]
    │     └─ Status: CONFIRMED (can pickup)
    └─ User B can pickup food at theater ✓

USER A PERSPECTIVE:
    ├─ GET /api/tickets/me
    │  └─ Does NOT show Ticket 1 (owner is User B now)
    ├─ GET /api/food/orders
    │  └─ Shows Food Order1
    │     ├─ originalBuyer: User A (ME)
    │     ├─ currentOwner: User B (transferred to)
    │     ├─ Items: [Bắp, Coca, Kem]
    │     └─ Status: CONFIRMED
    │     └─ transferHistoryId: xxx
    └─ User A can see order history & track who has food ✓

═════════════════════════════════════════════════════════════════════════
```

---

## **5. PAYMENT CALCULATION FLOW**

```
SCENARIO: BOOKING MOVIE + FOOD

Step 1: TICKET BOOKING
    Ticket Price: 120,000d/ticket × 2 tickets = 240,000d

Step 2: FOOD ORDER CREATION
    Food Items Selected:
    ├─ Bắp Rang Bơ Lớn (80,000d) × 2 = 160,000d
    ├─ Coca Cola Lớn (50,000d) × 1 = 50,000d
    ├─ Kem Dâu Tây (70,000d) × 1 = 70,000d
    └─ FOOD SUBTOTAL = 280,000d

Step 3: PAYMENT RECORD CREATED
    Payment {
        id: 1
        amount: ??? (TBD after all items added)
        has_food_order: true ⭐
        food_order_total: 280,000d ⭐
        status: PENDING
        ...existing fields
    }

Step 4: PAYMENT SIMULATION (CHECKOUT)
    POST /api/payments/simulate
    ├─ Calculate ticket total: 240,000d
    ├─ Get food order total: 280,000d
    ├─ TOTAL = 240,000d + 280,000d = 520,000d ⭐
    │
    ├─ Update Payment:
    │  ├─ amount = 520,000d ⭐ INCLUDES FOOD
    │  ├─ status = SUCCESS
    │  └─ paidAt = NOW
    │
    ├─ Update Tickets:
    │  └─ status = CONFIRMED (for each)
    │
    └─ Update FoodOrders:
       └─ status = CONFIRMED (via FoodOrderService)

Step 5: RECEIPT
    Booking Code: BK-ABC123DEF456
    
    Tickets:             120,000d × 2    = 240,000d
    ├─ Ticket 1 (Seat A1)
    └─ Ticket 2 (Seat A2)
    
    Food & Drink:                        = 280,000d
    ├─ Bắp Rang Bơ Lớn        80,000d × 2
    ├─ Coca Cola Lớn          50,000d × 1
    └─ Kem Dâu Tây            70,000d × 1
    
    ─────────────────────────────────────────
    TOTAL PAYMENT:                       520,000d ✓

═════════════════════════════════════════════════════════════════════════
```

---

## **6. STATUS TRANSITIONS**

```
TICKET STATUS:
    PENDING → CONFIRMED → CANCELLED (optional)
    
    Transition to PENDING: POST /api/bookings
    Transition to CONFIRMED: POST /api/payments/simulate (success=true)
    Transition to CANCELLED: DELETE /api/tickets/{id}

FOOD ORDER STATUS:
    PENDING → CONFIRMED (optional CANCELLED before confirm)
    
    Transition to PENDING: POST /api/food/orders
    Transition to CONFIRMED: Payment succeeds (PaymentService calls FoodOrderService)
    Transition to CANCELLED: DELETE /api/food/orders/{id} (only if PENDING)

PAYMENT STATUS:
    PENDING → SUCCESS (or FAILED)
    
    Transition to SUCCESS: POST /api/payments/simulate (success=true)
    Transition to FAILED: POST /api/payments/simulate (success=false)
```

---

## **7. ROLES & PERMISSIONS**

```
ANONYMOUS USER:
├─ GET /api/food/categories             ✓ Browse menu
├─ GET /api/food/items                  ✓ Browse items
└─ POST /api/food/orders                ✗ MUST BE AUTHENTICATED

AUTHENTICATED USER:
├─ POST /api/food/orders                ✓ Create own orders
├─ GET /api/food/orders                 ✓ View own orders
├─ GET /api/food/orders/{id}            ✓ View own order details
├─ GET /api/food/orders/ticket/{id}     ✓ View food for own ticket
├─ DELETE /api/food/orders/{id}         ✓ Cancel own PENDING orders
└─ Transfer ticket with food            ✓ Ticket transfer auto-updates food

ADMIN USER:
├─ POST /api/admin/food/categories      ✓ Create category
├─ PUT /api/admin/food/categories/{id}  ✓ Update category
├─ DELETE /api/admin/food/categories/{id} ✓ Delete category
├─ POST /api/admin/food/items           ✓ Create item
├─ PUT /api/admin/food/items/{id}       ✓ Update item
├─ DELETE /api/admin/food/items/{id}    ✓ Delete item
└─ GET /api/admin/food/...              ✓ View all items/categories
```

---

## **8. DATA VALIDATION RULES**

```
FOOD ORDER CREATION:
✓ Ticket must exist and belong to current user
✓ Payment must exist
✓ All food items must exist and be available
✓ Quantity must be > 0
✗ Cannot order expired food items
✗ Cannot order if quantity > stock

FOOD ORDER CANCELLATION:
✓ Only original buyer can cancel
✓ Only PENDING orders can be cancelled
✗ CONFIRMED orders cannot be cancelled (ask refund request)

TICKET TRANSFER WITH FOOD:
✓ Ticket must be CONFIRMED
✓ Showtime must not have started
✓ Recipient must exist
✓ Food order (if exists) auto-transfers currentOwner
✗ Cannot transfer to yourself
✗ Cannot transfer if showtime started
```

---

## **9. TRANSACTION SAFETY**

```
All operations that modify multiple tables are @Transactional:

CREATE FOOD ORDER:
├─ Create FoodOrder            }
├─ Create FoodOrderItems       } All or nothing
├─ Update Payment              }

PAYMENT SIMULATION:
├─ Create Payment              }
├─ Update Tickets              }
├─ Confirm FoodOrders          } All or nothing
└─ Update Payment with total   }

TICKET TRANSFER:
├─ Update Ticket owner         }
├─ Create TransferHistory      } All or nothing
└─ Update FoodOrder owner      }

If ANY step fails → ENTIRE transaction rolled back ✓
```

---

**Status: Architecture & Flows Complete! 🏗️**
**All diagrams explain the integration between Food & Tickets & Transfer! ✓**
