package edu.java.domain.jpa.repositories;

import edu.java.domain.jpa.model.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface JpaLinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findLinkByUrl(URI url);

    @Query("SELECT l FROM Link l WHERE l.lastCheck <= :thresholdTime ORDER BY l.lastCheck DESC")
    Page<Link> findOldLinksByThreshold(@Param("thresholdTime") OffsetDateTime thresholdTime, Pageable pageable);

}
