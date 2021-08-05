package io.susimsek.gallery.security;

import lombok.NoArgsConstructor;

/**
 * Constants for Spring Security authorities.
 */
@NoArgsConstructor
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
}
