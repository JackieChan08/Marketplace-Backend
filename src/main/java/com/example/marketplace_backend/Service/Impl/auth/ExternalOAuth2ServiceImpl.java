package com.example.marketplace_backend.Service.Impl.auth;

import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalOAuth2ServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User processOAuth2Token(String token, String provider) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if (provider == null || provider.trim().isEmpty()) {
            throw new IllegalArgumentException("Provider cannot be null or empty");
        }

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> userInfo;

        try {
            switch (provider.toLowerCase()) {
                case "google":
                    userInfo = restTemplate.getForObject(
                            "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + token,
                            Map.class
                    );
                    break;
                case "yandex":
                    userInfo = restTemplate.getForObject(
                            "https://login.yandex.ru/info?format=json&oauth_token=" + token,
                            Map.class
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported OAuth2 provider: " + provider);
            }
        } catch (HttpClientErrorException e) {
            log.error("Failed to fetch user info from {}: {}", provider, e.getMessage());
            throw new RuntimeException("Failed to authenticate with " + provider + ": Invalid token", e);
        } catch (Exception e) {
            log.error("Unexpected error during OAuth2 authentication with {}: {}", provider, e.getMessage());
            throw new RuntimeException("OAuth2 service unavailable for " + provider, e);
        }

        if (userInfo == null) {
            throw new RuntimeException("No user info returned from " + provider);
        }

        String email = switch (provider.toLowerCase()) {
            case "google" -> (String) userInfo.get("email");
            case "yandex" -> (String) userInfo.get("default_email");
            default -> null;
        };

        String name = switch (provider.toLowerCase()) {
            case "google" -> (String) userInfo.get("name");
            case "yandex" -> {
                String n = (String) userInfo.get("real_name");
                yield n != null ? n : (String) userInfo.get("display_name");
            }
            default -> null;
        };

        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email not found from provider: " + provider);
        }

        log.info("Processing OAuth2 user: provider={}, email={}", provider, email);

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            log.info("Creating new OAuth2 user: email={}, provider={}", email, provider);
            User newUser = User.builder()
                    .email(email)
                    .name(name != null && !name.trim().isEmpty() ? name : email)
                    .role(Role.USER)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .provider(provider)
                    .build();
            return userRepository.save(newUser);
        });

        if (name != null && !name.trim().isEmpty() && !Objects.equals(user.getName(), name)) {
            user.setName(name);
            userRepository.save(user);
        }

        return user;
    }
}