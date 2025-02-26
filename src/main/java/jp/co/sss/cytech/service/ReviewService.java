package jp.co.sss.cytech.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sss.cytech.DTO.ReviewDTO;
import jp.co.sss.cytech.entity.ProductEntity;
import jp.co.sss.cytech.entity.ReviewEntity;
import jp.co.sss.cytech.entity.UserEntity;
import jp.co.sss.cytech.repository.ProductRepository;
import jp.co.sss.cytech.repository.ReviewRepository;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserService userService; // 追加
    private final FileStorageService fileStorageService;

    public ReviewService(
    		ReviewRepository reviewRepository,
            ProductRepository productRepository,
            UserService userService,
            FileStorageService fileStorageService) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    public List<ReviewDTO> getReviewsByProductId(Integer productId) {
        List<ReviewEntity> reviews = reviewRepository.findByProductIdWithUser(productId);
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ReviewDTO convertToDTO(ReviewEntity review) {
        System.out.println("=== 変換処理開始 ===");
        System.out.println("入力エンティティ: " + review);

        return ReviewDTO.builder()
                .username(review.getUser().getUsername())
                .comment(review.getComment())
                .rating(review.getRating())
                .updatedAt(review.getUpdatedAt())
                .reviewImgPath(review.getReviewImgPath())
                .dummyUserName(review.getDummyUserName())
                .build();
    }

    @Transactional
    public ReviewEntity createReview(ReviewDTO reviewDTO, Integer productId) {
        
        UserEntity user = userService.getCurrentUser();
        ProductEntity product = productRepository.findById(productId)
        	.orElseThrow(() -> new NoSuchElementException("product not found"));
        
        // DTOからEntityへの変換（画像パス含む）
        ReviewEntity review = convertToEntity(reviewDTO);
        
        // リレーションシップ設定
        review.setUser(user);
        review.setProduct(product);
        
        // 必須フィールドの設定（createdAt/updatedAt）
        LocalDateTime now = LocalDateTime.now();
        review.setCreatedAt(now);
        review.setUpdatedAt(now);
        
        // データベース保存
        return reviewRepository.save(review);
    }   
    
    private ReviewEntity convertToEntity(ReviewDTO dto) {
        return ReviewEntity.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .dummyUserName(dto.getDummyUserName())
                .reviewImgPath(dto.getReviewImgPath())
                .build();
    }
        
    // 特定ユーザーのレビューを取得
    public List<ReviewDTO> getReviewsByUserId(Integer userId) {
        List<ReviewEntity> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
