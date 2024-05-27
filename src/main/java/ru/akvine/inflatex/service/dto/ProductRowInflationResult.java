package ru.akvine.inflatex.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ProductRowInflationResult<T> {
    private T identifier;
    private BigDecimal percent;
}
