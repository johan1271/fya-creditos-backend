package com.fya.creditos.service;

import com.fya.creditos.dto.CreditRequest;
import com.fya.creditos.dto.CreditResponse;
import com.fya.creditos.entity.Credit;
import com.fya.creditos.event.CreditRegisteredEvent;
import com.fya.creditos.mapper.CreditMapper;
import com.fya.creditos.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;
    private final ApplicationEventPublisher eventPublisher;

    public CreditResponse create(CreditRequest request) {
        Credit credit = creditMapper.toEntity(request);
        credit.setRegisteredAt(Instant.now());

        Credit saved = creditRepository.save(credit);

        eventPublisher.publishEvent(new CreditRegisteredEvent(
                saved.getId(),
                saved.getCustomerName(),
                saved.getCreditAmount(),
                saved.getSalesAgent(),
                saved.getRegisteredAt()
        ));

        return creditMapper.toResponse(saved);
    }

    public Page<CreditResponse> search(String q, Pageable pageable) {
        String term = (q == null) ? "" : q;
        return creditRepository.search(term, pageable).map(creditMapper::toResponse);
    }
}
