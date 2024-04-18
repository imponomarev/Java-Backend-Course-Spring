package edu.java.domain.jpa.repositories;

import edu.java.domain.jpa.model.Chat;
import edu.java.domain.jpa.model.Link;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByLinksContaining(Link link);
}
