package io.susimsek.gallery.service;

import io.susimsek.gallery.service.dto.PhotoDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.susimsek.gallery.domain.Photo}.
 */
public interface PhotoService {
    /**
     * Save a photo.
     *
     * @param photoDto the entity to save.
     * @return the persisted entity.
     */
    PhotoDto save(PhotoDto photoDto);

    /**
     * Partially updates a photo.
     *
     * @param photoDto the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PhotoDto> partialUpdate(PhotoDto photoDto);

    /**
     * Get all the photos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PhotoDto> findAll(Pageable pageable);

    /**
     * Get all the photos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PhotoDto> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" photo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PhotoDto> findOne(Long id);

    /**
     * Delete the "id" photo.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the photo corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PhotoDto> search(String query, Pageable pageable);
}
