package edu.java.domain.jpa.repositories;

import edu.java.domain.jpa.model.Chat;
import edu.java.domain.jpa.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByLinksContaining(Link link);
}
