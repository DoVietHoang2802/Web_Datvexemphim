package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.CommunityMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityMessageRepository extends JpaRepository<CommunityMessage, Long> {

    /** Tin nhắn mới nhất - giới hạn 100 tin */
    @Query("SELECT c FROM CommunityMessage c ORDER BY c.createdAt DESC LIMIT 100")
    List<CommunityMessage> findLatest();

    /** Tin nhắn sau 1 thời điểm (cho polling) */
    @Query("SELECT c FROM CommunityMessage c WHERE c.createdAt > :since ORDER BY c.createdAt ASC")
    List<CommunityMessage> findAfter(@Param("since") java.time.Instant since);
}
