package edu.java.updaters;

import edu.java.domain.dto.LinkDto;
import java.net.URI;

public interface LinkUpdater {

    boolean update(LinkDto linkDto);

    boolean support(URI url);
}
