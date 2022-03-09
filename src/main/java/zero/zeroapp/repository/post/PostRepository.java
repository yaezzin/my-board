package zero.zeroapp.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zero.zeroapp.entity.post.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p join fetch p.member where p.id = :id")
    Optional<Post> findByIdWithMember(@Param("id") Long id);
}
