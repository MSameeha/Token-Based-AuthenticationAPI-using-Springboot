package com.example.api.controller;

import com.example.api.model.User;
import com.example.api.model.UserRegistrationRequest;
import com.example.api.repository.UserRepository;
import com.example.api.service.JwtUserDetailsService;
import com.example.api.util.JwtTokenUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController //Handle HTTP Request and return responses
@RequestMapping("/auth") //'/auth' is the base URL for the controller. All routes in the class are relative to /auth
public class AuthenticationController {

    protected final Log logger = LogFactory.getLog(getClass());

    final UserRepository userRepository;
    final AuthenticationManager authenticationManager;
    final JwtUserDetailsService userDetailsService;
    final JwtTokenUtil jwtTokenUtil;

    //INJECTING DEPENDENCIES THROUGH CONSTRUCTOR
    public AuthenticationController(UserRepository userRepository, AuthenticationManager authenticationManager,
                                    JwtUserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    //WORKS FOR FORM-DATA TYPE OF REQUEST
//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(@RequestParam("user_name") String username,  @RequestParam("password") String password) {
//        Map<String, Object> responseMap = new HashMap<>();
//        try {
//            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//            if (auth.isAuthenticated()) {
//                logger.info("Logged In");
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                String token = jwtTokenUtil.generateToken(userDetails);
//                responseMap.put("error", false);
//                responseMap.put("message", "Logged In");
//                responseMap.put("token", token);
//                return ResponseEntity.ok(responseMap);
//            } else {
//                responseMap.put("error", true);
//                responseMap.put("message", "Invalid Credentials");
//                return ResponseEntity.status(401).body(responseMap);
//            }
//        } catch (DisabledException e) {
//            e.printStackTrace();
//            responseMap.put("error", true);
//            responseMap.put("message", "User is disabled");
//            return ResponseEntity.status(500).body(responseMap);
//        } catch (BadCredentialsException e) {
//            responseMap.put("error", true);
//            responseMap.put("message", "Invalid Credentials");
//            return ResponseEntity.status(401).body(responseMap);
//        } catch (Exception e) {
//            e.printStackTrace();
//            responseMap.put("error", true);
//            responseMap.put("message", "Something went wrong");
//            return ResponseEntity.status(500).body(responseMap);
//        }
//    }

//    @PostMapping("/register")
//    public ResponseEntity<?> saveUser(@RequestParam("first_name") String firstName, @RequestParam("last_name") String lastName, @RequestParam("user_name") String userName, @RequestParam("email") String email, @RequestParam("password") String password) {
//        Map<String, Object> responseMap = new HashMap<>();
//        User user = new User();
//        user.setFirstName(firstName);
//        user.setLastName(lastName);
//        user.setEmail(email);
//        user.setPassword(new BCryptPasswordEncoder().encode(password));
//        user.setRole("USER");
//        user.setUserName(userName);
//        UserDetails userDetails = userDetailsService.createUserDetails(userName, user.getPassword());
//        String token = jwtTokenUtil.generateToken(userDetails);
//        userRepository.save(user);
//        responseMap.put("error", false);
//        responseMap.put("username", userName);
//        responseMap.put("message", "Account created successfully");
//        responseMap.put("token", token);
//        return ResponseEntity.ok(responseMap);
//    }

    //WORKS FOR JSON RAW DATA TYPE OF REQUEST

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserRegistrationRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        String username = request.getUser_name();
        String password = request.getPassword();

        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if (auth.isAuthenticated()) {
                logger.info("Logged In");
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String token = jwtTokenUtil.generateToken(userDetails);
                responseMap.put("error", false);
                responseMap.put("message", "Logged In");
                responseMap.put("token", token);
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("error", true);
                responseMap.put("message", "Invalid Credentials");
                return ResponseEntity.status(401).body(responseMap);
            }
        } catch (DisabledException e) {
            e.printStackTrace();
            responseMap.put("error", true);
            responseMap.put("message", "User is disabled");
            return ResponseEntity.status(500).body(responseMap);
        } catch (BadCredentialsException e) {
            responseMap.put("error", true);
            responseMap.put("message", "Invalid Credentials");
            return ResponseEntity.status(401).body(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("error", true);
            responseMap.put("message", "Something went wrong");
            return ResponseEntity.status(500).body(responseMap);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestBody UserRegistrationRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        User user = new User();
        user.setFirstName(request.getFirst_name());
        user.setLastName(request.getLast_name());
        user.setEmail(request.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        user.setRole("USER");
        user.setUserName(request.getUser_name());
        UserDetails userDetails = userDetailsService.createUserDetails(request.getUser_name(), user.getPassword());
        String token = jwtTokenUtil.generateToken(userDetails);
        userRepository.save(user);
        responseMap.put("error", false);
        responseMap.put("username", request.getUser_name());
        responseMap.put("message", "Account created successfully");
        responseMap.put("token", token);
        return ResponseEntity.ok(responseMap);
    }
}