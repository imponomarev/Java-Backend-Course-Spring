package edu.java.scrapper.integrationEnviroment.repositories.jpa;

import edu.java.domain.jpa.model.Chat;
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
import java.util.Arrays;
import java.util.List;

@Testcontainers
@SpringBootTest
class JpaChatRepositoryTest extends IntegrationTest {

    @Autowired
    private JpaChatRepository chatRepository;

    @Autowired
    private JpaLinkRepository linkRepository;

    @Test
    @Transactional
    @Rollback
    void shouldFindByChatIdTest() {
        Chat chat = new Chat();
        chat.setLinks(new ArrayList<>());
        chat.setId(123L);
        chatRepository.saveAndFlush(chat);
        Assertions.assertTrue(chatRepository.findById(123L).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveByChatIdTest() {
        Chat chat = new Chat();
        chat.setLinks(new ArrayList<>());
        chat.setId(123L);
        chatRepository.saveAndFlush(chat);
        Assertions.assertTrue(chatRepository.findById(123L).isPresent());
        chatRepository.deleteById(123L);
        Assertions.assertTrue(chatRepository.findById(1L).isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void shouldFindAllChatsWhichContainLinkTest() {

        Link link = new Link();
        link.setUrl(URI.create("url"));
        link.setChats(new ArrayList<>());
        link.setLastUpdate(OffsetDateTime.now());
        link.setLastCheck(OffsetDateTime.now());

        linkRepository.save(link);

        Chat chat = new Chat();
        chat.setLinks(new ArrayList<>());
        chat.setId(123L);

        link.addChat(chat);
        chatRepository.saveAndFlush(chat);

        Chat chat2 = new Chat();
        chat2.setLinks(new ArrayList<>());
        chat2.setId(1234L);

        link.addChat(chat2);
        chatRepository.saveAndFlush(chat2);

        List<Chat> foundChats = chatRepository.findAllByLinksContaining(link);

        List<Chat> expectedChats = Arrays.asList(chat, chat2);

        Assertions.assertIterableEquals(expectedChats, foundChats);
    }

}
