package edu.java.scrapper.integrationEnviroment.repositories.jpa;

import edu.java.domain.jpa.model.Link;
import edu.java.domain.jpa.repositories.JpaChatRepository;
import edu.java.domain.jpa.repositories.JpaLinkRepository;
import edu.java.scrapper.integrationEnviroment.IntegrationTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@Testcontainers
@SpringBootTest
class JpaLinkRepositoryTest extends IntegrationTest {

    @Autowired
    private JpaChatRepository chatRepository;

    @Autowired
    private JpaLinkRepository linkRepository;

    @Test
    @Transactional
    @Rollback
    void shouldFindByUrlTest() {
        Link link = new Link();
        link.setUrl(URI.create("test"));
        link.setChats(new ArrayList<>());
        link.setLastUpdate(OffsetDateTime.now());
        link.setLastCheck(OffsetDateTime.now());

        linkRepository.saveAndFlush(link);

        Optional<Link> foundLink = linkRepository.findLinkByUrl(URI.create("test"));

        Assertions.assertTrue(foundLink.isPresent());
        Assertions.assertTrue(linkRepository.findById(foundLink.get().getId()).isPresent());
    }

}
