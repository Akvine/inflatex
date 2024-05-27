package ru.akvine.inflatex.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class ProductTableInflationResult<T> {
    private List<ProductRowInflationResult<T>> rowsResult;
    private BigDecimal meanInflation;
}
