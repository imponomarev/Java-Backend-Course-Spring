package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.dao.MapStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MapStorageTest {

    @Autowired
    MapStorage storage;
    @Mock
    Update update;

    @Mock
    Message message;

    @Mock
    Chat chat;

    @AfterEach
    public void clearStorage() {
        storage.clear();
    }

    @BeforeEach
    void setUp() {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(chat.id()).thenReturn(11L);
    }

    @Test
    void registrateShouldAddUserToStorage() {
        storage.registrate(update);
        assertTrue(storage.isRegistered(update));
    }

    @Test
    void isRegisteredShouldReturnFalseIfUserNotRegistered() {
        assertFalse(storage.isRegistered(update));
    }

    @Test
    void addSubscriptionShouldAddSubscriptionToUser() throws Exception {
        storage.registrate(update);
        URI link = new URI("https://github.com/imponomarev/Java-Backend-Course-Spring-2024");
        storage.addSubscription(update, link);
        assertTrue(storage.getSubscriptions(update).contains(link));
    }

    @Test
    void addSubscriptionShouldThrowExceptionIfUserNotRegistered() throws URISyntaxException {
        URI link = new URI("https://github.com/imponomarev/Java-Backend-Course-Spring-2024");
        assertThrows(Exception.class, () -> storage.addSubscription(update, link));
    }

    @Test
    void deleteSubscriptionShouldRemoveSubscriptionFromUser() throws Exception {
        storage.registrate(update);
        URI link = new URI("https://github.com/imponomarev/Java-Backend-Course-Spring-2024");
        storage.addSubscription(update, link);
        storage.deleteSubscription(update, link);
        assertFalse(storage.getSubscriptions(update).contains(link));
    }

    @Test
    void deleteSubscriptionShouldThrowExceptionIfUserNotRegistered() throws URISyntaxException {
        URI link = new URI("https://github.com/imponomarev/Java-Backend-Course-Spring-2024");
        assertThrows(Exception.class, () -> storage.deleteSubscription(update, link));
    }

    @Test
    void getSubscriptionsShouldReturnListOfSubscriptions() throws Exception {
        storage.registrate(update);
        URI link1 = new URI("https://github.com/imponomarev/Java-Backend-Course-Spring-2024/1");
        URI link2 = new URI("https://github.com/imponomarev/Java-Backend-Course-Spring-2024/2");
        storage.addSubscription(update, link1);
        storage.addSubscription(update, link2);
        List<URI> subscriptions = storage.getSubscriptions(update);
        assertEquals(2, subscriptions.size());
        assertTrue(subscriptions.contains(link1));
        assertTrue(subscriptions.contains(link2));
    }

    @Test
    void getSubscriptionsShouldThrowExceptionIfUserNotRegistered() {
        assertThrows(Exception.class, () -> storage.getSubscriptions(update));
    }

    @Test
    void isSubExistsShouldReturnTrueIfSubscriptionExists() throws Exception {
        storage.registrate(update);
        URI link = new URI("https://github.com/imponomarev/Java-Backend-Course-Spring-2024");
        storage.addSubscription(update, link);
        assertTrue(storage.isSubExists(update, link));
    }

    @Test
    void isSubExistsShouldReturnFalseIfSubscriptionNotExists() throws Exception {
        storage.registrate(update);
        URI link = new URI("https://github.com/imponomarev/Java-Backend-Course-Spring-2024");
        assertFalse(storage.isSubExists(update, link));
    }

    @Test
    void isSubExistsShouldThrowExceptionIfUserNotRegistered() throws URISyntaxException {
        URI link = new URI("https://github.com/imponomarev/Java-Backend-Course-Spring-2024");
        assertThrows(Exception.class, () -> storage.isSubExists(update, link));
    }
}
