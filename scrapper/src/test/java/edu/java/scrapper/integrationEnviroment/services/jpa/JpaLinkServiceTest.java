package edu.java.scrapper.integrationEnviroment.services.jpa;

import edu.java.domain.jdbc.dto.LinkDto;
import edu.java.domain.jpa.model.Chat;
import edu.java.domain.jpa.repositories.JpaChatRepository;
import edu.java.domain.jpa.repositories.JpaLinkRepository;
import edu.java.services.jpa.JpaChatService;
import edu.java.services.jpa.JpaLinkService;
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
class JpaLinkServiceTest {

    @Autowired
    private JpaChatRepository chatRepository;

    @Autowired
    private JpaLinkRepository linkRepository;

    @Autowired
    private JpaChatService chatService;

    @Autowired
    private JpaLinkService linkService;

    @Test
    @Transactional
    @Rollback
    void shouldAddChatLinkTest() {
        chatService.registerChat(123L);
        linkService.addLink(123L, URI.create("test"));
        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).isPresent());

        Chat chat = chatRepository.findById(123L).get();
        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).get().getChats().contains(chat));
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveChatLinkTest() {
        chatService.registerChat(123L);
        linkService.addLink(123L, URI.create("test"));
        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).isPresent());

        Chat chat = chatRepository.findById(123L).get();
        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).get().getChats().contains(chat));

        linkService.removeLink(123L, URI.create("test"));
        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).isEmpty());
        Assertions.assertTrue(chat.links.isEmpty());

    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveLinksOfRemovedChat() {
        chatService.registerChat(123L);
        linkService.addLink(123L, URI.create("test"));
        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).isPresent());

        Chat chat = chatRepository.findById(123L).get();
        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).get().getChats().contains(chat));

        chatService.deleteChat(123L);
        Assertions.assertTrue(linkRepository.findLinkByUrl(URI.create("test")).isEmpty());
        Assertions.assertTrue(chatRepository.findById(123L).isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void getLinksTest() {
        chatService.registerChat(123L);

        linkService.addLink(123L, URI.create("test"));
        linkService.addLink(123L, URI.create("test2"));
        linkService.addLink(123L, URI.create("test3"));

        List<LinkDto> links = linkService.getLinks(123L);
        Assertions.assertEquals("test", links.get(0).url().toString());
        Assertions.assertEquals("test2", links.get(1).url().toString());
        Assertions.assertEquals("test3", links.get(2).url().toString());

    }

    @Test
    @Transactional
    @Rollback
    void getChatIdsOfLinkTest() {
        chatService.registerChat(123L);
        chatService.registerChat(1234L);
        linkService.addLink(123L, URI.create("test"));
        linkService.addLink(1234L, URI.create("test"));
        List<Long> chatIds =
            linkService.getChatIdsOfLink(linkRepository.findLinkByUrl(URI.create("test")).get().getId());
        Assertions.assertEquals(chatIds.get(0), 123L);
        Assertions.assertEquals(chatIds.get(1), 1234L);
    }

}
