package ru.remodov.catalog.usecase.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.remodov.catalog.exception.ProductNotFoundException;
import ru.remodov.catalog.generated.enums.ProductStatus;
import ru.remodov.catalog.generated.api.model.ProductDto;
import ru.remodov.catalog.mapper.ProductJsonBeanMapper;
import ru.remodov.catalog.repository.ProductRepository;
import ru.vikulinva.usecase.UseCaseHandler;

@Component
@RequiredArgsConstructor
public class GetProductQueryHandler implements UseCaseHandler<GetProductQuery, ProductDto> {

    private final ProductRepository repo;
    private final ProductJsonBeanMapper mapper;

    @Override
    public Class<GetProductQuery> useCaseType() { return GetProductQuery.class; }

    @Override
    @Transactional(readOnly = true)
    public ProductDto handle(GetProductQuery q) {
        var pojo = repo.findById(q.productId().value())
            .orElseThrow(() -> new ProductNotFoundException(q.productId().value()));

        if (pojo.getStatus() != ProductStatus.PUBLISHED) {
            throw new ProductNotFoundException(q.productId().value());
        }
        return mapper.toDto(pojo);
    }
}
