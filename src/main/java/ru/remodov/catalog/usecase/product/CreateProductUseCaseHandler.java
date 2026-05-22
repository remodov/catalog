package ru.remodov.catalog.usecase.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.remodov.catalog.core.service.DateTimeService;
import ru.remodov.catalog.core.service.UuidGenerator;
import ru.remodov.catalog.generated.enums.ProductStatus;
import ru.remodov.catalog.generated.tables.pojos.ProductsPojo;
import ru.remodov.catalog.generated.api.model.ProductDto;
import ru.remodov.catalog.mapper.ProductJsonBeanMapper;
import ru.remodov.catalog.repository.ProductRepository;
import ru.vikulinva.usecase.UseCaseHandler;

@Component
@RequiredArgsConstructor
public class CreateProductUseCaseHandler implements UseCaseHandler<CreateProductUseCase, ProductDto> {

    private final ProductRepository repo;
    private final ProductJsonBeanMapper mapper;
    private final DateTimeService dateTimeService;
    private final UuidGenerator uuidGenerator;

    @Override
    public Class<CreateProductUseCase> useCaseType() { return CreateProductUseCase.class; }

    @Override
    @Transactional
    public ProductDto handle(CreateProductUseCase uc) {
        var now = dateTimeService.now().atOffset(java.time.ZoneOffset.UTC).toLocalDateTime();
        var pojo = new ProductsPojo();
        pojo.setId(uuidGenerator.generate());
        pojo.setTitle(uc.title());
        pojo.setDescription(uc.description());
        pojo.setPrice(uc.price());
        pojo.setCurrency(uc.currency());
        pojo.setSellerId(uc.sellerId().value());
        pojo.setStatus(ProductStatus.DRAFT);
        pojo.setCreatedAt(now);
        pojo.setUpdatedAt(now);
        repo.insert(pojo);
        return mapper.toDto(pojo);
    }
}
