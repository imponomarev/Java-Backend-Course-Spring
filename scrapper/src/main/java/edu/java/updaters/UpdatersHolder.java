package edu.java.updaters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class UpdatersHolder {

    private final Map<String, LinkUpdater> updaterMap = new HashMap<>();

    public UpdatersHolder(List<? extends LinkUpdater> updaters) {
        updaters.forEach(updater -> updaterMap.put(updater.getHost(), updater));
    }

    public LinkUpdater getUpdaterByHost(String host) {
        return updaterMap.get(host);
    }
}
