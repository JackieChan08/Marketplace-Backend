package com.example.marketplace_backend.Service.Impl.auth;


import com.example.marketplace_backend.Model.User;
import com.example.marketplace_backend.Repositories.UserRepository;
import com.example.marketplace_backend.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalOAuth2ServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User processOAuth2Token(String token, String provider) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> userInfo;

        switch (provider.toLowerCase()) {
            case "google":
                userInfo = restTemplate.getForObject("https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + token, Map.class);
                break;
            case "yandex":
                userInfo = restTemplate.getForObject("https://login.yandex.ru/info?format=json&oauth_token=" + token, Map.class);
                break;
            default:
                throw new RuntimeException("Unsupported OAuth2 provider: " + provider);
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

        if (email == null) throw new RuntimeException("Email not found from provider");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .name(name != null ? name : email)
                    .role(Role.USER)
                    .password(passwordEncoder.encode("oauth2_placeholder"))
                    .provider(provider)
                    .build();
            return userRepository.save(newUser);
        });

        if (name != null && !Objects.equals(user.getName(), name)) {
            user.setName(name);
            userRepository.save(user);
        }

        return user;
    }
}

