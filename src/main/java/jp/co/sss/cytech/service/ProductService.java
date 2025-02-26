package jp.co.sss.cytech.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.sss.cytech.DTO.ProductDTO;
import jp.co.sss.cytech.entity.ProductEntity;
import jp.co.sss.cytech.entity.SalesItemEntity;
import jp.co.sss.cytech.repository.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final SalesItemService salesItemService;
	
    @Autowired
    public ProductService(
    		ProductRepository productRepository,
    		SalesItemService salesItemService) {
        this.productRepository = productRepository;
        this.salesItemService = salesItemService;
    }
    
    // ProductEntity を直接取得するメソッド追加
    public ProductEntity getProductEntityById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません"));
    }
    
    public ProductDTO findById(Integer productId) {
        return productRepository.findById(productId)
                .map(entity -> {
                    ProductDTO dto = convertToDTO(entity);
                    List<SalesItemEntity> activeSales = 
                        salesItemService.getCurrentSalesForProduct(entity.getProductId());
                    
                    if (!activeSales.isEmpty()) {
                        SalesItemEntity sale = activeSales.get(0);
                        applyDiscount(dto, sale.getDiscountRate());
                    }
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("商品が見つかりません ID: " + productId));
    }
    
    public Optional<ProductDTO> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(entity -> {
                	ProductDTO dto = convertToDTO(entity);
    
                // セール情報取得
                List<SalesItemEntity> activeSales = 
                	salesItemService.getCurrentSalesForProduct(entity.getProductId());
                    
                // 有効なセールがある場合のみ割引適用
                if (!activeSales.isEmpty()) {
                	SalesItemEntity sale = activeSales.get(0);
                    applyDiscount(dto, sale.getDiscountRate());
                    }
                    
                    return dto;
                });
    }
    
    // 割引計算メソッド
    private void applyDiscount(ProductDTO dto, Integer discountRate) {
        dto.setDiscountRate(discountRate);
        dto.setDiscountedPrice(calculateDiscountedPrice(dto.getPrice(), discountRate));
        dto.setDiscountedIncludeTax(calculateDiscountedPrice(dto.getIncludeTax(), discountRate));
    }

    // 割引価格計算ヘルパー
    private Integer calculateDiscountedPrice(Integer originalPrice, Integer discountRate) {
        return (originalPrice * (100 - discountRate)) / 100;
    }
                	
    // エンティティをDTOに変換するメソッド
    private ProductDTO convertToDTO(ProductEntity productEntity) {
        return new ProductDTO(
            productEntity.getProductId(),
            productEntity.getProductName(),
            productEntity.getProductStock(),
            productEntity.getPrice(),
            productEntity.getIncludeTax(),
            productEntity.getProductImgPath(),
            productEntity.getCategory().getCategoryId(),
            productEntity.getCompany().getCompanyId(),
            productEntity.getCompany().getCompanyName()
        );
    }

    // カテゴリーIDと商品名を条件に商品を検索
    public List<ProductDTO> findProductsByCategoryIdAndName(Integer categoryId, String productName) {
        List<ProductEntity> productEntities = productRepository.findByCategory_CategoryIdAndProductNameContaining(categoryId, productName);
        return productEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // カテゴリーIDに基づいて商品を取得するメソッド
    public List<ProductDTO> findProductsByCategoryId(Integer categoryId) {
        List<ProductEntity> productEntities = productRepository.findByCategory_CategoryId(categoryId);
        return productEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 商品名に基づいて商品を取得するメソッド
    public List<ProductDTO> findProductsByName(String productName) {
        List<ProductEntity> productEntities = productRepository.findByProductNameContaining(productName);
        return productEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // すべての商品を取得するメソッド
    public List<ProductDTO> getAllProducts() {
        List<ProductEntity> productEntities = productRepository.findAll();
        return productEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
