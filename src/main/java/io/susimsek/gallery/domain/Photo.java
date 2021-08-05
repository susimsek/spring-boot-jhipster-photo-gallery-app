package io.susimsek.gallery.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Photo.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "photo")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "photo")
public class Photo implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    String title;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "description")
    String description;

    @Lob
    @Column(name = "image", nullable = false)
    byte[] image;

    @Column(name = "image_content_type", nullable = false)
    String imageContentType;

    @Column(name = "height")
    Integer height;

    @Column(name = "width")
    Integer width;

    @Column(name = "taken")
    Instant taken;

    @Column(name = "uploaded")
    Instant uploaded;

    @ToString.Exclude
    @ManyToOne
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    Album album;

    @Builder.Default
    @ToString.Exclude
    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "rel_photo__tag", joinColumns = @JoinColumn(name = "photo_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @JsonIgnoreProperties(value = { "photos" }, allowSetters = true)
    Set<Tag> tags = new HashSet<>();

    public Photo addTag(Tag tag) {
        this.tags.add(tag);
        tag.getPhotos().add(this);
        return this;
    }

    public Photo removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getPhotos().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Photo)) {
            return false;
        }
        return id != null && id.equals(((Photo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
