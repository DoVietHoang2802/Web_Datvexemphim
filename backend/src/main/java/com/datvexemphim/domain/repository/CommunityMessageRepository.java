package com.datvexemphim.domain.repository;

import com.datvexemphim.domain.entity.CommunityMessage;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityMessageRepository extends JpaRepository<CommunityMessage, Long> {

    /** Lấy 100 tin mới nhất và eager sender để tránh lazy lỗi */
    @EntityGraph(attributePaths = {"sender"})
    List<CommunityMessage> findTop100ByOrderByCreatedAtDesc();
}
