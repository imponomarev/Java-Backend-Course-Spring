package edu.java.bot.dao;

import com.pengrad.telegrambot.model.Update;
import java.net.URI;
import java.util.List;

public interface IStorage {
    void registrate(Update update);

    boolean isRegistered(Update update);

    void addSubscription(Update update, URI link) throws Exception;

    void deleteSubscription(Update update, URI link) throws Exception;

    List<URI> getSubscriptions(Update update) throws Exception;

    boolean isSubExists(Update update, URI link) throws Exception;

    void clear();
}
