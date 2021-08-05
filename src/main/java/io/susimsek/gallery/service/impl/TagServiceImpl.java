package io.susimsek.gallery.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.susimsek.gallery.domain.Tag;
import io.susimsek.gallery.repository.TagRepository;
import io.susimsek.gallery.repository.search.TagSearchRepository;
import io.susimsek.gallery.service.TagService;
import io.susimsek.gallery.service.dto.TagDto;
import io.susimsek.gallery.service.mapper.TagMapper;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Tag}.
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Service
@Transactional
public class TagServiceImpl implements TagService {

    final TagRepository tagRepository;

    final TagMapper tagMapper;

    final TagSearchRepository tagSearchRepository;

    @Override
    public TagDto save(TagDto tagDto) {
        log.debug("Request to save Tag : {}", tagDto);
        Tag tag = tagMapper.toEntity(tagDto);
        tag = tagRepository.save(tag);
        TagDto result = tagMapper.toDto(tag);
        tagSearchRepository.save(tag);
        return result;
    }

    @Override
    public Optional<TagDto> partialUpdate(TagDto tagDto) {
        log.debug("Request to partially update Tag : {}", tagDto);

        return tagRepository
            .findById(tagDto.getId())
            .map(
                existingTag -> {
                    tagMapper.partialUpdate(existingTag, tagDto);

                    return existingTag;
                }
            )
            .map(tagRepository::save)
            .map(
                savedTag -> {
                    tagSearchRepository.save(savedTag);

                    return savedTag;
                }
            )
            .map(tagMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagDto> findAll(Pageable pageable) {
        log.debug("Request to get all Tags");
        return tagRepository.findAll(pageable).map(tagMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TagDto> findOne(Long id) {
        log.debug("Request to get Tag : {}", id);
        return tagRepository.findById(id).map(tagMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Tag : {}", id);
        tagRepository.deleteById(id);
        tagSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagDto> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tags for query {}", query);
        return tagSearchRepository.search(queryStringQuery(query), pageable).map(tagMapper::toDto);
    }
}
