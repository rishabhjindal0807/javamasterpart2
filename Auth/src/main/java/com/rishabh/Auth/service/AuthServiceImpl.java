package com.rishabh.Auth.service;

import com.rishabh.Auth.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    HashMap<String, User> users;

    AuthServiceImpl(){
        users = new HashMap<>();
        User user1   = new User("Rishabh","Rishabh","",false);
        User user2   = new User("Jindal","Jindal","",false);
        users.put(user1.getUserName(),user1);
        users.put(user2.getUserName(),user2);}


    @Override
    public String login(String userName, String password) {
        String authToken="";
        if(users.containsKey(userName)){
            User user = users.get(userName);
            if(user.getPassword().equals(password)){
                authToken =  UUID.randomUUID().toString();
                user.setAuthToken(authToken);
                user.setIsLoggedIn(true);
                return authToken;
            }
        }
        throw new RuntimeException("Invalid Username or Password");
    }

    @Override
    public void logout(String userName) {
        if(users.containsKey(userName)){
            User user = users.get(userName);
            user.setIsLoggedIn(false);
            user.setAuthToken("");
        } else{
            throw new RuntimeException("Invalid Username");
        }
    }

    @Override
    public Boolean authenticate(String token) {
        return users.values().stream().anyMatch(user -> user.getAuthToken().equals(token));
    }
}
