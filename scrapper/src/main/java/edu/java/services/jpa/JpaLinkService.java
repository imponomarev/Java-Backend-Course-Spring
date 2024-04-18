package edu.java.services.jpa;

import edu.java.api.model.LinkResponse;
import edu.java.domain.jdbc.dto.LinkDto;
import edu.java.domain.jpa.model.Chat;
import edu.java.domain.jpa.model.Link;
import edu.java.domain.jpa.repositories.JpaChatRepository;
import edu.java.domain.jpa.repositories.JpaLinkRepository;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaLinkService implements LinkService {

    private final JpaLinkRepository jpaLinkRepository;
    private final JpaChatRepository jpaChatRepository;

    private final static String NOT_EXIST_LINK = "the link doesn't exist";
    private final static String CANNOT_DELETE = "you cannot delete a link that doesn't exist";
    private final static int PAGE_SIZE = 10000;


    @Override
    @Transactional
    public LinkDto addLink(Long chatId, URI url) {

        notFoundCheck(chatId, "you cannot add a link for a non-existent chat");

        Optional<Link> linkOptional = jpaLinkRepository.findLinkByUrl(url);

        Optional<Chat> chat = jpaChatRepository.findById(chatId);

        Link link;

        if (linkOptional.isEmpty()) {
            link = new Link();
            link.setUrl(url);
            link.setLastUpdate(OffsetDateTime.now());
            link.setLastCheck(OffsetDateTime.now());
            link.setChats(new ArrayList<>());
            jpaLinkRepository.saveAndFlush(link);
        } else {
            link = linkOptional.get();

            if (chat.get().getLinks().contains(link)) {
                throw new BadRequestException(
                    "the link is already being tracked",
                    "you cannot add an already tracked link"
                );
            }
        }
        link.addChat(chat.get());
        jpaLinkRepository.saveAndFlush(link);
        jpaChatRepository.saveAndFlush(chat.get());

        return new LinkDto(link.getId(), link.getUrl(), OffsetDateTime.now(), OffsetDateTime.now());
    }

    @Override
    @Transactional
    public LinkResponse removeLink(Long chatId, URI url) {

        notFoundCheck(chatId, "you cannot remove a link from a non-existent chat");

        Optional<Link> existingLink = jpaLinkRepository.findLinkByUrl(url);

        Optional<Chat> chat = jpaChatRepository.findById(chatId);

        if (existingLink.isEmpty()) {
            throw new NotFoundException(NOT_EXIST_LINK, CANNOT_DELETE);
        } else {
            Link link = existingLink.get();

            if (!chat.get().getLinks().contains(link)) {
                throw new NotFoundException(NOT_EXIST_LINK, CANNOT_DELETE);
            }

            link.removeChat(chat.get());
            if (link.getChats().isEmpty()) {
                jpaLinkRepository.delete(link);
            } else {
                jpaLinkRepository.saveAndFlush(link);
            }
            return new LinkResponse(link.getId(), url);
        }
    }

    @Override
    @Transactional
    public List<LinkDto> getLinks(Long chatId) {

        notFoundCheck(chatId, "you can't get links for a non-existent chat");

        Optional<Chat> chat = jpaChatRepository.findById(chatId);

        return chat.get().getLinks().stream()
            .map(link -> new LinkDto(
                link.getId(),
                link.getUrl(),
                link.getLastUpdate(),
                link.getLastCheck()
            )).toList();
    }

    @Override
    @Transactional
    public void update(LinkDto link) {

        Link oldLink = jpaLinkRepository.findLinkByUrl(link.url()).get();

        oldLink.setLastUpdate(link.lastUpdate());
        oldLink.setLastCheck(link.lastCheck());

        jpaLinkRepository.saveAndFlush(oldLink);

    }

    public List<LinkDto> getOldLinks(Long threshold) {

        OffsetDateTime thresholdTime = OffsetDateTime.now().minusSeconds(threshold);

        Pageable limit = PageRequest.of(0, PAGE_SIZE);

        Page<Link> pageOfLinks = jpaLinkRepository.findOldLinksByThreshold(thresholdTime, limit);

        List<Link> oldLinks = pageOfLinks.getContent();

        return oldLinks
            .stream()
            .map(link ->
                new LinkDto(
                    link.getId(),
                    link.getUrl(),
                    link.getLastUpdate(),
                    link.getLastCheck()
                )).toList();
    }

    @Override
    @Transactional
    public List<Long> getChatIdsOfLink(Long linkId) {

        Link link = jpaLinkRepository.findById(linkId).get();

        return jpaChatRepository.findAllByLinksContaining(link)
            .stream()
            .map(Chat::getId)
            .toList();

    }

    private void notFoundCheck(Long id, String message) {
        if (!jpaChatRepository.existsById(id)) {
            throw new NotFoundException("The chat wasn't registered", message);
        }
    }

}

