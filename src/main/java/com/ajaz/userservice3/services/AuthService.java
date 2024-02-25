package com.ajaz.userservice3.services;

import com.ajaz.userservice3.configs.KafkaProducerClient;
import com.ajaz.userservice3.dtos.SendEmailMessageDto;
import com.ajaz.userservice3.dtos.UserDto;
import com.ajaz.userservice3.exceptions.WrongCredentialsException;
import com.ajaz.userservice3.models.Role;
import com.ajaz.userservice3.models.Session;
import com.ajaz.userservice3.models.SessionStatus;
import com.ajaz.userservice3.models.User;
import com.ajaz.userservice3.repositories.SessionRepository;
import com.ajaz.userservice3.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class AuthService {

    private UserRepository userRepository;
    private SessionRepository sessionRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private KafkaProducerClient kafkaProducerClient;

    private ObjectMapper objectMapper;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                       KafkaProducerClient kafkaProducerClient, ObjectMapper objectMapper){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.kafkaProducerClient = kafkaProducerClient;
        this.objectMapper = objectMapper;
    }
    public ResponseEntity<UserDto> login(String email, String password) throws WrongCredentialsException {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            throw new WrongCredentialsException("email does not exist");
        }

        User user = userOptional.get();

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new WrongCredentialsException("password does not match.");
        }


        String token = RandomStringUtils.randomAlphanumeric(30);


        MacAlgorithm alg = Jwts.SIG.HS256;
        SecretKey key = alg.key().build();
        // user_id
        // email
        // roles

//        String message = "Hello World!";

//        String message = "{\n" +
//                "  \"email\" : \"ajaz@scaler.com\",\n" +
//                "  \"roles\" : [\n" +
//                "     \"mentor\",\n" +
//                "     \"ta\"\n" +
//                "  ],\n" +
//                "  \"expirationDate\" : \"31December2024\"\n" +
//                "}";
//
//
//        byte[] content = message.getBytes(StandardCharsets.UTF_8);

        Map<String, Object> jsonForJwt = new HashMap<>();

        jsonForJwt.put("email", user.getEmail());
//        jsonForJwt.put("roles", user.getRoles());
        jsonForJwt.put("createdAt", new Date());
        jsonForJwt.put("expiryAt", LocalDate.now().plusDays(3));

        token = Jwts.builder()
                .claims(jsonForJwt)
                .signWith(key, alg)
                .compact();

//        content = Jwts.parser().verifyWith(key).build().parseSignedContent(jws).getPayload();

        Session session = new Session();
        session.setToken(token);
        session.setExpiryAt(new Date(LocalDate.now().plusDays(10).toEpochDay()));
        session.setUser(user);
        session.setSessionStatus(SessionStatus.ACTIVE);

        sessionRepository.save(session);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());

        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" + token);

        UserDto userDto = UserDto.from(user);

        return new ResponseEntity<>(userDto, headers, HttpStatus.OK);


    }

    public ResponseEntity<Void> logout(Long userId, String token) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if(sessionOptional.isEmpty()){
            return null;
        }

        Session session = sessionOptional.get();

        session.setSessionStatus(SessionStatus.ENDED);

        Session savedSession = sessionRepository.save(session);

        return ResponseEntity.ok().build();

    }

    public ResponseEntity<UserDto> signUp(String email, String password) {

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        UserDto userDto = UserDto.from(savedUser);

        try{
            kafkaProducerClient.sendMessage("userSignup", objectMapper.writeValueAsString(userDto));

            SendEmailMessageDto emailMessageDto = new SendEmailMessageDto();
            emailMessageDto.setFrom("ahmed786ajaz@gmail.com");
            emailMessageDto.setTo(userDto.getEmail());
            emailMessageDto.setSubject("Welcome to my User Service");
            emailMessageDto.setBody("Thanks for signing up with us. Hope you enjoy our services");

            kafkaProducerClient.sendMessage("sendEmail", objectMapper.writeValueAsString(emailMessageDto));

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return new ResponseEntity<>(UserDto.from(savedUser), HttpStatus.OK);
    }

    public SessionStatus validateToken(Long userId, String token) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if(sessionOptional.isEmpty()){
            return SessionStatus.ENDED;
        }

        Session session = sessionOptional.get();

        if(!session.getSessionStatus().equals(SessionStatus.ACTIVE)){
            return SessionStatus.ENDED;
        }

//        Jws<Claims> claimsJws = (Jws<Claims>) Jwts.parser()
//                .build()
//                .parseEncryptedClaims(token);

//        String email = (String)claimsJws.getPayload().get("email");
//        List<Role> roles = (List<Role>) claimsJws.getPayload().get("roles");
//
//        Date createdAt = (Date) claimsJws.getPayload().get("createdAt");

//        if(createdAt.before(new Date())){
//            return SessionStatus.ENDED;
//        }


        int i, j;
        String s = "abcav";
        String[] A = {"a", "b", "a", "v", "b"};

//        Arrays.stream(A)

        Map<String, Long> m = Arrays.stream(A).map(String::toLowerCase).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        for(Map.Entry<String, Long> it : m.entrySet()){
            System.out.println(it.getKey() + " " + it.getValue());
        }

        return SessionStatus.ACTIVE;



    }
}
