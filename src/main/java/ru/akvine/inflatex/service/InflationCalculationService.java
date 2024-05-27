package ru.akvine.inflatex.service;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.akvine.inflatex.service.dto.ProductRowInflationResult;
import ru.akvine.inflatex.service.dto.ProductTable;
import ru.akvine.inflatex.service.dto.ProductTableInflationResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static ru.akvine.inflatex.constants.NumericConstants.ONE_HUNDRED;

@Service
@Slf4j
public class InflationCalculationService {

    public <T> ProductTableInflationResult<T> calculate(ProductTable<T> table, int precision) {
        return calculate(table, precision, RoundingMode.HALF_UP);
    }

    public <T> ProductTableInflationResult<T> calculate(ProductTable<T> table, int precision, RoundingMode roundingMode) {
        Preconditions.checkNotNull(table, "productTable is null");
        logger.info("Start inflation calculate");

        ProductTableInflationResult<T> result = new ProductTableInflationResult<>();
        result.setRowsResult(new ArrayList<>());
        table.getRows().forEach(category -> {
            ProductRowInflationResult<T> rowResult = new ProductRowInflationResult<>();
            rowResult.setIdentifier(category.getIdentifier());

            BigDecimal startPrice = category.getStartPrice();
            BigDecimal endPrice = category.getEndPrice();
            BigDecimal percent = (endPrice.subtract(startPrice)) // ((endPrice - startPrice) / startPrice) * 100
                    .divide(startPrice, precision, roundingMode)
                    .multiply(BigDecimal.valueOf(ONE_HUNDRED));

            rowResult.setPercent(percent);
            result.getRowsResult().add(rowResult);
        });

        BigDecimal meanInflationPercent;
        if (result.getRowsResult().isEmpty()) {
            meanInflationPercent = BigDecimal.ZERO.setScale(precision, roundingMode);
        } else {
            meanInflationPercent = result
                    .getRowsResult()
                    .stream()
                    .map(ProductRowInflationResult::getPercent)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(result.getRowsResult().size()), precision, roundingMode);
        }

        result.setMeanInflation(meanInflationPercent);

        logger.info("End inflation calculate");
        return result;
    }
}
