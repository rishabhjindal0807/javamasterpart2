package com.rishabh.Auth.controller;

import com.rishabh.Auth.model.AuthRequest;
import com.rishabh.Auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Controller
@RequestMapping("/api")
public class AuthIntegrationController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        try{
            String authToken = authService.login(authRequest.getUserName(),authRequest.getPassword());
            return ResponseEntity.ok(authToken);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String userName){
        authService.logout(userName);
        return ResponseEntity.ok("loggedOut");
    }

    @GetMapping("/validateUser")
    public ResponseEntity<?> validate(@RequestParam String authToken){
       Boolean isAuthenticated =  authService.authenticate(authToken);
       if(isAuthenticated){
           return ResponseEntity.ok("authenticated");
       }else {
           return ResponseEntity.status(HttpStatusCode.valueOf(401)).build();
       }
    }
}
