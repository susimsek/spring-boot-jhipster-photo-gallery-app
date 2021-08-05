package io.susimsek.gallery.service;

import io.susimsek.gallery.domain.*; // for static metamodels
import io.susimsek.gallery.domain.Photo;
import io.susimsek.gallery.repository.PhotoRepository;
import io.susimsek.gallery.repository.search.PhotoSearchRepository;
import io.susimsek.gallery.service.criteria.PhotoCriteria;
import io.susimsek.gallery.service.dto.PhotoDto;
import io.susimsek.gallery.service.mapper.PhotoMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Photo} entities in the database.
 * The main input is a {@link PhotoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PhotoDto} or a {@link Page} of {@link PhotoDto} which fulfills the criteria.
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PhotoQueryService extends QueryService<Photo> {

   final PhotoRepository photoRepository;

   final PhotoMapper photoMapper;

   final PhotoSearchRepository photoSearchRepository;

    /**
     * Return a {@link List} of {@link PhotoDto} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PhotoDto> findByCriteria(PhotoCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Photo> specification = createSpecification(criteria);
        return photoMapper.toDto(photoRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PhotoDto} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PhotoDto> findByCriteria(PhotoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Photo> specification = createSpecification(criteria);
        return photoRepository.findAll(specification, page).map(photoMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PhotoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Photo> specification = createSpecification(criteria);
        return photoRepository.count(specification);
    }

    /**
     * Function to convert {@link PhotoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Photo> createSpecification(PhotoCriteria criteria) {
        Specification<Photo> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Photo_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Photo_.title));
            }
            if (criteria.getHeight() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHeight(), Photo_.height));
            }
            if (criteria.getWidth() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getWidth(), Photo_.width));
            }
            if (criteria.getTaken() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTaken(), Photo_.taken));
            }
            if (criteria.getUploaded() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUploaded(), Photo_.uploaded));
            }
            if (criteria.getAlbumId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getAlbumId(), root -> root.join(Photo_.album, JoinType.LEFT).get(Album_.id))
                    );
            }
            if (criteria.getTagId() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getTagId(), root -> root.join(Photo_.tags, JoinType.LEFT).get(Tag_.id)));
            }
        }
        return specification;
    }
}
