package io.susimsek.gallery.web.rest.vm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * View Model object for storing the user's key and password.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class KeyAndPasswordVM {

    String key;

    String newPassword;
}
