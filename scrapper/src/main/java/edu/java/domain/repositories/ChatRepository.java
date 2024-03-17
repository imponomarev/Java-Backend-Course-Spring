package edu.java.domain.repositories;

import edu.java.domain.dto.ChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRepository {

    private final JdbcClient jdbcClient;

    public void addChat(Long id) {
        String query = "INSERT INTO db.chat(id, created_At) VALUES (?, CURRENT_TIMESTAMP)";
        jdbcClient.sql(query)
            .params(id)
            .update();
    }

    public void remove(Long id) {
        String query = "DELETE FROM db.chat WHERE id = ?";
        jdbcClient.sql(query)
            .params(id)
            .update();
    }

    public List<ChatDto> findAll() {
        String query = "SELECT * FROM db.chat";
        return jdbcClient.sql(query)
            .query(ChatDto.class)
            .list();
    }

    public Optional<ChatDto> findChatById(Long id) {
        String query = "SELECT * FROM db.chat WHERE id = ?";
        return jdbcClient.sql(query)
            .param(id)
            .query(ChatDto.class)
            .optional();
    }
}
