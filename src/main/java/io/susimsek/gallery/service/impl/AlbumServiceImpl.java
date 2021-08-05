package io.susimsek.gallery.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import io.susimsek.gallery.domain.Album;
import io.susimsek.gallery.repository.AlbumRepository;
import io.susimsek.gallery.repository.search.AlbumSearchRepository;
import io.susimsek.gallery.service.AlbumService;
import io.susimsek.gallery.service.dto.AlbumDto;
import io.susimsek.gallery.service.mapper.AlbumMapper;
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
 * Service Implementation for managing {@link Album}.
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Service
@Transactional
public class AlbumServiceImpl implements AlbumService {

    final AlbumRepository albumRepository;

    final AlbumMapper albumMapper;

    final AlbumSearchRepository albumSearchRepository;

    @Override
    public AlbumDto save(AlbumDto albumDto) {
        log.debug("Request to save Album : {}", albumDto);
        Album album = albumMapper.toEntity(albumDto);
        album = albumRepository.save(album);
        AlbumDto result = albumMapper.toDto(album);
        albumSearchRepository.save(album);
        return result;
    }

    @Override
    public Optional<AlbumDto> partialUpdate(AlbumDto albumDto) {
        log.debug("Request to partially update Album : {}", albumDto);

        return albumRepository
            .findById(albumDto.getId())
            .map(
                existingAlbum -> {
                    albumMapper.partialUpdate(existingAlbum, albumDto);

                    return existingAlbum;
                }
            )
            .map(albumRepository::save)
            .map(
                savedAlbum -> {
                    albumSearchRepository.save(savedAlbum);

                    return savedAlbum;
                }
            )
            .map(albumMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumDto> findAll(Pageable pageable) {
        log.debug("Request to get all Albums");
        return albumRepository.findAll(pageable).map(albumMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AlbumDto> findOne(Long id) {
        log.debug("Request to get Album : {}", id);
        return albumRepository.findById(id).map(albumMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Album : {}", id);
        albumRepository.deleteById(id);
        albumSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumDto> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Albums for query {}", query);
        return albumSearchRepository.search(queryStringQuery(query), pageable).map(albumMapper::toDto);
    }
}
