package io.susimsek.gallery.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.susimsek.gallery.repository.AlbumRepository;
import io.susimsek.gallery.service.AlbumQueryService;
import io.susimsek.gallery.service.AlbumService;
import io.susimsek.gallery.service.criteria.AlbumCriteria;
import io.susimsek.gallery.service.dto.AlbumDto;
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
 * REST controller for managing {@link io.susimsek.gallery.domain.Album}.
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AlbumResource {

    static final String ENTITY_NAME = "album";

    @Value("${jhipster.clientApp.name}")
    String applicationName;

    final AlbumService albumService;

    final AlbumRepository albumRepository;

    final AlbumQueryService albumQueryService;

    /**
     * {@code POST  /albums} : Create a new album.
     *
     * @param albumDto the albumDto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new albumDto, or with status {@code 400 (Bad Request)} if the album has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/albums")
    public ResponseEntity<AlbumDto> createAlbum(@Valid @RequestBody AlbumDto albumDto) throws URISyntaxException {
        log.debug("REST request to save Album : {}", albumDto);
        if (albumDto.getId() != null) {
            throw new BadRequestAlertException("A new album cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AlbumDto result = albumService.save(albumDto);
        return ResponseEntity
            .created(new URI("/api/albums/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /albums/:id} : Updates an existing album.
     *
     * @param id the id of the albumDto to save.
     * @param albumDto the albumDto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated albumDto,
     * or with status {@code 400 (Bad Request)} if the albumDto is not valid,
     * or with status {@code 500 (Internal Server Error)} if the albumDto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/albums/{id}")
    public ResponseEntity<AlbumDto> updateAlbum(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AlbumDto albumDto
    ) throws URISyntaxException {
        log.debug("REST request to update Album : {}, {}", id, albumDto);
        if (albumDto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, albumDto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!albumRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AlbumDto result = albumService.save(albumDto);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, albumDto.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /albums/:id} : Partial updates given fields of an existing album, field will ignore if it is null
     *
     * @param id the id of the albumDto to save.
     * @param albumDto the albumDto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated albumDto,
     * or with status {@code 400 (Bad Request)} if the albumDto is not valid,
     * or with status {@code 404 (Not Found)} if the albumDto is not found,
     * or with status {@code 500 (Internal Server Error)} if the albumDto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/albums/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<AlbumDto> partialUpdateAlbum(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AlbumDto albumDto
    ) throws URISyntaxException {
        log.debug("REST request to partial update Album partially : {}, {}", id, albumDto);
        if (albumDto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, albumDto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!albumRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AlbumDto> result = albumService.partialUpdate(albumDto);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, albumDto.getId().toString())
        );
    }

    /**
     * {@code GET  /albums} : get all the albums.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of albums in body.
     */
    @GetMapping("/albums")
    public ResponseEntity<List<AlbumDto>> getAllAlbums(AlbumCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Albums by criteria: {}", criteria);
        Page<AlbumDto> page = albumQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /albums/count} : count all the albums.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/albums/count")
    public ResponseEntity<Long> countAlbums(AlbumCriteria criteria) {
        log.debug("REST request to count Albums by criteria: {}", criteria);
        return ResponseEntity.ok().body(albumQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /albums/:id} : get the "id" album.
     *
     * @param id the id of the albumDto to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the albumDto, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/albums/{id}")
    public ResponseEntity<AlbumDto> getAlbum(@PathVariable Long id) {
        log.debug("REST request to get Album : {}", id);
        Optional<AlbumDto> albumDto = albumService.findOne(id);
        return ResponseUtil.wrapOrNotFound(albumDto);
    }

    /**
     * {@code DELETE  /albums/:id} : delete the "id" album.
     *
     * @param id the id of the albumDto to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/albums/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        log.debug("REST request to delete Album : {}", id);
        albumService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/albums?query=:query} : search for the album corresponding
     * to the query.
     *
     * @param query the query of the album search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/albums")
    public ResponseEntity<List<AlbumDto>> searchAlbums(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Albums for query {}", query);
        Page<AlbumDto> page = albumService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
