package io.susimsek.gallery.repository.search;

import io.susimsek.gallery.domain.Album;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Album} entity.
 */
public interface AlbumSearchRepository extends ElasticsearchRepository<Album, Long> {}
