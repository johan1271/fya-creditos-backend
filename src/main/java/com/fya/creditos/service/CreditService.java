package com.fya.creditos.service;

import com.fya.creditos.dto.CreditRequest;
import com.fya.creditos.dto.CreditResponse;
import com.fya.creditos.entity.Credit;
import com.fya.creditos.mapper.CreditMapper;
import com.fya.creditos.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;

    public CreditResponse create(CreditRequest request) {
        Credit credit = creditMapper.toEntity(request);
        credit.setRegisteredAt(LocalDateTime.now());

        Credit saved = creditRepository.save(credit);
        return creditMapper.toResponse(saved);
    }

    public Page<CreditResponse> search(String q, Pageable pageable) {
        return creditRepository.search(q, pageable).map(creditMapper::toResponse);
    }
}
