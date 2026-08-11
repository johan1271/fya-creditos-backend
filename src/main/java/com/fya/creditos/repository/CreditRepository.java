package com.fya.creditos.repository;

import com.fya.creditos.entity.Credit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CreditRepository extends JpaRepository<Credit, Long> {

    @Query("""
        SELECT c FROM Credit c
        WHERE :q IS NULL
           OR LOWER(c.customerName) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(c.idNumber) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(c.salesAgent) LIKE LOWER(CONCAT('%', :q, '%'))
        """)
    Page<Credit> search(@Param("q") String q, Pageable pageable);
}
