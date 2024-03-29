package com.pharmacy.management.service;


import javax.servlet.http.HttpServletRequest;

import com.pharmacy.management.config.ExcelHelper;
import com.pharmacy.management.dto.request.PasswordChangeDTO;
import com.pharmacy.management.dto.request.ProductRequestExcelDTO;
import com.pharmacy.management.dto.request.UserDataExcelDTO;
import com.pharmacy.management.dto.request.UserUpdateDataDTO;
import com.pharmacy.management.exception.CustomException;
import com.pharmacy.management.model.Company;
import com.pharmacy.management.model.CompanyPolicy;
import com.pharmacy.management.model.Roles;
import com.pharmacy.management.model.Users;
import com.pharmacy.management.repository.CompanyPolicyRepository;
import com.pharmacy.management.repository.CompanyRepository;
import com.pharmacy.management.repository.UserRepository;
import com.pharmacy.management.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CompanyRepository companyRepository;

    private final CompanyPolicyRepository companyPolicyRepository;
    private final ModelMapper modelMapper;

    public String signin(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            Users appUser = userRepository.findByEmail(email);
            return jwtTokenProvider.createToken(email, appUser);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email/password supplied");
        }
    }

    public String signup(Users appUser) {
        if (!userRepository.existsByEmail(appUser.getEmail())) {
            appUser.setIsActive(true);
            appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
            userRepository.save(appUser);
            return jwtTokenProvider.createToken(appUser.getEmail(), appUser);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use");
        }
    }


    public Boolean passwordChange(PasswordChangeDTO appUser) {
        Users users = userRepository.findById(appUser.getUserId()).get();
        users.setPassword(passwordEncoder.encode(appUser.getPassword()));
        userRepository.save(users);
        return true;
    }

    public Boolean updateUser(UserUpdateDataDTO userUpdateDataDTO) {
        Users users = userRepository.findById(userUpdateDataDTO.getId()).get();
        modelMapper.map(userUpdateDataDTO, users);
        userRepository.save(users);
        return true;
    }

    public void delete(String email) {
        userRepository.deleteByEmail(email);
    }

    public Users search(String email) {
        Users appUser = userRepository.findByEmail(email);
        if (appUser == null) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return appUser;
    }


    public Users getUserById(Long id) {
        Optional<Users> appUser = userRepository.findById(id);
        if (appUser.isEmpty()) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "The user doesn't exist");
        }
        return appUser.get();
    }

    public List<Users> allUser() {
        List<Users> appUser = userRepository.findAll();
        if (appUser == null) {
            return new ArrayList<>();
        }
        return appUser;
    }

    public Page<Users> allUserByEmail(String email, Pageable pageable) {
        Page<Users> appUser = userRepository.findAllByIsActiveAndEmailContainingIgnoreCase(true, email, pageable);
        return appUser;
    }


    public Page<Users> getUsersByCompanyId(Long companyId, Pageable pageable) {
        Page<Users> appUser = userRepository.findAllByCompany_Id(companyId, pageable);
        return appUser;
    }

    public Users whoami(HttpServletRequest req) {
        return userRepository.findByEmail(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
    }


    public Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return userRepository.findByEmail(currentUserName);
        }
        return null;
    }

    public Boolean getUserListFromExcel(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String ext = FilenameUtils.getExtension(fileName);
            List<UserDataExcelDTO> userDataExcelDTOList = ExcelHelper.excelToUser(file.getInputStream(), ext);
            List<Users> users = new ArrayList<>();
            for (UserDataExcelDTO userDataExcelDTO: userDataExcelDTOList) {

                Boolean existsByEmail = userRepository.existsByEmail(userDataExcelDTO.getEmail());
                if (existsByEmail){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already exist user with this email : " + userDataExcelDTO.getEmail());
                }
                Optional<CompanyPolicy> companyPolicy = companyPolicyRepository.findByNameAndIsActive(userDataExcelDTO.getCompanyPolicyName(), true);
                if (!companyPolicy.isPresent()){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company not found with : " + userDataExcelDTO.getCompanyName());
                }
                Users user = modelMapper.map(userDataExcelDTO, Users.class);
                user.setCompanyPolicy(companyPolicy.get());
                user.setCompany(companyPolicy.get().getCompany());

                Roles roles = new Roles();
                roles.setName("EMPLOYEE");
                Set<Roles> rolesSet = new HashSet<>();
                rolesSet.add(roles);

                user.setRoles(rolesSet);
                user.setIsActive(true);

                users.add(user);
            }
            userRepository.saveAll(users);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }


    public String refresh(String email) {
        return jwtTokenProvider.createToken(email, userRepository.findByEmail(email));
    }

}
