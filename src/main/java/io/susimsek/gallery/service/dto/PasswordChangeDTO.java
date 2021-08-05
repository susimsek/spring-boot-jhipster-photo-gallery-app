package io.susimsek.gallery.service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * A DTO representing a password change required data - current and new password.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangeDTO {

    String currentPassword;
    String newPassword;
}
