package com.app.api.services;

import com.app.api.entities.user.Confirmation;
import com.app.api.entities.user.Token;
import com.app.api.entities.user.TokenType;
import com.app.api.entities.user.UserEntity;
import com.app.api.exceptions.UserException;
import com.app.api.repositories.ConfirmationRepository;
import com.app.api.repositories.TokenRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.requests.AuthenticationRequest;
import com.app.api.requests.ChangePassRequest;
import com.app.api.requests.RegisterRequest;
import com.app.api.responses.AuthenticationResponse;
import com.app.api.responses.UserResponse;
import com.app.api.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    JwtService jwtService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    ConfirmationRepository confirmationRepository;
    @Autowired
    EmailService emailService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        if(request.getUsername().trim().isEmpty() || request.getPassword().trim().isEmpty())
        {
            throw new UserException("Missing required field");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            // Authentication failed
            throw new UserException("Incorrect username or password");
        }
        var user = userRepository.findByUsername(request.getUsername());
        if(!user.isEnable()){
            throw new UserException("Account Not Verified: Please verify your email address by clicking on the confirmation link.");

        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // revoke tokens for this user
        revokeAllUserTokens(user);
        // save the new user
        saveUserToken(user, jwtToken);

        return AuthenticationResponse
                .builder().accessToken(jwtToken)
                .refrechToken(refreshToken)
                .build();

    }


    private void revokeAllUserTokens(UserEntity user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
        }
        validUserTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(UserEntity user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }


    public UserEntity register(RegisterRequest request) {
        if(request.getFullName().trim().isEmpty() || request.getUsername().trim().isEmpty() || request.getPassword().trim().isEmpty())
        {
            throw new UserException("Missing required field");
        }
        var checkUser=userRepository.findByUsername(request.getUsername());
        if(checkUser!=null)
        {
            throw new UserException("User already exists with username: " + request.getUsername());
        }
        var user = UserEntity.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .build();
        user.setEnable(false);
        user.setCreatedDate(LocalDateTime.now());
        userRepository.save(user);
        Confirmation confirmation = new Confirmation(user);
        confirmationRepository.save(confirmation);
        //send confirmation email to user
        //emailService.sendMail(user.getFullName(),user.getUsername(),confirmation.getConfirmationKey());

        return user;
    }
    public Boolean verifyConfirmationKey(String key){
        Confirmation confirmation = confirmationRepository.findByConfirmationKey(key);
        UserEntity user = userRepository.findByUsername(confirmation.getUser().getUsername());
        user.setEnable(true);
        userRepository.save(user);
        //confirmationRepository.delete(confirmation);
        return Boolean.TRUE;
    }

    public void changePassword(ChangePassRequest request, Principal connectedUser) {
        //cast the object connectduser to userEntity
        var user = (UserEntity) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        //check if the current password is correct
        if (!bCryptPasswordEncoder.matches(request.getCurrentPass(), user.getPassword())) {
            throw new IllegalStateException("wrong password");
        }
        //check if the new password is correct
        if (!request.getNewPass().equals(request.getConfirmPass())) {
            throw new IllegalStateException("password are not the same");
        }
        user.setPassword(bCryptPasswordEncoder.encode(request.getNewPass()));
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    public void updateResetPasswordToken(String token, String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity != null) {
            userEntity.setResetPasswordToken(token);
            userRepository.save(userEntity);
        }

    }

    public UserEntity getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var userDetails = this.userRepository.findByUsername(username);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                var accessToken = jwtService.generateToken(userDetails);
                revokeAllUserTokens(userDetails);
                saveUserToken(userDetails,accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refrechToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(),authResponse);
            }
        }
    }






}
