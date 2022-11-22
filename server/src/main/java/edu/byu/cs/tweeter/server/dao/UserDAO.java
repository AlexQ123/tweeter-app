package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;

public interface UserDAO {

    User getUser(String username);
    User register(RegisterRequest request);
    boolean validPassword(String username, String password);

}
