package io.susimsek.gallery.service;

import io.susimsek.gallery.domain.*; // for static metamodels
import io.susimsek.gallery.domain.Tag;
import io.susimsek.gallery.repository.TagRepository;
import io.susimsek.gallery.repository.search.TagSearchRepository;
import io.susimsek.gallery.service.criteria.TagCriteria;
import io.susimsek.gallery.service.dto.TagDto;
import io.susimsek.gallery.service.mapper.TagMapper;
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
 * Service for executing complex queries for {@link Tag} entities in the database.
 * The main input is a {@link TagCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TagDto} or a {@link Page} of {@link TagDto} which fulfills the criteria.
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TagQueryService extends QueryService<Tag> {

    final TagRepository tagRepository;

    final TagMapper tagMapper;

    final TagSearchRepository tagSearchRepository;

    /**
     * Return a {@link List} of {@link TagDto} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TagDto> findByCriteria(TagCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Tag> specification = createSpecification(criteria);
        return tagMapper.toDto(tagRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TagDto} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TagDto> findByCriteria(TagCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Tag> specification = createSpecification(criteria);
        return tagRepository.findAll(specification, page).map(tagMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TagCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Tag> specification = createSpecification(criteria);
        return tagRepository.count(specification);
    }

    /**
     * Function to convert {@link TagCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Tag> createSpecification(TagCriteria criteria) {
        Specification<Tag> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Tag_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Tag_.name));
            }
            if (criteria.getPhotoId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPhotoId(), root -> root.join(Tag_.photos, JoinType.LEFT).get(Photo_.id))
                    );
            }
        }
        return specification;
    }
}
