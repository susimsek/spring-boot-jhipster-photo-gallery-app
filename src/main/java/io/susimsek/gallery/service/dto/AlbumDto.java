package io.susimsek.gallery.service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.susimsek.gallery.domain.Album} entity.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumDto implements Serializable {

    Long id;

    @NotNull
    String title;

    @Lob
    String description;

    Instant created;

    UserDto user;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AlbumDto)) {
            return false;
        }

        AlbumDto albumDto = (AlbumDto) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, albumDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
