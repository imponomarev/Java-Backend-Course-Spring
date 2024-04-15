package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.model.LinkUpdateRequest;
import edu.java.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;


@Service
@RequiredArgsConstructor
public class BotService {

    private final TelegramBot telegramBot;

    public void add(LinkUpdateRequest linkUpdateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid request parameters", "Try again");
        }

        String message = linkUpdateRequest.description();

        for (var id : linkUpdateRequest.tgChatIds()) {
            telegramBot.execute(new SendMessage(id, message));
        }

    }
}
