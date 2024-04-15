package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.api.model.LinkUpdateRequest;
import edu.java.bot.service.BotService;
import edu.java.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import java.net.URI;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BotServiceTest {

    @Mock
    private TelegramBot telegramBot;

    @Mock
    private BindingResult bindingResult;

    private BotService botService;

    @BeforeEach
    void setUp() {
        botService = new BotService(telegramBot);
    }

    @Test
    void shouldThrowBadRequestExceptionIfBindingResultHasErrors() {
        LinkUpdateRequest linkUpdateRequest =
            new LinkUpdateRequest(1L, URI.create("https://example.com"), "Update Available!", List.of(123L, 1234L));
        when(bindingResult.hasErrors()).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            botService.add(linkUpdateRequest, bindingResult);
        });

        assertEquals("Invalid request parameters", exception.getMessage());
        assertEquals("Try again", exception.getDescription());
    }
}
