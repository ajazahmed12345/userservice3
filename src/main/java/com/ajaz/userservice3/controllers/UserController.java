package com.ajaz.userservice3.controllers;

import com.ajaz.userservice3.dtos.SetUserRolesRequestDto;
import com.ajaz.userservice3.dtos.UserDto;
import com.ajaz.userservice3.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserDetails(@PathVariable("id") long userId){

        return userService.getUserDetails(userId);

    }

    @PostMapping("/roles/{id}")
    public ResponseEntity<UserDto> setUserRoles(@PathVariable("id") long userId, @RequestBody SetUserRolesRequestDto request){
        return userService.setUserRoles(userId, request.getRoleIds());
    }


}
