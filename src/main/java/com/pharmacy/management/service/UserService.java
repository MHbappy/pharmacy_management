package com.pharmacy.management.service;


import javax.servlet.http.HttpServletRequest;

import com.pharmacy.management.exception.CustomException;
import com.pharmacy.management.model.Users;
import com.pharmacy.management.repository.UserRepository;
import com.pharmacy.management.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;

  public String signin(String email, String password) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
      Users appUser = userRepository.findByEmail(email);
      return jwtTokenProvider.createToken(email, appUser);
    } catch (AuthenticationException e) {
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

  public List<Users> allUser() {
    List<Users> appUser = userRepository.findAll();
    if (appUser == null) {
      throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
    }
    return appUser;
  }

  public Users whoami(HttpServletRequest req) {
    return userRepository.findByEmail(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
  }

  public String refresh(String email) {
    return jwtTokenProvider.createToken(email, userRepository.findByEmail(email));
  }

}
