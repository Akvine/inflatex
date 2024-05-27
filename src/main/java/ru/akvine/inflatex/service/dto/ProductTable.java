package ru.akvine.inflatex.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ProductTable<T> {
    private List<ProductRow<T>> rows;

    public static <T> ProductTable<T> empty() {
        return new ProductTable<>();
    }

    public ProductTable() {
        this.rows = new ArrayList<>();
    }
}
