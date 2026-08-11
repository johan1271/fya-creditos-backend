package com.fya.creditos.controller;

import com.fya.creditos.dto.CreditRequest;
import com.fya.creditos.dto.CreditResponse;
import com.fya.creditos.dto.PagedResponse;
import com.fya.creditos.service.CreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @PostMapping
    public ResponseEntity<CreditResponse> create(@Valid @RequestBody CreditRequest request) {
        CreditResponse response = creditService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public PagedResponse<CreditResponse> search(@RequestParam(required = false) String q, @ParameterObject Pageable pageable) {
        return PagedResponse.from(creditService.search(q, pageable));
    }
}
