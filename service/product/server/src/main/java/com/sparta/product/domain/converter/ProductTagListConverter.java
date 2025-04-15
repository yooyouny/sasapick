package com.sparta.product.domain.converter;

import com.sparta.product.domain.model.ProductTag;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class ProductTagListConverter implements AttributeConverter<List<ProductTag>, String> {

  private static final String DELIMITER = ",";

  @Override
  public String convertToDatabaseColumn(List<ProductTag> attribute) {
    if (attribute == null || attribute.isEmpty()) {
      return "";
    }
    
    return attribute.stream()
        .map(Enum::name)
        .collect(Collectors.joining(DELIMITER));
  }

  @Override
  public List<ProductTag> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return Collections.emptyList();
    }
    
    return Arrays.stream(dbData.split(DELIMITER))
        .map(ProductTag::valueOf)
        .collect(Collectors.toList());
  }
}
