package com.ajaz.userservice3.services;

import com.ajaz.userservice3.dtos.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    public ResponseEntity<UserDto> getUserDetails(long userId) {
        return null;
    }

    public ResponseEntity<UserDto> setUserRoles(long userId, List<Long> roleIds) {
        return null;
    }
}
