package edu.java.scrapper.integrationEnviroment.repositories.jdbc;

import edu.java.domain.jdbc.dto.LinkDto;
import edu.java.domain.jdbc.repositories.JdbcLinkRepository;
import edu.java.scrapper.integrationEnviroment.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.net.URI;
import java.util.List;

@Testcontainers
@SpringBootTest
class JdbcLinkRepositoryTest extends IntegrationTest {

    @Autowired
    private JdbcLinkRepository jdbcLinkRepository;

    @Test
    @Transactional
    @Rollback
    void addLinkTest() {
        URI url = URI.create("https://github.com/imponomarev");
        LinkDto link = new LinkDto(null, url, null, null);
        jdbcLinkRepository.addLink(link);

        Assertions.assertTrue(jdbcLinkRepository.findLinkByUrl(url).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void removeLinkTest() {
        URI url = URI.create("https://github.com/imponomarev");
        LinkDto link = new LinkDto(null, url, null, null);
        jdbcLinkRepository.addLink(link);
        Assertions.assertTrue(jdbcLinkRepository.findLinkByUrl(url).isPresent());
        jdbcLinkRepository.remove(url);
        Assertions.assertTrue(jdbcLinkRepository.findLinkByUrl(url).isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void findAllLinksTest() {
        URI url1 = URI.create("https://github.com/imponomarev");
        URI url2 = URI.create("https://github.com/imponomarev2");
        LinkDto link1 = new LinkDto(null, url1, null, null);
        LinkDto link2 = new LinkDto(null, url2, null, null);
        jdbcLinkRepository.addLink(link1);
        jdbcLinkRepository.addLink(link2);
        List<LinkDto> linkDtoList = jdbcLinkRepository.findAll();
        Assertions.assertEquals(List.of(url1, url2), List.of(linkDtoList.get(0).url(), linkDtoList.get(1).url()));
    }
}
