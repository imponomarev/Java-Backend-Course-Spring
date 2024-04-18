package edu.java.scrapper.integrationEnviroment.repositories.jdbc;

import edu.java.domain.jdbc.dto.ChatDto;
import edu.java.domain.jdbc.repositories.JdbcChatRepository;
import edu.java.scrapper.integrationEnviroment.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import java.util.stream.Collectors;

@Testcontainers
@SpringBootTest(properties = "app.database-access-type=jdbc")
class JdbcChatRepositoryTest extends IntegrationTest {

    @Autowired
    private JdbcChatRepository jdbcChatRepository;

    @Test
    @Transactional
    @Rollback
    void addChatTest() {
        Long id = 100L;
        jdbcChatRepository.addChat(id);
        Assertions.assertTrue(jdbcChatRepository.findChatById(id).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void removeChatTest() {
        Long id = 100L;
        jdbcChatRepository.addChat(id);
        Assertions.assertNotEquals(null, jdbcChatRepository.findChatById(id));
        jdbcChatRepository.remove(id);
        Assertions.assertTrue(jdbcChatRepository.findChatById(id).isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void findAllChatsTest() {
        Long firstId = 100L;
        Long secondId = 101L;
        Long thirdId = 102L;
        jdbcChatRepository.addChat(firstId);
        jdbcChatRepository.addChat(secondId);
        jdbcChatRepository.addChat(thirdId);
        List<ChatDto> chatDtos = jdbcChatRepository.findAll();
        List<ChatDto> expected = List.of(new ChatDto(100L, null), new ChatDto(101L, null), new ChatDto(102L, null));
        List<ChatDto> actual = jdbcChatRepository.findAll().stream()
            .map(chatDto -> new ChatDto(chatDto.id(), null))
            .collect(Collectors.toList());
        Assertions.assertEquals(expected, actual);
    }
}
