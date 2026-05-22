package ru.remodov.catalog.usecase.product;

import java.math.BigDecimal;
import java.util.Objects;
import ru.remodov.catalog.domain.SellerId;
import ru.remodov.catalog.exception.InvalidCurrencyException;
import ru.remodov.catalog.exception.InvalidPriceException;
import ru.remodov.catalog.generated.api.model.ProductDto;
import ru.vikulinva.usecase.UseCaseCommand;

public record CreateProductUseCase(
    SellerId sellerId,
    String title,
    String description,
    BigDecimal price,
    String currency
) implements UseCaseCommand<ProductDto> {

    private static final String SUPPORTED_CURRENCY = "RUB";

    public CreateProductUseCase {
        Objects.requireNonNull(sellerId, "sellerId");
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must be non-empty");
        }
        if (price == null || price.signum() <= 0) {
            throw new InvalidPriceException("price must be > 0");
        }
        if (currency == null || !SUPPORTED_CURRENCY.equals(currency)) {
            throw new InvalidCurrencyException(currency);
        }
    }
}
