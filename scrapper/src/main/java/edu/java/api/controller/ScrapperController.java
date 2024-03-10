package edu.java.api.controller;

import edu.java.api.model.AddLinkRequest;
import edu.java.api.model.LinkResponse;
import edu.java.api.model.ListLinksResponse;
import edu.java.api.model.RemoveLinkRequest;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.service.ScrapperService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScrapperController {

    private final ScrapperService scrapperService;

    @PostMapping("/tg-chat/{id}")
    public String registerChat(@PathVariable("id") Long id) {
        scrapperService.registerChat(id);
        return "chat is registered";
    }

    @DeleteMapping("/tg-chat/{id}")
    public String deleteChat(@PathVariable("id") Long id) {
        scrapperService.deleteChat(id);
        return "chat was successfully deleted";
    }

    @GetMapping("/links")
    public ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") Long id) {
        List<LinkResponse> links = scrapperService.getLinks(id);
        return new ListLinksResponse(links, links.size());
    }

    @PostMapping("/links")
    public LinkResponse addLink(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody @Valid AddLinkRequest request
    ) throws BadRequestException, NotFoundException {
        return scrapperService.addLink(id, request.link());
    }

    @DeleteMapping("/links")
    public LinkResponse removeLink(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody @Valid @NotNull RemoveLinkRequest request
    ) throws NotFoundException {
        return scrapperService.removeLink(id, request.link());
    }


}
