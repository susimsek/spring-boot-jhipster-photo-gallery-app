package io.susimsek.gallery.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.susimsek.gallery.IntegrationTest;
import io.susimsek.gallery.domain.Album;
import io.susimsek.gallery.domain.Photo;
import io.susimsek.gallery.domain.Tag;
import io.susimsek.gallery.repository.PhotoRepository;
import io.susimsek.gallery.repository.search.PhotoSearchRepository;
import io.susimsek.gallery.service.PhotoService;
import io.susimsek.gallery.service.criteria.PhotoCriteria;
import io.susimsek.gallery.service.dto.PhotoDto;
import io.susimsek.gallery.service.mapper.PhotoMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link PhotoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PhotoResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final Integer DEFAULT_HEIGHT = 1;
    private static final Integer UPDATED_HEIGHT = 2;
    private static final Integer SMALLER_HEIGHT = 1 - 1;

    private static final Integer DEFAULT_WIDTH = 1;
    private static final Integer UPDATED_WIDTH = 2;
    private static final Integer SMALLER_WIDTH = 1 - 1;

    private static final Instant DEFAULT_TAKEN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TAKEN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPLOADED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPLOADED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/photos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/photos";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PhotoRepository photoRepository;

    @Mock
    private PhotoRepository photoRepositoryMock;

    @Autowired
    private PhotoMapper photoMapper;

    @Mock
    private PhotoService photoServiceMock;

    /**
     * This repository is mocked in the io.susimsek.gallery.repository.search test package.
     *
     * @see io.susimsek.gallery.repository.search.PhotoSearchRepositoryMockConfiguration
     */
    @Autowired
    private PhotoSearchRepository mockPhotoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPhotoMockMvc;

    private Photo photo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Photo createEntity(EntityManager em) {
        Photo photo =Photo.builder()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .height(DEFAULT_HEIGHT)
            .width(DEFAULT_WIDTH)
            .taken(DEFAULT_TAKEN)
            .uploaded(DEFAULT_UPLOADED)
            .build();
        return photo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Photo createUpdatedEntity(EntityManager em) {
        Photo photo = Photo.builder()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .height(UPDATED_HEIGHT)
            .width(UPDATED_WIDTH)
            .taken(UPDATED_TAKEN)
            .uploaded(UPDATED_UPLOADED)
            .build();
        return photo;
    }

    @BeforeEach
    public void initTest() {
        photo = createEntity(em);
    }

    @Test
    @Transactional
    void createPhoto() throws Exception {
        int databaseSizeBeforeCreate = photoRepository.findAll().size();
        // Create the Photo
        PhotoDto photoDto = photoMapper.toDto(photo);
        restPhotoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(photoDto)))
            .andExpect(status().isCreated());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeCreate + 1);
        Photo testPhoto = photoList.get(photoList.size() - 1);
        assertThat(testPhoto.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPhoto.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPhoto.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testPhoto.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testPhoto.getHeight()).isEqualTo(DEFAULT_HEIGHT);
        assertThat(testPhoto.getWidth()).isEqualTo(DEFAULT_WIDTH);
        assertThat(testPhoto.getTaken()).isEqualTo(DEFAULT_TAKEN);
        assertThat(testPhoto.getUploaded()).isEqualTo(DEFAULT_UPLOADED);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(1)).save(testPhoto);
    }

    @Test
    @Transactional
    void createPhotoWithExistingId() throws Exception {
        // Create the Photo with an existing ID
        photo.setId(1L);
        PhotoDto photoDto = photoMapper.toDto(photo);

        int databaseSizeBeforeCreate = photoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPhotoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(photoDto)))
            .andExpect(status().isBadRequest());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeCreate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = photoRepository.findAll().size();
        // set the field null
        photo.setTitle(null);

        // Create the Photo, which fails.
        PhotoDto photoDto = photoMapper.toDto(photo);

        restPhotoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(photoDto)))
            .andExpect(status().isBadRequest());

        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPhotos() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList
        restPhotoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(photo.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].taken").value(hasItem(DEFAULT_TAKEN.toString())))
            .andExpect(jsonPath("$.[*].uploaded").value(hasItem(DEFAULT_UPLOADED.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPhotosWithEagerRelationshipsIsEnabled() throws Exception {
        when(photoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPhotoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(photoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPhotosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(photoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPhotoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(photoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getPhoto() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get the photo
        restPhotoMockMvc
            .perform(get(ENTITY_API_URL_ID, photo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(photo.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.height").value(DEFAULT_HEIGHT))
            .andExpect(jsonPath("$.width").value(DEFAULT_WIDTH))
            .andExpect(jsonPath("$.taken").value(DEFAULT_TAKEN.toString()))
            .andExpect(jsonPath("$.uploaded").value(DEFAULT_UPLOADED.toString()));
    }

    @Test
    @Transactional
    void getPhotosByIdFiltering() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        Long id = photo.getId();

        defaultPhotoShouldBeFound("id.equals=" + id);
        defaultPhotoShouldNotBeFound("id.notEquals=" + id);

        defaultPhotoShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPhotoShouldNotBeFound("id.greaterThan=" + id);

        defaultPhotoShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPhotoShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPhotosByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where title equals to DEFAULT_TITLE
        defaultPhotoShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the photoList where title equals to UPDATED_TITLE
        defaultPhotoShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPhotosByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where title not equals to DEFAULT_TITLE
        defaultPhotoShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the photoList where title not equals to UPDATED_TITLE
        defaultPhotoShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPhotosByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultPhotoShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the photoList where title equals to UPDATED_TITLE
        defaultPhotoShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPhotosByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where title is not null
        defaultPhotoShouldBeFound("title.specified=true");

        // Get all the photoList where title is null
        defaultPhotoShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllPhotosByTitleContainsSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where title contains DEFAULT_TITLE
        defaultPhotoShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the photoList where title contains UPDATED_TITLE
        defaultPhotoShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPhotosByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where title does not contain DEFAULT_TITLE
        defaultPhotoShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the photoList where title does not contain UPDATED_TITLE
        defaultPhotoShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllPhotosByHeightIsEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where height equals to DEFAULT_HEIGHT
        defaultPhotoShouldBeFound("height.equals=" + DEFAULT_HEIGHT);

        // Get all the photoList where height equals to UPDATED_HEIGHT
        defaultPhotoShouldNotBeFound("height.equals=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    void getAllPhotosByHeightIsNotEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where height not equals to DEFAULT_HEIGHT
        defaultPhotoShouldNotBeFound("height.notEquals=" + DEFAULT_HEIGHT);

        // Get all the photoList where height not equals to UPDATED_HEIGHT
        defaultPhotoShouldBeFound("height.notEquals=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    void getAllPhotosByHeightIsInShouldWork() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where height in DEFAULT_HEIGHT or UPDATED_HEIGHT
        defaultPhotoShouldBeFound("height.in=" + DEFAULT_HEIGHT + "," + UPDATED_HEIGHT);

        // Get all the photoList where height equals to UPDATED_HEIGHT
        defaultPhotoShouldNotBeFound("height.in=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    void getAllPhotosByHeightIsNullOrNotNull() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where height is not null
        defaultPhotoShouldBeFound("height.specified=true");

        // Get all the photoList where height is null
        defaultPhotoShouldNotBeFound("height.specified=false");
    }

    @Test
    @Transactional
    void getAllPhotosByHeightIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where height is greater than or equal to DEFAULT_HEIGHT
        defaultPhotoShouldBeFound("height.greaterThanOrEqual=" + DEFAULT_HEIGHT);

        // Get all the photoList where height is greater than or equal to UPDATED_HEIGHT
        defaultPhotoShouldNotBeFound("height.greaterThanOrEqual=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    void getAllPhotosByHeightIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where height is less than or equal to DEFAULT_HEIGHT
        defaultPhotoShouldBeFound("height.lessThanOrEqual=" + DEFAULT_HEIGHT);

        // Get all the photoList where height is less than or equal to SMALLER_HEIGHT
        defaultPhotoShouldNotBeFound("height.lessThanOrEqual=" + SMALLER_HEIGHT);
    }

    @Test
    @Transactional
    void getAllPhotosByHeightIsLessThanSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where height is less than DEFAULT_HEIGHT
        defaultPhotoShouldNotBeFound("height.lessThan=" + DEFAULT_HEIGHT);

        // Get all the photoList where height is less than UPDATED_HEIGHT
        defaultPhotoShouldBeFound("height.lessThan=" + UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    void getAllPhotosByHeightIsGreaterThanSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where height is greater than DEFAULT_HEIGHT
        defaultPhotoShouldNotBeFound("height.greaterThan=" + DEFAULT_HEIGHT);

        // Get all the photoList where height is greater than SMALLER_HEIGHT
        defaultPhotoShouldBeFound("height.greaterThan=" + SMALLER_HEIGHT);
    }

    @Test
    @Transactional
    void getAllPhotosByWidthIsEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where width equals to DEFAULT_WIDTH
        defaultPhotoShouldBeFound("width.equals=" + DEFAULT_WIDTH);

        // Get all the photoList where width equals to UPDATED_WIDTH
        defaultPhotoShouldNotBeFound("width.equals=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllPhotosByWidthIsNotEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where width not equals to DEFAULT_WIDTH
        defaultPhotoShouldNotBeFound("width.notEquals=" + DEFAULT_WIDTH);

        // Get all the photoList where width not equals to UPDATED_WIDTH
        defaultPhotoShouldBeFound("width.notEquals=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllPhotosByWidthIsInShouldWork() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where width in DEFAULT_WIDTH or UPDATED_WIDTH
        defaultPhotoShouldBeFound("width.in=" + DEFAULT_WIDTH + "," + UPDATED_WIDTH);

        // Get all the photoList where width equals to UPDATED_WIDTH
        defaultPhotoShouldNotBeFound("width.in=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllPhotosByWidthIsNullOrNotNull() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where width is not null
        defaultPhotoShouldBeFound("width.specified=true");

        // Get all the photoList where width is null
        defaultPhotoShouldNotBeFound("width.specified=false");
    }

    @Test
    @Transactional
    void getAllPhotosByWidthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where width is greater than or equal to DEFAULT_WIDTH
        defaultPhotoShouldBeFound("width.greaterThanOrEqual=" + DEFAULT_WIDTH);

        // Get all the photoList where width is greater than or equal to UPDATED_WIDTH
        defaultPhotoShouldNotBeFound("width.greaterThanOrEqual=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllPhotosByWidthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where width is less than or equal to DEFAULT_WIDTH
        defaultPhotoShouldBeFound("width.lessThanOrEqual=" + DEFAULT_WIDTH);

        // Get all the photoList where width is less than or equal to SMALLER_WIDTH
        defaultPhotoShouldNotBeFound("width.lessThanOrEqual=" + SMALLER_WIDTH);
    }

    @Test
    @Transactional
    void getAllPhotosByWidthIsLessThanSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where width is less than DEFAULT_WIDTH
        defaultPhotoShouldNotBeFound("width.lessThan=" + DEFAULT_WIDTH);

        // Get all the photoList where width is less than UPDATED_WIDTH
        defaultPhotoShouldBeFound("width.lessThan=" + UPDATED_WIDTH);
    }

    @Test
    @Transactional
    void getAllPhotosByWidthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where width is greater than DEFAULT_WIDTH
        defaultPhotoShouldNotBeFound("width.greaterThan=" + DEFAULT_WIDTH);

        // Get all the photoList where width is greater than SMALLER_WIDTH
        defaultPhotoShouldBeFound("width.greaterThan=" + SMALLER_WIDTH);
    }

    @Test
    @Transactional
    void getAllPhotosByTakenIsEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where taken equals to DEFAULT_TAKEN
        defaultPhotoShouldBeFound("taken.equals=" + DEFAULT_TAKEN);

        // Get all the photoList where taken equals to UPDATED_TAKEN
        defaultPhotoShouldNotBeFound("taken.equals=" + UPDATED_TAKEN);
    }

    @Test
    @Transactional
    void getAllPhotosByTakenIsNotEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where taken not equals to DEFAULT_TAKEN
        defaultPhotoShouldNotBeFound("taken.notEquals=" + DEFAULT_TAKEN);

        // Get all the photoList where taken not equals to UPDATED_TAKEN
        defaultPhotoShouldBeFound("taken.notEquals=" + UPDATED_TAKEN);
    }

    @Test
    @Transactional
    void getAllPhotosByTakenIsInShouldWork() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where taken in DEFAULT_TAKEN or UPDATED_TAKEN
        defaultPhotoShouldBeFound("taken.in=" + DEFAULT_TAKEN + "," + UPDATED_TAKEN);

        // Get all the photoList where taken equals to UPDATED_TAKEN
        defaultPhotoShouldNotBeFound("taken.in=" + UPDATED_TAKEN);
    }

    @Test
    @Transactional
    void getAllPhotosByTakenIsNullOrNotNull() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where taken is not null
        defaultPhotoShouldBeFound("taken.specified=true");

        // Get all the photoList where taken is null
        defaultPhotoShouldNotBeFound("taken.specified=false");
    }

    @Test
    @Transactional
    void getAllPhotosByUploadedIsEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where uploaded equals to DEFAULT_UPLOADED
        defaultPhotoShouldBeFound("uploaded.equals=" + DEFAULT_UPLOADED);

        // Get all the photoList where uploaded equals to UPDATED_UPLOADED
        defaultPhotoShouldNotBeFound("uploaded.equals=" + UPDATED_UPLOADED);
    }

    @Test
    @Transactional
    void getAllPhotosByUploadedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where uploaded not equals to DEFAULT_UPLOADED
        defaultPhotoShouldNotBeFound("uploaded.notEquals=" + DEFAULT_UPLOADED);

        // Get all the photoList where uploaded not equals to UPDATED_UPLOADED
        defaultPhotoShouldBeFound("uploaded.notEquals=" + UPDATED_UPLOADED);
    }

    @Test
    @Transactional
    void getAllPhotosByUploadedIsInShouldWork() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where uploaded in DEFAULT_UPLOADED or UPDATED_UPLOADED
        defaultPhotoShouldBeFound("uploaded.in=" + DEFAULT_UPLOADED + "," + UPDATED_UPLOADED);

        // Get all the photoList where uploaded equals to UPDATED_UPLOADED
        defaultPhotoShouldNotBeFound("uploaded.in=" + UPDATED_UPLOADED);
    }

    @Test
    @Transactional
    void getAllPhotosByUploadedIsNullOrNotNull() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        // Get all the photoList where uploaded is not null
        defaultPhotoShouldBeFound("uploaded.specified=true");

        // Get all the photoList where uploaded is null
        defaultPhotoShouldNotBeFound("uploaded.specified=false");
    }

    @Test
    @Transactional
    void getAllPhotosByAlbumIsEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);
        Album album = AlbumResourceIT.createEntity(em);
        em.persist(album);
        em.flush();
        photo.setAlbum(album);
        photoRepository.saveAndFlush(photo);
        Long albumId = album.getId();

        // Get all the photoList where album equals to albumId
        defaultPhotoShouldBeFound("albumId.equals=" + albumId);

        // Get all the photoList where album equals to (albumId + 1)
        defaultPhotoShouldNotBeFound("albumId.equals=" + (albumId + 1));
    }

    @Test
    @Transactional
    void getAllPhotosByTagIsEqualToSomething() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);
        Tag tag = TagResourceIT.createEntity(em);
        em.persist(tag);
        em.flush();
        photo.addTag(tag);
        photoRepository.saveAndFlush(photo);
        Long tagId = tag.getId();

        // Get all the photoList where tag equals to tagId
        defaultPhotoShouldBeFound("tagId.equals=" + tagId);

        // Get all the photoList where tag equals to (tagId + 1)
        defaultPhotoShouldNotBeFound("tagId.equals=" + (tagId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPhotoShouldBeFound(String filter) throws Exception {
        restPhotoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(photo.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].taken").value(hasItem(DEFAULT_TAKEN.toString())))
            .andExpect(jsonPath("$.[*].uploaded").value(hasItem(DEFAULT_UPLOADED.toString())));

        // Check, that the count call also returns 1
        restPhotoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPhotoShouldNotBeFound(String filter) throws Exception {
        restPhotoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPhotoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPhoto() throws Exception {
        // Get the photo
        restPhotoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPhoto() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        int databaseSizeBeforeUpdate = photoRepository.findAll().size();

        // Update the photo
        Photo partialUpdatedPhoto = photoRepository.findById(photo.getId()).get();
        // Disconnect from session so that the updates on partialUpdatedPhoto are not directly saved in db
        em.detach(partialUpdatedPhoto);
        partialUpdatedPhoto.setTitle(UPDATED_TITLE);
        partialUpdatedPhoto.setDescription(UPDATED_DESCRIPTION);
        partialUpdatedPhoto.setImage(UPDATED_IMAGE);
        partialUpdatedPhoto.setImageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        partialUpdatedPhoto.setHeight(UPDATED_HEIGHT);
        partialUpdatedPhoto.setWidth(UPDATED_WIDTH);
        partialUpdatedPhoto.setTaken(UPDATED_TAKEN);
        partialUpdatedPhoto.setUploaded(UPDATED_UPLOADED);
        PhotoDto photoDto = photoMapper.toDto(partialUpdatedPhoto);

        restPhotoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, photoDto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(photoDto))
            )
            .andExpect(status().isOk());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);
        Photo testPhoto = photoList.get(photoList.size() - 1);
        assertThat(testPhoto.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPhoto.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPhoto.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testPhoto.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testPhoto.getHeight()).isEqualTo(UPDATED_HEIGHT);
        assertThat(testPhoto.getWidth()).isEqualTo(UPDATED_WIDTH);
        assertThat(testPhoto.getTaken()).isEqualTo(UPDATED_TAKEN);
        assertThat(testPhoto.getUploaded()).isEqualTo(UPDATED_UPLOADED);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository).save(testPhoto);
    }

    @Test
    @Transactional
    void putNonExistingPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();
        photo.setId(count.incrementAndGet());

        // Create the Photo
        PhotoDto photoDto = photoMapper.toDto(photo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPhotoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, photoDto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(photoDto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void putWithIdMismatchPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();
        photo.setId(count.incrementAndGet());

        // Create the Photo
        PhotoDto photoDto = photoMapper.toDto(photo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhotoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(photoDto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();
        photo.setId(count.incrementAndGet());

        // Create the Photo
        PhotoDto photoDto = photoMapper.toDto(photo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhotoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(photoDto)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void partialUpdatePhotoWithPatch() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        int databaseSizeBeforeUpdate = photoRepository.findAll().size();

        // Update the photo using partial update
        Photo partialUpdatedPhoto = new Photo();
        partialUpdatedPhoto.setId(photo.getId());

        partialUpdatedPhoto.setTitle(UPDATED_TITLE);
        partialUpdatedPhoto.setImage(UPDATED_IMAGE);
        partialUpdatedPhoto.setImageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        partialUpdatedPhoto.setHeight(UPDATED_HEIGHT);
        partialUpdatedPhoto.setTaken(UPDATED_TAKEN);

        restPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPhoto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPhoto))
            )
            .andExpect(status().isOk());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);
        Photo testPhoto = photoList.get(photoList.size() - 1);
        assertThat(testPhoto.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPhoto.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPhoto.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testPhoto.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testPhoto.getHeight()).isEqualTo(UPDATED_HEIGHT);
        assertThat(testPhoto.getWidth()).isEqualTo(DEFAULT_WIDTH);
        assertThat(testPhoto.getTaken()).isEqualTo(UPDATED_TAKEN);
        assertThat(testPhoto.getUploaded()).isEqualTo(DEFAULT_UPLOADED);
    }

    @Test
    @Transactional
    void fullUpdatePhotoWithPatch() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        int databaseSizeBeforeUpdate = photoRepository.findAll().size();

        // Update the photo using partial update
        Photo partialUpdatedPhoto = new Photo();
        partialUpdatedPhoto.setId(photo.getId());

        partialUpdatedPhoto.setTitle(UPDATED_TITLE);
        partialUpdatedPhoto.setDescription(UPDATED_DESCRIPTION);
        partialUpdatedPhoto.setImage(UPDATED_IMAGE);
        partialUpdatedPhoto.setImageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        partialUpdatedPhoto.setHeight(UPDATED_HEIGHT);
        partialUpdatedPhoto.setWidth(UPDATED_WIDTH);
        partialUpdatedPhoto.setTaken(UPDATED_TAKEN);
        partialUpdatedPhoto.setUploaded(UPDATED_UPLOADED);

        restPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPhoto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPhoto))
            )
            .andExpect(status().isOk());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);
        Photo testPhoto = photoList.get(photoList.size() - 1);
        assertThat(testPhoto.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPhoto.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPhoto.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testPhoto.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testPhoto.getHeight()).isEqualTo(UPDATED_HEIGHT);
        assertThat(testPhoto.getWidth()).isEqualTo(UPDATED_WIDTH);
        assertThat(testPhoto.getTaken()).isEqualTo(UPDATED_TAKEN);
        assertThat(testPhoto.getUploaded()).isEqualTo(UPDATED_UPLOADED);
    }

    @Test
    @Transactional
    void patchNonExistingPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();
        photo.setId(count.incrementAndGet());

        // Create the Photo
        PhotoDto photoDto = photoMapper.toDto(photo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, photoDto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(photoDto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();
        photo.setId(count.incrementAndGet());

        // Create the Photo
        PhotoDto photoDto = photoMapper.toDto(photo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhotoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(photoDto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPhoto() throws Exception {
        int databaseSizeBeforeUpdate = photoRepository.findAll().size();
        photo.setId(count.incrementAndGet());

        // Create the Photo
        PhotoDto photoDto = photoMapper.toDto(photo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhotoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(photoDto)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Photo in the database
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(0)).save(photo);
    }

    @Test
    @Transactional
    void deletePhoto() throws Exception {
        // Initialize the database
        photoRepository.saveAndFlush(photo);

        int databaseSizeBeforeDelete = photoRepository.findAll().size();

        // Delete the photo
        restPhotoMockMvc
            .perform(delete(ENTITY_API_URL_ID, photo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Photo> photoList = photoRepository.findAll();
        assertThat(photoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Photo in Elasticsearch
        verify(mockPhotoSearchRepository, times(1)).deleteById(photo.getId());
    }

    @Test
    @Transactional
    void searchPhoto() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        photoRepository.saveAndFlush(photo);
        when(mockPhotoSearchRepository.search(queryStringQuery("id:" + photo.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(photo), PageRequest.of(0, 1), 1));

        // Search the photo
        restPhotoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + photo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(photo.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].taken").value(hasItem(DEFAULT_TAKEN.toString())))
            .andExpect(jsonPath("$.[*].uploaded").value(hasItem(DEFAULT_UPLOADED.toString())));
    }
}
