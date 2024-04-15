package edu.java.services.jpa;

import edu.java.domain.jpa.model.Chat;
import edu.java.domain.jpa.model.Link;
import edu.java.domain.jpa.repositories.JpaChatRepository;
import edu.java.domain.jpa.repositories.JpaLinkRepository;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JpaChatService implements ChatService {

    private final JpaChatRepository jpaChatRepository;
    private final JpaLinkRepository jpaLinkRepository;

    @Override
    @Transactional
    public void registerChat(Long id) {
        if (jpaChatRepository.existsById(id)) {
            throw new BadRequestException("The chat is already registered", "You cannot re-register a chat");
        }
        Chat chat = new Chat();
        chat.setId(id);
        chat.setLinks(new ArrayList<>());
        jpaChatRepository.save(chat);
    }

    @Override
    @Transactional
    public void deleteChat(Long id) {

        notFoundCheck(id, "you cannot delete a non-existent chat");

        Optional<Chat> chat = jpaChatRepository.findById(id);

        List<Link> links = chat.get().getLinks();


        if (!links.isEmpty()) {
            List<Link> safeList = new ArrayList<>(links);
            for (var link : safeList) {
                link.removeChat(chat.get());

                if (link.getChats().isEmpty()) {
                    jpaLinkRepository.delete(link);
                }
            }
        }

        jpaChatRepository.deleteById(id);
        jpaChatRepository.flush();
    }

    private void notFoundCheck(Long id, String message) {
        if (!jpaChatRepository.existsById(id)) {
            throw new NotFoundException("The chat wasn't registered", message);
        }
    }
}
