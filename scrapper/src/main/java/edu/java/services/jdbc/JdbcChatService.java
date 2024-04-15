package edu.java.services.jdbc;

import edu.java.domain.jdbc.dto.ChatLinkDto;
import edu.java.domain.jdbc.repositories.JdbcChatLinkRepository;
import edu.java.domain.jdbc.repositories.JdbcChatRepository;
import edu.java.domain.jdbc.repositories.JdbcLinkRepository;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.services.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcChatService implements ChatService {

    private final JdbcChatLinkRepository jdbcChatLinkRepository;
    private final JdbcLinkRepository jdbcLinkRepository;
    private final JdbcChatRepository jdbcChatRepository;

    @Override
    @Transactional
    public void registerChat(Long id) {
        jdbcChatRepository.findChatById(id).ifPresent(chat -> {
            throw new BadRequestException("The chat is already registered", "You cannot re-register a chat");
        });
        jdbcChatRepository.addChat(id);
    }

    @Override
    @Transactional
    public void deleteChat(Long id) {
        notFoundCheck(id, "you cannot delete a non-existent chat");

        List<ChatLinkDto> chatLinkDtoList = jdbcChatLinkRepository.findAllByChatId(id);

        chatLinkDtoList.forEach(chatLinkDto -> {
            Long linkId = chatLinkDto.linkId();
            jdbcLinkRepository.remove(jdbcLinkRepository.getLinkUrl(linkId));
        });

        jdbcChatRepository.remove(id);
    }

    private void notFoundCheck(Long id, String message) {
        if (!jdbcChatRepository.exists(id)) {
            throw new NotFoundException("The chat wasn't registered", message);
        }
    }
}
