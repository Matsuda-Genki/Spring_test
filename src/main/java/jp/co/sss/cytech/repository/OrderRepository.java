package jp.co.sss.cytech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.sss.cytech.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    
    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.orderId = :id")
    Optional<OrderEntity> findByIdWithItems(@Param("id") Integer id);
}
