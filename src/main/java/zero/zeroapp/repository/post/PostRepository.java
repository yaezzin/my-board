package zero.zeroapp.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import zero.zeroapp.entity.post.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
