package io.susimsek.gallery.service.mapper;

import io.susimsek.gallery.domain.*;
import io.susimsek.gallery.service.dto.TagDto;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tag} and its DTO {@link TagDto}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TagMapper extends EntityMapper<TagDto, Tag> {
    @Named("nameSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Set<TagDto> toDtoNameSet(Set<Tag> tag);
}
