package jp.co.sss.cytech.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UserRegistrationDTO {

	@NotBlank(message = "ユーザー名を入力してください")
	private String username;

    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "正しいメールアドレス形式で入力してください")
    private String email;
    
    @NotBlank(message = "パスワードを入力してください")
    @Size(min = 8, message = "パスワードは8文字以上で入力してください")
    private String password;

    @NotBlank(message = "確認用パスワードは必須です")
    private String confirmPassword;
}
