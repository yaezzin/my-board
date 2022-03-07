package zero.zeroapp.factory.entity;

import zero.zeroapp.entity.category.Category;
import zero.zeroapp.entity.member.Member;
import zero.zeroapp.entity.post.Image;
import zero.zeroapp.entity.post.Post;

import java.util.List;

import static zero.zeroapp.factory.entity.CategoryFactory.createCategory;
import static zero.zeroapp.factory.entity.MemberFactory.createMember;


public class PostFactory {
    public static Post createPost() {
        return createPost(createMember(), createCategory());
    }

    public static Post createPost(Member member, Category category) {
        return new Post("title", "content", member, category, List.of());
    }

    public static Post createPostWithImages(Member member, Category category, List<Image> images) {
        return new Post("title", "content", member, category, images);
    }

    public static Post createPostWithImages(List<Image> images) {
        return new Post("title", "content", createMember(), createCategory(), images);
    }
}

