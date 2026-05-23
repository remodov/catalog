package ru.remodov.catalog.usecase.product;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.remodov.catalog.generated.api.model.ProductDto;
import ru.remodov.catalog.generated.api.model.ProductPageDto;
import ru.remodov.catalog.mapper.ProductJsonBeanMapper;
import ru.remodov.catalog.repository.ProductRepository;
import ru.vikulinva.usecase.UseCaseHandler;

@Component
@RequiredArgsConstructor
public class ListMyProductsQueryHandler implements UseCaseHandler<ListMyProductsQuery, ProductPageDto> {

    private final ProductRepository repo;
    private final ProductJsonBeanMapper mapper;

    @Override
    public Class<ListMyProductsQuery> useCaseType() { return ListMyProductsQuery.class; }

    @Override
    @Transactional(readOnly = true)
    public ProductPageDto handle(ListMyProductsQuery q) {
        var sellerId = q.requesterSellerId().value();
        long total = repo.countBySeller(sellerId, q.statusFilter());
        List<ProductDto> content = repo.findBySeller(sellerId, q.statusFilter(), (q.page() - 1) * q.size(), q.size(), q.sort())
            .stream()
            .map(mapper::toDto)
            .toList();

        int size = q.size();
        int totalPages = size == 0 ? 0 : (int) ((total + size - 1) / size);

        var dto = new ProductPageDto();
        dto.setContent(content);
        dto.setPage(q.page());
        dto.setSize(size);
        dto.setTotalElements(total);
        dto.setTotalPages(totalPages);
        return dto;
    }
}
