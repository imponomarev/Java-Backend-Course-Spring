package edu.java.bot.service;

import edu.java.bot.api.model.LinkUpdateRequest;
import edu.java.bot.exceptions.UpdateAlreadyExistsException;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class BotService {
    private final Set<LinkUpdateRequest> updates = new HashSet<>();

    public void add(LinkUpdateRequest linkUpdateRequest) throws UpdateAlreadyExistsException {
        if (!updates.add(linkUpdateRequest)) {
            throw new UpdateAlreadyExistsException("The update already exists");
        }
    }
}
