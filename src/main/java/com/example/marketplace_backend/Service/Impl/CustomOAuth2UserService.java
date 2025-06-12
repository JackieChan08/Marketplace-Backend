package com.example.marketplace_backend.Service.Impl;

import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.enums.Role;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);


        String registrationId = userRequest.getClientRegistration().getRegistrationId().toLowerCase();
        String email;
        String name;

        switch (registrationId) {
            case "google":
                email = oAuth2User.getAttribute("email");
                name = oAuth2User.getAttribute("name");
                break;
            case "yandex":
                email = oAuth2User.getAttribute("default_email");
                name = oAuth2User.getAttribute("real_name");
                if (name == null) {
                    name = oAuth2User.getAttribute("display_name");
                }
                break;
            default:
                throw new OAuth2AuthenticationException("Unknown OAuth2 provider: " + registrationId);
        }

        if (email == null) {
            throw new OAuth2AuthenticationException("OAuth2 provider did not return an email address");
        }

        log.info("OAuth2 login: provider={}, email={}, name={}", registrationId, email, name);

        final String finalEmail = email;
        final String finalName = name;

        User user = userRepository.findByEmail(finalEmail).orElseGet(() -> {
            log.info("Registering new user: {}", finalEmail);
            User newUser = User.builder()
                    .email(finalEmail)
                    .name(finalName != null ? finalName : finalEmail)
                    .role(Role.USER)
                    .password(passwordEncoder.encode("oauth2_placeholder"))
                    .provider(registrationId)
                    .build();
            return userRepository.save(newUser);
        });

        if (finalName != null && !Objects.equals(user.getName(), finalName)) {
            user.setName(finalName);
            userRepository.save(user);
        }

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("email", finalEmail);

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes,
                "email"
        );
    }
}
