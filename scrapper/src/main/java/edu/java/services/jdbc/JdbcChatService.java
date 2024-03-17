package edu.java.services.jdbc;

import edu.java.domain.dto.ChatLinkDto;
import edu.java.domain.repositories.ChatLinkRepository;
import edu.java.domain.repositories.ChatRepository;
import edu.java.domain.repositories.LinkRepository;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {

    private final ChatLinkRepository chatLinkRepository;
    private final LinkRepository linkRepository;
    private final ChatRepository chatRepository;

    @Override
    @Transactional
    public void registerChat(Long id) {
        if (chatRepository.findChatById(id).isPresent()) {
            throw new BadRequestException("The chat is already registered", "You cannot re-register a chat");
        }
        chatRepository.addChat(id);
    }

    @Override
    @Transactional
    public void deleteChat(Long id) {
        notFoundCheck(id, "you cannot delete a non-existent chat");

        List<ChatLinkDto> chatLinkDtoList = chatLinkRepository.findAll().stream()
            .filter(chatLinkDto -> chatLinkDto.chatId().equals(id))
            .toList();

        chatLinkDtoList.forEach(chatLinkDto -> {
            Long linkId = chatLinkDto.linkId();
            linkRepository.remove(linkRepository.getLinkUrl(linkId));
        });

        chatRepository.remove(id);
    }

    private void notFoundCheck(Long id, String message) {
        if (chatRepository.findChatById(id).isEmpty()) {
            throw new NotFoundException("The chat wasn't registered", message);
        }
    }
}
