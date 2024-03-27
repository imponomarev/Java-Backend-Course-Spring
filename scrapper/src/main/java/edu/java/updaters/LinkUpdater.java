package edu.java.updaters;

import edu.java.domain.dto.LinkDto;

public interface LinkUpdater {

    boolean update(LinkDto linkDto);

    String getHost();
}
