package edu.java.scrapper.integrationEnviroment;

import edu.java.domain.dto.ChatLinkDto;
import edu.java.domain.dto.LinkDto;
import edu.java.domain.repositories.ChatLinkRepository;
import edu.java.domain.repositories.ChatRepository;
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
class ChatLinkRepositoryTest extends IntegrationTest {

    @Autowired
    private ChatLinkRepository chatLinkRepository;
    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private ChatRepository chatRepository;

    @Test
    @Transactional
    @Rollback
    void addChatLinkTest() {
        Long chatId = 100L;
        chatRepository.addChat(chatId);

        URI url = URI.create("https://github.com/imponomarev");
        LinkDto link = new LinkDto(null, url, null, null);

        linkRepository.addLink(link);
        Long linkId = linkRepository.getLinkId(url);

        chatLinkRepository.add(chatId, linkId);
        Assertions.assertTrue(chatLinkRepository.find(chatId, linkId).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void removeChatLinkTest() {
        Long chatId = 100L;
        chatRepository.addChat(chatId);

        URI url = URI.create("https://github.com/imponomarev");
        LinkDto link = new LinkDto(null, url, null, null);

        linkRepository.addLink(link);
        Long linkId = linkRepository.getLinkId(url);

        chatLinkRepository.add(chatId, linkId);
        Assertions.assertTrue(chatLinkRepository.find(chatId, linkId).isPresent());

        chatLinkRepository.remove(chatId, linkId);
        Assertions.assertTrue(chatLinkRepository.find(chatId, linkId).isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void findAllChatLinkTest() {
        Long chatId1 = 100L;
        chatRepository.addChat(chatId1);

        URI url1 = URI.create("https://github.com/imponomarev");
        LinkDto link1 = new LinkDto(null, url1, null, null);

        linkRepository.addLink(link1);
        Long linkId1 = linkRepository.getLinkId(url1);

        Long chatId2 = 101L;
        chatRepository.addChat(chatId2);

        URI url2 = URI.create("https://github.com/imponomarev1");
        LinkDto link2 = new LinkDto(null, url2, null, null);

        linkRepository.addLink(link2);
        Long linkId2 = linkRepository.getLinkId(url2);

        chatLinkRepository.add(chatId1, linkId1);
        chatLinkRepository.add(chatId2, linkId2);

        List<ChatLinkDto> chatLinkDtos = chatLinkRepository.findAll();
        Assertions.assertEquals(chatLinkDtos.get(0).chatId(), chatId1);
        Assertions.assertEquals(chatLinkDtos.get(1).chatId(), chatId2);

    }

}
