package jp.co.sss.cytech.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.co.sss.cytech.entity.CompanyEntity;
import jp.co.sss.cytech.service.CompanyService;

@Controller
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    // 全ビューに企業情報を渡す
    @ModelAttribute("companies")
    public List<CompanyEntity> populateCompanies() {
        return companyService.getAllCompanies();
    }

    // 他のページ用のマッピング例
    @GetMapping("/companyPage")
    public String showCompanyPage() {
        return "companyPage";
    }
}
