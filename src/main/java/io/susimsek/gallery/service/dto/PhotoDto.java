package io.susimsek.gallery.service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.susimsek.gallery.domain.Photo} entity.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoDto implements Serializable {

    Long id;

    @NotNull
    String title;

    @Lob
    String description;

    @Lob
    byte[] image;

    String imageContentType;
    Integer height;

    Integer width;

    Instant taken;

    Instant uploaded;

    AlbumDto album;

    @Builder.Default
    Set<TagDto> tags = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PhotoDto)) {
            return false;
        }

        PhotoDto photoDto = (PhotoDto) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, photoDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
