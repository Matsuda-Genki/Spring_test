package jp.co.sss.cytech.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDTO {
    private String username;
    private String email;
    
    @NotBlank(message = "電話番号は必須です")
    @Pattern(regexp = "0\\d{1,4}-\\d{1,4}-\\d{4}", message = "電話番号の形式が不正です")
    private String phone;
    
    @NotBlank(message = "住所は必須です")
    private String userAddress;
}
