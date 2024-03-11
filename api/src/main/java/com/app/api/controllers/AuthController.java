package com.app.api.controllers;

import com.app.api.requests.AuthenticationRequest;
import com.app.api.requests.RegisterRequest;
import com.app.api.responses.AuthenticationResponse;
import com.app.api.responses.UserResponse;
import com.app.api.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;


@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @PostMapping("/register")

    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        var user = userService.register(request);
        return ResponseEntity.created(URI.create("")).body(
                UserResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .fullName(user.getFullName())
                        .username(user.getUsername())
                        .message("User created")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
        );

    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(userService.authenticate(request));
    }
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        userService.refreshToken(request, response);
    }

    @GetMapping
    public String confirmUserAccount(@RequestParam("token") String token) {
        Boolean isSuccess = userService.verifyConfirmationKey(token);
        return "Account verified";
    }


}
