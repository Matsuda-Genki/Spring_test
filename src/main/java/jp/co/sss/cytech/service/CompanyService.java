package jp.co.sss.cytech.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jp.co.sss.cytech.entity.CompanyEntity;
import jp.co.sss.cytech.repository.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<CompanyEntity> getAllCompanies() {
        return companyRepository.findAll();
    }
}
