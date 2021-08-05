package io.susimsek.gallery.service;

import io.susimsek.gallery.service.dto.AlbumDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.susimsek.gallery.domain.Album}.
 */
public interface AlbumService {
    /**
     * Save a album.
     *
     * @param albumDto the entity to save.
     * @return the persisted entity.
     */
    AlbumDto save(AlbumDto albumDto);

    /**
     * Partially updates a album.
     *
     * @param albumDto the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AlbumDto> partialUpdate(AlbumDto albumDto);

    /**
     * Get all the albums.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AlbumDto> findAll(Pageable pageable);

    /**
     * Get the "id" album.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AlbumDto> findOne(Long id);

    /**
     * Delete the "id" album.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the album corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AlbumDto> search(String query, Pageable pageable);
}
