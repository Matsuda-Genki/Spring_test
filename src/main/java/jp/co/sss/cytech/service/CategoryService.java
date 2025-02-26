package jp.co.sss.cytech.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.sss.cytech.entity.CategoryEntity;
import jp.co.sss.cytech.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }
}
