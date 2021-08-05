package io.susimsek.gallery.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.susimsek.gallery.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PhotoDtoTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PhotoDto.class);
        PhotoDto photoDto1 = new PhotoDto();
        photoDto1.setId(1L);
        PhotoDto photoDto2 = new PhotoDto();
        assertThat(photoDto1).isNotEqualTo(photoDto2);
        photoDto2.setId(photoDto1.getId());
        assertThat(photoDto1).isEqualTo(photoDto2);
        photoDto2.setId(2L);
        assertThat(photoDto1).isNotEqualTo(photoDto2);
        photoDto1.setId(null);
        assertThat(photoDto1).isNotEqualTo(photoDto2);
    }
}
