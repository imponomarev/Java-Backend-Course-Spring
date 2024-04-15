package edu.java.scrapper.integrationEnviroment.services.jpa;

import edu.java.domain.jpa.model.Chat;
import edu.java.domain.jpa.repositories.JpaChatRepository;
import edu.java.domain.jpa.repositories.JpaLinkRepository;
import edu.java.services.jpa.JpaChatService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.ArrayList;

@Testcontainers
@SpringBootTest
class JpaChatServiceTest {

    @Autowired
    private JpaChatRepository chatRepository;

    @Autowired
    private JpaLinkRepository linkRepository;

    @Autowired
    private JpaChatService chatService;

    @Test
    @Transactional
    @Rollback
    void shouldAddChatTest() {
        chatService.registerChat(123L);
        Assertions.assertTrue(chatRepository.findById(123L).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveChatTest() {
        chatService.registerChat(123L);
        Assertions.assertTrue(chatRepository.findById(123L).isPresent());
        Chat chat = chatRepository.findById(123L).get();
        chat.setLinks(new ArrayList<>());
        chatService.deleteChat(123L);
        Assertions.assertTrue(chatRepository.findById(123L).isEmpty());
    }

}
