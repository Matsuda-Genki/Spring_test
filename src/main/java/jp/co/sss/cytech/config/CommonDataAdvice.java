package jp.co.sss.cytech.config;

import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.co.sss.cytech.DTO.ProductDTO;
import jp.co.sss.cytech.entity.CategoryEntity;
import jp.co.sss.cytech.entity.SalesItemEntity;
import jp.co.sss.cytech.service.CategoryService;
import jp.co.sss.cytech.service.ProductService;
import jp.co.sss.cytech.service.SalesItemService;

@ControllerAdvice
public class CommonDataAdvice {
    private final CategoryService categoryService;
    private final SalesItemService salesItemService;
    private final ProductService productService;

    public CommonDataAdvice(
        CategoryService categoryService,
        SalesItemService salesItemService,
        ProductService productService
    ) {
        this.categoryService = categoryService;
        this.salesItemService = salesItemService;
        this.productService = productService;
    }

    @ModelAttribute("categories")
    public List<CategoryEntity> populateCategories() {
        return categoryService.getAllCategories();
    }

    @ModelAttribute("activeSales")
    public List<SalesItemEntity> populateActiveSales() {
        return salesItemService.getCurrentSalesItems();
    }

    @ModelAttribute("products")
    public List<ProductDTO> populateProducts() {
        return productService.getAllProducts();
    }
}
