package io.susimsek.gallery.service.dto;

import io.susimsek.gallery.config.Constants;
import io.susimsek.gallery.domain.Authority;
import io.susimsek.gallery.domain.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.*;

/**
 * A DTO representing a user, with his authorities.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserDto {

    Long id;

    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    String login;

    @Size(max = 50)
    String firstName;

    @Size(max = 50)
    String lastName;

    @Email
    @Size(min = 5, max = 254)
    String email;

    @Size(max = 256)
    String imageUrl;

    boolean activated = false;

    @Size(min = 2, max = 10)
    String langKey;

    String createdBy;

    Instant createdDate;

    String lastModifiedBy;

    Instant lastModifiedDate;

    Set<String> authorities;


    public AdminUserDto(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.activated = user.isActivated();
        this.imageUrl = user.getImageUrl();
        this.langKey = user.getLangKey();
        this.createdBy = user.getCreatedBy();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
    }
}
