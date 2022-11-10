package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthTokenDAO {

    AuthToken addToken();
    void deleteToken(String token);
    AuthToken tokenFound(String token);

}
