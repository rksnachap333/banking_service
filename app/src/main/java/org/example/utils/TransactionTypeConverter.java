package org.example.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.enums.TransactionType;

@Converter(autoApply = true)
public class TransactionTypeConverter implements AttributeConverter<TransactionType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TransactionType attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public TransactionType convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;
        for (TransactionType type : TransactionType.values()) {
            if (type.getCode() == dbData) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown TransactionType code: " + dbData);
    }
}
