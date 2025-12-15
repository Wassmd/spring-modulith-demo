package com.paxier.spring_modulith_demo.customer.mapper;

import com.paxier.spring_modulith_demo.customer.entity.CustomerEntity;
import com.paxier.spring_modulith_demo.customer.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CustomerMapper {

  @Mapping(target = "id", ignore = true)
  public abstract CustomerEntity toEntity(Customer customer);

  public abstract Customer toModel(CustomerEntity customerEntity);
}
