package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.TransferHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Long> {
    
    @Query("SELECT th FROM TransferHistory th WHERE th.ticket.id = :ticketId")
    List<TransferHistory> findByTicketId(@Param("ticketId") Long ticketId);

    void deleteByTicketId(Long ticketId);
}

