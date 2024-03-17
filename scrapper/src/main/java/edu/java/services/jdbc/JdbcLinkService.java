package edu.java.services.jdbc;

import edu.java.domain.dto.ChatLinkDto;
import edu.java.domain.dto.LinkDto;
import edu.java.domain.repositories.ChatLinkRepository;
import edu.java.domain.repositories.ChatRepository;
import edu.java.domain.repositories.LinkRepository;

import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.services.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {

    private final LinkRepository linkRepository;
    private final ChatLinkRepository chatLinkRepository;
    private final ChatRepository chatRepository;

    @Override
    @Transactional
    public LinkDto addLink(Long chatId, URI url) {

        notFoundCheck(chatId, "you cannot add a link for a non-existent chat");

        if (linkRepository.findLinkByUrl(url).isEmpty()) {
            linkRepository.addLink(new LinkDto(null, url, null));
        }
        Long lindId = linkRepository.getLinkId(url);
        if (chatLinkRepository.find(chatId, lindId).isPresent()) {
            throw new BadRequestException("the link is already being tracked",
                "you cannot add an already tracked link");
        }
        chatLinkRepository.add(chatId, lindId);

        return new LinkDto(lindId, url, null);
    }

    @Override
    @Transactional
    public LinkDto removeLink(Long chatId, URI url) {

        notFoundCheck(chatId, "you cannot remove a link from a non-existent chat");

        Optional<LinkDto> existingLink = linkRepository.findLinkByUrl(url);

        if (existingLink.isEmpty()) {
            throw new NotFoundException("the link doesn't exist", "you cannot delete a link that doesn't exist");
        }

        Long linkId = existingLink.get().id();

        OffsetDateTime lastUpdate = existingLink.get().lastUpdate();

        linkRepository.remove(url);

        return new LinkDto(linkId, url, lastUpdate);
    }

    @Override
    @Transactional
    public List<LinkDto> getLinks(Long chatId) {

        notFoundCheck(chatId, "you can't get links for a non-existent chat");

        List<ChatLinkDto> chatLinkDtoList = chatLinkRepository.findAll().stream()
            .filter(chatLinkDto -> chatLinkDto.chatId().equals(chatId))
            .toList();

        return chatLinkDtoList.stream()
            .map(chatLinkDto -> {
                Long linkId = chatLinkDto.linkId();
                Optional<LinkDto> linkDto = linkRepository.findLinkById(linkId);
                return linkDto.orElse(null);
            })
            .filter(Objects::nonNull)
            .toList();
    }

    @Override
    @Transactional
    public void update(LinkDto link) {
        linkRepository.update(link);
    }

    private void notFoundCheck(Long id, String message) {
        if (chatRepository.findChatById(id).isEmpty()) {
            throw new NotFoundException("The chat wasn't registered", message);
        }
    }
}
