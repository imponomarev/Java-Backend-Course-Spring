package edu.java.domain.jpa.model;

import edu.java.domain.jpa.converter.UriConverter;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(schema = "db")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Link {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    @Convert(converter = UriConverter.class)
    private URI url;

    @Column(name = "last_update")
    private OffsetDateTime lastUpdate;

    @Column(name = "last_check")
    private OffsetDateTime lastCheck;

    @ManyToMany(mappedBy = "links")
    @EqualsAndHashCode.Exclude
    public List<Chat> chats;

    public void addChat(Chat chat) {
        this.chats.add(chat);
        chat.getLinks().add(this);
    }

    public void removeChat(Chat chat) {
        this.chats.remove(chat);
        chat.getLinks().remove(this);
    }

}
