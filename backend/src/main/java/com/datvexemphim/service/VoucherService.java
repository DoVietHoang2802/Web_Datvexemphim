package com.datvexemphim.service;

import com.datvexemphim.api.dto.admin.VoucherDTO;
import com.datvexemphim.api.dto.admin.VoucherUpsertRequest;
import com.datvexemphim.domain.entity.Voucher;
import com.datvexemphim.domain.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Transactional(readOnly = true)
    public List<VoucherDTO> findAll() {
        return voucherRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherDTO> findActive() {
        return voucherRepository.findByIsActiveTrue().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public VoucherDTO findById(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
        return toDto(voucher);
    }

    @Transactional
    public VoucherDTO create(VoucherUpsertRequest request) {
        // Check duplicate code
        if (voucherRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Mã voucher đã tồn tại");
        }

        Voucher voucher = new Voucher();
        updateVoucherFromRequest(voucher, request);
        voucher = voucherRepository.save(voucher);
        return toDto(voucher);
    }

    @Transactional
    public VoucherDTO update(Long id, VoucherUpsertRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));

        // Check duplicate code (excluding current voucher)
        voucherRepository.findByCode(request.getCode()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Mã voucher đã tồn tại");
            }
        });

        updateVoucherFromRequest(voucher, request);
        voucher = voucherRepository.save(voucher);
        return toDto(voucher);
    }

    @Transactional
    public void delete(Long id) {
        if (!voucherRepository.existsById(id)) {
            throw new RuntimeException("Voucher không tồn tại");
        }
        voucherRepository.deleteById(id);
    }

    @Transactional
    public VoucherDTO toggleActive(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
        voucher.setIsActive(!voucher.getIsActive());
        voucher = voucherRepository.save(voucher);
        return toDto(voucher);
    }

    @Transactional
    public Voucher applyVoucher(String code, Long orderAmount) {
        Voucher voucher = voucherRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Mã voucher không hợp lệ"));

        // Check if active
        if (!voucher.getIsActive()) {
            throw new RuntimeException("Mã voucher đã bị vô hiệu hóa");
        }

        // Check validity period
        Instant now = Instant.now();
        if (now.isBefore(voucher.getValidFrom()) || now.isAfter(voucher.getValidUntil())) {
            throw new RuntimeException("Mã voucher đã hết hạn");
        }

        // Check usage limit
        if (voucher.getUsedCount() >= voucher.getUsageLimit()) {
            throw new RuntimeException("Mã voucher đã được sử dụng hết");
        }

        // Check minimum order amount
        if (orderAmount < voucher.getMinOrderAmount()) {
            throw new RuntimeException("Đơn hàng tối thiểu " + voucher.getMinOrderAmount() + " VND");
        }

        // Increment usage count
        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);

        return voucher;
    }

    public Long calculateDiscount(Voucher voucher, Long orderAmount) {
        long discount = (orderAmount * voucher.getDiscountPercent()) / 100;
        return Math.min(discount, voucher.getMaxDiscount());
    }

    private void updateVoucherFromRequest(Voucher voucher, VoucherUpsertRequest request) {
        voucher.setCode(request.getCode().toUpperCase());
        voucher.setDescription(request.getDescription());
        voucher.setDiscountPercent(request.getDiscountPercent());
        voucher.setMaxDiscount(request.getMaxDiscount());
        voucher.setMinOrderAmount(request.getMinOrderAmount());
        voucher.setValidFrom(request.getValidFrom());
        voucher.setValidUntil(request.getValidUntil());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
    }

    private VoucherDTO toDto(Voucher v) {
        return new VoucherDTO(
                v.getId(), v.getCode(), v.getDescription(), v.getDiscountPercent(),
                v.getMaxDiscount(), v.getMinOrderAmount(), v.getValidFrom(), v.getValidUntil(),
                v.getUsageLimit(), v.getUsedCount(), v.getIsActive(), v.getCreatedAt()
        );
    }
}
