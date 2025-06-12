package com.example.marketplace_backend.Service.Impl;


import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Repositories.RefreshTokenRepository;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.enums.Role;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public CustomOAuth2UserService(UserRepository userRepository, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = User.builder()
                    .email(email)
                    .name(name)
                    .role(Role.USER)
                    .password("oauth2_placeholder")
                    .provider(userRequest.getClientRegistration().getRegistrationId())
                    .build();
            userRepository.save(user);
        }

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                oAuth2User.getAttributes(),
                "email"
        );
    }
}
