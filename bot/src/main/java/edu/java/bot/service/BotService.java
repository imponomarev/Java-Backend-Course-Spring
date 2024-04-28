package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.api.model.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BotService {

    private final TelegramBot telegramBot;

    public void add(LinkUpdateRequest linkUpdateRequest) {

        String message = linkUpdateRequest.description();

        for (var id : linkUpdateRequest.tgChatIds()) {
            telegramBot.execute(new SendMessage(id, message));
        }

    }
}
