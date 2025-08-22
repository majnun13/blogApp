package com.nurlan.repository;

import com.nurlan.models.Comment;
import com.nurlan.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{

    @EntityGraph(attributePaths = "user")
    Page<Comment> findByPostId(Long postId, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Comment> findByPostIdAndEnabledTrue(Long postId, Pageable pageable);

    long countByPostId(Long postId);

    long countByPostIdAndEnabledTrue(Long postId);

    int deleteByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = {"user","post"}) // N+1 yeme
    @Query(
            value = """
        SELECT c FROM Comment c
        LEFT JOIN c.user u
        LEFT JOIN c.post p
        WHERE (:enabled IS NULL OR c.enabled = :enabled)
          AND (:postId  IS NULL OR p.id = :postId)
          AND (
               :q IS NULL OR :q = '' OR
               LOWER(c.content) LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(p.title)   LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(u.username)LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(u.firstName)LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(u.lastName) LIKE LOWER(CONCAT('%', :q, '%'))
          )
        """,
            countQuery = """
        SELECT COUNT(c) FROM Comment c
        LEFT JOIN c.user u
        LEFT JOIN c.post p
        WHERE (:enabled IS NULL OR c.enabled = :enabled)
          AND (:postId  IS NULL OR p.id = :postId)
          AND (
               :q IS NULL OR :q = '' OR
               LOWER(c.content) LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(p.title)   LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(u.username)LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(u.firstName)LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(u.lastName) LIKE LOWER(CONCAT('%', :q, '%'))
          )
        """
    )
    Page<Comment> adminSearch(Boolean enabled, Long postId, String q, Pageable pageable);
}
