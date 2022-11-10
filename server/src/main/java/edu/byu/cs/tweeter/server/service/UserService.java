package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService extends Service {

    public UserService(DAOFactory daoFactory) {
        super(daoFactory);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if(request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing user alias");
        }

        // Check for bad/expired authtoken
        if (expiredToken(request.getAuthToken().getToken())) {
            return new GetUserResponse("Session expired, please log out and log in again.");
        }

        User user = userDAO.getUser(request.getAlias());

        if (user == null) {
            return new GetUserResponse("User not found.");
        }

        return new GetUserResponse(user);
    }

    public AuthenticateResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        // Get the User object
        User userToLogIn = userDAO.getUser(request.getUsername());

        // Check to make sure the user we got actually exists in the DB
        if (userToLogIn == null) {
            return new AuthenticateResponse("There is no account that exists with this username.");
        }

        // Hash the password before checking
        request.setPassword(hashPassword(request.getPassword()));

        // Make sure the password in the request matches the one in the DB
        if (!userDAO.validPassword(request.getUsername(), request.getPassword())) {
            return new AuthenticateResponse("The password is incorrect.");
        }

        // Add the authtoken
        AuthToken authToken = authTokenDAO.addToken();

        return new AuthenticateResponse(userToLogIn, authToken);
    }

    public AuthenticateResponse register(RegisterRequest request) {
        if (request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a first name");
        } else if (request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        } else if (request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing an image");
        } else if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        // Check to make sure the user alias doesn't already exist in the database
        if (userDAO.getUser(request.getUsername()) != null) {
            return new AuthenticateResponse("Username is taken, please choose a different one.");
        }

        // Upload the image to S3 first to obtain the URL
        String imageURL = imageDAO.uploadImage(request.getImage(), request.getUsername());
        request.setImage(imageURL);

        // Hash the password before putting in DB
        request.setPassword(hashPassword(request.getPassword()));

        // Get the user that was just inserted into the DB
        User user = userDAO.register(request);

        // After registering the user, add an authtoken
        AuthToken authToken = authTokenDAO.addToken();

        return new AuthenticateResponse(user, authToken);
    }

    public LogoutResponse logout(LogoutRequest logoutRequest) {
        authTokenDAO.deleteToken(logoutRequest.getAuthToken().getToken());

        return new LogoutResponse();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }

}
