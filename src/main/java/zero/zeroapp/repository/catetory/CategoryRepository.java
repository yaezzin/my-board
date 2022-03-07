package zero.zeroapp.repository.catetory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import zero.zeroapp.entity.category.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c left join c.parent p order by p.id asc nulls first, c.id asc")
    List<Category> findAllOrderByParentIdAscNullsFirstCategoryIdAsc();

}
