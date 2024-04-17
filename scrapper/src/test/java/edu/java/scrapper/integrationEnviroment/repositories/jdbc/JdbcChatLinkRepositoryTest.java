package edu.java.scrapper.integrationEnviroment.repositories.jdbc;

import edu.java.domain.jdbc.dto.ChatLinkDto;
import edu.java.domain.jdbc.dto.LinkDto;
import edu.java.domain.jdbc.repositories.JdbcChatLinkRepository;
import edu.java.domain.jdbc.repositories.JdbcChatRepository;
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
@SpringBootTest(properties = "app.database-access-type=jdbc")
class JdbcChatLinkRepositoryTest extends IntegrationTest {

    @Autowired
    private JdbcChatLinkRepository jdbcChatLinkRepository;
    @Autowired
    private JdbcLinkRepository jdbcLinkRepository;
    @Autowired
    private JdbcChatRepository jdbcChatRepository;

    @Test
    @Transactional
    @Rollback
    void addChatLinkTest() {
        Long chatId = 100L;
        jdbcChatRepository.addChat(chatId);

        URI url = URI.create("https://github.com/imponomarev");
        LinkDto link = new LinkDto(null, url, null, null);

        jdbcLinkRepository.addLink(link);
        Long linkId = jdbcLinkRepository.getLinkId(url);

        jdbcChatLinkRepository.add(chatId, linkId);
        Assertions.assertTrue(jdbcChatLinkRepository.find(chatId, linkId).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void removeChatLinkTest() {
        Long chatId = 100L;
        jdbcChatRepository.addChat(chatId);

        URI url = URI.create("https://github.com/imponomarev");
        LinkDto link = new LinkDto(null, url, null, null);

        jdbcLinkRepository.addLink(link);
        Long linkId = jdbcLinkRepository.getLinkId(url);

        jdbcChatLinkRepository.add(chatId, linkId);
        Assertions.assertTrue(jdbcChatLinkRepository.find(chatId, linkId).isPresent());

        jdbcChatLinkRepository.remove(chatId, linkId);
        Assertions.assertTrue(jdbcChatLinkRepository.find(chatId, linkId).isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void findAllChatLinkTest() {
        Long chatId1 = 100L;
        jdbcChatRepository.addChat(chatId1);

        URI url1 = URI.create("https://github.com/imponomarev");
        LinkDto link1 = new LinkDto(null, url1, null, null);

        jdbcLinkRepository.addLink(link1);
        Long linkId1 = jdbcLinkRepository.getLinkId(url1);

        Long chatId2 = 101L;
        jdbcChatRepository.addChat(chatId2);

        URI url2 = URI.create("https://github.com/imponomarev1");
        LinkDto link2 = new LinkDto(null, url2, null, null);

        jdbcLinkRepository.addLink(link2);
        Long linkId2 = jdbcLinkRepository.getLinkId(url2);

        jdbcChatLinkRepository.add(chatId1, linkId1);
        jdbcChatLinkRepository.add(chatId2, linkId2);

        List<ChatLinkDto> chatLinkDtos = jdbcChatLinkRepository.findAll();
        Assertions.assertEquals(chatLinkDtos.get(0).chatId(), chatId1);
        Assertions.assertEquals(chatLinkDtos.get(1).chatId(), chatId2);

    }

}
