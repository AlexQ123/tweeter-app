package edu.byu.cs.tweeter.server.service;

import java.sql.Timestamp;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoAuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DynamoUserDAO;
import edu.byu.cs.tweeter.server.dao.ImageDAO;
import edu.byu.cs.tweeter.server.dao.S3ImageDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService extends Service {

    public UserService(DAOFactory daoFactory) {
        super(daoFactory);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if(request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing user alias");
        }

        User user = getFakeData().findUserByAlias(request.getAlias());
        return new GetUserResponse(user);
    }

    public AuthenticateResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        // Check to make sure a user with the requested username exists in the DB
        if (!userDAO.findUser(request.getUsername())) {
            return new AuthenticateResponse("There is no account that exists with this username.");
        }

        // Hash the password before checking
        request.setPassword(hashPassword(request.getPassword()));

        // Make sure the password in the request matches the one in the DB
        if (!userDAO.validPassword(request.getUsername(), request.getPassword())) {
            return new AuthenticateResponse("The password is incorrect.");
        }

        // Now that we know the user exists and the password is correct, get the User object
        User userToLogIn = userDAO.login(request);

        // Add the authtoken
        AuthToken authToken = authTokenDAO.addToken();

        return new AuthenticateResponse(userToLogIn, authToken);

//        User user = getDummyUser();
//        AuthToken authToken = getDummyAuthToken();
//        return new AuthenticateResponse(user, authToken);
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
        if (userDAO.findUser(request.getUsername())) {
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
        return password;
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }

}
