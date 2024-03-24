package edu.java.domain.repositories;

import edu.java.domain.dto.LinkDto;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LinkRepository {

    private final JdbcClient jdbcClient;

    public Optional<LinkDto> findLinkById(Long id) {
        String query = "SELECT * FROM db.link WHERE id = ?";
        return jdbcClient.sql(query)
            .params(id)
            .query(LinkDto.class)
            .optional();
    }

    public Optional<LinkDto> findLinkByUrl(URI url) {
        String query = "SELECT * FROM db.link WHERE url = ?";
        return jdbcClient.sql(query)
            .params(url.toString())
            .query(LinkDto.class)
            .optional();
    }

    public Long addLink(LinkDto link) {
        if (findLinkByUrl(link.url()).isEmpty()) {
            String query = "INSERT INTO db.link(url, last_update, last_check) VALUES(?,?,?) RETURNING id";
            return jdbcClient.sql(query)
                .params(link.url().toString(), link.lastUpdate(), link.lastCheck())
                .query(Long.class)
                .single();
        }
        return getLinkId(link.url());
    }

    public void remove(URI url) {
        if (findLinkByUrl(url).isPresent()) {
            String query = "DELETE FROM db.link WHERE url = ?";
            jdbcClient.sql(query)
                .params(url.toString())
                .update();
        }
    }

    public List<LinkDto> findAll() {
        String query = "SELECT * FROM db.link";
        return jdbcClient.sql(query)
            .query(LinkDto.class)
            .list();
    }

    public Long getLinkId(URI url) {
        String query = "SELECT id FROM db.link WHERE url = ?";
        return jdbcClient.sql(query)
            .params(url.toString())
            .query(Long.class)
            .single();
    }

    public URI getLinkUrl(Long id) {
        String query = "SELECT url FROM db.link WHERE id = ?";
        return jdbcClient.sql(query)
            .params(id)
            .query(URI.class)
            .single();
    }

    public void update(LinkDto link) {
        String query = "UPDATE db.link SET last_update = ? AND last_check = ? WHERE id = ?";
        jdbcClient.sql(query)
            .params(link.lastUpdate(), link.lastCheck(), link.id())
            .update();
    }

    public List<LinkDto> findOldLinksByThreshold(Long threshold) {
        String query = "SELECT * FROM db.link WHERE last_check <= NOW() - INTERVAL ? SECOND";
        return jdbcClient.sql(query)
            .param(threshold)
            .query(LinkDto.class)
            .list();
    }
}
