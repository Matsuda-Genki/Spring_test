package jp.co.sss.cytech.DTO;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewDTO {

    private String username;
    private Integer productId;

    @NotNull(message = "コメントを入力してください")
    private String comment;

    private String reviewImgPath;

    @Min(1)
    @Max(5)
    @NotNull(message = "評価を選択してください")
    private Integer rating;

    private LocalDateTime updatedAt;

    @NotBlank(message = "投稿者名を入力してください")
    private String dummyUserName;

    @NotBlank(message = "メールアドレスを入力してください")
    private String email;
    
    // Builderのカスタマイズ（Lombokとの競合対策）
    public static ReviewDTOBuilder builder() {
        return new ReviewDTOBuilder();
    }

    public ReviewDTO build() {
        ReviewDTO dto = new ReviewDTO();
        dto.reviewImgPath = this.reviewImgPath;
        dto.comment = this.comment;
        dto.rating = this.rating;
        
        return dto;
    }
    
    // カスタムゲッター（更新日時）
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt != null ? 
               this.updatedAt : 
               LocalDateTime.of(2000, 1, 1, 0, 0);
    }
}
