package edu.java.scrapper.integrationEnviroment;

import edu.java.domain.dto.ChatDto;
import edu.java.domain.repositories.ChatRepository;
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
@SpringBootTest
class ChatRepositoryTest extends IntegrationTest {

    @Autowired
    private ChatRepository chatRepository;

    @Test
    @Transactional
    @Rollback
    void addChatTest() {
        Long id = 100L;
        chatRepository.addChat(id);
        Assertions.assertTrue(chatRepository.findChatById(id).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    void removeChatTest() {
        Long id = 100L;
        chatRepository.addChat(id);
        Assertions.assertNotEquals(null, chatRepository.findChatById(id));
        chatRepository.remove(id);
        Assertions.assertTrue(chatRepository.findChatById(id).isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    void findAllChatsTest() {
        Long firstId = 100L;
        Long secondId = 101L;
        Long thirdId = 102L;
        chatRepository.addChat(firstId);
        chatRepository.addChat(secondId);
        chatRepository.addChat(thirdId);
        List<ChatDto> chatDtos = chatRepository.findAll();
        List<ChatDto> expected = List.of(new ChatDto(100L, null), new ChatDto(101L, null), new ChatDto(102L, null));
        List<ChatDto> actual = chatRepository.findAll().stream()
            .map(chatDto -> new ChatDto(chatDto.id(), null))
            .collect(Collectors.toList());
        Assertions.assertEquals(expected, actual);
    }
}
