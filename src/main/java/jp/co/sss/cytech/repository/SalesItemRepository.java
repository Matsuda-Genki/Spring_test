package jp.co.sss.cytech.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.cytech.entity.SalesItemEntity;

@Repository
public interface SalesItemRepository extends JpaRepository<SalesItemEntity, Integer> {
    
	@Query("SELECT s FROM SalesItemEntity s " + "WHERE :currentDate BETWEEN s.startMonth AND s.endMonth")
    List<SalesItemEntity> findActiveSalesItems(@Param("currentDate") LocalDate currentDate);

	// 特定商品の現在有効なセールを検索
    @Query("SELECT s FROM SalesItemEntity s " +
           "WHERE s.product.productId = :productId " + 
           "AND s.startMonth <= :currentDate " + 
           "AND s.endMonth >= :currentDate")
    List<SalesItemEntity> findActiveSalesByProductId(
        @Param("productId") Integer productId,
        @Param("currentDate") LocalDate currentDate
    );
}
