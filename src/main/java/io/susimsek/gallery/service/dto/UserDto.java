package io.susimsek.gallery.service.dto;

import io.susimsek.gallery.domain.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * A DTO representing a user, with only the public attributes.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    Long id;

    String login;

    public UserDto(User user) {
        this.id = user.getId();
        // Customize it here if you need, or not, firstName/lastName/etc
        this.login = user.getLogin();
    }
}
