package jp.co.sss.cytech.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.sss.cytech.DTO.CartDTO;
import jp.co.sss.cytech.DTO.OrderDTO;
import jp.co.sss.cytech.DTO.ProductDTO;
import jp.co.sss.cytech.entity.CustomUserDetails;
import jp.co.sss.cytech.entity.OrderEntity;
import jp.co.sss.cytech.entity.OrderItemEntity;
import jp.co.sss.cytech.entity.ProductEntity;
import jp.co.sss.cytech.entity.UserEntity;
import jp.co.sss.cytech.repository.ProductRepository;
import jp.co.sss.cytech.service.CartService;
import jp.co.sss.cytech.service.OrderService;
import jp.co.sss.cytech.service.ProductService;

@Controller
@SessionAttributes({"purchaseItems", "orderDetails"})
public class PurchaseController {
	
	@Autowired
    private OrderService orderService;
	
	@Autowired
	private ProductService productService;
	
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CartService cartService;
	
	// ユーザー取得
    private UserEntity getCurrentUser() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("ユーザーが認証されていません");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser();
        }
        
        System.out.println("Principal type: " + principal.getClass().getName());
        throw new SecurityException("無効なユーザータイプ"  + principal.getClass().getName());
    }
	
	// 単品購入処理
    @PostMapping("/single-purchase/{productId}")
    public String singlePurchase(@PathVariable Integer productId,
            					 @RequestParam Integer quantity,
            					 HttpSession session,RedirectAttributes redirectAttributes) {
    	
    		ProductEntity product = productRepository.findById(productId)
    		        .orElseThrow(() -> new RuntimeException("商品が見つかりません"));
    		    
    		CartDTO cartItem = new CartDTO();
    		cartItem.setProductId(productId);
    		cartItem.setProductName(product.getProductName());
    		cartItem.setPrice(product.getPrice());
    		cartItem.setQuantity(quantity);
    		    
    		// 新しいアイテムリストを作成
    		List<CartDTO> items = new ArrayList<>();
    		items.add(cartItem);
    		    
    		// セッションに保存
    		session.setAttribute("purchaseItems", items);
    		    
    		return "redirect:/purchase";
    }

    // 購入詳細画面表示
    @GetMapping("/purchase")
    public String showPurchaseForm(Model model) {
    	
        model.addAttribute("paymentMethods");
        model.addAttribute("pickupLocations");
        
        return "purchase";
    }

    // 購入確認処理
    @PostMapping("/purchase/confirm")
    public String confirmPurchase(@RequestParam(name = "recipientName", required = false) String recipientName,
            					  @RequestParam(name = "address", required = false) String address,
            					  @RequestParam(name = "apartment", required = false) String apartment,
            					  @RequestParam(name = "paymentMethod") String paymentMethod,
            					  @RequestParam(name = "pickupLocation") String pickupLocation,
            					  HttpSession session,
            					  Model model) {
    	
        List<CartDTO> items = (List<CartDTO>) session.getAttribute("purchaseItems");

        // カートアイテムがない場合の処理
        if (items == null || items.isEmpty()) {
            model.addAttribute("error", "購入する商品が選択されていません");
            return showPurchaseForm(model);
        }
        
        // 現在のユーザー取得
        UserEntity user = getCurrentUser();
        
        // データ保存せずに注文詳細を生成
        OrderDTO details = new OrderDTO();
        
        // 基本情報設定
        details.setRecipientName(recipientName);
        details.setAddress(address);
        details.setApartment(apartment);
        details.setPaymentMethod(paymentMethod);
        details.setPickupLocation(pickupLocation);
        
        // 合計金額計算
        int totalAmount = calculateTotalAmount(items); 
        details.setTotalAmount(totalAmount);
        
        // セッションに一時保存（後で使用）
        model.addAttribute("order", details);
        session.setAttribute("orderDetails", details);
        
        return "purchase-check";
    }
    
    private int calculateTotalAmount(List<CartDTO> items) {
        return items.stream()
                .mapToInt(item -> productService.findById(item.getProductId()).getIncludeTax() * item.getQuantity())
                .sum();
    }

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam Integer productId,
                            @RequestParam Integer quantity,
                            @ModelAttribute OrderEntity order) {
    	// 商品情報取得（ProductServiceからDTOを取得）
        ProductDTO productDTO = productService.findById(productId);

        // 商品エンティティをデータベースから取得
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません"));
        
        // DTOからEntityへの変換
        OrderItemEntity item = new OrderItemEntity();
        item.setProduct(productEntity);
        item.setQuantity(quantity);

        // 割引価格判定
        if (productDTO.getDiscountRate() != null && productDTO.getDiscountRate() > 0) {
            item.setTaxedPrice(productDTO.getDiscountedIncludeTax());
        } else {
            item.setTaxedPrice(productDTO.getIncludeTax());
        }

        order.getItems().add(item);
        order.calculateTotalAmount();

        return "redirect:/cart";
    }
    
    // 購入確定処理
    @PostMapping("/purchase/complete")
    public String completePurchase(HttpSession session) {
    	
    	// セッションから必要なデータを取得
        List<CartDTO> items = (List<CartDTO>) session.getAttribute("purchaseItems");
        OrderDTO orderDetails = (OrderDTO) session.getAttribute("orderDetails");
        
        if (items == null || orderDetails == null) {
            throw new RuntimeException("注文情報が見つかりません");
        }
        
        UserEntity user = getCurrentUser();
        
        // 実際の保存処理
        OrderEntity savedOrder = orderService.createOrder(
        		items,
        		user,
        		orderDetails.getRecipientName(),
                orderDetails.getAddress(),
                orderDetails.getApartment(),
                orderDetails.getPaymentMethod(),
                orderDetails.getPickupLocation()
        );

        // カートとセッションの削除処理
        cartService.clearCart(user.getUserId());        
        session.removeAttribute("purchaseItems");
        session.removeAttribute("orderDetails");
        
        return "redirect:/complete/" + savedOrder.getOrderId();
    }

    // 購入完了画面
    @GetMapping("/complete/{orderId}")
    public String showComplete(
            @PathVariable Integer orderId,
            Model model) {
        
        OrderDTO details = orderService.getOrderDTO(orderId);
        
        model.addAttribute("order", details);
        return "complete";
    }
}
