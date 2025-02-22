package jp.co.sss.cytech.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import jp.co.sss.cytech.entity.SalesItemEntity;
import jp.co.sss.cytech.repository.SalesItemRepository;

@Service
public class SalesItemService {
    private final SalesItemRepository salesItemRepository;

    public SalesItemService(SalesItemRepository salesItemRepository) {
        this.salesItemRepository = salesItemRepository;
    }

    public List<SalesItemEntity> getCurrentSalesItems() {
        LocalDate today = LocalDate.now();
        System.out.println("📅 現在の日付: " + today);  // デバッグ
        
        List<SalesItemEntity> salesItems = salesItemRepository.findActiveSalesItems(today);
        
        // 取得データの確認
        System.out.println("🔍 取得したセール品の数: " + salesItems.size());
        for (SalesItemEntity item : salesItems) {
            System.out.println("🛒 セール品: " + item.getSaleItemId() + ", 割引率: " + item.getDiscountRate() + "%");
        }
        
        return salesItems;
    }
}
