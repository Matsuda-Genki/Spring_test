package jp.co.sss.cytech.repository;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.sss.cytech.entity.CartEntity;
import jp.co.sss.cytech.entity.ProductEntity;
import jp.co.sss.cytech.entity.UserEntity;

public interface CartRepository extends JpaRepository<CartEntity, Integer> {

    // ユーザーのカートアイテム全取得
    List<CartEntity> findByUser(UserEntity user);
    List<CartEntity> findByUser_UserId(Integer userId);
    
    //カート内一括削除
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM carts WHERE user_id = :userId", nativeQuery = true)
    int deleteByUserId(@Param("userId") Integer userId);
    
    // ユーザーIDと商品IDで検索
    Optional<CartEntity> findByUserAndProduct(UserEntity user, ProductEntity product);

    @Query("SELECT c FROM CartEntity c JOIN FETCH c.product WHERE c.user.userId = :userId")
    List<CartEntity> findByUserIdWithProduct(@Param("userId") Integer userId);
    
    // SUM計算用のカスタムクエリを定義
    @Query("SELECT COALESCE(SUM(p.price * c.quantity), 0) " + 
           "FROM CartEntity c " + 
           "JOIN c.product p " + 
           "WHERE c.user.userId = :userId")
    Integer sumTotalByUserId(@Param("userId") Integer userId);

    // カートアイテム削除
    @Modifying
    @Query("DELETE FROM CartEntity c WHERE c.user.userId = :userId AND c.product.productId = :productId")
    void deleteByUserAndProduct(@Param("userId") Integer userId, @Param("productId") Integer productId);
}
