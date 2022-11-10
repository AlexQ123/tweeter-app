package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.ImageDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import java.sql.Timestamp;

public class Service {

    private static final long EXPIRE_TIME = 86400000;

    protected final UserDAO userDAO;
    protected final ImageDAO imageDAO;
    protected final AuthTokenDAO authTokenDAO;

    public Service (DAOFactory daoFactory) {
        this.userDAO = daoFactory.createUserDAO();
        this.imageDAO = daoFactory.createImageDAO();
        this.authTokenDAO = daoFactory.createAuthTokenDAO();
    }

    protected boolean expiredToken(String token) {
        // The token is null or empty for some reason
        if (token.isBlank()) {
            return true;
        }

        // Look for the token in the table
        AuthToken foundToken = authTokenDAO.tokenFound(token);
        // If the token is not found in the table
        if (foundToken == null) {
            return true;
        }

        // The token has expired
        long currentTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        if (currentTimestamp - Long.parseLong(foundToken.getDatetime()) > EXPIRE_TIME) {
            return true;
        }

        return false;
    }

}
