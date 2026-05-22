package ru.remodov.catalog.usecase.product;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.remodov.catalog.audit.AuditLogger;
import ru.remodov.catalog.core.service.DateTimeService;
import ru.remodov.catalog.exception.InvalidStateTransitionException;
import ru.remodov.catalog.exception.OwnProductRequiredException;
import ru.remodov.catalog.exception.ProductNotFoundException;
import ru.remodov.catalog.generated.enums.ProductStatus;
import ru.remodov.catalog.generated.tables.pojos.ProductsPojo;
import ru.remodov.catalog.generated.api.model.ProductDto;
import ru.remodov.catalog.mapper.ProductJsonBeanMapper;
import ru.remodov.catalog.repository.ProductRepository;
import ru.vikulinva.usecase.UseCaseHandler;

@Component
@RequiredArgsConstructor
public class HideProductUseCaseHandler implements UseCaseHandler<HideProductUseCase, ProductDto> {

    private final ProductRepository repo;
    private final ProductJsonBeanMapper mapper;
    private final DateTimeService dateTimeService;
    private final AuditLogger auditLogger;

    @Override
    public Class<HideProductUseCase> useCaseType() { return HideProductUseCase.class; }

    @Override
    @Transactional
    public ProductDto handle(HideProductUseCase uc) {
        ProductsPojo product = repo.findById(uc.productId().value())
            .orElseThrow(() -> new ProductNotFoundException(uc.productId().value()));

        if (!uc.isAdmin() && !product.getSellerId().equals(uc.requesterSellerId().value())) {
            throw new OwnProductRequiredException(uc.productId().value());
        }

        ProductStatus current = product.getStatus();
        if (current != ProductStatus.PUBLISHED) {
            throw new InvalidStateTransitionException(current, ProductStatus.HIDDEN);
        }

        var now = dateTimeService.now();
        repo.updateStatus(product.getId(), ProductStatus.HIDDEN, now);

        product.setStatus(ProductStatus.HIDDEN);
        product.setUpdatedAt(now.atOffset(java.time.ZoneOffset.UTC).toLocalDateTime());

        if (uc.isAdmin()) {
            auditLogger.recordAdminAction(
                uc.requesterSellerId(),
                AuditLogger.ACTION_PRODUCT_HIDDEN,
                product.getId(),
                Map.of("from", current.toString(), "to", ProductStatus.HIDDEN.toString(),
                       "ownerSellerId", product.getSellerId().toString())
            );
        }

        return mapper.toDto(product);
    }
}
