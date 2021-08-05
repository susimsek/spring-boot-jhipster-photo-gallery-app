package io.susimsek.gallery.service;

import io.susimsek.gallery.service.dto.TagDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.susimsek.gallery.domain.Tag}.
 */
public interface TagService {
    /**
     * Save a tag.
     *
     * @param tagDto the entity to save.
     * @return the persisted entity.
     */
    TagDto save(TagDto tagDto);

    /**
     * Partially updates a tag.
     *
     * @param tagDto the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TagDto> partialUpdate(TagDto tagDto);

    /**
     * Get all the tags.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TagDto> findAll(Pageable pageable);

    /**
     * Get the "id" tag.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TagDto> findOne(Long id);

    /**
     * Delete the "id" tag.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the tag corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TagDto> search(String query, Pageable pageable);
}
