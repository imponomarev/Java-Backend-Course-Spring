package edu.java.services.jdbc;

import edu.java.api.model.LinkResponse;
import edu.java.domain.jdbc.dto.ChatLinkDto;
import edu.java.domain.jdbc.dto.LinkDto;
import edu.java.domain.jdbc.repositories.JdbcChatLinkRepository;
import edu.java.domain.jdbc.repositories.JdbcChatRepository;
import edu.java.domain.jdbc.repositories.JdbcLinkRepository;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.services.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {

    private final JdbcLinkRepository jdbcLinkRepository;
    private final JdbcChatLinkRepository jdbcChatLinkRepository;
    private final JdbcChatRepository jdbcChatRepository;

    @Override
    @Transactional
    public LinkDto addLink(Long chatId, URI url) {

        notFoundCheck(chatId, "you cannot add a link for a non-existent chat");

        if (jdbcLinkRepository.findLinkByUrl(url).isEmpty()) {
            jdbcLinkRepository.addLink(new LinkDto(null, url, OffsetDateTime.now(), OffsetDateTime.now()));
        }
        Long linkId = jdbcLinkRepository.getLinkId(url);
        if (jdbcChatLinkRepository.find(chatId, linkId).isPresent()) {
            throw new BadRequestException(
                "the link is already being tracked",
                "you cannot add an already tracked link"
            );
        }
        jdbcChatLinkRepository.add(chatId, linkId);

        return new LinkDto(linkId, url, OffsetDateTime.now(), OffsetDateTime.now());
    }

    @Override
    @Transactional
    public LinkResponse removeLink(Long chatId, URI url) {

        notFoundCheck(chatId, "you cannot remove a link from a non-existent chat");

        Optional<LinkDto> existingLink = jdbcLinkRepository.findLinkByUrl(url);

        if (existingLink.isEmpty()) {
            throw new NotFoundException("the link doesn't exist", "you cannot delete a link that doesn't exist");
        }

        Long linkId = existingLink.get().id();

        jdbcChatLinkRepository.remove(chatId, linkId);

        if (jdbcChatLinkRepository.findAllByLinkId(linkId).isEmpty()) {
            jdbcLinkRepository.remove(url);
        }

        return new LinkResponse(linkId, url);
    }

    @Override
    @Transactional
    public List<LinkDto> getLinks(Long chatId) {

        notFoundCheck(chatId, "you can't get links for a non-existent chat");

        List<ChatLinkDto> chatLinkDtoList = jdbcChatLinkRepository.findAllByChatId(chatId);

        return chatLinkDtoList.stream()
            .map(chatLinkDto -> {
                Long linkId = chatLinkDto.linkId();
                Optional<LinkDto> linkDto = jdbcLinkRepository.findLinkById(linkId);
                return linkDto.orElse(null);
            })
            .filter(Objects::nonNull)
            .toList();
    }

    @Override
    @Transactional
    public void update(LinkDto link) {
        jdbcLinkRepository.update(link);
    }

    @Override
    @Transactional
    public List<LinkDto> getOldLinks(Long threshold) {
        return jdbcLinkRepository.findOldLinksByThreshold(threshold);
    }

    @Override
    @Transactional
    public List<Long> getChatIdsOfLink(Long linkId) {
        return jdbcChatLinkRepository.findAll().stream()
            .filter(chatLinkDto -> chatLinkDto.linkId().equals(linkId))
            .map(ChatLinkDto::chatId)
            .toList();
    }

    private void notFoundCheck(Long id, String message) {
        if (!jdbcChatRepository.exists(id)) {
            throw new NotFoundException("The chat wasn't registered", message);
        }
    }
}
