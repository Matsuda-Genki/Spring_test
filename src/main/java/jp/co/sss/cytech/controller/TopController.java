package jp.co.sss.cytech.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
public class TopController {

    // ルートパス ("/") のハンドラ追加
    @GetMapping("/")
    public String index() {
        System.out.println("🔥 [Debug] / へのリクエスト受信");
        return "top"; // index.htmlを表示
    }
     
    @ModelAttribute("currentUser")
    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}
