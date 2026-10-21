package com.paxier.spring_modulith_demo.inventory;

import com.paxier.spring_modulith_demo.inventory.internal.StockRepository;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

  private final StockRepository stock;

  @Transactional(readOnly = true)
  public InventoryItem getStock(int productId) {
    return new InventoryItem(productId, stock.quantity(productId));
  }

  @Transactional
  public InventoryItem addStock(int productId, int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    return new InventoryItem(productId, stock.add(productId, quantity));
  }

  // Runs inside the caller's transaction: if one item fails, the whole order rolls back.
  @Transactional(propagation = Propagation.MANDATORY)
  public void reserve(Collection<InventoryItem> items) {
    for (InventoryItem item : items) {
      if (!stock.decrease(item.productId(), item.quantity())) {
        throw new InsufficientStockException(item.productId(), item.quantity());
      }
    }
  }
}
