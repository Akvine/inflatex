package ru.akvine.inflatex.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akvine.inflatex.service.dto.ProductRow;
import ru.akvine.inflatex.service.dto.ProductRowInflationResult;
import ru.akvine.inflatex.service.dto.ProductTable;
import ru.akvine.inflatex.service.dto.ProductTableInflationResult;


import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Inflation Calculation Service")
public class InflationCalculationServiceTest {
    private final static InflationCalculationService inflationCalculationService = new InflationCalculationService();
    private final static int SCALE_PRECISION = 5; // Don't choose!!!

    @Test
    @DisplayName("Success test single row")
    void testSuccessSingleRow() {
        ProductRow<String> row = new ProductRow<>();
        row
                .setIdentifier("Milk")
                .setStartPrice(BigDecimal.valueOf(100))
                .setEndPrice(BigDecimal.valueOf(120));

        ProductTable<String> productTable = new ProductTable<>();
        productTable.setRows(List.of(row));

        ProductTableInflationResult<String> result = inflationCalculationService.calculate(productTable, SCALE_PRECISION);
        assertThat(result).isNotNull();
        assertThat(result.getRowsResult()).isNotEmpty();

        ProductRowInflationResult<String> rowResult = result.getRowsResult().getFirst();
        assertThat(rowResult.getIdentifier()).isEqualTo("Milk");
        assertThat(rowResult.getPercent()).isEqualTo(new BigDecimal("20.00000"));

        BigDecimal meanInflation = result.getMeanInflation();
        assertThat(meanInflation).isNotNull();
        assertThat(meanInflation).isEqualTo("20.00000");
    }

    @Test
    @DisplayName("Success test has no rows")
    void testSuccessHasNoRows() {
        ProductTableInflationResult<String> result = inflationCalculationService
                .calculate(ProductTable.empty(), SCALE_PRECISION);

        assertThat(result).isNotNull();
        assertThat(result.getRowsResult()).isNotNull();
        assertThat(result.getRowsResult()).isEmpty();
        assertThat(result.getMeanInflation()).isNotNull();
        assertThat(result.getMeanInflation()).isEqualTo(new BigDecimal("0.00000"));
    }

    @Test
    @DisplayName("Success test multiply rows")
    void testSuccessMultiplyRows() {
        ProductRow<String> firstRow = new ProductRow<>();
        firstRow
                .setIdentifier("Milk")
                .setStartPrice(BigDecimal.valueOf(100))
                .setEndPrice(BigDecimal.valueOf(120));
        ProductRow<String> secondRow = new ProductRow<>();
        secondRow
                .setIdentifier("Bread")
                .setStartPrice(BigDecimal.valueOf(200))
                .setEndPrice(BigDecimal.valueOf(400));
        ProductRow<String> thirdRow = new ProductRow<>();
        thirdRow
                .setIdentifier("Eggs")
                .setStartPrice(BigDecimal.valueOf(20))
                .setEndPrice(BigDecimal.valueOf(25));


        ProductTable<String> productTable = new ProductTable<>();
        productTable.setRows(List.of(firstRow, secondRow, thirdRow));

        ProductTableInflationResult<String> result = inflationCalculationService.calculate(productTable, SCALE_PRECISION);
        assertThat(result).isNotNull();
        assertThat(result.getRowsResult()).isNotEmpty();

        ProductRowInflationResult<String> firstRowResult = result.getRowsResult().getFirst();
        assertThat(firstRowResult.getIdentifier()).isEqualTo("Milk");
        assertThat(firstRowResult.getPercent()).isEqualTo(new BigDecimal("20.00000"));

        ProductRowInflationResult<String> secondRowResult = result.getRowsResult().get(1);
        assertThat(secondRowResult.getIdentifier()).isEqualTo("Bread");
        assertThat(secondRowResult.getPercent()).isEqualTo(new BigDecimal("100.00000"));

        ProductRowInflationResult<String> thirdRowResult = result.getRowsResult().getLast();
        assertThat(thirdRowResult.getIdentifier()).isEqualTo("Eggs");
        assertThat(thirdRowResult.getPercent()).isEqualTo(new BigDecimal("25.00000"));

        BigDecimal meanInflation = result.getMeanInflation();
        assertThat(meanInflation).isNotNull();
        assertThat(meanInflation).isEqualTo("48.33333");
    }
}
