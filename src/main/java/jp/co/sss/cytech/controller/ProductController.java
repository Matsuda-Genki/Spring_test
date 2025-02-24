package jp.co.sss.cytech.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import jp.co.sss.cytech.DTO.ProductDTO;
import jp.co.sss.cytech.DTO.ReviewDTO;
import jp.co.sss.cytech.service.ProductService;
import jp.co.sss.cytech.service.ReviewService;

@Controller
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    @Autowired
    public ProductController(ProductService productService,
                            ReviewService reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
    }
    
    @GetMapping("/products")
    public String showProductList(
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            Model model) {

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
        model.addAttribute("productName", productName);
        model.addAttribute("categoryId", categoryId);

        return "index"; 
    }
    
 // 商品詳細ページのマッピングを追加
    @GetMapping("/details/{id}")
    public String productDetails(
    		@PathVariable("id") Integer productId, 
    		Model model,
    		@ModelAttribute("successMessage") String successMessage) {
        Optional<ProductDTO> product = productService.getProductById(productId);
        if (product.isPresent()) {
        	List<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId);
        	
            // デバッグ用ログ出力
            System.out.println("取得した商品: " + product);
            System.out.println("関連レビュー件数: " + reviews.size());
        	
            model.addAttribute("product", product.get());
            model.addAttribute("reviews", reviews);
            return "detail";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "商品が見つかりません");
    }
}
