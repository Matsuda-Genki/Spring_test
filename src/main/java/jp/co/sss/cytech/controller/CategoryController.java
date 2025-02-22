package jp.co.sss.cytech.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.co.sss.cytech.entity.CategoryEntity;
import jp.co.sss.cytech.service.CategoryService;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // @ModelAttributeを使って、全てのビューにcategoriesを渡す
    @ModelAttribute("categories")
    public List<CategoryEntity> populateCategories() {
        return categoryService.getAllCategories(); // サービスで取得したカテゴリリストを返す
    }
    
    @GetMapping("/otherPage")
    public String showOtherPage() {
        return "otherPage"; // 他のページにもcategoriesが渡される
    }
}
