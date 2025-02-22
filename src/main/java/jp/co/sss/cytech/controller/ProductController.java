package jp.co.sss.cytech.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.cytech.DTO.ProductDTO;
import jp.co.sss.cytech.entity.CategoryEntity;
import jp.co.sss.cytech.service.CategoryService;
import jp.co.sss.cytech.service.ProductService;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public String showProductList(
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            Model model) {

    	List<CategoryEntity> categories = categoryService.getAllCategories();
        List<ProductDTO> products = productService.findProductsByCategoryIdAndName(categoryId, productName);
        if (categoryId != null && productName != null && !productName.isEmpty()) {
            // 両方の条件が指定されている場合
            products = productService.findProductsByCategoryIdAndName(categoryId, productName);
        } else if (categoryId != null) {
            // カテゴリーIDのみ指定されている場合
            products = productService.findProductsByCategoryId(categoryId);
        } else if (productName != null && !productName.isEmpty()) {
            // 商品名のみ指定されている場合
            products = productService.findProductsByName(productName);
        } else {
            // すべての商品を取得
            products = productService.getAllProducts();
        }

        // モデルにカテゴリー情報と商品情報を渡す
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("productName", productName);
        model.addAttribute("categoryId", categoryId);

        return "index"; 
    }
}
