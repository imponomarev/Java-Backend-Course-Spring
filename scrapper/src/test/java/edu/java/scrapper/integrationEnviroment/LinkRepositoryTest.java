package edu.java.scrapper.integrationEnviroment;

import edu.java.domain.dto.LinkDto;
import edu.java.domain.repositories.LinkRepository;
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
class LinkRepositoryTest extends IntegrationTest {

    @Autowired
    private LinkRepository linkRepository;

    @Test
    @Transactional
    @Rollback
    void addLinkTest() {
        URI url = URI.create("https://github.com/imponomarev");
        LinkDto link = new LinkDto(null, url, null);
        linkRepository.addLink(link);

        Assertions.assertTrue(linkRepository.findLinkByUrl(url).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void removeLinkTest() {
        URI url = URI.create("https://github.com/imponomarev");
        LinkDto link = new LinkDto(null, url, null);
        linkRepository.addLink(link);
        Assertions.assertTrue(linkRepository.findLinkByUrl(url).isPresent());
        linkRepository.remove(url);
        Assertions.assertTrue(linkRepository.findLinkByUrl(url).isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void findAllLinksTest() {
        URI url1 = URI.create("https://github.com/imponomarev");
        URI url2 = URI.create("https://github.com/imponomarev2");
        LinkDto link1 = new LinkDto(null, url1, null);
        LinkDto link2 = new LinkDto(null, url2, null);
        linkRepository.addLink(link1);
        linkRepository.addLink(link2);
        List<LinkDto> linkDtoList = linkRepository.findAll();
        Assertions.assertEquals(List.of(url1, url2), List.of(linkDtoList.get(0).url(), linkDtoList.get(1).url()));
    }
}
