package jp.co.sss.cytech.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.cytech.entity.SalesItemEntity;
import jp.co.sss.cytech.service.SalesItemService;

@Controller
@RequestMapping("/sales")
public class SalesItemController {

    private final SalesItemService salesItemService;

    public SalesItemController(SalesItemService salesItemService) {
        this.salesItemService = salesItemService;
    }

    // 特定商品の現在有効なセール情報を渡す
    @ModelAttribute("currentSaleForProduct")
    public List<SalesItemEntity> getCurrentSaleForProduct(
            @RequestParam(required = false) Integer productId) {
        if (productId != null) {
            return salesItemService.getCurrentSalesForProduct(productId);
        }
        return List.of();
    }
}
