package com.example.marketplace_backend.controller;

//import com.example.marketplace_crm.Config.Jwt.JwtProvider;
//import com.example.marketplace_crm.Model.User;
//import com.example.marketplace_crm.Service.Impl.UserServiceImpl;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//@Tag(name = "Authentication API", description = "API для аутентификации и регистрации пользователей")
//public class AuthController {
//    private final AuthenticationManager authenticationManager;
//    private final JwtProvider jwtProvider;
//    private final UserServiceImpl userService;
//
//    @Operation(summary = "Аутентификация пользователя", description = "Принимает логин и пароль, возвращает access и refresh токены")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Успешная аутентификация",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = Map.class))),
//            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации")
//    })
//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@RequestBody User loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword())
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        User user = userService.findByLogin(loginRequest.getLogin());
//        String token = jwtProvider.generateToken(user);
//        String refreshToken = jwtProvider.generateRefreshToken(user);
//
//        Map<String, String> tokens = new HashMap<>();
//        tokens.put("accessToken", token);
//        tokens.put("refreshToken", refreshToken);
//        return ResponseEntity.ok(tokens);
//    }
//
//    @Operation(summary = "Регистрация пользователя", description = "Создаёт нового пользователя, если логин не занят")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
//            @ApiResponse(responseCode = "400", description = "Логин уже занят или ошибка регистрации")
//    })
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody User registrationRequest) {
//        if (userService.findByLogin(registrationRequest.getLogin()) != null) {
//            return ResponseEntity.badRequest().body("Пользователь с таким логином уже существует");
//        }
//
//        User newUser = userService.save(registrationRequest);
//        if (newUser == null) {
//            return ResponseEntity.badRequest().body("Ошибка регистрации пользователя");
//        }
//
//        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
//    }
//
//    @Operation(summary = "Обновление токена", description = "Принимает refresh-токен и возвращает новый access-токен")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Новый access-токен успешно сгенерирован",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = Map.class))),
//            @ApiResponse(responseCode = "403", description = "Недействительный refresh-токен")
//    })
//    @PostMapping("/refresh")
//    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
//        String refreshToken = request.get("refreshToken");
//        if (jwtProvider.validateToken(refreshToken)) {
//            String username = jwtProvider.getUsernameFromToken(refreshToken);
//            User user = userService.findByLogin(username);
//            String newToken = jwtProvider.generateToken(user);
//            Map<String, String> response = new HashMap<>();
//            response.put("accessToken", newToken);
//            return ResponseEntity.ok(response);
//        }
//        return ResponseEntity.status(403).body("Invalid refresh token");
//    }
//}
