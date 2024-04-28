package edu.java.bot.api.controller;

import edu.java.api.model.LinkUpdateRequest;
import edu.java.bot.service.BotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class BotController {
    private final BotService botService;

    @PostMapping
    public String process(@RequestBody @Valid @NotNull LinkUpdateRequest linkUpdateRequest,
        BindingResult bindingResult) {

        botService.add(linkUpdateRequest);
        return "The update has been processed";

    }
}
