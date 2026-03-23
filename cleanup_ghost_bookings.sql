-- ============================================
-- CLEANUP SCRIPT - Remove Ghost PENDING Tickets
-- ============================================

-- Delete food order items from failed payments
DELETE FROM food_order_item WHERE food_order_id IN (
  SELECT id FROM food_order WHERE payment_id IN (
    SELECT id FROM payments WHERE status = 'FAILED'
  )
);

-- Delete food orders from failed payments
DELETE FROM food_order WHERE payment_id IN (
  SELECT id FROM payments WHERE status = 'FAILED'
);

-- Delete all FAILED payments
DELETE FROM payments WHERE status = 'FAILED';

-- Delete all PENDING tickets (ghost bookings)
DELETE FROM tickets WHERE status = 'PENDING';

-- Verify cleanup
SELECT 'PENDING tickets remaining:' as info, COUNT(*) FROM tickets WHERE status = 'PENDING';
SELECT 'FAILED payments remaining:' as info, COUNT(*) FROM payments WHERE status = 'FAILED';
SELECT 'Food orders with no payment:' as info, COUNT(*) FROM food_order WHERE payment_id IS NULL;
