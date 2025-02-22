package jp.co.sss.cytech.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.sss.cytech.DTO.UserRegistrationDTO;
import jp.co.sss.cytech.entity.UserEntity;
import jp.co.sss.cytech.repository.UserRepository;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // login.html
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam(required = true) String username, @RequestParam(required = true) String password, HttpSession session, Model model) {
    	try {
	    	Optional<UserEntity> user = userRepository.findByUsername(username);
	    	System.out.println("User retrieved from repository: " + user); // 追加: リポジトリからの結果を表示
	    	if (user.isPresent()) {
	            System.out.println("Found user: " + user.get().getUsername());
	        } else {
	            System.out.println("No user found with username: " + username);
	        }
	        
	        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
	            session.setAttribute("user", user.get()); // セッションにユーザー情報を保存
	            session.setAttribute("SPRING_SECURITY_CONTEXT", user.get());
	        	System.out.println("Received userName: " + username); // ログイン入力されたユーザー名を確認
	            System.out.println("User logged in: " + user.get().getUsername()); // ログイン成功の確認
	            return "redirect:/"; // ログイン後にホームページへリダイレクト
	        } else {
	            model.addAttribute("error", "Invalid username or password");
	            return "login"; // ログイン失敗
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("error", "Error occurred while retrieving user: " + e.getMessage());
	        return "login"; // エラー発生時にもログインページに戻す
	    }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // セッション無効化
        return "redirect:/login"; // ログインページにリダイレクト
    }
    
    /* 新規登録関連 */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
    model.addAttribute("registrationForm", new UserRegistrationDTO());
    return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
    @Valid @ModelAttribute("registrationForm") 
    UserRegistrationDTO dto,BindingResult result,RedirectAttributes redirectAttributes) {

    // パスワード一致チェック
    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
    result.rejectValue("confirmPassword", "password.mismatch");
    }

    // ユーザー名重複チェック
    if (userRepository.existsByUsername(dto.getUsername())) {
    result.rejectValue("username", "username.exists");
    }

    // メールアドレス重複チェック
    if (userRepository.existsByEmail(dto.getEmail())) {
    result.rejectValue("email", "email.exists");
    }

    if (result.hasErrors()) {
    return "register";
    }
    
    UserEntity newUser = convertToEntity(dto);
    userRepository.save(newUser);

    redirectAttributes.addFlashAttribute("successMessage", 
    "登録が完了しました！ログインしてください");
    return "redirect:/login";
    }
    
    private UserEntity convertToEntity(UserRegistrationDTO dto) {
        return UserEntity.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone("未登録")
                .userAddress("未登録")
                .build();
    }
}

