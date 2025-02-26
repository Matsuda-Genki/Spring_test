package jp.co.sss.cytech.controller;

import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.co.sss.cytech.DTO.UserProfileDTO;
import jp.co.sss.cytech.service.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

	private final UserService userService;

	@GetMapping("")
	public String myPage(Model model) {
		String username = getCurrentUsername();
		model.addAttribute("profileForm", userService.getProfile(username));
		return "mypage";
	}

	@PostMapping("/update")
	public String updateProfile(@Valid @ModelAttribute("profileForm") UserProfileDTO dto,
								BindingResult result,
								RedirectAttributes redirectAttributes) {
	
		if (result.hasErrors()) {
			return "mypage";
		}
	
		userService.updateProfile(getCurrentUsername(), dto);
		redirectAttributes.addFlashAttribute("successMessage", "プロフィールを更新しました");
		
		return "redirect:/mypage";
	}

	private String getCurrentUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
