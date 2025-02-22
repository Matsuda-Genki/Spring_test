package jp.co.sss.cytech.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jp.co.sss.cytech.entity.SalesItemEntity;

@Repository
public interface SalesItemRepository extends JpaRepository<SalesItemEntity, Integer> {
    @Query("SELECT s FROM SalesItemEntity s WHERE s.discountRate > 0 AND s.startMonth <= :currentDate AND s.endMonth >= :currentDate")
    List<SalesItemEntity> findActiveSalesItems(LocalDate currentDate);
}
