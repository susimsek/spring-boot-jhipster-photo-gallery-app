package io.susimsek.gallery.service.mapper;

import io.susimsek.gallery.domain.*;
import io.susimsek.gallery.service.dto.PhotoDto;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Photo} and its DTO {@link PhotoDto}.
 */
@Mapper(componentModel = "spring", uses = { AlbumMapper.class, TagMapper.class })
public interface PhotoMapper extends EntityMapper<PhotoDto, Photo> {
    @Mapping(target = "album", source = "album", qualifiedByName = "title")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "nameSet")
    PhotoDto toDto(Photo s);

    Photo toEntity(PhotoDto photoDto);
}
