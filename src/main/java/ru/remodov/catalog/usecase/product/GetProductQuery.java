package ru.remodov.catalog.usecase.product;

import java.util.Objects;
import ru.remodov.catalog.domain.ProductId;
import ru.remodov.catalog.generated.api.model.ProductDto;
import ru.vikulinva.usecase.UseCaseQuery;

public record GetProductQuery(ProductId productId) implements UseCaseQuery<ProductDto> {
    public GetProductQuery {
        Objects.requireNonNull(productId, "productId");
    }
}
