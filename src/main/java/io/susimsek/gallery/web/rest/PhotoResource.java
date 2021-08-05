package io.susimsek.gallery.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.susimsek.gallery.repository.PhotoRepository;
import io.susimsek.gallery.service.PhotoQueryService;
import io.susimsek.gallery.service.PhotoService;
import io.susimsek.gallery.service.criteria.PhotoCriteria;
import io.susimsek.gallery.service.dto.PhotoDto;
import io.susimsek.gallery.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.susimsek.gallery.domain.Photo}.
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PhotoResource {


    static final String ENTITY_NAME = "photo";

    @Value("${jhipster.clientApp.name}")
    String applicationName;

    final PhotoService photoService;

    final PhotoRepository photoRepository;

    final PhotoQueryService photoQueryService;

    /**
     * {@code POST  /photos} : Create a new photo.
     *
     * @param photoDto the photoDto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new photoDto, or with status {@code 400 (Bad Request)} if the photo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/photos")
    public ResponseEntity<PhotoDto> createPhoto(@Valid @RequestBody PhotoDto photoDto) throws URISyntaxException {
        log.debug("REST request to save Photo : {}", photoDto);
        if (photoDto.getId() != null) {
            throw new BadRequestAlertException("A new photo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PhotoDto result = photoService.save(photoDto);
        return ResponseEntity
            .created(new URI("/api/photos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /photos/:id} : Updates an existing photo.
     *
     * @param id the id of the photoDto to save.
     * @param photoDto the photoDto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated photoDto,
     * or with status {@code 400 (Bad Request)} if the photoDto is not valid,
     * or with status {@code 500 (Internal Server Error)} if the photoDto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/photos/{id}")
    public ResponseEntity<PhotoDto> updatePhoto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PhotoDto photoDto
    ) throws URISyntaxException {
        log.debug("REST request to update Photo : {}, {}", id, photoDto);
        if (photoDto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, photoDto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!photoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PhotoDto result = photoService.save(photoDto);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, photoDto.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /photos/:id} : Partial updates given fields of an existing photo, field will ignore if it is null
     *
     * @param id the id of the photoDto to save.
     * @param photoDto the photoDto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated photoDto,
     * or with status {@code 400 (Bad Request)} if the photoDto is not valid,
     * or with status {@code 404 (Not Found)} if the photoDto is not found,
     * or with status {@code 500 (Internal Server Error)} if the photoDto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/photos/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<PhotoDto> partialUpdatePhoto(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PhotoDto photoDto
    ) throws URISyntaxException {
        log.debug("REST request to partial update Photo partially : {}, {}", id, photoDto);
        if (photoDto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, photoDto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!photoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PhotoDto> result = photoService.partialUpdate(photoDto);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, photoDto.getId().toString())
        );
    }

    /**
     * {@code GET  /photos} : get all the photos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of photos in body.
     */
    @GetMapping("/photos")
    public ResponseEntity<List<PhotoDto>> getAllPhotos(PhotoCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Photos by criteria: {}", criteria);
        Page<PhotoDto> page = photoQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /photos/count} : count all the photos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/photos/count")
    public ResponseEntity<Long> countPhotos(PhotoCriteria criteria) {
        log.debug("REST request to count Photos by criteria: {}", criteria);
        return ResponseEntity.ok().body(photoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /photos/:id} : get the "id" photo.
     *
     * @param id the id of the photoDto to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the photoDto, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/photos/{id}")
    public ResponseEntity<PhotoDto> getPhoto(@PathVariable Long id) {
        log.debug("REST request to get Photo : {}", id);
        Optional<PhotoDto> photoDto = photoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(photoDto);
    }

    /**
     * {@code DELETE  /photos/:id} : delete the "id" photo.
     *
     * @param id the id of the photoDto to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/photos/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        log.debug("REST request to delete Photo : {}", id);
        photoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/photos?query=:query} : search for the photo corresponding
     * to the query.
     *
     * @param query the query of the photo search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/photos")
    public ResponseEntity<List<PhotoDto>> searchPhotos(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Photos for query {}", query);
        Page<PhotoDto> page = photoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
