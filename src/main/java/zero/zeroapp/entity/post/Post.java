package zero.zeroapp.entity.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import zero.zeroapp.entity.category.Category;
import zero.zeroapp.entity.common.BaseTimeEntity;
import zero.zeroapp.entity.member.Member;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member; // 1

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category; //


    /**
     * 게시글이 처음 저장될 때, 게시글에 등록했던 이미지도 함께 저장 -> cascade를 PERSIST로 설정
     * orphanRemoval=true로 설정 -> Image가 고아 객체가 되면, 데이터베이스에서 제거
     * 즉, 게시글이 제거되거나 게시글 이미지 수정으로 인해 연관 관계가 끊어졌을 경우, JPA에서 이를 감지하여 데이터베이스에서 제거
     *
     * */

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Image> images;

    public Post(String title, String content, Member member, Category category, List<Image> images) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.category = category;
        this.images = new ArrayList<>();
        addImages(images); // 4
    }

    private void addImages(List<Image> added) {
        added.stream().forEach(i -> {
            images.add(i);
            i.initPost(this);
        });
    }

}
