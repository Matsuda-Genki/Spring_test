package jp.co.sss.cytech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.sss.cytech.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

    // 商品IDでレビュー検索（ユーザー情報付き）
    @Query("SELECT r FROM ReviewEntity r JOIN FETCH r.user WHERE r.product.productId = :productId")
    List<ReviewEntity> findByProductIdWithUser(@Param("productId") Integer productId);

    // ユーザーIDでレビュー検索（追加）
    @Query("SELECT r FROM ReviewEntity r WHERE r.user.userId = :userId")
    List<ReviewEntity> findByUserId(@Param("userId") Integer userId);
}
