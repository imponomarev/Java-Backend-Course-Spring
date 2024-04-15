package edu.java.updaters;

import edu.java.domain.jdbc.dto.LinkDto;

public interface LinkUpdater {

    String update(LinkDto linkDto);

    String getHost();
}
