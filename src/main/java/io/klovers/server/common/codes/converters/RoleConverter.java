package io.klovers.server.common.codes.converters;

import io.klovers.server.common.codes.Role;
import jakarta.persistence.AttributeConverter;


public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null) {
            return null;
        }
        return role.name();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        try {
            return Role.valueOf(dbData);
        } catch (Exception e) {
            return null;
        }
    }
}
