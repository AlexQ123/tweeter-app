package edu.byu.cs.tweeter.server.dao;

public interface AuthTokenDAO {

    void addToken(String token, long currentTime);
    void deleteToken(String token);
    boolean tokenFound(String token);

}
