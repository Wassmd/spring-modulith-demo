package com.paxier.spring_modulith_demo.inventory.internal;

import com.paxier.spring_modulith_demo.inventory.InventoryItem;
import com.paxier.spring_modulith_demo.inventory.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
class InventoryController {

  private final InventoryService inventory;

  @GetMapping("/{productId}")
  InventoryItem getStock(@PathVariable @Positive int productId) {
    return inventory.getStock(productId);
  }

  @PostMapping("/{productId}/stock")
//  @PreAuthorize("hasRole('admin')")
  InventoryItem addStock(@PathVariable @Positive int productId, @Valid @RequestBody StockAddition addition) {
    return inventory.addStock(productId, addition.quantity());
  }

  record StockAddition(@Positive int quantity) {
  }
}
