package io.susimsek.gallery.service.mapper;

import io.susimsek.gallery.domain.*;
import io.susimsek.gallery.service.dto.AlbumDto;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Album} and its DTO {@link AlbumDto}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface AlbumMapper extends EntityMapper<AlbumDto, Album> {
    @Mapping(target = "user", source = "user", qualifiedByName = "login")
    AlbumDto toDto(Album s);

    @Named("title")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    AlbumDto toDtoTitle(Album album);
}
