package com.pharmacy.management.service;

import com.pharmacy.management.model.CompanyPolicy;
import com.pharmacy.management.repository.CompanyPolicyRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CompanyPolicyService {
    private final CompanyPolicyRepository companyPolicyRepository;

    public CompanyPolicy save(CompanyPolicy companyPolicy) {
        companyPolicy.setIsActive(true);
        Integer dayOfMonth = companyPolicy.getPolicyStartFrom().getDayOfMonth();
        if (!dayOfMonth.equals(1)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please select start of a month.");
        }
        return companyPolicyRepository.save(companyPolicy);
    }

    @Transactional(readOnly = true)
    public List<CompanyPolicy> findAll() {
        return companyPolicyRepository.findAllByIsActive(true);
    }

    @Transactional(readOnly = true)
    public Optional<CompanyPolicy> findOne(Long id) {
        return companyPolicyRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<CompanyPolicy> findAllCompanyPolicyByCompanyId(Long companyId) {
        return companyPolicyRepository.findAllByCompany_IdAndIsActive(companyId, true);
    }

    public void delete(Long id) {
        Optional<CompanyPolicy> companyPolicy = companyPolicyRepository.findById(id);
        companyPolicy.ifPresentOrElse(companyPolicy1 -> {
            companyPolicy1.setIsActive(false);
            companyPolicyRepository.save(companyPolicy1);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no category!");
        });
    }
}
