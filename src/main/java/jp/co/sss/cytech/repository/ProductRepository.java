package jp.co.sss.cytech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.cytech.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    // カテゴリーIDと商品名（部分一致）で商品を取得
	List<ProductEntity> findByCategory_CategoryIdAndProductNameContaining(Integer categoryId, String productName);

    // カテゴリーIDで商品を取得
    List<ProductEntity> findByCategory_CategoryId(Integer categoryId);

    // 商品名（部分一致）で商品を取得
    List<ProductEntity> findByProductNameContaining(String productName);
    
    //会社名を取得
    @Query("SELECT p FROM ProductEntity p " +
            "JOIN FETCH p.category " +
            "JOIN FETCH p.company")
     List<ProductEntity> findAllWithCompanyAndCategory();

     @Query("SELECT p FROM ProductEntity p " +
            "JOIN FETCH p.category " +
            "JOIN FETCH p.company " +
            "WHERE p.category.categoryId = :categoryId")
     List<ProductEntity> findByCategoryWithCompany(@Param("categoryId") Integer categoryId);
}
