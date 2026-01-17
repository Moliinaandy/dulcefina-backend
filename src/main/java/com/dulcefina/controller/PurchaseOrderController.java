package com.dulcefina.controller;

import com.dulcefina.entity.PurchaseOrder;
import com.dulcefina.repository.PurchaseOrderRepository;
import com.dulcefina.repository.SupplierRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderRepository poRepo;
    private final SupplierRepository supplierRepo;

    public PurchaseOrderController(PurchaseOrderRepository poRepo, SupplierRepository supplierRepo) {
        this.poRepo = poRepo;
        this.supplierRepo = supplierRepo;
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> all() {
        return ResponseEntity.ok(poRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return poRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PurchaseOrder po) {
        if (po.getSupplier() != null && po.getSupplier().getSupplierId() != null) {
            supplierRepo.findById(po.getSupplier().getSupplierId())
                    .orElseThrow(() -> new NoSuchElementException("Supplier not found"));
        }
        PurchaseOrder saved = poRepo.save(po);
        return ResponseEntity.created(URI.create("/api/purchase-orders/" + saved.getPoId())).body(saved);
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @PathVariable String status) {
        PurchaseOrder po = poRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("PO not found"));
        po.setStatus(Enum.valueOf(com.dulcefina.entity.PurchaseStatus.class, status.toUpperCase()));
        return ResponseEntity.ok(poRepo.save(po));
    }
}
