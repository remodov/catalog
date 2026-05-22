package ru.remodov.catalog.repository;

import static ru.remodov.catalog.generated.Tables.PRODUCTS;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.springframework.stereotype.Repository;
import ru.remodov.catalog.generated.enums.ProductStatus;
import ru.remodov.catalog.generated.tables.pojos.ProductsPojo;
import ru.remodov.catalog.generated.tables.records.ProductsRecord;

@Repository
@RequiredArgsConstructor
public class JooqProductRepository implements ProductRepository {

    private final DSLContext dsl;

    @Override
    public void insert(ProductsPojo product) {
        ProductsRecord rec = dsl.newRecord(PRODUCTS, product);
        rec.insert();
    }

    @Override
    public Optional<ProductsPojo> findById(UUID id) {
        return Optional.ofNullable(
            dsl.selectFrom(PRODUCTS)
                .where(PRODUCTS.ID.eq(id))
                .fetchOneInto(ProductsPojo.class)
        );
    }

    @Override
    public void updateStatus(UUID id, ProductStatus newStatus, Instant updatedAt) {
        dsl.update(PRODUCTS)
            .set(PRODUCTS.STATUS, newStatus)
            .set(PRODUCTS.UPDATED_AT, updatedAt.atOffset(java.time.ZoneOffset.UTC).toLocalDateTime())
            .where(PRODUCTS.ID.eq(id))
            .execute();
    }

    @Override
    public long countBySeller(UUID sellerId, ProductStatus statusFilterOrNull) {
        var cond = PRODUCTS.SELLER_ID.eq(sellerId);
        if (statusFilterOrNull != null) {
            cond = cond.and(PRODUCTS.STATUS.eq(statusFilterOrNull));
        }
        Integer count = dsl.selectCount().from(PRODUCTS).where(cond).fetchOne(0, Integer.class);
        return count == null ? 0L : count;
    }

    @Override
    public List<ProductsPojo> findBySeller(
        UUID sellerId,
        ProductStatus statusFilterOrNull,
        int offset,
        int limit,
        ProductRepository.SortField sort
    ) {
        var cond = PRODUCTS.SELLER_ID.eq(sellerId);
        if (statusFilterOrNull != null) {
            cond = cond.and(PRODUCTS.STATUS.eq(statusFilterOrNull));
        }
        return dsl.selectFrom(PRODUCTS)
            .where(cond)
            .orderBy(orderBy(sort))
            .offset(offset)
            .limit(limit)
            .fetchInto(ProductsPojo.class);
    }

    private SortField<?> orderBy(ProductRepository.SortField sort) {
        return switch (sort) {
            case CREATED_AT_ASC  -> PRODUCTS.CREATED_AT.asc();
            case CREATED_AT_DESC -> PRODUCTS.CREATED_AT.desc();
            case UPDATED_AT_ASC  -> PRODUCTS.UPDATED_AT.asc();
            case UPDATED_AT_DESC -> PRODUCTS.UPDATED_AT.desc();
            case TITLE_ASC       -> PRODUCTS.TITLE.asc();
            case TITLE_DESC      -> PRODUCTS.TITLE.desc();
        };
    }
}
