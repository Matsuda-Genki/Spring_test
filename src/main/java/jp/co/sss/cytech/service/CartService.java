package jp.co.sss.cytech.service;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.sss.cytech.entity.CartEntity;
import jp.co.sss.cytech.entity.ProductEntity;
import jp.co.sss.cytech.entity.UserEntity;
import jp.co.sss.cytech.repository.CartRepository;
import jp.co.sss.cytech.repository.ProductRepository;
import jp.co.sss.cytech.repository.UserRepository;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    public List<CartEntity> getCurrentUserCart(Integer userId) {        
        return cartRepository.findByUserIdWithProduct(userId);
    }

    public CartEntity addToCart(Integer userId, Integer productId, Integer quantity) {
        // ユーザーと商品のエンティティを取得
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        ProductEntity product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    
        // 既存のカートアイテムを検索
        	Optional<CartEntity> existingCart = cartRepository.findByUserAndProduct(user, product);

        	if (existingCart.isPresent()) {
	            // 既存アイテムがある場合は数量を更新
	            CartEntity cart = existingCart.get();
	            cart.setQuantity(cart.getQuantity() + quantity);
	        
	            return cartRepository.save(cart);

	        } else {
	            // 新規アイテムを作成
	            CartEntity newCart = new CartEntity();
	            newCart.setUser(user);
	            newCart.setProduct(product);
	            newCart.setQuantity(quantity);
	            
	            return cartRepository.save(newCart);
	        }
        }

    public Integer getCartTotal(Integer userId) {
        return cartRepository.sumTotalByUserId(userId);
    }
    
    public void removeFromCart(Integer userId, Integer productId) {
        cartRepository.deleteByUserAndProduct(userId, productId);
    }
    
    public void clearCart(Integer userId) {
    	// 削除前にログ出力（デバッグ用）
        List<CartEntity> beforeItems = cartRepository.findByUser_UserId(userId);
        System.out.println("削除前レコード数: " + beforeItems.size());
        
        // ネイティブクエリで直接削除
        int deletedCount = cartRepository.deleteByUserId(userId);
        System.out.println("削除件数: " + deletedCount);
        
        // 明示的にキャッシュクリア
        entityManager.flush();
        entityManager.clear();
        
        // 削除後の確認
        List<CartEntity> afterItems = cartRepository.findByUser_UserId(userId);
        System.out.println("残存レコード数: " + afterItems.size());
    }
}
