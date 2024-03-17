package edu.java.services;

import edu.java.domain.dto.LinkDto;
import java.net.URI;
import java.util.List;

public interface LinkService {

    List<LinkDto> getLinks(Long chatId);

    LinkDto addLink(Long chatId, URI url);

    LinkDto removeLink(Long chatId, URI url);

    void update(LinkDto linkDto);

}
