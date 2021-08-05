package io.susimsek.gallery.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import io.susimsek.gallery.domain.Photo;
import io.susimsek.gallery.repository.PhotoRepository;
import io.susimsek.gallery.repository.search.PhotoSearchRepository;
import io.susimsek.gallery.service.PhotoService;
import io.susimsek.gallery.service.dto.PhotoDto;
import io.susimsek.gallery.service.mapper.PhotoMapper;
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

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing {@link Photo}.
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Service
@Transactional
public class PhotoServiceImpl implements PhotoService {

    final PhotoRepository photoRepository;

    final PhotoMapper photoMapper;

    final PhotoSearchRepository photoSearchRepository;

    @Override
    public PhotoDto save(PhotoDto photoDto) {
        log.debug("Request to save Photo : {}", photoDto);

        try {
            photoDto = setMetadata(photoDto);
        } catch (ImageProcessingException | IOException | MetadataException ipe) {
            log.error(ipe.getMessage());
        }

        Photo photo = photoMapper.toEntity(photoDto);
        photo = photoRepository.save(photo);
        PhotoDto result = photoMapper.toDto(photo);
        photoSearchRepository.save(photo);
        return result;
    }

    @Override
    public Optional<PhotoDto> partialUpdate(PhotoDto photoDto) {
        log.debug("Request to partially update Photo : {}", photoDto);

        return photoRepository
            .findById(photoDto.getId())
            .map(
                existingPhoto -> {
                    photoMapper.partialUpdate(existingPhoto, photoDto);

                    return existingPhoto;
                }
            )
            .map(photoRepository::save)
            .map(
                savedPhoto -> {
                    photoSearchRepository.save(savedPhoto);

                    return savedPhoto;
                }
            )
            .map(photoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PhotoDto> findAll(Pageable pageable) {
        log.debug("Request to get all Photos");
        return photoRepository.findAll(pageable).map(photoMapper::toDto);
    }

    public Page<PhotoDto> findAllWithEagerRelationships(Pageable pageable) {
        return photoRepository.findAllWithEagerRelationships(pageable).map(photoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PhotoDto> findOne(Long id) {
        log.debug("Request to get Photo : {}", id);
        return photoRepository.findOneWithEagerRelationships(id).map(photoMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Photo : {}", id);
        photoRepository.deleteById(id);
        photoSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PhotoDto> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Photos for query {}", query);
        return photoSearchRepository.search(queryStringQuery(query), pageable).map(photoMapper::toDto);
    }

    private PhotoDto setMetadata(PhotoDto photoDto) throws ImageProcessingException, IOException, MetadataException {
        String str = DatatypeConverter.printBase64Binary(photoDto.getImage());
        byte[] data2 = DatatypeConverter.parseBase64Binary(str);
        InputStream inputStream = new ByteArrayInputStream(data2);
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        Metadata metadata = ImageMetadataReader.readMetadata(bis);
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        if (directory != null) {
            Date date = directory.getDateDigitized();
            if (date != null) {
                photoDto.setTaken(date.toInstant());
            }
        }

        if (photoDto.getTaken() == null) {
            log.debug("Photo EXIF date digitized not available, setting taken on date to now...");
            photoDto.setTaken(Instant.now());
        }

        photoDto.setUploaded(Instant.now());

        JpegDirectory jpgDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
        if (jpgDirectory != null) {
            photoDto.setHeight(jpgDirectory.getImageHeight());
            photoDto.setWidth(jpgDirectory.getImageWidth());
        }

        return photoDto;
    }
}
