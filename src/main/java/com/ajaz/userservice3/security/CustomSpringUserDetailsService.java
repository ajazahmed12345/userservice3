package com.ajaz.userservice3.security;

import com.ajaz.userservice3.models.User;
import com.ajaz.userservice3.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CustomSpringUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomSpringUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            throw new UsernameNotFoundException("email does not exist.");
        }

        User user = userOptional.get();

        return new CustomSpringUserDetails(user);


    }
}
