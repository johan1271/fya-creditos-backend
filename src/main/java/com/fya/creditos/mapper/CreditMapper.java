package com.fya.creditos.mapper;

import com.fya.creditos.dto.CreditRequest;
import com.fya.creditos.dto.CreditResponse;
import com.fya.creditos.entity.Credit;
import org.springframework.stereotype.Component;

@Component
public class CreditMapper {

    public Credit toEntity(CreditRequest request) {
        return Credit.builder()
                .customerName(request.customerName())
                .idNumber(request.idNumber())
                .creditAmount(request.creditAmount())
                .interestRate(request.interestRate())
                .termMonths(request.termMonths())
                .salesAgent(request.salesAgent())
                .build();
    }

    public CreditResponse toResponse(Credit credit) {
        return new CreditResponse(
                credit.getId(),
                credit.getCustomerName(),
                credit.getIdNumber(),
                credit.getCreditAmount(),
                credit.getInterestRate(),
                credit.getTermMonths(),
                credit.getSalesAgent(),
                credit.getRegisteredAt()
        );
    }
}
