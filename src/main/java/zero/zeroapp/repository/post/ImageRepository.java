package zero.zeroapp.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import zero.zeroapp.entity.post.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}