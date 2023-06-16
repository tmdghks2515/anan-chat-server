package io.klovers.server.common.codes.converters;

import io.klovers.server.common.codes.Gender;
import jakarta.persistence.AttributeConverter;

public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender gender) {
        if (gender == null) {
            return null;
        }
        return gender.name();
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        try {
            return Gender.valueOf(dbData);
        } catch (Exception e) {
            return null;
        }
    }
}
