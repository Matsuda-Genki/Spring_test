package jp.co.sss.cytech.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.sss.cytech.DTO.ProductDTO;
import jp.co.sss.cytech.entity.ProductEntity;
import jp.co.sss.cytech.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    // エンティティをDTOに変換するメソッド
    private ProductDTO convertToDTO(ProductEntity productEntity) {
        return new ProductDTO(
            productEntity.getProductId(),
            productEntity.getProductName(),
            productEntity.getProductStock(),
            productEntity.getPrice(),
            productEntity.getIncludeTax(),
            productEntity.getProductImgPath(),
            productEntity.getCategory().getCategoryId()
        );
    }

    // カテゴリーIDと商品名を条件に商品を検索
    public List<ProductDTO> findProductsByCategoryIdAndName(Integer categoryId, String productName) {
        List<ProductEntity> productEntities = productRepository.findByCategory_CategoryIdAndProductNameContaining(categoryId, productName);
        return productEntities.stream()
                .map(this::convertToDTO) // ProductEntityをProductDTOに変換
                .collect(Collectors.toList());
    }
    
    // カテゴリーIDに基づいて商品を取得するメソッド
    public List<ProductDTO> findProductsByCategoryId(Integer categoryId) {
        List<ProductEntity> productEntities = productRepository.findByCategory_CategoryId(categoryId);
        return productEntities.stream()
                .map(this::convertToDTO) // ProductEntityをProductDTOに変換
                .collect(Collectors.toList());
    }

    // 商品名に基づいて商品を取得するメソッド
    public List<ProductDTO> findProductsByName(String productName) {
        List<ProductEntity> productEntities = productRepository.findByProductNameContaining(productName);
        return productEntities.stream()
                .map(this::convertToDTO) // ProductEntityをProductDTOに変換
                .collect(Collectors.toList());
    }

    // すべての商品を取得するメソッド
    public List<ProductDTO> getAllProducts() {
        List<ProductEntity> productEntities = productRepository.findAll();
        return productEntities.stream()
                .map(this::convertToDTO) // ProductEntityをProductDTOに変換
                .collect(Collectors.toList());
    }
}
