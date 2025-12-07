//package com.example.marketplace_backend.exception;
//
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.boot.web.servlet.error.ErrorAttributes;
//import org.springframework.boot.web.servlet.error.ErrorController;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/error")
//public class CustomErrorController implements ErrorController {
//
//    private final ErrorAttributes errorAttributes;
//
//    public CustomErrorController(ErrorAttributes errorAttributes) {
//        this.errorAttributes = errorAttributes;
//    }
//
//    @RequestMapping
//    public ResponseEntity<?> handleError(HttpServletRequest request) {
//        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
//
//        int status = statusCode != null ? Integer.parseInt(statusCode.toString()) : 500;
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("timestamp", LocalDateTime.now());
//        body.put("status", status);
//        body.put("error", getReasonPhrase(status));
//        body.put("message", errorMessage != null ? errorMessage : "Unexpected error");
//
//        return ResponseEntity.status(status).body(body);
//    }
//
//    private String getReasonPhrase(int status) {
//        return switch (status) {
//            case 400 -> "Bad Request";
//            case 401 -> "Unauthorized";
//            case 403 -> "Forbidden";
//            case 404 -> "Not Found";
//            case 500 -> "Internal Server Error";
//            default -> "Error";
//        };
//    }
//}
