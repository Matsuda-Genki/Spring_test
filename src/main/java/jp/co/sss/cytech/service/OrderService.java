package jp.co.sss.cytech.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.sss.cytech.DTO.CartDTO;
import jp.co.sss.cytech.DTO.OrderDTO;
import jp.co.sss.cytech.DTO.OrderItemDTO;
import jp.co.sss.cytech.DTO.ProductDTO;
import jp.co.sss.cytech.entity.OrderEntity;
import jp.co.sss.cytech.entity.OrderItemEntity;
import jp.co.sss.cytech.entity.ProductEntity;
import jp.co.sss.cytech.entity.UserEntity;
import jp.co.sss.cytech.repository.OrderItemRepository;
import jp.co.sss.cytech.repository.OrderRepository;
import jp.co.sss.cytech.repository.ProductRepository;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductService productService;
    
    // 注文取得メソッド
    public OrderDTO getOrderDTO(Integer orderId) {
        OrderEntity order = orderRepository.findByIdWithItems(orderId)
            .orElseThrow(() -> new RuntimeException("注文が見つかりません"));
        
        List<OrderItemDTO> itemDTOs = order.getItems().stream().map(item -> {
            ProductEntity product = item.getProduct();
            
            return new OrderItemDTO(
                product.getProductId(),
                product.getProductName(),
                item.getQuantity(),
                item.getTaxedPrice(),
                item.getTaxedPrice(),
                product.getProductImgPath());
        }).collect(Collectors.toList());
        
        // 住所結合処理
        String fullAddress = order.getAddress();
        if (order.getApartment() != null && !order.getApartment().isEmpty()) {
            fullAddress += " " + order.getApartment();
        }
        
        // DTO構築
        return new OrderDTO(
            order.getOrderId(),
            order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
            order.getPaymentMethod(),
            order.getPickupLocation(),
            itemDTOs,
            order.getTotalAmount(),
            fullAddress
        );
    }
    
    @Transactional
    public OrderEntity createOrder(List<CartDTO> items,
    							   UserEntity user,
    							   String recipientName,
    							   String address,
    						       String apartment,
    						       String paymentMethod,
    						       String pickupLocation) {
    	
        OrderEntity order = new OrderEntity();
    	
        // 合計金額計算
        int totalAmount = items.stream()
            .mapToInt(item -> {
                ProductEntity product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
                return product.getIncludeTax() * item.getQuantity();
            })
            .sum();
        
        // 注文エンティティ
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setRecipientName(recipientName);
        order.setAddress(address);
        order.setApartment(apartment);
        order.setPaymentMethod(paymentMethod);
        order.setPickupLocation(pickupLocation);
        
        // 注文明細アイテムの作成
        List<OrderItemEntity> orderItems = new ArrayList<>();
        
        order.setTotalAmount(totalAmount);
        
        OrderEntity savedOrder = orderRepository.save(order);
        
        for (CartDTO item : items) {
            ProductEntity product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("商品が見つかりません"));
            
            // 在庫数チェック
            if (product.getProductStock() < item.getQuantity()) {
                throw new RuntimeException(product.getProductName() + "の在庫が不足しています。" + "現在の在庫数: " + 
                	    	     		   product.getProductStock() + "個");
            }
            
            // 在庫更新
            product.setProductStock(product.getProductStock() - item.getQuantity());
            productRepository.save(product); 
            
            // 注文明細
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTaxedPrice(product.getIncludeTax());
            
            // 双方向関連付け
            order.addItem(orderItem);
            
        }
        
        return savedOrder;
    }
   
    @Transactional
    public OrderDTO createOrderDTO(
            List<CartDTO> cartItems,
            UserEntity user,
            String paymentMethod,
            String pickupLocation,
            String recipientName,
            String address,
            String apartment) {

        	// カートアイテムのnullチェック
        	if (cartItems == null || cartItems.isEmpty()) {
        		throw new IllegalArgumentException("カートに商品がありません");
        	}
        	// 商品情報取得デバッグ
            for (CartDTO item : cartItems) {
                ProductEntity product = productRepository.findById(item.getProductId())
                	.orElseThrow(() -> {
                        return new RuntimeException("商品が見つかりません");
                	}
                );
                System.out.printf("- Product %d: %s x %d%n",
                    product.getProductId(),
                    product.getProductName(),
                    item.getQuantity());
            }
        	
    		// 合計金額計算
    		int totalAmount = cartItems.stream()
    			.mapToInt(cartItem -> {
    				ProductDTO product = productService.findById(cartItem.getProductId());
    				
    				return product.getIncludeTax() * cartItem.getQuantity();
    			})
    			.sum();

    		OrderEntity order = new OrderEntity();
    		order.setTotalAmount(totalAmount);
            order.setUser(user);
            order.setPaymentMethod(paymentMethod);
            order.setPickupLocation(pickupLocation);
            order.setOrderDate(LocalDateTime.now());
            order.setRecipientName(recipientName);
            order.setAddress(address);
            order.setApartment(apartment);

            OrderEntity savedOrder = orderRepository.save(order);

            List<OrderItemEntity> items = cartItems.stream()
                .map(cartItem -> {
                    ProductDTO product = productService.findById(cartItem.getProductId());
                    ProductEntity productEntity = productRepository.findById(product.getProductId())
                            .orElseThrow(() -> new RuntimeException("商品が見つかりません"));
                    
                    OrderItemEntity item = new OrderItemEntity();
                    item.setProduct(productEntity);
                    item.setQuantity(cartItem.getQuantity());
                    item.setTaxedPrice(
                  		 (product.getDiscountedIncludeTax() != null && product.getDiscountedIncludeTax() > 0) ?
                         product.getDiscountedIncludeTax() :
                         product.getIncludeTax()
                    );
                    item.setOrder(savedOrder);
                    return item;
                })
                .collect(Collectors.toList());

            // カスケード保存のために注文に明細を追加
            savedOrder.getItems().addAll(items);
            orderRepository.save(savedOrder);
            
            return convertToOrderDTO(savedOrder);
        }

        private OrderDTO convertToOrderDTO(OrderEntity order) {
            OrderDTO dto = new OrderDTO();
            dto.setOrderId(order.getOrderId());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            dto.setOrderDate(order.getOrderDate().format(formatter));
          
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setPickupLocation(order.getPickupLocation());
            dto.setRecipientName(order.getRecipientName());
            dto.setAddress(order.getAddress());
            dto.setApartment(order.getApartment());
            
            List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());
            
            dto.setItems(itemDTOs);
            dto.setTotalAmount(calculateTotal(order.getItems()));
            
            // 住所の結合処理
            String fullAddress = order.getAddress();
            if (order.getApartment() != null && !order.getApartment().isEmpty()) {
                fullAddress +=" " + order.getApartment();
            }
            dto.setFullAddress(fullAddress);
            System.out.println("Generated Full Address: " + fullAddress);
            System.out.println("From Entity - Address: " + order.getAddress());
            System.out.println("From Entity - Apartment: " + order.getApartment());
            
            return dto;
        }

        private Integer calculateTotal(List<OrderItemEntity> items) {
            return items.stream()
                .mapToInt(item -> item.getTaxedPrice() * item.getQuantity())
                .sum();
        }

        private OrderItemDTO convertToItemDTO(OrderItemEntity item) {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setProductName(item.getProduct().getProductName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getTaxedPrice());
            return dto;
        }
    }