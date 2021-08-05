package io.susimsek.gallery.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.susimsek.gallery.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AlbumDtoTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AlbumDto.class);
        AlbumDto albumDto1 = new AlbumDto();
        albumDto1.setId(1L);
        AlbumDto albumDto2 = new AlbumDto();
        assertThat(albumDto1).isNotEqualTo(albumDto2);
        albumDto2.setId(albumDto1.getId());
        assertThat(albumDto1).isEqualTo(albumDto2);
        albumDto2.setId(2L);
        assertThat(albumDto1).isNotEqualTo(albumDto2);
        albumDto1.setId(null);
        assertThat(albumDto1).isNotEqualTo(albumDto2);
    }
}
