package com.datvexemphim.service.admin;

import com.datvexemphim.domain.enums.PaymentStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;

@Service
public class AdminStatsService {
    private final EntityManager em;

    public AdminStatsService(EntityManager em) {
        this.em = em;
    }

    public long totalRevenue() {
        TypedQuery<Long> q = em.createQuery(
                "select coalesce(sum(p.amount),0) from Payment p where p.status = :st",
                Long.class
        );
        q.setParameter("st", PaymentStatus.SUCCESS);
        return q.getSingleResult();
    }
}

