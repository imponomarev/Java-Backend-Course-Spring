package edu.java.scrapper.integrationEnviroment.services.jbdc;

import edu.java.domain.jdbc.repositories.JdbcChatRepository;
import edu.java.scrapper.integrationEnviroment.IntegrationTest;
import edu.java.services.jdbc.JdbcChatService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
public class JdbcChatServiceTest extends IntegrationTest {

    @Autowired
    private JdbcChatRepository chatRepository;

    @Autowired
    private JdbcChatService chatService;

    @Test
    @Transactional
    @Rollback
    void registerChatTest() {
        chatService.registerChat(123L);
        Assertions.assertTrue(chatRepository.findChatById(123L).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void removeChatTest() {
        chatService.registerChat(123L);
        Assertions.assertTrue(chatRepository.findChatById(123L).isPresent());
        chatService.deleteChat(123L);
        Assertions.assertTrue(chatRepository.findChatById(123L).isEmpty());
    }

}
