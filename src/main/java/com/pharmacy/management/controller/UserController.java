package com.pharmacy.management.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.pharmacy.management.dto.request.PasswordChangeDTO;
import com.pharmacy.management.dto.request.UserUpdateDataDTO;
import com.pharmacy.management.dto.response.UserDataDTO;
import com.pharmacy.management.dto.response.UserResponseDTO;
import com.pharmacy.management.model.Users;
import com.pharmacy.management.repository.UserRepository;
import com.pharmacy.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @PostMapping("/signin")
    public String login(
            @RequestParam String username,
            @RequestParam String password) {
        return userService.signin(username, password);
    }

    @PostMapping("/signup")
    public String signup(@Valid @RequestBody UserDataDTO user) {
        return userService.signup(modelMapper.map(user, Users.class));
    }


    @PostMapping("/update-password")
    public Boolean updatePassword(@Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        if (passwordChangeDTO.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!userRepository.existsById(passwordChangeDTO.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }
        return userService.passwordChange(passwordChangeDTO);
    }

    @PutMapping("/update-user/{userId}")
    public Boolean updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateDataDTO user) {
        if (user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!Objects.equals(userId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }
        return userService.updateUser(user);
    }

    @DeleteMapping(value = "/{username}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String delete(@PathVariable String username) {
        userService.delete(username);
        return username;
    }

    @GetMapping(value = "/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Users search(@PathVariable Long id) {
        return userService.getUserById(id);
    }


//    @GetMapping(value = "/all-user")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public Page<Users> allUser(@RequestParam(name = "email", defaultValue = "") String email, Pageable pageable) {
//        return userService.allUserByEmail(email, pageable);
//    }

    @GetMapping(value = "/all-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<Users> allUser(@RequestParam(name = "email", defaultValue = "") String email, Pageable pageable) {
        return userService.allUserByEmail(email, pageable);
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public UserResponseDTO whoami(HttpServletRequest req) {
        return modelMapper.map(userService.whoami(req), UserResponseDTO.class);
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public String refresh(HttpServletRequest req) {
        return userService.refresh(req.getRemoteUser());
    }

}
