package com.paxier.spring_modulith_demo.product.restcontroller;

import com.paxier.spring_modulith_demo.product.dto.Product;
import com.paxier.spring_modulith_demo.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
  private final ProductService service;

  @PostMapping
  public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(service.createProduct(product));
  }
}
