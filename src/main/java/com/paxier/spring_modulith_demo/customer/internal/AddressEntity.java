package com.paxier.spring_modulith_demo.customer.internal;

import com.paxier.spring_modulith_demo.customer.AddressType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address", schema = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "customerEntity") // to avoid circular reference in toString
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    private String city;
    private String zipCode;

    @Builder.Default
    private AddressType type = AddressType.HOME;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customerEntity;
}

