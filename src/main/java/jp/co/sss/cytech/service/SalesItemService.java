package jp.co.sss.cytech.service;

import java.time.LocalDate;
import java.util.Collections;
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
    	System.out.println("✅ [Step1] getCurrentSalesItems() 開始");
        LocalDate today = LocalDate.now();
        System.out.println("✅ [Step2] 今日の日付: " + today);
        
        // 明示的な初期化（Null防止）
        List<SalesItemEntity> salesItems = Collections.emptyList();
        
        try {
        	salesItems = salesItemRepository.findActiveSalesItems(today);
        } catch (Exception e) {
        	System.out.println("🚨 エラー: " + e.getMessage());
        }
        	
        	System.out.println("✅ [Step3] クエリ結果サイズ: " + salesItems.size());
        	
        return salesItems;

    }
    
    // 特定商品の現在有効なセールを取得
    public List<SalesItemEntity> getCurrentSalesForProduct(Integer productId) {
        LocalDate today = LocalDate.now();
        return salesItemRepository.findActiveSalesByProductId(productId, today);
    }
}
