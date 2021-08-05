package io.susimsek.gallery.service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.susimsek.gallery.domain.Tag} entity.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDto implements Serializable {

    Long id;

    @NotNull
    @Size(min = 2)
    String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagDto)) {
            return false;
        }

        TagDto tagDto = (TagDto) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tagDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
