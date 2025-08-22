package com.nurlan.repository;

import com.nurlan.models.Post;
import com.nurlan.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    Optional<Post> findByUrlSlug(String urlSlug);

    boolean existsByUrlSlug(String urlSlug);

    Page<Post> findByPublishedTrueAndEnabledTrue(Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCaseAndPublishedTrueAndEnabledTrue(
            String q, Pageable pageable
    );

    Page<Post> findByAuthorUsernameOrderByIdDesc(String username, Pageable pageable);

    Page<Post> findAllByAuthor(User author, Pageable pageable);

    long countByAuthor(User author);

    @Query("""
       select distinct p from Post p
       join p.tags t
       where p.published = true
         and p.enabled   = true
         and lower(t.name) in :names
    """)
    Page<Post> findPublishedByAnyTag(@org.springframework.data.repository.query.Param("names") java.util.List<String> lowerNames,
                                     Pageable pageable);
}
