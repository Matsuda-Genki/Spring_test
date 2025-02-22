package jp.co.sss.cytech.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.co.sss.cytech.entity.CategoryEntity;
import jp.co.sss.cytech.entity.SalesItemEntity;
import jp.co.sss.cytech.service.CategoryService;
import jp.co.sss.cytech.service.SalesItemService;

@Controller
public class TopController {
    private final SalesItemService salesItemService;
    
    public TopController(SalesItemService salesItemService) {
        this.salesItemService = salesItemService;
    }
    
    @Autowired
    private CategoryService categoryService;
     // すべてのビューにカテゴリ情報を渡す
     @ModelAttribute("categories")
     public List<CategoryEntity> populateCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/")
    public String showTopPage(Model model) {
        List<SalesItemEntity> salesItems = salesItemService.getCurrentSalesItems();
        model.addAttribute("salesItems", salesItems);
        return "top"; // Thymeleafテンプレート `top.html` に渡す
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

