package jp.co.sss.cytech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.cytech.entity.OrderItemEntity;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Integer> {
    
    // 注文IDに関連する注文明細を取得
    List<OrderItemEntity> findByOrderOrderId(Integer orderId);
}
