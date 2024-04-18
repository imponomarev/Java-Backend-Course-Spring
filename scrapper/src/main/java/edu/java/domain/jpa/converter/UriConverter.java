package edu.java.domain.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.net.URI;

@Converter(autoApply = true)
public class UriConverter implements AttributeConverter<URI, String> {

    @Override
    public String convertToDatabaseColumn(URI uri) {
        return (uri == null ? null : uri.toString());
    }

    @Override
    public URI convertToEntityAttribute(String uriString) {
        return (uriString == null ? null : URI.create(uriString));
    }
}

