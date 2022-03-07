package zero.zeroapp.entity.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import zero.zeroapp.exception.UnsupportedImageFormatException;

import javax.persistence.*;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // 이미지 구분을 위해 고유한 이름 부여
    private String uniqueName;

    @Column(nullable = false) // 원래 이미지 이름
    private String originName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // 게시글 제거 시 이미지도 연쇄적으로 제거 -> @OnDelete
    private Post post;

    private final static String supportedExtension[] = {"jpg", "jpeg", "gif", "bmp", "png"}; // 지원하는 이미지 확장자

    public Image(String originName) {
        this.uniqueName = generateUniqueName(extractExtension(originName)); // 생성자에서 각 이미지의 고유한 이름을 생성
        this.originName = originName;
    }

    public void initPost(Post post) { // Post 클래스 Image를 추가할 때 호출하는 메소드
        if(this.post == null) {
            this.post = post;
        }
    }

    private String generateUniqueName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    private String extractExtension(String originName) { // 이미지에서 확장자 추출
        try {
            String ext = originName.substring(originName.lastIndexOf(".") + 1);
            if(isSupportedFormat(ext)) return ext;
        } catch (StringIndexOutOfBoundsException e) { }
        throw new UnsupportedImageFormatException(); //지원하지 않는 확장자인 경우 예외 발생
    }

    private boolean isSupportedFormat(String ext) { // 지원하는 확장자인지 확인
        return Arrays.stream(supportedExtension).anyMatch(e -> e.equalsIgnoreCase(ext));
    }

}
