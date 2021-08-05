package io.susimsek.gallery.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.susimsek.gallery.repository.TagRepository;
import io.susimsek.gallery.service.TagQueryService;
import io.susimsek.gallery.service.TagService;
import io.susimsek.gallery.service.criteria.TagCriteria;
import io.susimsek.gallery.service.dto.TagDto;
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
 * REST controller for managing {@link io.susimsek.gallery.domain.Tag}.
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TagResource {

    static final String ENTITY_NAME = "tag";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    final TagService tagService;

    final TagRepository tagRepository;

    final TagQueryService tagQueryService;

    /**
     * {@code POST  /tags} : Create a new tag.
     *
     * @param tagDto the tagDto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tagDto, or with status {@code 400 (Bad Request)} if the tag has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tags")
    public ResponseEntity<TagDto> createTag(@Valid @RequestBody TagDto tagDto) throws URISyntaxException {
        log.debug("REST request to save Tag : {}", tagDto);
        if (tagDto.getId() != null) {
            throw new BadRequestAlertException("A new tag cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TagDto result = tagService.save(tagDto);
        return ResponseEntity
            .created(new URI("/api/tags/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tags/:id} : Updates an existing tag.
     *
     * @param id the id of the tagDto to save.
     * @param tagDto the tagDto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagDto,
     * or with status {@code 400 (Bad Request)} if the tagDto is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tagDto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tags/{id}")
    public ResponseEntity<TagDto> updateTag(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody TagDto tagDto)
        throws URISyntaxException {
        log.debug("REST request to update Tag : {}, {}", id, tagDto);
        if (tagDto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tagDto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TagDto result = tagService.save(tagDto);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tagDto.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tags/:id} : Partial updates given fields of an existing tag, field will ignore if it is null
     *
     * @param id the id of the tagDto to save.
     * @param tagDto the tagDto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagDto,
     * or with status {@code 400 (Bad Request)} if the tagDto is not valid,
     * or with status {@code 404 (Not Found)} if the tagDto is not found,
     * or with status {@code 500 (Internal Server Error)} if the tagDto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tags/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<TagDto> partialUpdateTag(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TagDto tagDto
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tag partially : {}, {}", id, tagDto);
        if (tagDto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tagDto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TagDto> result = tagService.partialUpdate(tagDto);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tagDto.getId().toString())
        );
    }

    /**
     * {@code GET  /tags} : get all the tags.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tags in body.
     */
    @GetMapping("/tags")
    public ResponseEntity<List<TagDto>> getAllTags(TagCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Tags by criteria: {}", criteria);
        Page<TagDto> page = tagQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tags/count} : count all the tags.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/tags/count")
    public ResponseEntity<Long> countTags(TagCriteria criteria) {
        log.debug("REST request to count Tags by criteria: {}", criteria);
        return ResponseEntity.ok().body(tagQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tags/:id} : get the "id" tag.
     *
     * @param id the id of the tagDto to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tagDto, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tags/{id}")
    public ResponseEntity<TagDto> getTag(@PathVariable Long id) {
        log.debug("REST request to get Tag : {}", id);
        Optional<TagDto> tagDto = tagService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tagDto);
    }

    /**
     * {@code DELETE  /tags/:id} : delete the "id" tag.
     *
     * @param id the id of the tagDto to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tags/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        log.debug("REST request to delete Tag : {}", id);
        tagService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/tags?query=:query} : search for the tag corresponding
     * to the query.
     *
     * @param query the query of the tag search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/tags")
    public ResponseEntity<List<TagDto>> searchTags(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Tags for query {}", query);
        Page<TagDto> page = tagService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
