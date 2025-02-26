package jp.co.sss.cytech.controller;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.sss.cytech.DTO.CartDTO;
import jp.co.sss.cytech.entity.CartEntity;
import jp.co.sss.cytech.entity.CustomUserDetails;
import jp.co.sss.cytech.entity.ProductEntity;
import jp.co.sss.cytech.entity.UserEntity;
import jp.co.sss.cytech.service.CartService;
import jp.co.sss.cytech.service.ProductService;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    // カート追加処理
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Integer productId,
            				@RequestParam Integer quantity,
            				HttpSession session,
            				Model model) {

    // 商品存在チェック
    ProductEntity product = productService.getProductEntityById(productId);

    // 現在のユーザー取得
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    UserEntity user = userDetails.getUser();

    // カートへの追加処理
    CartEntity cart = cartService.addToCart(user.getUserId(), productId, quantity);
        
    // デバッグ用ログ
    System.out.println("Cart saved - ID: " + cart.getCartId() +
    				    "User:" + user.getUserId() +
    				    "Product:" + productId);

    // 表示用データ設定
    model.addAttribute("product", product);
    model.addAttribute("quantity", quantity);
    model.addAttribute("totalAmount", cartService.getCartTotal(user.getUserId()));

        return "cart-add";
    }
    
    @GetMapping("/cart")
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
    	// 現在のユーザー取得
    	// CustomUserDetailsにキャスト
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        UserEntity user = customUserDetails.getUser();
        Integer userId = user.getUserId(); 
    	
        List<CartEntity> cartItems = cartService.getCurrentUserCart(userId);
        int totalAmount = cartItems.stream()
            .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        
        return "cart";
    }
    
    @PostMapping("/cart")
    public String addToCart(@RequestParam Integer productId,
                            @RequestParam Integer quantity) {
    	
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUser().getUserId();

        cartService.addToCart(userId, productId, quantity);
    	
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Integer productId) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUser().getUserId();
    	
        cartService.removeFromCart(userId,productId);
        return "redirect:/cart";
    }
    
    @PostMapping("/cart/purchase")
    public String purchaseFromCart(HttpSession session, RedirectAttributes redirectAttributes) {
        // 現在のユーザー取得
        UserEntity user = getCurrentUser();
        
        // カート内アイテム取得
        List<CartEntity> cartItems = cartService.getCurrentUserCart(user.getUserId());
        
        // CartDTOリストに変換
        List<CartDTO> purchaseItems = cartItems.stream()
            .map(cart -> {
                CartDTO dto = new CartDTO();
                dto.setProductId(cart.getProduct().getProductId());
                dto.setProductName(cart.getProduct().getProductName());
                dto.setPrice(cart.getProduct().getPrice());
                dto.setQuantity(cart.getQuantity());
                return dto;
            })
            .collect(Collectors.toList());
        
        // セッションに保存
        session.setAttribute("purchaseItems", purchaseItems);
        
        return "redirect:/purchase";
    }
    
    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }
}

