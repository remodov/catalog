package ru.remodov.catalog.usecase.product;

import java.util.Objects;
import ru.remodov.catalog.domain.SellerId;
import ru.remodov.catalog.generated.enums.ProductStatus;
import ru.remodov.catalog.generated.api.model.ProductPageDto;
import ru.remodov.catalog.repository.ProductRepository;
import ru.vikulinva.usecase.UseCaseQuery;

public record ListMyProductsQuery(
    SellerId requesterSellerId,
    ProductStatus statusFilter,
    int page,
    int size,
    ProductRepository.SortField sort
) implements UseCaseQuery<ProductPageDto> {

    public ListMyProductsQuery {
        Objects.requireNonNull(requesterSellerId, "requesterSellerId");
        if (page < 0) {
            throw new IllegalArgumentException("page must be >= 0");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("size must be in (0, 100]");
        }
        if (sort == null) {
            sort = ProductRepository.SortField.CREATED_AT_DESC;
        }
    }
}
