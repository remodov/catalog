package ru.remodov.catalog.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.remodov.catalog.generated.api.model.ProductDto;
import ru.remodov.catalog.generated.tables.pojos.ProductsPojo;

@Mapper(componentModel = "spring")
public interface ProductJsonBeanMapper {

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "toUtcOffset")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "toUtcOffset")
    ProductDto toDto(ProductsPojo pojo);

    ru.remodov.catalog.generated.api.model.ProductStatus toApiStatus(
        ru.remodov.catalog.generated.enums.ProductStatus dbStatus);

    ru.remodov.catalog.generated.enums.ProductStatus toDbStatus(
        ru.remodov.catalog.generated.api.model.ProductStatus apiStatus);

    @Named("toUtcOffset")
    default OffsetDateTime toUtcOffset(LocalDateTime ldt) {
        return ldt == null ? null : ldt.atOffset(ZoneOffset.UTC);
    }
}
