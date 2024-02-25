package com.ajaz.userservice3.controllers;


import com.ajaz.userservice3.dtos.*;
import com.ajaz.userservice3.exceptions.WrongCredentialsException;
import com.ajaz.userservice3.models.SessionStatus;
import com.ajaz.userservice3.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) throws WrongCredentialsException {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request){
        return authService.logout(request.getUserId(), request.getToken());
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto request){
        return authService.signUp(request.getEmail(), request.getPassword());
    }

    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(@RequestBody ValidateTokenRequestDto request){
       SessionStatus sessionStatus = authService.validateToken(request.getUserId(), request.getToken());

        return new ResponseEntity<>(sessionStatus, HttpStatus.OK);
    }


}
