package edu.java.scrapper.integrationEnviroment.services.jbdc;

import edu.java.domain.jdbc.dto.ChatLinkDto;
import edu.java.domain.jdbc.dto.LinkDto;
import edu.java.domain.jdbc.repositories.JdbcChatLinkRepository;
import edu.java.domain.jdbc.repositories.JdbcChatRepository;
import edu.java.domain.jdbc.repositories.JdbcLinkRepository;
import edu.java.scrapper.integrationEnviroment.IntegrationTest;
import edu.java.services.jdbc.JdbcChatService;
import edu.java.services.jdbc.JdbcLinkService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Testcontainers
@SpringBootTest(properties = "app.database-access-type=jdbc")
public class JdbcLinkServiceTest extends IntegrationTest {

    @Autowired
    private JdbcChatRepository chatRepository;

    @Autowired
    private JdbcLinkRepository linkRepository;

    @Autowired
    private JdbcChatLinkRepository chatLinkRepository;

    @Autowired
    private JdbcChatService chatService;

    @Autowired
    private JdbcLinkService linkService;

    @Test
    @Transactional
    @Rollback
    void addChatLinkTest() {
        chatService.registerChat(123L);
        linkService.addLink(123L, URI.create("test"));
        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).isPresent());
        Assertions.assertTrue(chatLinkRepository.find(123L, linkRepository.getLinkId(URI.create("test"))).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void removeChatLinkTest() {
        chatService.registerChat(123L);
        linkService.addLink(123L, URI.create("test"));
        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).isPresent());

        Optional<ChatLinkDto> chatLinkDto = chatLinkRepository.find(123L, linkRepository.getLinkId(URI.create("test")));
        Assertions.assertTrue(chatLinkDto.isPresent());

        linkService.removeLink(123L, URI.create("test"));

        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).isEmpty());

        List<ChatLinkDto> chatLinkDtoList = chatLinkRepository.findAll();

        Assertions.assertFalse(chatLinkDtoList.contains(chatLinkDto));

    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveLinksOfRemovedChat() {
        chatService.registerChat(123L);
        linkService.addLink(123L, URI.create("test"));

        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).isPresent());

        Optional<ChatLinkDto> chatLinkDto = chatLinkRepository.find(123L, linkRepository.getLinkId(URI.create("test")));
        Assertions.assertTrue(chatLinkDto.isPresent());

        chatService.deleteChat(123L);
        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).isEmpty());

        List<ChatLinkDto> chatLinkDtoList = chatLinkRepository.findAll();
        Assertions.assertFalse(chatLinkDtoList.contains(chatLinkDto));

        Assertions.assertTrue(chatRepository.findChatById(123L).isEmpty());

    }

    @Test
    @Transactional
    @Rollback
    void getLinksTest() {
        chatService.registerChat(123L);
        linkService.addLink(123L, URI.create("test"));
        linkService.addLink(123L, URI.create("test2"));
        List<LinkDto> linkDtoList = linkService.getLinks(123L);
        Assertions.assertEquals(URI.create("test"), linkDtoList.get(0).url());
        Assertions.assertEquals(URI.create("test2"), linkDtoList.get(1).url());

    }

    @Test
    @Transactional
    @Rollback
    void getChatIdsOfLinkTest() {
        chatService.registerChat(123L);
        chatService.registerChat(1234L);
        linkService.addLink(123L, URI.create("test"));
        linkService.addLink(1234L, URI.create("test"));
        List<Long> linkDtoList = linkService.getChatIdsOfLink(linkRepository.getLinkId(URI.create("test")));
        Assertions.assertEquals(123L, linkDtoList.get(0));
        Assertions.assertEquals(1234L, linkDtoList.get(1));

    }

}
