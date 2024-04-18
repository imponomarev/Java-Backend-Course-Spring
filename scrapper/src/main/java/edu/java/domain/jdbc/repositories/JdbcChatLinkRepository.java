package edu.java.domain.jdbc.repositories;

import edu.java.domain.jdbc.dto.ChatLinkDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcChatLinkRepository {

    private final JdbcClient jdbcClient;

    public void add(Long chatId, Long linkId) {
        String query = "INSERT INTO db.chat_link_association (chat_id, link_id) VALUES (?, ?)";
        jdbcClient.sql(query)
            .params(chatId, linkId)
            .update();
    }

    public void remove(Long chatId, Long linkId) {
        String query = "DELETE FROM db.chat_link_association WHERE chat_id = ? AND link_id = ?";
        jdbcClient.sql(query)
            .params(chatId, linkId)
            .update();
    }

    public List<ChatLinkDto> findAll() {
        String query = "SELECT * FROM db.chat_link_association";
        return jdbcClient.sql(query)
            .query(ChatLinkDto.class)
            .list();
    }

    public List<ChatLinkDto> findAllByChatId(Long chatId) {
        String query = "SELECT * FROM db.chat_link_association WHERE chat_id = ?";
        return jdbcClient.sql(query)
            .params(chatId)
            .query(ChatLinkDto.class)
            .list();
    }

    public List<ChatLinkDto> findAllByLinkId(Long linkId) {
        String query = "SELECT * FROM db.chat_link_association WHERE link_id = ?";
        return jdbcClient.sql(query)
            .params(linkId)
            .query(ChatLinkDto.class)
            .list();
    }

    public Optional<ChatLinkDto> find(Long chatId, Long linkId) {
        String query = "SELECT * FROM db.chat_link_association WHERE chat_id = ? AND link_id = ?";
        return jdbcClient.sql(query)
            .params(chatId, linkId)
            .query(ChatLinkDto.class)
            .optional();
    }
}
