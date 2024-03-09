package edu.java.bot.service;

import edu.java.bot.api.model.LinkUpdateRequest;
import edu.java.bot.exceptions.UpdateAlreadyExistsException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;


@Service
public class BotService {
    private final Set<LinkUpdateRequest> updates = new HashSet<>();

    public void add(LinkUpdateRequest linkUpdateRequest) {
        if (!updates.add(linkUpdateRequest)) {
            throw new UpdateAlreadyExistsException("The update already exists");
        }
    }
}
