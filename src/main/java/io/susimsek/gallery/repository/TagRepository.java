package io.susimsek.gallery.repository;

import io.susimsek.gallery.domain.Tag;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Tag entity.
 */
@SuppressWarnings("unused")
public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {}
