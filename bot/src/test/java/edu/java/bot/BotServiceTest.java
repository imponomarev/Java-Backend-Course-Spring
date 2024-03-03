package edu.java.bot;

import edu.java.bot.api.model.LinkUpdateRequest;
import edu.java.bot.exceptions.UpdateAlreadyExistsException;
import edu.java.bot.service.BotService;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;

class BotServiceTest {

    @Test
    void twiceAddingUpdateTest() throws URISyntaxException, UpdateAlreadyExistsException {
        BotService botService = new BotService();
        LinkUpdateRequest update = new LinkUpdateRequest(
            100L,
            new URI("test-url"),
            "description",
            List.of(111L, 222L)
        );

        botService.add(update);

        Throwable actualException = catchThrowableOfType(
            () -> botService.add(update),
            UpdateAlreadyExistsException.class
        );

        assertThat(actualException)
            .isInstanceOf(UpdateAlreadyExistsException.class);

    }
}
