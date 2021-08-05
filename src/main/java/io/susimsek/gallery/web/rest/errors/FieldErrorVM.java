package io.susimsek.gallery.web.rest.errors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public class FieldErrorVM implements Serializable {

    static final long serialVersionUID = 1L;

    final String objectName;

    final String field;

    final String message;
}
