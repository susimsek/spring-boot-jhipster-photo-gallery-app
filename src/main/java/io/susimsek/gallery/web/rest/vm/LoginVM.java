package io.susimsek.gallery.web.rest.vm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * View Model object for storing a user's credentials.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
public class LoginVM {

    @NotNull
    @Size(min = 1, max = 50)
    String username;

    @ToString.Exclude
    @NotNull
    @Size(min = 4, max = 100)
    String password;

    boolean rememberMe;
}
