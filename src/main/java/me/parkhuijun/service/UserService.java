package me.parkhuijun.service;

import jakarta.transaction.Transactional;
import me.parkhuijun.auth.TokenProvider;
import me.parkhuijun.dto.TokenDTO;
import me.parkhuijun.dto.user.UserLoginRequests;
import me.parkhuijun.dto.user.UserSignUpRequest;
import me.parkhuijun.dto.user.UserSignUpResponse;
import me.parkhuijun.entity.User;
import me.parkhuijun.entity.UserRole;
import me.parkhuijun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public UserSignUpResponse createUser(UserSignUpRequest request) {
        String password = passwordEncoder.encode(request.getPassword());

        UserRole role = UserRole.builder()
                .id(1)
                .name("ROLE_USER")
                .build();

        Set<UserRole> auth = new HashSet<>();
        auth.add(role);

        User user = User.builder()
                .userId(request.getUserId())
                .pwd(password)
                .name(request.getName())
                .authorities(auth)
                .build();

        User createdUser = userRepository.save(user);

        UserSignUpResponse response = UserSignUpResponse.builder()
                .userId(createdUser.getUserId())
                .name(createdUser.getName())
                .regDt(createdUser.getRegDt())
                .build();

        return response;
    }

    @Transactional
    public TokenDTO login(UserLoginRequests request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String accessToken = tokenProvider.createToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .type("Bearer")
                .build();
    }
}
