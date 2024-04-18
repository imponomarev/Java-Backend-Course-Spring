package edu.java.configuration;

import edu.java.domain.jdbc.repositories.JdbcChatLinkRepository;
import edu.java.domain.jdbc.repositories.JdbcChatRepository;
import edu.java.domain.jdbc.repositories.JdbcLinkRepository;
import edu.java.services.ChatService;
import edu.java.services.LinkService;
import edu.java.services.jdbc.JdbcChatService;
import edu.java.services.jdbc.JdbcLinkService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    @Bean
    public LinkService linkService(
        JdbcLinkRepository jdbcLinkRepository,
        JdbcChatLinkRepository jdbcChatLinkRepository,
        JdbcChatRepository jdbcChatRepository
    ) {
        return new JdbcLinkService(jdbcLinkRepository, jdbcChatLinkRepository, jdbcChatRepository);
    }

    @Bean
    public ChatService chatService(
        JdbcChatLinkRepository jdbcChatLinkRepository,
        JdbcLinkRepository jdbcLinkRepository,
        JdbcChatRepository jdbcChatRepository
    ) {
        return new JdbcChatService(jdbcChatLinkRepository, jdbcLinkRepository, jdbcChatRepository);
    }

}
