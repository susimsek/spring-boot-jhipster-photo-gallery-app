package io.susimsek.gallery.service.criteria;

import java.io.Serializable;
import java.util.Objects;

import lombok.*;
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
 * Criteria class for the {@link io.susimsek.gallery.domain.Album} entity. This class is used
 * in {@link io.susimsek.gallery.web.rest.AlbumResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /albums?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
public class AlbumCriteria implements Serializable, Criteria {

    static final long serialVersionUID = 1L;

    LongFilter id;

    StringFilter title;

    InstantFilter created;

    LongFilter userId;

    public AlbumCriteria(AlbumCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.created = other.created == null ? null : other.created.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public AlbumCriteria copy() {
        return new AlbumCriteria(this);
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



    public InstantFilter created() {
        if (created == null) {
            created = new InstantFilter();
        }
        return created;
    }



    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AlbumCriteria that = (AlbumCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(created, that.created) &&
            Objects.equals(userId, that.userId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, created, userId);
    }
}
