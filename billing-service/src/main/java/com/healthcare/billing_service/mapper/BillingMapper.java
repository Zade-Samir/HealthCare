package com.healthcare.billing_service.mapper;

import com.healthcare.billing_service.dto.BillingRequestDTO;
import com.healthcare.billing_service.dto.BillingResponseDTO;
import com.healthcare.billing_service.entity.Billing;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BillingMapper {

    Billing requestDtoToBilling(BillingRequestDTO billingRequestDTO);

    BillingResponseDTO billingToResponseDto(Billing billing);
}
