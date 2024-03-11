package com.app.api.controllers;

import com.app.api.requests.ChangePassRequest;
import com.app.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    UserService userService;

    @PatchMapping
    public ResponseEntity<?> changePassword(@RequestBody ChangePassRequest request,
                                            Principal connectedUser) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/msg")
    public String getMsg() {
        return "hello world";
    }
}
