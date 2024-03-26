package edu.java.updaters;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
