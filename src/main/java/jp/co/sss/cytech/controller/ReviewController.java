package jp.co.sss.cytech.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.NoSuchElementException;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.sss.cytech.DTO.FileStorageDTO;
import jp.co.sss.cytech.DTO.ProductDTO;
import jp.co.sss.cytech.DTO.ReviewDTO;
import jp.co.sss.cytech.entity.UserEntity;
import jp.co.sss.cytech.service.FileStorageService;
import jp.co.sss.cytech.service.ProductService;
import jp.co.sss.cytech.service.ReviewService;
import jp.co.sss.cytech.service.UserService;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ProductService productService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    

    public ReviewController(
    		ProductService productService,
    		ReviewService reviewService,
    		UserService userService,
    		FileStorageService fileStorageService) {

        this.productService = productService;
        this.reviewService = reviewService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/{id}")
    public String showReviewForm(@PathVariable("id") Integer productId,
            					 Model model) {
        
        ProductDTO product = productService.getProductById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productId));
        
        model.addAttribute("product", product);
        model.addAttribute("productId", productId); // 追加
        model.addAttribute("review", new ReviewDTO());
        model.addAttribute("request", new FileStorageDTO()); // 追加       
        return "review";
    }

    @PostMapping("/{id}")
    public String submitReview(@PathVariable ("id") Integer productId,
            				   @ModelAttribute("request") @Valid FileStorageDTO fileStorageDTO,
            				   BindingResult fileBindingResult,
            				   @ModelAttribute("review") @Valid ReviewDTO reviewDTO,
            				   BindingResult reviewBindingResult,
            				   Principal principal,
            				   Model model,
            				   RedirectAttributes redirectAttributes) {
    	    	
    	// ファイルがあるかチェック
        MultipartFile file = fileStorageDTO.getFile();
        System.out.println("ファイルオブジェクト: " + (file != null ? "存在" : "null"));
        if (file  == null || file.isEmpty()) {
        	System.out.println("▼▼▼ ファイル未選択エラー ▼▼▼");
            fileBindingResult.rejectValue("file", "file.empty", "ファイルを選択してください");
        }
        
    	// メールアドレス照合
        try {
        	UserEntity currentUser = userService.getCurrentUser();
            System.out.println("認証ユーザー: " + currentUser.getEmail());
            System.out.println("入力メール: " + reviewDTO.getEmail());
        	if (!currentUser.getEmail().equalsIgnoreCase(reviewDTO.getEmail())) {
        		reviewBindingResult.rejectValue("email", "email.mismatch", "登録済みメールアドレスと一致しません");
        	}
        } catch (Exception e) {
            System.out.println("■■■ ユーザー取得エラー ■■■");
            reviewBindingResult.reject("user.error", "ユーザー認証に失敗しました");
        }
        // エラーチェック（個別に処理）
        if (fileBindingResult.hasErrors() || reviewBindingResult.hasErrors()) {
            System.out.println("▼▼▼ バリデーションエラー ▼▼▼");
            System.out.println("ファイルエラー数: " + fileBindingResult.getErrorCount());
            System.out.println("レビューエラー数: " + reviewBindingResult.getErrorCount());
            
            ProductDTO product = productService.getProductById(productId)
                    .orElseThrow(() -> new NoSuchElementException("商品が見つかりません"));
            System.out.println("■■■ 商品情報取得失敗 ■■■");
            
            model.addAttribute("product", product);
            model.addAttribute("request", fileStorageDTO); // 変更前: new FileStorageDTO()
            model.addAttribute("review", reviewDTO);       // 変更前: new ReviewDTO()
            
            return "review";
        }
        
        try {
        	System.out.println("■ ファイル保存開始 ■");
            String filePath = fileStorageService.storeFile(file);
            System.out.println("保存先パス: " + filePath);
            reviewDTO.setReviewImgPath(filePath);
            
            System.out.println("■ DB保存開始 ■");
            reviewService.createReview(reviewDTO, productId);
            System.out.println("▲▲▲ 保存成功 ▲▲▲");
            
            redirectAttributes.addFlashAttribute("successMessage", "投稿が成功しました");
            return "redirect:/details/" + productId;
            
        } catch (IOException e) {
        	System.out.println("▼▼▼ ファイル保存例外 ▼▼▼");
            fileBindingResult.rejectValue("file", "file.io.error", "ファイルの保存中にエラーが発生しました");
            return handleError(productId, model, fileStorageDTO, reviewDTO);
            
        } catch (Exception e) {
        	System.out.println("■■■ システム例外 ■■■");
            reviewBindingResult.reject("system.error", "予期せぬエラーが発生しました");
            return handleError(productId, model, fileStorageDTO, reviewDTO);
        }
    }
    
    private String handleError(
    		Integer productId, 
    		Model model,
    		FileStorageDTO fileStorageDTO, 
    		ReviewDTO reviewDTO) {
    	try {
    		ProductDTO product = productService.getProductById(productId)
    				.orElseThrow(() -> new NoSuchElementException("商品情報取得失敗"));
    		
    		model.addAttribute("product", product);
        
    	} catch (NoSuchElementException e) {
            model.addAttribute("errorMessage", "商品情報が見つかりません");
        }

        // 入力値を保持
        model.addAttribute("request", fileStorageDTO);
        model.addAttribute("review", reviewDTO);
        
        return "review";
    }
}
