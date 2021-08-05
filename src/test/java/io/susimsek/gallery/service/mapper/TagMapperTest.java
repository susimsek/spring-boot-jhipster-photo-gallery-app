package io.susimsek.gallery.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TagMapperTest {

    private TagMapper tagMapper;

    @BeforeEach
    public void setUp() {
        tagMapper = new TagMapperImpl();
    }
}
