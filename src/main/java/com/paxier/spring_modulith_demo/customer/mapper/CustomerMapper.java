package com.paxier.spring_modulith_demo.customer.mapper;

import com.paxier.spring_modulith_demo.customer.Address;
import com.paxier.spring_modulith_demo.customer.entity.CustomerEntity;
import com.paxier.spring_modulith_demo.customer.model.Customer;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CustomerMapper {

  @Mapping(target = "id", ignore = true)
  public abstract CustomerEntity toEntity(Customer customer);

  public abstract Customer toModel(CustomerEntity customerEntity);

  /**
   * After mapping hook to ensure bidirectional association is set:
   * each Address in the CustomerEntity.addresses list will have its
   * customerEntity field set to the owning CustomerEntity.
   */

  @AfterMapping
  protected void setCustomerInAddresses(@MappingTarget CustomerEntity customerEntity) {
    if (customerEntity.getAddresses() == null) {
      return;
    }
    for (Address address : customerEntity.getAddresses()) {
      if (address != null) {
        address.setCustomerEntity(customerEntity);
      }
    }
  }
}
