package io.susimsek.gallery.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.susimsek.gallery.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TagDtoTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TagDto.class);
        TagDto tagDto1 = new TagDto();
        tagDto1.setId(1L);
        TagDto tagDto2 = new TagDto();
        assertThat(tagDto1).isNotEqualTo(tagDto2);
        tagDto2.setId(tagDto1.getId());
        assertThat(tagDto1).isEqualTo(tagDto2);
        tagDto2.setId(2L);
        assertThat(tagDto1).isNotEqualTo(tagDto2);
        tagDto1.setId(null);
        assertThat(tagDto1).isNotEqualTo(tagDto2);
    }
}
