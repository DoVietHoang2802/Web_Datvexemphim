package com.datvexemphim.service;

import com.datvexemphim.api.dto.food.CreateFoodOrderRequest;
import com.datvexemphim.api.dto.food.FoodOrderDTO;
import com.datvexemphim.api.dto.food.FoodOrderItemDTO;
import com.datvexemphim.domain.entity.*;
import com.datvexemphim.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FoodOrderService {
    
    private final FoodOrderRepository foodOrderRepository;
    private final FoodOrderItemRepository foodOrderItemRepository;
    private final FoodItemRepository foodItemRepository;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    
    public FoodOrderService(FoodOrderRepository foodOrderRepository,
                           FoodOrderItemRepository foodOrderItemRepository,
                           FoodItemRepository foodItemRepository,
                           PaymentRepository paymentRepository,
                           TicketRepository ticketRepository,
                           UserRepository userRepository,
                           CurrentUserService currentUserService) {
        this.foodOrderRepository = foodOrderRepository;
        this.foodOrderItemRepository = foodOrderItemRepository;
        this.foodItemRepository = foodItemRepository;
        this.paymentRepository = paymentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }
    
    // ============ PUBLIC METHODS ============
    
    /**
     * Create food order from checkout
     * Called when user adds food items to their booking
     */
    @Transactional
    public FoodOrderDTO createFoodOrder(CreateFoodOrderRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        
        // Validate ticket exists and belongs to current user
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vé xem phim không tìm thấy"
                ));
        
        if (!ticket.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Bạn không có quyền tạo đơn đồ ăn cho vé này"
            );
        }
        
        // Validate payment exists
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Hóa đơn thanh toán không tìm thấy"
                ));
        
        // Calculate total price
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<FoodOrderItem> items = new java.util.ArrayList<>();
        
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (CreateFoodOrderRequest.FoodOrderItemRequest itemRequest : request.getItems()) {
                FoodItem foodItem = foodItemRepository.findById(itemRequest.getFoodItemId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Sản phẩm " + itemRequest.getFoodItemId() + " không tìm thấy"
                        ));
                
                if (!foodItem.getIsAvailable()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Sản phẩm " + foodItem.getName() + " không còn phục vụ"
                    );
                }
                
                if (itemRequest.getQuantity() <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0"
                    );
                }
                
                BigDecimal itemSubtotal = foodItem.getPrice()
                        .multiply(new BigDecimal(itemRequest.getQuantity()));
                totalPrice = totalPrice.add(itemSubtotal);
                
                FoodOrderItem orderItem = new FoodOrderItem();
                orderItem.setFoodItem(foodItem);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setPriceAtOrder(foodItem.getPrice());
                items.add(orderItem);
            }
        }
        
        // Create food order
        FoodOrder foodOrder = new FoodOrder();
        foodOrder.setPayment(payment);
        foodOrder.setTicket(ticket);
        foodOrder.setOriginalBuyer(currentUser);
        foodOrder.setCurrentOwner(currentUser);
        foodOrder.setTotalPrice(totalPrice);
        foodOrder.setFoodOrderStatus(FoodOrder.FoodOrderStatus.PENDING);
        foodOrder.setNotes(request.getNotes());
        
        FoodOrder savedOrder = foodOrderRepository.save(foodOrder);
        
        // Add items to food order
        for (FoodOrderItem item : items) {
            item.setFoodOrder(savedOrder);
            foodOrderItemRepository.save(item);
        }
        
        // Update payment with food order info
        payment.setHasFoodOrder(true);
        payment.setFoodOrderTotal(totalPrice);
        paymentRepository.save(payment);
        
        return convertToDTO(savedOrder);
    }
    
    /**
     * Confirm food order when payment is confirmed
     */
    @Transactional
    public void confirmFoodOrder(Long paymentId) {
        // Find payment first
        var payment = paymentRepository.findById(paymentId);
        if (payment.isEmpty()) {
            return; // No payment found, skip food order confirmation
        }
        
        // Find and confirm all food orders for this payment
        List<FoodOrder> foodOrders = foodOrderRepository.findByPayment(payment.get());
        
        for (FoodOrder order : foodOrders) {
            order.setFoodOrderStatus(FoodOrder.FoodOrderStatus.CONFIRMED);
            foodOrderRepository.save(order);
        }
    }
    
    /**
     * Cancel food order
     */
    @Transactional
    public FoodOrderDTO cancelFoodOrder(Long id) {
        FoodOrder foodOrder = foodOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Đơn đồ ăn không tìm thấy"
                ));
        
        User currentUser = currentUserService.getCurrentUser();
        
        // Only original buyer can cancel
        if (!foodOrder.getOriginalBuyer().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Bạn không có quyền hủy đơn đồ ăn này"
            );
        }
        
        if (!foodOrder.getFoodOrderStatus().equals(FoodOrder.FoodOrderStatus.PENDING)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Chỉ có thể hủy đơn đồ ăn ở trạng thái chờ thanh toán"
            );
        }
        
        foodOrder.setFoodOrderStatus(FoodOrder.FoodOrderStatus.CANCELLED);
        FoodOrder updated = foodOrderRepository.save(foodOrder);
        
        return convertToDTO(updated);
    }
    
    /**
     * Get food order by ticket
     */
    @Transactional(readOnly = true)
    public FoodOrderDTO getByTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vé xem phim không tìm thấy"
                ));
        
        return foodOrderRepository.findByTicket(ticket)
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    /**
     * Get all food orders of current user (as original buyer or current owner)
     */
    @Transactional(readOnly = true)
    public List<FoodOrderDTO> getUserFoodOrders() {
        User currentUser = currentUserService.getCurrentUser();
        List<FoodOrder> orders = foodOrderRepository.findByUserAsOwnerOrOriginalBuyer(currentUser);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * CRITICAL: Handle ticket transfer - update current owner but keep original buyer
     * This ensures food order tracking remains intact after ticket pass
     */
    @Transactional
    public void handleTicketTransfer(Long ticketId, Long newOwnerId, Long transferHistoryId) {
        FoodOrder foodOrder = foodOrderRepository.findByTicket(
                ticketRepository.findById(ticketId).orElse(null)
        ).orElse(null);
        
        if (foodOrder == null) {
            return;
        }
        
        User newOwner = userRepository.findById(newOwnerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Người dùng không tìm thấy"
                ));
        
        // UPDATE: current owner = new owner, but keep original buyer unchanged
        foodOrder.setCurrentOwner(newOwner);
        foodOrder.setTransferHistoryId(transferHistoryId);
        foodOrderRepository.save(foodOrder);
    }
    
    /**
     * Get food order by ID
     */
    @Transactional(readOnly = true)
    public FoodOrderDTO getById(Long id) {
        FoodOrder foodOrder = foodOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Đơn đồ ăn không tìm thấy"
                ));
        return convertToDTO(foodOrder);
    }
    
    // ============ PRIVATE METHODS ============
    
    private FoodOrderDTO convertToDTO(FoodOrder foodOrder) {
        List<FoodOrderItemDTO> itemDTOs = foodOrder.getItems() != null ?
                foodOrder.getItems().stream()
                        .map(this::convertItemToDTO)
                        .collect(Collectors.toList())
                : List.of();
        
        FoodOrderDTO dto = new FoodOrderDTO();
        dto.setId(foodOrder.getId());
        dto.setPaymentId(foodOrder.getPayment().getId());
        dto.setTicketId(foodOrder.getTicket().getId());
        dto.setOriginalBuyerId(foodOrder.getOriginalBuyer().getId());
        dto.setOriginalBuyerName(foodOrder.getOriginalBuyer().getFullName());
        dto.setCurrentOwnerId(foodOrder.getCurrentOwner().getId());
        dto.setCurrentOwnerName(foodOrder.getCurrentOwner().getFullName());
        dto.setTransferHistoryId(foodOrder.getTransferHistoryId());
        dto.setTotalPrice(foodOrder.getTotalPrice());
        dto.setFoodOrderStatus(foodOrder.getFoodOrderStatus().toString());
        dto.setNotes(foodOrder.getNotes());
        dto.setCreatedAt(foodOrder.getCreatedAt());
        dto.setUpdatedAt(foodOrder.getUpdatedAt());
        dto.setItems(itemDTOs);
        return dto;
    }
    
    private FoodOrderItemDTO convertItemToDTO(FoodOrderItem item) {
        FoodOrderItemDTO dto = new FoodOrderItemDTO();
        dto.setId(item.getId());
        dto.setFoodOrderId(item.getFoodOrder().getId());
        dto.setFoodItemId(item.getFoodItem().getId());
        dto.setFoodItemName(item.getFoodItem().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPriceAtOrder(item.getPriceAtOrder());
        dto.setSubtotal(item.getSubtotal());
        dto.setCreatedAt(item.getCreatedAt());
        return dto;
    }
}
