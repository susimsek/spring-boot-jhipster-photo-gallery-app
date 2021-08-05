package io.susimsek.gallery.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link TagSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class TagSearchRepositoryMockConfiguration {

    @MockBean
    private TagSearchRepository mockTagSearchRepository;
}
