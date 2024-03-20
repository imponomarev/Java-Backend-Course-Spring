package edu.java.api.controller;

import edu.java.api.model.AddLinkRequest;
import edu.java.api.model.LinkResponse;
import edu.java.api.model.ListLinksResponse;
import edu.java.api.model.RemoveLinkRequest;
import edu.java.domain.dto.LinkDto;
import edu.java.services.LinkService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LinkController {

    private final LinkService linkService;

    @GetMapping("/links")
    public ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") Long id) {
        List<LinkDto> links = linkService.getLinks(id);

        log.info("links has been received");
        return new ListLinksResponse(
            links.stream()
                .map(link -> {
                    return new LinkResponse(link.id(), link.url());
                }).toList(), links.size()
        );
    }

    @PostMapping("/links")
    public LinkResponse addLink(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody @Valid AddLinkRequest request
    ) {
        linkService.addLink(id, request.link());
        log.info("link has been added");
        return new LinkResponse(id, request.link());
    }

    @DeleteMapping("/links")
    public LinkResponse removeLink(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody @Valid @NotNull RemoveLinkRequest request
    ) {
        linkService.removeLink(id, request.link());
        log.info("link has been removed");
        return new LinkResponse(id, request.link());
    }
}
