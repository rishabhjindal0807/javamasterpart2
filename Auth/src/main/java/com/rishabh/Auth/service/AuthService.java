package com.rishabh.Auth.service;

public interface AuthService {

     String login(String userName, String password);

     void logout(String userName);

     Boolean authenticate(String token );
}
