package ru.remodov.catalog.usecase.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import ru.remodov.catalog.generated.api.model.ProductPageDto;
import ru.remodov.catalog.generated.enums.ProductStatus;
import ru.remodov.catalog.testsupport.CatalogBaseIntegrationTest;
import ru.remodov.catalog.testsupport.ProductTestObjectGenerator;
import ru.remodov.catalog.testsupport.TestHttpHeaders;

class ListMyProductsIntegrationTest extends CatalogBaseIntegrationTest {

    private static final String URL = "/api/v1/products/my";

    private final UUID sellerId = UUID.randomUUID();
    private final UUID otherSellerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        databasePreparer.clearAuditLog().clearProducts().prepare();
    }

    @Test
    @DisplayName("AC-C9: GET /products/my возвращает только продукты текущего seller'а")
    void listMy_returnsOnlyOwnProducts() {
        databasePreparer
            .createProduct(new ProductTestObjectGenerator().withSellerId(sellerId).withStatus(ProductStatus.DRAFT).generate())
            .createProduct(new ProductTestObjectGenerator().withSellerId(sellerId).withStatus(ProductStatus.PUBLISHED).generate())
            .createProduct(new ProductTestObjectGenerator().withSellerId(otherSellerId).withStatus(ProductStatus.PUBLISHED).generate())
            .prepare();

        var response = restTemplate.exchange(
            URL + "?page=1&size=20", HttpMethod.GET,
            new HttpEntity<>(TestHttpHeaders.withSellerToken(sellerId)),
            ProductPageDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var page = response.getBody();
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).allSatisfy(p -> assertThat(p.getSellerId()).isEqualTo(sellerId));
    }

    @Test
    @DisplayName("AC-C9: фильтр status=DRAFT возвращает только DRAFT")
    void listMy_withStatusFilter_returnsOnlyMatching() {
        databasePreparer
            .createProduct(new ProductTestObjectGenerator().withSellerId(sellerId).withStatus(ProductStatus.DRAFT).generate())
            .createProduct(new ProductTestObjectGenerator().withSellerId(sellerId).withStatus(ProductStatus.PUBLISHED).generate())
            .prepare();

        var response = restTemplate.exchange(
            URL + "?status=DRAFT&page=1&size=20", HttpMethod.GET,
            new HttpEntity<>(TestHttpHeaders.withSellerToken(sellerId)),
            ProductPageDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).hasSize(1);
        assertThat(response.getBody().getContent().get(0).getStatus().name()).isEqualTo("DRAFT");
    }
}
