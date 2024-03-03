package edu.java.service;

import edu.java.api.model.LinkResponse;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ScrapperService {
    private final Map<Long, List<LinkResponse>> chatLinks = new ConcurrentHashMap<>();

    public void registerChat(Long id) throws BadRequestException {
        if (chatLinks.containsKey(id)) {
            throw new BadRequestException("The chat is already registered", "You cannot re-register a chat");
        }
        chatLinks.put(id, new ArrayList<>());
    }

    public void deleteChat(Long id) throws NotFoundException {
        notFoundCheck(id, "you cannot delete a non-existent chat");
        chatLinks.remove(id);
    }

    public List<LinkResponse> getLinks(Long id) throws NotFoundException {
        notFoundCheck(id, "you can't get links for a non-existent chat");
        return chatLinks.get(id);
    }

    public LinkResponse addLink(Long id, URI link) throws BadRequestException, NotFoundException {
        notFoundCheck(id,  "you cannot add a link for a non-existent chat");

        List<LinkResponse> responses = chatLinks.get(id);
        for (var linkResponse : responses) {
            if (linkResponse.url().getPath().equals(link.getPath())) {
                throw new BadRequestException("the link is already being tracked", "you cannot add an already tracked link");
            }
        }

        LinkResponse linkResponse = new LinkResponse((long) (responses.size() + 1), link);
        responses.add(linkResponse);

        return linkResponse;
    }

    public LinkResponse removeLink(Long id, URI link) throws NotFoundException {
        notFoundCheck(id, "you cannot remove a link from a non-existent chat");

        List<LinkResponse> responses = chatLinks.get(id);
        for (var linkResponse : responses) {
            if (linkResponse.url().getPath().equals(link.getPath())) {
                responses.remove(linkResponse);
                return linkResponse;
            }
        }
        throw new NotFoundException("the link doesn't exist", "you cannot delete a link that doesn't exist");
    }

    private void notFoundCheck(Long id, String message) throws NotFoundException {
        if (!chatLinks.containsKey(id)) {
            throw new NotFoundException("The chat wasn't registered", message);
        }
    }
}
