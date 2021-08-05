package io.susimsek.gallery.service.criteria;

import java.io.Serializable;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link io.susimsek.gallery.domain.Photo} entity. This class is used
 * in {@link io.susimsek.gallery.web.rest.PhotoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /photos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
public class PhotoCriteria implements Serializable, Criteria {

    static final long serialVersionUID = 1L;

    LongFilter id;

    StringFilter title;

    IntegerFilter height;

    IntegerFilter width;

    InstantFilter taken;

    InstantFilter uploaded;

    LongFilter albumId;

    LongFilter tagId;

    public PhotoCriteria(PhotoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.height = other.height == null ? null : other.height.copy();
        this.width = other.width == null ? null : other.width.copy();
        this.taken = other.taken == null ? null : other.taken.copy();
        this.uploaded = other.uploaded == null ? null : other.uploaded.copy();
        this.albumId = other.albumId == null ? null : other.albumId.copy();
        this.tagId = other.tagId == null ? null : other.tagId.copy();
    }

    @Override
    public PhotoCriteria copy() {
        return new PhotoCriteria(this);
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public IntegerFilter height() {
        if (height == null) {
            height = new IntegerFilter();
        }
        return height;
    }

    public IntegerFilter width() {
        if (width == null) {
            width = new IntegerFilter();
        }
        return width;
    }

    public InstantFilter taken() {
        if (taken == null) {
            taken = new InstantFilter();
        }
        return taken;
    }

    public InstantFilter uploaded() {
        if (uploaded == null) {
            uploaded = new InstantFilter();
        }
        return uploaded;
    }

    public LongFilter albumId() {
        if (albumId == null) {
            albumId = new LongFilter();
        }
        return albumId;
    }

    public LongFilter tagId() {
        if (tagId == null) {
            tagId = new LongFilter();
        }
        return tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PhotoCriteria that = (PhotoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(height, that.height) &&
            Objects.equals(width, that.width) &&
            Objects.equals(taken, that.taken) &&
            Objects.equals(uploaded, that.uploaded) &&
            Objects.equals(albumId, that.albumId) &&
            Objects.equals(tagId, that.tagId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, height, width, taken, uploaded, albumId, tagId);
    }
}
