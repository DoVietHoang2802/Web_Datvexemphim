package com.datvexemphim.api.controller.admin;

import com.datvexemphim.api.dto.admin.VoucherDTO;
import com.datvexemphim.api.dto.admin.VoucherUpsertRequest;
import com.datvexemphim.service.VoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vouchers")
public class AdminVoucherController {
    private final VoucherService voucherService;

    public AdminVoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ResponseEntity<List<VoucherDTO>> getAll() {
        return ResponseEntity.ok(voucherService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(voucherService.findById(id));
    }

    @PostMapping
    public ResponseEntity<VoucherDTO> create(@RequestBody VoucherUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voucherService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoucherDTO> update(@PathVariable Long id, @RequestBody VoucherUpsertRequest request) {
        return ResponseEntity.ok(voucherService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        voucherService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<VoucherDTO> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(voucherService.toggleActive(id));
    }
}
