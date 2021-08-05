package io.susimsek.gallery.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PhotoMapperTest {

    private PhotoMapper photoMapper;

    @BeforeEach
    public void setUp() {
        photoMapper = new PhotoMapperImpl();
    }
}
