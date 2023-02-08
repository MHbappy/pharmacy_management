package com.pharmacy.management.security;

import com.pharmacy.management.model.Roles;
import com.pharmacy.management.model.Users;
import com.pharmacy.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyUserDetails implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final Users appUser = userRepository.findByEmail(username);

    if (appUser == null) {
      throw new UsernameNotFoundException("User '" + username + "' not found");
    }

    List<GrantedAuthority> authorities = new ArrayList<>(appUser.getRoles().size());

    for (Roles appRole: appUser.getRoles()) {
      authorities.add(new SimpleGrantedAuthority(appRole.getName()));
    }

    return org.springframework.security.core.userdetails.User
        .withUsername(username)
        .password(appUser.getPassword())
        .authorities(authorities)
        .roles(appUser.getRoles().stream().map(appRole -> appRole.getName()).collect(Collectors.toList()).toArray(String[]:: new))
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }

}
